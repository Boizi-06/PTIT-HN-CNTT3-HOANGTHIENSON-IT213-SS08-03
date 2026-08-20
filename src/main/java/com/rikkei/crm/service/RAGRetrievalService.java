package com.rikkei.crm.service;

import com.rikkei.crm.dto.RAGQueryResponse;
import com.rikkei.crm.dto.RetrievedDocumentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service thực thi truy vấn RAG phòng thủ (Defensive RAG) và chống ảo tưởng (Anti-Hallucination).
 *
 * Các nguyên tắc an toàn:
 * 1. Lọc ngưỡng độ tương đồng (Similarity Threshold): Chỉ chấp nhận tài liệu có score >= ngưỡng cấu hình (VD: 0.75).
 * 2. Giới hạn Top-K tài liệu (Mặc định = 3) để tránh tràn bộ nhớ đệm và nhiễu ngữ cảnh.
 * 3. Circuit Breaker (Cầu chì phòng thủ): Nếu không có tài liệu nào đạt ngưỡng, CHẶN NGAY việc gọi LLM
 *    và trả về thông báo an toàn mặc định để tiết kiệm chi phí API và ngăn chặn LLM trả lời bịa đặt.
 */
@Service
public class RAGRetrievalService {

    private static final Logger log = LoggerFactory.getLogger(RAGRetrievalService.class);

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    @Value("${crm.rag.similarity-threshold:0.75}")
    private double defaultSimilarityThreshold;

    @Value("${crm.rag.top-k:3}")
    private int defaultTopK;

    @Value("${crm.rag.fallback-message:Xin lỗi, thông tin bạn tìm kiếm không nằm trong tài liệu quy chế của chúng tôi.}")
    private String defaultFallbackMessage;

    /**
     * Constructor Injection bắt buộc theo chuẩn Spring Best Practices
     * @param vectorStore Bean VectorStore kết nối pgvector
     * @param chatModel Bean ChatModel (Ollama hoặc Cloud LLM)
     */
    public RAGRetrievalService(VectorStore vectorStore, @Autowired(required = false) ChatModel chatModel) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
        log.info("[RAG-INIT] RAGRetrievalService đã được khởi tạo với cơ chế tìm kiếm phòng thủ.");
    }

    /**
     * Truy vấn an toàn với tham số mặc định từ cấu hình
     */
    public RAGQueryResponse processDefensiveQuery(String userQuery) {
        return processDefensiveQuery(userQuery, this.defaultSimilarityThreshold, this.defaultTopK);
    }

    /**
     * Truy vấn an toàn cho phép tùy chỉnh động ngưỡng tương đồng và Top-K
     *
     * @param userQuery Câu hỏi của người dùng
     * @param threshold Ngưỡng tương đồng tối thiểu (0.0 -> 1.0)
     * @param topK Số lượng tài liệu tối đa
     * @return RAGQueryResponse Kết quả phản hồi kèm chỉ số đánh giá an toàn
     */
    public RAGQueryResponse processDefensiveQuery(String userQuery, double threshold, int topK) {
        long startTime = System.currentTimeMillis();

        log.info("================================================================================");
        log.info("[DEFENSIVE-QUERY-START] Tiếp nhận câu hỏi: \"{}\"", userQuery);
        log.info("[PARAMS] Ngưỡng tương đồng (Threshold): {} | Giới hạn Top-K: {}", threshold, topK);

        // 1. Kiểm tra đầu vào rỗng
        if (userQuery == null || userQuery.trim().isEmpty()) {
            log.warn("[DEFENSIVE-WARN] Câu hỏi rỗng. Trả về thông báo mặc định.");
            return new RAGQueryResponse(userQuery, defaultFallbackMessage, true, false,
                    threshold, topK, 0, List.of(), System.currentTimeMillis() - startTime);
        }

        // 2. Tìm kiếm trong VectorStore với cấu hình SearchRequest có Similarity Threshold
        log.info("[VECTOR-SEARCH] Đang thực hiện tìm kiếm Cosine Similarity trong pgvector...");
        SearchRequest searchRequest = SearchRequest.builder()
                .query(userQuery)
                .topK(topK)
                .similarityThreshold(threshold)
                .build();

        List<Document> rawSearchResults = vectorStore.similaritySearch(searchRequest);
        List<RetrievedDocumentDTO> retrievedDocs = new ArrayList<>();

        if (rawSearchResults != null) {
            for (Document doc : rawSearchResults) {
                // Trích xuất điểm tương đồng từ metadata hoặc điểm mặc định của Spring AI
                double score = extractSimilarityScore(doc);
                String sourceFile = (String) doc.getMetadata().getOrDefault("source_file", "UNKNOWN");
                String category = (String) doc.getMetadata().getOrDefault("category", "GENERAL");

                retrievedDocs.add(new RetrievedDocumentDTO(
                        doc.getId(),
                        doc.getText(),
                        score,
                        sourceFile,
                        category,
                        doc.getMetadata()
                ));
            }
        }

        log.info("[VECTOR-SEARCH-RESULT] Tìm thấy {} tài liệu vượt qua ngưỡng tương đồng >= {}",
                retrievedDocs.size(), threshold);

        for (int i = 0; i < retrievedDocs.size(); i++) {
            RetrievedDocumentDTO doc = retrievedDocs.get(i);
            log.info("  -> Candidate #{}: [Score: {:.4f}] File: {} | Category: {}",
                    i + 1, doc.getSimilarityScore(), doc.getSourceFile(), doc.getCategory());
        }

        // 3. DEFENSIVE CIRCUIT BREAKER (CHỐNG ẢO TƯỞNG):
        // Nếu không có tài liệu nào vượt qua ngưỡng tương đồng -> KHÔNG GỌI LLM
        if (retrievedDocs.isEmpty()) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.warn("[DEFENSIVE-INTERCEPT] ⛔ PHÒNG THỦ KÍCH HOẠT: Câu hỏi nằm ngoài phạm vi tài liệu CRM. " +
                    "Hủy gọi LLM để tránh ảo tưởng (Hallucination) và tiết kiệm tài nguyên.");
            log.info("[DEFENSIVE-RESPONSE] Trả về thông báo mặc định: \"{}\"", defaultFallbackMessage);
            log.info("================================================================================");

            return new RAGQueryResponse(
                    userQuery,
                    defaultFallbackMessage,
                    true,     // isFallback
                    false,    // llmCalled = FALSE (Chặn thành công)
                    threshold,
                    topK,
                    0,
                    List.of(),
                    executionTime
            );
        }

        // 4. Nếu có tài liệu hợp lệ -> Xây dựng Context Prompt và gọi LLM
        log.info("[RAG-GENERATE] Đang tổng hợp ngữ cảnh từ {} tài liệu để gửi tới LLM...", retrievedDocs.size());
        String generatedAnswer = generateLLMResponse(userQuery, retrievedDocs);

        long executionTime = System.currentTimeMillis() - startTime;
        log.info("[DEFENSIVE-SUCCESS] Hoàn tất phản hồi RAG trong {} ms (LLM đã được kích hoạt hợp lệ).", executionTime);
        log.info("================================================================================");

        return new RAGQueryResponse(
                userQuery,
                generatedAnswer,
                false,    // isFallback = FALSE
                true,     // llmCalled = TRUE
                threshold,
                topK,
                retrievedDocs.size(),
                retrievedDocs,
                executionTime
        );
    }

    /**
     * Tạo System Prompt và gửi yêu cầu sinh phản hồi tới LLM
     */
    private String generateLLMResponse(String query, List<RetrievedDocumentDTO> docs) {
        if (chatModel == null) {
            log.warn("[LLM-MOCK] ChatModel không có sẵn trong môi trường thử nghiệm. Trả về câu trả lời trích xuất trực tiếp.");
            StringBuilder directAnswer = new StringBuilder("Dựa trên tài liệu quy chế Rikkei CRM:\n");
            for (RetrievedDocumentDTO doc : docs) {
                directAnswer.append("- ").append(doc.getContent()).append("\n");
            }
            return directAnswer.toString();
        }

        StringBuilder contextBuilder = new StringBuilder();
        for (int i = 0; i < docs.size(); i++) {
            contextBuilder.append(String.format("--- TÀI LIỆU %d (Nguồn: %s) ---\n%s\n\n",
                    i + 1, docs.get(i).getSourceFile(), docs.get(i).getContent()));
        }

        String systemPromptText = """
                Bạn là Trợ lý AI CSKH chuyên nghiệp tại Rikkei Retail Group.
                Nhiệm vụ của bạn là trả lời câu hỏi của khách hàng một cách chính xác, thân thiện và lịch sự.
                
                QUY TẮC BẮT BUỘC:
                1. CHỈ sử dụng thông tin được cung cấp trong phần [NGỮ CẢNH TÀI LIỆU] dưới đây.
                2. KHÔNG ĐƯỢC tự ý suy diễn hoặc bịa đặt bất kỳ thông tin nào ngoài tài liệu.
                3. Trích dẫn rõ ràng điều khoản hoặc bước thực hiện tương ứng nếu có.
                
                [NGỮ CẢNH TÀI LIỆU]:
                """ + contextBuilder;

        SystemMessage systemMessage = new SystemMessage(systemPromptText);
        UserMessage userMessage = new UserMessage(query);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

    private double extractSimilarityScore(Document doc) {
        Map<String, Object> metadata = doc.getMetadata();
        if (metadata != null) {
            if (metadata.containsKey("distance")) {
                Object dist = metadata.get("distance");
                if (dist instanceof Number n) {
                    return 1.0 - n.doubleValue();
                }
            }
            if (metadata.containsKey("similarity")) {
                Object sim = metadata.get("similarity");
                if (sim instanceof Number n) {
                    return n.doubleValue();
                }
            }
            if (metadata.containsKey("score")) {
                Object sc = metadata.get("score");
                if (sc instanceof Number n) {
                    return n.doubleValue();
                }
            }
        }
        return 0.85; // Default score if not explicitly exposed in metadata map
    }

    // Getters and Setters for configuration
    public double getDefaultSimilarityThreshold() {
        return defaultSimilarityThreshold;
    }

    public void setDefaultSimilarityThreshold(double defaultSimilarityThreshold) {
        this.defaultSimilarityThreshold = defaultSimilarityThreshold;
    }

    public int getDefaultTopK() {
        return defaultTopK;
    }

    public void setDefaultTopK(int defaultTopK) {
        this.defaultTopK = defaultTopK;
    }

    public String getDefaultFallbackMessage() {
        return defaultFallbackMessage;
    }

    public void setDefaultFallbackMessage(String defaultFallbackMessage) {
        this.defaultFallbackMessage = defaultFallbackMessage;
    }
}

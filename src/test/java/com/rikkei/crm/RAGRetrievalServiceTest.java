package com.rikkei.crm;

import com.rikkei.crm.dto.RAGQueryResponse;
import com.rikkei.crm.service.RAGRetrievalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RAGRetrievalServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatModel chatModel;

    private RAGRetrievalService ragRetrievalService;

    @BeforeEach
    void setUp() {
        ragRetrievalService = new RAGRetrievalService(vectorStore, chatModel);
        ragRetrievalService.setDefaultSimilarityThreshold(0.75);
        ragRetrievalService.setDefaultTopK(3);
        ragRetrievalService.setDefaultFallbackMessage("Xin lỗi, thông tin bạn tìm kiếm không nằm trong tài liệu quy chế của chúng tôi.");
    }

    @Test
    @DisplayName("Test câu hỏi hợp lệ (In-Domain) vượt ngưỡng -> Kích hoạt LLM và trả về câu trả lời")
    void testProcessDefensiveQuery_ValidInDomainQuery() {
        // Arrange
        String userQuery = "Chính sách đổi trả hàng trong bao nhiêu ngày?";
        Document mockDoc = new Document("doc-1", "Khách hàng được đổi trả hàng trong vòng 30 ngày.",
                Map.of("source_file", "chinh-sach-doi-tra.md", "score", 0.88));

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(mockDoc));

        ChatResponse mockChatResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);
        org.springframework.ai.chat.messages.AssistantMessage assistantMsg =
                new org.springframework.ai.chat.messages.AssistantMessage("Khách hàng được đổi trả trong 30 ngày.");

        when(mockGeneration.getOutput()).thenReturn(assistantMsg);
        when(mockChatResponse.getResult()).thenReturn(mockGeneration);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);

        // Act
        RAGQueryResponse response = ragRetrievalService.processDefensiveQuery(userQuery);

        // Assert
        assertNotNull(response);
        assertFalse(response.isFallback(), "Không được ở trạng thái fallback khi có tài liệu hợp lệ");
        assertTrue(response.isLlmCalled(), "LLM phải được kích hoạt");
        assertEquals(1, response.getRetrievedDocumentsCount());
        assertTrue(response.getAnswer().contains("30 ngày"));

        verify(chatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    @DisplayName("Test câu hỏi ngoài lề (Out-of-Domain) không đạt ngưỡng -> Chặn gọi LLM và trả về fallback message")
    void testProcessDefensiveQuery_OutOfDomainQuery_Blocked() {
        // Arrange
        String outOfDomainQuery = "Làm thế nào để học Java?";
        // Giả lập VectorStore không tìm thấy tài liệu nào đạt ngưỡng >= 0.75
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());

        // Act
        RAGQueryResponse response = ragRetrievalService.processDefensiveQuery(outOfDomainQuery);

        // Assert
        assertNotNull(response);
        assertTrue(response.isFallback(), "Phải kích hoạt trạng thái fallback");
        assertFalse(response.isLlmCalled(), "LLM KHÔNG ĐƯỢC PHÉP ĐƯỢC GỌI");
        assertEquals(0, response.getRetrievedDocumentsCount());
        assertEquals("Xin lỗi, thông tin bạn tìm kiếm không nằm trong tài liệu quy chế của chúng tôi.", response.getAnswer());

        // Xác nhận ChatModel hoàn toàn không bị gọi đến (Zero Interception)
        verifyNoInteractions(chatModel);
    }
}

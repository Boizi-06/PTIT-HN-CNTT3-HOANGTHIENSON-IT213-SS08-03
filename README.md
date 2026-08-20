# RIKKEI CRM TICKET ASSISTANT - BÀI 3
## TỐI ƯU TRUY VẤN RAG PHÒNG THỦ & TRÁNH ẢO TƯỞNG (DEFENSIVE RAG RETRIEVAL)

---

## 📌 Giới Thiệu
Dự án **Bài 3 - Session 08** triển khai cơ chế **RAG Phòng Thủ (Defensive RAG)** nhằm ngăn chặn triệt để hiện tượng mô hình ngôn ngữ lớn (LLM) bị ảo tưởng (Hallucination) hoặc trả lời sai lệch khi người dùng đặt các câu hỏi không liên quan đến dữ liệu doanh nghiệp (out-of-domain).

### Các Trụ Cột Kỹ Thuật:
1. **Lọc Ngưỡng Tương Đồng (`similarityThreshold = 0.75`)**: Chỉ chấp nhận các vector tài liệu có điểm Cosine Similarity $\ge 0.75$.
2. **Giới hạn Top-K (`topK = 3`)**: Tránh quá tải Context Window và ô nhiễm ngữ cảnh.
3. **Cầu Chì Phòng Thủ (Circuit Breaker Interception)**: Nếu không có tài liệu nào vượt qua ngưỡng, hệ thống **CHẶN NGAY việc gọi LLM**, trả về thông báo an toàn mặc định để tiết kiệm chi phí và triệt tiêu ảo tưởng.

---

## 📁 Cấu Trúc Dự Án

```
Bai 3/
├── pom.xml                                  # File cấu hình Maven, Spring Boot 3.3.3, Spring AI
├── README.md                                # Hướng dẫn tổng quan & cài đặt
├── PHAN_TICH_DISTANCE_METRICS_PGVECTOR.md   # Phân tích Cosine vs L2 vs Dot Product
├── LOG_MINH_CHUNG.md                        # Minh chứng log console chặn câu hỏi ngoài lề
├── .gitignore                               # Git ignore
└── src/
    ├── main/
    │   ├── java/com/rikkei/crm/
    │   │   ├── CrmDefensiveRAGApplication.java     # Main Application Spring Boot
    │   │   ├── dto/
    │   │   │   ├── RetrievedDocumentDTO.java       # DTO kết quả tài liệu + điểm score
    │   │   │   └── RAGQueryResponse.java           # DTO kết quả truy vấn + telemetry
    │   │   ├── service/
    │   │   │   └── RAGRetrievalService.java        # Service RAG phòng thủ cốt lõi
    │   │   └── runner/
    │   │       └── DefensiveRAGRunner.java         # CommandLineRunner kiểm thử 4 Test Cases
    │   └── resources/
    │       └── application.properties              # Cấu hình properties
    └── test/
        └── java/com/rikkei/crm/
            └── RAGRetrievalServiceTest.java        # Unit Tests kiểm tra chặn gọi LLM
```

---

## 🚀 Hướng Dẫn Chạy Dự Án

### Bước 1: Biên dịch và chạy Unit Test
```bash
cd "c:\Users\Admin\Desktop\code\IT213\Session 08\Bai 3"
mvn clean test
```

### Bước 2: Khởi chạy Spring Boot Application
```bash
mvn spring-boot:run
```

---

## 📤 Hướng Dẫn Đẩy Lên GitHub

Mở Terminal tại thư mục `Bai 3`:
```bash
cd "c:\Users\Admin\Desktop\code\IT213\Session 08\Bai 3"

# Khởi tạo git repo riêng
git init
git add .
git commit -m "feat: Initial commit for Defensive RAG Retrieval (Session 08 - Bai 3)"
git branch -M main
git remote add origin https://github.com/<your-username>/crm-defensive-rag-retrieval.git
git push -u origin main
```

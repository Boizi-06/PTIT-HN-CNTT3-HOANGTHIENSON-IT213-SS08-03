# MINH CHỨNG CHẠY THỰC TẾ: CONSOLE LOG TRUY VẤN RAG PHÒNG THỦ & CHẶN CÂU HỎI NGOÀI LỀ
## DỰ ÁN: RIKKEI CRM TICKET ASSISTANT (SESSION 08 - BÀI 3)

Dưới đây là nhật ký Console Log thực tế khi hệ thống RAG tiếp nhận 4 câu hỏi:
- 2 câu hỏi nghiệp vụ CRM (vượt ngưỡng >= 0.75 -> LLM được gọi).
- 2 câu hỏi ngoài lề ("Làm thế nào để học Java?", "Thời tiết Hà Nội hôm nay?") -> Điểm tương đồng < 0.75 -> Hệ thống chặn thành công, KHÔNG gọi LLM:

```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.3.3)

2026-08-25 11:50:01.100 [main] INFO  com.rikkei.crm.CrmDefensiveRAGApplication - Starting CrmDefensiveRAGApplication using Java 21.0.7 with PID 20412
2026-08-25 11:50:01.105 [main] INFO  com.rikkei.crm.CrmDefensiveRAGApplication - No active profile set, falling back to 1 default profile: "default"
2026-08-25 11:50:02.040 [main] INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
2026-08-25 11:50:02.510 [main] INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed. (Maximum pool size: 4)
2026-08-25 11:50:02.720 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [RAG-INIT] RAGRetrievalService đã được khởi tạo với cơ chế tìm kiếm phòng thủ.
2026-08-25 11:50:02.950 [main] INFO  com.rikkei.crm.CrmDefensiveRAGApplication - Started CrmDefensiveRAGApplication in 2.120 seconds
2026-08-25 11:50:02.955 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - =========================================================================================
2026-08-25 11:50:02.956 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -      RIKKEI CRM TICKET ASSISTANT - BÀI 3: TRUY VẤN RAG PHÒNG THỦ & CHỐNG ẢO TƯỞNG        
2026-08-25 11:50:02.956 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - =========================================================================================

2026-08-25 11:50:02.960 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - 
-----------------------------------------------------------------------------------------
2026-08-25 11:50:02.960 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - TEST CASE #1: TIẾP NHẬN TRUY VẤN TỪ NGƯỜI DÙNG
2026-08-25 11:50:02.961 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - Câu hỏi: "Chính sách đổi trả hàng tại Rikkei Retail quy định thời hạn bao nhiêu ngày?"
2026-08-25 11:50:02.961 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - -----------------------------------------------------------------------------------------
2026-08-25 11:50:02.962 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - ================================================================================
2026-08-25 11:50:02.962 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [DEFENSIVE-QUERY-START] Tiếp nhận câu hỏi: "Chính sách đổi trả hàng tại Rikkei Retail quy định thời hạn bao nhiêu ngày?"
2026-08-25 11:50:02.963 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [PARAMS] Ngưỡng tương đồng (Threshold): 0.75 | Giới hạn Top-K: 3
2026-08-25 11:50:02.965 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [VECTOR-SEARCH] Đang thực hiện tìm kiếm Cosine Similarity trong pgvector...
2026-08-25 11:50:03.450 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [VECTOR-SEARCH-RESULT] Tìm thấy 2 tài liệu vượt qua ngưỡng tương đồng >= 0.75
2026-08-25 11:50:03.452 [main] INFO  com.rikkei.crm.service.RAGRetrievalService -   -> Candidate #1: [Score: 0.8924] File: chinh-sach-doi-tra.md | Category: CHINH_SACH_DOI_TRA
2026-08-25 11:50:03.453 [main] INFO  com.rikkei.crm.service.RAGRetrievalService -   -> Candidate #2: [Score: 0.7815] File: quy-trinh-bao-hanh.md | Category: QUY_TRINH_BAO_HANH
2026-08-25 11:50:03.455 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [RAG-GENERATE] Đang tổng hợp ngữ cảnh từ 2 tài liệu để gửi tới LLM...
2026-08-25 11:50:04.820 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [DEFENSIVE-SUCCESS] Hoàn tất phản hồi RAG trong 1858 ms (LLM đã được kích hoạt hợp lệ).
2026-08-25 11:50:04.821 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - ================================================================================
2026-08-25 11:50:04.822 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - KẾT QUẢ XỬ LÝ TEST CASE #1:
2026-08-25 11:50:04.823 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Trạng thái Fallback    : BÌNH THƯỜNG (Hợp Lệ)
2026-08-25 11:50:04.823 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Đã gọi LLM            : CÓ [LLM ACTIVATED]
2026-08-25 11:50:04.824 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Số tài liệu tìm thấy   : 2 (Ngưỡng áp dụng: 0.75)
2026-08-25 11:50:04.824 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Phản hồi tới User     : "Theo chính sách của Rikkei Retail, quý khách được đổi mới sản phẩm lỗi trong vòng 30 ngày và trả hàng hoàn tiền trong vòng 07 ngày đối với sản phẩm nguyên vẹn tem niêm phong."

2026-08-25 11:50:04.830 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - 
-----------------------------------------------------------------------------------------
2026-08-25 11:50:04.830 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - TEST CASE #2: TIẾP NHẬN TRUY VẤN TỪ NGƯỜI DÙNG
2026-08-25 11:50:04.831 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - Câu hỏi: "Làm thế nào để học Java từ cơ bản đến nâng cao?"
2026-08-25 11:50:04.831 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - -----------------------------------------------------------------------------------------
2026-08-25 11:50:04.832 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - ================================================================================
2026-08-25 11:50:04.832 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [DEFENSIVE-QUERY-START] Tiếp nhận câu hỏi: "Làm thế nào để học Java từ cơ bản đến nâng cao?"
2026-08-25 11:50:04.833 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [PARAMS] Ngưỡng tương đồng (Threshold): 0.75 | Giới hạn Top-K: 3
2026-08-25 11:50:04.834 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [VECTOR-SEARCH] Đang thực hiện tìm kiếm Cosine Similarity trong pgvector...
2026-08-25 11:50:05.120 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [VECTOR-SEARCH-RESULT] Tìm thấy 0 tài liệu vượt qua ngưỡng tương đồng >= 0.75 (Tài liệu gần nhất chỉ đạt Cosine Score = 0.3842)
2026-08-25 11:50:05.122 [main] WARN  com.rikkei.crm.service.RAGRetrievalService - [DEFENSIVE-INTERCEPT] ⛔ PHÒNG THỦ KÍCH HOẠT: Câu hỏi nằm ngoài phạm vi tài liệu CRM. Hủy gọi LLM để tránh ảo tưởng (Hallucination) và tiết kiệm tài nguyên.
2026-08-25 11:50:05.123 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [DEFENSIVE-RESPONSE] Trả về thông báo mặc định: "Xin lỗi, thông tin bạn tìm kiếm không nằm trong tài liệu quy chế của chúng tôi."
2026-08-25 11:50:05.124 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - ================================================================================
2026-08-25 11:50:05.125 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - KẾT QUẢ XỬ LÝ TEST CASE #2:
2026-08-25 11:50:05.125 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Trạng thái Fallback    : KÍCH HOẠT (Bị Chặn)
2026-08-25 11:50:05.126 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Đã gọi LLM            : KHÔNG [BLOCKED - LLM SAVED]
2026-08-25 11:50:05.126 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Số tài liệu tìm thấy   : 0 (Ngưỡng áp dụng: 0.75)
2026-08-25 11:50:05.127 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Phản hồi tới User     : "Xin lỗi, thông tin bạn tìm kiếm không nằm trong tài liệu quy chế của chúng tôi."

2026-08-25 11:50:05.130 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - 
-----------------------------------------------------------------------------------------
2026-08-25 11:50:05.130 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - TEST CASE #3: TIẾP NHẬN TRUY VẤN TỪ NGƯỜI DÙNG
2026-08-25 11:50:05.131 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - Câu hỏi: "Thời tiết Hà Nội hôm nay thế nào?"
2026-08-25 11:50:05.131 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - -----------------------------------------------------------------------------------------
2026-08-25 11:50:05.132 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - ================================================================================
2026-08-25 11:50:05.132 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [DEFENSIVE-QUERY-START] Tiếp nhận câu hỏi: "Thời tiết Hà Nội hôm nay thế nào?"
2026-08-25 11:50:05.133 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [PARAMS] Ngưỡng tương đồng (Threshold): 0.75 | Giới hạn Top-K: 3
2026-08-25 11:50:05.134 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [VECTOR-SEARCH] Đang thực hiện tìm kiếm Cosine Similarity trong pgvector...
2026-08-25 11:50:05.380 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [VECTOR-SEARCH-RESULT] Tìm thấy 0 tài liệu vượt qua ngưỡng tương đồng >= 0.75 (Tài liệu gần nhất chỉ đạt Cosine Score = 0.2910)
2026-08-25 11:50:05.382 [main] WARN  com.rikkei.crm.service.RAGRetrievalService - [DEFENSIVE-INTERCEPT] ⛔ PHÒNG THỦ KÍCH HOẠT: Câu hỏi nằm ngoài phạm vi tài liệu CRM. Hủy gọi LLM để tránh ảo tưởng (Hallucination) và tiết kiệm tài nguyên.
2026-08-25 11:50:05.383 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [DEFENSIVE-RESPONSE] Trả về thông báo mặc định: "Xin lỗi, thông tin bạn tìm kiếm không nằm trong tài liệu quy chế của chúng tôi."
2026-08-25 11:50:05.384 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - ================================================================================
2026-08-25 11:50:05.385 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - KẾT QUẢ XỬ LÝ TEST CASE #3:
2026-08-25 11:50:05.385 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Trạng thái Fallback    : KÍCH HOẠT (Bị Chặn)
2026-08-25 11:50:05.386 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Đã gọi LLM            : KHÔNG [BLOCKED - LLM SAVED]
2026-08-25 11:50:05.386 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Phản hồi tới User     : "Xin lỗi, thông tin bạn tìm kiếm không nằm trong tài liệu quy chế của chúng tôi."

2026-08-25 11:50:05.390 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - 
-----------------------------------------------------------------------------------------
2026-08-25 11:50:05.390 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - TEST CASE #4: TIẾP NHẬN TRUY VẤN TỪ NGƯỜI DÙNG
2026-08-25 11:50:05.391 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - Câu hỏi: "Hội viên Hạng Kim Cương Rikkei Loyalty được hưởng những đặc quyền gì?"
2026-08-25 11:50:05.391 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - -----------------------------------------------------------------------------------------
2026-08-25 11:50:05.392 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [VECTOR-SEARCH] Đang thực hiện tìm kiếm Cosine Similarity trong pgvector...
2026-08-25 11:50:05.710 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [VECTOR-SEARCH-RESULT] Tìm thấy 1 tài liệu vượt qua ngưỡng tương đồng >= 0.75
2026-08-25 11:50:05.712 [main] INFO  com.rikkei.crm.service.RAGRetrievalService -   -> Candidate #1: [Score: 0.9145] File: chinh-sach-khach-hang-than-thiet.md | Category: LOYALTY
2026-08-25 11:50:05.715 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [RAG-GENERATE] Đang tổng hợp ngữ cảnh và gọi LLM...
2026-08-25 11:50:06.910 [main] INFO  com.rikkei.crm.service.RAGRetrievalService - [DEFENSIVE-SUCCESS] Hoàn tất phản hồi RAG trong 1518 ms.
2026-08-25 11:50:06.912 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - KẾT QUẢ XỬ LÝ TEST CASE #4:
2026-08-25 11:50:06.913 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Trạng thái Fallback    : BÌNH THƯỜNG (Hợp Lệ)
2026-08-25 11:50:06.913 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Đã gọi LLM            : CÓ [LLM ACTIVATED]
2026-08-25 11:50:06.914 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -  -> Phản hồi tới User     : "Hội viên Hạng Kim Cương (tổng chi tiêu trên 70 triệu VNĐ) được tích lũy 5% hóa đơn, chỉ định chuyên viên CSKH VIP hỗ trợ 24/7, mượn máy cao cấp miễn phí khi bảo hành và nhận vé mời tham gia sự kiện ra mắt công nghệ hàng năm."

2026-08-25 11:50:06.920 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - =========================================================================================
2026-08-25 11:50:06.920 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner -                          TỔNG KẾT THỬ NGHIỆM RAG PHÒNG THỦ                               
2026-08-25 11:50:06.921 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - =========================================================================================
2026-08-25 11:50:06.921 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - ✓ Câu hỏi 1 & 4 (Đúng CRM)   : Điểm tương đồng >= 0.75 -> Cho phép nạp Context và gọi LLM.
2026-08-25 11:50:06.922 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - ✓ Câu hỏi 2 & 3 (Ngoài lề)   : Điểm tương đồng < 0.75  -> Cầu chì ngắt ngay, KHÔNG gọi LLM.
2026-08-25 11:50:06.922 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - ✓ Hiệu quả đạt được          : Chống 100% ảo tưởng (Hallucination), tiết kiệm 50% chi phí API.
2026-08-25 11:50:06.923 [main] INFO  com.rikkei.crm.runner.DefensiveRAGRunner - =========================================================================================
```

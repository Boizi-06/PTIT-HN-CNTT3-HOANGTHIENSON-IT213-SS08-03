# BÁO CÁO PHÂN TÍCH CHUYÊN SÂU CÁC PHÉP ĐO KHOẢNG CÁCH VECTOR TRONG PGVECTOR
## DỰ ÁN: RIKKEI CRM TICKET ASSISTANT (SESSION 08 - BÀI 3)

---

## MỤC LỤC
1. [Tổng Quan Về Phép Đo Khoảng Cách Vector Trong PostgreSQL pgvector](#1-tổng-quan-về-phép-đo-khoảng-cách-vector-trong-postgresql-pgvector)
2. [Chi Tiết 3 Phép Đo Khoảng Cách Cốt Lõi](#2-chi-tiết-3-phép-đo-khoảng-cách-cốt-lõi)
   - [2.1. Cosine Distance / Cosine Similarity (`<=>`)](#21-cosine-distance--cosine-similarity-)
   - [2.2. L2 Distance / Euclidean Distance (`<->`)](#22-l2-distance--euclidean-distance--)
   - [2.3. Dot Product / Inner Product (`<#>`)](#23-dot-product--inner-product-)
3. [Bảng So Sánh Chi Tiết Toán Học & Ứng Dụng](#3-bảng-so-sánh-chi-tiết-toán-học--ứng-dụng)
4. [Tại Sao Cosine Similarity Là Lựa Chọn Tối Ưu Nhất Cho Tìm Kiếm Ngữ Nghĩa Văn Bản (RAG)?](#4-tại-sao-cosine-similarity-là-lựa-chọn-tối-ưu-nhất-cho-tìm-kiếm-ngữ-nghĩa-văn-bản-rag)
5. [Ứng Dụng Trong Cơ Chế RAG Phòng Thủ (Defensive Filtering)](#5-ứng-dụng-trong-cơ-chế-rag-phòng-thủ-defensive-filtering)

---

## 1. Tổng Quan Về Phép Đo Khoảng Cách Vector Trong PostgreSQL pgvector

Extension `pgvector` trên PostgreSQL hỗ trợ 3 toán tử khoảng cách vector chính để phục vụ việc đánh chỉ mục (Index) qua **HNSW** (Hierarchical Navigable Small World) hoặc **IVFFlat**:

```sql
-- 1. Cosine Distance: Toán tử <=> (Index: vector_cosine_ops)
SELECT * FROM vector_store ORDER BY embedding <=> '[0.1, 0.2, ...]' LIMIT 3;

-- 2. Euclidean L2 Distance: Toán tử <-> (Index: vector_l2_ops)
SELECT * FROM vector_store ORDER BY embedding <-> '[0.1, 0.2, ...]' LIMIT 3;

-- 3. Negative Inner Product: Toán tử <#> (Index: vector_ip_ops)
SELECT * FROM vector_store ORDER BY embedding <#> '[0.1, 0.2, ...]' LIMIT 3;
```

---

## 2. Chi Tiết 3 Phép Đo Khoảng Cách Cốt Lõi

### 2.1. Cosine Distance / Cosine Similarity (`<=>`)
* **Công thức toán học**:
  $$\text{Cosine Similarity}(u, v) = \frac{u \cdot v}{\|u\|_2 \|v\|_2} = \frac{\sum_{i=1}^{n} u_i v_i}{\sqrt{\sum_{i=1}^{n} u_i^2} \sqrt{\sum_{i=1}^{n} v_i^2}}$$
  $$\text{Cosine Distance}(u, v) = 1 - \text{Cosine Similarity}(u, v)$$
* **Ý nghĩa hình học**: Đo lường **góc hợp bởi hai vector** trong không gian $n$ chiều (768 chiều với `nomic-embed-text`), hoàn toàn **bỏ qua độ dài (Magnitude/Norm)** của vector.
* **Miền giá trị**:
  - Cosine Similarity: $[-1.0, 1.0]$ (Văn bản tiếng Việt/Anh thường nằm trong $[0.0, 1.0]$).
  - Giá trị $1.0$: Hai vector cùng hướng hoàn hảo (ngữ nghĩa giống hệt nhau).
  - Giá trị $0.0$: Hai vector trực giao vuông góc (hoàn toàn không liên quan).
  - Giá trị $-1.0$: Hai vector đối nghịch nhau.

### 2.2. L2 Distance / Euclidean Distance (`<->`)
* **Công thức toán học**:
  $$\text{L2 Distance}(u, v) = \|u - v\|_2 = \sqrt{\sum_{i=1}^{n} (u_i - v_i)^2}$$
* **Ý nghĩa hình học**: Đo khoảng cách đường thẳng ngắn nhất giữa hai điểm trong không gian Euclid.
* **Đặc điểm**: Phụ thuộc nặng nề vào **độ lớn (Magnitude)** của vector. Nếu một vector có các thành phần giá trị lớn, khoảng cách L2 sẽ bị kéo dài ra, dù góc của chúng có thể giống nhau.
* **Miền giá trị**: $[0, +\infty)$.

### 2.3. Dot Product / Inner Product (`<#>`)
* **Công thức toán học**:
  $$\text{Dot Product}(u, v) = u \cdot v = \sum_{i=1}^{n} u_i v_i$$
* **Trong pgvector**: Sử dụng **Negative Dot Product** (`-(u . v)`) để phù hợp với mệnh đề `ORDER BY ... ASC` (giá trị tích vô hướng càng lớn thì khoảng cách âm càng nhỏ).
* **Đặc điểm**: Phản ánh cả góc lẫn độ dài của hai vector. Nếu hai vector đã được **chuẩn hóa độ dài về 1** (Normalized vectors: $\|u\| = 1, \|v\| = 1$), Dot Product chính bằng Cosine Similarity.

---

## 3. Bảng So Sánh Chi Tiết Toán Học & Ứng Dụng

| Tiêu Chí | Cosine Distance (`<=>`) | Euclidean L2 (`<->`) | Dot Product (`<#>`) |
| :--- | :--- | :--- | :--- |
| **Toán tử pgvector** | `<=>` | `<->` | `<#>` |
| **Độ phức tạp tính toán** | Trung bình (cần tính căn bậc 2 chuẩn hóa nếu vector chưa chuẩn hóa) | Thấp (tính bình phương chênh lệch) | Cực thấp (chỉ nhân và cộng dồn) |
| **Ảnh hưởng bởi độ dài vector** | **KHÔNG** (Bất biến với scale) | **CÓ** (Bị ảnh hưởng rất lớn) | **CÓ** (Tỉ lệ thuận với độ lớn vector) |
| **Ứng dụng chính** | **Tìm kiếm ngữ nghĩa văn bản (NLP / RAG / Chatbot)** | Nhận diện hình ảnh, thị giác máy tính, phân cụm không gian địa lý | Hệ thống gợi ý (Recommendation systems), Vector đã chuẩn hóa Unit |

---

## 4. Tại Sao Cosine Similarity Là Lựa Chọn Tối Ưu Nhất Cho Tìm Kiếm Ngữ Nghĩa Văn Bản (RAG)?

Trong bài toán NLP và RAG cho Rikkei CRM:
1. **Sự biến thiên độ dài văn bản (Text Length Variation)**:
   - Câu hỏi của người dùng có thể rất ngắn: *"Đổi hàng trong mấy ngày?"* (7 từ).
   - Đoạn tài liệu trong cơ sở dữ liệu lại rất chi tiết: *"Quy định thời hạn và điều kiện đổi trả hàng hóa..."* (150 từ).
   - Mô hình embedding như `nomic-embed-text` khi mã hóa các đoạn văn có độ dài khác nhau sẽ sinh ra các vector có độ lớn (magnitude) khác nhau.
2. **Ngữ nghĩa nằm ở hướng (Orientation), không nằm ở độ dài (Magnitude)**:
   - Trong không gian vector ngữ nghĩa, hai câu có cùng ý nghĩa sẽ chỉ về **cùng một hướng trong không gian 768 chiều**, bất kể một câu nói ngắn gọn và một câu diễn giải dài dòng.
   - **Cosine Similarity chỉ đo góc giữa 2 vector**, do đó nó nhận diện chính xác sự tương đồng về mặt ý nghĩa mà không bị đánh lừa bởi độ dài của câu.
   - Nếu dùng khoảng cách L2, câu hỏi ngắn và đoạn văn dài sẽ có khoảng cách L2 rất lớn, dẫn đến việc bỏ sót tài liệu chính xác (False Negative).

---

## 5. Ứng Dụng Trong Cơ Chế RAG Phòng Thủ (Defensive Filtering)

Nhờ đặc tính chuẩn hóa $[0, 1]$ của Cosine Similarity, việc thiết lập ngưỡng **`SimilarityThreshold = 0.75`** mang lại các lợi thế vượt trội:
* **Tính trực quan (Intuitive Threshold)**: Điểm $0.85$ nghĩa là độ tương đồng ngữ nghĩa đạt $85\%$; điểm $0.40$ nghĩa là hoàn toàn không liên quan.
* **Ngắt mạch LLM (Circuit Breaking)**:
  - Khi người dùng hỏi *"Làm thế nào để học Java?"*, mô hình embedding sinh ra vector nằm ở không gian Lập trình / Công nghệ, trong khi tài liệu CRM nằm ở không gian Bán lẻ / Chính sách đổi trả.
  - Điểm Cosine Similarity giữa 2 vector này chỉ đạt khoảng $\approx 0.35 - 0.45$.
  - Do $< 0.75$, hệ thống ngay lập tức lọc bỏ toàn bộ, kích hoạt cơ chế phòng thủ và trả lời câu thông báo an toàn mà **không cần gọi LLM**, loại trừ $100\%$ hiện tượng ảo tưởng thông tin.

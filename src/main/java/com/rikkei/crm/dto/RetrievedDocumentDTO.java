package com.rikkei.crm.dto;

import java.util.Map;

/**
 * DTO chứa thông tin đoạn tài liệu trích xuất từ Vector Store kèm độ tương đồng
 */
public class RetrievedDocumentDTO {

    private String id;
    private String content;
    private double similarityScore;
    private String sourceFile;
    private String category;
    private Map<String, Object> metadata;

    public RetrievedDocumentDTO() {}

    public RetrievedDocumentDTO(String id, String content, double similarityScore, String sourceFile,
                                String category, Map<String, Object> metadata) {
        this.id = id;
        this.content = content;
        this.similarityScore = similarityScore;
        this.sourceFile = sourceFile;
        this.category = category;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "RetrievedDocumentDTO{" +
                "score=" + String.format("%.4f", similarityScore) +
                ", file='" + sourceFile + '\'' +
                ", category='" + category + '\'' +
                ", preview='" + (content != null ? (content.length() > 60 ? content.substring(0, 60) + "..." : content) : "") + '\'' +
                '}';
    }
}

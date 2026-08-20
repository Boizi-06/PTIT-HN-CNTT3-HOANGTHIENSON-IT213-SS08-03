package com.rikkei.crm.dto;

import java.util.List;

/**
 * DTO kết quả phản hồi của RAG Retrieval Service
 */
public class RAGQueryResponse {

    private String query;
    private String answer;
    private boolean isFallback;
    private boolean llmCalled;
    private double similarityThresholdUsed;
    private int topKRequested;
    private int retrievedDocumentsCount;
    private List<RetrievedDocumentDTO> retrievedDocuments;
    private long executionTimeMs;

    public RAGQueryResponse() {}

    public RAGQueryResponse(String query, String answer, boolean isFallback, boolean llmCalled,
                            double similarityThresholdUsed, int topKRequested,
                            int retrievedDocumentsCount, List<RetrievedDocumentDTO> retrievedDocuments,
                            long executionTimeMs) {
        this.query = query;
        this.answer = answer;
        this.isFallback = isFallback;
        this.llmCalled = llmCalled;
        this.similarityThresholdUsed = similarityThresholdUsed;
        this.topKRequested = topKRequested;
        this.retrievedDocumentsCount = retrievedDocumentsCount;
        this.retrievedDocuments = retrievedDocuments;
        this.executionTimeMs = executionTimeMs;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isFallback() {
        return isFallback;
    }

    public void setFallback(boolean fallback) {
        isFallback = fallback;
    }

    public boolean isLlmCalled() {
        return llmCalled;
    }

    public void setLlmCalled(boolean llmCalled) {
        this.llmCalled = llmCalled;
    }

    public double getSimilarityThresholdUsed() {
        return similarityThresholdUsed;
    }

    public void setSimilarityThresholdUsed(double similarityThresholdUsed) {
        this.similarityThresholdUsed = similarityThresholdUsed;
    }

    public int getTopKRequested() {
        return topKRequested;
    }

    public void setTopKRequested(int topKRequested) {
        this.topKRequested = topKRequested;
    }

    public int getRetrievedDocumentsCount() {
        return retrievedDocumentsCount;
    }

    public void setRetrievedDocumentsCount(int retrievedDocumentsCount) {
        this.retrievedDocumentsCount = retrievedDocumentsCount;
    }

    public List<RetrievedDocumentDTO> getRetrievedDocuments() {
        return retrievedDocuments;
    }

    public void setRetrievedDocuments(List<RetrievedDocumentDTO> retrievedDocuments) {
        this.retrievedDocuments = retrievedDocuments;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
}

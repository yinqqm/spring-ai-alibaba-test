package com.ai.demo.agent;

import java.util.List;

public class TextAnalysisResult {
    private String summary;
    private List<String> keywords;
    private String sentiment;
    private Double confidence;
    private String originalText;
    private Double spentTimeSeconds;

    public Double getSpentTimeSeconds() {
        return spentTimeSeconds;
    }

    public void setSpentTimeSeconds(Double spentTimeSeconds) {
        this.spentTimeSeconds = spentTimeSeconds;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}

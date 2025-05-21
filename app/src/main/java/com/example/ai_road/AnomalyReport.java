package com.example.ai_road;

public class AnomalyReport {
    public String sessionName;
    public String timestamp;
    public String impact;

    public AnomalyReport(String sessionName, String timestamp, String impact) {
        this.sessionName = sessionName;
        this.timestamp = timestamp;
        this.impact = impact;
    }
}

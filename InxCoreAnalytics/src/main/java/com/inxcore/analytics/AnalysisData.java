package com.inxcore.analytics;

import java.io.Serializable;
import java.util.List;

public class AnalysisData implements Serializable {

    private List<String> fileName;
    private String data;

    public List<String> getFileName() {
        return fileName;
    }

    public void setFileName(List<String> fileName) {
        this.fileName = fileName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

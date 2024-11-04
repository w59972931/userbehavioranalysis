package com.inxcore.analytics;

import org.json.JSONObject;

import java.io.Serializable;

public class AnalysisResult implements Serializable {


    private int code;

    private JSONObject jsonObject;
    private AnalysisData analysis;
    private String jsonEncryptData;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public AnalysisData getAnalysis() {
        return analysis;
    }

    public void setAnalysis(AnalysisData analysis) {
        this.analysis = analysis;
    }

    public String getJsonEncryptData() {
        return jsonEncryptData;
    }

    public void setJsonEncryptData(String jsonEncryptData) {
        this.jsonEncryptData = jsonEncryptData;
    }
}

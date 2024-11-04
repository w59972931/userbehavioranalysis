package com.inxcore.analytics;

import org.json.JSONObject;

public class AnalysisResult {


    private int code;

    private JSONObject jsonObject;
    private AnalysisData analysis;

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
}

package com.inxcore.analytics;

import android.app.Activity;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class UserBehaviorAnalysisUtils {
    static JSONObject object(Object... params) {
        JSONObject object = new JSONObject();
        for (int i = 0; i < params.length / 2; i++) {
            if (params[2 * i] != null && params[2 * i + 1] != null) {
                try {
                    object.put(params[2 * i].toString(), params[2 * i + 1]);
                } catch (JSONException ignored) {
                }
            }
        }
        return object;
    }

    public static String getPageName(Activity activity) {
        if (activity instanceof UserBehaviorAnalysisActivity) {
            return ((UserBehaviorAnalysisActivity) activity).getPageName();
        }
        CharSequence title = activity.getTitle();
        if (!TextUtils.isEmpty(title)) {
            return title.toString();
        }
        return activity.getLocalClassName();
    }
}

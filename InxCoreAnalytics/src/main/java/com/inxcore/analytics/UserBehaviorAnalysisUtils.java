package com.inxcore.analytics;

import android.app.Activity;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

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

    private static Key toKey(byte[] key) {
        return new SecretKeySpec(key, "AES");
    }

    public static String bytes2String(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String encryptAes(String data,String aesKey) {
        try {
            Key k = toKey(aesKey.getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, k);
            return bytes2String(cipher.doFinal(data.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String gzipCompress(String str) {
        try {
            if (str == null || str.length() == 0) {
                return str;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
            return out.toString("ISO-8859-1");
        }catch (Exception e){

        }
        return "";
    }
}

package com.inxcore.analytics;

import static com.inxcore.analytics.UserBehaviorAnalysis.USER_BEHAVIOR_ANALYSIS_LEVEL_ELEMENT;
import static com.inxcore.analytics.UserBehaviorAnalysis.USER_BEHAVIOR_ANALYSIS_LEVEL_INPUT;
import static com.inxcore.analytics.UserBehaviorAnalysis.USER_BEHAVIOR_ANALYSIS_LEVEL_SELECT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
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
            Key k = new SecretKeySpec(aesKey.getBytes("UTF-8"), "AES");
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

    public static void elementEvent(Context activity,String pageName,UserBehaviorAnalysisElement uba){
        if (uba == null){
            return;
        }
        try {
            String elementName = uba.elementName;
            View.OnTouchListener touchListener = uba.touchListener;
            View view = uba.view;
            if(view instanceof EditText){
                EditText editText = (EditText)view;
                View.OnFocusChangeListener listener = uba.focusChangeListener;
                ActionMode.Callback callback = uba.callback;
                editText.setOnFocusChangeListener((eview, b) -> {
                    if (b) {
                        uba.startTime = System.currentTimeMillis();
                        uba.startValue = editText.getText().toString();
                    } else {
                        uba.endTime = System.currentTimeMillis();
                        uba.endValue = editText.getText().toString();
                        if(!uba.startValue.equals(uba.endValue)){
                            UserBehaviorAnalysis.onInputChange(activity, pageName, elementName, uba.startValue, uba.endValue, uba.startTime, uba.endTime, uba.parseTimes, uba.deleteTimes);
                        }
                    }
                    if (listener != null) {
                        listener.onFocusChange(eview, b);
                    }
                });
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (i1 > i2) {
                            uba.deleteTimes++;
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return callback == null || callback.onCreateActionMode(mode, menu);
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        uba.beforeParseValue = editText.getText().toString();
                        return callback != null && callback.onPrepareActionMode(mode, menu);
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case android.R.id.copy: {
                                Editable editable = editText.getText();
                                int selectionStart = editText.getSelectionStart();
                                int selectionEnd = editText.getSelectionEnd();
                                if (selectionStart != selectionEnd) {
                                    UserBehaviorAnalysis.onInputCopy(activity, pageName, elementName, editable.toString(), editable.subSequence(selectionStart, selectionEnd).toString());
                                }
                                break;
                            }
                            case android.R.id.paste: {
                                uba.parseTimes++;
                                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                                if (clipboardManager.hasPrimaryClip()) {
                                    ClipData clipData = clipboardManager.getPrimaryClip();
                                    if (clipData != null && clipData.getItemCount() > 0) {
                                        ClipData.Item itemPasted = clipData.getItemAt(0);
                                        if (itemPasted != null && itemPasted.getText() != null) {
                                            UserBehaviorAnalysis.onInputParse(activity, pageName, elementName, uba.beforeParseValue, editText.getText().toString(), itemPasted.getText().toString());
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        return callback != null && callback.onActionItemClicked(mode, item);
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        if (uba.callback != null) {
                            uba.callback.onDestroyActionMode(mode);
                        }
                    }
                });
            }

            view.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if(uba.levelType != null){
                            if(USER_BEHAVIOR_ANALYSIS_LEVEL_ELEMENT.equals(uba.levelType)){
                                UserBehaviorAnalysis.onElementClick(activity, pageName, elementName, motionEvent.getX(), motionEvent.getY());
                            }else if(USER_BEHAVIOR_ANALYSIS_LEVEL_SELECT.equals(uba.levelType)){
                                UserBehaviorAnalysis.onSelectClick(activity, pageName, elementName, motionEvent.getX(), motionEvent.getY());
                            }else if(USER_BEHAVIOR_ANALYSIS_LEVEL_INPUT.equals(uba.levelType)){
                                UserBehaviorAnalysis.onInputClick(activity, pageName, elementName, motionEvent.getX(), motionEvent.getY());
                            }
                        }else{
                            if(view instanceof EditText){
                                UserBehaviorAnalysis.onInputClick(activity, pageName, elementName, motionEvent.getX(), motionEvent.getY());
                            }else{
                                UserBehaviorAnalysis.onElementClick(activity, pageName, elementName, motionEvent.getX(), motionEvent.getY());
                            }
                        }
                    }
                    if (touchListener != null) {
                        touchListener.onTouch(view, motionEvent);
                    }

                    boolean isClick = false;
                    if(view instanceof EditText){
                        isClick = !view.isEnabled();
                    }

                    if(!(view instanceof EditText) || isClick){
                        for(EditText editText: UserBehaviorAnalysisCallbacks.elementEdit){
                            if(editText.hasFocus()){
                                editText.clearFocus();
                            }
                        }
                    }
                    return false;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

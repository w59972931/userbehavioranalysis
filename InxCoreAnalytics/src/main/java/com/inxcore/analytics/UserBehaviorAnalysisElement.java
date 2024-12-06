package com.inxcore.analytics;

import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class UserBehaviorAnalysisElement implements Serializable {
    public final String elementName;
    public final View view;
    public String beforeParseValue;
    public long startTime;
    public String startValue;
    public long endTime;
    public String endValue;
    public int deleteTimes;
    public final String levelType;
    public int parseTimes;
    @Nullable
    public final View.OnFocusChangeListener focusChangeListener;
    @Nullable
    public final ActionMode.Callback callback;
    @Nullable
    public final View.OnTouchListener touchListener;

    public UserBehaviorAnalysisElement(String elementName, EditText view, @Nullable View.OnTouchListener touchListener, @Nullable View.OnFocusChangeListener focusChangeListener, @Nullable ActionMode.Callback callback) {
        this.elementName = elementName;
        this.view = view;
        this.touchListener = touchListener;
        this.focusChangeListener = focusChangeListener;
        this.callback = callback;
        this.levelType = null;
    }

    public UserBehaviorAnalysisElement(String elementName, View view, @Nullable View.OnTouchListener touchListener) {
        this.elementName = elementName;
        this.view = view;
        this.touchListener = touchListener;
        focusChangeListener = null;
        callback = null;
        this.levelType = null;
    }

    public UserBehaviorAnalysisElement(String elementName, View view) {
        this.elementName = elementName;
        this.view = view;
        this.touchListener = null;
        this.focusChangeListener = null;
        this.callback = null;
        this.levelType = null;
    }

    public UserBehaviorAnalysisElement(String elementName, View view,String levelType) {
        this.elementName = elementName;
        this.view = view;
        this.touchListener = null;
        this.focusChangeListener = null;
        this.callback = null;
        this.levelType = levelType;
    }
}

package com.inxcore.analytics;

import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class UserBehaviorAnalysisElement {
    public final String elementName;
    public final View view;
    public String beforeParseValue;
    public long startTime;
    public String startValue;
    public long endTime;
    public String endValue;
    public int deleteTimes;
    public int parseTimes;
    public final EditText editText;
    @Nullable
    public final View.OnFocusChangeListener focusChangeListener;
    @Nullable
    public final ActionMode.Callback callback;
    @Nullable
    public final View.OnTouchListener touchListener;

    public UserBehaviorAnalysisElement(String elementName, EditText view, @Nullable View.OnTouchListener touchListener, @Nullable View.OnFocusChangeListener focusChangeListener, @Nullable ActionMode.Callback callback) {
        this.elementName = elementName;
        this.view = view;
        this.editText = view;
        this.touchListener = touchListener;
        this.focusChangeListener = focusChangeListener;
        this.callback = callback;
    }

    public UserBehaviorAnalysisElement(String elementName, View view, @Nullable View.OnTouchListener touchListener) {
        this.elementName = elementName;
        this.view = view;
        editText = null;
        this.touchListener = touchListener;
        focusChangeListener = null;
        callback = null;
    }
}

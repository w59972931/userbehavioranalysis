package com.inxcore.analytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class UserBehaviorAnalysisCallbacks implements Application.ActivityLifecycleCallbacks {
    private int activityCreatedCount = 0;
    int activityStartedCount = 0;
    private boolean isAppInForeground = true;
    private static final Map<Activity, Boolean> showStatus = new HashMap<>();
    private static final Map<Activity, Boolean> hideStatus = new HashMap<>();

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        Log.d("onActivityCreated", activity.toString());
        activityCreatedCount++;
        showStatus.put(activity, false);
        hideStatus.put(activity, false);
    }

    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        Log.d("onActivityPostCreated", activity.toString());
        String pageName = UserBehaviorAnalysisUtils.getPageName(activity);
        UserBehaviorAnalysis.onPageOpen(activity, pageName);
        if (activity instanceof UserBehaviorAnalysisActivity) {
            UserBehaviorAnalysisActivity analysisActivity = (UserBehaviorAnalysisActivity) activity;
            for (UserBehaviorAnalysisElement uba : analysisActivity.getElementList()) {

                if(uba.view == null){
                    continue;
                }
                UserBehaviorAnalysisUtils.elementEvent(activity,pageName,uba);
            }
        }
        activity.getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    UserBehaviorAnalysis.onElementClick(activity, pageName, null, motionEvent.getX(), motionEvent.getY());
                }
                return true;
            }
        });
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.d("onActivityStarted", activity.toString());
        showStatus.put(activity, true);
        show(activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.d("onActivityResumed", activity.toString());
        if (Boolean.TRUE.equals(showStatus.get(activity))) {
            showStatus.put(activity, false);
        } else {
            show(activity);
        }
    }

    private void show(Activity activity) {
        UserBehaviorAnalysis.onPageShow(activity, UserBehaviorAnalysisUtils.getPageName(activity));
        if (!isAppInForeground) {
            isAppInForeground = true;
            UserBehaviorAnalysis.onApplicationShow(activity);
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.d("onActivityPaused", activity.toString());
        hideStatus.put(activity, true);
        hide(activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.d("onActivityStopped", activity.toString());
        if (Boolean.TRUE.equals(hideStatus.get(activity))) {
            hideStatus.put(activity, false);
        } else {
            hide(activity);
        }
    }

    private void hide(Activity activity) {
        if (!activity.isFinishing()) {
            UserBehaviorAnalysis.onPageHide(activity, UserBehaviorAnalysisUtils.getPageName(activity));
            new Handler(Looper.getMainLooper()).postDelayed(() -> new Thread(() -> {
                if (activityStartedCount == 0) {
                    isAppInForeground = false;
                    UserBehaviorAnalysis.onApplicationHide(activity);
                }
            }).start(), 1000);
        }
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.d("onActivityDestroyed", activity.toString());
        if (activity instanceof UserBehaviorAnalysisActivity) {
            for (UserBehaviorAnalysisElement uba : ((UserBehaviorAnalysisActivity) activity).getElementList()) {
                if(uba.view == null){
                    continue;
                }
                if(uba.view instanceof EditText){
                    EditText editText = (EditText)uba.view;
                    editText.setOnFocusChangeListener(null);
                    editText.setCustomSelectionActionModeCallback(null);
                }
                uba.view.setOnTouchListener(null);

            }
        }
        activityCreatedCount--;
        UserBehaviorAnalysis.onPageExit(activity, UserBehaviorAnalysisUtils.getPageName(activity));
        if (activityCreatedCount == 0) {
            UserBehaviorAnalysis.onApplicationExit(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Log.d("onActivitySaveInstanceState", activity.toString());
    }
}

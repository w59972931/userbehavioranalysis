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
                String elementName = uba.elementName;
                EditText editText = uba.editText;
                if (editText != null) {
                    View.OnFocusChangeListener listener = uba.focusChangeListener;
                    ActionMode.Callback callback = uba.callback;
                    editText.setOnFocusChangeListener((view, b) -> {
                        if (b) {
                            uba.startTime = System.currentTimeMillis();
                            uba.startValue = editText.getText().toString();
                        } else {
                            uba.endTime = System.currentTimeMillis();
                            uba.endValue = editText.getText().toString();
                            UserBehaviorAnalysis.onInputChange(activity, pageName, elementName, uba.startValue, uba.endValue, uba.startTime, uba.endTime, uba.parseTimes, uba.deleteTimes);
                        }
                        if (listener != null) {
                            listener.onFocusChange(view, b);
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
                View view = uba.view;
                View.OnTouchListener listener = uba.touchListener;
                view.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            UserBehaviorAnalysis.onElementClick(activity, pageName, elementName, motionEvent.getX(), motionEvent.getY());
                        }
                        if (listener != null) {
                            listener.onTouch(view, motionEvent);
                        }
                        return false;
                    }
                });
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
                EditText editText = uba.editText;
                if (editText != null) {
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

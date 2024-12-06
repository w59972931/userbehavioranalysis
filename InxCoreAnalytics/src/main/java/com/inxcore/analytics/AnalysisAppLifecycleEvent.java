package com.inxcore.analytics;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class AnalysisAppLifecycleEvent implements LifecycleEventObserver {

    private Context context;

    public AnalysisAppLifecycleEvent(Context context){
        this.context = context;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
        Log.d("onApplicationState", event.toString());
        switch (event){
            case ON_START:
                UserBehaviorAnalysis.onApplicationShow(context);
                break;
            case ON_RESUME:
                break;
            case ON_PAUSE:
                break;
            case ON_STOP:
                UserBehaviorAnalysis.onApplicationHide(context);
                break;
        }
    }
}

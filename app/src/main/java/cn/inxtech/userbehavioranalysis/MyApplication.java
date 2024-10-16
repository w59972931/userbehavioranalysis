package cn.inxtech.userbehavioranalysis;

import android.app.Application;

import com.inxcore.analytics.UserBehaviorAnalysis;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化，如果auto为true则自动捕获所有实现了UserBehaviorAnalysisActivity接口的页面
        //可以调用UserBehaviorAnalysis.onXxx执行手动触发
        UserBehaviorAnalysis.init(this, true);
    }
}

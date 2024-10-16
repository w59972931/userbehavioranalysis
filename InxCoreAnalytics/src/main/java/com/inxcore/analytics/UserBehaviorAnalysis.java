package com.inxcore.analytics;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.ArraySet;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;

public class UserBehaviorAnalysis {
    private static final String USER_BEHAVIOR_ANALYSIS = "UserBehaviorAnalysis";
    private static final String USER_BEHAVIOR_ANALYSIS_LIST = "List";
    private static final String USER_BEHAVIOR_ANALYSIS_DIR = "user_behavior_analysis";
    private static final String USER_BEHAVIOR_ANALYSIS_START_TIME = "StartTime";
    private static final String USER_BEHAVIOR_ANALYSIS_LEVEL = "level";
    private static final String USER_BEHAVIOR_ANALYSIS_LEVEL_SYSTEM = "system";
    private static final String USER_BEHAVIOR_ANALYSIS_LEVEL_APPLICATION = "application";
    private static final String USER_BEHAVIOR_ANALYSIS_LEVEL_PAGE = "page";
    private static final String USER_BEHAVIOR_ANALYSIS_LEVEL_ELEMENT = "element";
    private static final String USER_BEHAVIOR_ANALYSIS_LEVEL_INPUT = "input";
    private static final String USER_BEHAVIOR_ANALYSIS_LEVEL_SELECT = "select";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE = "type";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_PERFORMANCE = "performance";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_THEME = "theme";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_OPEN = "open";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_EXIT = "exit";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_HIDE = "hide";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_SHOW = "show";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_REFRESH = "refresh";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_CRASH = "crash";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_CLICK = "click";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_COPY = "copy";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_PARSE = "parse";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_CHANGE = "change";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_PICK = "pick";
    private static final String USER_BEHAVIOR_ANALYSIS_TYPE_CANCEL = "cancel";
    private static final String USER_BEHAVIOR_ANALYSIS_PAGE_NAME = "pageName";
    private static final String USER_BEHAVIOR_ANALYSIS_ELEMENT_NAME = "elementName";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA = "data";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA_X = "x";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA_Y = "y";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA_VALUE = "value";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA_COPY_TEXT = "copyText";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA_PARSE_TEXT = "parseText";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA_START_VALUE = "startValue";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA_END_VALUE = "endVALUE";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA_START_TIME = "startTime";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA_END_TIME = "endTime";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA_PARSE_TIMES = "parseTimes";
    private static final String USER_BEHAVIOR_ANALYSIS_DATA_DELETE_TIMES = "deleteTimes";

    private static final int MAX_RECORD_SIZE = 50;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static int currentTheme;

    public static void init(Application application, boolean auto) {
        sharedPreferences = application.getSharedPreferences(USER_BEHAVIOR_ANALYSIS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Set<String> list = sharedPreferences.getStringSet(USER_BEHAVIOR_ANALYSIS_LIST, null);
        if (list != null) {
            Long currentTimeMillis = System.currentTimeMillis();
            writeToFile(application, currentTimeMillis, sharedPreferences.getLong(USER_BEHAVIOR_ANALYSIS_START_TIME, 0L), list);
        }
        if (auto) {
            onApplicationOpen(application);
            application.registerActivityLifecycleCallbacks(new UserBehaviorAnalysisCallbacks());
            Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> onApplicationCrash(application));
        }
        currentTheme = application.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    public static void onApplicationPerformanceChange(Context context) {
        // TODO 读取性能参数
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_SYSTEM, USER_BEHAVIOR_ANALYSIS_TYPE_PERFORMANCE);
    }

    public static void onApplicationThemeChange(Context context) {
        // TODO 读取主题参数
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_SYSTEM, USER_BEHAVIOR_ANALYSIS_TYPE_THEME);
    }

    private static void onApplicationOpen(Context context) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_APPLICATION, USER_BEHAVIOR_ANALYSIS_TYPE_OPEN);
    }

    public static void onApplicationExit(Context context) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_APPLICATION, USER_BEHAVIOR_ANALYSIS_TYPE_EXIT);
    }

    public static void onApplicationHide(Context context) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_APPLICATION, USER_BEHAVIOR_ANALYSIS_TYPE_HIDE);
    }

    public static void onApplicationShow(Context context) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_APPLICATION, USER_BEHAVIOR_ANALYSIS_TYPE_SHOW);
    }

    public static void onApplicationCrash(Context context) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_APPLICATION, USER_BEHAVIOR_ANALYSIS_TYPE_CRASH);
    }

    public static void onPageOpen(Context context, String pageName) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_PAGE, USER_BEHAVIOR_ANALYSIS_TYPE_EXIT, pageName);
    }

    public static void onPageExit(Context context, String pageName) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_PAGE, USER_BEHAVIOR_ANALYSIS_TYPE_EXIT, pageName);
    }

    //被其他 Activity 遮挡或对话框显示在前面
    //被其他 Activity 覆盖或者设备进入休眠状态
    public static void onPageHide(Context context, String pageName) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_PAGE, USER_BEHAVIOR_ANALYSIS_TYPE_HIDE, pageName);
    }

    public static void onPageShow(Context context, String pageName) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_PAGE, USER_BEHAVIOR_ANALYSIS_TYPE_SHOW, pageName);
    }

    public static void onPageRefresh(Context context, String pageName) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_PAGE, USER_BEHAVIOR_ANALYSIS_TYPE_REFRESH, pageName);
    }

    public static void onElementClick(Context context, String pageName, String elementName, float x, float y) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_ELEMENT, USER_BEHAVIOR_ANALYSIS_TYPE_CLICK, pageName, elementName, UserBehaviorAnalysisUtils.object(USER_BEHAVIOR_ANALYSIS_DATA_X, x, USER_BEHAVIOR_ANALYSIS_DATA_Y, y));
    }

    public static void onInputCopy(Context context, String pageName, String elementName, String value, String copyText) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_INPUT, USER_BEHAVIOR_ANALYSIS_TYPE_COPY, pageName, elementName, UserBehaviorAnalysisUtils.object(USER_BEHAVIOR_ANALYSIS_DATA_VALUE, value, USER_BEHAVIOR_ANALYSIS_DATA_COPY_TEXT, copyText));
    }

    public static void onInputParse(Context context, String pageName, String elementName, String startValue, String endValue, String parseText) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_INPUT, USER_BEHAVIOR_ANALYSIS_TYPE_PARSE, pageName, elementName, UserBehaviorAnalysisUtils.object(USER_BEHAVIOR_ANALYSIS_DATA_START_VALUE, startValue, USER_BEHAVIOR_ANALYSIS_DATA_END_VALUE, endValue, USER_BEHAVIOR_ANALYSIS_DATA_PARSE_TEXT, parseText));
    }

    public static void onInputChange(Context context, String pageName, String elementName, String startValue, String endValue, Long startTime, Long endTime, int parseTimes, int deleteTimes) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_INPUT, USER_BEHAVIOR_ANALYSIS_TYPE_CHANGE, pageName, elementName, UserBehaviorAnalysisUtils.object(
                USER_BEHAVIOR_ANALYSIS_DATA_START_VALUE, startValue,
                USER_BEHAVIOR_ANALYSIS_DATA_END_VALUE, endValue,
                USER_BEHAVIOR_ANALYSIS_DATA_START_TIME, startTime,
                USER_BEHAVIOR_ANALYSIS_DATA_END_TIME, endTime,
                USER_BEHAVIOR_ANALYSIS_DATA_PARSE_TIMES, parseTimes,
                USER_BEHAVIOR_ANALYSIS_DATA_DELETE_TIMES, deleteTimes
        ));
    }

    public static void onSelectShow(Context context, String pageName, String elementName) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_SELECT, USER_BEHAVIOR_ANALYSIS_TYPE_SHOW, pageName, elementName);
    }

    public static void onSelectChange(Context context, String pageName, String elementName) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_SELECT, USER_BEHAVIOR_ANALYSIS_TYPE_CHANGE, pageName, elementName);
    }

    public static void onSelectPick(Context context, String pageName, String elementName) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_SELECT, USER_BEHAVIOR_ANALYSIS_TYPE_PICK, pageName, elementName);
    }

    public static void onSelectCancel(Context context, String pageName, String elementName) {
        addRecord(context, USER_BEHAVIOR_ANALYSIS_LEVEL_SELECT, USER_BEHAVIOR_ANALYSIS_TYPE_CANCEL, pageName, elementName);
    }

    private static void retry(Context context) {
        File retryDir = new File(context.getFilesDir(), "retry");
        File[] files = retryDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".dat");
            }
        });

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    Log.d("Dat File Name", file.getName());
                }
            }
        } else {
            Log.i("Info", "Directory is empty or does not exist.");
        }
    }


    private static void writeToFile(Context context, Long timestamp, Long startTime, Set<String> content) {
        try {
            File directory = new File(context.getFilesDir(), USER_BEHAVIOR_ANALYSIS_DIR);
            if (!directory.exists()) {
                if (!directory.mkdirs()) return;
            }
            File file = new File(directory, timestamp + ".dat");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.toString().getBytes());
            fos.close();
        } catch (IOException ignored) {
        }
    }

    static void addRecord(Context context, String level, String type) {
        addRecord(context, level, type, null, null, null);
    }

    static void addRecord(Context context, String level, String type, JSONObject data) {
        addRecord(context, level, type, null, null, data);
    }

    static void addRecord(Context context, String level, String type, String pageName) {
        addRecord(context, level, type, pageName, null, null);
    }

    static void addRecord(Context context, String level, String type, String pageName, JSONObject data) {
        addRecord(context, level, type, pageName, null, data);
    }

    static void addRecord(Context context, String level, String type, String pageName, String elementName) {
        addRecord(context, level, type, pageName, elementName, null);
    }

    static void addRecord(Context context, String level, String type, String pageName, String elementName, JSONObject data) {
        Log.i("AddRecord", String.format("%s %s %s %s : %s", level, type, pageName, elementName, data == null ? "" : data.toString()));
        Set<String> list = new ArraySet<>();
        Set<String> stringSet = sharedPreferences.getStringSet(USER_BEHAVIOR_ANALYSIS_LIST, null);
        if (stringSet == null) {
            editor.putLong("start_time", System.currentTimeMillis());
        } else {
            list.addAll(stringSet);
        }
        list.add(UserBehaviorAnalysisUtils.object(USER_BEHAVIOR_ANALYSIS_LEVEL, level, USER_BEHAVIOR_ANALYSIS_TYPE, type, USER_BEHAVIOR_ANALYSIS_PAGE_NAME, pageName, USER_BEHAVIOR_ANALYSIS_ELEMENT_NAME, elementName, USER_BEHAVIOR_ANALYSIS_DATA, data).toString());
        if (list.size() >= MAX_RECORD_SIZE) {
            writeToFile(context, System.currentTimeMillis(), sharedPreferences.getLong(USER_BEHAVIOR_ANALYSIS_START_TIME, 0L), list);
            editor.putStringSet(USER_BEHAVIOR_ANALYSIS_LIST, null).apply();
        } else {
            editor.putStringSet(USER_BEHAVIOR_ANALYSIS_LIST, list).apply();
        }
    }
}
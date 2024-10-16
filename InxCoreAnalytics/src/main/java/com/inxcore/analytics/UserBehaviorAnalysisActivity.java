package com.inxcore.analytics;

import java.util.List;

public interface UserBehaviorAnalysisActivity {
    String getPageName();

    List<UserBehaviorAnalysisElement> getElementList();
}

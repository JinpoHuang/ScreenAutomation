package com.jinpo.screenautomation;

import android.app.Application;

import com.jinpo.screenautomationlib.ScreenAutomation;

/**
 * Autho: huang jinpo
 * User: 14214
 * Date: 2019/8/12 14:19
 * Function:
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ScreenAutomation.init(this);
    }
}

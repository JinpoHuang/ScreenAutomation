package com.jinpo.screenautomationlib;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * Autho: huang jinpo
 * User: 14214
 * Date: 2019/8/10 16:35
 * Function:
 */

public class ScreenAutomation {
    private static final String TAG="ScreenAutomation";
    private static Application mApplication;
    //设计图中的屏幕宽度
    //通过清单文件中application的meta-data获取
    private static final String SCREEN_WIDTH_DP = "screen_width_dp";
    //通过初始化获取
    private static float screen_width = 0.0F;
    //是否是debug版本，用来判断是否打印日志
    public static boolean isDebug = false;
    //DisplayMetrics的默认参数
    private static float oldDensity = -1.0F;
    private static int oldDensityDpi = -1;
    private static float oldScaledDensity = -1.0F;
    //设置DisplayMetrics的新参数
    private static float newDensity = -1.0F;
    private static int newDensityDpi = -1;
    private static float newScaledDensity = -1.0F;
    /*
    * DisplayMetrics参数：
    * densityDpi：屏幕密度，每英寸的像素数；
    * density：密度比值，densityDpi/160;不同手机dp换算px就是通过该值；
    * scaledDensity:同density，用于文字缩放的计算，也就是sp。
    * */

    public ScreenAutomation(){

    }

    public static void init(@NonNull Application application, float screenWidth, boolean debug){

    }




}

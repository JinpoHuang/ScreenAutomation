package com.jinpo.screenautomationlib;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Autho: huang jinpo
 * User: 14214
 * Date: 2019/8/10 16:35
 * Function:
 */

public class ScreenAutomation {
    private static final String TAG = "ScreenAutomation";
    private static Application mApplication;
    //设计图中的屏幕宽度
    //通过清单文件中application的meta-data获取
    private static final String SCREEN_WIDTH_DP = "screen_width_dp";
    private static final String IS_DEBUG = "isdebug";
    //通过初始化获取(单位是dp)
    private static float screen_width = 0.0F;
    //是否是debug版本，用来判断是否打印日志
    public static boolean isDebug = false;
    //DisplayMetrics的默认参数
    private static int oldDensityDpi = -1;
    private static float oldDensity = -1.0F;
    private static float oldScaledDensity = -1.0F;
    //设置DisplayMetrics的新参数
    private static int newDensityDpi = -1;
    private static float newDensity = -1.0F;
    private static float newScaledDensity = -1.0F;
    /*
    * DisplayMetrics参数：
    * densityDpi：屏幕密度，每英寸的像素数；
    * density：密度比值，densityDpi/160;不同手机dp换算px就是通过该值(px=density*dp)；
    * scaledDensity:同density，用于文字缩放的计算，也就是sp。
    * */

    public ScreenAutomation() {

    }

    //在自定义Application onCreate中初始化
    public static void init(@NonNull Application application) {
        init(application, 0.0F);
    }

    public static void init(@NonNull Application application, boolean debug) {
        init(application, 0.0F, debug);
    }

    public static void init(@NonNull Application application, float screenWidth) {
        init(application, screenWidth, false);
    }

    public static void init(@NonNull Application application, float screenWidth, boolean debug) {
        isDebug = debug;
        if (screenWidth != 0.0F) {
            //通过初始化获取
            screen_width = screenWidth;
        } else {
            //通过清单文件中application的meta-data获取
            try {
                ApplicationInfo applicationInfo = application.getPackageManager().
                        getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
                screen_width = (float) applicationInfo.metaData.getInt(SCREEN_WIDTH_DP, 0);
                isDebug=applicationInfo.metaData.getBoolean(IS_DEBUG,false);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (screen_width == 0.0F) {
            throw new SecurityException("----------screen_width not null----------");
        } else {
            if (mApplication == null) {
                mApplication = application;
            }
            initApplication();
        }
    }

    /*
    * 适配方法说明：
    * 布局文件中的dp，最后都会转化为px(px=dp*density)
    * 默认方法：通过屏幕的宽高和尺寸计算dpi(dpi=sqrt(w*w+h*h)/尺寸);
    *           通过dpi计算密度比值density(density=dpi/160);
    *           因为屏幕的尺寸有很多且没有固定比例，所以计算出来的px占屏幕宽度的比例也不一样
    * 适配方法：通过屏幕的宽和设计图的宽计算dpi(dpi=widthPixels/screen_width);
    *           密度比值density=dpi/160;
    *           px=dp*density=dp*dpi/160=(dp/screen_width)*(widthPixels/160)
    *           就算出来的px与屏幕宽度的比例，和设计图是一样的。
    * */
    //获取默认的DisplayMetrics参数，通过屏幕宽度和设计图宽度计算新的DisplayMetrics参数
    private static void initApplication() {
        DisplayMetrics displayMetrics = mApplication.getResources().getDisplayMetrics();
        if (oldDensity == -1.0F) {
            //dp密度比值
            oldDensity = displayMetrics.density;
            newDensity =(float) displayMetrics.widthPixels / screen_width;
        }
        if (oldDensityDpi == -1) {
            //屏幕密度，每英寸的像素数
            oldDensityDpi = displayMetrics.densityDpi;
            newDensityDpi = (int) (newDensity * 160.0F);
        }
        if (oldScaledDensity == -1.0F) {
            //sp密度比值
            oldScaledDensity = displayMetrics.scaledDensity;
            newScaledDensity = newDensity * (oldScaledDensity / oldDensity);
        }
        mApplication.registerComponentCallbacks(new ComponentCallbacks() {
            //当设备配置信息改变时调用
            @Override
            public void onConfigurationChanged(Configuration configuration) {
                //处理字体比例
                if (configuration != null && configuration.fontScale > 0.0F) {
                    ScreenAutomation.log("设备配置信息有变，字体比例发生改变。");
                    ScreenAutomation.oldScaledDensity = ScreenAutomation.oldDensity * configuration.fontScale;
                    ScreenAutomation.newScaledDensity = ScreenAutomation.newDensity * configuration.fontScale;
                }
            }

            //当整个系统内存不足时调用
            @Override
            public void onLowMemory() {

            }
        });
        log("oldDensity = " + oldDensity);
        log("oldDensityDpi = " + oldDensityDpi);
        log("oldScaledDensity = " + oldScaledDensity);
        log("newDensity : " + displayMetrics.widthPixels + " / " + screen_width + " = " + newDensity);
        log("newDensityDpi : " + newDensity + " * 160 = " + newDensityDpi);
        log("newScaledDensity : " + newDensity + " * (" + oldScaledDensity + " / " + oldDensity + ") = " + newScaledDensity);
    }

    //在Activity中调用设置适配,需要在设置布局(setContentView)之前
    public static void setScreenAuto(@NonNull Activity activity) {
        if (mApplication == null) {
            throw new SecurityException("----------mApplication not null----------");
        } else {
            //通过改变密度比值进行适配
            changeDensity(activity);
        }
    }

    private static void changeDensity(@NonNull Activity activity) {
        DisplayMetrics activityMetrics = activity.getResources().getDisplayMetrics();
        float activityDensity;
        int activityDensityDpi;
        float activityScaledDensity;
        if (activity instanceof ICancelAuto) {
            //取消屏幕适配,使用默认参数
            activityDensity = oldDensity;
            activityDensityDpi = oldDensityDpi;
            activityScaledDensity = oldScaledDensity;
        } else if (activity instanceof IChangeAuto) {
            //自定义屏幕适配，重新设置设计图宽度
            activityDensity = (float) activityMetrics.widthPixels / ((IChangeAuto) activity).newScreenWidth();
            activityDensityDpi = (int) (activityDensity * 160.0F);
            activityScaledDensity = activityDensity * (oldScaledDensity / oldDensity);
        } else {
            //使用全局适配
            activityDensity = newDensity;
            activityDensityDpi = newDensityDpi;
            activityScaledDensity = newScaledDensity;
        }
        //修改activityMetrics的参数，达到适配效果
        activityMetrics.density = activityDensity;
        activityMetrics.densityDpi = activityDensityDpi;
        activityMetrics.scaledDensity = activityScaledDensity;
    }

    //debug下打印日志
    private static void log(String msg) {
        if (isDebug) {
            Log.d("ScreenAutomation", msg);
        }
    }
}
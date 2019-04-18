package com.foryou.longan_api.core;

import android.app.Application;
import android.util.Log;

import com.foryou.longan_api.AppProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Created by liyawei
 * Date:2019/4/11 7:42 PM
 * Email:liyawei@foryou56.com
 */
public class LogisticsCenter {
    private static final String TAG = "LogisticsCenter";
    private static List<AppProxy> appProxies = new ArrayList<>();

    public static void init(Application application) {
        Log.i(TAG, "LogisticsCenter init start ");
        inject();
        for (AppProxy proxy : appProxies) {
            proxy.onCreate(application);
        }
    }

    public static void register(AppProxy proxy) {
        appProxies.add(proxy);
    }

    public static void inject() {
    }
}

package com.foryou.library;

import android.util.Log;

import com.foryou.longan_api.AppProxy;

/**
 * Description:
 * Created by liyawei
 * Date:2019/4/12 12:33 PM
 * Email:liyawei@foryou56.com
 */
public class AppProxyImpl implements AppProxy {
    private static final String TAG = "AppProxyImpl";

    public AppProxyImpl() {
        Log.e(TAG, "sdfasdfasdfasdfasdfsdf");
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "hahahahahahahhahahha");
    }
}

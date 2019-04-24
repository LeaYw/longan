package com.foryou.library;

import android.app.Application;
import android.util.Log;

import me.leayw.longan_api.AppProxy;

/**
 * Description:
 * Created by liyawei
 * Date:2019/4/12 12:33 PM
 * Email:liyawei@foryou56.com
 */
public class AppProxyImpl implements AppProxy {
    private static final String TAG = "AppProxyImpl";

    @Override
    public void onCreate(Application application) {
        Log.e(TAG, "hahahahahahahhahahha" + application.toString());
    }
}

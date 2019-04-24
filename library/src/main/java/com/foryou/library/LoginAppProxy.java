package com.foryou.library;

import android.app.Application;
import android.util.Log;

import me.leayw.longan_api.AppProxy;

/**
 * Description:
 * Created by liyawei
 * Date:2019/4/12 9:18 PM
 * Email:liyawei@foryou56.com
 */
public class LoginAppProxy implements AppProxy {

    private static final String TAG = "LoginAppProxy";

    @Override
    public void onCreate(Application application) {
        Log.e(TAG, "sdfsdfsdfasdfasdfasdfsf");
    }
}

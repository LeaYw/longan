package com.foryou.test;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Description:
 * Created by liyawei
 * Date:2019/1/31 5:33 PM
 * Email:liyawei@foryou56.com
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }
}

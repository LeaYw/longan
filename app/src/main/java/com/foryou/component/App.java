package com.foryou.component;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.foryou.longan_api.Longan;

/**
 * Description:
 * Created by liyawei
 * Date:2019/1/31 10:30 AM
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
        Longan.init(this);
    }
}

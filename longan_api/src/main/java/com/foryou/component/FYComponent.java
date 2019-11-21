package com.foryou.component;

import android.app.Application;
import android.util.Log;

import com.foryou.component.core.LogisticsCenter;

/**
 * Description:
 * Created by liyawei
 * Date:2019/4/11 7:37 PM
 * Email:liyawei@foryou56.com
 */
public class FYComponent {
    private static final String TAG = "FYComponent";

    public static void init(Application application){
        Log.i(TAG, "longan init start");
        LogisticsCenter.init(application);
        Log.i(TAG, "longan init end");
    }
}

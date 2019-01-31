package com.foryou.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.alibaba.android.arouter.facade.annotation.Route;

/**
 * Description:
 * Created by liyawei
 * Date:2019/1/31 10:33 AM
 * Email:liyawei@foryou56.com
 */

@Route(path = "/login/index")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
    }
}

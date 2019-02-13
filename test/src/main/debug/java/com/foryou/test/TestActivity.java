package com.foryou.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.foryou.test.R;

/**
 * Description:
 * Created by liyawei
 * Date:2019/1/31 5:34 PM
 * Email:liyawei@foryou56.com
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        TextView view = findViewById(R.id.textView);
        view.setText(BuildConfig.VERSION_NAME);
    }
}

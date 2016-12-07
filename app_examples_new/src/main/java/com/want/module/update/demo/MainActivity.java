package com.want.module.update.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.want.update.UpdateHelper;
import com.want.update.enums.CheckType;
import com.want.update.model.UpdateInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateHelper updateHelper = new UpdateHelper.Builder(MainActivity.this).setUpdateJson(getData()).setCheckType(CheckType.NORMAL).build();
                updateHelper.check();
            }
        });

    }

    private UpdateInfo getData() {
        UpdateInfo info = new UpdateInfo();
        info.upgrade = 801;
        info.version = "0.6.6";
        info.title = "update";
        info.url = "http://www.hollywant.com/download/hollywant_0.4.3.apk";
        info.limitTimes = 0;
        info.interval = 0;
        info.changes = "更新内容为";
        info.size = "30M";
        return info;
    }
}

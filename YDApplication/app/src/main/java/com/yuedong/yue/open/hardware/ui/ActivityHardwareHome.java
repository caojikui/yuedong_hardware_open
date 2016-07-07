package com.yuedong.yue.open.hardware.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.yuedong.sport.R;

/**
 * Created by virl on 7/7/16.
 */
public class ActivityHardwareHome extends Activity{

    private TextView labelPlugName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_home);
        labelPlugName = (TextView) findViewById(R.id.label_jump_plug_name);
        parseIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
    }

    private void parseIntent(Intent intent) {
        if(intent == null) {
            return;
        }
        String plugName = intent.getStringExtra("plug_name");
        labelPlugName.setText("传递的plug:" + plugName);
    }
}

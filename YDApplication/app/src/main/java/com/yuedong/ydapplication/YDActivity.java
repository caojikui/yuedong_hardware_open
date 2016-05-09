package com.yuedong.ydapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yuedong.open.hardware.app.R;
import com.yuedong.open.hardware.PlugConst;

import java.net.URISyntaxException;


/**
 * Created by LaiXiaodong
 * Date ： 2016/1/16.
 */
public class YDActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yd);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Button btnOpen;
    private Button btnAwake;
    private Button btnKeepAlive;
    private Button btnCloseBlueTooth;
    private void initView(){
        btnOpen = (Button) findViewById(R.id.btn_activity_my_open);
        btnOpen.setOnClickListener(this);
        btnAwake = (Button) findViewById(R.id.btn_activity_my_awake);
        btnAwake.setOnClickListener(this);
        btnKeepAlive = (Button) findViewById(R.id.btn_activity_my_keep_alive);
        btnKeepAlive.setOnClickListener(this);
        btnCloseBlueTooth = (Button) findViewById(R.id.btn_activity_my_close_blue_tooth);
        btnCloseBlueTooth.setOnClickListener(this);
        findViewById(R.id.btn_blue_opend).setOnClickListener(this);
        findViewById(R.id.btn_blue_closed).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_activity_my_open:
                goToSDKActivity();
                break;
            case R.id.btn_activity_my_awake:
                sendAction(PlugConst.kActionWakeUp);
                break;
            case R.id.btn_activity_my_keep_alive:
                sendAction(PlugConst.kActionKeepAlive);
                break;
            case R.id.btn_activity_my_close_blue_tooth:
                sendAction(PlugConst.kActionBluetoothStatusChanged);
                break;
            case R.id.btn_blue_opend:
                sendBluetoothStatusChanged(true);
                break;
            case R.id.btn_blue_closed:
                sendBluetoothStatusChanged(false);
        }
    }

    private SharedPreferences sp() {
        return getSharedPreferences(Const.kSpName, MODE_PRIVATE);
    }
    private void sendBluetoothStatusChanged(boolean isOpen) {
        if(!checkDevice()) {
            return;
        }
        String config = sp().getString(PlugConst.kActionBluetoothStatusChanged, null);
        if(config == null) {
            Toast.makeText(this, "该操作没有绑定", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = Intent.parseUri(config, 0);
            intent.putExtra(PlugConst.kActionKey, PlugConst.kActionBluetoothStatusChanged);
            intent.putExtra(PlugConst.kBluetoothStatus, isOpen);
            startService(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkDevice() {
        String deviceId = sp().getString(Const.kKeyDeviceId, null);
        if(deviceId == null) {
            Toast.makeText(this, "设备没有绑定", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void sendAction(String action) {
        if(!checkDevice()) {
            return;
        }
        String config = sp().getString(action, null);
        if(config == null) {
            Toast.makeText(this, "该操作没有绑定", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Intent intent = Intent.parseUri(config, 0);
            intent.putExtra(PlugConst.kActionKey, action);
            startService(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public static final String SDK_APPLICATION_PACKAGE_NAME = "com.yuedong.sdkapplication";
    public static final String SDK_APPLICATION_SDkActivity_NAME = SDK_APPLICATION_PACKAGE_NAME+".SDkActivity";
    private void goToSDKActivity(){
        try {
            Intent intent = new Intent();
            intent.setClassName(SDK_APPLICATION_PACKAGE_NAME, SDK_APPLICATION_SDkActivity_NAME);
            intent.putExtra(PlugConst.kKeyUserId, "123");
            startActivity(intent);
        } catch (RuntimeException exception) {
            Toast.makeText(this, "插件没有安装", Toast.LENGTH_SHORT).show();
        }
    }
}

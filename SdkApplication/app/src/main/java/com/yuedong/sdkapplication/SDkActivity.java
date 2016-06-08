package com.yuedong.sdkapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yuedong.open.hardware.DataHelper;
import com.yuedong.open.hardware.PlugConst;
import com.yuedong.open.hardware.ui.NavigationBar;
import com.yuedong.yue.open.hardware.YDHardwarePlugInterface;

import org.json.JSONObject;


/**
 * Created by LaiXiaodong
 * Date ： 2016/1/16.
 */
public class SDkActivity extends Activity implements View.OnClickListener {

    private ServiceConnection serviceConnection = null;
    YDHardwarePlugInterface registerServiceInterface = null;
    SharedPreferences sp;

    NavigationBar navigationBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oh_demo_activity_sdk);
        sp = getSharedPreferences("sp", MODE_PRIVATE);
        String deviceIdentify = sp.getString("device_identify", null);
        Const.deviceIdentify = deviceIdentify;
        initView();

        Intent intent = getIntent();
        if(intent!=null && intent.hasExtra(PlugConst.kKeyUserId)) {
            String uid = intent.getStringExtra(PlugConst.kKeyUserId);
            Toast.makeText(this, "uid:" + uid, Toast.LENGTH_SHORT).show();
//            TODO should check uid
        } else {
            Toast.makeText(this, "uid not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        onDeviceBindStatusChanged();
    }

    private void onDeviceBindStatusChanged() {
        if(Const.deviceIdentify == null) {
            btnBind.setVisibility(View.VISIBLE);
            btnUnBind.setVisibility(View.GONE);
        } else {
            btnUnBind.setVisibility(View.VISIBLE);
            btnBind.setVisibility(View.GONE);
        }
    }
    private Button btnBind;
    private Button btnRegisterAwake;
    private Button btnRegisterKeepAlive;
    private Button btnRegisterCloseBlueTooth;
    private Button btnWrite;
    private Button btnRead;
    private Button btnUnBind;
    private void initView(){
        btnBind = (Button) findViewById(R.id.btn_activity_sdk_bind);
        btnUnBind = (Button) findViewById(R.id.btn_activity_sdk_unbind);
        btnBind.setOnClickListener(this);
        btnRegisterAwake = (Button) findViewById(R.id.btn_activity_sdk_register_awake);
        btnRegisterAwake.setOnClickListener(this);
        btnRegisterKeepAlive = (Button) findViewById(R.id.btn_activity_sdk_register_keep_alive);
        btnRegisterKeepAlive.setOnClickListener(this);
        btnRegisterCloseBlueTooth = (Button) findViewById(R.id.btn_activity_sdk_register_close_blue_tooth);
        btnRegisterCloseBlueTooth.setOnClickListener(this);
        findViewById(R.id.oh_demo_bn_user_info).setOnClickListener(this);
        btnWrite = (Button) findViewById(R.id.btn_activity_sdk_write_data);
        btnWrite.setOnClickListener(this);
        btnRead = (Button) findViewById(R.id.btn_activity_sdk_read_data);
        btnRead.setOnClickListener(this);
        btnUnBind.setOnClickListener(this);
        navigationBar = (NavigationBar) findViewById(R.id.oh_sdk_navigation);
        navigationBar.setNavBnClickedListener(new NavigationBar.NavBnClickedListener() {
            @Override
            public void onNavLeftBnClicked() {
                finish();
            }

            @Override
            public void onNavRightBnClicked() {

            }
        });
        navigationBar.setLeftBnContent(NavigationBar.backBn(this));
    }

    private void bindService() {
        serviceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                registerServiceInterface = YDHardwarePlugInterface.Stub.asInterface(service);
                Toast.makeText(SDkActivity.this, "bind service succ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                serviceConnection = null;
            }
        };
        Intent service = new Intent();
//        上线使用这个TODO
//        service.setClassName(this, PlugConst.kPlugServiceName);
        service.setClassName(PlugConst.kPlugServicePkg, PlugConst.kPlugServiceName);

        bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void registerAction(String action) {
        if(registerServiceInterface == null) {
            Toast.makeText(this, "service 还未绑定完成", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, ServicePlug.class);
        try {
            registerServiceInterface.registerServiceAction(intent.toUri(0), action, Const.deviceIdentify);
            Toast.makeText(this, "注册:" + action + "成功", Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void bindDevice() {
        if(registerServiceInterface == null) {
            Toast.makeText(this, "service 还未绑定完成", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Const.deviceIdentify = registerServiceInterface.registerDevice("1234", Const.kPlugName);
            Toast.makeText(this, "设备绑定成功 :" + Const.deviceIdentify, Toast.LENGTH_SHORT).show();
            sp.edit().putString("device_identify", Const.deviceIdentify).apply();
            onDeviceBindStatusChanged();
            registerAction(PlugConst.kActionBluetoothStatusChanged);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unbindDevice() {
        if(registerServiceInterface == null) {
            Toast.makeText(this, "service 还未绑定完成", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            registerServiceInterface.unRegisterDevice(Const.deviceIdentify, Const.kPlugName);
            Const.deviceIdentify = null;
            Toast.makeText(this, "设备解除绑定成功 :", Toast.LENGTH_SHORT).show();
            sp.edit().remove("device_identify").apply();
            onDeviceBindStatusChanged();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(registerServiceInterface == null) {
            bindService();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_activity_sdk_bind:
                bindDevice();
                break;
            case R.id.btn_activity_sdk_register_awake:
                registerAction(PlugConst.kActionWakeUp);
                break;
            case R.id.btn_activity_sdk_register_keep_alive:
                registerAction(PlugConst.kActionKeepAlive);
                break;
            case R.id.btn_activity_sdk_register_close_blue_tooth:
                registerAction(PlugConst.kActionReleaseBluetooth);
                break;
            case R.id.btn_activity_sdk_write_data:
                writeData();
                Toast.makeText(this,"写入数据成功！",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_activity_sdk_read_data:
                readData();
                break;
            case R.id.btn_activity_sdk_unbind:
                unbindDevice();
                break;
            case R.id.oh_demo_bn_user_info:
                readUserInfo();
                break;
        }
    }

    private void readUserInfo() {
        if(registerServiceInterface == null) {
            Toast.makeText(this, "service 还未绑定完成", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String userInfo = registerServiceInterface.userInfoJsonStr();
            Toast.makeText(this, userInfo, Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void readData() {
        if(Const.deviceIdentify == null) {
            Toast.makeText(this, "还未绑定设备", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentResolver resolver = getContentResolver();
        String selection = PlugConst.kColDeviceIdentify + "=\"" + Const.deviceIdentify + '\"';
        Cursor cursor = resolver.query(DataHelper.kUriIntelligentScale, null, selection, null, null);
        if(cursor == null) {
            Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cursor.moveToFirst()){
            float bft = cursor.getFloat(cursor.getColumnIndex(PlugConst.kColBodyFatPercentage));
            float bmi = cursor.getFloat(cursor.getColumnIndex(PlugConst.kColBodyMassIndex));
            float bmp = cursor.getFloat(cursor.getColumnIndex(PlugConst.kColBodyMusclePercentage));
            float bmr = cursor.getFloat(cursor.getColumnIndex(PlugConst.kColBasalMetabolismRate));
            float bwp = cursor.getFloat(cursor.getColumnIndex(PlugConst.kColBodyWaterPercentage));
            int weight = cursor.getInt(cursor.getColumnIndex(PlugConst.kColWeightG));
            long timeSec = cursor.getInt(cursor.getColumnIndex(PlugConst.kColTimeSec));
            String extra = cursor.getString(cursor.getColumnIndex(PlugConst.kColExtra));
            StringBuilder sb = new StringBuilder();
            sb.append("bft:");
            sb.append(bft);
            sb.append(",bmi:");
            sb.append(bmi);
            sb.append(",bmp:");
            sb.append(bmp);
            sb.append(",bmr:");
            sb.append(bmr);
            sb.append(",bwp:");
            sb.append(bwp);
            sb.append(",weight:");
            sb.append(weight);
            sb.append(",time:");
            sb.append(timeSec);
            sb.append(". extra:");
            sb.append(extra);
            Toast.makeText(this, sb.toString(),Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        DataHelper.getInstance(this).getRealTimeStepCount(Const.deviceIdentify);
    }

    private void writeData() {
        if(Const.deviceIdentify == null) {
            Toast.makeText(this, "还未绑定设备", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject extra = new JSONObject();
        DataHelper.getInstance(this).writeIntelligentScaleData(Const.deviceIdentify, System.currentTimeMillis()/1000, 60, 20, 20, 20, 140, 70, extra);

        DataHelper.getInstance(this).writeSleepData(Const.deviceIdentify, 1, System.currentTimeMillis(), System.currentTimeMillis());

        DataHelper.getInstance(this).setRealTimeStepCount(Const.deviceIdentify, 100, 100, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serviceConnection != null){
            unbindService(serviceConnection);
        }
    }
}

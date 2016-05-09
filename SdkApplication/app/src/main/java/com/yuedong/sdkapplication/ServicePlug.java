package com.yuedong.sdkapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.yuedong.open.hardware.PlugConst;

/**
 * Created by LaiXiaodong
 * Date ： 2016/1/16.
 */
public class ServicePlug extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.hasExtra(PlugConst.kActionKey)) {
            String action = intent.getStringExtra(PlugConst.kActionKey);
            Toast.makeText(this, "receive action:" + action, Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}

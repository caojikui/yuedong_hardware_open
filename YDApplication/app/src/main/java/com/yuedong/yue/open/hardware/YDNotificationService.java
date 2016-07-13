package com.yuedong.yue.open.hardware;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.widget.Toast;

import com.yuedong.open.hardware.PlugConst;
import com.yuedong.ydapplication.Const;

import java.net.URISyntaxException;

@SuppressLint("OverrideAbstract")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class YDNotificationService extends NotificationListenerService {
    public YDNotificationService() {
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String deviceId = getRegisterDeviceId(this);
        if (TextUtils.isEmpty(deviceId)) {
            return;
        }
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        String packageName = sbn.getPackageName();
        String config = sp(this).getString(PlugConst.kActionNewNotification, null);
        try {
            Intent intent = Intent.parseUri(config, 0);
            intent.putExtra(PlugConst.kActionKey, PlugConst.kActionNewNotification);
            if(extras != null) {
                intent.putExtra("extras", extras);
            }
            intent.putExtra(PlugConst.kKeyNotificationPkgName, packageName);
            startService(intent);
            Toast.makeText(this, PlugConst.kActionNewNotification, Toast.LENGTH_SHORT).show();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

    private SharedPreferences sp(Context context) {
        return context.getSharedPreferences(Const.kSpName, Context.MODE_PRIVATE);
    }

    private String getRegisterDeviceId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Const.kSpName, Context.MODE_PRIVATE);
        return sp.getString(Const.kKeyDeviceId, null);
    }
}

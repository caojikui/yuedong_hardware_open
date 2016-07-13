package com.yuedong.ydapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.yuedong.open.hardware.PlugConst;

import java.net.URISyntaxException;

public class CallSMSReceiver extends BroadcastReceiver {
    public CallSMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String deviceId = getRegisterDeviceId(context);
        if (TextUtils.isEmpty(deviceId)) {
            return;
        }
        switch (intent.getAction()) {
            case "android.intent.action.PHONE_STATE": {
                TelephonyManager tManager = (TelephonyManager) context
                        .getSystemService(Service.TELEPHONY_SERVICE);
                switch (tManager.getCallState()) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        sendAction(context, PlugConst.kActionPhoneNewCallIn);
                        break;
                }
            }
            case "android.provider.Telephony.SMS_RECEIVED": {
                sendAction(context, PlugConst.kActionPhoneNewSMS);
            }
        }
    }

    private SharedPreferences sp(Context context) {
        return context.getSharedPreferences(Const.kSpName, Context.MODE_PRIVATE);
    }

    private void sendAction(Context context, String action) {
        String config = sp(context).getString(action, null);
        try {
            Intent intent = Intent.parseUri(config, 0);
            intent.putExtra(PlugConst.kActionKey, action);
            context.startService(intent);
            Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getRegisterDeviceId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Const.kSpName, Context.MODE_PRIVATE);
        return sp.getString(Const.kKeyDeviceId, null);
    }
}

package com.yuedong.ydapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
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
                        sendAction(context, PlugConst.kActionPhoneNewCallIn, intent.getExtras());
                        break;
                }
            }
            break;
            case "android.provider.Telephony.SMS_RECEIVED": {
                onNewSms(context, intent);
            }
        }
    }

    private SharedPreferences sp(Context context) {
        return context.getSharedPreferences(Const.kSpName, Context.MODE_PRIVATE);
    }

    private void onNewSms(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Object messages[] = (Object[]) bundle.get("pdus");
        if (messages!=null && messages.length>0) {
            SmsMessage smsMessage[] = new SmsMessage[messages.length];
            for (int n = 0; n < smsMessage.length; n++) {
                smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
            }
            for (SmsMessage message : smsMessage) {
                String content = message.getMessageBody();//得到短信内容
                String sender = message.getOriginatingAddress();//得到发件人号码
            }
            sendAction(context, PlugConst.kActionPhoneNewSMS, intent.getExtras());
        }
    }

    private void sendAction(Context context, String action, Bundle extras) {
        String config = sp(context).getString(action, null);
        if(TextUtils.isEmpty(config)) {
            return;
        }
        try {
            Intent intent = Intent.parseUri(config, 0);
            intent.putExtra(PlugConst.kActionKey, action);
            if(extras != null) {
                intent.putExtra(PlugConst.kKeyExtras, extras);
            }
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

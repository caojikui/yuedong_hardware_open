package com.yuedong.yue.open.hardware;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.Toast;

import com.yuedong.open.hardware.PlugConst;
import com.yuedong.ydapplication.Const;
import com.yuedong.yue.open.hardware.YDHardwarePlugInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by virl on 16/1/19.
 */
public class YDHardwarePlugService extends Service {

    public IBinder onBind(Intent intent) {
        return binder;
    }

    SharedPreferences sp;
    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Const.kSpName, MODE_PRIVATE);
    }

    YDHardwarePlugInterface.Stub binder = new YDHardwarePlugInterface.Stub() {

        @Override
        public String registerDevice(String deviceId, String plugName) throws RemoteException {
            String deviceIdentify = deviceId+"device_identity";
            sp.edit().putString(Const.kKeyDeviceId, deviceIdentify).apply();
            return deviceIdentify;
        }

        @Override
        public void unRegisterDevice(String deviceIdentify, String plugName) throws RemoteException {
            sp.edit().remove(Const.kKeyDeviceId).apply();
        }

        @Override
        public void registerServiceAction(String intentUri, String action, String deviceIdentify) throws RemoteException {
            String did = sp.getString(Const.kKeyDeviceId, null);
            if(TextUtils.isEmpty(did)) {
                Toast.makeText(YDHardwarePlugService.this, "请先注册插件设备", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(deviceIdentify) || !did.equals(deviceIdentify)) {
                Toast.makeText(YDHardwarePlugService.this, "deviceIdentify 不合法", Toast.LENGTH_SHORT).show();
                return;
            }

            sp.edit().putString(action, intentUri).commit();
        }

        @Override
        public String userInfoJsonStr() throws RemoteException {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(PlugConst.kKeyUserId, "123");
                jsonObject.put(PlugConst.kKeyGender, PlugConst.kGenderMale);
                jsonObject.put(PlugConst.kKeyBirthday, System.currentTimeMillis() / 1000 - 18 * 3600 * 24 * 365);
                jsonObject.put(PlugConst.kKeyNickname, "yuedong");
                jsonObject.put(PlugConst.kKeyAvatarPath, "");
                jsonObject.put(PlugConst.kKeyHeight, 180);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }
    };
}

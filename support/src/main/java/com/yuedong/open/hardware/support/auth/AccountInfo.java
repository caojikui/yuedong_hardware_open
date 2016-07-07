package com.yuedong.open.hardware.support.auth;

import com.yuedong.open.hardware.support.net.NetCall;
import com.yuedong.open.hardware.support.net.NetWorkCallback;

/**
 * Created by virl on 7/7/16.
 */
public abstract class AccountInfo {
    public abstract NetCall queryOpenId(String appId, NetWorkCallback callback);
    public abstract long uid();
    public abstract String avatarUrl();

    private static AccountInfo sInstance;
    public static AccountInfo instance() {
        if(sInstance == null) {
            sInstance = new AccountInfoImp();
        }
        return sInstance;
    }

    public static void setInstance(AccountInfo instance) {
        if(instance == null) {
            return;
        }
        sInstance = instance;
    }
}

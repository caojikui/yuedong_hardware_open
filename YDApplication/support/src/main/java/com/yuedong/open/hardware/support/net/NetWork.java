package com.yuedong.open.hardware.support.net;

import java.util.Map;

/**
 * Created by virl on 7/7/16.
 */
public abstract class NetWork {
    public enum HttpMethod {
        kHttpPost,
        kHttpGet,
        kHttpPut,
        kHttpPatch,
        kHttpDelete
    }
    public abstract NetCall asyncDo(HttpMethod method, Map<String, String> params, NetWorkCallback callback);

    private static NetWork sInstance;
    public static NetWork netWork() {
        return sInstance;
    }

    public static void setInstance(NetWork instance) {
        if(instance == null) {
            return;
        }
        sInstance = instance;
    }
}

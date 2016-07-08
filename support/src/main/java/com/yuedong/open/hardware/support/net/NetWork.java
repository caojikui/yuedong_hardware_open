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
    public abstract NetCall asyncDo(HttpMethod method, String url, Map<String, String> params, Map<String, String> headers, NetWorkCallback callback);
    public NetCall asyncDo(HttpMethod method, String url, Map<String, String> params, NetWorkCallback callback) {
        return asyncDo(method, url, params, null, callback);
    }

    public NetCall asyncGet(String url, Map<String, String> params, NetWorkCallback callback) {
        return asyncDo(HttpMethod.kHttpGet, url, params, null, callback);
    }

    public NetCall asyncPost(String url, Map<String, String> params, NetWorkCallback callback) {
        return asyncDo(HttpMethod.kHttpPost, url, params, null, callback);
    }


    private static NetWork sInstance;
    public static NetWork netWork() {
        if(sInstance == null) {
            sInstance = new NetWorkImp();
        }
        return sInstance;
    }
}

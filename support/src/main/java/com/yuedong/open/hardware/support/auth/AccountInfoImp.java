package com.yuedong.open.hardware.support.auth;

import com.yuedong.open.hardware.support.net.NetCall;
import com.yuedong.open.hardware.support.net.NetWork;
import com.yuedong.open.hardware.support.net.NetWorkCallback;

import java.util.HashMap;

/**
 * Created by virl on 7/7/16.
 */
class AccountInfoImp extends AccountInfo {
    @Override
    public NetCall queryOpenId(String appId, NetWorkCallback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(uid()));
        params.put("app_id", appId);
        String url = "http://open-api.51yund.com/get_openid_by_user_id";
        return NetWork.netWork().asyncDo(NetWork.HttpMethod.kHttpPost, url, params, null, callback);
    }

    @Override
    public long uid() {
        return 2267576;
    }

    @Override
    public String avatarUrl() {
        return "http://api.51yund.com/sport/get_head?user_id=2267576&size=160";
    }
}

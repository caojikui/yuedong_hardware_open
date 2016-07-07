package com.yuedong.open.hardware.support.net;


import okhttp3.Call;

/**
 * Created by virl on 7/7/16.
 */
class NetCallImp implements NetCall{
    private final Call call;
    NetCallImp(Call call) {
        this.call = call;
    }

    @Override
    public void cancel() {
        if(call != null) {
            call.cancel();
        }
    }
}

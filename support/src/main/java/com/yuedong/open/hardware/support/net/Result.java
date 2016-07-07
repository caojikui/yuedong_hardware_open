package com.yuedong.open.hardware.support.net;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by virl on 7/7/16.
 */
public class Result {
    public final boolean ok;
    public final String msg;
    private String content = null;
    public String text() {
        return content;
    }

    public JSONObject json() {
        try {
            return new JSONObject(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    Result(Call call, IOException e) {
        ok = false;
        msg = e.getLocalizedMessage();
    }

    Result(Call call, Response response) {
        ok = response.isSuccessful();
        msg = response.message();
        if(ok) {
            try {
                content = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.body().close();
        }
    }
}

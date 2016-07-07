package com.yuedong.open.hardware.support.net;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by virl on 7/7/16.
 */
public class Result {
    private boolean isOk;
    private String msg;
    private String content;
    public boolean ok() {
        return isOk;
    }

    public String msg() {
        return msg;
    }

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
}

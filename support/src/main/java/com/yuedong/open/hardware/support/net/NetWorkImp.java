package com.yuedong.open.hardware.support.net;

import android.os.Handler;
import android.os.Looper;

import junit.framework.Assert;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by virl on 7/7/16.
 */
class NetWorkImp extends NetWork{
    private OkHttpClient httpClient = new OkHttpClient();
    protected Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public NetCall asyncDo(HttpMethod method, String url, Map<String, String> params, Map<String, String> headers, NetWorkCallback callback) {
        Request.Builder builder = new Request.Builder();
        if(method == HttpMethod.kHttpGet) {
            url = buildUrlForGet(url, params);
            builder.get();
        } else {
            configBuilderForMethod(builder, params, method);
        }
        builder.url(url);

        builder.header("Connection" , "Keep-Alive");
        if(null!=headers) {
            for(Map.Entry<String, String> header : headers.entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }
        }

        Request request = builder.build();

        Call call = httpClient.newCall(request);
        call.enqueue(new CallbackWrap(callback));
        return new NetCallImp(call);
    }

    private class CallbackWrap implements Callback {
        private NetWorkCallback callback;
        CallbackWrap(NetWorkCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            if(callback != null) {
                final Result result = new Result(call, e);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onNetWorkFinished(result);
                    }
                });

            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(callback != null) {
                final Result result = new Result(call, response);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onNetWorkFinished(result);
                    }
                });
            }
        }
    }

    private void configBuilderForMethod(Request.Builder builder, Map<String, String> params, HttpMethod method) {
        if(null==params||params.isEmpty()) {
            return;
        }
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for(Map.Entry<String, String> param: params.entrySet()) {
            bodyBuilder.add(param.getKey(), param.getValue());
        }
        switch (method) {
            case kHttpPost:
                builder.post(bodyBuilder.build());
                break;
            case kHttpDelete:
                builder.delete(bodyBuilder.build());
                break;
            case kHttpPatch:
                builder.patch(bodyBuilder.build());
                break;
            case kHttpPut:
                builder.put(bodyBuilder.build());
                break;
            case kHttpGet:
                Assert.assertTrue(false);
        }
    }

    private String buildUrlForGet(String basUrl, Map<String, String> params) {
        if(null==params || params.isEmpty()) {
            return basUrl;
        }
        StringBuilder stringBuilder = new StringBuilder(basUrl);
        stringBuilder.append('?');
        for(Map.Entry<String, String> param: params.entrySet()) {
            stringBuilder.append(param.getKey());
            stringBuilder.append('=');
            stringBuilder.append(param.getValue());
            stringBuilder.append('&');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}

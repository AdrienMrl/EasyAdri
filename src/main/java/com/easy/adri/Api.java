package com.easy.adri;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api {

    public class Auth {

        public String mAccessToken;
        public String facebookAuthEndpoint = "/authorization/facebook";
        public String emailAuthEndpoint = "/authorization/email";

        public void authenticateWithFacebook(String token) {
            authenticateWithFacebook(token, null);
        }

        public void authenticateWithFacebook(String token, final JavaHelpers.CallBackWithArg<Boolean> cb) {

            HashMap<String, String> queryParam = new HashMap<>();
            queryParam.put("access_token", token);

            JSONObject json = new JSONObject();

            try {
                json.put("access_token", token);
                post(facebookAuthEndpoint, json, new JavaHelpers.CallBackWithArg<JSONObject>() {
                    @Override
                    public void call(JSONObject arg) {

                        if (arg == null)
                            return;

                        try {
                            mAccessToken = arg.getString("access_token");
                            cb.call(true);
                        } catch (JSONException e) {
                            Log.e("Api", e.toString());
                        }
                    }
                });
            } catch (JSONException e) {
                Log.e("Api", e.toString());
                cb.call(false);
            }
        }
    }

    public String mServerAddr;
    public Auth mAuth = new Auth();

    private OkHttpClient client = new OkHttpClient();

    public Api(String serverAddr) {
        mServerAddr = serverAddr;
    }

    public void get(String endpoint, Map<String, String> queryArg, JavaHelpers.CallBackWithArg<JSONObject> cb) {

        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .host(mServerAddr)
                .addPathSegment(endpoint);

        for (Map.Entry<String, String> entry: queryArg.entrySet())
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());

        call(urlBuilder.build(), "get", null, cb);
    }

    public void post(String endpoint, JSONObject payload, JavaHelpers.CallBackWithArg<JSONObject> cb) {
        call(mServerAddr + endpoint, "post", payload, cb);
    }

    private void callbackOnUi(final JSONObject json, final JavaHelpers.CallBackWithArg<JSONObject> cb) {

        JavaHelpers.onUIThread(new JavaHelpers.Callback() {
            @Override
            public void call() {
                cb.call(json);
            }
        });
    }

    private void call(final String url, String method, final JSONObject payload, final JavaHelpers.CallBackWithArg<JSONObject> cb) {
        call(HttpUrl.parse(url), method, payload, cb);
    }

    private void call(final HttpUrl url, final String method, final JSONObject payload, final JavaHelpers.CallBackWithArg<JSONObject> cb) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                Request.Builder builder = new Request.Builder().url(url);

                String body = "";

                if (payload != null)
                    body = payload.toString();

                if (method.equals("post"))
                    builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body));
                else builder.get();

                try {
                    Response response = client.newCall(builder.build()).execute();

                    try {
                        String data = response.body().string();
                        callbackOnUi(new JSONObject(data), cb);
                    } catch (JSONException e) {
                        callbackOnUi(null, cb);
                    }

                } catch (IOException e) {
                    Log.e("Api", e.toString());
                    callbackOnUi(null, cb);
                }
            }
        });
    }
}

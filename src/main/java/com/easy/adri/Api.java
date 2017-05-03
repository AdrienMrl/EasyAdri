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

        public void handleTokenResponse(JSONObject arg, JavaHelpers.CallBackWithArg<Boolean> cb) {

            if (arg == null)
                return;

            try {
                mAccessToken = arg.getString("access_token");
                if (cb != null)
                    cb.call(true);
            } catch (JSONException e) {
                Log.e("Api", e.toString());
            }
        }

        public void authenticateWithFacebook(String token) {
            authenticateWithFacebook(token, null);
        }

        public void authenticateWithFacebook(String token, final JavaHelpers.CallBackWithArg<Boolean> cb) {

            JSONObject json = new JSONObject();

            try {
                json.put("access_token", token);
                post(facebookAuthEndpoint, json, new JavaHelpers.CallBackWithArg<JSONObject>() {
                    @Override
                    public void call(JSONObject arg) {
                        handleTokenResponse(arg, cb);
                    }
                });
            } catch (JSONException e) {
                Log.e("Api", e.toString());
                cb.call(false);
            }
        }

        public void authenticateWithEmail(String email, String password) {
            JSONObject json = new JSONObject();

            try {
                json.put("email", email);
                json.put("password", password);
                post(emailAuthEndpoint, json, new JavaHelpers.CallBackWithArg<JSONObject>() {
                    @Override
                    public void call(JSONObject arg) {
                        handleTokenResponse(arg, null);
                    }
                });
            } catch (JSONException e) {
                Log.e("Api", e.toString());
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

        HttpUrl.Builder urlBuilder = HttpUrl.parse(mServerAddr + endpoint).newBuilder();

        if (queryArg != null)
            for (Map.Entry<String, String> entry: queryArg.entrySet())
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());

        call(urlBuilder.build(), "get", null, cb);
    }

    public void delete(String endpoint, JavaHelpers.CallBackWithArg<JSONObject> cb) {
        call(mServerAddr + endpoint, "delete", null, cb);
    }

    public void post(String endpoint, JSONObject payload, JavaHelpers.CallBackWithArg<JSONObject> cb) {
        call(mServerAddr + endpoint, "post", payload, cb);
    }

    private void callbackOnUi(final JSONObject json, final JavaHelpers.CallBackWithArg<JSONObject> cb) {

        Useful.onUIThread(new JavaHelpers.Callback() {
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

                if (mAuth.mAccessToken != null) {
                    builder.addHeader("Authorization", "Bearer " + mAuth.mAccessToken);
                }

                String body = "";
                if (payload != null)
                    body = payload.toString();

                switch (method) {
                    case "post":
                        builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body));
                        break;
                    case "get":
                        builder.get();
                        break;
                    case "delete":
                        builder.delete();
                        break;
                }

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

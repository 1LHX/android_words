package com.ncusoft.myapplication.service;

import android.util.Log;

import com.ncusoft.myapplication.model.AuthResponse;
import com.ncusoft.myapplication.model.LoginRequest;
import com.ncusoft.myapplication.model.RegisterRequest;
import com.ncusoft.myapplication.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthService {
    private static final String BASE_URL = "http://192.168.42.151:8080";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public void login(String username, String password, AuthCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            callback.onError("参数错误");
            return;
        }
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/user/login")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络错误: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("登录失败");
                    return;
                }
                try {
                    String resp = response.body().string();
                    JSONObject obj = new JSONObject(resp);
                    if (obj.getInt("code") == 200) {
                        // 后端登录成功时data字段为token字符串，不是用户对象
                        // 只需提示登录成功并回调onSuccess(null)
                        callback.onSuccess(null);
                    } else {
                        callback.onError(obj.optString("msg", "登录失败"));
                    }
                } catch (JSONException e) {
                    callback.onError("数据解析失败");
                }
            }
        });
    }

    public void register(String username, String password, String email, AuthCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
            json.put("email", email);
        } catch (JSONException e) {
            callback.onError("参数错误");
            return;
        }
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/user/register")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络错误: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("注册失败");
                    return;
                }
                try {
                    String resp = response.body().string();
                    JSONObject obj = new JSONObject(resp);
                    if (obj.getInt("code") == 200) {
                        // 注册成功，后端返回的data是字符串“注册成功”，不再是用户对象
                        callback.onSuccess(null);
                    } else {
                        callback.onError(obj.optString("msg", "注册失败"));
                    }
                } catch (JSONException e) {
                    callback.onError("数据解析失败");
                }
            }
        });
    }
}

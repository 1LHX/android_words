package com.ncusoft.myapplication.service;

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

public class UserService {
    private static final String BASE_URL = "http://192.168.42.151:8080/user";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    public interface UpdateUserCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public void updateUser(String oldUsername, User user, UpdateUserCallback callback) {
        try {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("username", user.getUsername());
            jsonUser.put("password", user.getPassword());
            jsonUser.put("email", user.getEmail());

            String url = BASE_URL + "/update/" + oldUsername;
            RequestBody body = RequestBody.create(JSON, jsonUser.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("网络请求失败: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(!response.isSuccessful()){
                        callback.onError("服务器响应失败1");
                        return;
                    }
                    if (response.body() == null) {
                        callback.onError("服务器响应失败2");
                        return;
                    }

                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        if (json.getInt("code") == 200) {
                            callback.onSuccess(json.getString("data"));
                        } else {
                            callback.onError(json.optString("msg", "更新失败"));
                        }
                    } catch (JSONException e) {
                        callback.onError("解析响应失败: " + e.getMessage());
                    }
                }
            });
        } catch (JSONException e) {
            callback.onError("创建请求数据失败: " + e.getMessage());
        }
    }

    public interface GetUserCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public void getUserByUsername(String username, String password, GetUserCallback callback) {
        String url = BASE_URL + "/username/" + username + "?password=" + password;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("服务器响应失败");
                    return;
                }
                
                try {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    if (json.getInt("code") == 200) {
                        JSONObject data = json.getJSONObject("data");
                        User user = new User();
                        user.setId(data.getInt("id"));
                        user.setUsername(data.getString("username"));
                        user.setPassword(data.getString("password"));
                        user.setEmail(data.getString("email"));
                        callback.onSuccess(user);
                    } else {
                        callback.onError(json.optString("msg", "获取用户信息失败"));
                    }
                } catch (JSONException e) {
                    callback.onError("解析响应失败: " + e.getMessage());
                }
            }
        });
    }

    public interface GetUserIdCallback {
        void onSuccess(int userId);
        void onError(String error);
    }

    public void getUserId(String username, String password, GetUserIdCallback callback) {
        String url = BASE_URL + "/username/" + username + "?password=" + password;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("服务器响应失败");
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    if (json.getInt("code") == 200) {
                        JSONObject data = json.getJSONObject("data");
                        int userId = data.getInt("id");
                        callback.onSuccess(userId);
                    } else {
                        callback.onError(json.optString("msg", "获取用户信息失败"));
                    }
                } catch (JSONException e) {
                    callback.onError("解析响应失败: " + e.getMessage());
                }
            }
        });
    }
}

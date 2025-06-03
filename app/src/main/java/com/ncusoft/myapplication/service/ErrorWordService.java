package com.ncusoft.myapplication.service;

import com.ncusoft.myapplication.model.ErrorWord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ErrorWordService {
    private static final String BASE_URL = "http://192.168.42.151:8080/error-vocabulary";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    public interface ErrorWordCallback {
        void onSuccess(List<ErrorWord> errorWords);
        void onError(String error);
    }

    public void getErrorWords(int userId, ErrorWordCallback callback) {
        String url = BASE_URL + "/words?userId=" + userId;
        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络错误: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("获取错题失败");
                    return;
                }
                try {
                    String resp = response.body().string();
                    JSONObject obj = new JSONObject(resp);
                    if (obj.getInt("code") == 200) {
                        JSONArray data = obj.getJSONArray("data");
                        List<ErrorWord> list = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            int wordId = data.getInt(i);
                            ErrorWord ew = new ErrorWord();
                            ew.setWordId(wordId);
                            list.add(ew);
                        }
                        callback.onSuccess(list);
                    } else {
                        callback.onError(obj.optString("msg", "获取错题失败"));
                    }
                } catch (JSONException e) {
                    callback.onError("数据解析失败");
                }
            }
        });
    }

    public void addErrorWord(int userId, int wordId, ErrorWordCallback callback) {
        String url = BASE_URL + "/add?userId=" + userId + "&wordId=" + wordId;
        Request request = new Request.Builder().post(RequestBody.create("", null)).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络错误: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("添加错题失败");
                    return;
                }
                try {
                    String resp = response.body().string();
                    JSONObject obj = new JSONObject(resp);
                    if (obj.getInt("code") == 200) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(obj.optString("msg", "添加错题失败"));
                    }
                } catch (JSONException e) {
                    callback.onError("数据解析失败");
                }
            }
        });
    }

    public void deleteErrorWord(int userId, int wordId, ErrorWordCallback callback) {
        String url = BASE_URL + "/delete?userId=" + userId + "&wordId=" + wordId;
        Request request = new Request.Builder().delete().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络错误: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("删除错题失败");
                    return;
                }
                try {
                    String resp = response.body().string();
                    JSONObject obj = new JSONObject(resp);
                    if (obj.getInt("code") == 200) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(obj.optString("msg", "删除错题失败"));
                    }
                } catch (JSONException e) {
                    callback.onError("数据解析失败");
                }
            }
        });
    }
}

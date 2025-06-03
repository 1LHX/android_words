package com.ncusoft.myapplication.service;

import android.content.Context;
import android.util.Log;

import com.ncusoft.myapplication.model.EnglishWords;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WordService {
    private static final String TAG = "WordService";
    private static final String BASE_API_URL = "http://192.168.42.151:8080/vocabulary"; // TODO: 替换为实际后端地址
    private OkHttpClient client;
    private WordApiService wordApiService;
    private Context context;

    // 新增字段，用于保存总页数和总单词数
    private int totalPages = 1;
    private int totalWords = 0;

    public WordService(Context context) {
        this.context = context;
        this.wordApiService = new WordApiService();
        this.client = new OkHttpClient();
    }

    public interface WordListCallback {
        void onSuccess(List<EnglishWords> words);

        void onError(String error);

        void onProgress(int current, int total);
    }

    // 分页获取单词
    public void getPagedWords(int page, int pageSize, WordListCallback callback) {
        String url = BASE_API_URL + "/words/page?page=" + page + "&pageSize=" + pageSize;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("后端接口响应失败");
                    return;
                }
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    if (json.getInt("code") != 200) {
                        callback.onError(json.optString("msg", "后端返回错误"));
                        return;
                    }
                    JSONObject data = json.getJSONObject("data");
                    JSONArray vocabulariesArray = data.getJSONArray("vocabularies");
                    List<String> wordList = new ArrayList<>();
                    for (int i = 0; i < vocabulariesArray.length(); i++) {
                        JSONObject vocabObj = vocabulariesArray.getJSONObject(i);
                        wordList.add(vocabObj.getString("word"));
                    }
                    // 解析分页信息
                    if (data.has("totalPages")) {
                        totalPages = data.getInt("totalPages");
                    }
                    if (data.has("totalWords")) {
                        totalWords = data.getInt("totalWords");
                    }
                    fetchWordDetails(wordList, callback);
                } catch (JSONException e) {
                    callback.onError("解析后端数据失败: " + e.getMessage());
                }
            }
        });
    }

    // 随机获取单词
    public void getRandomWords(int count, WordListCallback callback) {
        String url = BASE_API_URL + "/words/random?count=" + count;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("后端接口响应失败");
                    return;
                }
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    if (json.getInt("code") != 200) {
                        callback.onError(json.optString("msg", "后端返回错误"));
                        return;
                    }
                    JSONArray wordsArray = json.getJSONArray("data");
                    List<String> wordList = new ArrayList<>();
                    for (int i = 0; i < wordsArray.length(); i++) {
                        wordList.add(wordsArray.getString(i));
                    }
                    fetchWordDetails(wordList, callback);
                } catch (JSONException e) {
                    callback.onError("解析后端数据失败: " + e.getMessage());
                }
            }
        });
    }

    // 新增：直接解析后端/vocabulary/words/random接口返回的单词列表
    public void getRandomWordsFromApi(int count, WordListCallback callback) {
        String url = BASE_API_URL + "/words/random?count=" + count;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("后端接口响应失败");
                    return;
                }
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    if (json.getInt("code") != 200) {
                        callback.onError(json.optString("msg", "后端返回错误"));
                        return;
                    }
                    JSONArray dataArray = json.getJSONArray("data");
                    List<EnglishWords> result = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject obj = dataArray.getJSONObject(i);
                        EnglishWords ew = new EnglishWords();
                        if (obj.has("id")) ew.setId(obj.getInt("id"));
                        ew.setWord(obj.optString("word", ""));
                        ew.setMeaning(obj.optString("meaning", "")); // 解析meaning字段
                        result.add(ew);
                        callback.onProgress(i + 1, count);
                    }
                    callback.onSuccess(result);
                } catch (JSONException e) {
                    callback.onError("解析后端数据失败: " + e.getMessage());
                }
            }
        });
    }

    // 获取详细单词信息
    private void fetchWordDetails(List<String> wordList, WordListCallback callback) {
        if (wordList == null || wordList.isEmpty()) {
            callback.onError("单词列表为空");
            return;
        }
        List<EnglishWords> result = new ArrayList<>();
        AtomicInteger completedCount = new AtomicInteger(0);
        int total = wordList.size();
        for (String word : wordList) {
            wordApiService.getWordInfo(word, new WordApiService.WordApiCallback() {
                @Override
                public void onSuccess(EnglishWords englishWords) {
                    synchronized (result) {
                        result.add(englishWords);
                        int completed = completedCount.incrementAndGet();
                        callback.onProgress(completed, total);
                        if (completed == total) {
                            callback.onSuccess(result);
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    int completed = completedCount.incrementAndGet();
                    callback.onProgress(completed, total);
                    if (completed == total) {
                        if (result.isEmpty()) {
                            callback.onError("所有单词的API请求都失败了");
                        } else {
                            callback.onSuccess(result);
                        }
                    }
                }
            });
        }
    }

    // 由于单词总数和总页数需要后端支持，建议前端分页时通过接口返回的total字段动态设置
    public int getTotalPages(int pageSize) {
        // 建议移除本地实现，改为在WordListActivity中通过后端返回的totalPages设置
        return totalPages;
    }

    public int getTotalWords() {
        // 建议移除本地实现，改为在WordListActivity中通过后端返回的totalWords设置
        return totalWords;
    }

    public void searchWord(String word, WordApiService.WordApiCallback callback) {
        wordApiService.getWordInfo(word, callback);
    }

    public interface WordByIdCallback {
        void onSuccess(EnglishWords word);
        void onError(String error);
    }

    public void getWordById(int id, WordByIdCallback callback) {
        String url = BASE_API_URL + "/" + id;
        Request request = new Request.Builder().url(url).build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("后端接口响应失败");
                    return;
                }
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    if (json.getInt("code") != 200) {
                        callback.onError(json.optString("msg", "后端返回错误"));
                        return;
                    }
                    JSONObject data = json.getJSONObject("data");
                    EnglishWords word = new EnglishWords();
                    word.setId(data.getInt("id"));
                    word.setWord(data.getString("word"));
                    word.setMeaning(data.getString("meaning"));
                    callback.onSuccess(word);
                } catch (JSONException e) {
                    callback.onError("解析数据失败: " + e.getMessage());
                }
            }
        });
    }

    public interface WordSearchCallback {
        void onSuccess(List<EnglishWords> words);
        void onError(String error);
    }    public void searchWordByKeyword(String keyword, WordSearchCallback callback) {
        String url = BASE_API_URL + "/search?keyword=" + keyword;
        Request request = new Request.Builder().url(url).build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = "";
                try {
                    if (response.body() != null) {
                        responseBody = response.body().string();
                    }
                    // 即使HTTP状态码不是200，我们也尝试解析响应内容
                    JSONObject json = new JSONObject(responseBody);                    if (json.getInt("code") != 200) {
                        // 从JSON中获取详细的错误信息
                        String errorMsg = json.optString("msg", "后端返回错误");
                        Log.e(TAG, "API错误: " + errorMsg + ", 响应体: " + responseBody);
                        callback.onError(errorMsg);
                        return;
                    }
                      JSONArray dataArray = json.getJSONArray("data");
                    List<EnglishWords> words = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject wordObj = dataArray.getJSONObject(i);
                        EnglishWords word = new EnglishWords();
                        word.setId(wordObj.getInt("id"));
                        word.setWord(wordObj.getString("word"));
                        word.setMeaning(wordObj.getString("meaning"));
                        words.add(word);
                    }
                    callback.onSuccess(words);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON解析错误: " + e.getMessage() + ", 响应体: " + responseBody);
                    callback.onError("解析数据失败: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "未预期的错误: " + e.getMessage() + ", 响应体: " + responseBody);
                    callback.onError("处理请求时出错: " + e.getMessage());
                }
            }
        });
    }

    public interface DeleteErrorVocabularyCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public void deleteErrorVocabulary(int userId, int wordId, DeleteErrorVocabularyCallback callback) {
        String url = BASE_API_URL + "/error-vocabulary/delete?userId=" + userId + "&wordId=" + wordId;
        Request request = new Request.Builder().url(url).build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("后端接口响应失败");
                    return;
                }
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    if (json.getInt("code") != 200) {
                        callback.onError(json.optString("msg", "后端返回错误"));
                        return;
                    }
                    String message = json.getString("data");
                    callback.onSuccess(message);
                } catch (JSONException e) {
                    callback.onError("解析数据失败: " + e.getMessage());
                }
            }
        });
    }
}

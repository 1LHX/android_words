    package com.ncusoft.myapplication.service;

import android.os.AsyncTask;
import android.util.Log;

import com.ncusoft.myapplication.model.ApiResponse;
import com.ncusoft.myapplication.model.EnglishWords;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WordApiService {
    private static final String BASE_URL = "https://v2.xxapi.cn/api/englishwords?word=";
    private static final String TAG = "WordApiService";
    private OkHttpClient client;

    public WordApiService() {
        client = new OkHttpClient();
    }

    public interface WordApiCallback {
        void onSuccess(EnglishWords englishWords);

        void onError(String error);
    }

    public void getWordInfo(String word, WordApiCallback callback) {
        new AsyncTask<String, Void, ApiResponse>() {
            @Override
            protected ApiResponse doInBackground(String... words) {
                try {
                    String url = BASE_URL + words[0];
                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        Log.d(TAG, "API Response: " + jsonResponse);
                        return parseApiResponse(jsonResponse);
                    } else {
                        Log.e(TAG, "API call failed with code: " + response.code());
                        return null;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Network error: " + e.getMessage());
                    return null;
                } catch (Exception e) {
                    Log.e(TAG, "Unexpected error: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ApiResponse apiResponse) {
                if (apiResponse != null && apiResponse.getCode() == 200 && apiResponse.getData() != null) {
                    callback.onSuccess(apiResponse.getData());
                } else {
                    String errorMsg = apiResponse != null ? apiResponse.getMsg() : "Unknown error";
                    callback.onError(errorMsg);
                }
            }
        }.execute(word);
    }

    private ApiResponse parseApiResponse(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            ApiResponse apiResponse = new ApiResponse();

            apiResponse.setCode(jsonObject.getInt("code"));
            apiResponse.setMsg(jsonObject.getString("msg"));

            if (jsonObject.has("request_id")) {
                apiResponse.setRequest_id(jsonObject.getString("request_id"));
            }

            if (jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                EnglishWords englishWords = parseEnglishWords(dataObject);
                apiResponse.setData(englishWords);
            }

            return apiResponse;
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
            return null;
        }
    }

    private EnglishWords parseEnglishWords(JSONObject dataObject) throws JSONException {
        EnglishWords englishWords = new EnglishWords();

        // 解析基本字段
        if (dataObject.has("bookId")) {
            englishWords.setBookId(dataObject.getString("bookId"));
        }
        if (dataObject.has("word")) {
            englishWords.setWord(dataObject.getString("word"));
        }
        if (dataObject.has("ukphone")) {
            englishWords.setUkphone(dataObject.getString("ukphone"));
        }
        if (dataObject.has("ukspeech")) {
            englishWords.setUkspeech(dataObject.getString("ukspeech"));
        }
        if (dataObject.has("usphone")) {
            englishWords.setUsphone(dataObject.getString("usphone"));
        }
        if (dataObject.has("usspeech")) {
            englishWords.setUsspeech(dataObject.getString("usspeech"));
        }

        // 解析翻译数组
        if (dataObject.has("translations")) {
            JSONArray translationsArray = dataObject.getJSONArray("translations");
            List<EnglishWords.Translation> translations = new ArrayList<>();
            for (int i = 0; i < translationsArray.length(); i++) {
                JSONObject translationObj = translationsArray.getJSONObject(i);
                EnglishWords.Translation translation = new EnglishWords.Translation();
                if (translationObj.has("pos")) {
                    translation.setPos(translationObj.getString("pos"));
                }
                if (translationObj.has("tran_cn")) {
                    translation.setTran_cn(translationObj.getString("tran_cn"));
                }
                translations.add(translation);
            }
            englishWords.setTranslations(translations);
        }

        // 解析短语数组
        if (dataObject.has("phrases")) {
            JSONArray phrasesArray = dataObject.getJSONArray("phrases");
            List<EnglishWords.Phrase> phrases = new ArrayList<>();
            for (int i = 0; i < phrasesArray.length(); i++) {
                JSONObject phraseObj = phrasesArray.getJSONObject(i);
                EnglishWords.Phrase phrase = new EnglishWords.Phrase();
                if (phraseObj.has("p_cn")) {
                    phrase.setP_cn(phraseObj.getString("p_cn"));
                }
                if (phraseObj.has("p_content")) {
                    phrase.setP_content(phraseObj.getString("p_content"));
                }
                phrases.add(phrase);
            }
            englishWords.setPhrases(phrases);
        }

        // 解析例句数组
        if (dataObject.has("sentences")) {
            JSONArray sentencesArray = dataObject.getJSONArray("sentences");
            List<EnglishWords.Sentence> sentences = new ArrayList<>();
            for (int i = 0; i < sentencesArray.length(); i++) {
                JSONObject sentenceObj = sentencesArray.getJSONObject(i);
                EnglishWords.Sentence sentence = new EnglishWords.Sentence();
                if (sentenceObj.has("s_cn")) {
                    sentence.setS_cn(sentenceObj.getString("s_cn"));
                }
                if (sentenceObj.has("s_content")) {
                    sentence.setS_content(sentenceObj.getString("s_content"));
                }
                sentences.add(sentence);
            }
            englishWords.setSentences(sentences);
        }

        // 解析同义词数组
        if (dataObject.has("synonyms")) {
            JSONArray synonymsArray = dataObject.getJSONArray("synonyms");
            List<EnglishWords.Synonym> synonyms = new ArrayList<>();
            for (int i = 0; i < synonymsArray.length(); i++) {
                JSONObject synonymObj = synonymsArray.getJSONObject(i);
                EnglishWords.Synonym synonym = new EnglishWords.Synonym();
                if (synonymObj.has("pos")) {
                    synonym.setPos(synonymObj.getString("pos"));
                }
                if (synonymObj.has("tran")) {
                    synonym.setTran(synonymObj.getString("tran"));
                }
                if (synonymObj.has("Hwds")) {
                    JSONArray hwdsArray = synonymObj.getJSONArray("Hwds");
                    List<EnglishWords.SynonymWord> hwds = new ArrayList<>();
                    for (int j = 0; j < hwdsArray.length(); j++) {
                        JSONObject hwdObj = hwdsArray.getJSONObject(j);
                        EnglishWords.SynonymWord synonymWord = new EnglishWords.SynonymWord();
                        if (hwdObj.has("word")) {
                            synonymWord.setWord(hwdObj.getString("word"));
                        }
                        hwds.add(synonymWord);
                    }
                    synonym.setHwds(hwds);
                }
                synonyms.add(synonym);
            }
            englishWords.setSynonyms(synonyms);
        }

        // 解析相关词数组
        if (dataObject.has("relWords")) {
            JSONArray relWordsArray = dataObject.getJSONArray("relWords");
            List<EnglishWords.RelWord> relWords = new ArrayList<>();
            for (int i = 0; i < relWordsArray.length(); i++) {
                JSONObject relWordObj = relWordsArray.getJSONObject(i);
                EnglishWords.RelWord relWord = new EnglishWords.RelWord();
                if (relWordObj.has("Pos")) {
                    relWord.setPos(relWordObj.getString("Pos"));
                }
                if (relWordObj.has("Hwds")) {
                    JSONArray hwdsArray = relWordObj.getJSONArray("Hwds");
                    List<EnglishWords.Hwd> hwds = new ArrayList<>();
                    for (int j = 0; j < hwdsArray.length(); j++) {
                        JSONObject hwdObj = hwdsArray.getJSONObject(j);
                        EnglishWords.Hwd hwd = new EnglishWords.Hwd();
                        if (hwdObj.has("hwd")) {
                            hwd.setHwd(hwdObj.getString("hwd"));
                        }
                        if (hwdObj.has("tran")) {
                            hwd.setTran(hwdObj.getString("tran"));
                        }
                        hwds.add(hwd);
                    }
                    relWord.setHwds(hwds);
                }
                relWords.add(relWord);
            }
            englishWords.setRelWords(relWords);
        }

        return englishWords;
    }
}

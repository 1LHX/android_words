package com.ncusoft.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ncusoft.myapplication.model.EnglishWords;
import com.ncusoft.myapplication.model.User;
import com.ncusoft.myapplication.service.ErrorWordService;
import com.ncusoft.myapplication.service.UserService;
import com.ncusoft.myapplication.service.WordService;
import android.app.ProgressDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WordQuizActivity extends AppCompatActivity {    private List<EnglishWords> words;
    private Random random = new Random();
    private int currentQuestionIndex = 0;
    private WordService wordService;
    private ProgressDialog progressDialog;
    private int userId = -1;
    private ErrorWordService errorWordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_quiz);

        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        if (username == null || password == null) {
            Toast.makeText(this, "用户信息获取失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        wordService = new WordService(this);
        errorWordService = new ErrorWordService();
        words = new ArrayList<>();

        // 显示加载对话框
        ProgressDialog userLoadingDialog = new ProgressDialog(this);
        userLoadingDialog.setMessage("正在获取用户信息...");
        userLoadingDialog.setCancelable(false);
        userLoadingDialog.show();

        // 获取用户ID
        UserService userService = new UserService();
        userService.getUserByUsername(username, password, new UserService.GetUserCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    userLoadingDialog.dismiss();
                    userId = user.getId();
                    // 获取到用户ID后开始加载单词测验
                    startWordQuiz();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    userLoadingDialog.dismiss();
                    Toast.makeText(WordQuizActivity.this, "获取用户信息失败：" + error, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void startWordQuiz() {
        // 显示进度对话框
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载测验单词...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(5); // 只加载5个单词
        progressDialog.show();

        // 从API加载单词
        wordService.getRandomWordsFromApi(5, new WordService.WordListCallback() {
            @Override
            public void onSuccess(List<EnglishWords> englishWords) {
                runOnUiThread(() -> {
                    words.clear();
                    words.addAll(englishWords);
                    progressDialog.dismiss();

                    if (words.isEmpty()) {
                        Toast.makeText(WordQuizActivity.this, "没有加载到测验单词", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    loadQuestion();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(WordQuizActivity.this, "加载失败: " + error, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onProgress(int current, int total) {
                runOnUiThread(() -> {
                    progressDialog.setProgress(current);
                    progressDialog.setMessage("正在加载测验单词... (" + current + "/" + total + ")");
                });
            }
        });

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
    }

    private void loadQuestion() {
        if (currentQuestionIndex >= words.size()) {
            Toast.makeText(this, "Quiz ended!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        EnglishWords currentWord = words.get(currentQuestionIndex);

        TextView questionText = findViewById(R.id.question_text);
        String hintText = createHint(currentWord.getWord());

        // 从EnglishWords对象中获取meaning字段
        String meaning = currentWord.getMeaning();

        questionText.setText("What's the word?\nMeaning: " + meaning + "\nHint: " + hintText);

        EditText answerText = findViewById(R.id.answer_text);
        answerText.setText("");
    }

    private String createHint(String word) {
        if (word.length() <= 3) {
            return word.substring(0, 1) + "...";
        }
        return word.substring(0, 2) + "...";
    }

    private void checkAnswer() {
        EditText answerText = findViewById(R.id.answer_text);
        String userAnswer = answerText.getText().toString().trim();
        EnglishWords currentWord = words.get(currentQuestionIndex);
        TextView errorTip = findViewById(R.id.error_tip);
        if (userAnswer.equalsIgnoreCase(currentWord.getWord())) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            errorTip.setText("");
        } else {
            Toast.makeText(this, "Wrong! The correct answer is: " + currentWord.getWord(), Toast.LENGTH_SHORT).show();
            errorTip.setText("错题已记录");
            // 自动保存错题到后端，使用单词真实ID
            int wordId = 0;
            try {
                // 兼容EnglishWords有getId方法
                wordId = (Integer) EnglishWords.class.getMethod("getId").invoke(currentWord);
            } catch (Exception e) {
                // 若没有getId方法，兼容旧数据结构
                wordId = currentQuestionIndex + 1;
            }
            errorWordService.addErrorWord(userId, wordId, new ErrorWordService.ErrorWordCallback() {
                @Override
                public void onSuccess(java.util.List<com.ncusoft.myapplication.model.ErrorWord> errorWords) {
                    // 可选：处理成功逻辑
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(WordQuizActivity.this, "错题保存失败: " + error, Toast.LENGTH_SHORT).show());
                }
            });
        }
        currentQuestionIndex++;
        loadQuestion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
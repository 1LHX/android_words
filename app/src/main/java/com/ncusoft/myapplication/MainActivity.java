package com.ncusoft.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView tvUsername;
    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);

        // 显示用户信息
        updateUserInfo();

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WordListActivity.class);
            startActivity(intent);
        });        Button wordQuizButton = findViewById(R.id.word_quiz_button);
        wordQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WordQuizActivity.class);
            // 获取用户名和密码并传递
            SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
            String username = prefs.getString("username", "");
            String password = prefs.getString("password", "");
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            startActivity(intent);
        });
        
        Button errorBookButton = findViewById(R.id.btn_error_book);
        errorBookButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ErrorWordActivity.class);
            // 获取用户名和密码并传递
            SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
            String username = prefs.getString("username", "");
            String password = prefs.getString("password", "");
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            startActivity(intent);
        });

        Button searchWordButton = findViewById(R.id.btn_search_word);
        searchWordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchWordActivity.class);
            startActivity(intent);
        });

        Button searchBySpellingButton = findViewById(R.id.btn_search_by_spelling);
        searchBySpellingButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WordSearchActivity.class);
            startActivity(intent);
        });

        Button updateUserButton = findViewById(R.id.btn_update_user_info);
        updateUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UpdateUserActivity.class);
            intent.putExtra("username", getSharedPreferences("user_info", MODE_PRIVATE)
                    .getString("username", ""));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到主页面时更新用户信息
        updateUserInfo();
    }

    private void updateUserInfo() {
        SharedPreferences preferences = getSharedPreferences("user_info", MODE_PRIVATE);
        String username = preferences.getString("username", "");
        String email = preferences.getString("email", "");

        tvUsername.setText("用户名: " + username);
        tvEmail.setText("邮箱: " + email);
    }
}
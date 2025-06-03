package com.ncusoft.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ncusoft.myapplication.adapter.ErrorWordAdapter;
import com.ncusoft.myapplication.model.ErrorWord;
import com.ncusoft.myapplication.service.ErrorWordService;
import com.ncusoft.myapplication.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class ErrorWordActivity extends AppCompatActivity {
    private ListView lvErrorWords;
    private Button btnRefresh;
    private ErrorWordAdapter errorWordAdapter;
    private List<ErrorWord> errorWords = new ArrayList<>();
    private ErrorWordService errorWordService = new ErrorWordService();    private int userId = -1;    @Override    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_word);

        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        if (username == null || password == null) {
            Toast.makeText(this, "用户信息获取失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 显示加载对话框
        ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("正在获取用户信息...");
        loadingDialog.show();

        // 先获取用户ID
        UserService userService = new UserService();
        userService.getUserId(username, password, new UserService.GetUserIdCallback() {
            @Override
            public void onSuccess(int id) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    userId = id;
                    // 获取到ID后初始化其他内容
                    initializeErrorWordList();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(ErrorWordActivity.this, error, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void initializeErrorWordList() {
        // 初始化视图和数据
        lvErrorWords = findViewById(R.id.lv_error_words);
        btnRefresh = findViewById(R.id.btn_refresh_error);
        errorWordAdapter = new ErrorWordAdapter(this, errorWords);
        lvErrorWords.setAdapter(errorWordAdapter);

        btnRefresh.setOnClickListener(v -> loadErrorWords());
        loadErrorWords();
    }

    private void loadErrorWords() {
        errorWordService.getErrorWords(userId, new ErrorWordService.ErrorWordCallback() {
            @Override
            public void onSuccess(List<ErrorWord> result) {
                runOnUiThread(() -> {
                    errorWords.clear();
                    errorWords.addAll(result);
                    errorWordAdapter.notifyDataSetChanged();
                    Toast.makeText(ErrorWordActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ErrorWordActivity.this, error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}

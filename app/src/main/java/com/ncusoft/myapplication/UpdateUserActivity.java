package com.ncusoft.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ncusoft.myapplication.model.User;
import com.ncusoft.myapplication.service.UserService;

public class UpdateUserActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private Button btnUpdate;
    private UserService userService;
    private String oldUsername; // 用于存储当前登录用户的用户名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        // 初始化视图
        etUsername = findViewById(R.id.et_new_username);
        etPassword = findViewById(R.id.et_new_password);
        etEmail = findViewById(R.id.et_new_email);
        btnUpdate = findViewById(R.id.btn_update_user);

        // 获取当前登录用户名
        oldUsername = getIntent().getStringExtra("username");
        
        userService = new UserService();

        // 设置更新按钮点击事件
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });
    }

    private void updateUserInfo() {
        String newUsername = etUsername.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();

        // 验证输入
        if (newUsername.isEmpty() || newPassword.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "请填写所有信息", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(newUsername);
        user.setPassword(newPassword);
        user.setEmail(newEmail);

        // 调用服务更新用户信息
        userService.updateUser(oldUsername, user, new UserService.UpdateUserCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(UpdateUserActivity.this, message, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(UpdateUserActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}

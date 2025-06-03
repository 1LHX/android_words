package com.ncusoft.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncusoft.myapplication.adapter.WordSearchAdapter;
import com.ncusoft.myapplication.model.EnglishWords;
import com.ncusoft.myapplication.service.WordService;

import java.util.ArrayList;
import java.util.List;

public class WordSearchActivity extends AppCompatActivity {
    private EditText etKeyword;
    private Button btnSearch;
    private RecyclerView rvWordList;
    private WordSearchAdapter adapter;
    private WordService wordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_search);

        initViews();
        initService();
        setupListeners();
    }

    private void initViews() {
        etKeyword = findViewById(R.id.etKeyword);
        btnSearch = findViewById(R.id.btnSearch);
        rvWordList = findViewById(R.id.rvWordList);

        // 设置RecyclerView
        rvWordList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordSearchAdapter(new ArrayList<>());
        rvWordList.setAdapter(adapter);
    }

    private void initService() {
        wordService = new WordService(this);
    }

    private void setupListeners() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                btnSearch.setEnabled(s.length() > 0);
            }
        });
    }

    private void performSearch() {
        String keyword = etKeyword.getText().toString().trim();
        if (keyword.isEmpty()) {
            Toast.makeText(this, "请输入查询关键字", Toast.LENGTH_SHORT).show();
            return;
        }

        if (keyword.length() > 100) {
            Toast.makeText(this, "查询关键字长度不能超过100个字符", Toast.LENGTH_SHORT).show();
            return;
        }

        wordService.searchWordByKeyword(keyword, new WordService.WordSearchCallback() {
            @Override
            public void onSuccess(final List<EnglishWords> words) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (words.isEmpty()) {
                            Toast.makeText(WordSearchActivity.this, 
                                "未找到匹配的单词", Toast.LENGTH_SHORT).show();
                        }
                        adapter.updateWords(words);
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WordSearchActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

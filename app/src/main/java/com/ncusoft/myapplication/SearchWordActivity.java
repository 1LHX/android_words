package com.ncusoft.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ncusoft.myapplication.model.EnglishWords;
import com.ncusoft.myapplication.service.WordService;

public class SearchWordActivity extends AppCompatActivity {
    private EditText etWordId;
    private Button btnSearch;
    private TextView tvResult;
    private WordService wordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_word);

        etWordId = findViewById(R.id.etWordId);
        btnSearch = findViewById(R.id.btnSearch);
        tvResult = findViewById(R.id.tvResult);
        wordService = new WordService(this);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idStr = etWordId.getText().toString();
                if (idStr.isEmpty()) {
                    Toast.makeText(SearchWordActivity.this, "请输入单词ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                try {
                    int id = Integer.parseInt(idStr);
                    searchWord(id);
                } catch (NumberFormatException e) {
                    Toast.makeText(SearchWordActivity.this, "请输入有效的ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchWord(int id) {
        wordService.getWordById(id, new WordService.WordByIdCallback() {
            @Override
            public void onSuccess(final EnglishWords word) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String result = String.format("单词: %s\n释义: %s", 
                            word.getWord(), word.getMeaning());
                        tvResult.setText(result);
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SearchWordActivity.this, error, Toast.LENGTH_SHORT).show();
                        tvResult.setText("");
                    }
                });
            }
        });
    }
}

package com.ncusoft.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WordDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail); // 获取传递过来的单词数据
        String word = getIntent().getStringExtra("word");
        String meaning = getIntent().getStringExtra("meaning");
        String example = getIntent().getStringExtra("example");
        String phonetic = getIntent().getStringExtra("phonetic");
        String phrases = getIntent().getStringExtra("phrases");
        String synonyms = getIntent().getStringExtra("synonyms");

        // 设置界面文字
        TextView wordText = findViewById(R.id.word_text);
        TextView meaningText = findViewById(R.id.meaning_text);
        TextView exampleText = findViewById(R.id.example_text);
        TextView phoneticText = findViewById(R.id.phonetic_text);
        TextView phrasesText = findViewById(R.id.phrases_text);
        TextView synonymsText = findViewById(R.id.synonyms_text);
        TextView exampleLabel = findViewById(R.id.example_label);
        TextView phrasesLabel = findViewById(R.id.phrases_label);
        TextView synonymsLabel = findViewById(R.id.synonyms_label);

        wordText.setText(word != null ? word : "");
        meaningText.setText(meaning != null ? meaning : "");
        phoneticText.setText(phonetic != null ? phonetic : "");

        // 设置例句
        if (example != null && !example.trim().isEmpty()) {
            exampleText.setText(example);
            exampleLabel.setVisibility(TextView.VISIBLE);
        } else {
            exampleText.setText("");
            exampleLabel.setVisibility(TextView.GONE);
        }

        // 设置短语
        if (phrases != null && !phrases.trim().isEmpty()) {
            phrasesText.setText(phrases);
            phrasesLabel.setVisibility(TextView.VISIBLE);
        } else {
            phrasesText.setText("");
            phrasesLabel.setVisibility(TextView.GONE);
        }

        // 设置同义词
        if (synonyms != null && !synonyms.trim().isEmpty()) {
            synonymsText.setText(synonyms);
            synonymsLabel.setVisibility(TextView.VISIBLE);
        } else {
            synonymsText.setText("");
            synonymsLabel.setVisibility(TextView.GONE);
        }
    }
}
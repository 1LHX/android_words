package com.ncusoft.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.ncusoft.myapplication.adapter.WordAdapter;
import com.ncusoft.myapplication.model.EnglishWords;
import com.ncusoft.myapplication.service.WordService;
import java.util.ArrayList;
import java.util.List;

public class WordListActivity extends AppCompatActivity {
    private WordService wordService;
    private ProgressDialog progressDialog;
    private List<EnglishWords> words;
    private WordAdapter adapter;

    // 分页相关变量
    private int currentPage = 0;
    private final int pageSize = 10;
    private int totalPages = 1; // 默认1，后端返回后动态设置
    private int totalWords = 0; // 后端返回后动态设置
    // UI组件
    private Button btnPrevious;
    private Button btnNext;
    private Button btnRandomMode;
    private TextView tvPageInfo;

    // 模式控制
    private boolean isRandomMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        wordService = new WordService(this);
        words = new ArrayList<>();

        // 初始化UI组件
        initViews();

        // 设置ListView和适配器
        setupListView();

        // 直接加载第一页（totalPages/totalWords由接口返回后设置）
        loadCurrentPage();
    }

    private void initViews() {
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        btnRandomMode = findViewById(R.id.btn_random_mode);
        tvPageInfo = findViewById(R.id.tv_page_info);

        btnPrevious.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                loadCurrentPage();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                loadCurrentPage();
            }
        });

        // 隐藏随机模式按钮
        btnRandomMode.setVisibility(Button.GONE);
    }

    private void setupListView() {
        ListView wordListView = findViewById(R.id.word_list);
        adapter = new WordAdapter(this, words);
        wordListView.setAdapter(adapter);

        wordListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position < words.size()) {
                EnglishWords selectedWord = words.get(position);
                Intent intent = new Intent(WordListActivity.this, WordDetailActivity.class);

                // 传递完整的EnglishWords对象数据
                intent.putExtra("word", selectedWord.getWord());

                // 获取音标信息
                String phonetic = "";
                if (selectedWord.getUsphone() != null && !selectedWord.getUsphone().isEmpty()) {
                    phonetic = "US: /" + selectedWord.getUsphone() + "/";
                }
                if (selectedWord.getUkphone() != null && !selectedWord.getUkphone().isEmpty()) {
                    if (!phonetic.isEmpty())
                        phonetic += "  ";
                    phonetic += "UK: /" + selectedWord.getUkphone() + "/";
                }
                intent.putExtra("phonetic", phonetic);

                // 获取翻译信息
                String meaning = "";
                if (selectedWord.getTranslations() != null && !selectedWord.getTranslations().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (EnglishWords.Translation translation : selectedWord.getTranslations()) {
                        if (sb.length() > 0)
                            sb.append("\n");
                        if (translation.getPos() != null && !translation.getPos().isEmpty()) {
                            sb.append(translation.getPos()).append(". ");
                        }
                        sb.append(translation.getTran_cn());
                    }
                    meaning = sb.toString();
                }
                intent.putExtra("meaning", meaning);
                // 获取例句信息
                String example = "";
                if (selectedWord.getSentences() != null && !selectedWord.getSentences().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (EnglishWords.Sentence sentence : selectedWord.getSentences()) {
                        if (sb.length() > 0)
                            sb.append("\n\n");
                        sb.append(sentence.getS_content());
                        if (sentence.getS_cn() != null && !sentence.getS_cn().isEmpty()) {
                            sb.append("\n").append(sentence.getS_cn());
                        }
                    }
                    example = sb.toString();
                }
                intent.putExtra("example", example);

                // 获取短语信息
                String phrases = "";
                if (selectedWord.getPhrases() != null && !selectedWord.getPhrases().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (EnglishWords.Phrase phrase : selectedWord.getPhrases()) {
                        if (sb.length() > 0)
                            sb.append("\n\n");
                        sb.append(phrase.getP_content());
                        if (phrase.getP_cn() != null && !phrase.getP_cn().isEmpty()) {
                            sb.append("\n").append(phrase.getP_cn());
                        }
                    }
                    phrases = sb.toString();
                }
                intent.putExtra("phrases", phrases);

                // 获取同义词信息
                String synonyms = "";
                if (selectedWord.getSynonyms() != null && !selectedWord.getSynonyms().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (EnglishWords.Synonym synonym : selectedWord.getSynonyms()) {
                        if (synonym.getHwds() != null && !synonym.getHwds().isEmpty()) {
                            for (EnglishWords.SynonymWord hwd : synonym.getHwds()) {
                                if (sb.length() > 0)
                                    sb.append(", ");
                                sb.append(hwd.getWord());
                            }
                        }
                    }
                    synonyms = sb.toString();
                }
                intent.putExtra("synonyms", synonyms);

                startActivity(intent);
            }
        });
    }

    private void loadCurrentPage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载第 " + (currentPage + 1) + " 页单词...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(pageSize);
        progressDialog.show();

        // 从API加载当前页的单词
        wordService.getPagedWords(currentPage, pageSize, new WordService.WordListCallback() {
            @Override
            public void onSuccess(List<EnglishWords> englishWords) {
                runOnUiThread(() -> {
                    words.clear();
                    words.addAll(englishWords);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                    // 解析分页信息（从WordService获取最新totalPages/totalWords）
                    totalPages = wordService.getTotalPages(pageSize);
                    totalWords = wordService.getTotalWords();
                    updatePageInfo();
                    if (words.isEmpty()) {
                        Toast.makeText(WordListActivity.this, "当前页没有加载到任何单词", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(WordListActivity.this, "加载失败: " + error, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onProgress(int current, int total) {
                runOnUiThread(() -> {
                    progressDialog.setProgress(current);
                    progressDialog.setMessage("正在加载第 " + (currentPage + 1) + " 页单词... (" + current + "/" + total + ")");
                });
            }
        });
    }

    private void updatePageInfo() {
        String pageInfo = String.format("第 %d / %d 页 (共 %d 个单词)",
                currentPage + 1, totalPages, totalWords);
        tvPageInfo.setText(pageInfo);

        // 更新按钮状态
        btnPrevious.setEnabled(currentPage > 0);
        btnNext.setEnabled(currentPage < totalPages - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
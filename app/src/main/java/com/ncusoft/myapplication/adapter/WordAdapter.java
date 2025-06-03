package com.ncusoft.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ncusoft.myapplication.R;
import com.ncusoft.myapplication.model.EnglishWords;

import java.util.List;

public class WordAdapter extends ArrayAdapter<EnglishWords> {
    private final Context context;

    public WordAdapter(@NonNull Context context, List<EnglishWords> words) {
        super(context, 0, words);
        this.context = context;
    }    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.word_list_item, parent, false);
        }

        EnglishWords englishWord = getItem(position);

        TextView wordText = listItem.findViewById(R.id.word_text);
        TextView meaningText = listItem.findViewById(R.id.meaning_text);

        if (englishWord != null) {
            wordText.setText(englishWord.getWord());
            
            // 获取翻译作为意思显示
            String meaning = "";
            if (englishWord.getTranslations() != null && !englishWord.getTranslations().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (EnglishWords.Translation translation : englishWord.getTranslations()) {
                    if (sb.length() > 0) sb.append("; ");
                    if (translation.getPos() != null && !translation.getPos().isEmpty()) {
                        sb.append(translation.getPos()).append(". ");
                    }
                    sb.append(translation.getTran_cn());
                }
                meaning = sb.toString();
            }
            meaningText.setText(meaning);
        }

        return listItem;
    }
}
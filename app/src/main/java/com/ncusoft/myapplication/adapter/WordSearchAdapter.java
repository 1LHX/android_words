package com.ncusoft.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ncusoft.myapplication.R;
import com.ncusoft.myapplication.model.EnglishWords;

import java.util.List;

public class WordSearchAdapter extends RecyclerView.Adapter<WordSearchAdapter.WordViewHolder> {
    private List<EnglishWords> words;

    public WordSearchAdapter(List<EnglishWords> words) {
        this.words = words;
    }

    public void updateWords(List<EnglishWords> newWords) {
        this.words = newWords;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        EnglishWords word = words.get(position);
        holder.tvWord.setText(word.getWord());
        holder.tvMeaning.setText(word.getMeaning());
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord;
        TextView tvMeaning;

        WordViewHolder(View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
        }
    }
}

package com.ncusoft.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ncusoft.myapplication.model.ErrorWord;
import com.ncusoft.myapplication.R;
import com.ncusoft.myapplication.service.ErrorWordService;

import android.app.Activity;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;

public class ErrorWordAdapter extends BaseAdapter {
    private Context context;
    private List<ErrorWord> errorWords;
    private ErrorWordService errorWordService;
    private int userId = 1; // TODO: 从全局配置或登录信息中获取

    public ErrorWordAdapter(Context context, List<ErrorWord> errorWords) {
        this.context = context;
        this.errorWords = errorWords;
        this.errorWordService = new ErrorWordService();
    }

    @Override
    public int getCount() {
        return errorWords.size();
    }

    @Override
    public Object getItem(int position) {
        return errorWords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_error_word, parent, false);
            holder = new ViewHolder();
            holder.tvWord = convertView.findViewById(R.id.tv_error_word);
            holder.tvMeaning = convertView.findViewById(R.id.tv_error_meaning);
            holder.btnDelete = convertView.findViewById(R.id.btn_delete_error);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ErrorWord ew = errorWords.get(position);
        holder.tvWord.setText(ew.getWord() != null ? ew.getWord() : ("单词ID: " + ew.getWordId()));
        holder.tvMeaning.setText(ew.getMeaning() != null ? ew.getMeaning() : "");

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorWordService.deleteErrorWord(userId, ew.getWordId(), new ErrorWordService.ErrorWordCallback() {
                    @Override
                    public void onSuccess(List<ErrorWord> result) {
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 从列表中移除
                                    errorWords.remove(ew);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "删除失败: " + error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });

        return convertView;
    }    static class ViewHolder {
        TextView tvWord;
        TextView tvMeaning;
        Button btnDelete;
    }
}

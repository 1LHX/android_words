package com.ncusoft.myapplication.model;

public class ErrorWord {
    private int id;
    private int userId;
    private int wordId;
    private String word;
    private String meaning;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getWordId() { return wordId; }
    public void setWordId(int wordId) { this.wordId = wordId; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }
}

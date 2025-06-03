package com.ncusoft.myapplication.model;

import java.util.List;

public class EnglishWords {
    private String bookId;
    private List<Phrase> phrases;
    private List<RelWord> relWords;
    private List<Sentence> sentences;
    private List<Synonym> synonyms;
    private List<Translation> translations;
    private String ukphone;
    private String ukspeech;
    private String usphone;
    private String usspeech;
    private String word; // 内部类
    // 新增字段和方法用于兼容后端返回的id和meaning
    private int id;
    private String meaning;

    public static class Phrase {
        private String p_cn;
        private String p_content;

        public String getP_cn() {
            return p_cn;
        }

        public String getP_content() {
            return p_content;
        }

        public void setP_cn(String p_cn) {
            this.p_cn = p_cn;
        }

        public void setP_content(String p_content) {
            this.p_content = p_content;
        }
    }

    public static class RelWord {
        private List<Hwd> Hwds;
        private String Pos;

        public List<Hwd> getHwds() {
            return Hwds;
        }

        public String getPos() {
            return Pos;
        }

        public void setHwds(List<Hwd> hwds) {
            this.Hwds = hwds;
        }

        public void setPos(String pos) {
            this.Pos = pos;
        }
    }

    public static class Hwd {
        private String hwd;
        private String tran;

        public String getHwd() {
            return hwd;
        }

        public String getTran() {
            return tran;
        }

        public void setHwd(String hwd) {
            this.hwd = hwd;
        }

        public void setTran(String tran) {
            this.tran = tran;
        }
    }

    public static class Sentence {
        private String s_cn;
        private String s_content;

        public String getS_cn() {
            return s_cn;
        }

        public String getS_content() {
            return s_content;
        }

        public void setS_cn(String s_cn) {
            this.s_cn = s_cn;
        }

        public void setS_content(String s_content) {
            this.s_content = s_content;
        }
    }

    public static class Synonym {
        private List<SynonymWord> Hwds;
        private String pos;
        private String tran;

        public List<SynonymWord> getHwds() {
            return Hwds;
        }

        public String getPos() {
            return pos;
        }

        public String getTran() {
            return tran;
        }

        public void setHwds(List<SynonymWord> hwds) {
            this.Hwds = hwds;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }

        public void setTran(String tran) {
            this.tran = tran;
        }
    }

    public static class SynonymWord {
        private String word;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }
    }

    public static class Translation {
        private String pos;
        private String tran_cn;

        public String getPos() {
            return pos;
        }

        public String getTran_cn() {
            return tran_cn;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }

        public void setTran_cn(String tran_cn) {
            this.tran_cn = tran_cn;
        }
    }

    // Getter 方法
    public String getBookId() {
        return bookId;
    }

    public List<Phrase> getPhrases() {
        return phrases;
    }

    public List<RelWord> getRelWords() {
        return relWords;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public List<Synonym> getSynonyms() {
        return synonyms;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public String getUkphone() {
        return ukphone;
    }

    public String getUkspeech() {
        return ukspeech;
    }

    public String getUsphone() {
        return usphone;
    }

    public String getUsspeech() {
        return usspeech;
    }

    public String getWord() {
        return word;
    }

    public int getId() {
        return id;
    }

    public String getMeaning() {
        return meaning;
    }

    // Setter 方法
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setPhrases(List<Phrase> phrases) {
        this.phrases = phrases;
    }

    public void setRelWords(List<RelWord> relWords) {
        this.relWords = relWords;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public void setSynonyms(List<Synonym> synonyms) {
        this.synonyms = synonyms;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }

    public void setUkphone(String ukphone) {
        this.ukphone = ukphone;
    }

    public void setUkspeech(String ukspeech) {
        this.ukspeech = ukspeech;
    }

    public void setUsphone(String usphone) {
        this.usphone = usphone;
    }

    public void setUsspeech(String usspeech) {
        this.usspeech = usspeech;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
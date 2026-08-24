package com.saim.englishlearning.model;

public class SentencePair {
    public final int id;
    public final String category;
    public final int level;
    public final String urdu;
    public final String simple;
    public final String advanced;

    public SentencePair(int id, String category, int level, String urdu,
                        String simple, String advanced) {
        this.id = id;
        this.category = category;
        this.level = level;
        this.urdu = urdu;
        this.simple = simple;
        this.advanced = advanced;
    }
}

package com.saim.englishlearning.model;

public class SentencePair {
    public final int id;
    public final int level;
    public final String urdu;
    public final String simple;
    public final String advanced;

    public SentencePair(int id, int level, String urdu, String simple, String advanced) {
        this.id = id;
        this.level = level;
        this.urdu = urdu;
        this.simple = simple;
        this.advanced = advanced;
    }
}

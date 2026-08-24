package com.saim.englishlearning.model;

public class Phrase {
    public static final int KIND_IDIOM = 0;
    public static final int KIND_PHRASAL = 1;

    public final int id;
    public final int kind;
    public final int level;
    public final String phrase;
    public final String meaning;
    public final String urdu;
    public final String example;

    public Phrase(int id, int kind, int level, String phrase, String meaning, String urdu, String example) {
        this.id = id;
        this.kind = kind;
        this.level = level;
        this.phrase = phrase;
        this.meaning = meaning;
        this.urdu = urdu;
        this.example = example;
    }

    public boolean isIdiom() {
        return kind == KIND_IDIOM;
    }
}

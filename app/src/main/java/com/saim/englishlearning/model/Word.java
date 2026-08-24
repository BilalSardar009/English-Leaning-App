package com.saim.englishlearning.model;

public class Word {
    public final int id;
    public final int level;
    public final String word;
    public final String urdu;
    public final String meaning;
    public final String example;

    public Word(int id, int level, String word, String urdu, String meaning, String example) {
        this.id = id;
        this.level = level;
        this.word = word;
        this.urdu = urdu;
        this.meaning = meaning;
        this.example = example;
    }

    public static String levelName(int level) {
        switch (level) {
            case 1: return "Beginner";
            case 2: return "Intermediate";
            case 3: return "Advanced";
            default: return "Expert";
        }
    }

    public static String levelNameUrdu(int level) {
        switch (level) {
            case 1: return "ابتدائی";
            case 2: return "درمیانہ";
            case 3: return "اعلیٰ";
            default: return "ماہر";
        }
    }
}

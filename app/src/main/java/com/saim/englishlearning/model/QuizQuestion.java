package com.saim.englishlearning.model;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestion {
    public final String prompt;
    public final List<String> options;
    public final int correctIndex;
    public final String hint;

    public QuizQuestion(String prompt, List<String> options, int correctIndex, String hint) {
        this.prompt = prompt;
        this.options = options;
        this.correctIndex = correctIndex;
        this.hint = hint;
    }

    public QuizQuestion(String prompt, String correct, String a, String b, String c, String hint) {
        List<String> list = new ArrayList<>();
        list.add(correct);
        list.add(a);
        list.add(b);
        list.add(c);
        this.prompt = prompt;
        this.options = list;
        this.correctIndex = 0;
        this.hint = hint;
    }

    public String correctAnswer() {
        return options.get(correctIndex);
    }
}

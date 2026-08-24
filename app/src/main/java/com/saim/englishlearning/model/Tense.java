package com.saim.englishlearning.model;

import java.util.List;

public class Tense {
    public final String name;
    public final String urduName;
    public final String formula;
    public final String usage;
    public final String usageUrdu;
    public final List<String> examples;
    public final List<String> examplesUrdu;
    public final List<QuizQuestion> practice;

    public Tense(String name, String urduName, String formula, String usage, String usageUrdu,
                 List<String> examples, List<String> examplesUrdu, List<QuizQuestion> practice) {
        this.name = name;
        this.urduName = urduName;
        this.formula = formula;
        this.usage = usage;
        this.usageUrdu = usageUrdu;
        this.examples = examples;
        this.examplesUrdu = examplesUrdu;
        this.practice = practice;
    }
}

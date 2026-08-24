package com.saim.englishlearning.data;

import com.saim.englishlearning.model.Phrase;
import com.saim.englishlearning.model.QuizQuestion;
import com.saim.englishlearning.model.Tense;
import com.saim.englishlearning.model.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/** Builds practice questions on the fly so the exercises never run out. */
public final class ExerciseGenerator {

    public static final int TYPE_MEANING = 0;
    public static final int TYPE_URDU = 1;
    public static final int TYPE_URDU_TO_ENGLISH = 2;
    public static final int TYPE_BLANK = 3;
    public static final int TYPE_IDIOM = 4;
    public static final int TYPE_PHRASAL = 5;
    public static final int TYPE_MIXED = 6;

    private static final Random RANDOM = new Random();

    private ExerciseGenerator() {
    }

    public static String typeTitle(int type) {
        switch (type) {
            case TYPE_MEANING: return "Word Meanings";
            case TYPE_URDU: return "English to Urdu";
            case TYPE_URDU_TO_ENGLISH: return "Urdu to English";
            case TYPE_BLANK: return "Fill in the Blank";
            case TYPE_IDIOM: return "Idiom Meanings";
            case TYPE_PHRASAL: return "Phrasal Verbs";
            default: return "Mixed Practice";
        }
    }

    public static List<QuizQuestion> generate(int level, int type, int count) {
        List<QuizQuestion> out = new ArrayList<>();
        if (type == TYPE_MIXED) {
            int[] pool = {TYPE_MEANING, TYPE_URDU, TYPE_URDU_TO_ENGLISH, TYPE_BLANK, TYPE_IDIOM, TYPE_PHRASAL};
            for (int i = 0; i < count; i++) {
                int pick = pool[RANDOM.nextInt(pool.length)];
                List<QuizQuestion> one = generate(level, pick, 1);
                if (!one.isEmpty()) out.add(one.get(0));
            }
            return out;
        }

        if (type == TYPE_IDIOM || type == TYPE_PHRASAL) {
            return fromPhrases(level, type == TYPE_IDIOM ? Phrase.KIND_IDIOM : Phrase.KIND_PHRASAL, count);
        }
        return fromWords(level, type, count);
    }

    // ------------------------------------------------------------------ words

    private static List<QuizQuestion> fromWords(int level, int type, int count) {
        List<QuizQuestion> out = new ArrayList<>();
        List<Word> pool = WordBank.byLevel(level);
        if (pool.size() < 6) pool = new ArrayList<>(WordBank.all());
        if (pool.size() < 6) return out;

        List<Word> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled, RANDOM);

        for (Word word : shuffled) {
            if (out.size() >= count) break;
            QuizQuestion question = buildWordQuestion(word, pool, type);
            if (question != null) out.add(question);
        }
        return out;
    }

    private static QuizQuestion buildWordQuestion(Word word, List<Word> pool, int type) {
        switch (type) {
            case TYPE_MEANING: {
                List<String> options = distractors(pool, word, Field.MEANING, word.meaning);
                if (options == null) return null;
                return finish("What does \"" + word.word + "\" mean?", word.meaning, options,
                        "Example: " + word.example);
            }
            case TYPE_URDU: {
                List<String> options = distractors(pool, word, Field.URDU, word.urdu);
                if (options == null) return null;
                return finish("Choose the Urdu for \"" + word.word + "\"", word.urdu, options,
                        word.meaning);
            }
            case TYPE_URDU_TO_ENGLISH: {
                List<String> options = distractors(pool, word, Field.WORD, word.word);
                if (options == null) return null;
                return finish("Which English word means \"" + word.urdu + "\"?", word.word, options,
                        word.meaning);
            }
            case TYPE_BLANK:
            default: {
                String blanked = blankOut(word.example, word.word);
                if (blanked == null) return null;
                List<String> options = distractors(pool, word, Field.WORD, word.word);
                if (options == null) return null;
                return finish(blanked, word.word, options, word.meaning);
            }
        }
    }

    /** Replaces the target word inside its own example sentence with a gap. */
    private static String blankOut(String sentence, String word) {
        if (sentence == null || word == null) return null;
        String lowerSentence = sentence.toLowerCase(Locale.ROOT);
        String lowerWord = word.toLowerCase(Locale.ROOT);
        int at = lowerSentence.indexOf(lowerWord);
        if (at < 0) {
            // Try the stem so that "goes" still matches "go".
            if (lowerWord.length() > 4) {
                String stem = lowerWord.substring(0, lowerWord.length() - 1);
                at = lowerSentence.indexOf(stem);
                if (at < 0) return null;
                return sentence.substring(0, at) + "______" + sentence.substring(at + stem.length());
            }
            return null;
        }
        return sentence.substring(0, at) + "______" + sentence.substring(at + word.length());
    }

    private enum Field { WORD, MEANING, URDU }

    private static String valueOf(Word w, Field field) {
        switch (field) {
            case WORD: return w.word;
            case URDU: return w.urdu;
            default: return w.meaning;
        }
    }

    /** Picks three wrong answers that are all different from each other. */
    private static List<String> distractors(List<Word> pool, Word correctWord, Field field, String correct) {
        Set<String> used = new HashSet<>();
        used.add(correct.trim().toLowerCase(Locale.ROOT));
        List<String> options = new ArrayList<>();
        int guard = 0;
        while (options.size() < 3 && guard < 300) {
            guard++;
            Word candidate = pool.get(RANDOM.nextInt(pool.size()));
            if (candidate.id == correctWord.id) continue;
            String value = valueOf(candidate, field);
            if (value == null || value.trim().isEmpty()) continue;
            String key = value.trim().toLowerCase(Locale.ROOT);
            if (used.contains(key)) continue;
            used.add(key);
            options.add(value);
        }
        if (options.size() < 3) return null;
        return options;
    }

    private static QuizQuestion finish(String prompt, String correct, List<String> wrong, String hint) {
        List<String> all = new ArrayList<>();
        all.add(correct);
        all.addAll(wrong);
        Collections.shuffle(all, RANDOM);
        return new QuizQuestion(prompt, all, all.indexOf(correct), hint);
    }

    // ---------------------------------------------------------------- phrases

    private static List<QuizQuestion> fromPhrases(int level, int kind, int count) {
        List<QuizQuestion> out = new ArrayList<>();
        List<Phrase> pool = PhraseBank.byKindUpToLevel(kind, level);
        if (pool.size() < 6) pool = PhraseBank.byKind(kind);
        if (pool.size() < 6) return out;

        List<Phrase> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled, RANDOM);

        for (Phrase phrase : shuffled) {
            if (out.size() >= count) break;
            Set<String> used = new HashSet<>();
            used.add(phrase.meaning.trim().toLowerCase(Locale.ROOT));
            List<String> wrong = new ArrayList<>();
            int guard = 0;
            while (wrong.size() < 3 && guard < 200) {
                guard++;
                Phrase candidate = pool.get(RANDOM.nextInt(pool.size()));
                if (candidate.id == phrase.id) continue;
                String key = candidate.meaning.trim().toLowerCase(Locale.ROOT);
                if (used.contains(key)) continue;
                used.add(key);
                wrong.add(candidate.meaning);
            }
            if (wrong.size() < 3) continue;
            out.add(finish("What does \"" + phrase.phrase + "\" mean?", phrase.meaning, wrong,
                    "Example: " + phrase.example));
        }
        return out;
    }

    // ----------------------------------------------------------------- tenses

    /** Shuffles a tense's stored practice so repeats do not feel identical. */
    public static List<QuizQuestion> forTense(Tense tense) {
        List<QuizQuestion> out = new ArrayList<>();
        if (tense == null) return out;
        List<QuizQuestion> source = new ArrayList<>(tense.practice);
        Collections.shuffle(source, RANDOM);
        for (QuizQuestion original : source) {
            String correct = original.correctAnswer();
            List<String> options = new ArrayList<>(original.options);
            Collections.shuffle(options, RANDOM);
            out.add(new QuizQuestion(original.prompt, options, options.indexOf(correct), original.hint));
        }
        return out;
    }
}

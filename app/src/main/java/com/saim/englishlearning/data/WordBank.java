package com.saim.englishlearning.data;

import android.content.Context;

import com.saim.englishlearning.model.Word;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class WordBank {

    private static final List<Word> WORDS = new ArrayList<>();

    private WordBank() {
    }

    public static synchronized void init(Context context) {
        if (!WORDS.isEmpty()) return;
        int id = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.getAssets().open("words.tsv"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length != 5) continue;
                int level;
                try {
                    level = Integer.parseInt(parts[0].trim());
                } catch (NumberFormatException ignored) {
                    continue;
                }
                WORDS.add(new Word(id++, level, parts[1].trim(), parts[2].trim(),
                        parts[3].trim(), parts[4].trim()));
            }
        } catch (Exception ignored) {
            // A missing asset simply leaves the bank empty; callers handle that.
        }
    }

    public static List<Word> all() {
        return Collections.unmodifiableList(WORDS);
    }

    public static int size() {
        return WORDS.size();
    }

    public static List<Word> byLevel(int level) {
        List<Word> out = new ArrayList<>();
        for (Word w : WORDS) {
            if (w.level == level) out.add(w);
        }
        return out;
    }

    public static int countForLevel(int level) {
        int n = 0;
        for (Word w : WORDS) {
            if (w.level == level) n++;
        }
        return n;
    }

    public static Word byId(int id) {
        for (Word w : WORDS) {
            if (w.id == id) return w;
        }
        return null;
    }

    public static List<Word> search(String query) {
        List<Word> out = new ArrayList<>();
        if (query == null) return out;
        String q = query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return out;
        for (Word w : WORDS) {
            if (w.word.toLowerCase(Locale.ROOT).contains(q)
                    || w.meaning.toLowerCase(Locale.ROOT).contains(q)
                    || w.urdu.contains(q)) {
                out.add(w);
            }
            if (out.size() >= 120) break;
        }
        return out;
    }

    /** Same word for the whole day, and it advances every day. */
    public static Word wordOfTheDay() {
        if (WORDS.isEmpty()) return null;
        long day = System.currentTimeMillis() / (24L * 60 * 60 * 1000);
        return WORDS.get((int) Math.abs(day % WORDS.size()));
    }
}

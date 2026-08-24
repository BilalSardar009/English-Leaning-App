package com.saim.englishlearning.data;

import android.content.Context;

import com.saim.englishlearning.model.SentencePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SentenceBank {

    private static final List<SentencePair> PAIRS = new ArrayList<>();

    private SentenceBank() {
    }

    public static synchronized void init(Context context) {
        if (!PAIRS.isEmpty()) return;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.getAssets().open("sentences.tsv"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length != 4) continue;
                int level;
                try {
                    level = Integer.parseInt(parts[0].trim());
                } catch (NumberFormatException ignored) {
                    continue;
                }
                PAIRS.add(new SentencePair(PAIRS.size(), level, parts[1].trim(),
                        parts[2].trim(), parts[3].trim()));
            }
        } catch (Exception ignored) {
            // Missing asset leaves the bank empty; the screens show a friendly notice.
        }
    }

    public static List<SentencePair> all() {
        return Collections.unmodifiableList(PAIRS);
    }

    public static int size() {
        return PAIRS.size();
    }

    public static List<SentencePair> byLevel(int level) {
        List<SentencePair> out = new ArrayList<>();
        for (SentencePair p : PAIRS) {
            if (p.level == level) out.add(p);
        }
        if (out.isEmpty()) return new ArrayList<>(PAIRS);
        return out;
    }

    public static List<SentencePair> upToLevel(int level) {
        List<SentencePair> out = new ArrayList<>();
        for (SentencePair p : PAIRS) {
            if (p.level <= level) out.add(p);
        }
        if (out.isEmpty()) return new ArrayList<>(PAIRS);
        return out;
    }
}

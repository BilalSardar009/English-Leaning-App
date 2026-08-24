package com.saim.englishlearning.data;

import android.content.Context;

import com.saim.englishlearning.model.Phrase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class PhraseBank {

    private static final List<Phrase> PHRASES = new ArrayList<>();

    private PhraseBank() {
    }

    public static synchronized void init(Context context) {
        if (!PHRASES.isEmpty()) return;
        load(context, "idioms.tsv", Phrase.KIND_IDIOM);
        load(context, "phrasals.tsv", Phrase.KIND_PHRASAL);
    }

    private static void load(Context context, String asset, int kind) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.getAssets().open(asset), StandardCharsets.UTF_8))) {
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
                PHRASES.add(new Phrase(PHRASES.size(), kind, level, parts[1].trim(),
                        parts[2].trim(), parts[3].trim(), parts[4].trim()));
            }
        } catch (Exception ignored) {
            // Missing asset leaves this kind out; the screens handle an empty list.
        }
    }

    public static List<Phrase> all() {
        return Collections.unmodifiableList(PHRASES);
    }

    public static int size() {
        return PHRASES.size();
    }

    public static Phrase byId(int id) {
        for (Phrase p : PHRASES) {
            if (p.id == id) return p;
        }
        return null;
    }

    public static List<Phrase> byKind(int kind) {
        List<Phrase> out = new ArrayList<>();
        for (Phrase p : PHRASES) {
            if (p.kind == kind) out.add(p);
        }
        return out;
    }

    public static List<Phrase> byKindUpToLevel(int kind, int level) {
        List<Phrase> out = new ArrayList<>();
        for (Phrase p : PHRASES) {
            if (p.kind == kind && p.level <= level) out.add(p);
        }
        if (out.isEmpty()) return byKind(kind);
        return out;
    }

    public static List<Phrase> search(String query) {
        List<Phrase> out = new ArrayList<>();
        if (query == null) return out;
        String q = query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return out;
        for (Phrase p : PHRASES) {
            if (p.phrase.toLowerCase(Locale.ROOT).contains(q)
                    || p.meaning.toLowerCase(Locale.ROOT).contains(q)
                    || p.urdu.contains(q)) {
                out.add(p);
            }
        }
        return out;
    }

    /** Rotates once a day, offset from the word of the day so the two differ. */
    public static Phrase phraseOfTheDay() {
        if (PHRASES.isEmpty()) return null;
        long day = System.currentTimeMillis() / (24L * 60 * 60 * 1000);
        return PHRASES.get((int) Math.abs((day * 7 + 3) % PHRASES.size()));
    }
}

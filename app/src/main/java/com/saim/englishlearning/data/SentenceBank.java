package com.saim.englishlearning.data;

import android.content.Context;

import com.saim.englishlearning.model.SentencePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SentenceBank {

    /** A place or situation the sentences belong to. */
    public static class Topic {
        public final String key;
        public final String title;
        public final String emoji;
        public final String description;

        Topic(String key, String title, String emoji, String description) {
            this.key = key;
            this.title = title;
            this.emoji = emoji;
            this.description = description;
        }
    }

    private static final List<Topic> TOPICS = Collections.unmodifiableList(Arrays.asList(
            new Topic("everyday", "Everyday", "💬", "Greetings, manners and daily talk"),
            new Topic("home", "At Home", "🏠", "Meals, chores and family talk"),
            new Topic("classroom", "Classroom", "🎓", "Asking, answering and school talk"),
            new Topic("market", "Market", "🛒", "Prices, bargaining and buying"),
            new Topic("doctor", "Doctor", "🩺", "Symptoms, medicine and check ups"),
            new Topic("travel", "Travel", "🚌", "Buses, directions and tickets"),
            new Topic("food", "Restaurant", "🍽️", "Ordering, tasting and paying"),
            new Topic("friends", "Friends", "🧒", "Playing, sharing and making up"),
            new Topic("phone", "Phone", "📱", "Calls, messages and email"),
            new Topic("work", "Work", "💼", "Tasks, meetings and reports")
    ));

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
                if (parts.length != 5) continue;
                int level;
                try {
                    level = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException ignored) {
                    continue;
                }
                PAIRS.add(new SentencePair(PAIRS.size(), parts[0].trim(), level,
                        parts[2].trim(), parts[3].trim(), parts[4].trim()));
            }
        } catch (Exception ignored) {
            // Missing asset leaves the bank empty; the screens show a friendly notice.
        }
    }

    public static List<Topic> topics() {
        return TOPICS;
    }

    public static Topic topic(String key) {
        for (Topic t : TOPICS) {
            if (t.key.equals(key)) return t;
        }
        return TOPICS.get(0);
    }

    public static List<SentencePair> all() {
        return Collections.unmodifiableList(PAIRS);
    }

    public static int size() {
        return PAIRS.size();
    }

    public static int countInTopic(String key) {
        int n = 0;
        for (SentencePair p : PAIRS) {
            if (p.category.equals(key)) n++;
        }
        return n;
    }

    /**
     * Sentences for one place, at or below the learner's level. Falls back to the
     * whole topic if that leaves too few, so a beginner still sees something.
     */
    public static List<SentencePair> byTopic(String key, int level) {
        List<SentencePair> out = new ArrayList<>();
        for (SentencePair p : PAIRS) {
            if (p.category.equals(key) && p.level <= level) out.add(p);
        }
        if (out.size() < 5) {
            out.clear();
            for (SentencePair p : PAIRS) {
                if (p.category.equals(key)) out.add(p);
            }
        }
        return out;
    }

    /** Used by the scramble game, which does not care which place a line is from. */
    public static List<SentencePair> upToLevel(int level) {
        List<SentencePair> out = new ArrayList<>();
        for (SentencePair p : PAIRS) {
            if (p.level <= level) out.add(p);
        }
        if (out.isEmpty()) return new ArrayList<>(PAIRS);
        return out;
    }
}

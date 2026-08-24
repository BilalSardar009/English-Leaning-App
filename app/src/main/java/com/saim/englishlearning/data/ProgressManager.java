package com.saim.englishlearning.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.saim.englishlearning.model.Achievement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Single store for everything the app remembers about the learner. */
public class ProgressManager {

    private static final String PREFS = "saim_english_progress";

    private static final String KEY_XP = "xp";
    private static final String KEY_LEARNED = "learned_words";
    private static final String KEY_FAVOURITES = "favorite_words";
    private static final String KEY_MASTERED_PHRASES = "mastered_phrases";
    private static final String KEY_STREAK = "streak";
    private static final String KEY_LAST_DAY = "last_active_day";
    private static final String KEY_QUIZ_COUNT = "quiz_count";
    private static final String KEY_LOOKUPS = "lookup_count";
    private static final String KEY_WOTD = "wotd_count";
    private static final String KEY_SENTENCES = "sentence_count";
    private static final String KEY_FLIPS = "flashcard_flips";
    private static final String KEY_GAMES = "game_count";
    private static final String KEY_TRANSLATIONS = "translation_count";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_LEVEL = "user_level";
    private static final String KEY_ONBOARDED = "onboarded";
    private static final String KEY_TOUR_DONE = "tour_done";
    private static final String KEY_URDU = "urdu_enabled";
    private static final String KEY_ANIMATIONS = "animations_enabled";
    private static final String KEY_GEMINI = "gemini_api_key";
    private static final String KEY_REMINDER_ON = "reminder_on";
    private static final String KEY_REMINDER_HOUR = "reminder_hour";
    private static final String KEY_REMINDER_MINUTE = "reminder_minute";
    private static final String PREFIX_BEST = "best_score_";
    private static final String PREFIX_TENSE_BEST = "tense_best_";
    private static final String PREFIX_GAME_BEST = "game_best_";
    private static final String KEY_TENSE_PRACTICES = "tense_practices";
    private static final String KEY_UNLOCKED = "unlocked_achievements";

    private final SharedPreferences prefs;

    public ProgressManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // ---------------------------------------------------------------- profile

    public String getUserName() {
        return prefs.getString(KEY_NAME, "");
    }

    public void setUserName(String name) {
        prefs.edit().putString(KEY_NAME, name == null ? "" : name.trim()).apply();
    }

    public int getLevel() {
        return prefs.getInt(KEY_LEVEL, 1);
    }

    public void setLevel(int level) {
        prefs.edit().putInt(KEY_LEVEL, Math.max(1, Math.min(4, level))).apply();
    }

    public boolean isOnboarded() {
        return prefs.getBoolean(KEY_ONBOARDED, false);
    }

    public void setOnboarded(boolean value) {
        prefs.edit().putBoolean(KEY_ONBOARDED, value).apply();
    }

    public boolean isTourDone() {
        return prefs.getBoolean(KEY_TOUR_DONE, false);
    }

    public void setTourDone(boolean value) {
        prefs.edit().putBoolean(KEY_TOUR_DONE, value).apply();
    }

    /** Urdu support stays off until the learner turns it on in Settings. */
    public boolean isUrduEnabled() {
        return prefs.getBoolean(KEY_URDU, false);
    }

    public void setUrduEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_URDU, enabled).apply();
    }

    public boolean areAnimationsEnabled() {
        return prefs.getBoolean(KEY_ANIMATIONS, true);
    }

    public void setAnimationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ANIMATIONS, enabled).apply();
    }

    public String getGeminiKey() {
        return prefs.getString(KEY_GEMINI, "").trim();
    }

    public void setGeminiKey(String key) {
        prefs.edit().putString(KEY_GEMINI, key == null ? "" : key.trim()).apply();
    }

    // --------------------------------------------------------------------- xp

    public int getXp() {
        return prefs.getInt(KEY_XP, 0);
    }

    public void addXp(int amount) {
        if (amount <= 0) return;
        prefs.edit().putInt(KEY_XP, getXp() + amount).apply();
    }

    /** Every 100 XP is one rank. Ranks start at 1. */
    public int getRank() {
        return 1 + getXp() / 100;
    }

    public int getXpIntoRank() {
        return getXp() % 100;
    }

    // ----------------------------------------------------------------- streak

    public int getStreak() {
        return prefs.getInt(KEY_STREAK, 0);
    }

    /** Call once whenever the app is opened. Returns true when the streak grew. */
    public boolean touchStreak() {
        long today = System.currentTimeMillis() / (24L * 60 * 60 * 1000);
        long last = prefs.getLong(KEY_LAST_DAY, -1);
        if (last == today) return false;
        int streak = getStreak();
        if (last == today - 1) {
            streak = streak + 1;
        } else {
            streak = 1;
        }
        prefs.edit().putLong(KEY_LAST_DAY, today).putInt(KEY_STREAK, streak).apply();
        return true;
    }

    // ------------------------------------------------------------ learned set

    public Set<String> getLearnedWords() {
        return new HashSet<>(prefs.getStringSet(KEY_LEARNED, new HashSet<String>()));
    }

    public int getLearnedCount() {
        return getLearnedWords().size();
    }

    public boolean isLearned(int wordId) {
        return getLearnedWords().contains(String.valueOf(wordId));
    }

    public void markLearned(int wordId) {
        Set<String> set = getLearnedWords();
        if (set.add(String.valueOf(wordId))) {
            prefs.edit().putStringSet(KEY_LEARNED, set).apply();
            addXp(5);
        }
    }

    // --------------------------------------------------------------- favourites

    public Set<String> getFavourites() {
        return new LinkedHashSet<>(prefs.getStringSet(KEY_FAVOURITES, new LinkedHashSet<String>()));
    }

    public int getFavouriteCount() {
        return getFavourites().size();
    }

    public boolean isFavourite(int wordId) {
        return getFavourites().contains(String.valueOf(wordId));
    }

    /** Returns the new state so callers can animate the star. */
    public boolean toggleFavourite(int wordId) {
        Set<String> set = getFavourites();
        String id = String.valueOf(wordId);
        boolean nowFavourite;
        if (set.contains(id)) {
            set.remove(id);
            nowFavourite = false;
        } else {
            set.add(id);
            nowFavourite = true;
        }
        prefs.edit().putStringSet(KEY_FAVOURITES, set).apply();
        return nowFavourite;
    }

    // ----------------------------------------------------------- phrase mastery

    public Set<String> getMasteredPhrases() {
        return new HashSet<>(prefs.getStringSet(KEY_MASTERED_PHRASES, new HashSet<String>()));
    }

    public int getMasteredPhraseCount() {
        return getMasteredPhrases().size();
    }

    public boolean isPhraseMastered(int phraseId) {
        return getMasteredPhrases().contains(String.valueOf(phraseId));
    }

    public void markPhraseMastered(int phraseId) {
        Set<String> set = getMasteredPhrases();
        if (set.add(String.valueOf(phraseId))) {
            prefs.edit().putStringSet(KEY_MASTERED_PHRASES, set).apply();
            addXp(5);
        }
    }

    // ------------------------------------------------------------- activity log

    public int getQuizCount() {
        return prefs.getInt(KEY_QUIZ_COUNT, 0);
    }

    public int getLookupCount() {
        return prefs.getInt(KEY_LOOKUPS, 0);
    }

    public int getWordOfDayCount() {
        return prefs.getInt(KEY_WOTD, 0);
    }

    public int getSentenceCount() {
        return prefs.getInt(KEY_SENTENCES, 0);
    }

    public int getFlipCount() {
        return prefs.getInt(KEY_FLIPS, 0);
    }

    public int getGameCount() {
        return prefs.getInt(KEY_GAMES, 0);
    }

    public int getTranslationCount() {
        return prefs.getInt(KEY_TRANSLATIONS, 0);
    }

    public int getTensePracticeCount() {
        return prefs.getInt(KEY_TENSE_PRACTICES, 0);
    }

    public void recordLookup() {
        bump(KEY_LOOKUPS);
    }

    public void recordWordOfDay() {
        bump(KEY_WOTD);
    }

    public void recordFlip() {
        bump(KEY_FLIPS);
    }

    public void recordSentence() {
        bump(KEY_SENTENCES);
        addXp(4);
    }

    public void recordTranslation() {
        bump(KEY_TRANSLATIONS);
        addXp(2);
    }

    private void bump(String key) {
        prefs.edit().putInt(key, prefs.getInt(key, 0) + 1).apply();
    }

    // ------------------------------------------------------------------ scores

    public void recordQuiz(String tag, int score, int total) {
        int best = prefs.getInt(PREFIX_BEST + tag, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_QUIZ_COUNT, getQuizCount() + 1);
        if (score > best) editor.putInt(PREFIX_BEST + tag, score);
        editor.apply();
        addXp(score * 6);
    }

    public int getBestScore(String tag) {
        return prefs.getInt(PREFIX_BEST + tag, 0);
    }

    public void recordTensePractice(int tenseIndex, int score, int total) {
        int best = prefs.getInt(PREFIX_TENSE_BEST + tenseIndex, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_TENSE_PRACTICES, getTensePracticeCount() + 1);
        if (score > best) editor.putInt(PREFIX_TENSE_BEST + tenseIndex, score);
        editor.apply();
        addXp(score * 6);
    }

    public int getTenseBest(int tenseIndex) {
        return prefs.getInt(PREFIX_TENSE_BEST + tenseIndex, 0);
    }

    public void recordGame(String gameKey, int score) {
        int best = prefs.getInt(PREFIX_GAME_BEST + gameKey, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_GAMES, getGameCount() + 1);
        if (score > best) editor.putInt(PREFIX_GAME_BEST + gameKey, score);
        editor.apply();
        addXp(score * 3);
    }

    public int getGameBest(String gameKey) {
        return prefs.getInt(PREFIX_GAME_BEST + gameKey, 0);
    }

    // -------------------------------------------------------------- reminders

    public boolean isReminderOn() {
        return prefs.getBoolean(KEY_REMINDER_ON, true);
    }

    public void setReminderOn(boolean on) {
        prefs.edit().putBoolean(KEY_REMINDER_ON, on).apply();
    }

    public int getReminderHour() {
        return prefs.getInt(KEY_REMINDER_HOUR, 19);
    }

    public int getReminderMinute() {
        return prefs.getInt(KEY_REMINDER_MINUTE, 30);
    }

    public void setReminderTime(int hour, int minute) {
        prefs.edit().putInt(KEY_REMINDER_HOUR, hour).putInt(KEY_REMINDER_MINUTE, minute).apply();
    }

    // ----------------------------------------------------------- achievements

    private Set<String> unlockedKeys() {
        return new HashSet<>(prefs.getStringSet(KEY_UNLOCKED, new HashSet<String>()));
    }

    private void unlock(String key) {
        Set<String> set = unlockedKeys();
        if (set.add(key)) {
            prefs.edit().putStringSet(KEY_UNLOCKED, set).apply();
        }
    }

    /**
     * Builds the achievement list from live counters, unlocking any that are newly
     * complete. Returns the titles unlocked during this call so the caller can
     * celebrate them.
     */
    public List<Achievement> buildAchievements(List<String> newlyUnlocked) {
        List<Achievement> list = new ArrayList<>();
        Set<String> already = unlockedKeys();

        add(list, already, newlyUnlocked, "first_steps", "First Steps", "Learn your first 5 words", "🌱", 5, getLearnedCount());
        add(list, already, newlyUnlocked, "word_50", "Word Collector", "Learn 50 words", "📚", 50, getLearnedCount());
        add(list, already, newlyUnlocked, "word_200", "Vocabulary Builder", "Learn 200 words", "🏅", 200, getLearnedCount());
        add(list, already, newlyUnlocked, "word_500", "Word Master", "Learn 500 words", "👑", 500, getLearnedCount());
        add(list, already, newlyUnlocked, "streak_3", "Warming Up", "A 3 day streak", "🔥", 3, getStreak());
        add(list, already, newlyUnlocked, "streak_7", "One Week Strong", "A 7 day streak", "⚡", 7, getStreak());
        add(list, already, newlyUnlocked, "streak_30", "Unstoppable", "A 30 day streak", "🚀", 30, getStreak());
        add(list, already, newlyUnlocked, "quiz_10", "Quiz Starter", "Finish 10 exercises", "✏️", 10, getQuizCount());
        add(list, already, newlyUnlocked, "quiz_50", "Quiz Champion", "Finish 50 exercises", "🎯", 50, getQuizCount());
        add(list, already, newlyUnlocked, "tense_12", "Tense Explorer", "Practise tenses 12 times", "⏳", 12, getTensePracticeCount());
        add(list, already, newlyUnlocked, "sentence_25", "Sentence Builder", "Translate 25 sentences", "🧩", 25, getSentenceCount());
        add(list, already, newlyUnlocked, "phrase_25", "Idiom Hunter", "Master 25 idioms or phrasal verbs", "🦊", 25, getMasteredPhraseCount());
        add(list, already, newlyUnlocked, "game_20", "Game On", "Play 20 game rounds", "🎮", 20, getGameCount());
        add(list, already, newlyUnlocked, "flip_100", "Flashcard Fan", "Flip 100 flashcards", "🔄", 100, getFlipCount());
        add(list, already, newlyUnlocked, "lookup_30", "Curious Mind", "Look up 30 words", "🔎", 30, getLookupCount());
        add(list, already, newlyUnlocked, "wotd_15", "Daily Habit", "Open the word of the day 15 times", "☀️", 15, getWordOfDayCount());
        add(list, already, newlyUnlocked, "translate_20", "Translator", "Translate 20 of your own sentences", "🌐", 20, getTranslationCount());
        add(list, already, newlyUnlocked, "xp_1000", "Rising Star", "Earn 1000 XP", "⭐", 1000, getXp());
        add(list, already, newlyUnlocked, "xp_5000", "Legend", "Earn 5000 XP", "🏆", 5000, getXp());

        return list;
    }

    private void add(List<Achievement> list, Set<String> already, List<String> newlyUnlocked,
                     String key, String title, String detail, String emoji, int target, int progress) {
        boolean done = progress >= target;
        if (done && !already.contains(key)) {
            unlock(key);
            if (newlyUnlocked != null) newlyUnlocked.add(title);
        }
        list.add(new Achievement(key, title, detail, emoji, target, Math.min(progress, target), done));
    }

    public int getUnlockedAchievementCount() {
        return unlockedKeys().size();
    }

    // ----------------------------------------------------------------- reset

    public void resetEverything() {
        String name = getUserName();
        int level = getLevel();
        boolean urdu = isUrduEnabled();
        String key = getGeminiKey();
        prefs.edit().clear().apply();
        prefs.edit()
                .putString(KEY_NAME, name)
                .putInt(KEY_LEVEL, level)
                .putBoolean(KEY_URDU, urdu)
                .putString(KEY_GEMINI, key)
                .putBoolean(KEY_ONBOARDED, true)
                .putBoolean(KEY_TOUR_DONE, true)
                .apply();
    }

    /** Convenience for screens that show "3 of 12" style summaries. */
    public static List<String> levelNames() {
        return new ArrayList<>(Arrays.asList("Beginner", "Intermediate", "Advanced", "Expert"));
    }
}

package com.saim.englishlearning.game;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.saim.englishlearning.R;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.WordBank;
import com.saim.englishlearning.model.Word;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.ConfettiView;
import com.saim.englishlearning.util.FlowLayout;
import com.saim.englishlearning.util.Speaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Listen to the word, then spell it one letter at a time by tapping letter tiles.
 * Each tap must be the next correct letter, so the learner is genuinely spelling
 * rather than recognising a finished word.
 */
public class SpellingBeeActivity extends AppCompatActivity {

    public static final String KEY = "spelling_bee";
    private static final int ROUND_LENGTH = 10;
    private static final int POINTS_PER_WORD = 5;
    private static final int EXTRA_LETTERS = 5;

    private static final int GREEN = Color.parseColor("#2E9E6B");
    private static final int RED = Color.parseColor("#D6455D");

    private final Random random = new Random();

    private ProgressManager progress;
    private Speaker speaker;
    private ConfettiView confetti;

    private final List<Word> words = new ArrayList<>();
    private final List<TextView> slots = new ArrayList<>();

    private int index;
    private int score;
    /** How many letters of the current word are already locked in. */
    private int solved;
    private int mistakes;
    private boolean locked;

    private FlowLayout slotLayout;
    private FlowLayout keyLayout;
    private TextView meaning;
    private TextView feedback;
    private TextView counter;
    private TextView scoreLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spelling_bee);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);
        confetti = findViewById(R.id.confetti);

        slotLayout = findViewById(R.id.layoutSpellingSlots);
        keyLayout = findViewById(R.id.layoutSpellingKeys);
        meaning = findViewById(R.id.textSpellingMeaning);
        feedback = findViewById(R.id.textSpellingFeedback);
        counter = findViewById(R.id.textSpellingCounter);
        scoreLabel = findViewById(R.id.textSpellingScore);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.buttonHear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                if (hasWord()) speaker.say(current().word);
            }
        });

        findViewById(R.id.buttonHearSlow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                if (hasWord()) speaker.saySlowly(current().word);
            }
        });

        findViewById(R.id.buttonHint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                revealNextLetter();
            }
        });

        List<Word> pool = WordBank.byLevel(progress.getLevel());
        if (pool.size() < ROUND_LENGTH) pool = new ArrayList<>(WordBank.all());
        Collections.shuffle(pool);
        words.addAll(pool.subList(0, Math.min(ROUND_LENGTH, pool.size())));

        show();
    }

    private boolean hasWord() {
        return !words.isEmpty() && index < words.size();
    }

    private Word current() {
        return words.get(Math.min(index, words.size() - 1));
    }

    // ------------------------------------------------------------------ round

    private void show() {
        if (words.isEmpty()) {
            meaning.setText(R.string.no_words);
            return;
        }
        if (index >= words.size()) {
            finishRound();
            return;
        }

        solved = 0;
        mistakes = 0;
        locked = false;
        feedback.setVisibility(View.INVISIBLE);

        Word word = current();
        meaning.setText(word.meaning);
        counter.setText(getString(R.string.spelling_counter, index + 1, words.size()));
        scoreLabel.setText(getString(R.string.spelling_score, score));

        buildSlots(word.word);
        buildKeys(word.word);

        speaker.say(word.word);
        Anim.enter(meaning, 0);
    }

    /** One blank tile per letter of the word. */
    private void buildSlots(String word) {
        slotLayout.removeAllViews();
        slots.clear();
        int size = (int) (34 * getResources().getDisplayMetrics().density);
        int margin = (int) (3 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < word.length(); i++) {
            TextView slot = new TextView(this);
            ViewGroup.MarginLayoutParams params =
                    new ViewGroup.MarginLayoutParams(size, (int) (size * 1.25f));
            params.setMargins(margin, margin, margin, margin);
            slot.setLayoutParams(params);
            slot.setGravity(Gravity.CENTER);
            slot.setTextSize(19f);
            slot.setText("");
            slot.setBackgroundResource(R.drawable.bg_letter_slot);
            slot.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            slotLayout.addView(slot);
            slots.add(slot);
            Anim.enter(slot, i * 25L);
        }
    }

    /**
     * The keyboard holds every distinct letter of the word plus a few extras, so
     * the tiles are a real choice rather than a giveaway. Tiles are never used up,
     * which keeps repeated letters such as the two t's in "letter" simple.
     */
    private void buildKeys(String word) {
        keyLayout.removeAllViews();

        Set<Character> letters = new LinkedHashSet<>();
        for (char c : word.toLowerCase(Locale.ROOT).toCharArray()) {
            letters.add(c);
        }
        int guard = 0;
        while (letters.size() < word.length() + EXTRA_LETTERS && letters.size() < 26 && guard < 200) {
            guard++;
            letters.add((char) ('a' + random.nextInt(26)));
        }

        List<Character> keys = new ArrayList<>(letters);
        Collections.shuffle(keys, random);

        int margin = (int) (4 * getResources().getDisplayMetrics().density);
        int size = (int) (46 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < keys.size(); i++) {
            final char letter = keys.get(i);
            MaterialButton key = new MaterialButton(this, null,
                    com.google.android.material.R.attr.materialButtonOutlinedStyle);
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(size, size);
            params.setMargins(margin, margin, margin, margin);
            key.setLayoutParams(params);
            key.setText(String.valueOf(letter).toUpperCase(Locale.ROOT));
            key.setAllCaps(false);
            key.setTextSize(17f);
            key.setInsetTop(0);
            key.setInsetBottom(0);
            key.setPadding(0, 0, 0, 0);
            resetKey(key);
            key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLetterTapped(letter, (MaterialButton) v);
                }
            });
            keyLayout.addView(key);
            Anim.enter(key, i * 20L);
        }
    }

    private void resetKey(MaterialButton key) {
        key.setStrokeColor(android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.outline)));
        key.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
    }

    // ----------------------------------------------------------------- guesses

    private void onLetterTapped(char letter, final MaterialButton key) {
        if (locked || !hasWord() || solved >= slots.size()) return;

        char expected = Character.toLowerCase(current().word.charAt(solved));
        if (Character.toLowerCase(letter) == expected) {
            lockLetter(expected);
            Anim.pop(key);
        } else {
            mistakes++;
            Anim.shake(key);
            key.setStrokeColor(android.content.res.ColorStateList.valueOf(RED));
            key.setTextColor(RED);
            feedback.setVisibility(View.VISIBLE);
            feedback.setTextColor(RED);
            feedback.setText(R.string.spelling_letter_wrong);
            key.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetKey(key);
                }
            }, 500);
        }
    }

    /** Fills the next slot and finishes the word when the last letter lands. */
    private void lockLetter(char letter) {
        TextView slot = slots.get(solved);
        slot.setText(String.valueOf(letter).toUpperCase(Locale.ROOT));
        slot.setBackgroundResource(R.drawable.bg_letter_slot_filled);
        slot.setTextColor(GREEN);
        Anim.pop(slot);
        solved++;

        if (solved >= slots.size()) {
            completeWord();
        }
    }

    private void revealNextLetter() {
        if (locked || !hasWord() || solved >= slots.size()) return;
        mistakes++;
        feedback.setVisibility(View.VISIBLE);
        feedback.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        feedback.setText(R.string.spelling_hint_used);
        lockLetter(Character.toLowerCase(current().word.charAt(solved)));
    }

    private void completeWord() {
        locked = true;
        int gained = Math.max(1, POINTS_PER_WORD - mistakes);
        score += gained;
        progress.markLearned(current().id);

        feedback.setVisibility(View.VISIBLE);
        feedback.setTextColor(GREEN);
        feedback.setText(getString(R.string.spelling_right, gained));
        Anim.pop(feedback);
        scoreLabel.setText(getString(R.string.spelling_score, score));
        speaker.say(current().word);
        if (progress.areAnimationsEnabled()) confetti.burst(30);

        feedback.postDelayed(new Runnable() {
            @Override
            public void run() {
                index++;
                show();
            }
        }, 1200);
    }

    // ------------------------------------------------------------------ result

    private void finishRound() {
        progress.recordGame(KEY, score);
        findViewById(R.id.layoutSpellingGame).setVisibility(View.GONE);
        findViewById(R.id.layoutSpellingResult).setVisibility(View.VISIBLE);
        Anim.countUp((TextView) findViewById(R.id.textSpellingResultScore), 0, score, "");
        if (progress.areAnimationsEnabled()) confetti.burst();
        findViewById(R.id.buttonSpellingAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        super.onDestroy();
    }
}

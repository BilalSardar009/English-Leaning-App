package com.saim.englishlearning.game;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.saim.englishlearning.R;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.WordBank;
import com.saim.englishlearning.model.Word;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.ConfettiView;
import com.saim.englishlearning.util.Speaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Listen to the word, then spell it. Hints reveal one letter at a time. */
public class SpellingBeeActivity extends AppCompatActivity {

    public static final String KEY = "spelling_bee";
    private static final int ROUND_LENGTH = 10;

    private static final int GREEN = Color.parseColor("#2E9E6B");
    private static final int RED = Color.parseColor("#D6455D");

    private ProgressManager progress;
    private Speaker speaker;
    private ConfettiView confetti;

    private final List<Word> words = new ArrayList<>();
    private int index;
    private int score;
    private int revealed;

    private EditText answer;
    private TextView meaning;
    private TextView pattern;
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

        answer = findViewById(R.id.inputSpelling);
        meaning = findViewById(R.id.textSpellingMeaning);
        pattern = findViewById(R.id.textSpellingPattern);
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
                if (!words.isEmpty()) speaker.say(current().word);
            }
        });

        findViewById(R.id.buttonHearSlow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                if (!words.isEmpty()) speaker.saySlowly(current().word);
            }
        });

        findViewById(R.id.buttonHint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                revealed++;
                showPattern();
            }
        });

        findViewById(R.id.buttonCheckSpelling).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                check();
            }
        });

        List<Word> pool = WordBank.byLevel(progress.getLevel());
        if (pool.size() < ROUND_LENGTH) pool = new ArrayList<>(WordBank.all());
        Collections.shuffle(pool);
        words.addAll(pool.subList(0, Math.min(ROUND_LENGTH, pool.size())));

        show();
    }

    private Word current() {
        return words.get(Math.min(index, words.size() - 1));
    }

    private void show() {
        if (words.isEmpty()) {
            meaning.setText(R.string.no_words);
            return;
        }
        if (index >= words.size()) {
            finishRound();
            return;
        }
        revealed = 1;
        answer.setText("");
        feedback.setVisibility(View.INVISIBLE);
        Word word = current();
        meaning.setText(word.meaning);
        counter.setText(getString(R.string.spelling_counter, index + 1, words.size()));
        scoreLabel.setText(getString(R.string.spelling_score, score));
        showPattern();
        speaker.say(word.word);
        Anim.enter(meaning, 0);
    }

    /** Shows the first {@code revealed} letters and dots for the rest. */
    private void showPattern() {
        if (words.isEmpty()) return;
        String word = current().word;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c == ' ') {
                builder.append("   ");
            } else if (i < revealed) {
                builder.append(c).append(' ');
            } else {
                builder.append("_ ");
            }
        }
        pattern.setText(builder.toString().trim());
        Anim.pop(pattern);
    }

    private void check() {
        if (words.isEmpty() || index >= words.size()) return;
        String typed = answer.getText().toString().trim().toLowerCase(Locale.ROOT);
        String correct = current().word.trim().toLowerCase(Locale.ROOT);
        feedback.setVisibility(View.VISIBLE);

        if (typed.equals(correct)) {
            int gained = Math.max(1, 4 - (revealed - 1));
            score += gained;
            feedback.setTextColor(GREEN);
            feedback.setText(getString(R.string.spelling_right, gained));
            progress.markLearned(current().id);
            Anim.pop(feedback);
            if (progress.areAnimationsEnabled()) confetti.burst(30);
            answer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    index++;
                    show();
                }
            }, 850);
        } else {
            feedback.setTextColor(RED);
            feedback.setText(getString(R.string.spelling_wrong, current().word));
            Anim.shake(answer);
            answer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    index++;
                    show();
                }
            }, 1400);
        }
        scoreLabel.setText(getString(R.string.spelling_score, score));
    }

    private void finishRound() {
        progress.recordGame(KEY, score);
        findViewById(R.id.layoutSpellingGame).setVisibility(View.GONE);
        View result = findViewById(R.id.layoutSpellingResult);
        result.setVisibility(View.VISIBLE);
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

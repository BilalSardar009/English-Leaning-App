package com.saim.englishlearning.game;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.saim.englishlearning.R;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.SentenceBank;
import com.saim.englishlearning.model.SentencePair;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.ConfettiView;
import com.saim.englishlearning.util.FlowLayout;
import com.saim.englishlearning.util.Speaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The English sentence is broken into word chips and shuffled. Tap them in the
 * right order to rebuild it.
 */
public class ScrambleActivity extends AppCompatActivity {

    public static final String KEY = "scramble";
    private static final int ROUND_LENGTH = 8;

    private static final int GREEN = Color.parseColor("#2E9E6B");
    private static final int RED = Color.parseColor("#D6455D");

    private ProgressManager progress;
    private Speaker speaker;
    private ConfettiView confetti;

    private final List<SentencePair> rounds = new ArrayList<>();
    /** The correct word order for the current sentence. */
    private final List<String> target = new ArrayList<>();
    /** The same words shuffled once per sentence, so chips keep their places. */
    private final List<String> pool = new ArrayList<>();
    /** Indices into {@link #pool}, in the order the learner tapped them. */
    private final List<Integer> built = new ArrayList<>();

    private int index;
    private int score;

    private FlowLayout poolLayout;
    private FlowLayout answerLayout;
    private TextView urduPrompt;
    private TextView feedback;
    private TextView counter;
    private TextView scoreLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scramble);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);
        confetti = findViewById(R.id.confetti);

        poolLayout = findViewById(R.id.layoutChipPool);
        answerLayout = findViewById(R.id.layoutAnswer);
        urduPrompt = findViewById(R.id.textScrambleUrdu);
        feedback = findViewById(R.id.textScrambleFeedback);
        counter = findViewById(R.id.textScrambleCounter);
        scoreLabel = findViewById(R.id.textScrambleScore);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.buttonScrambleCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                check();
            }
        });

        findViewById(R.id.buttonScrambleClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                built.clear();
                render();
            }
        });

        findViewById(R.id.buttonScrambleSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                index++;
                show();
            }
        });

        List<SentencePair> candidates = new ArrayList<>(SentenceBank.upToLevel(progress.getLevel()));
        Collections.shuffle(candidates);
        for (SentencePair pair : candidates) {
            if (rounds.size() >= ROUND_LENGTH) break;
            int words = pair.simple.trim().split("\\s+").length;
            if (words >= 3 && words <= 9) rounds.add(pair);
        }
        if (rounds.isEmpty() && !candidates.isEmpty()) {
            rounds.addAll(candidates.subList(0, Math.min(ROUND_LENGTH, candidates.size())));
        }

        show();
    }

    private void show() {
        if (rounds.isEmpty()) {
            urduPrompt.setText(R.string.no_sentences);
            return;
        }
        if (index >= rounds.size()) {
            finishRound();
            return;
        }

        SentencePair pair = rounds.get(index);
        target.clear();
        built.clear();
        target.addAll(Arrays.asList(pair.simple.trim().split("\\s+")));

        // Shuffle once here, not in render, so the chips stay put between taps.
        pool.clear();
        pool.addAll(target);
        Collections.shuffle(pool);
        if (pool.size() > 1 && pool.equals(target)) {
            Collections.reverse(pool);
        }

        urduPrompt.setText(pair.urdu);
        counter.setText(getString(R.string.scramble_counter, index + 1, rounds.size()));
        scoreLabel.setText(getString(R.string.scramble_score, score));
        feedback.setVisibility(View.INVISIBLE);
        render();
        Anim.enter(urduPrompt, 0);
    }

    private void render() {
        poolLayout.removeAllViews();
        for (int i = 0; i < pool.size(); i++) {
            if (built.contains(i)) continue;
            final int poolIndex = i;
            MaterialButton button = chip(pool.get(i), false);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Anim.pop(v);
                    built.add(poolIndex);
                    render();
                }
            });
            poolLayout.addView(button);
            Anim.enter(button, poolLayout.getChildCount() * 25L);
        }

        answerLayout.removeAllViews();
        for (int i = 0; i < built.size(); i++) {
            final int slot = i;
            MaterialButton button = chip(pool.get(built.get(i)), true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    built.remove(slot);
                    render();
                }
            });
            answerLayout.addView(button);
        }
    }

    private MaterialButton chip(String text, boolean chosen) {
        MaterialButton button = new MaterialButton(this, null,
                com.google.android.material.R.attr.materialButtonOutlinedStyle);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = (int) (4 * getResources().getDisplayMetrics().density);
        params.setMargins(margin, margin, margin, margin);
        button.setLayoutParams(params);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(15f);
        button.setInsetTop(0);
        button.setInsetBottom(0);
        int colour = ContextCompat.getColor(this,
                chosen ? R.color.brand_primary : R.color.outline);
        button.setStrokeColor(android.content.res.ColorStateList.valueOf(colour));
        button.setTextColor(ContextCompat.getColor(this,
                chosen ? R.color.brand_primary : R.color.text_primary));
        return button;
    }

    private void check() {
        if (rounds.isEmpty() || index >= rounds.size()) return;

        List<String> made = new ArrayList<>();
        for (Integer poolIndex : built) {
            made.add(pool.get(poolIndex));
        }
        feedback.setVisibility(View.VISIBLE);

        if (join(made).equalsIgnoreCase(join(target))) {
            score += 3;
            feedback.setTextColor(GREEN);
            feedback.setText(R.string.scramble_right);
            speaker.say(rounds.get(index).simple);
            progress.recordSentence();
            if (progress.areAnimationsEnabled()) confetti.burst(35);
            feedback.postDelayed(new Runnable() {
                @Override
                public void run() {
                    index++;
                    show();
                }
            }, 1100);
        } else {
            feedback.setTextColor(RED);
            feedback.setText(R.string.scramble_wrong);
            Anim.shake(answerLayout);
        }
        scoreLabel.setText(getString(R.string.scramble_score, score));
    }

    private static String join(List<String> parts) {
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (builder.length() > 0) builder.append(' ');
            builder.append(part);
        }
        return builder.toString();
    }

    private void finishRound() {
        progress.recordGame(KEY, score);
        findViewById(R.id.layoutScrambleGame).setVisibility(View.GONE);
        View result = findViewById(R.id.layoutScrambleResult);
        result.setVisibility(View.VISIBLE);
        Anim.countUp((TextView) findViewById(R.id.textScrambleResultScore), 0, score, "");
        if (progress.areAnimationsEnabled()) confetti.burst();
        findViewById(R.id.buttonScrambleAgain).setOnClickListener(new View.OnClickListener() {
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

package com.saim.englishlearning.game;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
import com.saim.englishlearning.util.Speaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Three words come from one level, one comes from a different level. Spot the
 * one that does not belong.
 */
public class OddOneOutActivity extends AppCompatActivity {

    public static final String KEY = "odd_one_out";
    private static final int ROUND_LENGTH = 10;

    private static final int GREEN = Color.parseColor("#2E9E6B");
    private static final int RED = Color.parseColor("#D6455D");

    private ProgressManager progress;
    private Speaker speaker;
    private ConfettiView confetti;

    private final List<MaterialButton> buttons = new ArrayList<>();
    private final List<Word> current = new ArrayList<>();
    private int oddIndex;
    private int index;
    private int score;
    private boolean answered;

    private TextView counter;
    private TextView scoreLabel;
    private TextView feedback;
    private TextView hint;
    private LinearLayout options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odd_one_out);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);
        confetti = findViewById(R.id.confetti);

        counter = findViewById(R.id.textOddCounter);
        scoreLabel = findViewById(R.id.textOddScore);
        feedback = findViewById(R.id.textOddFeedback);
        hint = findViewById(R.id.textOddHint);
        options = findViewById(R.id.layoutOddOptions);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buildButtons();
        nextRound();
    }

    private void buildButtons() {
        options.removeAllViews();
        buttons.clear();
        int margin = (int) (7 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < 4; i++) {
            MaterialButton button = new MaterialButton(this, null,
                    com.google.android.material.R.attr.materialButtonOutlinedStyle);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, margin, 0, margin);
            button.setLayoutParams(params);
            button.setAllCaps(false);
            button.setTextSize(16f);
            button.setMinHeight((int) (56 * getResources().getDisplayMetrics().density));
            final int position = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choose(position);
                }
            });
            buttons.add(button);
            options.addView(button);
        }
    }

    private void nextRound() {
        if (index >= ROUND_LENGTH) {
            finishRound();
            return;
        }
        answered = false;
        feedback.setVisibility(View.INVISIBLE);

        int homeLevel = progress.getLevel();
        int otherLevel = homeLevel == 1 ? 4 : homeLevel - 1;

        List<Word> home = new ArrayList<>(WordBank.byLevel(homeLevel));
        List<Word> other = new ArrayList<>(WordBank.byLevel(otherLevel));
        if (home.size() < 3 || other.isEmpty()) {
            home = new ArrayList<>(WordBank.all());
            other = new ArrayList<>(WordBank.all());
        }
        if (home.size() < 3 || other.isEmpty()) {
            hint.setText(R.string.no_words);
            return;
        }

        Collections.shuffle(home);
        Collections.shuffle(other);

        current.clear();
        for (int i = 0; i < 3 && i < home.size(); i++) {
            current.add(home.get(i));
        }
        Word odd = other.get(0);
        current.add(odd);
        Collections.shuffle(current);
        oddIndex = current.indexOf(odd);

        hint.setText(getString(R.string.odd_hint, Word.levelName(homeLevel)));
        counter.setText(getString(R.string.odd_counter, index + 1, ROUND_LENGTH));
        scoreLabel.setText(getString(R.string.odd_score, score));

        for (int i = 0; i < buttons.size(); i++) {
            MaterialButton button = buttons.get(i);
            button.setText(current.get(i).word);
            button.setEnabled(true);
            reset(button);
            Anim.enter(button, i * 55L);
        }
    }

    private void reset(MaterialButton button) {
        button.setStrokeColor(android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.outline)));
        button.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
    }

    private void choose(int position) {
        if (answered) return;
        answered = true;
        for (MaterialButton button : buttons) {
            button.setEnabled(false);
        }

        MaterialButton right = buttons.get(oddIndex);
        right.setStrokeColor(android.content.res.ColorStateList.valueOf(GREEN));
        right.setTextColor(GREEN);
        feedback.setVisibility(View.VISIBLE);

        Word odd = current.get(oddIndex);
        if (position == oddIndex) {
            score += 2;
            Anim.pop(right);
            feedback.setTextColor(GREEN);
            feedback.setText(getString(R.string.odd_right,
                    odd.word, Word.levelName(odd.level)));
            speaker.say(odd.word);
            if (progress.areAnimationsEnabled()) confetti.burst(25);
        } else {
            MaterialButton chosen = buttons.get(position);
            chosen.setStrokeColor(android.content.res.ColorStateList.valueOf(RED));
            chosen.setTextColor(RED);
            Anim.shake(chosen);
            feedback.setTextColor(RED);
            feedback.setText(getString(R.string.odd_wrong,
                    odd.word, Word.levelName(odd.level)));
        }
        scoreLabel.setText(getString(R.string.odd_score, score));

        feedback.postDelayed(new Runnable() {
            @Override
            public void run() {
                index++;
                nextRound();
            }
        }, 1500);
    }

    private void finishRound() {
        progress.recordGame(KEY, score);
        findViewById(R.id.layoutOddGame).setVisibility(View.GONE);
        findViewById(R.id.layoutOddResult).setVisibility(View.VISIBLE);
        Anim.countUp((TextView) findViewById(R.id.textOddResultScore), 0, score, "");
        if (progress.areAnimationsEnabled()) confetti.burst();
        findViewById(R.id.buttonOddAgain).setOnClickListener(new View.OnClickListener() {
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

package com.saim.englishlearning.game;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.saim.englishlearning.R;
import com.saim.englishlearning.data.ExerciseGenerator;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.model.QuizQuestion;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.ConfettiView;

import java.util.ArrayList;
import java.util.List;

/**
 * Sixty seconds of rapid fire questions. Answering correctly in a row builds a
 * combo multiplier, so speed and accuracy both matter.
 */
public class SpeedRoundActivity extends AppCompatActivity {

    public static final String KEY = "speed_round";
    private static final long ROUND_MILLIS = 60_000L;

    private static final int GREEN = Color.parseColor("#2E9E6B");
    private static final int RED = Color.parseColor("#D6455D");

    private ProgressManager progress;
    private ConfettiView confetti;
    private CountDownTimer timer;

    private final List<QuizQuestion> questions = new ArrayList<>();
    private final List<MaterialButton> buttons = new ArrayList<>();

    private int index;
    private int score;
    private int combo;
    private int bestCombo;
    private boolean running = true;

    private TextView prompt;
    private TextView timeLabel;
    private TextView scoreLabel;
    private TextView comboLabel;
    private ProgressBar timeBar;
    private LinearLayout options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_round);
        progress = new ProgressManager(this);
        confetti = findViewById(R.id.confetti);

        prompt = findViewById(R.id.textSpeedPrompt);
        timeLabel = findViewById(R.id.textSpeedTime);
        scoreLabel = findViewById(R.id.textSpeedScore);
        comboLabel = findViewById(R.id.textSpeedCombo);
        timeBar = findViewById(R.id.progressSpeedTime);
        options = findViewById(R.id.layoutSpeedOptions);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        questions.addAll(ExerciseGenerator.generate(progress.getLevel(),
                ExerciseGenerator.TYPE_MIXED, 60));

        buildButtons();
        timeBar.setMax((int) (ROUND_MILLIS / 1000));
        showQuestion();
        startTimer();
    }

    private void buildButtons() {
        options.removeAllViews();
        buttons.clear();
        int margin = (int) (6 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < 4; i++) {
            MaterialButton button = new MaterialButton(this, null,
                    com.google.android.material.R.attr.materialButtonOutlinedStyle);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, margin, 0, margin);
            button.setLayoutParams(params);
            button.setAllCaps(false);
            button.setTextSize(15f);
            button.setMinHeight((int) (52 * getResources().getDisplayMetrics().density));
            final int position = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answer(position);
                }
            });
            buttons.add(button);
            options.addView(button);
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(ROUND_MILLIS, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) Math.ceil(millisUntilFinished / 1000.0);
                timeLabel.setText(getString(R.string.speed_time, seconds));
                timeBar.setProgress(seconds);
                if (seconds <= 10) {
                    timeLabel.setTextColor(RED);
                }
            }

            @Override
            public void onFinish() {
                running = false;
                finishRound();
            }
        };
        timer.start();
    }

    private void showQuestion() {
        if (!running) return;
        if (index >= questions.size()) {
            questions.addAll(ExerciseGenerator.generate(progress.getLevel(),
                    ExerciseGenerator.TYPE_MIXED, 30));
            if (index >= questions.size()) {
                finishRound();
                return;
            }
        }
        QuizQuestion question = questions.get(index);
        prompt.setText(question.prompt);
        for (int i = 0; i < buttons.size(); i++) {
            MaterialButton button = buttons.get(i);
            if (i < question.options.size()) {
                button.setVisibility(View.VISIBLE);
                button.setText(question.options.get(i));
                button.setEnabled(true);
                reset(button);
            } else {
                button.setVisibility(View.GONE);
            }
        }
        Anim.enter(prompt, 0);
    }

    private void reset(MaterialButton button) {
        button.setStrokeColor(android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.outline)));
        button.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
    }

    private void answer(int position) {
        if (!running || index >= questions.size()) return;
        QuizQuestion question = questions.get(index);
        MaterialButton chosen = buttons.get(position);

        if (position == question.correctIndex) {
            combo++;
            bestCombo = Math.max(bestCombo, combo);
            score += 1 + Math.min(4, combo / 3);
            chosen.setStrokeColor(android.content.res.ColorStateList.valueOf(GREEN));
            chosen.setTextColor(GREEN);
            Anim.pop(chosen);
            if (combo > 0 && combo % 5 == 0 && progress.areAnimationsEnabled()) {
                confetti.burst(25);
            }
        } else {
            combo = 0;
            chosen.setStrokeColor(android.content.res.ColorStateList.valueOf(RED));
            chosen.setTextColor(RED);
            Anim.shake(chosen);
        }

        scoreLabel.setText(getString(R.string.speed_score, score));
        comboLabel.setText(combo > 1
                ? getString(R.string.speed_combo, combo) : getString(R.string.speed_no_combo));
        if (combo > 1) Anim.pop(comboLabel);

        index++;
        prompt.postDelayed(new Runnable() {
            @Override
            public void run() {
                showQuestion();
            }
        }, 260);
    }

    private void finishRound() {
        if (timer != null) timer.cancel();
        progress.recordGame(KEY, score);
        findViewById(R.id.layoutSpeedGame).setVisibility(View.GONE);
        findViewById(R.id.layoutSpeedResult).setVisibility(View.VISIBLE);
        Anim.countUp((TextView) findViewById(R.id.textSpeedResultScore), 0, score, "");
        ((TextView) findViewById(R.id.textSpeedResultCombo))
                .setText(getString(R.string.speed_best_combo, bestCombo));
        if (progress.areAnimationsEnabled()) confetti.burst();
        findViewById(R.id.buttonSpeedAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    @Override
    protected void onDestroy() {
        running = false;
        if (timer != null) timer.cancel();
        super.onDestroy();
    }
}

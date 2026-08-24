package com.saim.englishlearning;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.saim.englishlearning.data.ExerciseGenerator;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.TenseBank;
import com.saim.englishlearning.model.QuizQuestion;
import com.saim.englishlearning.model.Tense;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.ConfettiView;
import com.saim.englishlearning.util.Speaker;

import java.util.ArrayList;
import java.util.List;

/**
 * The shared multiple choice runner. It handles both the word exercises and the
 * tense practice, deciding which from the extras it is given.
 */
public class ExerciseActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "exercise_type";
    public static final String EXTRA_COUNT = "exercise_count";
    public static final String EXTRA_TENSE_INDEX = "tense_index";

    private static final int GREEN = Color.parseColor("#2E9E6B");
    private static final int RED = Color.parseColor("#D6455D");

    private ProgressManager progress;
    private Speaker speaker;
    private ConfettiView confetti;

    private final List<QuizQuestion> questions = new ArrayList<>();
    private final List<MaterialButton> optionButtons = new ArrayList<>();

    private int index;
    private int score;
    private int tenseIndex = -1;
    private int type = ExerciseGenerator.TYPE_MIXED;
    private boolean answered;

    private TextView counter;
    private TextView scoreLabel;
    private TextView prompt;
    private TextView feedback;
    private ProgressBar bar;
    private LinearLayout optionsLayout;
    private MaterialButton nextButton;
    private View quizLayout;
    private View resultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);

        counter = findViewById(R.id.textCounter);
        scoreLabel = findViewById(R.id.textScore);
        prompt = findViewById(R.id.textPrompt);
        feedback = findViewById(R.id.textFeedback);
        bar = findViewById(R.id.progressQuiz);
        optionsLayout = findViewById(R.id.layoutOptions);
        nextButton = findViewById(R.id.buttonNext);
        quizLayout = findViewById(R.id.layoutQuiz);
        resultLayout = findViewById(R.id.layoutResult);
        confetti = findViewById(R.id.confetti);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tenseIndex = getIntent().getIntExtra(EXTRA_TENSE_INDEX, -1);
        type = getIntent().getIntExtra(EXTRA_TYPE, ExerciseGenerator.TYPE_MIXED);
        int count = getIntent().getIntExtra(EXTRA_COUNT, 10);

        String title;
        if (tenseIndex >= 0) {
            Tense tense = TenseBank.get(tenseIndex);
            questions.addAll(ExerciseGenerator.forTense(tense));
            title = tense == null ? getString(R.string.practice_title)
                    : getString(R.string.tense_practice_title, tense.name);
        } else {
            questions.addAll(ExerciseGenerator.generate(progress.getLevel(), type, count));
            title = ExerciseGenerator.typeTitle(type);
        }
        ((TextView) findViewById(R.id.textExerciseTitle)).setText(title);

        buildOptionButtons();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                index++;
                showQuestion();
            }
        });

        findViewById(R.id.buttonAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restart();
            }
        });

        findViewById(R.id.buttonDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (questions.isEmpty()) {
            prompt.setText(R.string.no_questions);
            optionsLayout.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        } else {
            showQuestion();
        }
    }

    private void buildOptionButtons() {
        optionButtons.clear();
        optionsLayout.removeAllViews();
        int marginPx = (int) (8 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < 4; i++) {
            MaterialButton button = new MaterialButton(this, null,
                    com.google.android.material.R.attr.materialButtonOutlinedStyle);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, marginPx, 0, marginPx);
            button.setLayoutParams(params);
            button.setAllCaps(false);
            button.setTextSize(16f);
            button.setMinHeight((int) (56 * getResources().getDisplayMetrics().density));
            final int position = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionChosen(position);
                }
            });
            optionButtons.add(button);
            optionsLayout.addView(button);
        }
    }

    private void showQuestion() {
        if (index >= questions.size()) {
            showResult();
            return;
        }
        answered = false;
        QuizQuestion question = questions.get(index);

        counter.setText(getString(R.string.quiz_counter, index + 1, questions.size()));
        scoreLabel.setText(getString(R.string.quiz_score, score));
        bar.setMax(questions.size());
        bar.setProgress(index);
        prompt.setText(question.prompt);
        feedback.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);

        for (int i = 0; i < optionButtons.size(); i++) {
            MaterialButton button = optionButtons.get(i);
            if (i < question.options.size()) {
                button.setVisibility(View.VISIBLE);
                button.setText(question.options.get(i));
                button.setEnabled(true);
                resetButton(button);
                Anim.enter(button, i * 55L);
            } else {
                button.setVisibility(View.GONE);
            }
        }
        Anim.enter(prompt, 0);
    }

    private void resetButton(MaterialButton button) {
        button.setStrokeColor(android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(this, R.color.outline)));
        button.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_primary));
        button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.TRANSPARENT));
    }

    private void onOptionChosen(int position) {
        if (answered) return;
        answered = true;
        QuizQuestion question = questions.get(index);
        boolean correct = position == question.correctIndex;

        for (MaterialButton button : optionButtons) {
            button.setEnabled(false);
        }

        MaterialButton chosen = optionButtons.get(position);
        MaterialButton right = optionButtons.get(question.correctIndex);

        right.setStrokeColor(android.content.res.ColorStateList.valueOf(GREEN));
        right.setTextColor(GREEN);

        if (correct) {
            score++;
            Anim.pop(chosen);
            feedback.setText(R.string.feedback_correct);
            feedback.setTextColor(GREEN);
            speaker.say(question.correctAnswer());
        } else {
            chosen.setStrokeColor(android.content.res.ColorStateList.valueOf(RED));
            chosen.setTextColor(RED);
            Anim.shake(chosen);
            String hint = question.hint == null || question.hint.trim().isEmpty()
                    ? question.correctAnswer()
                    : question.hint;
            feedback.setText(getString(R.string.feedback_wrong, hint));
            feedback.setTextColor(RED);
        }

        Anim.fadeIn(feedback);
        scoreLabel.setText(getString(R.string.quiz_score, score));
        nextButton.setText(index == questions.size() - 1
                ? R.string.quiz_see_result : R.string.quiz_next);
        Anim.fadeIn(nextButton);
    }

    private void showResult() {
        quizLayout.setVisibility(View.GONE);
        resultLayout.setVisibility(View.VISIBLE);

        if (tenseIndex >= 0) {
            progress.recordTensePractice(tenseIndex, score, questions.size());
        } else {
            progress.recordQuiz(PracticeActivity.tagFor(type), score, questions.size());
        }

        TextView resultScore = findViewById(R.id.textResultScore);
        TextView resultMessage = findViewById(R.id.textResultMessage);
        TextView resultEmoji = findViewById(R.id.textResultEmoji);

        Anim.countUp(resultScore, 0, score, " / " + questions.size());

        int percent = questions.isEmpty() ? 0 : (score * 100) / questions.size();
        if (percent >= 90) {
            resultEmoji.setText("🏆");
            resultMessage.setText(R.string.result_excellent);
        } else if (percent >= 70) {
            resultEmoji.setText("🎉");
            resultMessage.setText(R.string.result_good);
        } else if (percent >= 40) {
            resultEmoji.setText("💪");
            resultMessage.setText(R.string.result_ok);
        } else {
            resultEmoji.setText("🌱");
            resultMessage.setText(R.string.result_keep_going);
        }

        Anim.pop(resultEmoji);
        if (percent >= 70 && progress.areAnimationsEnabled()) {
            confetti.burst();
        }
    }

    private void restart() {
        index = 0;
        score = 0;
        questions.clear();
        if (tenseIndex >= 0) {
            questions.addAll(ExerciseGenerator.forTense(TenseBank.get(tenseIndex)));
        } else {
            questions.addAll(ExerciseGenerator.generate(progress.getLevel(), type,
                    getIntent().getIntExtra(EXTRA_COUNT, 10)));
        }
        resultLayout.setVisibility(View.GONE);
        quizLayout.setVisibility(View.VISIBLE);
        showQuestion();
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        super.onDestroy();
    }
}

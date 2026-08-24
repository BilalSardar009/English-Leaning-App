package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saim.englishlearning.adapter.WordAdapter;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.WordBank;
import com.saim.englishlearning.model.Word;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.ConfettiView;
import com.saim.englishlearning.util.Speaker;

import java.util.ArrayList;
import java.util.List;

/** One unit of ten words: read, listen, star and mark as learned. */
public class LessonActivity extends AppCompatActivity {

    public static final String EXTRA_UNIT_NUMBER = "unit_number";
    public static final String EXTRA_WORD_IDS = "word_ids";

    private ProgressManager progress;
    private Speaker speaker;
    private ConfettiView confetti;
    private TextView progressLabel;
    private WordAdapter adapter;
    private final List<Word> words = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);
        confetti = findViewById(R.id.confetti);
        progressLabel = findViewById(R.id.textLessonProgress);

        int unitNumber = getIntent().getIntExtra(EXTRA_UNIT_NUMBER, 1);
        int[] ids = getIntent().getIntArrayExtra(EXTRA_WORD_IDS);
        if (ids != null) {
            for (int id : ids) {
                Word word = WordBank.byId(id);
                if (word != null) words.add(word);
            }
        }

        ((TextView) findViewById(R.id.textLessonHeading))
                .setText(getString(R.string.lesson_heading, unitNumber));

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView list = findViewById(R.id.listWords);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordAdapter(words, progress, new WordAdapter.Listener() {
            @Override
            public void onSpeak(Word word) {
                speaker.say(word.word);
            }

            @Override
            public void onSpeakExample(Word word) {
                speaker.say(word.example);
            }

            @Override
            public void onToggleLearned(Word word, View row) {
                boolean wasLearned = progress.isLearned(word.id);
                if (!wasLearned) {
                    progress.markLearned(word.id);
                    Anim.pop(row);
                    if (allLearned() && progress.areAnimationsEnabled()) {
                        confetti.burst();
                    }
                }
                updateProgressLabel();
            }

            @Override
            public void onToggleFavourite(Word word, View star) {
                progress.toggleFavourite(word.id);
                Anim.pop(star);
            }
        });
        list.setAdapter(adapter);

        findViewById(R.id.buttonPracticeUnit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                Intent intent = new Intent(LessonActivity.this, ExerciseActivity.class);
                intent.putExtra(ExerciseActivity.EXTRA_TYPE, com.saim.englishlearning.data
                        .ExerciseGenerator.TYPE_MIXED);
                intent.putExtra(ExerciseActivity.EXTRA_COUNT, 10);
                startActivity(intent);
            }
        });

        updateProgressLabel();
    }

    private boolean allLearned() {
        for (Word word : words) {
            if (!progress.isLearned(word.id)) return false;
        }
        return !words.isEmpty();
    }

    private void updateProgressLabel() {
        int done = 0;
        for (Word word : words) {
            if (progress.isLearned(word.id)) done++;
        }
        progressLabel.setText(getString(R.string.lesson_progress, done, words.size()));
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        super.onDestroy();
    }
}

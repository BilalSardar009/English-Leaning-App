package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.saim.englishlearning.data.ExerciseGenerator;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.util.Anim;

/** Chooses which kind of exercise to run. */
public class PracticeActivity extends AppCompatActivity {

    private ProgressManager progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        progress = new ProgressManager(this);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        wire(R.id.rowMeaning, ExerciseGenerator.TYPE_MEANING);
        wire(R.id.rowUrdu, ExerciseGenerator.TYPE_URDU);
        wire(R.id.rowUrduToEnglish, ExerciseGenerator.TYPE_URDU_TO_ENGLISH);
        wire(R.id.rowBlank, ExerciseGenerator.TYPE_BLANK);
        wire(R.id.rowIdiom, ExerciseGenerator.TYPE_IDIOM);
        wire(R.id.rowPhrasal, ExerciseGenerator.TYPE_PHRASAL);
        wire(R.id.rowMixed, ExerciseGenerator.TYPE_MIXED);

        Anim.enterAll(50,
                findViewById(R.id.rowMeaning),
                findViewById(R.id.rowUrdu),
                findViewById(R.id.rowUrduToEnglish),
                findViewById(R.id.rowBlank),
                findViewById(R.id.rowIdiom),
                findViewById(R.id.rowPhrasal),
                findViewById(R.id.rowMixed));
    }

    @Override
    protected void onResume() {
        super.onResume();
        showBest(R.id.textBestMeaning, ExerciseGenerator.TYPE_MEANING);
        showBest(R.id.textBestUrdu, ExerciseGenerator.TYPE_URDU);
        showBest(R.id.textBestUrduToEnglish, ExerciseGenerator.TYPE_URDU_TO_ENGLISH);
        showBest(R.id.textBestBlank, ExerciseGenerator.TYPE_BLANK);
        showBest(R.id.textBestIdiom, ExerciseGenerator.TYPE_IDIOM);
        showBest(R.id.textBestPhrasal, ExerciseGenerator.TYPE_PHRASAL);
        showBest(R.id.textBestMixed, ExerciseGenerator.TYPE_MIXED);
    }

    private void showBest(int viewId, int type) {
        TextView view = findViewById(viewId);
        if (view == null) return;
        int best = progress.getBestScore(tagFor(type));
        view.setText(best > 0 ? getString(R.string.best_score, best) : getString(R.string.not_tried));
    }

    static String tagFor(int type) {
        return "type_" + type;
    }

    private void wire(int viewId, final int type) {
        View row = findViewById(viewId);
        if (row == null) return;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                Intent intent = new Intent(PracticeActivity.this, ExerciseActivity.class);
                intent.putExtra(ExerciseActivity.EXTRA_TYPE, type);
                intent.putExtra(ExerciseActivity.EXTRA_COUNT, 10);
                startActivity(intent);
            }
        });
    }
}

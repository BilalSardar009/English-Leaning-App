package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.TenseBank;
import com.saim.englishlearning.model.Tense;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.Speaker;

/** Teaches one tense, then sends the learner into practice for it. */
public class TenseDetailActivity extends AppCompatActivity {

    public static final String EXTRA_INDEX = "tense_index";

    private ProgressManager progress;
    private Speaker speaker;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tense_detail);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);

        index = getIntent().getIntExtra(EXTRA_INDEX, 0);
        final Tense tense = TenseBank.get(index);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (tense == null) {
            finish();
            return;
        }

        TextView name = findViewById(R.id.textTenseName);
        TextView urduName = findViewById(R.id.textTenseUrduName);
        TextView formula = findViewById(R.id.textTenseFormula);
        TextView usage = findViewById(R.id.textTenseUsage);
        TextView usageUrdu = findViewById(R.id.textTenseUsageUrdu);

        name.setText(tense.name);
        formula.setText(tense.formula);
        usage.setText(tense.usage);

        if (progress.isUrduEnabled()) {
            urduName.setVisibility(View.VISIBLE);
            urduName.setText(tense.urduName);
            usageUrdu.setVisibility(View.VISIBLE);
            usageUrdu.setText(tense.usageUrdu);
        } else {
            urduName.setVisibility(View.GONE);
            usageUrdu.setVisibility(View.GONE);
        }

        buildExamples(tense);

        findViewById(R.id.buttonPractiseTense).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                Intent intent = new Intent(TenseDetailActivity.this, ExerciseActivity.class);
                intent.putExtra(ExerciseActivity.EXTRA_TENSE_INDEX, index);
                startActivity(intent);
            }
        });

        Anim.enterAll(70,
                findViewById(R.id.cardFormula),
                findViewById(R.id.cardUsage),
                findViewById(R.id.cardExamples),
                findViewById(R.id.buttonPractiseTense));
    }

    private void buildExamples(Tense tense) {
        LinearLayout container = findViewById(R.id.layoutExamples);
        container.removeAllViews();
        boolean urdu = progress.isUrduEnabled();
        int padding = (int) (12 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < tense.examples.size(); i++) {
            final String english = tense.examples.get(i);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(padding, padding, padding, padding);
            row.setBackgroundResource(R.drawable.bg_example_row);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.bottomMargin = padding / 2;
            row.setLayoutParams(rowParams);

            TextView englishView = new TextView(this);
            englishView.setText(english);
            englishView.setTextSize(16f);
            englishView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            row.addView(englishView);

            if (urdu && i < tense.examplesUrdu.size()) {
                TextView urduView = new TextView(this);
                urduView.setText(tense.examplesUrdu.get(i));
                urduView.setTextSize(15f);
                urduView.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
                urduView.setPadding(0, padding / 3, 0, 0);
                row.addView(urduView);
            }

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Anim.pop(v);
                    speaker.say(english);
                }
            });

            container.addView(row);
            Anim.enter(row, i * 60L);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int best = progress.getTenseBest(index);
        TextView bestView = findViewById(R.id.textTenseBest);
        bestView.setText(best > 0
                ? getString(R.string.tense_best, best)
                : getString(R.string.tense_not_practised));
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        super.onDestroy();
    }
}

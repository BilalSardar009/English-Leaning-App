package com.saim.englishlearning.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.saim.englishlearning.R;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.util.Anim;

/** Hub for the arcade style practice games. */
public class GamesActivity extends AppCompatActivity {

    private ProgressManager progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        progress = new ProgressManager(this);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        wire(R.id.cardMatch, WordMatchActivity.class);
        wire(R.id.cardSpelling, SpellingBeeActivity.class);
        wire(R.id.cardScramble, ScrambleActivity.class);
        wire(R.id.cardSpeed, SpeedRoundActivity.class);
        wire(R.id.cardOddOne, OddOneOutActivity.class);

        Anim.enterAll(60,
                findViewById(R.id.cardMatch),
                findViewById(R.id.cardSpelling),
                findViewById(R.id.cardScramble),
                findViewById(R.id.cardSpeed),
                findViewById(R.id.cardOddOne));
    }

    @Override
    protected void onResume() {
        super.onResume();
        best(R.id.textBestMatch, WordMatchActivity.KEY);
        best(R.id.textBestSpelling, SpellingBeeActivity.KEY);
        best(R.id.textBestScramble, ScrambleActivity.KEY);
        best(R.id.textBestSpeed, SpeedRoundActivity.KEY);
        best(R.id.textBestOddOne, OddOneOutActivity.KEY);
    }

    private void best(int viewId, String key) {
        TextView view = findViewById(viewId);
        if (view == null) return;
        int value = progress.getGameBest(key);
        view.setText(value > 0
                ? getString(R.string.game_best, value) : getString(R.string.not_tried));
    }

    private void wire(int viewId, final Class<?> target) {
        View card = findViewById(viewId);
        if (card == null) return;
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Anim.pop(v);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(GamesActivity.this, target));
                    }
                }, 110);
            }
        });
    }
}

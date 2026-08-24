package com.saim.englishlearning.game;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.GridLayout;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tap an English word, then tap its meaning. Matched pairs fade away; wrong
 * pairs shake and reset. Clear the board to finish the round.
 */
public class WordMatchActivity extends AppCompatActivity {

    public static final String KEY = "word_match";
    private static final int PAIRS_PER_ROUND = 6;

    private static final int GREEN = Color.parseColor("#2E9E6B");
    private static final int RED = Color.parseColor("#D6455D");

    private ProgressManager progress;
    private ConfettiView confetti;
    private GridLayout board;
    private TextView scoreLabel;
    private TextView roundLabel;

    private final List<MaterialButton> tiles = new ArrayList<>();
    private MaterialButton selected;
    private int selectedWordId = -1;
    private int matched;
    private int score;
    private int round = 1;
    private boolean locked;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_match);
        progress = new ProgressManager(this);

        confetti = findViewById(R.id.confetti);
        board = findViewById(R.id.gridMatch);
        scoreLabel = findViewById(R.id.textMatchScore);
        roundLabel = findViewById(R.id.textMatchRound);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.buttonMatchAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                round++;
                deal();
            }
        });

        deal();
    }

    private void deal() {
        board.removeAllViews();
        tiles.clear();
        selected = null;
        selectedWordId = -1;
        matched = 0;
        locked = false;

        List<Word> pool = WordBank.byLevel(progress.getLevel());
        if (pool.size() < PAIRS_PER_ROUND) pool = new ArrayList<>(WordBank.all());
        if (pool.size() < PAIRS_PER_ROUND) {
            ((TextView) findViewById(R.id.textMatchHint)).setText(R.string.no_words);
            return;
        }

        List<Word> chosen = new ArrayList<>(pool);
        Collections.shuffle(chosen);
        chosen = new ArrayList<>(chosen.subList(0, PAIRS_PER_ROUND));

        List<Object[]> cells = new ArrayList<>();
        for (Word word : chosen) {
            cells.add(new Object[]{word.id, word.word, true});
            cells.add(new Object[]{word.id, word.meaning, false});
        }
        Collections.shuffle(cells);

        board.setColumnCount(2);
        for (Object[] cell : cells) {
            final int wordId = (Integer) cell[0];
            String label = (String) cell[1];

            MaterialButton tile = new MaterialButton(this, null,
                    com.google.android.material.R.attr.materialButtonOutlinedStyle);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            int margin = (int) (5 * getResources().getDisplayMetrics().density);
            params.setMargins(margin, margin, margin, margin);
            tile.setLayoutParams(params);
            tile.setText(label);
            tile.setAllCaps(false);
            tile.setTextSize(13f);
            tile.setMinHeight((int) (64 * getResources().getDisplayMetrics().density));
            tile.setTag(wordId);
            reset(tile);

            tile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTileTapped((MaterialButton) v, wordId);
                }
            });

            tiles.add(tile);
            board.addView(tile);
            Anim.enter(tile, tiles.size() * 30L);
        }

        roundLabel.setText(getString(R.string.match_round, round));
        scoreLabel.setText(getString(R.string.match_score, score));
        findViewById(R.id.buttonMatchAgain).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.textMatchHint)).setText(R.string.match_hint);
    }

    private void reset(MaterialButton tile) {
        tile.setStrokeColor(android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.outline)));
        tile.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
    }

    private void select(MaterialButton tile) {
        tile.setStrokeColor(android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.brand_primary)));
        tile.setTextColor(ContextCompat.getColor(this, R.color.brand_primary));
    }

    private void onTileTapped(final MaterialButton tile, int wordId) {
        if (locked || tile.getVisibility() != View.VISIBLE || tile == selected) return;
        Anim.pop(tile);

        if (selected == null) {
            selected = tile;
            selectedWordId = wordId;
            select(tile);
            return;
        }

        final MaterialButton first = selected;
        final MaterialButton second = tile;
        selected = null;

        if (selectedWordId == wordId) {
            first.setStrokeColor(android.content.res.ColorStateList.valueOf(GREEN));
            first.setTextColor(GREEN);
            second.setStrokeColor(android.content.res.ColorStateList.valueOf(GREEN));
            second.setTextColor(GREEN);
            matched++;
            score += 2;
            scoreLabel.setText(getString(R.string.match_score, score));
            progress.markLearned(wordId);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Anim.fadeOut(first);
                    Anim.fadeOut(second);
                    first.setVisibility(View.INVISIBLE);
                    second.setVisibility(View.INVISIBLE);
                    if (matched == PAIRS_PER_ROUND) finishRound();
                }
            }, 260);
        } else {
            locked = true;
            second.setStrokeColor(android.content.res.ColorStateList.valueOf(RED));
            second.setTextColor(RED);
            Anim.shake(second);
            Anim.shake(first);
            score = Math.max(0, score - 1);
            scoreLabel.setText(getString(R.string.match_score, score));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reset(first);
                    reset(second);
                    locked = false;
                }
            }, 520);
        }
        selectedWordId = -1;
    }

    private void finishRound() {
        progress.recordGame(KEY, score);
        if (progress.areAnimationsEnabled()) confetti.burst();
        ((TextView) findViewById(R.id.textMatchHint))
                .setText(getString(R.string.match_cleared, score));
        View again = findViewById(R.id.buttonMatchAgain);
        again.setVisibility(View.VISIBLE);
        Anim.pop(again);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}

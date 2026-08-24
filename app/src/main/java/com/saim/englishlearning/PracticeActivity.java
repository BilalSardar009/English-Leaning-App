package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.saim.englishlearning.data.ExerciseGenerator;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.game.OddOneOutActivity;
import com.saim.englishlearning.game.ScrambleActivity;
import com.saim.englishlearning.game.SpeedRoundActivity;
import com.saim.englishlearning.game.SpellingBeeActivity;
import com.saim.englishlearning.game.WordMatchActivity;
import com.saim.englishlearning.util.Anim;

/**
 * One place for everything you practise: the quiz rounds and the games, rather
 * than two separate hubs that did much the same thing.
 */
public class PracticeActivity extends AppCompatActivity {

    /** A quiz round: exercise type plus how it is described. */
    private static class Quiz {
        final int type;
        final String title;
        final String detail;
        final String emoji;
        final int circle;

        Quiz(int type, String title, String detail, String emoji, int circle) {
            this.type = type;
            this.title = title;
            this.detail = detail;
            this.emoji = emoji;
            this.circle = circle;
        }
    }

    /** A game screen. */
    private static class Game {
        final Class<?> target;
        final String key;
        final String title;
        final String detail;
        final String emoji;
        final int circle;

        Game(Class<?> target, String key, String title, String detail, String emoji, int circle) {
            this.target = target;
            this.key = key;
            this.title = title;
            this.detail = detail;
            this.emoji = emoji;
            this.circle = circle;
        }
    }

    private ProgressManager progress;
    private Quiz[] quizzes;
    private Game[] games;

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

        quizzes = new Quiz[]{
                new Quiz(ExerciseGenerator.TYPE_MEANING, getString(R.string.practice_meaning),
                        getString(R.string.practice_meaning_sub), "📖", R.drawable.bg_circle_purple),
                new Quiz(ExerciseGenerator.TYPE_URDU_BOTH, getString(R.string.practice_urdu_both),
                        getString(R.string.practice_urdu_both_sub), "🔁", R.drawable.bg_circle_teal),
                new Quiz(ExerciseGenerator.TYPE_BLANK, getString(R.string.practice_blank),
                        getString(R.string.practice_blank_sub), "✏️", R.drawable.bg_circle_amber),
                new Quiz(ExerciseGenerator.TYPE_PHRASES, getString(R.string.practice_phrases),
                        getString(R.string.practice_phrases_sub), "🦊", R.drawable.bg_circle_plum),
                new Quiz(ExerciseGenerator.TYPE_MIXED, getString(R.string.practice_mixed),
                        getString(R.string.practice_mixed_sub), "🎲", R.drawable.bg_circle_coral),
        };

        games = new Game[]{
                new Game(WordMatchActivity.class, WordMatchActivity.KEY,
                        getString(R.string.game_match), getString(R.string.game_match_sub),
                        "🧠", R.drawable.bg_circle_purple),
                new Game(SpellingBeeActivity.class, SpellingBeeActivity.KEY,
                        getString(R.string.game_spelling), getString(R.string.game_spelling_sub),
                        "🐝", R.drawable.bg_circle_teal),
                new Game(ScrambleActivity.class, ScrambleActivity.KEY,
                        getString(R.string.game_scramble), getString(R.string.game_scramble_sub),
                        "🔀", R.drawable.bg_circle_pink),
                new Game(SpeedRoundActivity.class, SpeedRoundActivity.KEY,
                        getString(R.string.game_speed), getString(R.string.game_speed_sub),
                        "⚡", R.drawable.bg_circle_coral),
                new Game(OddOneOutActivity.class, OddOneOutActivity.KEY,
                        getString(R.string.game_odd), getString(R.string.game_odd_sub),
                        "🔍", R.drawable.bg_circle_amber),
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        build();
    }

    private void build() {
        LinearLayout quizBox = findViewById(R.id.layoutQuizzes);
        LinearLayout gameBox = findViewById(R.id.layoutGames);
        quizBox.removeAllViews();
        gameBox.removeAllViews();

        int delay = 0;
        for (final Quiz quiz : quizzes) {
            View row = row(quiz.emoji, quiz.circle, quiz.title, quiz.detail,
                    badge(progress.getBestScore(tagFor(quiz.type))));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Anim.pop(v);
                    Intent intent = new Intent(PracticeActivity.this, ExerciseActivity.class);
                    intent.putExtra(ExerciseActivity.EXTRA_TYPE, quiz.type);
                    intent.putExtra(ExerciseActivity.EXTRA_COUNT, 10);
                    startActivity(intent);
                }
            });
            quizBox.addView(row);
            Anim.enter(row, delay);
            delay += 45;
        }

        for (final Game game : games) {
            View row = row(game.emoji, game.circle, game.title, game.detail,
                    badge(progress.getGameBest(game.key)));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Anim.pop(v);
                    startActivity(new Intent(PracticeActivity.this, game.target));
                }
            });
            gameBox.addView(row);
            Anim.enter(row, delay);
            delay += 45;
        }
    }

    private String badge(int best) {
        return best > 0 ? getString(R.string.best_score, best) : getString(R.string.not_tried);
    }

    /** Builds one tappable row; the quizzes and the games look the same on purpose. */
    private View row(String emoji, int circle, String title, String detail, String badge) {
        float density = getResources().getDisplayMetrics().density;
        int pad = (int) (16 * density);

        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = (int) (10 * density);
        card.setLayoutParams(cardParams);
        card.setRadius(18 * density);
        card.setCardElevation(2 * density);
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout line = new LinearLayout(this);
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setGravity(Gravity.CENTER_VERTICAL);
        line.setPadding(pad, pad, pad, pad);

        TextView icon = new TextView(this);
        int size = (int) (48 * density);
        icon.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        icon.setBackgroundResource(circle);
        icon.setGravity(Gravity.CENTER);
        icon.setText(emoji);
        icon.setTextSize(21f);
        line.addView(icon);

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textParams.leftMargin = (int) (14 * density);
        texts.setLayoutParams(textParams);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(17f);
        titleView.setTypeface(titleView.getTypeface(), android.graphics.Typeface.BOLD);
        titleView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        texts.addView(titleView);

        TextView detailView = new TextView(this);
        detailView.setText(detail);
        detailView.setTextSize(13f);
        detailView.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        texts.addView(detailView);
        line.addView(texts);

        TextView badgeView = new TextView(this);
        badgeView.setText(badge);
        badgeView.setTextSize(12f);
        badgeView.setBackgroundResource(R.drawable.bg_pill_light);
        badgeView.setPadding(pad - 4, pad / 2 - 2, pad - 4, pad / 2 - 2);
        badgeView.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        line.addView(badgeView);

        card.addView(line);
        return card;
    }

    static String tagFor(int type) {
        return "type_" + type;
    }
}

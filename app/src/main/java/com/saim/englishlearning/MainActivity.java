package com.saim.englishlearning;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.saim.englishlearning.data.PhraseBank;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.SentenceBank;
import com.saim.englishlearning.data.WordBank;
import com.saim.englishlearning.game.GamesActivity;
import com.saim.englishlearning.model.Word;
import com.saim.englishlearning.notifications.ReminderScheduler;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.ConfettiView;
import com.saim.englishlearning.util.RingProgressView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressManager progress;
    private TextView greeting;
    private TextView subGreeting;
    private TextView streakText;
    private TextView xpText;
    private TextView wordOfDayWord;
    private TextView wordOfDayMeaning;
    private TextView wordOfDayUrdu;
    private RingProgressView rankRing;
    private ConfettiView confetti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressManager(this);

        if (!progress.isOnboarded()) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        greeting = findViewById(R.id.textGreeting);
        subGreeting = findViewById(R.id.textSubGreeting);
        streakText = findViewById(R.id.textStreak);
        xpText = findViewById(R.id.textXp);
        rankRing = findViewById(R.id.ringRank);
        confetti = findViewById(R.id.confetti);
        wordOfDayWord = findViewById(R.id.textWotdWord);
        wordOfDayMeaning = findViewById(R.id.textWotdMeaning);
        wordOfDayUrdu = findViewById(R.id.textWotdUrdu);

        wire(R.id.cardLessons, LessonListActivity.class);
        wire(R.id.cardPractice, PracticeActivity.class);
        wire(R.id.cardGames, GamesActivity.class);
        wire(R.id.cardTenses, TensesActivity.class);
        wire(R.id.cardSentences, SentenceBuilderActivity.class);
        wire(R.id.cardPhrases, PhrasesActivity.class);
        wire(R.id.cardFlashcards, FlashcardActivity.class);
        wire(R.id.cardDictionary, DictionaryActivity.class);
        wire(R.id.cardTranslator, TranslatorActivity.class);
        wire(R.id.cardFavourites, FavoritesActivity.class);
        wire(R.id.cardAchievements, AchievementsActivity.class);
        wire(R.id.cardWordOfDay, WordOfDayActivity.class);
        wire(R.id.buttonSettings, SettingsActivity.class);

        requestNotificationPermission();
        ReminderScheduler.sync(this);
    }

    private void wire(int viewId, final Class<?> target) {
        View view = findViewById(viewId);
        if (view == null) return;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Anim.pop(v);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, target));
                    }
                }, 110);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!progress.isOnboarded()) return;
        boolean streakGrew = progress.touchStreak();
        refresh();
        celebrateNewAchievements(streakGrew);
    }

    private void refresh() {
        String name = progress.getUserName();
        greeting.setText(name.isEmpty()
                ? getString(R.string.greeting_plain)
                : getString(R.string.greeting_named, name));

        int learned = progress.getLearnedCount();
        int total = Math.max(1, WordBank.size());
        subGreeting.setText(getString(R.string.greeting_sub, learned, total,
                com.saim.englishlearning.model.Word.levelName(progress.getLevel())));

        streakText.setText(getString(R.string.stat_streak, progress.getStreak()));
        Anim.countUp(xpText, 0, progress.getXp(), " XP");

        rankRing.setPercent(progress.getXpIntoRank(),
                getString(R.string.rank_short, progress.getRank()));
        rankRing.setCaption(getString(R.string.rank_caption));

        Word word = WordBank.wordOfTheDay();
        if (word != null) {
            wordOfDayWord.setText(word.word);
            wordOfDayMeaning.setText(word.meaning);
            if (progress.isUrduEnabled()) {
                wordOfDayUrdu.setVisibility(View.VISIBLE);
                wordOfDayUrdu.setText(word.urdu);
            } else {
                wordOfDayUrdu.setVisibility(View.GONE);
            }
        }

        TextView counts = findViewById(R.id.textLibraryCounts);
        counts.setText(getString(R.string.library_counts,
                WordBank.size(), PhraseBank.size(), SentenceBank.size()));

        if (progress.areAnimationsEnabled()) {
            Anim.enterAll(55,
                    findViewById(R.id.cardHeader),
                    findViewById(R.id.cardWordOfDay),
                    findViewById(R.id.gridTop),
                    findViewById(R.id.gridMiddle),
                    findViewById(R.id.gridBottom));
        }
    }

    private void celebrateNewAchievements(boolean streakGrew) {
        List<String> unlocked = new ArrayList<>();
        progress.buildAchievements(unlocked);
        if (!unlocked.isEmpty() && progress.areAnimationsEnabled()) {
            confetti.burst();
            android.widget.Toast.makeText(this,
                    getString(R.string.achievement_unlocked, unlocked.get(0)),
                    android.widget.Toast.LENGTH_LONG).show();
        } else if (streakGrew && progress.getStreak() > 1 && progress.areAnimationsEnabled()) {
            confetti.burst(45);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 91);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 91) {
            ReminderScheduler.sync(this);
        }
    }
}

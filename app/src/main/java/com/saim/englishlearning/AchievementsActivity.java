package com.saim.englishlearning;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saim.englishlearning.adapter.AchievementAdapter;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.model.Achievement;
import com.saim.englishlearning.util.RingProgressView;

import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private ProgressManager progress;
    private RecyclerView list;
    private RingProgressView ring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        progress = new ProgressManager(this);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ring = findViewById(R.id.ringAchievements);
        list = findViewById(R.id.listAchievements);
        list.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Achievement> achievements = progress.buildAchievements(new ArrayList<String>());
        list.setAdapter(new AchievementAdapter(achievements));

        int unlocked = 0;
        for (Achievement a : achievements) {
            if (a.unlocked) unlocked++;
        }
        int percent = achievements.isEmpty() ? 0 : (unlocked * 100) / achievements.size();
        ring.setPercent(percent, unlocked + "/" + achievements.size());
        ring.setCaption(getString(R.string.achievements_ring_caption));

        ((TextView) findViewById(R.id.textAchievementsStats)).setText(
                getString(R.string.achievements_stats,
                        progress.getXp(), progress.getRank(), progress.getStreak()));
    }
}

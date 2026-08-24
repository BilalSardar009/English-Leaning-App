package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saim.englishlearning.adapter.TenseAdapter;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.TenseBank;
import com.saim.englishlearning.util.RingProgressView;

public class TensesActivity extends AppCompatActivity {

    private ProgressManager progress;
    private RecyclerView list;
    private RingProgressView ring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenses);
        progress = new ProgressManager(this);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ring = findViewById(R.id.ringTenses);
        list = findViewById(R.id.listTenses);
        list.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.setAdapter(new TenseAdapter(TenseBank.all(), progress, new TenseAdapter.Listener() {
            @Override
            public void onTenseClicked(int index) {
                Intent intent = new Intent(TensesActivity.this, TenseDetailActivity.class);
                intent.putExtra(TenseDetailActivity.EXTRA_INDEX, index);
                startActivity(intent);
            }
        }));

        int total = TenseBank.size();
        int mastered = 0;
        for (int i = 0; i < total; i++) {
            if (progress.getTenseBest(i) >= 5) mastered++;
        }
        int percent = total == 0 ? 0 : (mastered * 100) / total;
        ring.setPercent(percent, mastered + "/" + total);
        ring.setCaption(getString(R.string.tenses_ring_caption));
        ((TextView) findViewById(R.id.textTensesSub))
                .setText(getString(R.string.tenses_sub, mastered, total));
    }
}

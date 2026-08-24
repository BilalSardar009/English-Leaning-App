package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saim.englishlearning.adapter.UnitAdapter;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.WordBank;
import com.saim.englishlearning.model.Word;
import com.saim.englishlearning.util.RingProgressView;

import java.util.ArrayList;
import java.util.List;

/** Breaks the current level into small units of ten words each. */
public class LessonListActivity extends AppCompatActivity {

    public static final int WORDS_PER_UNIT = 10;

    private ProgressManager progress;
    private RecyclerView list;
    private RingProgressView ring;
    private TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);
        progress = new ProgressManager(this);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        subtitle = findViewById(R.id.textLessonSubtitle);
        ring = findViewById(R.id.ringLevel);
        list = findViewById(R.id.listUnits);
        list.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.buttonFlashcards).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.saim.englishlearning.util.Anim.pop(v);
                startActivity(new Intent(LessonListActivity.this, FlashcardActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int level = progress.getLevel();
        List<Word> words = WordBank.byLevel(level);

        ((TextView) findViewById(R.id.textLessonTitle))
                .setText(getString(R.string.lessons_title, Word.levelName(level)));

        int learned = 0;
        for (Word w : words) {
            if (progress.isLearned(w.id)) learned++;
        }
        subtitle.setText(getString(R.string.lessons_sub, learned, words.size()));
        int percent = words.isEmpty() ? 0 : (learned * 100) / words.size();
        ring.setPercent(percent, percent + "%");
        ring.setCaption(getString(R.string.lessons_ring_caption));

        List<UnitAdapter.Unit> units = new ArrayList<>();
        for (int start = 0; start < words.size(); start += WORDS_PER_UNIT) {
            int end = Math.min(start + WORDS_PER_UNIT, words.size());
            int done = 0;
            List<Integer> ids = new ArrayList<>();
            for (int i = start; i < end; i++) {
                ids.add(words.get(i).id);
                if (progress.isLearned(words.get(i).id)) done++;
            }
            units.add(new UnitAdapter.Unit(
                    (start / WORDS_PER_UNIT) + 1,
                    words.get(start).word,
                    words.get(end - 1).word,
                    done,
                    end - start,
                    ids));
        }

        list.setAdapter(new UnitAdapter(units, new UnitAdapter.Listener() {
            @Override
            public void onUnitClicked(UnitAdapter.Unit unit) {
                Intent intent = new Intent(LessonListActivity.this, LessonActivity.class);
                intent.putExtra(LessonActivity.EXTRA_UNIT_NUMBER, unit.number);
                intent.putExtra(LessonActivity.EXTRA_WORD_IDS, toArray(unit.wordIds));
                startActivity(intent);
            }
        }));
    }

    private static int[] toArray(List<Integer> ids) {
        int[] out = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) out[i] = ids.get(i);
        return out;
    }
}

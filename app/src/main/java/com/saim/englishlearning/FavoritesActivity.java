package com.saim.englishlearning;

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
import com.saim.englishlearning.util.Speaker;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private ProgressManager progress;
    private Speaker speaker;
    private WordAdapter adapter;
    private final List<Word> favourites = new ArrayList<>();
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        empty = findViewById(R.id.textFavouritesEmpty);
        RecyclerView list = findViewById(R.id.listFavourites);
        list.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WordAdapter(favourites, progress, new WordAdapter.Listener() {
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
                progress.markLearned(word.id);
                Anim.pop(row);
            }

            @Override
            public void onToggleFavourite(Word word, View star) {
                progress.toggleFavourite(word.id);
                Anim.pop(star);
                reload();
            }
        });
        list.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        favourites.clear();
        for (String id : progress.getFavourites()) {
            try {
                Word word = WordBank.byId(Integer.parseInt(id));
                if (word != null) favourites.add(word);
            } catch (NumberFormatException ignored) {
                // A stale id from an older build is simply skipped.
            }
        }
        adapter.notifyDataSetChanged();
        empty.setVisibility(favourites.isEmpty() ? View.VISIBLE : View.GONE);
        ((TextView) findViewById(R.id.textFavouritesCount))
                .setText(getString(R.string.favourites_count, favourites.size()));
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        super.onDestroy();
    }
}

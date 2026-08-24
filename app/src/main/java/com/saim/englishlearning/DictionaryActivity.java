package com.saim.englishlearning;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

/** Search the whole word bank in English, Urdu or by meaning. */
public class DictionaryActivity extends AppCompatActivity {

    private ProgressManager progress;
    private Speaker speaker;
    private WordAdapter adapter;
    private EditText search;
    private TextView empty;
    private final List<Word> shown = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search = findViewById(R.id.inputSearch);
        empty = findViewById(R.id.textDictionaryEmpty);
        RecyclerView list = findViewById(R.id.listDictionary);
        list.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WordAdapter(shown, progress, listener());
        list.setAdapter(adapter);

        ((TextView) findViewById(R.id.textDictionarySub))
                .setText(getString(R.string.dictionary_sub, WordBank.size()));

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {
            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                apply(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        apply("");
    }

    private WordAdapter.Listener listener() {
        return new WordAdapter.Listener() {
            @Override
            public void onSpeak(Word word) {
                speaker.say(word.word);
                progress.recordLookup();
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
            }
        };
    }

    private void apply(String query) {
        shown.clear();
        if (query.trim().isEmpty()) {
            List<Word> all = WordBank.byLevel(progress.getLevel());
            shown.addAll(all.subList(0, Math.min(60, all.size())));
        } else {
            shown.addAll(WordBank.search(query));
            progress.recordLookup();
        }
        adapter.notifyDataSetChanged();
        empty.setVisibility(shown.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        super.onDestroy();
    }
}

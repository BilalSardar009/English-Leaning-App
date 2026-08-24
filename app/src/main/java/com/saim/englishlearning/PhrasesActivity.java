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

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.saim.englishlearning.adapter.PhraseAdapter;
import com.saim.englishlearning.data.PhraseBank;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.model.Phrase;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.Speaker;

import java.util.ArrayList;
import java.util.List;

/** Browsable library of idioms and phrasal verbs. */
public class PhrasesActivity extends AppCompatActivity {

    private ProgressManager progress;
    private Speaker speaker;
    private PhraseAdapter adapter;
    private RecyclerView list;
    private EditText search;
    private TextView empty;
    private int kind = Phrase.KIND_IDIOM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrases);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list = findViewById(R.id.listPhrases);
        search = findViewById(R.id.inputPhraseSearch);
        empty = findViewById(R.id.textPhraseEmpty);
        list.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PhraseAdapter(new ArrayList<Phrase>(), progress, new PhraseAdapter.Listener() {
            @Override
            public void onSpeak(Phrase phrase) {
                speaker.say(phrase.phrase);
            }

            @Override
            public void onSpeakExample(Phrase phrase) {
                speaker.say(phrase.example);
            }

            @Override
            public void onToggleMastered(Phrase phrase, View row) {
                if (!progress.isPhraseMastered(phrase.id)) {
                    progress.markPhraseMastered(phrase.id);
                    Anim.pop(row);
                }
            }
        });
        list.setAdapter(adapter);

        MaterialButtonToggleGroup toggle = findViewById(R.id.togglePhraseKind);
        toggle.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (!isChecked) return;
                kind = checkedId == R.id.buttonIdioms ? Phrase.KIND_IDIOM : Phrase.KIND_PHRASAL;
                apply();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {
            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        apply();
    }

    private void apply() {
        String query = search.getText().toString().trim();
        List<Phrase> shown = new ArrayList<>();
        if (query.isEmpty()) {
            shown.addAll(PhraseBank.byKind(kind));
        } else {
            for (Phrase p : PhraseBank.search(query)) {
                if (p.kind == kind) shown.add(p);
            }
        }
        adapter.replace(shown);
        empty.setVisibility(shown.isEmpty() ? View.VISIBLE : View.GONE);
        ((TextView) findViewById(R.id.textPhraseCount))
                .setText(getString(R.string.phrase_count, shown.size()));
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        super.onDestroy();
    }
}

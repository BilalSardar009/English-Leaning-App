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
import com.saim.englishlearning.adapter.WordAdapter;
import com.saim.englishlearning.data.PhraseBank;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.WordBank;
import com.saim.englishlearning.model.Phrase;
import com.saim.englishlearning.model.Word;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.Speaker;

import java.util.ArrayList;
import java.util.List;

/**
 * The single place to look things up: words, idioms, phrasal verbs and the
 * learner's own saved words, chosen with one row of tabs.
 */
public class DictionaryActivity extends AppCompatActivity {

    private static final int TAB_WORDS = 0;
    private static final int TAB_IDIOMS = 1;
    private static final int TAB_PHRASALS = 2;
    private static final int TAB_SAVED = 3;

    private ProgressManager progress;
    private Speaker speaker;

    private WordAdapter wordAdapter;
    private PhraseAdapter phraseAdapter;
    private RecyclerView list;
    private EditText search;
    private TextView empty;
    private TextView countLabel;

    private final List<Word> words = new ArrayList<>();
    private int tab = TAB_WORDS;

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
        countLabel = findViewById(R.id.textDictionarySub);
        list = findViewById(R.id.listDictionary);
        list.setLayoutManager(new LinearLayoutManager(this));

        wordAdapter = new WordAdapter(words, progress, wordListener());
        phraseAdapter = new PhraseAdapter(new ArrayList<Phrase>(), progress, phraseListener());

        MaterialButtonToggleGroup tabs = findViewById(R.id.toggleDictionary);
        tabs.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (!isChecked) return;
                if (checkedId == R.id.buttonTabIdioms) tab = TAB_IDIOMS;
                else if (checkedId == R.id.buttonTabPhrasals) tab = TAB_PHRASALS;
                else if (checkedId == R.id.buttonTabSaved) tab = TAB_SAVED;
                else tab = TAB_WORDS;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        apply();
    }

    private void apply() {
        String query = search.getText().toString().trim();

        if (tab == TAB_IDIOMS || tab == TAB_PHRASALS) {
            int kind = tab == TAB_IDIOMS ? Phrase.KIND_IDIOM : Phrase.KIND_PHRASAL;
            List<Phrase> shown = new ArrayList<>();
            if (query.isEmpty()) {
                shown.addAll(PhraseBank.byKind(kind));
            } else {
                for (Phrase p : PhraseBank.search(query)) {
                    if (p.kind == kind) shown.add(p);
                }
            }
            if (list.getAdapter() != phraseAdapter) list.setAdapter(phraseAdapter);
            phraseAdapter.replace(shown);
            finishApply(shown.size());
            return;
        }

        words.clear();
        if (tab == TAB_SAVED) {
            for (String id : progress.getFavourites()) {
                try {
                    Word word = WordBank.byId(Integer.parseInt(id));
                    if (word == null) continue;
                    if (query.isEmpty() || matches(word, query)) words.add(word);
                } catch (NumberFormatException ignored) {
                    // A stale id from an older build is simply skipped.
                }
            }
        } else if (query.isEmpty()) {
            List<Word> all = WordBank.byLevel(progress.getLevel());
            words.addAll(all.subList(0, Math.min(60, all.size())));
        } else {
            words.addAll(WordBank.search(query));
            progress.recordLookup();
        }

        if (list.getAdapter() != wordAdapter) list.setAdapter(wordAdapter);
        wordAdapter.notifyDataSetChanged();
        finishApply(words.size());
    }

    private boolean matches(Word word, String query) {
        String q = query.toLowerCase(java.util.Locale.ROOT);
        return word.word.toLowerCase(java.util.Locale.ROOT).contains(q)
                || word.meaning.toLowerCase(java.util.Locale.ROOT).contains(q)
                || word.urdu.contains(query);
    }

    private void finishApply(int count) {
        empty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        empty.setText(tab == TAB_SAVED ? R.string.favourites_empty : R.string.dictionary_empty);
        countLabel.setText(getString(R.string.dictionary_sub, count));
    }

    private WordAdapter.Listener wordListener() {
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
                if (tab == TAB_SAVED) apply();
            }
        };
    }

    private PhraseAdapter.Listener phraseListener() {
        return new PhraseAdapter.Listener() {
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
        };
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        super.onDestroy();
    }
}

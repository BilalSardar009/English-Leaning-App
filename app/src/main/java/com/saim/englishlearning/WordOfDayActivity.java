package com.saim.englishlearning;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.saim.englishlearning.data.PhraseBank;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.WordBank;
import com.saim.englishlearning.model.Phrase;
import com.saim.englishlearning.model.Word;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.ConfettiView;
import com.saim.englishlearning.util.Speaker;

/** Today's word and today's phrase, in one place. */
public class WordOfDayActivity extends AppCompatActivity {

    private ProgressManager progress;
    private Speaker speaker;
    private Word word;
    private Phrase phrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_of_day);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);
        progress.recordWordOfDay();

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        word = WordBank.wordOfTheDay();
        phrase = PhraseBank.phraseOfTheDay();

        TextView wordView = findViewById(R.id.textDayWord);
        TextView meaningView = findViewById(R.id.textDayMeaning);
        TextView urduView = findViewById(R.id.textDayUrdu);
        TextView exampleView = findViewById(R.id.textDayExample);
        TextView levelView = findViewById(R.id.textDayLevel);

        if (word != null) {
            wordView.setText(word.word);
            meaningView.setText(word.meaning);
            exampleView.setText(word.example);
            levelView.setText(Word.levelName(word.level));
            if (progress.isUrduEnabled()) {
                urduView.setVisibility(View.VISIBLE);
                urduView.setText(word.urdu);
            } else {
                urduView.setVisibility(View.GONE);
            }
        }

        TextView phraseView = findViewById(R.id.textDayPhrase);
        TextView phraseMeaning = findViewById(R.id.textDayPhraseMeaning);
        TextView phraseExample = findViewById(R.id.textDayPhraseExample);
        TextView phraseUrdu = findViewById(R.id.textDayPhraseUrdu);

        if (phrase != null) {
            phraseView.setText(phrase.phrase);
            phraseMeaning.setText(phrase.meaning);
            phraseExample.setText(phrase.example);
            if (progress.isUrduEnabled()) {
                phraseUrdu.setVisibility(View.VISIBLE);
                phraseUrdu.setText(phrase.urdu);
            } else {
                phraseUrdu.setVisibility(View.GONE);
            }
        }

        findViewById(R.id.buttonSpeakDay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                if (word != null) speaker.say(word.word + ". " + word.example);
            }
        });

        findViewById(R.id.buttonSpeakDayPhrase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                if (phrase != null) speaker.say(phrase.phrase + ". " + phrase.example);
            }
        });

        findViewById(R.id.buttonLearnDay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                if (word != null) progress.markLearned(word.id);
                if (phrase != null) progress.markPhraseMastered(phrase.id);
                ((ConfettiView) findViewById(R.id.confetti)).burst(60);
                ((TextView) v).setText(R.string.marked_learned);
                v.setEnabled(false);
            }
        });

        Anim.enterAll(90,
                findViewById(R.id.cardDayWord),
                findViewById(R.id.cardDayPhrase),
                findViewById(R.id.buttonLearnDay));
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        super.onDestroy();
    }
}

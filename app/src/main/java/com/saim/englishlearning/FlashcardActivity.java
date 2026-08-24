package com.saim.englishlearning;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.WordBank;
import com.saim.englishlearning.model.Word;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.Speaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Tap to flip, swipe through the level's words. */
public class FlashcardActivity extends AppCompatActivity {

    private ProgressManager progress;
    private Speaker speaker;
    private final List<Word> cards = new ArrayList<>();
    private int index;
    private boolean showingBack;

    private View card;
    private TextView front;
    private TextView backMeaning;
    private TextView backUrdu;
    private TextView backExample;
    private TextView counter;
    private TextView hint;
    private View backGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);

        card = findViewById(R.id.cardFlash);
        front = findViewById(R.id.textCardFront);
        backGroup = findViewById(R.id.layoutCardBack);
        backMeaning = findViewById(R.id.textCardMeaning);
        backUrdu = findViewById(R.id.textCardUrdu);
        backExample = findViewById(R.id.textCardExample);
        counter = findViewById(R.id.textCardCounter);
        hint = findViewById(R.id.textFlipHint);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cards.addAll(WordBank.byLevel(progress.getLevel()));
        if (cards.isEmpty()) cards.addAll(WordBank.all());
        Collections.shuffle(cards);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flip();
            }
        });

        findViewById(R.id.buttonPrevCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                index = (index - 1 + cards.size()) % cards.size();
                showCard();
            }
        });

        findViewById(R.id.buttonNextCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                index = (index + 1) % cards.size();
                showCard();
            }
        });

        findViewById(R.id.buttonSpeakCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                if (!cards.isEmpty()) speaker.say(cards.get(index).word);
            }
        });

        findViewById(R.id.buttonKnowCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cards.isEmpty()) return;
                Anim.pop(v);
                progress.markLearned(cards.get(index).id);
                index = (index + 1) % cards.size();
                showCard();
            }
        });

        showCard();
    }

    private void showCard() {
        if (cards.isEmpty()) {
            front.setText(R.string.no_words);
            return;
        }
        showingBack = false;
        Word word = cards.get(index);
        front.setText(word.word);
        backMeaning.setText(word.meaning);
        backExample.setText(word.example);
        if (progress.isUrduEnabled()) {
            backUrdu.setVisibility(View.VISIBLE);
            backUrdu.setText(word.urdu);
        } else {
            backUrdu.setVisibility(View.GONE);
        }
        backGroup.setVisibility(View.GONE);
        front.setVisibility(View.VISIBLE);
        hint.setText(R.string.flip_hint_front);
        counter.setText(getString(R.string.card_counter, index + 1, cards.size()));
        Anim.enter(card, 0);
    }

    private void flip() {
        if (cards.isEmpty()) return;
        Anim.flip(card, new Runnable() {
            @Override
            public void run() {
                showingBack = !showingBack;
                front.setVisibility(showingBack ? View.GONE : View.VISIBLE);
                backGroup.setVisibility(showingBack ? View.VISIBLE : View.GONE);
                hint.setText(showingBack ? R.string.flip_hint_back : R.string.flip_hint_front);
            }
        });
        progress.recordFlip();
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        super.onDestroy();
    }
}

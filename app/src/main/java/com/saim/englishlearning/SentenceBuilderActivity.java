package com.saim.englishlearning;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.SentenceBank;
import com.saim.englishlearning.model.SentencePair;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.Speaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sentence translation practice. Every Urdu sentence comes with two English
 * versions: a simple one to get the meaning across, and an advanced one that
 * shows how a fluent speaker would put it.
 */
public class SentenceBuilderActivity extends AppCompatActivity {

    public static final String EXTRA_TOPIC = "topic";

    private ProgressManager progress;
    private String topicKey;
    private Speaker speaker;

    private final List<SentencePair> pairs = new ArrayList<>();
    private int index;
    private boolean urduToEnglish = true;

    private TextView counter;
    private TextView promptLabel;
    private TextView promptText;
    private View answerCard;
    private TextView simpleText;
    private TextView advancedText;
    private TextView simpleLabel;
    private TextView advancedLabel;
    private MaterialButton revealButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_builder);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);

        counter = findViewById(R.id.textSentenceCounter);
        promptLabel = findViewById(R.id.textPromptLabel);
        promptText = findViewById(R.id.textPromptSentence);
        answerCard = findViewById(R.id.cardAnswers);
        simpleText = findViewById(R.id.textSimple);
        advancedText = findViewById(R.id.textAdvanced);
        simpleLabel = findViewById(R.id.labelSimple);
        advancedLabel = findViewById(R.id.labelAdvanced);
        revealButton = findViewById(R.id.buttonReveal);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MaterialButtonToggleGroup toggle = findViewById(R.id.toggleDirection);
        toggle.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (!isChecked) return;
                urduToEnglish = checkedId == R.id.buttonUrduToEnglish;
                show();
            }
        });

        revealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                reveal();
            }
        });

        findViewById(R.id.buttonNextSentence).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                index++;
                if (index >= pairs.size()) index = 0;
                show();
            }
        });

        findViewById(R.id.buttonSpeakSimple).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speaker.say(currentPair().simple);
            }
        });

        findViewById(R.id.buttonSpeakAdvanced).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speaker.say(currentPair().advanced);
            }
        });

        topicKey = getIntent().getStringExtra(EXTRA_TOPIC);
        if (topicKey == null) topicKey = SentenceBank.topics().get(0).key;
        SentenceBank.Topic topic = SentenceBank.topic(topicKey);
        ((TextView) findViewById(R.id.textSentenceTitle))
                .setText(topic.emoji + "  " + topic.title);

        pairs.addAll(SentenceBank.byTopic(topicKey, progress.getLevel()));
        Collections.shuffle(pairs);
        show();
    }

    private SentencePair currentPair() {
        return pairs.get(Math.min(index, pairs.size() - 1));
    }

    private void show() {
        if (pairs.isEmpty()) {
            promptText.setText(R.string.no_sentences);
            revealButton.setVisibility(View.GONE);
            return;
        }
        SentencePair pair = currentPair();
        counter.setText(getString(R.string.sentence_counter, index + 1, pairs.size()));

        if (urduToEnglish) {
            promptLabel.setText(R.string.prompt_translate_to_english);
            promptText.setText(pair.urdu);
            simpleLabel.setText(R.string.label_simple_english);
            advancedLabel.setText(R.string.label_advanced_english);
            simpleText.setText(pair.simple);
            advancedText.setText(pair.advanced);
        } else {
            promptLabel.setText(R.string.prompt_translate_to_urdu);
            promptText.setText(pair.simple);
            simpleLabel.setText(R.string.label_urdu);
            advancedLabel.setText(R.string.label_advanced_english);
            simpleText.setText(pair.urdu);
            advancedText.setText(pair.advanced);
        }

        answerCard.setVisibility(View.GONE);
        revealButton.setVisibility(View.VISIBLE);
        Anim.enter(promptText, 0);
    }

    private void reveal() {
        revealButton.setVisibility(View.GONE);
        Anim.fadeIn(answerCard);
        Anim.enter(simpleText, 60);
        Anim.enter(advancedText, 160);
        progress.recordSentence();
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        super.onDestroy();
    }
}

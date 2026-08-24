package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.TenseBank;
import com.saim.englishlearning.model.Tense;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.GeminiClient;
import com.saim.englishlearning.util.OfflineTranslator;
import com.saim.englishlearning.util.Speaker;
import com.saim.englishlearning.util.TenseDetector;

/**
 * Free text translation in either direction. With a free AI key the translation
 * is idiomatic and comes with a real explanation; without one it falls back to
 * the on device model, which is literal but works offline.
 */
public class TranslatorActivity extends AppCompatActivity {

    private ProgressManager progress;
    private Speaker speaker;
    private GeminiClient gemini;
    private OfflineTranslator offline;

    private EditText input;
    private MaterialButton translateButton;
    private View resultCard;
    private View aiHint;
    private TextView statusText;
    private TextView resultText;
    private TextView tenseHint;
    private TextView explanationHeader;
    private TextView explanationText;
    private TextView explanationUrdu;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
        progress = new ProgressManager(this);
        speaker = new Speaker(this);
        gemini = new GeminiClient();
        offline = new OfflineTranslator();

        input = findViewById(R.id.inputTranslate);
        translateButton = findViewById(R.id.buttonTranslate);
        resultCard = findViewById(R.id.cardResult);
        aiHint = findViewById(R.id.textAiHint);
        statusText = findViewById(R.id.textTranslateStatus);
        resultText = findViewById(R.id.textTranslateResult);
        tenseHint = findViewById(R.id.textTenseHint);
        explanationHeader = findViewById(R.id.textExplanationHeader);
        explanationText = findViewById(R.id.textTranslateExplanation);
        explanationUrdu = findViewById(R.id.textTranslateExplanationUrdu);
        spinner = findViewById(R.id.progressTranslating);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                translate();
            }
        });

        findViewById(R.id.buttonSpeakResult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                speaker.say(resultText.getText().toString());
            }
        });

        findViewById(R.id.buttonClearTranslate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText("");
                resultCard.setVisibility(View.GONE);
            }
        });

        aiHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TranslatorActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        aiHint.setVisibility(progress.getGeminiKey().isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void translate() {
        final String text = input.getText().toString().trim();
        if (text.isEmpty()) {
            input.setError(getString(R.string.translator_empty));
            return;
        }

        translateButton.setEnabled(false);
        resultCard.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        resultText.setVisibility(View.GONE);
        clearExplanation();

        String apiKey = progress.getGeminiKey();
        if (!apiKey.isEmpty()) {
            statusText.setText(R.string.translator_ai_working);
            gemini.translate(apiKey, text, new GeminiClient.Callback() {
                @Override
                public void onResult(GeminiClient.Result result) {
                    statusText.setText(R.string.translator_ai_label);
                    showTranslation(text, result.translation);
                    showAiExplanation(result);
                }

                @Override
                public void onError(String message) {
                    statusText.setText(getString(R.string.translator_ai_fallback, message));
                    translateOffline(text);
                }
            });
        } else {
            statusText.setText(R.string.translator_offline_label);
            translateOffline(text);
        }
    }

    private void translateOffline(final String text) {
        boolean urduSource = OfflineTranslator.isUrdu(text);
        offline.translate(text, urduSource, new OfflineTranslator.Callback() {
            @Override
            public void onResult(String translation) {
                showTranslation(text, translation);
                explanationHeader.setVisibility(View.VISIBLE);
                explanationText.setVisibility(View.VISIBLE);
                explanationText.setText(R.string.translator_offline_note);
            }

            @Override
            public void onError(String message) {
                spinner.setVisibility(View.GONE);
                resultText.setVisibility(View.VISIBLE);
                resultText.setText(message);
                translateButton.setEnabled(true);
            }
        });
    }

    private void showTranslation(String source, String translation) {
        spinner.setVisibility(View.GONE);
        translateButton.setEnabled(true);
        resultText.setVisibility(View.VISIBLE);
        resultText.setText(translation == null || translation.trim().isEmpty()
                ? getString(R.string.translator_no_result) : translation.trim());
        Anim.enter(resultText, 0);
        progress.recordTranslation();
        showTense(source, translation);
    }

    /** Detects the tense of whichever side of the pair is English. */
    private void showTense(String source, String translation) {
        String english = OfflineTranslator.isUrdu(source) ? translation : source;
        if (english == null || english.trim().isEmpty()) {
            tenseHint.setVisibility(View.GONE);
            return;
        }
        int index = TenseDetector.detect(english);
        Tense tense = TenseBank.get(index);
        if (tense == null) {
            tenseHint.setVisibility(View.GONE);
            return;
        }
        tenseHint.setVisibility(View.VISIBLE);
        tenseHint.setText(getString(R.string.translator_tense, tense.name, tense.formula));
        Anim.enter(tenseHint, 120);
    }

    private void showAiExplanation(GeminiClient.Result result) {
        if (result.explanation != null && !result.explanation.trim().isEmpty()) {
            explanationHeader.setVisibility(View.VISIBLE);
            explanationText.setVisibility(View.VISIBLE);
            explanationText.setText(result.explanation.trim());
            Anim.enter(explanationText, 180);
        }
        if (progress.isUrduEnabled()
                && result.explanationUrdu != null
                && !result.explanationUrdu.trim().isEmpty()) {
            explanationUrdu.setVisibility(View.VISIBLE);
            explanationUrdu.setText(result.explanationUrdu.trim());
            Anim.enter(explanationUrdu, 240);
        }
    }

    private void clearExplanation() {
        explanationHeader.setVisibility(View.GONE);
        explanationText.setVisibility(View.GONE);
        explanationUrdu.setVisibility(View.GONE);
        tenseHint.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        speaker.shutdown();
        gemini.shutdown();
        offline.close();
        super.onDestroy();
    }
}

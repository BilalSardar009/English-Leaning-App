package com.saim.englishlearning.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/** Thin wrapper around TextToSpeech so screens do not repeat the setup dance. */
public class Speaker {

    private TextToSpeech tts;
    private boolean ready;
    private String pending;

    public Speaker(Context context) {
        tts = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS && tts != null) {
                    int result = tts.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts.setLanguage(Locale.US);
                    }
                    tts.setSpeechRate(0.92f);
                    ready = true;
                    if (pending != null) {
                        String text = pending;
                        pending = null;
                        say(text);
                    }
                }
            }
        });
    }

    public void say(String text) {
        if (text == null || text.trim().isEmpty()) return;
        if (!ready || tts == null) {
            pending = text;
            return;
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "saim-english");
    }

    /** Reads a word slowly, letter friendly, for spelling practice. */
    public void saySlowly(String text) {
        if (tts != null && ready) {
            tts.setSpeechRate(0.6f);
            say(text);
            tts.setSpeechRate(0.92f);
        } else {
            say(text);
        }
    }

    public void stop() {
        if (tts != null) tts.stop();
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        ready = false;
    }
}

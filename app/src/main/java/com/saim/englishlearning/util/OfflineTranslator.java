package com.saim.englishlearning.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

/**
 * On device translation with ML Kit. It works without a key and without the
 * internet once the language pack has been downloaded, but it is literal, so the
 * app only uses it when no AI key is set or the AI call fails.
 */
public class OfflineTranslator {

    public interface Callback {
        void onResult(String translation);

        void onError(String message);
    }

    private Translator urduToEnglish;
    private Translator englishToUrdu;

    private Translator get(boolean urduSource) {
        if (urduSource) {
            if (urduToEnglish == null) {
                urduToEnglish = Translation.getClient(new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.URDU)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build());
            }
            return urduToEnglish;
        }
        if (englishToUrdu == null) {
            englishToUrdu = Translation.getClient(new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.URDU)
                    .build());
        }
        return englishToUrdu;
    }

    public void translate(final String text, final boolean urduSource, final Callback callback) {
        final Translator translator = get(urduSource);
        DownloadConditions conditions = new DownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        translator.translate(text)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        if (callback != null) callback.onResult(result);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if (callback != null) {
                                            callback.onError("Offline translation failed. Please try again.");
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (callback != null) {
                            callback.onError("The language pack could not be downloaded. "
                                    + "Connect to the internet once to set it up.");
                        }
                    }
                });
    }

    public void close() {
        if (urduToEnglish != null) {
            urduToEnglish.close();
            urduToEnglish = null;
        }
        if (englishToUrdu != null) {
            englishToUrdu.close();
            englishToUrdu = null;
        }
    }

    /** True when the text is mostly Urdu or Arabic script. */
    public static boolean isUrdu(String text) {
        if (text == null) return false;
        int urduChars = 0;
        int letters = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetter(c)) {
                letters++;
                Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
                if (block == Character.UnicodeBlock.ARABIC
                        || block == Character.UnicodeBlock.ARABIC_SUPPLEMENT
                        || block == Character.UnicodeBlock.ARABIC_EXTENDED_A
                        || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A
                        || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B) {
                    urduChars++;
                }
            }
        }
        return letters > 0 && urduChars * 2 > letters;
    }
}

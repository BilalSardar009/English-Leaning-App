package com.saim.englishlearning.util;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Talks to the Gemini free tier for translations that read naturally instead of
 * word by word. The learner supplies their own free key in Settings; without a
 * key the app falls back to the offline translator.
 */
public class GeminiClient {

    private static final String MODEL = "gemini-2.5-flash";
    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent?key=";

    public interface Callback {
        void onResult(Result result);

        void onError(String message);
    }

    public static class Result {
        public final String translation;
        public final String explanation;
        public final String explanationUrdu;

        public Result(String translation, String explanation, String explanationUrdu) {
            this.translation = translation;
            this.explanation = explanation;
            this.explanationUrdu = explanationUrdu;
        }
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public void translate(final String apiKey, final String sentence, final Callback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result result = callApi(apiKey, sentence);
                    post(callback, result, null);
                } catch (ApiException e) {
                    post(callback, null, e.getMessage());
                } catch (Exception e) {
                    post(callback, null, "Could not reach the AI service. Check your internet.");
                }
            }
        });
    }

    private void post(final Callback callback, final Result result, final String error) {
        main.post(new Runnable() {
            @Override
            public void run() {
                if (callback == null) return;
                if (result != null) {
                    callback.onResult(result);
                } else {
                    callback.onError(error);
                }
            }
        });
    }

    private static class ApiException extends Exception {
        ApiException(String message) {
            super(message);
        }
    }

    private Result callApi(String apiKey, String sentence) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT + apiKey).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(40000);
        connection.setDoOutput(true);

        String body = buildRequest(sentence);
        try (OutputStream out = connection.getOutputStream()) {
            out.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int code = connection.getResponseCode();
        if (code != 200) {
            drain(connection.getErrorStream());
            connection.disconnect();
            if (code == 400 || code == 401 || code == 403) {
                throw new ApiException("The API key was rejected. Check it in Settings.");
            }
            if (code == 429) {
                throw new ApiException("The free limit is reached for now. Try again in a minute.");
            }
            throw new ApiException("The AI service returned an error (" + code + ").");
        }

        String response = drain(connection.getInputStream());
        connection.disconnect();
        return parse(response);
    }

    private String buildRequest(String sentence) throws Exception {
        String instruction =
                "You are an expert Urdu and English translator helping a Pakistani learner.\n"
                        + "Translate the text below. If it is Urdu, translate into natural English. "
                        + "If it is English, translate into natural Urdu.\n"
                        + "Rules:\n"
                        + "1. Translate the MEANING, never word by word. Idioms, slang and insults must "
                        + "become the natural equivalent a native speaker would actually say.\n"
                        + "2. Keep the same tone and force. A blunt or rude sentence stays blunt.\n"
                        + "3. In the explanation, explain what the whole sentence really means, any idiom "
                        + "or figurative phrase in it, the tense used, and why the translation is worded "
                        + "that way. Do NOT list the words one by one.\n"
                        + "4. Write the explanation in simple English a learner can follow, 2 to 4 short "
                        + "sentences.\n"
                        + "5. Reply with ONLY a JSON object, no code fences, in exactly this shape:\n"
                        + "{\"translation\":\"...\",\"explanation\":\"...\",\"explanation_urdu\":\"...\"}\n\n"
                        + "Text:\n" + sentence;

        JSONObject part = new JSONObject().put("text", instruction);
        JSONObject content = new JSONObject()
                .put("role", "user")
                .put("parts", new JSONArray().put(part));
        JSONObject generation = new JSONObject()
                .put("temperature", 0.3)
                .put("maxOutputTokens", 900);
        return new JSONObject()
                .put("contents", new JSONArray().put(content))
                .put("generationConfig", generation)
                .toString();
    }

    private Result parse(String response) throws Exception {
        JSONObject root = new JSONObject(response);
        JSONArray candidates = root.optJSONArray("candidates");
        if (candidates == null || candidates.length() == 0) {
            throw new ApiException("The AI did not return a translation. Please try again.");
        }
        JSONObject content = candidates.getJSONObject(0).optJSONObject("content");
        if (content == null) {
            throw new ApiException("The AI did not return a translation. Please try again.");
        }
        JSONArray parts = content.optJSONArray("parts");
        StringBuilder text = new StringBuilder();
        if (parts != null) {
            for (int i = 0; i < parts.length(); i++) {
                text.append(parts.getJSONObject(i).optString("text", ""));
            }
        }
        String raw = text.toString().trim();
        if (raw.isEmpty()) {
            throw new ApiException("The AI returned an empty reply. Please try again.");
        }

        // The model is asked for bare JSON, but strip code fences just in case.
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start < 0 || end <= start) {
            return new Result(raw, "", "");
        }
        JSONObject parsed = new JSONObject(raw.substring(start, end + 1));
        return new Result(
                parsed.optString("translation", "").trim(),
                parsed.optString("explanation", "").trim(),
                parsed.optString("explanation_urdu", "").trim());
    }

    private String drain(InputStream stream) {
        if (stream == null) return "";
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception ignored) {
            // An unreadable body still leaves the status code to report.
        }
        return builder.toString();
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}

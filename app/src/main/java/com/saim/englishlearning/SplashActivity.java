package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.GifView;

/**
 * Opening screen. It plays res/raw/splash.gif, then hands over to onboarding on a
 * first run or to the dashboard afterwards. If the GIF cannot be decoded the
 * mascot is shown instead, so the app always starts.
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_MILLIS = 2400L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean moved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        GifView gif = findViewById(R.id.gifSplash);
        ImageView fallback = findViewById(R.id.imageSplashFallback);

        gif.setGifResource(R.raw.splash);
        if (gif.hasFailed()) {
            gif.setVisibility(View.GONE);
            fallback.setVisibility(View.VISIBLE);
            Anim.pulse(fallback);
        }

        Anim.enter(findViewById(R.id.textSplashName), 200);
        Anim.enter(findViewById(R.id.textSplashTagline), 420);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goNext();
            }
        }, SPLASH_MILLIS);

        // Let an impatient learner skip straight through.
        findViewById(R.id.layoutSplashRoot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });
    }

    private void goNext() {
        if (moved) return;
        moved = true;
        handler.removeCallbacksAndMessages(null);

        ProgressManager progress = new ProgressManager(this);
        Class<?> target = progress.isOnboarded() ? MainActivity.class : OnboardingActivity.class;
        startActivity(new Intent(this, target));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}

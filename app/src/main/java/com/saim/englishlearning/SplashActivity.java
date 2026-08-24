package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.util.Anim;
import com.saim.englishlearning.util.BouncingTextView;

/**
 * Opening screen: the app name bounces letter by letter, then it hands over to
 * onboarding on a first run or to the dashboard afterwards. Tapping skips it.
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_MILLIS = 2200L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean moved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        BouncingTextView name = findViewById(R.id.textSplashName);
        name.setText(getString(R.string.app_name));
        name.start();

        View logo = findViewById(R.id.imageSplashLogo);
        Anim.enter(logo, 0);
        Anim.pulse(logo);
        Anim.enter(findViewById(R.id.textSplashTagline), 500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goNext();
            }
        }, SPLASH_MILLIS);

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

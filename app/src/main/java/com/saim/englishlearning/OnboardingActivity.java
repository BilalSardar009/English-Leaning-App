package com.saim.englishlearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.util.Anim;

/** First launch: ask for a name and a starting level, then hand over to the tour. */
public class OnboardingActivity extends AppCompatActivity {

    private ProgressManager progress;
    private EditText nameInput;
    private RadioGroup levelGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        progress = new ProgressManager(this);

        nameInput = findViewById(R.id.inputName);
        levelGroup = findViewById(R.id.groupLevel);

        findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                save();
            }
        });

        Anim.enterAll(90,
                findViewById(R.id.imageMascot),
                findViewById(R.id.textWelcome),
                findViewById(R.id.textWelcomeSub),
                findViewById(R.id.cardName),
                findViewById(R.id.cardLevel),
                findViewById(R.id.buttonStart));
        Anim.pulse(findViewById(R.id.imageMascot));
    }

    private void save() {
        String name = nameInput.getText().toString().trim();
        progress.setUserName(name);

        int checked = levelGroup.getCheckedRadioButtonId();
        int level = 1;
        if (checked == R.id.radioIntermediate) level = 2;
        else if (checked == R.id.radioAdvanced) level = 3;
        else if (checked == R.id.radioExpert) level = 4;
        progress.setLevel(level);
        progress.setOnboarded(true);

        startActivity(new Intent(this, TourActivity.class));
        finish();
    }
}

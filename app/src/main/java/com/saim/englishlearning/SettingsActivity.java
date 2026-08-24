package com.saim.englishlearning;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.saim.englishlearning.data.PhraseBank;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.SentenceBank;
import com.saim.englishlearning.data.WordBank;
import com.saim.englishlearning.notifications.ReminderScheduler;
import com.saim.englishlearning.util.Anim;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private ProgressManager progress;
    private TextView reminderTime;
    private EditText nameInput;
    private EditText geminiInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        progress = new ProgressManager(this);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupName();
        setupLevel();
        setupUrdu();
        setupAnimations();
        setupAiKey();
        setupReminder();
        setupTour();
        setupReset();

        ((TextView) findViewById(R.id.textLibrarySummary)).setText(
                getString(R.string.settings_library_summary,
                        WordBank.size(), PhraseBank.size(), SentenceBank.size()));
    }

    private void setupName() {
        nameInput = findViewById(R.id.inputSettingsName);
        nameInput.setText(progress.getUserName());
        findViewById(R.id.buttonSaveName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                progress.setUserName(nameInput.getText().toString());
                Toast.makeText(SettingsActivity.this, R.string.settings_name_saved,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLevel() {
        RadioGroup group = findViewById(R.id.groupSettingsLevel);
        switch (progress.getLevel()) {
            case 2: group.check(R.id.radioSettingsIntermediate); break;
            case 3: group.check(R.id.radioSettingsAdvanced); break;
            case 4: group.check(R.id.radioSettingsExpert); break;
            default: group.check(R.id.radioSettingsBeginner); break;
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int level = 1;
                if (checkedId == R.id.radioSettingsIntermediate) level = 2;
                else if (checkedId == R.id.radioSettingsAdvanced) level = 3;
                else if (checkedId == R.id.radioSettingsExpert) level = 4;
                progress.setLevel(level);
                Toast.makeText(SettingsActivity.this,
                        getString(R.string.settings_level_saved,
                                com.saim.englishlearning.model.Word.levelName(level)),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupUrdu() {
        MaterialSwitch urdu = findViewById(R.id.switchUrdu);
        urdu.setChecked(progress.isUrduEnabled());
        urdu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                progress.setUrduEnabled(isChecked);
            }
        });
    }

    private void setupAnimations() {
        MaterialSwitch animations = findViewById(R.id.switchAnimations);
        animations.setChecked(progress.areAnimationsEnabled());
        animations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                progress.setAnimationsEnabled(isChecked);
            }
        });
    }

    private void setupAiKey() {
        geminiInput = findViewById(R.id.inputGeminiKey);
        geminiInput.setText(progress.getGeminiKey());
        findViewById(R.id.buttonSaveGeminiKey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                String key = geminiInput.getText().toString().trim();
                progress.setGeminiKey(key);
                Toast.makeText(SettingsActivity.this,
                        key.isEmpty() ? R.string.ai_key_cleared : R.string.ai_key_saved,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupReminder() {
        reminderTime = findViewById(R.id.textReminderTime);
        updateReminderLabel();

        MaterialSwitch reminder = findViewById(R.id.switchReminder);
        reminder.setChecked(progress.isReminderOn());
        reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                progress.setReminderOn(isChecked);
                ReminderScheduler.sync(SettingsActivity.this);
            }
        });

        findViewById(R.id.rowReminderTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(SettingsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                progress.setReminderTime(hourOfDay, minute);
                                updateReminderLabel();
                                ReminderScheduler.sync(SettingsActivity.this);
                            }
                        },
                        progress.getReminderHour(),
                        progress.getReminderMinute(),
                        false).show();
            }
        });
    }

    private void updateReminderLabel() {
        int hour = progress.getReminderHour();
        int minute = progress.getReminderMinute();
        String suffix = hour >= 12 ? "PM" : "AM";
        int display = hour % 12;
        if (display == 0) display = 12;
        reminderTime.setText(String.format(Locale.UK, "%d:%02d %s", display, minute, suffix));
    }

    private void setupTour() {
        findViewById(R.id.rowViewTour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Anim.pop(v);
                startActivity(new Intent(SettingsActivity.this, TourActivity.class));
            }
        });
    }

    private void setupReset() {
        findViewById(R.id.buttonReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle(R.string.settings_reset_title)
                        .setMessage(R.string.settings_reset_message)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.settings_reset_confirm,
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        progress.resetEverything();
                                        Toast.makeText(SettingsActivity.this,
                                                R.string.settings_reset_done,
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                        .show();
            }
        });
    }
}

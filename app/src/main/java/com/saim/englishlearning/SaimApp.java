package com.saim.englishlearning;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.saim.englishlearning.data.PhraseBank;
import com.saim.englishlearning.data.SentenceBank;
import com.saim.englishlearning.data.WordBank;

public class SaimApp extends Application {

    public static final String CHANNEL_ID = "saim_daily_challenge";

    @Override
    public void onCreate() {
        super.onCreate();
        WordBank.init(this);
        SentenceBank.init(this);
        PhraseBank.init(this);
        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Daily Challenge",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Your daily word and practice reminder");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
}

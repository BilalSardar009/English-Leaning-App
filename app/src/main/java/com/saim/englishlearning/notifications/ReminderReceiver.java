package com.saim.englishlearning.notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.saim.englishlearning.MainActivity;
import com.saim.englishlearning.R;
import com.saim.englishlearning.SaimApp;
import com.saim.englishlearning.data.ProgressManager;
import com.saim.englishlearning.data.WordBank;
import com.saim.englishlearning.model.Word;

public class ReminderReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 7301;

    @Override
    public void onReceive(Context context, Intent intent) {
        WordBank.init(context);
        ProgressManager progress = new ProgressManager(context);

        Word word = WordBank.wordOfTheDay();
        String title = context.getString(R.string.notification_title);
        String body;
        if (word != null) {
            body = context.getString(R.string.notification_body, word.word, word.meaning);
        } else {
            body = context.getString(R.string.notification_body_fallback);
        }
        int streak = progress.getStreak();
        if (streak > 1) {
            body = body + " " + context.getString(R.string.notification_streak, streak);
        }

        Intent open = new Intent(context, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pending = PendingIntent.getActivity(context, 0, open, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SaimApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pending);

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException ignored) {
            // The learner has not granted notification permission; nothing to do.
        }
    }
}

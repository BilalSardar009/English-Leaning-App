package com.saim.englishlearning.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.saim.englishlearning.data.ProgressManager;

import java.util.Calendar;

public final class ReminderScheduler {

    private static final int REQUEST_CODE = 4201;

    private ReminderScheduler() {
    }

    private static PendingIntent pendingIntent(Context context) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags);
    }

    /** Reschedules from whatever is stored in settings. Safe to call any time. */
    public static void sync(Context context) {
        ProgressManager progress = new ProgressManager(context);
        if (progress.isReminderOn()) {
            schedule(context, progress.getReminderHour(), progress.getReminderMinute());
        } else {
            cancel(context);
        }
    }

    public static void schedule(Context context, int hour, int minute) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager == null) return;

        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);
        if (target.getTimeInMillis() <= System.currentTimeMillis()) {
            target.add(Calendar.DAY_OF_YEAR, 1);
        }

        manager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                target.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent(context));
    }

    public static void cancel(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) manager.cancel(pendingIntent(context));
    }
}

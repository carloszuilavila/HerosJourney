package dev.carloszuil.herojourney;

import android.app.Application;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import dev.carloszuil.herojourney.worker.DailyResetWorker;

public class HeroJourneyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        scheduleDailyResetAt4AM();
    }

    private void scheduleDailyResetAt4AM() {
        Calendar now = Calendar.getInstance();
        Calendar firstRun = (Calendar) now.clone();
        firstRun.set(Calendar.HOUR_OF_DAY, 4);
        firstRun.set(Calendar.MINUTE, 0);
        firstRun.set(Calendar.SECOND, 0);
        if (firstRun.before(now)) {
            firstRun.add(Calendar.DAY_OF_YEAR, 1);
        }
        long initialDelay = firstRun.getTimeInMillis() - now.getTimeInMillis();

        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        // Aquí añadimos un “flexInterval” de 15 minutos:
        PeriodicWorkRequest resetRequest = new PeriodicWorkRequest.Builder(
                DailyResetWorker.class,
                24, TimeUnit.HOURS,
                15, TimeUnit.MINUTES
        )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        "daily_reset_work",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        resetRequest
                );
    }

}

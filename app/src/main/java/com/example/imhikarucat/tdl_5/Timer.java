package com.example.imhikarucat.tdl_5;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Timer extends AppCompatActivity {
    private Button startButton;
    private Button pauseButton;
    private Button endButton;
    private Chronometer chronometerDisplay;
    private TextView dailyCount;
    private TextView weeklyCount;
    private TextView monthlyCount;
    private long pausePosition;
    private long dailyBreathCount;
    private long weeklyBreathCount;
    private long monthlyBreathCount;
    private long dailyUpdateTime;
    private long weeklyUpdateTime;
    private long monthlyUpdateTime;
    private RelativeLayout viewTotalTime;
    SharedPreferences breathTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        startButton = findViewById(R.id.buttonStart);
        pauseButton = findViewById(R.id.buttonPause);
        endButton = findViewById(R.id.buttonEnd);
        chronometerDisplay = findViewById(R.id.meter);
        viewTotalTime = findViewById(R.id.exerciseCounterView);
        dailyCount = findViewById(R.id.dailyMeter);
        weeklyCount = findViewById(R.id.weeklyMeter);
        monthlyCount = findViewById(R.id.monthlyMeter);

        startButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        endButton.setVisibility(View.VISIBLE);
        chronometerDisplay.setVisibility(View.VISIBLE);
        viewTotalTime.setVisibility(View.VISIBLE);

        breathTimer = getSharedPreferences("BREATHTIMER", MODE_PRIVATE);

        dailyUpdateTime = breathTimer.getLong("daily", 0);
        weeklyUpdateTime = breathTimer.getLong("weekly", 0);
        monthlyUpdateTime = breathTimer.getLong("monthly", 0);

        dailyReset();
        weeklyReset();
        monthlyReset();

        breathTimer.edit().putLong("daily", dailyUpdateTime).apply();
        breathTimer.edit().putLong("weekly", weeklyUpdateTime).apply();
        breathTimer.edit().putLong("monthly", monthlyUpdateTime).apply();

        onStartClicked(startButton);
        onPauseClicked(pauseButton);
        onEndClicked(endButton);
    }

    //get day, week and month to prepare for resetCounter funcs
    public static int getDay(long timestamp){
        Calendar t = Calendar.getInstance();
        t.clear();
        t.setTimeInMillis(timestamp);
        return t.get(Calendar.DATE);
    }

    public int getWeek(long timestamp){
        Calendar t = Calendar.getInstance();
        t.clear();
        t.setTimeInMillis(timestamp);
        return t.get(Calendar.WEEK_OF_MONTH);
    }

    public int getMonth(long timestamp){
        Calendar t = Calendar.getInstance();
        t.clear();
        t.setTimeInMillis(timestamp);
        return t.get(Calendar.MONTH);
    }

    //Reset dailyCounter when time is pass 4am
    public void dailyReset() {
        if (dailyUpdateTime == 0 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 4) {
            dailyBreathCount = 0;
            dailyUpdateTime = Calendar.getInstance().getTimeInMillis();
        }
        else if (Calendar.getInstance().get(Calendar.DATE) != getDay(dailyUpdateTime) && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 4) {
            dailyBreathCount = 0;

            dailyUpdateTime = Calendar.getInstance().getTimeInMillis();
        }
        else {
            dailyBreathCount = breathTimer.getLong("dailyBreathCount", 0);
        }
    }

    //Reset weeklyCounter when index of the week change
    public void weeklyReset() {
        if (weeklyUpdateTime == 0) {
            weeklyBreathCount = 0;
            weeklyUpdateTime = Calendar.getInstance().getTimeInMillis();
        }
        else if (Calendar.getInstance().get(Calendar.MONTH) != getMonth(weeklyUpdateTime)
                || Calendar.getInstance().get(Calendar.WEEK_OF_MONTH) != getWeek(weeklyUpdateTime)) {
            weeklyBreathCount = 0;
            weeklyUpdateTime = Calendar.getInstance().getTimeInMillis();
        }
        else {
            weeklyBreathCount = breathTimer.getLong("weeklyBreathCount", 0);
        }
    }

    //Reset monthlyCounter when index of the month change
    public void monthlyReset() {
        if (monthlyUpdateTime == 0) {
            monthlyBreathCount = 0;
            monthlyUpdateTime = Calendar.getInstance().getTimeInMillis();
        }
        else if (Calendar.getInstance().get(Calendar.MONTH) != getMonth(monthlyUpdateTime)) {
            monthlyBreathCount = 0;
            monthlyUpdateTime = Calendar.getInstance().getTimeInMillis();
        }
        else  {
            monthlyBreathCount = breathTimer.getLong("monthlyBreathCount", 0);
        }
    }

    protected void onDestroy(){
        super.onDestroy();
        breathTimer.edit().putLong("daily", dailyUpdateTime).apply();
        breathTimer.edit().putLong("weekly", weeklyUpdateTime).apply();
        breathTimer.edit().putLong("monthly", monthlyUpdateTime).apply();
    }

    //Convert to humans date time since counter count as millisecs
    public static String convertTime(long seconds) {
        int day = (int) TimeUnit.MILLISECONDS.toDays(seconds);
        long hours = TimeUnit.MILLISECONDS.toHours(seconds) -
                TimeUnit.DAYS.toHours(day);
        long minute = TimeUnit.MILLISECONDS.toMinutes(seconds) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(seconds));
        long second = TimeUnit.MILLISECONDS.toSeconds(seconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seconds));
        return (day + " Day " + hours + " Hour " + minute+ " Minute " + second + " Seconds ");
    }

    public void onStartClicked(View view) {
        if (pausePosition == 0) {
            chronometerDisplay.setBase(SystemClock.elapsedRealtime());
        }
        else {
            chronometerDisplay.setBase(chronometerDisplay.getBase() + SystemClock.elapsedRealtime() - pausePosition);
        }

        chronometerDisplay.start();
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        endButton.setEnabled(true);
    }

    public void onPauseClicked(View view) {
        if (pauseButton.getText() == "Resume") {
            pausePosition = SystemClock.elapsedRealtime();
            chronometerDisplay.setBase(chronometerDisplay.getBase() + SystemClock.elapsedRealtime() - pausePosition);
            chronometerDisplay.start();
            pauseButton.setText("Pause");
        }
        else {
            pausePosition = SystemClock.elapsedRealtime();
            chronometerDisplay.stop();
            pauseButton.setText("Resume");
            pauseButton.setEnabled(true);
            startButton.setEnabled(false);
        }
    }

    public void onEndClicked(View view) {
        chronometerDisplay.stop();
        pausePosition = 0;
        long elapsedTime = SystemClock.elapsedRealtime() - chronometerDisplay.getBase();
        dailyBreathCount += elapsedTime;
        weeklyBreathCount += elapsedTime;
        monthlyBreathCount += elapsedTime;
        dailyCount.setText(convertTime(dailyBreathCount));
        weeklyCount.setText(convertTime(weeklyBreathCount));
        monthlyCount.setText(convertTime(monthlyBreathCount));

        chronometerDisplay.setBase(SystemClock.elapsedRealtime());
        pauseButton.setText("Pause");
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        endButton.setEnabled(false);

        // When end button clicked, timer data will stack to counter prefs
        SharedPreferences.Editor timeCounterUpdate = breathTimer.edit();

        timeCounterUpdate.putLong("dailyBreathCount", dailyBreathCount);
        timeCounterUpdate.putLong("weeklyBreathCount", weeklyBreathCount);
        timeCounterUpdate.putLong("monthlyBreathCount", monthlyBreathCount);

        timeCounterUpdate.apply();

    }
}

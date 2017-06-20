package ghost.gboardgametracker.ui.playtime;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ghost.gboardgametracker.R;

import java.util.concurrent.TimeUnit;

public class PlayTimeActivity extends Activity {

    private static final String EXTRA_KEY_DURATION = "duration";
    private static final String SAVE_STATE_TIME_START = "time_start";

    private static final long DEFAULT_TIMER = 0; // time out

    @BindView(R.id.timer_text)
    TextView mTimerTextView;

    @BindView(R.id.close_btn)
    ImageView mCloseBtn;

    private CountDownTimer mCountDownTimer;
    private MediaPlayer mTimeoutSound;
    private Vibrator mTimeoutVibrator;
    private long mTimeStart;
    private boolean mActive;

    public static void createInstance(Activity fromActivity, long duration) {
        Intent intent = new Intent(fromActivity, PlayTimeActivity.class);
        intent.putExtra(EXTRA_KEY_DURATION, duration);
        fromActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_time);
        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        long duration = getIntent().getLongExtra(EXTRA_KEY_DURATION, DEFAULT_TIMER);

        createAssets();

        if (savedInstanceState != null) {
            mTimeStart = savedInstanceState.getLong(SAVE_STATE_TIME_START);
        } else {
            mTimeStart = System.currentTimeMillis();
        }

        startCountDown(duration - (System.currentTimeMillis() - mTimeStart) );

        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAssets();
                finish();
            }
        });
    }

    private void startCountDown(long duration) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        if (duration > 0) {
            mCountDownTimer = new CountDownTimer(duration + 1000, 1000) {
                @Override
                public void onTick(long millis) {
                    long minute = TimeUnit.MILLISECONDS.toMinutes(millis);
                    long second = TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(minute);

                    mTimerTextView.setText(String.format("%d:%02d", minute, second));
                }

                @Override
                public void onFinish() {
                    displayTimeout();
                }
            };
            mCountDownTimer.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActive = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(SAVE_STATE_TIME_START, mTimeStart);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        removeAssets();
        super.onDestroy();
    }

    private void createAssets() {
        if (mTimeoutSound == null) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mTimeoutSound = MediaPlayer.create(getApplicationContext(), notification);
            mTimeoutSound.setLooping(true);
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
        }

        if (mTimeoutVibrator == null) {
            mTimeoutVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    private void removeAssets() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        if (mTimeoutSound != null) {
            mTimeoutSound.release();
        }
        if (mTimeoutVibrator != null) {
            mTimeoutVibrator.cancel();
        }
    }

    private void displayTimeout() {
        mTimerTextView.setText(R.string.time_out);
        if (mActive) {
            mTimeoutSound.start();

            /**
             * vibrate for 1 second
             * sleep for 2 seconds
             */
            long[] pattern = {0, 1000, 2000};
            mTimeoutVibrator.vibrate(pattern, 0);
        } else {
            showNotification();
        }
    }

    private void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Time Out")
                        .setContentText("Play turn is ended!");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, PlayTimeActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults|= Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(1, notification);
    }
}
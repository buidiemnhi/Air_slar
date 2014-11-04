package com.evilgeniustechnologies.airsla.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.*;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import com.evilgeniustechnologies.airsla.PodcastBroadcastingActivity;
import com.evilgeniustechnologies.airsla.R;
import com.evilgeniustechnologies.airsla.utilities.DialogManager;
import com.evilgeniustechnologies.airsla.utilities.UserPreferences;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by benjamin on 3/8/14.
 */
public class StreamingMediaService extends Service
        implements MediaPlayer.OnPreparedListener {
    private static final int BROADCAST_ID = 1;
    private static final int RESUME_ID = 2;
    private static final int REWIND_ID = 3;
    private static final int PLAY_ID = 4;
    private static final int NEXT_ID = 5;
    private static final int PREVIOUS_ID = 6;

    public static final String BROADCASTS = "BROADCASTS";
    public static final String INDEX = "INDEX";

    public static final String TASK = "TASK";
    public static final String PLAY_URL = "URL";
    public static final String PLAY = "PLAY";
    public static final String NEXT = "NEXT";
    public static final String PREVIOUS = "PREVIOUS";
    public static final String REWIND = "REWIND";
    public static final String HIGH = "HIGH SPEED";
    public static final String NORMAL = "NORMAL SPEED";
    public static final String FAVORITE = "FAVORITE";
    public static final String RESUME = "RESUME";
    public static final String PENDING = "PENDING";
    public static final String UPDATE = "UPDATE";

    public static final String START_OF_LIST = "START_OF_LIST";
    public static final String END_OF_LIST = "END_OF_LIST";
    public static final String IS_PLAYING = "IS_PLAYING";

    private ServiceHandler serviceHandler;
    //    private FastForwardHandler fastForward;
    private MediaPlayer mediaPlayer = null;
    private ResultReceiver receiver;
//    private int current;
//    private int duration;

    private ArrayList<UserPreferences.Broadcast> broadcasts;
    private int index;
    private String task;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            handleIntent((Intent) msg.obj);
        }
    }

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);

        HandlerThread thread = new HandlerThread("Playing",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper looper = thread.getLooper();
        serviceHandler = new ServiceHandler(looper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
//        if (fastForward != null) {
//            fastForward.removeCallbacks(fastForward.runnable);
//            fastForward = null;
//        }
        mediaPlayer.release();
        mediaPlayer = null;
        stopForeground(true);
        Log.e("Service destroyed", "executed");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            Bundle bundle = new Bundle();
            bundle.putString(TASK, PLAY_URL);
            bundle.putParcelable(BROADCASTS, broadcasts.get(index));
            if (index <= 0) {
                bundle.putBoolean(START_OF_LIST, true);
            } else {
                bundle.putBoolean(START_OF_LIST, false);
            }
            if (index >= broadcasts.size() - 1) {
                bundle.putBoolean(END_OF_LIST, true);
            } else {
                bundle.putBoolean(END_OF_LIST, false);
            }
            // Reset Alert
            DialogManager.reset();
            receiver.send(0, bundle);
            // Display notification
            showNotification();
        }
    }

    private void showNotification() {
        Intent resumeIntent = new Intent(this, PodcastBroadcastingActivity.class);
        resumeIntent.putExtra(TASK, RESUME);
        resumeIntent.putParcelableArrayListExtra(BROADCASTS, broadcasts);
        resumeIntent.putExtra(INDEX, index);

        Intent playIntent = new Intent(this, StreamingMediaService.class);
        playIntent.putExtra(ServiceReceiver.RECEIVER, receiver);
        playIntent.putExtra(StreamingMediaService.TASK, StreamingMediaService.PLAY);

        Intent nextIntent = new Intent(this, StreamingMediaService.class);
        nextIntent.putExtra(ServiceReceiver.RECEIVER, receiver);
        nextIntent.putExtra(StreamingMediaService.TASK, StreamingMediaService.NEXT);

        Intent previousIntent = new Intent(this, StreamingMediaService.class);
        previousIntent.putExtra(ServiceReceiver.RECEIVER, receiver);
        previousIntent.putExtra(StreamingMediaService.TASK, StreamingMediaService.PREVIOUS);

        Intent rewindIntent = new Intent(this, StreamingMediaService.class);
        rewindIntent.putExtra(ServiceReceiver.RECEIVER, receiver);
        rewindIntent.putExtra(StreamingMediaService.TASK, StreamingMediaService.REWIND);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, RESUME_ID, resumeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent playPendingIntent = PendingIntent.getService(this, PLAY_ID, playIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, NEXT_ID, nextIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent rewindPendingIntent = PendingIntent.getService(this, REWIND_ID, rewindIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent previousPendingIntent = PendingIntent.getService(this, PREVIOUS_ID, previousIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        remoteViews.setTextViewText(R.id.player_title, broadcasts.get(index).heading);
        remoteViews.setOnClickPendingIntent(R.id.player_icon, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.player_rewind, rewindPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.player_play, playPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.player_next, nextPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.player_previous, previousPendingIntent);

        if (mediaPlayer.isPlaying()) {
            remoteViews.setImageViewResource(R.id.player_play, R.drawable.notification_pause_button);
        } else {
            remoteViews.setImageViewResource(R.id.player_play, R.drawable.notification_play_button);
        }

        if (index <= 0) {
            remoteViews.setImageViewResource(R.id.player_previous, R.drawable.ic_action_previous_pressed);
        } else {
            remoteViews.setImageViewResource(R.id.player_previous, R.drawable.notification_previous_button);
        }
        if (index >= broadcasts.size() - 1) {
            remoteViews.setImageViewResource(R.id.player_next, R.drawable.ic_action_next_pressed);
        } else {
            remoteViews.setImageViewResource(R.id.player_next, R.drawable.notification_next_button);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification)
                        .setContent(remoteViews)
                        .setOngoing(true);

        startForeground(BROADCAST_ID, builder.build());
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getStringExtra(TASK) == null) {
            return;
        }
        Log.e("new receiver", "executed");
        receiver = intent.getParcelableExtra(ServiceReceiver.RECEIVER);
        task = intent.getStringExtra(TASK);
        if (task != null) {
            Log.e("task", task);
            if (task.equals(PLAY_URL)) {
                // Tell the activity to wait
//                Bundle bundle = new Bundle();
//                bundle.putString(TASK, PENDING);
//                receiver.send(0, bundle);
//                if (fastForward != null) {
//                    fastForward.removeCallbacks(fastForward.runnable);
//                    fastForward = null;
//                }
                try {
                    // Populate contents
                    broadcasts = intent.getParcelableArrayListExtra(BROADCASTS);
                    index = intent.getIntExtra(INDEX, 0);
                    // Play media
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(broadcasts.get(index).link);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (task.equals(PLAY)) {
                Bundle bundle = new Bundle();
                bundle.putString(TASK, task);
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    bundle.putBoolean(IS_PLAYING, true);
                } else {
                    mediaPlayer.pause();
                    bundle.putBoolean(IS_PLAYING, false);
                }
                // Send back to activity
                receiver.send(0, bundle);
            } else if (task.equals(NEXT)) {
                // Tell the activity to wait
                DialogManager.prepareProgress();
                Bundle bundle = new Bundle();
                bundle.putString(TASK, PENDING);
                receiver.send(0, bundle);
                try {
                    if (index < broadcasts.size() - 1) {
                        index++;
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(broadcasts.get(index).link);
                        mediaPlayer.prepareAsync();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (task.equals(PREVIOUS)) {
                // Tell the activity to wait
                DialogManager.prepareProgress();
                Bundle bundle = new Bundle();
                bundle.putString(TASK, PENDING);
                receiver.send(0, bundle);
                try {
                    if (index > 0) {
                        index--;
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(broadcasts.get(index).link);
                        mediaPlayer.prepareAsync();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (task.equals(REWIND)) {
                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayer.getCurrentPosition() < 15000) {
                        mediaPlayer.seekTo(0);
                    } else {
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 15000);
                    }
                }
            } else if (task.equals(HIGH)) {
                if (mediaPlayer.isPlaying()) {
//                    if (fastForward == null) {
//                        current = mediaPlayer.getCurrentPosition();
//                        duration = mediaPlayer.getDuration();
//                        fastForward = new FastForwardHandler(mediaPlayer, 150, 200);
//                    }
//                    fastForward.execute();
                }
            } else if (task.equals(NORMAL)) {
                if (mediaPlayer.isPlaying()) {
//                    if (fastForward != null) {
//                        fastForward.removeCallbacks(fastForward.runnable);
//                        fastForward = null;
//                    }
                }
            } else if (task.equals(FAVORITE)) {
                Bundle bundle = new Bundle();
                bundle.putString(TASK, task);
                bundle.putBoolean(
                        FAVORITE,
                        UserPreferences.saveBroadcast(
                                getApplicationContext(),
                                broadcasts.get(index).link, broadcasts.get(index).title,
                                broadcasts.get(index).heading, broadcasts.get(index).description,
                                broadcasts.get(index).author, broadcasts.get(index).date,
                                broadcasts.get(index).duration
                        )
                );
                receiver.send(0, bundle);
            } else if (task.equals(UPDATE)) {
                Bundle bundle = new Bundle();
                bundle.putString(TASK, task);
                bundle.putParcelable(BROADCASTS, broadcasts.get(index));
                if (index <= 0) {
                    bundle.putBoolean(START_OF_LIST, true);
                } else {
                    bundle.putBoolean(START_OF_LIST, false);
                }
                if (index >= broadcasts.size() - 1) {
                    bundle.putBoolean(END_OF_LIST, true);
                } else {
                    bundle.putBoolean(END_OF_LIST, false);
                }
                receiver.send(0, bundle);
            }
        }
        // Display notification
        showNotification();
    }

//    private class FastForwardHandler extends Handler {
//        private Runnable runnable;
//        private int delay;
//
//        public FastForwardHandler(final MediaPlayer mediaPlayer,
//                                  final int delay,
//                                  final int forward) {
//            current = mediaPlayer.getCurrentPosition();
//            duration = mediaPlayer.getDuration();
//            runnable = new Runnable() {
//                @Override
//                public void run() {
////                    if (mediaPlayer.getCurrentPosition() + forward < mediaPlayer.getDuration()) {
////                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + forward);
////                    }
////                    FastForwardHandler.this.postDelayed(this, delay);
//                    try {
//                        if (mediaPlayer.isPlaying()) {
//                            if (current + forward < duration) {
//                                mediaPlayer.seekTo(current + forward);
//                                current += forward;
//                            }
//                        }
//                        FastForwardHandler.this.postDelayed(this, delay);
//                    } catch (IllegalStateException ex) {
//                        Log.e("Illegal state", "thrown");
//                    }
//                }
//            };
//            this.delay = delay;
//        }
//
//        public void execute() {
//            this.postDelayed(runnable, delay);
//        }
//    }
}

package com.evilgeniustechnologies.airsla;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.evilgeniustechnologies.airsla.base.SearchableListViewActivity;
import com.evilgeniustechnologies.airsla.base.ServiceActivity;
import com.evilgeniustechnologies.airsla.services.ServiceReceiver;
import com.evilgeniustechnologies.airsla.services.StreamingMediaService;
import com.evilgeniustechnologies.airsla.utilities.DataValidator;
import com.evilgeniustechnologies.airsla.utilities.DialogManager;
import com.evilgeniustechnologies.airsla.utilities.UserPreferences;

import java.util.ArrayList;

/**
 * Created by benjamin on 2/26/14.
 */
public class PodcastBroadcastingActivity extends ServiceActivity {
    private static final String SPEED = "SPEED";
    private ArrayList<UserPreferences.Broadcast> broadcasts;
    private int index;
    private TextView textView;
    private ImageView playView;
    private ImageView nextView;
    private ImageView previousView;
    //    private ImageView speedView;
    private boolean isFastForward = false;
    private boolean isPlayed = true;
    private boolean isStartOfList = false;
    private boolean isEndOfList = false;
    private String task = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.podcast_broadcasting_layout);

        // Instantiates the buttons
        playView = (ImageView) findViewById(R.id.play_pause);
        nextView = (ImageView) findViewById(R.id.next);
        previousView = (ImageView) findViewById(R.id.previous);
        textView = (TextView) findViewById(R.id.broadcast_title);

        // Populates contents
        Bundle bundle = getIntent().getExtras();
        if (savedInstanceState != null) {
            playView.setImageResource(R.drawable.pause_button);
            broadcasts = savedInstanceState.getParcelableArrayList(SearchableListViewActivity.CONTENTS);
            index = savedInstanceState.getInt(PodCastDetailsActivity.INDEX);
            isFastForward = savedInstanceState.getBoolean(SPEED);
        } else if (bundle != null) {
            task = bundle.getString(StreamingMediaService.TASK);
            Log.e("get bundle", "executed");
            if (task != null) {
                Log.e("resume task", task);
                broadcasts = bundle.getParcelableArrayList(StreamingMediaService.BROADCASTS);
                index = bundle.getInt(StreamingMediaService.INDEX);
            } else {
                broadcasts = bundle.getParcelableArrayList(SearchableListViewActivity.CONTENTS);
                index = bundle.getInt(PodCastDetailsActivity.INDEX);
            }
        }

        // Display dialog if any
        DialogManager.showDialog(this);
        // Set title
        setTitle(broadcasts.get(index).title);
        // Set label
        textView.setText(broadcasts.get(index).heading);
        if (task != null) {
            // Checks start or end of list
            checkStartOrEndOfList();
            // Register with the new receiver
            Log.e("register new receiver", "executed");
            Intent service = new Intent(this, StreamingMediaService.class);
            service.putExtra(ServiceReceiver.RECEIVER, receiver);
            service.putExtra(StreamingMediaService.TASK, StreamingMediaService.RESUME);
            startService(service);
        } else if (savedInstanceState == null) {
            // Check for internet connection
            if (DataValidator.checkInternetConnection(this)) {
                // Send command to service
                Intent service = new Intent(this, StreamingMediaService.class);
                service.putExtra(ServiceReceiver.RECEIVER, receiver);
                service.putExtra(StreamingMediaService.TASK, StreamingMediaService.PLAY_URL);
                service.putExtra(StreamingMediaService.BROADCASTS, broadcasts);
                service.putExtra(StreamingMediaService.INDEX, index);
                startService(service);
                // Checks start or end of list
                checkStartOrEndOfList();
                // Check fast forward
//                if (isFastForward) {
//                    speedView.setImageResource(R.drawable.normal_speed_button);
//                } else {
//                    speedView.setImageResource(R.drawable.high_speed_button);
//                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(SearchableListViewActivity.CONTENTS, broadcasts);
        outState.putInt(PodCastDetailsActivity.INDEX, index);
        outState.putBoolean(SPEED, isFastForward);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent service = new Intent(this, StreamingMediaService.class);
        service.putExtra(ServiceReceiver.RECEIVER, receiver);
        service.putExtra(StreamingMediaService.TASK, StreamingMediaService.UPDATE);
        startService(service);
    }

    @Override
    public void onReceiveResult(int task, Bundle data) {
        String job = data.getString(StreamingMediaService.TASK);
        Log.e("Job", job);
        if (job != null) {
            if (job.equals(StreamingMediaService.PENDING)) {
                // Display progress dialog
                DialogManager.showProgress(this);
            } else if (job.equals(StreamingMediaService.UPDATE)) {
                // Get data
                UserPreferences.Broadcast broadcast =
                        data.getParcelable(StreamingMediaService.BROADCASTS);
                if (broadcast != null) {
                    // Set title
                    setTitle(broadcast.title);
                    // Set label
                    textView.setText(broadcast.heading);
                }
                // Set play
                playView.setImageResource(R.drawable.pause_button);
                // Check start of list
                if (data.getBoolean(StreamingMediaService.START_OF_LIST)) {
                    isStartOfList = true;
                    disable(previousView);
                } else {
                    isStartOfList = false;
                    Log.e("enable", "executed");
                    enable(previousView);
                }
                // Check end of list
                if (data.getBoolean(StreamingMediaService.END_OF_LIST)) {
                    isEndOfList = true;
                    disable(nextView);
                } else {
                    isEndOfList = false;
                    enable(nextView);
                }
            } else if (job.equals(StreamingMediaService.FAVORITE)) {
                boolean result = data.getBoolean(StreamingMediaService.FAVORITE);
                if (result) {
                    // Display notification
                    Toast toast = Toast.makeText(this, R.string.broadcast_added, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, -100);
                    toast.show();
                } else {
                    // Display error
                    DialogManager.showDialog(this, DialogManager.Alerts.BROADCAST_EXIST);
                }
            } else if (job.equals(StreamingMediaService.PLAY)) {
                boolean result = data.getBoolean(StreamingMediaService.IS_PLAYING);
                if (result) {
                    // Set pause button
                    playView.setImageResource(R.drawable.pause_button);
                    isPlayed = true;
                } else {
                    // Set play button
                    playView.setImageResource(R.drawable.play_button);
                    isPlayed = false;
                }
            } else {
                Log.e("set title", "executed");
                // Dismiss progress
                DialogManager.closeProgress();
                // Get data
                UserPreferences.Broadcast broadcast =
                        data.getParcelable(StreamingMediaService.BROADCASTS);
                if (broadcast != null) {
                    // Set title
                    setTitle(broadcast.title);
                    // Set label
                    textView.setText(broadcast.heading);
                }
                // Set play
                playView.setImageResource(R.drawable.pause_button);
                // Check start of list
                if (data.getBoolean(StreamingMediaService.START_OF_LIST)) {
                    isStartOfList = true;
                    disable(previousView);
                } else {
                    isStartOfList = false;
                    Log.e("enable", "executed");
                    enable(previousView);
                }
                // Check end of list
                if (data.getBoolean(StreamingMediaService.END_OF_LIST)) {
                    isEndOfList = true;
                    disable(nextView);
                } else {
                    isEndOfList = false;
                    enable(nextView);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Destroy media service
        if (!isPlayed) {
            Intent service = new Intent(this, StreamingMediaService.class);
            stopService(service);
        }
        // Return to previous activity
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Destroy media service
                if (!isPlayed) {
                    Intent service = new Intent(this, StreamingMediaService.class);
                    stopService(service);
                }
                // Return to previous activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkStartOrEndOfList() {
        if (index <= 0) {
            disable(previousView);
            previousView.setEnabled(false);
        }
        if (index >= broadcasts.size() - 1) {
            disable(nextView);
            nextView.setEnabled(false);
        }
    }

    public void onPlay(View v) {
        Intent service = new Intent(this, StreamingMediaService.class);
        service.putExtra(ServiceReceiver.RECEIVER, receiver);
        service.putExtra(StreamingMediaService.TASK, StreamingMediaService.PLAY);
        startService(service);
    }

    public void onPrevious(View v) {
        if (isStartOfList) {
            return;
        }
        isFastForward = false;
        // Send command to service
        Intent service = new Intent(this, StreamingMediaService.class);
        service.putExtra(ServiceReceiver.RECEIVER, receiver);
        service.putExtra(StreamingMediaService.TASK, StreamingMediaService.PREVIOUS);
        startService(service);
        // Check fast forward
//            if (isFastForward) {
//                speedView.setImageResource(R.drawable.normal_speed_button);
//            } else {
//                speedView.setImageResource(R.drawable.high_speed_button);
//            }
//            playView.setImageResource(R.drawable.pause_button);
//        }
    }

    public void onNext(View v) {
        if (isEndOfList) {
            return;
        }
        isFastForward = false;
        // Send command to service
        Intent service = new Intent(this, StreamingMediaService.class);
        service.putExtra(ServiceReceiver.RECEIVER, receiver);
        service.putExtra(StreamingMediaService.TASK, StreamingMediaService.NEXT);
        startService(service);
        // Check fast forward
//            if (isFastForward) {
//                speedView.setImageResource(R.drawable.normal_speed_button);
//            } else {
//                speedView.setImageResource(R.drawable.high_speed_button);
//            }
//            playView.setImageResource(R.drawable.pause_button);
//        }
    }

    public void onRewind(View v) {
        // Rewind media service
        Intent service = new Intent(this, StreamingMediaService.class);
        service.putExtra(ServiceReceiver.RECEIVER, receiver);
        service.putExtra(StreamingMediaService.TASK, StreamingMediaService.REWIND);
        startService(service);
    }

    public void onSpeed(View v) {
        if (!isFastForward) {
            // Set label
//            speedView.setImageResource(R.drawable.normal_speed_button);
            // Fast forward media service
            Intent service = new Intent(this, StreamingMediaService.class);
            service.putExtra(ServiceReceiver.RECEIVER, receiver);
            service.putExtra(StreamingMediaService.TASK, StreamingMediaService.HIGH);
            startService(service);
            isFastForward = true;
        } else {
            // Set label
//            speedView.setImageResource(R.drawable.high_speed_button);
            // Play normal
            Intent service = new Intent(this, StreamingMediaService.class);
            service.putExtra(ServiceReceiver.RECEIVER, receiver);
            service.putExtra(StreamingMediaService.TASK, StreamingMediaService.NORMAL);
            startService(service);
            isFastForward = false;
        }
    }

    public void onAddFavorite(View v) {
        Intent service = new Intent(this, StreamingMediaService.class);
        service.putExtra(ServiceReceiver.RECEIVER, receiver);
        service.putExtra(StreamingMediaService.TASK, StreamingMediaService.FAVORITE);
        startService(service);
    }

    private void enable(View view) {
        view.setEnabled(true);
        setOpacity(view, 0.5f, 1f);
    }

    private void disable(View view) {
        view.setEnabled(false);
        setOpacity(view, 1f, 0.5f);
    }

    private void setOpacity(View view, float fromAlpha, float toAlpha) {
        AlphaAnimation alpha = new AlphaAnimation(fromAlpha, toAlpha);
        alpha.setDuration(0);
        alpha.setFillAfter(true);
        view.startAnimation(alpha);
    }
}
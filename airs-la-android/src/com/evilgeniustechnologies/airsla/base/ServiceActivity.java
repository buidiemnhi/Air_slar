package com.evilgeniustechnologies.airsla.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import com.evilgeniustechnologies.airsla.R;
import com.evilgeniustechnologies.airsla.services.ServiceReceiver;
import com.evilgeniustechnologies.airsla.services.StreamingMediaService;
import com.evilgeniustechnologies.airsla.utilities.DialogManager;

/**
 * Created by benjamin on 3/11/14.
 */
public abstract class ServiceActivity extends ActionBarActivity
        implements ServiceReceiver.Receiver {
    protected ServiceReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_Light_DarkActionBar);
        super.onCreate(savedInstanceState);

        // Modify Action Bar to add 'Navigating Up' behavior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Enable rotation if the device is tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Restore after orientation
        if (savedInstanceState != null) {
            receiver = savedInstanceState.getParcelable(ServiceReceiver.RECEIVER);
        } else if (getIntent().getExtras() == null ||
                getIntent().getExtras().getString(StreamingMediaService.TASK) == null) {
            Log.e("base reset", "executed");
            DialogManager.reset();
        }

        // Register receiver
        if (receiver == null) {
            receiver = new ServiceReceiver(new Handler(), this);
        }
        receiver.setReceiver(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(ServiceReceiver.RECEIVER, receiver);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Dismiss progress on rotation
        DialogManager.destroyProgress();
        // Unregister receiver
        receiver.setReceiver(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register receiver
        receiver.setReceiver(this);
        // Restore progress
        DialogManager.restoreProgress(this);
    }

    @Override
    public abstract void onReceiveResult(int task, Bundle data);
}

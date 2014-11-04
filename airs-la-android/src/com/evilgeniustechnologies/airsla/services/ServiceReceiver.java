package com.evilgeniustechnologies.airsla.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by benjamin on 3/11/14.
 */
public class ServiceReceiver extends ResultReceiver {
    public static final String RECEIVER = "RECEIVER";
    private Receiver receiver;

    public ServiceReceiver(Handler handler, Receiver receiver) {
        super(handler);
        this.receiver = receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }

    public interface Receiver {
        public void onReceiveResult(int task, Bundle data);
    }
}

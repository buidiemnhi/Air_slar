package com.evilgeniustechnologies.airsla.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by benjamin on 2/20/14.
 */
public class ErrorDialog extends DialogFragment {
    private String message;
    private String title;

    public ErrorDialog(String message, String title) {
        this.message = message;
        this.title = title;
    }

    public void onClicked() {
        // Caller overrides here
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogManager.reset();
                        onClicked();
                    }
                });
        setCancelable(false);
        // Retain when rotate
        setRetainInstance(true);
        return builder.create();
    }
}

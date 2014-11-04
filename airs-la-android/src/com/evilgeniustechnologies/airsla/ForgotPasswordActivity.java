package com.evilgeniustechnologies.airsla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.evilgeniustechnologies.airsla.base.ServiceActivity;
import com.evilgeniustechnologies.airsla.services.ServiceReceiver;
import com.evilgeniustechnologies.airsla.services.SubmitService;
import com.evilgeniustechnologies.airsla.utilities.DataValidator;
import com.evilgeniustechnologies.airsla.utilities.DialogManager;

/**
 * Created by benjamin on 2/19/14.
 */
public class ForgotPasswordActivity extends ServiceActivity {
    private String email;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_layout);

        // Show dialog if any
        DialogManager.showDialog(this);
    }

    public void submit(View v) {
        EditText emailField = (EditText) findViewById(R.id.forgot_email);

        email = emailField.getText().toString();

        if (DataValidator.validateForgotPassword(this, email)) {
            // Start service
            Intent service = new Intent(this, SubmitService.class);
            service.putExtra(ServiceReceiver.RECEIVER, receiver);
            service.putExtra(SubmitService.TASK, SubmitService.FORGOT_PASS);
            service.putExtra(SubmitService.EMAIL, email);
            startService(service);
        }
    }

    @Override
    public void onReceiveResult(int task, Bundle data) {
        DataValidator.processForgotPasswordResponse(this,
                data.getString(SubmitService.RESPONSE));
    }
}
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
public class ChangePasswordActivity extends ServiceActivity {
    private String email = null;
    private String oldPass = null;
    private String newPass = null;
    private String newPassConfirm = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);

        // Display dialog if any
        DialogManager.showDialog(this);
    }

    public void submit(View v) {
        EditText emailField = (EditText) findViewById(R.id.current_email);
        EditText oldPassField = (EditText) findViewById(R.id.current_password);
        EditText newPassField = (EditText) findViewById(R.id.new_password);
        EditText newPassConfirmField = (EditText) findViewById(R.id.confirm_password);

        email = emailField.getText().toString();
        oldPass = oldPassField.getText().toString();
        newPass = newPassField.getText().toString();
        newPassConfirm = newPassConfirmField.getText().toString();

        if (DataValidator.validateChangePassword(this,
                email, oldPass, newPass, newPassConfirm)) {
            // Start service
            Intent service = new Intent(this, SubmitService.class);
            service.putExtra(ServiceReceiver.RECEIVER, receiver);
            service.putExtra(SubmitService.TASK, SubmitService.CHANGE_PASS);
            service.putExtra(SubmitService.EMAIL, email);
            service.putExtra(SubmitService.PASSWORD, oldPass);
            service.putExtra(SubmitService.NEW_PASS, newPass);
            service.putExtra(SubmitService.NEW_PASS_CONFIRM, newPassConfirm);
            startService(service);
        }
    }

    @Override
    public void onReceiveResult(int task, Bundle data) {
        DataValidator.processChangePasswordResponse(this,
                data.getString(SubmitService.RESPONSE),
                data.getString(SubmitService.NEW_PASS));
    }
}
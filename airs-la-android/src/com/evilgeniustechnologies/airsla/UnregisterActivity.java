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
public class UnregisterActivity extends ServiceActivity {
    private String email = null;
    private String password = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unregister_layout);

        // Display dialog if any
        DialogManager.showDialog(this);
    }

    public void unregister(View v) {
        EditText emailField = (EditText) findViewById(R.id.unregister_email);
        EditText passField = (EditText) findViewById(R.id.unregister_password);

        email = emailField.getText().toString();
        password = passField.getText().toString();

        if (DataValidator.validateUnregister(this, email, password)) {
            Intent service = new Intent(this, SubmitService.class);
            service.putExtra(ServiceReceiver.RECEIVER, receiver);
            service.putExtra(SubmitService.TASK, SubmitService.UNREGISTER);
            service.putExtra(SubmitService.EMAIL, email);
            service.putExtra(SubmitService.PASSWORD, password);
            startService(service);
        }
    }

    @Override
    public void onReceiveResult(int task, Bundle data) {
        DataValidator.processUnregisterResponse(this,
                data.getString(SubmitService.RESPONSE));
    }
}
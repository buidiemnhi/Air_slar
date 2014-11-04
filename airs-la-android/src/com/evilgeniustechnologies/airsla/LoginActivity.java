package com.evilgeniustechnologies.airsla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.evilgeniustechnologies.airsla.base.ServiceActivity;
import com.evilgeniustechnologies.airsla.services.ServiceReceiver;
import com.evilgeniustechnologies.airsla.services.SubmitService;
import com.evilgeniustechnologies.airsla.utilities.DataValidator;
import com.evilgeniustechnologies.airsla.utilities.DialogManager;
import com.evilgeniustechnologies.airsla.utilities.UserPreferences;

/**
 * Created by benjamin on 2/17/14.
 */
public class LoginActivity extends ServiceActivity {
    private String email = null;
    private String password = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Disable home button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        // Display dialog if any
        if (!DialogManager.showDialog(this)) {
            // Restore preferences
            SharedPreferences settings = getSharedPreferences(UserPreferences.PREFS_NAME, 0);
            email = settings.getString(UserPreferences.EMAIL, null);
            password = settings.getString(UserPreferences.PASSWORD, null);

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                executeLogin(email, password);
            }
        }
    }

    private void executeLogin(String email, String password) {
        if (DataValidator.validateLogin(this, email, password)) {
            // Start service
            Intent service = new Intent(this, SubmitService.class);
            service.putExtra(ServiceReceiver.RECEIVER, receiver);
            service.putExtra(SubmitService.TASK, SubmitService.LOGIN);
            service.putExtra(SubmitService.EMAIL, email);
            service.putExtra(SubmitService.PASSWORD, password);
            startService(service);
        }
    }

    public void login(View v) {
        EditText emailField = (EditText) findViewById(R.id.email);
        EditText passwordField = (EditText) findViewById(R.id.password);

        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        executeLogin(email, password);
    }

    public void register(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        DataValidator.processLoginResponse(this,
                data.getString(SubmitService.RESPONSE),
                data.getString(SubmitService.EMAIL),
                data.getString(SubmitService.PASSWORD));
    }
}
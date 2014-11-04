package com.evilgeniustechnologies.airsla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.evilgeniustechnologies.airsla.base.BaseMenuActivity;

/**
 * Created by benjamin on 2/19/14.
 */
public class PasswordMenuActivity extends BaseMenuActivity {
    private enum Contents {
        FORGOT_PASSWORD,
        CHANGE_PASSWORD
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Populate item menus
        populateList(getResources().getStringArray(R.array.password_menu_content));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Contents item = Contents.values()[i];
        Intent intent;
        switch (item) {
            case FORGOT_PASSWORD:
                intent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            case CHANGE_PASSWORD:
                intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
        }
    }
}
package com.evilgeniustechnologies.airsla;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.evilgeniustechnologies.airsla.base.ServiceActivity;
import com.evilgeniustechnologies.airsla.services.FeedEater;
import com.evilgeniustechnologies.airsla.services.ParsingService;
import com.evilgeniustechnologies.airsla.services.ServiceReceiver;
import com.evilgeniustechnologies.airsla.utilities.DataValidator;
import com.evilgeniustechnologies.airsla.utilities.DialogManager;

import java.util.List;

/**
 * Created by benjamin on 2/19/14.
 */
public class ContactActivity extends ServiceActivity {
    private static final String ADDRESS_1 = "ADDRESS_1";
    private static final String ADDRESS_2 = "ADDRESS_2";
    private static final String PHONE = "PHONE";
    private static final String EMAIL = "EMAIL";
    private String address1;
    private String address2;
    private String phone;
    private String email;
    private TextView address1View;
    private TextView address2View;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);

        // Instantiate the text views
        address1View = (TextView) findViewById(R.id.address1);
        address2View = (TextView) findViewById(R.id.address2);

        // Restore after orientation
        if (savedInstanceState != null) {
            address1 = savedInstanceState.getString(ADDRESS_1);
            address2 = savedInstanceState.getString(ADDRESS_2);
            phone = savedInstanceState.getString(PHONE);
            email = savedInstanceState.getString(EMAIL);
            // Display dialog if any
            if (!DialogManager.showDialog(this)) {
                populateViews();
            }
        } else {
            // Check for internet connection
            if (DataValidator.checkInternetConnection(this)) {
                // Start parsing xml
                Intent service = new Intent(this, ParsingService.class);
                service.putExtra(ServiceReceiver.RECEIVER, receiver);
                service.putExtra(ParsingService.URL, FeedEater.URL_INFO);
                service.putExtra(ParsingService.ROOT, FeedEater.I_ROOT);
                startService(service);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ADDRESS_1, address1);
        outState.putString(ADDRESS_2, address2);
        outState.putString(PHONE, phone);
        outState.putString(EMAIL, email);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onReceiveResult(int task, Bundle data) {
        List list = data.getParcelableArrayList(ParsingService.RESULT);
        // Check timeout
        if (DataValidator.checkTimeout(this, list)) {
            // Execute result
            executeResult(list);
        }
    }

    private void executeResult(List s) {
        FeedEater.Contact contact = (FeedEater.Contact) s.get(0);
        if (contact != null) {
            address1 = contact.address1;
            address2 = contact.address2;
            phone = contact.telephone;
            email = contact.email;
            // Populate views
            populateViews();
        }
    }

    private void populateViews() {
        address1View.setText(address1);
        address2View.setText(address2);
    }

    public void call(View v) {
        if (phone != null) {
            String number = "tel:" + phone;
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(number));
            startActivity(intent);
        }
    }

    public void email(View v) {
        if (email != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.setType("message/rfc822");
            startActivity(intent);
        }
    }
}
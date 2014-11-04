package com.evilgeniustechnologies.airsla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.evilgeniustechnologies.airsla.base.SearchableListViewActivity;
import com.evilgeniustechnologies.airsla.services.FeedEater;
import com.evilgeniustechnologies.airsla.services.ParsingService;
import com.evilgeniustechnologies.airsla.services.ServiceReceiver;
import com.evilgeniustechnologies.airsla.utilities.DataValidator;
import com.evilgeniustechnologies.airsla.utilities.ResourceAdapter;

/**
 * Created by benjamin on 3/3/14.
 */
public class ResourcesActivity extends SearchableListViewActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Check for internet connection
            if (DataValidator.checkInternetConnection(this)) {
                // Start parsing xml
                parsingXml();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        FeedEater.Resource resource = (FeedEater.Resource) adapterView.getItemAtPosition(i);
//        if (resource != null) {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(resource.url));
//            startActivity(intent);
//        }
    }

    @Override
    protected void parsingXml() {
        Intent service = new Intent(this, ParsingService.class);
        service.putExtra(ServiceReceiver.RECEIVER, receiver);
        service.putExtra(ParsingService.URL, FeedEater.URL_INFO);
        service.putExtra(ParsingService.ROOT, FeedEater.I_ROOT);
        service.putExtra(ParsingService.SUB_ROOT, FeedEater.R_SUB_ROOT);
        startService(service);
    }

    @Override
    protected void populateListView(int active) {
        adapter = new ResourceAdapter(this, contents);
        adapter.setActive(active);
        root.setAdapter(adapter);
        root.setTextFilterEnabled(true);
        // Add listener
        root.setOnItemClickListener(adapter);
    }
}
package com.evilgeniustechnologies.airsla;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import com.evilgeniustechnologies.airsla.base.SearchableListViewActivity;
import com.evilgeniustechnologies.airsla.services.FeedEater;
import com.evilgeniustechnologies.airsla.services.ParsingService;
import com.evilgeniustechnologies.airsla.services.ServiceReceiver;
import com.evilgeniustechnologies.airsla.utilities.BroadcastAdapter;
import com.evilgeniustechnologies.airsla.utilities.DataValidator;

/**
 * Created by benjamin on 2/26/14.
 */
public class PodCastDetailsActivity extends SearchableListViewActivity {
    public static final String CHANNEL_TITLE = "CHANNEL_TITLE";
    public static final String TARGET = "TARGET";
    public static final String INDEX = "INDEX";
    private String target;
    private String title;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the target channel
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            target = extras.getString(TARGET);
            title = extras.getString(CHANNEL_TITLE);
        }

        // Restore after orientation
        if (savedInstanceState != null) {
            target = savedInstanceState.getString(TARGET);
            title = savedInstanceState.getString(CHANNEL_TITLE);
        } else {
            // Check for internet connection
            if (DataValidator.checkInternetConnection(this)) {
                // Start parsing xml
                parsingXml();
            }
        }

        // Set title
        setTitle(title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TARGET, target);
        outState.putString(CHANNEL_TITLE, title);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void parsingXml() {
        Intent service = new Intent(this, ParsingService.class);
        service.putExtra(ServiceReceiver.RECEIVER, receiver);
        service.putExtra(ParsingService.URL, FeedEater.URL_RSS.replace("@", target));
        service.putExtra(ParsingService.ROOT, FeedEater.C_ROOT);
        startService(service);
    }

    @Override
    protected void populateListView(int active) {
        Log.e("size", String.valueOf(contents.size()));
        adapter = new BroadcastAdapter(this, contents);
        adapter.setActive(active);
        root.setAdapter(adapter);
        root.setTextFilterEnabled(true);
        // Add listener
        root.setOnItemClickListener(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList(CONTENTS, getAllBroadcasts());
//        bundle.putInt(INDEX, i);
//        Intent intent = new Intent(this, PodcastBroadcastingActivity.class);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

//    private ArrayList<UserPreferences.Broadcast> getAllBroadcasts() {
//        ArrayList<UserPreferences.Broadcast> broadcasts = new ArrayList<UserPreferences.Broadcast>();
//        for (Object o : contents) {
//            FeedEater.Item item = (FeedEater.Item) o;
//            Log.e("item channel", item.channel);
//            broadcasts.add(new UserPreferences.Broadcast(item.channel, item.title,
//                    item.description, item.author, item.date,
//                    item.duration, item.enclosure));
//        }
//        return broadcasts;
//    }
}
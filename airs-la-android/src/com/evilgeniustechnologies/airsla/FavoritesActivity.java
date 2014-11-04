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
import com.evilgeniustechnologies.airsla.utilities.DialogManager;
import com.evilgeniustechnologies.airsla.utilities.FavoriteAdapter;
import com.evilgeniustechnologies.airsla.utilities.UserPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 3/4/14.
 */
public class FavoritesActivity extends SearchableListViewActivity {
    public static final String NULL = UserPreferences.saveTarget("NULL");
    public static final String CHANNEL_TITLES = "CHANNEL_TITLES";
    private static final String BROADCASTS = "BROADCASTS";
    private static final String CHANNEL_NAMES = "CHANNEL_NAMES";
    private ArrayList<UserPreferences.Broadcast> broadcasts;
    private ArrayList<String> channelNames;
    private ArrayList<String> channelTitles;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            broadcasts = savedInstanceState.getParcelableArrayList(BROADCASTS);
            channelTitles = savedInstanceState.getStringArrayList(CHANNEL_TITLES);
            channelNames = savedInstanceState.getStringArrayList(CHANNEL_NAMES);
        } else {
            // Check for internet connection
            if (DataValidator.checkInternetConnection(this)) {
                // Start retrieving data
                getFavorites();
                if (DataValidator.checkEmpty(this, broadcasts)) {
                    // Start parsing xml
                    parsingXml();
                }
            }
        }

        // Display dialog if any
        DialogManager.showDialog(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(BROADCASTS, broadcasts);
        outState.putStringArrayList(CHANNEL_TITLES, channelTitles);
        outState.putStringArrayList(CHANNEL_NAMES, channelNames);
        super.onSaveInstanceState(outState);
    }

    private void getFavorites() {
        broadcasts = new ArrayList<UserPreferences.Broadcast>();
        broadcasts = UserPreferences.loadBroadcasts(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        if (((UserPreferences.Broadcast) contents.get(i)).description.equals(NULL)) {
//            return;
//        }
//        ArrayList<UserPreferences.Broadcast> newBroadcasts = new ArrayList<UserPreferences.Broadcast>();
//        int realIndex = -1;
//        for (int j = 0; j <= i; j++) {
//            realIndex++;
//            if (((UserPreferences.Broadcast) contents.get(j)).description.equals(NULL)) {
//                realIndex--;
//            }
//        }
//        for (Object o : contents) {
//            UserPreferences.Broadcast broadcast = (UserPreferences.Broadcast) o;
//            if (!broadcast.description.equals(NULL)) {
//                newBroadcasts.add(broadcast);
//            }
//        }
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList(SearchableListViewActivity.CONTENTS, newBroadcasts);
//        bundle.putInt(PodCastDetailsActivity.INDEX, realIndex);
//        Intent intent = new Intent(this, PodcastBroadcastingActivity.class);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

    @Override
    protected void parsingXml() {
        Intent service = new Intent(this, ParsingService.class);
        service.putExtra(ServiceReceiver.RECEIVER, receiver);
        service.putExtra(ParsingService.URL, FeedEater.URL_PRO);
        service.putExtra(ParsingService.ROOT, FeedEater.P_ROOT);
        startService(service);
    }

    @Override
    protected void executeResult(List s) {
        // Get the contents
        channelNames = getAllChannelNames(s);
        channelTitles = getAllChannelTitles(broadcasts);
        // Populates the contents
        populateContents();
        // Populates the list view
        populateListView(-1);
    }

    @Override
    protected void populateListView(int active) {
        adapter = new FavoriteAdapter(this, contents);
        adapter.setActive(active);
        root.setAdapter(adapter);
        root.setTextFilterEnabled(true);
        // Add listener
        root.setOnItemClickListener(adapter);
    }

    private void populateContents() {
        contents = new ArrayList();
        ArrayList<String> newChannelNames = new ArrayList<String>();
        for (String channelName : channelNames) {
            for (String channelTitle : channelTitles) {
                if (channelName.equals(channelTitle) &&
                        !newChannelNames.contains(channelName)) {
                    newChannelNames.add(channelName);
                }
            }
        }
        for (String newChannelName : newChannelNames) {
            contents.add(new UserPreferences.Broadcast(newChannelName, newChannelName, NULL, NULL, NULL, NULL, NULL));
            for (UserPreferences.Broadcast broadcast : broadcasts) {
                if (broadcast.title.equals(newChannelName)) {
                    contents.add(new UserPreferences.Broadcast(
                            broadcast.title, broadcast.heading,
                            broadcast.description, broadcast.author,
                            broadcast.date, broadcast.duration, broadcast.link)
                    );
                }
            }
        }
    }

    private ArrayList<String> getAllChannelNames(List items) {
        ArrayList<String> contents = new ArrayList<String>();
        for (Object b : items) {
            contents.add(((FeedEater.Project) b).pageHeading);
        }
        return contents;
    }

    private ArrayList<String> getAllChannelTitles(List items) {
        ArrayList<String> contents = new ArrayList<String>();
        for (Object b : items) {
            contents.add(((UserPreferences.Broadcast) b).title);
        }
        return contents;
    }
}
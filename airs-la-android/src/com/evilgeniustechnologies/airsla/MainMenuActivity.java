package com.evilgeniustechnologies.airsla;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.evilgeniustechnologies.airsla.base.BaseMenuActivity;

/**
 * Created by benjamin on 2/19/14.
 */
public class MainMenuActivity extends BaseMenuActivity {
    private enum Contents {
        TOP_5_PODCASTS,
        SEARCH_ALL_PODCASTS,
        PODCASTS_BY_CATEGORY,
        MY_FAVORITES,
        ABOUT_AIRSLA,
        DONATE_TO_AIRSLA,
        CONTACT_US,
        PASSWORD_HELP,
        CANCEL_MEMBERSHIP
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Modify Action Bar to add 'Navigating Up' behavior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        // Populate item menus
        populateList(getResources().getStringArray(R.array.main_menu_content));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Contents item = Contents.values()[i];
        Intent intent;
        switch (item) {
            case TOP_5_PODCASTS:
                Log.e("Top 5 Podcasts", String.valueOf(i));
                intent = new Intent(this, TopBroadcastsActivity.class);
                startActivity(intent);
                break;
            case SEARCH_ALL_PODCASTS:
                Log.e("Search All Podcasts", String.valueOf(i));
                intent = new Intent(this, AllPodcastsActivity.class);
                startActivity(intent);
                break;
            case PODCASTS_BY_CATEGORY:
                Log.e("Podcasts by Category", String.valueOf(i));
                intent = new Intent(this, PodCastCategoriesActivity.class);
                startActivity(intent);
                break;
            case MY_FAVORITES:
                Log.e("My Favorites", String.valueOf(i));
                intent = new Intent(this, FavoritesActivity.class);
                startActivity(intent);
                break;
            case ABOUT_AIRSLA:
                Log.e("About Airs-LA", String.valueOf(i));
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case DONATE_TO_AIRSLA:
                Log.e("Donate to Airs-LA", String.valueOf(i));
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.airsla.org/m/don8nodelete.html#_1"));
                startActivity(intent);
                break;
            case CONTACT_US:
                Log.e("Contact Us", String.valueOf(i));
                intent = new Intent(this, ContactActivity.class);
                startActivity(intent);
                break;
            case PASSWORD_HELP:
                Log.e("Password Help", String.valueOf(i));
                intent = new Intent(this, PasswordMenuActivity.class);
                startActivity(intent);
                break;
            case CANCEL_MEMBERSHIP:
                Log.e("Cancel Membership", String.valueOf(i));
                intent = new Intent(this, UnregisterActivity.class);
                startActivity(intent);
                break;
        }
    }
}
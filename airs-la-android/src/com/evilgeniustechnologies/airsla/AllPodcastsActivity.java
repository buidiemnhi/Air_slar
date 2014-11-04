package com.evilgeniustechnologies.airsla;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import com.evilgeniustechnologies.airsla.base.ListViewActivity;
import com.evilgeniustechnologies.airsla.services.FeedEater;
import com.evilgeniustechnologies.airsla.services.ParsingService;
import com.evilgeniustechnologies.airsla.services.ServiceReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 2/25/14.
 */
public class AllPodcastsActivity extends ListViewActivity {
    // Extras from Podcast Categories
    private String category = null;
    private String cateName = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get request category, if any
        cateName = getIntent().getStringExtra(PodCastCategoriesActivity.CATE_NAME);
        category = getIntent().getStringExtra(PodCastCategoriesActivity.CATEGORY);

        // Set title
        if (cateName != null) {
            setTitle(cateName + " " + getResources().getString(R.string.podcast));
        }
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
        String title = adapterView.getItemAtPosition(i).toString();
        if (title != null) {
            Intent intent = new Intent(this, PodCastDetailsActivity.class);
            intent.putExtra(PodCastDetailsActivity.TARGET, getTargetContent(title));
            intent.putExtra(PodCastDetailsActivity.CHANNEL_TITLE, title);
            startActivity(intent);
        }
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
    protected ArrayList<String> getAllContents(List list) {
        ArrayList<String> names = new ArrayList<String>();
        for (Object pro : list) {
            FeedEater.Project p = (FeedEater.Project) pro;
            if (category != null) {
                if (p.categories.contains(category)) {
                    names.add(p.name);
                }
            } else {
                names.add(p.name);
            }
        }
        return names;
    }

    @Override
    protected ArrayList<String> getAllTitles(List list) {
        ArrayList<String> headings = new ArrayList<String>();
        for (Object pro : list) {
            FeedEater.Project p = (FeedEater.Project) pro;
            if (category != null) {
                if (p.categories.contains(category)) {
                    headings.add(p.pageHeading);
                }
            } else {
                headings.add(p.pageHeading);
            }
        }
        return headings;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false);

        SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Filters adapter
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Filters adapter
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
                return true;
            }
        };
        searchView.setOnQueryTextListener(onQueryTextListener);

        return super.onCreateOptionsMenu(menu);
    }
}
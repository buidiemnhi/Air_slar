package com.evilgeniustechnologies.airsla.base;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import com.evilgeniustechnologies.airsla.R;
import com.evilgeniustechnologies.airsla.services.ParsingService;
import com.evilgeniustechnologies.airsla.utilities.DataValidator;
import com.evilgeniustechnologies.airsla.utilities.DialogManager;
import com.evilgeniustechnologies.airsla.utilities.EntryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 3/12/14.
 */
public abstract class SearchableListViewActivity extends BaseViewActivity {
    public static final String ACTIVE = "ACTIVE";
    public static final String CONTENTS = "CONTENTS";
    protected EntryAdapter adapter;
    protected ArrayList contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore after orientation
        if (savedInstanceState != null) {
            contents = savedInstanceState.getParcelableArrayList(CONTENTS);
            if (!DialogManager.showDialog(this)) {
                populateListView(savedInstanceState.getInt(ACTIVE));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (adapter != null) {
            outState.putInt(ACTIVE, adapter.getActive());
        } else {
            outState.putInt(ACTIVE, -1);
        }
        outState.putParcelableArrayList(CONTENTS, contents);
        super.onSaveInstanceState(outState);
    }

    @Override
    public abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);

    @Override
    public void onReceiveResult(int task, Bundle data) {
        List list = data.getParcelableArrayList(ParsingService.RESULT);
        // Check timeout
        if (DataValidator.checkTimeout(this, list)) {
            // Execute result
            executeResult(list);
        }
    }

    protected abstract void parsingXml();

    protected void executeResult(List s) {
        // Get the contents
        contents = (ArrayList) s;
        // Populates the list view
        populateListView(-1);
    }

    protected abstract void populateListView(int active);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
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

package com.evilgeniustechnologies.airsla.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.evilgeniustechnologies.airsla.R;
import com.evilgeniustechnologies.airsla.utilities.DialogManager;

/**
 * Created by benjamin on 3/12/14.
 */
public abstract class BaseMenuActivity extends ActionBarActivity
        implements AdapterView.OnItemClickListener {
    protected ListView root;
    protected ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_Light_DarkActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        // Enable rotation if the device is tablet
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Modify Action Bar to add 'Navigating Up' behavior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Get the list view
        root = (ListView) findViewById(R.id.list_layout_list_view);

        // Restore after orientation
        if (savedInstanceState == null) {
            DialogManager.reset();
        }
    }

    @Override
    public abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);

    protected void populateList(String[] contents) {
        // Populates the list view
        adapter = new ArrayAdapter<String>(
                this,
                R.layout.list_item_menu_layout,
                R.id.item_label,
                contents
        );
        root.setAdapter(adapter);
        root.setTextFilterEnabled(true);

        // Add listener
        root.setOnItemClickListener(this);
    }
}

package com.evilgeniustechnologies.airsla.base;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.evilgeniustechnologies.airsla.R;
import com.evilgeniustechnologies.airsla.services.ParsingService;
import com.evilgeniustechnologies.airsla.utilities.DataValidator;
import com.evilgeniustechnologies.airsla.utilities.DialogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 3/11/14.
 */
public abstract class ListViewActivity extends BaseViewActivity {
    private static final String CONTENTS = "CONTENTS";
    private static final String TITLES = "TITLES";
    protected ArrayAdapter<String> adapter;
    protected ArrayList<String> contents;
    protected ArrayList<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore after orientation
        if (savedInstanceState != null) {
            contents = savedInstanceState.getStringArrayList(CONTENTS);
            titles = savedInstanceState.getStringArrayList(TITLES);
            // Display dialog if any
            if (!DialogManager.showDialog(this)) {
                populateListView(titles);
            }
        } else {
            // Check for internet connection
            if (DataValidator.checkInternetConnection(this)) {
                // Start parsing xml
                parsingXml();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(CONTENTS, contents);
        outState.putStringArrayList(TITLES, titles);
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

    protected void executeResult(List list) {
        // Get the contents
        contents = getAllContents(list);
        titles = getAllTitles(list);
        // Populates the list view
        populateListView(titles);
    }

    protected void populateListView(List<String> list) {
        adapter = new ArrayAdapter<String>(
                this,
                R.layout.list_item_menu_layout,
                R.id.item_label,
                list
        );
        root.setAdapter(adapter);
        root.setTextFilterEnabled(true);

        // Add listener
        root.setOnItemClickListener(this);
    }

    protected abstract ArrayList<String> getAllContents(List list);

    protected abstract ArrayList<String> getAllTitles(List list);

    protected String getTargetContent(String title) {
        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i).equals(title)) {
                return contents.get(i);
            }
        }
        return null;
    }
}

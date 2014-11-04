package com.evilgeniustechnologies.airsla.base;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.evilgeniustechnologies.airsla.R;

/**
 * Created by benjamin on 3/12/14.
 */
public abstract class BaseViewActivity extends ServiceActivity
        implements AdapterView.OnItemClickListener {
    protected ListView root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        // Get the list view
        root = (ListView) findViewById(R.id.list_layout_list_view);
    }

    @Override
    public abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);
}

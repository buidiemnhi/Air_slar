package com.evilgeniustechnologies.airsla.utilities;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.evilgeniustechnologies.airsla.R;

/**
 * Created by benjamin on 3/7/14.
 */
public abstract class EntryAdapter extends ArrayAdapter
        implements AdapterView.OnItemClickListener {
    protected int active = -1;

    public EntryAdapter(Context context) {
        super(context, R.layout.list_item_detail_layout);
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getActive() {
        return active;
    }
}

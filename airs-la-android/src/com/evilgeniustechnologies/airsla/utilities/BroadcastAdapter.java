package com.evilgeniustechnologies.airsla.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import com.evilgeniustechnologies.airsla.PodCastDetailsActivity;
import com.evilgeniustechnologies.airsla.PodcastBroadcastingActivity;
import com.evilgeniustechnologies.airsla.R;
import com.evilgeniustechnologies.airsla.services.FeedEater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 3/17/14.
 */
public class BroadcastAdapter extends EntryAdapter {
    private List<FeedEater.Item> items;
    private List<FeedEater.Item> current;
    private BroadcastFilter broadcastFilter;

    public BroadcastAdapter(Context context, List<FeedEater.Item> items) {
        super(context);
        this.items = items;
        current = new ArrayList<FeedEater.Item>(items);
        populate();
    }

    private void populate() {
        clear();
        for (FeedEater.Item item : current) {
            add(item.title);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_detail_layout, parent, false);
            // Configure view holder
            if (rowView != null) {
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.titView = (TextView) rowView.findViewById(R.id.item_title);
                viewHolder.desView = (TextView) rowView.findViewById(R.id.item_description);
                viewHolder.autView = (TextView) rowView.findViewById(R.id.item_author);
                viewHolder.durView = (TextView) rowView.findViewById(R.id.item_duration);
                viewHolder.infoView = (ImageView) rowView.findViewById(R.id.item_info);
                rowView.setTag(viewHolder);
            }
        }
        // Load data to row view
        if (rowView != null) {
            final ViewHolder viewHolder = (ViewHolder) rowView.getTag();
            // Fill data
            viewHolder.position = position;
            viewHolder.titView.setText(current.get(viewHolder.position).title);
            viewHolder.infoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    active = viewHolder.position;
                    viewHolder.desView.setText(current.get(viewHolder.position).description);
                    viewHolder.desView.setVisibility(View.VISIBLE);
                    viewHolder.autView.setText(current.get(viewHolder.position).author);
                    viewHolder.autView.setVisibility(View.VISIBLE);
                    viewHolder.durView.setText(current.get(viewHolder.position).duration);
                    viewHolder.durView.setVisibility(View.VISIBLE);
                    BroadcastAdapter.this.notifyDataSetChanged();
                }
            });
            rowView.setPadding(6, 6, 6, 6);
            // Check if expand
            if (active == viewHolder.position) {
                viewHolder.desView.setText(current.get(viewHolder.position).description);
                viewHolder.desView.setVisibility(View.VISIBLE);
                viewHolder.autView.setText(current.get(viewHolder.position).author);
                viewHolder.autView.setVisibility(View.VISIBLE);
                viewHolder.durView.setText(current.get(viewHolder.position).duration);
                viewHolder.durView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.desView.setVisibility(View.GONE);
                viewHolder.autView.setVisibility(View.GONE);
                viewHolder.durView.setVisibility(View.GONE);
            }
        }
        return rowView;
    }

    @Override
    public Filter getFilter() {
        if (broadcastFilter == null) {
            broadcastFilter = new BroadcastFilter();
        }
        return broadcastFilter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<UserPreferences.Broadcast> broadcasts = new ArrayList<UserPreferences.Broadcast>();
        for (FeedEater.Item item : items) {
            broadcasts.add(new UserPreferences.Broadcast(item.channel, item.title,
                    item.description, item.author, item.date, item.duration, item.enclosure));
        }
        int realIndex = -1;
        for (int i = 0; i < broadcasts.size(); i++) {
            if (current.get(position).enclosure.equals(broadcasts.get(i).link)) {
                realIndex = i;
            }
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PodCastDetailsActivity.CONTENTS, broadcasts);
        bundle.putInt(PodCastDetailsActivity.INDEX, realIndex);
        Intent intent = new Intent(getContext(), PodcastBroadcastingActivity.class);
        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }

    private static class ViewHolder {
        public TextView titView;
        public TextView desView;
        public TextView autView;
        public TextView durView;
        public ImageView infoView;
        public int position;
    }

    private class BroadcastFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = items;
                results.count = items.size();
            } else {
                // We perform filtering operation
                List<FeedEater.Item> filtered = new ArrayList<FeedEater.Item>();

                for (FeedEater.Item item : items) {
                    if (item.title.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        filtered.add(item);
                    } else {
                        final String[] words = item.title.toLowerCase().split(" ");
                        for (String word : words) {
                            if (word.startsWith(constraint.toString().toLowerCase())) {
                                filtered.add(item);
                                break;
                            }
                        }
                    }
                }
                results.values = filtered;
                results.count = filtered.size();
                Log.e("search count", String.valueOf(results.count));
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            current = (List<FeedEater.Item>) results.values;
            populate();
            Log.e("count", String.valueOf(results.count));
            if (results.count > 0) {
                active = -1;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}

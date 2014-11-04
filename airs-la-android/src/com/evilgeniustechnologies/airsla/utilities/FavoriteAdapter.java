package com.evilgeniustechnologies.airsla.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import com.evilgeniustechnologies.airsla.FavoritesActivity;
import com.evilgeniustechnologies.airsla.PodCastDetailsActivity;
import com.evilgeniustechnologies.airsla.PodcastBroadcastingActivity;
import com.evilgeniustechnologies.airsla.R;
import com.evilgeniustechnologies.airsla.base.SearchableListViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 3/17/14.
 */
public class FavoriteAdapter extends EntryAdapter {
    private List<UserPreferences.Broadcast> broadcasts;
    private List<UserPreferences.Broadcast> current;
    private FavoriteFilter favoriteFilter;

    public FavoriteAdapter(Context context, List<UserPreferences.Broadcast> broadcasts) {
        super(context);
        this.broadcasts = broadcasts;
        current = new ArrayList<UserPreferences.Broadcast>(broadcasts);
        populate();
    }

    private void populate() {
        clear();
        for (UserPreferences.Broadcast broadcast : current) {
            add(broadcast.heading);
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
                viewHolder.arrowView = (ImageView) rowView.findViewById(R.id.item_arrow);
                rowView.setTag(viewHolder);
            }
        }
        // Load data to row view
        if (rowView != null) {
            final ViewHolder viewHolder = (ViewHolder) rowView.getTag();
            // Fill data
            viewHolder.position = position;
            viewHolder.titView.setText(current.get(viewHolder.position).heading);
            if (current.get(viewHolder.position).description.equals(FavoritesActivity.NULL)) {
                rowView.setBackgroundColor(Color.LTGRAY);
                viewHolder.titView.setTypeface(null, Typeface.BOLD);
                viewHolder.arrowView.setVisibility(View.INVISIBLE);
                viewHolder.infoView.setVisibility(View.INVISIBLE);
                viewHolder.desView.setVisibility(View.GONE);
                viewHolder.autView.setVisibility(View.GONE);
                viewHolder.durView.setVisibility(View.GONE);
            } else {
                rowView.setBackgroundResource(android.support.v7.appcompat.R.drawable.abc_list_selector_holo_light);
                viewHolder.titView.setTypeface(null, Typeface.NORMAL);
                viewHolder.arrowView.setVisibility(View.VISIBLE);
                viewHolder.infoView.setVisibility(View.VISIBLE);
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
                        FavoriteAdapter.this.notifyDataSetChanged();
                    }
                });
            }
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
        if (favoriteFilter == null) {
            favoriteFilter = new FavoriteFilter();
        }
        return favoriteFilter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ((current.get(position)).description.equals(FavoritesActivity.NULL)) {
            return;
        }
        ArrayList<UserPreferences.Broadcast> newBroadcasts = new ArrayList<UserPreferences.Broadcast>();
        for (UserPreferences.Broadcast broadcast : broadcasts) {
            if (!broadcast.description.equals(FavoritesActivity.NULL)) {
                newBroadcasts.add(broadcast);
            }
        }
        int realIndex = -1;
        for (int i = 0; i < newBroadcasts.size(); i++) {
            if (current.get(position).link.equals(newBroadcasts.get(i).link)) {
                realIndex = i;
            }
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SearchableListViewActivity.CONTENTS, newBroadcasts);
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
        public ImageView arrowView;
        public int position;
    }

    private class FavoriteFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = broadcasts;
                results.count = broadcasts.size();
            } else {
                // We perform filtering operation
                List<UserPreferences.Broadcast> filtered = new ArrayList<UserPreferences.Broadcast>();

                for (UserPreferences.Broadcast broadcast : broadcasts) {
                    if (!broadcast.description.equals(FavoritesActivity.NULL)) {
                        if (broadcast.heading.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            filtered.add(broadcast);
                        } else {
                            final String[] words = broadcast.heading.toLowerCase().split(" ");
                            for (String word : words) {
                                if (word.startsWith(constraint.toString().toLowerCase())) {
                                    filtered.add(broadcast);
                                    break;
                                }
                            }
                        }
                    }
                }
                results.values = filtered;
                results.count = filtered.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            current = (List<UserPreferences.Broadcast>) results.values;
            populate();
            if (results.count > 0) {
                active = -1;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}

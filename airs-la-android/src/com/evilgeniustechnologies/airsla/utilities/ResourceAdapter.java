package com.evilgeniustechnologies.airsla.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import com.evilgeniustechnologies.airsla.R;
import com.evilgeniustechnologies.airsla.services.FeedEater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 3/17/14.
 */
public class ResourceAdapter extends EntryAdapter {
    private List<FeedEater.Resource> resources;
    private List<FeedEater.Resource> current;
    private BroadcastFilter broadcastFilter;

    public ResourceAdapter(Context context, List<FeedEater.Resource> resources) {
        super(context);
        this.resources = resources;
        current = new ArrayList<FeedEater.Resource>(resources);
        populate();
    }

    private void populate() {
        clear();
        for (FeedEater.Resource resource : current) {
            add(resource.name);
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
                viewHolder.autView.setVisibility(View.GONE);
                viewHolder.durView.setVisibility(View.GONE);
                rowView.setTag(viewHolder);
            }
        }
        // Load data to row view
        if (rowView != null) {
            final ViewHolder viewHolder = (ViewHolder) rowView.getTag();
            // Fill data
            viewHolder.position = position;
            viewHolder.titView.setText(current.get(viewHolder.position).name);
            viewHolder.infoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    active = viewHolder.position;
                    viewHolder.desView.setText(current.get(viewHolder.position).description);
                    viewHolder.desView.setVisibility(View.VISIBLE);
                    ResourceAdapter.this.notifyDataSetChanged();
                }
            });
            rowView.setPadding(6, 6, 6, 6);
            // Check if expand
            if (active == viewHolder.position) {
                viewHolder.desView.setText(current.get(viewHolder.position).description);
                viewHolder.desView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.desView.setVisibility(View.GONE);
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
        FeedEater.Resource resource = current.get(position);
        if (resource != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(resource.url));
            getContext().startActivity(intent);
        }
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
                results.values = resources;
                results.count = resources.size();
            } else {
                // We perform filtering operation
                List<FeedEater.Resource> filtered = new ArrayList<FeedEater.Resource>();

                for (FeedEater.Resource resource : resources) {
                    if (resource.name.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        filtered.add(resource);
                    } else {
                        final String[] words = resource.name.toLowerCase().split(" ");
                        for (String word : words) {
                            if (word.startsWith(constraint.toString().toLowerCase())) {
                                filtered.add(resource);
                                break;
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
            current = (List<FeedEater.Resource>) results.values;
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

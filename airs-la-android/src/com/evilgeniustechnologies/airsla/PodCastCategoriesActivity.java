package com.evilgeniustechnologies.airsla;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import com.evilgeniustechnologies.airsla.base.ListViewActivity;
import com.evilgeniustechnologies.airsla.services.FeedEater;
import com.evilgeniustechnologies.airsla.services.ParsingService;
import com.evilgeniustechnologies.airsla.services.ServiceReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 2/27/14.
 */
public class PodCastCategoriesActivity extends ListViewActivity {
    public static final String CATE_NAME = "CATEGORY_NAME";
    public static final String CATEGORY = "CATEGORY";

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String title = adapterView.getItemAtPosition(i).toString();
        if (title != null) {
            Intent intent = new Intent(this, AllPodcastsActivity.class);
            intent.putExtra(CATE_NAME, title);
            intent.putExtra(CATEGORY, getTargetContent(title));
            startActivity(intent);
        }
    }

    @Override
    protected void parsingXml() {
        Intent service = new Intent(this, ParsingService.class);
        service.putExtra(ServiceReceiver.RECEIVER, receiver);
        service.putExtra(ParsingService.URL, FeedEater.URL_INFO);
        service.putExtra(ParsingService.ROOT, FeedEater.I_ROOT);
        service.putExtra(ParsingService.SUB_ROOT, FeedEater.I_SUB_ROOT);
        startService(service);
    }

    @Override
    protected ArrayList<String> getAllContents(List list) {
        ArrayList<String> contents = new ArrayList<String>();
        for (Object pro : list) {
            contents.add(((FeedEater.Category) pro).category);
        }
        return contents;
    }

    @Override
    protected ArrayList<String> getAllTitles(List list) {
        ArrayList<String> contents = new ArrayList<String>();
        for (Object pro : list) {
            contents.add(((FeedEater.Category) pro).cateName);
        }
        return contents;
    }
}
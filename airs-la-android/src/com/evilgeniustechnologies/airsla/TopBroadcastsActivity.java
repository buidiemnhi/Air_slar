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
 * Created by benjamin on 3/3/14.
 */
public class TopBroadcastsActivity extends ListViewActivity {

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String title = adapterView.getItemAtPosition(i).toString();
        if (title != null) {
            Intent intent = new Intent(this, PodCastDetailsActivity.class);
            intent.putExtra(PodCastDetailsActivity.CHANNEL_TITLE, title);
            intent.putExtra(PodCastDetailsActivity.TARGET, getTargetContent(title));
            startActivity(intent);
        }
    }

    @Override
    protected void parsingXml() {
        Intent service = new Intent(this, ParsingService.class);
        service.putExtra(ServiceReceiver.RECEIVER, receiver);
        service.putExtra(ParsingService.URL, FeedEater.URL_INFO);
        service.putExtra(ParsingService.ROOT, FeedEater.I_ROOT);
        service.putExtra(ParsingService.SUB_ROOT, FeedEater.T_SUB_ROOT);
        startService(service);
    }

    @Override
    protected ArrayList<String> getAllContents(List list) {
        ArrayList<String> contents = new ArrayList<String>();
        for (Object pro : list) {
            contents.add(((FeedEater.TopProject) pro).project);
        }
        return contents;
    }

    @Override
    protected ArrayList<String> getAllTitles(List list) {
        ArrayList<String> contents = new ArrayList<String>();
        for (Object pro : list) {
            contents.add(((FeedEater.TopProject) pro).proName);
        }
        return contents;
    }
}
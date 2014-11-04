package com.evilgeniustechnologies.airsla;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import com.evilgeniustechnologies.airsla.base.ServiceActivity;
import com.evilgeniustechnologies.airsla.services.FeedEater;
import com.evilgeniustechnologies.airsla.services.ParsingService;
import com.evilgeniustechnologies.airsla.services.ServiceReceiver;
import com.evilgeniustechnologies.airsla.utilities.DataValidator;
import com.evilgeniustechnologies.airsla.utilities.DialogManager;

import java.io.*;
import java.util.List;

/**
 * Created by benjamin on 2/19/14.
 */
public class AboutActivity extends ServiceActivity {
    private static final String CONTENT = "CONTENT";
    private static final String URL = "about.html";
    private String content;
    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        // Populate web view
        webView = (WebView) findViewById(R.id.about_web_view);

        // Restore after orientation
        if (savedInstanceState != null) {
            content = savedInstanceState.getString(CONTENT);
            // Display dialog if any
            if (!DialogManager.showDialog(this)) {
                webView.loadData(
                        content,
                        "text/html",
                        "UTF-8"
                );
            }
        } else {
            // Check for internet connection
            if (DataValidator.checkInternetConnection(this)) {
                // Start parsing xml
                Intent service = new Intent(this, ParsingService.class);
                service.putExtra(ServiceReceiver.RECEIVER, receiver);
                service.putExtra(ParsingService.URL, FeedEater.URL_INFO);
                service.putExtra(ParsingService.ROOT, FeedEater.I_ROOT);
                service.putExtra(ParsingService.SUB_ROOT, FeedEater.A_SUB_ROOT);
                startService(service);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CONTENT, content);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onReceiveResult(int task, Bundle data) {
        List list = data.getParcelableArrayList(ParsingService.RESULT);
        // Check timeout
        if (DataValidator.checkTimeout(this, list)) {
            // Execute result
            executeResult(list);
        }
    }

    private void executeResult(List s) {
        // Populates the web view
        InputStream is = null;
        InputStreamReader ir = null;
        BufferedReader br = null;
        try {
            is = getAssets().open(URL);
            ir = new InputStreamReader(is);
            br = new BufferedReader(ir);

            StringBuilder sb = new StringBuilder();
            String eachLine = br.readLine();

            while (eachLine != null) {
                sb.append(eachLine);
                sb.append("\n");
                eachLine = br.readLine();
            }

            content = sb.toString().replace("$", ((FeedEater.About) s.get(0)).paragraph);

            webView.loadData(
                    content,
                    "text/html",
                    "UTF-8"
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (ir != null) {
                    ir.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void resource(View v) {
        Intent intent = new Intent(this, ResourcesActivity.class);
        startActivity(intent);
    }
}
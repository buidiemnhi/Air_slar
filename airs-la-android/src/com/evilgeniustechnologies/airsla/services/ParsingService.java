package com.evilgeniustechnologies.airsla.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by benjamin on 3/11/14.
 */
public class ParsingService extends IntentService {
    public static final String RESULT = "RESULT";
    public static final String URL = "URL";
    public static final String ROOT = "ROOT";
    public static final String SUB_ROOT = "SUB_ROOT";

    private String url;
    private String subRoot;
    private String root;
    private ResultReceiver receiver;

    public ParsingService() {
        super("IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        receiver = intent.getParcelableExtra(ServiceReceiver.RECEIVER);
        url = intent.getStringExtra(URL);
        root = intent.getStringExtra(ROOT);
        subRoot = intent.getStringExtra(SUB_ROOT);
        executeResult(executeData());
    }

    private ArrayList executeData() {
        try {
            return loadXmlFromNetwork(url);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void executeResult(ArrayList list) {
        Bundle data = new Bundle();
        data.putParcelableArrayList(RESULT, list);
        receiver.send(0, data);
    }

    private ArrayList loadXmlFromNetwork(String url) throws IOException, XmlPullParserException {
        InputStream stream = null;

        // Instantiates the parser
        FeedEater feedEater = new FeedEater();
        ArrayList contents = null;

        try {
            stream = downloadUrl(url);
            contents = feedEater.parse(stream, root, subRoot);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return contents;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}

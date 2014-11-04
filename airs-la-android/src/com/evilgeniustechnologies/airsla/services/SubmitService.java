package com.evilgeniustechnologies.airsla.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 3/11/14.
 */
public class SubmitService extends IntentService {
    public static final String TASK = "TASK";
    public static final String RESPONSE = "RESPONSE";
    private int task;

    public static final int NULL = 0;

    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";

    public static final String FIRST = "FIRST";
    public static final String LAST = "LAST";
    public static final String VERIFY = "VERIFY";
    public static final String GENDER = "GENDER";
    public static final String BIRTH = "BIRTH";
    public static final String CAREER = "CAREER";
    public static final String ADDRESS = "ADDRESS";
    public static final String VISION = "VISION";
    public static final String AFFECTED = "AFFECTED";
    public static final String INTEREST = "INTEREST";

    public static final String NEW_PASS = "NEW_PASS";
    public static final String NEW_PASS_CONFIRM = "NEW_PASS_CONFIRM";

    public static final int LOGIN = 1;
    public static final int REGISTER = 2;
    public static final int FORGOT_PASS = 3;
    public static final int CHANGE_PASS = 4;
    public static final int UNREGISTER = 5;

    private String email;
    private String password;

    private String firstName;
    private String lastName;
    private String verifyEmail;
    private String gender;
    private String birth;
    private String career;
    private String address;
    private String visionImpairment;
    private String whoAffected;
    private List<String> interests;

    private String newPass;
    private String newPassConfirm;

    private ResultReceiver receiver;

    public SubmitService() {
        super("SubmitService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        receiver = intent.getParcelableExtra(ServiceReceiver.RECEIVER);
        task = intent.getIntExtra(TASK, NULL);
        switch (task) {
            case LOGIN:
                email = intent.getStringExtra(EMAIL);
                password = intent.getStringExtra(PASSWORD);
                break;
            case REGISTER:
                firstName = intent.getStringExtra(FIRST);
                lastName = intent.getStringExtra(LAST);
                email = intent.getStringExtra(EMAIL);
                verifyEmail = intent.getStringExtra(VERIFY);
                gender = intent.getStringExtra(GENDER);
                birth = intent.getStringExtra(BIRTH);
                career = intent.getStringExtra(CAREER);
                address = intent.getStringExtra(ADDRESS);
                visionImpairment = intent.getStringExtra(VISION);
                whoAffected = intent.getStringExtra(AFFECTED);
                interests = intent.getStringArrayListExtra(INTEREST);
                break;
            case FORGOT_PASS:
                email = intent.getStringExtra(EMAIL);
                break;
            case CHANGE_PASS:
                email = intent.getStringExtra(EMAIL);
                password = intent.getStringExtra(PASSWORD);
                newPass = intent.getStringExtra(NEW_PASS);
                newPassConfirm = intent.getStringExtra(NEW_PASS_CONFIRM);
                break;
            case UNREGISTER:
                email = intent.getStringExtra(EMAIL);
                password = intent.getStringExtra(PASSWORD);
                break;
        }
        executeResult(executeData());
    }

    private String executeData() {
        try {
            // Add data
            List<NameValuePair> data = new ArrayList<NameValuePair>();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost();
            if (task == LOGIN) {
                httpPost.setURI(URI.create("http://www.airsla.org/bmembership/Login.asp"));
                data.add(new BasicNameValuePair("EmailAddress", email));
                data.add(new BasicNameValuePair("Password", password));
            } else if (task == REGISTER) {
                httpPost.setURI(URI.create("http://www.airsla.org/bmembership/Signup.asp"));
                data.add(new BasicNameValuePair("first", firstName));
                data.add(new BasicNameValuePair("last", lastName));
                data.add(new BasicNameValuePair("eml", email));
                data.add(new BasicNameValuePair("veml", verifyEmail));
                data.add(new BasicNameValuePair("gender", gender));
                data.add(new BasicNameValuePair("born", birth));
                data.add(new BasicNameValuePair("status", career));
                data.add(new BasicNameValuePair("region", address));
                data.add(new BasicNameValuePair("vision", visionImpairment));
                data.add(new BasicNameValuePair("whoaff", whoAffected));
                data.add(new BasicNameValuePair("interests", getInterests()));
            } else if (task == FORGOT_PASS) {
                httpPost.setURI(URI.create("http://www.airsla.org/bmembership/Forgot.asp"));
                data.add(new BasicNameValuePair("EmailAddress", email));
            } else if (task == CHANGE_PASS) {
                httpPost.setURI(URI.create("http://www.airsla.org/bmembership/ChangePass.asp"));
                data.add(new BasicNameValuePair("EmailAddress", email));
                data.add(new BasicNameValuePair("OldPass", password));
                data.add(new BasicNameValuePair("NewPass", newPass));
                data.add(new BasicNameValuePair("VerPass", newPassConfirm));
            } else if (task == UNREGISTER) {
                httpPost.setURI(URI.create("http://www.airsla.org/bmembership/Resign.asp"));
                data.add(new BasicNameValuePair("EmailAddress", email));
                data.add(new BasicNameValuePair("Password", password));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(data));

            // Execute Http Post Request
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                InputStream in = response.getEntity().getContent();
                return inputStreamToString(in);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void executeResult(String response) {
        Bundle data = new Bundle();
        data.putString(RESPONSE, response);
        if (task == LOGIN) {
            data.putString(EMAIL, email);
            data.putString(PASSWORD, password);
        } else if (task == CHANGE_PASS) {
            data.putString(NEW_PASS, newPass);
        }
        receiver.send(task, data);
    }

    private String getInterests() {
        String interData = "";
        for (String item : interests) {
            interData += item + ",";
        }
        return interData.substring(0, interData.length() - 1);
    }

    private String inputStreamToString(InputStream is) throws IOException {
        String line;
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        while ((line = rd.readLine()) != null) {
            total.append(line);
        }

        // Return full string
        return total.toString();
    }
}

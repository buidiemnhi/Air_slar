package com.evilgeniustechnologies.airsla.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by benjamin on 2/23/14.
 */
public class UserPreferences {
    public static final String PREFS_NAME = "User Preferences";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String TOKEN = ".com.evilgeniustechnologies.airsla.";

    public static String convertToRawData(String title, String heading,
                                          String description, String author,
                                          String date, String duration, String link) {
        return title + TOKEN + heading + TOKEN +
                description + TOKEN + author + TOKEN +
                date + TOKEN + duration + TOKEN + link;
    }

    public static ArrayList<String> extractFromRawData(String data) {
        ArrayList<String> contents = new ArrayList<String>();
        Collections.addAll(contents, data.split(TOKEN));
        return contents;
    }

    public static String extractTarget(String target) {
        return target.substring(TOKEN.length());
    }

    public static String saveTarget(String target) {
        return TOKEN + target;
    }

    public static boolean isTarget(String target) {
        return target.contains(TOKEN);
    }

    public static boolean saveBroadcast(Context context, String link, String title,
                                        String heading, String description, String author,
                                        String date, String duration) {
        // Get user preferences
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(UserPreferences.PREFS_NAME, 0);
        // Do not modify this map
        final Map<String, ?> values = sharedPreferences.getAll();
        // Check if the broadcast already exist
        if (values != null && !values.isEmpty() && values.containsKey(saveTarget(link))) {
            return false;
        }
        // Save broadcast to user preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(saveTarget(link), convertToRawData(title, heading,
                description, author, date, duration, link));
        editor.commit();
        return true;
    }

    public static ArrayList<Broadcast> loadBroadcasts(Activity activity) {
        ArrayList<Broadcast> broadcasts = new ArrayList<Broadcast>();
        // Get user preferences
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(UserPreferences.PREFS_NAME, 0);
        // Do not modify this map
        final Map<String, ?> values = sharedPreferences.getAll();
        if (values != null && !values.isEmpty()) {
            for (Map.Entry<String, ?> entry : values.entrySet()) {
                if (UserPreferences.isTarget(entry.getKey())) {
                    List<String> raw = extractFromRawData((String) entry.getValue());
                    broadcasts.add(new Broadcast(raw.get(0), raw.get(1), raw.get(2),
                            raw.get(3), raw.get(4), raw.get(5), raw.get(6)));
                }
            }
        }
        final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        Collections.sort(broadcasts, new Comparator<Broadcast>() {
            @Override
            public int compare(Broadcast lhs, Broadcast rhs) {
                try {
                    Date lhsDate = format.parse(lhs.date);
                    Date rhsDate = format.parse(rhs.date);
                    return rhsDate.compareTo(lhsDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        return broadcasts;
    }

    public static class Broadcast implements Parcelable {
        public final String title;
        public final String heading;
        public final String description;
        public final String author;
        public final String date;
        public final String duration;
        public final String link;

        public Broadcast(String title, String heading, String description,
                         String author, String date, String duration, String link) {
            this.title = title;
            this.heading = heading;
            this.description = description;
            this.author = author;
            this.date = date;
            this.duration = duration;
            this.link = link;
        }

        public Broadcast(Parcel in) {
            String[] data = new String[7];
            in.readStringArray(data);
            title = data[0];
            heading = data[1];
            description = data[2];
            author = data[3];
            date = data[4];
            duration = data[5];
            link = data[6];
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(new String[]{title, heading, description,
                    author, date, duration, link});
        }

        public static final Creator CREATOR = new Creator() {
            @Override
            public Broadcast createFromParcel(Parcel source) {
                return new Broadcast(source);
            }

            @Override
            public Broadcast[] newArray(int size) {
                return new Broadcast[size];
            }
        };
    }
}

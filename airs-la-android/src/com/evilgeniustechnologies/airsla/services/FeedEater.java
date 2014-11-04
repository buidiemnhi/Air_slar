package com.evilgeniustechnologies.airsla.services;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by benjamin on 2/25/14.
 */
public class FeedEater {
    public static final String URL_PRO = "http://www.airsla.org/includes/projdata.xml";
    public static final String URL_RSS = "http://www.airsla.org/broadcasts/@rss.xml";
    public static final String URL_INFO = "http://airsla.org/m/iInfo.xml";

    public static final String P_ROOT = "projects";
    private final String P_SUB_ROOT = "proj";
    private final String P_NAME = "name";
    private final String P_PAGE_HEADING = "pageheading";
    private final String P_DESCRIPTION = "description";
    private final String P_TABLE_HEADING = "tableheading";
    private final String P_CATEGORIES = "cats";
    private final String P_CATEGORY = "cat";

    public static final String C_ROOT = "rss";
    private final String C_SUB_ROOT = "channel";
    private final String C_CHANNEL = "title";
    private final String C_ITEM = "item";
    private final String C_TITLE = "title";
    private final String C_DESCRIPTION = "description";
    private final String C_AUTHOR = "author";
    private final String C_DATE = "pubDate";
    private final String C_ENCLOSURE = "enclosure";
    private final String C_DURATION = "itunes:duration";
    private final String C_ATTRIBUTE_URL = "url";

    public static final String I_ROOT = "idata";
    public static final String I_SUB_ROOT = "categories";
    private final String I_CATEGORY = "category";
    private final String I_ATTRIBUTE_NAME = "catname";

    public static final String T_SUB_ROOT = "top5";
    private final String T_PROJECT = "project";
    private final String T_ATTRIBUTE_NAME = "projname";

    public static final String A_SUB_ROOT = "about";
    private final String A_PART = "part";
    private final String A_PARA = "para";
    private final String A_ATTRIBUTE_NAME = "heading";
    private final String S_P = "<p>";
    private final String E_P = "</p>";
    private final String S_B = "<b>";
    private final String E_B = "</b>";

    public static final String R_SUB_ROOT = "resources";
    private final String R_RESOURCE = "resource";
    private final String R_ATTRIBUTE_NAME = "resname";
    private final String R_ATTRIBUTE_URL = "url";

    private final String U_ADDRESS_1 = "address1";
    private final String U_ADDRESS_2 = "address2";
    private final String U_TELEPHONE = "telephone";
    private final String U_EMAIL = "email";

    private static final String ns = null;

    // Instantiates the parser
    public ArrayList parse(InputStream in, String root, String subRoot) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser, root, subRoot);
        } finally {
            in.close();
        }
    }

    // Reads the feed
    private ArrayList readFeed(XmlPullParser parser, String root, String subRoot) throws IOException, XmlPullParserException {
        ArrayList contents = new ArrayList();

        Log.e("read feed", "executed");

        if (root.equals(I_ROOT) && subRoot == null) {
            Log.e("read contact", "executed");
            contents.add(readContact(parser));
        } else {
            parser.require(XmlPullParser.START_TAG, ns, root);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                Log.e("current", name);
                // Starts by looking for the 'root' tag
                if (root.equals(P_ROOT)
                        && name.equals(P_SUB_ROOT)) {
                    Log.e("read projects", "executed");
                    contents.add(readProject(parser));
                } else if (root.equals(C_ROOT)
                        && name.equals(C_SUB_ROOT)) {
                    Log.e("read channels", "executed");
                    contents = readChannel(parser);
                } else if (root.equals(I_ROOT)) {
                    if (subRoot.equals(I_SUB_ROOT)
                            && name.equals(I_SUB_ROOT)) {
                        Log.e("read categories", "executed");
                        contents = readCategoriesTag(parser);
                    } else if (subRoot.equals(T_SUB_ROOT)
                            && name.equals(T_SUB_ROOT)) {
                        Log.e("read top 5", "executed");
                        contents = readTopFiveTag(parser);
                    } else if (subRoot.equals(A_SUB_ROOT)
                            && name.equals(A_SUB_ROOT)) {
                        Log.e("read about", "executed");
                        contents.add(readAbout(parser));
                    } else if (subRoot.equals(R_SUB_ROOT)
                            && name.equals(R_SUB_ROOT)) {
                        Log.e("read resources", "executed");
                        contents = readResourcesInfo(parser);
                    } else {
                        Log.e("skip", name);
                        skip(parser);
                    }
                } else {
                    Log.e("skip", name);
                    skip(parser);
                }
            }
        }
        Log.e("return contents", "executed");
        return contents;
    }

    public static class Project implements Parcelable {
        public final String name;
        public final String pageHeading;
        public final String description;
        public final String tableHeading;
        public final String categories;

        public Project(
                String name,
                String pageHeading,
                String description,
                String tableHeading,
                String categories
        ) {
            this.name = name;
            this.pageHeading = pageHeading;
            this.description = description;
            this.tableHeading = tableHeading;
            this.categories = categories;
        }

        public Project(Parcel in) {
            String data[] = new String[5];
            in.readStringArray(data);
            name = data[0];
            pageHeading = data[1];
            description = data[2];
            tableHeading = data[3];
            categories = data[4];
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(new String[]{name, pageHeading,
                    description, tableHeading, categories});
        }

        public static final Creator CREATOR = new Creator() {

            @Override
            public Project createFromParcel(Parcel source) {
                return new Project(source);
            }

            @Override
            public Project[] newArray(int size) {
                return new Project[size];
            }
        };
    }

    // Parses the contents of a project. If it encounters interested tags, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Project readProject(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, P_SUB_ROOT);
        String name = null;
        String pageHeading = null;
        String description = null;
        String tableHeading = null;
        String categories = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            if (tag.equals(P_NAME)) {
                name = readTag(parser, P_NAME);
            } else if (tag.equals(P_PAGE_HEADING)) {
                pageHeading = readTag(parser, P_PAGE_HEADING);
            } else if (tag.equals(P_DESCRIPTION)) {
                description = readTag(parser, P_DESCRIPTION);
            } else if (tag.equals(P_TABLE_HEADING)) {
                tableHeading = readTag(parser, P_TABLE_HEADING);
            } else if (tag.equals(P_CATEGORIES)) {
                categories = readCategories(parser);
            } else {
                skip(parser);
            }
        }
        return new Project(name, pageHeading, description, tableHeading, categories);
    }

    // Processes categories tags in the feed.
    private String readCategories(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, P_CATEGORIES);
        String categories = "";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            if (tag.equals(P_CATEGORY)) {
                categories += readTag(parser, P_CATEGORY) + "|";
            } else {
                skip(parser);
            }
        }
        // Uncomment the line below if you want to remove the last token "|"
//        return categories.substring(0, categories.length() - 1);
        return categories;
    }

    public static class Item implements Parcelable {
        public final String channel;
        public final String title;
        public final String description;
        public final String author;
        public final String date;
        public final String enclosure;
        public final String duration;

        public Item(
                String channel,
                String title,
                String description,
                String author,
                String date,
                String enclosure,
                String duration
        ) {
            this.channel = channel;
            this.title = title;
            this.description = description;
            this.author = author;
            this.date = date;
            this.enclosure = enclosure;
            this.duration = duration;
        }

        public Item(Parcel in) {
            String[] data = new String[6];
            in.readStringArray(data);
            channel = data[0];
            title = data[1];
            description = data[2];
            author = data[3];
            date = data[4];
            enclosure = data[5];
            duration = data[6];
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(new String[]{channel, title, description,
                    author, date, enclosure, duration});
        }

        public static final Creator CREATOR = new Creator() {
            @Override
            public Item createFromParcel(Parcel source) {
                return new Item(source);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };
    }

    // Read channel tag in the feed
    private ArrayList readChannel(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList contents = new ArrayList();
        String channel = null;

        parser.require(XmlPullParser.START_TAG, ns, C_SUB_ROOT);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Looks for the 'title' tag
            if (name.equals(C_CHANNEL)) {
                channel = readTag(parser, C_CHANNEL);
            } else if (name.equals(C_ITEM)) {
                contents.add(readItem(parser, channel));
            } else {
                skip(parser);
            }
        }
        return contents;
    }

    // Parses the contents of an item. If it encounters interested tags, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag
    private Item readItem(XmlPullParser parser, String channel) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, C_ITEM);
        String title = null;
        String description = null;
        String author = null;
        String date = null;
        String enclosure = null;
        String duration = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            if (tag.equals(C_TITLE)) {
                title = readTag(parser, C_TITLE);
            } else if (tag.equals(C_DESCRIPTION)) {
                description = readTag(parser, C_DESCRIPTION);
            } else if (tag.equals(C_AUTHOR)) {
                author = readTag(parser, C_AUTHOR);
            } else if (tag.equals(C_DATE)) {
                date = readTag(parser, C_DATE);
            } else if (tag.equals(C_ENCLOSURE)) {
                enclosure = readEnclosure(parser);
            } else if (tag.equals(C_DURATION)) {
                duration = readTag(parser, C_DURATION);
            } else {
                skip(parser);
            }
        }
        return new Item(channel, title, description, author, date, enclosure, duration);
    }

    // Processes enclosure tags in the feed
    private String readEnclosure(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, C_ENCLOSURE);
        String tag = parser.getName();
        if (tag.equals(C_ENCLOSURE)) {
            link = parser.getAttributeValue(null, C_ATTRIBUTE_URL);
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, C_ENCLOSURE);
        return link;
    }

    public static class Category implements Parcelable {
        public final String category;
        public final String cateName;

        public Category(String category, String catName) {
            this.category = category;
            this.cateName = catName;
        }

        public Category(Parcel in) {
            String[] data = new String[2];
            in.readStringArray(data);
            category = data[0];
            cateName = data[1];
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(new String[]{category, cateName});
        }

        public static final Creator CREATOR = new Creator() {
            @Override
            public Category createFromParcel(Parcel source) {
                return new Category(source);
            }

            @Override
            public Category[] newArray(int size) {
                return new Category[size];
            }
        };
    }

    // Read categories tag in the feed
    private ArrayList readCategoriesTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList contents = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, I_SUB_ROOT);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the 'category' tag
            if (name.equals(I_CATEGORY)) {
                contents.add(readCategoryInformation(parser));
            } else {
                skip(parser);
            }
        }
        return contents;
    }

    // Processes category info in the feed
    private Category readCategoryInformation(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, I_CATEGORY);
        String category = parser.getAttributeValue(null, I_ATTRIBUTE_NAME);
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, I_CATEGORY);
        return new Category(category, name);
    }

    public static class TopProject implements Parcelable {
        public final String proName;
        public final String project;

        public TopProject(String project, String proName) {
            this.project = project;
            this.proName = proName;
        }

        public TopProject(Parcel in) {
            String[] data = new String[2];
            in.readStringArray(data);
            project = data[0];
            proName = data[1];
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(new String[]{project, proName});
        }

        public static final Creator CREATOR = new Creator() {
            @Override
            public TopProject createFromParcel(Parcel source) {
                return new TopProject(source);
            }

            @Override
            public TopProject[] newArray(int size) {
                return new TopProject[size];
            }
        };
    }

    // Read top 5 tag in the feed
    private ArrayList readTopFiveTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList contents = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, T_SUB_ROOT);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the 'category' tag
            if (name.equals(T_PROJECT)) {
                contents.add(readTopProject(parser));
            } else {
                skip(parser);
            }
        }
        return contents;
    }

    // Read top 5 project tags in the feed
    private TopProject readTopProject(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, T_PROJECT);
        String project = parser.getAttributeValue(null, T_ATTRIBUTE_NAME);
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, T_PROJECT);
        return new TopProject(project, name);
    }

    public static class About implements Parcelable {
        public final String paragraph;

        public About(String paragraph) {
            this.paragraph = paragraph;
        }

        public About(Parcel in) {
            String[] data = new String[1];
            in.readStringArray(data);
            paragraph = data[0];
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(new String[]{paragraph});
        }

        public static final Creator CREATOR = new Creator() {
            @Override
            public About createFromParcel(Parcel source) {
                return new About(source);
            }

            @Override
            public About[] newArray(int size) {
                return new About[size];
            }
        };
    }

    // Read about tag in the feed
    private About readAbout(XmlPullParser parser) throws IOException, XmlPullParserException {
        String contents = "";

        parser.require(XmlPullParser.START_TAG, ns, A_SUB_ROOT);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            // Starts by looking for the 'part' tag
            if (tag.equals(A_PART)) {
                contents += S_P + S_B
                        + parser.getAttributeValue(null, A_ATTRIBUTE_NAME)
                        + E_B + E_P;
                contents += readPart(parser);
            } else {
                skip(parser);
            }
        }
        return new About(contents);
    }

    // Read part tag in the feed
    private String readPart(XmlPullParser parser) throws IOException, XmlPullParserException {
        String contents = "";

        parser.require(XmlPullParser.START_TAG, ns, A_PART);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the 'para' tag
            if (name.equals(A_PARA)) {
                contents += S_P + readTag(parser, A_PARA) + E_P;
            } else {
                skip(parser);
            }
        }
        return contents;
    }

    public static class Resource implements Parcelable {
        public final String name;
        public final String url;
        public final String description;

        public Resource(String name, String url, String description) {
            this.name = name;
            this.url = url;
            this.description = description;
        }

        public Resource(Parcel in) {
            String[] data = new String[3];
            in.readStringArray(data);
            name = data[0];
            url = data[1];
            description = data[2];
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(new String[]{name, url, description});
        }

        public static final Creator CREATOR = new Creator() {
            @Override
            public Resource createFromParcel(Parcel source) {
                return new Resource(source);
            }

            @Override
            public Resource[] newArray(int size) {
                return new Resource[size];
            }
        };
    }

    // Read resources tag in the feed
    private ArrayList readResourcesInfo(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList contents = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, R_SUB_ROOT);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            // Starts by looking for the 'resource' tag in the feed
            if (tag.equals(R_RESOURCE)) {
                contents.add(readResource(parser));
            } else {
                skip(parser);
            }
        }
        return contents;
    }

    // Read resource tags in the feed
    private Resource readResource(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, R_RESOURCE);
        String name = parser.getAttributeValue(null, R_ATTRIBUTE_NAME);
        String link = parser.getAttributeValue(null, R_ATTRIBUTE_URL);
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, R_RESOURCE);
        return new Resource(name, link, description);
    }

    public static class Contact implements Parcelable {
        public final String address1;
        public final String address2;
        public final String telephone;
        public final String email;

        public Contact(String address1, String address2, String telephone, String email) {
            this.address1 = address1;
            this.address2 = address2;
            this.telephone = telephone;
            this.email = email;
        }

        public Contact(Parcel in) {
            String[] data = new String[4];
            in.readStringArray(data);
            address1 = data[0];
            address2 = data[1];
            telephone = data[2];
            email = data[3];
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(new String[]{address1, address2, telephone, email});
        }

        public static final Creator CREATOR = new Creator() {
            @Override
            public Contact createFromParcel(Parcel source) {
                return new Contact(source);
            }

            @Override
            public Contact[] newArray(int size) {
                return new Contact[size];
            }
        };
    }

    // Read contact tags in the feed
    private Contact readContact(XmlPullParser parser) throws IOException, XmlPullParserException {
        String address1 = null;
        String address2 = null;
        String telephone = null;
        String email = null;

        parser.require(XmlPullParser.START_TAG, ns, I_ROOT);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            // Starts by looking for the contact tags in the feed
            if (tag.equals(U_ADDRESS_1)) {
                address1 = readTag(parser, U_ADDRESS_1);
            } else if (tag.equals(U_ADDRESS_2)) {
                address2 = readTag(parser, U_ADDRESS_2);
            } else if (tag.equals(U_TELEPHONE)) {
                telephone = readTag(parser, U_TELEPHONE);
            } else if (tag.equals(U_EMAIL)) {
                email = readTag(parser, U_EMAIL);
            } else {
                skip(parser);
            }
        }
        return new Contact(address1, address2, telephone, email);
    }

    // Processes tags in the feed.
    private String readTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return name;
    }

    // Extracts value from the tags.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips unnecessary tags
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}

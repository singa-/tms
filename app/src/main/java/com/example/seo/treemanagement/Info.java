package com.example.seo.treemanagement;

/**
 * Created by seo on 9/30/15.
 */

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class Info {

    public ArrayList<History> mhistories;
    public Tree mtree;

    public Info(Tree tree,ArrayList<History> histories){
        this.mtree = tree;
        this.mhistories = histories;
    }

    public class Tree{
        public String mtagId;
        public String mtreeName;
        public String mtype;
        public String mtreeDate;

        public Tree(String tagId,String treeName,String type,String treeDate){
            this.mtagId = tagId;
            this.mtreeName = treeName;
            this.mtype = type;
            this.mtreeDate = treeDate;
        }
    }

    public class History{
        public String mHistoryId;
        public String mhistoryDate;
        public String mActivity;
        public String mNote;
        public String mManager;

        public History(String historyId,String historyDate,String activity,String note,String manager){
            this.mHistoryId = historyId;
            this.mhistoryDate = historyDate;
            this.mNote = note;
            this.mActivity = activity;
            this.mManager = manager;
        }
    }

    public ArrayList<History> readHistory(XmlPullParser parser,ArrayList<History> items) throws XmlPullParserException, IOException {

        String historyId = null;
        String historyDate = null;
        String activity = null;
        String note = null;
        String manager = null;

        while (parser.getEventType()  != XmlPullParser.END_DOCUMENT) {
            parser.require(XmlPullParser.START_TAG, null, "history");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("id")) {
                    historyId = readString(parser, "id");
                } else if (name.equals("date")) {
                    historyDate = readString(parser, "date");
                } else if (name.equals("activity")) {
                    activity = readString(parser, "activity");
                } else if (name.equals("text")) {
                    note = readString(parser, "text");
                } else if (name.equals("manager")) {
                    manager = readString(parser, "manager");
                } else {
                    ;
                }
            }

            items.add(new History(historyId,historyDate,activity,note,manager));
        }

        //add
        return items;
    }

    public Tree readTree(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "tree");
        String tagId = null;
        String treeName = null;
        String type = null;
        String treeDate = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("id")) {
                tagId = readString(parser, "id");
            } else if (name.equals("name")) {
                treeName = readString(parser, "name");
            } else if (name.equals("type")) {
                type = readString(parser, "type");
            } else if (name.equals("date")) {
                treeDate = readString(parser, "date");
            } else {
                ;
            }
        }
        return new Tree(tagId, treeName, type, treeDate);
    }

    private String readString(XmlPullParser parser,String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        String string = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, tag);
        return string;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}


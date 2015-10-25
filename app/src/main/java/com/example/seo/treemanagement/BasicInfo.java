package com.example.seo.treemanagement;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by seo on 9/30/15.
 */
public class BasicInfo {

    public String mtagId;
    public String mtreeName;
    public String mtype;
    public String mtreeDate;

    public BasicInfo(String tagId,String treeName,String type,String treeDate){
        this.mtagId = tagId;
        this.mtreeName = treeName;
        this.mtype = type;
        this.mtreeDate = treeDate;
    }

    public BasicInfo readTree(XmlPullParser parser) throws XmlPullParserException, IOException {
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
        return new BasicInfo(tagId, treeName, type, treeDate);
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

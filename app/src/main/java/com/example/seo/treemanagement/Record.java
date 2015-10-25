package com.example.seo.treemanagement;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by seo on 9/21/15.
 */
public class Record {

    private String m_RecordId;
    private String m_szDateOfEditInfo;
    private String m_szActivityInfo;
    private String m_szNoteInfo;
    private String m_szNameOfAdminInfo;

    public Record( String RecordId, String DateOfEditInfo, String ActivityInfo, String NoteInfo, String NameOfAdminInfo) {
        this.m_RecordId = RecordId;
        this.m_szDateOfEditInfo = DateOfEditInfo;
        this.m_szActivityInfo = ActivityInfo;
        this.m_szNoteInfo = NoteInfo;
        this.m_szNameOfAdminInfo = NameOfAdminInfo;
    }

    public String getRecordId() { return m_RecordId; }
    public void setRecordId(String RecordId) { this.m_szDateOfEditInfo = RecordId;}

    public String getDateOfEditInfo() { return m_szDateOfEditInfo; }
    public void setDateOfEditInfo(String DateOfEditInfo) { this.m_szDateOfEditInfo = DateOfEditInfo;}

    public String getActivityInfo() {return m_szActivityInfo;}
    public void setActivityInfo(String deviceAddress) {this.m_szDateOfEditInfo = deviceAddress;}

    public String getNoteInfo() { return m_szNoteInfo; }
    public void setDeviceType(String NoteInfo) { this.m_szDateOfEditInfo = NoteInfo;}

    public String getNameOfAdminInfo() { return m_szNameOfAdminInfo; }
    public void setNameOfAdminInfo(String NameOfAdminInfo) { this.m_szNameOfAdminInfo = NameOfAdminInfo;}

}
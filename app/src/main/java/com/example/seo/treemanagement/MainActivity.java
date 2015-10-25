package com.example.seo.treemanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import android.app.Dialog;

import android.content.Intent;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends AppCompatActivity {

    private ListView mListView = null;
    private CustomAdapter lvAdapter;
    private ArrayList<Record> m_records;
    //private ArrayList<Info.History> histories;

    public static final int EDIT_BASIC_INFO =1;
    public static final int EDIT_RECORD =2;

    public static final int NAME = 1;
    public static final int SPECIES = 2;
    public static final int DATE = 3;

    TextView TagId;
    TextView NameOfTree;
    TextView SpeciesOfTree;
    TextView DateOfPlanting;

    NfcAdapter mNfcAdapter;
    AlertDialog mDialog;

    byte[] ReadByteArray;

    private static final String TAG = "TMSMainActivity";

    private Toolbar toolbar;
    ImageButton FAB;

    boolean Flag_EditBasicInfoCalled = false;
    boolean Flag_EditRecordCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate() is called");

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(intent, EDIT_RECORD);
            }
        });

        TagId = (TextView) findViewById(R.id.TagIdInfo);
        NameOfTree = (TextView) findViewById(R.id.NameOfTreeInfo);
        SpeciesOfTree = (TextView) findViewById(R.id.SpeciesOfTreeInfo);
        DateOfPlanting = (TextView) findViewById(R.id.DateOfPlantingInfo);

        //ListView ls2 = new ListView(getApplicationContext());
        mListView = (ListView) findViewById(R.id.mlistview);
        // clear previous results in the LV
        mListView.setAdapter(null);
        // populate
        m_records = new ArrayList<Record>();

        lvAdapter =  new CustomAdapter(getApplicationContext(), m_records);
        mListView.setAdapter(lvAdapter);

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mDialog = createDialog();
        mDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() is called");
        // Check which request we're responding to
         switch(requestCode)
        {
            case EDIT_RECORD:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "resultCode is RESULT_OK");
                    String DateOfEditInfo = data.getStringExtra("DateOfEditInfo");
                    String ActivityInfo = data.getStringExtra("ActivityInfo");
                    String NoteInfo = data.getStringExtra("NoteInfo");
                    String NameOfAdminInfo = data.getStringExtra("NameOfAdminInfo");
                    //Log.d(TAG, DateOfEditInfo+" "+ActivityInfo+" "+NoteInfo+" "+NameOfAdminInfo);
                    //Toast.makeText(this,DateOfEditInfo+" "+ActivityInfo+" "+NoteInfo+" "+NameOfAdminInfo, Toast.LENGTH_LONG).show();
                    //byte[] EditedByteArray = EditedString.getBytes(Charset.forName("UTF-8"));
                    Record record = new Record("",DateOfEditInfo,ActivityInfo,NoteInfo,NameOfAdminInfo);
                    m_records.add(record);
                    lvAdapter.notifyDataSetChanged();

                    Flag_EditRecordCalled = true;

                    PostDataToServer();

                    //send data to server to update record
                }
                break;

            case EDIT_BASIC_INFO:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "resultCode is RESULT_OK");

                    String STagIdInfo = data.getStringExtra("TagIdInfo");
                    String SNameOfTreeInfo =  data.getStringExtra("NameOfTreeInfo");
                    String SSpeciesOfTreeInfo =  data.getStringExtra("SpeciesOfTreeInfo");
                    String SDateOfPlantingInfo =  data.getStringExtra("DateOfPlantingInfo");

                    setBasicInfo(STagIdInfo,SNameOfTreeInfo, SSpeciesOfTreeInfo,SDateOfPlantingInfo);

                    Flag_EditBasicInfoCalled = true;

                    Log.d(TAG, "EDIT_BASIC_INFO:\"+TagIdInfo+\" \"+NameOfTreeInfo+\" \"+SpeciesOfTreeInfo+\" \"+DateOfPlantingInfo");

                    //send data to server to update basic info
                }
                break;
            default:
                break;
        }

    }

    void setBasicInfo(String TagIdInfo,String NameOfTreeInfo, String SpeciesOfTreeInfo,String DateOfPlantingInfo)
    {
        TagId.setText(TagIdInfo);
        NameOfTree.setText(NameOfTreeInfo);
        SpeciesOfTree.setText(SpeciesOfTreeInfo);
        DateOfPlanting.setText(DateOfPlantingInfo);
    }

    @Override
    protected void onResume(){
        super.onResume();

        Log.d(TAG, "onResume() is called");

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            setDismiss(mDialog);

            if(Flag_EditBasicInfoCalled == false) {
                processIntent(getIntent());
                if(Flag_EditRecordCalled != true)
                    getDataFromServer();
            }
            else
                Flag_EditBasicInfoCalled = false;
        }
    }

    protected void getDataFromServer(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if(TagId.getText().toString() != null)
                new DownloadFromWebTask().execute("http://tms8099.cafe24.com/tree_detail_xml.jsp?ID="+TagId.getText().toString());
            else
                Toast.makeText(this, "Tag ID is null..", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Internet is not connected. Check your network connection.", Toast.LENGTH_LONG).show();
        }
    }

    protected void PostDataToServer(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if(TagId.getText().toString() != null)
                //new DownloadFromWebTask().execute("http://tms8099.cafe24.com/tree_detail_xml.jsp?ID="+TagId.getText().toString());
                new PostTomWebTask().execute("http://tms8099.cafe24.com/history_add.jsp");
            else
                Toast.makeText(this, "Tag ID is null..", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Internet is not connected. Check your network connection.", Toast.LENGTH_LONG).show();
        }
    }

    private class PostTomWebTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... urls) {
            Log.d(TAG, "PostTomWebTask doInBackground is called");
            // params comes from the execute() call: params[0] is the url.
            //uploadUrl(urls[0]);


            //upload(urls[0]);
            try {
                return upload(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }

        }

        protected void onPostExecute(int a) {
            Log.d(TAG, "onPostExecute is called");
        }
    }

    private class DownloadFromWebTask extends AsyncTask<String, Void, ArrayList<Record>> {
        @Override
        protected ArrayList<Record> doInBackground(String... urls) {
            Log.d(TAG, "doInBackground is called");
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return m_records;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return m_records;
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<Record> result) {
            //textView.setText(result);
            //Log.d(TAG, "HTTP response text is: " + result);

            lvAdapter.notifyDataSetChanged();
        }
    }

    private Integer upload(String myurl) throws IOException {
        //InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        //int len = 500;
        //String contentAsString;
        int a = 1;
        Log.d(TAG, "uploadURL is called");

        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        //conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setChunkedStreamingMode(0);
        conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

        //make data
        StringBuffer buffer = new StringBuffer();
        buffer.append("<input type=\"hidden\" name=\"tree_id\" value=\"04b749823f2b80\"/><br />\n" +
                "<input type=\"text\" name=\"activity\" value=\"a\" /> <br />\n" +
                "<input type=\"text\" name=\"name\" value=\"a\" /><br />\n" +
                "<input type=\"text\" name=\"date\" value=\"1980-01-01\" />1980-01-01<br />\n" +
                "<input type=\"text\" name=\"text\" value=\"a\" /> <br /><br />\n" +
                "<input type=\"submit\" value=\"확인submit\" />");


        OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "EUC-KR");
        PrintWriter writer = new PrintWriter(outStream);
        writer.write(buffer.toString());
        writer.flush();

        return a;
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private ArrayList<Record> downloadUrl(String myurl) throws IOException, XmlPullParserException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        //int len = 500;
        //String contentAsString;

        Log.d(TAG, "downloadUrl is called");

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            Log.d(TAG, "The response is: " + String.valueOf(conn.getResponseCode()));
            is = new BufferedInputStream(conn.getInputStream());

            //is = conn.getInputStream();

            return parse(is);

        } finally {
            if (is != null) {
                is.close();
            }
         }
    }

    ArrayList<Record> parse(InputStream in) throws XmlPullParserException, IOException {

        Log.d(TAG,"parse is called");
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(in, "UTF-8");

        parser.nextTag();

        boolean conditionNotMeet = true;
        int i = 0;

        while(conditionNotMeet == true) {

            if((parser.getName() != null)&&(parser.getEventType() == XmlPullParser.START_TAG )){
                if(parser.getName().equals("history"))
                    conditionNotMeet = false;
            }

            Log.d(TAG, String.valueOf(i) + " getEventType is " + String.valueOf(parser.getEventType()) + " | getName is " + parser.getName());

            if (conditionNotMeet != false)
                parser.next();
            i++;
            if(i == 90)
                break;
        }

        while(parser.getEventType() != XmlPullParser.END_DOCUMENT){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                parser.next();
                continue;
            }
            m_records.add(readRecord(parser));
        }

        return m_records;
    }

    public Record readRecord(XmlPullParser parser) throws XmlPullParserException, IOException {

        Log.d(TAG,"readRecord is called");
        parser.require(XmlPullParser.START_TAG, null, "history");

        String historyId = null;
        String historyDate = null;
        String activity = null;
        String note = null;
        String manager = null;

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
        parser.next();
        return new Record(historyId, historyDate, activity, note, manager);
    }

    public String readString(XmlPullParser parser,String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        String string = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, tag);
        return string;
    }

    public String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     * @throws FormatException
     * @throws IOException
     */
    void processIntent(Intent intent) {

        Log.d(TAG, "processIntent is Called!!");
        Tag mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        TagId.setText(byteArrayToHex(mytag.getId()));

        //TagInfo = (TextView) findViewById(R.id.TagInfo);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present

        ReadByteArray = msg.getRecords()[0].getPayload();

        if (ReadByteArray == null)
        {
            Log.d(TAG,"ReadByteArray is null");
        }else
            parseNSetData();



    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    // Set data to textview
    void parseNSetData()
    {
        int i;
        int IdxOfPreDel=0,ItemDipCnt = 0;
        boolean FirstDataFlag = false;

        for (i=0; i< ReadByteArray.length; i++)
        {
            //Log.d(TAG, "i ="+String.valueOf(i)+" value:"+String.valueOf(ReadByteArray[i]));

            if(ReadByteArray[i] == 124 )// '|' == 124
            {
                //Log.d(TAG, "Delimeter detected!!");
                if(FirstDataFlag == false) {
                    //Log.d(TAG, "FirstDataFlag is false!!");

                    //byte[] DataToWrite = new byte[i-3];
                    //System.arraycopy(ReadByteArray,3,DataToWrite,0,i-3);
                    //NameOfTree.setText(new String(DataToWrite));
                    ItemDipCnt++;
                    FirstDataFlag=true;
                }else{
                    //Log.d(TAG, "(i-IdxOfPreDel-1) ="+ String.valueOf((i-IdxOfPreDel-1)));
                    if((i-IdxOfPreDel-1) != 0)
                    {
                        byte[] DataToWrite = new byte[i-IdxOfPreDel-1];
                        System.arraycopy(ReadByteArray,IdxOfPreDel+1,DataToWrite,0,i-IdxOfPreDel-1);
                        switch(ItemDipCnt)
                        {

                            case NAME:
                                //Log.d(TAG, "NameOfTree.setText");
                                NameOfTree.setText(new String(DataToWrite));
                                ItemDipCnt++;
                                break;
                            case SPECIES:
                                //Log.d(TAG, "SpeciesOfTree.setText");
                                SpeciesOfTree.setText(new String(DataToWrite));
                                ItemDipCnt++;
                                break;
                            case DATE:
                                //Log.d(TAG, "DateOfPlanting.setText");
                                DateOfPlanting.setText(new String(DataToWrite));
                                ItemDipCnt++;
                                break;
                            default:
                                break;
                        }
                    }
                    else
                        ItemDipCnt++;

                }
                IdxOfPreDel = i;
            }
        }
    }

    AlertDialog createDialog() {
        Log.d(TAG, "createDialog is Called!!");
        //final View innerView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        //ab.setTitle("Title");
        //ab.setView(innerView);
        ab.setMessage("Attach Tag");

        return ab.create();
    }

    void setDismiss(Dialog dialog){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_edit) {
            //start edit basic info activity
            Intent intent = new Intent(MainActivity.this,TagEditActivity.class);
            intent.putExtra("TagIdInfo", TagId.getText().toString());
            intent.putExtra("NameOfTreeInfo", NameOfTree.getText().toString());
            intent.putExtra("SpeciesOfTreeInfo", SpeciesOfTree.getText().toString());
            intent.putExtra("DateOfPlantingInfo", DateOfPlanting.getText().toString());

            startActivityForResult(intent, EDIT_BASIC_INFO);

            return true;
        }

        if (id == R.id.menu_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

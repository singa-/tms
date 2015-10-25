package com.example.seo.treemanagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class TagEditActivity extends AppCompatActivity {

    ImageButton FAB;

    public static final int EDIT_BASIC_INFO =1;

    private TextView TagIdInfo;
    private EditText NameOfTreeInfo;
    private EditText SpeciesOfTreeInfo;
    private EditText DateOfPlantingInfo;

    NfcAdapter mNfcAdapter;
    Tag mytag;
    PendingIntent pendingIntent;
    IntentFilter[] intentFiltersArray;
    String[][] techListsArray;
    boolean write_Dialog_acitve = false;
    AlertDialog mDialog;
    String StringToWrite;

    byte[] ReadDataArray;

    private static final String TAG = "WriteTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_edit);

        FAB = (ImageButton) findViewById(R.id.TagEditImageButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringToWrite = "|"+NameOfTreeInfo.getText().toString()
                        +"|"+SpeciesOfTreeInfo.getText().toString()
                        +"|"+DateOfPlantingInfo.getText().toString()
                        +"|";

                mDialog = createDialog();
                mDialog.show();
            }
        });

        TagIdInfo = (TextView) findViewById(R.id.TagIdInfo);
        NameOfTreeInfo = (EditText) findViewById(R.id.NameOfTreeInfo);
        SpeciesOfTreeInfo = (EditText) findViewById(R.id.SpeciesOfTreeInfo);
        DateOfPlantingInfo = (EditText) findViewById(R.id.DateOfPlantingInfo);

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                           You should specify only the ones that you need. */
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        intentFiltersArray = new IntentFilter[] {ndef, };

        techListsArray = new String[][] { new String[] { NfcF.class.getName() } };

        if(getIntent().getStringExtra("TagIdInfo") != null)
        {
            Log.d(TAG, "getIntent().getStringExtra(\"TagId\") is not null!!");
            //TagIdInfo.setText(getIntent().getExtras().getByteArray("TagId"));

            TagIdInfo.setText(getIntent().getStringExtra("TagIdInfo"));
            NameOfTreeInfo.setText(getIntent().getStringExtra("NameOfTreeInfo"));
            SpeciesOfTreeInfo.setText(getIntent().getStringExtra("SpeciesOfTreeInfo"));
            DateOfPlantingInfo.setText(getIntent().getStringExtra("DateOfPlantingInfo"));
            /* parseNSetData(ReadDataArray); */
        }

    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        Log.d(TAG, "createRecord is Called!!");
        //create the message in according with the standard
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        // copy lang bytes and text bytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return recordNFC;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() is called");
        // Check which request we're responding to
        if (requestCode == EDIT_BASIC_INFO) {
            Log.d(TAG, "requestCode isEDIT_BASIC_INFO");
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "resultCode is RESULT_OK");
                TagIdInfo.setText(data.getStringExtra("TagId"));
            }
        }
    }

    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume is Called!!");
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent is Called!!");

        mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //do something with tagFromIntent
        if( mDialog != null)
        {
            if(mDialog.isShowing() == true)
            {
                try {
                    writeTag(StringToWrite, mytag);
                    setDismiss(mDialog);

                    Intent toMainIntent = new Intent();
                    String STagIdInfo = TagIdInfo.getText().toString();
                    String SNameOfTreeInfo = NameOfTreeInfo.getText().toString();
                    String SSpeciesOfTreeInfo = SpeciesOfTreeInfo.getText().toString();
                    String SDateOfPlantingInfo = DateOfPlantingInfo.getText().toString();

                    toMainIntent.putExtra("TagIdInfo", STagIdInfo);
                    toMainIntent.putExtra("NameOfTreeInfo", SNameOfTreeInfo);
                    toMainIntent.putExtra("SpeciesOfTreeInfo", SSpeciesOfTreeInfo);
                    toMainIntent.putExtra("DateOfPlantingInfo", SDateOfPlantingInfo);

                    Log.d(TAG, "putExtra: " + STagIdInfo + " " + SNameOfTreeInfo + " " + SSpeciesOfTreeInfo +" "+ SDateOfPlantingInfo);
                    intent.setFlags(toMainIntent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    setResult(RESULT_OK, toMainIntent);
                    finish();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeTag(String text, Tag tag) throws IOException, FormatException {
        Log.d(TAG, "write is Called!!");
        Vibrator mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        NdefRecord[] records = { createRecord(text),NdefRecord.createApplicationRecord("com.example.seo.treemanagement") };
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        Log.d(TAG, "This tag can be readOnly?? " + String.valueOf(ndef.canMakeReadOnly()));
        ndef.writeNdefMessage(message);
        mVibrator.vibrate(300);
        Toast.makeText(getApplicationContext(), "Write is done!!", Toast.LENGTH_LONG).show();
        ndef.close();

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

}

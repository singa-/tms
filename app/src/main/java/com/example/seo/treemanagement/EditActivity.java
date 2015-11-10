package com.example.seo.treemanagement;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.nio.charset.Charset;


public class EditActivity extends AppCompatActivity {

    private EditText DateOfEditInfo;
    private EditText ActivityInfo;
    private EditText NoteInfo;
    private EditText NameOfAdminInfo;

    public static final int EDIT_RECORD =2;

    ImageButton FAB;

    byte[] EditedByteArray;

    private static final String TAG = "EditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        DateOfEditInfo = (EditText) findViewById(R.id.DateOfEditInfo);
        ActivityInfo = (EditText) findViewById(R.id.ActivityInfo);
        NoteInfo = (EditText) findViewById(R.id.NoteInfo);
        NameOfAdminInfo = (EditText) findViewById(R.id.NameOfAdminInfo);

        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.fab_edit);
        //FAB = (ImageButton) findViewById(R.id.EditImageButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                String EditedString =
                        DateOfEditInfo.getText().toString()
                                +"@"+ActvityInfo.getText().toString()
                                +"@"+NoteInfo.getText().toString()
                                +"@"+NameOfAdminInfo.getText().toString();
                */
                Log.d(TAG, DateOfEditInfo.getText().toString());
                Log.d(TAG, ActivityInfo.getText().toString());
                Log.d(TAG, NoteInfo.getText().toString());
                Log.d(TAG, NameOfAdminInfo.getText().toString());

                Intent intent = new Intent();
                intent.putExtra("DateOfEditInfo",DateOfEditInfo.getText().toString());
                intent.putExtra("ActivityInfo",ActivityInfo.getText().toString());
                intent.putExtra("NoteInfo",NoteInfo.getText().toString());
                intent.putExtra("NameOfAdminInfo",NameOfAdminInfo.getText().toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

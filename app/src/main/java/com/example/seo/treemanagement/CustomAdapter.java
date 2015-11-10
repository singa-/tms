package com.example.seo.treemanagement;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by seo on 9/20/15.
 */
public class CustomAdapter extends BaseAdapter /*implements OnClickListener*/ {

	/*private class OnItemClickListener implements OnClickListener{
	    private int mPosition;
	    OnItemClickListener(int position){
	            mPosition = position;
	    }
	    public void onClick(View arg0) {
	            Log.v("ddd", "onItemClick at position" + mPosition);
	    }
	}*/

    public static final String LOG_TAG = "CustomAdapter";
    private Context context;
    private List<Record> recordList;

    public CustomAdapter(Context context, List<Record> recordList ) {
        this.context = context;
        this.recordList = recordList;
    }

    public int getCount() {
        return recordList.size();
    }

    public Object getItem(int position) {
        return recordList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        Record record = recordList.get(position);
        View v = new CustomAdapterView(this.context, record );

        //v.setBackgroundColor((position % 2) == 1 ? Color.rgb(50,50,50) : Color.BLACK);
        //v.setBackgroundColor(Color.parseColor("#00d27f"));
        //v.setBackgroundColor(Color.parseColor("#b6fcd5"));
        v.setBackgroundColor(Color.parseColor("#cccccc"));
        /*v.setOnClickListener(new OnItemClickListener(position));*/
        return v;
    }

    /*public void onClick(View v) {
            Log.v(LOG_TAG, "Row button clicked");
    }*/

}
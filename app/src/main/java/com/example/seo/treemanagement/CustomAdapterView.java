package com.example.seo.treemanagement;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by seo on 9/21/15.
 */
class CustomAdapterView extends LinearLayout {
    public CustomAdapterView(Context context, Record record)
    {
        super( context );
		/*setOnClickListener((OnClickListener) context);
		setClickable(true);
		setFocusable(false);*/
        //setId(record.getDateOfEditInfo());

        //container is a horizontal layer
        //setOrientation(LinearLayout.HORIZONTAL);
        //setPadding(0, 6, 0, 6);

        //image:params
        //LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //Params.setMargins(15, 15, 15, 15);
        //setPadding(16, 16, 16, 16);

        //vertical layer for text
        //RelativeLayout.LayoutParams Params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        //LinearLayout PanelV = new LinearLayout(context);
        //RelativeLayout PanelV = new RelativeLayout(context);
        //PanelV.setOrientation(LinearLayout.VERTICAL);
        //PanelV.setGravity(Gravity.BOTTOM);
        setOrientation(LinearLayout.HORIZONTAL);
        setPadding(30, 0, 15, 15);

        LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout PanelMeta = new LinearLayout(context);
        Params.setMargins(15, 15, 15, 15);
        PanelMeta.setOrientation(LinearLayout.VERTICAL);

        TextView DateOfEdit = new TextView( context );
        DateOfEdit.setTextSize(15);
        DateOfEdit.setText("날짜");
        DateOfEdit.setTextColor(Color.parseColor("#ffffff"));
        PanelMeta.addView(DateOfEdit);

        TextView Activity = new TextView( context );
        Activity.setTextSize(15);
        Activity.setText("행위");
        Activity.setTextColor(Color.parseColor("#ffffff"));
        PanelMeta.addView(Activity);

        TextView Note = new TextView( context );
        Note.setTextSize(15);
        Note.setTextColor(Color.parseColor("#ffffff"));
        Note.setText("비고");
        PanelMeta.addView(Note);

        TextView NameOfAdmin = new TextView( context );
        NameOfAdmin.setTextSize(15);
        NameOfAdmin.setTextColor(Color.parseColor("#ffffff"));
        NameOfAdmin.setText("관리자명");
        PanelMeta.addView(NameOfAdmin);

        addView(PanelMeta, Params);

        LinearLayout PanelData = new LinearLayout(context);
        Params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        Params.setMargins(30, 15, 15, 15);
        PanelData.setOrientation(LinearLayout.VERTICAL);
        //PanelV.setGravity(Gravity.BOTTOM);

        TextView DateOfEditInfo = new TextView( context );
        DateOfEditInfo.setTextSize(15);
        DateOfEditInfo.setText(record.getDateOfEditInfo());
        DateOfEditInfo.setTextColor(Color.parseColor("#ffffff"));
        PanelData.addView(DateOfEditInfo);

        TextView ActivityInfo = new TextView( context );
        ActivityInfo.setTextSize(15);
        ActivityInfo.setText(record.getActivityInfo());
        ActivityInfo.setTextColor(Color.parseColor("#ffffff"));
        PanelData.addView(ActivityInfo);

        TextView NoteInfo = new TextView( context );
        NoteInfo.setTextSize(15);
        NoteInfo.setText(record.getNoteInfo());
        NoteInfo.setTextColor(Color.parseColor("#ffffff"));
        PanelData.addView(NoteInfo);

        TextView NameOfAdminInfo = new TextView( context );
        NameOfAdminInfo.setTextSize(15);
        NameOfAdminInfo.setText(record.getNameOfAdminInfo());
        NameOfAdminInfo.setTextColor(Color.parseColor("#ffffff"));
        PanelData.addView(NameOfAdminInfo);

        addView(PanelData, Params);
    }
}

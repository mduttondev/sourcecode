package com.mkdutton.feedback.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mkdutton.feedback.R;
import com.mkdutton.feedback.Utils;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Matt on 11/7/14.
 */
public class AllCampaignsAdapter extends BaseAdapter {

    public static final int CONST_ID = 12312;

    Context context;
    List<ParseObject> parseObjects;


    public AllCampaignsAdapter(Context context, List<ParseObject> parseObjects) {
        this.context = context;
        this.parseObjects = parseObjects;
    }

    @Override
    public int getCount() {
        if (parseObjects != null){
            return parseObjects.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return parseObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return CONST_ID + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        ParseObject campaign = (ParseObject)getItem(position);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String time = sdf.format( new Date( campaign.getLong("creationTime") ) );

        String camp_id = campaign.getString(Utils.CAMPAIGN_ID);

        ((TextView)convertView.findViewById(R.id.cell_title)).setText( "Name: " + campaign.getString("prettyName") );
        ((TextView)convertView.findViewById(R.id.cell_date)).setText( "Created on: " + time );
        ((TextView)convertView.findViewById(R.id.cell_comment)).setText( "ID: " + camp_id );

        return convertView;
    }

    public void delete(ParseObject campaign) {

        parseObjects.remove(campaign);

    }
}

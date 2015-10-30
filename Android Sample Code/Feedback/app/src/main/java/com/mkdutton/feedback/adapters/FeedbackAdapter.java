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
 * Created by Matt on 11/13/14.
 */
public class FeedbackAdapter extends BaseAdapter {


    public static final int CONST_ID = 45612;

    Context context;
    List<ParseObject> parseObjects;


    public FeedbackAdapter(Context context, List<ParseObject> parseObjects) {
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


        TextView title = (TextView)convertView.findViewById(R.id.cell_title);
        TextView date = (TextView)convertView.findViewById(R.id.cell_date);
        TextView comment = (TextView)convertView.findViewById(R.id.cell_comment);

        ParseObject feedback = (ParseObject)getItem(position);

        long creation = feedback.getLong(Utils.CREATION_TIME);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        String time = sdf.format( new Date(creation) );

        String comments = feedback.getString(Utils.FEEDBACK_TEXT);

        String name = feedback.getString(Utils.FEEDBACK_CONTACT_NAME);

        if (name.equals("None Given")){
            name = "Anonymous";
        }

        title.setText( "From: " + name );
        date.setText( "Left on: " + time );
        comment.setText("Comments:\n     " + comments);


        return convertView;
    }

    public void delete(ParseObject feedback) {

        parseObjects.remove(feedback);

    }

}

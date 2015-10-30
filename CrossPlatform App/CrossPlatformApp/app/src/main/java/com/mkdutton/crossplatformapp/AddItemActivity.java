package com.mkdutton.crossplatformapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class AddItemActivity extends Activity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        (findViewById(R.id.addItemButton)).setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.addItemButton){

            final String itemName = ((TextView)findViewById(R.id.itemName)).getText().toString();
            String qty = ((TextView)findViewById(R.id.itemAmount)).getText().toString();


            //make sure strings are present and not null, not an empty field
            if (  ( itemName != null && !itemName.isEmpty() && qty!= null && !qty.isEmpty() && qty.length() <=4 ) ) {

                int itemQty = Integer.valueOf(qty);


                ParseObject item = new ParseObject(Utils.PARSE_CLASS_LIST_ITEM);
                item.put(Utils.ITEM_OWNER, ParseUser.getCurrentUser());
                item.setACL(new ParseACL(ParseUser.getCurrentUser()));
                item.put(Utils.ITEM_NAME, itemName);
                item.put(Utils.ITEM_QTY, itemQty);
                item.put(Utils.ITEM_ID, Utils.generateRandomIdOfLength(Utils.ITEM_ID_LENGTH));


                if (Utils.isConnected(this)) {
                    item.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(AddItemActivity.this,
                                        itemName + " saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(AddItemActivity.this,
                            "No Network\nWill save when one is available", Toast.LENGTH_SHORT).show();

                    item.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                Toast.makeText(AddItemActivity.this,
                                        itemName + " saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


                Intent complete = getIntent();
                setResult(RESULT_OK, complete);
                finish();



            } else {
                Toast.makeText(this, "Fields cannot be blank and Quantity must me 4 digits or less\n" +
                        "Please try again", Toast.LENGTH_SHORT).show();
            }

        }

    }

}

package com.mkdutton.crossplatformapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditActivity extends Activity implements View.OnClickListener
{

    public static final String EXTRA_EDIT_OBJECT_ID = "EXTRA_EDIT_OBJECT_ID";

    ParseObject objToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent editIntent = getIntent();

        if (!editIntent.hasExtra(EXTRA_EDIT_OBJECT_ID)){
            throw new IllegalArgumentException("An object ID must be included");
        }

        String objID = editIntent.getStringExtra(EXTRA_EDIT_OBJECT_ID);

        getObjectFromParse(objID);

        (findViewById(R.id.saveChangesButton)).setOnClickListener(this);


    }


    private void getObjectFromParse(String _id){

        if (Utils.isConnected(this)) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Utils.PARSE_CLASS_LIST_ITEM);

            query.whereEqualTo("objectId", _id);
            query.whereEqualTo(Utils.ITEM_OWNER, ParseUser.getCurrentUser());

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {

                        if (parseObjects.size() == 1) {

                            objToEdit = parseObjects.get(0);

                            assignValuesToFields(objToEdit);

                        } else {
                            Toast.makeText(EditActivity.this,
                                    "Returned more than 1 result to edit, try again", Toast.LENGTH_LONG).show();
                            resultCancelledandFinish();
                        }

                    } else {
                        Toast.makeText(EditActivity.this,
                                "Something bad happened, try again", Toast.LENGTH_LONG).show();
                        resultCancelledandFinish();
                    }
                }
            });
        } else {

            Toast.makeText(this, "Connection Was Lost\n\nPlease Try Again", Toast.LENGTH_SHORT).show();

            resultCancelledandFinish();
        }

    }


    public void assignValuesToFields(ParseObject _editObject){

        ((EditText)findViewById(R.id.editItem)).setText(_editObject.getString("Item"));
        ((EditText)findViewById(R.id.editAmount)).setText( Integer.toString(_editObject.getInt("Qty") ) );

    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.saveChangesButton){

            String item = ((TextView)findViewById(R.id.editItem)).getText().toString();
            int qty = Integer.valueOf(((TextView) findViewById(R.id.editAmount)).getText().toString());

            // checks to make sure there was still text in the fields.
            // qty fields are locked to number format
            if ( item != null && !item.isEmpty() &&
                    !((TextView)findViewById(R.id.editAmount)).getText().toString().isEmpty() &&
                    ((TextView)findViewById(R.id.editAmount)).getText().toString().length() <=4 ) {

                objToEdit.put(Utils.ITEM_NAME, item);
                objToEdit.put(Utils.ITEM_QTY, qty);

                if (Utils.isConnected(this)) {

                    objToEdit.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(EditActivity.this, "Save Successfull", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                    resultOkAndFinish();

                } else {

                    new AlertDialog.Builder(this)
                            .setTitle("Network Error")
                            .setMessage("No Network, changes will be saved after network is available.\n\n" +
                                    "This can take some time even after connection is established, " +
                                    "are you sure you want to save now?\n")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    objToEdit.saveEventually(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(EditActivity.this, "Save Successfull", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(EditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    resultOkAndFinish();
                                }
                            })
                            .setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resultCancelledandFinish();
                                }
                            }).show();
                }

            } else {

                new AlertDialog.Builder(this)
                        .setTitle("Field Error")
                        .setMessage("Fields can't be blank and quantity can't be more than 4 digits\n\n" +
                                "Please try again")
                        .setPositiveButton("Ok", null)
                        .show();

            }

        }

    }

    private void resultOkAndFinish() {
        setResult(RESULT_OK);
        finish();
    }

    private void resultCancelledandFinish() {
        setResult(RESULT_CANCELED);
        finish();
    }
}

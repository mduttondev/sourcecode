package com.mkdutton.androidnavigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Matt on 9/24/14.
 */
public class DeleteDialogFragment extends DialogFragment {

    public static final String TAG = "DELETE_DIALOG_FRAG";

    DeleteDialogListener mListener;


    public interface DeleteDialogListener {

        public void deletePost(int _selected);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof DeleteDialogListener) {
            mListener = (DeleteDialogListener) activity;
        } else {
            throw new IllegalArgumentException("Calling Class Must Implement DeletePostListener");
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());

        builder.setMessage("Are you sure you want to delete this post?" )
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        mListener.deletePost( getArguments().getInt(DetailsFragment.POSTS) );

                        Toast.makeText((Context)mListener, "Post Deleted", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", null);


        return builder.create();


    }





}

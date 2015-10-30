package com.mkdutton.superduperimageduper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.mkdutton.superduperimageduper.fragments.GalleryFragment;

/**
 * Created by Matt on 10/10/14.
 */
public class DeleteDialogFragment extends DialogFragment {


    public static final String TAG = "DELETE_DIALOG_FRAG";

    DeleteDialogListener mListener;


    public interface DeleteDialogListener {

        public void deleteImage(int _imageIndex);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof DeleteDialogListener) {

            mListener = (DeleteDialogListener) activity;

        } else {

            throw new IllegalArgumentException("Calling Class Must Implement DeleteImageListener");

        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());

        builder.setMessage( "Are you sure you want\nto delete this image?" )
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        mListener.deleteImage(getArguments().getInt(GalleryFragment.EXTRA_SELECTED_IMAGE, -1));

                        Toast.makeText((Context) mListener, "Image Deleted", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", null);


        return builder.create();


    }

}

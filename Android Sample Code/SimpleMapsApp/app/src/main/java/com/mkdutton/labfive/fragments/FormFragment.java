package com.mkdutton.labfive.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.mkdutton.labfive.R;

import java.text.Normalizer;

/**
 * Created by Matt on 10/9/14.
 */
public class FormFragment extends Fragment{

    public static final String TAG = "FORM_FRAG";

    ImageView mImageView;

    public static FormFragment newInstance() {

        return new FormFragment();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mImageView = (ImageView)getActivity().findViewById(R.id.imageView);



    }


    public String[] getDataToSave (){



        EditText et_top = ((EditText)getActivity().findViewById(R.id.topField));
        EditText et_btm =((EditText)getActivity().findViewById(R.id.bottomField));

        if (et_btm.getText().toString().isEmpty() || et_top.getText().toString().isEmpty()){
            alertUser("Empty Field:", "Ensure both fields contain text");

        } else {

            String[] strings = new String[]{

                    //remove the special characters to ensure file name is ok.
                    Normalizer.normalize(et_top.getText().toString(), Normalizer.Form.NFD).replaceAll("[^ a-zA-Z]", ""),

                    et_btm.getText().toString()
            };


            return strings;
        }

        return null;
    }

    public void setImage(Bitmap _image) {
        mImageView.setImageBitmap(_image);
    }

    public void alertUser(String _title, String _message){

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(_title)
                .setMessage(_message)
                .setPositiveButton("Ok", null)
                .show();


    }


}

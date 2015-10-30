package com.mkdutton.crossplatformapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mkdutton.crossplatformapp.R;
import com.mkdutton.crossplatformapp.Utils;

/**
 * Created by Matt on 11/25/14.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener
{

    public static final String TAG = "REGISTER_FRAGMENT";

    RegisterUserListener mListener;

    public static RegisterFragment newInstance(){
        return new RegisterFragment();
    }

    public interface RegisterUserListener{
        public void registerUser(String username, String password, String name, String email, String user_ID);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof RegisterUserListener){
            mListener = (RegisterUserListener)activity;
        } else {
            throw new IllegalArgumentException("Calling class must implement RegisterUserListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_register, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        (getActivity().findViewById(R.id.reg_registerButton)).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.reg_registerButton){

            if (checkInputs()){

                String username = ((TextView)getActivity()
                        .findViewById(R.id.reg_desiredUsername)).getText().toString();

                String password = ((TextView)getActivity()
                        .findViewById(R.id.reg_desiredPassword)).getText().toString();

                String name = ((TextView)getActivity().findViewById(R.id.reg_name)).getText().toString();
                String email = ((TextView)getActivity().findViewById(R.id.reg_email)).getText().toString();
                String user_ID = Utils.generateRandomIdOfLength(Utils.USER_ID_LENGTH);

                mListener.registerUser(username, password, name, email, user_ID);

            } else {
                // a field was null or empty -> alert to that fact
                new AlertDialog.Builder(getActivity())
                        .setTitle("Field Error")
                        .setMessage("Field was Empty or E-mail was not valid\nPlease try again")
                        .setPositiveButton("Ok", null)
                        .show();
            }

        }

    }

    private boolean checkInputs() {

        String username = ((TextView)getActivity().findViewById(R.id.reg_desiredUsername)).getText().toString();
        String password = ((TextView)getActivity().findViewById(R.id.reg_desiredPassword)).getText().toString();
        String name = ((TextView)getActivity().findViewById(R.id.reg_name)).getText().toString();
        String email = ((TextView)getActivity().findViewById(R.id.reg_email)).getText().toString();

        if (username == null || username.isEmpty()){
            return false;

        }

        if (password == null || password.isEmpty()){
            return false;

        }

        if (name == null || name.isEmpty()){
            return false;

        }

        if (email == null || email.isEmpty()){
            return false;

        } else {

            if ( !(isValidEmail(email))){
                return false;
            }

        }

        return true;

    }



    public boolean isValidEmail(CharSequence email) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }


}

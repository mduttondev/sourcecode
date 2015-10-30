package com.mkdutton.feedback.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mkdutton.feedback.R;
import com.mkdutton.feedback.Utils;

/**
 * Created by Matt on 11/3/14.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = "REGISTER_FRAG";

    RegisterUserListener mRegisterListener;


    public interface RegisterUserListener{
        public void registerUser(String userID, String user, String pass, String email, String fName, String lName);
        public void alertUser(String title, String message);
    }

    public static RegisterFragment newInstance(){
        return new RegisterFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof RegisterUserListener){
            mRegisterListener = (RegisterUserListener)activity;
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

        (getActivity().findViewById(R.id.reg_submitBtn)).setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.reg_submitBtn){

            boolean isValid;

            String user = ((EditText)getActivity().findViewById(R.id.regUN_Text)).getText().toString();
            String pass = ((EditText)getActivity().findViewById(R.id.regPASS_Text)).getText().toString();
            String email = ((EditText)getActivity().findViewById(R.id.regEmail_Text)).getText().toString();

            String first = ((EditText)getActivity().findViewById(R.id.regFN_Text)).getText().toString();
            String last = ((EditText)getActivity().findViewById(R.id.regLN_Text)).getText().toString();


            if ( !(user.isEmpty() || pass.isEmpty() || email.isEmpty() || first.isEmpty() || last.isEmpty()) ) {
                user.replace(" ", "");
                pass.replace(" ", "");

                if (user.matches("[a-zA-Z0-9]*")) {

                    if (isValidEmail(email)) {
                        isValid = true;

                    } else {
                        isValid = false;
                        mRegisterListener.alertUser("Email Error", "Provided E-mail is not valid.\n\nPlease re-evaluate and try again.");

                    }

                } else {
                    isValid = false;
                    mRegisterListener.alertUser("Content Error", "User name must be alphanumeric\n\nPlease remove special characters and try again");

                }

                if (isValid) {

                    if (Utils.isConnected( getActivity() )) {
                        mRegisterListener.registerUser(Utils.generateRandomIdOfLength(Utils.RANDOM_USER_ID), user, pass, email, first, last);

                    } else {
                        mRegisterListener.alertUser("Network error", "No network available\nEnsure connection and try again");
                    }
                }

            } else {
                mRegisterListener.alertUser("Error", "Please ensure all fields are completed and try again");

            }

        }
    }

    public boolean isValidEmail(CharSequence email) {

        if (email != null) {

            if (TextUtils.isEmpty(email)) {

                return false;

            } else {

                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }

        } else {

            return false;
        }

    }


}

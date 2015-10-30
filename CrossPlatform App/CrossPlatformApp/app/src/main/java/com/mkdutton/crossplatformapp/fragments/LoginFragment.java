package com.mkdutton.crossplatformapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mkdutton.crossplatformapp.R;
import com.mkdutton.crossplatformapp.Utils;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by Matt on 11/25/14.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{


    public static final String TAG = "LOGIN_FRAGMENT";

    public static final int FAILED_USER_PASS_COMBO = 0x01234;

    public static final int FAILED_OTHER_REASON = 0x04321;

    LoginListener mListener;

    TextView mErrorText;

    public static LoginFragment newInstance(){
        return new LoginFragment();
    }

    public interface LoginListener{
        public void loginSuccessful(ProgressDialog progress);
        public void launchRegistration();
        public void launchRecovery();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof LoginListener){
            mListener = (LoginListener)activity;
        } else {
            throw new IllegalArgumentException("Calling class must implement LoginListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mErrorText = (TextView)getActivity().findViewById(R.id.loginErrorText);

        (getActivity().findViewById(R.id.loginButton)).setOnClickListener(this);
        (getActivity().findViewById(R.id.registerButton)).setOnClickListener(this);
        (getActivity().findViewById(R.id.resetPasswordBtn)).setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        ((TextView)getActivity().findViewById(R.id.usernameText)).setText("");
        ((TextView)getActivity().findViewById(R.id.passwordText)).setText("");


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.loginButton){

            String username = ((TextView)getActivity().findViewById(R.id.usernameText)).getText().toString();
            String password = ((TextView)getActivity().findViewById(R.id.passwordText)).getText().toString();

            if (  !(username == null || username.isEmpty() ||password == null || password.isEmpty()) ){

                attemptLogin(username, password);

            } else {
                // username or password were null or empty -> alert to that fact
                new AlertDialog.Builder(getActivity())
                        .setTitle("Field Error")
                        .setMessage("Fields cannot be empty\nPlease try again")
                        .setPositiveButton("Ok", null)
                        .show();
            }

        }else if (id == R.id.registerButton){
            Log.i( TAG, "REGISTER BUTTON PRESSED" );

            mListener.launchRegistration();



        } else if (id == R.id.resetPasswordBtn){
            Log.i( TAG, "RESET PASSWORD BUTTON PRESSED" );

            mListener.launchRecovery();

        }
    }

    private void attemptLogin(String username, String password) {

        if (Utils.isConnected( (Activity)mListener )){

            final ProgressDialog progress = new ProgressDialog(getActivity());
            progress.setIndeterminate(true);
            progress.setMessage("Checking Credentials...");
            progress.show();

            ParseUser.logInInBackground(username, password, new LogInCallback() {

                public void done(ParseUser user, ParseException e) {

                    if (e == null && user != null) {
                        mListener.loginSuccessful(progress);

                    } else if (user == null) {
                        progress.dismiss();
                        loginFailed(FAILED_USER_PASS_COMBO);
                        Log.i(TAG, e.getMessage());

                    } else {
                        progress.dismiss();
                        loginFailed(FAILED_OTHER_REASON);
                        Log.i(TAG, e.getMessage());
                    }
                }
            });



        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Network Error")
                    .setMessage("No network connection, please try again later")
                    .setPositiveButton("Ok", null)
                    .show();
        }

    }

    private void loginFailed(int reason) {

        if (reason == FAILED_USER_PASS_COMBO){

            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            getActivity().findViewById(R.id.loginRoot).startAnimation(shake);

            mErrorText.setVisibility(View.VISIBLE);

        } else {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Authentication Error")
                    .setMessage("Something wasn't right, please try again")
                    .setPositiveButton("Ok", null)
                    .show();
        }

    }


}

package com.mkdutton.feedback.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mkdutton.feedback.R;
import com.mkdutton.feedback.Utils;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by Matt on 11/3/14.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    public static final String TAG = "LOGIN_FRAGMENT";

    public static final int FAILED_USER_PASS_COMBO = 0x01010101;
    public static final int FAILED_OTHER_REASON = 0x03030303;

    LoginListener mLoginListener;

    TextView mErrorText;



    public interface LoginListener {
        public void launchRegisterFragment();
        public void loginSuccessful(ProgressDialog dialog);
        public void launchResetFragment();
    }

    public static LoginFragment newInstance(){
        return new LoginFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof LoginListener){
            mLoginListener = (LoginListener)activity;

        } else {
            throw new IllegalArgumentException("Calling class must implement LaunchRegListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mErrorText = (TextView)getActivity().findViewById(R.id.errorText);

        (getActivity().findViewById(R.id.loginButton)).setOnClickListener(this);
        (getActivity().findViewById(R.id.registerButton)).setOnClickListener(this);
        (getActivity().findViewById(R.id.resetPasswordBtn)).setOnClickListener(this);

        CheckBox checkBox = (CheckBox)getActivity().findViewById(R.id.rememberMeCheckBox);
        checkBox.setOnCheckedChangeListener(this);
        if ( (PreferenceManager.getDefaultSharedPreferences(getActivity())).getBoolean("remember", false) ){
            checkBox.setChecked(true);

            if (ParseUser.getCurrentUser() != null) {
                ((TextView) getActivity().findViewById(R.id.usernameText)).setText(ParseUser.getCurrentUser().getUsername());
            }
        } else {
            checkBox.setChecked(false);
        }

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit().putBoolean("remember", isChecked).commit();

    }

    @Override
    public void onClick(View v) {

        mErrorText.setVisibility(View.INVISIBLE);

        int id = v.getId();

        if (id == R.id.registerButton){
            mLoginListener.launchRegisterFragment();

        } else if ( id == R.id.loginButton){
            attemptLogin();

        } else if ( id == R.id.resetPasswordBtn){

            mLoginListener.launchResetFragment();

        }
    }


    private void attemptLogin() {

        String username = ((TextView)getActivity().findViewById(R.id.usernameText)).getText().toString();
        String password = ((TextView)getActivity().findViewById(R.id.passwordText)).getText().toString();

        final ProgressDialog progress = new ProgressDialog(getActivity());

        if (  !( username.isEmpty() || password.isEmpty() )  ) {

            progress.setMessage("Attempting Log-in");
            progress.setIndeterminate(true);
            progress.show();

            if (Utils.isConnected(getActivity())) {

                ParseUser.logInInBackground(username, password, new LogInCallback() {

                    public void done(ParseUser user, ParseException e) {

                        if (e == null && user != null) {
                            mLoginListener.loginSuccessful(progress);

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
                progress.dismiss();
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Network Error")
                        .setMessage("No Network. Please try again")
                        .setPositiveButton("Ok", null)
                        .show();
            }

        } else {
            progress.dismiss();

            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            getActivity().findViewById(R.id.login_root).startAnimation(shake);

            mErrorText.setText("Username and password cannot be blank");
            mErrorText.setVisibility(View.VISIBLE);

        }
    }

    private void loginFailed(int reason) {

        if (reason == FAILED_USER_PASS_COMBO){

            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            getActivity().findViewById(R.id.login_root).startAnimation(shake);

            mErrorText.setText("Username or Password incorrect.\nPlease try again");
            mErrorText.setVisibility(View.VISIBLE);

        }

    }


}

package com.mkdutton.crossplatformapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.mkdutton.crossplatformapp.fragments.LoginFragment;
import com.mkdutton.crossplatformapp.fragments.RecoverPasswordFragment;
import com.mkdutton.crossplatformapp.fragments.RegisterFragment;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends Activity implements LoginFragment.LoginListener, RegisterFragment.RegisterUserListener{

    public static final String TAG = "MAIN_ACTIVITY";
    private boolean didInitialize = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Utils.isConnected(this)) {

            initializeParseAndStartApp();

        } else {

            // no connection

            launchLoginFragment();

            alertUser("Network Error", "No Network\nPlease Try Again Later");

        }
    }

    private void initializeParseAndStartApp() {

        didInitialize = true;

        Parse.initialize(this, "UeC5vJyE6OSULWOgggRBLcBwG6nYHOKYTbClSRQI", "DdF86UXcdfOx0MuN7ox3Ui4xxsaFy9Wjg5I6kJ2e");

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {

            launchLoginFragment();

            final ProgressDialog progress = new ProgressDialog(this);
            progress.setIndeterminate(true);
            progress.setMessage("Logging in......");
            progress.show();


            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            loginSuccessful(progress);
                        }
                    }, 500);


        } else {

            launchLoginFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (!didInitialize){
            initializeParseAndStartApp();
        }


    }

    private void launchLoginFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.loginContainer, LoginFragment.newInstance(), LoginFragment.TAG)
                .commit();
    }

    @Override
    public void loginSuccessful(ProgressDialog progress) {

        Intent intent = new Intent(this, LoggedInActivity.class);
        startActivity(intent);

        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    public void registerUser(String username, String password, String name, String email, final String user_ID) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Attempting to register....");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.put(Utils.KEY_NAME, name);
        newUser.put(Utils.KEY_USER_ID, user_ID);

        newUser.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo(Utils.KEY_USER_ID, user_ID);

                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> parseUsers, ParseException e) {
                            if (e == null) {

                                if (parseUsers.size() == 1) {

                                    launchLoginFragment();

                                    loginSuccessful(progressDialog);

                                } else {
                                    Log.i(TAG, "MULTIPLE USERS WITH SAME USER_ID");
                                }

                            } else {
                                Log.i(TAG, e.getMessage());
                            }
                        }
                    });

                } else {

                    if (e.getCode() == 202) {

                        alertUser("Oops", e.getMessage());
                        progressDialog.dismiss();



                    } else if (e.getCode() == 203 ){

                        alertUser("Oops", e.getMessage());
                        progressDialog.dismiss();

                    }

                    Log.i(TAG, "" + e.getCode() + e.getMessage());

                }
            }
        });

    }


    @Override
    public void launchRegistration() {

        getFragmentManager().beginTransaction()
            .replace(R.id.loginContainer, RegisterFragment.newInstance(), RegisterFragment.TAG)
            .addToBackStack(RegisterFragment.TAG)
            .commit();

    }

    @Override
    public void launchRecovery() {

        getFragmentManager().beginTransaction()
            .replace(R.id.loginContainer, RecoverPasswordFragment.newInstance(), RecoverPasswordFragment.TAG)
            .addToBackStack(RecoverPasswordFragment.TAG)
            .commit();

    }



    public void alertUser(String title, String message) {

        AlertDialog.Builder alert = new AlertDialog.Builder( this );
        alert.setTitle(title).setMessage(message).setPositiveButton("Ok", null).show();

    }

    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();

        } else{
            super.onBackPressed();
        }
    }
}

package com.mkdutton.feedback;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mkdutton.feedback.fragments.LoginFragment;
import com.mkdutton.feedback.fragments.RecoverPasswordFragment;
import com.mkdutton.feedback.fragments.RegisterFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;


public class LoginActivity extends BaseActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterUserListener {

    public static final String TAG = "LOGIN_ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(R.id.login_registerContainer, LoginFragment.newInstance(), LoginFragment.TAG)
                .commit();


        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && (PreferenceManager.getDefaultSharedPreferences(this)).getBoolean("remember", false) ) {

            loginSuccessful(null);

        }

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){

           int stackCount =  getFragmentManager().getBackStackEntryCount();

            if (stackCount > 0){
                getFragmentManager().popBackStack();
            } else {
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void launchRegisterFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.login_registerContainer, RegisterFragment.newInstance(), RegisterFragment.TAG)
                .addToBackStack(RegisterFragment.TAG)
                .commit();
    }

    @Override
    public void loginSuccessful( ProgressDialog dialog ) {

        Intent campaignIntent = new Intent(this, CampaignListActivity.class);
        ActivityCompat.startActivity(this, campaignIntent, null);

        if (dialog != null) {
            dialog.dismiss();
        }

    }

    @Override
    public void launchResetFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.login_registerContainer,
                        RecoverPasswordFragment.newInstance(), RecoverPasswordFragment.TAG)
                .addToBackStack(RecoverPasswordFragment.TAG)
                .commit();
    }


    @Override
    public void registerUser(final String userID, String user, String pass, String email, String fName, String lName) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Attempting to register....");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        ParseUser newUser = new ParseUser();
        newUser.setUsername(user);
        newUser.setPassword(pass);
        newUser.setEmail(email);

        newUser.put(Utils.LOGIN_KEY_USER_ID, userID);
        newUser.put(Utils.LOGIN_KEY_FIRST_NAME, fName);
        newUser.put(Utils.LOGIN_KEY_LAST_NAME, lName);

        newUser.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo(Utils.LOGIN_KEY_USER_ID, userID);

                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> parseUsers, ParseException e) {
                            if (e == null) {

                                if (parseUsers.size() == 1) {

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
    public void alertUser(String title, String message) {

        AlertDialog.Builder alert = new AlertDialog.Builder( this );
        alert.setTitle(title).setMessage(message).setPositiveButton("Ok", null).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        int stackCount =  getFragmentManager().getBackStackEntryCount();

        if (stackCount > 0){
            getFragmentManager().popBackStack();
        } else {
            finish();
        }

    }
}

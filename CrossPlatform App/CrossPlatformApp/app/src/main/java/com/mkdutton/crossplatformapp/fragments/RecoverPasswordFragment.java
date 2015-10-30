package com.mkdutton.crossplatformapp.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mkdutton.crossplatformapp.R;
import com.mkdutton.crossplatformapp.Utils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Created by Matt on 12/1/14.
 */
public class RecoverPasswordFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = "RECOVER_PASS_FRAG";




    public static RecoverPasswordFragment newInstance(){
        return new RecoverPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recover_password, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        (getActivity().findViewById(R.id.recoverBtn)).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setIndeterminate(true);
        progress.setMessage("Processing");
        progress.show();

        if (v.getId() == R.id.recoverBtn) {

            if (Utils.isConnected(getActivity())) {

                String email = ((TextView) getActivity().findViewById(R.id.recoverEmail)).getText().toString();

                if (isValidEmail(email)) {

                    ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {

                        public void done(ParseException e) {
                            if (e == null) {

                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setMessage("Recovery Email sent.\nPlease check your inbox for instructions")
                                        .setPositiveButton("Ok", null)
                                        .show();

                                progress.dismiss();

                                getActivity().getFragmentManager().popBackStack();

                            } else {
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setTitle("Oops")
                                        .setMessage(e.getMessage() + "\n\nTry again")
                                        .setPositiveButton("Ok", null)
                                        .show();

                                progress.dismiss();
                            }
                        }

                    });

                } else {

                    progress.dismiss();
                    Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_SHORT).show();
                }

            } else { // no data connection

                new AlertDialog.Builder(getActivity())
                        .setTitle("Network Error")
                        .setMessage("No Network Available\nPlease Try Again Later")
                        .setPositiveButton("Ok", null)
                        .show();


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

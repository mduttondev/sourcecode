package com.mkdutton.androidnavigation;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String TAG = "SETTINGS_FRAG";

    Context mContext;

    public static SettingsFragment newInstance() {

        return new SettingsFragment();
    }

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* getting the preference fragment resource to use */
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* setting the clear data button to a Preference */
        Preference eraseData = findPreference("CLEAR_DATA");


        eraseData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                AlertDialog.Builder alert = new AlertDialog.Builder(mContext)
                        .setTitle("Delete:")
                        .setMessage("Are you sure you want to delete all cached files")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                    /* get a reference to where the files are being stored */
                                File dir = mContext.getExternalFilesDir(null);

                 /* if that "File" is a dir then get a file list, loop through and delete all the files
                 * with a for:each loop and log the file name of each */
                                if (dir.isDirectory()) {

                                    String[] children = dir.list();

                                    for (String name : children) {

                                        new File(dir, name).delete();

                                        Log.i("DUTTON", "Deleted: " + name);

                                    }

                                }

                /* Toast user that all files were deleted */
                                Toast tst = Toast.makeText(mContext, "All cached files have been deleted ", Toast.LENGTH_LONG);
                                tst.show();

                            }
                        })
                        .setNegativeButton("Cancel", null);
                        alert.show();

                return true;

            }

        });


    }// close onActivityCreated






}//close Fragment
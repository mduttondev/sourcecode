package com.mkdutton.feedback.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mkdutton.feedback.R;
import com.mkdutton.feedback.Utils;

/**
 * Created by Matt on 11/14/14.
 */
public class DetailFragment extends Fragment {


    public static final String TAG = "DETAIL_FRAGMENT";




    public static DetailFragment newInstance(Bundle info){
        DetailFragment frag = new DetailFragment();
        frag.setArguments(new Bundle(info));
        return frag;
    }

    public DetailFragment(){

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        getActivity().findViewById(R.id.seperator).setVisibility(View.GONE);


        Bundle args = getArguments();
        if (args == null){
            throw new IllegalArgumentException("Bundle args, must contain values.");
        }

        String name = args.getString(Utils.FEEDBACK_CONTACT_NAME);

        if (name.equals("None Given")){
            name = "Anonymous";
        }

        ((TextView)getActivity().findViewById(R.id.detail_capaignPretty)).setText(args.getString(Utils.CAMPAIGN_PRETTYNAME));

        ((TextView)getActivity().findViewById(R.id.detail_name)).setText( name );

        ((TextView)getActivity().findViewById(R.id.detail_info)).setText(args.getString(Utils.FEEDBACK_CONTACT_INFO));

        ((TextView)getActivity().findViewById(R.id.detail_feedback)).setText("     " + args.getString(Utils.FEEDBACK_TEXT));

        ((TextView)getActivity().findViewById(R.id.detail_timeStamp)).setText(args.getString(Utils.CREATION_TIME));

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share_button, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final String feedbackToShare = ((TextView)getActivity().findViewById(R.id.detail_feedback)).getText().toString();

        int id = item.getItemId();

        if (id == R.id.action_share_feedback){

            new AlertDialog.Builder(getActivity())
                    .setMessage("If sharing to Facebook, You'll need to copy and paste the text into the post window.")
                    .setPositiveButton("Ok, Copy it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("feedback", feedbackToShare + "\n\n-Via Feedback for Android");
                            clipboard.setPrimaryClip(clip);

                            Toast.makeText(getActivity(), "Text Copied to clipboard", Toast.LENGTH_SHORT).show();

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, feedbackToShare + "\n\n          -Via Feedback for Android" );
                            startActivity(shareIntent);

                        }
                    })
                    .setNegativeButton("Not using Facebook", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, feedbackToShare + "\n\n          -Via Feedback for Android");
                            startActivity(Intent.createChooser(shareIntent, "Share via"));
                        }
                    })
                    .show();

            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}


























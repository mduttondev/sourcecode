package com.mkdutton.feedback;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.parse.Parse;


public class MainActivity extends BaseActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "n7kd5d1AGCk3xGtVS9uw5tIeUnbusmluGQiPpIwW", "LGGdxNM7zTYK8GSE0bfGWmaqW0Eh6Dq3tIPKfjE6");

        setActionBarIcon(R.drawable.ic_launcher);

        (findViewById(R.id.leaveFeedBackBtn)).setOnClickListener(this);
        (findViewById(R.id.viewFeedBackBtn)).setOnClickListener(this);

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }


    @Override
    public void onClick(View v) {
        int id  = v.getId();

        if (id == R.id.leaveFeedBackBtn){

            Intent leaveFeedbackIntent = new Intent(this, LvFeedbackActivity.class);
            ActivityCompat.startActivity(this, leaveFeedbackIntent, null);

        } else if (id == R.id.viewFeedBackBtn){

            Intent viewFeedbackIntent = new Intent(this, LoginActivity.class);
            ActivityCompat.startActivity(this, viewFeedbackIntent, null);

        }
    }



}

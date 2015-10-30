package com.mkdutton.feedback;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Matt on 11/2/14.
 */
public abstract class BaseActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null){
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

    }

    protected abstract int getLayout();

    protected void setActionBarIcon(int resource){
        mToolbar.setNavigationIcon(resource);
    }

    public Toolbar getToolbar(){
        return mToolbar;
    }

}

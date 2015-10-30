package com.mkdutton.androidnavigation;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class MainActivity extends Activity implements ActionBar.OnNavigationListener, DeleteDialogFragment.DeleteDialogListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    public static final String SELECTED_INDEX = "SELECTED_INDEX";

    public static final String SELECTED_ARRAY = "SELECTED_ARRAY";

    int mSelectedIndex = -1;

    ArrayList<RedditPosts> mPosts;

    ActionMode mActionMode;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        getResources().getStringArray(R.array.subredditsArray)),
                this);



        if (savedInstanceState != null) {
            // Restore the previously serialized current dropdown position.
            if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
                actionBar.setSelectedNavigationItem(
                        savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
            }

            if (savedInstanceState.containsKey(SELECTED_INDEX)) {
                mSelectedIndex = savedInstanceState.getInt(SELECTED_INDEX);
            }

            if (savedInstanceState.containsKey(SELECTED_ARRAY)) {
                mPosts = (ArrayList<RedditPosts>) savedInstanceState.getSerializable(SELECTED_ARRAY);
            }

        }



    }



    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());

        outState.putInt(SELECTED_INDEX, mSelectedIndex);

        outState.putSerializable(SELECTED_ARRAY, mPosts);

        super.onSaveInstanceState(outState);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case R.id.action_refresh:

                int selected = getActionBar().getSelectedNavigationIndex();

                refreshData( getResources().getStringArray(R.array.subredditsArray)[selected] );

                return true;


            case R.id.action_settings:

                showSettings();

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }

    }

    public void showSettings() {

        /* creates an explicit intent to show the correct view.
        this Activity will then load and show the correct fragment, see the
        SettingsActivity.class file for further exlanation */
            Intent settingsIntent = new Intent(this, SettingsActivity.class);

            // start the activity
            startActivity(settingsIntent);


    }

    @Override
    public void deletePost(int _selected) {

        DetailsFragment frag = (DetailsFragment)getFragmentManager().findFragmentByTag(DetailsFragment.TAG);


        frag.deletePost( _selected );

    }
    private void refreshData(String sub) {

        mSelectedIndex =  getActionBar().getSelectedNavigationIndex();

        GetApiData getData = new GetApiData();

        getData.execute("http://api.reddit.com/r/", sub, "?limit=50");

    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {

        if (position == mSelectedIndex){

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, DetailsFragment.newInstance(mPosts),DetailsFragment.TAG)
                    .commit();

        } else {

            getRedditApiData(getResources().getStringArray(R.array.subredditsArray)[position]);

        }

        return true;
    }




    public void getRedditApiData (String sub){

        mSelectedIndex =  getActionBar().getSelectedNavigationIndex();

        GetApiData getData = new GetApiData();

        getData.execute("http://api.reddit.com/r/", sub, "?limit=50");

    }


    //////**************//////   ASYNCTASK   //////**************//////
    public class GetApiData extends AsyncTask<String, Void, ArrayList<RedditPosts> > {

        ArrayList<RedditPosts> posts = new ArrayList<RedditPosts>();

        HttpURLConnection connection;

        ProgressDialog prog;

        boolean isCached = false;

        String calledAPI = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // instantiate and show a new diaglog for while the API is being made
            prog = new ProgressDialog(MainActivity.this);

            prog.setIndeterminate(true);

            prog.setMessage(getString(R.string.progress_message));

            prog.show();

        }



        @Override
        protected ArrayList<RedditPosts> doInBackground(String... params) {

            // setting default values for the returns in case one of the returned ojects
            // doesnt have one of the required entries
            String title = "Not Available";

            String sub = "Not Available";

            String auth = "Not Available";

            String postURL = "http://www.google.com";

            long time = 0;

            // setting the calledAPI to the name of the desired subreddit, this is used for a file name
            // for the cache of the object
            calledAPI = params[1];

            // if the network is considered connected, see the NetworkConnection.class for further
            // breakdown on this method
            if ( NetworkConnection.isConnected( MainActivity.this ) ) {

                try {

                    // form the dynamic url from the given in params
                    URL url = new URL( params[0] + params[1] + params[2] );

                    connection = (HttpURLConnection) url.openConnection();

                    connection.connect();

                    InputStream input = connection.getInputStream();

                    String jsonReturn = IOUtils.toString(input);

                    input.close();

                    // Parsing throught the JSON return
                    JSONObject outerObj = new JSONObject(jsonReturn);

                    JSONObject data = outerObj.getJSONObject("data");

                    JSONArray children = new JSONArray();
                    if (data.has("children")) {

                        children = data.getJSONArray("children");

                    }

                    for (int i = 0; i < children.length(); i++) {

                        JSONObject dataObj = children.getJSONObject(i);

                        JSONObject innerData;
                        if (dataObj.has("data")) {

                            innerData = dataObj.getJSONObject("data");

                            if (innerData.has("subreddit")) {

                                sub = innerData.getString("subreddit");
                            }

                            if (innerData.has("author")) {

                                auth = innerData.getString("author");
                            }

                            if (innerData.has("title")) {

                                title = innerData.getString("title");
                            }

                            if (innerData.has("created")) {

                                time = innerData.getInt("created");

                            }

                            if (innerData.has("url")) {

                                postURL = innerData.getString("url");
                            }

                            //adding RedditPosts classes for each new "story" returned from the
                            // reddit API. and then adding each to an arraylist for further processing
                            posts.add(new RedditPosts(title, auth, sub, time, postURL));

                        }
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();

                } catch (JSONException e) {
                    e.printStackTrace();

                } finally {

                    connection.disconnect();
                }

                // call CacheData and pass in the arraylist and the name of the sub to be used as
                // the file name
                cacheData(posts, calledAPI );

                // making sure that the isCached variable is set false.
                isCached = false;

                return posts;


            } else {

                // if the connection is not availbe then call getCachedata and look for the file name
                // that was passed in that the user was trying to get.
                ArrayList<RedditPosts> cachedPosts = getCacheData( calledAPI );

                // if that array list comes back as null then there was no saved data so return null
                if ( cachedPosts == null ) {

                    return null;

                } else {

                    /* if that arraylist comes back as having content then set the
                    isCached bool to true since we will be returning cached data
                    and return those cached "stories/posts" for the onPostExecute to use */
                    cachedPosts = getCacheData( calledAPI );

                    isCached = true;

                    return cachedPosts;
                }

            }
        }



        @Override
        protected void onPostExecute(ArrayList<RedditPosts> value) {
            super.onPostExecute( value );

            mPosts = value;

            /* take the passed in ArrayList which could be either live or cached and pass it into the
            details fragment for displaying. if there was nothing returned then value == null and
            the details will be a blank screen and the user will be alerted with the logic below */
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, DetailsFragment.newInstance(value), DetailsFragment.TAG)
                    .commit();


            // if there was no cached data found alert the user to that fact
            if (value == null){

                alertUser("No Cached Data:", "No saved data for this selection" +
                        "\n\nTry Again after a data connection has been established");

            /* if there was data found but cached == true then put a
            toast so the user knows its old data */
            } else if (isCached) {

                Toast tst = Toast.makeText( MainActivity.this ,"Data Being Shown Was Previously " +
                        "stored\nNo Data Connection Available", Toast.LENGTH_LONG );

                tst.show();

            }

            // close the dialog
            prog.dismiss();

        }



    }// Close AsyncTask

    private void alertUser(String _title, String _message) {

        /* method containing a generic alert that can be used to alert the user of no cache data */
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setTitle(_title);

        alert.setMessage(_message);

        alert.setPositiveButton("OK", null);

        alert.show();


    }




    private void cacheData( ArrayList<RedditPosts> _cacheableData, String _title ) {

        /* method to save the returned data which has been placed in an arraylist
           of custom objects */
        Log.i("DUTTON", "cacheData (SAVE) was called and saved: " + _title);

        // getting the protected storage location
        File storeProtected = this.getExternalFilesDir(null);

        File file = new File( storeProtected, _title );

        try {

            FileOutputStream fos = new FileOutputStream(file);

            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(_cacheableData);

            oos.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    public ArrayList<RedditPosts> getCacheData(String _fileName){

        /* method to retreive the data from the file system when its asked for */
        ArrayList<RedditPosts> out_cache = null;

        Log.i("DUTTON", "getCacheData (RETRIEVE) was called Looking for: " + _fileName);

        // get the location of the "protected" external storage.
        File storeProtected = this.getExternalFilesDir(null);

        // check the folder thats created when theres stored data
        // and check if there is a file with the API name called.
        File file = new File( storeProtected, _fileName );

        try {

            FileInputStream fin = new FileInputStream(file);

            ObjectInputStream oin = new ObjectInputStream(fin);

            out_cache = (ArrayList<RedditPosts>)oin.readObject();

            oin.close();

        } catch (FileNotFoundException e ){

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return out_cache;

    }








}

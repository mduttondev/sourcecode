package com.mkdutton.crossplatformapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mkdutton.crossplatformapp.fragments.ShoppingListFragment;
import com.parse.ParseUser;


public class LoggedInActivity extends Activity implements ShoppingListFragment.ShoppingListListener{

    public static final int ADD_ITEM_REQ_CODE = 0x000101;
    public static final int EDIT_ITEM_REQ_CODE = 0x000202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        getActionBar().setTitle("User: " + ParseUser.getCurrentUser().getUsername());

        getFragmentManager().beginTransaction()
                .replace(R.id.loggedInContainer, ShoppingListFragment.newInstance(), ShoppingListFragment.TAG)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logged_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_logout) {
            stopRunnable();
            ParseUser.logOut();
            finish();
            return true;

        } else if (id == R.id.action_add_new){
            Intent addIntent = new Intent(this, AddItemActivity.class);
            startActivityForResult(addIntent, ADD_ITEM_REQ_CODE);
            return true;

        } else if (id == R.id.action_refresh){

            ShoppingListFragment frag =
                    (ShoppingListFragment)getFragmentManager().findFragmentByTag(ShoppingListFragment.TAG);

            if (frag != null){
                frag.loadShoppingItems();
            }
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (  (requestCode == ADD_ITEM_REQ_CODE || requestCode == EDIT_ITEM_REQ_CODE) &&  resultCode == RESULT_OK){

            ShoppingListFragment frag =
                    (ShoppingListFragment)getFragmentManager().findFragmentByTag(ShoppingListFragment.TAG);

            if (frag != null){
                frag.loadShoppingItems();
            }

        } else if ( (requestCode == ADD_ITEM_REQ_CODE || requestCode == EDIT_ITEM_REQ_CODE) && resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();


        }else {
            Toast.makeText(this, "Something weird happened, try again if item did not add", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        stopRunnable();

        ParseUser.logOut();
    }

    private void stopRunnable() {
        ShoppingListFragment frag = (ShoppingListFragment)getFragmentManager().findFragmentByTag(ShoppingListFragment.TAG);

        if (frag != null) {
            frag.dismissHandlerAndRunnable();
        }
    }

    @Override
    public void launchEditActivity(String _objID) {

        Intent editIntent = new Intent(this, EditActivity.class);
        editIntent.putExtra(EditActivity.EXTRA_EDIT_OBJECT_ID, _objID );
        startActivityForResult( editIntent, LoggedInActivity.EDIT_ITEM_REQ_CODE);

    }
}

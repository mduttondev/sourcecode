package com.mkdutton.superduperimageduper.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mkdutton.superduperimageduper.DeleteDialogFragment;
import com.mkdutton.superduperimageduper.GalleryAdapter;
import com.mkdutton.superduperimageduper.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Matt on 10/10/14.
 */
public class GalleryFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{


    public static final String TAG = "Gallery_FRAGMENT";

    public static final String EXTRA_SELECTED_IMAGE = "com.mkdutton.superduperimageduper.fragments.EXTRA_SELECTED_IMAGE";

    GridView mGallery;

    ArrayList<String> mImages;

    int mSelectedImage = -1;

    ActionMode mActionMode;

    GalleryAdapter mAdapter;


    public static GalleryFragment newInstance(){

        return new GalleryFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mImages = new ArrayList<String>();

        mGallery = (GridView)getActivity().findViewById(R.id.imageGrid);
        mGallery.setOnItemClickListener(this);
        mGallery.setOnItemLongClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateGrid();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        File storageLoc = getActivity().getExternalFilesDir(null);

        String[] files = storageLoc.list();

        if (files != null) {

            File jpgFile = new File(storageLoc, files[i]);

            Intent showIntent = new Intent(Intent.ACTION_VIEW);

            showIntent.setDataAndType(Uri.fromFile(jpgFile),"image/*");

            startActivity(showIntent);

        }
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (mActionMode != null){

            return false;

        } else {

            mSelectedImage = i;

            mActionMode = getActivity().startActionMode(mActionCallback);

            return true;
        }
    }

    public void updateGrid(){

        mImages.clear();

        File storageLoc = getActivity().getExternalFilesDir(null);

        String[] files = storageLoc.list();

        if (files != null){

            for (String name : files){

                //Bitmap image = null;
                String image;
                File jpgFile = new File(storageLoc, name);

                image = jpgFile.getPath();//BitmapFactory.decodeStream( new FileInputStream( jpgFile ), null, options );

                mImages.add(image);

            }

            mAdapter = new GalleryAdapter(getActivity(), mImages);

            mGallery.setAdapter(mAdapter);


        }

    }


    ActionMode.Callback mActionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.contextual_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            int id = menuItem.getItemId();

            switch (id){

                case R.id.action_share:

                    File storageLoc = getActivity().getExternalFilesDir(null);

                    String[] files = storageLoc.list();

                    if (files != null) {

                        File jpgFile = new File(storageLoc, files[mSelectedImage]);

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/jpeg");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(jpgFile));
                        startActivity(shareIntent);

                    }

                    break;

                case R.id.action_delete_image:

                    DeleteDialogFragment dialog = new DeleteDialogFragment();

                    Bundle args = new Bundle();
                    args.putInt(EXTRA_SELECTED_IMAGE, mSelectedImage);
                    dialog.setArguments(args);

                    dialog.show(getActivity().getFragmentManager(), DeleteDialogFragment.TAG);

                    break;

            }

            mActionMode.finish();

            return true;

        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null;
        }
    };


    // used by the delete dialog fragment
    public void deleteImage(int _image){

        if (mImages != null && mAdapter != null) {

            File storageLoc = getActivity().getExternalFilesDir(null);

            String[] files = storageLoc.list();

            if (files != null) {

                File jpgFile = new File(storageLoc, files[_image]);

                jpgFile.delete();
                Log.i(TAG, "Deleted: " + files[_image]);

                mAdapter.remove(_image);
                mAdapter.notifyDataSetChanged();
            }


        }


    }


}

package com.mkdutton.superduperimageduper;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Matt on 10/10/14.
 */
public class GalleryAdapter extends BaseAdapter {

    public static final long ID_CONST = 0x01000342;

    Context mContext;
    ArrayList<String> mImages;

    public GalleryAdapter(Context _context, ArrayList<String> _images) {
        this.mContext = _context;
        this.mImages = _images;
    }

    @Override
    public int getCount() {
        if (mImages != null){
            return mImages.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        if (mImages != null && i < mImages.size() && i >= 0) {
            return mImages.get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return ID_CONST + i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){

            view = LayoutInflater.from(mContext).inflate(R.layout.grid_cell_layout, viewGroup, false);

        }

        ((ImageView)view.findViewById(R.id.cell_image)).setImageURI( Uri.parse(mImages.get(i)) );


        return view;
    }

    public void remove(int _position){

        mImages.remove( _position );

    }

}

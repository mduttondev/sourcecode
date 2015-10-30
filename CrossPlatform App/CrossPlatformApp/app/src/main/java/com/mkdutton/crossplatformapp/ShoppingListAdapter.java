package com.mkdutton.crossplatformapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Matt on 12/1/14.
 */
public class ShoppingListAdapter extends BaseAdapter {

    public static final int ID_CONSTANT = 0x3323;

    List<ParseObject> shoppingItems;
    Context mContext;

    public ShoppingListAdapter( Context mContext, List<ParseObject> shoppingItems) {
        this.shoppingItems = shoppingItems;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if (shoppingItems != null){
            return shoppingItems.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return shoppingItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ID_CONSTANT + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.shopping_item_cell, parent, false);
        }

        ParseObject item = (ParseObject)getItem(position);

        String title = item.getString(Utils.ITEM_NAME);
        int qty = item.getInt(Utils.ITEM_QTY);

        ((TextView)convertView.findViewById(R.id.shoppingItem)).setText(title);
        ((TextView)convertView.findViewById(R.id.shoppingQty)).setText( "Qty: " + String.valueOf( qty ));

        return convertView;
    }

    public void delete(ParseObject object){

        shoppingItems.remove(object);
        notifyDataSetChanged();

    }
}














package com.csce445project.storyintime;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Justin on 3/21/2016.
 */
public class TextAdapter extends ArrayAdapter<View> {

    private Context mContext;
    private ArrayList<View> mValues;

    public TextAdapter(Context context, ArrayList<View> values) {
        super(context,R.layout.text_link,values);
        this.mContext = context;
        this.mValues = values;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return mValues.get(position);
    }
}

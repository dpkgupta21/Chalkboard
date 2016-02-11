package com.chalkboard.teacher.matchrequest.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.traphoria.R;

/**
 * Created by Deepak on 2/10/2016.
 */
public class MessagesAdapter extends BaseAdapter {


    private Activity mActivity;

    public MessagesAdapter(Activity mActivity) {

        this.mActivity = mActivity;

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_layout, parent, false);
        return null;

    }


}

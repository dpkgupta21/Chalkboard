package com.chalkboard.teacher.matchrequest.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.traphoria.R;
import com.app.traphoria.model.NotificationDTO;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {


    private Activity mActivity;
    private LayoutInflater mLayoutInflater;


    public NotificationAdapter(Activity mActivity) {
        this.mActivity = mActivity;
       // this.notificationList = notificationList;
        try {
            mLayoutInflater = (LayoutInflater) mActivity
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return position;
    }


    @Override
    public int getViewTypeCount() {

        return 2;
    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.notification_row_layout, parent, false);
            holder = new ViewHolder();
            holder.notification = (TextView) convertView.findViewById(R.id.member_name);
            holder.more_icon = (ImageView) convertView.findViewById(R.id.select_img);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.notification.setText(notificationList.get(position).getMessage());

        return convertView;
    }


    public class ViewHolder {
        TextView notification;
        ImageView more_icon;
    }
}

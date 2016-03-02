package com.chalkboard.recruiter.navigationdrawer.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chalkboard.R;
import com.chalkboard.model.MenuCountDTO;
import com.chalkboard.recruiter.navigationdrawer.RecruiterNavDrawerItem;

import java.util.ArrayList;


public class RecruiterNavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RecruiterNavDrawerItem> navDrawerItems;
    private MenuCountDTO menuCountDTO;

    public RecruiterNavDrawerListAdapter(Context context,
                                         ArrayList<RecruiterNavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    public void setMenuCountDTO(MenuCountDTO menuCountDTO) {
        this.menuCountDTO = menuCountDTO;
    }


    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView = convertView;
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            mView = mInflater.inflate(R.layout.drawer_list_item, parent, false);
            holder = new ViewHolder();
            holder.img_menu_icon = (ImageView) mView
                    .findViewById(R.id.img_menu_icon);
            holder.txt_menu_title = (TextView) mView
                    .findViewById(R.id.txt_menu_title);
            holder.txt_unread_projects = (TextView) mView.findViewById(R.id.txt_unread_projects);

            mView.setTag(holder);
        } else {
            holder = (ViewHolder) mView.getTag();
        }

        if (((ListView) parent).isItemChecked(position)) {
            holder.img_menu_icon.setImageResource(navDrawerItems.get(position).getSelectIcon());
            holder.txt_menu_title.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.txt_unread_projects.setTextColor(context.getResources().getColor(android.R.color.black));
        } else {
            holder.img_menu_icon.setImageResource(navDrawerItems.get(position).getIcon());
            holder.txt_menu_title.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.txt_unread_projects.setTextColor(context.getResources().getColor(android.R.color.white));
        }

        if (menuCountDTO != null) {
            if (position == 1) {
                holder.txt_unread_projects.setVisibility(View.VISIBLE);
                holder.txt_unread_projects.setText("" +
                        (menuCountDTO.getCredits() == 0 ? "" : menuCountDTO.getCredits()));
            } else if (position == 4) {
                holder.txt_unread_projects.setVisibility(View.VISIBLE);
                holder.txt_unread_projects.setText("" +
                        (menuCountDTO.getMatchRequestCount() == 0 ? "" :
                                menuCountDTO.getMatchRequestCount()));
            } else if (position == 5) {
                holder.txt_unread_projects.setVisibility(View.VISIBLE);
                holder.txt_unread_projects.setText("" +
                        (menuCountDTO.getMatchCount() == 0 ? "" : menuCountDTO.getMatchCount()));
            } else if (position == 7) {
                holder.txt_unread_projects.setVisibility(View.VISIBLE);
                holder.txt_unread_projects.setText("" +
                        (menuCountDTO.getMsgcount() == 0 ? "" : menuCountDTO.getMsgcount()));
            } else {
                holder.txt_unread_projects.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.txt_unread_projects.setVisibility(View.INVISIBLE);
        }

        holder.txt_menu_title.setText(navDrawerItems.get(position).getTitle());

        return mView;
    }

    private static class ViewHolder {
        private TextView txt_menu_title;
        private ImageView img_menu_icon;
        private TextView txt_unread_projects;

    }
}

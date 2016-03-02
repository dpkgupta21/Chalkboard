package com.chalkboard.recruiter.matchrequest.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.chalkboard.ImageLoader;
import com.chalkboard.R;
import com.chalkboard.model.RecruiterMatchReceivedDTO;
import com.chalkboard.utility.Utils;
import java.util.List;

public class RecruiterReceivedAdapter extends BaseAdapter {


    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private List<RecruiterMatchReceivedDTO> recruiterMatchReceivedDTOList;
    private ImageLoader imageLoader;

    public RecruiterReceivedAdapter(Activity mActivity,
                                    List<RecruiterMatchReceivedDTO> recruiterMatchReceivedDTOList) {
        this.mActivity = mActivity;
        this.recruiterMatchReceivedDTOList = recruiterMatchReceivedDTOList;
        imageLoader = new ImageLoader(mActivity);

        try {
            mLayoutInflater = (LayoutInflater) mActivity
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getCount() {

        return recruiterMatchReceivedDTOList.size();

    }

    @Override
    public Object getItem(int position) {


        return recruiterMatchReceivedDTOList.get(position);

    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        RecruiterMatchReceivedDTO recruiterMatchReceivedDTO = recruiterMatchReceivedDTOList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.recruiter_received_row_layout,
                    parent, false);
            holder.txtTeacherName = (TextView) convertView.findViewById(R.id.txt_teacher_name);
            holder.txtTeacherLocation = (TextView) convertView.findViewById(R.id.txt_teacher_location);
            holder.txtJobTitle = (TextView) convertView.findViewById(R.id.txt_job_name);
            holder.imgMoreIcon = (ImageView) convertView.findViewById(R.id.img_more_icon);
            holder.circleImage = (ImageView) convertView.findViewById(R.id.teacher_circle_img);


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        imageLoader.DisplayImage(recruiterMatchReceivedDTO.getImage(),
                holder.circleImage);
        holder.txtTeacherName.setText(recruiterMatchReceivedDTO.getName());
        holder.txtTeacherLocation.setText(Utils.formatCityCountry(recruiterMatchReceivedDTO.getCity()
                , recruiterMatchReceivedDTO.getCountry()));
        holder.txtJobTitle.setText(recruiterMatchReceivedDTO.getJob().getTitle());

        return convertView;
    }


    public class ViewHolder {
        ImageView circleImage;
        TextView txtTeacherName;
        ImageView imgMoreIcon;
        TextView txtTeacherLocation;
        TextView txtJobTitle;

    }
}

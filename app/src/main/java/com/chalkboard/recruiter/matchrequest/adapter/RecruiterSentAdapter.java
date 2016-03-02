package com.chalkboard.recruiter.matchrequest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.ImageLoader;
import com.chalkboard.R;
import com.chalkboard.model.RecruiterMatchSentDTO;
import com.chalkboard.utility.Utils;

import java.util.List;


public class RecruiterSentAdapter extends RecyclerView.Adapter<RecruiterSentAdapter.DetailsViewHolder> {


    private Context context;
    private List<RecruiterMatchSentDTO> matchSentDTOList;
    private ImageLoader imageLoader;

    public RecruiterSentAdapter(Context context, List<RecruiterMatchSentDTO> matchSentDTOList) {

        this.context = context;
        this.matchSentDTOList = matchSentDTOList;
        imageLoader = new ImageLoader(this.context);
    }

    @Override
    public DetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recruiter_sent_row_layout, parent, false);

        DetailsViewHolder detailsViewHolder = new DetailsViewHolder(v);
        return detailsViewHolder;
    }

    @Override
    public void onBindViewHolder(DetailsViewHolder detailsViewHolder, int position) {


        imageLoader.DisplayImage(matchSentDTOList.get(position).getImage(),
                detailsViewHolder.circleImage);
        detailsViewHolder.txtTeacherName.setText(matchSentDTOList.get(position).getName());
        detailsViewHolder.txtTeacherAge.setText(matchSentDTOList.get(position).getAge());
        detailsViewHolder.txtTeacherGender.setText(matchSentDTOList.get(position).getGender());
        detailsViewHolder.txtTeacherLocation.setText(Utils.formatCityCountry(matchSentDTOList.get(position).getCity(),
                matchSentDTOList.get(position).getCountry()));


    }

    @Override
    public int getItemCount() {
        return matchSentDTOList.size();
    }


    public static class DetailsViewHolder extends RecyclerView.ViewHolder {

        ImageView circleImage;
        TextView txtTeacherName;
        TextView txtTeacherAge;
        TextView txtTeacherGender;
        TextView txtTeacherLocation;


        public DetailsViewHolder(View itemView) {

            super(itemView);

            circleImage = (ImageView) itemView.findViewById(R.id.teacher_circle_img);
            txtTeacherName = (TextView) itemView.findViewById(R.id.txt_teacher_name);
            txtTeacherAge = (TextView) itemView.findViewById(R.id.txt_teacher_age);
            txtTeacherGender = (TextView) itemView.findViewById(R.id.txt_teacher_gender);
            txtTeacherLocation = (TextView) itemView.findViewById(R.id.txt_teacher_location);

        }
    }

}

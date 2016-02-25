package com.chalkboard.recruiter.matchrequest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.R;
import com.chalkboard.model.RecruiterMatchSentDTO;
import com.chalkboard.utility.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.List;


public class RecruiterSentAdapter extends RecyclerView.Adapter<RecruiterSentAdapter.DetailsViewHolder> {


    private Context context;
    private List<RecruiterMatchSentDTO> matchSentDTOList;
    private DisplayImageOptions options;

    public RecruiterSentAdapter(Context context, List<RecruiterMatchSentDTO> matchSentDTOList) {

        this.context = context;
        this.matchSentDTOList = matchSentDTOList;
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .showImageOnLoading(R.drawable.unactive_circle)
                .showImageOnFail(R.drawable.unactive_circle)
                .showImageForEmptyUri(R.drawable.unactive_circle)
                .build();


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

        ImageLoader.getInstance().displayImage(matchSentDTOList.get(position).getImage(),
                detailsViewHolder.circleImage,
                options);
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

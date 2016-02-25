package com.chalkboard.teacher.matchrequest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.R;
import com.chalkboard.model.MatchSentDTO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.List;


public class SentAdapter extends RecyclerView.Adapter<SentAdapter.DetailsViewHolder> {


    private Context context;
    private List<MatchSentDTO> matchSentDTOList;
    private DisplayImageOptions options;

    public SentAdapter(Context context, List<MatchSentDTO> matchSentDTOList) {

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
                inflate(R.layout.match_sent_row_layout, parent, false);

        DetailsViewHolder detailsViewHolder = new DetailsViewHolder(v);
        return detailsViewHolder;
    }

    @Override
    public void onBindViewHolder(DetailsViewHolder detailsViewHolder, int position) {

        ImageLoader.getInstance().displayImage(matchSentDTOList.get(position).getImage(),
                detailsViewHolder.circleImage,
                options);
        detailsViewHolder.txtTitle.setText(matchSentDTOList.get(position).getTitle());
        detailsViewHolder.txtCountry.setText(matchSentDTOList.get(position).getCity() + ", " +
                matchSentDTOList.get(position).getCountry());
        detailsViewHolder.txtDateTime.setText(matchSentDTOList.get(position).getMatch_date());


    }

    @Override
    public int getItemCount() {
        return matchSentDTOList.size();
    }


    public static class DetailsViewHolder extends RecyclerView.ViewHolder {

        ImageView circleImage;
        TextView txtTitle;
        TextView txtCountry;
        TextView txtDateTime;


        public DetailsViewHolder(View itemView) {

            super(itemView);

            circleImage = (ImageView) itemView.findViewById(R.id.circle_img);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtCountry = (TextView) itemView.findViewById(R.id.txt_country);
            txtDateTime = (TextView) itemView.findViewById(R.id.txt_date_time);

        }
    }

}

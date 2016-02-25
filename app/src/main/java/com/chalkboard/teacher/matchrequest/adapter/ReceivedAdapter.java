package com.chalkboard.teacher.matchrequest.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.R;
import com.chalkboard.model.MatchReceivedDTO;
import com.chalkboard.model.MatchReceivedDTO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.List;

public class ReceivedAdapter extends BaseAdapter {


    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private List<MatchReceivedDTO> matchReceivedDTOList;
    private DisplayImageOptions options;

    public ReceivedAdapter(Activity mActivity, List<MatchReceivedDTO> matchReceivedDTOList) {
        this.mActivity = mActivity;
        this.matchReceivedDTOList = matchReceivedDTOList;
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mActivity));
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
        try {
            mLayoutInflater = (LayoutInflater) mActivity
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getCount() {

        return matchReceivedDTOList.size();

    }

    @Override
    public Object getItem(int position) {


        return matchReceivedDTOList.get(position);

    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        MatchReceivedDTO matchReceivedDTO = matchReceivedDTOList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.received_row_layout,
                    parent, false);
            holder.txtCityCountry = (TextView) convertView.findViewById(R.id.txt_city_country);
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            holder.imgMoreIcon = (ImageView) convertView.findViewById(R.id.img_more_icon);
            holder.circleImage = (ImageView) convertView.findViewById(R.id.circle_image);


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageLoader.getInstance().displayImage(matchReceivedDTO.getImage(), holder.circleImage,
                options);

        holder.txtCityCountry.setText(matchReceivedDTO.getCity()+", "+matchReceivedDTO.getCountry());
        holder.txtName.setText(matchReceivedDTO.getName());
        holder.txtDate.setText(matchReceivedDTO.getMatch_date());


        return convertView;
    }


    public class ViewHolder {
        ImageView circleImage;
        TextView txtCityCountry;
        ImageView imgMoreIcon;
        TextView txtName;
        TextView txtDate;

    }
}

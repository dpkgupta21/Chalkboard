package com.chalkboard.recruiter.matchrequest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.R;
import com.chalkboard.model.MatchSentDTO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * Created by DeepakGupta on 2/21/16.
 */
public class RecruiterMatchSentDetailActivity extends Activity {

    //private MatchSentDTO sentDetailDTO;
    private DisplayImageOptions options;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_match_sent_detail);
        mActivity = this;

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
        MatchSentDTO sentDetailDTO = (MatchSentDTO) getIntent().getSerializableExtra("sentDetail");

        setUIValues(sentDetailDTO);
        ImageView img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(imgBackClickListener);

    }

    private void setUIValues(MatchSentDTO sentDetailDTO) {
        ImageView circleImage = (ImageView) findViewById(R.id.circle_img);
        ImageLoader.getInstance().displayImage(sentDetailDTO.getImage(), circleImage,
                options);
        ImageView imgFavIcon = (ImageView) findViewById(R.id.img_fav_icon);
        if(sentDetailDTO.is_favorite()){
            imgFavIcon.setImageResource(R.drawable.like_icon);
        }else{
            imgFavIcon.setImageResource(R.drawable.unlike_icon);
        }

        ((TextView) findViewById(R.id.txt_job_location)).setText(sentDetailDTO.getCity() +
                ", " + sentDetailDTO.getCountry());
        ((TextView) findViewById(R.id.txt_job_title)).setText(sentDetailDTO.getTitle());
        ((TextView) findViewById(R.id.txt_job_description)).setText(sentDetailDTO.getDescription());
        ((TextView) findViewById(R.id.txt_salary_val)).setText(sentDetailDTO.getSalary());
        ((TextView) findViewById(R.id.txt_start_date_val)).setText(sentDetailDTO.getStart_date());
        ((TextView) findViewById(R.id.txt_about_company_val)).setText(sentDetailDTO.getRecruiter().getAbout());


    }

    private View.OnClickListener imgBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();

        }
    };
}

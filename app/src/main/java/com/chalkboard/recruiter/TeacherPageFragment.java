package com.chalkboard.recruiter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.ImageLoader11;
import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;
import com.chalkboard.customviews.CustomAlert;
import com.chalkboard.model.ReadMapIdDTO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

public class TeacherPageFragment extends Fragment {

    View rootView = null;

    Activity context = null;
    TeacherObject teacherObject;


    //Typeface font,font2;
    public TeacherPageFragment() {

    }

    static String TEACHER_OBJECT = "TEACHER_OBJECT";

    public static TeacherPageFragment newInstance(TeacherObject teacherObject) {
        TeacherPageFragment teacherFragment = new TeacherPageFragment();

        Bundle args = new Bundle();
        args.putSerializable(TEACHER_OBJECT, teacherObject);
        teacherFragment.setArguments(args);

        return teacherFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        teacherObject = (TeacherObject) getArguments().getSerializable(
                TEACHER_OBJECT);

        context = getActivity();
        //		Toast.makeText(context, "innnnn", 3000).show();

        rootView = inflater.inflate(R.layout.page_teacher, container, false);

        refineUI();
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void refineUI() {

        final Button btn_match_request = (Button) rootView.findViewById(R.id.btn_match_request);
        if (teacherObject.isTeacherMatch()) {

            btn_match_request
                    .setText(getString(R.string.unmatch_teacher));
        } else {
            btn_match_request.setText(getString(R.string.match_teacher));
        }


        btn_match_request.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_match_request.getText().toString().equalsIgnoreCase(getString(R.string.match_teacher))) {
                    showSendMatchRequestDialog();
                } else {
                    dblBtnCallbackResponse(true, 1001);
                }

            }
        });


        final ImageView img_fav_icon = (ImageView) rootView.findViewById(R.id.img_fav_icon);

        if (teacherObject.isTeacherFavorite()) {
            img_fav_icon.setImageResource(R.drawable.like_icon_active);

        } else {
            img_fav_icon.setImageResource(R.drawable.like_icon_grey);

        }
        ((TextView) rootView.findViewById(R.id.teacher_name))
                .setText(teacherObject.getTeacherName());

        ((TextView) rootView.findViewById(R.id.teacher_location)).setText(
                teacherObject.getTeacherLocation());

        if (teacherObject.getTeacherEducation() != null && !teacherObject.getTeacherEducation().equalsIgnoreCase("")) {
            ((TextView) rootView.findViewById(R.id.education_heading)).setVisibility(View.VISIBLE);
            //			((TextView) rootView.findViewById(R.id.education_heading)).setText(teacherObject.getTeacherEducation());
        }

        if (teacherObject.getTeacherExperience() != null && !teacherObject.getTeacherExperience().equalsIgnoreCase("")) {
            ((TextView) rootView.findViewById(R.id.current_heading)).setVisibility(View.VISIBLE);

            try {
                JSONObject obj = new JSONArray("" + teacherObject.getTeacherExperience()).getJSONObject(0);

                if (obj.getBoolean("is_current")) {
                    ((TextView) rootView.findViewById(R.id.current_job_show)).setText(
                            "" + obj.getString("title") + "\n" + "" + obj.getString("start_date") + "-present");
                } else {
                    ((TextView) rootView.findViewById(R.id.current_job_show)).setText(
                            "" + obj.getString("title") + "\n" + "" + obj.getString("start_date") + "-" + "" + obj.getString("end_date"));

                }


                ((TextView) rootView.findViewById(R.id.education_heading)).setVisibility(View.VISIBLE);

                if (teacherObject.getTeacherEducation() != null && !teacherObject.getTeacherEducation().equalsIgnoreCase("")) {
                    JSONObject obj2 = new JSONArray("" + teacherObject.getTeacherEducation()).getJSONObject(0);

                    if (obj2.getBoolean("is_current")) {
                        ((TextView) rootView.findViewById(R.id.current_edu_show)).setText(
                                "" + obj2.getString("institute") + "\n" + "" + obj2.getString("start_date") + "-present");
                    } else {
                        ((TextView) rootView.findViewById(R.id.current_edu_show)).setText(
                                "" + obj2.getString("institute") + "\n" + "" + obj2.getString("start_date") + "-" + "" + obj2.getString("end_date"));

                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
//                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
            }
        }


        ImageLoader imageloader = new ImageLoader(context);

        imageloader.DisplayImage(teacherObject.getTeacherImage(),
                ((ImageView) rootView.findViewById(R.id.teacher_icon)));

        ImageLoader11 imageloader1 = new ImageLoader11(context);


        imageloader1.DisplayImage(teacherObject.getTeacherImage(),
                ((ImageView) rootView.findViewById(R.id.teacher_image)));

        new Handler().postAtTime(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                try {
                    BitmapDrawable drawable = (BitmapDrawable) ((ImageView) rootView.findViewById(R.id.teacher_image)).getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    bitmap = fastblur(bitmap, 1, 7);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(context, "dddd " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, 3000);


        img_fav_icon
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        if (teacherObject.isTeacherFavorite()) {
                            img_fav_icon.setImageResource(R.drawable.like_icon_grey);
                            removeJobFavorites = new RemoveJobFavorites(
                                    teacherObject.getId());
                            removeJobFavorites.execute();
                        } else {
                            img_fav_icon.setImageResource(R.drawable.like_icon_active);

                            addJobFavorites = new AddJobFavorites(teacherObject
                                    .getId());
                            addJobFavorites.execute();
                        }

                    }
                });

        rootView.findViewById(R.id.img_location_icon)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        String map = "http://maps.google.co.in/maps?q=" +
                                teacherObject.getTeacherLocation();

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                        startActivity(intent);

                    }
                });

//        if (teacherObject.isTeacherMatch()) {
//
//            ((TextView) rootView.findViewById(R.id.match_teacher))
//                    .setText("Unmatch Teacher");
//            ((TextView) rootView.findViewById(R.id.match_teacher))
//                    .setOnClickListener(new OnClickListener() {
//
//                        @Override
//                        public void onClick(View arg0) {
//                            showSendMatchRequestDialog();
////                            if (GlobalClaass.isInternetPresent(context)) {
////
////                                removeJobMatch = new RemoveJobMatch(teacherObject.getId());
////                                removeJobMatch.execute();
////                            } else {
////                                GlobalClaass.showToastMessage(context, "Please check internet connection");
////                            }
//
//                        }
//                    });
//        } else {
//
//            ((TextView) rootView.findViewById(R.id.match_teacher))
//                    .setText("Match Teacher");
//            ((TextView) rootView.findViewById(R.id.match_teacher))
//                    .setOnClickListener(new OnClickListener() {
//
//                        @Override
//                        public void onClick(View arg0) {
//                            if (GlobalClaass.isInternetPresent(context)) {
//
//                                addJobMatch = new AddJobMatch(teacherObject.getId());
//                                addJobMatch.execute();
//                            } else {
//                                GlobalClaass.showToastMessage(context, "Please check internet connection");
//                            }
//
//                        }
//                    });
//
//        }

//		rootView.findViewById(R.id.share).setOnClickListener(
//				new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//
//						String shareBody = teacherObject.getTeacherName()
//								+ " @ " + teacherObject.getTeacherLocation();
//						Intent sharingIntent = new Intent(
//								android.content.Intent.ACTION_SEND);
//						sharingIntent.setType("text/plain");
//						sharingIntent.putExtra(
//								android.content.Intent.EXTRA_SUBJECT,
//								"Chalkboard Android");
//						sharingIntent.putExtra(
//								android.content.Intent.EXTRA_TEXT, shareBody);
//						startActivity(Intent.createChooser(sharingIntent,
//								"Share via..."));
//
//					}
//				});

        ((TextView) rootView.findViewById(R.id.teacher_about))
                .setText(teacherObject.getTeacherAbout());

    }

    private void showSendMatchRequestDialog() {
        String message = "This lets " + teacherObject.getTeacherName() +
                " know you're interested. You'll get a notification if it's a match!";
        new CustomAlert(getActivity(), TeacherPageFragment.this)
                .circleTransparentDialog(
                        message,
                        getString(R.string.cancel),
                        getString(R.string.send), teacherObject.getTeacherImage(),
                        "dblBtnCallbackResponse", 1000);
    }

    public void dblBtnCallbackResponse(Boolean flag, int code) {
        if (flag) {
            if (teacherObject.isTeacherMatch()) {
                removeJobMatch = new RemoveJobMatch(teacherObject
                        .getId());
                removeJobMatch.execute();
            } else {
                addJobMatch = new AddJobMatch(teacherObject.getId());
                addJobMatch.execute();
            }
        }

    }


    RemoveJobMatch removeJobMatch;

    class RemoveJobMatch extends AsyncTask<String, String, String> {

        String teacherId;

        public RemoveJobMatch(String id) {
            teacherId = id;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar(context, rootView);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("action",
                        "removeFromMatchProfile"));

                nameValuePairs.add(new BasicNameValuePair("teacher_id",
                        teacherId));
                nameValuePairs.add(new BasicNameValuePair("user_id",
                        GlobalClaass.getUserId(context)));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                resultStr = EntityUtils.toString(entity);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultStr;

        }

        @Override
        protected void onPostExecute(String result) {

            hideProgressBar(context, rootView);

            teacherObject.setTeacherMatch(false);

            refineUI();

        }

    }

    AddJobMatch addJobMatch;

    class AddJobMatch extends AsyncTask<String, String, String> {

        String teacherId;

        public AddJobMatch(String id) {
            teacherId = id;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar(context, rootView);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("action",
                        "addToMatchProfile"));

                nameValuePairs.add(new BasicNameValuePair("teacher_id",
                        teacherId));
                nameValuePairs.add(new BasicNameValuePair("user_id",
                        GlobalClaass.getUserId(context)));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                resultStr = EntityUtils.toString(entity);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultStr;

        }

        @Override
        protected void onPostExecute(String result) {

            hideProgressBar(context, rootView);

            teacherObject.setTeacherMatch(true);

            refineUI();

        }

    }

    RemoveJobFavorites removeJobFavorites;

    class RemoveJobFavorites extends AsyncTask<String, String, String> {

        String teacherId;

        public RemoveJobFavorites(String id) {
            teacherId = id;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar(context, rootView);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("action",
                        "removeFromFavoriteProfile"));

                nameValuePairs.add(new BasicNameValuePair("teacher_id",
                        teacherId));
                nameValuePairs.add(new BasicNameValuePair("user_id",
                        GlobalClaass.getUserId(context)));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                resultStr = EntityUtils.toString(entity);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultStr;

        }

        @Override
        protected void onPostExecute(String result) {

            hideProgressBar(context, rootView);

            teacherObject.setTeacherFavorite(false);

            refineUI();

        }

    }

    AddJobFavorites addJobFavorites;

    class AddJobFavorites extends AsyncTask<String, String, String> {

        String teacherId;

        public AddJobFavorites(String id) {
            teacherId = id;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar(context, rootView);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("action",
                        "addToFavoriteProfile"));

                nameValuePairs.add(new BasicNameValuePair("teacher_id",
                        teacherId));
                nameValuePairs.add(new BasicNameValuePair("user_id",
                        GlobalClaass.getUserId(context)));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                resultStr = EntityUtils.toString(entity);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultStr;

        }

        @Override
        protected void onPostExecute(String result) {

            hideProgressBar(context, rootView);

            teacherObject.setTeacherFavorite(true);
            refineUI();

        }

    }

    /**
     * Stack Blur v1.0 from
     * http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
     * Java Author: Mario Klingemann <mario at quasimondo.com>
     * http://incubator.quasimondo.com
     * <p/>
     * created Feburary 29, 2004
     * Android port : Yahel Bouaziz <yahel at kayenko.com>
     * http://www.kayenko.com
     * ported april 5th, 2012
     * <p/>
     * This is a compromise between Gaussian Blur and Box blur
     * It creates much better looking blurs than Box Blur, but is
     * 7x faster than my Gaussian Blur implementation.
     * <p/>
     * I called it Stack Blur because this describes best how this
     * filter works internally: it creates a kind of moving stack
     * of colors whilst scanning through the image. Thereby it
     * just has to add one new block of color to the right side
     * of the stack and remove the leftmost color. The remaining
     * colors on the topmost layer of the stack are either added on
     * or reduced by one, depending on if they are on the right or
     * on the left side of the stack.
     * <p/>
     * If you are using this algorithm in your code please add
     * the following line:
     * Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
     */

    public Bitmap fastblur(Bitmap sentBitmap, float scale, int radius) {

        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

}
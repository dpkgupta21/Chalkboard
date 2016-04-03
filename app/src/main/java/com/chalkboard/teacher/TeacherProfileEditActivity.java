package com.chalkboard.teacher;

import static com.chalkboard.GlobalClaass.hideProgressBar;
import static com.chalkboard.GlobalClaass.showProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chalkboard.GlobalClaass;
import com.chalkboard.ImageLoader;
import com.chalkboard.PreferenceConnector;
import com.chalkboard.R;
import com.chalkboard.customviews.CustomAlert;
import com.chalkboard.teacher.navigationdrawer.JobListActivity;

@SuppressLint("NewApi")
public class TeacherProfileEditActivity extends Activity implements
        NumberPicker.OnValueChangeListener {

    Activity context;
    static Dialog d;
    EditText edit_age_select, edit_countrylist, edit_gender, edit_date,
            edit_country_pref, et_experiencetype, et_certificatetype,
            et_yourname, et_city, et_location, et_summery;
    ImageView profile_image;
    Button btn_edit_save, btn_view_profile;
    Switch recruitervisible_swich;
    public ImageLoader imageloader = null;
    String min_nunber = "", max_number = "", name_edittext = "";

    String arrstr[] = {"Male", "Female", "Other"};
    Dialog dialog;

    private int year;
    private int month;
    private int day;
    static final int DATE_PICKER_ID = 1111;
    RadioGroup certificategroup;
    RadioButton yes, no;

    protected static final int RESULT_CAMERA = 5;
    protected static final int RESULT_LIBRARY = 6;
    String selectedImagePath = "";
    String str = "", ext = "", EncodeImage = "";
    long filebyte;
    ImageView btn_back;
    ShowProfile showprofile = null;

    EditProfile editprofile = null;
    String st_country_list = "", st_exp = "",
            st_certificate = "";
    // View view;
    LinearLayout add_education, add_experience, add_country;
    TextView add_edu, add_current_exp;
    TextView teacher_experience_txt, teacher_certificate_txt, earlierdate_txt, countypreference_txt,
            visible_recriuer_txt, personalinfo, edit_header;
    Typeface font, font2;
    TextView top_header_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_teacher_profile_edit);

        context = this;
        imageloader = new ImageLoader(context);
        font = Typeface.createFromAsset(getAssets(), "fonts/mark.ttf");
        font2 = Typeface.createFromAsset(getAssets(), "fonts/marlbold.ttf");

        top_header_count = (TextView) findViewById(R.id.top_header_count);

        String count = GlobalClaass.getHeader_Count(context);

        if (count != null) {

            if (count.length() == 1) {
                top_header_count.setText("  " + count);
            }
            if (count.length() == 2) {
                top_header_count.setText(" " + count);
            } else {
                top_header_count.setText(count);
            }
        } else {
            top_header_count.setVisibility(View.GONE);
        }
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        edit_age_select = (EditText) findViewById(R.id.edit_age_select);

        // company_stratyear = (EditText) findViewById(R.id.company_stratyear);
        // company_endyear = (EditText) findViewById(R.id.company_endyear);
        edit_countrylist = (EditText) findViewById(R.id.edit_countrylist);
        edit_gender = (EditText) findViewById(R.id.edit_gender);
        edit_date = (EditText) findViewById(R.id.edit_date);
        edit_country_pref = (EditText) findViewById(R.id.edit_country_pref);
        et_experiencetype = (EditText) findViewById(R.id.et_experiencetype);
        et_certificatetype = (EditText) findViewById(R.id.et_certificatetype);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        btn_edit_save = (Button) findViewById(R.id.btn_edit_save);
        btn_view_profile = (Button) findViewById(R.id.btn_view_profile);
        et_yourname = (EditText) findViewById(R.id.et_yourname);
        et_city = (EditText) findViewById(R.id.et_city);
        et_location = (EditText) findViewById(R.id.et_location);
        et_summery = (EditText) findViewById(R.id.et_summery);

        // et_companyname = (EditText) findViewById(R.id.et_companyname);
        recruitervisible_swich = (Switch) findViewById(R.id.recruitervisible_swich);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        certificategroup = (RadioGroup) findViewById(R.id.certificategroup);
        yes = (RadioButton) findViewById(R.id.yes);
        no = (RadioButton) findViewById(R.id.no);
        add_education = (LinearLayout) findViewById(R.id.add_education);
        add_edu = (TextView) findViewById(R.id.add_edu);

        add_current_exp = (TextView) findViewById(R.id.add_current_exp);
        add_experience = (LinearLayout) findViewById(R.id.add_experience);
        add_country = (LinearLayout) findViewById(R.id.add_coutry_pref);


        try {
            edit_countrylist.setTypeface(font);
            edit_gender.setTypeface(font);
            edit_date.setTypeface(font);
            edit_country_pref.setTypeface(font);
            et_experiencetype.setTypeface(font);
            et_yourname.setTypeface(font);
            et_certificatetype.setTypeface(font);
            et_city.setTypeface(font);
            et_location.setTypeface(font);
            et_summery.setTypeface(font);
            add_edu.setTypeface(font);
            add_current_exp.setTypeface(font);
            top_header_count.setTypeface(font);
            btn_edit_save.setTypeface(font);
            btn_view_profile.setTypeface(font);

            ((TextView) findViewById(R.id.teacher_experience_txt)).setTypeface(font);
            ((TextView) findViewById(R.id.teacher_certificate_txt)).setTypeface(font);
            ((TextView) findViewById(R.id.earlierdate_txt)).setTypeface(font);
            ((TextView) findViewById(R.id.countypreference_txt)).setTypeface(font);
            ((TextView) findViewById(R.id.visible_recriuer_txt)).setTypeface(font);
            ((TextView) findViewById(R.id.personalinfo)).setTypeface(font);
            ((TextView) findViewById(R.id.edit_header)).setTypeface(font2);

        } catch (Exception e) {

        }

        // add_education.addView(view);

        add_current_exp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (add_experience.getChildCount() < 3) {
                    addExperience(1, "", "", "");
                } else {
                    GlobalClaass.showToastMessage(context, "Maximum added.");
                }
            }
        });

        add_edu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (add_education.getChildCount() < 3) {
                    addEducation(1, "", "", "", "");
                } else {
                    GlobalClaass.showToastMessage(context, "Maximum added.");
                }
            }
        });

        certificategroup
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                        // TODO Auto-generated method stub
                        if (checkedId == R.id.yes) {
                            GlobalClaass.savePrefrencesfor(context,
                                    PreferenceConnector.RadioValue, "1");

                            et_certificatetype.setVisibility(View.VISIBLE);
                        } else {
                            GlobalClaass.savePrefrencesfor(context,
                                    PreferenceConnector.RadioValue, "0");
                            et_certificatetype.setVisibility(View.GONE);
                        }
                    }
                });

        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                new CustomAlert(context, TeacherProfileEditActivity.this).doubleButtonAlertDialog(
                        "Would you like to save your profile changes?",
                        "Save", "cancel", "dblBtnCallbackResponse", 1100);
//				startActivity(new Intent(context,
//						TeacherProfileViewActivity.class));
//				GlobalClaass.activitySlideBackAnimation(context);
//				finish();
            }
        });

        btn_view_profile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startActivity(new Intent(context,
                        TeacherProfileViewActivity.class));
                GlobalClaass.activitySlideBackAnimation(context);

            }
        });

        recruitervisible_swich
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            // The toggle is enabled
                        } else {
                            // The toggle is disabled
                        }
                    }
                });

        if (GlobalClaass.isInternetPresent(context)) {

            showprofile = new ShowProfile();
            showprofile.execute();

        } else {

            GlobalClaass.showToastMessage(context,
                    "Please check internet connection.");
        }

        btn_edit_save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                dblBtnCallbackResponse(true, 1001);
//                new CustomAlert(context, TeacherProfileEditActivity.this).doubleButtonAlertDialog(
//                        "Would you like to save your profile changes?",
//                        "Save", "cancel", "dblBtnCallbackResponse", 1100);
            }

        });

        profile_image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                selectImageMethod();
            }

        });

        et_certificatetype.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, Certificate_Type.class);
                startActivityForResult(i, 4);
            }
        });

        et_experiencetype.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, Experience_Activity.class);
                startActivityForResult(i, 3);
            }
        });

        edit_date.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                showDialog(DATE_PICKER_ID);
            }
        });

        edit_gender.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog.setContentView(R.layout.gender_custom);

                ListView eventlist = (ListView) dialog
                        .findViewById(R.id.eventlistView1);

                ArrayList<String> stringList = new ArrayList<String>(Arrays
                        .asList(arrstr));

                eventlist.setAdapter(new MyAdap1(stringList));

                dialog.show();
            }
        });

        edit_country_pref.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // startActivity(new Intent(context,
                // CountryList_Activity.class));
                Intent i = new Intent(context,
                        MultipleCounrtySelect_Activity.class);
                startActivityForResult(i, 2);
            }
        });

        edit_countrylist.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // startActivity(new Intent(context,
                // CountryList_Activity.class));
                Intent i = new Intent(context, CountryList_Activity.class);
                startActivityForResult(i, 1);
            }
        });

        edit_age_select.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                min_nunber = "18";
                max_number = "99";
                name_edittext = "edit_age_select";
                show();
            }
        });

    }


    public void dblBtnCallbackResponse(Boolean flag, int code) {
        if (flag) {

            if (GlobalClaass.isInternetPresent(context)) {

                editprofile = new EditProfile(context);
                editprofile.execute();


                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus()
                                .getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

            } else {
                GlobalClaass.showToastMessage(context,
                        "Please check internet connection");
            }
//            startActivity(new Intent(context,
//                    TeacherProfileViewActivity.class));
//            GlobalClaass.activitySlideBackAnimation(context);
//            finish();
        }

    }

    void addExperience(int index, String stYear, String enYear, String title) {
        final View view = LayoutInflater.from(context).inflate(
                R.layout.row_add_experience, null);

        final EditText exp_startyear = (EditText) view
                .findViewById(R.id.company_stratyear);
        final EditText exp_endyear = (EditText) view
                .findViewById(R.id.company_endyear);

        final TextView exp_remove = (TextView) view
                .findViewById(R.id.remove_experience);
        EditText et_companyname = (EditText) view
                .findViewById(R.id.et_companyname);

        try {
            exp_startyear.setTypeface(font);
            exp_endyear.setTypeface(font);
            et_companyname.setTypeface(font);
            exp_remove.setTypeface(font);


        } catch (Exception e) {

        }

        exp_startyear.setText(stYear);
        exp_endyear.setText(enYear);
        et_companyname.setText(title);

        exp_remove.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                removeExperience(view);

            }
        });

        if (index == 0) {
            exp_remove.setVisibility(View.GONE);
            et_companyname.setHint("Current company name");
        } else {
            exp_remove.setVisibility(View.VISIBLE);
            et_companyname.setHint("Previous company name");
        }

        exp_startyear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                min_nunber = "1980";
                max_number = "2015";
                name_edittext = "company_stratyear";
                show(exp_startyear);
            }
        });

        exp_endyear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                min_nunber = "1980";
                max_number = "2015";
                name_edittext = "company_endyear";
                show(exp_endyear);
            }
        });

        if (add_experience.getChildCount() < 3) {
            add_experience.addView(view);
        }
    }

    void addEducation(int index, String stYear, String enYear, String degree,
                      String institute) {

        final View view = LayoutInflater.from(context).inflate(
                R.layout.row_edit_profile, null);

        final EditText education_endyear = (EditText) view
                .findViewById(R.id.education_endyear);
        final EditText education_stratyear = (EditText) view
                .findViewById(R.id.education_stratyear);

        EditText et_degree = (EditText) view.findViewById(R.id.et_degree);
        EditText et_institute = (EditText) view.findViewById(R.id.et_institute);

        final TextView llRemove = (TextView) view
                .findViewById(R.id.remove_education);

        education_endyear.setText(enYear);
        education_stratyear.setText(stYear);


        try {
            education_endyear.setTypeface(font);
            education_stratyear.setTypeface(font);
            et_degree.setTypeface(font);
            et_institute.setTypeface(font);
            llRemove.setTypeface(font);


        } catch (Exception e) {

        }


        llRemove.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                removeEducation(view);

            }
        });

        if (index == 0) {
            llRemove.setVisibility(View.GONE);
        } else {
            llRemove.setVisibility(View.VISIBLE);
        }


        et_degree.setText(degree);

        et_institute.setText(institute);

        education_stratyear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                min_nunber = "1980";
                max_number = "2015";
                name_edittext = "education_stratyear";
                show(education_stratyear);
            }
        });

        education_endyear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                min_nunber = "1980";
                max_number = "2015";
                name_edittext = "education_endyear";
                show(education_endyear);
            }
        });

        if (add_education.getChildCount() < 3) {
            add_education.addView(view);
        }

    }


    void addCountry(String name, String id) {

        final View view = LayoutInflater.from(context).inflate(
                R.layout.county_pref, null);


        final TextView tvCountry = (TextView) view
                .findViewById(R.id.country_pref_show);


        tvCountry.setText(name);
        tvCountry.setTag(id);


        try {

            tvCountry.setTypeface(font);


        } catch (Exception e) {

        }


        tvCountry.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                removeCountry(view);

            }
        });


        if (add_country.getChildCount() < 5) {
            add_country.addView(view);
        }

    }

    void removeCountry(View view) {

        add_country.removeView(view);

    }

    void removeExperience(View view) {
        if (add_experience.getChildCount() > 1) {
            add_experience.removeView(view);
        }
    }

    void removeEducation(View view) {

        if (add_education.getChildCount() > 1) {
            add_education.removeView(view);
        }

    }

    private void selectImageMethod() {

        Log.i("choose option", "camera or library");

        final CharSequence[] options = {"Camera", "Choose an image"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chalkboard");
        // builder.setCancelable(true);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Camera")) {

                    Log.i("Camera Clcik", "camera click");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(intent, RESULT_CAMERA);

                } else if (options[item].equals("Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LIBRARY);

                }
            }
        });
        builder.show();
    }


    public Uri getImageUri11(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI11(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            inImage.compress(Bitmap.CompressFormat.JPEG, 90, out);

            Log.e("PAth", file.getAbsolutePath());

            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            profile_image.setImageBitmap(myBitmap);

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(context,
                new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                        str = path;
                    }
                });

        return Uri.parse(str);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null,
                null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static String getFileExt(String FileName) {
        return FileName.substring((FileName.lastIndexOf(".") + 1),
                FileName.length());
    }

    public long getImageLength(String absFileName) {
        File file = new File(absFileName);
        return file.length();
    }

    public byte[] bitmapToByteArray(Bitmap b) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        Log.i("value is", "" + newVal);

    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case RESULT_CAMERA: {

                if (resultCode == context.RESULT_OK && null != data) {

                    try {

					/*Bitmap photo = (Bitmap) data.getExtras().get("data");

					Uri tempUri = getImageUri(context, photo);

					selectedImagePath = getRealPathFromURI(tempUri);

					ext = getFileExt(selectedImagePath);

					filebyte = getImageLength(selectedImagePath);
					int pixel;

					if (photo.getHeight() < photo.getWidth()) {
						pixel = photo.getHeight();
					} else {
						pixel = photo.getWidth();
					}

					profile_image.setImageBitmap((photo));

					Log.e("BInary", selectedImagePath);*/

                        Bitmap photo = (Bitmap) data.getExtras().get("data");

                        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                        Uri tempUri = getImageUri11(getApplicationContext(), photo);

                        selectedImagePath = getRealPathFromURI11(tempUri);

                        profile_image.setImageBitmap(photo);
                    } catch (Exception e) {

                        e.printStackTrace();

                        GlobalClaass.showToastMessage(context, e.toString());

                    }

                }

                break;
            }
            case RESULT_LIBRARY: {

                if (resultCode == context.RESULT_OK && null != data) {

                    try {

                        Uri selectedImage = data.getData();

                        String[] filePath = {MediaStore.Images.Media.DATA};

                        Cursor c = context.getContentResolver().query(
                                selectedImage, filePath, null, null, null);

                        c.moveToFirst();

                        int columnIndex = c.getColumnIndex(filePath[0]);

                        selectedImagePath = c.getString(columnIndex);

                        ext = getFileExt(selectedImagePath);

                        c.close();

                        filebyte = getImageLength(selectedImagePath);

                        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);

                        int pixel;

                        if (bitmap.getHeight() < bitmap.getWidth()) {
                            pixel = bitmap.getHeight();
                        } else {
                            pixel = bitmap.getWidth();
                        }

                        profile_image.setImageBitmap((bitmap));

                        Log.e("BInary", selectedImagePath);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }

                break;
            }

        }

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                edit_countrylist.setText(data.getStringExtra("name"));
                edit_countrylist.setTag(data.getStringExtra("id"));
                st_country_list = data.getStringExtra("id");
                // String result=data.getStringExtra("result");
            }

        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {


                // String result=data.getStringExtra("result");
                List<String> nameList = Arrays.asList(data.getStringExtra("name").split(","));
                List<String> idList = Arrays.asList(data.getStringExtra("id").split(","));

                for (int i = 0; i < nameList.size(); i++) {
                    addCountry(nameList.get(i), idList.get(i));
                }


            }

        } else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {

                et_experiencetype.setText(data.getStringExtra("name")
                        + " Years");
                et_experiencetype.setTag(data.getStringExtra("id"));
                st_exp = data.getStringExtra("id");
            }
        } else if (requestCode == 4) {
            if (resultCode == RESULT_OK) {

                et_certificatetype.setText(data.getStringExtra("name"));
                et_certificatetype.setTag(data.getStringExtra("id"));
                st_certificate = data.getStringExtra("id");
            }

        }

		/*
         * else if (resultCode == 5) {
		 * 
		 * if (resultCode == context.RESULT_OK && null != data) {
		 * 
		 * try {
		 * 
		 * Bitmap photo = (Bitmap) data.getExtras().get("data");
		 * 
		 * Uri tempUri = getImageUri(context, photo);
		 * 
		 * selectedImagePath= getRealPathFromURI(tempUri);
		 * 
		 * ext = getFileExt(selectedImagePath);
		 * 
		 * filebyte = getImageLength(selectedImagePath);
		 * 
		 * profile_image.setImageBitmap(photo);
		 * 
		 * Log.e("BInary", selectedImagePath); }
		 * 
		 * catch (Exception e) {
		 * 
		 * e.printStackTrace();
		 * 
		 * GlobalClaass.showToastMessage(context, e.toString());
		 * 
		 * }
		 * 
		 * }
		 * 
		 * }
		 * 
		 * 
		 * else if (resultCode == 6) {
		 * 
		 * if (resultCode == RESULT_OK && null != data) {
		 * 
		 * try {
		 * 
		 * Uri selectedImage = data.getData();
		 * 
		 * String[] filePath = { MediaStore.Images.Media.DATA };
		 * 
		 * Cursor c = context.getContentResolver().query(selectedImage,
		 * filePath, null, null, null);
		 * 
		 * c.moveToFirst();
		 * 
		 * int columnIndex = c.getColumnIndex(filePath[0]);
		 * 
		 * selectedImagePath= c.getString(columnIndex);
		 * Log.e("selectedImagePath",selectedImagePath); c.close();
		 * 
		 * 
		 * Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
		 * 
		 * profile_image.setImageBitmap(bitmap);
		 * 
		 * 
		 * 
		 * } catch (Exception e) { e.printStackTrace();
		 * 
		 * } }
		 * 
		 * }
		 */
    }

    public void show(final EditText edit) {

        final Dialog d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(true);
        d.setContentView(R.layout.number_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);

        final NumberPicker np = (NumberPicker) d
                .findViewById(R.id.numberPicker1);
        np.setMaxValue(Integer.parseInt(max_number));
        np.setMinValue(Integer.parseInt(min_nunber));
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                edit.setText("01/"
                        + String.valueOf(np.getValue()));

                d.dismiss();
            }
        });

        d.show();

    }

    public void show() {

        final Dialog d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(true);
        d.setContentView(R.layout.number_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);

        final NumberPicker np = (NumberPicker) d
                .findViewById(R.id.numberPicker1);
        np.setMaxValue(Integer.parseInt(max_number));
        np.setMinValue(Integer.parseInt(min_nunber));
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name_edittext.equalsIgnoreCase("edit_age_select")) {

                    edit_age_select.setText(String.valueOf(np.getValue()));
                }

                d.dismiss();
            }
        });

        d.show();

    }

    class MyAdap1 extends BaseAdapter {

        ArrayList<String> comp_details;

        public MyAdap1(ArrayList<String> stringList) {
            comp_details = stringList;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return comp_details.size();
        }

        public String getItem(int position) {
            // TODO Auto-generated method stub
            return comp_details.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        class ViewHolder {
            TextView parent_name;
            ImageView add_icon;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub

            ViewHolder holder = null;
            if (convertView == null) {

                holder = new ViewHolder();
                convertView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.yahoo, null);

                holder.parent_name = (TextView) convertView
                        .findViewById(R.id.textView123);

                convertView.setTag(holder);
                convertView.setTag(R.id.textView123, holder.parent_name);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.parent_name.setText("" + comp_details.get(position));

            boolean isTab = GlobalClaass.isTablet(context);

            if (isTab) {
                holder.parent_name.setTextAppearance(context,
                        android.R.style.TextAppearance_Large);
            }

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    String str = comp_details.get(position);
                    edit_gender.setText(str);
                    dialog.dismiss();
                }
            });

            return convertView;

        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                ContextThemeWrapper themedContext = new ContextThemeWrapper(
                        context, android.R.style.Theme_DeviceDefault_Light_Dialog);

                return new DatePickerDialog(themedContext, pickerListener, year,
                        month, day);
        }
        return null;
    }

    private DatePickerDialog createDialogWithoutDateField() {
        DatePickerDialog dpd = new DatePickerDialog(this, null, 2014, 1, 24);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return dpd;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            edit_date.setText(new StringBuilder().append(year).append("-")
                    .append(month + 1).append("-").append(day));

        }
    };

    class ShowProfile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            showProgressBar(context);
        }

        @Override
        protected String doInBackground(String... params) {

            String resultStr = null;
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("action",
                        "getProfile"));
                nameValuePairs.add(new BasicNameValuePair("id", GlobalClaass
                        .getUserId(context)));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                resultStr = EntityUtils.toString(entity);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultStr;

        }

        // 09-15 11:43:19.785: E/Profile Show(6143):
        // {"user":{"id":"680","name":"Ashok","email":"kumar@gmail.com","city":"jaipur","country":"Iceland","country_id":"111","experience":null,"certification":true,"certification_type":"1","address":"manshrovar","is_visible":true,"about":"I am fine","age":"28","gender":"Male","job_start_date":"2015-07-16","TeacherEducation":"","TeacherExperience":"","CountryPreference":"4","image":"http:\/\/128.199.234.133\/yonder\/files\/user\/image\/680\/thumb_1436962715.jpg","school_photo":"http:\/\/128.199.234.133\/yonder\/img\/user.png"},"message":"","status":true}

        @Override
        protected void onPostExecute(String resultStr) {

            Log.e("Profile Show", resultStr);
            JSONObject jObject, jobj, obj1, obj2;
            JSONArray jarray1, jarray2;

            String get_replycode = "", get_message = "";

            try {

                jObject = new JSONObject(resultStr);

                get_message = jObject.getString("message").trim();
                get_replycode = jObject.getString("status").trim();

                if (get_replycode.equalsIgnoreCase("true")) {

                    jobj = jObject.getJSONObject("user");


                    if (!jobj.getString("name").equalsIgnoreCase("null")) {
                        et_yourname.setText(jobj.getString("name"));
                    }
                    if (!jobj.getString("country").equalsIgnoreCase("null")) {
                        edit_countrylist.setText(jobj.getString("country"));
                    }
                    if (!jobj.getString("country_id").equalsIgnoreCase("null")) {
                        st_country_list = jobj.getString("country_id");
                    }


                    if (jobj.has("selectedCountry")) {
                        if (!jobj.get("selectedCountry").toString().equalsIgnoreCase("")) {


                            jarray1 = jobj.getJSONArray("selectedCountry");
                            if (jarray1.length() > 0) {
                                for (int i = 0; i < jarray1.length(); i++) {

                                    obj1 = jarray1.getJSONObject(i);


                                    addCountry(obj1.getString("name"), obj1.getString("id"));

                                }
                            }
                        }
                    }

					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
				/*	if (!jobj.getString("CountryPreference").equalsIgnoreCase(
                            "null")) {
						//st_county_pref = jobj.getString("CountryPreference");
						
						Log.e("Deepak", jobj.getString("CountryPreference"));
						
					}*/
                    if (!jobj.getString("city").equalsIgnoreCase("null")) {
                        et_city.setText(jobj.getString("city"));
                    }
                    if (!jobj.getString("age").equalsIgnoreCase("null")) {
                        edit_age_select.setText(jobj.getString("age"));
                    }
                    if (!jobj.getString("gender").equalsIgnoreCase("null")) {
                        edit_gender.setText(jobj.getString("gender"));
                    }
                    if (!jobj.getString("address").equalsIgnoreCase("null")) {
                        et_location.setText(jobj.getString("address"));
                    }
                    if (!jobj.getString("about").equalsIgnoreCase("null")) {
                        et_summery.setText(jobj.getString("about"));
                    }

                    if (jobj.get("is_visible").toString().trim().equalsIgnoreCase("true")) {
                        recruitervisible_swich.setChecked(true);
                    } else {
                        recruitervisible_swich.setChecked(false);
                    }

                    if (!jobj.getString("image").equalsIgnoreCase("null")) {

                        imageloader.DisplayImage(jobj.getString("image"),
                                profile_image);
                    }
                    if (!jobj.getString("certification").equalsIgnoreCase(
                            "null")) {

                        if (jobj.getString("certification").equalsIgnoreCase(
                                "true")) {
                            GlobalClaass.savePrefrencesfor(context,
                                    PreferenceConnector.RadioValue, "1");
                            yes.setChecked(true);
                            et_certificatetype.setVisibility(View.VISIBLE);
                            et_certificatetype.setText(jobj.getString("certification_value"));
                            et_certificatetype.setTag(jobj.getString("certification_type"));
                        } else {
                            GlobalClaass.savePrefrencesfor(context,
                                    PreferenceConnector.RadioValue, "0");
                            no.setChecked(true);

                            et_certificatetype.setVisibility(View.GONE);
                        }

                    }


                    if (jobj.has("TeacherEducation")) {
                        if (!jobj.get("TeacherEducation").toString().equalsIgnoreCase("")) {


                            jarray1 = jobj.getJSONArray("TeacherEducation");
                            if (jarray1.length() > 0) {
                                for (int i = 0; i < jarray1.length(); i++) {

                                    obj1 = jarray1.getJSONObject(i);

                                    addEducation(i, obj1.getString("start_date"),
                                            obj1.getString("end_date"),
                                            obj1.getString("title"), obj1.getString("institute"));

                                }
                            } else {
                                addEducation(0, "", "", "", "");
                            }

                        } else {
                            addEducation(0, "", "", "", "");
                        }

                    } else {
                        addEducation(0, "", "", "", "");
                    }

                    if (jobj.has("TeacherExperience")) {
                        if (!jobj.get("TeacherExperience").toString().equalsIgnoreCase("")) {


                            jarray2 = jobj.getJSONArray("TeacherExperience");


                            if (jarray2.length() > 0) {
                                for (int j = 0; j < jarray2.length(); j++) {

                                    obj2 = jarray2.getJSONObject(j);

                                    addExperience(j, obj2.getString("start_date"),
                                            obj2.getString("end_date"),
                                            obj2.getString("title"));

                                }
                            } else {
                                addExperience(0, "", "", "");
                            }
                        } else {
                            addExperience(0, "", "", "");
                        }
                    } else {
                        addExperience(0, "", "", "");
                    }

                    if (!jobj.getString("experience").equalsIgnoreCase("null")) {
                        et_experiencetype.setText(jobj.getString("experience"));
                    }

                    if (!jobj.getString("job_start_date").equalsIgnoreCase("null")) {
                        edit_date.setText(jobj.getString("job_start_date"));
                    }


                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.NAME, jobj.getString("name"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.EMAIL, jobj.getString("email"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.COUNTRY,
                            jobj.getString("country"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.CITY, jobj.getString("city"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.ADDRESS,
                            jobj.getString("address"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.AGE, jobj.getString("age"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.GENDER,
                            jobj.getString("gender"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.IMAGE, jobj.getString("image"));

                } else {
                    GlobalClaass.showToastMessage(context, get_message);

                }

            } catch (Exception e) {

            }

            hideProgressBar(context);

        }

    }

    public class EditProfile extends AsyncTask<String, String, String> {

        String responseString;

        Activity context;

        JSONObject jObject1;

        boolean remember;

        public EditProfile(Activity ctx) {
            // TODO Auto-generated constructor stub

            context = ctx;

        }

        protected void onPreExecute() {

            GlobalClaass.showProgressBar(context);

        }

        protected String doInBackground(String... params) {

            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(GlobalClaass.Webservice_Url);

                try {

                    MultipartEntity entity = new MultipartEntity(
                            HttpMultipartMode.BROWSER_COMPATIBLE);

                    if (!selectedImagePath.equalsIgnoreCase("")) {
                        // File file = new File(selectedImagePath);
                        // Log.e("Image",file.getPath());

                        // entity.addPart("image", new FileBody(file,
                        // "image/jpg"));

                        File file = new File(selectedImagePath);

                        ContentBody encFile = new FileBody(file, "image/jpg");
                        entity.addPart("image", encFile);
                    }

                    entity.addPart("action", new StringBody("account_edit"));
                    entity.addPart("user_id",
                            new StringBody(GlobalClaass.getUserId(context)));
                    entity.addPart("name", new StringBody(et_yourname.getText()
                            .toString()));
                    entity.addPart("country_id",
                            new StringBody(st_country_list));
                    entity.addPart("city", new StringBody(et_city.getText()
                            .toString()));
                    entity.addPart("address", new StringBody(et_location
                            .getText().toString()));
                    entity.addPart("age", new StringBody(edit_age_select
                            .getText().toString()));
                    entity.addPart("gender", new StringBody(edit_gender
                            .getText().toString()));
                    entity.addPart("description", new StringBody(et_summery
                            .getText().toString()));
                    entity.addPart("certification",
                            new StringBody(GlobalClaass.getRadioValue(context)));
                    entity.addPart("certification_type", new StringBody(
                            et_certificatetype.getTag().toString() ));
                    entity.addPart("job_start_date", new StringBody(edit_date
                            .getText().toString()));


                    String visible = "0";

                    if (recruitervisible_swich.isChecked()) {
                        visible = "1";
                    }

                    entity.addPart("is_visible", new StringBody(visible));
                    // entity.addPart("educations", new StringBody(et_degree
                    // .getText().toString()));
                    // education

                    // '[{"title":"MCA","start_date":"06\\/2012","end_date":"06\\/2015"}]',

                    try {

                        StringBuilder st_county_pref = new StringBuilder();

                        String seperator = "";

                        for (int i = 0; i < add_country.getChildCount(); i++) {

                            JSONObject JObj = new JSONObject();
                            LinearLayout ll = (LinearLayout) add_country
                                    .getChildAt(i);

                            TextView ll1 = (TextView) ll.getChildAt(0);

                            st_county_pref.append(seperator);
                            st_county_pref.append(ll1.getTag().toString());

                            seperator = ",";

                        }

                        Log.e("Deepak", "IDDDDDDSSSSSSSSSSSSS   " + st_county_pref.toString() + "");

                        entity.addPart("country_preference", new StringBody(
                                st_county_pref.toString()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        JSONArray jArray = new JSONArray();

                        // JSONObject JObj = new JSONObject();

                        for (int i = 0; i < add_education.getChildCount(); i++) {

                            JSONObject JObj = new JSONObject();
                            LinearLayout ll = (LinearLayout) add_education
                                    .getChildAt(i);

                            LinearLayout ll1 = (LinearLayout) ll.getChildAt(3);

                            JObj.put("title", ((EditText) ll.getChildAt(1))
                                    .getText().toString().trim());

                            JObj.put("institute",
                                    ((EditText) ll.getChildAt(2)).getText().toString().trim());

                            JObj.put("end_date",
                                    ((EditText) ll1.getChildAt(1))
                                            .getText().toString()
                                            .trim());
                            JObj.put("start_date",
                                    ((EditText) ll1.getChildAt(0))
                                            .getText().toString()
                                            .trim());

                            jArray.put(JObj);
                        }

                        Log.e("Deepak", jArray + "");

                        entity.addPart("educations",
                                new StringBody(jArray + ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage() + "<<<<", 3000).show();
                    }

                    // education

                    // Experience

                    // [{"title":"PWS","start_date":"06\\/2015","end_date":"06\\/2012","is_current":1}

                    try {
                        JSONArray jArr = new JSONArray();

                        // JSONObject JObject = new JSONObject();

                        for (int i = 0; i < add_experience.getChildCount(); i++) {

                            JSONObject JObject = new JSONObject();
                            LinearLayout ll = (LinearLayout) add_experience
                                    .getChildAt(i);

                            LinearLayout ll1 = (LinearLayout) ll.getChildAt(2);

                            JObject.put("title", ((EditText) ll.getChildAt(1))
                                    .getText().toString().trim());

                            if (i == 0) {
                                JObject.put("is_current", 1);
                            } else {

                                JObject.put("is_current", 0);
                            }

                            JObject.put("end_date",
                                    ((EditText) ll1.getChildAt(1))
                                            .getText().toString()
                                            .trim());
                            JObject.put("start_date",
                                    ((EditText) ll1.getChildAt(0))
                                            .getText().toString()
                                            .trim());

                            jArr.put(JObject);
                        }

                        Log.e("Deepak", jArr + "");

                        entity.addPart("experiences", new StringBody(jArr + ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Experience

                    request.setEntity(entity);
                    HttpResponse response = httpClient.execute(request);

                    HttpEntity entity2 = response.getEntity();

                    responseString = EntityUtils.toString(entity2);
                    Log.e("Responce Edit profile", responseString);

                } catch (Exception e1) {

                    e1.printStackTrace();

                    Log.e("Excep", e1.getMessage());

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseString;

        }

        protected void onPostExecute(String responseString) {

            // {"message":"Your Password changed successfully.","status":true}
            JSONObject jObject, jobj;
            String get_status = "", get_message = "";

            try {

                jObject = new JSONObject(responseString);

                get_message = jObject.getString("message").trim();
                get_status = jObject.getString("status").trim();

                jobj = jObject.getJSONObject("user");
                if (get_status.equalsIgnoreCase("true")) {

                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.NAME, jobj.getString("name"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.EMAIL, jobj.getString("email"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.COUNTRY,
                            jobj.getString("country"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.CITY, jobj.getString("city"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.ADDRESS,
                            jobj.getString("address"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.AGE, jobj.getString("age"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.GENDER,
                            jobj.getString("gender"));
                    GlobalClaass.savePrefrencesfor(context,
                            PreferenceConnector.IMAGE, jobj.getString("image"));

                    GlobalClaass.showToastMessage(context, get_message);

                    startActivity(new Intent(context,
                            JobListActivity.class));
                    GlobalClaass.activitySlideBackAnimation(context);
                    finish();

                } else {
                    GlobalClaass.showToastMessage(context, get_message);
                }

            } catch (Exception e) {

            }

            GlobalClaass.hideProgressBar(context);

        }
    }

    @Override
    public void onBackPressed() {
        new CustomAlert(context, TeacherProfileEditActivity.this).doubleButtonAlertDialog(
                "Would you like to save your profile changes?",
                "Save", "cancel", "dblBtnCallbackResponse", 1100);

    }

}
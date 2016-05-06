package com.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class CustomJsonImageRequest extends Request<JSONObject> {

    public static final String KEY_PICTURE = "image";
    public static final String KEY_SCHOOL_PHOTO = "school_photo";

    private Listener<JSONObject> listener;
    private Map<String, String> params;
    private File file1;
    private File file2;

    // private MultipartEntity entity = new MultipartEntity();
    private HttpEntity mHttpEntity;

    public CustomJsonImageRequest(String url, Map<String, String> params,
                                  Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = reponseListener;
        this.params = params;

    }

    public CustomJsonImageRequest(int method, String url, Map<String, String> params,
                                  Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;

    }


    public CustomJsonImageRequest(int method, String url,
                                  Map<String, String> params,
                                  File file1, File file2,
                                  Listener<JSONObject> reponseListener,
                                  ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
        this.file1 = file1;
        this.file2 = file2;

        mHttpEntity = buildMultipartEntity(file1, file2);

        //buildMultipartEntity();
    }

    private HttpEntity buildMultipartEntity(File file1, File file2) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        if (file1 != null) {
            String fileName1 = file1.getName();

            FileBody fileBody1 = new FileBody(file1);
            builder.addPart(KEY_PICTURE, fileBody1);
        }

        if(file2!=null){
            String fileName2 = file2.getName();

            FileBody fileBody2 = new FileBody(file2);
            builder.addPart(KEY_SCHOOL_PHOTO, fileBody2);
        }
        try {
            for (String key : params.keySet())
                builder.addPart(key, new StringBody(params.get(key)));

        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
        return builder.build();
    }

//    private void buildMultipartEntity() {
//        entity.addPart("image", new FileBody(file));
//
//        try {
//            for (String key : params.keySet())
//                entity.addPart(key, new StringBody(params.get(key)));
//
//        } catch (UnsupportedEncodingException e) {
//            VolleyLog.e("UnsupportedEncodingException");
//        }
//    }


    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws com.android.volley.AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }

}

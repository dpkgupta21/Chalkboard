package com.chalkboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;
import android.widget.Toast;

import com.chalkboard.utility.Utils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
@SuppressLint("NewApi")
public class ImageLoader11 {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Context con;
	public ImageLoader11(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
		con = context;
	}

	final int stub_id = R.drawable.ic_launcher;

	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(getRoundedCornerBitmap(blur(con, bitmap)));
		else {
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);

		}
	}

	public static Bitmap blur(Context context, Bitmap image) {
        int width = Math.round(image.getWidth() * 0.4f);
        int height = Math.round(image.getHeight() * 0.4f);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(7.5f);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

	@SuppressLint("NewApi")
	public void DisplayImage(String url, ImageView imageView, Context con) {
		try{
			imageViews.put(imageView, url);
			Bitmap bitmap = memoryCache.get(url);
			if (bitmap != null)
				imageView.setImageBitmap(getRoundedCornerBitmap(bitmap));
			else {
				queuePhoto(url, imageView);
				imageView.setImageResource(stub_id);

				final RenderScript rs = RenderScript.create( con );
				final Allocation input = Allocation.createFromBitmap( rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
				final Allocation output = Allocation.createTyped( rs, input.getType() );
				final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create( rs, Element.U8_4( rs ) );
				script.setRadius( 3.f /* e.g. 3.f */ );
				script.setInput( input );
				script.forEach( output );
				output.copyTo( bitmap );

			}
		}
		catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(con,""+e.getMessage(), 3000).show();
		}
	}
	


private void queuePhoto(String url, ImageView imageView) {
	PhotoToLoad p = new PhotoToLoad(url, imageView);
	executorService.submit(new PhotosLoader(p));
}

private Bitmap getBitmap(String url) {
	File f = fileCache.getFile(url);

	// from SD cache
	Bitmap b = decodeFile(f);
	if (b != null)
		return b;

	// from web
	try {
		Bitmap bitmap = null;
		URL imageUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) imageUrl
				.openConnection();
		conn.setConnectTimeout(30000);
		conn.setReadTimeout(30000);
		conn.setInstanceFollowRedirects(true);
		InputStream is = conn.getInputStream();
		OutputStream os = new FileOutputStream(f);
		Utils.CopyStream(is, os);
		os.close();
		bitmap = getRoundedCornerBitmap(decodeFile(f));

		return bitmap;
	} catch (Throwable ex) {
		ex.printStackTrace();
		if (ex instanceof OutOfMemoryError)
			memoryCache.clear();
		return null;
	}
}

public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
			bitmap.getHeight(), Config.ARGB_8888);
	Canvas canvas = new Canvas(output);

	final int color = 0xff424242;
	final Paint paint = new Paint();
	final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	final RectF rectF = new RectF(rect);
	final float roundPx = 12;

	paint.setAntiAlias(true);
	canvas.drawARGB(0, 0, 0, 0);
	paint.setColor(color);
	canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	canvas.drawBitmap(bitmap, rect, rect, paint);

	return output;
}

// decodes image and scales it to reduce memory consumption
private Bitmap decodeFile(File f) {
	try {
		// decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(new FileInputStream(f), null, o);

		// Find the correct scale value. It should be the power of 2.
		final int REQUIRED_SIZE = 70;
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE
					|| height_tmp / 2 < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	} catch (FileNotFoundException e) {
	}
	return null;
}

// Task for the queue
private class PhotoToLoad {
	public String url;
	public ImageView imageView;

	public PhotoToLoad(String u, ImageView i) {
		url = u;
		imageView = i;
	}
}

class PhotosLoader implements Runnable {
	PhotoToLoad photoToLoad;

	PhotosLoader(PhotoToLoad photoToLoad) {
		this.photoToLoad = photoToLoad;
	}

	@Override
	public void run() {
		if (imageViewReused(photoToLoad))
			return;
		Bitmap bmp = getBitmap(photoToLoad.url);
		memoryCache.put(photoToLoad.url, getRoundedCornerBitmap(bmp));
		if (imageViewReused(photoToLoad))
			return;
		BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
		Activity a = (Activity) photoToLoad.imageView.getContext();
		a.runOnUiThread(bd);
	}
}

boolean imageViewReused(PhotoToLoad photoToLoad) {
	String tag = imageViews.get(photoToLoad.imageView);
	if (tag == null || !tag.equals(photoToLoad.url))
		return true;
	return false;
}

// Used to display bitmap in the UI thread
class BitmapDisplayer implements Runnable {
	Bitmap bitmap;
	PhotoToLoad photoToLoad;

	public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
		bitmap = b;
		photoToLoad = p;
	}

	public void run() {
		if (imageViewReused(photoToLoad))
			return;
		if (bitmap != null)
			photoToLoad.imageView
			.setImageBitmap(getRoundedCornerBitmap(bitmap));
		else
			photoToLoad.imageView.setImageResource(stub_id);
	}
}

public void clearCache() {
	memoryCache.clear();
	fileCache.clear();
}

}
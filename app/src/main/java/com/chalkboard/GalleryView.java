package com.chalkboard;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.android.volley.toolbox.ImageLoader;
import com.volley.ApplicationController;

public class GalleryView extends Activity {

	private ViewPager mViewPager;

	ArrayList<String> dataList = new ArrayList<String>();

	Activity context = null;
	
	static int pad;
	
	int position;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		context = this;
		
		setContentView(R.layout.activity_gallery_view);

		pad = (int) getResources().getDimension(R.dimen.padding);
		
		dataList = getIntent().getStringArrayListExtra("dataList");

		position = getIntent().getIntExtra("position", 0);
		
		mViewPager = (HackyViewPager) findViewById(R.id.gallery_pager);
		//setContentView(mViewPager);

		mViewPager.setAdapter(new SamplePagerAdapter(dataList));

		
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mViewPager.setPageTransformer(true,
					new ZoomOutPageTransformer());
		}
		
		mViewPager.setCurrentItem(position);
		
	}

	static class SamplePagerAdapter extends PagerAdapter {

		ImageLoader imageLoader = ApplicationController.getInstance()
				.getImageLoader();

		ArrayList<String> dataList;

		public SamplePagerAdapter(ArrayList<String> dataList) {

			this.dataList = dataList;

		}

		@Override
		public int getCount() {
			return dataList.size();

		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());

			
			
			photoView.setPadding(pad, pad, pad, pad);
			
			try {
				String urlStr = dataList.get(position);
				URL url = new URL(urlStr);
				URI uri = new URI(url.getProtocol(), url.getUserInfo(),
						url.getHost(), url.getPort(), url.getPath(),
						url.getQuery(), url.getRef());
				url = uri.toURL();

				if (imageLoader == null)
					imageLoader = ApplicationController.getInstance()
							.getImageLoader();

				photoView.setImageUrl(url.toString(), imageLoader);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// photoView.setImageResource(sDrawables[position]);

			container.addView(photoView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}



}

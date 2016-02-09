package com.chalkboard;

import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationListAdapter extends BaseAdapter {

	Typeface font,font2;
	Activity mContext;
	LayoutInflater inflater;
	private List<NotificationObject> mainDataList = null;

	ImageLoader imageloader = null;

	public NotificationListAdapter(Activity context, List<NotificationObject> mainDataList) {

		mContext = context;
		this.mainDataList = mainDataList;
		inflater = LayoutInflater.from(mContext);
		font=Typeface.createFromAsset(mContext.getAssets(), "mark.ttf");
		font2=Typeface.createFromAsset(mContext.getAssets(), "marlbold.ttf");
		imageloader = new ImageLoader(mContext);

	}

	class ViewHolder {
		protected TextView h1,h2,h3,date;
		ImageView image;


	}

	@Override
	public int getCount() {
		return mainDataList.size();
	}

	@Override
	public NotificationObject getItem(int position) {
		return mainDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.item_notification_list, null);

			holder.h1 = (TextView) view.findViewById(R.id.job_name);
			holder.h2 = (TextView) view.findViewById(R.id.job_location);
			holder.h3 = (TextView) view.findViewById(R.id.job_info);
			holder.date = (TextView) view.findViewById(R.id.job_date);
			holder.image = (ImageView) view.findViewById(R.id.job_image);

try {
				
				holder.h1.setTypeface(font2);
				holder.h2.setTypeface(font);
				holder.h3.setTypeface(font);
				holder.date.setTypeface(font);
				
			} catch (Exception e) {
				
			}
			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.h1.setText(mainDataList.get(position).getHeading1());
		
		holder.h2.setText(mainDataList.get(position).getHeading2());
		holder.h3.setText(mainDataList.get(position).getHeading3());
		holder.date.setText(mainDataList.get(position).getTimestamp());
		
		imageloader.DisplayImage(mainDataList.get(position).getImage(),
				holder.image);

		return view;
	}

}
package com.chalkboard;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InboxListAdapter extends BaseAdapter {

	Activity mContext;
	LayoutInflater inflater;
	private List<InboxObject> mainDataList = null;
	Typeface font,font2;
	ImageLoader imageloader = null;

	public InboxListAdapter(Activity context, List<InboxObject> mainDataList) {

		mContext = context;
		this.mainDataList = mainDataList;
		inflater = LayoutInflater.from(mContext);
		font = Typeface.createFromAsset(mContext.getAssets(), "fonts/mark.ttf");
		font2=Typeface.createFromAsset(mContext.getAssets(), "fonts/marlbold.ttf");
		imageloader = new ImageLoader(mContext);

	}

	class ViewHolder {
		protected TextView name;
		protected ImageView image;
		protected TextView date;
		protected TextView lastmessage;
		protected TextView unread;

	}

	@Override
	public int getCount() {
		return mainDataList.size();
	}

	@Override
	public InboxObject getItem(int position) {
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
			view = inflater.inflate(R.layout.item_inbox_list, null);

			holder.name = (TextView) view.findViewById(R.id.message_name);
			holder.unread = (TextView) view.findViewById(R.id.message_unread);
			
			holder.date = (TextView) view.findViewById(R.id.message_date);
			

			holder.image = (ImageView) view.findViewById(R.id.message_image);
			
			holder.lastmessage = (TextView) view.findViewById(R.id.messagelast);

			try {

				holder.name.setTypeface(font2);
				holder.unread.setTypeface(font);
				holder.date.setTypeface(font);
				holder.lastmessage.setTypeface(font);
				
			} catch (Exception e) {

			}
			
			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.name.setText(mainDataList.get(position).getUser());
		holder.date.setText(mainDataList.get(position).getTimestamp());

		if (Integer.parseInt(mainDataList.get(position).getUnread()) > 0) {
			holder.unread.setText(mainDataList.get(position).getUnread());
			holder.unread.setVisibility(View.VISIBLE);
			holder.name.setTextColor(Color.parseColor("#000000"));

		}else{
			holder.unread.setText("");
			holder.unread.setVisibility(View.INVISIBLE);
			holder.name.setTextColor(Color.parseColor("#00c7d4"));
		}

		holder.lastmessage.setText(mainDataList.get(position).getMessage());

		imageloader.DisplayImage(
				mainDataList.get(position).getImage(), holder.image);

		return view;
	}

}
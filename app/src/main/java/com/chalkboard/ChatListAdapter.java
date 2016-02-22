package com.chalkboard;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatListAdapter extends BaseAdapter {

	Activity mContext;
	LayoutInflater inflater;
	private List<ChatObject> mainDataList = null;
	Typeface font;
	ImageLoader11 imageloader11 = null;
	ImageLoader imageloader = null;

	public ChatListAdapter(Activity context, List<ChatObject> mainDataList) {

		mContext = context;
		this.mainDataList = mainDataList;
		inflater = LayoutInflater.from(mContext);
		font = Typeface.createFromAsset(mContext.getAssets(), "fonts/mark.ttf");
		imageloader11 = new ImageLoader11(mContext);
		imageloader = new ImageLoader(mContext);

	}

	class ViewHolder {
		// protected TextView time;
		protected ImageView image;
		protected ImageView image1;
		
		protected ImageView b;
		protected ImageView b1;

		protected TextView message;

		RelativeLayout rlBG;
		ImageView p;
		ImageView p1;
		ImageView attachment;

	}

	@Override
	public int getCount() {
		return mainDataList.size();
	}

	@Override
	public ChatObject getItem(int position) {
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
			view = inflater.inflate(R.layout.item_chat_list, null);

			// holder.time = (TextView) view.findViewById(R.id.message_time);

			holder.rlBG = (RelativeLayout) view.findViewById(R.id.chat_bg);

			holder.image = (ImageView) view.findViewById(R.id.message_image);

			holder.p = (ImageView) view.findViewById(R.id.p);
			holder.p1 = (ImageView) view.findViewById(R.id.p1);
			
			holder.b = (ImageView) view.findViewById(R.id.b);
			holder.b1 = (ImageView) view.findViewById(R.id.b1);

			holder.image1 = (ImageView) view.findViewById(R.id.message_image1);

			holder.message = (TextView) view.findViewById(R.id.message);
			holder.attachment = (ImageView) view.findViewById(R.id.attachment);

			try {

				holder.message.setTypeface(font);
			} catch (Exception e) {

			}

			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}

		// holder.time.setText(mainDataList.get(position).getTimestamp());
		if (!mainDataList.get(position).getAttachment()
				.equalsIgnoreCase("null")) {
			imageloader11.DisplayImage(mainDataList.get(position)
					.getAttachment(), holder.attachment);
			holder.message.setText("");
			holder.attachment.setVisibility(View.VISIBLE);
			holder.attachment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					ArrayList<String> imagesDataList = new ArrayList<String>();
					imagesDataList.add(mainDataList.get(position)
							.getAttachment());
					mContext.startActivity(new Intent(mContext,
							GalleryView.class).putStringArrayListExtra(
							"dataList", imagesDataList).putExtra("position",
							position));
				}
			});
		} else {
			holder.message.setText(mainDataList.get(position).getMessage());
			holder.attachment.setImageResource(0);
			holder.attachment.setVisibility(View.GONE);

		}

		if (mainDataList.get(position).getUser_id()
				.equalsIgnoreCase(GlobalClaass.getUserId(mContext))) {
			imageloader.DisplayImage(mainDataList.get(position).getUserImg(),
					holder.image1);
			
			holder.b1.setVisibility(View.VISIBLE);
			holder.b.setVisibility(View.GONE);
			
			holder.image.setImageResource(0);
			holder.rlBG.setBackgroundResource(R.drawable.chat_bg);
			holder.p.setVisibility(View.VISIBLE);
			holder.p1.setVisibility(View.GONE);

		} else {
			imageloader.DisplayImage(mainDataList.get(position).getUserImg(),
					holder.image);
			
			holder.b.setVisibility(View.VISIBLE);
			holder.b1.setVisibility(View.GONE);
			
			holder.image1.setImageResource(0);
			holder.rlBG.setBackgroundResource(R.drawable.chat_bg1);
			holder.p1.setVisibility(View.VISIBLE);
			holder.p.setVisibility(View.GONE);
		}

		return view;
	}

}
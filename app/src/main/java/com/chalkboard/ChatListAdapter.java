package com.chalkboard;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class ChatListAdapter extends BaseAdapter {

	Activity mContext;
	LayoutInflater inflater;
	private List<ChatObject> mainDataList = null;
	//Typeface font;
	ImageLoader11 imageloader11 = null;
	ImageLoader imageloader = null;

	public ChatListAdapter(Activity context, List<ChatObject> mainDataList) {

		mContext = context;
		this.mainDataList = mainDataList;
		inflater = LayoutInflater.from(mContext);
		//font = Typeface.createFromAsset(mContext.getAssets(), "fonts/mark.ttf");
		imageloader11 = new ImageLoader11(mContext);
		imageloader = new ImageLoader(mContext);

	}

	class ViewHolder {
		// protected TextView time;
		RelativeLayout chat_bg;
		protected ImageView b;
		protected ImageView message_image;
		protected TextView message;

		protected ImageView message_image1;
		protected ImageView b1;
		RelativeLayout chat_bg1;
		protected TextView message1;
		//ImageView attachment;

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

			holder.chat_bg = (RelativeLayout) view.findViewById(R.id.chat_bg);
			holder.chat_bg1 = (RelativeLayout) view.findViewById(R.id.chat_bg1);

			holder.message_image = (ImageView) view.findViewById(R.id.message_image);


			
			holder.b = (ImageView) view.findViewById(R.id.b);
			holder.b1 = (ImageView) view.findViewById(R.id.b1);

			holder.message_image1 = (ImageView) view.findViewById(R.id.message_image1);

			holder.message = (TextView) view.findViewById(R.id.message);
			holder.message1 = (TextView) view.findViewById(R.id.message1);
			//holder.attachment = (ImageView) view.findViewById(R.id.attachment);

			try {

				//holder.message.setTypeface(font);
			} catch (Exception e) {

			}

			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}

		// holder.time.setText(mainDataList.get(position).getTimestamp());
//		if (!mainDataList.get(position).getAttachment()
//				.equalsIgnoreCase("null")) {
//			imageloader11.DisplayImage(mainDataList.get(position)
//					.getAttachment(), holder.attachment);
//			holder.message.setText("");
//			holder.attachment.setVisibility(View.VISIBLE);
//			holder.attachment.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					ArrayList<String> imagesDataList = new ArrayList<String>();
//					imagesDataList.add(mainDataList.get(position)
//							.getAttachment());
//					mContext.startActivity(new Intent(mContext,
//							GalleryView.class).putStringArrayListExtra(
//							"dataList", imagesDataList).putExtra("position",
//							position));
//				}
//			});
//		} else {

		//	holder.message.setText(mainDataList.get(position).getMessage());
			//holder.attachment.setImageResource(0);
			//holder.attachment.setVisibility(View.GONE);

		//}
		if (mainDataList.get(position).getUser_id()
				.equalsIgnoreCase(GlobalClaass.getUserId(mContext))) {
			holder.chat_bg1.setVisibility(View.VISIBLE);
			holder.chat_bg.setVisibility(View.GONE);

			holder.b1.setVisibility(View.VISIBLE);
			holder.b.setVisibility(View.GONE);

			holder.message1.setText(mainDataList.get(position).getMessage());
			imageloader.DisplayImage(mainDataList.get(position).getUserImg(),
					holder.message_image1);

		}else{
			holder.chat_bg1.setVisibility(View.GONE);
			holder.chat_bg.setVisibility(View.VISIBLE);

			holder.b1.setVisibility(View.GONE);
			holder.b.setVisibility(View.VISIBLE);

			holder.message.setText(mainDataList.get(position).getMessage());
			imageloader.DisplayImage(mainDataList.get(position).getUserImg(),
					holder.message_image);
		}


//		if (mainDataList.get(position).getUser_id()
//				.equalsIgnoreCase(GlobalClaass.getUserId(mContext))) {
//			imageloader.DisplayImage(mainDataList.get(position).getUserImg(),
//					holder.image1);
//
//			holder.b1.setVisibility(View.VISIBLE);
//			holder.b.setVisibility(View.GONE);
//
//			holder.image.setImageResource(0);
//			holder.rlBG.setBackgroundResource(R.drawable.chat_msg_boundary);
//
//
//
//		} else {
//			imageloader.DisplayImage(mainDataList.get(position).getUserImg(),
//					holder.image);
//
//			holder.b.setVisibility(View.VISIBLE);
//			holder.b1.setVisibility(View.GONE);
//
//			holder.image1.setImageResource(0);
//			holder.rlBG.setBackgroundResource(R.drawable.chat_msg_boundary);
//		}

		return view;
	}

}
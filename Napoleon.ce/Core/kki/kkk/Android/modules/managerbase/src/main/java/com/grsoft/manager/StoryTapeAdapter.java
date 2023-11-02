package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.database.StoryTape;
import com.grsoft.database.StoryTapeItem;
import com.grsoft.database.StoryTapePic;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.BitmapUtils;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StoryTapeAdapter extends BaseAdapter{
	private List<StoryTape> data = new ArrayList<StoryTape>();
	private Context context;
	private SimpleDateFormat sdf;
	
	public StoryTapeAdapter(Context context) {
		this.context = context;
		sdf = new HumanDateFormat(context);
		reload();
	}
	
	public void reload() {
		data.clear();
		DataTraveler.travel(StoryTape.class, new DataTraveler.Travel<StoryTape>(true) {

			@Override
			public boolean travel(DataTraveler<StoryTape> item) {
				data.add(item.data);
				return true;
			}}, null);
		
		Collections.sort(data, new Comparator<StoryTape>() {

			@Override
			public int compare(StoryTape lhs, StoryTape rhs) {
				return lhs.sended.compareTo(rhs.sended) * -1;
			}});
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = View.inflate(context, R.layout.storytaperow, null);
		}
		
		StoryTape tape = (StoryTape) getItem(position);
		
		TextView tv = (TextView) convertView.findViewById(R.id.tvOrg);
		tv.setText(tape.org);
		
		tv = (TextView) convertView.findViewById(R.id.tvDate);
		tv.setText(sdf.format(tape.sended));
		
		tv = (TextView) convertView.findViewById(R.id.tvUser);
		tv.setText(tape.username);
		
		ViewGroup docs = (ViewGroup) convertView.findViewById(R.id.docs);
		docs.removeAllViews();
		
		for(StoryTapeItem i : tape.items) {
			View v = View.inflate(context, R.layout.storytapedocrow, null);
			
			tv = (TextView) v.findViewById(R.id.tvName);
			tv.setText(i.name);
			
			tv = (TextView) v.findViewById(R.id.tvValue);
			tv.setText(Html.fromHtml(i.text));
			
			docs.addView(v);
		}
		
		ViewGroup pics = (ViewGroup) convertView.findViewById(R.id.pics);
		pics.removeAllViews();
		int sz = (int) context.getResources().getDimension(R.dimen.story_tape_pic_size);
		
		for(StoryTapePic p : tape.photo) {
			View v = View.inflate(context, R.layout.storytapepic, null);
			ImageView iv = (ImageView) v.findViewById(R.id.ivPic);
			iv.setTag(p);
			iv.setImageDrawable(BitmapUtils.createBitmap(context, p.pic, sz, sz));
			iv.setOnClickListener(showImage);
			pics.addView(v);
		}
		
		pics.setVisibility(tape.photo.size() == 0 ? View.GONE : View.VISIBLE);

		return convertView;
	}
	
	View.OnClickListener showImage = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			StoryTapePic p = (StoryTapePic) v.getTag();
			com.grsoft.napoleon.util.Config cfg = ConfigManager.getConfig();
			new LoadPicture(v.getContext()).execute(cfg.hrefBase() + p.name);
		}
	};
}

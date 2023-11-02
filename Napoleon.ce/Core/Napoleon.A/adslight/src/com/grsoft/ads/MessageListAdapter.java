package com.grsoft.ads;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.MessageNew;
import com.grsoft.network.BaseSimpleAdapter;
import com.grsoft.util.Util;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageListAdapter extends BaseSimpleAdapter{
	private Context context;
	
	public MessageListAdapter(Context context){
		this.context = context;
	}
	
	private List<MessageNew> data = new ArrayList<MessageNew>();
	
	@Override public int getCount() { return data.size();	}

	@Override public Object getItem(int position) { return data.get(position); }
	
	public void load(){
		data.clear();
		
		final String order = "date DESC LIMIT 20";
		DataTraveler.travel(MessageNew.class, new DataTraveler.Travel<MessageNew>(true) {
			@Override
			public boolean travel(DataTraveler<MessageNew> item) {
				data.add(item.data);
				return true;
			}}, null, order); 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)
			convertView = View.inflate(context, R.layout.messagelistrow, null);
		
		MessageNew msg = (MessageNew) getItem(position);
		boolean b = msg.read == 0;
		
		valueTextView(convertView, R.id.tvDate, Util.simpleDateFormat.format(msg.date), b);
		valueTextView(convertView, R.id.tvText, msg.message, b);
		
		return super.getView(position, convertView, parent);
	}
	
	private void valueTextView(View view, int id, String text, boolean bold){
		TextView tv = (TextView) view.findViewById(id);
		tv.setText(Html.fromHtml(text));
		tv.setTypeface(null, bold ? Typeface.BOLD : Typeface.NORMAL);
	}
}

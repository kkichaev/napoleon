package com.grsoft.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Message;
import com.grsoft.dataobjects.impl.MessageImpl;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.util.Util;

public class Messages extends Activity {
	ListView lvMessages;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messages);
		lvMessages = (ListView) findViewById(R.id.lvMessages);
	}
	
	public static void open(Context context){
		Intent intent = new Intent(context, Messages.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		lvMessages.setAdapter(MessagesAdapter.create(this));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		DataBaseAdapter<?> adapter = (DataBaseAdapter<?>)lvMessages.getAdapter();
		
		if (adapter != null)
			adapter.close();
	}
}

class MessagesAdapter extends DataBaseAdapter<Message>{

	public MessagesAdapter(Context context)
			throws IllegalAccessException, InstantiationException {
		super(context, new MessageImpl(), "", "date desc");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)
			convertView = View.inflate(context, R.layout.message_list_row, null);
		
		MessageImpl messageImpl = (MessageImpl) getItem(position);
		
		if(messageImpl != null){
			((TextView)convertView.findViewById(R.id.tvData))
				.setText(Util.simpleDateFormat.format(messageImpl.getData().date));
			((TextView)convertView.findViewById(R.id.tvText))
				.setText(messageImpl.getData().message);
		}
		
		return convertView;
	}
	
	public static MessagesAdapter create(Context context){
		MessagesAdapter result = null;
		
		try{
			result = new MessagesAdapter(context);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}

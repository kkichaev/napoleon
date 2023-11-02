package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ArchiveMessage;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Message;
import com.grsoft.dataobjects.impl.ManagerAgentImpl;

@SuppressLint("SimpleDateFormat")
public class HistoryMessage extends Activity {
	private static final String USERID = "userid";
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
	
	public static Class<? extends Activity> activity = HistoryMessage.class;
	private ListView list;
	
	public static void open(Context context, String userid){
		Intent intent = new Intent(context, activity);
		intent.putExtra(USERID, userid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historymessage);
		
		final String userid = getIntent().getStringExtra(USERID);

		ManagerAgentImpl impl = new ManagerAgentImpl();
		impl.read("id", userid);
		
		TextView tv = (TextView) findViewById(R.id.tvName);
		tv.setText(getString(R.string.messagehistoryofagent, impl.getData().name));
		
		list = (ListView) findViewById(R.id.list);
		list.setDividerHeight(0);
		list.setAdapter(new BaseAdapter() {
			List<Message> data = new ArrayList<Message>();
			
			{
				ArchiveMessage msg = new ArchiveMessage();
				DbReader reader = new DbReader();
				boolean bdo = reader.select(msg, DataObjectInfo.getInstance().getTableName(msg.getClass()), String.format("userid='%s'", userid));
				
				while(bdo){
					data.add((Message) msg.clone());
					bdo = reader.selectNext(msg);
				}
				
				reader.close();
				
				Collections.sort(data, new Comparator<Message>() {

					@Override
					public int compare(Message lhs, Message rhs) {
						return lhs.date.compareTo(rhs.date);
					}
				});
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null)
					convertView = View.inflate(HistoryMessage.this, R.layout.historymessage_row, null);
				
				Message msg = (Message) getItem(position);
				
				TextView tv = (TextView) convertView.findViewById(R.id.tvDate);
				tv.setText(sdf.format(msg.date));
				
				tv = (TextView) convertView.findViewById(R.id.tvMessage);
				tv.setText(msg.message);
				
				convertView.setBackgroundResource(position % 2 != 0 ? 
						R.drawable.even_row_selector :
						R.drawable.list_selector);	
				
				return convertView;
			}
			
			@Override
			public long getItemId(int position) { return 0; }
			
			@Override
			public Object getItem(int position) { return data.get(position); }
			
			@Override
			public int getCount() {	return data.size(); }
		});
	}
}

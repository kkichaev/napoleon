package com.grsoft.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.dataobjects.ChatAgent;
import com.grsoft.dataobjects.ChatData;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.impl.ChatAgentImpl;
import com.grsoft.dataobjects.impl.ChatImpl;
import com.grsoft.network.BaseFragmentActivity;


public class Chat extends BaseFragmentActivity {
	public static Class<? extends Chat> activity = Chat.class;
	protected List<ChatData> data = new ArrayList<ChatData>();
	private View btnPost;
	private EditText edText;
	protected BaseAdapter adapter;
	private ListView list;
	private String myid = "";
	protected Map<String, String> users = new HashMap<String, String>();
	
	@Override
	protected int getLayoutID() {return R.layout.chat;}
	
	@Override
	protected void inflateView() {
		btnPost = findViewById(R.id.btnPost);
		edText = (EditText) findViewById(R.id.edText);
		list = (ListView) findViewById(R.id.list);
	}
	
	@Override
	protected void init() {
		fillData();
		adapter = (BaseAdapter) createAdapter();
		initUsers();
	}

	protected void initUsers() {
		myid = ChatAgentImpl.getMyid();
		DataTraveler.travel(ChatAgent.class, new DataTraveler.Travel<ChatAgent>() {

			@Override
			public boolean travel(DataTraveler<ChatAgent> item) {
				users.put(item.data.id, item.data.name);
				item.data = new ChatAgent();
				return true;
			}
		}, null);
	}
	
	protected void fillData() {
		data.clear();
		DataTraveler.travel(ChatData.class, new DataTraveler.Travel<ChatData>() {

			@Override
			public boolean travel(DataTraveler<ChatData> item) {
				data.add(item.data);
				return true;
			}
			
			@Override public boolean isDataNewInstance() { return true; }
		}, null);
		
			
		
		Collections.sort(data, new Comparator<ChatData>() {
			@Override public int compare(ChatData lhs, ChatData rhs) { return lhs.created.compareTo(rhs.created);	}});
	}

	@Override
	protected void initView() {
		btnPost.setOnClickListener(onPost());
		list.setAdapter(adapter);
		list.setDividerHeight(0);
		
		enableBtnPost();
	}

	protected void enableBtnPost() {
		btnPost.setEnabled(myid.trim().length() > 0);
	}

	protected ListAdapter createAdapter() {	
		return new BaseAdapter() {
		
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null)
					convertView = createChatItem();
				
				ChatData c = (ChatData) getItem(position);
				updateChatItem(convertView, c);
				return convertView;
			}
			
			@Override
			public long getItemId(int position) { return position; }
			
			@Override
			public Object getItem(int position) { return data.get(position);}
			
			@Override
			public int getCount() { return data.size(); }
		}; 
	}

	protected void updateChatItem(View view, ChatData c) {
		StringBuilder sb = new StringBuilder();
		int g = Gravity.RIGHT;
		sb.append("<b>");
		int bkg = R.drawable.bubble_b;
		if(c.userid.equals(myid)){
			sb.append("ß");
			bkg = R.drawable.bubble_a;
			g = Gravity.LEFT;
		}else
			sb.append(getUserName(c.userid));
		sb.append("</b>");
		sb.append(":( ").append("").append(")<br><i>").append(c.text).append("</i>");
		
		TextView tv = ((TextView)view.findViewById(R.id.text));
		tv.setText(Html.fromHtml(sb.toString()));
		tv.setBackgroundDrawable(getResources().getDrawable(bkg));
		
		LinearLayout.LayoutParams params = (LayoutParams) tv.getLayoutParams();
		params.gravity = g;
		tv.setLayoutParams(params);
	}

	private Object getUserName(String userid) {
		String result = userid;
		
		if(users.containsKey(userid))
			result = users.get(userid);
		
		return result;
	}

	protected View createChatItem() { return View.inflate(this, R.layout.chat_row, null); }

	private OnClickListener onPost() {
		return new OnClickListener() { @Override public void onClick(View v) {	post(); }};
	}

	protected void post() {
		String msg = edText.getText().toString().trim();
		
		if(msg.length() > 0){
			ChatImpl ci = new ChatImpl();
			ci.init();
			ChatData c = fillChatData(msg, ci);
			ci.write();
			ci.close();
			data.add(c);
			adapter.notifyDataSetChanged();
			sendBroadcast(new Intent(ChatService.SYNC_ACTION));
			
			edText.setText("");
		}
	}

	protected ChatData fillChatData(String msg, ChatImpl ci) {
		ChatData c = ci.getData();
		c.userid = myid;
		c.text = msg;
		return c;
	}
	
	BroadcastReceiver refresh = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			fillData();
			adapter.notifyDataSetChanged();
			
			if(myid.trim().length() == 0){
				initUsers();
				enableBtnPost();
			}
		}
	};
	
	protected void onResume() {
		super.onResume();
		registerReceiver(refresh, new IntentFilter(ChatService.SYNC_FINISHED));
	};
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(refresh);
	}
}

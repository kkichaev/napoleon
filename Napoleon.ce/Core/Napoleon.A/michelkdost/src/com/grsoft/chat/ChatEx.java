package com.grsoft.chat;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.ChatAgent;
import com.grsoft.dataobjects.ChatData;
import com.grsoft.dataobjects.ChatDataEx;
import com.grsoft.dataobjects.ChatGroup;
import com.grsoft.dataobjects.ChatUser;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.impl.ChatImpl;
import com.grsoft.napoleon.dostavka.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ChatEx extends Chat {
	private Spinner spGroup;
	
	
	@Override
	protected int getLayoutID() { return R.layout.chatex; }
	
	@Override protected void initView() {
		super.initView();
		
		final List<ChatGroup> data = new ArrayList<ChatGroup>();
		
		DataTraveler.travel(ChatGroup.class, new DataTraveler.Travel<ChatGroup>(true) {

			@Override
			public boolean travel(DataTraveler<ChatGroup> item) {
				data.add(item.data);
				return true;
			}}, null);
		
		ArrayAdapter<ChatGroup> aa = new GroupAdapter(getContext(), data);
		spGroup.setAdapter(aa);
		spGroup.setOnItemSelectedListener(spGroupOnSelect);
	}
	
	@Override
	protected void inflateView() {
		super.inflateView();
		spGroup = (Spinner) findViewById(R.id.spGroup);
	}
	
	private OnItemSelectedListener spGroupOnSelect = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}};
		
	protected android.widget.ListAdapter createAdapter() {
		return new ChatAdapter();
	};	
	
	class ChatAdapter extends BaseAdapter{
		private List<ChatData> filter = new ArrayList<ChatData>();
		
		private void reload(){
			filter.clear();
			
			String gid = "";
			ChatGroup g = (ChatGroup) spGroup.getSelectedItem();
			
			if (g != null)
				gid = g.id;
			
			for(ChatData d : data){
				ChatDataEx de = (ChatDataEx)d;
				
				if(gid.length() == 0 || de.group.equals(gid))
					filter.add(d);
			}
		}
		
		@Override public int getCount() { return filter.size();}
		@Override public Object getItem(int position) { return filter.get(position); }
		@Override public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = createChatItem();
			
			ChatData c = (ChatData) getItem(position);
			updateChatItem(convertView, c);
			return convertView;
		}
		
		@Override
		public void notifyDataSetChanged() {
			reload();
			super.notifyDataSetChanged();
		}
	}
	
	@Override
	protected ChatData fillChatData(String msg, ChatImpl ci) {
		ChatData result = super.fillChatData(msg, ci);
		
		ChatGroup g = (ChatGroup) spGroup.getSelectedItem();
		
		if(g != null){
			ChatDataEx e = (ChatDataEx)result;
			e.group = g.id;
		}
		
		return result;
	}
	
	@Override
	protected void initUsers() {
		super.initUsers();
		
		DataTraveler.travel(ChatUser.class, new DataTraveler.Travel<ChatUser>() {

			@Override
			public boolean travel(DataTraveler<ChatUser> item) {
				users.put(item.data.id, item.data.name);
				return true;
			}
		}, null);
	}
}

class GroupAdapter extends ArrayAdapter<ChatGroup>{
	static final int TEXT_VIEW_ID = R.id.tvFirmaName;
	
	public GroupAdapter(Context context,  List<ChatGroup> objects) {
		super(context, R.layout.simple_spinner_layout, TEXT_VIEW_ID, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		TextView tv = (TextView) view.findViewById(TEXT_VIEW_ID);
		ChatGroup g = getItem(position);
		
		tv.setText(g.title);
		
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}
}

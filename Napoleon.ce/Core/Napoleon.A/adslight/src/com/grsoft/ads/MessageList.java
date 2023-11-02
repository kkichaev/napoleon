package com.grsoft.ads;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.MessageNew;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class MessageList extends Activity implements OnItemClickListener, OnClickListener {
	private ListView list;
	
	public static void open(Context context){
		Intent i = new Intent(context, MessageList.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.messagelist);
		list = (ListView) findViewById(R.id.list);
		findViewById(R.id.btnOK).setOnClickListener(this);
		
		list.setAdapter(new MessageListAdapter(this));
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int pos, long arg3) {
		MessageNew msg = (MessageNew) adapter.getItemAtPosition(pos);
		msg.read ^= 1;
		DbWriter dw = new DbWriter();
		dw.updateRecord(msg, msg.rowid);
		dw.close();
		
		((BaseAdapter)adapter.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		MessageListAdapter a = (MessageListAdapter) list.getAdapter();
		
		if (a != null){
			a.load();
			a.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.btnOK)
			finish();
	}
	
}

/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Форма сообщений
 *
 * kki   08/04/2011   creating
 */
package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Message;
import com.grsoft.dataobjects.impl.MessageImpl;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.view.RegDurationActivity;

public class Messages extends RegDurationActivity 
	implements OnItemClickListener, OnDateSetListener {
	
	private ListView lvMessages;
	private OptionsMenuHelper optionsMenuHelper = new OptionsMenuHelper();
	private ContextMenuHelper contextMenuHelper = new ContextMenuHelper();
	
	public static SimpleDateFormat DATE_FORMAT =  new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(NetworkAsyncTask.MESSAGE_VIEW_LAYOUT);
		
		lvMessages = (ListView) findViewById(R.id.lvMessages);
		lvMessages.setOnItemClickListener(this);
		registerForContextMenu(lvMessages);
		
		try	{
			lvMessages.setAdapter(new MessagesListAdapter(this));
		}
		catch(Exception e){}
	}
	
	public static void open(Context context)
	{
		Intent i = new Intent(context, Messages.class);
		
		context.startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionsMenuHelper.onCreateOptionsMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		optionsMenuHelper.onOptionsItemSelect(item);
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		contextMenuHelper.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		contextMenuHelper.onContextItemSelected(item);		
		return super.onContextItemSelected(item);
	}
	
	public void notifyDataSetChanged(){
		BaseAdapter adapter = (BaseAdapter)lvMessages.getAdapter();
		
		if (adapter != null) 
			adapter.notifyDataSetChanged();
	}
	
	class MessagesListAdapter extends DataBaseAdapter<Message> {
		
		public MessagesListAdapter(Context context)
				throws IllegalAccessException, InstantiationException {
			super(context, new MessageImpl(), "", "date DESC");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MessageImpl messageImpl = (MessageImpl) cursor.get(position) ;
			Message message = messageImpl.getData(); 
			
			if (convertView == null)
				convertView = View.inflate(context, NetworkAsyncTask.MESSAGE_ROW_LAYOUT, null);
			
			TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			tvDate.setText(DATE_FORMAT.format(message.date));
			
			TextView tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
			tvMessage.setText(message.message);
			final int COUNT_LINE_PER_ROW = 2;
			tvMessage.setLines(COUNT_LINE_PER_ROW);
			
			convertView.setTag(messageImpl.getRowid());
			return convertView;
		}
	}
	
	class OptionsMenuHelper
	{
		public static final int MNU_DELETE_ALL_ID = 0;
		public static final int MNU_DELETE_ALL_BY_DATE_ID = 1;
		
		public void onCreateOptionsMenu(Menu menu)
		{
			menu.add(Menu.NONE, MNU_DELETE_ALL_ID, Menu.NONE, R.string.delete_all);
			menu.add(Menu.NONE, MNU_DELETE_ALL_BY_DATE_ID, Menu.NONE, R.string.delete_by_date);
		}
		
		public void onOptionsItemSelect(MenuItem item)
		{
			switch(item.getItemId())
			{
				case MNU_DELETE_ALL_ID:
					deleteAllMessages();
					break;
				case MNU_DELETE_ALL_BY_DATE_ID:
					deleteByDate();
					break;
			}
		}

		private void deleteByDate() {
			Calendar calendar = Calendar.getInstance();
			
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			
			DatePickerDialog datePickerDialog = 
				new DatePickerDialog(Messages.this, Messages.this, year, month, day);
			datePickerDialog.show();
			
		}

		private void deleteAllMessages() {
			AlertDialog delConfirm = new AlertDialog.Builder(Messages.this).create();
			delConfirm.setTitle(R.string.confirm);
			delConfirm.setMessage(getString(R.string.delete_all_messages));
			delConfirm.setButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MessageImpl messageImpl = new MessageImpl();
					messageImpl.deleteAll();
					notifyDataSetChanged();
				}
			});
			
			delConfirm.setButton2(getString(R.string.no),  (DialogInterface.OnClickListener)null);
			delConfirm.show();
		}
	}
	
	class ContextMenuHelper
	{
		private final int MNU_DEL_MESG_ID = 0;
		
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			
			menu.add(0, MNU_DEL_MESG_ID,0, R.string.delete);
		}
		
		public boolean onContextItemSelected(MenuItem item) {
			Long messageRowid = (Long)((AdapterContextMenuInfo)
					item.getMenuInfo()).targetView.getTag();
			
			switch(item.getItemId())
			{
				case MNU_DEL_MESG_ID:
					deleteMessage(messageRowid);
					break;
			}
			
			return false;
		}
		
		private void deleteMessage(long rowid){
			MessageImpl messageImpl = new MessageImpl();
			messageImpl.read(rowid);
			messageImpl.delete();
			notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		Long rowid = (Long)arg1.getTag();
		MessageImpl messageImpl = new MessageImpl();
		messageImpl.read(rowid);
		Message message = messageImpl.getData();
		builder.setTitle(String.format(getString(R.string.date) + ": %s", DATE_FORMAT.format(message.date)));
		builder.setMessage(message.message);
		builder.create().show();
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year,monthOfYear,dayOfMonth,0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		Date dateBegin = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date dateEnd = calendar.getTime();
		
		MessageImpl messageImpl = new MessageImpl();
		messageImpl.delete("[date] >= ? and [date] < ?", 
				new String[] {Long.toString(dateBegin.getTime()), Long.toString(dateEnd.getTime())});
		notifyDataSetChanged();
	}
}

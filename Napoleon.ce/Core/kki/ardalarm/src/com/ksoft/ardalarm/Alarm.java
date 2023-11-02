package com.ksoft.ardalarm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ksoft.ardalarm.database.DataBase;
import com.ksoft.ardalarm.database.TimeAlarm;

@SuppressLint("SimpleDateFormat")
public class Alarm extends Fragment {
	private Adapter adapter;
	private ListView list;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		list = (ListView) inflater.inflate(R.layout.alarm, null, false);
		
		return list;
	}

	@Override
	public void onPause() {
		super.onPause();
		adapter.getCursor().close();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		SQLiteDatabase db = new DataBase(getActivity()).getReadableDatabase();
		Cursor c = db.query(TimeAlarm.TABLE_NAME, 
				TimeAlarm.PROJECTION, null, null, null, null, null);
		adapter = new Adapter(getActivity(), c);
		list.setAdapter(adapter);
	}
	
}

class Adapter extends CursorAdapter{

	public Adapter(Context context, Cursor c) {
		super(context, c, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		tvName.setText(cursor.getString(cursor.getColumnIndex(TimeAlarm.NAME)));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup arg2) {
		View result = View.inflate(context, R.layout.alarmrow, null);
		return result;
	}
	
}

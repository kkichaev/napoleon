package com.grsoft.napoleon.manager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.impl.EditorObject;
import com.grsoft.dataobjects.impl.EditorObjectData;
import com.grsoft.dataobjects.impl.RatingActionTemplImpl;

public class Editors extends Activity {
	public static Class<? extends Activity> activity = Editors.class;
	private ListView list;

	public static void open(Context context) {
		Intent intent = new Intent(context, activity);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editors);

		list = (ListView) findViewById(R.id.list);
		list.setAdapter(new EditorsAdapter(this));
		list.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long arg3) {
				EditorsItems.open(view.getContext(),
						(Class<? extends EditorObject<?>>) adapter.getAdapter().getItem(pos));
			}
		});
	}
}

class EditorsAdapter extends BaseAdapter {
	public static ArrayList<Class<? extends EditorObject<?>>> data = 
			new ArrayList<Class<? extends EditorObject<?>>>();

	static {
		data.add(RatingActionTemplImpl.class);
	}

	private Context context;

	public EditorsAdapter(Context context) {
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.editors_row, null);

		int titleid = data.get(position).getAnnotation(EditorObjectData.class).titleid();
		((TextView) view.findViewById(R.id.tvName)).setText(titleid);

		return view;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public int getCount() {
		return data.size();
	}
}

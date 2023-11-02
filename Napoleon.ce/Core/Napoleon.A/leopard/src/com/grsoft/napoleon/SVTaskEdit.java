package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.grsoft.dataobjects.AgentTask;
import com.grsoft.dataobjects.CreatableDocumentW;
import com.grsoft.dataobjects.SVTask;
import com.grsoft.dataobjects.TaskCategory;
import com.grsoft.dataobjects.impl.AgentTaskImpl;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.SVTaskImpl;
import com.grsoft.dataobjects.impl.TaskCategoryImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.TaskBeginDoc;
import com.grsoft.script.dataobjects.impl.ScriptImplEx;
import com.grsoft.util.Util;

public class SVTaskEdit extends Activity{
	private static final String FINISH = "finish";
	private Spinner spCateg;
	private static final String ALL_FILTER = "<Все>"; 
	private TaskAdapter taskAdapter;
	private boolean finish;
	private static final String ROWID = "date";
	private static final String ID = "id";
	public long date;
	private String id = "";
	
	public static void open(Context context, long date, boolean finish, String id){
		Intent intent = new Intent(context, SVTaskEdit.class);
		intent.putExtra(ROWID, date);
		intent.putExtra(FINISH, finish);
		intent.putExtra(ID, id);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taskscedit);
		
		Intent intent = getIntent();
		
		if(intent != null){
			finish = intent.getBooleanExtra(FINISH, false);
			date = intent.getLongExtra(ROWID, -1);
			id = intent.getStringExtra(ID);
		}else if(savedInstanceState != null){
			date = savedInstanceState.getLong(ROWID);
			id = savedInstanceState.getString(ID);
		}
			
		
		Cursor<TaskCategory> c = new Cursor<TaskCategory>(new TaskCategoryImpl());
		List<String> catList = new ArrayList<String>();
		catList.add(ALL_FILTER);
		
		while(c.moveNext())
			catList.add(c.current().getData().name);
		
		c.close();
		String[] catArr = new String[catList.size()];
		catArr = catList.toArray(catArr);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, R.layout.simple_spinner_layout, catArr);
		spCateg = (Spinner) findViewById(R.id.spCateg);
		spCateg.setAdapter(adapter);
		
		taskAdapter = new TaskAdapter(this, finish, id);
		((ListView) findViewById(android.R.id.list))
			.setAdapter(taskAdapter);
		
		spCateg.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View arg1,
					int pos, long arg3) {
				String filter = (String) adapter.getItemAtPosition(pos);
				taskAdapter.requery(filter.equals(ALL_FILTER) ? 
						null : filter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ROWID, date);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (finish && keyCode == KeyEvent.KEYCODE_BACK){
			AgentTaskEdit.open(this, date);
			finish();
			return true;
		}else
			return super.onKeyDown(keyCode, event);
	}
}

class TaskAdapter extends BaseAdapter 
implements OnClickListener{
	List<SVTask> svtask = new ArrayList<SVTask>();
	Context context;
	boolean finish;
	String id;
	
	public TaskAdapter(Context context, boolean finish, String id){
		this.context = context;
		this.finish = finish;
		this.id = id;
		requery(null);
	}
	
	public void requery(String filter){
		final String cmt = "((flags & " + TaskBeginDoc.DONE + ") != " + TaskBeginDoc.DONE + ")"  + " and id="+id;
		svtask.clear();
		String where = filter == null || filter.trim().length() == 0 
				? cmt
				: "category='" + filter + "' and " + cmt; 
		
		Cursor<SVTask> c1 = new Cursor<SVTask>(new SVTaskImpl(),where);
		
		while (c1.moveNext()){
			SVTask t = (SVTask) c1.current().getData().clone();
			
			if(t != null)
				svtask.add(t);
		}
		
		c1.close();
		
		Cursor<AgentTask> c2 = new Cursor<AgentTask>(new AgentTaskImpl(),where);
		
		while (c2.moveNext()){
			SVTask t = (SVTask) c2.current().getData().clone();
			
			if(t != null)
				svtask.add(t);
		}
		
		c2.close();
		
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return svtask.size();
	}

	@Override
	public Object getItem(int position) {
		return svtask.get(position);
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int pos, View view, ViewGroup parent) {
		if(view == null)
			view = View.inflate(context, R.layout.taskscedit_row, null);
			
		SVTask t = (SVTask) getItem(pos);
		TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
		tvDate.setText(Util.simpleDateFormat.format(t.appointDate));
		TextView tvText = (TextView) view.findViewById(R.id.tvText);
		tvText.setText(t.text);
		
		if(t instanceof AgentTask)
			tvText.setTextColor(Color.BLACK);
		else
			tvText.setTextColor(Color.RED);
		
		ImageView ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
		
		if ((t.flags & TaskBeginDoc.DONE) == TaskBeginDoc.DONE)
			ivStatus.setImageResource(R.drawable.check_checked);
		else
			ivStatus.setImageResource(R.drawable.check_unchecked);
		
		ivStatus.setTag(pos);
		
		if(finish)
			ivStatus.setOnClickListener(this);
		
		return view;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		int pos = (Integer)v.getTag();
		SVTask data = (SVTask) getItem(pos);
		DocType dt = DocType.getCurDoc();
		CreatableDocument<?> doc = (CreatableDocument<?>) dt.create();
		doc.getData().created = new Date(((SVTaskEdit)context).date);
		Context context = v.getContext();
		SharedPreferences pref = context.getSharedPreferences(ScriptImplEx.SCRIPT_PREF,
				Context.MODE_PRIVATE);
		long scdt = pref.getLong(ScriptImplEx.SCRIPT_DATE, -1);
		String id = pref.getString(ScriptImplEx.SCRIPT_ID, "");
		if(doc != null && doc.read()){
			data.flags ^= TaskBeginDoc.DONE;
			data.id = id;
			data.execDate = new Date(scdt);
			data.params = 0;
			
			Object dbo = null;
			
			if(data instanceof AgentTask){
				dbo = new AgentTaskImpl();
				((CreatableDocumentW<AgentTask>)dbo).setData((AgentTask) data);
			}else{
				dbo = new SVTaskImpl();
				((CreatableDocumentW<SVTask>)dbo).setData(data);
			}
			
			((CreatableDocumentW<?>)dbo).write();
			notifyDataSetChanged();
		}
		doc.close();
	}
}


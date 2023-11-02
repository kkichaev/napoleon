package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.AnswerImpl;
import com.grsoft.dataobjects.impl.QuestionImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class QuestAnswer extends BaseActivity {
	public static Class<? extends Activity> QuestAnswerActivity =  QuestAnswer.class;
	private QuestionImpl questionImpl = new QuestionImpl();
		 
	public static void open(Context context, long rowid, String orgid){
		Intent intent = new Intent(context, QuestAnswerActivity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		intent.putExtra(ExtrasConst.ORG_ID_STR, orgid);
		context.startActivity(intent);
	}
	
	private ListView list;
	protected ImageButton btnSend;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quest_answer);
		
		Intent intent = getIntent();
		final long rowid = intent.getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		final String orgid = intent.getStringExtra(ExtrasConst.ORG_ID_STR);
		
		list = (ListView) findViewById(R.id.list);
		
		if (rowid != ExtrasConst.INVALID_ID){
			questionImpl.read(rowid);
			questionImpl.close();
			list.setAdapter(new QuestAnswerAdapter(this, 
					questionImpl.getData().idquest, orgid));
			
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					QuestionWebView.open(arg1.getContext(), 
							rowid, orgid, (Long)arg0.getAdapter().getItem(arg2));
					
				}
			});
		}
		
		registerForContextMenu(list);
		
		findViewById(R.id.btnNewDoc).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QuestionWebView.open(v.getContext(), rowid, orgid);
			}
		});
		
		btnSend = (ImageButton) findViewById(R.id.btnSend); 
		btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String where = "question='" + questionImpl.getData().idquest + "' and id='" + orgid + "'";
				com.grsoft.napoleon.documents.DocList dl = new DocList(AnswerImpl.class, where, "");
				
				if( dl.getCount() > 0 ) {
					DocSendListner docSend = new DocSendListner(QuestionDoc.instance().getObjectName(), dl);
					DocumentSender ds = new DocumentSender(QuestAnswer.this, findViewById(R.id.btnSend), docSend, null);
					ds.execute((Void[])null);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		QuestAnswerAdapter adapter = (QuestAnswerAdapter)list.getAdapter();
		
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (list != null){
			QuestAnswerAdapter adapter = (QuestAnswerAdapter)list.getAdapter();
			
			if(adapter != null)
				adapter.close();
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.question_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int pos = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
		long rowid = (Long)list.getAdapter().getItem(pos);
		
		if (item.getItemId() == R.id.itDelete){
			DbWriter writer = new DbWriter();
			writer.deleteRecord(new Answer(), rowid);
			((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
		}
		
		return true;
	}
}

class QuestAnswerAdapter extends BaseAdapter{
	private ArrayList<Long> ids = new ArrayList<Long>();
	private Context context;
	private AnswerImpl answer = (AnswerImpl) QuestionDoc.instance().create();
	private String questid = "";
	private String orgid = "";
	
	public QuestAnswerAdapter(Context context, String questid, String orgid){
		this.context = context;
		this.questid = questid;
		this.orgid = orgid;
		readIds(questid, orgid);
	}

	protected void readIds(String questid, String orgid) {
		ids.clear();
		SQLiteDatabase db = DataBaseManager.getDataBase();
		Cursor c = db.query(DataObjectInfo.getInstance().getTableName(Answer.class), 
				new String[]{"rowid"}, "question=? and id=?", 
				new String[]{questid, orgid}, null, null, "created DESC");
		
		while(c.moveToNext()){
			ids.add(c.getLong(0));
		}
		
		c.close();
	}
	
	public void close() {
		answer.close();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(
					context, 
					R.layout.quest_answer_row, null);
		
		if (answer.read((Long) getItem(position))){
			((TextView)convertView.findViewById(android.R.id.text1))
				.setText(Util.simpleDateFormat.format(answer.getData().created));
			
			if (!answer.isEditable())
				convertView.setBackgroundResource(R.drawable.list_grey_selector);
			else {
				convertView.setBackgroundResource(position % 2 != 0 ?
						R.drawable.even_row_selector :
							R.drawable.list_selector);
			}
		}
		
		return convertView;
	}
	
	@Override
	public long getItemId(int position) {
		return -1;
	}
	
	@Override
	public Object getItem(int position) {
		return ids.get(position);
	}
	
	@Override
	public int getCount() {
		return ids.size();
	}
	
	@Override
	public void notifyDataSetChanged() {
		readIds(questid, orgid);
		super.notifyDataSetChanged();
	}
}
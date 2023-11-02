package com.grsoft.manager;

import com.grsoft.dataobjects.MAnswer;
import com.grsoft.dataobjects.impl.MAnswerImpl;
import com.grsoft.dataobjects.impl.MQuestImpl;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class AuditQuests extends Activity implements OnItemClickListener {
	public static final String ID_QUEST = "id_quest";
	private MQuestImpl quest = new MQuestImpl();
	private ListView list;
	
	public static void open(Context context, String idquest) {
		Intent intent = new Intent(context, AuditQuests.class);
		intent.putExtra(ID_QUEST, idquest);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.auditquest);
		list = (ListView) findViewById(R.id.list);
		
		View v = getLayoutInflater().inflate(getActionBarLayoutID(), null);
        ActionBar a = getActionBar();
        a.setCustomView(v);
        a.setDisplayShowCustomEnabled(true);
        a.setDisplayShowTitleEnabled(false);
        
        quest.read("idquest", getIntent().getStringExtra(ID_QUEST));
        
        TextView tv = (TextView) v.findViewById(R.id.tvTitle);
        tv.setText(quest.getData().name);
        
        list.setAdapter(new AuditQuestsAdapter(this, quest.getData().idquest));
        list.setOnItemClickListener(this);
        registerForContextMenu(list);
	}

	private int getActionBarLayoutID() {
		return R.layout.action_bar;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.auditquests_context_menu, menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.auditquests_menu, menu);
		return true;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itDelete) {
			AdapterView.AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item.getMenuInfo();		
			MAnswer a = (MAnswer) list.getItemAtPosition(mi.position);
			
			MAnswerImpl impl = new MAnswerImpl();
			impl.read(a.created.getTime());
			impl.delete();
			impl.close();

			reloadAdapter();
			
			return true;
		}else
			return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itAdd) {
			AuditEdit.open(this, quest.getData().idquest, -1);
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		reloadAdapter();
	}

	private void reloadAdapter() {
		AuditQuestsAdapter a = (AuditQuestsAdapter) list.getAdapter();
		
		if (a != null) {
			a.reload();
			a.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MAnswer a = (MAnswer) parent.getItemAtPosition(position);
		AuditEdit.open(this, quest.getData().idquest, a.created.getTime());
	}
}

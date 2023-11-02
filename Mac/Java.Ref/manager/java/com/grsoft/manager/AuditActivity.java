package com.grsoft.manager;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.PicStoreHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Question;
import com.grsoft.manager.documents.MAnswerDoc;
import com.grsoft.network.ObjectListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AuditActivity extends DrawerActivity implements OnItemClickListener {
	private ListView list;
	
	public static void open(Context context) {
		Intent intent = new Intent(context, AuditActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(new AuditAdapter(this));
		list.setOnItemClickListener(this);
	}
	
	@Override
	protected int getLayoutID() {
		return R.layout.audit_view;
	}

	@Override
	protected void postSyncUpdate() {
		AuditAdapter a = (AuditAdapter) list.getAdapter();
		a.reload();
		a.notifyDataSetChanged();
	}

	@Override
	protected String getActionBarTitle() {
		return getString(R.string.audit);
	}

	@Override
	protected int getOptionsMenuID() {
		return R.menu.audit_menu;
	}
	
	@Override
	protected void initHitchings(List<Hitching> list) {
		super.initHitchings(list);
		list.add(new RcvNewHitching(Question.class, "AuditQuest"));
	}

	@Override
	protected void setSending(List<ObjectListener> toSend) {
		toSend.add(MAnswerDoc.instance().getDirtyDocuments());
		toSend.add(new PicStoreHitching());
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Question q = (Question) parent.getItemAtPosition(position);
		AuditQuests.open(this, q.idquest);
	}
}

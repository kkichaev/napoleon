package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.AuditEquipItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Rfrgr;
import com.grsoft.dataobjects.impl.InvEquImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.AuditEquipDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.ExtrasConst;

import java.util.HashMap;
import java.util.Map;

public class AuditEquipEdit extends Activity implements SendResultListener {
	private InvEquImpl doc = new InvEquImpl();

	Map<String, Rfrgr> items = new HashMap<>();
	Adapter adapter;

	private final static String FRGID = "idfrg";

	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, AuditEquipEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invequedit);

		ListView list = (ListView) findViewById(R.id.list);

		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));

		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		org.close();
		OrgEx o = (OrgEx) org.getData();
		for(Rfrgr r : o.rfrgr) {
			items.put(r.id, r);
		}


		adapter = new Adapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(doc.isEditable()) {
					AuditEquipItem f = (AuditEquipItem) parent.getItemAtPosition(position);
					f.exists = (f.exists > 0) ? 0 : 1;
					doc.write();

					adapter.notifyDataSetChanged();
				}
			}
		});

		TextView tv = (TextView) findViewById(R.id.tvOrg);
		tv.setText(o.name);

		findViewById(R.id.btnSend).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!doc.isEmpty()) {
					new DocumentSender(AuditEquipEdit.this, findViewById(R.id.btnSend), AuditEquipDoc.instance().getObjectName(),
							doc, doc.getRowid(), AuditEquipEdit.this).execute((Void[]) null);
				}
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}

	@Override
	public void onBackPressed() {
		if(doc.isEditable() && doc.isEmpty()) {
			doc.delete();
		}
		super.onBackPressed();
	}

	@Override
	public void postSendExecute(boolean result) {
		doc.read(doc.getRowid(), false);
	}

	private class Adapter extends BaseAdapter {
		public Adapter() {}
		
		@Override
		public int getCount() {
			return doc.getData().items.size();
		}

		@Override
		public Object getItem(int position) {
			return doc.getData().items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(AuditEquipEdit.this, android.R.layout.simple_list_item_checked, null);

			AuditEquipItem i = (AuditEquipItem) getItem(position);

			CheckedTextView tv = (CheckedTextView) view;
			Rfrgr r = items.get(i.id);
			String text = r.getText();
			tv.setText(Html.fromHtml(text));
			tv.setChecked(i.exists > 0);

			return view;
		}
	}
}

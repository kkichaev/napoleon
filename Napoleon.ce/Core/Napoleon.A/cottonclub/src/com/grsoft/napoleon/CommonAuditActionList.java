package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.grsoft.dataobjects.CommonAuditItem;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.impl.CommonAuditImpl;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class CommonAuditActionList extends ActionListBase {
	private Spinner spMerch;
	private CommonAuditItem item;

	public static void open(Context context, long rowid, String id, List<String> actions) {
		ActionListBase.open(context, CommonAuditActionList.class, rowid, id, actions);
	}

	@Override
	protected CreatableDocument<? extends CreateDocDataObject> createDocument() {
		return new CommonAuditImpl();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		document.read();
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		cfg.getValue(sb, "Мерчендайзинг");

		for (CommonAuditItem i : ((CommonAuditImpl) document).getData().items)
			if (i.id.equals(itemid)) {
				item = i;
				break;
			}

		if (item != null)
			DialogHelper.loadSpinnerWithKeyW(cfg, "Мерчендайзинг",
					new ArrayList<KeyValue>(), spMerch, item.merch, true);
		
		
		BaseAdapter adapter = (BaseAdapter) list.getAdapter();
		
		if(adapter != null)
			adapter.notifyDataSetChanged();
		
		spMerch.setEnabled(document.isEditable());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		spMerch = (Spinner) findViewById(R.id.spMerch);
		spMerch.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				KeyValue kv = (KeyValue) parent.getItemAtPosition(position);
				item.merch = kv.key.toString();
				document.write();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}

	public int getLayoutId() {
		return R.layout.actionlist;
	}
}



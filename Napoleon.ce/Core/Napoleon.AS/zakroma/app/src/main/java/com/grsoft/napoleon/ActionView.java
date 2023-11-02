package com.grsoft.napoleon;

import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ActionView extends Activity implements OnItemClickListener {
	private OrderImpl document = (OrderImpl) OrderDoc.instance().create();
	private ListView list;
	
	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, ActionView.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		document.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		DocType.setCurDoc(BonusDoc.instance());
		
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing()) {
			document.close();
			DocType.setCurDoc(OrderDoc.instance());
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action);
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(new ActionAdapter(this));
		list.setOnItemClickListener(this);
		
	}

	public void bindViewRow(View view, Object item) {
		Action a = (Action) item;
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		int tc = R.color.black;
		
		if(a.bonus == 0 && ((OrderImplEx)document).getDiscountAction(a.id) != null ||
				a.bonus != 0 && BonusImpl.find(document.getData(), a.id) != ExtrasConst.INVALID_ROWID)
			tc = R.color.green;
		
		tv.setTextColor(getResources().getColor(tc));
		tv.setText(a.name);
	}

	public int getRowLayout() {
		return R.layout.actionrow;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (document.isEditable()) {
			Action a = (Action) parent.getItemAtPosition(position);
			
			if (a.bonus == 0) {
				((OrderImplEx)document).checkDiscountAction(a.id);
				((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
			}else {
				BonusImpl b = BonusImpl.findOrCreate(this, document.getData(), a.id);
				Warehouse.open(this, b, true);
			}
		}
	}

}

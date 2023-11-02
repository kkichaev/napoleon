package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.DatePeriod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class DlvDocList extends DocList {
	public static Class<? extends Activity> activity = DlvDocList.class;
	private DocType prevDocType;
	
	static void open(Context context) {
		Intent i = new Intent(context, activity);
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		prevDocType = DocType.getCurDoc();
		super.onCreate(savedInstanceState);

		setTitle(R.string.dlv_doc_list);
		
		btnDocFilter.setVisibility(View.GONE);
		btnDelete.setVisibility(View.GONE);
		btnSend.setVisibility(View.GONE);
		
		llFilterPanelClick();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (isFinishing())
			DocType.setCurDoc(prevDocType);
	}
	
	@Override
	protected void init(DocType docType) {
		super.init(DeliveryDoc.instance());
	}
	
	@Override
	protected OptionsMenuHelper createOptionsMenu() {
		return new OptionsMenuHelper() {
			public void onCreateOptionsMenu(Menu menu)
			{
				menu.add(Menu.NONE, MNU_FILTER_ID, Menu.NONE, R.string.filter_by_date);
			}
		};
	}
	
	@Override
	protected void applyFilter(DatePeriod dp, String id, String name) {
		dp.periodType = DatePeriod.DATE;
		super.applyFilter(dp, id, name);
	}
	
	protected int getFilterLayout() {
		return R.layout.dlv_date_selection;
	}

	@Override protected boolean countSumFromDocuments(boolean useFilter) { return true; }
}

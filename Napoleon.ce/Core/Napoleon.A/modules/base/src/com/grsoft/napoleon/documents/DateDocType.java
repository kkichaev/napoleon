package com.grsoft.napoleon.documents;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Util;

/***
 * Тип документа, суммарное значение - дата.
 * @author kki
 *
 */
public abstract class DateDocType extends DocType {

	protected DateDocType(String name, Class<? extends Document<?>> docClass) {
		super(name, docClass);
	}
	
	protected DateDocType(String name, String objName, Class<? extends Document<?>> docClass) {
		super(name, objName, docClass);
	}

	@Override
	public void refreshDocSum() throws RuntimeException {
		Map<String, Date> datas = new HashMap<String, Date>();
		DocList list = docList(null, null);
		
		for( int i=0; i<list.getCount(); i++ ) {
			Document<?> d = list.get(i);
			String id = d.getId();
			Date date = d.getDate();
			
			if( datas.containsKey(id) && date.compareTo(datas.get(id)) == -1)
				date = (Date) datas.get(id).clone();
			
			datas.put(id, date);
		}
		list.close();
		
		OrgSum os = new OrgSum();
		DataBaseManager.getDataBase().execSQL(String.format("DELETE FROM '%s' WHERE type='%s'", 
				DataObjectInfo.getInstance().getTableName(os.getClass()), name));
		
		DbWriter w = new DbWriter();
		os.type = this.name;
		for( Entry<String, Date> v : datas.entrySet() ) {
			os.id = v.getKey();
			os.date = v.getValue();
			w.insertRecord(os);
		}
		w.close();
	}
	
	@Override
	public void refreshDocSum(String orgId){
		Date date = new Date(0);
		DocList list = docList(orgId, null);
		for( int i=0; i<list.getCount(); i++ )
		{
			Document<?> d = list.get(i);
			Date docDate = d.getDate();
			
			if (date == null || date.compareTo(docDate) == -1)
				date = (Date) docDate.clone();
		}
		
		list.close();
		
		OrgSum os = new OrgSum();
		os.id = orgId;
		os.sum = 0;
		os.date = date;
		os.type = this.name;
		
		DbWriter w = new DbWriter();
		w.insertRecord(os);
		w.close();		;
	}
	
	@Override
	protected String getValueFromOrgSum(OrgSumImpl orgSumImpl) {
		Date date = null;
		
		if (orgSumImpl != null && 
				orgSumImpl.read() &&
				!orgSumImpl.getData().date.equals(new Date(0)))
			date = orgSumImpl.getData().date;
		
		if (orgSumImpl != null)
			orgSumImpl.close();
		
		return date != null ? Util.simpleDateFormat.format(date) : "";
	}
	
	@Override
	public void updateTotalSum(Activity activity, int sum, int weight, int count) {
		TextView tvTotalSum = (TextView) activity.findViewById(R.id.tvTotalSum);
		
		if (tvTotalSum != null)
			tvTotalSum.setVisibility(View.GONE);
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		TextView tvMainDocValColTitle = (TextView) documentsView
		.findViewById(R.id.tvMainDocValColTitle);
		
		if (tvMainDocValColTitle != null)
			tvMainDocValColTitle.setText(R.string.date);
		
		TextView tvSumColumnTitle = (TextView) documentsView
			.findViewById(R.id.SumColumnTitle);
		
		if (tvSumColumnTitle != null)
			tvSumColumnTitle.setVisibility(View.GONE);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);
		
		TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
		tvSum.setVisibility(View.GONE);
	}

}

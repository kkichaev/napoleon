package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.ReturnRequestImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.util.DisabledFirms;
import com.grsoft.util.DatePeriod;

public class DocListEx extends DocList implements DisabledFirms.Handler {
	
	ProgressDialog pd = null;
	List<String> chkFirms = new ArrayList<String>();
	List<FirmEx> firms = new ArrayList<FirmEx>();
	
	FirmsAdapter firmsAdater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DataTraveler.travel(FirmEx.class, new DataTraveler.Travel<FirmEx>(true) {

			@Override
			public boolean travel(DataTraveler<FirmEx> item) {
				firms.add(item.data);
				return true;
			}
		}, "", "name");
	}
			
	@Override
	protected String getDocText(Org o, Document<?> doc) {
		String text = "<b>" + o.name + "</b>";
		if( doc instanceof OrderImplEx ) {
			String name = ((OrderImplEx)doc).getFirmName();
			if( name != null ) {
				text += "&nbsp;&nbsp;&nbsp;<i>" + name + "</i>";
			}
		}
		if( o.address.length() > 0 )
			text += "<br>" + o.address;
		
		return text;
	}
	
	@Override protected int getFilterLayout() { return R.layout.date_selection_ex; }
	
	class FirmsAdapter extends BaseAdapter {

		@Override public int getCount() { return firms.size(); }
		@Override public Object getItem(int arg0) { return firms.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View v, ViewGroup arg2) {
			if(v == null)
				v = View.inflate(DocListEx.this, R.layout.firm_check_row, null);

			Firm f = (Firm)getItem(pos);
			CheckBox cb = (CheckBox)v.findViewById(R.id.cbFirm);
			cb.setText(f.name);
			cb.setTag(f);
			cb.setChecked(chkFirms.contains(f.id));
			
			cb.setOnCheckedChangeListener(cbHandler);
			return v;
		}
	}
	
	CompoundButton.OnCheckedChangeListener cbHandler = new CompoundButton.OnCheckedChangeListener(){
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			Firm f = (Firm)arg0.getTag();
			if(!arg1 && chkFirms.contains(f.id))
				chkFirms.remove(f.id);
			else if(arg1 && !chkFirms.contains(f.id))
				chkFirms.add(f.id);
		}
	}; 
	
	@Override
	protected void setFilterText(int d1, int m1, int y1, int d2, int m2, int y2, String org) {
		TextView tvFilter = (TextView) llFilterPanel.findViewById(R.id.tvFilter);
		String data = getString(R.string.date_filter, d1,m1+1,y1,d2,m2+1,y2);
		if( org != null )  
			data += "<br>по " + org;
		if( chkFirms.size() > 0 ) {
			data += "<br>фабрики: ";
			String ftxt = "";
			for(String fid : chkFirms) {
				for(FirmEx f : firms) {
					if(f.id.equals(fid)) {
						if(ftxt.length() > 0)
							ftxt += ", ";
						ftxt += f.shortName;
						if(ftxt.length() > 80) {
							ftxt += ", ...";
							break;
						}
					}
				}
			}
			data += ftxt;
		}
		tvFilter.setText(Html.fromHtml(data));
	}
	
	@Override
	protected Dialog createDlgFilter() {
		Dialog ret = super.createDlgFilter();

		ListView lv = (ListView)dialogView.findViewById(R.id.lvFirms);
		firmsAdater = new FirmsAdapter();
		lv.setAdapter(firmsAdater);
		return ret;
	}
	
	@Override
	public void filter() {
		if(firmsAdater != null)
			firmsAdater.notifyDataSetChanged();
		super.filter();
	}
	
	@Override
	protected void send() {
		if( DocType.getCurDoc() != OrderDoc.instance() ) { 
			super.send();
			return;
		}
		pd = ProgressDialog.show(this, "Подождите, пожалуйста", "Проверка запрета отправки");
		DisabledFirms.loadDisabledFirms(this, this);
	}
	
	@Override
	protected void drawData(View view, Document<?> doc, int position) {
		super.drawData(view, doc, position);

		DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        
		int width =  getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ?
        				displaymetrics.heightPixels : displaymetrics.widthPixels;
		TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
		width /= 5;
		if( width < 70 )
			width = 70;
		if( width > 130 )
			width = 130;
		tvSum.setWidth(width);
	}

	void closeWaitDialog() {
		if( pd != null ) {
			pd.dismiss();
			pd = null;
		}
	}

	@Override
	public void firmsLoaded(final HashSet<String> disabledFirms) {
		OrderDocEx ode = (OrderDocEx)OrderDoc.instance();
		ode.setDiabledFirms(disabledFirms);
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				closeWaitDialog();
				
				if( disabledFirms.size() > 0 )
					Toast.makeText(DocListEx.this, "Включена блокировка передачи, заявки могут не отправиться", Toast.LENGTH_SHORT).show();
				DocListEx.super.send();
			}
		});
	}

	@Override
	public void error(final String message) {
		runOnUiThread(new Runnable() {
			@Override public void run() { 
				closeWaitDialog();
				String err = "Ошибка проверки\n" + message;
				Toast.makeText(DocListEx.this, err, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	protected DocStatusChangeListener createStatusChangeListener() {
		return new DocStatusChangeEx();
	}
	
	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		return new Adapter(this, docType, saveDatePeriod);
	}
	
	class Adapter extends DocListAdapter {
		@SuppressLint("SimpleDateFormat")
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
		
		public Adapter(Context context, DocType docType, DatePeriod filter) {
			super(context, docType, filter);
			viewId = R.layout.doc_list_row_ex;
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			super.setData(view, doc, position);
			String text = "";
			if(doc instanceof OrderImplEx) {
				OrderEx oe = (OrderEx) doc.getData();
				if(oe.editPostSend.getTime() > 10 * 24 * 3600 * 1000 && oe.editPostSend.compareTo(oe.created) != 0) {
					text = "Корректировка<br/>" + sdf.format(oe.editPostSend);
				}
			}
			TextView tv = (TextView)view.findViewById(R.id.tvModified);
			tv.setText(Html.fromHtml(text));
		}
		
		@Override
		public void fetchByPeriod(DocType docType, DatePeriod dp, String orgId, Price item, HashMap<Long, Integer> values){
			this.orgId = orgId;
			documents.close();
			documents = docType.docList(orgId, order, dp);
			
			if( values != null)
				values.clear();
			
			List<Long> toRemoveIds = new ArrayList<Long>();
			for (Document<?> curDoc : documents) {
				if( exclude(curDoc) ) {
					toRemoveIds.add(curDoc.getRowid());
					continue;
				}
				if (item != null && curDoc instanceof Itemsable ) {
					int qty = ((Itemsable)curDoc).getItemQty(item);
					if( qty == 0 )
						toRemoveIds.add(curDoc.getRowid());
					else if( values != null )
						values.put(curDoc.getRowid(), qty);
				}
			}
			documents.removeDocuments(toRemoveIds);
			
			curDocType = docType;
			datePeriod = dp;
			notifyDataSetChanged();
		}

		protected boolean exclude(Document<?> doc) {
			if( chkFirms.size() > 0 &&  doc instanceof OrderImplEx ) {
				OrderEx oe = (OrderEx)doc.getData();
				return !chkFirms.contains(oe.firmCode);
			}
			return false;
		}
	}
	
	class DocStatusChangeEx extends DocStatusChangeListener {
		@Override
		protected boolean isAllowChangeStatus(CreatableDocument<?> cd) {
			if( cd instanceof ReturnRequestImpl)
				return false;
			return super.isAllowChangeStatus(cd);
		}
	}
}

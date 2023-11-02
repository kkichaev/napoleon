package com.grsoft.napoleon.documents;

import java.util.Date;
import java.util.HashMap;
import android.app.Activity;
import android.database.Cursor;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.IncassItem;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;


public class DebtDocEx extends DebtDoc {
	private Date syncDate = new Date();
	private HashMap<String, Integer> sums = new HashMap<String, Integer>(); 
	
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx();
	}

	@Override
	public DocList docList(String orgId, String order, DatePeriod selection) {
		initSyncDate();
		loadSums(orgId);
		
		return super.docList(orgId, order, selection);
	}

	protected void loadSums(String orgId) {
		DatePeriod dp = new DatePeriod(syncDate, new Date());
		dp.periodType = DatePeriod.CREATED;
		DocList docs = IncassDoc.instance().docList(orgId, null, dp);

		sums.clear();
		
		for(Document<?> d : docs){
			IncassEx iex = (IncassEx)d.getData();
			
			if (iex != null && iex.items != null && iex.items.size() > 0){
				for (IncassItem ii : iex.items){
					int val = ii.sum;
					
					Integer s = sums.get(ii.number);
					
					if(s != null)
						val += s;
					
					sums.put(ii.number, val);
				}
			}
		}
	}

	protected void initSyncDate() {
		Cursor c = null;
		
		try{
			StringBuilder sql = new StringBuilder();
			sql.append("select max(created) from syncinfo where (([syncparam] & ").append(SyncInfo.DEBT).append(" ) == ").append(SyncInfo.DEBT).append(")  and result=1");
			
			c = DataBaseManager.getDataBase().rawQuery(sql.toString(), null);
			
			if(c.moveToFirst())
				syncDate = new Date(c.getLong(0));
			else 
				syncDate = new Date();
			
			c.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(c != null){
				c.close();
			}
		}
	}
	
	private CharSequence wrapColor(boolean red, String text){
		if(red){
			StringBuilder sb = new StringBuilder();
			sb.append("<font color='red'>").append(text).append("</font>");
			return Html.fromHtml(sb.toString());
		}else
			return text;
		
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		if(doc instanceof DeliveryImpl){
			Delivery dlv = (Delivery)doc.getData();
			Integer incass = sums.get(dlv.number);
			
			boolean red = incass != null && incass > 0;
			
			TextView tv = (TextView)view.findViewById(R.id.tvDate);
			
			if (doc.getDate() == null)
				tv.setText(wrapColor(red, view.getContext().getString(R.string.doc_error)));
			else
				tv.setText(wrapColor(red, getDateDocText(doc)));
			
			tv = (TextView)view.findViewById(R.id.tvSum);
			tv.setVisibility(View.VISIBLE);
			long s = incass == null ? doc.sum() : doc.sum() - incass;
			tv.setText(wrapColor(red, Util.IntToScaleWStr(s, Consts.SUM_SCALE, 2, false)));
			
			tv = (TextView)view.findViewById(R.id.tvOther);
			tv.setText(wrapColor(red, doc.getDescription(view.getContext())));
		}else
			super.setView(adapter, view, doc);
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		
		ImageButton ib = (ImageButton) documentsView.findViewById(R.id.btnIncass);
		
		if(ib != null)
			ib.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		super.viewClosed(documentsView);
		
		ImageButton ib = (ImageButton) documentsView.findViewById(R.id.btnIncass);
		
		if(ib != null)
			ib.setVisibility(View.GONE);
	}
	
	@Override
	public void refreshDocSum(String orgId) {
		loadSums(orgId);
		
		try{
			DbWriter.checkDBTable(OrgSum.class);
			int sum = 0;
			DocList list = docList(orgId, null);
			for( int i=0; i<list.getCount(); i++ )
			{
				Document<?> d = list.get(i);
				if( d != null ) sum += d.sum();
			}
			list.close();
			
			for(int i :  sums.values())
				sum -= i;
			
			OrgSum os = new OrgSum();
			os.id = orgId;
			os.sum = sum;
			os.type = this.name;
			
			DbWriter w = new DbWriter();
			w.insertRecord(os);
			w.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

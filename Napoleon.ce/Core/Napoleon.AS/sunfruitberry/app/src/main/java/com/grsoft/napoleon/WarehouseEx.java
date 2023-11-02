package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends Warehouse {
	Map<String, Date> saled = new HashMap<String, Date>();
	Date month = new Date();
	Date month3 = new Date();
	Boolean createAM = null;
	
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnNextMatrix).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				createAM = false;
				resetMatrix();
				arg0.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	@Override
	public void afterBuildSet() {
		if(saled.size() == 0 && document != null && document.getId().length() > 0) {
			Calendar c = Calendar.getInstance();
			Date end = c.getTime();
			
			c.add(Calendar.MONTH, -1);
			month = c.getTime();

			c.add(Calendar.MONTH, -2);
			month3 = c.getTime();
			
			DatePeriod dp = new DatePeriod(month3, end);
			DocList dl = DeliveryDoc.instance().docList(document.getId(), "", dp);
			
			for(Document<?> doc : dl) {
				DeliveryImpl di = (DeliveryImpl)doc;
				for(DeliveryItem item : di.getData().items) {
					Date sld = saled.get(item.id);
					if(sld == null || sld.compareTo(di.getDate()) < 0 ) {
						saled.put(item.id, di.getDate());
					}
				}
			}
			
			dl.close();
		}
	}
	
	@Override
	public void setColor(TextView textView, Price price) {
		super.setColor(textView, price);

		Date dt = saled.get(price.id);
		if(dt != null) {
			if(dt.compareTo(month) > 0) {
				textView.setTextColor(Color.RED);
			} else {
				textView.setTextColor(Color.BLUE);
			}
		}
	}
	
	@Override
	protected FoldersAdapter createAdapterInstance() {
//		if(createAM == null)
//			createAM = !editMode;
//		if(createAM && docRowId != ExtrasConst.INVALID_ROWID && DocType.getCurDoc() == OrderDoc.instance()) {
//			findViewById(R.id.btnNextMatrix).setVisibility(View.VISIBLE);
//			return new AssortmentMatrixAdapter(this, document.getId());
//		}
		return super.createAdapterInstance();
	}

	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.resetCache();
		if( document instanceof ReturnImplEx) {
			return new ReturnAdapter(this, document.getId());
		}
		return super.createListAdapter();
	}
	
	class ReturnAdapter extends FoldersAdapter {

		HashSet<String> ids = new HashSet<String>();
		
		public ReturnAdapter(WarehouseManager warehouse, String orgId) {
			super(warehouse);
						
			DocList dl = DeliveryDoc.instance().docList(orgId);
			for(Document<?> d : dl) {
				for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
					ids.add(di.id);
			}
			dl.close();
		}
		
		@Override public boolean inset(long rowid, String id) { return ids.contains(id); }
	}
}

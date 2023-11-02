package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnCommit;
import com.grsoft.dataobjects.ReturnCommitItem;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReturnCommitDetail extends BaseActivity {
	
	ReturnImplEx doc;
	ReturnCommit rc;
	
	List<Item> src = new ArrayList<ReturnCommitDetail.Item>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.return_commit);
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc = new ReturnImplEx();
		doc.read(rid);
		doc.close();
		
		OrgImpl oi = new OrgImpl();
		Org o = oi.getData();
		o.id = doc.getId();
		oi.read();
		oi.close();
		
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(o.name);
		
		rc = ReturnCommit.get(doc.getData().created);
		
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		for(OrderItem item : doc.getData().items) {
			p.id = item.id;
			pi.read();
			src.add(new Item((ReturnItemEx) item, p));
		}
		
		long sum = 0;
		if( rc != null ) {
			sum = rc.sum();
			for(ReturnCommitItem rci : rc.items) {
				for(Item i : src) {
					if(i.isEqual(rci) == false)
						continue;
					
					i.add(rci);
					if(rci.qty <= 0)
						break;
				}
			}
		}
		pi.close();
		
		TextView tv = (TextView)findViewById(R.id.tvTotalSum);
		tv.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(new Adapter());
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		super.onSaveInstanceState(outState);
	}
	
	class Item {
		public Date bestBefore;
		public String name = "";
		public String remark = "";
		public String id = "";
		
		public int qty;
		public int cost;
		public int c_qty;
		public int c_cost;
		
		public Item(ReturnItemEx src, Price p) {
			id = p.id;
			name = p.name;
			bestBefore = src.bestBefore;
			qty = src.qty;
			cost = src.cost;
		}
		
		public boolean isEqual(ReturnCommitItem src) {
			return src.id.equals(id) && src.bestBefore.equals(bestBefore);
		}
		
		public boolean isMatch() { return qty == c_qty && cost == c_cost; }
		
		public long getSum() { 
			return (long)cost * qty / Consts.QTY_SCALE; 
		}
		
		public long getCSum() {
			return (long)c_cost * c_qty / Consts.QTY_SCALE;
		}
		
		public void add(ReturnCommitItem src) {
			int q = src.qty;
			if(qty < q)
				q = qty;
			c_qty = q;
			c_cost = src.cost;
			remark = src.remark;

			src.qty -= q;
		}
	}
	
	class Adapter extends BaseAdapter {
		@Override public int getCount() { return src.size(); }
		@Override public Object getItem(int arg0) { return src.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if(arg1 == null) {
				arg1 = View.inflate(ReturnCommitDetail.this, R.layout.returncommit_list_row, null);
			}
						
			Item i = (Item) getItem(arg0);
			int clr = i.isMatch() ? Color.BLACK : Color.RED;
			
			TextView tv;
			String text;
			
			tv = (TextView)arg1.findViewById(R.id.tvName);
			tv.setText(i.name);
			tv.setTextColor(clr);
			
			tv = (TextView)arg1.findViewById(R.id.tvBestBefore);
			tv.setText(Util.simpleDateFormat.format(i.bestBefore));
			tv.setTextColor(clr);

			tv = (TextView)arg1.findViewById(R.id.tvQty);
			text = Util.IntToScaleStr(i.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true) + "<br/>" +
					Util.IntToScaleStr(i.c_qty, Consts.QTY_SCALE, Util.DEC_DELIM, true);
			tv.setText(Html.fromHtml(text));
			tv.setTextColor(clr);

			tv = (TextView)arg1.findViewById(R.id.tvSum);
			text = Util.IntToScaleStr(i.getSum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "<br/>" +
					Util.IntToScaleStr(i.getCSum(), Consts.SUM_SCALE, Util.DEC_DELIM, false);
			tv.setText(Html.fromHtml(text));
			tv.setTextColor(clr);
			
			tv = (TextView)arg1.findViewById(R.id.tvRemark);
			tv.setText(i.remark);
			tv.setTextColor(clr);
			
			return arg1;
		}
		
	}
}

package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Util;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class OrdView extends Activity {
	private ListView list;
	private TextView tvSum;
	private TextView tvQty;
	private TextView tvWeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ordview);
		
		list = (ListView) findViewById(R.id.list);
		tvSum = (TextView) findViewById(R.id.tvSum);
		tvQty = (TextView) findViewById(R.id.tvQty);
		tvWeight = (TextView) findViewById(R.id.tvWeight);
		
		Date start = new Date(getIntent().getLongExtra(OrdRep.START, new Date().getTime()));
		Date finish = new Date(getIntent().getLongExtra(OrdRep.FINISH, new Date().getTime()));
		String id = getIntent().getStringExtra(OrdRep.ORGID);
		String ids = getIntent().getStringExtra(OrdRep.IDS);
		
		Map<String, Data> data = new HashMap<String, Data>();
		
		DatePeriod dp = new DatePeriod(start, finish);
		DocList dl = OrderDoc.instance().docList(id.trim().length() > 0 ? id : null, "created", dp);

		List<String> idlist = new ArrayList<String>();
		for (String s : ids.split(OrdRep.DELIM))
			idlist.add(s);
		
		PriceImpl pr = new PriceImpl();
		
		for(Document<?> d : dl){
			Order o = (Order) d.getData();
			
			for(OrderItem i : o.items){
				if(idlist.contains(i.id)){
					if(!data.containsKey(i.id)){
						pr.read("id", i.id);
						data.put(i.id, new Data(pr.getData().name));
					}
					Data t = data.get(i.id);
					t.qty += i.qty;
					t.weight += pr.getData().weight;
					t.sum += FPOperation.itemMul(i.cost, i.qty, Consts.QTY_SCALE);
				}
			}
		}
		
		int s = 0, w = 0, q = 0;
		for(Data d : data.values()){
			s += d.sum;
			w += d.weight;
			q += d.qty;
		}
		
		tvSum.setText(Util.IntToScaleStr(s, Consts.SUM_SCALE));
		tvWeight.setText(Util.IntToScaleStr(w, Consts.WEIGHT_SCALE));
		tvQty.setText(Util.IntToScaleStr(q, Consts.QTY_SCALE));
		
		list.setAdapter(new Adapter(data.values()));
	}
	
	private class Data{
		public long sum;
		public String name;
		public int qty;
		public int weight;
		
		public Data(String name){
			this.name = name;
		}
	}
	
	private class Adapter extends BaseAdapter{
		List<Data> data = new ArrayList<Data>();
		
		public Adapter(Collection<Data> data){
			this.data.addAll(data);
			
			Collections.sort(this.data, new Comparator<Data>(){	@Override public int compare(Data lhs, Data rhs) { return lhs.name.compareTo(rhs.name); }});
		}

		@Override
		public int getCount() { return data.size();	}

		@Override
		public Object getItem(int position) { return data.get(position);}

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(OrdView.this, R.layout.ordviewrow, null);
			
			Data d = (Data) getItem(position);
			
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(d.name);
			
			tv = (TextView) view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(d.qty, Consts.QTY_SCALE));
			
			tv = (TextView) view.findViewById(R.id.tvWeight);
			tv.setText(Util.IntToScaleStr(d.weight, Consts.WEIGHT_SCALE));
			
			tv = (TextView) view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(d.sum, Consts.SUM_SCALE));
			
			
			return view;
		}
	}
}

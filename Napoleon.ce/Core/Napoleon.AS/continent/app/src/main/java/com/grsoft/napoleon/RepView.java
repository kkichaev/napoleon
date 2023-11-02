package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.SalesDoc;
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


public class RepView extends Activity {
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
		
		Date start = new Date(getIntent().getLongExtra(RepEdit.START, new Date().getTime()));
		Date finish = new Date(getIntent().getLongExtra(RepEdit.FINISH, new Date().getTime()));
		int mode = getIntent().getIntExtra(RepEdit.MODE, R.id.rbDocs);
		String ids = getIntent().getStringExtra(RepEdit.IDS);
		
		Map<String, Data> data = new HashMap<String, Data>();
		
		DatePeriod dp = new DatePeriod(start, finish);
		DocList dl = SalesDoc.instance().docList(null, "created", dp);

		List<String> idlist = new ArrayList<String>();
		for (String s : ids.split(RepEdit.DELIM))
			idlist.add(s);
		
		PriceImpl pr = new PriceImpl();
		
		OrgImpl org = new OrgImpl();
		
		for(Document<?> d : dl){
			Sales o = (Sales) d.getData();
			
			for(OrderItem i : o.items){
				if(idlist.contains(i.id)){
					if(!data.containsKey(i.id)){
						pr.read("id", i.id);
						data.put(i.id, new Data(pr.getData().name, ((Price)pr.getData()).unit));
					}
					
					Data t = data.get(i.id);
					t.qty += i.qty;
					t.weight += pr.getData().weight;
					t.sum += FPOperation.itemMul(i.cost, i.qty, Consts.QTY_SCALE);
					
					String id = mode == R.id.rbOrgs ? o.id : "";
					
					if (!t.orgs.containsKey(id)){
						if(id.length() > 0)
							org.read("id", id);
						t.orgs.put(id, new DataOrg(org.getData().name));
					}
					
					DataItem ti = t.orgs.get(id);
					ti.qty += i.qty;
					ti.weight += pr.getData().weight;
					ti.sum += FPOperation.itemMul(i.cost, i.qty, Consts.QTY_SCALE);
					
					String n = o.number;
					
					if(!t.orgs.get(id).docs.containsKey(n))
						t.orgs.get(id).docs.put(n, new DataItem(n, ""));
					
					DataItem t2i = t.orgs.get(id).docs.get(n);
					t2i.qty += i.qty;
					t2i.weight += pr.getData().weight;
					t2i.sum += FPOperation.itemMul(i.cost, i.qty, Consts.QTY_SCALE);
				}
			}
		}
		
		org.close();
		
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
	
	private class DataItem{
		public String name;
		public int qty = 0;
		public int sum = 0;
		public int weight;
		public String unit;
		
		public DataItem(String name, String unit){
			this.name = name;
			this.unit = unit;
		}
	
		public int getBkg(){ return R.drawable.list_selector; }
	}
	
	private class DataOrg extends DataItem{
		public Map<String, DataItem> docs = new HashMap<String, DataItem>();
		
		public DataOrg(String name){
			super(name, "");
		}
		
		@Override public int getBkg() { return R.drawable.list_grey_selector; }
	}
	
	private class Data extends DataItem{
		public Map<String, DataOrg> orgs = new HashMap<String, DataOrg>();
		
		public Data(String name, String unit){
			super(name, unit);
		}
		
		@Override
		public int getBkg() { return R.drawable.even_row_selector; }
	}
	
	private Comparator<DataItem> cmp = new Comparator<DataItem>(){	@Override public int compare(DataItem lhs, DataItem rhs) { return lhs.name.compareTo(rhs.name); }};
	private class Adapter extends BaseAdapter{
		List<DataItem> data = new ArrayList<DataItem>();
		
		public Adapter(Collection<Data> data){
			List<Data> d1 = new ArrayList<Data>();
			d1.addAll(data);
			
			Collections.sort(d1,cmp);
			
			for(Data d : d1){
				this.data.add(d);
				List<DataOrg> d2 = new ArrayList<DataOrg>();
				d2.addAll(d.orgs.values());
				Collections.sort(d2,cmp);
				
				for(DataOrg i : d2){
					if(i.name.length() > 0)
						this.data.add(i);
					
					List<DataItem> d3 = new ArrayList<DataItem>();
					d3.addAll(i.docs.values());
					Collections.sort(d3,cmp);
					
					for(DataItem i2 : d3)
						this.data.add(i2);
				}
			}
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
				view = View.inflate(RepView.this, R.layout.ordviewrow, null);
			
			DataItem d = (DataItem) getItem(position);
			
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(d.name);
			
			tv = (TextView) view.findViewById(R.id.tvUnit);
			tv.setText(d.unit);
			tv.setVisibility(d.unit.length() > 0 ? View.VISIBLE : View.GONE);
			
			tv = (TextView) view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(d.qty, Consts.QTY_SCALE));
			
			tv = (TextView) view.findViewById(R.id.tvWeight);
			tv.setText(Util.IntToScaleStr(d.weight, Consts.WEIGHT_SCALE));
			
			tv = (TextView) view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(d.sum, Consts.SUM_SCALE));
			
			view.setBackgroundResource(d.getBkg());
			
			return view;
		}
	}
}

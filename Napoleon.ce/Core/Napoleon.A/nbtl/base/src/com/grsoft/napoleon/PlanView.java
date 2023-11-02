package com.grsoft.napoleon;

import com.grsoft.dataobjects.BtlPlanItem;
import com.grsoft.dataobjects.impl.BtlPlanImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class PlanView extends Activity {
	private static final String CID = "cid";
	private static final String ID = "id";
	private BtlPlanImpl plan = new BtlPlanImpl();
	private PriceImpl price = new PriceImpl();
	private ListView list;
	private TextView tvTitle;
	
	public static void open(Context context, String cid, String id){
		Intent i = new Intent(context, PlanView.class);
		i.putExtra(CID, cid);
		i.putExtra(ID, id);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planview);
	
		list = (ListView) findViewById(R.id.list);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		
		plan.getData().id = getIntent().getStringExtra(ID);
		plan.getData().cid = getIntent().getStringExtra(CID);
		
		plan.read();
		plan.close();
		
		list.setAdapter(new Adapter());
		tvTitle.setText(getString(R.string.total_plan, Util.IntToScaleStr(plan.getData().face, Consts.QTY_SCALE)));
	}
	
	class Adapter extends BaseAdapter{
		@Override public int getCount() { return plan.getData().items.size();	}
		@Override public Object getItem(int position) { return plan.getData().items.get(position); }
		@Override public long getItemId(int position) { return 0; }
		@Override public View getView(int position, View convertView, ViewGroup parent) { return getAdapterView((BtlPlanItem) getItem(position),convertView); }
		
	}

	public View getAdapterView(BtlPlanItem item, View view) {
		if (view == null)
			view = View.inflate(this, R.layout.planview_row, null);
		
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		
		price.read("id", item.id);
		tv.setText(price.getData().name);
		
		tv = (TextView) view.findViewById(R.id.tvQty);
		tv.setText(Util.IntToScaleStr(item.face, Consts.QTY_SCALE));
		
		return view;
	}

}

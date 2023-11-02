package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgRemnants;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.OrderMasterAdapter.ViewItem;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class OrderMaster extends Activity {
	public static Class<? extends Activity> activity = OrderMaster.class;

	private final static String RMN_ROW_ID = "rmn_row_id";
	private OrderImpl order = new OrderImpl();
	private RemnantsImpl remnants = new RemnantsImpl();
	private ListView list;
	private ImageButton btnMode;
	private ImageButton btnOrder;

	public static void open(Context context, long rmnid, long rowid) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(RMN_ROW_ID, rmnid);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_master);

		list = (ListView) findViewById(R.id.list);
		btnMode = (ImageButton) findViewById(R.id.btnMode);
		btnOrder = (ImageButton) findViewById(R.id.btnOrder);

		Bundle bundle = getIntent().getExtras();
		order.read(bundle.getLong(ExtrasConst.DOC_ROW_ID_STR,
				ExtrasConst.INVALID_ID));
		order.close();

		remnants.read(bundle.getLong(RMN_ROW_ID, ExtrasConst.INVALID_ID));
		remnants.close();

		list.setAdapter(new OrderMasterAdapter(this, remnants, order));
		list.setDividerHeight(0);

		btnMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((OrderMasterAdapter) list.getAdapter()).swapMode((Activity) v.getContext());
			}
		});

		btnOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				order.open(v.getContext());
				finish();
			}
		});
		
		btnOrder.setVisibility(View.GONE);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				OrderMasterAdapter adapter = (OrderMasterAdapter) parent.getAdapter();
				ViewItem item = (ViewItem) adapter.getItem(position);
				if(item != null){
					PriceImpl p = new PriceImpl();
					p.getData().id = item.getId();
					p.read();
					p.close();
					
					if(p.getRowid() != ExtrasConst.INVALID_ID)
						order.editItem(p.getRowid(), view.getContext());
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		order.read(order.getRowid(), false);
		order.close();
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
	}
}

class OrderMasterAdapter extends BaseAdapter {
	class ViewItem {
		public PriceImpl priceImpl = new PriceImpl();
		public CostStrategy costStrategy;
		public OrderImpl order;
		OrgRemnants mtx;

		public ViewItem(OrderImpl order, OrgRemnants mtx) {
			costStrategy = CostStrategy.getInstance(order.getClass());
			this.order = order;
			this.mtx = mtx;
		}

		protected void setView(View view) {
			if(mtx != null)
				priceImpl.getData().id = mtx.id;
			
			priceImpl.read();
			priceImpl.close();

			Price p = priceImpl.getData();

			int color = ((Itemsable)order).findItem(mtx.id) == null ? Color.BLACK : ((Itemsable)order).getItemColor();
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(p.name);
			tv.setTextColor(color);
			
			tv = (TextView) view.findViewById(R.id.tvCost);
			tv.setText(Util.IntToScaleStr(costStrategy.getItemCost(p, order),
					Consts.SUM_SCALE));
			tv.setTextColor(color);

			tv = (TextView) view.findViewById(R.id.tvMtxInfo);
			StringBuilder sb = new StringBuilder();
			sb.append(mtx.qty).append("/").append(
							Util.simpleDateFormat.format(mtx.date));
			tv.setText(sb.toString());
			tv.setTextColor(color);

			tv = (TextView) view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(order.getItemValue(p),
					Consts.QTY_SCALE));
			tv.setTextColor(color);
		}
		
		public String getId(){
			return mtx.id;
		}
	}

	private Context context;

	enum Mode {
		ZeroFacing, HaseFacing
	}

	private Mode mode = Mode.ZeroFacing;
	private List<ViewItem> facing = new ArrayList<ViewItem>();
	private List<ViewItem> zeroFacing = new ArrayList<ViewItem>();
	private HashMap<String, OrgRemnants> mtx = new HashMap<String, OrgRemnants>();

	public OrderMasterAdapter(Context context, RemnantsImpl remnants,
			OrderImpl order) {
		this.context = context;
		((TextView) ((Activity)context).findViewById(R.id.tvOrg)).setText(R.string.order_master_step1);
		HashSet<String> z = new HashSet<String>();

		OrgImpl orgImpl = new OrgImpl();
		orgImpl.getData().id = remnants.getId();
		orgImpl.read();
		orgImpl.close();

		for (OrgRemnants o : ((OrgEx) orgImpl.getData()).remnants)
			mtx.put(o.id, o);

		for (RemnantItem r : remnants.getData().items) {
			z.add(r.id);
			facing.add(new ViewItem(order, mtx.get(r.id)));
		}

		for (OrgRemnants o : ((OrgEx) orgImpl.getData()).remnants) {
			if (!z.contains(o.id))
				this.zeroFacing.add(new ViewItem(order, mtx.get(o.id)));
			mtx.put(o.id, o);
		}
	}

	public void swapMode(Activity view) {
		ImageButton btnMode = (ImageButton) view.findViewById(R.id.btnMode);
		ImageButton btnOrder = (ImageButton) view.findViewById(R.id.btnOrder);
		switch (mode) {
		case ZeroFacing:
			mode = Mode.HaseFacing;
			btnMode.setImageResource(R.drawable.backward);
			((TextView) ((Activity)context).findViewById(R.id.tvOrg)).setText(R.string.order_master_step2);
			btnOrder.setVisibility(View.VISIBLE);
			break;
		case HaseFacing:
			mode = Mode.ZeroFacing;
			btnMode.setImageResource(R.drawable.forward);
			((TextView) ((Activity)context).findViewById(R.id.tvOrg)).setText(R.string.order_master_step1);
			btnOrder.setVisibility(View.GONE);
			break;
		default:
		}
		
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		switch (mode) {
		case ZeroFacing:
			return zeroFacing.size();
		case HaseFacing:
			return facing.size();
		default:
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		switch (mode) {
		case ZeroFacing:
			return zeroFacing.get(position);
		case HaseFacing:
			return facing.get(position);
		default:
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.order_master_row, null);

		ViewItem viewItem = (ViewItem) getItem(position);
		viewItem.setView(view);

		view.setBackgroundResource(position % 2 != 0 ? 
				R.drawable.even_row_selector :
				R.drawable.list_selector);
		
		return view;
	}

}

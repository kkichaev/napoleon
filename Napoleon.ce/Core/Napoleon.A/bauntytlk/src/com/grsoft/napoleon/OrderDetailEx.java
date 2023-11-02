package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PayType;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail implements OnClickListener {
	private View btnNewPayType;
	private List<PayType> payType = new ArrayList<PayType>();
	int selPayType = -1;
	private Map<String, Long> new_sums = new HashMap<String, Long>(); 

	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		OrgEx oe = (OrgEx)org.getData();
		String costype = (doc == null) ? "" : oe.category;
		btnNewPayType = findViewById(R.id.btnNewPayType);
		btnNewPayType.setOnClickListener(this);
		
		DataTraveler.travel(PayType.class, new DataTraveler.Travel<PayType>(true) {

			@Override
			public boolean travel(DataTraveler<PayType> item) {
				payType.add(item.data);
				return true;
			}

			}, "category='" + costype + "'");
		
		Collections.sort(payType, new Comparator<PayType>() {

			@Override
			public int compare(PayType lhs, PayType rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnNewPayType)
			showDialog(R.id.new_pay_type);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.new_pay_type)
			return createNewPayTypeDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createNewPayTypeDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String[] arr = new String[payType.size()];
		for(int i = 0; i < payType.size(); i++)
			arr[i] = payType.get(i).name;
		
		builder.setTitle(R.string.select_pay_type);
		builder.setItems(arr, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showNewPayType(which);
			}
		});
		
		return builder.create();
	}

	protected void showNewPayType(int which) {
		selPayType = which;
		calcNewSums();
		((BaseAdapter)lvItems.getAdapter()).notifyDataSetChanged();
	}
	
	private void calcNewSums() {
		new_sums.clear();
		PayType tp = payType.get(selPayType);
		@SuppressWarnings("unchecked")
		CostStrategyEx ce =  (CostStrategyEx) CostStrategy.getInstance((Class<? extends Document<?>>) doc.getClass());
		
		for(OrderItem i : doc.getData().items) {
			
			PriceImpl price = new PriceImpl();
			price.read("id", i.id);

			int bc = ce.getBaseCost(price.getData(), doc);
			int cost = ce.getFixedOrgCost(price.getData(), (OrgEx)org.getData(), bc);
			if(cost == 0) {
				cost = ce.getCostWhithoutPayType(price.getData(), (OrgEx)org.getData(), bc);
				if(tp.discount != 0) {
					cost += (CostStrategy.costWithDiscount(bc, tp.discount, Consts.SUM_SCALE) - bc);
				}
			}
			
			long new_cost = FPOperation.itemMul(cost, i.qty, Consts.QTY_SCALE);
			
			new_sums.put(i.id, new_cost);
		}
		
		updateTotalSum();
		TextView tv = (TextView) findViewById(R.id.tvTotalSum);
		String s = DocType.getCurDoc().getTotalSumStr(this, doc.sum(), doc.weight(), doc.count());
		StringBuilder sb = new StringBuilder(s);
		sb.append("<b>\\").append(Util.IntToScaleStr(sum(new_sums), Consts.SUM_SCALE, Util.DEC_DELIM, false)).append("<b>");
		tv.setText(Html.fromHtml(sb.toString()));
	}

	private long sum(Map<String, Long> sums) {
		long res = 0;
		
		for(Long s : sums.values())
			res += s;
		
		return res;
	}

	@Override
	protected void onPause() {
		super.onPause();
		selPayType = -1;
	}
	
	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapter() {
			@Override
			protected void drawInternal(View view, String name, int color, OrderItem item) {
				super.drawInternal(view, name, color, item);
				TextView tv = (TextView) view.findViewById(R.id.tvSum);
				
				if (tv != null && selPayType != -1) {
					String str = tv.getText().toString();
					str += "<br>";
					
					if(new_sums.containsKey(item.id))
						str += Util.IntToScaleStr(new_sums.get(item.id), Consts.SUM_SCALE, Util.DEC_DELIM, false);
					
					tv.setText(Html.fromHtml(str));
				}
			}
		});
	}
}

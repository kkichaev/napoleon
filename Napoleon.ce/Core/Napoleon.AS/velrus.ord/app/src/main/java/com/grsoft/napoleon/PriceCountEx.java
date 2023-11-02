package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.BonusItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderAction;
import com.grsoft.dataobjects.OrderActionBonus;
import com.grsoft.dataobjects.OrderActionItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	protected OnClickListener baseClickListener;
	private Spinner spPrices;
	private int client_cost;
	private OrderAction action;
	private OrderActionItem actionItem;

	private TextView tvActionDescr;
	private CheckBox cbAction;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		spPrices = findViewById(R.id.spPrices);
		tvActionDescr = findViewById(R.id.tvActionDescr);
		cbAction = findViewById(R.id.cbApplyAction);

		if (document instanceof OrderImpl) {
			String where = String.format("\"start\"<=%1$d and \"finish\">=%1$d", new Date().getTime());

			DataTraveler.travel(OrderAction.class, new DataTraveler.Travel<OrderAction>(true) {
				@Override
				public boolean travel(DataTraveler<OrderAction> item) {
					for(OrderActionItem i : item.data.items)
						if (price.getData().id.equals(i.id)) {
							action = item.data;
							actionItem = i;
							return false;
						}
					return true;
				}
			}, where);

			if (action != null){
				tvActionDescr.setText(action.descr);

				if (((OrderImplEx)document).hasBonus(price.getData().id))
					cbAction.setChecked(true);
			}else
				findViewById(R.id.layoutAction).setVisibility(View.INVISIBLE);

			client_cost = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass()).getItemCost(price.getData(), (Document<?>) document);;
			OrderImpl orderImpl = ((OrderImpl) document);
			orderImpl.setUpdateQtyHandler(new UpdateQtyHandler() {

				@Override
				public void itemUpdated(OrderItem item, Order order,
						boolean isNewItem) {
					((OrderItemEx)item).idx = spPrices.getSelectedItemPosition();
				}
			});
			
			spPrices.setVisibility(View.VISIBLE);
			
			List<String> p = new ArrayList<String>();
			PriceEx pe = (PriceEx) price.getData();
			p.add(getString(R.string.price_client) + " (" + Util.IntToScaleStr(client_cost, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " руб.)");
			p.add(getString(R.string.price_akc1) + " (" + Util.IntToScaleStr(pe.akc1, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " руб.)");
			p.add(getString(R.string.price_akc2) + " (" + Util.IntToScaleStr(pe.akc2, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " руб.)");
			String[] items = new String[p.size()];
			items = p.toArray(items);
			
			OrderItemEx item = (OrderItemEx) orderImpl.findItem(price.getData().id);
			ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, items);
			spPrices.setAdapter(aa);
			
			if(item != null){
				switch(item.idx){
				case 1:
					priceVal = pe.akc1;
					break;
				case 2:
					priceVal = pe.akc2;
					break;
				default:
					priceVal = client_cost;
				}
				
				spPrices.setSelection(item.idx, true);
				updateCost();
				updateSumTextView();
			}
			
			spPrices.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					int cost =  client_cost;
					PriceEx pe = (PriceEx) price.getData();
					
					if(position == 1)
						cost = pe.akc1;
					else if(position == 2)
						cost = pe.akc2;
					
					onChangeCost(cost);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {}}
			);
		}
	}
	
	protected boolean isInputValid(Runnable r) { return priceVal > 0; }
	
	@Override
	protected void invalidInputValueHandler() { Toast.makeText(this, R.string.empty_price_error, Toast.LENGTH_SHORT).show(); }
	
	@Override
	protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void postOKProcess() {
		if (document instanceof OrderImpl){
			OrderEx order = (OrderEx) ((OrderImpl)document).getData();
			OrderItem item = (OrderItem) ((OrderImpl)document).findItem(price.getData().id);

			List<BonusItem> bonus = new ArrayList<>();

			if (order.bonus != null)
				for(BonusItem b : order.bonus)
					if (!b.id.equals(price.getData().id))
						bonus.add((b));


			if (item != null && cbAction.isChecked() && actionItem != null) {
				int coef = item.qty / actionItem.qty;
				if(coef > 0 ) {
					for(OrderActionBonus b : actionItem.bonus) {
						BonusItem bonusItem = new BonusItem();
						bonusItem.id = price.getData().id;
						bonusItem.bonusID = b.id;
						bonusItem.qty = b.qty * coef;
						bonus.add(bonusItem);
					}
				}
			}

			order.bonus = bonus;
			document.write();
			document.close();
		}

		super.postOKProcess();
	}
}

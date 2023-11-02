package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	protected OnClickListener baseClickListener;
	private Spinner spPrices;
	private int client_cost;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		spPrices = (Spinner) findViewById(R.id.spPrices);

		if (document instanceof OrderImpl) {
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
}

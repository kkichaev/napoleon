package com.grsoft.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AliantaOffer;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OfferItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.WhData;
import com.grsoft.napoleon.AliantaOfferEdit;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.DiscountInputDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AliantaOfferImpl extends CreatableDocument<AliantaOffer> implements Itemsable {

	@Override
	public void open(Context context) {
		AliantaOfferEdit.open(context, this);
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		
		open(context);
		Warehouse.open(context, this, true);
		return false;
	}

	@Override
	public CreatableDocument<AliantaOffer> copy() {
		AliantaOfferImpl dest = null;
		if(rowid != ExtrasConst.INVALID_ROWID) {
			dest = new AliantaOfferImpl();
			dest.read(rowid);

			dest.data.created = Util.getDateTime();
			dest.data.date = Util.getDate();
			dest.data.params = 0;
			dest.data.offerDoc = "";
			dest.rowid =  ExtrasConst.INVALID_ID;
			dest.write();
			dest.close();
		}
		return dest;
	}

	@Override public int getSumType() { return data.costType; }
	
	@Override
	public void postInit() {
		OrgImpl oi = new OrgImpl();
		oi.read("id", data.id);
		data.costType = oi.getData().costype;
		
		super.postInit();
	}
	
	public boolean empty() {
		return (data.items.size() == 0);
	}
	
	public void addItem(Price item) {
		OfferItem oi = new OfferItem();
		oi.id = item.id;
		oi.priceCost = (int)CostStrategy.defaultInstance.getItemCost(item, this);
		oi.cost = (int)CostStrategy.costWithDiscount(oi.priceCost, data.discount, Consts.SUM_SCALE);
		data.items.add(oi);
	}

	
	public void applyDiscount(int newDiscount) {
		if(isEditable()) {
			for(OfferItem oi : data.items) {
				oi.cost = (int)CostStrategy.costWithDiscount(oi.priceCost, newDiscount, Consts.SUM_SCALE);
				oi.discount = newDiscount;
			}
			data.discount = newDiscount;
			write();
		}
	}

	@Override
	public void editItem(long itemRowid, final Context context) {
		final PriceImpl pi = new PriceImpl();
		pi.read(itemRowid);
		pi.close();

		final List<WhData> years = DbReader.fetch(WhData.class, "id='" + pi.getData().id + "' and qty > 0 and year > 0");
		
		int prcCost = (int)CostStrategy.defaultInstance.getItemCost(pi.getData(), this);
		final OfferItem oi = (OfferItem) findItem(pi.getData().id);
		final int cost = oi == null ? prcCost : oi.priceCost;

		DHelper helper = new DHelper(years, oi);

		DiscountInputDlg.open(context, new InputNumber(cost) {
			
			@Override
			public long getValue() { return oi == null ? data.discount : oi.discount; }
			
			@Override
			public void applayInput(int value, Object... params) {
				value = -value;
				int newCost = (int)CostStrategy.costWithDiscount(cost, value, Consts.SUM_SCALE);
				if(oi == null) {
					OfferItem ti = new OfferItem();
					ti.id = pi.getData().id;
					ti.cost = newCost;
					ti.discount = value;
					ti.priceCost = cost;
					ti.year = helper.getYear();
					data.items.add(ti);
				} else {
					oi.discount = value;
					oi.cost = newCost;
					oi.year = helper.getYear();
				}
				write();
				if(context instanceof DataSetNotify)
					((DataSetNotify) context).notifyDataSetChanged();
			}
		}, Consts.SUM_SCALE, false, "¬ведите скидку", DiscountInputDlg.Type.OnlyDiscount, helper);
		
	}

	static class DHelper extends DiscountInputDlg.Helper {
		int sel = -1;
		List<Integer> vals = new ArrayList<>();
		Spinner sp;

		public DHelper(List<WhData> years, OfferItem oi) {
			super();

			boolean find = false;
			for(WhData wd : years) {
				if(oi != null && wd.year == oi.year) {
					find = true;
				}
				vals.add(wd.year);
			}

			if(oi != null && !find)
				vals.add(oi.year);

			Collections.sort(vals);
			if(oi != null) {
				for (Integer i : vals) {
					if(i == oi.year) {
						sel = vals.indexOf(i);
						break;
					}
				}
			}
		}

		@Override
		public int getLayoutId() {
			return R.layout.offer_item_qty;
		}

		@Override
		public void adjustView(View view) {
			super.adjustView(view);

			sp = view.findViewById(R.id.spYear);
			ArrayAdapter<Integer> aa = new ArrayAdapter<Integer>(view.getContext(), R.layout.year_spinner, vals);
			aa.setDropDownViewResource(R.layout.year_spinner);
			sp.setAdapter(aa);

			if( sel >= 0 )
				sp.setSelection(sel);
		}

		public int getYear() {
			Integer v = (Integer) sp.getSelectedItem();
			return v == null ? 0 : v;
		}
	}
	
	@Override
	public boolean isEditable() {
		return data.emailSended != 0 ? false : super.isEditable();
	}

	@Override
	public DataObject findItem(String itemId) {
		for(OfferItem oi : data.items)
			if(oi.id.equals(itemId))
				return oi;
		return null;
	}
	
	public void remove(OfferItem i) {
		if(isEditable()) {
			data.items.remove(i);
			write();
		}
	}

	@Override public int getItemColor() { return R.color.item_highlight; }
	@Override public int getItemValue(Price item) { return item.qty; }
	@Override public int getItemQty(Price item) { return 0; }

	@Override
	public long getItemSum(Price item) {
		OfferItem oi = (OfferItem) findItem(item.id);
		return oi == null ? 0 : oi.cost;
	}


	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, long cost, boolean inPack) {
		return false;
	}

	public void markSendEmail() {
		data.emailSended = 1;
		write();		
	}
}

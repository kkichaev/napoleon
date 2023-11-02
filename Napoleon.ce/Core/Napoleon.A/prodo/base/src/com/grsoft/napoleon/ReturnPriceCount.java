package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class ReturnPriceCount extends PriceCount implements com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler {

	@Override protected int getContentViewId() { return R.layout.return_pricecount; }
	@Override protected boolean isComplexSalesHistory() { return false; }
	@Override protected boolean canChangeCost() { return (dlvList==null || dlvList.size() == 0); }
	
	@Override protected int getStartValue() { return Consts.QTY_SCALE; }
	
	List<DlvItemData> dlvList;
	
	
	public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, ReturnPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();

		Price p = price.getData();
		ReturnImplEx ret = (ReturnImplEx)document;
		ReturnItemEx ri = (ReturnItemEx)ret.findItem(p.id);
		
		ret.setUpdateQtyHandler(this);
		
		dlvList = getDeliveryList(p.id, ret.getId());
		ArrayAdapter<DlvItemData> aa = new ArrayAdapter<DlvItemData>(this, R.layout.simple_spinner_layout, dlvList);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);

		Spinner sp = (Spinner) findViewById(R.id.spDiscount);
		sp.setAdapter(aa);
		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				DlvItemData dd = dlvList.get(arg2);
				if( dd.cost != priceVal )
					onChangeCost(dd.cost);
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) { }
		});
		
		if( ri != null ) {
			for(DlvItemData dd : dlvList) {
				if( dd.number.equals(ri.dlvNumber) ) {
					sp.setSelection(dlvList.indexOf(dd));
					break;
				}
			}
			
			if( dlvList.size() == 0 && priceVal != ri.cost )
				onChangeCost(ri.cost);
		}
		
		ConfigImpl ci = new ConfigImpl();
		sp = (Spinner)findViewById(R.id.spRetCause);
		DialogHelper.loadSpinnerFromConfig(ci, "ПричиныВозврата", new ArrayList<CharSequence>(), sp, (ri == null) ? "" : ri.cause);
	}
	
	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		ReturnItemEx ri = (ReturnItemEx)item;
		
		Spinner sp = (Spinner) findViewById(R.id.spDiscount);
		DlvItemData dd = (DlvItemData) sp.getSelectedItem();
		if( dd != null ) {
			ri.dlvNumber = dd.number;
		}
		
		sp = (Spinner)findViewById(R.id.spRetCause);
		Object val = sp.getSelectedItem();
		if( val != null )
			ri.cause = val.toString();
	}

	List<DlvItemData> getDeliveryList(String itemId, String orgId) {
		List<DlvItemData> ret = new ArrayList<DlvItemData>();
		
		String table = DataObjectInfo.getInstance().getTableName(Delivery.class);
		String where = "id='" + orgId + "'";
		Class<? extends DataObject> dlvType = DbObject.getDataType(Delivery.class);
		DbReader r = new DbReader();
		
		try {
			Delivery d = (Delivery) dlvType.newInstance();
			boolean bdo = r.select(d, table, where);
			while(bdo) {
				for(DeliveryItem di : d.items) {
					if( di.id.equals(itemId) ) {
						DlvItemData dd = new DlvItemData();
						dd.date = d.date;
						dd.number = d.number;
						dd.discName = "";

						dd.cost = (int)((long)di.sum * Consts.QTY_SCALE/ di.qty);
						ret.add(dd);
						break;
					}
				}
				bdo = r.selectNext(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		r.close();
		
		return ret;
	}
}

class DlvItemData {
	public String number;
	public Date date;
	public String discName;
	
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int cost;
	
	@Override
	public String toString() {
		String text = number + " " + Util.simpleDateFormat.format(date);
		if( discName.length() > 0 )
			text +=  " " + discName;
		text += " " + Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		return text;
	}
}

package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import android.content.Context;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.NdsItem;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Pko;
import com.grsoft.dataobjects.Sales;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.PkoInfo;
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;


public class PkoImplBase<T extends Pko> extends CreatableDocument<T> {

	@Override
	public void open(Context context) {
		PkoInfo.open(context, getRowid());
	}

	@Override
	public long sum() { return data.sum; }
	
	@Override
	public String getDescription(Context context) {
		return data.number;
	}
	
	@Override public String getNumber() { return data.number; }

	@Override
	public boolean isEditable() {
		return super.isEditable() && 
				(!Features.DISABLE_EDIT_AFTER_PRINT || ((data.params & ParamState.ofPrinted) == 0));
	}
	
	public void markPrinted() { data.params |= ParamState.ofPrinted; }
		
	void initInternal(Document<?> src, GpsCoord location, Map<Integer, Integer> nds, String supplyercode) {
		data.date = Util.getDate();
		data.created = Util.getDateTime();
		
		data.id = src.getId();
		data.latitude = location.latitude;
		data.longitude = location.longitude;
		data.params = 0;

		for(Entry<Integer, Integer> entry : nds.entrySet()){
			NdsItem ndsItem = new NdsItem();
			ndsItem.nds = entry.getKey();
			ndsItem.sumtax = entry.getValue();
			data.nds.add(ndsItem);
		}
		
		data.sum = src.sum();
		data.supplyercode = supplyercode;
		data.number = DocHelper.makeDocNumber(this);

		TimeZone tz = TimeZone.getDefault();
		Date now = new Date();
		data.timeZone = -tz.getOffset(now.getTime()) / (60*1000);

		write();
		PkoDoc.instance().refreshDocSum(data.id);
	}
	
	static PkoImpl findFromSales(String number) {
		String table = DataObjectInfo.getInstance().getTableName(Pko.class);
		String where = new String("salesnumber = '");
		where += number;
		where += "'";
		List<Long> res = DbReader.readIds(table, where, null);
		if( res.size() > 0 ) {
			PkoImpl ret = new PkoImpl();
			ret.read(res.get(0));
			return ret;
		}
		return null;
	}

	static void makeAlertText(Context context) {
		Toast.makeText(context, R.string.cant_create_pko, Toast.LENGTH_SHORT).show();
	}
	
	public static PkoImpl fromSales(SalesBaseImpl<?> sales, GpsCoord location, Context context) {
		Sales src = sales.getData();
		
		PkoImpl ret = (Features.ALLOW_MULTY_PKO_ON_SALES) ? null : findFromSales(src.number);
		if ( ret == null ) {
			ret = new PkoImpl();
			
			ret.data.sales = src.created;
			ret.data.salesnumber = src.number;
			
			ret.initInternal(sales, location, src.makeTaxEntries(), src.supplyercode);
			ret.close();
		} else {
			makeAlertText(context);
		}
		
		return ret;
	}

	public static PkoImpl fromSales(DeliveryImpl sales, GpsCoord location, Context context) {
		Delivery src = (Delivery) sales.getData();
		
		PkoImpl ret = (Features.ALLOW_MULTY_PKO_ON_SALES) ? null : findFromSales(src.number);
		if ( ret == null ) {
			ret = new PkoImpl();
			
			ret.data.sales = src.created;
			ret.data.salesnumber = src.number;
			
			ret.initInternal(sales, location, src.makeTaxEntries(), src.supplyercode);
			ret.close();
		} else {
			makeAlertText(context);
		}
		
		return ret;
	}
	
	@Override
	public boolean delete() {
		if(Features.CANT_DEL_PRINTED_DOCS && (data.params & ParamState.ofPrinted) != 0)
			return true;
			
		boolean result = super.delete();
		if(result) 
			PkoDoc.instance().refreshDocSum(data.id);
		
		return result;
	}
	
	@Override
	public void postInit(){
		data.number = DocHelper.makeDocNumber(this);
		
		DataTraveler.travel(Firm.class, new DataTraveler.Travel<Firm>() {

			@Override
			public boolean travel(DataTraveler<Firm> item) {
				data.supplyercode = item.data.id;
				return false;
			}
		}, null);
	}
}

package com.grsoft.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceWhData;
import com.grsoft.dataobjects.WhRest;
import com.grsoft.dataobjects.WhRestItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import android.database.SQLException;

public class RestHitching extends Hitching {
	
	Map<String, PriceEx> qtys = new HashMap<String, PriceEx>();
	Map<String, Integer> sklads = new HashMap<String, Integer>();
	boolean started = false;

	
	public RestHitching() {
		super(WhRest.class, "WhRest");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if(!started) {
			try {
				String sql = "update Price set qty = 0, whQty = null";
				DataBaseManager.getDataBase().execSQL(sql);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}

			ConfigImpl ci = new ConfigImpl();
			StringBuilder sb = new StringBuilder();
			if(ci.getValue(sb, "Склады")) {
				int idx = 0;
				for(String skl : sb.toString().split(";")) {
					sklads.put(skl.split("\t")[1], idx);
					idx++;
				}
			}
			started = true;
		}
		
		WhRest data = (WhRest) rawObject.createDataObject(WhRest.class);
		Integer idx = sklads.get(data.id);
		if(idx == null)
			return;
		
		for(WhRestItem wri : data.items) {
			PriceEx pe = qtys.get(wri.id);
			if(pe == null) {
				PriceImpl pi = new PriceImpl();
				pe = (PriceEx) pi.getData();
				pe.id = wri.id;
				pi.read();
				pi.close();
				
				qtys.put(wri.id, pe);
			}
			if(idx == 0)
				pe.qty = wri.qty;
			else {
				while(pe.whQty.size() < idx) {
					pe.whQty.add(new PriceWhData());
				}
				pe.whQty.get(idx-1).qty = wri.qty;
			}
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();

		DbWriter wr = new DbWriter();
		for(Entry<String, PriceEx> kv : qtys.entrySet()) {
			wr.insertRecord(kv.getValue());
		}
		wr.close();	
	}
}

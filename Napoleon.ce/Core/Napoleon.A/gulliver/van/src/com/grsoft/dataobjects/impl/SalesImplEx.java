package com.grsoft.dataobjects.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ArchSales;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderExtended;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgExtended;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.documents.ArchSalesDoc;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.GlobalServiceContext;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SalesImplEx extends SalesImpl {
	
	public static final String COUNTERGEN = "com.grsoft.dataobjects.impl.SalesImpl.counter";
	public static final String COUNTERDOP = "com.grsoft.dataobjects.impl.SalesImpl.counterdop";
	private static final String DATE = "com.grsoft.dataobjects.impl.SalesImpl.date";
	public static String SALESIMPLPREF = "sales_impl_pref";
	public static final String RECEIPTCNT = "com.grsoft.dataobjects.impl.SalesImpl.receiptcnt";
	public static final String COUNTERGENDATA = "com.grsoft.dataobjects.impl.SalesImpl.countergendata";
	@Override
	public void initFromOrder(OrderImplBase<?> src, GpsCoord location) {
		OrderExtended se = (OrderExtended) getData();
		OrderExtended oe = (OrderExtended) src.getData();
		se.setDogCode(oe.getDogCode());
		se.setFirmCode(oe.getFirmCode());
		
		OrgImpl orgImpl = new OrgImpl();
		orgImpl.getData().id = src.getId();
		
		boolean isGen = false;
		
		if(orgImpl.read()){
			List<OrgDogovor> dogovors = ((OrgExtended)orgImpl.getData()).getDogovors();
			
			for(OrgDogovor dog : dogovors)
				if(se.getDogCode().trim().equals(dog.id.trim())){
					isGen = dog.isGeneral();
					break;
				}
		}
		
		orgImpl.close();
		convertNumberType(isGen);
		super.initFromOrder(src, location);
	}
	
	
	@Override
	public boolean delete() {
		if( data.items.size() > 0 && (data.params & ParamState.ofPrinted) != 0 && !isExported() ) {
			ArchSales as = new ArchSales();
			String table = DataObjectInfo.getInstance().getTableName(Sales.class);
			DbReader r = new DbReader();
			if( r.select(as, table, "created="+data.created.getTime()) ) {
				DbWriter w = new DbWriter();
				as.params = 0;
				w.insertRecord(as);
				w.close();
				ArchSalesDoc.instance().refreshDocSum(data.id);
			}
		}
		return super.delete();
	}
	
	@Override public void initDocNumber() {}

	@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
	protected String makeGenNumber(int counter, String prefix) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(data.date);
		String number = String.format("%s%s/%04d", prefix, date, counter);
		return number;
	}

	@SuppressLint("DefaultLocale")
	protected String makeSupplNumber(int counter, String prefix) {
		String number = String.format("%s%09d", prefix, counter);
		return number;
	}
	
	protected int getNextDocNumber(boolean inc, boolean isGeneral) {
		SharedPreferences pref = GlobalServiceContext.
				service.getSharedPreferences(SALESIMPLPREF, Context.MODE_PRIVATE);
		int counter = 1;

		String storeData = pref.getString(DATE, "");
		String today = Util.simpleDateFormat.format(data.date);
		
		android.content.SharedPreferences.Editor editor = pref.edit();
		String prefName = isGeneral ? COUNTERGEN : COUNTERDOP; 
		
		if (storeData.equals(today) || !isGeneral)
			counter = pref.getInt(prefName, 1);
		else
			editor.putString(DATE, today);
		
		if (inc){
			editor.putInt(prefName, counter+1);
			editor.commit();
		}
		
		return counter;
	}
	
	
	private int getGenCounter(Date date){
		SharedPreferences pref = GlobalServiceContext.service.getSharedPreferences(SALESIMPLPREF, Context.MODE_PRIVATE);
		android.content.SharedPreferences.Editor editor = pref.edit();
		final String OD = ";";
		final String FD	= " ";
		
		int result = 0;
		
		Map<Long, Integer> map = parseMap(pref.getString(COUNTERGENDATA, ""), OD, FD);
		
		long k = date.getTime();
		
		if (map.containsKey(k))
			result = map.get(k);
		else
			map.put(k, result);
		
		result++;
		map.put(k, result);
		String s = parseString(map, OD, FD);
		editor.putString(COUNTERGENDATA, s);
		editor.commit();
		
		return result;
	}
	
	protected String parseString(Map<Long, Integer> src, final String OD, final String FD) {
		StringBuilder sb = new StringBuilder();
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getDate());
		c.add(Calendar.DATE, -7);
		long d = c.getTime().getTime();
		
		for(Entry<Long, Integer> e : src.entrySet()){
			if (e.getKey() < d)
				continue;
			
			if (sb.length() > 0)
				sb.append(OD);
			sb.append(e.getKey().toString());
			sb.append(FD);
			sb.append(e.getValue().toString());
		}
		
		return sb.toString();
	}

	protected Map<Long, Integer> parseMap(String src, final String OD, final String FD) {
		Map<Long, Integer> result = new HashMap<Long, Integer>();
		
		for(String o : src.split(OD)){
			String v[] = o.split(FD);
			
			if(v.length > 1){
				try{
					Long k = Long.parseLong(v[0]);
					Integer e = Integer.parseInt(v[1]);
					
					result.put(k, e);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	public void convertNumberType(boolean isGeneral){
		int counter = getNextDocNumber(true, isGeneral);

		String prefix = DocHelper.getAgentPrefix();
		String number = isGeneral ? makeGenNumber(getGenCounter(data.date), prefix) : makeSupplNumber(counter, prefix); 
		
		data.number = number;
		((SalesEx)data).isGenDoc = isGeneral ? 1 : 0;
	}

	public String getNumber() {
		return data.number;
	}
}

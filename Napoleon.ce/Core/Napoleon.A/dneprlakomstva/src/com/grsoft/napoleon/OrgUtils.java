package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class OrgUtils {
	public static long getOutDebt(String id) {
		long outSum = 0;
		
		DbWriter.checkDBTable(Delivery.class);
		Date checkDate = new Date();
		String table = DataObjectInfo.getInstance().getTableName(Delivery.class);			
		String sql = "SELECT sum(sumD) FROM " + table + " WHERE paydate <= ? and sumD > 0 and id='" + id + "'";		
		String[] args = { Long.toString(checkDate.getTime()) };
		Cursor c = null;;
		try {
			SQLiteDatabase db = DataBaseManager.getDataBase();
			c = db.rawQuery(sql, args);
			if( c.moveToNext() )
				outSum = c.getLong(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( c != null )
				c.close();
		}
		
		return outSum;
	}
	
	public static boolean isDocsDiff(OrderEx oe, Delivery d) {
		if(oe.items.size() != d.items.size())
			return true;
		
		HashMap<String, Integer> data = new HashMap<String, Integer>();
		for(OrderItem oi : oe.items) {
			Integer val = data.get(oi.id);
			if(val == null)
				val = 0;
			val += oi.qty;
			data.put(oi.id, val);
		}
		
		for(DeliveryItem di : d.items) {
			Integer val = data.get(di.id);
			if( val == null || val != di.qty)
				return true;
		}
		
		return false;
	}
	
	public static String makeOrgInfo(OrgEx o, OrderImpl doc) {
		ArrayList<CharSequence> costTypes = new ArrayList<CharSequence>();
		OrderEx order = null;
		if( doc != null)
			order = (OrderEx)doc.getData();
		
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "ВидЦены";
		ci.read();
		ci.close();
		DialogHelper.makeList(c.value, costTypes);
		
		String costType = (costTypes.size() > o.costype) ? costTypes.get(o.costype).toString() : "";
		
		
		OrgSumImpl osi = new OrgSumImpl();
		OrgSum os = osi.getData();
		os.id = o.id;
		os.type = DebtDoc.instance().getName();
		osi.read();
		osi.close();
		
		String ret = o.name;
		if(Features.SHOW_ORG_ADDRESS && o.address.length() > 0 ) {
			ret += "<br><i>" + o.address + "</i>";
		}

		ret += "<br/><b>";
		if( os.sum > 0 ) {
			ret += "Долг " + Util.IntToScaleStr(os.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			long outSum = OrgUtils.getOutDebt(o.id);
			if( outSum > 0 )
				ret += " <font color='red'>просрочено " + Util.IntToScaleStr(outSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</font>";
		} else if( os.sum < 0 )
			ret += "Переплата " + Util.IntToScaleStr(-os.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		else 
			ret += "Долгов нет";
		ret += "</b>";
		
		ret += "<br/>тип цен:" + costType + " категория:" + o.category + "," + o.segment; 
		ret +="<br/>отсрочка:" + Integer.toString(o.delay);
		ret += "<br/>лимит:" + Util.IntToScaleStr(o.debt, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		long limitFree = o.debt - os.sum;
		if(order != null) {
			limitFree +=  order.incass + order.willSum - doc.sum();
		}
		if( limitFree > o.debt)
			limitFree = o.debt;
		
		String font = "", fontEnd = "";
		if( limitFree < 0 ) {
//			limitFree = limitFree;
			font = "<font color='red'>";
			fontEnd = "</font>";
		}
		ret += " остаток:<b>" + font + Util.IntToScaleStr(limitFree, Consts.SUM_SCALE, Util.DEC_DELIM, false) + fontEnd + "</b>";
		
		return ret;
	}
}

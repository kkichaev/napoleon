package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgBalance;
import com.grsoft.dataobjects.OrgBalanceData;
import com.grsoft.util.Util;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;

public class BalanceHelper {
	public static final String START_BALANCE = "Начальное сальдо";
	
	public static void refreshBalance() {
		final Map<BalanceDocKey, OrgBalanceData> balance = new HashMap<BalanceDocKey, OrgBalanceData>();

		final DbWriter wr = new DbWriter();
		DbWriter.checkDBTable(OrgBalanceData.class);
		final OrgBalanceData blncData = new OrgBalanceData();
		SQLiteDatabase db = DataBaseManager.getDataBase();
		db.execSQL("delete from " + blncData.getTableName());
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -3);
		final Date startDate = Util.getDayStart(c.getTime());
		
		DataTraveler.travel(OrgBalance.class, new DataTraveler.Travel<OrgBalance>() {
			@Override
			public boolean travel(DataTraveler<OrgBalance> item) {
				OrgBalanceData obd = new OrgBalanceData();
				obd.id = item.data.id;
				obd.idDog = item.data.idDog;
				obd.payDate = startDate;
				obd.sumD = item.data.balance;
				balance.put(new BalanceDocKey(item.data), obd);
				return true;
			}
		}, "");
		
		DataTraveler.travel(DeliveryEx.class, new DataTraveler.Travel<DeliveryEx>(){

			@Override
			public boolean travel(DataTraveler<DeliveryEx> item) {
				BalanceDocKey key = new BalanceDocKey(item.data);
				OrgBalanceData obd = balance.get(key);
				int sumD = (obd == null) ? 0 : obd.sumD;
				int sum = (int)item.data.sum();
				sum = (sumD > sum) ? sum : sumD;
				if(sum > 0) {
					blncData.id = item.data.id;
					blncData.idDog = key.idDog;
					blncData.number = item.data.number;
					blncData.payDate = item.data.payDate;
					blncData.sumD = sum;
					wr.insertRecord(blncData);
				} else if(sum < 0)
					sum = 0;
				sumD -= sum;
				if(obd != null) {
					obd.sumD = sumD;
				}
				return true;
			}
			
		}, "", "ido asc, date desc, number desc");
		
		try {
			String sql = "SELECT id from " + (new Org()).getTableName() + " WHERE ido = ?";
			SQLiteStatement stmt = DataBaseManager.getDataBase().compileStatement(sql);
			for(OrgBalanceData obd : balance.values()) {
				if(obd.sumD > 0) {
					stmt.clearBindings();
					stmt.bindString(1, obd.id);
					String id = stmt.simpleQueryForString();
					if(id != null) {
						obd.id = id;
					}
					obd.number = START_BALANCE;
					wr.insertRecord(obd);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		wr.close();
	}
	
	public static int colorIndex(Date dueDate, Date checkDate) {
		long check_date = checkDate.getTime() / (24 * 3600 * 1000); 
		long curdate = dueDate.getTime() / (24 * 3600 * 1000);
		long diff = curdate - check_date;
		if(diff > 25)
			return 0;
		if(diff >= 11)
			return 1;
		if(diff >= 1)
			return 2;
		return -1;
	}
	
	public static int getColorFromDueDays(int overdueDays) {
		if(overdueDays >= 26)
			return colors[0];
		if(overdueDays >= 11)
			return colors[1];
		if(overdueDays >= 1)
			return colors[2];
		return Color.BLACK;
	}
	
	static int[] colors = new int[] {
			Color.RED,
			Color.BLUE,
			-16751616, //Color.GREEN,
	};
	
	public static int getColor(int index) {
		if(index < 0 || index >= colors.length)
			return Color.BLACK;
		
		return colors[index];
	}
	
	public static int getOrgColor(String ido) {

		long payDate = 0;
		OrgBalanceData blncData = new OrgBalanceData();
		String stmt = "select min(payDate) from " + blncData.getTableName() + " where ido='" + ido + "'";
		
		SQLiteDatabase db = DataBaseManager.getDataBase();
		try {
			Cursor c = db.rawQuery(stmt, null);
			if(c.moveToNext())
				payDate = c.getLong(0);
			c.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(payDate == 0)
			return Color.BLACK;
		return getColor(colorIndex(Util.getDate(), Util.getDayStart(new Date(payDate))));
	}
}

class BalanceDocKey {
	public String id;
	public String idDog;
	
	public BalanceDocKey(OrgBalance src) {
		id = src.id;
		idDog = src.idDog;
	}
	
	public BalanceDocKey(DeliveryEx src) {
		id = src.ido;
		idDog = src.dogovor;
	}
	
	@Override
	public int hashCode() {
		return (id + idDog).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof BalanceDocKey) {
			BalanceDocKey ref = (BalanceDocKey)o;
			return id.equals(ref.id) && idDog.equals(ref.idDog);
		}
		return false;
	}
}

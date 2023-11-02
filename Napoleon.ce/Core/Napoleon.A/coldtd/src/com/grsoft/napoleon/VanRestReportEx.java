package com.grsoft.napoleon;

import java.io.File;

import android.database.sqlite.SQLiteCursor;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.modules.print.util.VanRestData;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.utl.PricePrintHelper;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class VanRestReportEx extends VanRestReport {

	@Override
	protected void printing() {
		new AsyncTask<Integer, Void, File>(){
			protected void onPreExecute() { showDialog(WAIT_FOR_PRINT_DLG); };
			
			@Override
			protected File doInBackground(Integer... params) {
				File result = null;
				if (params.length > 0) {
					int which = params[0];
					OrderImpl document = new OrderImplEx();
					result = PricePrintHelper.printPrice(VanRestReportEx.this, which, document);
					document.close();
				}
				
				return result;
			}
			
			protected void onPostExecute(File output) {
				try {
					dismissDialog(WAIT_FOR_PRINT_DLG);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.execute(2);
	}
	
	@Override
	protected void buildData() {
		String table = DataObjectInfo.getInstance().getTableName(Price.class);
		String sql = "SELECT name, vanQty, qtyInPack, packName  FROM " + table + " where vanQty>0 ORDER BY priceOrder";
		
		try {
			SQLiteCursor c = (SQLiteCursor) DataBaseManager.getDataBase().rawQuery(sql, null);
			while(c.moveToNext()) {
				VanRestDataEx d = new VanRestDataEx();
				d.name = c.getString(0);
				d.qty = c.getInt(1);
				d.inPack = c.getInt(2);
				d.packName = c.getString(3);
				data.add(d);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(new Adapter());
	}

	class Adapter extends RestAdapter {
		@Override
		protected void setQty(TextView tv, VanRestData d) {
			if(((CfgNpl)ConfigManager.getConfig()).isPackView) {
				int inPack = ((VanRestDataEx)d).inPack;
				if( inPack == 0 )
					inPack = Consts.QTY_SCALE;
				int qty = (int)((long)d.qty * Consts.QTY_SCALE / inPack);
				String qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " " + ((VanRestDataEx)d).packName;
				tv.setText(qtyText);
			} else
				super.setQty(tv, d);
		}
	}
}

class VanRestDataEx extends VanRestData {
	public int inPack;
	public String packName;
}

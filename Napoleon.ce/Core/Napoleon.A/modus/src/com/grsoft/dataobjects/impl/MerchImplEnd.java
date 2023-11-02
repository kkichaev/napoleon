package com.grsoft.dataobjects.impl;

import java.util.Date;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.MerchItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;


public class MerchImplEnd extends MerchImpl {
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		android.database.Cursor c = null;
		
		try{
			String tn = getTableName();
			
			StringBuilder where = new StringBuilder();
			where.append("select rowid from ").append(tn).append(" where ");
			where.append("created = (select max(created) from ").append(tn).append(" where id=?)");
			
			c = DataBaseManager.getDataBase().rawQuery(where.toString(), new String[]{orgId});
			
			if (c.moveToFirst()){
				long time = c.getLong(0);
				data.created = new Date(time);
				
				read();
				
				for(MerchItem i : data.items)
					i.finish = i.start;
				
				write();
				close();
				
				Intent i = new Intent(context, Warehouse.activity);
				i.putExtra(ExtrasConst.DOC_ROW_ID_STR, time);
				i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
				i.putExtra(ExtrasConst.EDIT_MODE_STR, false);
				
				context.startActivity(i);
			}
				
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(c != null)
				c.close();
		}
		
		return false;
	}
	
	protected void adjView(AlertDialog arg0, View view, KeypadHelper kh, int finish) {
		View v = view.findViewById(R.id.edCount);
		
		if(v != null)
			v.setEnabled(false);
		
		v = view.findViewById(R.id.edFinish);
		
		if(v != null){
			((EditText)v).setText(Util.IntToScaleStr(finish, Consts.QTY_SCALE));
			kh.setTargetID(v.getId());
			v.requestFocus();
			((EditText)v).selectAll();
		}
	}
	
	@Override
	protected int apply(int value, Object... params) {
		int result = value;
		
		if(params.length >= 2 && params[1] instanceof AlertDialog){
			AlertDialog dialog = (AlertDialog)params[1];
			
			EditText ed = (EditText)dialog.findViewById(R.id.edFinish);
			
			if(ed != null){
				String s = ed.getText().toString().trim();
				result = Util.StrToScale(s, Consts.QTY_SCALE);
			}
				
		}
		
		return result;
	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		Price price = priceImpl.getData();
		MerchItem item = (MerchItem) findItem(price.id);

		boolean needUpdate = true;
		
		if( item.finish != qty )
			item.finish = qty;
		else
			needUpdate = false;
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}
}

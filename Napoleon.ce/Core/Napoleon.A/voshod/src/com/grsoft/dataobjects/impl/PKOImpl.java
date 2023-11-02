package com.grsoft.dataobjects.impl;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.PKO;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PKODoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PKOImpl extends CreatableDocument<PKO> {

	@Override public long sum() { return data.sum; }
	
	@Override
	public void open(Context context) {
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		return false;
	}
	
	public void setSum(Context context, BaseAdapter a, int sum) {
		InputNumberDlg.open(context, new InputHandler(a, sum), Consts.SUM_SCALE, false, "¬ведите сумму");
	}
	
	class InputHandler extends InputNumber {
		BaseAdapter adapter = null;
		int sum;
		public InputHandler(BaseAdapter a, int sum) {
			adapter = a;
			this.sum = sum;
		}
		
		@Override
		public void applayInput(int value, Object... params) {
			data.sum = value;
			write();
			PKODoc.instance().refreshDocSum(data.id);
			if( adapter != null )
				adapter.notifyDataSetChanged();
		}

		@Override public int getValue() { return sum; }		
	}
	
	static public PKOImpl find(PaymentEx p) {
		String table = DataObjectInfo.getInstance().getTableName(PKO.class);
		String stmt = String.format(
				"dogId='%s' and id='%s' and number='%s' and fiscal=%d", 
				p.dogId, p.id, p.number, p.fiscal);

		List<Long> ids = DbReader.readIds(table, stmt, null);
		PKOImpl ret = null;
		if( ids.size() > 0 ) {
			ret = new PKOImpl();
			if( !ret.read(ids.get(0)) )
				ret = null;
		}
		
		return ret;
	}
	
	public boolean init(PaymentEx p) {
		data.id = p.id;
		data.dogId = p.dogId;
		data.number = p.number;
		data.sum = (int)p.sum;
		data.fiscal = p.fiscal;
		
		data.date = Util.getDate();
		data.created = Util.getDateTime();
		
		data.params = 0;
		
		return (write() != ExtrasConst.INVALID_ID);
	}
}

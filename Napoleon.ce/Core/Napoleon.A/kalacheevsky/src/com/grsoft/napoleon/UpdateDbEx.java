package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;

public class UpdateDbEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result =  super.getGenDataHitchings();
		
		if(result == null)
			result = new ArrayList<Hitching>();
		
		if(((CheckBox)findViewById(R.id.cbGenData)).isChecked()) {
			result.add(new RcvNewHitching(DbObject.getDataType(Dogovor.class), "Dogovor"));
		}
		
		return result;
	}
	
	@Override
	protected List<Hitching> getDebetHitching() {
		List<Hitching> ret = super.getDebetHitching(); 
		ret.add(new ReturnHitching());
		return ret;
	}
}

class ReturnHitching extends RcvNewHitching {
	public ReturnHitching() {
		super(DbObject.getDataType(Return.class), ReturnDoc.OBJ_NAME);
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Return dobj = (Return)rawObject.createDataObject(dataObject);
		for(OrderItem oi : dobj.items) {
			if( oi.qty > 0 )
				oi.cost = (int)(((long)((ReturnItemEx)oi).sum * Consts.QTY_SCALE)/ oi.qty);
		}
		dbProxy.insertRecord(dobj);
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		try {
			ReturnDoc.instance().refreshDocSum();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
}

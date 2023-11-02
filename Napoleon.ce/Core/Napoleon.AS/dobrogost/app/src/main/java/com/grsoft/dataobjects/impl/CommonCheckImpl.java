package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Banks;
import com.grsoft.dataobjects.CheckStatusHandler;
import com.grsoft.dataobjects.ChekBase;
import com.grsoft.dataobjects.CommonChek;
import com.grsoft.dataobjects.CommonChekItem;
import com.grsoft.dataobjects.CommonIncassItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.RequestChek;
import com.grsoft.napoleon.CommonChekEdit;

import android.content.Context;

public class CommonCheckImpl extends CommonIncassImplBase<CommonChek> {

	@Override
	public void open(Context context) {
		CommonChekEdit.open(context, rowid);
	}
	
	public CommonChekItem haveChek(RequestChek chek) {
		for(CommonIncassItem ci : data.items) {
			if( ((CommonChekItem)ci).created.equals(chek.created) )
				return (CommonChekItem)ci;
		}
		
		return null;
	}
	
	@Override
	public void init() {
		DataTraveler.travel(Banks.class, new DataTraveler.Travel<Banks>() {

			@Override
			public boolean travel(DataTraveler<Banks> item) {
				data.bank = item.data.id;
				return false;
			}
		}, "");
		super.init();		
	}

	@Override
	public boolean delete() {
		if(isExported() == false) {
			CheckStatusHandler csh = new CheckStatusHandler();
			for(CommonIncassItem ci : data.items)
				csh.update(((CommonChekItem)ci).created, ChekBase.CHEK_COMMITED);
			csh.close();
		}
		return super.delete();
	}
}

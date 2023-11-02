package com.grsoft.napoleon;

import java.util.List;

import android.os.Bundle;
import android.view.View;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.FolderDiscount;
import com.grsoft.dataobjects.NetUser;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.DiscountType;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.AssortmentMatrixAdapter;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.cbRemains).setVisibility(View.GONE);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> res = super.getGenDataHitchings();
		res.add(0, new RcvNewHitching(FolderDiscount.class, "FolderDiscount"));
		res.add(0, new RcvNewHitching(NetUser.class, "NetAgent"));
		
		CostStrategyEx.clearCache();
		return res;
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		try {
			DiscountType.instance().refreshDocSum();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		StringBuilder value = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		if(cfg.getValue(value, "Range")){
			try{
				int per = Integer.parseInt(value.toString());
				
				value.setLength(0);
				if(cfg.getValue(value, "Usrrng"))
					if(!Boolean.parseBoolean(value.toString()))
						AssortmentMatrixAdapter.PERIOD_IN_MONTH = per;
					else
					{
						ConfigImpl c = new ConfigImpl();
						c.getData().key = "Range";
						c.getData().value = Integer.toString(AssortmentMatrixAdapter.PERIOD_IN_MONTH);
					}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
			
		return true;
	}
}

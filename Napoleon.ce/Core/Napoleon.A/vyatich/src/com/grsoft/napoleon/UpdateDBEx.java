package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.PriceHitching;
import com.grsoft.database.PriceHitchingW;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.StockOrg;
import com.grsoft.dataobjects.impl.OrderCancelImpl;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.modules.CostManagerImpl;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.view.SimpleMessageBox;

import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {

	PriceHitchingEx phe = null;
	FullPriceEx fpe = null;
	
	@Override
	protected Hitching getPriceHitching(boolean rcvRemains) {
		if( rcvRemains ) {
			phe = new PriceHitchingEx();
			return phe;
		}
		fpe = new FullPriceEx();
		return fpe;
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		boolean showError = false; 
		CheckBox rcvCost = (CheckBox)findViewById(R.id.cbCost);
		if( rcvCost != null && rcvCost.isChecked() ) {
			showError = !((CostManagerImpl)Features.COST_MANAGER).isCostReaded();
		}
		
		if( !showError ) {
			if( phe != null && !phe.getReaded() )
				showError = true;
			if( fpe != null && !fpe.getReaded())
				showError = true;
		}
		
		if( showError ) {
			SimpleMessageBox mb = new SimpleMessageBox("Внимание! Новый прайс не принят, цены или остатки могут быть неактуальными.", this);
			task.onUpdateMessage(mb);
		}
		
		return !showError;
	}
	
	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> ret = super.getExported();
		
		DocExportListener dl = new DocSendListner("OrderCancel", OrderCancelImpl.class, "params", ParamState.ofExported);
		if( dl.getDocuments().getCount() > 0 )
			ret.add(dl);
		
		return ret;
	}
	
	@Override
	protected List<Hitching> getCostHitching() {
		List<Hitching> result =  super.getCostHitching();
		result.add(new RcvNewHitching(StockOrg.class));
		return result;
	}
}

class PriceHitchingEx extends PriceHitching {

	boolean readed = false;
	
	boolean getReaded() { return readed; }
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		super.onRead(rawObject);
		readed = true;
	}
}

class FullPriceEx extends PriceHitchingW {
	boolean readed = false;
	
	boolean getReaded() { return readed; }
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		super.onRead(rawObject);
		readed = true;
	}
}

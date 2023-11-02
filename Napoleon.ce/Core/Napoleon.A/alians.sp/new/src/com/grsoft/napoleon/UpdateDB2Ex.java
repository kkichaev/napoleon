package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.PicStoreHitchingEx;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Contract;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.ModifyOrgImpl;
import com.grsoft.dataobjects.impl.NewOrgImpl;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.network.DocExportListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.AssortmentMatrixAdapter;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;

public class UpdateDB2Ex extends UpdateDBEx {
	CheckBox cbDiscount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((CheckBox)findViewById(R.id.cbDebt)).setChecked(true);
		cbDiscount = (CheckBox) findViewById(R.id.cbDiscount);
	}
	
	@Override protected int getContentView() { return R.layout.updatedbex; }
	
	@Override
	protected void postSync(Boolean result) {
		super.postSync(result);
		
		if(result){
			CostStrategy2Ex.resetCash();
			NapoleonEx.notifyDataSet = true;
			
			ConfigImpl cfg = new ConfigImpl();
			StringBuilder sb = new StringBuilder();
			if(cfg.getValue(sb, "јктивныйјссортимент")){
				try{
					AssortmentMatrixAdapter.PERIOD_IN_DAY = Integer.parseInt(sb.toString());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected UpdateProcess getUpdateProcess() {
		return new UpdateProcess(this) {
			@Override
			protected void customSyncProcess() throws RuntimeException {
				if(cbDiscount.isChecked()) {
						List<Hitching> result = new ArrayList<Hitching>();
						result.add(new RcvNewHitching(Contract.class));
						
						ReadService dataBaseUpdater = (ReadService) RWServiceFactory.instance.createReadService(result);
						dataBaseUpdater.setUpdateProcessListenet(this);
	
						if (!dataBaseUpdater.update(activity, getRcvUserInfo(), false)) {
							errMessage = dataBaseUpdater.getMessage();
							Log.d(getClass().getCanonicalName(), "Discount imported: FAILURE");
						} else {
							Log.d(getClass().getCanonicalName(), "Discount imported: SUCCESS");
							traffic += dataBaseUpdater.getReceivedBytes();
						}
				}
			}
		};
	}
	
	@Override
	protected List<DocExportListener> getExportedDocs(boolean docs, boolean visit) {
		List<DocExportListener> res = super.getExportedDocs(docs, visit); 
		
		DocSendListner d = new DocSendListner("NewOrg", NewOrgImpl.class, "params", ParamState.ofExported);
		
		if (d.getDocuments().getCount() > 0)
			res.add(d);
		
		d = new DocSendListner("ModifyOrg", ModifyOrgImpl.class, "params", ParamState.ofExported);
		
		if (d.getDocuments().getCount() > 0)
			res.add(d);
				
		d = new PicStoreHitchingEx(); 

		if (d.getDocuments().getCount() > 0)
			res.add(d);
		
		return res;
	}
}

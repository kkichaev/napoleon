package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Colors;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.dataobjects.Sizes;
import com.grsoft.dataobjects.Sklads;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		
		ret.add(new RcvNewHitching(Colors.class, "Colors"));
		ret.add(new RcvNewHitching(Sizes.class, "Sizes"));
		ret.add(new RcvNewHitching(Sklads.class, "Sklads"));
		
		return ret;
	}
	
	@Override protected int getContentView() { return R.layout.updatedbex; }	
	@Override protected UpdateProcess getUpdateProcess() { return new UpdateProcessEx(this); }
	
	class UpdateProcessEx extends UpdateProcess {

		public UpdateProcessEx(Activity context) {
			super(context);
		}
		
		@Override
		protected void customSyncProcess() {
			if( ((CheckBox)findViewById(R.id.cbRest)).isChecked() ) {
				List<Hitching> rcvHitch = new ArrayList<Hitching>();
				rcvHitch.add(new RcvNewHitching(PriceQty.class, "PriceQty"));
				
				if( rcvHitch.size() > 0 ) {
					ReadService reader =  (ReadService) RWServiceFactory.instance.createReadService(rcvHitch);
					reader.setUpdateProcessListenet(this);
					
					try {
						if( !reader.update(activity, getRcvUserInfo(), false) ){
							errMessage = reader.getMessage();
						}else{
							traffic += reader.getReceivedBytes();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}

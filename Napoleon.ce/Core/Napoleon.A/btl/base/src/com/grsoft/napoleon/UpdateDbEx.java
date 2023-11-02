package com.grsoft.napoleon;

import java.util.List;

import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitching;
import com.grsoft.database.PotenzialOrgRcv;
import com.grsoft.database.PotenzialOrgRestore;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Region;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDbEx extends UpdateDB {
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		
		if (result != null && ((CheckBox)findViewById(R.id.cbGenData)).isChecked()){
			//result.add(new RcvNewHitching(Question.class, "Question"));
			result.add(new RcvNewHitching(Region.class, "Region"));
			result.add(new RcvNewHitching(AgentPrefix.class, "AgentPrefix"));
		}
		
		return result;
	}
	
	@Override
	protected List<Hitching> getRestoreHitching() {
		List<Hitching> result = super.getRestoreHitching();
		
		if (result != null){
			result.add(new PotenzialOrgRestore());
			//result.add(new AnswerRestore());
		}
		
		return result;
	}
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		((CheckBox)findViewById(R.id.cbVisit)).setChecked(true);
		findViewById(R.id.cbDebt).setVisibility(View.GONE);
		findViewById(R.id.cbRemains).setVisibility(View.GONE);
		findViewById(R.id.cbPresent).setVisibility(View.GONE);
		((CheckBox)findViewById(R.id.cbDocs)).setText("Документы");
		((CheckBox)findViewById(R.id.cbRecreateStory)).setText("Восстановить историю");
	}
	
	@Override
	protected Hitching getOrgHitching() {
		return new OrgHitching(){
			@Override
			public void onRead(RawObject rawObject) throws RuntimeException {
				OrgEx dobj = (OrgEx)rawObject.createDataObject(dataObject);
				dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.address.toUpperCase() + "|" + dobj.id.toUpperCase();
				dbProxy.insertRecord(dobj);
			}
		};
	}
	
	@Override
	protected PotenzialOrgRcv getPtncOrgHitching() {
		return new PotenzialOrgRcv(){
			@Override
			public void onRead(RawObject rawObject) throws RuntimeException {
				OrgEx dobj = (OrgEx)rawObject.createDataObject(dataObject);
				dobj.flags |= (Org.FL_USER_CREATED | Org.FL_EXPORTED);
				dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.address.toUpperCase() + "|" + dobj.id.toUpperCase();
				dbProxy.insertRecord(dobj);
			}
		};
	}
}

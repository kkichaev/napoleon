package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.SVTask;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.TaskCategory;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDbEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result =  super.getGenDataHitchings();
		
		if(result == null)
			result = new ArrayList<Hitching>();
		
		result.add(new Hitching(Firm.class, "Firm"));
		result.add(new Hitching(Sklad.class, "Sklad"));
		result.add(new SVTaskHitching());
		result.add(new Hitching(TaskCategory.class, "TaskCategory"));
		
		return result;
	}
	
	protected void onFinishUpdate() {
		NapoleonApp app = (NapoleonApp) getApplication();
		if(app != null)
			app.createScript();
	}
}

class SVTaskHitching extends Hitching{

	public SVTaskHitching() {
		super(SVTask.class, "SVTask");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		SVTask dobj = (SVTask) rawObject.createDataObject(dataObject);
		dobj.created = dobj.date;
		dbProxy.insertRecord(dobj);
	}
	
}

package com.grsoft.napoleon;

import java.util.List;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrgInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;


public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		result.add(new PriceRestHitching());
		result.add(new OrgInfoHitching());
		result.add(new QtyExHitching());
		
		return result;
	}
}

abstract class HitchingUpdate extends Hitching{
	protected SQLiteStatement stm = null;
	
	public HitchingUpdate(Class<? extends DataObject> dataObject, String objectName) {
		super(dataObject, objectName);
		
		try{
			stm = DataBaseManager.getDataBase().compileStatement(getSql());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected abstract String getSql();
	protected abstract void bindStatement(DataObject dobj);
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if(stm != null){
			try{
				stm.clearBindings();
				DataObject dobj = rawObject.createDataObject(dataObject);
				bindStatement(dobj);
				stm.execute();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		try{
			if(stm != null)
				stm.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

class PriceRestHitching extends HitchingUpdate{
	public PriceRestHitching() {
		super(DbObject.getDataType(Price.class), "PriceRest");
	}
	
	@Override
	protected String getSql() {	return "UPDATE price SET qty=? WHERE id=?"; }

	@Override
	protected void bindStatement(DataObject dobj) {
		Price p = (Price)dobj;
		stm.bindLong(1, p.qty);
		stm.bindString(2, p.id);
	}
}

class OrgInfoHitching extends HitchingUpdate{
	
	public OrgInfoHitching() {
		super(OrgInfo.class, "OrgInfo");
	}

	@Override
	protected String getSql() { return "UPDATE org SET flags=?, balance=?, outDays=? WHERE id=?" ; }

	@Override
	protected void bindStatement(DataObject dobj) {
		OrgInfo oi = (OrgInfo)dobj;
		stm.bindLong(1, oi.flags);
		stm.bindLong(2, oi.balance);
		stm.bindLong(3, oi.outDays);
		stm.bindString(4, oi.id);
	}
}

class QtyExHitching extends PriceRestHitching{
	private SQLiteCursor select = null;
	
	public QtyExHitching() {
		objectName = "QtyEx";
	}
	
	@Override
	protected void bindStatement(DataObject dobj) {
		Price p = (Price)dobj;
		int qty = 0;
		
		try{
			if(select == null)
				select = (SQLiteCursor) DataBaseManager.getDataBase().rawQuery("SELECT qty FROM price WHERE id=?", new String[]{p.id});
			else{
				select.setSelectionArguments(new String[]{p.id});
				select.requery();
			}
			
			if(select.moveToFirst())
				qty = select.getInt(0);
		}catch(Exception e){
			e.printStackTrace();
		}
		p.qty += qty;
		super.bindStatement(p);
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		
		try{
			if(select != null)
				select.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

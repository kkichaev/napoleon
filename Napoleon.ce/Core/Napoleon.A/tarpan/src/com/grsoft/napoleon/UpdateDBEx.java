package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrdFlag;
import com.grsoft.dataobjects.Update;
import com.grsoft.dataobjects.UpdateItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.ReadService;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	private ListView list;
	private boolean updateStatus = false;

	@Override
	protected int getContentView() {
		return R.layout.updatedbex;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DbReader r = new DbReader();
		Update upd = new Update();
		DbWriter.checkDBTable(DbObject.getDataType(Update.class));
		boolean bdo = r.select(upd, DataObjectInfo.getInstance().getTableName(Update.class), "");

		Button btnUdate = (Button)findViewById(R.id.btnUpdate);
		ScrollView svScroll = (ScrollView) findViewById(R.id.svScroll);
		list = (ListView) findViewById(android.R.id.list);
		
		if (bdo){
			btnUdate.setVisibility(View.GONE);
			svScroll.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
			setCustumerControls(r, upd);
		}else{
			svScroll.setVisibility(View.VISIBLE);
			btnUdate.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
		}
		
		r.close();
	}

	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = super.getGenDataHitchings();
		result.add(new RcvNewHitching(DbObject.getDataType(OrdFlag.class), "OrdFlag"));
		return result;
	}
	protected void setCustumerControls(DbReader r, Update upd) {
		boolean bdo = true;
		ArrayList<Update> values = new ArrayList<Update>();

		while (bdo) {
			values.add((Update) upd.clone());
			bdo = r.selectNext(upd);
		}

		Collections.sort(values, new Comparator<Update>() {
	        @Override
	        public int compare(Update s1, Update s2) {
	            return s1.name.compareToIgnoreCase(s2.name);
	        }
	    });
		
		list.setAdapter(new ArrayAdapter<Update>(this, R.layout.upd_list_row,
				values));
		list.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> av, View arg1, int pos,
					long arg3) {
				Update upd = ((ArrayAdapter<Update>) av.getAdapter())
						.getItem(pos);

				CheckBox cbClearDB = (CheckBox) findViewById(R.id.cbClearDB);
				cbClearDB.setChecked(false);
				CheckBox cbGenData = (CheckBox) findViewById(R.id.cbGenData);
				cbGenData.setChecked(false);
				CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
				cbRemains.setChecked(false);
				CheckBox cbDocs = (CheckBox) findViewById(R.id.cbDocs);
				cbDocs.setChecked(false);
				CheckBox cbVisit = (CheckBox) findViewById(R.id.cbVisit);
				cbVisit.setChecked(false);
				CheckBox cbPresent = (CheckBox) findViewById(R.id.cbPresent);
				cbPresent.setChecked(false);
				CheckBox cbDebt = (CheckBox) findViewById(R.id.cbDebt);
				cbDebt.setChecked(false);
				CheckBox cbRecreateStory = (CheckBox) findViewById(R.id.cbRecreateStory);
				cbRecreateStory.setChecked(false);

				for (UpdateItem i : upd.items) {
					switch (i.id) {
					case 1:
						cbClearDB.setChecked(true);
						break;
					case 2:
						cbGenData.setChecked(true);
						cbRemains.setChecked(i.param == 0);
						break;
					case 3:
						cbDocs.setChecked(true);
						break;
					case 4:
						cbVisit.setChecked(true);
						break;
					case 5:
						cbPresent.setChecked(true);
						break;
					case 6:
						cbDebt.setChecked(true);
						break;
					case 7:
						cbRecreateStory.setChecked(true);
						CfgNpl config = (CfgNpl) ConfigManager.getConfig();
						config.monthsToRecreate = i.param == 0 ? 1 : i.param;
						break;
					case 8:
						updateStatus = true;
						break;
					}
				}

				getUpdateProcess().execute((Void[]) null);
			}
		});
	}
	
	@Override
	protected void postExported(boolean docExported) {
		final String TAG = "postExported";
		
		if(updateStatus){
			updateStatus = false;
			
			ReadService readService = (ReadService) RWServiceFactory
					.instance.createReadService(new ArrayList<Hitching>());
			readService.setUpdateProcessListenet(null);
			
			try{
				if (!readService.update(this, getGpsUserInfo(), false)){
					errMessage = readService.getMessage();
					Log.d(TAG, "status imported: FAILURE");
				}else{
					Log.d(TAG, "status imported: SUCCESS");
					traffic += readService.getReceivedBytes();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		CheckBox cbClearDB = (CheckBox) findViewById(R.id.cbClearDB);
		
		if(cbClearDB.isChecked()){
			SharedPreferences pref = getApplication().getSharedPreferences(
					BehaviorSettingEx.SETING_NAME, Context.MODE_PRIVATE);
			
			Editor ed = pref.edit();
			ed.putLong(NapoleonApp.UPDTATE_PRESENT_TIME, -1);
			ed.commit();
		}
			
		return super.onFinishUpdate(task);
	}
}

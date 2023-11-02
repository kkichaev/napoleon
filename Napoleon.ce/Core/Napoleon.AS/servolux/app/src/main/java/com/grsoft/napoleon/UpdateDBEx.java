package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.RequestSync;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.OrderDocEx;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.DisabledFirms;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class UpdateDBEx extends UpdateDB implements DisabledFirms.Handler {
	
	ProgressDialog pd = null;
	Set<String> disaabled = new HashSet<String>();
	List<Long> photoDocs = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.cbRemains).setVisibility(View.INVISIBLE);
		
		((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);
		((CheckBox) findViewById(R.id.cbVisit)).setChecked(true);
		if (Features.COST_MANAGER != null)
			((CheckBox) findViewById(R.id.cbCost)).setChecked(true);
	}
	
	@Override
	protected UpdateProcess getUpdateProcess() {
		if( ((CheckBox) findViewById(R.id.cbDocs)).isChecked() ) {
			pd = ProgressDialog.show(this, "Подождите, пожалуйста", "Проверка запрета отправки");
			DisabledFirms.loadDisabledFirms(this, this);
			
			return null;
		}
		
		return super.getUpdateProcess();
	}

	void closeWaitDialog() {
		if( pd != null ) {
			pd.dismiss();
			pd = null;
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		DbWriter.dropTable(new RequestSync().getTableName());
	}
	
	@Override
	protected List<DocExportListener> getExportedDocs(boolean bdocs, boolean visit) {
		photoDocs.clear();
		if(ScriptDefImpl.canScripting()) {
			List<DocExportListener> ret = new ArrayList<DocExportListener>();
			
			Map<String, List<Long>> docs = new HashMap<String, List<Long>>();
			DocExportListener del = ScriptDoc.instance().getDirtyDocuments();
			DocList dl = del.getDocuments();
			if(dl.getCount() > 0) {
				ret.add(del);
				for(Document<?> d : dl) {
					ScriptImplEx sie = (ScriptImplEx)d;
					sie.setDisabledFirms(disaabled);
					sie.addDocumentsToSend(docs);
				}
				dl.close();

				for(Entry<String, List<Long>> kv : docs.entrySet()) {
					if(kv.getValue().size() > 0) {
						DocTypeBase dt = DocTypeBase.getDocType(kv.getKey());
						if(dt != null) {
							if(dt == VisitDoc.instance()) {
								photoDocs.addAll(kv.getValue());
								continue;
							}
							if(!dt.photoDoc() || visit ) {
								DocSendListner dsl = new DocSendListner(dt.getObjectName(), new DocList(dt.getDocClass(), kv.getValue()));
								ret.add(dsl);
							}
						}
					}
				}
			}			
			return ret;
		}
		return super.getExportedDocs(bdocs, visit);
	}

	@Override
	protected List<CreateDocDataObject> getPhotoDocs() {
		if (ScriptDefImpl.canScripting()) {
			List<CreateDocDataObject> ret = new ArrayList<>();
			if(photoDocs.size() > 0) {
				String where = "created in (";
				for (Long l : photoDocs) {
					where += Long.toString(l) + ",";
				}
				where += "0)";
				List<Visit> docs = DbReader.fetch(Visit.class, where);
				ret.addAll(docs);
			}
			return ret;
		}
		return super.getPhotoDocs();
	}

	@Override
	public void firmsLoaded(final HashSet<String> disabledFirms) {
		OrderDocEx ode = (OrderDocEx)OrderDoc.instance();
		ode.setDiabledFirms(disabledFirms);
		disaabled = disabledFirms;
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				closeWaitDialog();
				
				String text = "";
				if( disabledFirms.size() > 0 )
					text += "Включена блокировка передачи, заявки могут не отправиться";
				
				if(ScriptImpl.hasUncomplete()) {
					if(text.length() > 0)
						text += "\n";
					text += "У вас есть незавершенные сценарии, они не будут отправлены";
				}
				
				if(text.length() > 0)
					Toast.makeText(UpdateDBEx.this, text, Toast.LENGTH_LONG).show();
				
				UpdateDBEx.super.getUpdateProcess().execute((Void[]) null);
			}
		});
	}
	
	@Override
	public void error(final String message) {
		runOnUiThread(new Runnable() {
			@Override public void run() { 
				closeWaitDialog();
				String err = "Ошибка проверки\n" + message;
				Toast.makeText(UpdateDBEx.this, err, Toast.LENGTH_SHORT).show();
			}
		});
	}
}

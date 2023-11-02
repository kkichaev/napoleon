package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;


public class MainEx extends Main {
	private Map<String, Integer> orgColors = new HashMap<String, Integer>();
	
	@Override
	protected void onResume() {
		super.onResume();
		orgColors.clear();
	}

	@Override
	protected void drawOrg(Org org, View view) {
		super.drawOrg(org, view);

		if (!orgColors.containsKey(org.id))
			initOrgColor(org, view);

		((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(orgColors.get(org.id));
	}

	private void initOrgColor(Org data, View view) {
		TextView tv = (TextView)view.findViewById(R.id.tvOrgName);
		int c = tv.getCurrentTextColor();
		
		OrgEx e = (OrgEx)data;
		List<OrgDogovor> d = e.dogovors;
		
		if( d != null && d.size() > 0){
			boolean sd = false;
			boolean ad = true;
			
			for(int i = 0; i < d.size(); i++){
				String m = d.get(i).stopMsg.trim();
			
				if (!sd && m.length() > 0)
					sd = true;
				
				if(ad && m.length() == 0)
					ad = false;
			}
			
			if (ad)
				orgColors.put(data.id, getResources().getColor(R.color.red));
			else if(sd)
				orgColors.put(data.id, getResources().getColor(R.color.blue));
			else
				orgColors.put(data.id, c);
		}else
			orgColors.put(data.id, c);
	}

	public void calcScriptSums() {
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				DocList list = ScriptDoc.instance().docList(null);
				for(Document<?> d : list){
					if (d instanceof ScriptImpl){
						ScriptImpl s = (ScriptImpl)d;
						
						long c = s.calcDocSum();
						
						if(s.getData().sum != c){
							s.getData().sum = c;
							s.write();
							s.close();
						}
					}
				}
				
				try{
					ScriptDoc.instance().refreshDocSum();
				}catch(Exception e){
					e.printStackTrace();
				}
				
				return null;
			}
			
			protected void onPostExecute(Void result) {
				if (solidMainAdapter != null && solidMainAdapter instanceof BaseMainAdapter)
					((BaseMainAdapter)solidMainAdapter).reload();
				
				if (foldersMainAdapter != null && foldersMainAdapter instanceof BaseMainAdapter)
					((BaseMainAdapter)foldersMainAdapter).reload();
				
				BaseAdapter adapter = ((BaseAdapter)list.getAdapter());
				
				if(adapter != null)
					adapter.notifyDataSetChanged();
				
				dismissDialog(R.id.calc_script_sums_dlg);
			};
			
			protected void onPreExecute() {
				showDialog(R.id.calc_script_sums_dlg);
			};
		}.execute((Void[])null);
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.calc_script_sums_dlg)
			return calcScriptSumsDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog calcScriptSumsDlg() {
		ProgressDialog result = new ProgressDialog(this);
		result.setMessage(getString(R.string.calc_script_sum_in_progress));
		result.setCancelable(false);
		
		return result;
	}
}

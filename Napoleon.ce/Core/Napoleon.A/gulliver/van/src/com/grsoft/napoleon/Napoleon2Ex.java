package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

public class Napoleon2Ex extends NapoleonEx {
	
	public void calcScriptSums() {
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				DocList list = new DocList(ScriptImpl.class, null, null);
				
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
				if (mainOrgsAdapter != null && mainOrgsAdapter instanceof Refresh)
					((Refresh)mainOrgsAdapter).refresh();
				
				if (orgFoldersAdapter != null && orgFoldersAdapter instanceof Refresh)
					((Refresh)orgFoldersAdapter).refresh();
				
				BaseAdapter adapter = ((BaseAdapter)lvMainOrgs.getAdapter());
				
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

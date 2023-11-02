package com.grsoft.napoleon;

import com.grsoft.script.ScriptEdit;
import android.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;


public class ScriptEditEx extends ScriptEdit {
	

	@Override
	protected boolean keyBackPressed() {
		if(doc.isEditable() && doc.isContainsItem() && doc.isComplete()){
			DialogFragment dlg = new AskToSendDlg();
			dlg.show(getFragmentManager(), dlg.getClass().getCanonicalName());
			return false;
		}
		
		return super.keyBackPressed();
	}
	
	@Override
	public void send() {
		if(doc.isExported())
			Toast.makeText(this, R.string.order_sended, Toast.LENGTH_SHORT).show();
		else
			super.send();
	}
	
	public int getItemViewID(){	return R.layout.script_row_ex; }
	
	protected OnClickListener skipClick = new OnClickListener() {
		
		
		@Override
		public void onClick(View v) {
			doc.setSkipped((Integer)v.getTag());
			BaseAdapter adapter = (BaseAdapter)listView.getAdapter();
			if(adapter != null)
				adapter.notifyDataSetChanged();
		}
	};
	
	protected ItemsAdapter createItemsAdapter() { return new ItemsAdapter(){
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View result = super.getView(position, convertView, parent);
			
			View view = result.findViewById(R.id.btnSkip);
			view.setVisibility(View.INVISIBLE);
			
			if (def.getData().items.get(position).canSkip() && doc.IsEnabled(position, def.getData())) {
				view.setVisibility(View.VISIBLE);
				view.setTag(position);
				view.setOnClickListener(skipClick);
			}
				
			return result;
		}
	}; }
}

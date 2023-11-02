package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Terminal;
import com.grsoft.napoleon.documents.IncassDoc;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class IncassEditEx extends IncassEdit {
	
	private boolean noncash;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Spinner spTerminal = (Spinner)findViewById(R.id.spTerminal);
		
		noncash = ((IncassEx)doc.getData()).noncash == 1;
		findViewById(R.id.termialLayout).setVisibility(noncash ? View.VISIBLE : View.GONE);
		
		if (noncash) {
			spTerminal.setAdapter(new TerminalAdapter(this));
			IncassEx ie = (IncassEx) doc.getData();
			String tid = ie.tid;
			
			if (tid.length() > 0) {
				for(int i = 0; i < spTerminal.getCount(); i++) {
					Terminal t = (Terminal) spTerminal.getItemAtPosition(i);
					
					if(t != null && t.id.equals(tid)) {
						spTerminal.setSelection(i, true);
						break;
					}
				}
			}
			
			spTerminal.setEnabled(doc.isEditable());
		}
	}
	@Override protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	public void onBackPressed() {
		if( !Features.OK_BTN_INCASS )
			save();
		else if( doc.isEditable() ) {
			doc.delete();
			IncassDoc.instance().refreshDocSum(doc.getId());
		}
		finish();
	}

	@Override
	public void postSendExecute(boolean result) {
		super.postSendExecute(result);
		finish();
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();
		
		if(noncash) {
			Terminal trm = (Terminal) ((Spinner)findViewById(R.id.spTerminal)).getSelectedItem();
			
			if (trm != null) {
				IncassEx i = (IncassEx) doc.getData();
				i.tid = trm.id;
				i.tnm = trm.number;
			}
		}
	}
	
	private static class TerminalAdapter implements SpinnerAdapter
	{
		Context context = null;
		
		final List<Terminal> data = new ArrayList<Terminal>();
		
		{
			DataTraveler.travel(Terminal.class, new DataTraveler.Travel<Terminal>(true){

				@Override
				public boolean travel(DataTraveler<Terminal> item) {
					data.add(item.data);
					return true;
				}}, null);
		}
		
		public TerminalAdapter(Context context) {
			this.context = context;
		}
		
		@Override public void unregisterDataSetObserver(DataSetObserver observer) {}
		
		@Override public void registerDataSetObserver(DataSetObserver observer) {}
		
		@Override public boolean isEmpty() { return false; }
		
		@Override
		public boolean hasStableIds() { return false; }
		
		@Override public int getViewTypeCount() { return 0;	}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getDropDownView(position, convertView, parent);
		}
		
		@Override
		public int getItemViewType(int position) { return 0; }
		
		@Override
		public long getItemId(int position) { return 0;	}
		
		@Override public Object getItem(int position) { return data.get(position); }
		
		@Override
		public int getCount() {	return data.size(); }
		
		@Override
		public View getDropDownView(int position, View cnv, ViewGroup parent) {
			if (cnv == null)
				cnv = View.inflate(context,  android.R.layout.simple_spinner_item, null);
			
			Terminal cause = (Terminal)getItem(position);
			((TextView) cnv).setText(String.format("%s, %s", cause.id, cause.number));
			return cnv;
		}
	}
}

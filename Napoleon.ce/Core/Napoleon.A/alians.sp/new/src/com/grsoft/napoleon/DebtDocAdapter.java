package com.grsoft.napoleon;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;

import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocumentsAdapter;

public class DebtDocAdapter extends DocumentsAdapter{
	private int selected = 0;
	
	public DebtDocAdapter(Context context, String id){
		super(context, DebtDocEx.instance(), id, "date",
			R.layout.deb_doc_list_row);
	}
	
	public void select(int i) {
		selected += i;
		DocumentsEx activity = (DocumentsEx)context;
		activity.updateSelected(selected);
	}
	
	@Override
	public OnItemClickListener clickListner() {
		return new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckBox cbSel = (CheckBox) view.findViewById(R.id.cbSel);
				cbSel.setChecked(!cbSel.isChecked());
			}
		};
	}
}
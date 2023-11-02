package com.grsoft.napoleon;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.util.view.dialog_helper.KeyValue;


abstract public class SelFrg extends DialogFragment {
	private EditText edFind;
	private FindTextWatcher textWatcher;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.seldlg, null, false);
		ListView list = (ListView) result.findViewById(R.id.list);
		list.setDividerHeight(0);
		
		ListAdapter a = createAdapter();
		initTitle();
		
		if(a != null)
			list.setAdapter(a);
		
		edFind = (EditText) result.findViewById(R.id.edFind);
		textWatcher = new FindTextWatcher(edFind, list);
		edFind.addTextChangedListener(textWatcher);
		
		View del = result.findViewById(R.id.btnDelFind);
		del.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { edFind.setText("");	}});
		
		list.setOnItemClickListener(selectItem());
		
		return result;
	}
	
	abstract protected ListAdapter createAdapter();
	abstract protected void initTitle();

	private OnItemClickListener selectItem() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				KeyValue kv = (KeyValue) parent.getItemAtPosition(position);
				onItemSelect(kv);
				dismiss();
			}};
	}
	
	abstract protected void onItemSelect(KeyValue kv);
}

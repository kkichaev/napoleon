package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import com.grsoft.napoleon.documents.Document;


public class DocListEx extends DocList {
	private final static String DESC = "desc";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		unregisterForContextMenu(lvDocs);
		
		lvDocs.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				String descr = ((Document<?>)parent.getItemAtPosition(position)).getDescription(view.getContext());
				Bundle bundle = new Bundle();
				bundle.putString(DESC, descr);
				showDialog(R.id.description_dlg, bundle);
				return true;
			}});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.description_dlg:
			return createDescDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		switch(id){
		case R.id.description_dlg:
			prepareDescDlg(dialog, args);
			break;
		default:
			super.onPrepareDialog(id, dialog, args);
		}
	}
	
	private void prepareDescDlg(Dialog dialog, Bundle args) {
		if(args != null){
			AlertDialog adlg = (AlertDialog) dialog;
			adlg.setMessage(Html.fromHtml(args.getString(DESC)));
		}
	}

	private Dialog createDescDlg() {
		AlertDialog.Builder result = new AlertDialog.Builder(this);
		result.setTitle(R.string.doc_param);
		result.setMessage("");
		result.setPositiveButton(R.string.ok, null);
		return result.create();
	}
	
	
}

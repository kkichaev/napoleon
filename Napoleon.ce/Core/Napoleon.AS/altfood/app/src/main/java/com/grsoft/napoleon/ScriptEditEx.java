package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.RejectCause;
import com.grsoft.dataobjects.ScriptItemEx;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDefItem;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.Adapter;


public class ScriptEditEx extends ScriptEdit {
	private static String ROW_POS = "row_pos"; 
	@Override
	protected void skipItem(int pos) {
		Adapter adapter = listView.getAdapter();
		if(adapter != null)
		{
			ScriptDefItem sdi = (ScriptDefItem) adapter.getItem(pos);
			if (sdi.canSkip() && sdi.curType.equals(OrderDoc.instance().getObjectName())){
				Bundle args = new Bundle();
				args.putInt(ROW_POS, pos);
				showDialog(R.id.rejectdlg, args);
			}else
				super.skipItem(pos);
		}
	}
	
	private void skipBaseItem(int pos){
		super.skipItem(pos);
	}
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if(id == R.id.rejectdlg)
			return createRejectDlg(args.getInt(ROW_POS));
		return super.onCreateDialog(id, args);
	}

	private Dialog createRejectDlg(final int pos) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.rejectcause);
		
		final List<String> items = new ArrayList<String>();
		DataTraveler.travel(RejectCause.class, new DataTraveler.Travel<RejectCause>(true) {
			@Override public boolean travel(DataTraveler<RejectCause> item) {
				items.add(item.data.text);
				return true;
			}}, null);
		
		builder.setItems(items.toArray(new String[items.size()]), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (doc.getData().items.size() > pos){
					ScriptItemEx i = (ScriptItemEx) doc.getData().items.get(pos);
					i.cause = items.get(which);
					doc.write();
					skipBaseItem(pos);
				}
			}
		});
		
		return builder.create();
	}
}

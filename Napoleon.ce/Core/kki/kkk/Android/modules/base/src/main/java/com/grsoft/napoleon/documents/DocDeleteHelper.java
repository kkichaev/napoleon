package com.grsoft.napoleon.documents;

import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.grsoft.database.DataBaseManager;
import com.grsoft.napoleon.R;
import com.grsoft.view.Refreshable;

public class DocDeleteHelper {
	static CreatableDocument<?> doc;
	static Context content;
	
	public static void delete(CreatableDocument<?> d, Context ctx) {
		
		doc = d;
		content = ctx;
		
		AlertDialog delConfirm = new AlertDialog.Builder(ctx).create();
		delConfirm.setTitle(ctx.getString(R.string.confirm));
		delConfirm.setMessage((doc.isExported()) ? ctx.getString(R.string.ask_to_delete_doc) : ctx.getString(R.string.doc_not_sent));
		delConfirm.setButton(ctx.getString(R.string.yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if( doc.delete()){
					DocType.getCurDoc().refreshDocSum(doc.getId());
					if (content != null && content instanceof Refreshable )
						((Refreshable)content).refreshContent();
				}
			}
		});

		delConfirm.setButton2(ctx.getString(R.string.no), (DialogInterface.OnClickListener)null);

		delConfirm.show();
	}
	
	public static boolean deleteTill(Date date, String table, String field) {
		boolean res = true;
		
		String stmt = "DELETE FROM '" + table + "' WHERE [" + field + "] < " + Long.toString(date.getTime());
		try {
			DataBaseManager.getDataBase().execSQL(stmt);
		} catch(Exception e) {
			res = false;
		}
		
		return res;
	}
}

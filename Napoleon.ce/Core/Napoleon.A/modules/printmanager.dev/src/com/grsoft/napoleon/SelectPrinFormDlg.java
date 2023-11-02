package com.grsoft.napoleon;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.NPrinter;

public class SelectPrinFormDlg {
	protected Context context;
	protected int waitDlgid;
	
	String title;
	
	protected Runnable postExec;
	protected DataSource dataSource;
	
	public SelectPrinFormDlg(Context context, int waitDlgid){
		this.context = context;
		this.waitDlgid = waitDlgid;
	}
	
	public void setTitle(String newTitle) { title = newTitle; }
	
	public Dialog createDialog(final String captions[]){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if( title != null )
			builder.setTitle(title);
		else
			builder.setTitle(R.string.print_docs);
		builder.setItems(captions, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (dataSource != null)
					onItemSelect(captions, which);
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}
	
	protected void onItemSelect(final String[] captions, int which) {
		createPrintForm((Activity)context, dataSource, waitDlgid, getFormName(captions[which]), postExec);
	}
	
	protected String getFormName(String baseName) { return baseName; } 
	
	public static void createPrintForm(final Activity activity, final DataSource dataSource, final int dialogid, String reportName, final Runnable postExec) {
		new AsyncTask<String, Void, File>(){
			protected void onPreExecute() { activity.showDialog(dialogid); };
			
			@Override
			protected File doInBackground(String... params) {
				File result = null;
				
				try {
					if (params.length > 0)
						result = NPrinter.print(activity, params[0], dataSource);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return result;
			}
			
			protected void onPostExecute(File output) {
				activity.dismissDialog(dialogid);
				if(output != null){ 
					NPrinter.sendPrintTask(activity, output);

					if (postExec != null)
						postExec.run();
				}
			};
		}.execute(reportName);
	}
	
	public static Dialog createWaitDlg(Context context){
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Подождите...");
		progressDialog.setMessage("Формирование печатного документа...");
		return progressDialog;
	}

	public void setPostExec(Runnable runnable) {
		postExec = runnable;
	}
	
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource; 
	}
}

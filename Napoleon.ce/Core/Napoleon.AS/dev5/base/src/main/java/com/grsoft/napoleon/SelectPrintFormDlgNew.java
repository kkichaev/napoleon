package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.napoleon.SelectPrintFormDlgNew.Adapter.Data;
import com.grsoft.napoleon.modules.print.GraphicPrinter;


public class SelectPrintFormDlgNew extends SelectPrinFormDlg {
	private static final String PREF = "com.grsoft.napoleon.SelectPrintFormDlgNew.PREF";
	
	public SelectPrintFormDlgNew(Context context, int waitDlgid) {
		super(context, waitDlgid);
	}
	
	public Dialog createDialog(final String captions[]){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if( title != null )
			builder.setTitle(title);
		else
			builder.setTitle(R.string.print_docs_copies);
		
		ListView list = new ListView(context);
		list.setId(R.id.list);
		list.setAdapter(new Adapter(captions));
		builder.setView(list);
		
		builder.setPositiveButton(R.string.ok, okclick);
		builder.setNegativeButton(R.string.cancel, null);
		
		return builder.create();
	}
	
	protected void addFormToPrint(StringBuilder sb, int count, String text) {
		for(int c = 0; c < count; c++ ){
			if(sb.length() > 0)
				sb.append(GraphicPrinter.FORM_DELIM_SIM);
			
			sb.append(text);
		}
	}

	protected void beforePrint(StringBuilder sb) {
		
	}
	
	protected void doPrint(DialogInterface dialog) {
		StringBuilder sb = new StringBuilder();
		
		ListView list = (ListView) ((Dialog)dialog).findViewById(R.id.list);
		if(list != null){
			Adapter a = (Adapter) list.getAdapter();
			SharedPreferences pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
			Editor ed = pref.edit();
			
			for(int i=0; i < a.getCount(); i++){
				Data d = (Data) a.getItem(i);
				ed.putInt(d.text, d.val);
				
				addFormToPrint(sb, d.val, d.text);
//				for(int c = 0; c < d.val; c++ ){
//					if(sb.length() > 0)
//						sb.append(GraphicPrinter.FORM_DELIM_SIM);
//					
//					sb.append(d.text);
//				}
					
			}
			
			ed.commit();
			beforePrint(sb);
			createPrintForm((Activity)context, dataSource, waitDlgid, sb.toString(), postExec);
		}
	}
	
	DialogInterface.OnClickListener okclick = new DialogInterface.OnClickListener() {
		@Override public void onClick(DialogInterface dialog, int which) { doPrint(dialog); }
	};
	
	OnClickListener incclick = new View.OnClickListener() {
		final static int MAX_VAL = 5;
		@Override
		public void onClick(View v) {
			Adapter.Data d = (Data) v.getTag();
			
			if(d.val < MAX_VAL){
				d.val += 1;
				d.invalidate();
			}
		}
	};
	
	OnClickListener decclick = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Adapter.Data d = (Data) v.getTag();
				
				if(d.val > 0){
					d.val -= 1;
					d.invalidate();
				}
			}
		};
	
	class Adapter extends BaseAdapter{
		class Data{
			String text = "";
			int val = 0;
			
			public void invalidate(){notifyDataSetChanged();};
		}
		
		List<Data> data = new ArrayList<Data>();
		
		public Adapter(String[] arr){
			SharedPreferences pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
			
			for(String s : arr){
				Data d = new Data();
				d.text = s;
				d.val = pref.getInt(s, 0);
				
				data.add(d);
			}
		}
		
		@Override
		public int getCount() { return data.size(); }

		@Override
		public Object getItem(int position) { return data.get(position); }

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = View.inflate(context, R.layout.selprintrow, null);
			
			Data d = (Data) getItem(position);
			
			TextView tv = (TextView) convertView.findViewById(R.id.tvName);
			tv.setText(d.text);
			tv = (TextView) convertView.findViewById(R.id.tvVal);
			tv.setText(Integer.toString(d.val));
			View btn = convertView.findViewById(R.id.btnInc);
			btn.setTag(d);
			btn.setOnClickListener(incclick);
			
			btn = convertView.findViewById(R.id.btnDec);
			btn.setTag(d);
			btn.setOnClickListener(decclick);
			
			return convertView;
		}
		
	}

}

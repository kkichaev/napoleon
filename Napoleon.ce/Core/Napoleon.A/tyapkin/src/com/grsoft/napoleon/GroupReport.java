package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.Selector;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.DialogOwner;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class GroupReport extends WarehouseNew implements Selector, DialogOwner{
	private final static String GROUP_REPORT_FILTER = "group_report_filter";
	private DatePeriod period;
	private ImageButton btnDocFilter;
	private Dialog activeDialog;
	protected View btnFilter;
	protected static final int DLG_FILTER_SELECT = 0;
	private View dialogView;
	private static final int BEGIN_DATE_CODE = 10;
	private static final int END_DATE_CODE = 11;
	
	public static void open(Context context) {
		Intent i = new Intent(context, GroupReport.class);
		context.startActivity(i);
	}
	
	protected FoldersAdapter createAdapterInstance() {
		return new FoldersAdapter(this) {
			@Override
			protected void fillPriceIds(SQLiteDatabase database) {
			}
			
			@Override
			protected void deleteEmptyNodes(FolderTreeNode node) {
			}
		};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		period = periodForDalySales();
		super.onCreate(savedInstanceState);
		
		btnDocFilter = (ImageButton)findViewById(R.id.btnDocFilter);
		btnDocFilter.setOnClickListener(createDocListFilter());
		
		btnFilter = (ImageButton)findViewById(R.id.btnFilter);
		btnFilter.setOnClickListener(new FilterOnClickListener());
		
		DocType curType = DocType.getCurDoc();
		
		if (curType != SalesDoc.instance() || curType != OrderDoc.instance())
			curType = SalesDoc.instance();
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		cfg.getValue(sb, GROUP_REPORT_FILTER);
		
		btnFilter.setEnabled(sb.toString().trim().equals("1"));
		adjustViewForDocType(curType);
	}
	
	private OnClickListener createDocListFilter() {
		List<DocTypeBase> filter = new ArrayList<DocTypeBase>();
		filter.add(SalesDoc.instance());
		filter.add(OrderDoc.instance());
		
		return new DocFilterOnClickListener(this, true, false, filter);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.groupreport;
	}

	@Override
	public void selectedType(DocType newDocType) {
		DocType docType = (DocType) DocType.getCurDoc();
		if( newDocType != null && (docType == null  || newDocType.equals(docType) == false) )
			adjustViewForDocType((DocType) newDocType);
	}

	@Override
	public void setActiveDialog(Dialog dlg) {
		activeDialog = dlg;
	}
	
	protected DocType getSalesDoc() {
		return DocType.getCurDoc();
	}
	
	protected void adjustViewForDocType(DocType docType)
	{
		DocType.setCurDoc(docType);
		loadDailySales();
		btnDocFilter.setImageResource(docType.getResurce2Id());
		adapter.notifyDataSetChanged();
		
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
	}
	
	class FilterOnClickListener extends OnClickListenerToNotify{
		@Override
		public void onClick(View v) {
			super.onClick(v);
			filter();
		}
	}
	
	public void filter() {
		showDialog(DLG_FILTER_SELECT);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DLG_FILTER_SELECT:
			return createDlgFilter();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	protected int getFilterLayout() {
		return R.layout.group_date_selection;
	}
	
	protected Dialog createDlgFilter() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		dialogView = View.inflate(this, getFilterLayout(), null);
		((TextView)dialogView.findViewById(R.id.tvBegin)).setText(getString(R.string.begin) + ":");
		((TextView)dialogView.findViewById(R.id.tvEnd)).setText(getString(R.string.end) + ":");
		
		TextView dv = (TextView) dialogView.findViewById(R.id.tvDateBegin);
		dv.setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) {
				setDate(BEGIN_DATE_CODE, period.begin); 
			}
		});
		
		dv.setText(Util.simpleDateFormat.format(period.begin));
		
		dv = (TextView) dialogView.findViewById(R.id.tvDateEnd);
		dv.setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { 
				setDate(END_DATE_CODE, period.end); 
			}
		});
		
		dv.setText(Util.simpleDateFormat.format(period.end));
		
		builder.setView(dialogView);
		builder.setPositiveButton(R.string.ok, setFilter);
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
	
	void setDate(int dateType, Date date) {
		Intent i = new Intent(this, CalendarActivity.class);
		i.putExtra(ExtrasConst.DATE_TAG, date.getTime());
		startActivityForResult(i, dateType);
	}
	
	private DialogInterface.OnClickListener setFilter = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) { filterClick(dialog); }
	};

	protected void filterClick(DialogInterface dialog) {
		loadDailySales();
		adapter.notifyDataSetChanged();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		
		Date curDate = new Date();
		if( data != null ) {
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());

			Date newDate = new Date();
			int id = R.id.tvDateBegin;
			DatePeriod dp = period;
			
			if( requestCode == BEGIN_DATE_CODE ) {
				dp.begin = new Date(ct);
				newDate = dp.begin;
				id = R.id.tvDateBegin;
			}
			else if( requestCode == END_DATE_CODE) {
				ct += (24 * 3600 - 1) * 1000;
				dp.end = new Date(ct);
				newDate = dp.end;
				id = R.id.tvDateEnd;
			}
			
			TextView dv = (TextView) dialogView.findViewById(id);
			dv.setText(Util.simpleDateFormat.format(newDate));
		}
	}
	
	@Override
	protected void updateTotalSum() {
	}
}

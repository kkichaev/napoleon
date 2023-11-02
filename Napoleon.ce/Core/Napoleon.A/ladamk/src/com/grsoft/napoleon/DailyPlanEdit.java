package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.DailyPlan;
import com.grsoft.dataobjects.DailyPlanItem;
import com.grsoft.dataobjects.DailySalesData;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.FolderColor;
import com.grsoft.dataobjects.FolderSalesData;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DailyPlanImpl;
import com.grsoft.dataobjects.impl.FolderColorImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DailyPlanDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FolderTree;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DailyPlanEdit extends BaseActivity {
	
	protected static final int DIALOG_DATE_PICKER_ID = 10;
	private static final int REPLACE_DOC_DLG = 11;
	
	FolderColorImpl fcolor = new FolderColorImpl();
	DailyPlanImpl doc = new DailyPlanImpl();
	long replacedDoc = -1;
	long planDate = -1;
	FolderSalesData salesData;
	
	Adapter adapter;
	
	public static void open(Context context, DailyPlanImpl doc) {
		Intent i = new Intent(context, DailyPlanEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.daily_plan_edit);
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(rid);
		
		OrgImpl oi = new OrgImpl();
		Org o = oi.getData();
		o.id = doc.getId();
		oi.read();
		oi.close();
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvOrg);
		tv.setText(o.name);
		
		findViewById(R.id.tvPlanDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, doc.getDate().getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});
		
		refreshPlanDate();
		
		adapter = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(doc.isEditable() == false)
					return;
				
				final ItemData id = (ItemData) arg0.getItemAtPosition(arg2);
				InputNumberDlg.open(DailyPlanEdit.this, new InputNumber() {
					@Override public int getValue() { return id.weight; }
					
					@Override
					public void applayInput(int value, Object... params) {
						id.weight = value;
						adapter.notifyDataSetChanged();
					}
				}, Consts.WEIGHT_SCALE, true,  "Введите количество");
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		saveDoc();
		super.onBackPressed();
	}
	
	private void saveDoc() {
		if(doc.isEditable() == false)
			return;
		
		DailyPlan dp = doc.getData();
		dp.items.clear();
		
		for(int i=0; i<adapter.getCount(); i++) {
			ItemData id = (ItemData) adapter.getItem(i);
			if(id.weight != 0) {
				DailyPlanItem item = new DailyPlanItem();
				item.id = id.folder.fid;
				item.weight = id.weight;
				
				dp.items.add(item);
			}
		}
		
		if(dp.items.size() > 0) {
			doc.write();
		} else {
			doc.delete();
		}
		
		DailyPlanDoc.instance().refreshDocSum(doc.getId());
	}

	@Override
	protected void onDestroy() {
		doc.close();
		fcolor.close();
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = Util.getDate();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newPlanDate = new Date(ct);
			
			if(ct < curDate.getTime()) {
				Toast.makeText(this, "Дата плана меньше текущей", Toast.LENGTH_SHORT).show();
				return;
			}
			
			long created = DailyPlanImpl.getPlan(doc.getId(), newPlanDate);
			if(created != ExtrasConst.INVALID_ROWID && created != doc.getRowid()) {
				replacedDoc = created;
				planDate = ct;
				showDialog(REPLACE_DOC_DLG);
				return;
			}
			
			doc.getData().date = new Date(ct);
			refreshPlanDate();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == REPLACE_DOC_DLG) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Вопрос");
			b.setMessage("Найден другой план на этот день. Заменить план?");
			b.setNegativeButton(android.R.string.no, null);
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					DailyPlanImpl rmv = new DailyPlanImpl();
					rmv.read(replacedDoc);
					rmv.delete();
					rmv.close();

					doc.getData().date = new Date(planDate);
					refreshPlanDate();
					adapter.notifyDataSetChanged();
					arg0.dismiss();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	private void refreshPlanDate() {
		TextView tv;
		tv = (TextView)findViewById(R.id.tvPlanDate);
		
		String text = "план на: <u><font color='blue'>" + Util.simpleDateFormat.format(doc.getDate()) + "</font></u>";
		tv.setText(Html.fromHtml(text));

		salesData = null;
		DailySalesData dsd = DailySalesData.load(doc.getId(), doc.getDate());
		for(Date dk : dsd.keySet()) {
			salesData = dsd.get(dk);
			break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	class Adapter extends BaseAdapter {

		List<ItemData> items = new ArrayList<ItemData>();
		
		public Adapter() {
			FolderTree ft = new FolderTree();
			ft.load();
			
			int level = -1;
			for(int i=0; i<ft.size(); i++) {
				Folder f = ft.get(i);
				if(i == 0) {
					items.add(new ItemData(f));
					level = f.level;
					continue;
				}
				if(f.level <= level)
					items.add(new ItemData(f));				
			}
			
			refresh();
		}
		
		public void refresh() {
			for(DailyPlanItem i : doc.getData().items) {
				for(ItemData id : items) {
					if(id.folder.fid.equals(i.id)) {
						id.weight = i.weight;
						break;
					}
				}
			}
		}
		
		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if(view == null)
				view = View.inflate(DailyPlanEdit.this, R.layout.daily_plan_row, null);
			
			ItemData id = (ItemData) getItem(pos);
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(id.folder.name);

			int color = Color.LTGRAY;
			FolderColor fc = fcolor.getData();
			fc.id = id.folder.fid;
			if(fcolor.read() && fc.color != 0)
				color = fc.color;
			
			view.setBackgroundColor(Util.GrServerColorToSystem(color));
			tv = (TextView)view.findViewById(R.id.tvQty);
			String text = "";
			if(id.weight > 0)
				text = Util.IntToScaleStr(id.weight, Consts.WEIGHT_SCALE);
			tv.setText(text);
			
			text = "";
			if(id.weight > 0 && salesData != null) {
				long w = salesData.get(id.folder);
				if(w != 0)
					text = Util.IntToScaleStr(w, Consts.WEIGHT_SCALE);
			}
			tv = (TextView)view.findViewById(R.id.tvFactQty);
			tv.setText(text);
			return view;
		}
	}
}

class ItemData {
	public Folder folder;
	public int weight = 0;
	
	public ItemData(Folder f) {
		folder = f;
	}
}

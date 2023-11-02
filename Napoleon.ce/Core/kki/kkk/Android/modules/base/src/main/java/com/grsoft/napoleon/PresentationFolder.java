package com.grsoft.napoleon;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class PresentationFolder extends PresentationFolderW {
	protected static final String SHOW_FOLDERS = "show_folders";
	protected static final String SHOW_PRICE_NAMES = "show_price_names";
	
	@Override
	protected void initAdapter(FoldersAdapter adapter) {
		SharedPreferences pref = getSharedPreferences(SETTING, Context.MODE_PRIVATE);
		adapter.setExpanded(!pref.getBoolean(SHOW_FOLDERS, true));
	}
	
	@Override
	protected int getSettingLayoutId() { return R.layout.pres_setting_dialog_new;}
	
	@Override
	protected void postPrepareSettingDlg(Dialog dialog) {
		SharedPreferences pref = getSharedPreferences(SETTING, Context.MODE_PRIVATE);
		CheckBox cb = (CheckBox) dialog.findViewById(R.id.cbShowFolders);
		cb.setChecked(pref.getBoolean(SHOW_FOLDERS, true));
		cb = (CheckBox) dialog.findViewById(R.id.cbShowPriceNames);
		cb.setChecked(pref.getBoolean(SHOW_PRICE_NAMES, true));
	}
	
	@Override
	protected void childEditSetting(Editor edit, Dialog dialog) {
		CheckBox cb = (CheckBox) dialog.findViewById(R.id.cbShowFolders);
		edit.putBoolean(SHOW_FOLDERS, cb.isChecked());
		
		boolean isExpand = !cb.isChecked();
		
		if(adapter.isExpanded() != isExpand){
			adapter.setExpanded(isExpand);
			adapter.buildSet();
		}
		
		cb = (CheckBox) dialog.findViewById(R.id.cbShowPriceNames);
		edit.putBoolean(SHOW_PRICE_NAMES , cb.isChecked());
	}

	@Override
	public boolean isPriceExpand() {
		SharedPreferences pref = getSharedPreferences(SETTING, Context.MODE_PRIVATE);
		return !pref.getBoolean(SHOW_FOLDERS, true);
	}

	@Override
	protected void setPriceText(TextView textView, Price price) {
		textView.setText("");
		SharedPreferences pref = getSharedPreferences(SETTING, Context.MODE_PRIVATE);
		if (pref.getBoolean(SHOW_PRICE_NAMES, true)) {
			super.setPriceText(textView, price);
		}
	}
	
	protected void adapterInit() {
		SharedPreferences pref = getSharedPreferences(Warehouse.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		if (pref.getBoolean(ZERO_FILTER, false))
			adapter.putFilter(createZeroPositionFilter());
	}
	
	protected void updateForZeroFilter() {
		boolean zeroFilter = false;

		if (adapter.getFilter(ZeroPositionFilter.NAME) == null) {
			adapter.putFilter(createZeroPositionFilter());
			zeroFilter = true;
		} else
			adapter.deleteFilter(ZeroPositionFilter.NAME);

		SharedPreferences pref = getSharedPreferences(Warehouse.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putBoolean(ZERO_FILTER, zeroFilter);
		ed.commit();

		adapter.buildSet();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.presentation_opt_menuex, menu);
		return true;
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View view = super.getPriceView(node, convertView);
		
		TextView tv = (TextView) view.findViewById(R.id.tvInfo);
		int cost = CostStrategy.getInstance((Class<? extends Document<?>>) doc.getClass()).getItemCost(price.getData(), (Document<?>) doc);
		int qty = getWhQty((Itemsable)doc, price.getData());
		
		tv.setText(Html.fromHtml(getString(R.string.costinfo, 
			Util.IntToScaleStr(qty, Consts.QTY_SCALE),
			Util.IntToScaleStr(cost, Consts.SUM_SCALE))));
		
		tv = (TextView)view.findViewById(R.id.tvItem);
		tv.setBackgroundColor(Color.WHITE);
		
		return view;
	}
	
	int getWhQty(Itemsable id, Price p) {
		int qty = id.getItemValue(p);
		if( Features.QTY_IN_PACK_IN_DOCS &&((CfgNplW)ConfigManager.getConfig()).isPackView )
			qty = (int)((long)qty * Consts.QTY_SCALE / p.qtyInPack);
		
		return qty;
	}
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		View view = super.getFolderView(node, convertView);
		
		TextView tv = (TextView) view.findViewById(R.id.tvInfo);
		tv.setText("");
		
		tv = (TextView)view.findViewById(R.id.tvItem);
		tv.setBackgroundColor(Color.WHITE);
		
		return view;
	}
	
	@Override
	public boolean useInterlaceBackground() {
		return false;
	}
}

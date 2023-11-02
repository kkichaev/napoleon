package com.grsoft.napoleon;

import java.util.HashSet;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrice;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class WarehouseEx extends Warehouse {
	private static final String SUMMERTIME_MODE = "summertimie_mode";

	boolean useCheckBox = false, summerTimeMode = false;
	
	HashSet<Integer> lastOrderFolders = new HashSet<Integer>();
	HashSet<Integer> lastBuyingFolders = new HashSet<Integer>();
	HashSet<String> orgAssortiment = new HashSet<String>();
	
	FolderImpl folderImpl = new FolderImpl();
	
	public static void openSummerTime(Context context,  Document<?> doc) {
		Intent i = new Intent(context, WarehouseEx.class);
		
		if( doc != null ) {
			i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
			i.putExtra(ExtrasConst.ORG_ID_STR, doc.getId());
			i.putExtra(ExtrasConst.EDIT_MODE_STR, true);
			i.putExtra(SUMMERTIME_MODE, true);
		}
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		if( b != null )
			summerTimeMode = b.getBoolean(SUMMERTIME_MODE, false);
		
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SUMMERTIME_MODE, summerTimeMode);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		folderImpl.close();
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.resetCache();
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		
		if( summerTimeMode ) {
			ret.putFilter(new FilterPrice());
			useCheckBox = true;			
		} else {
			useCheckBox = false;
			boolean summertime = false;
			StringBuilder sb = new StringBuilder();
			ConfigImpl ci = new ConfigImpl();
			if( ci.getValue(sb, "ЛетнийВариант")) {
				int val = 0;
				try {
					val = Integer.parseInt(sb.toString());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if( val == 1 )
					summertime = true;
			}
			
			if( document instanceof OrderImplEx && !summertime ) {
				if( ((OrderImplEx)document).haveUnsettedItems() ) {
					ret.putFilter(new FilterPrice());
					useCheckBox = true;
				}
			}
		}
		return ret;
	}
	
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }
	
	OnClickListener checkItem = new OnClickListener() {
		@Override
		public void onClick(View view) {
			((OrderImplEx)document).check((String)view.getTag());
			document.write();
			notifyDataSetChanged();
		}
	};
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View view = super.getPriceView(node, convertView); 
		CheckBox cb = (CheckBox)view.findViewById(R.id.cbCheckItem);
		if( useCheckBox ) {
			cb.setVisibility(View.VISIBLE);
			String id = node.getId();
			cb.setChecked(((OrderImplEx)document).isChecked(id));
			cb.setTag(id);
			cb.setOnClickListener(checkItem);
		} else {
			cb.setVisibility(View.GONE);
		}
		return view;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
				
		lastOrderFolders.clear();
		lastBuyingFolders.clear();
		orgAssortiment.clear();
		if( document instanceof OrderImpl ) {
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			if ( ((OrderImpl)document).getData().items != null ) {
				for(OrderItem item : ((OrderImpl)document).getData().items ) {
					p.id = item.id;
					if( pi.read() )
						lastOrderFolders.add(p.folderID);
				}
			}
			OrgImpl oi = new OrgImpl();
			OrgEx o = (OrgEx)oi.getData();
			o.id = document.getId();
			oi.read();
			oi.close();
			
			for(OrgPrice pid : o.price) {
				orgAssortiment.add(pid.id);
				p.id = pid.id;
				if( pi.read() )
					lastBuyingFolders.add(p.folderID);
			}
			pi.close();		
		}
	}
	
	@Override
	protected int getDefaultColor(Price p) {
		return orgAssortiment.contains(p.id) ? Color.RED : super.getDefaultColor(p);
	}
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		View v = super.getFolderView(node, convertView);
		
//		FolderEx fe = (FolderEx)folderImpl.getData();
//		fe.id = node.id;
//		folderImpl.read();

		TextView tvOrgName = (TextView) v.findViewById(R.id.tvItemSelectRowName);
		int color = (lastOrderFolders.contains(node.id)) ? Color.GREEN : 
			lastBuyingFolders.contains(node.id) ? Color.RED : 
			Color.BLACK;
		tvOrgName.setTextColor( color );
		
		return v;
	}
	
	class FilterPrice extends Filter {
		
		public static final String NAME = "FilterPrice";
		
		HashSet<String> contained;

		public FilterPrice() {
			super(NAME);
			contained = ((OrderImplEx)document).getPriceItems();
		}
		
		@Override public boolean inset(long priceRowID, String id) { return !useCheckBox || contained.contains(id);	}
	}
	
	@Override
	protected int calcCellWidth(DisplayMetrics metrics) {
		return metrics.widthPixels / 4 - 40;
	}
}

package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DiscountObj;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.DiscountImpl;
import com.grsoft.dataobjects.impl.FolderDiscountImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.CostStrategyEx;
import com.grsoft.napoleon.FolderTree;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class DiscountType extends DocType {
	static DiscountType doc = null;
	
	protected DiscountType() {
		super("Скидки на товары", DiscountImpl.class);
	}
	
	public static DocType instance() {
		if( doc == null )
			doc = new DiscountType();
		return doc;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.disc_doc;
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		
		TextView tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setVisibility(View.GONE);
		
		tv = (TextView)documentsView.findViewById(R.id.NameTitle);
		if( tv != null )
			tv.setText("Папка товаров");
		
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Cкидка,%");
	
		tv = (TextView) documentsView.findViewById(R.id.tvMainDocValColTitle);		
		if (tv != null)
			tv.setText("Cкидка,%");
	}
	
	@Override
	public void refreshDocSum() throws RuntimeException {
		OrgEx oe = new OrgEx();
		DbReader r = new DbReader();
		DbWriter w = new DbWriter();
		String table = DataObjectInfo.getInstance().getTableName(oe.getClass());

		OrgSum os = new OrgSum();
		os.type = this.name;

		boolean bdo = r.select(oe, table, null);
		while(bdo) {
			os.id = oe.id;
			os.sum = oe.discount;
			w.insertRecord(os);
			
			bdo = r.selectNext(oe);
		}
		r.close();
		w.close();
	}
	
	@Override
	public void refreshDocSum(String orgId) {
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = orgId;
		oi.read();
		oi.close();
		
		OrgSum os = new OrgSum();
		os.id = orgId;
		os.sum = oe.discount;
		os.type = this.name;
		
		DbWriter w = new DbWriter();
		w.insertRecord(os);
		w.close();
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		super.viewClosed(documentsView);
		
		TextView tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null ) 
			tv.setVisibility(View.VISIBLE);
		
		tv = (TextView)documentsView.findViewById(R.id.NameTitle);
		if( tv != null )
			tv.setText("Дата");
		
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Сумма");
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		TextView tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setVisibility(View.VISIBLE);
		tv.setText(Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false));
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setText(Html.fromHtml(doc.getDescription(view.getContext())));
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setVisibility(View.GONE);

		LayoutParams lps = tv.getLayoutParams();
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(lps);
		lp.weight = 1;
		tv.setLayoutParams(lp);
	}
	
	@Override
	public DocList docList(String orgId, String order, DatePeriod selection) {
		return new DiscountList(orgId);
	}

	@Override
	public void updateTotalSum(Activity activity, int sum, int weight, int count) {
		TextView tvTotalSum = (TextView) activity.findViewById(R.id.tvTotalSum);		
		if (tvTotalSum != null)
			tvTotalSum.setVisibility(View.GONE);
	}
}

class DiscountList extends DocList implements FilterAdapter {
	
	FolderTree folders, mainFolders, filtred;
	FolderDiscountImpl fdsc = new FolderDiscountImpl();
	OrgImpl org = new OrgImpl();
	
	public DiscountList(String id) {
		document = new DiscountImpl();
		
		if( id != null ) {
			org.getData().id = id;
			org.read();
			
			filtred = new FolderTree();
			
			FolderTree ref = CostStrategyEx.getFolders(); 
			mainFolders = new FolderTree(); 			
			for(OrgDiscount od : ((OrgEx)org.getData()).fldDsc) {
				for(Folder f : ref) {
					if( f.fid.equals(od.fid) ) {
						mainFolders.add(f);
						break;
					}
				}
			}
			folders = mainFolders;
			updateIds();
		}
	}

	protected void updateIds() {
		ids = new ArrayList<Long>();
		for(int idx = 0; idx<folders.size(); idx++)
			ids.add((long) idx);
	}
	
	@Override
	public Document<?> get(int index) {
		if( index >= folders.size() )
			return null;
		
		DiscountObj dobj = (DiscountObj)document.getData();
		Folder f = folders.get(index);
		dobj.folder = f.name;
		dobj.level = f.level;
		
//		int discount = CostStrategyEx.getDiscount((OrgEx)org.getData(), f.id);
//		if( CostStrategyEx.isNetUser() == false ) {
//			fdsc.getData().folderID = f.id;
//			if( fdsc.read() )
//				discount = fdsc.getData().discount;
//		}
		
		Integer fd = CostStrategyEx.findDiscount((OrgEx)org.getData(), f.fid);
		dobj.discount = (fd == null) ? 0 : fd;
		
		return document;
	}
	
	@Override
	public void close() {
		super.close();
		org.close();
		fdsc.close();
	}

	@Override
	public void applyFilter(String value) {		
		if( value.length() == 0 ) {
			if( folders != mainFolders )
				resetFilter();
			return;
		}
		
		filtred.clear();
		value = value.toUpperCase();
		for(Folder f : mainFolders) {
			if( f.name.toUpperCase().contains(value) )
				filtred.add(f);
		}
		folders = filtred;
		updateIds();
	}

	@Override
	public void resetFilter() {
		filtred.clear();
		folders = mainFolders;
		updateIds();
	}
}

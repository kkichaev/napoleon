package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.grsoft.dataobjects.PresentItem;
import com.grsoft.dataobjects.PresentList;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.AgentSalesPlanImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PresentListImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class PresentPage extends Fragment{
	
	//private GridView gvPresentation;
	public static String PAGE_ID = "page_id";

	boolean initing = true;
	OrderImpl doc;
	PresentListImpl list = new PresentListImpl();
	ArrayList<TableRowData> rows = new ArrayList<TableRowData>();
	int w, h;
	
	BitmapDrawable createBitmap(PresentItem item, int w, int h, Paint gpaint) {
		String path = item.path;
		BitmapFactory.Options opt = new BitmapFactory.Options();
		path = path.replace("\\", "/");

		File file = new File(((CfgNpl) ConfigManager.getConfig()).presentpath, path);
		if( file.canRead() == false )
			return null;

		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		Bitmap src = BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
		double coef = Math.min((double)w / src.getWidth(), (double)h / src.getHeight());
		if (coef != 1.0)
			src = Bitmap.createScaledBitmap(src, (int)(src.getWidth() * coef + 0.5), (int)(src.getHeight() * coef + 0.5), true);

		canvas.drawBitmap(src, 0 , 0, null);
		BitmapDrawable result = new BitmapDrawable(bitmap);
		result.setBounds(0, 0, w, h);
		return result;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.prezentpage, container, false);
		
		doc = (OrderImpl) OrderDoc.instance().create();
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int pageid = getArguments().getInt(PAGE_ID);
		if( DocType.getCurDoc() == OrderDoc.instance() ) {
			long docRowId = getArguments().getLong(ExtrasConst.DOC_ROW_ID_STR);
			doc.read(docRowId);
			doc.close();
		}
		
		TextView tv = (TextView)v.findViewById(R.id.tvPage);
		tv.setText(String.format("Лист %d", pageid));
		
		TableLayout table = (TableLayout)v.findViewById(R.id.tblItems);
		
		PresentList pl = list.getData();
		list.getData().id = pageid;
		list.read();
		list.close();

		Context context = v.getContext();
		
		w = displaymetrics.widthPixels / pl.col - 1;		
		h = displaymetrics.heightPixels / pl.row - 15;
		
		for( int i=0; i<pl.row; i++ ) {
			TableRowData trd = new TableRowData(pl.col, w, h, context); 
			table.addView(trd.row);
			rows.add(trd);
		}
				
		Paint gPaint = new Paint();
		for(PresentItem item : pl.items) {
			if( item.row < pl.row && item.col < pl.col ) {
				TableRowData trd = rows.get(item.row);
				TextView tvi = trd.cells.get(item.col);
				
				BitmapDrawable bmp = createBitmap(item, w, h, gPaint);
				if( bmp != null ) {
					tvi.setBackgroundDrawable(bmp);
					tvi.getBackground().setAlpha(150);
				}
			}
		}
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if( initing )
			initing = false;
		else {
			doc.read(doc.getRowid(), false);
			AgentSalesPlanImpl.refreshDocCache();			
		}
		String info = "<i>"+ Util.IntToScaleStr(doc.count(), 1) + " шт</i>&nbsp;&nbsp;&nbsp;<b>" + 
				Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р. </b>";
		((TextView)getView().findViewById(R.id.tvInfo)).setText(Html.fromHtml(info));
		
		PresentList pl = list.getData();

		PriceImpl pi = new PriceImpl();
		Price price = pi.getData();
		
		CostStrategy cs = CostStrategy.getInstance(doc.getClass());
		for(PresentItem item : pl.items) {
			if( item.row < pl.row && item.col < pl.col ) {
				TableRowData trd = rows.get(item.row);
				TextView tvi = trd.cells.get(item.col);
				
				if( item.ids.size() > 0 ) {
					price.id = item.ids.get(0).id;
					if( pi.read() ) {
						int qty = doc.getItemQty(price);
						
						String color = (qty > 0) ? "\"#00ff40\"" : "\"#000000\"";
						
						tvi.setOnClickListener(new OpenPriceCount(pi.getRowid()));
						
						String text = "<font color=" + color + ">" + price.name;
						text += "<br>на складе: " + Util.IntToScaleStr(doc.getItemValue(price), Consts.QTY_SCALE) + "</font>";
						SalesDataItem planItem = AgentSalesPlanImpl.getItemQty(price.id);
						
						if(planItem != null)
							if( planItem.qty > 0) 
								text += "<br><b><font color=\"#C50000\">план: " + Util.IntToScaleStr(planItem.qty, Consts.QTY_SCALE) + "</font></b>";
							else if(planItem.qty == 0)
								text += "<br><b><font color=\"#C50000\">ПЛАН ВЫПОЛНЕН</font></b>";
							else if(planItem.qty < 0)
								text += "<br><b><font color=\"#0000C5\">план перевыполен на: " + Util.IntToScaleStr(Math.abs(planItem.qty), Consts.QTY_SCALE) + "</font></b>";
						
						int cost = cs.getItemCost(price, doc);
						text += "<br><font color=" + color + ">цена: " + Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
						if( qty > 0 )
							text += "<br>заказ: " + Util.IntToScaleStr(qty, Consts.QTY_SCALE);
						text += "</font>";
						
						tvi.setText(Html.fromHtml(text));
					}
				}
			}
		}
		pi.close();
	}

	class OpenPriceCount implements View.OnClickListener {
		long rid;
		
		public OpenPriceCount(long rid) { this.rid = rid; }
		@Override public void onClick(View v) { PriceCount.open(v.getContext(), rid, doc); }
	}
}

class TableRowData {
	public TableRow row;
	public List<TextView> cells;
	
	public TableRowData(int cellNum, int w, int h, Context context) {
		row = new TableRow(context);
		cells = new ArrayList<TextView>(cellNum);
		
		for( int i=0; i< cellNum; i++ ) {
			TextView tv = new TextView(context);
			tv.setWidth(w);
			tv.setHeight(h);
			
			row.addView(tv);
			
			cells.add(i, tv);
		}
	}
}

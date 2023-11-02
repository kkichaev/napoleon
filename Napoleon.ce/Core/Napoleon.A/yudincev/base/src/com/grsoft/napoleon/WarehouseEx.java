package com.grsoft.napoleon;

import java.util.HashSet;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Filter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	PriceImpl priceImpl = new PriceImpl();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnScanBC).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				IntentIntegrator ii = new IntentIntegrator(WarehouseEx.this);
				ii.initiateScan();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
		     String bc = scanResult.getContents();
		     List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(PriceEx.class), "barcode LIKE '%|" + bc + "|%'", null);
		     if( ids.size() > 0 ) {
		    	 ((Itemsable)document).editItem(ids.get(0), this);
		     }
		}
	}
	
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroPositionFilter(){
			HashSet<Long> rowids = new HashSet<Long>();
			
			{
				DbWriter.checkDBTable(PriceQty.class);
				DbWriter.checkDBTable(Price.class);
				Cursor c = DataBaseManager.getDataBase().rawQuery("select price.rowid from price, priceqty where price.id = priceqty.id", null);
				while(c.moveToNext())
					rowids.add(c.getLong(c.getColumnIndex("rowid")));
				
				c.close();
			}
			
			@Override
			public boolean inset(long priceRowID, String id) {
				return rowids.contains(priceRowID);
			}
			
			@Override
			public String getWhereStr() {
				return "" ;
			}
		};
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);
		ImageView iv = (ImageView)v.findViewById(R.id.ivImage);
		String image = "";
		try {
			String table = DataObjectInfo.getInstance().getTableName(Present.class);
			Cursor c = DataBaseManager.getDataBase().rawQuery("select photoPath from " + table + " where id='" + node.getId() + "'", null);
			if( c.moveToNext() )
				image = c.getString(0);
			c.close();
		} catch(Exception e) {
			image = "";
		}
		final int PICSZ = 200;
		BitmapDrawable bmp = null;
		if( image.length() > 0 ) {
			try {
				bmp = BitmapUtils.createBitmap(image, PICSZ);
			} catch (Exception e) {
				bmp = null;
				e.printStackTrace();
			}
		}
		if( bmp == null )
			iv.setVisibility(View.INVISIBLE);
		else {
			iv.setImageDrawable(bmp);
			iv.setOnClickListener(new OpenPresent(image));
			iv.setVisibility(View.VISIBLE);
		}
		return v;
	}
	
	class OpenPresent implements OnClickListener {
		String image;
		
		public OpenPresent(String image) {
			this.image = image;
		}
		
		@Override public void onClick(View v) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + image), "image/*");
			startActivity(intent);
			//PricePresentation.open(v.getContext(), image, document.getRowid(), null); 
			}
	}
	
	@Override protected int getItemLayoutId() { return R.layout.priceitemrow_ex; }
	
	@Override
	protected void onPause() {
		priceImpl.close();
		super.onPause();
	}
}

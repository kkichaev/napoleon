package com.grsoft.napoleon;

import java.util.HashSet;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.MLM;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.FMLMImpl;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WarehouseEx extends Warehouse {
	FolderImpl fi = new FolderImpl();
	FMLMImpl fact = new FMLMImpl();
	MLM plan = new MLM();
	boolean readed = false;
	
	@Override
	protected void onResume() {
		if( !readed ) {
			String id = "";
			if( document != null )
				id = document.getId();
			
			fact.getData().id = id;
			fact.read();

			DataTraveler.travel(MLM.class, new DataTraveler.Travel<MLM>() {
				@Override
				public boolean travel(DataTraveler<MLM> item) {
					plan = item.data;
					return false;
				}
			}, "");
			
			readed = true;
		}
		

		super.onResume();		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		fi.close();
		fact.close();
	}

	@Override
	protected BaseAdapter createListAdapter() {
//		if( document instanceof ReturnImplEx)
//			return new ReturnAdapter(this, document.getId());
		return new Adapter(this);
	}
	
	class Adapter extends FoldersAdapter {

		public Adapter(WarehouseManager warehouse) {
			super(warehouse);
		}
		
		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			ViewGroup ret = (ViewGroup) super.getView(arg0, convertView, arg2);
			PriceEx pe = (PriceEx)price.getData();
			Folder f = fi.getData();
			f.id = pe.folderID;
			fi.read();
			
			int tpFace = Typeface.NORMAL;
			if(plan.isSelected(f, pe)) {
				ret.setBackgroundResource(R.drawable.yellow_row_selector);
				if(!fact.getData().haveItem(pe))
					tpFace = Typeface.BOLD;
			}
			int count = ret.getChildCount();
			for(int i=0; i<count; i++) {
				View v = ret.getChildAt(i);
				if( v instanceof TextView) {
					TextView tv = (TextView)v;
					tv.setTypeface(null, tpFace);
				}
			}
			return ret;
		}
	}
	class ReturnAdapter extends FoldersAdapter {

		HashSet<String> ids = new HashSet<String>();
		
		public ReturnAdapter(WarehouseManager warehouse, String orgId) {
			super(warehouse);
			
			DocList dl = DeliveryDoc.instance().docList(orgId);
			for(Document<?> d : dl) {
				for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
					ids.add(di.id);
			}
			dl.close();
		}
		
		@Override public boolean inset(long rowid, String id) { return ids.contains(id); }
	}
}

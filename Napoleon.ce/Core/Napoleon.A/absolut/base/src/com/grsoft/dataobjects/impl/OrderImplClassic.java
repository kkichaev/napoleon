package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;

import com.grsoft.dataobjects.FocusedGroupItem;
import com.grsoft.dataobjects.OrderBase;
import com.grsoft.dataobjects.OrderFocusedFolder;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgBase;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceBase;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.script.ScriptEdit;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.MessageBox;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class OrderImplClassic extends OrderImpl implements 
	IMatrix {
	public static final int AUTOORDER = 0x200;  
	
	public void autoorder(Date begin, String orgId, GpsCoord coord, long rid) {		
		
		HashMap<String, Integer> items = new HashMap<String, Integer>();
		
		OffTakeHistory hist = new OffTakeHistory(orgId, true);
		for(OffTakeHistory.DateSales ds : hist.getSalesData()) {
			if( ds.date.before(begin) )
				continue;
			
			for(Entry<String,OffTakeHistory.SaleItem> ih : ds.items.entrySet()) {
				if( items.containsKey(ih.getKey()) == false) {
					int qty = ih.getValue().offTake * 3 / 2;
					items.put(ih.getKey(), qty);
				}
			}
		}

		HashMap<String, Integer> ritems = new HashMap<String, Integer>();
		
		RemnantsImpl ri = new RemnantsImpl();
		if( rid != ExtrasConst.INVALID_ID ) {
			ri.read(rid);
			ri.close();
			for(RemnantItem rmni : ri.getData().items)
				ritems.put(rmni.id, rmni.qty);
		}

		for(Entry<String, Integer> se : items.entrySet()) {
			int qty = se.getValue();
			if((qty % Consts.QTY_SCALE) != 0 )
				qty = (qty / Consts.QTY_SCALE + 1) * Consts.QTY_SCALE;
			
			Integer rv = ritems.get(se.getKey());
			if( rv != null )
				qty -= rv;

			if(qty == 0)
				qty = Consts.QTY_SCALE;
			se.setValue(qty);
		}
		
		OrgImpl oi = new OrgImpl();
		Org org = oi.getData();
		org.id = orgId;
		if( oi.read() )
			data.sumType = org.costype;
		oi.close();
		
		ConfigImpl ci = new ConfigImpl();
		ci.getData().key = "Склады";
		if( ci.read() ) {
			ArrayList<CharSequence> v = new ArrayList<CharSequence>();
			DialogHelper.makeList(ci.getData().value, v);
			if( v.size() > 0 ) {
				((OrderBase)data).setWhIndex(0);
				((OrderBase)data).setWhName(v.get(0).toString());
			}
		}
		ci.close();
		
		super.autoorder(orgId, coord, items, true);
		data.params |= AUTOORDER;
		write();
		close();
	}
	
	@Override
	public boolean init(final Context context, final String orgId, final GpsCoord coord) {
		initSilent(orgId, coord);
		
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("Выберите вариант");
		CharSequence[] items = new CharSequence[] {"Автозаказ", "Обычный заказ"};
		b.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				delete();
				context.sendBroadcast(new Intent(ScriptEdit.REFRESH_DOC_ACTION));
			}
		});
		
		b.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if( which == 1 ) {
					editProperties(context, false);
					dialog.dismiss();
				} else {
					Date begin = new Date();
					long rid = RemnantsImpl.find(orgId, begin);
					if( rid == ExtrasConst.INVALID_ID ) {
						MessageBox.show(context, "Ошибка", "Нет остатков на текущую дату");
						return;
					}
					//вычтем неделю
					begin = new Date(begin.getTime() - 1000l * 3600 * 24 * 7 * 4);
					autoorder(begin, orgId, coord, rid);
					open(context);
					dialog.dismiss();
				}
			}
		});
		b.create().show();
		
		return false;		
	}

	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		PriceBase pe = (PriceBase)price.getData();
		int wh = ((OrderBase)data).getWhIndex();
		if( wh > 0 && pe.getWhQty().size() >= wh ) {
			pe.getWhQty().get(wh-1).qty += qty;
			price.write();
		} else
			super.updatePrice(price, qty);
	}
	
	@Override
	public int getItemValue(Price item) {
		PriceBase pe = (PriceBase) item;

		int wh = ((OrderBase)data).getWhIndex();
		if( wh > 0 && pe.getWhQty().size() >= wh )
			return pe.getWhQty().get(wh-1).qty;
		return item.qty;
	}
	
	public OrgMatrixImpl matrix = null;
	
	public OrgMatrix getMatrix() {
		return MatrixInflator.inflate(this);
	}

	public HashSet<String> getFocusedItems() {
		HashSet<String> focusedItems = new HashSet<String>();
	
		OrgImpl oi = new OrgImpl();
		Org o = oi.getData();
		OrgBase ob = (OrgBase)o;
		o.id = data.id;
		
		if( oi.read() && ob.getOrgType().length() > 0 ) {
			String[] items = ob.getOrgType().split(",");
			OrgMatrixImpl matrix = new OrgMatrixImpl();
			OrgMatrix m = matrix.getData();
			for(String type : items) {
				m.name = type;
				if( matrix.read() ) {
					for(OrgMatrixItem omi : m.items) {
						focusedItems.add(omi.id);
					}
				}
			}
			matrix.close();
		}
		oi.close();
		
		return focusedItems;
	}
	
	public List<FocusedGroupItem> getUnsettedFocusedItems() {
		List<FocusedGroupItem> ret = new ArrayList<FocusedGroupItem>(); 
		HashSet<String> fi = getFocusedItems();
		
		if( fi.size() > 0 ) {
			for(OrderItem oi : data.items) {
				if(fi.contains(oi.id))
					fi.remove(oi.id);
			}
			
			for(OrderFocusedFolder off : data.focusedFolders) {
				if( fi.contains(off.fid) )
					fi.remove(off.fid);
			}
		}
		
		if( fi.size() > 0) {
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			ArrayList<String> needRemove = new ArrayList<String>();
			for(String pid : fi) {
				p.id = pid;
				if( !pi.read() || getItemValue(p) <= 0 )
					needRemove.add(pid);
			}
			
			fi.removeAll(needRemove);
			pi.close();
		}
		
		for(String pid : fi) {
			FocusedGroupItem fgi = new FocusedGroupItem();
			fgi.fid = pid;
			ret.add(fgi);
		}
		return ret;
	}
}

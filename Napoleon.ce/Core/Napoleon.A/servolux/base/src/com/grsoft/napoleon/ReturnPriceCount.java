package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.DeliveryKey;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ReturnCause;
import com.grsoft.dataobjects.ReturnItemDlv;
import com.grsoft.dataobjects.ReturnLimit;
import com.grsoft.dataobjects.ReturnRequest;
import com.grsoft.dataobjects.ReturnRequestItem;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnLimitImpl;
import com.grsoft.dataobjects.impl.ReturnRequestImpl;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.PhotoClickHandler;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ReturnPriceCount extends PriceCount implements OrderImplBase.UpdateQtyHandler, PhotoClickHandler.EventHandler {
	
	protected static final int DIALOG_MFR_DATE = 0x10;
	DlvAdapter adapter = new DlvAdapter();
	HashMap<DlvKeySum, Integer> dlvQtys = new HashMap<DlvKeySum, Integer>();
	Date mfrDate;
	VisitImplEx visit = new VisitImplEx();
	String picPath;
	boolean justCreated = true;
	
	long returnLimit = 0;
	boolean canOverlimit = false;
	int returnLimitType = ReturnLimit.LIMIT_SUM;
	ReturnLimit rlimit;
	int svQtyTotal = 0;
	
	@Override protected int getContentViewId() { return R.layout.returncount; }
	
	public static void open(Context context, long priceRoid, ReturnRequestImpl doc) {
		Intent i = new Intent(context, ReturnPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((ReturnRequestImpl)document).setUpdateQtyHandler(this);
		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setAdapter(adapter);

//		findViewById(R.id.tvMfrDate).setOnClickListener(new View.OnClickListener() {			
//			@Override 
//			public void onClick(View arg0) { 
//				Intent i = new Intent(ReturnPriceCount.this, CalendarActivity.class);
//				i.putExtra(ExtrasConst.DATE_TAG, (mfrDate == null) ? new Date() :  mfrDate.getTime());
//				startActivityForResult(i, DIALOG_MFR_DATE);
//			}
//		});
		
		findViewById(R.id.btnMakePhoto).setOnClickListener(new PhotoClickHandler((PhotoDocument)visit, ReturnPriceCount.this, VisitDoc.instance()));
	}
	
	@Override
	protected void onResume() {
		if(justCreated) {
			justCreated = false;
			
			ReturnRequestImpl rri = (ReturnRequestImpl)document;
			visit.getData().created = rri.getData().visitDoc; 
			if(!visit.read()) {
				ReturnRequest rr = ((ReturnRequestImpl)document).getData();
				GpsCoord loc = new GpsCoord(rr.latitude, rr.longitude, rr.stltime);
				visit.init(this, document.getId(), loc);
				rr.visitDoc = visit.getData().created;
				document.write();
			}
		} else if(visit.getRowid() != ExtrasConst.INVALID_ID)
			visit.read(visit.getRowid(), false);
		
		super.onResume();
	
		VisitItemEx vi = visit.findPhoto(price.getData().id);
		if( vi != null ) {
			String fileName = new String(vi.id);
			Bitmap bm = BitmapFactory.decodeFile(fileName);
			ImageView iv = (ImageView)findViewById(R.id.ivPresent2);
			iv.setImageBitmap(bm);
			iv.setVisibility(View.VISIBLE);
		}
		
//		updatePhotoText((ReturnCause)((Spinner)findViewById(R.id.spCause)).getSelectedItem());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		visit.close();
	}
	
	public void onBackPressed() {
		super.onBackPressed();
		if(visit.isEmpty())
			visit.delete();
	}
	
	boolean havePhoto() { return visit.findPhoto(price.getData().id) != null; }
	
	@Override
	protected boolean isInputValid(Runnable r) {
		String errText = null;
		if( overLimit() && !canOverlimit ) {
			errText = "Превышен лимит возвратов";
//		} else if(mfrDate == null) {
//			errText = "Введите дату производства";
		} else {
			ReturnCause value = (ReturnCause) ((Spinner)findViewById(R.id.spCause)).getSelectedItem();
			if( value == null || value.id.length() == 0) {
				errText = "Выберите причину возврата";
//			} else if(value.needPhoto != 0 && !havePhoto()) {
//				errText = "Необходимо сделать фото товара";				
			}
		}
		if( errText != null ) {
			Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
			return false;
		}
		return super.isInputValid(r);
	}
	
	long countCurValue() {
		long curValue = 0;
		for(Entry<DlvKeySum, Integer> kv : dlvQtys.entrySet()) {
			if( kv.getValue() == 0 )
				continue;
			
			long qty = (long)kv.getValue();
			if(returnLimitType == ReturnLimit.LIMIT_SUM) {
				curValue += qty * kv.getKey().cost / Consts.QTY_SCALE;
			} else {
				curValue += qty * price.getData().weight / Consts.QTY_SCALE;
			}
		}
		
		curValue /= ((returnLimitType == ReturnLimit.LIMIT_SUM) ? Consts.SUM_SCALE : Consts.WEIGHT_SCALE);
		return curValue;
	}
	
	
	protected void updateCurrentLimit() {
		TextView tv = (TextView)findViewById(R.id.tvAvailQty); 
		if( rlimit == null ) {
			tv.setText("лимит не задан");
			return;
		}
		
		long value = returnLimit - countCurValue();
		int c = Color.BLACK;
		if(value < 0 )
			c = Color.RED;

		tv.setTextColor(c);
		tv.setText(String.format("план %d, выполнено %d, остаток %d", rlimit.limit,  rlimit.limit - value, value));

	}

	private boolean overLimit() {
		return countCurValue() > returnLimit;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if( data != null && requestCode == DIALOG_MFR_DATE ) {
//			Date curDate = new Date();
//			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
//			mfrDate = new Date(ct);
//			refreshDate();
//		} else 
		if(requestCode == PhotoClickHandler.CAMERA_ACTIVITY && resultCode == RESULT_OK){
			if(picPath != null && picPath.trim().length() > 0)
				visit.addPhoto(picPath.getBytes(), price.getData().id);
		}
	}
	
//	private void refreshDate() {
//		TextView tv = (TextView)findViewById(R.id.tvMfrDate);
//		String text = mfrDate == null ? "Введите дату" : Util.simpleDateFormat.format(mfrDate); 
//		tv.setText(Html.fromHtml("<u>" + text + "</u>"));		
//	}

	@Override protected boolean getStartInPack() { return false; }
	
	@Override protected boolean isComplexSalesHistory() { return false; }
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		dlvQtys.clear();
		
		PriceEx pe = (PriceEx)price.getData();
		String itemId = pe.id;
		ReturnRequestImpl rrdoc = (ReturnRequestImpl)document;
		final ReturnRequestItem re = (ReturnRequestItem) rrdoc.findItem(itemId);
		
		View trAvailCount = findViewById(R.id.trAvailCount);
		if(document.isExported() == false) {
			returnLimit = 0;
			String limitText = getString(R.string.limit);
			rlimit = ReturnLimitImpl.getLimit(document.getDate(), pe.idType);			
			if(rlimit != null) {
				returnLimit = rlimit.countCurrentLimit();
				returnLimitType = rlimit.limitType;
				canOverlimit = rlimit.canOverlimit > 0;
				
				if(rlimit.limitType == ReturnLimit.LIMIT_SUM) {
					limitText += ", руб.";
					if(re != null) {
						long addLimit = 0;
						for(ReturnItemDlv rid : re.items)
							addLimit += ((long)rid.cost * rid.qty) / Consts.QTY_SCALE;
						returnLimit += addLimit / Consts.SUM_SCALE;
					}
				} else {
					limitText += ", кг";					
					if(re != null) {
						long addLimit = 0;
						for(ReturnItemDlv rid : re.items)
							addLimit += ((long)pe.weight * rid.qty) / Consts.QTY_SCALE;
						returnLimit += addLimit / Consts.WEIGHT_SCALE;
					}
				}
			}
			((TextView)findViewById(R.id.tvAvailText)).setText(limitText);
			trAvailCount.setVisibility(View.VISIBLE);
		} else {
			trAvailCount.setVisibility(View.GONE);
		}
		
		if( re != null ) {
			for(ReturnItemDlv rid : re.items) {
				dlvQtys.put(new DlvKeySum(rid), rid.qty);
				svQtyTotal += rid.svQty;
			}
			mfrDate = re.mfrDate;
			if(mfrDate.compareTo(new Date(70, 2, 2)) < 0)
				mfrDate = null;
		} else
			mfrDate = null;
		
		Spinner sp;
		sp = (Spinner)findViewById(R.id.spCause);
//		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				updatePhotoText((ReturnCause) arg0.getAdapter().getItem(arg2));
//			}
//
//			@Override public void onNothingSelected(AdapterView<?> arg0) { }
//		});

		String where = "(idType='" + pe.idType + "' or idType = '') and firm='" + rrdoc.getData().firmCode + "'";
		DialogHelper.loadSpinnerFromDataObject(sp, ReturnCause.class, new DialogHelper.Selected<ReturnCause>() {
			@Override public boolean isSelected(ReturnCause object) { return ((re == null) ? "" : re.cause).equals(object.id); }
		}, true, "name", where);
		
		int viewAccepted = View.GONE;
		
//		if(rrdoc.isAccepted()) {
//			sp = (Spinner)findViewById(R.id.spSvCause);
//			DialogHelper.loadSpinnerFromDataObject(sp, ReturnCause.class, new DialogHelper.Selected<ReturnCause>() {
//				@Override public boolean isSelected(ReturnCause object) { return ((re == null) ? "" : re.svCause).equals(object.id); }
//			}, true, "name", where);
//			viewAccepted = View.VISIBLE;
//		} 
		findViewById(R.id.trSvCause).setVisibility(viewAccepted);
		
		adapter.refresh((ReturnRequest)document.getData(), itemId);
		updateQty();
//		refreshDate();
	}

	void updateQty() {
		int qty = 0;
		for(Entry<DlvKeySum, Integer> kv : dlvQtys.entrySet())
			qty += kv.getValue();
		
		qtyItems = qty;
		String text = Util.IntToScaleStr(qty, Consts.QTY_SCALE);
//		if(((ReturnRequestImpl)document).isAccepted()) {
//			text += "/" + Util.IntToScaleStr(svQtyTotal, Consts.QTY_SCALE);
//		}
			
		((TextView)findViewById(R.id.tvTotalQty)).setText(text);
		updateCurrentLimit();
	}
		
	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		Spinner sp = (Spinner)findViewById(R.id.spCause);
		ReturnCause value = (ReturnCause) sp.getSelectedItem();
		ReturnRequestItem rie = (ReturnRequestItem)item;
		
		if( value != null && value.id.length() > 0)
			rie.cause = value.id;
		
		long sum = 0;

		rie.mfrDate = mfrDate == null ? new Date(70, 1, 1) : mfrDate;
		if( isNewItem )
			rie.uid = UUID.randomUUID().toString().replace("-", "");
		
		rie.items.clear();
		for(Entry<DlvKeySum, Integer> kv : dlvQtys.entrySet()) {
			if( kv.getValue() == 0 )
				continue;
			
			ReturnItemDlv dlv = new ReturnItemDlv();
			dlv.date = kv.getKey().date;
			dlv.number = kv.getKey().number;
			dlv.cost = kv.getKey().cost;
			dlv.qty = kv.getValue();
			dlv.party = kv.getKey().party;
			rie.items.add(dlv);

			sum += (long)dlv.cost * dlv.qty / Consts.QTY_SCALE;
		}
		
		rie.cost = (int)((sum * Consts.QTY_SCALE)/ qtyItems);
	}
	
	class DlvAdapter extends BaseAdapter {
		ReturnRequestItem item;
		List<DlvData> docs = new ArrayList<DlvData>();
		
		public void refresh(ReturnRequest rdoc, String itemId) {
			ReturnRequest rr = (ReturnRequest) document.getData();
			item = (ReturnRequestItem)((ReturnRequestImpl)document).findItem(itemId);
			if( item == null)
				item = new ReturnRequestItem();
			 
			String orgId = rdoc.id;
			 
			docs.clear();
			Date expDate = rr.getExpiredDate();
			DocList dl = DeliveryDoc.instance().docList(orgId, "date", "firm='" + rr.firmCode + "'");
			for(Document<?> d : dl) {
			 for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
				 if(di.id.equals(itemId) && ((DeliveryItemEx)di).expired.compareTo(expDate) >= 0 ) {
					 docs.add(new DlvData((Delivery)d.getData(), di));
				 }
			}
			dl.close();
			 
			notifyDataSetChanged();
		}

		@Override public int getCount() { return docs.size(); }
		@Override public Object getItem(int arg0) { return docs.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(ReturnPriceCount.this, R.layout.return_doc_row, null);
			
			DlvData dd = (DlvData)getItem(pos);
			TextView tv;
			String text;
			
			tv = (TextView)view.findViewById(R.id.tvDoc);
			text = dd.doc.number + " " + Util.simpleDateFormat.format(dd.doc.date);
			tv.setText(text);
			
			tv = (TextView)view.findViewById(R.id.tvCost);
			tv.setText(Util.IntToScaleStr(dd.doc.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

			tv = (TextView)view.findViewById(R.id.tvMaxQty);
			tv.setText(Util.IntToScaleStr(dd.doc.qty, Consts.QTY_SCALE));
			
//			int svQty = 0;
//			String rem = "";
			int color = Color.BLACK;
			Integer val = dlvQtys.get(dd.doc);
			
//			boolean accepted = ((ReturnRequestImpl)document).isAccepted();  
//			if(accepted) {
//				for(Entry<DlvKeySum, Integer> kv : dlvQtys.entrySet()) {
//					DlvKeySum key = kv.getKey(); 
//					if(key.equals(dd.doc)) {
//						svQty = key.svQty;
//						rem = key.remark;
//						break;
//					}
//				}
//				if( val != null && (int)val != svQty )
//					color = Color.RED;
//			}
//			
			text = val == null ? "" : Util.IntToScaleStr(val, Consts.QTY_SCALE);
			tv = (TextView)view.findViewById(R.id.tvQty);
			
//			if(accepted && val != null) {
//				text += "/" + Util.IntToScaleStr(svQty, Consts.QTY_SCALE);
//			}
			
			tv.setText(text);
			tv.setTextColor(color);
			if(document.isEditable())
				tv.setOnClickListener(new SetQty(dd.doc));
						
			tv = (TextView) view.findViewById(R.id.tvExpDate);
			tv.setText(Util.simpleDateFormat.format(((DeliveryItemEx)dd.item).expired));

			tv = (TextView) view.findViewById(R.id.tvParty);
			tv.setText(((DeliveryItemEx)dd.item).party);
//			tvSvRem.setText(rem);
//			tvSvRem.setVisibility(View.GONE);
//			tvSvRem.setVisibility(accepted ? View.VISIBLE : View.GONE);
			
			return view;
		}
		
	}
	
	class SetQty implements View.OnClickListener {
		DlvKeySum doc;
		
		public SetQty(DlvKeySum doc) {
			this.doc = doc;
		}

		@Override
		public void onClick(View v) {
			InputNumberDlg.open(v.getContext(), new InputNumber() {
				
				@Override
				public int getValue() {
					Integer value = dlvQtys.get(doc);
					return value == null ? 0 : value;
				}
				
				@Override
				public void applayInput(int value, Object... params) {
					if( value > doc.qty) {
						Toast.makeText(ReturnPriceCount.this, "Введенное количество превышает количество в накладной", Toast.LENGTH_SHORT).show();
						return;
					}
					if( value == 0 )
						dlvQtys.remove(doc);
					else
						dlvQtys.put(doc, value);
					adapter.notifyDataSetChanged();
					updateQty();
				}
				
			}, Consts.QTY_SCALE, true, "Введите количество");
		}
		
	}

	@Override public void prepareBoforeClick() { VisitImplEx.setPhotoTag(price.getData().id); }
	@Override public void makePhotoFile(File newFile) { picPath = newFile.getAbsolutePath(); }

//	private void updatePhotoText(ReturnCause selected) {
//		String text = "";
//		if(selected != null && selected.needPhoto != 0 && havePhoto() == false) {
//			text = "Необходимо фото!";
//		}
//		((TextView)findViewById(R.id.tvPhotoText)).setText(text);
//	}
}

class DlvKeySum extends DeliveryKey {
	public int qty;
	public int cost;
	public String remark;
	public int svQty;
	public String party;
	
	public DlvKeySum(Delivery doc, int qty, int cost, String party) {
		super(doc);
		
		this.qty = qty;
		this.cost = cost;
		this.svQty = 0;
		this.remark = "";
		this.party = party;
	}

	public DlvKeySum(ReturnItemDlv rid) {
		super(rid.date, rid.number);
		
		this.qty = rid.qty;
		this.cost = rid.cost;
		this.remark = rid.remark;
		this.svQty = rid.svQty;
		this.party = rid.party;
	}
	
	@Override
	public int hashCode() {
		return (date.toString() + number + party).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DeliveryKey) {
			DlvKeySum ref = (DlvKeySum)o;
			return date.equals(ref.date) && number.equals(ref.number) && party.equals(ref.party);
		}
		return false;
	}
}

class DlvData {
	public DlvKeySum doc;
	public DeliveryItem item;
	
	public DlvData(Delivery doc, DeliveryItem item) {
		this.doc = new DlvKeySum(doc, item.qty, item.qty == 0 ? 0: (int)((long)item.sum * Consts.QTY_SCALE / item.qty), ((DeliveryItemEx)item).party);
		this.item = item;
	}
}

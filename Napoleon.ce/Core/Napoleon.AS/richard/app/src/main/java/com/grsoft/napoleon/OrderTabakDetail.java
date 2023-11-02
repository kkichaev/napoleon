package com.grsoft.napoleon;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ScannedItems;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Pair;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OrderTabakDetail extends OrderDetail {
    Adapter adapter;
    
    
	public static void open(Context context, OrderImplEx doc) {
		Intent i = new Intent(context, OrderTabakDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override protected void setContentView() { setContentView(R.layout.order_detail_tabak); }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { doScan(); }
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void setAdapter() {
		adapter = new Adapter();
		lvItems.setAdapter(adapter);
	}

	int ctr = 0;
	void doScan() {
		if(doc.isEditable() == false)
			return;

//		if(BuildConfig.DEBUG) {
//			if ((ctr % 2) == 0) {
//				onNewBarcode("010468006229158321Ai0CFJR\u001D8005000000\u001D93No0s");
//			}  else {
//				onNewBarcode("010468006229158300RGERMOL\u001D0000000000\u001D003002");
//			}
//			ctr ++;
////			onNewBarcode("010468006229125500RRCXIPI\u001D0000000000\u001D010756");
//		} else {
			IntentIntegrator ii = new IntentIntegrator(this);
			ii.initiateScan();
//		}
	}

	@Override
	public void send() {
		if(((OrderImplEx)doc).isGood())
			super.send();
		else {
			Toast.makeText(this, "Вы не можете отправить заказ, просканируйте весь товар", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onBackPressed() {
		if(((OrderImplEx)doc).isGood())
			super.onBackPressed();
		else {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Предупреждение");
			b.setMessage("Вы не просканировали весь товар, удалить заказ?");
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doc.delete();
					dialog.dismiss();
					OrderTabakDetail.super.onBackPressed();
				}
			});

			b.setNegativeButton(android.R.string.no, null);
			b.create().show();
		}
	}

	boolean updateBC(PriceEx p, String bc, BarcodeData bd) {
		OrderItemEx oie = (OrderItemEx) doc.findItem(p.id);
		if(oie.isScanned()) {
			Toast.makeText(this, "Товар уже набран", Toast.LENGTH_LONG).show();
			return false;
		}

		boolean haveSamePack = (bd.isItemCode && oie.unitInpack == Consts.QTY_SCALE) || (!bd.isItemCode && oie.unitInpack == p.qtyInPack);
		if(!haveSamePack) {
			Toast.makeText(this, "Упаковка в заказе не соответствует товару", Toast.LENGTH_LONG).show();
			return false;
		}

		for(ScannedItems si : oie.barcodes) {
			if(si.barcode.equals(bc)) {
				Toast.makeText(this, "GTIN уже добавлен", Toast.LENGTH_LONG).show();
				return false;
			}
		}

		ScannedItems sadd = new ScannedItems();
		sadd.barcode = bc;
		oie.barcodes.add(sadd);
		adapter.notifyDataSetChanged();
		doc.write();

		runOnUiThread(new Runnable() {
			@Override public void run() { Toast.makeText(OrderTabakDetail.this, "Код маркировки отсканирован", Toast.LENGTH_LONG).show(); }
		});

		return true;
	}

	boolean checkAndAdd(List<PriceEx> p, String bc, BarcodeData bd) {
		List<Pair<PriceEx, OrderItemEx>> fnd = ((OrderImplEx)doc).makeIntersect(p);
		if(fnd.size() == 0) {
			Toast.makeText(OrderTabakDetail.this, "Нет товара в накладной", Toast.LENGTH_LONG).show();
			return false;
		}

		if(fnd.size() == 1) {
			Pair<PriceEx, OrderItemEx> val = fnd.get(0);
			return updateBC(val.first, bc, bd);
		}

		ChooseItem ci = new ChooseItem();
		ci.setParams(fnd, bc, bd);
		ci.show(getFragmentManager(), "");
    	return false;
	}

	public static class ChooseItem extends DialogFragment {

		static List<Pair<PriceEx, OrderItemEx>> src;
		static String bc;
		static BarcodeData bd;

		public void setParams(List<Pair<PriceEx, OrderItemEx>> src, String bc, BarcodeData bd) {
			ChooseItem.src = src;
			ChooseItem.bc = bc;
			ChooseItem.bd = bd;
		}

		@Nullable
		@Override
		public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.choose_item, null);
			ListView lv = v.findViewById(R.id.lvItems);

			lv.setAdapter(new Adapter());

			lv.setOnItemClickListener((adapterView, view, i, l) -> {
				Pair<PriceEx, OrderItemEx> sel = src.get(i);
				((OrderTabakDetail)getActivity()).updateBC(sel.first, bc, bd);
				dismiss();
			});

			getDialog().setTitle("Выберите товар");
			return v;
		}

		class Adapter extends BaseAdapter {

			@Override
			public int getCount() {
				return src.size();
			}

			@Override
			public Object getItem(int position) {
				return src.get(position).first;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null)
					convertView = View.inflate(getActivity(), R.layout.choose_item_row, null);

				PriceEx pe = (PriceEx) getItem(position);
				((TextView)convertView).setText(pe.name);
				return convertView;
			}
		}
	}

	
	List<PriceEx> findItem(BarcodeData bd) {

		List<PriceEx> ret = new ArrayList<>();
		
        PriceEx pe = new PriceEx();
        DbReader r = new DbReader();
        boolean have = false;

        boolean bdo = false;
        bdo = r.select(pe, pe.getTableName(), "barcode like '%" + bd.itemBC + "%'");
        while(bdo) {
            int inpack = pe.qtyInPack;
            if(inpack == 0)
                inpack = Consts.QTY_SCALE;
            int checkCost = bd.isItemCode ? bd.cost : (int) ((long) bd.cost * Consts.QTY_SCALE / inpack);
            int itemCost = pe.mrc;
            if (checkCost == itemCost) {
				ret.add(pe);
				pe = new PriceEx();
//                have = true;
//                break;
            }
            bdo = r.selectNext(pe);
        }
        r.close();

		return ret;
//        return have ? pe : null;
	}
	
	void onNewBarcode(String newBc) {
		if(newBc == null){
			return;
		}

		final String bc = newBc.replace("\u001d", "");
		final BarcodeData bd = new BarcodeData(bc);
		if(bd.haveError) {
			return;
		}
		if(((OrderImplEx)doc).haveBc(bc)) {
			Toast.makeText(this, "Товар уже добавлен",  Toast.LENGTH_LONG).show();
			return;
		}
		List<PriceEx> prc = findItem(bd);
        if(prc.size() > 0) {
        	checkAndAdd(prc, bc, bd);
        } else {
    		runOnUiThread(new Runnable() {

				@Override
				public void run() {
					String text = "Не найден ШК <b>" + bd.itemBC + "</b>" +
							"<br/>МРЦ <b>" + Util.IntToScaleStr(bd.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>" +
							"<br/>Полный код " + bc;
					MessageBox.show(OrderTabakDetail.this, "Ошибка", text);
				}
    			
			});
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
			onNewBarcode(scanResult.getContents());
		}
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override int getResourceID() { return R.layout.orderdeliverydetail_list_row; }
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
			OrderItemEx oie = (OrderItemEx)item;
			String text = "";
			int scanned = (int)((long)(oie).barcodes.size() * oie.unitInpack / Consts.QTY_SCALE);
			if(scanned > 0) {
				text = Integer.toString(scanned);
			}
			if(oie.isScanned())
				color = Color.GREEN;
			
			super.drawInternal(view, name, color, item, pos);
			
			TextView tv = (TextView)view.findViewById(R.id.tvDispatch);
			tv.setText(text);
			tv.setTextColor(color);
		}
	}
}

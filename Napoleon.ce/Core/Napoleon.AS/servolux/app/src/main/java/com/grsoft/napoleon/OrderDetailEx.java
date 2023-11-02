package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDog;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.DisabledFirms;

public class OrderDetailEx extends OrderDetail implements DisabledFirms.Handler {
	
	private static final int DROP_ALERT = 100;
	private static final int SEND_ALERT = 101;
	ProgressDialog pd = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnAddItems.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				OrderEx oe = (OrderEx) doc.getData();
				if(oe.linked == 0) {
					oe.linked = (new Date()).getTime();
					doc.write();
				}
				OrderDocEdit.open(arg0.getContext(), oe.linked);
			}
		});
	}
	
	@Override
	protected String getOrgText(Org o) {
		String ret = super.getOrgText(o);
		DbReader r = new DbReader();
		OrgDog dog = new OrgDog();
		String table = DataObjectInfo.getInstance().getTableName(dog.getClass());
		String where = "ido='" + ((OrgEx)o).ido + "' and firm='" + ((OrderEx)doc.getData()).firmCode + "'";
		
		if(r.select(dog, table, where)) {
			ret += "<br><i>" + dog.name + "</i>";
		}		
		r.close();
		
		return ret;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == DROP_ALERT) {
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("Подтвердите действие");
			ab.setMessage("Ваш заказ меньше мимнимального. Продолжить? Нажав 'Нет' Вы удалите заказ");
			ab.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});
			
			ab.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doc.delete();
					dialog.dismiss();
					finish();
				}
			});
			
			return ab.create();
		} else if( id == SEND_ALERT) {
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("Подтвердите действие");
			ab.setMessage("Ваш заказ меньше мимнимального. Продолжить?");
			ab.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					checkFirmDisable();
				}
			});
			
			ab.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
			});
			
			return ab.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public void send() {
		if( !isDocCorrect() ) {
			showDialog(SEND_ALERT);
			return;
		}
		checkFirmDisable();
	}

	private void checkFirmDisable() {
		pd = ProgressDialog.show(this, "Подождите, пожалуйста", "Проверка запрета отправки");
		DisabledFirms.loadDisabledFirms(this, this);
	}
	
	boolean isDocCorrect() {
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx)oi.getData();
		oe.id = doc.getId();
		oi.read();
		oi.close();
		
		if( oe.noDrop == 0 ) {
			FirmEx fe = ((OrderImplEx)doc).getFirm();
			long sum = doc.sum();
			if( sum < fe.dropSize && sum > 0 ) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if( !isDocCorrect() ) {
			showDialog(DROP_ALERT);
			return;
		}
		super.onBackPressed();
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new Adapter());
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
			PriceEx pe = (PriceEx) price.getData();
			super.drawInternal(view, name + " " + pe.thermalState + "/" + pe.packName, color, item, pos);
		}
	}
	
	void closeWaitDialog() {
		if( pd != null ) {
			pd.dismiss();
			pd = null;
		}
	}

	@Override
	public void firmsLoaded(final HashSet<String> disabledFirms) {
		runOnUiThread(new Runnable() {
			@Override public void run() { 
				closeWaitDialog();
				OrderEx oe = (OrderEx) doc.getData();
				if( disabledFirms.contains(oe.firmCode) ) {
					Toast.makeText(OrderDetailEx.this, "Блокировка отправки включена", Toast.LENGTH_SHORT).show();
				} else {
					OrderDetailEx.super.send();
				}
			}
		});
	}

	@Override
	public void error(final String message) {
		runOnUiThread(new Runnable() {
			@Override public void run() { 
				closeWaitDialog();
				String err = "Ошибка проверки\n" + message;
				Toast.makeText(OrderDetailEx.this, err, Toast.LENGTH_SHORT).show();
			}
		});
	}
}

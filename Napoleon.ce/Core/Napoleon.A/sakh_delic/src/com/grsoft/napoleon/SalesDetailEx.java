package com.grsoft.napoleon;

import android.widget.Toast;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.ObjectExchange;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.NPrinter;

public class SalesDetailEx extends SalesDetail {
	
	boolean onLine = false;
	
	@Override
	protected String[] createPrintCaption() {
		return onLine ? new String[] {"ТТН ТОРГ 12", "Расх.накл.", "Счет-фактура", "Акт приема-передачи", NPrinter.UPD_CAPTION } :
			new String[] {"Расх.накл.", "Акт приема-передачи" };
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	
		SalesEx se = (SalesEx) doc.getData();
		FirmImpl fi = new FirmImpl();
		FirmEx fe = (FirmEx)fi.getData();
		fe.id = se.supplyercode;
		fi.read();
		fi.close();
		
		onLine = (fe.onLine > 0); 
	}
	
	@Override
	protected void doPrint() {
		SalesEx se = (SalesEx) doc.getData();
		//se.schfNumber = "557889";
		
		if( se.schfNumber.length() == 0 && onLine ) {
			receiveNumberOnLine();
			return;
		}		
		super.doPrint();
	}

	protected void checkExchangeResult(SalesImplEx object, final String response, int result) {
		if( result < 0 || result == ObjectExchange.RESULT_FAIL) {
			runOnUiThread(new Runnable() {
				@Override public void run() { Toast.makeText(SalesDetailEx.this, "Ошибка при обмене: " + response, Toast.LENGTH_SHORT).show(); }
			});
			return;
		}
		
		SalesEx src = (SalesEx)object.getData();
		SalesEx dest = (SalesEx)doc.getData();
		//dest.number = src.number;
		dest.division = src.division;
		dest.divisionName = src.divisionName;
		dest.schfNumber = src.schfNumber;
		doc.setExported(true);
		doc.setProceeded();
		
		runOnUiThread(new Runnable() {
			@Override public void run() { SalesDetailEx.super.doPrint(); }
		});
	}
	
	private void receiveNumberOnLine() {
		SalesImplEx se = new SalesImplEx();
		se.read(doc.getRowid());
		se.close();

		new ObjectExchange(this, null,  SalesDoc.instance().getObjectName(), ObjectExchange.WRITE_OBJECTS, se, new ObjectExchange.ObjectSendedHandler() {						
			@Override public void sended(DbObject<?> object, String response, int result) { checkExchangeResult((SalesImplEx) object, response, result); }
		}).execute((Void[])null);
	}
}

package com.grsoft.napoleon;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.printsources.SalesPrint;
import com.grsoft.napoleon.printsources.SalesSource;

public class SalesDetailEx extends SalesDetail implements SendResultListener {
	
	static final int WAIT_PRINT_EX = 100;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.salesdetailex);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnPrintNakl).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					SalesPrint sp = SalesPrintType.getConstructor(Sales.class).newInstance((Sales)doc.getData());
					SelectPrinFormDlg.createPrintForm(SalesDetailEx.this, new SalesSource(sp), 
							WAIT_PRINT_EX, "—борочной лист", null);;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == WAIT_FOR_PRINT_DLG ) {
			markPrintedAndSendDoc();
		} else if( id == WAIT_PRINT_EX )
			id = WAIT_FOR_PRINT_DLG;
		return super.onCreateDialog(id);
		
	}

	private void markPrintedAndSendDoc() {
		if( !doc.isExported() ) {
			((SalesEx)doc.getData()).printCount++;
			doc.write();
		}
		new DocumentSender(this, null,  SalesDoc.instance().getDirtyDocuments(), this).execute((Void[])null);
	}

	@Override
	public void postSendExecute(boolean result) {
		if( result )
			doc.read(doc.getRowid(), false);
	}
}

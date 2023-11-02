package com.grsoft.napoleon;

import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgDogovorImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.napoleon.printsources.IncassSource;
import com.grsoft.napoleon.printsources.PkoSource;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class IncassEditEx extends IncassEdit {

	private static final int WAIT_FOR_PRINT_DLG = 1;
	
	OrgImpl oi = new OrgImpl();
	OrgEx org;
	IncassEx incass;
	
	@Override
	protected int getContentViewID() {
		return R.layout.incassex;
	}
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		incass = (IncassEx) doc.getData();

		org = (OrgEx) oi.getData();		
		org.id = incass.id;
		oi.read();
		oi.close();

		OrgDogovorImpl.loadFirms((Spinner)findViewById(R.id.spFirma), 
				(Spinner)findViewById(R.id.spDog),
				incass.ido, incass.supplyercode, incass.dogovor);
		
        EditText edNumber = (EditText) findViewById(R.id.edNumber);
        edNumber.setText(incass.number);
        edNumber.setEnabled(false);
        
        if( incass.sum > 0 )
        	findViewById(R.id.edCount).setEnabled(false);

        findViewById(R.id.btnPrint).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try{
					setDocument();
					final IncassEx ie = (IncassEx) doc.getData();
					PkoSource pko = new IncassSource(ie);
					SelectPrinFormDlg.createPrintForm(IncassEditEx.this, 
							pko, WAIT_FOR_PRINT_DLG, "pko",
							new Runnable() {
								@Override
								public void run() {
									DocHelper.saveDocNumber(doc.getTableName(), ie.number);
									save();
									finish();
								}
							});
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	protected void send() {
		if( getSum() == 0 ) {
			Toast.makeText(this, "Документ с нулевой суммой передавать нельзя", Toast.LENGTH_SHORT).show();
			return;
		}
		super.send();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case WAIT_FOR_PRINT_DLG:
			return SelectPrinFormDlg.createWaitDlg(this);
		default:
			return super.onCreateDialog(id);
		}
	}
	
	@Override
	protected void setDocument() {
		super.setDocument();
		
		IncassEx incass = (IncassEx) doc.getData();

		FirmEx fe = (FirmEx) ((Spinner)findViewById(R.id.spFirma)).getSelectedItem();
		if( fe != null )
			incass.supplyercode = fe.id;

		OrgDogovor dg = (OrgDogovor) ((Spinner)findViewById(R.id.spDog)).getSelectedItem();
		if( dg != null )
			incass.dogovor = dg.idDog;

        EditText edNumber = (EditText) findViewById(R.id.edNumber);
		incass.number = edNumber.getText().toString();
	}
	
	@Override
	protected DocumentSender createDocumentSender() {
		return new DocumentSender(this, findViewById(R.id.btnSend), IncassDoc.OBJ_NAME, doc, doc.getRowid(), this){
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				
				if(result)
					try {
						DebtDoc.postUpdateProcess();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		};
	}
}

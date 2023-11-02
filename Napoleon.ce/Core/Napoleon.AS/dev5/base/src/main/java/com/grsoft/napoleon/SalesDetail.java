package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PkoImpl;
import com.grsoft.dataobjects.impl.SalesBaseImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.PkoDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.printsources.SalesPrint;
import com.grsoft.napoleon.printsources.SalesSource;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.gps.GPSUtilNew;

public class SalesDetail extends OrderDetail{
	protected static final int WAIT_FOR_PRINT_DLG = 100;
	protected static SelectPrinFormDlg selectPrintFormDlg;
	
	public static Class<? extends Activity> activity = SalesDetail.class;
	public static Class<? extends SalesPrint> SalesPrintType = SalesPrint.class;
	
	DocType prevDocType;
	
	static public void open(Context context, OrderImplBase<? extends Order> order) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
		context.startActivity(i);
	}
	
	@Override protected boolean haveFocusedGroup() { return false; }
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.salesdetail);
	}
	
	protected void setSalesDoc(){ DocType.setCurDoc(SalesDoc.instance()); } 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		prevDocType = DocType.getCurDoc();
		setSalesDoc();
		
		super.onCreate(savedInstanceState);
		
		selectPrintFormDlg = createPrintDlg();
		selectPrintFormDlg.setPostExec(new Runnable() {
			@Override public void run() { 
				((SalesBaseImpl<?>)doc).markPrinted();
				doc.write();
			}
		});
		
		findViewById(R.id.btnPrint).setOnClickListener(new OnClickListener() {			
			@Override public void onClick(final View v) { doPrint(); }
		});
		
	}

	protected SelectPrinFormDlg createPrintDlg() {
		return new SelectPrintFormDlgNew(this, WAIT_FOR_PRINT_DLG);
	}

	@Override protected long getItemSum(OrderItem item) { return ((SalesItem)item).sum; }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(prevDocType != null)
				DocType.setCurDoc(prevDocType);
		}
		return super.onKeyDown(keyCode, event);
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

	protected String[] createPrintCaption() {
		List<String> items = new ArrayList<String>();
		
		items.add(NPrinter.TORG_12_CAPTION);
		items.add(NPrinter.SCHET_FACT_CAPTION);
		
		if (Features.UPD)
			items.add(NPrinter.UPD_CAPTION);
	
		addPrintItems(items);
		String[] result = new String[items.size()];
		result = items.toArray(result);
		
		return result;
	}
	
	protected void addPrintItems(List<String> items) {}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case WAIT_FOR_PRINT_DLG:
			ProgressBar p = (ProgressBar) dialog.findViewById(android.R.id.progress);
	        p.setVisibility(View.GONE);
	        p.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	protected final int MNU_PKO_ID = 4;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if(DocType.getDocType(PkoDoc.instance().getObjectName()) != null)
			menu.add(0, MNU_PKO_ID, 0, R.string.make_pko);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MNU_PKO_ID) {
			PkoImpl pko = PkoImpl.fromSales((SalesBaseImpl<?>)doc, GPSUtilNew.getLastKnownLocation(), this);
			pko.open(this);
			
			finish();
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}

	protected SalesSource createPrintSource(Sales sdoc) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
		SalesPrint sp = SalesPrintType.getConstructor(Sales.class).newInstance(sdoc);
		return new SalesSource(sp);
	}

	protected void doPrint() {
		try {
			Dialog d = selectPrintFormDlg.createDialog(createPrintCaption());
			selectPrintFormDlg.setDataSource(createPrintSource((Sales) doc.getData()));
			d.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


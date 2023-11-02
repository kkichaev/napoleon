package com.grsoft.napoleon;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderPrint;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.DummySalesImpl;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.printsources.PrintInfo;
import com.grsoft.napoleon.printsources.SalesPrint;
import com.grsoft.napoleon.printsources.SalesSource;
import com.grsoft.napoleon.printsources.SupplSource;
import com.grsoft.util.GpsCoord;


public class DocumentsEx extends DocumentsPrint {
	String reportpath = "";
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		if(DocType.getCurDoc() == OrderDoc.instance()){
			menu.add(Menu.NONE, R.id.itSendBill, Menu.NONE, R.string.send_bill);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.itSendBill:
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
			Document<?> doc = (Document<?>) adapter.getItem(menuInfo.position);
			sendBill((OrderImpl) doc);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	protected OnClickListener createInfoClickListener() {
		return new OrgInfo();
	}
	
	class OrgInfo extends OrgInfoClickListener {
		@Override
		protected int getContentView() {
			return R.layout.org_detail_info_ex;
		}
		
		@Override
		protected void adjustDialogView(View view) {
			super.adjustDialogView(view);
			OrgEx o = (OrgEx) org.getData();
			TextView tv = (TextView)view.findViewById(R.id.tvEmail);
			tv.setText("E-mail: " + o.email);
		}
	}

	private void sendBill(OrderImpl order) {
		OrderPrint ordobj = (OrderPrint) order.getData();
		final SalesImpl sales = new DummySalesImpl();
		sales.initFromOrder(order, new GpsCoord(ordobj.latitude, ordobj.longitude));
		sales.getData().number = ((OrderEx)order.getData()).docnumber;
		FirmImpl firm = new FirmImpl();
		firm.read("id", ordobj.firmCode);
		String form = ((FirmEx)firm.getData()).billform;
		
		if(form.trim().length() > 0)
			new AsyncTask<String, Void, File>(){
				protected void onPreExecute() {showDialog(R.id.wait_dlg_id); };
				
				@Override
				protected File doInBackground(String... params) {
					File result = null;
					reportpath = "";
					
					if (params.length > 0)
						result = NPrinter.print(DocumentsEx.this, params[0], new SalesSource(
								new  BillSourcePrint(sales.getData())));
					
					return result;
				}
				
				protected void onPostExecute(File output) {
					dismissDialog(R.id.wait_dlg_id);
					if(output != null){ 
						reportpath = output.getAbsolutePath();
						showDialog(R.id.offer_preview_dlg);
					}
//					if(output != null){ 
//						OrgEx o = (OrgEx) org.getData();
//						Intent intent = new Intent(Intent.ACTION_SEND);
//						intent.setType("text/plain");
//						intent.putExtra(Intent.EXTRA_EMAIL, new String[] {o.email});
//						intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bill_mail_theme, o.name));
//						Uri uri = Uri.parse("file://" + output.getAbsolutePath());
//						intent.putExtra(Intent.EXTRA_STREAM, uri);
//						intent.putExtra(Intent.EXTRA_TEXT, "");
//						startActivity(intent);
//					}
				};
			}.execute(form);
	}
	
	private Dialog createOfferPreviewDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.offer_dialog_title);
		builder.setItems(new String[]{"Просмотр", "Отправить на email"}, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = null;
				Uri uri = Uri.parse("file://" + reportpath);
				
				if(which == 0){
					intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType( uri, OfferDetail.MIME_TYPE_PDF );
				}else if(which == 1){
					dismissDialog(R.id.offer_preview_dlg);
					OrgEx o = (OrgEx) org.getData();
					intent = new Intent(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_EMAIL, new String[] {o.email});
					intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bill_mail_theme, o.name));
					intent.putExtra(Intent.EXTRA_STREAM, uri);
					intent.putExtra(Intent.EXTRA_TEXT, "");
				}
				
				if(intent != null)
					try{
						startActivity(intent);
					}catch(Exception e){
						startActivity(Intent.createChooser(intent, "Выберите программу"));
					}
			}
		});
		return builder.create();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.wait_dlg_id:
			return createWaitDlg();
		case R.id.offer_preview_dlg:
			return createOfferPreviewDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createWaitDlg() {
		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.wait);
		progressDialog.setMessage(getString(R.string.wait_for_print_form));
		return progressDialog;
	}
}

class BillSourcePrint extends SalesPrint{
	public int totalNum = 0;
	
	public BillSourcePrint(Sales sales) {
		super(sales);
		totalNum = items.size();
	}
	
	@Override
	protected void initSupplyer(Sales sales) {
		supplSource = new BillSupl();
		supplSource.setSupplyer(sales.supplyercode);
	}
}

class BillSupl extends SupplSource{
	@PrintInfo(name="Менеджер")
	public String manager = "";
	
	@PrintInfo(name="КПП")
	public String kpp = "";
	
	@PrintInfo(name="БИК")
	public String bic = "";
	
	@PrintInfo(name="БанкНаименование")
	public String bname = "";
	
	@PrintInfo(name="КОРСЧЕТ")
	public String kacc = "";
	
	@PrintInfo(name="РСЧЕТ")
	public String account = "";
	
	@Override
	protected void initFirm(Firm firm) {
		super.initFirm(firm);
		
		FirmEx fe = (FirmEx) firm;
		
		AgentPrefix ap = AgentPrefix.get();
		manager = ap.fullname;
		
		if(manager.trim().length() == 0)
			manager = ap.name;
		
		final String INN_DELIMETER = "/";
		String[] arr = supl_inn.split(INN_DELIMETER);
		
		if(arr.length == 2){
			supl_inn = arr[0];
			kpp = arr[1];
		}
		
		bic = fe.bic;
		bname = fe.bname;
		kacc = fe.kacc;
		account = fe.account;
	}
}
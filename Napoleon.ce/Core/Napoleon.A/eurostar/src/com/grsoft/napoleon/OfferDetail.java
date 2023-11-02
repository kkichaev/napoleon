package com.grsoft.napoleon;

import java.io.File;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Offer;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.DummySalesImpl;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.OfferImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PayTimeImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.printsources.PrintInfo;
import com.grsoft.napoleon.printsources.SalesPrint;
import com.grsoft.napoleon.printsources.SalesSource;
import com.grsoft.napoleon.printsources.SupplSource;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class OfferDetail extends BaseActivity {
	private OfferImpl doc = new OfferImpl();
	private ListView list;
	private ImageButton btnAddItems;
	private ImageButton btnEditOrder;
	private ImageButton btnLines;
	private ImageButton btnSend;
	private TextView tvOrg;
	private ImageButton btnEmail;
	private LinesCountController linesController;
	
	private OrgImpl org = new OrgImpl();
	private String reportpath = "";
	
	public static final String MIME_TYPE_PDF = "application/pdf";
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, OfferDetail.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offerdetail);
		inflateView();
		initData();
		initView();
	}

	private void inflateView() {
		list = (ListView) findViewById(R.id.lvItems);
		tvOrg = (TextView) findViewById(R.id.tvOrg);
		btnAddItems = (ImageButton) findViewById(R.id.btnAddItems);
		btnEditOrder = (ImageButton) findViewById(R.id.btnEditOrder);
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnLines = (ImageButton) findViewById(R.id.btnLines);
		btnEmail = (ImageButton) findViewById(R.id.btnEmail);
	}

	private void initData() {
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		org.read("id",doc.getId());
	}

	private void initView() {
		tvOrg.setText(org.getData().name);
		
		list.setAdapter(new Adapter());
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				doc.getData().items.remove(position);
				doc.write();
				doc.close();
				((BaseAdapter)parent.getAdapter()).notifyDataSetChanged();
			}});
		btnAddItems.setOnClickListener(new OnClickListener() {	@Override public void onClick(View v) {Warehouse.open(v.getContext(), doc, true);	} });
		btnEditOrder.setOnClickListener(new OnClickListener() {	@Override public void onClick(View v) { doc.editProperties(v.getContext()); }	});
		
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(list, btnLines, this, true);
		linesController = linesOnClickListener.getController();
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(doc.getData().items.size() == 0)
					Toast.makeText(v.getContext(), R.string.cant_send_empty_doc_str, Toast.LENGTH_SHORT).show();
				else
					new DocumentSender(v.getContext(), btnSend, 
							DocType.getCurDoc().getObjectName(), doc, 
							doc.getRowid()).execute((Void[])null);
			}
		});
		
		btnEmail.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) {sendToMail();} });
	}
	
	private void sendToMail(){
		Offer ordobj = doc.getData();
		final SalesImpl sales = new DummySalesImpl();
		sales.initFromOrder(doc, new GpsCoord(ordobj.latitude, ordobj.longitude));
		sales.getData().number = ordobj.number;
		FirmImpl firm = new FirmImpl();
		firm.read("id", ordobj.firmCode);
		String form = ((FirmEx)firm.getData()).offerform;
		
		if(form.trim().length() > 0)
			new AsyncTask<String, Void, File>(){
				protected void onPreExecute() {showDialog(R.id.wait_dlg_id); };
				
				@Override
				protected File doInBackground(String... params) {
					File result = null;
					reportpath = "";
					
					if (params.length > 0)
						result = NPrinter.print(OfferDetail.this, params[0], new SalesSource(
								new  OfferSourcePrint(sales.getData())));
					
					return result;
				}
				
				protected void onPostExecute(File output) {
					dismissDialog(R.id.wait_dlg_id);
					if(output != null){ 
						reportpath = output.getAbsolutePath();
						showDialog(R.id.offer_preview_dlg);
					}
				};
			}.execute(form);
	}
	
	class Adapter extends BaseAdapter{
		CostStrategy cs;
		private PriceImpl price = new PriceImpl();
		
		public Adapter(){
			cs = CostStrategy.getInstance(doc.getClass());
		}
		@Override
		public int getCount() { return doc.getData().items.size(); }

		@Override
		public Object getItem(int position) { return doc.getData().items.get(position);	}

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = View.inflate(OfferDetail.this, R.layout.offerdetail_row, null);
			
			OrderItem item = (OrderItem) getItem(position);
			price.read("id", item.id);
			
			TextView tv = (TextView) convertView.findViewById(R.id.tvName);
			linesController.prepareTextView(tv);
			tv.setText(price.getData().name);
			
			tv = (TextView) convertView.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(cs.getItemCost(price.getData(), doc), Consts.SUM_SCALE));
			
			return convertView;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID), false);
		doc.close();
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		if(doc.getData().items.size() == 0)
			doc.delete();
		
		super.onBackPressed();
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
					intent.setDataAndType( uri, MIME_TYPE_PDF );
				}else if(which == 1){
					dismissDialog(R.id.offer_preview_dlg);
					intent = new Intent(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_EMAIL, new String[] {((OrgEx)org.getData()).email});
					intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.offer_mail_theme, org.getData().name));
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

	private Dialog createWaitDlg() {
		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.wait);
		progressDialog.setMessage(getString(R.string.wait_for_print_form));
		return progressDialog;
	}
	
	class OfferSourcePrint extends SalesPrint{
		public int totalNum = 0;
		
		@PrintInfo(name="Форма оплаты")
		public String cash = "";
		
		@PrintInfo(name="График оплаты")
		public String paytime = "";
		
		@PrintInfo(name="Склад")
		public String store = "";
		
		public OfferSourcePrint(Sales sales) {
			super(sales);
			totalNum = items.size();
			store = "ОСНОВНОЙ СКЛАД";
			
			Offer offer = doc.getData();
			cash = (offer.params & ParamState.ofCash) == ParamState.ofCash ? "Наличная" : "Безналичная" ;  
			
			PayTimeImpl impl = new PayTimeImpl();
			impl.read("id", offer.paytime);
			
			paytime = impl.getData().name;
		}
		
		@Override
		protected void initSupplyer(Sales sales) {
			supplSource = new OfferBillSupl();
			supplSource.setSupplyer(sales.supplyercode);
		}
	}

	class OfferBillSupl extends SupplSource{
		@PrintInfo(name="Менеджер")
		public String manager = "";
		
		@PrintInfo(name="МенеджерМайл")
		public String managermail = "";
		
		@PrintInfo(name="Емайл")
		public String mail = "";
		@Override
		public void setSupplyer(String code) {
			super.setSupplyer(code);
			
			AgentPrefixEx ap = (AgentPrefixEx) AgentPrefix.get();
			manager = ap.fullname;
			managermail = ap.email;
			
			FirmImpl impl = new FirmImpl();
			impl.read("id", code);
			
			mail = ((FirmEx) impl.getData()).email;
		}
	}
}



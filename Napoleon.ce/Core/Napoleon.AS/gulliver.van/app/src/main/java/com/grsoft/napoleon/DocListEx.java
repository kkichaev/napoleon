package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.ArchSalesDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.printsources.DataSourceAdapter;
import com.grsoft.napoleon.printsources.SalesPrintEx;
import com.grsoft.napoleon.printsources.SalesSource;
import com.grsoft.napoleon.printsources.SilentReflector;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;

public class DocListEx extends DocList {
	private static final int WAIT_DLG_ID = R.id.wait_dlg;
	public static final String RECEIPT_TITLE = "Экспедиторская расписка";
	public static final String RECEIPT_NAME = "receipt";
	
	private String cause = null;
	private String form = null;

	ImageButton btnPrint;
	SelectAdapter selAdapter = new SelectAdapter();

	final static String RASHOD = "Расходная накладная";
	final static String CHFKT = "Товарно-транспортная накладная";
	final static String TORG12 = "Торг-12";

	@Override
	protected int getViewID() {
		return R.layout.doclistex;
	}

	@Override
	protected void initUI() {
		btnPrint = (ImageButton) findViewById(R.id.btnPrint);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		btnPrint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(DocType.getCurDoc() == IncassDoc.instance())
					printReestr(true);
				else
					showDialog(R.id.ask_what_print_dlg);
			}
		});
	}

	@Override
	protected void loadConfig(Bundle b) {
		DocType.setCurDoc(OrderDoc.instance());
		super.loadConfig(b);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		if(DocType.getCurDoc() == VisitDoc.instance())
			menu.findItem(R.id.itDelete).setVisible(false);
	}


	protected Dialog createAskWhatPrintDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.print);
		builder.setItems(R.array.doc_list_print_items, createPrintItemClick());
		return builder.create();
		
	}
	
	@Override
	public void selectedType(DocType newDocType) {
		super.selectedType(newDocType);

		if(newDocType == VisitDoc.instance())
			btnDelete.setEnabled(false);
		else
			btnDelete.setEnabled(true);

		if (btnDelete.isEnabled() && newDocType == ArchSalesDoc.instance())
			btnDelete.setEnabled(false);
	}

	public android.content.DialogInterface.OnClickListener createPrintItemClick() {
		return new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0)
					printReestr(true);
				else if(which == 3)
					showDialog(R.id.select_sales);
				else if(which==4)
					printReestr(false);
				else
					printReceipt(which);
			}
		};
	}
	
	class SelectAdapter extends BaseAdapter {
		
		public boolean[] selected;
		public void refresh() {
			int count = getCount();
			selected = new boolean[count];
			for(int i=0; i<count; i++)
				selected[i] = true;
			notifyDataSetChanged();
		}
		
		public void toggle(int pos) {
			selected[pos] = !selected[pos];
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return adapter.getCount(); }
		@Override public Object getItem(int arg0) { return adapter.getItem(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			Document<?> doc = (Document<?>) getItem(arg0);
			if( arg1 == null )
				arg1 = View.inflate(DocListEx.this, R.layout.sel_docs_list_row, null);
			drawData(arg1, doc, arg0);
			
			arg1.setBackgroundResource( selected[arg0] ? R.drawable.list_grey_selector : R.drawable.list_selector);
			return arg1;
		}
	}
		
	private Dialog createSelectSalesDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.choose_sales);
		b.setAdapter(selAdapter, null);
		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { printLoadList(); }
		});
		
		AlertDialog ad  = b.create();
		ListView lv = ad.getListView();
		lv.setItemsCanFocus(false);
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	selAdapter.toggle(position);
		    }
		});
		return ad;
	}

	private void refreshSalesDialog(Dialog dialog) {
		selAdapter.refresh();
	}


	
	protected void printLoadList() {
		new AsyncTask<Void, Void, File>() {
			PriceImpl price = new PriceImpl();
			@Override
			protected File doInBackground(Void... params) {
				File result = null;
				
				if(DocType.getCurDoc() == SalesDoc.instance()){
					int qty = 0;
					int packqty = 0;
					
					Map<String, LoadListData> map = new HashMap<String, LoadListData>();
					
					for (int i = 0; i < adapter.getCount(); i++) {
						if(selAdapter.selected[i] == false)
							continue;
						
						Object obj = adapter.getItem(i);

						if (obj instanceof SalesImpl) {
							SalesImpl doc = (SalesImpl) obj;
							
							for(OrderItem item : doc.getData().items){
								LoadListData data = null;
								
								price.read("id", item.id);
								String k = item.id + item.inPack();
								
								if(map.containsKey(k))
									data = map.get(k);
								else{
									data = new LoadListData();
									data.id = ((PriceEx)price.getData()).article;
									data.name = price.getData().name;
									map.put(k, data);
								}
								
								if(item.inPack()){
									int q = (int)((long)item.qty * Consts.QTY_SCALE / price.getData().qtyInPack);
									data.iqty += q;
									data.packName = getString(R.string.box_item);
									packqty += q;
								}else{
									data.iqty += item.qty; 
									data.packName = getString(R.string.sht);
									qty += item.qty;
								}
							}
						}
					}

					List<LoadListData> list = new ArrayList<LoadListData>();
					list.addAll(map.values());
					Collections.sort(list, new Comparator<LoadListData>(){@Override public int compare(LoadListData lhs, LoadListData rhs) {return lhs.name.compareTo(rhs.name); }});
					int pos = 1;
					
					for(LoadListData d : list){
						d.num = pos++;
						d.qty = Util.IntToScaleStr(d.iqty, Consts.QTY_SCALE);
					}
					
					LoadListSource src = new LoadListSource(list);
					src.itemqty = Util.IntToScaleStr(qty, Consts.QTY_SCALE, Util.DEC_DELIM, true);
					src.packqty = Util.IntToScaleStr(packqty, Consts.QTY_SCALE, Util.DEC_DELIM, true);
					src.sumqty = Util.IntToScaleStr(qty + packqty, Consts.QTY_SCALE, Util.DEC_DELIM, true);
					
					result = NPrinter.print(DocListEx.this, "loadlist", src);
				}
				
				return result;
			}

			protected void onPostExecute(File output) {
				if (output != null)
					NPrinter.sendPrintTask(DocListEx.this, output);

				dismissDialog(WAIT_DLG_ID);
				btnPrint.setEnabled(true);
			};

			protected void onPreExecute() {
				btnPrint.setEnabled(false);
				showDialog(WAIT_DLG_ID);
			};

		}.execute((Void[]) null);
	}

	class ReceiptPrint extends SalesPrintEx{
		public String header = "";
		public ReceiptPrint(Sales sales) {
			super(sales);
		}
		
		@Override
		protected boolean getUseTax() { return true; }
	}
	
	public void printReceipt(int type){
		new AsyncTask<Object, Void, File>(){

			@Override
			protected File doInBackground(Object... params) {
				int type =(Integer) params[0];
				
				Sales sls = new Sales();
				sls.created = Util.getDateTime();
				sls.date = Util.getDate();
				
				StringBuilder orgs = new StringBuilder();
				List<String> ids = new ArrayList<String>();
				
				OrgImpl org = new OrgImpl();
				
				for (int i = 0; i < adapter.getCount(); i++) {
					Object obj = adapter.getItem(i);
					
					if(obj instanceof SalesImpl){
						SalesImpl si = (SalesImpl)obj;
						SalesEx six = (SalesEx) si.getData();
						String id = si.getData().id;
						
						if (org.read("id", id)){
							boolean isGeneral = false;
							
							List<OrgDogovor> dogovors = ((OrgEx) org.getData()).dogovors;
	
							for (OrgDogovor dog : dogovors)
								if (six.dogCode.trim().equals(
										dog.id.trim())) {
									isGeneral = dog.isGeneral();
									break;
								}
							
							
							if(type == 1 && isGeneral || type == 2 && !isGeneral){
								
								if(!ids.contains(id)){
									ids.add(id);
									
									
										if(orgs.length() > 0)
											orgs.append(", ");
									
										orgs.append(org.getData().name);
								}
								
								for(OrderItem ssi : si.getData().items)
									sls.items.add(ssi);
							}
						}
					}
				}
				
				
				
				SharedPreferences pref = getSharedPreferences(SalesImplEx.SALESIMPLPREF, Context.MODE_PRIVATE);
				int num = pref.getInt(SalesImplEx.RECEIPTCNT, 1);
				sls.number = String.format("%011d", num);
				Editor ed = pref.edit();
				ed.putInt(SalesImplEx.RECEIPTCNT, num + 1);
				ed.commit();
				
				ReceiptPrint rp = new ReceiptPrint(sls);
				rp.name = orgs.toString();
				rp.header = type == 1 ? "Приложение №2\nк Порядку оформления\nи формы экспедиторских\n документов (п.5)" : "";
				
				return NPrinter.print(DocListEx.this, RECEIPT_TITLE, new SalesSource(rp));
			}
			
			protected void onPostExecute(File output) {
				if (output != null)
					NPrinter.sendPrintTask(DocListEx.this, output);

				dismissDialog(WAIT_DLG_ID);
				btnPrint.setEnabled(true);
			};

			protected void onPreExecute() {
				btnPrint.setEnabled(false);
				showDialog(WAIT_DLG_ID);
			};
			
		}.execute(new Object[]{type});
	}

	public void printReestr(final boolean printDocList) {
		new AsyncTask<Void, Void, File>() {
			OrgImpl org = new OrgImpl();

			@Override
			protected File doInBackground(Void... params) {
				if(DocType.getCurDoc() == SalesDoc.instance())
					return salesPrint(printDocList);
				else
					return incassPrint();
			}

			private File incassPrint() {
				List<IncassListData> list = new ArrayList<IncassListData>();
				
				for (int i = 0; i < adapter.getCount(); i++) {
					Object obj = adapter.getItem(i);
					
					if(obj instanceof IncassImpl){
						IncassImpl doc = (IncassImpl) obj;
						IncassListData data = new IncassListData();
							Org o = org.getData();
							
							o.id = doc.getId();
							org.read();
							org.close();
							
							Incass incass = doc.getData();
							
							data.org = o.name;
							data.address = o.address; 
							data.data = Util.simpleDateFormat.format(incass.created);
							data.isum = incass.sum;
							data.time = incass.created.getTime();
							
							list.add(data);
					}
				}
				
				Collections.sort(list, new Comparator<IncassListData>() { @Override	public int compare(IncassListData lhs, IncassListData rhs) { return (int)(lhs.time - rhs.time); } });
				
				int itog = 0;
				
				for(int i = 0; i < list.size(); i++){
					IncassListData data = list.get(i); 
					data.pos = i + 1;
					data.sum = Util.IntToScaleStr(data.isum, Consts.SUM_SCALE);
					itog += data.isum;  
				}
				
				IncassSource src = new IncassSource(list);
				
				src.form = form;
				src.created = Util.simpleDateFormat.format(new Date());
				src.itogo = Util.IntToScaleStr(itog, Consts.SUM_SCALE);

				DatePeriod period = adapter.getFilter();

				if (period == null) {
					Date now = Util.getDate();
					period = new DatePeriod(now, now);
				}
				src.start = Util.simpleDateFormat.format(period.begin);
				src.finish = Util.simpleDateFormat.format(period.end);
				
				return NPrinter.print(DocListEx.this, "incasslist", src);
			}

			@SuppressWarnings("unchecked")
			public File salesPrint(boolean printDocList) {
				ArrayList<DocListData> list = new ArrayList<DocListData>();
				int pos = 1;

				DatePeriod period = adapter.getFilter();

				if (period == null) {
					Date now = Util.getDate();
					period = new DatePeriod(now, now);
				}

				for (int i = 0; i < adapter.getCount(); i++) {
					Object obj = adapter.getItem(i);
					if (obj instanceof SalesImpl) {
						SalesImpl doc = (SalesImpl) obj;
						pos = putSale(list, pos, doc, false, printDocList);
					}
				}
				
				period.end = Util.getDayEnd(period.end);
				com.grsoft.napoleon.documents.DocList dl = ArchSalesDoc.instance().docList(null, "created", period);
				for(Document<?> d : dl) {
					pos = putSale(list, pos, (OrderImplBase<? extends Sales>)d, true, printDocList);
				}
				dl.close();
				
				if(!printDocList) {
					DocListData dld = new DocListData();
					list.add(dld);
					dld.pos = list.size();
					
					dld = new DocListData();
					list.add(dld);
					dld.pos = list.size();
					
					dld = new DocListData();
					list.add(dld);
					dld.pos = list.size();
				}

				DocListSource src = new DocListSource(list);
				src.start = Util.simpleDateFormat.format(period.begin);
				src.finish = Util.simpleDateFormat.format(period.end);
				src.date = Util.simpleDateFormat.format(Util.getDate());

				src.itogo = pos - 1;

				AgentPrefix ap = AgentPrefix.get();

				if (ap != null)
					src.agent = ap.name;

				ConfigImpl configImpl = new ConfigImpl();
				StringBuilder val = new StringBuilder();
				final String BUHOPER = "БухгалтерОператор";

				if (configImpl.getValue(val, BUHOPER))
					src.buh = val.toString();

				return NPrinter.print(DocListEx.this, printDocList? "doclist" : "money_list", src);
			}

			private int putSale(ArrayList<DocListData> list, int pos, OrderImplBase<? extends Sales> doc, boolean isDeleted, boolean addTTN) {
				String DEL_MSG = " УДАЛЕН";
				DocListData data = new DocListData();
				data.pos = pos;
				SalesEx sales = (SalesEx) doc.getData();
				data.number = sales.number;
				data.created = Util.simpleDateFormat
						.format(sales.created);

				org.getData().id = sales.id;
				org.read();
				org.close();

				StringBuilder str = new StringBuilder();
				str.append(org.getData().name).append(" (")
						.append(org.getData().address)
						.append(")");
				data.org = str.toString();
				data.sum = Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE);

				boolean isGen = false;

				List<OrgDogovor> dogovors = ((OrgEx) org.getData()).dogovors;

				for (OrgDogovor dog : dogovors)
					if (sales.dogCode.trim().equals(
							dog.id.trim())) {
						isGen = dog.isGeneral();
						break;
					}

				if (!isGen) {
					data.name = RASHOD;
					if( isDeleted )
						data.name += DEL_MSG;
				} else {
					if(addTTN) {
					data.name = CHFKT;
						if( isDeleted )
							data.name += DEL_MSG;
						try {
							list.add((DocListData) data.clone());
						} catch (Exception e) {
							e.printStackTrace();
						}
						pos++;
					}
					
					
					data.name = TORG12;
					if( isDeleted )
						data.name += DEL_MSG;
					data.pos = pos;
				}

				list.add(data);
				pos++;
				return pos;
			}

			protected void onPostExecute(File output) {
				if (output != null)
					NPrinter.sendPrintTask(DocListEx.this, output);

				dismissDialog(WAIT_DLG_ID);
				btnPrint.setEnabled(true);
			};

			protected void onPreExecute() {
				btnPrint.setEnabled(false);
				showDialog(WAIT_DLG_ID);
			};

		}.execute((Void[]) null);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (btnPrint != null)
			btnPrint.setEnabled(isAllowPrint());
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		
		if (btnPrint != null)
			btnPrint.setEnabled(isAllowPrint());
	}
	
	private boolean isAllowPrint(){
		return DocType.getCurDoc() == SalesDoc.instance() || DocType.getCurDoc() == IncassDoc.instance();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case WAIT_DLG_ID:
			return createWaitDlg();
		case R.id.ask_what_print_dlg:
			return createAskWhatPrintDlg();
		case R.id.select_sales:
			return createSelectSalesDialog();
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DLG_FILTER_SELECT:
			prepareFilterDlg(dialog);
		case R.id.select_sales:
			refreshSalesDialog(dialog);
		default:
			super.onPrepareDialog(id, dialog);
		}

	}

	private void prepareFilterDlg(Dialog dialog) {
		CheckBox cbGenDog = (CheckBox) dialog.findViewById(R.id.cbGenDog);
		cbGenDog.setVisibility(DocType.getCurDoc() == IncassDoc.instance() ? View.VISIBLE : View.GONE);
	}

	private Dialog createWaitDlg() {
		ProgressDialog result = new ProgressDialog(this);
		result.setTitle(R.string.please_wait);
		result.setMessage(getString(R.string.print_docs));

		return result;
	}

	@Override
	protected int getFilterLayout() {
		return R.layout.date_selectionex;
	}
	
	@Override
	protected void filterClick(DialogInterface dialog) {
		if(DocType.getCurDoc() == IncassDoc.instance())
			if(((CheckBox)((AlertDialog)dialog).findViewById(R.id.cbGenDog)).isChecked()){
				form = "1";
				cause = "gendog=1";
			}else{
				form = "2";
				cause = "gendog=0";
			}
		else
			cause = null;
		
		super.filterClick(dialog);
	}
	
	@Override
	protected DocListAdapter createListAdapter(DocType docType) {
		return new DocListAdapter(this, docType, saveDatePeriod){
			@Override
			public void fetch(DocType docType, DatePeriod dp, String id, Price p) {
				orgId = id;
				documents.close();
				documents = docType.docList(orgId, order, dp, cause);
				curDocType = docType;
				datePeriod = dp;
				notifyDataSetChanged();
			}
		};
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem item = menu.findItem(OptionsMenuHelper.MNU_DEL_DOC_ID);
		
		if(item != null && DocType.getCurDoc() == ArchSalesDoc.instance())
			item.setVisible(false);
		
		return true;
	}
	
	@Override
	protected DocStatusChangeListener createStatusChangeListener() {
		return new DocStatusChangeListener(){
			@Override
			protected boolean isAllowChangeStatus(CreatableDocument<?> cd) {
				return (DocType.getCurDoc() != ArchSalesDoc.instance()) && super.isAllowChangeStatus(cd);
			}
		};
	}
}

class DocListData implements Cloneable {
	public int pos;
	public String number = "";
	public String name = "";
	public String created = "";
	public String org = "";
	public String sum = "";

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

class IncassSource extends DataSourceAdapter{
	public IncassSource(List<IncassListData> list) {
		items.items.addAll(list);
	}
	
	public String form;
	public String created;
	public String itogo;
	public String start;
	public String finish;
	
	public IncassSourceItems items = new IncassSourceItems();
	
	@Override
	public DataSource getObject(String name) {
		if (name.equals("items"))
			return items;
		return null;
	}
}

class IncassListData {
	public int pos;
	public String org = "";
	public String address = "";
	public String sum = "";
	public int isum;
	public String data = "";
	public long time;
}

class IncassSourceItems extends DataSource {
    public ArrayList<IncassListData> items = new ArrayList<IncassListData>();
	int index = 0;
	
	@Override
	public void startPage() {}

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		return SilentReflector.getFieldValue(value, name, items.get(index), format);
	}

	@Override
	public DataSource getObject(String name) { return this;	}

	@Override
	public boolean haveMoreData() {	return (index + 1 < items.size()); }

	@Override
	public void calculate() {}

	@Override
	public boolean moveNext() {
		index++;
		return (index >= items.size()) ? false : true;
	}
}

class DocListSource extends DataSourceAdapter {
	public String start;
	public String finish;
	public String date;
	public String agent;
	public String buh;
	public int itogo;

	DocListItems items;

	public DocListSource(List<DocListData> data) {
		items = new DocListItems(data);
	}

	@Override
	public DataSource getObject(String name) {
		if (name.equals("items"))
			return items;
		return null;
	}
}

class DocListItems extends DataSource {
	public ArrayList<DocListData> items = new ArrayList<DocListData>();
	int index = 0;

	public DocListItems(List<DocListData> src) {
		this.items.addAll(src);
	}

	@Override
	public void startPage() {
	}

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		return index >= items.size() ? false : SilentReflector.getFieldValue(value, name, items.get(index), format);
	}

	@Override
	public DataSource getObject(String name) {
		return this;
	}

	@Override
	public boolean haveMoreData() {
		return (index + 1 < items.size());
	}

	@Override
	public void calculate() {
	}

	@Override
	public boolean moveNext() {
		index++;
		return (index >= items.size()) ? false : true;
	}

}

class LoadListSource extends DataSourceAdapter{
	public String sumqty = ""; 
	public String packqty = "";
	public String itemqty = "";
	
	LoadListItems items;
	
	public LoadListSource(List<LoadListData> data) {
		items = new LoadListItems(data);
	}
	
	@Override
	public DataSource getObject(String name) {
		if (name.equals("items"))
			return items;
		return null;
	}
}

class LoadListItems extends DataSource{
	public ArrayList<LoadListData> items = new ArrayList<LoadListData>();
	int index = 0;
	
	public LoadListItems(List<LoadListData> src) {
		this.items.addAll(src);
	}
	
	@Override
	public void startPage() {}

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		return index >= items.size() ? false : SilentReflector.getFieldValue(value, name, items.get(index), format);
	}

	@Override
	public DataSource getObject(String name) { return this;	}

	@Override
	public boolean haveMoreData() {	return (index + 1 < items.size()); }

	@Override
	public void calculate() {}

	@Override
	public boolean moveNext() {
		index++;
		return (index >= items.size()) ? false : true;
	}
}

class LoadListData{
	public int num;
	public String name;
	public String qty;
	public String id;
	public String packName;
	public int iqty;
}


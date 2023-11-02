package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PaySale;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.napoleon.printsources.DataSourceAdapter;
import com.grsoft.napoleon.printsources.SilentReflector;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SalesList extends DocList implements OnItemClickListener, DataSetNotify, OnClickListener{
	private View btnPaySale;
	private View btnPrint;
	protected static final int WAIT_DLG_ID = 1;
	
	public static void open(Context context){
		Intent i = new Intent(context, SalesList.class);
		context.startActivity(i);
	}
	
	@Override protected int getViewID() { return R.layout.doclistex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnPaySale = findViewById(R.id.btnPaySale);
		btnPrint = findViewById(R.id.btnPrint);
		
		btnDocFilter.setVisibility(View.GONE);
		btnSend.setVisibility(View.GONE);
		btnDelete.setVisibility(View.GONE);
		btnPaySale.setOnClickListener(this);
		btnPrint.setOnClickListener(this);
		
		unregisterForContextMenu(lvDocs);
	}
	
	protected DocListAdapter createListAdapter(DocType docType){
		docType = SalesDoc.instance();
		return new DocListAdapter(this, docType, saveDatePeriod){
			{
				viewId = R.layout.sales_list_row;
			}
			
			@Override public OnItemClickListener clickListner() { return SalesList.this; }
			
			@Override
			public com.grsoft.napoleon.documents.DocList fillDocList(DocType docType, String orgId, String order, DatePeriod dp) {
				com.grsoft.napoleon.documents.DocList result = super.fillDocList(docType, orgId, order, dp);
				result.sort(comparator);
				return result;
			}
		};
	}
	
	Comparator<Long> comparator = new Comparator<Long>() {
		OrgImpl org = new OrgImpl();
		SalesImpl sale = new SalesImpl();
		
		@Override
		public int compare(Long lhs, Long rhs) {
			int result = 0;
			
			sale.read(lhs, false);
			org.read("id", sale.getId());
			
			int leftKPK = ((OrgEx)org.getData()).kpk;
			int leftIsBlack = ((SalesEx)sale.getData()).isBlack | ((OrgEx)org.getData()).isBlack ;
			String leftName = ((OrgEx)org.getData()).name;
			
			sale.read(rhs, false);
			sale.close();
			org.read("id", sale.getId());
			
			int rightKPK = ((OrgEx)org.getData()).kpk;
			int rightIsBlack = ((SalesEx)sale.getData()).isBlack | ((OrgEx)org.getData()).isBlack;
			String rightName = ((OrgEx)org.getData()).name;
			
			result = leftKPK - rightKPK;
			
			if(result == 0)
				result = leftIsBlack - rightIsBlack;
			
			if(result == 0)
				result = leftName.compareTo(rightName);
			
			return result;
		}
	};
	
	@Override
	protected String getDocText(Org o, com.grsoft.napoleon.documents.Document<?> doc) {
		String result = "";
		
		if (((OrgEx)o).kpk > 0){
			SalesEx s = (SalesEx) doc.getData();
			String text = s.orgAddress.trim();
			result = text;
		}else
			result = super.getDocText(o, doc);
		
		return result;
	};
	
	protected void drawData(View view, Document<?> doc, int position) {
		if( doc != null ) {
			Org o = org.getData();
			o.id = doc.getId();
			boolean readed = org.read();
			
			int color = getDocColor(doc);
			
			TextView tvPos = (TextView) view.findViewById(R.id.tvPos);
			tvPos.setText(Integer.toString(position + 1));
			
			TextView tvName = (TextView) view.findViewById(R.id.tvName);
			String text = "";
			if( readed )
				text = getDocText(o, doc);
			tvName.setText(Html.fromHtml(text));
			tvName.setTextColor(color);
			
			TextView tvDate = (TextView)view.findViewById(R.id.tvDate);
			tvDate.setText(Util.simpleDateFormat.format(doc.getDate()));
			tvDate.setTextColor(color);
			
			TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
			text = docSumText(doc);
			Integer qty = values.get(doc.getRowid());
			if( qty != null ) {
				boolean packView = ((CfgNplW)ConfigManager.getConfig()).isPackView; 
				if( packView && price != null && price.qtyInPack != 0 )
					qty = (int)((long)qty * Consts.QTY_SCALE / price.qtyInPack);
				String qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE);
				if( packView )
					qtyText += " у.";
				
				text += "<br><i>(" + qtyText + ")</i>"; 
			}
			tvSum.setText(Html.fromHtml(text));
			tvSum.setTextColor(color);
			
			SalesEx s = (SalesEx) ((SalesImpl)doc).getData();
			TextView tvNumber = (TextView) view.findViewById(R.id.tvNumber);
			
			if((s.isBlack | ((OrgEx)o).isBlack) > 0)
				s.number = "Ч/"+s.number;
			
			tvNumber.setText(s.number);
			
			TextView tv = (TextView) view.findViewById(R.id.tvIncass);
			tv.setText(Util.IntToScaleStr(((SalesEx)doc.getData()).incass, Consts.SUM_SCALE));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int pos, long arg3) {
		SalesImplEx s = (SalesImplEx) adapter.getItemAtPosition(pos);
		OrgImpl org = new OrgImpl();
		org.read("id", s.getId());
		
		if ((((SalesEx)s.getData()).isBlack | ((OrgEx)org.getData()).isBlack) > 0)
			s.inputIncass(this);
	}

	@Override public void notifyDataSetChanged() { adapter.notifyDataSetChanged();}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.btnPaySale)
			PaySaleList.open(this);
		if(id == R.id.btnPrint)
			print();
	}

	private void print() {
		new AsyncTask<Void, Void, File>() {
			
			@Override
			protected File doInBackground(Void... params) {
				File result = null;
				
				List<PageSource.Data> d1 = new ArrayList<PageSource.Data>();
				OrgImpl org = new OrgImpl();
				
				long sumsum = 0;
				long incasssum = 0;
				long whitesum = 0;
				long resultsum = 0;
				
				for(int i = 0; i < adapter.getCount(); i++){
					SalesImpl si = (SalesImpl)adapter.getItem(i);
					SalesEx s = (SalesEx)si.getData();
					PageSource.Data d = new PageSource.Data();
					d.pos = Integer.toString(i+1);
					org.read("id", s.id);
					
					if(((OrgEx)org.getData()).kpk > 0)
						d.name = s.orgAddress;
					else	
						d.name = org.getData().name; 
					d.type = s.isBlack > 0 ? "нал" : ((OrgEx)org.getData()).isBlack > 0 ? "нал" : "б//н";
					long ds = si.sum();
					d.sum = Util.IntToScaleStr(ds, Consts.SUM_SCALE);
					d.incass = Util.IntToScaleStr(s.incass, Consts.SUM_SCALE);
					
					sumsum += ds;
					incasssum += s.incass;
					
					if((s.isBlack | ((OrgEx)org.getData()).isBlack) == 0)
						whitesum += ds;
					
					d1.add(d);
				}
				
				StringBuilder sb = new StringBuilder();
				
				if (saveDatePeriod != null)
					sb.append("[created] > ").append(saveDatePeriod.begin.getTime()).append(" and [created] < ").append(saveDatePeriod.end.getTime());
				
				final List<Integer> sums = new ArrayList<Integer>();
				final List<PageSource.Data> d2 = new ArrayList<PageSource.Data>();
				DataTraveler.travel(PaySale.class, new DataTraveler.Travel<PaySale>(){
					@Override
					public boolean travel(DataTraveler<PaySale> item) {
						PageSource.Data d = new PageSource.Data();
						d.name = item.data.name;
						d.sum = Util.IntToScaleStr(item.data.sum, Consts.SUM_SCALE);
						d2.add(d);
						sums.add(item.data.sum);
						return true;
					}}, sb.toString());
				
				long oplatsum = 0;
				for(Integer s : sums)
					oplatsum += s;
				
				resultsum = oplatsum + incasssum + whitesum - sumsum;
				
				PageSource p = new PageSource(d1, d2);
				p.sumsum = Util.IntToScaleStr(sumsum, Consts.SUM_SCALE);
				p.incasssum = Util.IntToScaleStr(incasssum, Consts.SUM_SCALE);
				p.whitesum = Util.IntToScaleStr(whitesum, Consts.SUM_SCALE);
				p.resultsum  = Util.IntToScaleStr(resultsum, Consts.SUM_SCALE);
				
				if(saveDatePeriod != null){
					p.start = Util.simpleDateFormat.format(saveDatePeriod.begin);
					
					Calendar c = Calendar.getInstance();
					c.setTime(saveDatePeriod.end);
					c.add(Calendar.DATE, -1);
					p.finish = Util.simpleDateFormat.format(c.getTime());
				}
				
				AgentPrefix ap = AgentPrefix.get();
				p.agent = ap == null ? "" : ap.fullname.length() > 0 ? ap.fullname : ap.name;
				
				result = NPrinter.print(SalesList.this, "salelistpage", p);
				
				return result;
			}

			protected void onPostExecute(File output) {
				if (output != null)
					NPrinter.sendPrintTask(SalesList.this, output);

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
	protected Dialog onCreateDialog(int id) {
		if (id == WAIT_DLG_ID)
			return SelectPrinFormDlg.createWaitDlg(this);
		
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		DocType.setCurDoc(SalesDoc.instance());
	}
}

class PageSource extends DataSourceAdapter{
	public String resultsum = "";
	public String sumsum = ""; 
	public String incasssum = "";
	public String whitesum = "";
	public String start = "";
	public String finish = "";
	public String agent = "";
	
	public PageSourceItems items1;
	public PageSourceItems items2;
	
	public PageSource(List<Data> data1, List<Data> data2) {
		items1 = new PageSourceItems(data1);
		items2 = new PageSourceItems(data2);
	}
	
	@Override
	public DataSource getObject(String name) {
		if (name.equals("items1"))
			return items1;
		else if (name.equals("items2"))
			return items2;
		return null;
	}
	
	public static class Data{
		public String pos = "";
		public String name = "";
		public String type = "";
		public String sum = "";
		public String incass = "";
	}
}

class PageSourceItems extends DataSource{
	public ArrayList<PageSource.Data> items = new ArrayList<PageSource.Data>();
	int index = 0;
	
	public PageSourceItems(List<PageSource.Data> src) {
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



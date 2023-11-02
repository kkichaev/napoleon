package com.grsoft.napoleon.printsources;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.content.Context;

public class SalesPrintItems extends DataSource{
	
	protected List<SalesItemPrint> items = new ArrayList<SalesItemPrint>(); 
	
	public int index = 0;
	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int pagepack = 0;
	public int ipageqty = 0;
	public String pageqty = ""; 
	public int ipagesumwtax = 0;
	public String pagesumwtax = "";
	public int ipagesum = 0;
	public String pagesum = "";
	public int ipagesumtax = 0;
	public String pagesumtax = "";
	public int pack = 0;
	public int iqty = 0;
	public String qty = "";
	public int isumwtax = 0;
	public String sumwtax = "";
	public int isumtax = 0;
	public String sumtax = ""; 
	
	public String pageBrutto = "";
	public int ibrutto = 0;
	public String pageWeight = "";
	public int iweight = 0;
	
	protected SalesPrint owner;
	
	public SalesPrintItems(SalesPrint owner) {
		this.owner = owner;
	}
	
	public void add(SalesItemPrint item) { items.add(item); }
	
	public List<SalesItemPrint> getItems() { return items; }
	
	
	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		return (index < items.size() && SilentReflector.getFieldValue(value, name, items.get(index), format)) || 
				SilentReflector.getFieldValue(value, name, this, format);
	}

	@Override
	public void startPage() {
		pagepack = 0;
		ipageqty = 0;
		ipagesumwtax = 0;
		ipagesum = 0;
		ipagesumtax = 0;
		ibrutto = 0;
		iweight = 0;
		
		owner.startPage();
	}

	@Override
	public DataSource getObject(String name) {
		return this;
	}

	@Override
	public boolean haveMoreData() { return (index + 1 < items.size()); }

	@Override public void init(Context context, int res) { 
		index = 0; 

		pagepack = 0;
		ipageqty = 0;
		ipagesumwtax = 0;
		ipagesum = 0;
		ipagesumtax = 0;
		ibrutto = 0;
		iweight = 0;
		
		pack = 0;
		iqty = 0;
		isumwtax = 0;
		isumtax = 0;
	}

	@Override
	public void calculate() {
		if( index >= items.size() )
			return;
		
		SalesItemPrint sip = items.get(index);
		pagepack += sip.pack;
		ipageqty += sip.iqty;
		pageqty = Util.IntToScaleStr(ipageqty, Consts.QTY_SCALE);
		ipagesumwtax += sip.isumwtax;
		pagesumwtax = Util.IntToScaleStr(ipagesumwtax, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		ipagesum += sip.isum;
		pagesum = Util.IntToScaleStr(ipagesum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		ipagesumtax += sip.isumtax;
		pagesumtax = Util.IntToScaleStr(ipagesumtax, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		
		iweight += sip.iweight;
		ibrutto += sip.ibrutto;
		
		pageWeight = Util.IntToScaleStr(iweight, Consts.WEIGHT_SCALE);
		pageBrutto = Util.IntToScaleStr(ibrutto, Consts.WEIGHT_SCALE);

		pack += sip.pack;
		iqty += sip.iqty;
		qty = Util.IntToScaleStr(iqty, Consts.QTY_SCALE);
		isumwtax += sip.isumwtax;
		sumwtax = Util.IntToScaleStr(isumwtax, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		isumtax += sip.isumtax;
		sumtax = Util.IntToScaleStr(isumtax, Consts.SUM_SCALE, Util.DEC_DELIM, false);
		
		owner.calculate(sip);
	}

	@Override
	public boolean moveNext() {
		index++;
		
		if (index >= items.size())
			return false;
		else{
			return true;
		}
	}
}
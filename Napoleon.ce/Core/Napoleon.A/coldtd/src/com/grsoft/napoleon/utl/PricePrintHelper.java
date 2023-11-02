package com.grsoft.napoleon.utl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.modules.print.BaseDataSource;
import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.NPrinter;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class PricePrintHelper {
	
	public static File printPrice(final Activity context, int which, Document<?> document) {
		File result = null;
		DataSource dataSource = makeDataSource(which, document);
		result = NPrinter.print(context, (which < 2) ? "price" : "rest", dataSource);

		if( result != null ) {
			context.runOnUiThread(new Runnable() {
				@Override public void run() { Toast.makeText(context, "Документ отправлен на печать", Toast.LENGTH_SHORT).show(); }
			});
			WiFiPrinterConfig cfg = WiFiPrinterConfig.get(context);
			WiFiPrint.print(cfg,context, result.getAbsolutePath());
		}
		return result;
	}

	static DataSource makeDataSource(final Integer which, final Document<?> document) {
		@SuppressWarnings("unchecked")
		final CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		
		Firm f = new Firm();
		DbReader r = new DbReader();
		r.select(f, DataObjectInfo.getInstance().getTableName(f.getClass()), null);
		r.close();
		
		final List<PriceEx> priceData = new ArrayList<PriceEx>();
		DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>() {

			@Override
			public boolean travel(DataTraveler<PriceEx> item) {
				if( which == 2 || cs.getItemCost(item.data, document) != 0 ) {
					priceData.add(item.data);
					item.data = new PriceEx();
				}
				return true;
			}
		}, which == 0 ? "priceOrder <> 0" : which == 1 ? "assortimentGroup <> ''" : "vanQty > 0");

		Collections.sort(priceData, new PriceSort(which));
		
		if( which == 2)
			for(int i=0; i<priceData.size(); i++)
				priceData.get(i).priceOrder = i+1;
		
		PricePrintData ppd = new PricePrintData(f, document, priceData);
		return new BaseDataSource(ppd);
	}
}

class PriceSort implements Comparator<PriceEx> {

	int sortType;
	
	public PriceSort(int sortType) { this.sortType = sortType; }
	
	@Override
	public int compare(PriceEx arg0, PriceEx arg1) {
		int cmp;
		if( sortType == 0 )
			cmp = arg0.priceOrder - arg1.priceOrder;
		else if( sortType == 1 )
			cmp = arg0.assortimentGroup.compareTo(arg1.assortimentGroup);
		else
			cmp = arg0.name.compareTo(arg1.name);
		return cmp;
	}
	
}


class PricePrintItem {
	public int order;
	public String id;
	public String name;
	public String unit;
	public String pack;
	
	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int qty;
	
	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int cost;

	@Scale(value=Consts.SUM_SCALE, hideRest=false)
	public int packCost;

	public PricePrintItem(int order, PricePrint src, int cost) {
		this.order = order;
		this.id = src.id;
		this.name = src.name;
		this.cost = cost;
		
		unit = src.unit;
		pack = src.packName;
		qty = (int)((long)src.vanQty * Consts.QTY_SCALE / src.qtyInPack);
		
		packCost = (int)((long)cost * src.qtyInPack / Consts.QTY_SCALE);
	}
}

class PricePrintData {
	public Date date;
	public String cost = "";
	public String firm = "";
	public String address = "";
	public String phone = "";
	
	public String agent = "";
	
	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int qty = 0;
	
	public List<PricePrintItem> items = new ArrayList<PricePrintItem>();
	
	public PricePrintData(Firm firm, Document<?> doc, List<PriceEx> price) {
		date = Util.getDate();

		this.firm = firm.name;
		address = firm.address;
		phone = firm.phone;
		
		AgentPrefix ap = AgentPrefix.get();
		if( ap != null )
			agent = ap.name;
		
		ConfigImpl ci = new ConfigImpl();
		ci.getData().key = "ВидЦены";
		ci.read();
		ci.close();
		ArrayList<CharSequence> values = new ArrayList<CharSequence>();
		DialogHelper.makeList(ci.getData().value, values);
		int selected = 0;
		if( doc.getSumType() < values.size() )
			selected = doc.getSumType();
		if( values.size() > 0 )
			cost = values.get(selected).toString();
		
		@SuppressWarnings("unchecked")
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) doc.getClass());
		for(PriceEx pp : price) {
			int cost = cs.getItemCost(pp, doc);
			PricePrintItem ppi = new PricePrintItem(pp.priceOrder, pp, cost); 
			items.add(ppi);
			qty += ppi.qty;
		}
	}
}


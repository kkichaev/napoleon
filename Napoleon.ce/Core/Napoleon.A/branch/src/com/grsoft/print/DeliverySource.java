package com.grsoft.print;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Vector;

import com.grsoft.dataobjects.ConfigImplEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Util;


public class DeliverySource implements DataSource {
	private DeliveryPrint data;
	private int pageCount;
	
	public DeliverySource(OrderImpl orderImpl){
		this(new DeliveryPrint(orderImpl));
	}
	
	public DeliverySource(DeliveryPrint data){
		this.data = data;
	}
	
	@Override
	public void startPage() {
		pageCount++;
		data.init();
	}
	
	@Override
	public boolean getValue(StringBuilder value, String name) {
		if (value != null && name != null){
			value.setLength(0);
			ConfigImplEx config = new ConfigImplEx();
			
			return config.getValue(value, name) || 
				data.getSupplSorce().getValue(value, name) ||
				data.getValue(value, name);
		}else 
			return false;
	}

	@Override
	public DataSource getObject(String name) {
		return data.getItems();
	}

	@Override
	public boolean haveMoreData() {
		return true;
	}

	@Override
	public void calculate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean moveNext() {
		// TODO Auto-generated method stub
		return false;
	}
}

class DeliveryPrint{
	private SupplSource supplSource = new SupplSource();
	private OrderImpl orderImpl;
	@SuppressWarnings("unused")
	private String created;
	@SuppressWarnings("unused")
	private String number;
	@SuppressWarnings("unused")
	private String date;
	private DeliveriPrintItems items = new DeliveriPrintItems();
	
	public DeliveryPrint(OrderImpl orderImpl){
		this.orderImpl = orderImpl;
		supplSource.setSupplyer("Ф0001");
		
		int index = 1;
		for(OrderItem item : orderImpl.getData().items){
			DeliveryItemPrint dip = new DeliveryItemPrint(item,
					index, orderImpl.getData().sumType);
			items.add(dip);
			index++;
		}
		
	}
	
	public void init(){
		created = Util.simpleDateFormat.format(orderImpl.getData().created);
		number = orderImpl.getData().number;
		date = Util.simpleDateFormat.format(orderImpl.getData().date);
	}
	
	public boolean getValue(StringBuilder value, String name) {
		return SilentReflector.getFieldValue(value, name, this);
	}
	
	public SupplSource getSupplSorce(){
		return supplSource;
	}
	
	public DataSource getItems(){
		return items;
	}
}

@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@interface PrintInfo{
	String name() default "";
}

class SupplSource{
	@SuppressWarnings("unused")
	@PrintInfo(name="ИНН")
	private String inn = "";
	@SuppressWarnings("unused")
	@PrintInfo(name="Наименование")
	private String name = "";
	@SuppressWarnings("unused")
	@PrintInfo(name="Банк")
	private String bank = "";
	@SuppressWarnings("unused")
	@PrintInfo(name="Адрес")
	private String address = "";
	@SuppressWarnings("unused")
	@PrintInfo(name="Телефон")
	private String phone = "";
	@SuppressWarnings("unused")
	private String buh = "";
	@SuppressWarnings("unused")
	private String chief = "";
	
	public SupplSource(){
	}
	   
	public void setSupplyer(String code){
		if (code != null && code.length() > 0){
			FirmImpl firmImpl = new FirmImpl();
			Firm firm = firmImpl.getData();
			firm.id = code;
			
			try{
				if (firmImpl.read()){
					name = firm.name;
				    bank = firm.bank;
				    address = firm.address;
				    phone = firm.phone;
				    inn = firm.inn;
				}
			}finally{
				firmImpl.close();
			}
		}
	}
	
	public boolean getValue(StringBuilder value, String name){
		if (value != null && name != null && name.length() > 0){
			value.setLength(0);
			
			Field fields[] = this.getClass().getDeclaredFields();
			
			for(Field f : fields){
				PrintInfo printInfo = f.getAnnotation(PrintInfo.class);
				if (printInfo != null && 
						printInfo.name().equals(name)){
					try{
						value.append(f.get(this));
						return true;
					}catch(Exception e){
						e.printStackTrace();
						return false;
					}
				}
			}
			
			return false;
		}else
			return false;
	}
}

class SilentReflector{
	public static boolean getFieldValue(StringBuilder value, String name, Object object){
		if (value != null && name != null){
			value.setLength(0);
			try {
				Field fld = object.getClass().getDeclaredField(name);
				fld.setAccessible(true);
				
				if (fld == null)
					return false;
				
				if (fld.getType() == String.class)
				{ 
					value.append((String)fld.get(object));
					return true;
				} else if (fld.getType() == int.class){
					value.append((Integer)fld.get(object));
					return true;
				} else
					return false;
			} catch (Exception e) {	return false; }
		}else
			return false;
	}
}

class DeliveryItemPrint{
	@SuppressWarnings("unused")
	private OrderItem item;
	 @SuppressWarnings("unused")
	private String id = "";
	 private int qty;  // QTY_SCALE
	 private int sum;  // SUM_SCALE
	 private int qtyInPack; // QTY_SCALE
	 private int pack;      // QTY_SCALE
	 @SuppressWarnings("unused")
	private String name = "";
	 @SuppressWarnings("unused")
	private int num;      // SUM_SCALE
	 private int cost;     // SUM_SCALE
	 private int costtax;  // SUM_SCALE
	 @SuppressWarnings("unused")
	private int sumwtax;  // SUM_SCALE
	 private int sumtax;   // SUM_SCALE
	 private int tax;      // SUM_SCALE
	 @SuppressWarnings("unused")
	private int weight;   //WEIGHT_SCALE
	 @SuppressWarnings("unused")
	private String unit = "шт";
	 @SuppressWarnings("unused")
	private String country = "";
	 @SuppressWarnings("unused")
	private String ntd = "";
	
	public DeliveryItemPrint(OrderItem item, int index, int costType){
		this.item = item;
		this.num = index;
		this.sum = FPOperation.itemMul(item.cost, item.qty, Consts.QTY_SCALE);
		
		PriceImpl priceImpl = new PriceImpl();
		priceImpl.getData().id = item.id;
		
		if (priceImpl.read()){
			PricePrint pp = (PricePrint) priceImpl.getData();
			name = pp.name;
			tax = pp.tax1;
			
			if (pp.packName.length() != 0)
				unit = pp.packName;
			
			weight = pp.weight;
			qtyInPack = pp.qtyInPack;
			country = pp.country;
			ntd = pp.ntd;
		}
		
		priceImpl.close();
		
		costtax = item.cost;
		cost = costtax * 100 / (100 + tax);
		sumtax = sum - FPOperation.itemMul(cost, qty, Consts.QTY_SCALE);
		sumwtax = sum - sumtax;
		
		if( qtyInPack == 0 ) qtyInPack = 1;
		pack = 666;//(DivideInPack(qty, qtyInPack, Consts.QTY_SCALE) / Consts.QTY_SCALE) * Consts.QTY_SCALE;
		if( (qty % qtyInPack) != 0 ) pack += Consts.QTY_SCALE;
		tax *= Consts.SUM_SCALE;
	}
}

@SuppressWarnings("serial")
class DeliveriPrintItems extends Vector<DeliveryItemPrint>
implements DataSource{
	int index = 0;
	
	@Override
	public boolean getValue(StringBuilder value, String name) {
		return SilentReflector.getFieldValue(value, name, get(index));
	}

	@Override
	public void startPage() {
		// TODO Auto-generated method stub
	}

	@Override
	public DataSource getObject(String name) {
		return this;
	}

	@Override
	public boolean haveMoreData() {
		return !(index + 1 >= size());
	}

	@Override
	public void calculate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean moveNext() {
		index++;
		
		if (index >= size())
			return false;
		else{
			return true;
		}
	}
}
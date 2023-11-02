package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgMatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class OrderImplEx extends OrderImpl {
	int whIndex = -1; 
	
	public int getWhIndex() {
		int index = -1;
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "Склады";
		if(ci.read()) {
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			index = DialogHelper.makeListWithKey(c.value, values, ((OrderEx)data).whCode);
		}
		ci.close();
		
		if( index < 0 )
			index = 0;
		
		return index;
	}
	
	private List<OrgMatrixItem> matrix;
	public List<OrgMatrixItem> getOrgMatrix() {
		if(matrix != null)
			return matrix;
		
		matrix = new ArrayList<OrgMatrixItem>();
		
		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx)oi.getData();
		org.id = data.id;
		if(oi.read() && org.matrix != null && org.matrix.length() != 0) {
			OrgMatrixImpl mtx = new OrgMatrixImpl();
			OrgMatrix om = mtx.getData();
			om.name = org.matrix;
			if( mtx.read())
				matrix = om.items;
			mtx.close();
		}
		
		oi.close();
		return matrix;
	}
	
	@Override
	public int getItemValue(Price item) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex();

		if( whIndex == 0 )
			return item.qty;
		
		return (whIndex <= ((PriceEx)item).whQty.size()) ? 
				((PriceEx)item).whQty.get(whIndex-1).qty : 
				0;
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex();

		PriceEx pe = (PriceEx)price.getData();
		if( whIndex == 0 )
			super.updatePrice(price, qty);
		else if( whIndex <= pe.whQty.size() ) {
			pe.whQty.get(whIndex-1).qty += qty;
			price.write();
		}
	}
	
	public void updateDiscount(int newDiscount) {
		CostStrategy cs = CostStrategy.getInstance(getClass());
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		
		if( data.items != null ) {
			for(OrderItem i : data.items) {
				OrderItemEx ie = (OrderItemEx)i; 
				if(ie.discount == ((OrderEx)data).discount ) {
					p.id = i.id;
					if( pi.read() ) {
						int cost = cs.getItemCost(p, this);
						ie.discount = newDiscount;
						i.cost = cost + (int)(((long)cost * newDiscount + Consts.SUM_SCALE * Consts.DISCOUNT_SCALE / 2) / (Consts.SUM_SCALE * Consts.DISCOUNT_SCALE));
					}
				}
			}
		}
		((OrderEx)data).discount = newDiscount;
		
		write();
		pi.close();
	}
	
	public int cubature(){
		int result = 0;
		
		if( data.items != null ) {
			PriceImpl p = new PriceImpl();
			p.setReadingFields("cubature");
			
			PriceEx pd = (PriceEx) p.getData();
			for (OrderItem item: data.items) {
				pd.id = item.id;
				
				if( p.read() )
					result += FPOperation.itemMul(item.qty, pd.cubature, Consts.QTY_SCALE);
			}
			p.close();
		}
		
		return result;
	}
}

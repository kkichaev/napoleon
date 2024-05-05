package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class SalesItem extends OrderItem 
implements Cloneable{

	/***
	 * Дата партии
	 */
	@FieldOrder(order=4)
	public Date date;
	
	/***
	 * Номер торговой группы
	 */
	@FieldOrder(order=5)
	public String ntd = "";
	
	@FieldOrder(order=6)
	@Scale(value=Consts.SUM_SCALE)
	public int taxSum;

//	@FieldOrder(order=7)
//	@Scale(value=Consts.SUM_SCALE)
//	public int sum;

	@FieldOrder(order=7)
	@Scale(value=Consts.SUM_SCALE)
	public int costWOtax;

	public SalesItem copy(){
		SalesItem result = null;
		try{
			result = (SalesItem) clone();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void countTax(Sales owner, int tax) {
		if(owner.useTax == 0) {
			tax = 0;
		}

		sum = (int)((long)cost * qty /Consts.QTY_SCALE);
		double val = (double)cost * 100 / (100 + tax);
		taxSum = (int)(sum - val * qty / Consts.QTY_SCALE + 0.5);
		costWOtax = (int)(val + 0.5);
	}
}

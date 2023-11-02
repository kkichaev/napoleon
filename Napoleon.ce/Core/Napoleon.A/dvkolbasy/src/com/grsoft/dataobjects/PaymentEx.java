package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PaymentEx extends Payment implements DgvItem{
	/***
	 * Начальный остаток
	 */
	@Scale(value=Consts.SUM_SCALE)
	public int nachost;
	
	/***
	 * Приход
	 */
	@Scale(value=Consts.SUM_SCALE)
	public int prihod;
	
	/***
	 * Расход
	 */
	@Scale(value=Consts.SUM_SCALE)
	public int rashod;
	
	public String type = "";

	@Override public String getNumber() { return number; }
	@Override public Date getDate() {	return date; }
	@Override public int getNachost() { return nachost; }
	@Override public int getPrihod() { return prihod; }
	@Override public int getKonost() { return (int) sum; }
	@Override public int getRashod() { return rashod; }
	@Override public String getType() { return type; }
}

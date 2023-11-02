package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="pko", keyFields="created")
public class Pko extends CreateDocDataObject {
	
	/***
	 * Номер
	 */
	public String number;
	
	/***
	 * Дата накладной
	 */
	public Date sales;
	
	/***
	 * Номер накладной
	 */
	public String salesnumber;
	
	/***
	 * Сумма
	 */
	@Scale(value=Consts.SUM_SCALE)
	public long sum;
	
	/**
	 * Код фирмы(Firm)
	 */
	public String supplyercode;
	
	/***
	 * Nds
	 */
	public List<NdsItem> nds = new ArrayList<NdsItem>();
}

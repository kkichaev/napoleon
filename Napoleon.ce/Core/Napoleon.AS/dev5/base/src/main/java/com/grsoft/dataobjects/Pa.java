package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

/***
 * PA 
 * power of attorney(доверенность)
 * @author kkichaev
 *
 */

@TableInfo(name="pa", keyFields="created")
public class Pa extends CreateDocDataObject {
	/***
	 * Номер PKO
	 */
	public String docnumber;
	
	/***
	 * PKO created
	 */
	public Date doccreated;
	
	/***
	 * Срок действия
	 */
	public Date period;
	
	/***
	 * Номер
	 */
	public String number;
	
	/***
	 * Сумма
	 */
	@Scale(value=Consts.SUM_SCALE)
	public long sum;
	
	/**
	 * Код фирмы(Firm)
	 */
	public String supplyercode;
}

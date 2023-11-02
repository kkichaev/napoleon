package com.grsoft.dataobjects;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;

public class OrgDogovor extends DataObject {
	/***
	 * Название договора
	 */
	@FieldOrder(order=0)
	public String name;
	
	/***
	 * Код (номер) договора
	 */
	@FieldOrder(order=1)
    public String number;
    
    /***
     * Тип цены
     */
	@FieldOrder(order=2)
    public String ctype;
    
    /***
     * Код фирмы
     */
	@FieldOrder(order=3)
     public String firm;
}

package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class ChekBase extends CreateDocDataObject {
	/**
	 * Чек добавлен в список для инкассации
	 */
	public static int CHEK_IN_COMMON_LIST = 100;
	
	public static int CHEK_ERROR = -1;
	
	public static int CHEK_IS_NEW = 0;

	/**
	 * Чек передан и распечатан (value=1)
	 */
	public static int CHEK_COMMITED = 1;

	/**
	 * Чек в очереди на печать
	 */
	public static int CHEK_IN_QUEUE = 2;
	
	/**
	 * Чек инкассирован
	 */
	public static int CHEK_IS_ACCEPTED = 3;
	
	/**
	 * Чек добавлен в список для инкассации
	 */
	public static int CHEK_HAVE_RETURN = 4;

	/**
	 * Чек закрыт
	 */
	public static int CHEK_CLOSED = 5;

	@Scale(value=Consts.SUM_SCALE)
	public long sum = 0;
	
	public String qrcode = "";
	public String handleRemark = "";
	public int handleStatus = 0;
	
	public String getInfoText() {
		return handleRemark;
	}
	
	public String getStatus() {
		String ret = "";
		if(handleStatus == CHEK_COMMITED )
			ret = "распечатан";
		else if(handleStatus == CHEK_IN_QUEUE)
			ret = "в очереди";
		else if(handleStatus == CHEK_ERROR)
			ret = "ошибка";
		else if(handleStatus == CHEK_IS_ACCEPTED)
			ret = "инкассирован";
		else if(handleStatus == CHEK_HAVE_RETURN)
			ret = "отменен";
		else if(handleStatus == CHEK_CLOSED)
			ret = "закрыт";
		
		return ret;
	}
}

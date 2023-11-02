package com.grsoft.dataobjects;


public class FirmEx extends Firm {
	public String billform = "";
	public String offerform = "";
	public String email = "";
	public String bic = "";
	/***
	 * - корр/счет (на сл. строке после бик)
	 */
	public String kacc = "";
	
	/***
	 *  р/счет (после корр счета)
	 */
	public String account = "";
	
	public String bname = "";
	
	/***
	 * Префикс документа
	 */
	public String prefix = "";
	
	@Override
	public String toString() { return name; }
}

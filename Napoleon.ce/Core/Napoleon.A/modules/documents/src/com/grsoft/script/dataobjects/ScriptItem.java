package com.grsoft.script.dataobjects;

import java.util.Date;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;

public class ScriptItem extends DataObject {
	/**
	 * Документ не инициализирован
	 */
	public static final int DOC_NONE = 0;
	
	/**
	 * Документ создан
	 */
	public static final int DOC_INITED = 1;

	/***
	 * Документ пропущен
	 */
	public static final int DOC_SKIPPED = 3;
	
	@FieldOrder(order=0)
	public String type="";
	
	@FieldOrder(order=1)
	public Date date;

	/**
	 * 0 - не инициализировано
	 * 1 - есть документ
	 */
	@FieldOrder(order=2)
	public int state = DOC_NONE;
	
	@FieldOrder(order=3)
	@FieldVersion(version=1)
	public int pos = 0;
	
	public boolean isCompleete() { return state == ScriptItem.DOC_INITED || state == ScriptItem.DOC_SKIPPED; }
	
	@FieldOrder(order=3)
	@FieldVersion(version=2)
	public String itemID = "";
}

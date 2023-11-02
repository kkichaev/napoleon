package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="answer", keyFields="created")
public class Answer extends CreateDocDataObject {
	/***
	 * Id Анкеты
	 */
	@FieldOrder(order=0)
	public String question = "";
	
	/***
	 * Имя анкеты
	 */
	@FieldOrder(order=1)
	public String qname = "";
	
	/**
	 * Ответы
	 */
	@FieldOrder(order=2)
	public List<AnswerItem> items = new ArrayList<AnswerItem>();
}

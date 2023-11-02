package com.grsoft.ads.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;

@TableInfo(name="answer", keyFields="created")
public class QuestAnswer extends CreateDocDataObject {
	/***
	 * Id Анкеты
	 */
	public String question = "";
	
	/***
	 * Имя анкеты
	 */
	public String qname = "";
	
	/**
	 * Ответы
	 */
	public List<QuestAnswerItem> items = new ArrayList<QuestAnswerItem>();
	
	public String task = ""; 
}

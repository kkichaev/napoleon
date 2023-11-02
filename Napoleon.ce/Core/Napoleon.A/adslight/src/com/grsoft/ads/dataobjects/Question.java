package com.grsoft.ads.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DocDataObject;

@TableInfo(name="question", keyFields="idquest")
public class Question extends DocDataObject{
	/***
	 * Код анкеты
	 */
	public String idquest = "";
	
	/***
	 * Наименование
	 */
	public String name = "";
	
	/***
	 * Действительна с 
	 */
	public Date from;
	
	/***
	 * Действительна по
	 */
	public Date till;
	
	/***
	 * Текстовое описание анкеты
	 */
	public String text = "";
	
	/***
	 * Шаблон HTML документа
	 */
	public String html = ""; 
	
	/***
	 * Параметры
	 */
	public int params = 0;
	
	/***
	 * Вопросы
	 */
	public List<QuestionItem> items = new ArrayList<QuestionItem>();
	
	/***
	 * Порядковый номер
	 */
	public int number;
}

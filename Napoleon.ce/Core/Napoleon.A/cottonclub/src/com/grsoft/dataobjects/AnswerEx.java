package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="answer", keyFields="answerid")
public class AnswerEx extends Answer {
	@FieldOrder(order=3)
	public String answerid;
	
	@FieldOrder(order=4)
	public String price;
	
	@FieldOrder(order=5)
	public int actprezent = 0;
}

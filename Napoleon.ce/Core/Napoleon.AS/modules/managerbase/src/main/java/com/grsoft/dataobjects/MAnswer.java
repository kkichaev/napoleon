package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="answer", keyFields="created", indexes="questionidx")
public class MAnswer extends Answer {
	public String agentid = "";
}

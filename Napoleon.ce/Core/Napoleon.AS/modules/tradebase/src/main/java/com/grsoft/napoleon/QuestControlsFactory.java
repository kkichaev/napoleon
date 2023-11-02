package com.grsoft.napoleon;

import com.grsoft.dataobjects.QuestionItem;

public class QuestControlsFactory {
	public static QuestControlsFactory instance = new QuestControlsFactory();

	public static QuestControlsFactory getInstance() {
		return instance;
	}
	
	public QuestControl createItem(QuestionItem item) {
		QuestControl result = null;
		
		if (item.type == QuestionItem.TEXT)
			result = new QuestText(item);
		else if (item.type == QuestionItem.NUMBER)
			result = new QuestNumber(item);
		else if (item.type == QuestionItem.LIST)
			result = new QuestList(item);
		else if (item.type == QuestionItem.SET)
			result = new QuestSet(item);
		else if (item.type == QuestionItem.BOOLEAN)
			result = new QuestBoolean(item);
		else if (item.type == QuestionItem.DATASET)
			result = new QuestDataset(item);
		else if (item.type == QuestionItem.SPINNER)
			result = new QuestSpinner(item);
		else if (item.type == QuestionItem.IMAGE)
			result = new QuestImage(item);
		else if (item.type == QuestionItem.NUMBER_LIST)
			result = new QuestNumberList(item);
		
		return result;
	}

	public int getItemLayout(){
		return R.layout.quest_item_view;
	}
}

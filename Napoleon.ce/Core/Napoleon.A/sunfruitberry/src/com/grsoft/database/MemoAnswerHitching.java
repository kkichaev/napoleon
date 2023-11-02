package com.grsoft.database;

public class MemoAnswerHitching extends RestoreDocProceeded {
	public MemoAnswerHitching() {
		super();
		makeDocReceiveCondition("created", 0, 1);
	}
}

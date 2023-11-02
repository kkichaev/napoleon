package com.grsoft.database;

import com.grsoft.dataobjects.PinRegAnswer;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PinRegHitching extends Hitching {
	
	PinRegAnswer answer = null;
	
	
	public PinRegHitching() {
		super(PinRegAnswer.class, "PinRegAnswer");
	}
	
	@Override public void onStart() { }
	@Override public void onEnd() { }
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		answer = (PinRegAnswer) rawObject.createDataObject(PinRegAnswer.class);
	}
	
	public PinRegAnswer getAnswer() { return answer; }
}

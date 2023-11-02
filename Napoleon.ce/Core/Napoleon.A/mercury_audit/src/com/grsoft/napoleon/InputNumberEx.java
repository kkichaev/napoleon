package com.grsoft.napoleon;

import com.grsoft.util.InputNumber;

public abstract class InputNumberEx extends InputNumber {
	public abstract int[] getValues();
	public int getValue() { return 0; }
	public void applayInput(int value, Object... params){};
	public abstract void applayInput(int[] value, Object... params);
}

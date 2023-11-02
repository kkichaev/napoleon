package com.grsoft.util;
import com.grsoft.aceteam.R;

/***
 * ¬вод значени€
 * @author kki
 *
 */
public abstract class InputNumber {
	
	protected String editValue = "";
	
	public int priceCost = 0;
	
	public abstract void applayInput(int value, Object... params);
	public abstract long getValue();
	public boolean isInpack(){return false;}
	public boolean isPackCanChange(){return true;}
	public boolean useComma() { return true; }
	public boolean replaceCommaToPlus() { return false; }
	public boolean isValid(int value, Object... params) { return true; }
	
	public void setEditValue(String newValue) { editValue = newValue; }
	
	public InputNumber() {}
	public InputNumber(int priceCost) { this.priceCost = priceCost; }
}

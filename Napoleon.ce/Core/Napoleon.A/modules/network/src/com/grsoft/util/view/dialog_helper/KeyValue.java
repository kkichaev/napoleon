package com.grsoft.util.view.dialog_helper;

public class KeyValue {
	public CharSequence key;
	public CharSequence value;
	
	public KeyValue (String f) {
		int sep = f.indexOf('\t');
		if( sep >= 0 ) {
			key = f.substring(sep+1, f.length());
			value = f.substring(0, sep);
		} else {
			key = "";
			value = f;
		}
			
	}
	
	public KeyValue(String k, String v) {
		key = k;
		value = v;
	}
	
	@Override public String toString() { return value.toString(); }
}

package com.grsoft.dataobjects;

public class SalesEx extends Sales {
	public String dogCode = "";
	public int isGenDoc = 0;

	public boolean isGenDoc() {  return isGenDoc > 0; }
}

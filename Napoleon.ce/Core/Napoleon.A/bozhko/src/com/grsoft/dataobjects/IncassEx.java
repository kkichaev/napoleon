package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class IncassEx extends Incass {
	public String firmCode;
	public List<IncassItem> items = new ArrayList<IncassItem>();
}

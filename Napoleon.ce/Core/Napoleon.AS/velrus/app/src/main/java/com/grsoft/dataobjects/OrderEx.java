package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrderEx extends Order {
	public String costType;
	public int notcomplete = 0;

	public List<BonusItem> bonus = new ArrayList<>();
}

package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	public List<UnitItem> units = new ArrayList<UnitItem>();

    @Override
    public String toString() {
        return name;
    }
}

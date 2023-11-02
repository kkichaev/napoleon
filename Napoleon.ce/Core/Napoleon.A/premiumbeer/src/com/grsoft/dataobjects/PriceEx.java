package com.grsoft.dataobjects;

import com.grsoft.types.Scale;

public class PriceEx extends Price {
	@Scale(value=1000)
    public int tank;
}

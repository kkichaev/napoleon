package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price{
    public String fid = "";
    
	@Scale(value= Consts.SUM_SCALE)
	public int costRRC = 0;
}

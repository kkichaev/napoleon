package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Date;

public class PriceEx extends Price{
    public String fid = "";
    
	@Scale(value= Consts.SUM_SCALE)
	public int costRRC = 0;

	public String task = "";
	public int quant = 0;

	public Date date = new Date();

	@Scale(value=Consts.SUM_SCALE)
	public int arrival = 0;
}

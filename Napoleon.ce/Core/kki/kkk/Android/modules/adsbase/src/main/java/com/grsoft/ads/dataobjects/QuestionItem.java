package com.grsoft.ads.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;

public class QuestionItem extends DataObject {
	public static final int TEXT = 0;
    public static final int NUMBER = 1;
    public static final int LIST = 2;
    public static final int SET = 3;
    public static final int BOOLEAN = 4;
    public static final int DATASET = 5;
    
    @FieldOrder(order=0)
	public String id = "";
    
    @FieldOrder(order=1)
	public int type;
	
    @FieldOrder(order=2)
    public List<QuestionItemValues> values = new ArrayList<QuestionItemValues>();
    
    @FieldOrder(order=3)
	public String iditem = "";
	
}

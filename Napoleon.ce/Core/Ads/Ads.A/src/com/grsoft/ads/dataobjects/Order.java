package com.grsoft.ads.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.ads.database.OrderItem;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;

@TableInfo(name="orders", keyFields = "created")
public class Order extends CreateDocDataObject {
	
	/***
	 * Выполненная
	 */
	public static final int DOING_PARAMS = 0x40000;
	
	/***
	 * Выполненная
	 */
	public static final int DONE_PARAMS = 0x80000;
	
	/**
	 * Отказана
	 */
	public static final int REJECTED = 0x100000;
	
    public String userid = "";
    public Date planbegin;
    public Date planend;
    public String text = "";
    public String client = "";
    public String address = "";
    public Date factbegin;
    public Date factend;
    
    public List<OrderItem> items = new ArrayList<OrderItem>();
    public String number = "";
    public List<OrderWorkType> wtypes = new ArrayList<OrderWorkType>();
}

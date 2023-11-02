package com.grsoft.dataobjects;


import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="kupecaction", indexes = "regionID")
@ServerInfo(name="KupecAction")
public class KupecAction extends DataObject{
  public String regionID = "";
  public String contract = "";
  public String id = "";

  @Scale(value = Consts.SUM_SCALE)
  public int price  = 0;

  @Scale(value = Consts.SUM_SCALE)
  public int shelfPrice = 0;
  public String promoID = "";
}

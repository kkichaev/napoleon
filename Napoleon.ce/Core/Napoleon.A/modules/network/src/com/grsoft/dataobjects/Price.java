/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объект данных Price(список доступных товаров)
 *
 * kki   19/10/2010   creating
 */
package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;

@TableInfo(name="Price", keyFields = "id")
@ServerInfo(name="Price")
public class Price extends DataObject
{
	 public String id = ""; 
     
     @Scale(value=1)
     public int folderID;
     
     @Scale(value=1000)
     public int qtyInPack;
     
     public String name = "";
     
     // это поле для поиска переведено в верхний регистр :(
     public String srchName = "";
     
     @Scale(value=1000)
     public int qty;
     
     @Scale(value=1000)
     public int weight;
     
     public String unitName = "";
     
     @Scale(value=1)
     public int color;
     
     public List<CostItem> cost = new ArrayList<CostItem>();
     
     public int hidden = 0;
}

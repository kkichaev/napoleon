/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объект данных Price(список доступных товаров)
 *
 * kki   19/10/2010   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

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

     @Scale(Consts.QTY_SCALE)
     public int vanQty = 0;

     @Scale(Consts.WEIGHT_SCALE)
     public int brutto = 0;

     public String country = "";
     public String countryCode = "";

     public String ntd = "";

     public int tax1;

     public String unit = "";
     public String unitCode = "";

     public String packName = "";
     public String packCode = "";

     @Scale(Consts.SUM_SCALE)
     public int akciz = 0;

     public List<PriceQtyItem> whQty = new ArrayList<PriceQtyItem>();

     public List<PriceUnit> units = new ArrayList<>();

     /**
      * Бит - номер склада, где qty > 0
      */
     public long whStates = 0;

     public void updateWhState() {
          for (int i = 0; i < whQty.size(); i++){
               PriceQtyItem q = whQty.get(i);

               if (q.qty > 0)
                    whStates |= (1 << i);
          }
     }
}

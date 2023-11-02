/*
 * Copyright (C), 2007 - 2010, ƒенис ћос€гин
 *
 * ѕоследние продажи, OffTake
 *
 *  ert   29/08/2010   creating
 */
#ifndef __OFF_TAKE_LAST_SALES_H
#define __OFF_TAKE_LAST_SALES_H

struct OffTakeItemData
{
   DWORD qty;  // кол-во товара поступившее от предыдущего посещени€ (включа€ тот день), до текущего (не включа€ этот день)
   DWORD rest; // остаток на дату
   DWORD ret;

   OffTakeItemData()
   {
      qty = 0;
      rest = 0;
      ret = 0;
   }
};

struct OffTakeData
{
   FILETIME date;

   typedef std::map<std::wstring, OffTakeItemData> PriceSales;
   PriceSales items;
};

//
// дата посещени€ - начало дн€ (так проще работать с накладными)
// если есть приход после последнего посещени€ - их не учитываем
// сортировка дней по возрастанию
//
class OffTakeHolder : public std::vector<OffTakeData>
{
public:
   void Load(std::vector<ItemSales> *sales, bool fromOrders, const wchar_t* orgID, const wchar_t* itemID);
   void UpdateLastRest(std::vector<ItemSales> *sales, const wchar_t* itemID, DWORD newRest, DWORD newRet = 0);
   void ClearCache() { clear(); curOrg.clear(); }

protected:
   void LoadData(const wchar_t* orgID, bool fromOrders);

   void LoadRemnants();
   void LoadOrders();
   void LoadDelivery();

   void LoadRets();

   OffTakeData* Find(const FILETIME& date);

   std::wstring curOrg;
};
extern OffTakeHolder offTakeHolder;
DWORD GetOffTakeCoef(const wchar_t* itemId);

#endif

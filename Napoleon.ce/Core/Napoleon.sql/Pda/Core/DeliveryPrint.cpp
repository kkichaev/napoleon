/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Реализация функций накладной
 *
 *  ert   17/05/2008   creating
 */
#include "stdafx.h"
//#include <FormEntries.h>

#include "DoPrint.h"

#include <Module.h>
#include <StdFuncs.h>
#include "ObjImpl.h"
#include "DocImpl.h"

//
//--------------------- Delivery Print --------------------
//

DeliveryPrint::DeliveryPrint(const OrderImpl &dlv)
{
   created = dlv.created;
   date = dlv.date;

   number = sh.Add(dlv.docNum);
   remark = sh.Add(dlv.remark);
   link = -1;

   flags = 0;

   pack = qty = sumwtax = sumtax = sumtax10 = sumtax18 = sum = 0;

   int ctr = QTY_SCALE;
   vector_t<OrderItem>::const_iterator i = dlv.items.begin();
   for( ; i != dlv.items.end(); i++, ctr += QTY_SCALE )
   {
      DeliveryItemPrint di((*i), &sh, ctr, dlv.sumType);

      qty += di.qty;
      sumwtax += di.sumwtax;
      sumtax += di.sumtax;
      sum += di.sum;

      pack += di.pack;

      if( di.tax == 10 )
         sumtax10 += di.sumtax;
      else if( di.tax == 18 )
         sumtax18 += di.sumtax;

      items.push_back(di);
   }

   wchar_t buf[50];
   std::wstring txt;
   DigToText(&txt, ctr / QTY_SCALE - 1);
   buf[0] = txt[1];
   buf[1] = L'\0';
   txt[1] = *CharUpper(buf);
   numText = sh.Add(txt.c_str()+1);

   DigToText(&txt, sum / SUM_SCALE);
   wsprintf(buf, L" руб. %02d коп.", sum % SUM_SCALE);

   txt += buf;
   buf[0] = txt[1];
   buf[1] = L'\0';
   txt[1] = *CharUpper(buf);
   sumText = sh.Add(txt.c_str()+1);

   DigToText(&txt, qty / QTY_SCALE);
   buf[0] = txt[1];
   buf[1] = L'\0';
   txt[1] = *CharUpper(buf);
   qtyText = sh.Add(txt.c_str()+1);

   OrgImpl oi;
   oi.id = dlv.id;
   oi.Read();

   name = sh.Add(oi.name);
   address = sh.Add(oi.address);
   phone = sh.Add(oi.phone);
   inn = sh.Add(oi.inn);
   bank = sh.Add(oi.bank);
   id = sh.Add(dlv.id);

   TotalPageText = L"";

   suppl.SetSupplyer(dlv.supplCode);
   //wsprintf(buf, L"\t%d", dlv.account);
   //account = sh.Add(buf);
}

DeliveryItemPrint::DeliveryItemPrint(const OrderItem &item, StringHolder *sh, DWORD num, DWORD costType)
{
   this->num = num;
   id = item.id;
   qty = item.qty;
   sum = ItemSum(item.cost, qty);

   PriceImpl pi;
   pi.id = item.id;
   pi.Read();

   name = sh->Add(pi.name);
   tax = pi.tax1;

   //costtax = CostManager::GetCost(pi.id, costType);
   costtax = item.cost;
   cost = costtax * 100 / (100 + tax);

   if( *pi.packName == L'\0' )
   {
      unit = L"шт.";
      unitCode = L"796";
   }
   else 
   {
      unit = sh->Add(pi.packName);
      unitCode = sh->Add(pi.unitCode);
   }

   sumtax = sum - ItemSum(cost, qty);
   sumwtax = sum - sumtax;

   weight = pi.weight;

   qtyInPack = pi.qtyInPack;
   if( qtyInPack == 0 ) qtyInPack = QTY_SCALE;
   pack = (DivideInPack(qty, qtyInPack, QTY_SCALE) / QTY_SCALE) * QTY_SCALE;
   if( (qty % qtyInPack) != 0 ) pack += QTY_SCALE;

   tax *= SUM_SCALE;

   country = sh->Add(pi.country);
   countryCode = sh->Add(pi.countryCode);
   ntd = sh->Add(pi.ntd);
}

BEGIN_TYPE_REFLECTION(DeliveryItemPrint)
   REGISTER_STRING_MEMBER(DeliveryItemPrint, id)
   REGISTER_LONG_SCALE_MEMBER2(DeliveryItemPrint, qty, QTY_SCALE, true)
   REGISTER_LONG_SCALE_MEMBER(DeliveryItemPrint, sum, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER2(DeliveryItemPrint, qtyInPack, QTY_SCALE, true)
   REGISTER_LONG_SCALE_MEMBER2(DeliveryItemPrint, pack, QTY_SCALE, true)

   REGISTER_STRING_MEMBER(DeliveryItemPrint, name)
   REGISTER_LONG_SCALE_MEMBER2(DeliveryItemPrint, num, QTY_SCALE, true)
   REGISTER_LONG_SCALE_MEMBER2(DeliveryItemPrint, tax, SUM_SCALE, true)
   REGISTER_LONG_SCALE_MEMBER(DeliveryItemPrint, cost, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(DeliveryItemPrint, costtax, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(DeliveryItemPrint, sumwtax, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(DeliveryItemPrint, sumtax, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(DeliveryItemPrint, weight, WEIGHT_SCALE)
   REGISTER_LONG_SCALE_MEMBER2(DeliveryItemPrint, tax, SUM_SCALE, true)
   REGISTER_STRING_MEMBER(DeliveryItemPrint, unit)
   REGISTER_STRING_MEMBER(DeliveryItemPrint, unitCode)
   REGISTER_STRING_MEMBER(DeliveryItemPrint, country)
   REGISTER_STRING_MEMBER(DeliveryItemPrint, countryCode)
   REGISTER_STRING_MEMBER(DeliveryItemPrint, ntd)
END_TYPE_REFLECTION(DeliveryItemPrint)

BEGIN_TYPE_REFLECTION(DeliveryPrint)
   REGISTER_FILETIME_MEMBER(DeliveryPrint, created)
   REGISTER_FILETIME_MEMBER(DeliveryPrint, date)
   REGISTER_STRING_MEMBER(DeliveryPrint, id)
   REGISTER_STRING_MEMBER(DeliveryPrint, number)
   REGISTER_STRING_MEMBER(DeliveryPrint, remark)
   REGISTER_ULONG_MEMBER(DeliveryPrint, flags)
   REGISTER_ULONG_MEMBER(DeliveryPrint, link)
   REGISTER_COLLECTION_MEMBER(DeliveryPrint, items, DeliveryItemPrint)

   REGISTER_STRING_MEMBER(DeliveryPrint, name)
   REGISTER_STRING_MEMBER(DeliveryPrint, address)
   REGISTER_STRING_MEMBER(DeliveryPrint, phone)
   REGISTER_STRING_MEMBER(DeliveryPrint, inn)
   REGISTER_STRING_MEMBER(DeliveryPrint, bank)

   REGISTER_LONG_SCALE_MEMBER2(DeliveryPrint, qty, QTY_SCALE, true)
   REGISTER_LONG_SCALE_MEMBER2(DeliveryPrint, pack, QTY_SCALE, true)
   REGISTER_LONG_SCALE_MEMBER(DeliveryPrint, sum, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(DeliveryPrint, sumwtax, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(DeliveryPrint, sumtax, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(DeliveryPrint, sumtax10, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(DeliveryPrint, sumtax18, SUM_SCALE)

   REGISTER_LONG_SCALE_MEMBER2(DeliveryPrint, pageqty, QTY_SCALE, true)
   REGISTER_LONG_SCALE_MEMBER2(DeliveryPrint, pagepack, QTY_SCALE, true)
   REGISTER_LONG_SCALE_MEMBER(DeliveryPrint, pagesum, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(DeliveryPrint, pagesumwtax, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(DeliveryPrint, pagesumtax, SUM_SCALE)

   REGISTER_STRING_MEMBER(DeliveryPrint, numText)
   REGISTER_STRING_MEMBER(DeliveryPrint, sumText)
   REGISTER_STRING_MEMBER(DeliveryPrint, qtyText)
   REGISTER_STRING_MEMBER(DeliveryPrint, TotalPageText)
END_TYPE_REFLECTION(DeliveryPrint)

//
//--------------------- Suppl Source --------------------
//
SupplSource::SupplSource()
{
   name = L"";
   bank = L"";
   address = L"";
   phone = L"";
   inn = L"";
}

void SupplSource::SetSupplyer(const wchar_t* code)
{
   FirmImpl fi;
   fi.id = (wchar_t*)code;
   if( fi.Read() )
   {
      name = sh.Add(fi.name);
      bank = sh.Add(fi.bank);
      address = sh.Add(fi.address);
      phone = sh.Add(fi.phone);
      inn = sh.Add(fi.inn);
   }
}

bool SupplSource::GetValue(std::wstring* value, const wchar_t* name)
{
   if( wcscmp(name, L"Наименование") == 0 )
   {
      *value = this->name;
      return true;
   }
   if( wcscmp(name, L"Банк") == 0 )
   {
      *value = bank;
      return true;
   }
   if( wcscmp(name, L"Адрес") == 0 )
   {
      *value = address;
      return true;
   }
   if( wcscmp(name, L"Телефон") == 0 )
   {
      *value = phone;
      return true;
   }
   if( wcscmp(name, L"ИНН") == 0 )
   {
      *value = inn;
      return true;
   }

   return false;
}


//
//--------------------- Delivery Source --------------------
//
DeliverySource::DeliverySource(DeliveryPrint *data) : ReflectableSource(data)
{
   pageCount = 0;
}

bool DeliverySource::GetValue(std::wstring *value, const wchar_t *name)
{
   NapoleonConfig cfg;
   if( cfg.ReadValue(value, name) || ((DeliveryPrint*)data)->suppl.GetValue(value, name) )
      return true;

   return ReflectableSource::GetValue(value, name);
}

void DeliverySource::StartPage()
{
   pageCount++;

   ((DeliveryPrint*)data)->pageqty = 0;
   ((DeliveryPrint*)data)->pagepack = 0;
   ((DeliveryPrint*)data)->pagesum = 0;
   ((DeliveryPrint*)data)->pagesumwtax = 0;
   ((DeliveryPrint*)data)->pagesumtax = 0;

   wchar_t buf[50];
   std::wstring txt;
   DigToText(&txt, pageCount);
   buf[0] = txt[1];
   buf[1] = L'\0';
   txt[1] = *CharUpper(buf);

   ((DeliveryPrint*)data)->TotalPageText = ((DeliveryPrint*)data)->sh.Add(txt.c_str());
}

void DeliverySource::NextCollectionItem(const IReflectableData &idata)
{
   ((DeliveryPrint*)data)->pageqty += ((DeliveryItemPrint&)idata).qty;
   ((DeliveryPrint*)data)->pagepack += ((DeliveryItemPrint&)idata).pack;
   ((DeliveryPrint*)data)->pagesum += ((DeliveryItemPrint&)idata).sum;
   ((DeliveryPrint*)data)->pagesumwtax += ((DeliveryItemPrint&)idata).sumwtax;
   ((DeliveryPrint*)data)->pagesumtax += ((DeliveryItemPrint&)idata).sumtax;
}
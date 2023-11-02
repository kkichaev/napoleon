/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Тип документа
 *
 *  ert   05/11/2010   creating
 */
#include "stdafx.h"
#include "DocType.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DBImpl.h>

#include "FormEntries.h"
#include "OrgDocs.h"
#include "ObjImpl.h"
#include <DataReader.h>
#include <NetExchange.h>
#include <BalanceRcv.h>

DeliveryRcvr::DeliveryRcvr() : Base(L"Обработка накладных...", true)
{
}

bool DeliveryRcvr::Write(const DeliveryImpl& data)
{
   ROWID id = table->Write(data);
   if( id == NO_ROWID ) return false;

   DocTypeManager::AddDeliveryInfo(data, id);
   return true;
}

PaymentRcvr::PaymentRcvr() : Base(L"Обработка оплат...", true) {}

bool PaymentRcvr::Write(const PaymentImpl& data)
{
   ROWID id = table->Write(data);
   if( id == NO_ROWID ) return false;

   DocTypeManager::AddPaymentInfo(data, id);
   return true;
}

struct CmpOrgSum
{
   bool operator() (const OrgSums& _l, const OrgSums &_r) const 
   {
      int cmp = wcscmp(_l.type, _r.type);
      return (cmp < 0) ? true : (cmp == 0) ? (wcscmp(_l.id, _r.id) < 0) : false; 
   }
};
std::set<OrgSums, CmpOrgSum> orgSumAdd;
StringHolder orgSumHolder;

#ifdef ORD_DLV_BIND
struct CmpFileTime
{
   bool operator() (const FILETIME& _l, const FILETIME&_r) const 
   { 
      return CompareFileTime(&_l, &_r) < 0; 
   }
};
std::map<FILETIME, std::wstring, CmpFileTime> orderDlv;
#endif

DWORD SumDeliveryItems(const Delivery& data)
{
   DWORD sum = 0;
   vector_t<DeliveryItem>::const_iterator i = data.items.begin();
   for( ; i != data.items.end(); i++ )
      sum += i->sum;

   return sum;
}

void DocTypeManager::AddDeliveryInfo(const IReflectableData &data, const ROWID &id)
{
   OrgSums os;
   os.id = orgSumHolder.Add(((const Delivery&)data).id);
   os.type = dtDelivery;
   os.sum = SumDeliveryItems((const Delivery&)data);

#ifdef ORD_DLV_BIND
   orderDlv[((const Delivery&)data).created] = ((const Delivery&)data).number;
#endif

   std::pair<std::set<OrgSums, CmpOrgSum>::iterator, bool> ins = orgSumAdd.insert(os);
   if( ins.second == false )
      (*ins.first).sum += os.sum;

#ifdef MAKE_BALANCE
   OrgSums os2;
   os2.id = os.id;
   os2.type = dtBalance;
   os2.sum = os.sum;

   ins = orgSumAdd.insert(os2);
   if( ins.second == false )
      (*ins.first).sum += os2.sum;
#else
   OrgSums os2;
   os2.id = os.id;
   os2.type = dtBalance;
   os2.sum = ((const Delivery&)data).sumD;

   if( os2.sum != 0 )
   {
      ins = orgSumAdd.insert(os2);
      if( ins.second == false )
         (*ins.first).sum += os2.sum;
   }
#endif
}

void DocTypeManager::AddPaymentInfo(const IReflectableData &data, const ROWID &id)
{
   OrgSums os;
   os.id = orgSumHolder.Add(((const Payment&)data).id);
   os.type = dtBalance;
   os.sum = ((const Payment&)data).sum;

   std::pair<std::set<OrgSums, CmpOrgSum>::iterator, bool> ins = orgSumAdd.insert(os);
   if( ins.second == false )
      (*ins.first).sum += os.sum;
}

void DocTypeManager::UpdateDocInfo()
{
   OrgSumImpl os;
   SQLTable table(os.Name());

   SQLCheckTable(os);

   std::wstring sql(L"DELETE FROM ");
   sql += os.Name();
   sql += L" WHERE type in ('";
   sql += dtBalance;
   sql += L"','";
   sql += dtDelivery;
   sql += L"')";
   SQLTable::Execute(sql.c_str());
   SQLTable::EndTransaction();

   SQLTable::StartTransaction(200);
   std::set<OrgSums, CmpOrgSum>::const_iterator i = orgSumAdd.begin();
   for( ; i != orgSumAdd.end(); i++ )
   {
      table.Write((*i));
   }

#ifdef ORD_DLV_BIND
   OrderImpl o;
   SQLTable ordUpdate(o.Name());
   std::map<FILETIME, std::wstring, CmpFileTime>::const_iterator oi = orderDlv.begin();
   for( ; oi != orderDlv.end() ; oi++ )
   {
      o.created = oi->first;
      if( o.Read() )
      {
         o.number = (wchar_t*)oi->second.c_str();
         ordUpdate.Update(o, L"number", o.RID());
      }
   }
   orderDlv.clear();
#endif

   SQLTable::EndTransaction();

   orgSumAdd.clear();
   orgSumHolder.Clear();
}

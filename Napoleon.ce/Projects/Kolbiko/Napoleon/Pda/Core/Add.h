/*
 * Copyright (C), 2007 - 2011, Денис Мосягин
 *
 * Реализация функций заказа
 *
 *  ert   22/00/2011   creating
 */
#ifndef __ADD_H_KOLBIKO
#define __ADD_H_KOLBIKO

#include <StdFuncs.h>
#include <DataReader.h>
#include <NetExchange.h>
#include <OrgRmnts.h>
#include <Exchange.h>

extern FILETIME sheduleDate;
extern wchar_t dtReturn[];

struct FolderCoef : public IReflectableData
{
   DWORD id;
   WORD  coef; // SUM_SCALE

   DECLARE_TYPE_REFLECTION(FolderCoef)
}; 

class FolderCoefImpl : public DBImpl<FolderCoef>
{
public:
   FolderCoefImpl() : DBImpl(L"FolderCoef") {}

   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }

};

class ReturnImpl : public OrderImpl
{
public:
   ReturnImpl() : OrderImpl(L"Returns", dtReturn) { instanceFlags |= ifNoUpdatePrice; }

   virtual bool Init(const ROWID &orgID);
   virtual void EditDocument(UINT retForm);
   virtual bool CanRemove() const;

   virtual bool HideRemnants() const { return true; }
   virtual bool CreateDocument(const ROWID &orgID);

   virtual bool CheckQty() const { return false; }
   virtual bool EditDetail();

   static ReturnImpl* GetAssociated(const OrderImpl& src);

   //virtual DWORD Sum() const { return 0; }

   void HiddenInit(const ROWID &orgID);
};

class RetRcvr : public DBObjAliasRcvr<ReturnImpl>
{
public:
   RetRcvr() : DBObjAliasRcvr(L"Обработка возвратов...",false, L"RetSend") {}

   virtual bool Write(const ReturnImpl& data)
   {
      ReturnImpl& d = const_cast<ReturnImpl&>(data);
      if( IsStartDate(data.created) )
      {
         __int64 val = *(__int64*)&data.date;
         // перейдем в начало дня
         val -= (val % ((__int64)24 * 3600 * 10000000));
         // добавим остаток от деления номера на число секунд в дне
         __int64 add = ((__int64)(_wtoi(data.number) % (24 * 3600))) * 10000000;
         val += add;
         d.created = *(FILETIME*)&val;
      }
      d.params |= (ofProceeded | ofExported);

      org.id = d.id;
      org.Read();
      WORD coef = org.coef;
      if( coef == 0 )
         coef = SUM_SCALE;

      vector_t<OrderItem>::iterator oi = d.items.begin();
      for( ; oi != d.items.end(); oi++ )
      {
         p.id = oi->id;
         if( p.Read() )
         {
            DWORD cost = p.cost[0];
            oi->cost = (coef == SUM_SCALE) ? cost : (int)((__int64)cost * coef / SUM_SCALE);
         }
      }
      return DBObjAliasRcvr::Write(data);
   }

   PriceImpl p;
   OrgImpl org;
};

class OrgRestRcvr : public DBObjAliasRcvr<OrgRemnantsImpl>
{
public:
   OrgRestRcvr() : DBObjAliasRcvr(L"Обработка остатков...",false, L"OrgRmntsSend") {}

   virtual bool Write(const OrgRemnantsImpl& data)
   {
      OrgRemnantsImpl& d = const_cast<OrgRemnantsImpl&>(data);
      d.ClearDirty(NULL, false);
      return DBObjAliasRcvr::Write(data);
   }
};

#endif
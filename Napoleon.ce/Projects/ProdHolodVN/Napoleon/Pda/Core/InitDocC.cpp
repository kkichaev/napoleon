/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Загрузчик типов докуменов дополнительный
 *
 *  ert   14/03/2008   creating
 */
#include "stdafx.h"
#include <Exchange.h>
#include <DocType.h>
#include <InitDoc.h>
#include "Add.h"

wchar_t dtPlans[] = L"Планы";
wchar_t dtBonus[] = L"Бонусы";
wchar_t dtReturn[] = L"Возвраты";
wchar_t dtPKO[] = L"ПКО";

struct PKOFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new PKOImpl(); }
   virtual void Free(IDocument* document) const { delete (PKOImpl*)document; }
} pkoFactory;

struct PKOType : public DocType
{
   PKOType() : DocType(dtPKO, &pkoFactory, dtHaveSum)
   {
   }

   virtual bool ShowInDocumentList() const { return true; }
   virtual const wchar_t* SendTypeName() const { return L"PKO"; }
};

struct PlanFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new PlanImpl(); }
   virtual void Free(IDocument* document) const { delete (PlanImpl*)document; }
} planFactory;

struct BonusFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new BonusImpl(); }
   virtual void Free(IDocument* document) const { delete (BonusImpl*)document; }
} bonusFactory;

struct ReturnFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new ReturnImpl(); }
   virtual void Free(IDocument* document) const { delete (ReturnImpl*)document; }
} returnFactory;

struct PlanType : public DocType
{
   PlanType() : DocType(dtPlans, &planFactory, dtHaveSum)
   {
   }

   virtual bool ShowInDocumentList() const { return false; }
};

struct BonusType : public DocType
{
   BonusType() : DocType(dtBonus, &bonusFactory, 0)
   {
   }

   virtual const wchar_t* SendTypeName() const { return L"Bonus"; }
};

struct ReturnType : public DocType
{
   ReturnType() : DocType(dtReturn, &returnFactory, 0)
   {
   }

   virtual const wchar_t* SendTypeName() const { return L"Returns"; }
};

static SQLTable* MakePCDCommand(const OrderProceeded& data, const wchar_t* tableName)
{
   SQLTable *table = new SQLTable(tableName);

   const DataReflector& type = data.GetType();
   std::vector<MemberType*> params;
   std::wstring sql;

   wchar_t ibuf[10];
   _itow(ofProceeded, ibuf, 10);

   sql.assign(L"UPDATE ");
   sql.append(tableName);
   sql.append(L" SET params = params | ");
   sql.append(ibuf);

#ifdef POD_COMMENT   
   sql += L", podRemark = ?";

   MemberType *remT = (MemberType*)&type.Type(L"remark");
   params.push_back(remT);

#endif
   sql += L" WHERE created = ?";

   MemberType *idT = (MemberType*)&type.Type(L"created");
   params.push_back(idT);

   if( !table->PrepareCommand(sql, params) )
   {
      delete table;
      table = NULL;
   }

   return table;
}

class BonusPCD : public IProceededHandler
{
public:
  BonusPCD() {}

   virtual const wchar_t* Name() const { return L"Bonus"; }
   virtual SQLTable* Prepare(const OrderProceeded& data) { return MakePCDCommand(data, BonusImpl().Name()); }
   virtual bool SetProceeded(SQLTable *table, const OrderProceeded& data) { return table->ExecCommand(data); }
};

class ReturnsPCD : public IProceededHandler
{
public:
  ReturnsPCD() {}

   virtual const wchar_t* Name() const { return L"Return"; }
   virtual SQLTable* Prepare(const OrderProceeded& data) { return MakePCDCommand(data, ReturnImpl().Name()); }
   virtual bool SetProceeded(SQLTable *table, const OrderProceeded& data) { return table->ExecCommand(data); }
};


void InitCustomDocTypeSet()
{
   docTypeManager.insert(new PlanType());
   docTypeManager.insert(new BonusType());
   docTypeManager.insert(new ReturnType());
   docTypeManager.insert(new PKOType());

   AddProceededHandler(new BonusPCD());
   AddProceededHandler(new ReturnsPCD());
}

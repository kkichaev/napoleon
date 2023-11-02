/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Восход дополнения
 *
 *  ert   02/08/2010   creating
 */
#ifndef __ADD_VOSHOD_H
#define __ADD_VOSHOD_H

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DocImpl.h>
#include <PriceForm.h>

const int PLAN_SCALE = 10;

extern wchar_t dtPlans[];
extern wchar_t dtBonus[];
extern wchar_t dtReturn[];

#define PRICE_INDEX_FILE L"NplPrice.idx"

struct AgentPrefix : public IReflectableData
{
   wchar_t* id;
   wchar_t* login;
   wchar_t* password;
   wchar_t* prefix;

   DECLARE_TYPE_REFLECTION(AgentPrefix)
};

struct PlanSkuItem : public IReflectableData
{
   wchar_t* id;

   DECLARE_TYPE_REFLECTION(PlanSkuItem)
};

// в планах даты "с" и "по" с временем 0 (ResetTime) дата "по" не включается в план
// начало следующего плана совпадает с концом предыдущего
struct Plan : public IReflectableData
{
   wchar_t* name;

   FILETIME date; // дата создания
   DWORD  plan; // SUM_SCALE процент выполнения плана
   DWORD fact;  // SUM_SCALE

   DECLARE_TYPE_REFLECTION(Plan)
};

class PlanImpl : public DBImpl<Plan>, public IDocument
{
public:
   PlanImpl() : DBImpl(L"Plans") {}

   virtual const wchar_t*  KeyFields() const { return L"name,date"; }
   virtual const wchar_t** Indexes() const { return NULL; }
   
// ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return L""; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const { return L""; }

   virtual IReflectableData* Data() { return this; }

   virtual DWORD Sum() const { return 0; }
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return NULL; }

   void IDocument::EditDocument(UINT) {}
   // ------------------------------- Instance functions -----------------------------------
protected:
};

class AgentPrefixImpl : public DBImpl<AgentPrefix>
{
public:
   AgentPrefixImpl() : DBImpl(L"AgentPrefix") {}

   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }

   static bool GetPrefix(std::wstring *prefix);
};

class BonusImpl : public OrderImpl
{
public:
   BonusImpl() : OrderImpl(L"Bonus", dtBonus) {}

   virtual bool Init(const ROWID &orgID);
   virtual void EditDocument(UINT retForm);
   virtual bool CanRemove() const;

   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool HideRemnants() const { return true; }

   virtual bool EditDetail();
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
};

class DiscountImpl : public DBImpl<Discount>
{
public:
   DiscountImpl() : DBImpl(L"discounts") {}

   virtual const wchar_t*  KeyFields() const { return L"id,dogovor"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

void RefreshDiscount(OrderImpl* oi);
int PriceToIndex(const Price& price);

void OpenPlans(const wchar_t *svDocType);

// это для матрицы
struct SKUData
{
   std::wstring name;
   std::vector<ROWID> rids;
};
void GetSKUPlans(std::vector<SKUData> *data);

struct ItemData
{
   wchar_t *dog;
   wchar_t *cost;
};

class UnitList : public CWindowImpl<CListViewCtrl, CListViewCtrl>
{
public:
   DECLARE_WND_CLASS(L"ADDR_LIST")

   BEGIN_MSG_MAP(UnitList)
      MESSAGE_HANDLER(OCM_DRAWITEM, DrawItem)
   END_MSG_MAP()

   void Init(const wchar_t *ido, const wchar_t *selected);
   void UpdateLayout();
   LRESULT DrawItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/);

protected:
   StringHolder sh;
};

struct PKO : public IReflectableData
{
   enum State { Exported = 1, Proceeded = 4, Fixed = 8 };

   FILETIME date;
   wchar_t *id;
   wchar_t *number;

   DWORD    sum;

   wchar_t* supplyer;
   wchar_t* dogId;
   WORD     fiscal;

   FILETIME created;
   DWORD params;

#ifdef GPS_POS
   int   latitude;
   int   longitude;
#endif

   DECLARE_TYPE_REFLECTION(PKO)
};

class PKOImpl : public DBImpl<PKO>, public IDocument, public ICreatableDocument
{
public:
   //PaymentImpl(const ROWID &orgID);

// ------------------------------- DBImpl -----------------------------------

   PKOImpl() : DBImpl(L"PKO") {}

   virtual const wchar_t*  KeyFields() const { return L"id,number,dogId,fiscal"; }
   virtual const wchar_t** Indexes() const { return NULL; }

// ------------------------------- IDocument functions -----------------------------------
   
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const { return number; }

   virtual IReflectableData* Data() { return this; }

   virtual DWORD Sum() const { return sum; }
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   virtual IDocument* Copy();

   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);
   virtual bool CanRemove() const;
   virtual bool RemoveDocument() { return Remove(); } 
   virtual void EditDocument(UINT retForm);
   virtual bool WriteDocument() { return Write(); }

   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   virtual const wchar_t* SendText(int count) const { return L"Передача оплат"; }

   virtual bool IsDirty() const { return (params & Exported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }
};

void LoadDogovors(CComboBox &dogs, const wchar_t* id, const wchar_t* firm, const wchar_t *selCode);
void FreeDogovors();

extern wchar_t dtPKO[];

#endif
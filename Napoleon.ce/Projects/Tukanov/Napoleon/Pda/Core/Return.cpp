/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Возвраты
 *
 *  ert   02/08/2010   creating
 */
#include "stdafx.h"
#include <Exchange.h>
#include <DocType.h>
#include <InitDoc.h>
#include "Add.h"
#include <Invoice.h>
#include <StdFuncs.h>
#include <BaseDialog.h>
#include <NplConfig.h>

class Selector : public IPriceSelect
{
public:
   Selector(OrderImpl* r)
   {
      if( r->rid == NO_ROWID )
         r->Write();
      rid = r->rid; 
   }

   virtual bool IsSelected(const wchar_t* id) const { return false; }
   virtual void Select(const wchar_t* id) {}
   virtual void Backing()
   {
      ReturnImpl *r = new ReturnImpl();
      r->Read(rid);
      r->EditDocument(0);
   }

   virtual bool CanSelect() const { return false; }
   virtual bool CanBacking() const { return true; }

protected:
   ROWID rid;
};

class ReturnData : public InvoiceDlvData
{
public:
   ReturnData(OrderImpl *_order, bool retToDocList) : InvoiceDlvData(_order, retToDocList) {}

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   //bool EditDetail();

   virtual bool Adding()
   {
      OrderImpl *co = order;
      order = NULL;
      SelectPriceItem(new Selector(co), co);
      return false;
   }
};

class ReturnForm : public Invoice
{
public:
   DECLARE_FORM(ReturnForm, IDD_RETURN)

   BEGIN_MSG_MAP(ReturnForm)
      CHAIN_MSG_MAP(Invoice)
   END_MSG_MAP()

   virtual DWORD GetMenuBarID() const { return IDD_INVOICE; }
};

IMPLEMENT_FORM(ReturnForm)

bool ReturnImpl::EditDetail()
{
   return OrderImpl::EditDetail();
}

bool ReturnImpl::Init(const ROWID &orgID)
{
   ClearMembers(this);

   OrgImpl org;
   org.Read(orgID);

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &created);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &date);

   id = holder.Add(org.id);
   rid = NO_ROWID;

   sumType = 0;

   return EditDetail();
}

bool ReturnImpl::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) == true )
   {
      SelectPriceItem(new Selector(this), this);
      return true;
   }

   return false;
}

bool ReturnImpl::CanRemove() const
{
   bool needDelete = false;
   int id = MessageBox(GetActiveWindow(), L"Удалить документ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION);
   if( id == IDYES )
      needDelete = true;

   return needDelete;
}

void ReturnImpl::EditDocument(UINT retForm)
{
   _Module.GetFrame()->Load(IDD_RETURN, new ReturnData(this, (retForm != IDD_ORDER_LIST)));
}

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"Кол-во", L"qty", 50 },
};
const ListFormData::Header* ReturnData::GetHeader() const
{
   return header;
}

int ReturnData::ColumnsCount() const
{
   return sizeof(header) / sizeof(header[0]);
}

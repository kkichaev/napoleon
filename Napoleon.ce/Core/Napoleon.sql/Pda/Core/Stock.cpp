/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Информаиця по организации
 * 
 *  ert   10/09/2009   creating
 */ 
#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>
#include <DocType.h>
#include <ListForm.h>
#include "FormEntries.h"
#include <StdFuncs.h>
#include "OrgDocs.h"
#include "DocType.h"
#include "InitDoc.h"
#include "ObjImpl.h"

#include <algorithm>
#include <set>

wchar_t dtStock[] = L"Ассортимент";

struct StockFactory : public IDocFactory
{
   virtual IDocument* Create() const { return NULL; }
   virtual void Free(IDocument* document) const { }
} stockFactory;


static ListFormData::Header infoHeader[] =
{
   { ListFormData::Header::Left, L"Название", L"name", 120 },
   { ListFormData::Header::Right, L"Количество", L"qty", 30 },
};

struct StockItem : public IReflectableData
{
   wchar_t *name;
   DWORD qty;

   bool operator < (const StockItem &_item) const { return (wcscmp(name, _item.name) < 0); }

   DECLARE_TYPE_REFLECTION(StockItem)
};

struct StockData : public ListFormData
{
   StockData (const wchar_t *id);

   virtual int Count() const { return items.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   virtual bool Selecting(int index) { return false; }

   virtual const Header *GetHeader() const { return infoHeader; }
   virtual int ColumnsCount() const { return 2; }

   virtual DWORD ItemCount() const { return itemCount; }

   virtual const DataReflector& DataType() const { return StockItem().GetType(); }

   vector_t<StockItem> items;
   StringHolder holder;
   OrgImpl org;
   DWORD itemCount;
};

class StockForm : public ListForm
{
 public:
   StockForm() {}

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(StockForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_ADD, Adding)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   virtual void LoadMenuBar(bool hideSIP)
   {
      ListForm::LoadMenuBar(hideSIP);

      TBBUTTONINFO bi;
      bi.cbSize = sizeof(bi);
      bi.dwMask = TBIF_TEXT;
      bi.pszText = (LPWSTR)dtStock;
      menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);

#ifdef STOP_LIST
      if( ((StockData*)data)->org.flags & ofStopList )
         menuBar.EnableButton(IDC_ADD, FALSE);
#endif

      sumLabel.CreateLabel(menuBar.m_hWnd);
      sumLabel.SetSum(((StockData*)data)->ItemCount());
   }

   virtual DWORD GetResourceID() const { return IDD_STOCK; }
   virtual DWORD GetMenuBarID() const { return IDD_STOCK; }
   virtual DWORD GetMenuID() const { return 0; }

   DECLARE_FORM(StockForm, IDD_STOCK);

protected:
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled)
   {
      if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;

      const DocType *dt = SelectDocType(&menuBar, m_hWnd);
      if( dt != NULL && dt->Type() != dtStock)
         dt->OpenForm(((StockData*)data)->org.id, NULL);
      return 0;
   }

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
   {
      OpenOrgList(dtStock);
      return 0;
   }

   LRESULT Adding(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
   {
      const DocType *dt = docTypeManager.GetDocType(dtOrder);
      dt->CreateDocument(((StockData*)data)->org.RID());
      return 0;
   }

   virtual void UpdateLayout(bool forceRecalc);
};

struct QTYCmp
{
   bool operator()(const StockItem& _left, const StockItem& _right) const
   {
      return (_left.qty > _right.qty);
   }
};

StockData::StockData(const wchar_t *id) : itemCount(0)
{
   org.id = (wchar_t*)id;
   org.Read();

   DocumentList *dl;
   const DocType *dt = docTypeManager.GetDocType(dtDelivery);
   if( dt->GetDocuments(id, &dl) )
   {
      StringHolder sh;
      std::set<StockItem> its;

      int count = dl->Count();
      for( int i=0; i<count; i++ )
      {
         DeliveryImpl& d = *(DeliveryImpl*)(dl->Get(i)->Data());
         vector_t<DeliveryItem>::const_iterator di = d.items.begin();
         for( ; di != d.items.end(); di++ )
         {
            StockItem si;

            itemCount += di->qty;

            si.name = di->id;
            std::set<StockItem>::iterator fi = its.find(si);
            if( fi == its.end() )
            {
               si.name = sh.Add(di->id);
               si.qty = di->qty;
               its.insert(si);
            } else
            {
               fi->qty += di->qty;
            }
         }
      }
      delete dl;

      PriceImpl price;
      std::set<StockItem>::iterator fi = its.begin();
      for( ; fi != its.end(); fi++ )
      {
         price.id = fi->name;
         price.Read();

         StockItem si;
         si.name = holder.Add(price.name);
         si.qty = fi->qty;

         items.push_back(si);
      }

      sort(items.begin(), items.end(), QTYCmp());
   }

   itemCount = (itemCount / QTY_SCALE) * SUM_SCALE;
}

bool StockData::Get(IReflectableData* data, int index) const
{
   if( index >= (int)items.size() ) return false;

   *(StockItem*)data = items[index];
   return true;
}

bool StockForm::SetData(IFormData *_data)
{
   if( ListForm::SetDataEx(_data, 2) == false )
      return false;

   CStatic title(GetDlgItem(IDC_ORG_TITLE));
   title.SetWindowTextW(((StockData*)data)->org.name);

   LoadMenuBar(true);
   return true;
}

void StockForm::UpdateLayout(bool forceRecalc)
{
   const int gap = 2;
   CRect bounds;
   CStatic title(GetDlgItem(IDC_ORG_TITLE));
   CRect rc;

   GetClientRect(bounds);

   rc.top = 2;
   rc.bottom = 2;
   rc.left = 2;
   rc.right = bounds.right-4;

   CalcTextHeight(title.m_hWnd, &rc);
   title.MoveWindow(rc, FALSE);

   SetListLayout(forceRecalc, rc.bottom + 2);
}

IMPLEMENT_FORM(StockForm);

BEGIN_TYPE_REFLECTION(StockItem)
   REGISTER_STRING_MEMBER(StockItem, name)
   REGISTER_LONG_SCALE_MEMBER2(StockItem, qty, QTY_SCALE, true)
END_TYPE_REFLECTION(StockItem)

void OpenOrgStock(const wchar_t *id)
{
   _Module.GetFrame()->Load(IDD_STOCK, new StockData(id));
}

OrgStock::OrgStock() : DocType(dtStock, &stockFactory, 0)
{
}

void OrgStock::OpenForm(const wchar_t *orgid, OrgDocsList* curForm) const
{
   OpenOrgStock(orgid);
}

bool OrgStock::GetDocuments(const wchar_t *orgid, DocumentList **orgDocs,  
                            const wchar_t *whereStr, const wchar_t *orderStr) const
{
   *orgDocs = NULL;
   return false;
}

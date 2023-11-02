/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Информаиця по организации
 * 
 *  ert   02/06/2009   creating
 */ 
#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>
#include <DocType.h>
#include "Add.h"
#include <ListForm.h>
#include "FormEntries.h"
#include <StdFuncs.h>
#include <OrgDocs.h>
#include <DocType.h>
#include <ObjImpl.h>

static ListFormData::Header dogHeader[] =
{
   { ListFormData::Header::Left, L"Договор", L"name", 50 },
   { ListFormData::Header::Left, L"с", L"from", 25 },
   { ListFormData::Header::Left, L"по", L"till", 25 },
};

struct DogovorsData : public ListFormData
{
   DogovorsData() : dogovors(NULL) {}

   virtual int Count() const { return (dogovors==NULL) ? 0 : dogovors->size(); }
   virtual bool Get(IReflectableData* data, int index) const
   {
      if( dogovors == NULL || index >= (int)dogovors->size() ) return false;
      const Dogovor& dog = (*dogovors)[index];
      *(Dogovor*)data = dog;

      return true;
   }
   virtual bool Selecting(int index) { return false; }

   virtual const Header *GetHeader() const { return dogHeader; }
   virtual int ColumnsCount() const { return 3; }

   virtual const DataReflector& DataType() const { return Dogovor().GetType(); }

   vector_t<Dogovor> *dogovors;
};

static ListFormData::Header infoHeader[] =
{
   { ListFormData::Header::Left, L"Свойство", L"name", 50 },
   { ListFormData::Header::Left, L"Значение", L"value", 50 },
};

struct InfoData : public ListFormData
{
   InfoData (const wchar_t *id);

   virtual int Count() const { return org.props.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   virtual bool Selecting(int index) { return false; }

   virtual const Header *GetHeader() const { return infoHeader; }
   virtual int ColumnsCount() const { return 2; }

   virtual const DataReflector& DataType() const { return OrgProp().GetType(); }

   OrgImpl org;
};

class InfoForm : public ListForm
{
 public:
   InfoForm() {}

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(InfoForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_ADD, Adding)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   void LoadMenuBar()
   {
      menuBar.m_hWnd = NULL;
      menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

      TBBUTTONINFO bi;
      bi.cbSize = sizeof(bi);
      bi.dwMask = TBIF_TEXT;
      bi.pszText = (LPWSTR)L"Инфо";
      menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);

      if( ((InfoData*)data)->org.flags & ofStopList )
         menuBar.EnableButton(IDC_ADD, FALSE);
   }

   virtual DWORD GetResourceID() const { return IDD_ORG_INFO; }
   virtual DWORD GetMenuID() const { return 0; }

   DECLARE_FORM(InfoForm, IDD_ORG_INFO);

 protected:
   DogovorsData dogovors;
   ListViewMultiLine dogovorsList;

 protected:
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled)
   {
      if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;

      const DocType *dt = SelectDocType(&menuBar, m_hWnd);
      if( dt != NULL && dt->Type() != dtOrgInfo)
         dt->OpenForm(((InfoData*)data)->org.id, NULL);

      return 0;
   }

   LRESULT Adding(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
   {
      const DocType *dt = docTypeManager.GetDocType(dtOrder);
      dt->CreateDocument(((InfoData*)data)->org.RID());
      return 0;
   }

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
   {
      OpenOrgList(dtOrgInfo);
      return 0;
   }

   virtual void UpdateLayout(bool forceRecalc);
   virtual LRESULT SetCellInfo(LPNMHDR hdr);
};

InfoData::InfoData(const wchar_t *id)
{
   org.id = (wchar_t*)id;
   org.Read();
}

bool InfoData::Get(IReflectableData* data, int index) const
{
   if( index >= (int)org.props.size() ) return false;

   const OrgProp& prop = org.props[index];
   *(OrgProp*)data = prop;

   return true;
}

static void LoadOrgData(const wchar_t *id, int *totalQTY, int *lastQTY, WORD *lastPremium)
{
   *totalQTY = 0;
   *lastQTY = 0;
   *lastPremium = 0;

   DocumentList *dl;
   const DocType *doctype = docTypeManager.GetDocType(dtOrder);
   if( !doctype->GetDocuments(id, &dl, L"", L"ORDER BY data DESC") ) return;

   bool lastLoaded = false;
   unsigned size = dl->Count();
   for( unsigned i=0; i<size; i++ )
   {
      OrderImpl *o = (OrderImpl*)(dl->Get(i)->Data());

      if( !lastLoaded )
      {
         PriceImpl price;
         std::vector<OrderItem>::const_iterator i = o->items.begin();
         for( ; i != o->items.end(); i++ )
         {
            price.id = i->id;
            price.Read();

            // cost 1 минимальная цена от нее считаем наценку
            DWORD diff = DivideInPack(i->cost, price.cost[0], 10000) - 10000;
            *lastPremium += (WORD)diff;

            DWORD qty = i->qty / QTY_SCALE;
            *lastQTY += qty;
            *totalQTY += qty;
         }
         lastLoaded = true;
      } else
      {
         std::vector<OrderItem>::const_iterator i = o->items.begin();
         for( ; i != o->items.end(); i++ )
            *totalQTY += i->qty / QTY_SCALE;
      }
   }

   delete dl;
}

bool InfoForm::SetData(IFormData *_data)
{
   if( ListForm::SetData(_data) == false )
      return false;

   dogovors.dogovors = &((InfoData*)data)->org.dogovors;
   dogovorsList.SubclassWindow(GetDlgItem(IDC_DOGOVORS));
   SetupListCtrl(&dogovorsList, 1, &dogovors);

   CStatic title(GetDlgItem(IDC_ORG_TITLE));
   title.SetWindowTextW(((InfoData*)data)->org.name);

   int tq, lq;
   WORD lp;
   wchar_t buf[300];

   LoadOrgData(((InfoData*)data)->org.id, &tq, &lq, &lp);

   wsprintf(buf, L"Продажи: % 5d шт. наценка %02d.%02d%%", 
      tq, ((InfoData*)data)->org.premium / SUM_SCALE, ((InfoData*)data)->org.premium % SUM_SCALE);
   GetDlgItem(IDC_INFO).SetWindowTextW(buf);

   wsprintf(buf, L"Посл.: % 5d шт. наценка %02d.%02d%%",  lq, lp / SUM_SCALE, lp % SUM_SCALE);
   GetDlgItem(IDC_INFO1).SetWindowTextW(buf);

   LoadMenuBar();
   return true;
}

LRESULT InfoForm::SetCellInfo(LPNMHDR hdr)
{
   if( hdr->hwndFrom != dogovorsList.m_hWnd )
      return ListForm::SetCellInfo(hdr);

   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( !(di->item.mask & LVIF_TEXT) )
      return TRUE;

   int index = di->item.iItem;
   const DataReflector& reflector = dogovors.DataType();
   IReflectableData *rd = reflector.Create();
   if( dogovors.Get(rd, index) && CanSetColumn(index, di->item.iSubItem))
   {
      const MemberType &tp = reflector.Type(dogovors.GetHeader()[di->item.iSubItem].field);
      tp.ToString(*rd, di->item.pszText, di->item.cchTextMax);
   } else
      *di->item.pszText = L'\0';

   delete rd;
   return TRUE;
}

void InfoForm::UpdateLayout(bool forceRecalc)
{
   const int gap = 2;
   CRect bounds;
   CStatic title(GetDlgItem(IDC_ORG_TITLE));
   RECT rc = {0};

   GetClientRect(bounds);
   bounds.left++;
   bounds.right-=2;

   rc.top = 0;
   rc.bottom = 0;
   rc.left = 1;
   rc.right = bounds.right;

   CalcTextHeight(title.m_hWnd, &rc);
   int height = rc.bottom - rc.top;
   title.MoveWindow(bounds.left, 0, bounds.right, height, FALSE);
   int top = height + gap;

   rc.top = 0;
   rc.bottom = 0;
   rc.left = 1;
   rc.right = bounds.right-10;
   CStatic info(GetDlgItem(IDC_INFO));
   CalcTextHeight(info.m_hWnd, &rc);
   height = rc.bottom - rc.top;
   info.MoveWindow(bounds.left, top, bounds.right-10, height, FALSE);
   top += height + gap;

   rc.top = 0;
   rc.bottom = 0;
   rc.left = 1;
   rc.right = bounds.right-10;
   CStatic info1(GetDlgItem(IDC_INFO1));
   CalcTextHeight(info1.m_hWnd, &rc);
   height = rc.bottom - rc.top;
   info1.MoveWindow(bounds.left, top, bounds.right-10, height, FALSE);
   top += height + gap;

   dogovorsList.GetWindowRect(&rc);

   SetListLayout(forceRecalc, top + rc.bottom - rc.top + gap);
   SetListLayout(forceRecalc, top, rc.bottom - rc.top, &dogovorsList, &dogovors);
}

IMPLEMENT_FORM(InfoForm);

void OpenOrgInfo(const wchar_t *id)
{
   _Module.GetFrame()->Load(IDD_ORG_INFO, new InfoData(id));
}

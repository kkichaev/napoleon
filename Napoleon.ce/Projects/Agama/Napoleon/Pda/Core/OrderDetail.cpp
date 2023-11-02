/*
* Copyright (C), 2007, Денис Мосягин
*
* Детали заказа
*
*  ert   07/02/2008   creating
*/
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include <Exchange.h>
#include <ObjImpl.h>
#include <DocImpl.h>

#include "NplConfig.h"
#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"

#include "PropDialog.h"
#include <TopApp.h>

#include "Add.h"
#include <Visit.h>
#include <StdFuncs.h>

BOOL CALLBACK DisableChildsProc(HWND hwnd, LPARAM lParam);

class OrderPage : public PropPage
{
public:
   OrderPage(WORD wID, ATL::_U_STRINGorID title = (LPCTSTR)NULL) : PropPage(wID, title) {}
   virtual void Save(OrderImpl *order) = 0;

   void DisableChilds()
   {
      EnumChildWindows(m_hWnd, DisableChildsProc, (LPARAM)((HWND)GetDlgItem(IDCANCEL)));
   }

   void ResizeWindow(UINT id, WORD width)
   {
      CWindow wnd(GetDlgItem(id));
      CRect rc;

      wnd.GetWindowRect(rc);
      ScreenToClient(rc);
      rc.right = width - offset;
      wnd.MoveWindow(rc);
   }

};

class OrderDetailDialog : public PropDialog
{
public:
   OrderDetailDialog(OrderImpl *_order);

   ~OrderDetailDialog() {}

   bool OnOK()
   {
      std::vector<PropPage*>::iterator i = pages.begin();
      for( ;i != pages.end(); i++ )
         ((OrderPage*)(*i))->Save(order);

      return true; 
   }

   OrderImpl* Order() const { return order; }

protected:
   OrderImpl *order;
   std::wstring title;
};


class OrderPage1 : public OrderPage
{
public:
   OrderPage1() : OrderPage(IDD_ORDER_DETAIL, L"Основная") {}

   BEGIN_MSG_MAP(OrderDetailDialog)
      CHAIN_MSG_MAP(OrderPage)
      REFLECT_NOTIFICATIONS()
   END_MSG_MAP()

   //StaticAnchor unitAddress;
   //AddressList  addressList;
   UnitList units;

protected:
   virtual void Init()
   {
      OrderImpl *order = ((OrderDetailDialog*)owner)->Order();

      if( order->IsExported() )
         DisableChilds();

      SYSTEMTIME st;
      FileTimeToSystemTime(&order->date, &st);
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

      FileTimeToSystemTime(&order->supplDate, &st);
      ((CDateTimePickerCtrl)GetDlgItem(IDC_DELIVERY_DATE)).SetSystemTime(GDT_VALID, &st);

      Preference p;
      p.Load();
      if( p.addFlags & afAutoVisit )
         CheckDlgButton(IDD_VISIT, BST_CHECKED);

      OrgImpl o;
      o.id = order->id;
      if( o.Read() )
      {
         NapoleonConfig cfg;

         std::wstring cost;
         
         order->sumType = o.costype;
         cfg.GetStringItem(&cost, COST_TYPE, o.costype);

         if( order->sumType >= MAX_NUM_COST )
            order->sumType = MAX_NUM_COST-1;

         std::wstring costext(L"Тип цены: ");
         costext += cost;

         SetDlgItemText(IDC_COST_TEXT, costext.c_str());

         if( o.units.size() > 0 )
         {
            if( order->unitCode == 0 )
               order->unitCode = o.units.front().id;

            units.Init(*this, IDC_UNIT_LIST, IDC_UNIT_TEXT_LABEL, IDC_UNIT_TEXT, o, order->unitCode);
         }
      }
   }

   virtual void Sizing(WORD wdh, WORD hgh)
   {
      OrderPage::Sizing(wdh, hgh);

      CRect urect;
      int visitWidth;
      CWindow v(GetDlgItem(IDD_VISIT));
      HDC dc = v.GetDC();
      HFONT hFont = v.GetFont();
      if( hFont == NULL )
         hFont = (HFONT)GetStockObject(SYSTEM_FONT);

      std::wstring val;
      GetString(&val, v.m_hWnd);
      HGDIOBJ svFont = SelectObject(dc, hFont);
      DrawText(dc, val.c_str(), val.size(), urect, DT_CALCRECT|DT_WORDBREAK);
      SelectObject(dc, svFont);
      v.ReleaseDC(dc);
      visitWidth = urect.Width() + 3 * GetSystemMetrics(SM_CXSMICON) / 2;

      CWindow costText(GetDlgItem(IDC_COST_TEXT));
      costText.GetWindowRect(urect);
      ScreenToClient(urect);
      costText.MoveWindow(offset, urect.top, wdh - 4 * offset - visitWidth, urect.Height());

      v.MoveWindow(wdh - 2 * offset - visitWidth, urect.top, visitWidth, urect.Height());

      units.UpdateLayout(wdh, hgh);
   }

   virtual void Save(OrderImpl *order)
   {
      if( m_hWnd == NULL )
         return;

      SYSTEMTIME st;
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
      SystemTimeToFileTime(&st, &order->date);

      ((CDateTimePickerCtrl)GetDlgItem(IDC_DELIVERY_DATE)).GetSystemTime(&st);
      SystemTimeToFileTime(&st, &order->supplDate);

      order->unitCode = units.GetSelectedItemCode();
      if( order->unitCode < 0 )
         order->unitCode = 0;

      if( order->rid == NO_ROWID && IsDlgButtonChecked(IDD_VISIT) == BST_CHECKED )
      {
         OrgImpl o;
         o.id = order->id;

         if( o.Read() )
         {
            VisitImpl v;

            v.Init(o.rid);
            v.unitCode = order->unitCode;
            v.remark = L"Автовизит";
            v.Write();
         }
      }
   }
};

class OrderPage2 : public OrderPage
{
public:
   OrderPage2() : OrderPage(IDD_DETAIL_PAGE2, L"Дополнительно") {}

   virtual void Init()
   {
      OrderImpl *order = ((OrderDetailDialog*)owner)->Order();

      if( order->IsExported() )
         DisableChilds();

      GetDlgItem(IDC_REMARK).SetWindowText(order->remark);
   }

   void GetDlgItemRect(CRect &bounds, UINT id)
   {
      GetDlgItem(id).GetWindowRect(bounds);
      ScreenToClient(bounds);
   }

   virtual void Sizing(WORD wdh, WORD hgh)
   {
      OrderPage::Sizing(wdh, hgh);

      CRect bounds, bounds2;
      GetDlgItemRect(bounds, IDC_REMARK);
      GetDlgItemRect(bounds2, IDCANCEL);
      GetDlgItem(IDC_REMARK).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds2.top - bounds.top - offset);
   }

   virtual void Save(OrderImpl *order)
   {
      if( m_hWnd == NULL )
         return;

      CWindow wnd(GetDlgItem(IDC_REMARK));
      int len = wnd.GetWindowTextLength();

      wchar_t *buf = (wchar_t*)malloc((len + 1)* sizeof(wchar_t));
      wnd.GetWindowText(buf, len+1);
      order->AssignRemark(buf);
      free(buf);
   }
};

OrderDetailDialog::OrderDetailDialog(OrderImpl *_order) : order(_order)
{
   //OrgImpl o;
   //o.id = order->id;
   //o.Read();
   //title = o.name;
   //SetTitle(title.c_str());

   AddPage(new OrderPage1());
   AddPage(new OrderPage2());
}

bool EditOrderDetail(OrderImpl *order)
{
   TopApp::EnableDoneButton(true);

   OrderDetailDialog dlg(order);
   bool res = (dlg.DoModal() == IDOK);

   TopApp::EnableDoneButton(false);

   return res;
}

//
//--------------------------------- Address List ----------------------------------
//
void AddressList::Init()
{
   CRect rc;
   GetClientRect(rc);

   HFONT hf = GetFont();
   LOGFONT lf;
   GetObject(hf, sizeof(lf), &lf);
   lf.lfHeight *= 3;
   HFONT newFont = CreateFontIndirect(&lf);
   SetFont(newFont);

   InsertColumn(0, L"Название", LVCFMT_LEFT, rc.Width());
   SetExtendedListViewStyle(LVS_EX_FULLROWSELECT);
   ModifyStyle(0, LVS_REPORT|LVS_SHOWSELALWAYS|LVS_SINGLESEL|WS_VSCROLL);

   SetFont(hf);
}

void AddressList::UpdateLayout(int top)
{
   CRect rc;
   GetParent().GetClientRect(rc);
   MoveWindow(rc.left, top, rc.Width(), rc.Height() - top);

   SetColumnWidth(0, rc.Width()-GetSystemMetrics(SM_CXVSCROLL)-2);
}

LRESULT AddressList::OnNotify(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   LPNMHDR hdr = (LPNMHDR)lParam;
   switch( hdr->code )
   {
   case LVN_GETDISPINFO:
   {
      NMLVDISPINFO *di = (NMLVDISPINFO*)lParam;
      if( dataHandler )
      {
         if( !dataHandler->SetData(di) )
            *di->item.pszText = L'\0';
      } else
         *di->item.pszText = L'\0';
      break;
   }

   case NM_CLICK:
      if( handler ) handler->Click(this);
      else bHandled = FALSE;
      break;
   }
   return 0;
}

LRESULT AddressList::DrawItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
{
   LPDRAWITEMSTRUCT ds = (LPDRAWITEMSTRUCT)lParam;
   
   COLORREF textColor;
   HBRUSH bkBrsh;
   if( ds->itemState & ODS_SELECTED )
   {
      bkBrsh = GetSysColorBrush(COLOR_HIGHLIGHT);
      textColor = GetSysColor(COLOR_HIGHLIGHTTEXT);
   } else
   {
      bkBrsh = GetSysColorBrush(COLOR_WINDOW);
      textColor = GetSysColor(COLOR_WINDOWTEXT);
   }
   FillRect(ds->hDC, &ds->rcItem, bkBrsh);
   ::SetTextColor(ds->hDC, textColor);

   if( ds->itemState & ODS_FOCUS )
      DrawFocusRect(ds->hDC, &ds->rcItem);

   CRect textBounds(ds->rcItem);
   textBounds.InflateRect(-1, -1);

   wchar_t buf[500];
   GetItemText(ds->itemID, 0, buf, sizeof(buf)/sizeof(buf[0]));
   DrawText(ds->hDC, buf, -1, textBounds, DT_WORDBREAK);

   return 0;
}

void AddressList::Refresh(DWORD count)
{
   SetItemCount(count);
   if( count > 0 )
      RedrawItems(GetTopIndex(), count);
}

//
//----------------------------------- UnitList --------------------------------
//
UnitList::UnitList() : search(IDC_FIND, IDC_FIND), inSearch(false), selCode(-1)
{
}

bool UnitList::Init(CWindow owner, UINT addrListID, UINT unitLabelID, UINT unitTextID, const Org& o, int code)
{
   this->owner = owner;

   addres.SubclassWindow(owner.GetDlgItem(addrListID));
   addres.Init();
   addres.ShowWindow(SW_HIDE);
   addres.SetHandler(this);
   addres.SetDataHandler(this);

   search.SetHandler(owner.m_hWnd, this);
   search.ShowWindow(SW_HIDE);

   if( o.units.size() > 1 )
   {
      unitLabel.SubclassWindow(owner.GetDlgItem(unitLabelID));
      unitLabel.ModifyStyle(0, SS_NOTIFY);
      unitLabel.SetClickHandler(this);
   }

   unitText = owner.GetDlgItem(unitTextID);
   selCode = code;

   wchar_t buf[500];
   int ctr = 0, selected = -1;
   std::vector<OrgUnit>::const_iterator i = o.units.begin();
   for( ; i != o.units.end(); i++ )
   {
      UnitData ud;
      ud.id = i->id;
      ud.text = i->name;

      wcscpy(buf, i->name);
      ud.textLower = _wcslwr(buf);

      if( i->id == code )
      {
         selected = ctr;
         unitText.SetWindowText(i->name);
      }
      units.push_back(ud);
      ctr++;
   }

   addres.SetItemCount(units.size());
   if( selected >= 0 )
      addres.SetItemState(selected, LVIS_SELECTED, LVIS_SELECTED);

   return true;
}

bool UnitList::SetData(NMLVDISPINFO* di)
{
   if( !(di->item.mask & LVIF_TEXT) )
      return false;

   bool res = false;
   DWORD index = (DWORD)di->item.iItem;
   if( inSearch )
   {
      if( searching.size() > index )
         index = searching.at(index);
      else 
         return res;
   }

   if( units.size() > index )
   {
      res = true;

      const UnitData& ud = units.at(index);
      wcsncpy(di->item.pszText, ud.text.c_str(), di->item.cchTextMax);
   }

   return res;
}

void UnitList::UpdateLayout(WORD wdh, WORD hgh)
{
   const int offset = 2;

   CRect urect;

   if( unitLabel.m_hWnd ) 
   {
      unitLabel.GetWindowRect(urect);
      owner.ScreenToClient(urect);
      unitLabel.MoveWindow(urect.left, urect.top, wdh - offset - urect.left, urect.Height());
   }

   CRect bounds;

   if( unitText.m_hWnd )
   {
      unitText.GetWindowRect(bounds);
      owner.ScreenToClient(bounds);
      unitText.MoveWindow(offset, bounds.top, wdh - 2*offset, bounds.Height()); 
   }
}

int UnitList::GetSelectedItemCode()
{
   //int index = addres.GetSelectedIndex();
   //if( index < 0 )
   //   return index;

   //if( inSearch )
   //   index = searching.at(index);

   return selCode;
}

void UnitList::Click(void* source)
{
   if( source == &addres )
   {
      int index = addres.GetSelectedIndex();
      if( index >= 0 )
      {
         wchar_t buf[500];

         search.ShowWindow(SW_HIDE);
         addres.ShowWindow(SW_HIDE);
         addres.GetItemText(index, 0, buf, sizeof(buf)/sizeof(buf[0]));
         unitText.SetWindowText(buf);

         if( inSearch )
            index = searching.at(index);
         if( (unsigned)index < units.size() )
            selCode = units.at(index).id;
      }
   } else if( source == &unitLabel )
   {
      CRect bounds;
      addres.GetHeader().GetWindowRect(bounds);
      int itemHeight = bounds.Height();

      owner.GetClientRect(bounds);

      search.UpdateLayout(bounds.Width(), itemHeight, addres.GetFont());
      search.BringWindowToTop();
      search.ShowWindow(SW_SHOW);
      search.NewSearch(false);
      inSearch = false;
      searching.clear();

      addres.UpdateLayout(itemHeight);
      addres.BringWindowToTop();
      //addres.SetItemState(selIndex, LVIS_SELECTED, LVIS_SELECTED);
      addres.ShowWindow(SW_SHOW);
   }
}

void UnitList::SearchClear()
{
   inSearch = false;
   searching.clear();
   addres.Refresh(units.size());
}

void UnitList::SearchDo(const wchar_t *text)
{
   inSearch = true;

   wchar_t* textL = wcsdup(text);

   searching.clear();
   int ctr = 0;
   UnitArray::const_iterator i = units.begin();
   for( ; i != units.end(); i++, ctr++ )
   {
      if( wcsstr(i->textLower.c_str(), textL) != NULL )
         searching.push_back(ctr);
   }

   addres.Refresh(searching.size());
   free(textL);
}


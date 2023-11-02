/*
* Copyright (C), 2007, Денис Мосягин
*
* Детали заказа
*
*  ert   09/09/2007   creating
*/
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"
#include "PropDialog.h"

#include <NplConfig.h>
#include "ObjImpl.h"

#ifdef COST_MANAGER
#include <Costs.h>
#endif

#include "SAnchor.h"
#include <SearchCtrl.h>
#include <StdFuncs.h>

class AddressList : public CWindowImpl<CListViewCtrl, CListViewCtrl>
{
public:
   struct ISetData
   {
      virtual bool SetData(NMLVDISPINFO* info) = 0;
   };

   AddressList() : handler(NULL), dataHandler(NULL) { }

   DECLARE_WND_CLASS(L"ADDR_LIST")

   BEGIN_MSG_MAP(AddressList)
      MESSAGE_HANDLER(OCM_DRAWITEM, DrawItem)
      MESSAGE_HANDLER(OCM_NOTIFY, OnNotify)
   END_MSG_MAP()

   void Init();
   void UpdateLayout(int top = 0);

   LRESULT DrawItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/);
   LRESULT OnNotify(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/);

   void SetHandler(StaticAnchor::IClickHandler* h) { handler = h; }
   void ClearHandler() { handler = NULL; }

   void SetDataHandler(ISetData* h) { dataHandler = h; }
   void ClearDataHandler() { dataHandler = NULL; }

   void Refresh(DWORD count);

protected:
   StaticAnchor::IClickHandler *handler;
   ISetData* dataHandler;
};

class UnitList : public StaticAnchor::IClickHandler, public AddressList::ISetData, public SearchControl::ISearchEvent
{
public:
   UnitList();

   bool Init(CWindow owner, UINT addrListID, UINT unitLabelID, UINT unitTextID, const Org& org, const wchar_t* code);

   void UpdateLayout(WORD wdh, WORD hgh);
   const wchar_t* GetSelectedItemCode();

protected:
   const wchar_t* selCode;

   CWindow owner;
   AddressList addres;
   StaticAnchor unitLabel;
   CStatic unitText;

   SearchControl search;
   bool inSearch;

   struct UnitData
   {
      std::wstring text;
      std::wstring textLower;
      std::wstring id;
   };

   typedef std::vector<UnitData> UnitArray;
   typedef std::vector<WORD> SearchArray;

   UnitArray units;
   SearchArray searching;

   virtual void Click(void* source);
   virtual bool SetData(NMLVDISPINFO* info);

   virtual void SearchClear(); // нажали на кнопку новый
   virtual void SearchDo(const wchar_t *text); 
};

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

   void LoadCombobox(const std::wstring &val, int id, int value, int start = 0)
   {
      CComboBox cbBox(GetDlgItem(id));

      std::wstring::size_type sp = 0;
      for( int i=start; ; i++ )
      {
         std::wstring::size_type ep = val.find_first_of(SEP_SYM, sp);
         if( ep == std::wstring::npos )
            cbBox.InsertString(i, val.substr(sp, ep).c_str());
         else
            cbBox.InsertString(i, val.substr(sp, ep - sp).c_str());

         if( ep == std::wstring::npos ) break;
         sp = ep + 1;
      }
      cbBox.SetCurSel(value);
   }

   bool GetDlgItemRect(LPRECT r, int id)
   {
      CWindow w(GetDlgItem(id));
      
      if( w.m_hWnd == NULL ) return false;

      w.GetWindowRect(r);
      ScreenToClient(r);
      return true;
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

      CDateTimePickerCtrl orderTime(GetDlgItem(IDC_ORDER_TIME));
      orderTime.SetFormat(L"HH:mm");
      orderTime.SetSystemTime(GDT_VALID, &st);

      NapoleonConfig config;
      std::wstring val;

//#ifdef FIRMS_TABLE
//      FirmsImpl f;
//      CComboBox fcb(GetDlgItem(IDC_SUPPL));
//      SQLTable ft(f.Name());
//      bool bdo = ft.Select(&f, L"ORDER BY name");
//      while( bdo )
//      {
//         int index = fcb.AddString(f.name);
//         fcb.GetItemDataPtr(index, holder.Add(f.id));
//         if( !wcscmp(order->supplyer, f.id) )
//            fcb.SetCurSel(index);
//         bdo = ft.SelectNext(&f);
//      }
//#else
      config.ReadValue(&val, SUPPL_TYPE);
      LoadCombobox(val, IDC_SUPPL, order->supplyer);
//#endif

      SetDlgItemInt(IDC_DELAY, order->delay);

      if( (order->params & ofCash) != 0 )
         CheckDlgButton(IDC_CACHE, BST_CHECKED);

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);


#ifdef COST_MANAGER
      const CostManager::CostList& cl = CostManager::CostTypes();
      CComboBox ccb(GetDlgItem(IDC_COST_TYPE));
      int i = 0;
      wchar_t name[300];
      CostManager::CostList::const_iterator ci = cl.begin();
      for( ; ci != cl.end(); ci++,i++ )
      {
         mbstowcs(name, ci->name.c_str(), strlen(ci->name.c_str()) + 1);
         int index = ccb.AddString(name);
         ccb.SetItemData(index, i);
         if( i == order->sumType)
            ccb.SetCurSel(index);
      }
#elif ORG_COST_TYPE
      bool canChange = false;
      if( config.ReadValue(&val, ALLOW_CHG_COST) && _wtoi(val.c_str()) == 1 )
         canChange = true;

      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, order->sumType);

      if( !canChange )
         GetDlgItem(IDC_COST_TYPE).EnableWindow(FALSE);
#else

      config.ReadValue(&val, COST_TYPE);
      LoadCombobox(val, IDC_COST_TYPE, order->sumType);

#endif

      if( org.orgAddress.size() > 0 )
      {
         if( *order->adrCode == 0 )
            order->adrCode = order->holder.Add(org.orgAddress.front().id);
         units.Init(*this, IDC_UNIT_LIST, IDC_UNIT_TEXT_LABEL, IDC_UNIT_TEXT, org, order->adrCode);
      }
   }

   virtual void Sizing(WORD wdh, WORD hgh)
   {
      OrderPage::Sizing(wdh, hgh);

      //MoveButtons(wdh, hgh);
      units.UpdateLayout(wdh, hgh);

      CRect bounds, bounds2;
      GetDlgItemRect(bounds, IDC_SUPPL);
      GetDlgItem(IDC_SUPPL).MoveWindow(bounds.left, bounds.top, wdh - bounds.left - offset, bounds.Height());

      GetDlgItemRect(bounds2, IDC_COST_TYPE);
      GetDlgItem(IDC_COST_TYPE).MoveWindow(bounds.left, bounds2.top, wdh - bounds.left - offset, bounds.Height());
   }

   virtual void Save(OrderImpl *order)
   {
      if( m_hWnd != NULL )
      {
         SYSTEMTIME st, st1;

         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_TIME)).GetSystemTime(&st1);

         st.wHour = st1.wHour;
         st.wMinute = st1.wMinute;
         st.wSecond = st1.wSecond;

         SystemTimeToFileTime(&st, &order->date);

         if( IsDlgButtonChecked(IDC_CACHE) == BST_CHECKED ) order->params |= ofCash;
         else order->params &= (~ofCash);

         order->supplyer = ((CComboBox)GetDlgItem(IDC_SUPPL)).GetCurSel();
         order->delay = GetDlgItemInt(IDC_DELAY, NULL, FALSE);

         CComboBox costs = GetDlgItem(IDC_COST_TYPE);
         int ct = costs.GetCurSel();
#ifdef COST_MANAGER
         ct = costs.GetItemData(ct);
#endif
         if( order->sumType != ct )
         {
            WORD st = ct; //(WORD)costs.GetItemData(ct);
            if( order->items.size() > 0 && MessageBox(L"Пересчитать цену товара?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
               order->ChangeSumType(st);
            else
               order->sumType = st;
         }

         order->adrCode = order->holder.Add(units.GetSelectedItemCode());
      }

   }

#if defined(FIRMS_TABLE) || defined(COST_MANAGER)
   StringHolder holder;
#endif
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
UnitList::UnitList() : search(IDC_FIND, IDC_FIND), inSearch(false), selCode(L"")
{
}

bool UnitList::Init(CWindow owner, UINT addrListID, UINT unitLabelID, UINT unitTextID, const Org& o, const wchar_t* code)
{
   this->owner = owner;

   addres.SubclassWindow(owner.GetDlgItem(addrListID));
   addres.Init();
   addres.ShowWindow(SW_HIDE);
   addres.SetHandler(this);
   addres.SetDataHandler(this);

   search.SetHandler(owner.m_hWnd, this);
   search.ShowWindow(SW_HIDE);

   if( o.orgAddress.size() > 1 )
   {
      unitLabel.SubclassWindow(owner.GetDlgItem(unitLabelID));
      unitLabel.ModifyStyle(0, SS_NOTIFY);
      unitLabel.SetClickHandler(this);
   }

   unitText = owner.GetDlgItem(unitTextID);
   selCode = code;

   wchar_t buf[500];
   int ctr = 0, selected = -1;
   std::vector<OrgAddress>::const_iterator i = o.orgAddress.begin();
   for( ; i != o.orgAddress.end(); i++ )
   {
      UnitData ud;
      ud.id = i->id;
      ud.text = i->name;
      ud.id = i->id;

      wcscpy(buf, i->name);
      ud.textLower = _wcslwr(buf);

      if( wcscmp(i->id, code) == 0 )
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

const wchar_t* UnitList::GetSelectedItemCode()
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
            selCode = units.at(index).id.c_str();
      }
   } else if( source == &unitLabel )
   {
      CRect bounds;
      addres.GetHeader().GetWindowRect(bounds);
      int itemHeight = bounds.Height();
      if( itemHeight == 0 ) itemHeight = 20;

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

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);
   return (dlg.DoModal() == IDOK);
}

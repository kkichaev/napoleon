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

#include <Exchange.h>

#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>

#include "BaseDialog.h"
#include "SAnchor.h"

#include <NplConfig.h>
#include "ObjImpl.h"
#include <SearchCtrl.h>

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
   std::wstring selCode;

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


class OrderDetailDialog : public BaseDialog//<IDD_ORDER_DETAIL>
{
public:
   OrderDetailDialog(OrderImpl *_order) : BaseDialog(IDD_ORDER_DETAIL), order(_order) {}

   //typedef BaseDialog<IDD_ORDER_DETAIL> BaseClass;
   typedef BaseDialog BaseClass;

   BEGIN_MSG_MAP(OrderDetailDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      CHAIN_MSG_MAP(BaseClass)
      REFLECT_NOTIFICATIONS()
   END_MSG_MAP()

   UnitList units;

protected:
   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;

      if( order->IsExported() )
         DisableChilds();

      SYSTEMTIME st;
      FileTimeToSystemTime(&order->date, &st);

      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

      GetDlgItem(IDC_REMARK).SetWindowText(order->remark);

      NapoleonConfig config;
      std::wstring val;

      config.ReadValue(&val, SUPPL_TYPE);
      LoadCombobox(val, IDC_SUPPL, order->supplyer);

      OrgImpl o;
      o.id = order->id;
      o.Read();

      SetWindowText(o.name);

#ifdef ORG_COST_TYPE
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

      if( o.units.size() > 0 )
      {
         if( order->unitCode == L"" )
            order->unitCode = o.units.front().id;

         units.Init(*this, IDC_UNIT_LIST, IDC_UNIT_TEXT_LABEL, IDC_UNIT_TEXT, o, order->unitCode);
      }

      return TRUE;
   }

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

      MoveButtons(wdh, hgh);

      CRect bounds, bounds2;

      GetDlgItemRect(bounds, IDC_SUPPL);
      GetDlgItem(IDC_SUPPL).MoveWindow(bounds.left, bounds.top, wdh - bounds.left - offset, bounds.Height());

      GetDlgItemRect(bounds2, IDC_COST_TYPE);
      GetDlgItem(IDC_COST_TYPE).MoveWindow(bounds.left, bounds2.top, wdh - bounds.left - offset, bounds.Height());

      //addressList.UpdateLayout();

      CWindow address(GetDlgItem(IDC_UNIT_TEXT));                                      
      address.GetWindowRect(bounds);
      ScreenToClient(bounds);
      address.MoveWindow(offset, bounds.top, wdh - 2*offset, bounds.Height()); 

      GetDlgItemRect(bounds, IDC_REMARK);
      GetDlgItemRect(bounds2, IDCANCEL);
      GetDlgItem(IDC_REMARK).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds2.top - bounds.top - offset);

      units.UpdateLayout(wdh, hgh);
      return 0;
   }

   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      bHandled = FALSE;
      if( wID == IDOK )
      {
         SYSTEMTIME st;//, st1;

         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);

         SystemTimeToFileTime(&st, &order->date);

         order->supplyer = ((CComboBox)GetDlgItem(IDC_SUPPL)).GetCurSel();

         CComboBox costs = GetDlgItem(IDC_COST_TYPE);
         int ct = costs.GetCurSel();
         if( order->sumType != ct )
         {
            WORD st = ct;
            if( order->items.size() > 0 && MessageBox(L"Пересчитать цену товара?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
               order->ChangeSumType(st);
            else
               order->sumType = st;
         }

         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength();

         wchar_t *buf = (wchar_t*)malloc((len + 1)* sizeof(wchar_t));
         wnd.GetWindowText(buf, len+1);
         order->AssignRemark(buf);
         free(buf);

         order->unitCode = order->holder.Add(units.GetSelectedItemCode());
      }

      return 0;
   }
protected:
   OrderImpl *order;
};

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);
   return (dlg.DoModal() == IDOK);
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
UnitList::UnitList() : search(IDC_FIND, IDC_FIND), inSearch(false)
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
   return selCode.c_str();
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

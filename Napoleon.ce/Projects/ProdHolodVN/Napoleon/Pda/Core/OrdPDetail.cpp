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

#include <NplConfig.h>
#include "ObjImpl.h"
#include "Costs.h"
#include <Preference.h>
#include "Add.h"
#include <SAnchor.h>

void UnitList::Init(const wchar_t *ido, const wchar_t *selected)
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

   OrgImpl o;
   SQLTable table(o.Name());
   wchar_t buf[200];
   wsprintf(buf, L" WHERE ido='%s'", ido);
   int ctr = 0;
   bool bdo = table.Select(&o, buf);
   while( bdo )
   {
      LVITEM li = { 0 };
      li.mask = LVIF_TEXT | LVIF_PARAM;
      li.iItem = ctr++;
      li.lParam = (DWORD)sh.Add(o.id);
      li.pszText = o.name;
      InsertItem(&li);

      if( selected && wcscmp(o.id, selected) == 0 )
         SetItemState(ctr-1, LVIS_SELECTED, LVIS_SELECTED);

      bdo = table.SelectNext(&o);
   }
}

void UnitList::UpdateLayout()
{
   CRect rc;
   GetParent().GetClientRect(rc);
   MoveWindow(rc);

   SetColumnWidth(0, rc.Width()-3);
}

LRESULT UnitList::DrawItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& /*bHandled*/)
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

class OrderDetailDialog : public BaseDialog
{
   StaticAnchor units;
   UnitList list;

public:
   OrderDetailDialog(OrderImpl *_order) : BaseDialog(IDD_ORDER_DETAIL), order(_order) {}

   ~OrderDetailDialog()
   {
      FreeDogovors();
   }

   typedef BaseDialog BaseClass;

   BEGIN_MSG_MAP(OrderDetailDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      NOTIFY_HANDLER(IDC_UNIT_LIST, NM_CLICK, CheckAddressItem);
      COMMAND_HANDLER(IDD_DELIVERY, BN_CLICKED, SetDlvDate)
      COMMAND_HANDLER(IDC_UNIT_TEXT, STN_CLICKED, SetUnit)
      COMMAND_HANDLER(IDC_SUPPL, CBN_SELCHANGE, SetDogovors)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      REFLECT_NOTIFICATIONS()
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

protected:
   LRESULT SetUnit(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      list.UpdateLayout();
      list.BringWindowToTop();
      list.ShowWindow(SW_SHOW);

      return 0;
   }

   LRESULT CheckAddressItem( int id, LPNMHDR hdr, BOOL &bHandled)
   {
      list.ShowWindow(SW_HIDE);

      BringWindowToTop();
      int index = list.GetSelectedIndex();
      if( index < 0 )
         return 0;
      
      const wchar_t *c = (const wchar_t*)list.GetItemData(index);
      if( wcscmp(c, order->dlvCode) )
      {
         order->dlvCode = order->holder.Add(c);

         OrgImpl o;
         o.id = (wchar_t*)c;
         o.Read();

         SetDlgItemText(IDC_UNIT_TEXT, o.name);
      }
      return 0;
   }

   LRESULT SetDlvDate(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      GetDlgItem(IDC_ORDER_DATE).EnableWindow((IsDlgButtonChecked(IDD_DELIVERY) == BST_CHECKED) ? TRUE : FALSE);
      return 0;
   }

   LRESULT SetDogovors(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
   {
      CComboBox cbx(GetDlgItem(IDC_SUPPL));
      int idx = cbx.GetCurSel();
      const wchar_t* scode = (const wchar_t*)cbx.GetItemDataPtr(idx);
   
      CComboBox dogs(GetDlgItem(IDC_DOGOVORS));
      LoadDogovors(dogs, order->id, scode, NULL);

      return 0;
   }

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;

      if( order->IsExported() )
         DisableChilds();

      SYSTEMTIME st;
      FileTimeToSystemTime(&order->date, &st);

      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

      //CDateTimePickerCtrl orderTime(GetDlgItem(IDC_ORDER_TIME));
      //orderTime.SetFormat(L"HH:mm");
      //orderTime.SetSystemTime(GDT_VALID, &st);

      GetDlgItem(IDC_REMARK).SetWindowText(order->remark);

      NapoleonConfig config;
      std::wstring val;

      config.ReadValue(&val, SUPPL_TYPE);
      LoadComboboxWithCode(val, IDC_SUPPL, order->suplCode, L'\t', &sh);

      CComboBox dogs(GetDlgItem(IDC_DOGOVORS));
      LoadDogovors(dogs, order->id, order->suplCode, order->dogovor);

      //SetDlgItemInt(IDC_DELAY, order->delay);
      __int64 payDate = (__int64)order->delay * 10000000 * 3600 * 24; // перевод в число дней
      payDate += *(__int64*)&order->date;
      FileTimeToSystemTime((FILETIME*)&payDate, &st);
      ((CDateTimePickerCtrl)GetDlgItem(IDC_PAY_DATE)).SetSystemTime(GDT_VALID, &st);

      if( (order->params & ofCash) != 0 )
         CheckDlgButton(IDC_CACHE, BST_CHECKED);

      if( (order->params & ofDelivery) != 0 )
         CheckDlgButton(IDD_DELIVERY, BST_CHECKED);
      else
         GetDlgItem(IDC_ORDER_DATE).EnableWindow(FALSE);

      OrgImpl org;
      org.id = order->id;
      org.Read();
      SetWindowText(org.name);

      if( wcscmp(order->id, order->dlvCode) )
      {
         org.id = order->dlvCode;
         org.Read();
      }

      GetDlgItem(IDC_UNIT_TEXT).SetWindowText(org.name);
      units.SubclassWindow(GetDlgItem(IDC_UNIT_TEXT));

      list.SubclassWindow(GetDlgItem(IDC_UNIT_LIST));
      list.Init(org.ido, order->dlvCode);
      list.ShowWindow(SW_HIDE);

      return TRUE;
   }

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

      MoveButtons(wdh, hgh);

      CRect bounds, bounds2;
      GetDlgItemRect(bounds, IDC_REMARK);
      GetDlgItemRect(bounds2, IDCANCEL);
      GetDlgItem(IDC_REMARK).MoveWindow(offset, bounds.top, wdh - 2*offset, bounds2.top - bounds.top - offset);

      GetDlgItemRect(bounds, IDC_SUPPL);
      GetDlgItem(IDC_SUPPL).MoveWindow(bounds.left, bounds.top, wdh - bounds.left - offset, bounds.Height());

      GetDlgItemRect(bounds2, IDC_UNIT_TEXT);
      GetDlgItem(IDC_UNIT_TEXT).MoveWindow(bounds2.left, bounds2.top, wdh - bounds2.left - offset, bounds2.Height());

      GetDlgItemRect(bounds2, IDC_DOGOVORS);
      GetDlgItem(IDC_DOGOVORS).MoveWindow(bounds2.left, bounds2.top, wdh - bounds2.left - offset, bounds2.Height());

      return 0;
   }

   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      if( wID == IDOK )
      {
         SYSTEMTIME st;

         ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
         //((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_TIME)).GetSystemTime(&st1);

         //st.wHour = st1.wHour;
         //st.wMinute = st1.wMinute;
         //st.wSecond = st1.wSecond;

         SystemTimeToFileTime(&st, &order->date);

         if( IsDlgButtonChecked(IDC_CACHE) == BST_CHECKED ) order->params |= ofCash;
         else order->params &= (~ofCash);

         if( IsDlgButtonChecked(IDD_DELIVERY) == BST_CHECKED ) order->params |= ofDelivery;
         else order->params &= (~ofDelivery);

         CComboBox cbx(GetDlgItem(IDC_SUPPL));
         int idx = cbx.GetCurSel();
         if( idx >= 0 )
         {
            const wchar_t* scode = (const wchar_t*)cbx.GetItemDataPtr(idx);
            if( wcscmp(scode, order->suplCode) )
               order->suplCode = order->holder.Add(scode);

            Preference p;
            p.Load();
            wcsncpy(p.defaultFirm, scode, sizeof(p.defaultFirm) / sizeof(wchar_t));
            p.Save();
         }

         //order->delay = GetDlgItemInt(IDC_DELAY, NULL, FALSE);
         ((CDateTimePickerCtrl)GetDlgItem(IDC_PAY_DATE)).GetSystemTime(&st);
         __int64 payDate;
         SystemTimeToFileTime(&st, (FILETIME*)&payDate);
         payDate -= *(__int64*)&order->date;
         order->delay = (WORD)(payDate / ((__int64)10000000 * 3600 * 24));

         CComboBox dogs(GetDlgItem(IDC_DOGOVORS));
         int ct = dogs.GetCurSel();
         if( ct >= 0 )
         {
            ItemData* d = (ItemData*)dogs.GetItemDataPtr(ct);
            if( wcscmp(d->dog, order->dogovor) )
            {
               order->dogovor = order->holder.Add(d->dog);
               order->sumType = (WORD)CostManager::CostIndex(d->cost);
            }
         }

         CWindow wnd(GetDlgItem(IDC_REMARK));
         int len = wnd.GetWindowTextLength() + 1;

         wchar_t *buf = (wchar_t*)alloca(len* sizeof(wchar_t));
         wnd.GetWindowText(buf, len);
         order->AssignRemark(buf);

         RefreshDiscount(order);
      }

      EndDialog(m_hWnd, wID);
      return 0;
   }
protected:
   OrderImpl *order;
   StringHolder sh;
};

static StringHolder dogH;
static std::vector<ItemData*> data;

void FreeDogovors()
{
   std::vector<ItemData*>::iterator i = data.begin();
   for( ; i != data.end(); i++ )
      delete (*i);

   data.clear();
   dogH.Clear();
}

void LoadDogovors(CComboBox &dogs, const wchar_t* id, const wchar_t* firm, const wchar_t *selCode)
{
   OrgImpl org;
   org.id = (wchar_t*)id;
   org.Read();

   if( selCode == NULL || *selCode == '\0' )
   {
      vector_t<CloseFirmItem>::const_iterator ci = org.closed.begin();
      for( ; ci != org.closed.end(); ci++ )
      {
         if( wcscmp(ci->firm, firm) == 0 )
         {
            MessageBox(NULL, L"Контрагент закрыт по фирме!", L"Предупреждение", MB_OK | MB_ICONSTOP);
            break;
         }
      }
   }

   dogs.ResetContent();
   FreeDogovors();

   vector_t<Dogovor>::const_iterator di = org.dogovors.begin();
   for( ; di != org.dogovors.end(); di++ )
   {
      if( wcscmp(di->firm, firm) )
         continue;

      int index = dogs.AddString(di->name);
      ItemData* d = new ItemData();
      d->dog = dogH.Add(di->number);
      d->cost = dogH.Add(di->ctype);

      data.push_back(d);

      dogs.SetItemDataPtr(index, d);
      if( selCode != NULL && wcscmp(di->number, selCode) == 0 )
         dogs.SetCurSel(index);
   }

   if( selCode == NULL || *selCode == L'\0' )
      dogs.SetCurSel(0);
}

bool EditOrderDetail(OrderImpl *order)
{
   OrderDetailDialog dlg(order);
   return (dlg.DoModal() == IDOK);
}

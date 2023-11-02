/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Форма заказа
 *
 *  ert   06/02/2009   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "FormEntries.h"
#include "Invoice.h"
#include "BaseDialog.h"
#include <ListForm.h>
#include <StdFuncs.h>

class DeliveryAdd : public DeliveryForm
{
public:
   DeliveryAdd() {}

   DECLARE_FORM(DeliveryAdd, IDD_DELIVERY_ADD);

   BEGIN_MSG_MAP(DeliveryAdd)
      COMMAND_ID_HANDLER(IDC_DETAIL, Detail)
      CHAIN_MSG_MAP(DeliveryForm)
  END_MSG_MAP();

   virtual DWORD GetMenuBarID() const { return IDD_DELIVERY_ADD; }
   LRESULT Detail(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
};

IMPLEMENT_FORM(DeliveryAdd)

class DlvDetail : public BaseDialog
{
public:
   DlvDetail(const DeliveryImpl* d) : BaseDialog(IDC_KEYVALUE), delivery(d) {}

   typedef BaseDialog BaseClass;

   BEGIN_MSG_MAP(OrderDetailDialog)
      NOTIFY_CODE_HANDLER_EX(LVN_GETDISPINFO, SetCellInfo)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      CHAIN_MSG_MAP(BaseClass)
      REFLECT_NOTIFICATIONS()
   END_MSG_MAP()

   LRESULT SetCellInfo(LPNMHDR hdr)
   {
      NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
      if( !(di->item.mask & LVIF_TEXT) )
         return TRUE;

      if( delivery->values.size() > (unsigned)di->item.iItem )
      {
         const Config& kv = delivery->values.at(di->item.iItem);
         wcsncpy(di->item.pszText, (di->item.iSubItem == 0) ? kv.key : kv.value,
            di->item.cchTextMax);
      }
      return TRUE;
   }

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      bHandled = FALSE;

      listView.SubclassWindow(GetDlgItem(IDC_TABLE));

      SetSystemFont(listView.m_hWnd);
      HFONT listFont;
      LOGFONT lf;
      listFont = listView.GetFont();
      GetObject(listFont, sizeof(lf), &lf);
      lf.lfHeight *= 2;

      HFONT newFont = CreateFontIndirect(&lf);
      listView.SetFont(newFont);

      listView.SetExtendedListViewStyle(LVS_EX_FULLROWSELECT);
      listView.ModifyStyle(0, LVS_REPORT|LVS_SHOWSELALWAYS|LVS_SINGLESEL|WS_VSCROLL);
      listView.SetRedraw(false);

      listView.InsertColumn(0, L"Название", LVCFMT_LEFT, 50);
      listView.InsertColumn(1, L"Значение", LVCFMT_LEFT, 50);

      listView.SetItemCount(delivery->values.size());

      listView.SetFont(listFont);
      listView.SetRedraw(true);
      return 0;
   }

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

      MoveButtons(wdh, hgh);

      CRect b1;
      GetDlgItem(IDOK).GetWindowRect(b1);
      ScreenToClient(b1);

      int top = nTitleHeight+2;
      listView.MoveWindow(0, top, wdh, b1.top - top);

      listView.SetColumnWidth(0, wdh/2);
      listView.SetColumnWidth(1, wdh/2);
      return 0;
   }

   const DeliveryImpl* delivery;
   ListViewMultiLine listView;
};

LRESULT DeliveryAdd::Detail(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   DlvDetail dlvDetail(((DeliveryData*)data)->GetDelivery());
   dlvDetail.DoModal();
   return 0;
}

//
//--------------------------- Globals -------------------------------------
//
void OpenDelivery(DeliveryImpl *dlv, const wchar_t *type)
{
   _Module.GetFrame()->Load(IDD_DELIVERY_ADD, new DeliveryData(dlv, type));
}

/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Диалог количества, custom
 *
 *  ert   17/08/2007   creating
 */
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include "Preference.h"
#include <NapoleonRes.h>

#include "Qty.h"

#include <MainFrame.h>
#include <Table.h>

static void StrReplace(wchar_t *buf, wchar_t sym)
{
   wchar_t *p = wcschr(buf, sym);
   while( p != NULL )
   {
      *p = L' ';
      p = wcschr(p+1, sym);
   }
}

class QTYDlg : public CQTYDialog
{
 public:
   QTYDlg(QTYData *data) : CQTYDialog(data) {}

   std::wstring nameBuf;

   virtual void SetData(const PriceImpl& price)
   {
      CQTYDialog::SetData(price);

      int len = wcslen(price.remark) + 1;
      wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
      wcscpy(buf, price.remark);

      StrReplace(buf, L'\n');
      StrReplace(buf, '\r');

      //GetDlgItem(IDC_REMARK).SetWindowText(buf);

      nameBuf = price.name;
      nameBuf += L"\n";
      nameBuf += buf;
      GetDlgItem(IDC_ITEM_NAME).SetWindowText(nameBuf.c_str());
   }

   //virtual void MoveControls(WORD wdh, WORD hgh)
   //{
   //   CWindow rem(GetDlgItem(IDC_REMARK));
   //   CWindow name(GetDlgItem(IDC_ITEM_NAME));
   //   CRect bnds, packBnds;
   //   name.GetWindowRect(bnds);
   //   ScreenToClient(bnds);

   //   GetDlgItem(IDC_PACK).GetWindowRect(packBnds);
   //   ScreenToClient(packBnds);

   //   hgh = bnds.Height() / 3;
   //   name.MoveWindow(bnds.left, bnds.top, bnds.Width(), hgh * 2);
   //   rem.MoveWindow(bnds.left, bnds.top + hgh * 2, packBnds.left - bnds.left - 1, hgh);
   //}

};


//LRESULT QTYDlg::OnSizeChanged(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
//{
//   CQTYDialog::OnSizeChanged(uMsg, wParam, lParam, bHandled);
//
//   CWindow rem(GetDlgItem(IDC_REMARK));
//   CWindow name(GetDlgItem(IDC_ITEM_NAME));
//   CRect bnds, packBnds;
//   name.GetWindowRect(bnds);
//   ScreenToClient(bnds);
//
//   GetDlgItem(IDC_PACK).GetWindowRect(packBnds);
//   ScreenToClient(packBnds);
//
//   int hgh = packBnds.top - bnds.top - offset;
//   name.MoveWindow(bnds.left, bnds.top, bnds.Width(), hgh);
//   rem.MoveWindow(bnds.left, packBnds.top, packBnds.left - bnds.left - 1, packBnds.Height());
//
//   if( GetSystemMetrics(SM_CXSCREEN) >= GetSystemMetrics(SM_CYSCREEN) )
//   {
//      int top, left = packBnds.left;
//
//      GetDlgItemRect(bnds, IDC_DIG_1);
//      top = bnds.top;
//      hgh = bnds.Height();
//
//      int ctrls[][3] = { 
//         { IDC_DIG_1, IDC_DIG_4, IDC_DIG_7 },
//         { IDC_DIG_2, IDC_DIG_5, IDC_DIG_8 }, 
//         { IDC_DIG_3, IDC_DIG_6, IDC_DIG_9 },
//         { IDC_DIG_BS, IDC_DIG_PT, IDC_DIG_0 } };
//
//      for( int i = 0; i<4; i++ )
//      {
//         int ctop = top;
//         for( int j=0; j<3; j++ )
//         {
//            GetDlgItem(ctrls[i][j]).MoveWindow(left, ctop, hgh, hgh);
//            ctop += hgh-1;
//         }
//         left += hgh-1;
//      }
//   }
//   return 0;
//}

bool SetQTY(QTYData *data)
{
   HWND oldFocus = GetFocus();

   QTYDlg dlg(data);
   int code = dlg.DoModal();

   SetFocus(oldFocus);
   return (code == IDOK);
}

/*
 * Copyright (C), 2006-2011, Денис Мосягин
 *
 * Регион Сибирь add-in
 *
 *  ert   17/03/2011   creating
 */ 
#include "stdafx.h"
#include <Exchange.h>
#include <ObjImpl.h>

#include "Add.h"

#include <PriceForm.h>
#include <FormEntries.h>

#include <EnterNumber.h>

EditGoodsItem::EditGoodsItem(GoodsRestImpl *doc, const wchar_t* id) : BaseDialog(IDD_GOODS_ITEMS)
{
   this->doc = doc;
   this->id = id;
   doc->GetItemInfo(&data, id);
}

LRESULT EditGoodsItem::ItemSelected(LPNMHDR hdr)
{
   int index = ((NMLISTVIEW*)hdr)->iItem;
   if( index >= 0 && index < (int)data.size() )
   {
      EnterNumberT<IDD_ENTER_VALUE, QTY_SCALE, true> dlg;
      dlg.value = data[index].qty;

      if( dlg.DoModal() == IDOK )
      {
         data[index].qty = dlg.value;

         wchar_t buf[100], src[50];
         ConvertScaling(src, (long)dlg.value, QTY_SCALE);
         FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(dlg.value) % QTY_SCALE, QTY_SCALE, true);

         LVITEM item = {0};
         item.mask = LVIF_TEXT;
         item.iItem = index;
         item.iSubItem = 1;
         item.pszText = buf;
      
         CListViewCtrl list(GetDlgItem(IDC_TABLE));
         list.SetItem(&item);
         list.RedrawItems(index, index);
      }
   }
   return 0;
}

LRESULT EditGoodsItem::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   bHandled = FALSE;

   PriceImpl p;
   p.id = (wchar_t*)id;
   p.Read();

   CListViewCtrl list(GetDlgItem(IDC_TABLE));
   list.SetExtendedListViewStyle(LVS_EX_FULLROWSELECT);
   list.ModifyStyle(0, LVS_REPORT|LVS_SHOWSELALWAYS|LVS_SINGLESEL|WS_VSCROLL);
   list.InsertColumn(0, L"Дата", LVCFMT_LEFT, 100);
   list.InsertColumn(1, L"Кол-во", LVCFMT_RIGHT, 100);

   int ctr = 0;
   wchar_t buf[100], src[50];
   SYSTEMTIME st;
   std::vector<GoodsItemInfo>::const_iterator i = data.begin();
   for( ; i != data.end(); i++, ctr++ )
   {
      LVITEM item = {0};
      item.iItem = ctr;
      item.mask = LVIF_TEXT;

      FileTimeToSystemTime(&i->date, &st);
      GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));
      item.pszText = buf;

      list.InsertItem(&item);

      item.iItem = ctr;
      item.iSubItem = 1;

      ConvertScaling(src, (long)i->qty, QTY_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(i->qty) % QTY_SCALE, QTY_SCALE, true);
      item.pszText = buf;
      list.SetItem(&item);
   }

   GetDlgItem(IDC_ORG_TITLE).SetWindowText(p.name);

   if( doc->IsDirty() == false )
      list.EnableWindow(FALSE);

   return 0;
}

LRESULT EditGoodsItem::OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

   CRect nameBounds, bounds;
   GetDlgItemRect(nameBounds, IDC_ORG_TITLE);
   GetDlgItem(IDC_ORG_TITLE).SetWindowPos(HWND_BOTTOM, offset, nameBounds.top, wdh-2*offset, nameBounds.Height(), 0);

   MoveButtons(wdh, hgh);

   GetDlgItem(IDCANCEL).GetWindowRect(bounds);
   ScreenToClient(bounds);

   CListViewCtrl list(GetDlgItem(IDC_TABLE));
   list.MoveWindow(offset, nameBounds.bottom + offset, wdh - 2*offset, bounds.top - nameBounds.bottom - 2*offset);
   list.SetColumnWidth(1, wdh - 2*offset - 150);

   return 0;
}

LRESULT EditGoodsItem::Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   if( wID == IDOK )
   {
      doc->UpdateItems(data, id);
   }

   EndDialog(m_hWnd, wID);
   return 0;
}

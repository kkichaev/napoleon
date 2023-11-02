/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Диалог количества
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

#ifdef SHOW_OFF_TAKE
#include "OffTake.h"
LRESULT CQTYDialog::UpdateRemnants(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   wchar_t buf[20];
   if( data->canChange && qtyText.m_hWnd )
   {
      GetDlgItemText(IDD_REMNANTS_QTY, buf, sizeof(buf)/sizeof(buf[0]));
      DWORD remnants = GetValue(buf, QTY_SCALE);
      offTakeHolder.UpdateLastRest(&data->sales, data->id.c_str(), remnants);

      SetSalesInfo();
   }
   return 0;
}
#endif

void CQTYDialog::SetSalesInfo()
{
   std::wstring qtyStr;
   std::wstring dateStr;
   std::vector<ItemSales>::const_iterator i = data->sales.begin();

#ifdef SHOW_OFF_TAKE
   std::wstring offTakeStr;
   std::wstring restStr;

   qtyStr = L"\x1"; // select last qty

   int scale = GetSystemMetrics(SM_CXSMICON) / 16;
#ifdef HappyLand // показываем только 4 предудыщих
   int ictr = 0;
   qtyText.SetLabels(L"остаток\noff-take\nзаказ\nдата", 45 * scale, true);
   for( ;i != data->sales.end() && ictr < 4; i++, ictr++ )
#else
   qtyText.SetLabels(L"ост\noff\nзак\nдат", 20 * scale, true);
   for( ;i != data->sales.end(); i++ )
#endif
   {
      wchar_t buf[50];
      SYSTEMTIME st;
      FileTimeToSystemTime(&i->date, &st);

      wsprintf(buf, L"%02d.%02d\t", st.wDay, st.wMonth);
      dateStr += buf;

      wchar_t src[50];
      ConvertScaling(src, (long)i->qty, QTY_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(i->qty) % QTY_SCALE, QTY_SCALE, true);
      qtyStr += buf;
      qtyStr += L"\t";

      ConvertScaling(src, (long)i->rest, QTY_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(i->rest) % QTY_SCALE, QTY_SCALE, true);
      restStr += buf;
      restStr += L"\t";

      ConvertScaling(src, (long)i->offTake, QTY_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(i->offTake) % QTY_SCALE, QTY_SCALE, true);
      offTakeStr += buf;
      offTakeStr += L"\t";
   }

   restStr += L"\n";
   restStr += offTakeStr;
   restStr += L"\n";
   restStr += qtyStr;
   restStr += L"\n";
   restStr += dateStr;

   qtyText.SetWindowText(restStr.c_str());
#else
   for( ;i != data->sales.end(); i++ )
   {
      wchar_t buf[50];
      SYSTEMTIME st;
      FileTimeToSystemTime(&i->date, &st);

      wsprintf(buf, L"%02d.%02d\t", st.wDay, st.wMonth);
      dateStr += buf;

      wchar_t src[50];
      ConvertScaling(src, (long)i->qty, QTY_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(i->qty) % QTY_SCALE, QTY_SCALE, true);
      qtyStr += buf;
      qtyStr += L"\t";
   }

   qtyStr += L'\n';
   qtyStr += dateStr;

   qtyText.SetWindowText(qtyStr.c_str());
#endif
}

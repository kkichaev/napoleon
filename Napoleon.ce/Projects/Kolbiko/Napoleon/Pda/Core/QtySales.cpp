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

extern bool qtyInited;
LRESULT CQTYDialog::UpdateRemnants(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
{
   wchar_t buf[20];
   if( data->canChange && qtyText.m_hWnd )
   {
      GetDlgItemText(IDD_REMNANTS_QTY, buf, sizeof(buf)/sizeof(buf[0]));
      DWORD remnants = GetValue(buf, QTY_SCALE);

      GetDlgItemText(IDC_ADD_QTY, buf, sizeof(buf)/sizeof(buf[0]));
      DWORD rets = GetValue(buf, QTY_SCALE);
      offTakeHolder.UpdateLastRest(&data->sales, data->id.c_str(), remnants, rets);

      SetSalesInfo();

      if( qtyInited && data->sales.size() > 0 )
         SetScalingValue(IDC_QTY, data->sales.front().qty, QTY_SCALE, true);
   }
   return 0;
}
#endif

void CQTYDialog::SetSalesInfo()
{
   std::wstring qtyStr;
   std::wstring dateStr, retStr;
   std::vector<ItemSales>::const_iterator i = data->sales.begin();

#ifdef SHOW_OFF_TAKE
   std::wstring offTakeStr;
   std::wstring restStr;

   qtyStr = L"\x1"; // select last qty

   int scale = GetSystemMetrics(SM_CXSMICON) / 16;
   qtyText.SetLabels(L"ост\noff\nзак\nвоз\nдат", 20 * scale, true);
   for( ;i != data->sales.end(); i++ )
   {
      wchar_t buf[50];
      SYSTEMTIME st;
      FileTimeToSystemTime(&i->date, &st);

      wsprintf(buf, L"%02d.%02d\t", st.wDay, st.wMonth);
      dateStr += buf;

      wchar_t src[50];
      long value = (__int64)(long)(i->qty + 5) * SUM_SCALE / QTY_SCALE;
      ConvertScaling(src, value, SUM_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % SUM_SCALE, SUM_SCALE, false);
      qtyStr += buf;
      qtyStr += L"\t";

      value = (__int64)(long)(i->rest + 5) * SUM_SCALE / QTY_SCALE;
      ConvertScaling(src, value, SUM_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % SUM_SCALE, SUM_SCALE, false);
      restStr += buf;
      restStr += L"\t";

      value = (__int64)(long)(i->ret + 5) * SUM_SCALE / QTY_SCALE;
      ConvertScaling(src, value, SUM_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % SUM_SCALE, SUM_SCALE, false);
      retStr += buf;
      retStr += L"\t";

      value = (__int64)(long)(i->offTake + 5) * SUM_SCALE / QTY_SCALE;
      ConvertScaling(src, value, SUM_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % SUM_SCALE, SUM_SCALE, false);
      offTakeStr += buf;
      offTakeStr += L"\t";
   }

   restStr += L"\n";
   restStr += offTakeStr;
   restStr += L"\n";
   restStr += qtyStr;
   restStr += L"\n";
   restStr += retStr;
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

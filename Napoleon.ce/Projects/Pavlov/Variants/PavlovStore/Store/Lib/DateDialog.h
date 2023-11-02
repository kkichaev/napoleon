/*
 * Copyright (C), 2007-2008, Денис Мосягин
 *
 * Диалог для ввода даты
 * 
 *  ert   17/11/2008   creating
 */ 

#ifndef __RMV_DATE_DLG_H
#define __RMV_DATE_DLG_H

#include <NapoleonRes.h>
#include <BaseDialog.h>

class RemoveDateDialog : public BaseDialog
{
public:
   RemoveDateDialog() : BaseDialog(IDD_REMOVE_ORDERS) {}

   SYSTEMTIME date;

   BEGIN_MSG_MAP(RemoveDateDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, OnOK)
      CHAIN_MSG_MAP(BaseDialog)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
   {
      // set date to month before
      GetLocalTime(&date);
      if( date.wMonth > 1 ) date.wMonth--;
      else
      {
         date.wMonth = 12;
         date.wYear--;
      }

      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &date);
      bHandled = FALSE;
      return 0;
   }

   LRESULT OnOK(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      bHandled = false;
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&date);

      // set last second of day
      date.wHour = 23;
      date.wMinute = 59;
      date.wSecond = 59;

      return 0;
   }

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);
      MoveButtons(wdh, hgh);

      return 0;
   }
};

#endif


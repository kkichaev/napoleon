/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Ввод числа
 *
 *  ert   12/08/2010   creating
 */
#ifndef _ENTER_QTY_H
#define _ENTER_QTY_H

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>

#include <NapoleonRes.h>
#include <NumInput.h>

template< UINT DLG_ID, WORD SCALE = SUM_SCALE, bool HIDE_REST = false  > 
class EnterNumberT : public CSimpleDialog<DLG_ID, TRUE>
{
public:
   EnterNumberT() : numInput(IDD_ENTER_VALUE) { value = 0; title = NULL; }

   DWORD value;
   const wchar_t* title;

   typedef CSimpleDialog<DLG_ID, TRUE> BaseClass;

   BEGIN_MSG_MAP(EnterNumber)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, Close)
      NUM_INPUT_HANDLER(numInput)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT , WPARAM , LPARAM , BOOL& bHandled)
   {
      if( title != NULL )
         SetWindowText(title);

      SetScalingValue(IDD_ENTER_VALUE, value, SCALE, HIDE_REST);
      Init();
      bHandled = FALSE;
      return 0;
   }

   LRESULT Close(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      wchar_t buf[50];

      GetDlgItem(IDD_ENTER_VALUE).GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
      value = GetValue(buf, SCALE);

      Save();
      bHandled = FALSE;
      return 0;
   }

protected:
   void SetScalingValue(int id, int value, DWORD scale, bool hideRest)
   {
      wchar_t buf[20], src[20];

      ConvertScaling(src, (long)value, SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, SCALE, hideRest);
      SetDlgItemText(id, buf);
   }

   virtual void Init() {}
   virtual void Save() {}

   NumInput numInput;
};

typedef EnterNumberT<IDD_ENTER_VALUE> EnterNumber;

#endif

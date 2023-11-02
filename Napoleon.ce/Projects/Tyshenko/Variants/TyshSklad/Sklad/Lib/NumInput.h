/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Компонент обработка ввода чисел
 *
 *  ert   17/08/2007   creating
 */
#ifndef _NUM_INPUT_H
#define _NUM_INPUT_H

#define NUM_INPUT_HANDLER(ni) \
   if( uMsg == WM_COMMAND && LOWORD(wParam) >= IDC_DIG_0  && LOWORD(wParam) <= IDC_DIG_BS ) \
   { \
      bHandled = TRUE; \
      lResult = ni.OnDigPressed((UINT)HIWORD(wParam), (int)LOWORD(wParam), (HWND)lParam, bHandled); \
      if( bHandled == TRUE ) return lResult; \
   }

class NumInput
{
public:
   NumInput(UINT ctrlID) { this->ctrlID = ctrlID; }
   void SetTargetControl(UINT ctrlID) { this->ctrlID = ctrlID; }

   LRESULT OnDigPressed(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);

   void Show(CWindow& parent, UINT show);

protected:
   UINT ctrlID;
};

#endif

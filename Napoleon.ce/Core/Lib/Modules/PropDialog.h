/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Диалог с вкладками
 *
 *  ert   04/06/2009   creating
 */ 
#ifndef __PROP_DLG_H
#define __PROP_DLG_H

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include <NapoleonRes.h>
#include "BaseDialog.h"

#include <atldlgs.h>

class PropPage;
class PropDialog : public CPropertySheetImpl<PropDialog>
{
public:
   typedef CPropertySheetImpl<PropDialog> Base;

   PropDialog();
   virtual ~PropDialog();

   virtual bool OnOK() = 0;
   virtual void OnCancel();
   virtual void PageRemoved(PropPage *page);

protected:
   PropDialog(ATL::_U_STRINGorID title);

   std::vector<PropPage*> pages;

protected:
   void AddPage(PropPage *page);
};

BOOL CALLBACK DisableChildsProc(HWND hwnd, LPARAM lParam);

class PropPage : public CPropertyPageImpl<PropPage>
{
public:
   enum { IDD = 0 };

   PropPage(WORD wID, ATL::_U_STRINGorID title = (LPCTSTR)NULL) : 
      CPropertyPageImpl<PropPage>(title), owner(NULL)
	{
      m_psp.pszTemplate = MAKEINTRESOURCE(wID);
   }
   virtual ~PropPage() {}

   typedef CPropertyPageImpl<PropPage> BaseClass;
   BEGIN_MSG_MAP(PropPage)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      COMMAND_ID_HANDLER(IDCANCEL, OnCancel)
      COMMAND_ID_HANDLER(IDOK, OnOK)
      if( m_hWnd != NULL )
         CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   PROPSHEETPAGE* GetPropPage() { return &m_psp; }
   void SetOwner(PropDialog *dlg) { owner = dlg; }

   LRESULT OnCancel(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      if( owner )
         owner->OnCancel();
      return 0;
   }

   LRESULT OnOK(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      if( owner )
         owner->PostMessage(PSM_PRESSBUTTON, PSBTN_OK, 0);
      return 0;
   }

   void DisableChilds()
   {
      EnumChildWindows(m_hWnd, DisableChildsProc, (LPARAM)((HWND)GetDlgItem(IDCANCEL)));
   }

   void SetScalingValue(int id, int value, DWORD scale, bool hideRest)
   {
      wchar_t buf[20], src[20];

      ConvertScaling(src, (long)value, scale);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, scale, hideRest);
      SetDlgItemText(id, buf);
   }

   bool OnApply() { return (owner != NULL) ? owner->OnOK() : true; }

   void OnPageRelease()
   { 
      if( owner != NULL ) 
         owner->PageRemoved(this); 
   }

   virtual void Init() {}

   virtual void Sizing(WORD width, WORD height)
   {
      MoveButton(width, height);
   }

   LRESULT OnInitDialog(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
   {
      bHandled = FALSE;

      if( owner != NULL )
         Init();

      return 0;
   }

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
   {
      WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);
      Sizing(wdh, hgh);
      return 0;
   }

protected:
   static const int offset = 2;
   PropDialog *owner;

protected:
   void MoveButton(WORD wdh, WORD hgh)
   {
      int cof = offset;
      int ids [] = { IDCANCEL, IDOK };

      for( int i=0; i < sizeof(ids)/sizeof(ids[0]); i++ )
      {
         CWindow button(GetDlgItem(ids[i]));
         if( button.m_hWnd != NULL )
         {
            int bwdh;
            CRect bounds;

            button.GetWindowRect(bounds);
            bwdh = bounds.Width();
            button.MoveWindow(cof, hgh - bounds.Height() - offset, bwdh, bounds.Height(), FALSE);
            cof += bwdh + offset;
         }
      }
   }
};

#endif

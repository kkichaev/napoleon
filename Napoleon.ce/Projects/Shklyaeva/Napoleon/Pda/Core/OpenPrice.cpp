/*
 * Copyright (C), 2007, Денис Мосягин
 *
 *  Процедура открытия прайс-листа
 *
 *  ert   06/02/2008   creating
 */
#include "stdafx.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "PriceForm.h"
#include "FormEntries.h"
#include "NplConfig.h"
#include "Preference.h"
#include <DocType.h>


class PriceFormAdd : public PriceForm
{
 public:
   PriceFormAdd();

   BEGIN_MSG_MAP(PriceFormAdd)
      COMMAND_ID_HANDLER(IDC_FLAT_PRICE, ChangePrice)
      CHAIN_MSG_MAP(PriceForm)
   END_MSG_MAP()

   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuID() const { return IDD_PRICE_LIST; }

   DECLARE_FORM(PriceFormAdd, IDD_PRICE_LIST_ADD)

   virtual void LoadMenuBar();

   bool IsReportView() const { return reportView; }

 protected:
   LRESULT ChangePrice(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   bool reportView;
};

IMPLEMENT_FORM(PriceFormAdd)

PriceFormAdd::PriceFormAdd() : reportView(false)
{
}

void PriceFormAdd::LoadMenuBar()
{
   PriceForm::LoadMenuBar();

   TBBUTTON mbutton;
   mbutton.iBitmap = (reportView) ? 21 : 20;
   mbutton.idCommand = IDC_FLAT_PRICE;
   mbutton.fsState = TBSTATE_ENABLED;
   mbutton.fsStyle = TBSTYLE_BUTTON | TBSTYLE_AUTOSIZE;
   mbutton.dwData = 0;
   mbutton.iString = 0;

   menuBar.AddButtons(1, &mbutton);
}


LRESULT PriceFormAdd::ChangePrice(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   reportView = !reportView;

   ((PriceFormData*)data)->SetFlatPrice(reportView);
   Refresh();

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (reportView) ? 21 : 20;
   menuBar.SetButtonInfo(IDC_FLAT_PRICE, &bi);

   return 0;
}


void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormData(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}

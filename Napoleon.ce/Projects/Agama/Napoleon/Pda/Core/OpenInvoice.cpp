/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Форма заказа
 *
 *  ert   16/08/2007   creating
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

#include <StdFuncs.h>

class InvoiceAdd : public Invoice
{
 public:
   DECLARE_FORM(InvoiceAdd, IDD_INVOICE_ADD)

   virtual DWORD GetMenuBarID() const { return IDD_INVOICE_ADD; }
   virtual void LoadMenuBar(bool hideSIP);

   BEGIN_MSG_MAP(InvoiceAdd)
      COMMAND_ID_HANDLER(IDC_PHONE_ONOFF, PhoneOnOff)
      CHAIN_MSG_MAP(Invoice)
   END_MSG_MAP()

protected:
   LRESULT PhoneOnOff(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
};

IMPLEMENT_FORM(InvoiceAdd)

void InvoiceAdd::LoadMenuBar(bool hideSIP)
{
   Invoice::LoadMenuBar(hideSIP);

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (IsPhoneOn()) ? 23 : 24;
   
   menuBar.SetButtonInfo(IDC_PHONE_ONOFF, &bi);
}

static HANDLE hObj;
static DWORD SetCursorProc(void *)
{
   SetCursor(LoadCursor(NULL, IDC_WAIT));

   WaitForSingleObject(hObj, INFINITE);
   SetCursor(NULL);

   CloseHandle(hObj);

   return 0;
}

void SetWaitCursor(bool on)
{
   if( on )
   {
      hObj = CreateEvent(NULL, TRUE, FALSE, NULL);
      CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)SetCursorProc, NULL, 0, NULL);
   } else
      SetEvent(hObj);
}

HWND hMenuBar;
void SetButtonImage()
{
   SetWaitCursor(false);
   if( IsWindow(hMenuBar) == FALSE )
      return;

   CMenuBarCtrl b(hMenuBar);
   b.HideButton(IDC_PHONE_ONOFF, TRUE);
   b.ChangeBitmap(IDC_PHONE_ONOFF, (IsPhoneOn()) ? 23 : 24);
   b.HideButton(IDC_PHONE_ONOFF, FALSE);

   hMenuBar = NULL;
}

LRESULT InvoiceAdd::PhoneOnOff(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   SetWaitCursor(true);
   hMenuBar = menuBar.m_hWnd;

   if( IsPhoneOn() )
      ClosePhoneLine(SetButtonImage);
   else
      OpenPhoneLine(SetButtonImage);

   return 0;
}

void OpenInvoice(OrderImpl* order, bool retToDocList)
{
#ifdef ORD_DLV_BIND
   _Module.GetFrame()->Load(IDD_INVOICE_ADD, new InvoiceDlvData(order, retToDocList));
#else
   _Module.GetFrame()->Load(IDD_INVOICE_ADD, new InvoiceData(order, retToDocList));
#endif
}

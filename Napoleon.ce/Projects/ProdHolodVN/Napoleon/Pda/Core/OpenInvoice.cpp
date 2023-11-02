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
#include "Add.h"

#include <Visit.h>
#include <StdFuncs.h>

#ifdef ORD_DLV_BIND
class InvoiceDataAdd : public InvoiceDlvData
#else
class InvoiceDataAdd : public InvoiceData
#endif
{
public:
#ifdef ORD_DLV_BIND
   InvoiceDataAdd(OrderImpl *_order, bool retToDocList) : InvoiceDlvData(_order, retToDocList) {}
#else
   InvoiceDataAdd(OrderImpl *_order, bool retToDocList) : InvoiceData(_order, retToDocList) {}
#endif

   void CheckOrder()
   {
      if( order->IsDirty() )
      {
         if( remnants.items.size() == 0 )
         {
            VisitImpl v;
            SQLTable t(v.Name());

            SYSTEMTIME st;
            FILETIME start;
            FILETIME end;
            wchar_t sts[20], ends[20];

            FileTimeToSystemTime(&order->created, &st);
            ResetTime(&st);
            SystemTimeToFileTime(&st, &start);
            wsprintf(sts, L"%d%09d", (DWORD)((*(__int64*)&start) / 1000000000), (DWORD)((*(__int64*)&start) % 1000000000));

            st.wHour = 23;
            st.wMinute = 59;
            st.wSecond = 59;
            SystemTimeToFileTime(&st, &end);
            wsprintf(ends, L"%d%09d", (DWORD)((*(__int64*)&end) / 1000000000), (DWORD)((*(__int64*)&end) % 1000000000));

            std::wstring sql(L"WHERE ");
            sql += L"date > "; sql += sts; sql += L" and  date < "; sql += ends; 
            sql += L" and id ='"; sql += order->id; sql += L"'";

            if( !t.Select(&v, sql.c_str()) )
               order->params |= ofOutPlan;
            else
               order->params &= (~ofOutPlan);
            order->Write();
         }
      }
   }
};

class InvoiceAdd : public Invoice
{
public:
   InvoiceAdd() {}

   virtual DWORD GetResourceID() const { return IDD_INVOICE; }
   virtual DWORD GetMenuBarID() const { return IDD_INVOICE; }

   BEGIN_MSG_MAP(InvoiceAdd)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      CHAIN_MSG_MAP(Invoice)
   END_MSG_MAP()

   DECLARE_FORM(InvoiceAdd, IDD_INVOICE_ADD)

protected:
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      ((InvoiceDataAdd*)data)->CheckOrder();

      Invoice::Backing(nCode, id, hWnd, bHandled);
      return 0;
   }
};

IMPLEMENT_FORM(InvoiceAdd)

void OpenInvoice(OrderImpl* order, bool retToDocList)
{
   _Module.GetFrame()->Load(IDD_INVOICE_ADD, new InvoiceDataAdd(order, retToDocList));
}

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

#ifdef Autopteka_van
#include <Progress.h>
#include "DoPrint.h"
#endif

#ifdef ORD_DLV_BIND
#define INV_DATA_ADD InvoiceDlvDataAdd
#define INV_DATA InvoiceDlvData
#else
#define INV_DATA_ADD InvoiceDataAdd
#define INV_DATA InvoiceData
#endif

class OrderInfo : public CWindowImpl<OrderInfo>
{
public:
   OrderInfo()
   {
      minPremium = 0;
      minSum = 0;

      premium = 0;
      sum = 0;
   }

   void SetMinData(DWORD minPremium, DWORD minSum)
   {
      this->minPremium = minPremium / 10;
      this->minSum = minSum;
   }

   bool UpdateData(const Order& order);
   
   int Height() const;

   DECLARE_WND_CLASS(L"ORD_INFO")

   BEGIN_MSG_MAP(StaticAnchor)
      MESSAGE_HANDLER(WM_PAINT, DoPaint)
   END_MSG_MAP()

   LRESULT DoPaint(UINT uMsg, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);

protected:
   DWORD minPremium, minSum;
   DWORD sum, premium;
};

struct INV_DATA_ADD : public INV_DATA
{
   INV_DATA_ADD(OrderImpl *_order, bool retToDocList) : INV_DATA(_order, retToDocList) {}

   void AddToFullPackEx();

#ifdef Autopteka_van
   void DoPrint(const wchar_t* form, IProgressIndicator *pi);
#endif

   const Order& GetOrder() const { return *order; }

   virtual bool Selecting(int index);
};

class InvoiceAdd : public Invoice
{
public:
   InvoiceAdd() {}

   virtual DWORD GetMenuBarID() const { return IDD_INVOICE; }
   virtual DWORD GetResourceID() const { return IDD_INVOICE_ADD; }

   BEGIN_MSG_MAP(InvoiceAdd)
     COMMAND_ID_HANDLER(IDC_PACKET_INPUT, ToPack)
#ifdef Autopteka_van
     COMMAND_ID_HANDLER(IDC_PRINT, Print)
#endif
     CHAIN_MSG_MAP(Invoice)
   END_MSG_MAP()

#ifdef Autopteka_van
   LRESULT Print(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
#endif

   LRESULT ToPack(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   DECLARE_FORM(InvoiceAdd, IDD_INVOICE_ADD)

   virtual bool SetData(IFormData *_data)
   {
      if( !Invoice::SetDataEx(_data, 1) )
         return false;

      orderInfo.SubclassWindow(GetDlgItem(IDC_TEXT));
      LOGFONT lf;
      if( GetObject(GetStockObject(SYSTEM_FONT), sizeof(lf), &lf) )
      {
         if( lf.lfHeight < 0 ) lf.lfHeight++;
         else lf.lfHeight--;
         lf.lfWeight = FW_BOLD;

         HFONT font = CreateFontIndirect(&lf);
         if( font != NULL )
            orderInfo.SetFont(font);
      }

#ifdef Autopteka
      OrgImpl oi;
      oi.id = (wchar_t*)((InvoiceData*)data)->ID();
      oi.Read();
      orderInfo.SetMinData(oi.minPremium, oi.minOrder);
#endif

      orderInfo.UpdateData(((INV_DATA_ADD*)data)->GetOrder());
      MoveOrderInfo();
      return true;
   }

   void CheckOrderInfo(const Order& order)
   {
      if( orderInfo.UpdateData(order) )
         MoveOrderInfo();
   }

   void MoveOrderInfo()
   {
      int hgh = orderInfo.Height();
      CRect bounds, b2;

      GetClientRect(bounds);
      listCtrl.GetWindowRect(b2);
      ScreenToClient(b2);

      b2.bottom = bounds.Height() - hgh;
      listCtrl.MoveWindow(b2);
      orderInfo.MoveWindow(b2.left, b2.bottom, b2.Width(), hgh);
   }

   virtual void UpdateLayout(bool forceRecalc)
   {
      CRect bounds;
      Invoice::UpdateLayout(forceRecalc);

      GetClientRect(bounds);
      if( orderInfo.m_hWnd != NULL )
      {
         int hgh = orderInfo.Height();
         orderInfo.MoveWindow(0, bounds.bottom - hgh, bounds.right, hgh);
      }
   }

   virtual int BottomGap() const
   {
      return orderInfo.Height();
   }

   virtual void Refresh()
   {
      orderInfo.Invalidate();
      orderInfo.UpdateWindow();

      Invoice::Refresh();
   }

   OrderInfo orderInfo;
};

IMPLEMENT_FORM(InvoiceAdd)

LRESULT InvoiceAdd::ToPack(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( !((InvoiceData*)data)->IsExported() && data->Count() > 0 &&
      MessageBox(L"Дополнить количество до полной упаковки?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) == IDYES )
   {
      ((INV_DATA_ADD*)data)->AddToFullPackEx();
      Refresh();
   }
   return 0;
}

bool INV_DATA_ADD::Selecting(int index)
{
   if( INV_DATA::Selecting(index) )
   {
      ((InvoiceAdd*)owner)->CheckOrderInfo(*order);
      return true;
   }

   return false;
}

void INV_DATA_ADD::AddToFullPackEx()
{
   order->AddToFullPack();
   order->Write();
   ((InvoiceAdd*)owner)->CheckOrderInfo(*order);
}


#ifdef Autopteka_van

void INV_DATA_ADD::DoPrint(const wchar_t* fileName, IProgressIndicator *pi)
{
   wchar_t buf[MAX_PATH];
   GetModuleFileName(_Module.GetModuleInstance(), buf, sizeof(buf)/sizeof(buf[0]));

   wchar_t *p = wcsrchr(buf, L'\\');
   if( p ) p++;
   else p = buf;
   *p = L'\0';

   DeliveryPrint dp(*order);
   DeliverySource rs(&dp);
   PC canceller;

   wcscat(p, fileName);
   if( ::DoPrint(buf, &rs, pi, &canceller) == true )
   {
      //flags |= dfPrinted;
      //Write();
   }
}

LRESULT InvoiceAdd::Print(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   CRect menuBounds;
   menuBar.GetRect(IDC_PRINT, menuBounds);
   menuBar.ClientToScreen(menuBounds);

   HMENU hm = CreatePopupMenu();

   int ctr = 1;
   AppendMenu(hm, MF_STRING, ctr++, L"&Торг-12");
   AppendMenu(hm, MF_STRING, ctr++, L"&Счет-фактура");
   AppendMenu(hm, MF_STRING, ctr++, L"Торг-12 + С&чет-фактура");

   int res = TrackPopupMenuEx(hm,  TPM_RETURNCMD | TPM_BOTTOMALIGN, 
      menuBounds.left, menuBounds.top, m_hWnd, NULL);
   DestroyMenu(hm);

   if( res == 0 ) return 0;

   ProgressWindow pw;
   pw.CreateSTDWindow(m_hWnd);

   if( res == 1 || res == 3 )
      ((INV_DATA_ADD*)data)->DoPrint(L"TORG12.xml", &pw);

   if( res == 2 || res == 3 )
      ((INV_DATA_ADD*)data)->DoPrint(L"SCHF.xml", &pw);

   pw.DestroyWindow();
   return 0;
}
#endif

bool OrderInfo::UpdateData(const Order& order)
{
   int hgh = Height();

   PriceImpl price;
   std::vector<OrderItem>::const_iterator i = order.items.begin();

   DWORD sum0 = 0;
   sum = 0;
   for( ; i != order.items.end(); i++ )
   {
      price.id = i->id;
      price.Read();

      sum += ItemSum(i->cost, i->qty);
      sum0 += ItemSum(price.cost[0], i->qty);
   }

   premium = (sum == 0) ? 0 : DivideInPack(sum, sum0, 1000) - 1000;
   return (hgh != Height());
}

int OrderInfo::Height() const
{
   if( m_hWnd == NULL ) return 0;

   const wchar_t* p = L"А\nA";
   if( sum > minSum ) p += 2;

   HFONT hFont = GetFont();
   if( hFont == NULL )
      hFont = (HFONT)GetStockObject(SYSTEM_FONT);

   HDC dc = const_cast<OrderInfo*>(this)->GetDC();
   RECT rc = {0};
   SelectObject(dc, hFont);
   DrawText(dc, p, -1, &rc, DT_CALCRECT);
   const_cast<OrderInfo*>(this)->ReleaseDC(dc);

   return rc.bottom + 5;
}

LRESULT OrderInfo::DoPaint(UINT uMsg, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
   PAINTSTRUCT ps;
   HDC dc = BeginPaint(&ps);
   CRect bounds;
   GetClientRect(bounds);

   HBRUSH brsh = CreateSolidBrush(GetSysColor(COLOR_WINDOW));
   FillRect(dc, bounds, brsh);
   DeleteObject(brsh);

   HPEN pen = ::CreatePen(PS_SOLID, 1, RGB(0, 0, 192));
   HPEN svPen = (HPEN)SelectObject(dc, pen);

   int b = bounds.top;
   MoveToEx(dc, 0, b, NULL);
   LineTo(dc, bounds.right, b);

   HFONT hFont = GetFont();
   if( hFont != NULL )
      SelectObject(dc, hFont);

   wchar_t buf[100];

   b+=2;
   if( premium < minPremium )
   {
      SetTextColor(dc, RGB(255,0,0));
      wsprintf(buf, L"Наценка %d.%01d%% меньше %d.%01d%%", 
         premium/10, premium % 10, minPremium/10, minPremium % 10);
   } else
   {
      SetTextColor(dc, RGB(0,0,0));
      wsprintf(buf, L"Наценка по заявке %d.%01d%%", premium/10, premium % 10);
   }
   bounds.top = 0;
   bounds.left = 0;
   DrawText(dc, buf, -1, bounds, DT_CALCRECT);

   bounds.bottom = bounds.Height() + b;
   bounds.top = b;
   DrawText(dc, buf, -1, bounds, DT_SINGLELINE);
   b = bounds.bottom + 2;

   if( sum < minSum )
   {
      SetTextColor(dc, RGB(255,0,0));
      wsprintf(buf, L"Сумма заявки %d.%02d меньше %d.%02d", 
         sum/SUM_SCALE, sum % SUM_SCALE, minSum/SUM_SCALE, minSum % SUM_SCALE);

      bounds.top = 0;
      bounds.left = 0;
      DrawText(dc, buf, -1, bounds, DT_CALCRECT);

      bounds.bottom = bounds.Height() + b;
      bounds.top = b;
      DrawText(dc, buf, -1, bounds, DT_SINGLELINE);
   }

   SelectObject(dc, svPen);
   DeleteObject(pen);

   EndPaint(&ps);
   return TRUE;
}

void OpenInvoice(OrderImpl* order, bool retToDocList)
{
   _Module.GetFrame()->Load(IDD_INVOICE_ADD, new INV_DATA_ADD(order, retToDocList));
}

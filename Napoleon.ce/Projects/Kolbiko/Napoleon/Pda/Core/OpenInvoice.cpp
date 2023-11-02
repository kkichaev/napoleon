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

#include "Add.h"
#include <OrgRmnts.h>

#ifdef ORD_DLV_BIND
#define INV_DATA_ADD InvoiceDlvDataAdd
#define INV_DATA InvoiceDlvData
#else
#define INV_DATA_ADD InvoiceDataAdd
#define INV_DATA InvoiceData
#endif

struct INV_DATA_ADD;
class OrderInfo : public CWindowImpl<OrderInfo>
{
public:
   OrderInfo()
   {
      retCount = 0;
      restCount = 0;
      orderCount = 0;
   }

   bool UpdateData(const INV_DATA_ADD& data);
   
   int Height() const;

   DECLARE_WND_CLASS(L"ORD_INFO")

   BEGIN_MSG_MAP(StaticAnchor)
      MESSAGE_HANDLER(WM_PAINT, DoPaint)
   END_MSG_MAP()

   LRESULT DoPaint(UINT uMsg, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);

protected:
   DWORD retCount, restCount, orderCount;
};

struct InvFormItem : public IReflectableData
{
   wchar_t *name;
   wchar_t *sum;
   wchar_t *qty;

   DECLARE_TYPE_REFLECTION(InvFormItem)
};

BEGIN_TYPE_REFLECTION(InvFormItem)
   REGISTER_STRING_MEMBER(InvFormItem, name)
   REGISTER_STRING_MEMBER(InvFormItem, sum)
   REGISTER_STRING_MEMBER(InvFormItem, qty)
END_TYPE_REFLECTION(InvFormItem)

static ListFormData::Header invHeader[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"Зак/Возвр", L"qty", 50 },
   { ListFormData::Header::Right, L"Сумма/Вес", L"sum", 50 }
};

struct INV_DATA_ADD : public INV_DATA
{
   INV_DATA_ADD(OrderImpl *_order, bool retToDocList);
   virtual ~INV_DATA_ADD();

   virtual const Header *GetHeader() const { return invHeader; }
   virtual int ColumnsCount() const { return sizeof(invHeader)/sizeof(invHeader[0]); }

   virtual const DataReflector& DataType() const { return InvFormItem().GetType(); }
   virtual int Count() const { return items.size(); }
   //virtual int Count() const { return order->items.size(); }

   const Order& GetOrder() const { return *order; }

   virtual bool Get(IReflectableData* data, int index) const;
   virtual bool Selecting(int index);

   virtual void BeforeSetQty(QTYData* qd);
   virtual void AfterSetQty(const QTYData& qd);

   DWORD RetWeight() const { return retDoc->Weight(); }

   void SyncRetDoc()
   {
      if( order != NULL )
      {
         retDoc->ordDate = order->date;
         retDoc->shedule = order->created;
         retDoc->Write();
      }
   }

   virtual bool Send()
   {
      SyncRetDoc();
      bool ret = INV_DATA::Send();
      if( ret )
         retDoc->ClearDirty(NULL, false);
      return ret;
   }

   ReturnImpl *retDoc;
   OrgRemnantsImpl restDoc;

   std::vector<ROWID> items;
   mutable std::wstring sum, qty;
};

class InvoiceAdd : public Invoice
{
public:
   InvoiceAdd() {}

   virtual DWORD GetMenuBarID() const { return IDD_INVOICE; }
   virtual DWORD GetResourceID() const { return IDD_INVOICE_ADD; }

   DECLARE_FORM(InvoiceAdd, IDD_INVOICE_ADD)

   virtual bool SetData(IFormData *_data)
   {
      if( !Invoice::SetDataEx(_data, 2) )
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

      orderInfo.UpdateData(*(INV_DATA_ADD*)data);
      MoveOrderInfo();
      return true;
   }

   void CheckOrderInfo(const INV_DATA_ADD& data)
   {
      if( orderInfo.UpdateData(data) )
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

   virtual void SetDocumentInfoText();

   OrderInfo orderInfo;
};

IMPLEMENT_FORM(InvoiceAdd)

INV_DATA_ADD::INV_DATA_ADD(OrderImpl *_order, bool retToDocList) : INV_DATA(_order, retToDocList)
{
   retDoc = ReturnImpl::GetAssociated(*_order);
   restDoc.Load(*_order);

   std::set<std::wstring> loaded;
   PriceImpl pi;

   std::vector<OrderItem>::const_iterator oi = order->items.begin();
   for( ; oi != order->items.end(); oi++ )
   {
      if( loaded.insert(oi->id).second )
      {
         pi.id = (wchar_t*)oi->id;
         pi.Read();
         items.push_back(pi.RID());
      }
   }

   std::vector<OrgRemnantsItem>::const_iterator rsi = restDoc.items.begin();
   for( ; rsi != restDoc.items.end(); rsi++ )
   {
      if( loaded.insert(rsi->id).second )
      {
         pi.id = (wchar_t*)rsi->id;
         pi.Read();
         items.push_back(pi.RID());
      }
   }

   std::vector<OrderItem>::const_iterator ri = retDoc->items.begin();
   for( ; ri != retDoc->items.end(); ri++ )
   {
      if( loaded.insert(ri->id).second )
      {
         pi.id = (wchar_t*)ri->id;
         pi.Read();
         items.push_back(pi.RID());
      }
   }

}

INV_DATA_ADD::~INV_DATA_ADD()
{
   SyncRetDoc();
   delete retDoc;
}

bool INV_DATA_ADD::Selecting(int index)
{
   QTYData qd;

   price.Read(items[index]);
   qd.id = price.id;
   qd.canChange = !order->IsExported();

   std::vector<OrderItem>::iterator i = order->FindItem(price.id);
   if( i != order->items.end() )
   {
      qd.qty = i->qty;
      qd.sum = ItemSum(i->cost, i->qty);
      qd.flags = i->flags;
      qd.cost = i->cost;
   }

   
   if( order->HideRemnants() ) 
      qd.flags |= oiHideRemnants;

   qd.remnants = remnants.GetItemQty(price.id);

   if( order->CheckQty() == false )
      qd.flags |= oiNoCheckWHQty;

   LoadItemSales(&qd.sales, SALES_FROM_ORDERS, order->id, price.id, 0);

   ((DocumentForm*)owner)->LoadMenuBar(false);

   BeforeSetQty(&qd);
   bool retVal = SetQTY(&qd);

   ((DocumentForm*)owner)->LoadMenuBar(true);

   if( retVal )
   {
      AfterSetQty(qd);

      order->UpdateOrder(i, qd);

#ifdef SHOW_OFF_TAKE
      remnants.Update(qd.id.c_str(), qd.remnants, false);
#endif
   }
   if( retVal )
      ((InvoiceAdd*)owner)->CheckOrderInfo(*this);
   return retVal;
}

void INV_DATA_ADD::BeforeSetQty(QTYData* qd)
{
   std::vector<OrderItem>::iterator fnd = retDoc->FindItem(qd->id.c_str());
   if( fnd != retDoc->items.end() )
      qd->retQty = fnd->qty;
}

void INV_DATA_ADD::AfterSetQty(const QTYData& qd)
{
   QTYData qdr = qd;
   qdr.qty = qd.retQty;

   retDoc->UpdateOrder(retDoc->FindItem(qd.id.c_str()), qdr);
}

bool INV_DATA_ADD::Get(IReflectableData* data, int index) const
{
   //if( (unsigned)index >= order->items.size() )
   //   return false;
   if( (unsigned)index >= items.size() )
      return false;

   bool readed = price.Read(items[index]);
   if( !readed )
   {
      ((InvFormItem*)data)->name = L"?";
   } else
      ((InvFormItem*)data)->name = price.name;

   wchar_t buf[100], src[100];
   std::vector<OrderItem>::const_iterator oi = order->FindItem(price.id);

   sum.clear();
   qty.clear();
   
   long sumV = (oi != order->items.end()) ? oi->qty : 0;
   ConvertScaling(src, sumV, QTY_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % QTY_SCALE, QTY_SCALE, true);
   qty += buf;

   OrgRemnantsItem* ritem = restDoc.FindItem(price.id);
   sumV = (ritem != NULL) ? ritem->qty : 0;

   ConvertScaling(src, sumV, QTY_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % QTY_SCALE, QTY_SCALE, true);
   qty += L"\n"; 
   qty += buf;

   std::vector<OrderItem>::const_iterator ri = retDoc->FindItem(price.id);
   sumV = ( ri != retDoc->items.end() ) ? ri->qty : 0;

   ConvertScaling(src, sumV, QTY_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % QTY_SCALE, QTY_SCALE, true);
   qty += L"/"; 
   qty += buf;

   if( oi != order->items.end() )
   {
      sumV = ItemSum(oi->cost, oi->qty);
      ConvertScaling(src, sumV, SUM_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
      sum += buf;

      if( readed )
      {
         sumV = ItemWeight(price.weight, oi->qty);
         ConvertScaling(src, sumV, WEIGHT_SCALE);
         FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % WEIGHT_SCALE, WEIGHT_SCALE, TRUE);
         sum += L"\n";
         sum += buf;
      }
   }

   ((InvFormItem*)data)->qty = (wchar_t*)qty.c_str();
   ((InvFormItem*)data)->sum = (wchar_t*)sum.c_str();

   return true;
}

bool OrderInfo::UpdateData(const INV_DATA_ADD& data)
{
   int hgh = Height();

   orderCount = data.GetOrder().items.size();
   //std::vector<OrderItem>::const_iterator oi = data.GetOrder().items.begin();
   //for( ; oi != data.GetOrder().items.end(); oi++ )
   //   orderCount += oi->qty;
   //orderCount = (orderCount + QTY_SCALE / 2) / QTY_SCALE;

   retCount = data.retDoc->items.size();
   //std::vector<OrderItem>::const_iterator ri = data.retDoc->items.begin();
   //for( ; ri != data.retDoc->items.end(); ri++ )
   //   retCount += ri->qty;
   //retCount = (retCount + QTY_SCALE / 2) / QTY_SCALE;

   restCount = data.restDoc.items.size();
   //std::vector<OrgRemnantsItem>::const_iterator rsi = data.restDoc.items.begin();
   //for( ; rsi != data.restDoc.items.end(); rsi++ )
   //   restCount += rsi->qty;
   //restCount = (restCount + QTY_SCALE / 2) / QTY_SCALE;

   return (hgh != Height());
}

void InvoiceAdd::SetDocumentInfoText()
{
   sumLabel.SetSum(((DocumentData*)data)->Sum());

   DWORD weight = ((DocumentData*)data)->Weight();
   DWORD retWeight = ((INV_DATA_ADD*)data)->RetWeight();

   if( weight > WEIGHT_SCALE/2 || retWeight > WEIGHT_SCALE/2)
   {
      wchar_t buf[40], src[40], info[100];

      // округлим до одного знака после запятой
      weight += (WEIGHT_SCALE / 20);
      retWeight += (WEIGHT_SCALE / 20);

      // масштабируем
      DWORD w = WEIGHT_SCALE;
      while( w > 10 )
      {
         weight /= 10;
         retWeight /= 10;
         w /= 10;
      }

      ConvertScaling(src, weight, 10);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), weight % 10, 10, false);
      wcscpy(info, buf);
      wcscat(info, L" / ");
      ConvertScaling(src, retWeight, 10);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), retWeight % 10, 10, false);
      wcscat(info, buf);
      sumLabel.SetInfoText(info);
   }
}

int OrderInfo::Height() const
{
   if( m_hWnd == NULL ) return 0;

   const wchar_t* p = L"А";

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

   SelectObject(dc, svPen);
   DeleteObject(pen);

   wchar_t buf[100];

   b+=2;
   wsprintf(buf, L"Заказ - %d / В витр - %d / Возврат - %d", orderCount, restCount, retCount);
   SetTextColor(dc, RGB(0,0,0));

   bounds.top = 0;
   bounds.left = 0;
   int right = bounds.right;
   DrawText(dc, buf, -1, bounds, DT_CALCRECT);

   bounds.bottom = bounds.Height() + b;
   bounds.top = b;
   bounds.right = right;
   DrawText(dc, buf, -1, bounds, DT_SINGLELINE | DT_RIGHT);

   EndPaint(&ps);
   return TRUE;
}

void OpenInvoice(OrderImpl* order, bool retToDocList)
{
   _Module.GetFrame()->Load(IDD_INVOICE_ADD, new INV_DATA_ADD(order, retToDocList));
}

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

#include <DocType.h>
#include <StdFuncs.h>

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"Кол-во", L"qtyText", 50 },
   { ListFormData::Header::Right, L"Сумма", L"sum", 50 }
};

struct InvoiceDataItem : public DocumentFormItem
{
   wchar_t *qtyText;
   DECLARE_TYPE_REFLECTION(InvoiceDataItem)
};

BEGIN_TYPE_REFLECTION(InvoiceDataItem)
   REGISTER_STRING_MEMBER(InvoiceDataItem, qtyText)
   CHAIN_REFLECTION(InvoiceDataItem, DocumentFormItem)
END_TYPE_REFLECTION(InvoiceDataItem)

struct InvoiceDataAdd : public InvoiceData
{
   InvoiceDataAdd(OrderImpl *_order, bool retToDocList);
   ~InvoiceDataAdd();

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;
   virtual bool Get(IReflectableData* data, int index) const;
   virtual const DataReflector& DataType() const;

   bool DocClosed() const
   {
      if( order == NULL || order->podRemark == NULL || *order->podRemark == L'\0' )
         return false;

      int len = wcslen(order->podRemark) + 1;
      wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
      wcscpy(buf, order->podRemark);
      CharUpper(buf);

      return (wcscmp(buf, L"ЗАКРЫТА") == 0);
   }

protected:
   mutable wchar_t textBuf[100];
};

class InvoiceAdd : public Invoice
{
public:
   InvoiceAdd() {}

   virtual bool SetData(IFormData *_data);
   virtual DWORD GetResourceID() const { return IDD_INVOICE; }
   virtual DWORD GetMenuBarID() const { return IDD_INVOICE; }

   DECLARE_FORM(InvoiceAdd, IDD_INVOICE_ADD)
};

IMPLEMENT_FORM(InvoiceAdd)

InvoiceDataAdd::InvoiceDataAdd(OrderImpl *_order, bool retToDocList) : 
   InvoiceData(_order, retToDocList)
{
}

const DataReflector& InvoiceDataAdd::DataType() const
{
   return InvoiceDataItem().GetType();
}

InvoiceDataAdd::~InvoiceDataAdd()
{
}

const ListFormData::Header* InvoiceDataAdd::GetHeader() const
{
   return header;
}

int InvoiceDataAdd::ColumnsCount() const
{
   return sizeof(header)/sizeof(header[0]);
}

bool InvoiceDataAdd::Get(IReflectableData* data, int index) const
{
   if( !InvoiceData::Get(data, index) ) return false;
   
   const OrderItem &oi = order->items[index];
   
   wchar_t src[20];

   long value;
   wchar_t *rest;
   DWORD scale = QTY_SCALE;

   if( (oi.flags & oiInPack) != 0 )
   {
      PriceImpl p;
      p.id = oi.id;
      p.Read();
      value = DivideInPack(oi.qty, p.qtyInPack, QTY_SCALE);
      rest = L"у";
   } else
   {
      value = oi.qty;
      rest = L"";
   }

   ConvertScaling(src, (long)value, scale);
   FormatScaling(src, textBuf, sizeof(textBuf)/sizeof(textBuf[0]), abs(value) % scale, scale, true);
   wcscat(textBuf, rest);

   ((InvoiceDataItem*)data)->qtyText = textBuf;

   return true;
}

bool InvoiceAdd::SetData(IFormData *_data)
{
   if( Invoice::SetData(_data) == false ) return false;

   if( ((InvoiceDataAdd*)data)->DocClosed() )
      menuBar.EnableButton(IDC_SEND, FALSE);
   return true;
}

void OpenInvoice(OrderImpl* order, bool retToDocList)
{
   _Module.GetFrame()->Load(IDD_INVOICE_ADD, new InvoiceDataAdd(order, retToDocList));
}

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

BEGIN_TYPE_REFLECTION(DocumentFormItem)
   REGISTER_STRING_MEMBER(DocumentFormItem, name)
   REGISTER_LONG_SCALE_MEMBER(DocumentFormItem, sum, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER2(DocumentFormItem, qty, QTY_SCALE, true)
END_TYPE_REFLECTION(DocumentFormItem)

//
//--------------------------- InvoiceData -------------------------------------
//
InvoiceData::InvoiceData(OrderImpl *_order, bool retToDocList) : order(_order)
{
   this->retToDocList = retToDocList;

#ifdef SHOW_OFF_TAKE
   if( order )
      remnants.Load(*order);
#endif
}

InvoiceData::~InvoiceData()
{
#ifdef SHOW_OFF_TAKE
   if( remnants.items.size() == 0 )
      remnants.Remove();
#endif
   delete order;
   //offTakeHolder.ClearCache();
}

#ifdef ORDER_ONLINE
// see ObjExchange
#else
bool InvoiceData::Send()
{
   return order->Send();
}
#endif

bool InvoiceData::Adding()
{
   OrderImpl *co = order;
   order = NULL;
   OpenPriceList(co);

   return false;
}

void InvoiceData::DeleteDoc()
{
   order->Remove();

#ifdef SHOW_OFF_TAKE
   if( remnants.items.size() == 0 )
      remnants.Remove();
#endif
}

bool InvoiceData::Removing(int index)
{
   if( order->IsExported() )
      return false;

   QTYData qd;
   std::vector<OrderItem>::iterator i = order->items.begin() + index;

   qd.qty = 0;
   qd.id = i->id;
   order->UpdateOrder(i, qd);


   return true;
}

void InvoiceData::Replacing(int index)
{
   if( order->IsExported() )
      return;

   Removing(index);
   Adding();
}

#ifdef PRICE_MOVER
bool IMover::Move(QTYData *data, bool next)
{
   std::vector<OrderItem>::iterator fnd = order->FindItem(data->id.c_str());
   order->UpdateOrder(fnd, *data);
   if( data->sumLabel != NULL ) data->sumLabel->SetSum(order->Sum());

   if( fnd == order->items.end() ) return false;
   if( next )
   {
      fnd++;
      if( fnd == order->items.end() ) return false;
   } else
   {
      if( fnd == order->items.begin() ) return false;
      fnd--;
   }

   data->id = fnd->id;
   data->flags = fnd->flags;
   data->qty = fnd->qty;
   data->cost = fnd->cost;
   data->sum = 0;

   data->sales.clear();
   LoadItemSales(&data->sales, SALES_FROM_ORDERS, order->id, data->id.c_str(), 0);
   return true;
}
#endif

bool InvoiceData::Get(IReflectableData* data, int index) const
{
   if( (unsigned)index >= order->items.size() )
      return false;

   const OrderItem &oi = order->items[index];
   ((DocumentFormItem*)data)->qty = oi.qty;
   ((DocumentFormItem*)data)->sum = ItemSum(oi.cost, oi.qty);

   price.id = oi.id;
   if( price.Read() )
      ((DocumentFormItem*)data)->name = price.name;
   else
      ((DocumentFormItem*)data)->name = L"?";

   return true;
}

#ifdef ORD_DLV_BIND

#include "DocType.h"

static ListFormData::Header invDlvHeader[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"Кол-во", L"qty", 50 },
   { ListFormData::Header::Right, L"Отгрузка", L"dlvQty", 50 },
   { ListFormData::Header::Right, L"Сумма", L"sum", 50 }
};

struct InvoiceDlvDataItem : public DocumentFormItem
{
   DWORD dlvQty;

   DECLARE_TYPE_REFLECTION(InvoiceDlvDataItem)
};

BEGIN_TYPE_REFLECTION(InvoiceDlvDataItem)
   REGISTER_ULONG_SCALE_MEMBER(InvoiceDlvDataItem, dlvQty, QTY_SCALE)
   CHAIN_REFLECTION(InvoiceDlvDataItem, DocumentFormItem)
END_TYPE_REFLECTION(InvoiceDlvDataItem)

InvoiceDlvData::InvoiceDlvData(OrderImpl *_order, bool retToDocList) : 
   InvoiceData(_order, retToDocList)
{
   if( _order != NULL )
      refDoc = FindRefDoc(_order->id, _order->number);
   else
      refDoc = NULL;

   const DataReflector &reflector = GetTypeReflector(L"InvoiceDlvDataItem");
   MemberType &mt = (MemberType&)reflector.Type(L"dlvQty");
   ULongScaleType::ScaleFormat format;
   format.scale = QTY_SCALE;
   format.hideRest = true;
   mt.SetFormat(format);
}

#ifdef ORDER_ONLINE
static bool HaveItem(const DeliveryImpl& d, const wchar_t *id)
{
   vector_t<DeliveryItem>::const_iterator i = d.items.begin();
   for( ; i != d.items.end(); i++ )
   {
      if( wcscmp(i->id, id) == 0 )
         return (i->qty > 0);
   }

   return false;
}
#endif

DWORD InvoiceDlvData::Sum() const
{
#ifdef ORDER_ONLINE
   if( refDoc != NULL )
   {
      DWORD sum = 0;
      std::set<std::wstring> items;

      vector_t<OrderItem>::const_iterator oi = order->items.begin();
      for( ; oi != order->items.end(); oi++ )
      {
         if( (oi->flags & oiDirtyItem) != 0 )
         {
            items.insert(oi->id);
            sum += ItemSum(oi->cost, oi->qty);
         }
      }

      vector_t<DeliveryItem>::const_iterator di = refDoc->items.begin();
      for( ; di != refDoc->items.end(); di++ )
      {
         if( items.find(di->id) == items.end() )
            sum += di->sum;
      }
      return sum;
   }
#endif
   return (refDoc != NULL) ? refDoc->Sum() : InvoiceData::Sum();
}

DeliveryImpl* InvoiceDlvData::FindRefDoc(const wchar_t* id, const wchar_t* number)
{
   if( number == NULL || *number == L'\0' ) return NULL;

   const ::DocType* dt = docTypeManager.GetDocType(dtDelivery);

   std::wstring whereStr(L"number='");
   whereStr += number;
   whereStr += L"'";

   DeliveryImpl *doc = NULL;
   DocumentList *orgDocs = NULL;
   if( dt->GetDocuments(id, &orgDocs, whereStr.c_str()) && orgDocs->Count() >= 1 )
   {
      IDocument *d = orgDocs->Get(0);
      if( d != NULL )
      {
         orgDocs->Unbind(d);
         doc = (DeliveryImpl*)d->Data();
      }
   }
   delete orgDocs;
   return doc;
}

const DataReflector& InvoiceDlvData::DataType() const
{
   return (refDoc==NULL) ? InvoiceData::DataType() : InvoiceDlvDataItem().GetType();
}

InvoiceDlvData::~InvoiceDlvData()
{
   delete refDoc;
}

const ListFormData::Header* InvoiceDlvData::GetHeader() const
{
   return (refDoc == NULL) ? InvoiceData::GetHeader() : invDlvHeader;
}

int InvoiceDlvData::ColumnsCount() const
{
   return (refDoc == NULL) ? InvoiceData::ColumnsCount() : sizeof(invDlvHeader)/sizeof(invDlvHeader[0]);
}

bool InvoiceDlvData::Get(IReflectableData* data, int index) const
{
   if( refDoc == NULL )
      return InvoiceData::Get(data, index);

   if( !InvoiceData::Get(data, index) ) return false;
   
   std::vector<DeliveryItem>::const_iterator i = refDoc->items.begin();
   const OrderItem& item = order->items[index];
   const wchar_t *id = item.id;

   for( ; i != refDoc->items.end(); i++ )
   {
      if( wcscmp(i->id, id) == 0 )
      {
         ((InvoiceDlvDataItem*)data)->dlvQty = i->qty;
#ifdef ORDER_ONLINE
         if( (item.flags & oiDirtyItem) == 0 )
            ((InvoiceDlvDataItem*)data)->sum = i->sum;
#endif
         return true;
      }
   }
   ((InvoiceDlvDataItem*)data)->dlvQty = 0;

#ifdef ORDER_ONLINE
   if( (item.flags & oiDirtyItem) == 0 )
      ((InvoiceDlvDataItem*)data)->sum = 0;
#endif
   return true;
}

COLORREF InvoiceDlvData::GetItemColor(int index) const
{
   if( refDoc == NULL )
      return InvoiceData::GetItemColor(index);


   if( (unsigned)index < order->items.size() )
   {
      const OrderItem& item = order->items[index];

#ifdef ORDER_ONLINE
      if( (item.flags & oiDirtyItem) != 0 )
         return RGB(0, 192, 0);
#endif

      int qty = item.qty;
      const wchar_t* id = item.id;
      std::vector<DeliveryItem>::const_iterator i = refDoc->items.begin();

      for( ; i != refDoc->items.end(); i++ )
      {
         if( wcscmp(i->id, id) == 0 )
         {
            if( i->qty == qty )
               return InvoiceData::GetItemColor(index);
            break;
         }
      }
   }
   return RGB(255, 0, 0);
}

#endif // #ifdef ORD_DLV_BIND

//
//--------------------------- DocumentForm -------------------------------------
//
static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"Кол-во", L"qty", 50 },
   { ListFormData::Header::Right, L"Сумма", L"sum", 50 }
};

const ListFormData::Header* DocumentData::GetHeader() const
{
   return header;
}

int DocumentData::ColumnsCount() const
{
   return sizeof(::header)/sizeof(::header[0]);
}

const DataReflector& DocumentData::DataType() const
{ 
   return GetTypeReflector(L"DocumentFormItem");
}

bool DocumentForm::SetDataEx(IFormData *_data, int scale)
{
   if( ListForm::SetDataEx(_data, scale) == false )
      return false;

   ((DocumentData*)data)->textColor = listCtrl.GetTextColor();
   LoadMenuBar(true);

   CStatic title(::GetDlgItem(m_hWnd, IDC_ORG_TITLE));

   OrgImpl org;
   org.id = (wchar_t*)((DocumentData*)_data)->ID();
   org.Read();
   title.SetWindowTextW(org.name);

   UpdateLayout(false);   

#ifdef PRICE_MOVER
   ((DocumentData*)data)->sumLabel = &sumLabel;
#endif

   return true;
}

DWORD DocumentForm::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   lvcd->clrText = ((DocumentData*)data)->GetItemColor(lvcd->nmcd.dwItemSpec);
	return CDRF_NOTIFYITEMDRAW;
}

void DocumentForm::SetDocumentInfoText()
{
   sumLabel.SetSum(((DocumentData*)data)->Sum());

#ifndef Troya
   DWORD weight = ((DocumentData*)data)->Weight();
   if( weight > WEIGHT_SCALE/2 )
   {
      wchar_t buf[40], src[40];
      if(weight % WEIGHT_SCALE) // округление
         weight = ((weight + WEIGHT_SCALE/2) / WEIGHT_SCALE) * WEIGHT_SCALE;

      ConvertScaling(src, weight, WEIGHT_SCALE);
      FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), weight % WEIGHT_SCALE, WEIGHT_SCALE, true);
      wcscat(buf, L" кг");
      sumLabel.SetInfoText(buf);
   }
#endif
}

void DocumentForm::LoadMenuBar(bool hideSIP)
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 
      (hideSIP) ? SHCMBF_HIDESIPBUTTON : 0));

   if( hideSIP )
   {
      sumLabel.CreateLabel(menuBar.m_hWnd, 120);
      SetDocumentInfoText();
   }
}

void DocumentForm::UpdateLayout(bool forceRecalc)
{
   CRect bounds, rcTitle;
   CStatic title(GetDlgItem(IDC_ORG_TITLE));

   title.GetWindowRect(&rcTitle);
   GetClientRect(bounds);
   ScreenToClient(rcTitle);

   int listHeight = BottomGap();
   if( listHeight != 0 )
      listHeight = bounds.Height() - rcTitle.bottom - listHeight;

   /*
   HWND hInfo = ::GetDlgItem(m_hWnd, IDC_ORDER_INFO);
   if( hInfo != NULL )
   {
      CWindow info(hInfo);
      CRect infoBounds;

      info.GetWindowRect(infoBounds);
      info.SetWindowPos(NULL, 0, bounds.bottom - infoBounds.Height(), 
         bounds.right, infoBounds.Height(), SWP_NOZORDER|SWP_NOOWNERZORDER);

      listHeight = bounds.Height() - rcTitle.bottom - infoBounds.Height();
   }
   */

   SetListLayout(forceRecalc, rcTitle.bottom, listHeight);
   sumLabel.UpdateLayout();
   
   title.SetWindowPos(NULL, 0, 0, bounds.right, rcTitle.Height(), SWP_NOZORDER|SWP_NOOWNERZORDER);
}

void DocumentForm::Refresh()
{
   ListForm::Refresh();
   SetDocumentInfoText();
}

//
//--------------------------- Invoice -------------------------------------
//
IMPLEMENT_FORM(Invoice)

Invoice::Invoice()
{
}

void Invoice::LoadMenuBar(bool hideSIP)
{
   DocumentForm::LoadMenuBar(hideSIP);

   menuBar.EnableButton(IDC_SEND, (listCtrl.GetItemCount()>0) ? TRUE : FALSE);
   if( ((InvoiceData*)data)->IsExported() )
      menuBar.EnableButton(IDC_ADD, FALSE);
}

LRESULT Invoice::ShowContextMenu(HWND hWnd, const CPoint &org)
{
   if( ((InvoiceData*)data)->IsExported() )
      return 0;
   return ListForm::ShowContextMenu(hWnd, org);
}

void Invoice::WriteChanges()
{
   if( ((InvoiceData*)data)->Count() == 0 )
      ((InvoiceData*)data)->DeleteDoc();
}

LRESULT Invoice::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   WriteChanges();

   if( !CreateNextDoc(((InvoiceData*)data)->ID()) )
   {
      if( ((InvoiceData*)data)->retToDocList )
         OpenOrgDocs(((InvoiceData*)data)->ID(), ((InvoiceData*)data)->DocType());
      else
         OpenListDoc(((InvoiceData*)data)->DocType());
   }
   return 0;
}

LRESULT Invoice::ShowDetail(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   LoadMenuBar(false);
   if( ((InvoiceData*)data)->EditDetail() )
      Refresh();
   LoadMenuBar(true);
   return 0;
}

LRESULT Invoice::SendOrder(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( ((InvoiceData*)data)->Send() )
      menuBar.EnableButton(IDC_ADD, FALSE);
   return 0;
}

LRESULT Invoice::Replace(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   ((InvoiceData*)data)->Replacing(listCtrl.GetSelectedIndex());
   return 0;
}

#ifdef ORD_ADD_TO_PACK
LRESULT Invoice::ToPack(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( !((InvoiceData*)data)->IsExported() && data->Count() > 0 &&
      MessageBox(L"Дополнить количество до полной упаковки?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) == IDYES )
   {
      ((InvoiceData*)data)->AddToFullPack();
      Refresh();
   }

   return 0;
}
#endif


#ifdef ORDER_ONLINE
DWORD Invoice::GetMenuID() const
{
   return IDC_REPLACE_ADD_DEL;
}
#else
DWORD Invoice::GetMenuID() const
{
   return IDR_ADD_REMOVE;
}
#endif

void Invoice::Refresh()
{
   DocumentForm::Refresh();
   menuBar.EnableButton(IDC_SEND, (listCtrl.GetItemCount()>0) ? TRUE : FALSE);
}

#ifdef VAN_SELLING
#include <Progress.h>
#include "DoPrint.h"

void InvoiceData::DoPrint(const wchar_t* form, IProgressIndicator *pi)
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

   wcscat(p, form);
   if( ::DoPrint(buf, &rs, pi, &canceller) == true )
   {
   }
}

LRESULT Invoice::Print(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   CRect menuBounds;
   menuBar.GetRect(IDC_PRINT, menuBounds);
   menuBar.ClientToScreen(menuBounds);

   HMENU hm = CreatePopupMenu();

   int ctr = 1;
   AppendMenu(hm, MF_STRING, ctr++, L"&Торг-12");
   AppendMenu(hm, MF_STRING, ctr++, L"&Счет-фактура");
#ifdef Fusion
   AppendMenu(hm, MF_STRING, ctr++, L"Накладная");
#else
   AppendMenu(hm, MF_STRING, ctr++, L"Торг-12 + С&чет-фактура");
#endif

   int res = TrackPopupMenuEx(hm,  TPM_RETURNCMD | TPM_BOTTOMALIGN, 
      menuBounds.left, menuBounds.top, m_hWnd, NULL);
   DestroyMenu(hm);

   if( res == 0 )
      return 0;

   ProgressWindow pw;
   pw.CreateSTDWindow(m_hWnd);

#ifdef Fusion
   if( res == 1 )
      ((InvoiceData*)data)->DoPrint(L"TORG12.xml", &pw);

   if( res == 2 )
      ((InvoiceData*)data)->DoPrint(L"SCHF.xml", &pw);

   if( res == 3 )
      ((InvoiceData*)data)->DoPrint(L"nakl.xml", &pw);
#else
   if( res == 1 || res == 3 )
      ((InvoiceData*)data)->DoPrint(L"TORG12.xml", &pw);

   if( res == 2 || res == 3 )
      ((InvoiceData*)data)->DoPrint(L"SCHF.xml", &pw);
#endif
   pw.DestroyWindow();
   return 0;
}
#else
LRESULT Invoice::Print(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   return 0;
}
#endif

//
//--------------------------- Delivery -------------------------------------
//
IMPLEMENT_FORM(DeliveryForm);

DeliveryData::DeliveryData(DeliveryImpl *d, const wchar_t* retType)
{
   delivery = d;
   this->retType = retType;
}

DeliveryData::~DeliveryData()
{
   delete delivery;
}

bool DeliveryData::Get(IReflectableData *data, int index) const
{
   if( delivery == NULL || delivery->items.size() <= (unsigned)index ) return false;

   const DeliveryItem &di = delivery->items[index];
   ((DocumentFormItem*)data)->qty = di.qty;
   ((DocumentFormItem*)data)->sum = di.sum;

   price.id = di.id;
   if( price.Read() )
      ((DocumentFormItem*)data)->name = price.name;
   else
      ((DocumentFormItem*)data)->name = L"?";

   return true;
}

bool DeliveryData::Selecting(int index)
{
   QTYData qd;

   std::vector<DeliveryItem>::iterator i = delivery->items.begin() + index;

   price.id = i->id;
   if( !price.Read() ) return false;

   qd.id = i->id;
   qd.cost = ItemCost(i->sum, i->qty);
#ifdef Provisia
   qd.dcost = qd.cost;
#endif
   qd.qty = i->qty;
   qd.sum = i->sum;
   qd.flags = 0;
   qd.canChange = false;
   
   ((DocumentForm*)owner)->LoadMenuBar(false);
   SetQTY(&qd);
   ((DocumentForm*)owner)->LoadMenuBar(true);

   return false;
}

DeliveryForm::DeliveryForm()
{
}

LRESULT DeliveryForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenOrgDocs(((DocumentData*)data)->ID(), ((DeliveryData*)data)->GetDocType());
   return 0;
}

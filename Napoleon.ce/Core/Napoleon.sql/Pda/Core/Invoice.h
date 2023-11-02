/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Форма заказа
 *
 *  ert   16/08/2007   creating
 */
#ifndef __INVOICE_H
#define __INVOICE_H

#include <ListForm.h>
#include <NapoleonRes.h>

#include "ObjImpl.h"
#include "SumLabel.h"

#ifdef SHOW_OFF_TAKE
#include "OrgRmnts.h"
#include "OffTake.h"
#endif

struct DocumentFormItem : public IReflectableData
{
   const wchar_t *name;
   DWORD qty;
   DWORD sum;

   DECLARE_TYPE_REFLECTION(DocumentFormItem)
};

struct DocumentData : public ListFormData
{
   virtual DWORD Sum() const = 0;
   virtual DWORD Weight() const = 0;
   virtual const wchar_t* ID() const = 0;

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual COLORREF GetItemColor(int index) const { return textColor; }
   virtual const DataReflector& DataType() const;

   mutable PriceImpl price;
   COLORREF textColor;

#ifdef PRICE_MOVER
   SumLabel *sumLabel;
#endif

};

class DocumentForm : public ListForm, public CCustomDraw<DocumentForm>
{
public:
   DocumentForm() {}

   virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 1); }
   virtual void LoadMenuBar(bool hideSIP);

   virtual void UpdateLayout(bool forceRecalc);
   virtual void Refresh();

   BEGIN_MSG_MAP(DocumentForm)
      CHAIN_MSG_MAP(CCustomDraw<DocumentForm>)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/)
   {
      return CDRF_NOTIFYITEMDRAW;
   }

   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

   virtual DWORD GetResourceID() const { return IDD_INVOICE; }

protected:
   bool SetDataEx(IFormData *_data, int scale);
   virtual void SetDocumentInfoText();
   virtual int BottomGap() const { return 0; }
};

struct InvoiceData : public DocumentData
{
   InvoiceData(OrderImpl *_order, bool retToDocList);
   ~InvoiceData();

   //const Order& GetOrder() const { return *order; }
   virtual DWORD Sum() const { return order->Sum(); }
   virtual DWORD Weight() const { return order->Weight(); }
   virtual const wchar_t* ID() const { return order->id; }

   const wchar_t* DocType() const { return order->DocType(); }

   bool IsExported() const { return order->IsExported(); }

   virtual int Count() const { return order->items.size(); }

   virtual bool Get(IReflectableData* data, int index) const;
   
   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }

   virtual bool Selecting(int index);
   virtual bool Adding();
   virtual bool Removing(int index);
   virtual bool Editing(int index) { return Selecting(index); }

   virtual void BeforeSetQty(QTYData* qd) {}
   virtual void AfterSetQty(const QTYData& qd) {}

   //DocumentTypes DocumentType() const { return (order ==NULL) ? dtOrder : order->DocumentType(); }

   bool EditDetail()
   {
      if( order->EditDetail())
      {
         order->Write();
         return true;
      }
      return false;
   }

   virtual bool Send();

   bool retToDocList;

   void DeleteDoc();

   void Replacing(int index);

#ifdef VAN_SELLING
   void DoPrint(const wchar_t* form, IProgressIndicator *pi);
#endif

#ifdef ORD_ADD_TO_PACK
   void AddToFullPack()
   {
      order->AddToFullPack();
      order->Write();
   }
#endif

protected:
   OrderImpl *order;

#ifdef SHOW_OFF_TAKE
   OrgRemnantsImpl remnants;
#endif

};

#ifdef ORD_DLV_BIND
struct InvoiceDlvData : public InvoiceData
{
   InvoiceDlvData(OrderImpl *_order, bool retToDocList);
   ~InvoiceDlvData();

   virtual DWORD Sum() const;
   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;
   virtual bool Get(IReflectableData* data, int index) const;
   virtual const DataReflector& DataType() const;

   virtual COLORREF GetItemColor(int index) const;

protected:
   DeliveryImpl *FindRefDoc(const wchar_t* id, const wchar_t *number);

   DeliveryImpl *refDoc;
};
#endif

class Invoice : public DocumentForm
{
public:
   Invoice();

   DECLARE_FORM(Invoice, IDD_INVOICE)

   BEGIN_MSG_MAP(Invoice)
      MSG_WM_CONTEXTMENU(ShowContextMenu)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_DETAIL, ShowDetail)
      COMMAND_ID_HANDLER(IDC_REPLACE, Replace)
      COMMAND_ID_HANDLER(IDC_SEND, SendOrder)
      COMMAND_ID_HANDLER(IDC_PRINT, Print)
#ifdef ORD_ADD_TO_PACK
      COMMAND_ID_HANDLER(IDC_PACKET_INPUT, ToPack)
#endif
      CHAIN_MSG_MAP(DocumentForm)
   END_MSG_MAP()

   virtual void LoadMenuBar(bool hideSIP);
   virtual DWORD GetMenuID() const;

   virtual void WriteChanges();
	
protected:
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ShowDetail(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT SendOrder(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ShowContextMenu(HWND hWnd, const CPoint &org);
   LRESULT Print(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Replace(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

#ifdef ORD_ADD_TO_PACK
   LRESULT ToPack(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
#endif

   virtual void Refresh();
};

class DeliveryData : public DocumentData
{
public:
   DeliveryData(DeliveryImpl *d, const wchar_t* retType);
   ~DeliveryData();

   virtual DWORD Sum() const { return (delivery == NULL ) ? 0 : delivery->Sum(); }
   virtual DWORD Weight() const { return 0; }
   virtual const wchar_t* ID() const { return (delivery == NULL ) ? L"" : delivery->id; }
   virtual int Count() const { return (delivery == NULL ) ? 0 : delivery->items.size(); }

   virtual bool Get(IReflectableData* data, int index) const;
   
   virtual bool Selecting(int index);
   virtual bool Editing(int index) { return Selecting(index); }

   const wchar_t* GetDocType() const { return retType; }

   const DeliveryImpl* GetDelivery() const { return delivery; }

protected:
   DeliveryImpl* delivery;
   const wchar_t* retType;
};

class DeliveryForm : public DocumentForm
{
public:
   DeliveryForm();

   DECLARE_FORM(DeliveryForm, IDD_DELIVERY);

   BEGIN_MSG_MAP(DeliveryForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      CHAIN_MSG_MAP(DocumentForm)
  END_MSG_MAP();

protected:
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
};

#ifdef PRICE_MOVER
#include "FormEntries.h"
struct IMover : public QTYData::Mover
{
   IMover(OrderImpl *o) : order(o) {}

   virtual bool Move(QTYData *data, bool next);

   OrderImpl *order;
};
#endif

#endif

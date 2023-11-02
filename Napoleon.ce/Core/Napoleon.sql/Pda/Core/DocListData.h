/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Список заказов
 * 
 *  ert   01/09/2007   creating
 */ 
#ifndef __LIST_DOC_H
#define __LIST_DOC_H

#include "ObjImpl.h"

struct ListDocItem : public IReflectableData
{
   const wchar_t *name;
   FILETIME date;
   DWORD sum;

   DECLARE_TYPE_REFLECTION(ListDocItem)
};

class ListDoc;
class ListDocData : public ListFormData
{
public:
   ListDocData(const wchar_t *docType);
   ~ListDocData();

   const DocType* Type() const { return docType; }

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual const DataReflector& DataType() const { return ListDocItem().GetType(); }
   virtual int Count() const { return docList->Count(); }
   virtual bool Get(IReflectableData* data, int index) const;
   
   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }

   virtual bool Selecting(int index);
   virtual bool Editing(int index);
   virtual bool Removing(int index);

   //const Order& CurOrder() const { return *order; }

   void RemoveOrdersTill(const SYSTEMTIME &check);

   bool IsProceeded(int index) const;
   bool IsDirty(int index) const;

#if defined(Zakroma) || defined(SklRybinsk)
   bool IsClosed(int index) const;
#endif

#ifdef ORD_DLV_BIND
   bool OrderHandled(int index) const;
#endif

   bool SendOrders();

   void OpenDocType(const wchar_t *type);

protected:
   const DocType* docType;
   DocumentList *docList;

   mutable OrgImpl org;

   static const wchar_t *lastViewType;
};

class ListDoc : public ListForm
{
public:
   ListDoc();

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(ListDoc)
      NOTIFY_CODE_HANDLER_EX(NM_CLICK, DoSelect)
      NOTIFY_CODE_HANDLER_EX(LVN_GETDISPINFO, SetCellInfo)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      COMMAND_ID_HANDLER(IDC_REMOVE_ORDERS, Remove)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   DECLARE_FORM(ListDoc, IDD_ORDER_LIST)

   DWORD GetHitTest() const { return hitFlags; }

   virtual DWORD GetMenuID() const { return IDR_ADD_REMOVE; }

protected:
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);
   LRESULT DoSelect(LPNMHDR hdr);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   virtual LRESULT SetCellInfo(LPNMHDR hdr);

protected:
   DWORD hitFlags;
};

#endif

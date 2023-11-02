/*
 * Copyright (C), 2006-2011, Денис Мосягин
 *
 * отметка о приеме заявки
 *
 *  ert   17/06/2009   creating
 */ 
#ifndef __ORD_PRCD_RCVR_H
#define __ORD_PRCD_RCVR_H

#include <map>

class OrderProceededImpl : public DBImpl<OrderProceeded>
{
public:
   OrderProceededImpl() :  DBImpl(OrderImpl().Name()) { }

   virtual const wchar_t*  KeyFields() const { return OrderImpl().KeyFields(); }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class OrdPcdRcvr : public DBObjectRcvr<OrderProceededImpl>
{
public:
   typedef DBObjectRcvr<OrderProceededImpl> Base;

   OrdPcdRcvr();

   virtual bool Prepare(ReceivedStream* stream);
   virtual bool Write(const OrderProceededImpl& data);
   virtual void Close();

protected:
   struct Handler
   {
      ~Handler() { delete table; }

      SQLTable *table;
      IProceededHandler *handler;
   };

   typedef std::map<std::wstring, Handler*> HandlerList;
   HandlerList handlers;
};


#endif
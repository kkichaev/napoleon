/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Баланс
 *
 *  ert   05/11/2010   creating
 */
#ifndef __BALANCE_DOC_H
#define __BALANCE_DOC_H

class DeliveryRcvr : public DBObjectRcvr<DeliveryImpl>
{
public:
   typedef DBObjectRcvr<DeliveryImpl> Base;
   DeliveryRcvr() ;

   virtual bool Write(const DeliveryImpl& data);
};

class PaymentRcvr : public DBObjectRcvr<PaymentImpl>
{
public:
   typedef DBObjectRcvr<PaymentImpl> Base;
   PaymentRcvr();

   virtual bool Write(const PaymentImpl& data);
};


#endif
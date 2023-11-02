   /*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Печать
 *
 *  ert   28/05/2008   creating
 */
#ifndef __DO_PRINT_H
#define __DO_PRINT_H

#include <Print.h>
#include <DocImpl.h>
#include "NplConfig.h"

class ReflectableSource;
class CollectionSource : public IDataSource
{
 public:
   CollectionSource(const IDataCollection &data, ReflectableSource *parent);
   ~CollectionSource();

   virtual IDataSource *GetObject(const wchar_t *name);
   virtual bool GetValue(std::wstring *value, const wchar_t *name);
   virtual bool HaveMoreData() const { return (index < data.Count() - 1); }
   virtual bool MoveNext();
   virtual void PrintData();

   virtual void StartPage() {}

 protected:
   int index;
   ReflectableSource *ritem, *parent;
   IReflectableData *item;
   const IDataCollection &data;
};

class ReflectableSource : public IDataSource
{
 public:
   ReflectableSource(IReflectableData *data);
   ~ReflectableSource();

   virtual IDataSource *GetObject(const wchar_t *name);
   virtual bool GetValue(std::wstring *value, const wchar_t *name);
   virtual bool HaveMoreData() const { return false; }
   virtual bool MoveNext() { return false; }

   virtual void StartPage() {}
   virtual void PrintData() {}

   virtual void NextCollectionItem(const IReflectableData &data) {}

 protected:
   IReflectableData *data;
   std::vector<CollectionSource*> objects;
};

struct ConnectConfig
{
   ConnectConfig();
   ~ConnectConfig();

   bool Save();
   bool Load();

   void SetData(ConnectData *data); // copy data from

   ConnectData *data;
   std::string type;
   WORD copies;
};

struct IPrintCancel
{
   virtual void Cancel() = 0;
};

struct IPrintCanceller
{
   virtual void SetCanceller(IPrintCancel *printCancel) = 0;
};

struct PC : public IPrintCanceller
{
   virtual void SetCanceller(IPrintCancel *printCancel) {}
};

bool DoPrint(const wchar_t *formName, IDataSource *source, IProgressIndicator *pc, IPrintCanceller *printCanceller);

void DigToText(std::wstring *str, DWORD dig);
void AddRest( long val, std::wstring *str, const wchar_t *base, const wchar_t *restSet[] );

struct DeliveryItemPrint : public IReflectableData
{
   DeliveryItemPrint() {}
   DeliveryItemPrint(const OrderItem &item, StringHolder *sh, DWORD num, DWORD costType);

   wchar_t  *id;
   DWORD    qty;  // QTY_SCALE
   DWORD    sum;  // SUM_SCALE
   DWORD    qtyInPack; // QTY_SCALE
   DWORD    pack;      // QTY_SCALE

   wchar_t *name;
   DWORD    num;      // SUM_SCALE
   DWORD    cost;     // SUM_SCALE
   DWORD    costtax;  // SUM_SCALE
   DWORD    sumwtax;  // SUM_SCALE
   DWORD    sumtax;   // SUM_SCALE
   DWORD    tax;      // SUM_SCALE
   DWORD    weight;   //WEIGHT_SCALE
   wchar_t *unit;
   wchar_t *unitCode;

   wchar_t *country;
   wchar_t *countryCode;
   wchar_t *ntd;
   
   DECLARE_TYPE_REFLECTION(DeliveryItemPrint)
};

class SupplSource
{
public:
   SupplSource();

#ifdef Autopteka_van
   void SetSupplyer(const wchar_t* code, const wchar_t* account);
#else
   void SetSupplyer(const wchar_t* code);
#endif
   bool GetValue(std::wstring* val, const wchar_t* name);

protected:

   wchar_t *inn;
   wchar_t *name;
   wchar_t *bank;
   wchar_t *address;
#ifdef Fusion
   wchar_t *factAddress;
   wchar_t *fullName;
#endif
   wchar_t *phone;
   wchar_t *buh;
   wchar_t *chief;


   StringHolder sh;
};

struct DeliveryPrint : public IReflectableData
{
   DeliveryPrint() {}
   DeliveryPrint(const OrderImpl &dlv);

   FILETIME created;
   FILETIME date;
   wchar_t *id;
   wchar_t *number;
   wchar_t *remark;
   DWORD    flags;
   DWORD link;

   wchar_t *account;

   vector_t<DeliveryItemPrint> items;

   wchar_t *name;
   wchar_t *address;
#ifdef Fusion
   wchar_t *factAddress;
   wchar_t *agent;
   wchar_t *baseName;
   wchar_t *baseAddress;
   wchar_t *basePhone;
   wchar_t *baseBank;
   wchar_t *baseInn;
#endif
   wchar_t *phone;
   wchar_t *inn;
   wchar_t *bank;

   DWORD    qty;  // QTY_SCALE
   DWORD    pack;  // QTY_SCALE
   DWORD    sumwtax;  // SUM_SCALE
   DWORD    sumtax;   // SUM_SCALE
   DWORD    sumtax10;   // SUM_SCALE
   DWORD    sumtax18;   // SUM_SCALE
   DWORD    sum;   // SUM_SCALE

   DWORD    pageqty; // QTY_SCALE
   DWORD    pagepack;  // QTY_SCALE
   DWORD    pagesumwtax; // SUM_SCALE
   DWORD    pagesumtax;   // SUM_SCALE
   DWORD    pagesum;   // SUM_SCALE

   wchar_t *numText;
   wchar_t *sumText;
   wchar_t *qtyText;

   wchar_t *TotalPageText;

   SupplSource suppl;
   DECLARE_TYPE_REFLECTION(DeliveryPrint)

   StringHolder sh;
};

class DeliverySource : public ReflectableSource
{
public:
   DeliverySource(DeliveryPrint *data);

   virtual bool GetValue(std::wstring *value, const wchar_t *name);
   virtual void StartPage();
   virtual void NextCollectionItem(const IReflectableData &idata);

   WORD pageCount;
};

struct PaymentPrint  : public IReflectableData
{
   PaymentPrint() {}
   PaymentPrint(const PaymentImpl &pi);

   FILETIME date;
   wchar_t *number;
   wchar_t *remark;

   wchar_t *name;
   wchar_t *address;
   wchar_t *phone;
   wchar_t *inn;
   wchar_t *bank;

   DWORD    sum;    // SUM_SCALE
   DWORD    sumTax; // SUM_SCALE

   wchar_t* sumText;
   wchar_t* taxText;

   DECLARE_TYPE_REFLECTION(PaymentPrint)

   SupplSource suppl;
   StringHolder sh;
};

class PaymentSource : public ReflectableSource
{
public:
   PaymentSource(PaymentPrint *data) : ReflectableSource(data)
   {
   }

   virtual bool GetValue(std::wstring *value, const wchar_t *name);
};

#endif


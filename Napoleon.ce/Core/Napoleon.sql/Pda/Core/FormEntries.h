/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Точки входа для форм. Функции сами освобождают параметр который передается
 *
 *  ert   17/08/2007   creating
 */
#ifndef __FORMS_ENTRIES_H
#define __FORMS_ENTRIES_H

#include <Exchange.h>
#include <DocType.h>
#include <DocImpl.h>

#include <set>
#include <string>

//
// Диалог количество
//
class SumLabel;
struct QTYData
{
   QTYData();

   std::wstring id;

   DWORD qty; // QTY_SCALE
   WORD  flags; // oiInPack etc
   DWORD cost; // if cost == 0 -> cost == sum / qty
   DWORD sum;

#ifdef Provisia
   DWORD dcost;
   short discount; // DISCOUNT_SCALE
#endif

#ifdef Alians
   DWORD remnants; // QTY_SCALE
   bool  showRemnants;
   bool  enterPacket;
#endif

#if defined(Autopteka) || defined(Autopteka_van)
   FILETIME orderCreated;
   std::wstring orgID;
#endif

#ifdef QTY_DATA_COST_TYPE
   WORD costType;
#endif

#ifdef Proviant
   std::wstring unit;
#endif

#ifdef PRICE_MOVER
   ~QTYData();

   struct Mover
   {
      virtual bool Move(QTYData *data, bool next) = 0;
   };

   Mover *mover;
   SumLabel *sumLabel;
#endif

#if defined(MULTI_WH) || defined(FIRMS_REST) || defined(WH_QTY)
   short whIndex;
#endif

#ifdef ORD_ITEM_DISCOUNT
   int discount; // SUM_SCALE
   DWORD itemCost; // SUM_SCALE
#endif

#ifdef SHOW_OFF_TAKE
   DWORD remnants; // QTY_SCALE
#endif

#ifdef Leopard
   std::wstring pack;
   std::wstring whCode;
#endif

#ifdef Kolbiko
   DWORD retQty; // QTY_SCALE
#endif

   bool canChange;

   std::vector<ItemSales> sales; 
};

//
// открыть список организаций
//
// открыть список по умолчанию (обычно заявки)
void OpenOrgList();
void OpenOrgList(const wchar_t* type);

//
// открыть папки с организациями
//
void OpenOrgFolders(const wchar_t* type);

//
// открыть список документов по организации
//
void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type);

//
// открыть заказ
//
void OpenInvoice(OrderImpl* order, bool retToDocList = true);

//
// открыть накладную
//
void OpenDelivery(DeliveryImpl *delivery, const wchar_t *type);

//
// параметры заказа
//
bool EditOrderDetail(OrderImpl *order);

//
// Загрузить продажи по позиции
//
void LoadItemSales(std::vector<ItemSales> *sales, bool fromOrders, const wchar_t* orgID, 
                   const wchar_t* itemID, const ROWID& ignoredDocument);

//
// Загрузка последних продах
//
void LoadLastSales(std::set<std::wstring> *items, const OrderImpl& order, bool fromOrders);

//
// форма прайс-листа
//
void OpenPriceList(OrderImpl* order);

struct IPriceSelect
{
   virtual ~IPriceSelect() {}

   virtual bool IsSelected(const wchar_t* id) const = 0;
   virtual void Select(const wchar_t* id) = 0;
   virtual void Backing() = 0;

   virtual bool CanSelect() const { return true; }
   virtual bool CanBacking() const { return true; }
};
void SelectPriceItem(IPriceSelect *selector, OrderImpl *o = NULL);

//
//Диалог количество
//
bool SetQTY(QTYData *data); // data не удаляется

//
// Общая функция создания документа для OrgList & OrgDocs
//
class ListForm;
class DocType;
bool AddNewDocument(ListForm* owner, const DocType* docType, const ROWID& orgID);

//
// открыть список заказов
//
void OpenListDoc(const wchar_t *docType = dtOrder);

#ifdef RCV_MESSAGE
void OpenMessageList();
void AddMessage(const wchar_t *message);
#endif

#ifdef PROXY_DOC
extern wchar_t dtProxy[];
#endif

#ifdef PRICE_MOVER
struct PriceFormData;
struct PMover : public QTYData::Mover
{
   PMover(OrderImpl *o, PriceFormData *d, int sel) : order(o), data(d), selected(sel)
   {
   }

   virtual bool Move(QTYData *data, bool next);

   bool LoadNext(ROWID *id, bool next);

   OrderImpl *order;
   PriceFormData *data;

   int selected;
};
#endif

#ifdef ORG_NOTE
void OpenNote(HWND parent, const wchar_t *orgID, bool openIfExist);
#endif

#ifdef GPS_POS
bool CheckGPSPos(const wchar_t *message, DWORD waitTime = 5 * 60 * 1000);
#endif

#endif

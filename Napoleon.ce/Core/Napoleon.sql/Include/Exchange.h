/*
 * Copyright (C), 2006-2007, Денис Мосягин
 *
 * Форматы для синхронизации (тест)
 *
 *  ert   01/12/2006   creating
 */ 
#ifndef __EXCHANGE_TYPE_H
#define __EXCHANGE_TYPE_H

#include <TypeHolder.h>
#include <StdConsts.h>

#ifdef COMPATIBILITY
//
//------------------------ SERVER COMMANDS ----------------------------
//
#define CMD_LENGTH 60
#define SND_ORDER_W      "SNDWORDERS"    // DB_VER Login Pwd Count Size
#define SND_ORG_RMNTS    "SNDORGRMNTS"   // DB_VER Login Pwd Count Size
#define SND_ORG_INFO     "SNDORGINFO"    // DB_VER Login Pwd Count Size
#define SND_ORG_POLL     "SNDORGPOLL"    // DB_VER Login Pwd Count Size
#define SND_ORG_REST     "SNDORGREST"    // DB_VER Login Pwd Count Size
#define SND_PROXY        "SNDPROXY"      // DB_VER Login Pwd Count Size

#define SND_GPS_DATA     "SNDGPSDATA"    // DB_VER Login Pwd Count Size

#ifdef RPK
#define SND_SKU          "SNDSKUS"       // DB_VER Login Pwd Count Size
#define SND_PLANS        "SNDPLANS"      // DB_VER Login Pwd Count Size
#define SND_DEFECTS      "SNDDEFECTS"    // DB_VER Login Pwd Count Size
#define SND_PLN_TBL     L"SNDPLANTABLE"  // DB_VER Size
#endif

#ifdef Tukanov
#define SND_RETS         "SNDRETS"       // DB_VER Login Pwd Count Size
#endif

#ifdef VISIT_DOC
#define SND_VISIT        "SNDVISIT"      // DB_VER Login Pwd Count Size
#endif

#define ACK_ORDERS       "ACKWORDERS"    // DB_VER Login Pwd
#define ACK_PRICE_W      "ACKWPRICE"     // DB_VER Login Pwd
#define ACK_REMNANTS_W   "ACKREMNANTS"   // DB_VER Login Pwd
#define ACK_BALANCE      "ACKBALANCE"    // DB_VER Login Pwd
#define ACK_PHOTO        "ACKPHOTO"      // DB_VER Login Pwd
#define ACK_IMAGE        "ACKIMAGE"      // Image Name
#define ACK_PRCD         "ACKPRCD"       // DB_VER Login Pwd
#define ACK_PRCD_EX      "ACKEX_PRCD"    // DB_VER Login Pwd

// команды от сервера приходят в UNICODE
#define SND_PRICE_W      L"SNDWPRICE"    // DB_VER Size
#define SND_REMNANTS_W   L"SNDREMNANTS"  // DB_VER Size
#define SND_ORGS_W       L"SNDWORGS"     // DB_VER Size
#define SND_FORGS_W      L"SNDFLORGS"    // DB_VER Size
#define SND_FOLDERS_W    L"SNDWFOLDERS"  // DB_VER Size
#define SND_POLL         L"SNDPOLL"      // DB_VER Size
#define SND_ORDERS_W     L"SNDWORDERS"   // DB_VER Size
#define SND_ORG_RMNTS_W  L"SNDWORGRMNTS" // DB_VER Size
#define SND_CONFIG_W     L"SNDCONFIG"    // Size
#define SND_DELIVERY_W   L"SNDWDLVR"     // Size
#define SND_PAY_W        L"SNDWPAYMENT"  // Size
#define SND_PHOTO        L"SNDPHOTO"     // Size
#define SND_IMAGE        L"SNDIMAGE"     // Size
#define SND_REST         L"SNDREST"      // DB_VER Size
#define SND_ORD_PCD      L"SNDORDPRCDD"  // DB_VER Size

#ifdef PRICE_MATRIX
#define SND_MATRIX       L"SNDMATRIX"     // DB_VER Size
#endif

#ifdef RCV_MESSAGE
#define SND_MESSAGE      L"SNDMESSAGE"   // DB_VER Size
#endif

#ifdef ORG_TASK
#define SND_ORG_DO       "SNDORG_DO"      // DB_VER Login Pwd Count Size
#define SND_ORG_TASK     L"SNDORG_TASK"   // DB_VER Size
#endif

#if defined(Autopteka) || defined(Autopteka_van) || (defined(COST_MANAGER) && defined(COMPATIBILITY))
#define ACK_COSTS        "ACKCOSTS"       // DB_VER Login Pwd
#define ACK_VAN_PRICE    "ACKVANPRICE"    // DB_VER Login Pwd PriceDate
#define SND_COSTS        L"SNDCOSTS"      // DB_VER Size
#define SND_INCOME       L"SNDINCOME"     // DB_VER Size
#define SND_VAN_ORD      "SNDVANDLV"      // DB_VER Login Pwd Count Size
#define SND_RPLMNT       "SNDRPLMNT"      // DB_VER Login Pwd Count Size
#endif

#endif // COMPATIBILITY

#define ACK_ORD_PCD      L"ACKORDPRCDD" // запрос сервера на КПК

// В отличии от Palm версии ответы приходят в UNICODE
#define FAIL_RESPONSE    L"Ошибка при передаче"
#define ORDER_RESPONSE   L"Заказ отправлен"
#define GOOD_RESPONSE_W  L"OK"
#define BYE_CMD_W        L"BYE"

#define GOOD_RESPONSE    "OK"
#define FAIL_RESPONSE_A  "FAIL"
#define BYE_CMD          "BYE"

//
//----------------------- Config Items -------------------------------
//
#define CONFIG_FILE     "order.cfg"
#define SEP_DATA_SYM    L'&'

#define IP_W            L"IP"
#define COST_TYPE       L"ВидЦены"
#define SUPPL_TYPE      L"Организация"
#define SPEC_TYPE       L"СпецУсловия"
#define PAY_TYPE        L"ФормаОплаты"
#define BANK            L"Банк"
#define ADMPWD          L"ADMPWD"
#define ALLOW_CHG_COST  L"МожноИзменятьЦену"
#define DISCOUNT        L"Скидки"

#define WH_NUMBER       L"Номер склада"

#define OFFTAKE_COEF    L"OffTakeCoef"

#ifdef MULTI_WH
#ifdef Agama
#define WAREHOUSES      L"Склад"
#else
#define WAREHOUSES      L"Склады"
#endif
#endif

#if defined(Autopteka) || defined(Autopteka_van)
#define CAUSE_FAULT     L"ПричиныОтказа"
#define NVS_PREFIX      L"ПрефиксНакладных"
#define NUM_TEMPLATE    L"ШаблонНомера"
#endif

#ifdef Proviant
#define UNIT_LIST      L"ЕдиницыИзменерня"
#endif

#define ORDER_SURVEY    L"ОпросВЗаказе"

//
//------------------------ CONSTANTS ----------------------------------
//

#ifndef BASE_VERSION
#ifndef Suchanov
#define BASE_VERSION 0x00000004
#else
#define BASE_VERSION 0x00000002
#endif
#endif

#ifndef MAX_NUM_COST
#define MAX_NUM_COST 5    
#endif

//
// выгрузка истории продаж из заказов или накладных
//
#ifndef SALES_FROM_ORDERS
#define SALES_FROM_ORDERS true
#endif

//
// показывать клавиатуру в диалоге количества
//
#ifndef SHOW_QTY_KEYBOARD
#define SHOW_QTY_KEYBOARD false
#endif

#define ORG_DB      L"NapoleonOrgs"
#define ORG_OBJ     L"Org"
#define ORG_KEY     L"id"

#define FOLDER_DB   L"NapoleonFolders"
#define FOLDER_OBJ  L"FolderObj"
#define FOLDER_KEY  L"id"

#define PRICE_DB    L"NapoleonPrice"
#define PRICE_OBJ   L"Price"
#define PRICE_KEY   L"id"

#define ORDER_DB    L"NapoleonOrders"
#define ORDER_OBJ   L"Order"
#define ORDER_KEY   L"created"

#define DLV_DB      L"NapoleonDeliveries"
#define DLV_OBJ     L"Delivery"
#define DLV_KEY     L"number"

#define PAY_DB      L"NapoleonPayments"
#define PAY_OBJ     L"Payment"
#define PAY_KEY     L"number"

#define FORG_DB     L"NapoleonFOrgs"
#define FORG_OBJ    L"OrgFolder"
#define FORG_KEY    L"name"

#define RMNTS_DB    L"NapoleonRemnants"
#define RMNTS_OBJ   L"OrgRemnants"
#define RMNTS_KEY   L"id";

#define POLL_DB     L"NapoleonPoll"
#define POLL_OBJ    L"Poll"
#define POLL_KEY    L"id"

#define ORGPOLL_DB  L"NapoleonOrgPoll"
#define ORGPOLL_OBJ L"OrgPoll"
#define ORGPOLL_KEY L"id"
//
//------------------------------- TYPE DEFINITIONS ------------------------------------------
//
#if defined(ORG_UNITS) || defined(ORG_UNITS_STR)
struct OrgUnit : public IReflectableData // торговый точки
{
#ifdef ORG_UNITS
   DWORD     id;
#elif ORG_UNITS_STR
   wchar_t  *id;
#endif

   wchar_t *name;

#ifdef PCMagazine
   WORD type;
#endif

   DECLARE_TYPE_REFLECTION(OrgUnit);
};
#endif

#if defined(Alians)
struct Contact : public IReflectableData
{
   wchar_t *name;
   wchar_t *phone;
   wchar_t *remark;

   DECLARE_TYPE_REFLECTION(Contact)
};
#endif

#if defined(Autopteka) || defined(Autopteka_van)
enum OrgFlags { ofDirty = 1, ofStopList = 2, ofCheckRest = 4 };
#else
enum OrgFlags { ofDirty = 1, ofBlw15 = 1, ofStopList = 2 };
#endif

#ifdef ORG_INFO
struct Contact : public IReflectableData
{
   wchar_t *name;
   wchar_t *phone;

#if defined(Zakroma) || defined(SklRybinsk)
   wchar_t *remark;
#endif

   DECLARE_TYPE_REFLECTION(Contact)
};
#endif

#if defined(Autopteka) || defined(Autopteka_van)
struct IncomeItem : public IReflectableData
{
   wchar_t *id;
   DWORD folderID;
   DWORD qty; // QTY_SCALE
   wchar_t *remark;

   DECLARE_TYPE_REFLECTION(IncomeItem)
};

struct Incomes : public IReflectableData
{
   FILETIME date;
   vector_t<IncomeItem> items;

   DECLARE_TYPE_REFLECTION(Incomes)
};

struct Dogovor : public IReflectableData
{
   wchar_t *number;
   wchar_t *name;

   FILETIME from;
   FILETIME till;

   wchar_t *costType;

   DECLARE_TYPE_REFLECTION(Dogovor)
};

struct OrgDocID : public IReflectableData
{
   wchar_t *id;

   DECLARE_TYPE_REFLECTION(OrgDocID)
};

struct OrgProp : public IReflectableData
{
   wchar_t *name;
   wchar_t *value;

   DECLARE_TYPE_REFLECTION(OrgProp)
};

struct RplItem : public IReflectableData
{
   wchar_t *id;
   DWORD qty; // QTY_SCALE
   DECLARE_TYPE_REFLECTION(RplItem)
};

// пополнение запасов
struct Replenishment : public IReflectableData
{
   vector_t<RplItem> items;
   DECLARE_TYPE_REFLECTION(Replenishment)
};
#endif // Autopteka

#ifdef PRICE_MATRIX
struct MatrixItem : public IReflectableData
{
   wchar_t *id;

   DECLARE_TYPE_REFLECTION(MatrixItem)
};

struct Matrix : public IReflectableData
{
   wchar_t *name;
   vector_t<MatrixItem> items;

   DECLARE_TYPE_REFLECTION(Matrix)
};
#endif // PRICE_MATRIX

#ifdef ORG_TASK
struct Task : public IReflectableData
{
   FILETIME date;

   wchar_t *id;
   wchar_t *task;
   wchar_t *doing;

   DWORD flags;

   DECLARE_TYPE_REFLECTION(Task)
};
#endif // ORG_TASK

#ifdef Spartak
struct Dogovor : public IReflectableData
{
   wchar_t *number;
   wchar_t *name;

   FILETIME from;
   FILETIME till;

   wchar_t *costType;
   wchar_t *firm;

   DECLARE_TYPE_REFLECTION(Dogovor)
};
#endif

#ifdef Voshod
struct Dogovor : public IReflectableData
{
   wchar_t *number;
   wchar_t *name;
   wchar_t *ctype;
   wchar_t *firm;

   DECLARE_TYPE_REFLECTION(Dogovor)
};

struct DiscountPriceItem : public IReflectableData
{
   WORD index;

   DECLARE_TYPE_REFLECTION(DiscountPriceItem)
};

struct DiscountItem : public IReflectableData
{
   short    discount; // SUM_SCALE
   DWORD    qty;
   DWORD    sum;

   vector_t<DiscountPriceItem> items;

   DECLARE_TYPE_REFLECTION(DiscountItem)
};

struct Discount : public IReflectableData
{
   wchar_t *id;
   wchar_t *dogovor;

   vector_t<DiscountItem> items;

   DECLARE_TYPE_REFLECTION(Discount)
};

struct CloseFirmItem : public IReflectableData
{
   wchar_t *firm;

   DECLARE_TYPE_REFLECTION(CloseFirmItem)
};
#endif

#ifdef LiderT
struct Card : public IReflectableData
{
   wchar_t* id;
   wchar_t* name;
   wchar_t* costype;

   DECLARE_TYPE_REFLECTION(Card)
};

struct DiscountItem : public IReflectableData
{
   DWORD id;
   short discount; // DISCOUNT_SCALE

   DECLARE_TYPE_REFLECTION(DiscountItem)
};

struct Discount : public IReflectableData
{
   wchar_t* id;
   vector_t<DiscountItem> items;

   DECLARE_TYPE_REFLECTION(Discount)
};

#endif

#if defined(Tarpan_East) || defined(RosProdukt)
struct Dogovor : public IReflectableData
{
   wchar_t* id;
   wchar_t* name;

   DECLARE_TYPE_REFLECTION(Dogovor)
};
#endif

#ifdef DELIVERY_ADDRESS
struct OrgAddress : public IReflectableData
{
   wchar_t* id;
   wchar_t* name;

   DECLARE_TYPE_REFLECTION(OrgAddress)
};
#endif

#ifdef ERCom
struct OrgDiscount : public IReflectableData
{
   int id;
   int discount; // SUM_SCALE

   DECLARE_TYPE_REFLECTION(OrgDiscount)
};
#endif

#if defined(Orange) || defined(Provisia)
struct Refrigerator : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

   DECLARE_TYPE_REFLECTION(Refrigerator)
};
#endif

#ifdef BastionNeva
struct OrgProps : public IReflectableData
{
   wchar_t *key;
   wchar_t *value;

   DECLARE_TYPE_REFLECTION(OrgProps)
};
#endif
#ifdef Fusion
struct OrgDog : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

   DECLARE_TYPE_REFLECTION(OrgDog)
};
#endif

#if defined(Michailova_O)
struct OrgMatrix : public IReflectableData
{
   wchar_t *name;

   DECLARE_TYPE_REFLECTION(OrgMatrix)
};
#endif

//
//-------------------------------------------------------------------------------- Org
//
struct Org : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

#ifdef ORG_INFO
   wchar_t *address;
   vector_t<Contact> contacts;
#endif

#ifdef Migma
   wchar_t *dcost;
#endif

#ifdef ORG_COST_TYPE
   WORD costype;
#endif

#if defined(STOP_LIST) || defined(Vkk)
   WORD flags;
#endif

#if defined(ORG_UNITS) || defined(ORG_UNITS_STR)
   vector_t<OrgUnit> units;
#endif

#ifdef Alians
   DWORD folderID;
   DWORD sort;
   wchar_t *address;
   wchar_t *workTime;
   wchar_t *dinner;
   wchar_t *remark;
   vector_t<Contact> contacts;
   WORD flags;
#endif

#ifdef Ila
   wchar_t *suplDay;
#endif

#if defined(Troya) || defined (Provisia)
   WORD kind;
#endif

#ifdef RPK
   wchar_t *dogovor;
   WORD     discount;
   WORD     payDelay;
#endif

#if defined(Byloe) || defined(Byloe2)
   wchar_t *phone;
   DWORD flags;
#endif

#ifdef Orange
   // DWORD costType;
   WORD delay;
   vector_t<Refrigerator> refrigerators;
   vector_t<MatrixItem> matrix;
#endif
#ifdef Provisia
   vector_t<Refrigerator> refrigerators;
#endif
#if defined(Autopteka) || defined(Autopteka_van)
   DWORD color;
   DWORD premium; // SUM_QTY
   DWORD plan;
   DWORD fact;
   FILETIME firstDebt;
   vector_t<Dogovor> dogovors;
   vector_t<OrgProp> props;
#ifdef Autopteka_van
   wchar_t *phone;
   wchar_t *inn;
   wchar_t *bank;
   wchar_t *fullName;

   vector_t<OrgDocID> dogOrgID;
#endif
#ifdef Autopteka
   DWORD minPremium; // SUM_SCALE
   DWORD minOrder;   // SUM_SCALE
#endif
#endif

#ifdef PCMagazine
   WORD delay;
   WORD type;
#endif

#ifdef Spartak
   DWORD color;
   vector_t<Dogovor> dogovors;
   vector_t<MatrixItem> matrix;
#endif

#if defined(MediaDistribution) || (defined (VAN_SELLING) && !defined(Autopteka_van))
   wchar_t *phone;
   wchar_t *inn;
   wchar_t *bank;
#endif

#ifdef ORG_COLOR
   DWORD color;
#endif

#ifdef VinStyle
   WORD delay;
#endif

#ifdef Voshod
   vector_t<Dogovor> dogovors;
   wchar_t* ido;
   vector_t<CloseFirmItem> closed;
#endif

#ifdef Fusion
   wchar_t *factAddress;
   wchar_t *fullName;
   wchar_t *baseOrg;
   vector_t<OrgDog> dogovors;
#endif

#ifdef Abdullin
   short discount; // DISCOUNT_SCALE
#endif

#ifdef Enoteka
   DWORD state;
#endif

#ifdef Leopard
   wchar_t* costype;
   WORD payDelay;
   DWORD limit; // SUM_SCALE
   wchar_t* stopMsg;
#endif
#if defined(Suchanov) || defined(TKSibir)
   wchar_t* info;
#endif
#ifdef Suchanov
   wchar_t* type1;
   wchar_t* type2;
#endif

#ifdef Leonov
   FILETIME endDogovor;
   WORD firm;
#endif

#ifdef LiderT
   vector_t<Card> cards;
#endif
#if defined(Tarpan_East) || defined(RosProdukt)
   vector_t<Dogovor> dogovors;
#endif

#if defined(BastionNeva) | defined(Kolbiko) | defined(Tukanov)
   vector_t<MatrixItem> matrix;
#endif

#ifdef Lira
   FILETIME endLicense;
#endif

#ifdef Vkk
   WORD firm;
   WORD delay;
#endif

#ifdef Kolbiko
   WORD coef; // SUM_SCALE
#endif

#ifdef KondMir
   int discount; // SUM_SCALE
   WORD firm;
#endif

#ifdef DELIVERY_ADDRESS
   vector_t<OrgAddress> orgAddress;
#endif

#ifdef ERCom
   vector_t<OrgDiscount> discounts;
#endif

#ifdef KirovOpt
   wchar_t* code;
#endif

#ifdef Fortune
   wchar_t* stopMsg;
   wchar_t* debtMsg;
#endif

#if defined(BastionNeva)
   vector_t<OrgProps> params;
#endif

#if defined(Michailova_O)
   vector_t<OrgMatrix> matrix;
#endif

   DECLARE_TYPE_REFLECTION(Org);
};

#ifdef Autopteka
// точное повторение Org
struct OrgVan : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

#ifdef ORG_INFO
   wchar_t *address;
   vector_t<Contact> contacts;
#endif

#ifdef ORG_COST_TYPE
   WORD costype;
#endif

#ifdef STOP_LIST
   WORD flags;
#endif

#if defined(ORG_UNITS) || defined(ORG_UNITS_STR)
   vector_t<OrgUnit> units;
#endif

#if defined(Autopteka) || defined(Autopteka_van)
   DWORD color;
   DWORD premium; // SUM_QTY
   DWORD plan;
   DWORD fact;
   FILETIME firstDebt;
   vector_t<Dogovor> dogovors;
   vector_t<OrgProp> props;
   wchar_t *phone;
   wchar_t *inn;
   wchar_t *bank;
   wchar_t *fullName;
   vector_t<OrgDocID> dogOrgID;
#endif

   DECLARE_TYPE_REFLECTION(OrgVan);
};
#endif

//
//-------------------------------------------------------------------------------- FolderObj
//
struct FolderObj : public IReflectableData
{
   wchar_t *name;
   DWORD    id;
   //WORD     size;
   WORD     level;

#if defined(Imperia) || defined(Suchanov) || defined(Repnikov) || defined(Kirov_Pavel) || defined(Michailov_V)
   DWORD    firstID; // первый элемент прайс-листа
#endif

#if defined(ORD_SURVAY) || defined(Metelica)
   wchar_t* fid;
#endif

   DECLARE_TYPE_REFLECTION2(FolderObj, L"Folder");
};

#ifdef Alians
struct OrgFolder : public IReflectableData
{
   wchar_t *name;
   DWORD id;
   WORD level;
   DWORD sort;
   DECLARE_TYPE_REFLECTION(OrgFolder)
};
#else
struct OrgFolderItem : public IReflectableData
{
   wchar_t *name;

#if defined(Alians_sp) || defined(SHEDULE) || defined(Kolbiko)
   wchar_t *time;
#endif
   DECLARE_TYPE_REFLECTION(OrgFolderItem);
};

struct OrgFolder : public IReflectableData
{
   wchar_t *name;

   vector_t<OrgFolderItem> items;

   DECLARE_TYPE_REFLECTION(OrgFolder);
};
#endif

#ifdef Alians
struct Packet : public IReflectableData
{
   wchar_t *code;
   DWORD qty; // QTY_SCALE

   DECLARE_TYPE_REFLECTION(Packet)
};
#endif

#if defined (Agama) || defined (Byloe) || defined(Byloe2)
struct PQty : public IReflectableData
{
   int qty;

   DECLARE_TYPE_REFLECTION(PQty)
};
#endif

struct CostItem : public IReflectableData
{
   DWORD cost;

   operator DWORD&() { return cost; }
   operator const DWORD() const { return cost; }

   CostItem() { cost = 0; }
   CostItem(DWORD val) { cost = val; }
   CostItem& operator = (DWORD val) { cost = val; return *this; }

   DECLARE_TYPE_REFLECTION(CostItem)
};

#if defined(MULTI_WH) || defined(FIRMS_REST) || defined(WH_QTY)
struct QtyItem : public IReflectableData
{
   int qty;

   operator int&() { return qty; }
   operator const int() const { return qty; }

   QtyItem() { qty = 0; }
   QtyItem(int val) { qty = val; }
   QtyItem& operator = (int val) { qty = val; return *this; }

   DECLARE_TYPE_REFLECTION(QtyItem)
};
#endif

#if defined(Byloe) || defined(Byloe2)
enum PriceFlags { pfBlw15 = 1, };
#endif

#if defined(Autopteka) || defined(Autopteka_van) || defined(Spartak)
enum PriceFlags { pfNeedRest = 1, } ;
#endif

#ifdef Leopard
struct PackItem : public IReflectableData
{
   enum Flags { Main = 1, };

   wchar_t* pack;
   wchar_t* warehouse;
   DWORD flags; 
   DWORD inPack; // QTY_SCALE
   DWORD qty; // QTY_SCALE

   DECLARE_TYPE_REFLECTION(PackItem)
};
#endif

//
//-------------------------------------------------------------------------------- Price
//
struct Price : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;
   wchar_t *photo;
   DWORD    folderID;

   vector_t<CostItem> cost;

#ifdef MULTI_WH
   vector_t<QtyItem> qty;
#else
   int      qty;         // QTY_SCALE
#endif

#ifdef FIRMS_REST
   vector_t<QtyItem> firmQty;
#endif

   DWORD    qtyInPack;   // QTY_SCALE
   WORD     flags;
   WORD     tax1;

#ifdef Migma
   wchar_t *remark;
#endif

#ifdef Suchanov
   WORD     type;
#else
   DWORD    weight;  // WEIGHT_SCALE
#endif

#ifdef Provisia
   DWORD    minPart; //минимальная отгрузка QTY_SCALE
   WORD    types;
#endif

#ifdef Troya
   WORD    types;
#endif

#ifdef Alians
   vector_t<Packet> packets;
#endif

#ifdef PRICE_COLOR
   DWORD   color;
#endif

#if defined(Byloe) || defined(Byloe2)
   vector_t<PQty> qtys;
#endif

#ifdef Autopteka
   wchar_t *article;
#endif

#ifdef Autopteka_van
   wchar_t *article;

   wchar_t *packName;
   wchar_t *ntd;
   wchar_t *country;
#endif

#ifdef Gudkova
   DWORD itemQty; // QTY_SCALE
#endif

#ifdef KK
   DWORD minPart; // QTY_SCALE
#endif

#ifdef Spartak
   wchar_t *article;
#endif
#if defined(MediaDistribution) || (defined (VAN_SELLING) && !defined(Autopteka_van))
   wchar_t *packName;
   wchar_t *ntd;
   wchar_t *country;
   wchar_t *countryCode;
   wchar_t *unitCode;
#endif
#ifdef Voshod
   wchar_t *unitName;
#endif
#ifdef Leopard
   vector_t<PackItem> packs;
#endif
#ifdef BastionNeva
   int limit; // QTY_SCALE
#endif

#ifdef Vkk
   DWORD mult; // QTY_SCALE
#endif

#ifdef Kolbiko
   DWORD    avgWeight; // WEIGHT_SCALE
   wchar_t* unitName;
#endif

#ifdef WH_QTY
   vector_t<QtyItem> whQty;
#endif

#ifdef Volnenko
   DWORD minNac; // SUM_SCALE
#endif
   DECLARE_TYPE_REFLECTION(Price);
};

#ifdef Autopteka
struct PriceVan : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;
   wchar_t *photo;
   DWORD    folderID;

   vector_t<CostItem> cost;

   int      qty;         // QTY_SCALE
   DWORD    qtyInPack;   // QTY_SCALE
   WORD     flags;
   WORD     tax1;

   DWORD    weight;  // WEIGHT_SCALE

#ifdef PRICE_COLOR
   DWORD   color;
#endif

#ifdef Autopteka
   wchar_t *article;

   wchar_t *packName;
   wchar_t *ntd;
   wchar_t *country;
#endif

   DECLARE_TYPE_REFLECTION(PriceVan);
};
#endif

struct PriceRemnants : public IReflectableData
{
   wchar_t *id;

#ifdef MULTI_WH
   vector_t<QtyItem> qty;
#else
   int      qty;
#endif

#ifdef FIRMS_REST
   vector_t<QtyItem> firmQty;
#endif

#ifdef Agama
   vector_t<PQty> qtys;
#endif

   DECLARE_TYPE_REFLECTION(PriceRemnants);
};

#ifdef Alians
struct PacketsChain : public IReflectableData
{
   DWORD qty; // QTY_SCALE
   vector_t<Packet> chain;

   DECLARE_TYPE_REFLECTION(PacketsChain)
};
#endif

//
//-------------------------------------------------------------------------------- ItemSales
//
struct ItemSales
{
   FILETIME date;
   DWORD    qty;

#ifdef SHOW_OFF_TAKE
   DWORD    rest;    // витрина
   DWORD    offTake; // Off Take
   DWORD    ret;     // возврат
#endif
};
   
struct ItemSaleDateCompare
{
   bool operator() (const ItemSales& _Left, const ItemSales& _Right) const
   {
      return (CompareFileTime(&_Left.date, &_Right.date) > 0);
   }
};

//
//-------------------------------------------------------------------------------- OrderSendItem
//
#ifdef ORD_SURVAY
struct Survay : public IReflectableData
{
   DWORD folder;
#ifdef COMPATIBILITY
#else
   wchar_t* fid;
#endif
   wchar_t* choice;

   DECLARE_TYPE_REFLECTION(Survay)
};
#endif

enum OrderItemFlags
{
   oiInPack = 1, // показывать упаковками
#if defined(Autopteka) || defined(Autopteka_van)
   oiCustomCost   = 2,
#endif
   oiNoCheckWHQty = 4, // не проверять остатки на складе

   oiHideRemnants = 8, // скрывать окно ввода остатков

   oiDirtyItem    = 0x10, // новый или измененный элемент (для ORDER_ONLINE)
};

struct OrderItem : public IReflectableData
{
   wchar_t *id;
   DWORD   qty;    //QTY_SCALE
   DWORD   cost;   //SUM_SCALE   // всегда показывает действительное количество
   WORD    flags; 

#ifdef Alians
   vector_t<PacketsChain> packets; // if (packets.size() > 1) => qty = sum(packets[i].qty)
#endif

#ifdef Proviant
   wchar_t *unit;
#endif

#ifdef ORD_ITEM_DISCOUNT
   int discount; // DISCOUNT_SCALE
#endif
#ifdef Leopard
   wchar_t *pack;
#endif
#ifdef SHOW_OFF_TAKE
#ifdef Provisia
#else
   int offTakeDiff; // QTY_SCALE 
#endif
#endif
#ifdef Volnenko
   DWORD costType;
#endif

//#ifdef ERCom
//   int discount;
//#endif
   DECLARE_TYPE_REFLECTION(OrderItem);
};

#if defined(Voshod)
#define __OUT_OF_PLAN_FLAG 0x00004
#else
#define __OUT_OF_PLAN_FLAG 0x40000
#endif

enum OrderFlags
{
   ofExported  = 0x00001,
   ofCash      = 0x00002,
   ofProceeded = 0x20000,
   ofOutPlan   = __OUT_OF_PLAN_FLAG,
};

#ifdef Suchanov
enum OrderParamFlags
{
   ofTopicB   = 0x0001,
   ofDiscount = 0x0002,
   ofTax      = 0x0004,
};
#endif
#ifdef Provisia
enum OrderParamFlags
{
   ofNetCost = 0x0002,
};
#endif
#ifdef Alians
enum OrderParamFlags
{
   ofEnterRemnants = 0x0002,
   ofPayGuaranty   = 0x0004,
   ofBankPay       = 0x0008,
};
#endif
#ifdef Migma
enum OrderParamFlags
{
   ofBankPay = 0x0002,
};
#endif
#if defined(Zakroma) || defined(SklRybinsk)
enum OrderParamFlags
{
   ofUnused    = 0x0002, // ИП Балякин - не
   ofSert      = 0x0004,
   ofQuality   = 0x0008,
   ofDate      = 0x0010,
   ofPayBefore = 0x0020, // без оплаты не отгружать
   ofFact      = 0x0040, //отсрочка - нал/факт
};
#endif
#ifdef Orange
enum OrderParamFlags
{
   ofCache = 0x0002,
   ofWaybill = 0x0004,
};
#endif
#ifdef Voshod
enum OrderParamFlags
{
   ofDelivery   = 0x0008,
};
#endif
#ifdef RPK
enum OrderParamFlags
{
   ofQuality = 0x0004,
   ofSert    = 0x0008,
};
#endif


#if defined(Autopteka) || defined(Autopteka_van)
enum DocComplect
{
   dcFull = 0,    // полный
   dcCheck,       // полный + чек
   dcMinimal,     // минимальный
};

enum PayType
{
   ptDelivery = 0,   // по накладной
   ptAccount,        // безнал
   ptPaySum,         // оплата суммы (paySum);
   ptNone,           // без оплаты
};

enum OrderParamFlags
{
   ofDocsMask = 0x000C,  // комплет документов DocComplect
   ofPayMask  = 0x0030,  // маска способа оплаты PayType
   ofInvoice  = 0x0040,  // выставлять счет
   ofReestr   = 0x0080,  // реестр
   ofSert     = 0x0100,  // сертификаты
   ofOther    = 0x0200,  // другое
};
#endif

#ifdef Polus
struct PdaTime : public IReflectableData
{
   FILETIME time;
   DECLARE_TYPE_REFLECTION(PdaTime)
};
#endif
//
//-------------------------------------------------------------------------------- Order
//
struct Order : public IReflectableData
{
   FILETIME created;
   FILETIME date;
   wchar_t  *id;
   DWORD    params;
   WORD     supplyer;
   WORD     sumType;
   wchar_t  *remark;
   vector_t<OrderItem> items;

   short timeZone;

#ifdef GPS_POS
   int   latitude;
   int   longitude;
#endif

#ifdef PAY_DELAY
   WORD     delay;
#endif

#ifdef ORG_UNITS
   DWORD unitCode;
#elif ORG_UNITS_STR
   wchar_t *unitCode;
#endif

#if defined(ORDER_DISCOUNT) || defined (Provisia)
   short discount; // DISCOUNT_SCALE
   WORD  sendBefore;
#endif

#ifdef ORD_DLV_BIND
   wchar_t  *number;    // номер заказа из учетной системы
#endif

#ifdef POD_COMMENT
   wchar_t *podRemark;
#endif

#ifdef Suchanov
   DWORD    flags;
   WORD     bank;
   FILETIME pay;
   WORD     specCondition;
   short    discount;
#endif

#ifdef Agama
   FILETIME supplDate;
   //DWORD    unitCode;
#endif

#ifdef Alians
   DWORD  orderNumber;
#endif

#if defined(Alians_sp) || defined(SHEDULE)
   FILETIME shedule;
#endif

#if defined(Byloe) || defined(Byloe2)
   wchar_t *whCode;
   wchar_t *prcCode;
#endif

//#ifdef Orange
//   DWORD    unitCode;
//#endif

#if defined(Autopteka) || defined(Autopteka_van)
   FILETIME supplDate;
   DWORD paySum;
   wchar_t *dogNum;
   wchar_t *sumTypeID;
#endif

#ifdef ORD_SURVAY
   vector_t<Survay> survay;
#endif

#if defined(Zakroma) || defined(SklRybinsk)
   DWORD    collectSum; // инкассация
   wchar_t *collectNum;

   wchar_t *logistic;
   wchar_t *fcontrol;
#endif

#ifdef Polus
   FILETIME curPdaTime;
   vector_t<PdaTime> pdaTimeChanged;
#endif

#if defined(Autopteka_van)
   wchar_t *docNum;
   WORD account;
#endif

#ifdef Spartak
   wchar_t *supplCode;
   wchar_t *costCode;
   wchar_t *dogovor;
#endif

#ifdef MULTI_WH
#ifdef Agama
   int whIndex;
#else
   wchar_t* warehouseCode;
#endif
#endif

#ifdef Judaev
   FILETIME invoiceClose;
#endif

#if defined(MediaDistribution) 
   wchar_t *docNum;
   WORD account;
#endif
#if (defined (VAN_SELLING) && !defined(Autopteka_van))
   wchar_t *docNum;
   wchar_t *supplCode;
#endif
#ifdef Voshod
   wchar_t *dogovor;
   wchar_t *suplCode;
   wchar_t *retNum;
   wchar_t *dlvCode;
#endif
#ifdef PROVISIA_ADD
   wchar_t* supplCode;
#endif

#ifdef Sega
   wchar_t* supplCode;
   wchar_t* costType;
#endif
#ifdef Leopard
   wchar_t* supplCode;
   wchar_t* costType;
   wchar_t* ordType;
   wchar_t* whCode;
#endif
#ifdef LiderT
   wchar_t* card;
#endif
#if defined(Tarpan_East) || defined(RosProdukt)
   wchar_t* dogovor;
#endif
#ifdef Kolbiko
   wchar_t* payType;
   FILETIME  shedule;
   FILETIME  ordDate;
#endif
#ifdef Migma
   wchar_t* costtype;
#endif

#ifdef DELIVERY_ADDRESS
   wchar_t* adrCode;
#endif

#ifdef KirovOpt
   wchar_t *unit;
#endif

#ifdef WH_QTY
  int whIndex;
#endif

#ifdef Fusion
   wchar_t* dogovor;
#endif

   bool IsExported() const { return ((params & ofExported) != 0); }
   //bool IsProceeded() const { return ((params & ofProceeded) != 0); }

   DECLARE_TYPE_REFLECTION(Order);
};

#ifdef Autopteka
struct OrdVan : public Order
{
   wchar_t *docNum;
   WORD account;

   DECLARE_TYPE_REFLECTION(OrdVan)
};
#endif

//
//-------------------------------------------------------------------------------- OrderProceeded
//
struct OrderProceeded : public IReflectableData
{
   FILETIME created;

#if defined(POD_COMMENT)
   wchar_t *remark;
#endif

   wchar_t *type;

   DECLARE_TYPE_REFLECTION(OrderProceeded)
};

struct Config : public IReflectableData
{
   wchar_t *key;
   wchar_t *value;

   DECLARE_TYPE_REFLECTION(Config);
};

struct DeliveryItem : public IReflectableData
{
   wchar_t *id;
   DWORD    qty;  // QTY_SCALE
   DWORD    sum;  // SUM_SCALE

   DECLARE_TYPE_REFLECTION(DeliveryItem)
};

#ifdef Alians
struct DeliveryPay : public IReflectableData
{
   wchar_t *number;
   FILETIME date;
   DWORD sum; // SUM_SCALE

   DECLARE_TYPE_REFLECTION(DeliveryPay)
};
#endif

//
//-------------------------------------------------------------------------------- Delivery
//
struct Delivery : public IReflectableData
{
   FILETIME date;
   wchar_t *id;
   wchar_t *number;

   vector_t<DeliveryItem> items;

#ifdef ORD_DLV_BIND
   FILETIME created;
#endif

#ifdef Alians
   DWORD costType;
   vector_t<DeliveryPay> pays;
#endif

#ifdef MAKE_BALANCE
#else
   DWORD sumD;
#endif
#if defined(Zakroma) || defined(SklRybinsk)
   DWORD sumT;
#endif

#if defined(Gudkova) || defined(Michailova_O)
   FILETIME payDate;
#endif
#if defined(Autopteka) || defined(Autopteka_van)
   FILETIME payDate;
#endif
#ifdef Suchanov
   wchar_t *agent;
   FILETIME payDate;
   vector_t<Config> values;
#endif

#ifdef Spartak
   FILETIME payDate;
#endif

#ifdef Voshod
   wchar_t* supplyer;
   wchar_t* dogId;
   WORD     fiscal;
   wchar_t* type;
#endif

#ifdef HappyLand
   FILETIME payDate;
#endif

#ifdef BastionNeva
   FILETIME payDate;
#endif
   DECLARE_TYPE_REFLECTION(Delivery)
};

//
//-------------------------------------------------------------------------------- Payment
//
struct Payment : public IReflectableData
{
   FILETIME date;
   wchar_t *id;
   wchar_t *number;

   DWORD    sum;
#ifdef Agama
   DWORD    sum2;
#endif
#ifdef RPK
   DWORD    color;
   DWORD    outSum;
#endif
#ifdef Repnikov
   FILETIME dlvDate;
   DWORD    dlvSum;
   WORD     payDelay;
#endif
#ifdef Provisia
   FILETIME dlvDate;
   WORD     delay;
   wchar_t *type;
   DWORD    color;
#endif
#ifdef VAN_SELLING
#ifdef GPS_POS
   int   latitude;
   int   longitude;
#endif
   FILETIME created;
   DWORD    params;
   wchar_t* remark;
   wchar_t* supplyer;
   DWORD    sumTax;
#endif

#ifdef Voshod
   wchar_t* supplyer;
   wchar_t* dogId;
   WORD     fiscal;
#endif
#if defined(Byloe) || defined(Byloe2)
   DWORD dlvSum;
   short  payDelay;
   short overDelay;
   wchar_t* manager;
#endif
#ifdef Vkk
   wchar_t* agent;
   FILETIME payDate;
#endif
   DECLARE_TYPE_REFLECTION(Payment)
};

enum OrgRemnantItem { oriSKU = 1 };

struct OrgRemnantsItem : public IReflectableData
{
   wchar_t *id;
   DWORD    qty; // QTY_SCALE

#ifdef ORG_REMNANTS
   DWORD flags;
#endif

   DECLARE_TYPE_REFLECTION(OrgRemnantsItem)
};

enum OrgRemnantsFlags { orfDirty = 0x1, orfCall = 0x2, orfMeet = 0x4, orfOther =0x8 };

struct OrgRemnants : public IReflectableData
{
   wchar_t *id;
   FILETIME date;
   FILETIME created;
   WORD flags;
   vector_t<OrgRemnantsItem> items;

#ifdef GPS_POS
   int   latitude;
   int   longitude;
#endif

   DECLARE_TYPE_REFLECTION(OrgRemnants)
};

struct OrgPollItem : public IReflectableData
{
   unsigned short id;
   unsigned short value;

   DECLARE_TYPE_REFLECTION(OrgPollItem)
};

struct OrgPoll : public IReflectableData
{
   wchar_t *id;
   wchar_t *pollID;
   FILETIME date;

   vector_t<OrgPollItem> items;

   DECLARE_TYPE_REFLECTION(OrgPoll)
};

struct PollItem : public IReflectableData
{
   unsigned short id;
   wchar_t *value;

   DECLARE_TYPE_REFLECTION(PollItem)
};

struct Poll : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

   vector_t<PollItem> items;

   DECLARE_TYPE_REFLECTION(Poll)
};

struct PricePhoto : public IReflectableData
{
   wchar_t *id;
   wchar_t *photo;

   DECLARE_TYPE_REFLECTION(PricePhoto)
};

#ifdef Alians
struct OrgInfo : public IReflectableData
{
   wchar_t *id;
   wchar_t *address;
   wchar_t *workTime;
   wchar_t *dinner;
   wchar_t *remark;

   vector_t<Contact> contacts;

   DECLARE_TYPE_REFLECTION(OrgInfo)
};
#endif // Alians

#ifdef ORG_SKU
struct RestItem : public IReflectableData
{
   wchar_t *id;

   DECLARE_TYPE_REFLECTION(RestItem)
};

struct OrgRest : public IReflectableData
{
   FILETIME date;
   wchar_t  *id;
   vector_t<RestItem> items;
   WORD state;

   DECLARE_TYPE_REFLECTION(OrgRest)
};
#endif // ORG_SKU

#ifdef PROXY_DOC
struct Proxy : public IReflectableData
{
   wchar_t *id;

   FILETIME date;
   DWORD sum; // SUM_SCALE

   wchar_t *remark;
   DWORD flags;

   DECLARE_TYPE_REFLECTION(Proxy)
};
#endif // PROXY_DOC

#ifdef RCV_MESSAGE
struct Message : public IReflectableData
{
   FILETIME date;
   wchar_t *message;

   DECLARE_TYPE_REFLECTION(Message)
};
#endif

#ifdef GPS_POS

#ifndef GPS_SCALE_DEFINED
#define GPS_SCALE_DEFINED

const DWORD GPS_SCALE = 100000;
const DWORD GPS_SPEED_SCALE = 100;

#endif

//
// структуры GEOPos & GSMCell должны быть одинаковые
//

struct GPSPos : public IReflectableData
{
   WORD isGSM; // 1 - GMSCell 0 - GESPos
   FILETIME date;

   int longitude;
   int latitude;
   DWORD speed;

   DECLARE_TYPE_REFLECTION(GPSPos)
};
#endif // GPS_POS

#ifdef RPK
struct PlanItem : public IReflectableData
{
   wchar_t *id;
   DWORD value; // без масштабирования

   DECLARE_TYPE_REFLECTION(PlanItem)
};

struct PlanTable : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

   DECLARE_TYPE_REFLECTION(PlanTable)
};

struct Plan : public IReflectableData
{
   wchar_t *id;
   FILETIME date;

   vector_t<PlanItem> items;

   DWORD flags;
   DECLARE_TYPE_REFLECTION(Plan)
};
#endif

#ifdef ORG_NOTE
struct OrgNote : public IReflectableData
{
   wchar_t *id;
   wchar_t *note;

   DECLARE_TYPE_REFLECTION(OrgNote)
};
#endif

#ifdef FIRMS_TABLE
struct Firm : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

   wchar_t *address;

   wchar_t *phone;
   wchar_t *inn;
   wchar_t *bank;

#ifdef Fusion
   wchar_t *factAddress;
   wchar_t *buh;
   wchar_t *chief;
   wchar_t* fullName;
#endif

   DECLARE_TYPE_REFLECTION(Firm);
};
#endif

#ifdef AGENT_TASK

struct AgentTask : public IReflectableData
{
   enum Flags { Done = 1, Exported = 2, SuperTask = 4 };

   FILETIME date;          // дата создания (или связи со сценарием)
   FILETIME execDate;      // дата выполнения задачи
   FILETIME appointDate;   // дата назначения

   wchar_t *id;
   wchar_t *category;
   wchar_t *text;

   DWORD flags;

   DECLARE_TYPE_REFLECTION(AgentTask);
};

struct SVTask : public AgentTask
{
   DECLARE_TYPE_REFLECTION(SVTask);
};

struct TaskCategory : public IReflectableData
{
   wchar_t *name;
   DECLARE_TYPE_REFLECTION(TaskCategory);
};

#endif

#endif

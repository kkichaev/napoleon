/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Файл для управления совместимостью версий
 *
 * ert   15/07/2010   creating
 */ 


#define COMPATIBILITY 1

//#define Alcom
//#define GPS_POS
//#define ORG_INFO
//#define VISIT_DOC
//#define PAY_DELAY
//#define POD_COMMENT
//#define ORD_DLV_BIND
//#define PROXY_DOC
//#define RCV_MESSAGE
//#define ORG_REMNANTS
//#define ORD_ITEM_DISCOUNT

#include <Exchange.h>
#include <Reflector.h>

struct RcvInfo
{
   const wchar_t *name;
   bool compressed;
};

RcvInfo rcvOrder = { L"Order", true };
RcvInfo rcvProxy = { L"Proxy", true };
RcvInfo rcvRest = { L"OrgRest:OrgRemnants", true };

const wchar_t CONFIG_STR[] = L"Config";

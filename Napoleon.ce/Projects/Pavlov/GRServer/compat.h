/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Файл для управления совместимостью версий
 *
 * ert   15/07/2010   creating
 */ 

#define COMPATIBILITY 1

//#define Provisia
//#define ORG_REMNANTS
//#define PROXY_DOC
//#define ORG_SKU

#undef VISIT_DOC
#undef ORG_NOTE
#undef POD_COMMENT
#undef RCV_MESSAGE
#undef ORD_DLV_BIND
#undef PRICE_MATRIX
#undef PRICE_COLOR
#define USE_FIRST_ID
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

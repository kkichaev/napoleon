/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Файл для управления совместимостью версий
 *
 * ert   15/07/2010   creating
 */ 

#define COMPATIBILITY 1

#undef GPS_POS
#undef ORG_INFO
#undef VISIT_DOC
#undef POD_COMMENT
#undef RCV_MESSAGE
#undef ORG_REMNANTS
#undef ORD_DLV_BIND
#define USE_FIRST_ID
// warehouseCode == whCode
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

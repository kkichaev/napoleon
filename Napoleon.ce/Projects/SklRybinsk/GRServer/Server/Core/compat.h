/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Файл для управления совместимостью версий
 *
 * ert   15/07/2010   creating
 */ 

#define COMPATIBILITY 1

#undef GPS_POS
#undef ORG_COLOR
#undef ORG_REMNANTS
#undef ORG_SKU
#undef RCV_MESSAGE
#undef PRICE_MATRIX 
#undef PRICE_COLOR
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

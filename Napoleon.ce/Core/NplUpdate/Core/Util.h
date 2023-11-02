/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* Util functions
*
*  ert   09/11/2009   creating
*/

#ifndef __NPL_UTIL_H
#define __NPL_UTIL_H

#include <string>
#include <NetExchange.h>

class ProgConfig;

bool ReadToken(std::wstring* value, const wchar_t *sp, const wchar_t **ep);

ReceivedStream* Receive(const wchar_t* command, const wchar_t* paramValue, const ProgConfig& config);

#endif //__NPL_UTIL_H

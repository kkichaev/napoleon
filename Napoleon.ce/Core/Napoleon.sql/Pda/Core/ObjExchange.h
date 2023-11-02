/*
 * Copyright (C), 2007-2011, Денис Мосягин
 *
 * Обмен объектами
 *
 *  ert   13/07/2011   creating
 */
#ifndef __OBJ_EXCHNAGE_H
#define __OBJ_EXCHNAGE_H


#include "ServObject.h"

const int RESULT_FAIL   = 0; // документ не сохранен
const int RESULT_SAVE   = 1; // документ сохранен
const int RESULT_COMMIT = 2; // документ проведен. Изменения не возможны

struct SendObjParam
{
   IServObject* object;
   const wchar_t *command;
   IProgressIndicator *pi;

   std::wstring answer;
   int ec;

   SendObjParam() : ec(0) {}
};

DWORD ObjExchange(SendObjParam* param);


#endif
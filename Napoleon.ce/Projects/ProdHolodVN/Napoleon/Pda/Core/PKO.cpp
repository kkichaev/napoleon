/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Реализация функций оплаты при продаже
 *
 *  ert   27/07/20107   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "ObjImpl.h"
#include "DocImpl.h"
#include "FormEntries.h"
#include <StdFuncs.h>
#include <NapoleonRes.h>
#include "Progress.h"
#include <BaseDialog.h>
#include "InitDoc.h"
#include "NumInput.h"

#include <ListForm.h>
#include <EnterNumber.h>

#ifdef GPS_POS
#include <OrgDocs.h>
#include <FormEntries.h>
#endif

#include "Add.h"

BEGIN_TYPE_REFLECTION(PKO)
   REGISTER_FILETIME_MEMBER(PKO, date)
   REGISTER_STRING_MEMBER(PKO, id)
   REGISTER_STRING_MEMBER(PKO, number)
   REGISTER_ULONG_SCALE_MEMBER(PKO, sum, SUM_SCALE)
   REGISTER_STRING_MEMBER(PKO, supplyer)
   REGISTER_STRING_MEMBER(PKO, dogId)
   REGISTER_USHORT_MEMBER(PKO, fiscal)
#ifdef GPS_POS
   REGISTER_LONG_SCALE_MEMBER(PKO, latitude, GPS_SCALE)
   REGISTER_LONG_SCALE_MEMBER(PKO, longitude, GPS_SCALE)
#endif
   REGISTER_TIMESTAMP_MEMBER(PKO, created)
   REGISTER_ULONG_MEMBER(PKO, params)
END_TYPE_REFLECTION(PKO)


IDocument* PKOImpl::Copy()
{
   return NULL;
}

bool PKOImpl::CreateDocument(const ROWID &orgID)
{
   return false;
}

bool PKOImpl::Init(const ROWID &orgID)
{
#ifdef GPS_POS
   if( !CheckGPSPos(L"Получить координаты?") )
      return false;

   latitude = gCurrentGPSPos.latitude;
   longitude = gCurrentGPSPos.longitude;
#endif

   OrgImpl org;
   org.Read(orgID);
   id = holder.Add(org.id);
   rid = NO_ROWID;

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &created);
   date = created;

   params = 0;
   sum = 0;

   fiscal = 0;
   number = L"";
   supplyer = L"";
   dogId = L"";

   return true;
}

bool PKOImpl::CanRemove() const
{
   return false;
}

void PKOImpl::EditDocument(UINT retForm)
{
}

bool PKOImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   if( reverse )
   {
      if( params & Exported ) params &= (~Exported);
      else params |= Exported;
   } else
      params |= Exported;

   return (updateTable == NULL) ? true : updateTable->Update(*this, L"params", rid);
}


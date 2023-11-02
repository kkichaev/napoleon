/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * Документ задачи по контрагенту
 *
 *  ert   21/06/2010   creating
 */ 
#include "stdafx.h"
#include "OrgTask.h"
#include <ObjImpl.h>

BEGIN_TYPE_REFLECTION(OrgPlan)
   REGISTER_TIMESTAMP_MEMBER(OrgPlan, date)
   REGISTER_STRING_MEMBER(OrgPlan, id)
   REGISTER_STRING_MEMBER(OrgPlan, task)
   REGISTER_STRING_MEMBER(OrgPlan, remark)
   REGISTER_ULONG_MEMBER(OrgPlan, params)
END_TYPE_REFLECTION(OrgPlan)

bool OrgPlanImpl::CreateDocument(const ROWID &orgID)
{
   if( !Init(orgID) ) return false;

   OpenPlan(this, 0);
   return true;
}

bool OrgPlanImpl::Init(const ROWID &orgID)
{
   OrgImpl org;
   SYSTEMTIME st;
   GetLocalTime(&st);

   org.Read(orgID);
   id = holder.Add(org.id);

   SystemTimeToFileTime(&st, &date);

   params = 0;
   task = L"";
   remark = L"";
   return true;
}

bool OrgPlanImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   if( reverse )
   {
      if( params & ofExported ) params &= (~ofExported);
      else params |= ofExported;
   } else
      params |= ofExported;
   return (updateTable == NULL) ? true : updateTable->Update(*this, L"params", rid);
}

void OrgPlanImpl::OpenPlan(OrgPlanImpl* plan, UINT retForm)
{
}

void OrgPlanImpl::OpenPlanList(const wchar_t* id, bool onlyNotDone)
{
}

/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Список организаций
 *
 *  ert   13/08/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "OrgList.h"
#include "FldOrgs.h"
#include <DocType.h>

void OpenOrgList()
{
   OpenOrgList(dtOrder);
}

void OpenOrgFolders(const wchar_t *type)
{
#ifdef SHEDULE
   SheduleData::ClearShedule();

   OrgFoldersData *od = new SheduleData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_SHEDULE, od);
#else
   OrgFoldersData *od = new OrgFoldersData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_FLDORGS, od);
#endif
}

void OpenOrgList(const wchar_t* type)
{
   OrgFolderImpl of;
   SQLTable table(of.Name());
#ifdef SHEDULE
   SheduleData::ClearShedule();
#endif

   if( SQLTable::IsTableExist(of.Name()) && table.Count() != 0 )
   {
//#ifdef SHEDULE
      Preference prf;
      prf.Load();
      if( prf.flags & opfCalendarView )
      {
         OpenOrgFolders(type);
         return;
      }
//#else
//      OpenOrgFolders(type);
//      return;
//#endif
   }

   OrgListData *od = new OrgListData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_ORG_LIST, od);
}

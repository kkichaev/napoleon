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
#include <algorithm>
#include <StdFuncs.h>
#include "Add.h"

FILETIME sheduleDate;

void OpenOrgList()
{
   OpenOrgList(dtOrder);
}

class OrgFoldersDataAdd : public OrgFoldersData
{
public:
   virtual OrgFoldersData* Clone() const
   {
      OrgFoldersData *od = new OrgFoldersDataAdd();
      od->SetDocType(GetDocType()->Type());
      return od;
   }

   virtual void LoadFolderData(const TreeNode& folder);
   virtual bool Get(IReflectableData* data, int index) const;
   virtual bool SelectLeaf(int index)
   {
      ROWID id = GetOID(index);
      std::map<ROWID, FILETIME>::const_iterator f1 = shedule.find(id);
      if( f1 != shedule.end() )
         sheduleDate = f1->second;

      return OrgFoldersData::SelectLeaf(index);
   }

   bool CanSetColumn(int rowIndex, int colIndex) const
   {
      if( colIndex == 0 )
         return true;

      return (!IsTopLevel() || inSearch);
   }

   std::map<ROWID, FILETIME> shedule;
   mutable std::wstring name;
};

class OrgFoldersAdd : public OrgFolders
{
public:
   OrgFoldersAdd() {}

   DECLARE_FORM(OrgFoldersAdd, IDD_FLDORGS_ADD)

   virtual DWORD GetResourceID() const { return IDD_ORG_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_FLDORGS; }

   virtual bool CanSetColumn(int rowIndex, int colIndex) const
   {
      return ((OrgFoldersDataAdd*)data)->CanSetColumn(rowIndex, colIndex);
   }
};

IMPLEMENT_FORM(OrgFoldersAdd)


static std::map<ROWID, FILETIME> *__shed;
bool CMPData( const ROWID& elem1, const ROWID& elem2 )
{
   std::map<ROWID, FILETIME>::const_iterator 
      f1 = __shed->find(elem1), 
      f2 = __shed->find(elem2);

   if( f1 == __shed->end() ) return true;
   if( f2 == __shed->end() ) return false;

   return (CompareFileTime(&f1->second, &f2->second) < 0);
}

const wchar_t* GetFolderName(const wchar_t* name);
int DayToInt(const wchar_t *day);
void OrgFoldersDataAdd::LoadFolderData(const TreeNode& folder)
{
   shedule.clear();

   if( folder.id == NO_ROWID )
   {
      title = L"Организации";
      return;
   }

   orgFolder.Read(folder.id, false, false);
   title = GetFolderName(orgFolder.name);

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wSecond = 0;
   __int64 dayDiff = (__int64)(((DayToInt(orgFolder.name) + 1) % 7) - st.wDayOfWeek) * 24 * 3600 * 10000000;

   vector_t<OrgFolderItem>::const_iterator i = orgFolder.items.begin();
   for( ; i != orgFolder.items.end(); i++ )
   {
      org.id = i->name;
      org.Read(false);
      
      st.wHour = 0;
      st.wMinute = 0;

      swscanf(i->time, L"%d:%d", &st.wHour, &st.wMinute);
      
      FILETIME ft;
      SystemTimeToFileTime(&st, &ft);
      *(__int64*)&ft += dayDiff;
      shedule[org.RID()] = ft;
      leafs.push_back(org.RID());
   }

   __shed = &shedule; 
   sort(leafs.begin(), leafs.end(), CMPData);
   lastFolder = UpFolder();
}

bool OrgFoldersDataAdd::Get(IReflectableData* data, int index) const
{
   ((OrgDataItem*)data)->sum = 0;

   if( IsTopLevel() && !inSearch )
   {
      if( folders.size() <= (unsigned)index ) return false;
      if( !orgFolder.Read(folders[index], true, false) ) return false;

      ((OrgDataItem*)data)->sum = CountFolderSum(orgFolder);
      ((OrgDataItem*)data)->name = GetFolderName(orgFolder.name);
   } else
   {
      if( (unsigned)index >= leafs.size() ) return false;
      
      std::map<ROWID, FILETIME>::const_iterator f1 = shedule.find(leafs[index]);
      if( org.Read(leafs[index], true, false) == false || f1 == shedule.end() ) return false;

      wchar_t buf[20];
      SYSTEMTIME st;
      FileTimeToSystemTime(&f1->second, &st);
      wsprintf(buf, L"%02d:%02d ", st.wHour, st.wMinute);
      name = buf;
      name += org.name;
      ((OrgDataItem*)data)->sum = sums.GetSum(docType->Type(), org.id);
      ((OrgDataItem*)data)->name = name.c_str();
   }
   return true;
}


void OpenOrgFolders(const wchar_t *type)
{
#ifdef SHEDULE
   SheduleData::ClearShedule();

   OrgFoldersData *od = new SheduleData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_SHEDULE, od);
#else
   OrgFoldersData *od = new OrgFoldersDataAdd();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_FLDORGS_ADD, od);
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

   SYSTEMTIME st;
   GetLocalTime(&st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &sheduleDate);

   OrgListData *od = new OrgListData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_ORG_LIST, od);
}

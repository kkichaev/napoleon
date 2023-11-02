/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Папки организаций (маршруты)
 *
 *  ert   30/10/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "FldOrgs.h"
#include "FormEntries.h"
#include <DocImpl.h>
#include <algorithm>

const wchar_t* SHEDULE_START_KEY = L"SheduleStart";

IMPLEMENT_FORM(OrgFolders);
int OrgFolders::lastViewed;
ROWID OrgFoldersData::lastFolder = NO_ROWID;

BEGIN_TYPE_REFLECTION(OrgDataItem)
   REGISTER_STRING_MEMBER(OrgDataItem, name)
   REGISTER_LONG_SCALE_MEMBER(OrgDataItem, sum, SUM_SCALE)
END_TYPE_REFLECTION(OrgDataItem)

#ifdef SHEDULE
#include "OrgList.h"

IMPLEMENT_FORM(SheduleForm);

DWORD SheduleData::viewedDate = 0;

SYSTEMTIME SheduleData::orgShedule;
#endif

const wchar_t* GetFolderName(const wchar_t* name)
{
   return (iswdigit(*name)) ? name + 1 : name;
}

//
//------------------- OrgFoldersData ----------------------
//
static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
   //{ ListFormData::Header::Left, L"Кол-во", L"qty", 50 },
   { ListFormData::Header::Right, L"Сумма", L"sum", 50 }
};

OrgFoldersData::OrgFoldersData() : inSearch(false)
{
   searchHelper.SetData(org.Name(), L"name");
}

OrgFoldersData::~OrgFoldersData()
{
   DestroyData();
}

void OrgFoldersData::RefreshCurrent()
{
   if( !inSearch )
      SelectFolder((current==NULL) ? root.id : current->id, true);
}

OrgFoldersData* OrgFoldersData::Clone() const
{
   OrgFoldersData *od = new OrgFoldersData();
   od->SetDocType(GetDocType()->Type());

   return od;
}

void OrgFoldersData::ClearSearch()
{
   searchHelper.Clear();
   leafs.clear();

   inSearch = false;

   SetCurrent(current);
}

bool OrgFoldersData::FindItems(const wchar_t *text)
{
   inSearch = true;
   leafs.clear();
   folders.clear();

   searchHelper.Search(&leafs, text);
   return true;
}

int DayToInt(const wchar_t *day)
{
   if( !_wcsicmp(day, L"понедельник") ) return 0;
   if( !_wcsicmp(day, L"вторник") ) return 1;
   if( !_wcsicmp(day, L"среда") ) return 2;
   if( !_wcsicmp(day, L"четверг") ) return 3;
   if( !_wcsicmp(day, L"пятница") ) return 4;
   if( !_wcsicmp(day, L"суббота") ) return 5;
   //L"воскресенье"
   return 6;
}

struct SortDate
{
   mutable OrgFolderImpl of;

   bool operator()(TreeNode* _left, TreeNode* _right) const
   {
      of.Read(_left->id);
      int val = DayToInt(GetFolderName(of.name));

      of.Read(_right->id);
      return (val < DayToInt(GetFolderName(of.name)));
   }
};

static int GetWeekIndex()
{
   int wi = 0;
   ConfigImpl c;
   c.key = (wchar_t*)SHEDULE_START_KEY;
   if( c.Read() )
   {
      SYSTEMTIME st = {0};
      bool read = false;

      wchar_t* p = c.value;
      st.wYear = (WORD)wcstol(p, &p, 10);
      if( *p != L'\0' )
      {
         st.wMonth = (WORD)wcstol(p+1, &p, 10);
         if( *p != L'\0' )
         {
            st.wDay = (WORD)wcstol(p+1, &p, 10);
            read = true;
         }
      }

      if( read )
      {
         __int64 stt, ctt;
         SystemTimeToFileTime(&st, (FILETIME*)&stt);

         GetLocalTime(&st);
         SystemTimeToFileTime(&st, (FILETIME*)&ctt);

         // проверяем неделю только когда текущая дата позже начала расписания
         if( ctt >= stt )
         {
            const __int64 week = (__int64)10000000 * 3600 * 24 * 7;
            __int64 diff = ctt - stt;
            if( week <= diff )
            {
               int weeks = (int)(diff / week);
               wi = weeks % 4 + 1;
            } else
               wi = 1;
         }
      }
   }

   return wi;
}

void OrgFoldersData::LoadTree()
{
   int weekIndex = GetWeekIndex();

   SQLTable table(orgFolder.Name());
   std::vector<ROWID> rids;

   table.RIDList(&rids);

   OrgFolderImpl oi;

   std::vector<ROWID>::const_iterator i = rids.begin();
   while( i != rids.end() )
   {
      oi.Read(*i);

      if( !isdigit(*oi.name) || (*oi.name - L'0') == weekIndex )
      {
         TreeNode *current = new TreeNode(&root);
         current->id = (*i);
         current->haveLeafs = true;
         root.childs.push_back(current);
      }
      i++;
   }
   sort(root.childs.begin(), root.childs.end(), SortDate());
}

ROWID OrgFoldersData::GetOID(int index) const
{
   return (leafs.size() > (unsigned)index) ? leafs[index] : 0;
}

const ListFormData::Header* OrgFoldersData::GetHeader() const
{ 
   return header;
}

int OrgFoldersData::ColumnsCount() const
{
   return sizeof(header)/sizeof(header[0]); 
}

const DataReflector& OrgFoldersData::DataType() const
{
   return odItem.GetType();
}

DWORD OrgFoldersData::FOrgSum::GetFolderSum(const wchar_t *type, const vector_t<OrgFolderItem>& items)
{
   DWORD sum = 0;

   std::wstring add(L"WHERE type='");
   add += type;
   add += L"' AND id in (";

   std::vector<OrgFolderItem>::const_iterator i = items.begin();
   for( ; i!= items.end(); i++ )
   {
      if( i != items.begin() ) add += L",";
      add += L"'";
      add += i->name;
      add += L"'";
   }
   add += L")";

   bool bdo = table.Select(this, add.c_str());
   while( bdo )
   {
      sum += this->sum;
      bdo = table.SelectNext(this);
   }

   return sum;
}

DWORD OrgFoldersData::CountFolderSum(const OrgFolder &folder) const
{
   return sums.GetFolderSum(docType->Type(), folder.items);
}

void OrgFoldersData::LoadFolderData(const TreeNode& folder)
{
   if( folder.id == NO_ROWID )
   {
      title = L"Организации";
      return;
   }

   orgFolder.Read(folder.id, false, false);
   title = GetFolderName(orgFolder.name);

   vector_t<OrgFolderItem>::const_iterator i = orgFolder.items.begin();
   for( ; i != orgFolder.items.end(); i++ )
   {
      org.id = i->name;
      org.Read(false);
      leafs.push_back(org.RID());
   }

   lastFolder = UpFolder();
}

bool OrgFoldersData::Get(IReflectableData* data, int index) const
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
      if( org.Read(leafs[index], true, false) == false ) return false;

      ((OrgDataItem*)data)->sum = sums.GetSum(docType->Type(), org.id);
      name = org.name;
      AfterSetName(org);
      ((OrgDataItem*)data)->name = name.c_str();
   }
   return true;
}

bool OrgFoldersData::SelectLeaf(int index)
{
   ROWID id = GetOID(index);
   if( !org.Read(id) ) return false;

   if( owner ) OrgFolders::lastViewed = owner->GetLastVisibleItem();
   OpenOrgDocs(org.id, docType->Type());

   return true;
}

bool OrgFoldersData::IsCurDate(int index) const
{
   if( IsTopLevel() && index < (int)folders.size() )
   {
      SYSTEMTIME st;
      GetLocalTime(&st);

      if( st.wDayOfWeek == 0 ) st.wDayOfWeek = 7;

      orgFolder.Read(folders[index], true, false);
      if( DayToInt(GetFolderName(orgFolder.name)) == st.wDayOfWeek - 1)
         return true;
   }
   return false;
}

//
//------------------- OrgFolders ----------------------
//
OrgFolders::OrgFolders() : OrgFuncs(this)
{
}

DWORD OrgFolders::GetMenuID() const
{
   return (((OrgFoldersData*)data)->GetDocType()->IsCreatable()) ? IDR_NEW_ORDER : 0;
}

void OrgFolders::UpdateLayout(bool forceRecalc)
{
   SQLFolderForm::UpdateLayout(forceRecalc);
   
   CRect bounds;
   CHeaderCtrl header(listCtrl.GetHeader());
   header.GetWindowRect(bounds);

   OrgFuncs::UpdateLayout(bounds);

   int itemHeight = bounds.Height();
   SetListLayout(forceRecalc, itemHeight);
   listCtrl.EnsureVisible(lastViewed, FALSE);

   InvalidateRect(NULL);
   UpdateWindow();
}

void OrgFolders::SearchClear()
{
   ((OrgFoldersData*)data)->ClearSearch();
   Refresh();
}

void OrgFolders::SearchDo(const wchar_t *text)
{
   if( ((OrgFoldersData*)data)->FindItems(text) )
      Refresh();
}


bool OrgFolders::SetData(IFormData *_data)
{
   Preference p;
   p.Load();

   return SetDataEx(_data, p.orgScale + 1);
}

bool OrgFolders::SetDataEx(IFormData *_data, int scale)
{
   if( SQLFolderForm::SetDataEx(_data, scale) == false )
      return false;

   SetOrgData((OrgFoldersData*)data, this);
   UpdateLayout(false);
   OrgFuncs::LoadMenuBar(false);

   //menuBar.EnableButton(IDC_BACK, (((SQLFolderFormData*)data)->IsTopLevel()) ? FALSE : TRUE);

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (scale == 1) ? 12 : (scale == 2) ? 28 : 13;
   menuBar.SetButtonInfo(IDC_SHOW_2_ROW, &bi);

   return true;
}

LRESULT OrgFolders::ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Preference p;
   p.Load();

   p.orgScale++;
   if( p.orgScale > MAX_SCALE_ROW )
      p.orgScale = 0;

   p.Save();

   OrgFoldersData *od = ((OrgFoldersData*)data)->Clone();
   _Module.GetFrame()->Load(GetID(), od);
   return 0;
}

void OrgFolders::Refresh()
{
   ((OrgFoldersData*)data)->RefreshCurrent();
   SQLFolderForm::Refresh();
   if( sumLabel.m_hWnd != NULL )
      sumLabel.SetSum(((OrgFoldersData*)data)->GetSum());
}

LRESULT OrgFolders::ShowContextMenu(HWND hWnd, const CPoint &org)
{
   int i = listCtrl.GetSelectedIndex();
   if( ((OrgFoldersData*)data)->IsFolder(i) ) return 0;

   return ListForm::ShowContextMenu(hWnd, org);
}

int OrgFolders::ImageIndex(int index) const
{
   int i = SQLFolderForm::ImageIndex(index);
   if( i < 0 ) 
      return i;

   return (((OrgFoldersData*)data)->IsCurDate(index)) ? 3 : i;
}

LRESULT OrgFolders::ChangeView(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Preference prf;
   prf.Load();
   prf.flags &= (~opfCalendarView);
   prf.Save();

   OpenOrgList(((OrgFoldersData*)data)->GetDocType()->Type());

   return 0;
}

//
//------------------- SheduleForm ----------------------
//
#ifdef SHEDULE
static void NextDay(SYSTEMTIME *st)
{
   FILETIME ft;
   SystemTimeToFileTime(st, &ft);

   // C9 2A69 C000
   ft.dwLowDateTime += 0x2A69C000;
   ft.dwHighDateTime += 0xC9;
   if( ft.dwLowDateTime < 0x2A69C000 )
      ft.dwHighDateTime++;

   FileTimeToSystemTime(&ft, st);
}

static const wchar_t * DayOfWeekName(WORD dayOfWeek)
{
   switch(dayOfWeek)
   {
      case 1: return L"Понедельник";
      case 2: return L"Вторник";
      case 3: return L"Среда";
      case 4: return L"Четверг";
      case 5: return L"Пятница";
      case 6: return L"Суббота";
      case 7: return L"Воскресенье";
   }
   return L"";
}

static DWORD SystemTimeToDate(const SYSTEMTIME &st)
{
   return (DWORD)st.wYear * 10000 + (DWORD)st.wMonth * 100 + st.wDay;
}

SheduleData::SheduleData() : currentSum(0)
{
}

bool SheduleData::IsCurDate(int index) const
{
   if( (unsigned)index >= folders.size() || data.size() == 0 ) return false;

   FOLDER_MAP::const_iterator i = data.begin();
   while( index-- > 0 ) i++;

   SYSTEMTIME st;
   GetLocalTime(&st);

   return (i->first.date == SystemTimeToDate(st));
}

void SheduleData::AddOrder(const Order& order, CEOID orderID)
{
   if( order.shedule.dwHighDateTime == 0 ) return;

   SYSTEMTIME st;
   FileTimeToSystemTime(&order.shedule, &st);
   Folder f(SystemTimeToDate(st));

   AddShedule(st);
   FOLDER_MAP::iterator fnd = data.find(f);

   if( fnd == data.end() ) return;

   //ATLASSERT(fnd != data.end());

   Folder &fld = (Folder&)fnd->first;
   fld.sum += order.Sum();

   std::vector<FolderItem>::iterator i = fnd->second.begin();
   for( ; i != fnd->second.end(); i++ )
   {
      if( i->id == order.id )
      {
         int hour = 0, minute = 0;
         swscanf(i->time, L"%d:%d", &hour, &minute);

         if( hour == st.wHour && minute == st.wMinute )
         {
            i->orderID = orderID;
            break;
         }
      }
   }
}

void SheduleData::AddShedule(const SYSTEMTIME &st)
{
   if( shedule.size() == 0 )
      LoadShedule();

   DWORD date = SystemTimeToDate(st);
   short dayOfWeek = (st.wDayOfWeek == 0) ? 6 : st.wDayOfWeek - 1;

   std::vector<OrgFolderItem> &ref = shedule[dayOfWeek];
   if( ref.size() == 0 )
      return;

   Folder folder;
   folder.date = date;
   folder.dayOfWeek = dayOfWeek + 1;
   std::vector<FolderItem> &items = data[folder];

   if( items.size() > 0 ) return;

   std::vector<OrgFolderItem>::iterator i = ref.begin();
   for( ; i != ref.end(); i++ )
   {
      FolderItem fi;
      fi.time = i->time;
      fi.id = i->id;

      items.push_back(fi);
   }
}

void SheduleData::LoadShedule()
{
   OrgFolder of;
   SyncFOrg sf;
   CEDBFormat format(sf);
   CETable table(format);

   table.Open(sf.FileName());
   CEOID oid = table.SetPos(0);

   int ctr = 0;
   while( ctr++ < 7 )
   {
      std::vector<OrgFolderItem> items;
      shedule.push_back(items);
   }

   while( oid != NULL )
   {
      table.GetCurrent(&of);

      int day = _wtoi(of.name);

      std::vector<OrgFolderItem>::iterator i = of.items.begin();
      for( ; i != of.items.end(); i++ )
         i->time = sh.Add(i->time);

      shedule[day-1] = of.items;

      oid = table.MoveNext(true);
   }
}

void SheduleData::Load()
{
   SYSTEMTIME st;
   GetLocalTime(&st);

   Order o;
   SyncOrder so;
   CEDBFormat format(so);
   CETable table(format);

   if( table.Open(so.FileName()) )
   {
      CEOID oid = table.SetPos(0);
      while( oid != NULL )
      {
         table.GetCurrent(&o);

         AddOrder(o, oid);
         oid = table.MoveNext(true);
      }
   }

   for( int i=0; i<7; i++ )
   {
      AddShedule(st);
      NextDay(&st);
   }

   upFolders.push_back(0);
   int loadedFolder = 0;
   if( viewedDate != NULL )
   {
      loadedFolder = 1;
      FOLDER_MAP::const_iterator i = data.begin();
      while( i != data.end() && i->first.date != viewedDate )
      {
         loadedFolder++;
         i++;
      }
      if( i == data.end() ) loadedFolder = 0;
      else upFolders.push_back(loadedFolder);

      viewedDate = 0;
   }
   //upFolders.push_back(0);
   LoadFolder(loadedFolder);
}

void SheduleData::LoadData(CEOID upFolder)
{
   SyncFOrg forg;
   CEDBFormat fmt(forg);
   CETable table(fmt);
   OrgFolder of;

   if( table.Open(forg.FileName()) == false  || table.Seek(upFolder) == false ) return;

   table.GetCurrent(&of);

   std::vector<OrgFolderItem>::const_iterator i = of.items.begin();
   for( ; i != of.items.end(); i++ )
      times.push_back(i->time);

   SYSTEMTIME st = {0};
   orgShedule = st;
   int year, month, day;
   swscanf(of.name, L"%d.%d.%d", &day, &month, &year);

   orgShedule.wDay = day;
   orgShedule.wMonth = month;
   orgShedule.wYear = year;
}

void SheduleData::LoadFolder(CEOID upFolder)
{
   folders.clear();
   leafs.clear();

   if( upFolder == 0 )
   {
      int i = 1, sz = data.size();
      while( i <= sz )
         folders.push_back(i++);

      FOLDER_MAP::const_iterator fi = data.begin();
      currentSum = 0;
      while( fi != data.end() )
      {
         currentSum += fi->first.sum;
         fi++;
      }

      viewedDate = 0;
      return;
   }

   currentData = data.begin();
   while( --upFolder > 0 ) currentData++;
 
   MakeName(&title, currentData);

   int ctr = 0, size = currentData->second.size();
   while( ctr < size )
      leafs.push_back(currentData->second[ctr++].id);

   currentSum = currentData->first.sum;

   int rest = currentData->first.date % 10000;
   orgShedule.wDay = rest % 100;
   orgShedule.wMonth = rest / 100;
   orgShedule.wYear = (WORD)(currentData->first.date / 10000);

   viewedDate = currentData->first.date;
}

bool SheduleData::SelectLeaf(int index)
{
   int ti = index - folders.size();

   if( ti < (int)leafs.size() )
   {
      orgShedule.wHour = 0;
      orgShedule.wMinute = 0;
      orgShedule.wSecond = 0;

      const FolderItem &fi = currentData->second[ti];
      swscanf(fi.time, L"%d:%d", &orgShedule.wHour, &orgShedule.wMinute);

      //viewedDate = currentData->first.date;

      if( fi.orderID == NULL )
      {
         SYSTEMTIME st;
         GetLocalTime(&st);

         DWORD curDate = SystemTimeToDate(st);

         // не создаем заказ за предыдущие дни
         if( currentData->first.date < curDate ) return false;
         OrderImpl::Create(fi.id);
      } else
      {
         OrderImpl *o = OrderImpl::Read(fi.orderID);
         if( o != NULL )
            OpenInvoice(o);
      }
   }

   return false;
}

void SheduleData::MakeName(std::wstring *name, FOLDER_MAP::const_iterator &i) const
{
   int y = i->first.date / 10000;
   int rest = i->first.date % 10000;

   wchar_t buf[30];
   wsprintf(buf, L"%02d.%02d.%d - ", rest % 100, rest / 100, y);
   *name = buf;
   (*name) += DayOfWeekName(i->first.dayOfWeek);
}

bool SheduleData::Get(IReflectableData *data, int index) const
{
   if( (unsigned)index < folders.size() )
   {
      FOLDER_MAP::const_iterator i = this->data.begin();
      while( index-- > 0 ) i++;

      MakeName(&name, i);

      ((OrgDataItem*)data)->sum = i->first.sum;
   } else
   {
      if( !OrgFoldersData::Get(data, index) ) return false;
      
      index -= folders.size();
      if( (unsigned)index >= leafs.size() ) return false;

      const FolderItem &fi = currentData->second[index];

#if defined(Autopteka) || defined(Autopteka_van)
#else
      std::wstring tname = name;
      name = fi.time;
      name += L" ";
      name += tname;
#endif

      ((OrgDataItem*)data)->sum = 0;

      if( fi.orderID != NULL )
      {
         OrderImpl *oi = OrderImpl::Read(fi.orderID);
         if( oi != NULL )
         {
            ((OrgDataItem*)data)->sum = oi->Sum();
            delete oi;
         }
      }
   }

   ((OrgDataItem*)data)->name = name.c_str();
   return true;
}

void SheduleData::ClearShedule()
{
   orgShedule.wYear = 0;
   orgShedule.wMonth = 0;
   orgShedule.wDay = 0;
}

void SheduleForm::UpdateLayout(bool forceRecalc)
{
   FolderForm::UpdateLayout(forceRecalc);
   CRect rc, bounds;

   CHeaderCtrl header(listCtrl.GetHeader());

   header.GetWindowRect(bounds);
   GetParent().GetClientRect(rc);

   if( search.m_hWnd != NULL )
      search.ShowWindow(SW_HIDE);

   SetListLayout(forceRecalc, 0);

   listCtrl.EnsureVisible(lastViewed, FALSE);
}

LRESULT SheduleForm::SetCellInfo(LPNMHDR hdr)
{
   if( OrgFolders::SetCellInfo(hdr) == FALSE )
      return FALSE;

   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( di->item.mask & LVIF_IMAGE )
   {
      int index = di->item.iItem;
      if( ((SheduleData*)data)->IsCurDate(index) )
         di->item.iImage = 3;
   }
   return TRUE;
}

#endif

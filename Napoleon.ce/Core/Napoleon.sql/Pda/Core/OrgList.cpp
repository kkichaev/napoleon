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
#include <DocType.h>
#include "DocImpl.h"
#include <Preference.h>

IMPLEMENT_FORM(OrgList);
int OrgList::lastViewed;

BEGIN_TYPE_REFLECTION(OrgListItem)
   REGISTER_STRING_MEMBER(OrgListItem, name)
   REGISTER_LONG_SCALE_MEMBER(OrgListItem, sum, SUM_SCALE)
END_TYPE_REFLECTION(OrgListItem)

//
//--------------------------- OrgListData -------------------------------------
//
const DataReflector& OrgListData::DataType() const 
{
   return OrgListItem().GetType(); 
}

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"Сумма", L"sum", 50 }
};

const ListFormData::Header* OrgListData::GetHeader() const
{
   return header;
}

int OrgListData::ColumnsCount() const
{
   return sizeof(::header)/sizeof(::header[0]);
}

OrgListData::OrgListData() : inSearch(false)
{
   Open();
   searchHelper.SetData(org.Name(), L"name");
}

OrgListData::~OrgListData()
{
   DestroyData();
}

OrgListData* OrgListData::Clone()
{
   OrgListData *od = new OrgListData();
   od->SetDocType(GetDocType()->Type());

   return od;
}

bool OrgListData::FindItems(const wchar_t *text)
{
   inSearch = true;
   searchResult.clear();
   searchHelper.Search(&searchResult, text);
   return true;
}

void OrgListData::ClearSearch()
{
   searchHelper.Clear();
   searchResult.clear();

   inSearch = false;
}

void OrgListData::Refresh()
{
   Open();
}

bool OrgListData::Selecting(int index)
{
   if( owner ) OrgList::lastViewed = owner->GetLastVisibleItem();
   ROWID id = GetOID(index);

   if( !org.Read(id) ) return false;
   docType->OpenForm(org.id, NULL);

   return false;
}

bool OrgListData::Open()
{
   SQLTable table(org.Name());
   rows.clear();

   Preference p;
   p.Load();
   wchar_t buf[50];
   wcscpy(buf, L"ORDER BY name COLLATE " );
   wcscat(buf, (p.flags & apfSortNoCase) ? L"RUSS_NOCASE" : L"RUSS" );

   table.RIDList(&rows, buf);
   return true;
}

ROWID OrgListData::GetOID(int index) const
{
   return (inSearch) ? searchResult[index] : rows[index];
};

bool OrgListData::Remove(int index)
{
   ROWID oid = GetOID(index);
   org.Read(oid);
   docTypeManager.RemoveOrg(org.id);

   org.Remove();

   return true;
}

void OrgListData::AfterSetName() const
{
}

bool OrgListData::Get(IReflectableData* rdata, int index) const
{
   const std::vector<ROWID> &r = (inSearch) ? searchResult : rows;

   if( index >= (int)r.size() ) return false;
   if( !org.Read(r[index]) ) return false;

   ((OrgListItem*)rdata)->sum = sums.GetSum(docType->Type(), org.id);

   name = org.name;
   AfterSetName();
   ((OrgListItem*)rdata)->name = name.c_str();

   return true;
}

//
//--------------------------- OrgList -------------------------------------
//
OrgList::OrgList() : OrgFuncs(this)
{
}

void OrgList::UpdateLayout(bool forceRecalc)
{
   ListForm::UpdateLayout(forceRecalc);

   CRect bounds;
   CHeaderCtrl header(listCtrl.GetHeader());
   header.GetWindowRect(bounds);

   OrgFuncs::UpdateLayout(bounds);

   int itemHeight = bounds.Height();
   SetListLayout(forceRecalc, itemHeight);
   listCtrl.EnsureVisible(lastViewed, FALSE);
}

DWORD OrgList::GetMenuID() const
{ 
   return (((OrgListData*)data)->GetDocType()->IsCreatable()) ? IDD_ORG_LIST : IDC_DEL;
}

void OrgList::SearchClear()
{
   ((OrgListData*)data)->ClearSearch();
   Refresh();
}

void OrgList::SearchDo(const wchar_t *text)
{
   if( ((OrgListData*)data)->FindItems(text) )
      Refresh();
}

bool OrgList::SetData(IFormData *_data)
{
   Preference p;
   p.Load();

   return SetDataEx(_data, p.orgScale + 1);
}

bool OrgList::SetDataEx(IFormData *_data, int scale)
{
   if( ListForm::SetDataEx(_data, scale) == false )
      return false;

   SetOrgData((OrgListData*)data, this);

   UpdateLayout(false);
   OrgFuncs::LoadMenuBar(false);

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (scale == 1) ? 12 : (scale == 2) ? 28 : 13;
   menuBar.SetButtonInfo(IDC_SHOW_2_ROW, &bi);

   return true;
}

LRESULT OrgList::RemoveOrg(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   int cs = listCtrl.GetSelectedIndex();

   if( MessageBox(L"Удалить организацию из базы?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) != IDYES )
      return 0;

   if( ((OrgListData*)data)->Remove(cs) )
      Refresh();

   return 0;
}

LRESULT OrgList::ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Preference p;
   p.Load();

   p.orgScale++;
   if( p.orgScale > MAX_SCALE_ROW )
      p.orgScale = 0;

   p.Save();

   OrgListData *od = ((OrgListData*)data)->Clone();
   _Module.GetFrame()->Load(GetID(), od);
   return 0;
}

void OrgList::Refresh()
{
   ((OrgListData*)data)->Refresh();

   ListForm::Refresh();

   if( sumLabel.m_hWnd != NULL )
      sumLabel.SetSum(((OrgListData*)data)->GetSum());

   LVCOLUMN clmn;
   clmn.mask = LVCF_TEXT;
   clmn.pszText = (((OrgListData*)data)->GetDocType()->Type() == dtBalance) ? L"Долг" : L"Сумма";
   listCtrl.SetColumn(1, &clmn);
}

LRESULT OrgList::ChangeView(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Preference prf;
   prf.Load();

   prf.flags |= opfCalendarView;
   prf.Save();

   OpenOrgFolders(((OrgListData*)data)->GetDocType()->Type());
   return 0;
}

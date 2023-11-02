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

#include <DocType.h>

#include "OrgDocs.h"
#include "FormEntries.h"
#include "PhoneDlg.h"
#include <StdFuncs.h>
#include "ObjImpl.h"
#include "DocImpl.h"

#ifdef ORG_TASK
#include "Task.h"
#endif

IMPLEMENT_FORM(OrgDocsList)

BEGIN_TYPE_REFLECTION(OrderDocListItem)
   REGISTER_FILETIME_MEMBER(OrderDocListItem, date)
   REGISTER_STRING_MEMBER(OrderDocListItem, flags)
   REGISTER_LONG_SCALE_MEMBER(OrderDocListItem, sum, SUM_SCALE)
END_TYPE_REFLECTION(OrderDocListItem)

//
//--------------------------- OrgDocsData -------------------------------------
//
static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"", L"flags", 80 },
   { ListFormData::Header::Left, L"Дата", L"date", 50 },
   { ListFormData::Header::Right, L"Сумма", L"sum", 50 }
};

const ListFormData::Header* OrgDocsListData::GetHeader() const
{
   return header;
}

int OrgDocsListData::ColumnsCount() const
{
   return sizeof(::header)/sizeof(::header[0]);
}

OrgDocsListData::OrgDocsListData(const wchar_t *orgID, const wchar_t* type) : docList(NULL), svDocType(type)
{
   OrgImpl o;
   o.id = (wchar_t*)orgID;
   o.Read();

   this->id = orgID;
   this->orgID = o.RID();
}

int OrgDocsListData::Count() const
{
   if( !docList )
      const_cast<OrgDocsListData*>(this)->SetDocType(svDocType);
   return docList->Count();
}

bool OrgDocsListData::Adding()
{
   //if( owner != NULL )
   //   ((OrgDocsList*)owner)->LoadMenuBar(false);

   if( AddNewDocument(owner, docType, orgID) )
   {
      //if( owner != NULL )
      //   ((OrgDocsList*)owner)->LoadMenuBar(true);
   }
   return false;
}

bool OrgDocsListData::Removing(int index)
{
   IDocument *doc = docList->Get(index);
   if( doc == NULL ) return false;

   bool ret = false;

   std::wstring id = doc->ID();
   ICreatableDocument *cd = doc->Creatable();
   if( cd != NULL )
   {
      docList->Unbind(doc);
      if( cd->CanRemove() && cd->RemoveDocument() )
      {
         delete docList;
         docType->GetDocuments(id.c_str(), &docList, L"", L"date");

         docTypeManager.SumChanged(docType->Type(), id.c_str());
         ret = true;
      }
      docList->Free(doc);
   }

   return ret;
}

void OrgDocsListData::SetDocType(const wchar_t* type)
{
   const DocType *dt = docTypeManager.GetDocType(type);
   if( dt != NULL )
   {
      docType = dt;
      delete docList;
      docType->GetDocuments(id.c_str(), &docList, L"", DocOrderField(type) );
   }
}

bool OrgDocsListData::Selecting(int index)
{
   IDocument *doc = docList->Get(index);
   if( doc == NULL ) return false;

   docList->Unbind(doc);
   doc->EditDocument(IDD_ORG_DOCS);

   return false;
}

IDocument* OrgDocsListData::CopyDoc(int index)
{
   IDocument *doc = docList->Get(index);
   if( doc == NULL ) return NULL;

   ICreatableDocument *cd = doc->Creatable();
   if( cd == NULL ) return NULL;

   return cd->Copy();
}

const DataReflector& OrgDocsListData::DataType() const 
{ 
   return GetTypeReflector(L"OrderDocListItem"); 
}

bool OrgDocsListData::Get(IReflectableData* data, int index) const
{
   IDocument *doc = docList->Get(index);
   if( doc == NULL ) return false;

   ((OrderDocListItem*)data)->date = doc->Date();
   ((OrderDocListItem*)data)->sum = doc->Sum();
   ((OrderDocListItem*)data)->flags = doc->Description();
   return true;
}

DWORD OrgDocsListData::GetSum() const
{
   OrgSumImpl os;
   return os.GetSum(docType->Type(), id.c_str());
}

void OrgDocsListData::GetTitle(const Org &org, std::wstring *title)
{
   title->assign(org.name);
}

//
//--------------------------- OrgDocsList -------------------------------------
//
OrgDocsList::OrgDocsList()
{
}

DWORD OrgDocsList::GetMenuID() const
{ 
#ifdef DISABLE_DOC_COPY
   return (((OrgDocsListData*)data)->CanCreateDocument()) ? IDR_ADD_REMOVE : 0; 
#else
   return (((OrgDocsListData*)data)->CanCreateDocument()) ? IDR_ADD_REMOVE_COPY : 0; 
#endif
}

LRESULT OrgDocsList::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
#if ORG_TASK
   const wchar_t *oid = ((OrgDocsListData*)data)->ID();
   if( TaskImpl::HaveTask(oid) )
   {
      if( MessageBox(L"Есть не отправленное задание. Будете делать?", L"Информация", MB_YESNO|MB_ICONINFORMATION) == IDYES )
      {
         TaskImpl::EditTask(oid, true);
         return 0;
      }
   }
#endif
   OpenOrgList(((OrgDocsListData*)data)->GetDocType()->Type());
   return 0;
}

LRESULT OrgDocsList::Copying(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   IDocument *o = ((OrgDocsListData*)data)->CopyDoc(listCtrl.GetSelectedIndex());
   if( o != NULL )
      o->EditDocument(IDD_ORG_DOCS);

   return 0;
}

const DocType* SelectDocType(CMenuBarCtrl *menuBar, HWND hWnd, bool creatable)
{
   CRect menuBounds;
   menuBar->GetRect(IDC_VIEW_TYPE, menuBounds);
   menuBar->ClientToScreen(menuBounds);

   HMENU hm = CreatePopupMenu();

   int ctr = 1;
   MENUITEMINFO mi;
   DocTypeManager::const_iterator i = docTypeManager.begin();
   mi.cbSize = sizeof(mi);
   mi.fMask = MIIM_DATA;
   for( ; i != docTypeManager.end(); i++ )
   {
      if( (*i)->ShowInDocumentList() == false )
         continue;

      if( creatable )
      {
         if( (*i)->IsCreatable() == false ) continue;
      }
      //else
      //   if( (*i)->ShowInDocumentList() == false ) continue;

      UINT flag = MF_STRING;
      std::wstring name(L"&");
      name += (*i)->Type();

      AppendMenu(hm, flag, ctr, name.c_str());
      
      mi.dwItemData = (DWORD)(*i);
      SetMenuItemInfo(hm, ctr, FALSE, &mi); 
      ctr++;
   }

   int res = TrackPopupMenuEx(hm,  TPM_RETURNCMD | TPM_BOTTOMALIGN, 
      menuBounds.left, menuBounds.top, hWnd, NULL);

   DocType *dt = NULL;
   if( res != 0 )
   {
      GetMenuItemInfo(hm, res, FALSE, &mi);
      dt = (DocType*)mi.dwItemData;
   }
   DestroyMenu(hm);

   if( res == 0 ) return NULL;

   TBBUTTONINFO bi;
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_TEXT;
   bi.pszText = (LPWSTR)dt->Type();
   menuBar->SetButtonInfo(IDC_VIEW_TYPE, &bi);
   return dt;
}

LRESULT OrgDocsList::SetViewType(int id, LPNMHDR header, BOOL &handled)
{
   if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;

   const DocType *dt = SelectDocType(&menuBar, m_hWnd);
   if( dt != NULL )
      dt->OpenForm(((OrgDocsListData*)data)->ID(), this);

   return 0;
}

void OrgDocsList::SetViewType(const DocType *newDT)
{
   if( newDT->Type() != ((OrgDocsListData*)data)->GetDocType()->Type() != 0 )
   {
      ((OrgDocsListData*)data)->SetDocType(newDT->Type());
      Refresh();
   }
}

BOOL OrgDocsList::EnableAddButton() const
{
#ifdef Agama
   return TRUE;
#else
   return (((OrgDocsListData*)data)->CanCreateDocument());
#endif
}

void OrgDocsList::LoadMenuBar(bool hideSIP)
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, (hideSIP) ? SHCMBF_HIDESIPBUTTON : 0));

   TBBUTTONINFO bi;
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_TEXT;
   bi.pszText = (LPWSTR)((OrgDocsListData*)data)->GetDocType()->Type();
   menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);

   //if( hideSIP )
   {
      sumLabel.CreateLabel(menuBar.m_hWnd, SumLabel::STD_WIDTH, GetSystemMetrics(SM_CXSMICON) * 9 / 4);
      sumLabel.SetSum(((OrgDocsListData*)data)->GetSum());
   }

   menuBar.EnableButton(IDC_ADD, EnableAddButton());

#ifdef ORG_NOTE
   {
      TBBUTTON button;
      button.iBitmap = 22;
      button.idCommand = IDC_NOTES;
      button.fsState = TBSTATE_ENABLED;
      button.fsStyle = TBSTYLE_BUTTON | TBSTYLE_AUTOSIZE;
      button.dwData = 0;
      button.iString = IDC_NOTES;

      menuBar.AddButtons(1, &button);
   }
#endif

#ifdef ORG_TASK
   if( TaskImpl::HaveTask(((OrgDocsListData*)data)->ID()) )
   {
      TBBUTTON button;
      button.iBitmap = 26;
      button.idCommand = IDC_WARNING;
      button.fsState = TBSTATE_ENABLED;
      button.fsStyle = TBSTYLE_BUTTON | TBSTYLE_AUTOSIZE;
      button.dwData = 0;
      button.iString = IDC_WARNING;

      menuBar.AddButtons(1, &button);
   }
#endif
}

void OrgDocsList::MouseDown(UINT flags, const CPoint &pt)
{
   orgInfo.SwitchInfo();

   UpdateLayout(false);
   Invalidate();
   UpdateWindow();
}

void OrgDocsList::Paint(HDC dc)
{
   orgInfo.Paint(dc);
   SetMsgHandled(FALSE);
}

int OrgDocsList::ImageListID(ListViewMultiLine *list) const
{
   return -1;
}

void OrgDocsList::UpdateLayout(bool forceRecalc)
{
   int docsTop;
   CRect rc;

   // надо установить размер до вызова UpdateLayout
   GetParent().GetClientRect(rc);
   SetWindowPos(NULL, 0, 0, rc.right, rc.Height(), SWP_NOZORDER|SWP_NOOWNERZORDER);

   orgInfo.UpdateLayout(&docsTop, forceRecalc);

   SetListLayout(forceRecalc, docsTop);
   sumLabel.UpdateLayout();
}

LRESULT OrgDocsList::ItemSelected(LPNMHDR hdr)
{
   if( !orgInfo.CanHandle(hdr) )
      return ListForm::ItemSelected(hdr);

   if( orgInfo.Selecting(hdr) )
      Refresh();

   return TRUE;
}

LRESULT OrgDocsList::SetCellInfo(LPNMHDR hdr)
{
   if( !orgInfo.CanHandle(hdr) )
      return ListForm::SetCellInfo(hdr);

   return orgInfo.SetCellInfo(hdr);
}

bool OrgDocsList::SetDataEx(IFormData *_data, int scale)
{
   if( ListForm::SetDataEx(_data, scale) == false )
      return false;

   orgInfo.Init(((OrgDocsListData*)_data)->OrgID(), *this, IDC_ORG_TITLE, IDC_ADDRESS_LABEL, IDC_CONTACTS, (OrgDocsListData*)data);

   LoadMenuBar(false);

#ifdef ORG_NOTE
   menuBar.EnableWindow(FALSE);
   OpenNote(m_hWnd, ((OrgDocsListData*)data)->ID(), true);
   menuBar.EnableWindow(TRUE);
#endif

   //LoadMenuBar(true); // call UpdateLayout internal

   return true;
}

void OrgDocsList::Refresh()
{
   ListForm::Refresh();
   sumLabel.SetSum(((OrgDocsListData*)data)->GetSum());

   menuBar.EnableButton(IDC_ADD, EnableAddButton());
}

#ifdef ORG_TASK
LRESULT OrgDocsList::Task(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   TaskImpl::EditTask(((OrgDocsListData*)data)->ID(), true);
   return 0;
}
#endif

#ifdef ORG_NOTE
LRESULT OrgDocsList::Notes(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   //LoadMenuBar(false);
   menuBar.EnableWindow(FALSE);
   OpenNote(m_hWnd, ((OrgDocsListData*)data)->ID(), false);
   menuBar.EnableWindow(TRUE);
   //LoadMenuBar(true);

   return 0;
}
#endif
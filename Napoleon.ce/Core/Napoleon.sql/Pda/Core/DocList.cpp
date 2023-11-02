/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Список документов
 * 
 *  ert   01/09/2007   creating
 */ 
#include "stdafx.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <ListForm.h>
#include <SQLTable.h>
#include "Progress.h"
#include "FormEntries.h"
#include <NapoleonRes.h>
#include "OrgDocs.h"
#include "DocListData.h"

#include <DateDialog.h>

#ifdef VISIT_DOC
#include <Visit.h>
#endif

#ifdef SCRIPT_DOC
#include <ScriptDoc.h>
#endif

#ifdef Alians_sp
#include <Password.h>
#include <NplConfig.h>
#endif

BEGIN_TYPE_REFLECTION(ListDocItem)
   REGISTER_STRING_MEMBER(ListDocItem, name)
   REGISTER_FILETIME_MEMBER(ListDocItem, date)
   REGISTER_ULONG_SCALE_MEMBER(ListDocItem, sum, SUM_SCALE)
END_TYPE_REFLECTION(ListDocItem)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"", L"name", 80 },
   { ListFormData::Header::Left, L"Дата", L"date", 50 },
   { ListFormData::Header::Right, L"Сумма", L"sum", 50 }
};

const wchar_t *ListDocData::lastViewType = NULL;

ListDocData::ListDocData(const wchar_t *dt) : docList(NULL)
{
   if( dt != NULL )
      lastViewType = dt;
   else if( lastViewType == NULL )
      lastViewType = dtOrder;

   OpenDocType(lastViewType);
}

ListDocData::~ListDocData()
{
   delete docList;
}

void ListDocData::OpenDocType(const wchar_t *type)
{
   docType = docTypeManager.GetDocType(type);
   lastViewType = type;

   delete docList;
   docType->GetDocuments(L"", &docList, L"", L"date");
}

const ListFormData::Header *ListDocData::GetHeader() const 
{
   return header;
}

int ListDocData::ColumnsCount() const 
{
   return sizeof(header)/sizeof(header[0]);
}

bool ListDocData::IsProceeded(int index) const
{
   IDocument *d = docList->Get(index);
   if( d != NULL )
   {
      ICreatableDocument *cd = d->Creatable();
      return (cd != NULL) ? cd->IsProceeded() : false;
   }
   return false;
}

bool ListDocData::IsDirty(int index) const
{
   IDocument *d = docList->Get(index);
   if( d != NULL )
   {
      ICreatableDocument *cd = d->Creatable();
      return (cd != NULL) ? cd->IsDirty() : false;
   }
   return false;
}

#ifdef ORD_DLV_BIND
bool ListDocData::OrderHandled(int index) const
{
   IDocument *d = docList->Get(index);
   if( d == NULL ) return false;

   bool res = false;
   IReflectableData* data = d->Data();
   const DataReflector& r = data->GetType();
   int idx = r.Find(L"params"), idx1 = r.Find(L"number");
   if( idx >= 0 && idx1 >= 0 )
   {
      DWORD params = *(DWORD*)r.Type(idx).GetValue(*data);
      const wchar_t *number = *(const wchar_t**)r.Type(idx1).GetValue(*data);
      res = ((params & ofExported) && *number != L'\0');
   }
   return res;
}
#endif

bool ListDocData::Selecting(int index)
{
   IDocument *d = docList->Get(index);
   if( d == NULL ) return false;

#ifdef CANT_SEND_SENDED
   if( lastViewType == dtOrder )
      return Editing(index);
#endif // CANT_SEND_SENDED
#ifdef CANT_CLEAR_DIRTY
   if( owner )
      return Editing(index);
   return false;
#else
   if( owner )
   {
      DWORD flg = ((ListDoc*)owner)->GetHitTest();
      if( flg != LVHT_ONITEMICON )
         return Editing(index);
   }

   SQLTable table(d->DBData()->Name());
   ICreatableDocument *c = d->Creatable();
   if( c->IsProceeded() )
      return false;

#ifdef SCRIPT_DOC
   if( docType->Type() == dtScript )
      ((ScriptImpl*)d)->SetClearCompleete();
#endif

   c->ClearDirty(&table, true);
#endif
   return true;
}

bool ListDocData::SendOrders()
{
   ProgressWindow pw;
   std::wstring answer;
   HWND activeWindow = GetActiveWindow();
   pw.CreateSTDWindow(activeWindow);

   DWORD flags = NapoleonApp::efDocs;

#ifdef VISIT_DOC
   if( docType->Type() == dtVisit )
      flags |= NapoleonApp::efVisits;
#endif

#ifdef SCRIPT_DOC
   if( docType->Type() == dtScript )
      flags |= NapoleonApp::efVisits;
#endif

   long ec = _Module.ExportDocuments(&answer, &pw, flags);

   pw.DestroyWindow();

   if( ec )
   {
      _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при передаче:\n");
   } else
   {
      docList->ClearCache();
      MessageBox(activeWindow, answer.c_str(), L"Подтверждение", MB_OK|MB_ICONINFORMATION);
   }

#ifdef RCV_MESSAGE
      _Module.ShowMessage();
#endif

   return (ec) ? false : true; 
}

bool ListDocData::Editing(int index)
{
   IDocument *d = docList->Get(index);
   if( d == NULL ) return false;
   docList->Unbind(d);
   d->EditDocument(IDD_ORDER_LIST);
   return false;
}

bool ListDocData::Removing(int index)
{
   IDocument *d = docList->Get(index);
   if( d == NULL ) return false;
   ICreatableDocument *c = d->Creatable();
   if( c == NULL ) return false;

   bool ret = false;

   std::wstring id = d->ID();
   docList->Unbind(d);
   if( c->CanRemove() )
      ret = c->RemoveDocument();
   docList->Free(d);

   if( ret )
      docTypeManager.SumChanged(docType->Type(), id.c_str());

   OpenDocType(docType->Type());
   return ret;
}

bool ListDocData::Get(IReflectableData* data, int index) const
{
   IDocument *d = docList->Get(index);
   if( d == NULL ) return false;

   org.id = (wchar_t*)d->ID();
   if( !org.Read() )
      ((ListDocItem*)data)->name = L"?";
   else
      ((ListDocItem*)data)->name = org.name;

   ((ListDocItem*)data)->sum = d->Sum();
   ((ListDocItem*)data)->date = d->Date();

   return true;
}


IMPLEMENT_FORM(ListDoc);

ListDoc::ListDoc()
{
}

LRESULT ListDoc::DoSelect(LPNMHDR hdr)
{
   int index = ((NMLISTVIEW*)hdr)->iItem;

   listCtrl.HitTest(((NMLISTVIEW*)hdr)->ptAction, (UINT*)&hitFlags);
   if( index >= 0 && ((ListFormData*)data)->Selecting(index) )
   {
      listCtrl.SetItemState(index, 0, LVIS_SELECTED);
      listCtrl.RedrawItems(index, index);
   }
   return TRUE;
}

LRESULT ListDoc::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   std::wstring msg(L"Отправить все неотправленные '");
   msg += ((ListDocData*)data)->Type()->Type();
   msg += L"'";

   if( ::MessageBox(m_hWnd, msg.c_str(),  L"Вопрос", MB_YESNO|MB_ICONQUESTION) != IDYES )
   {
      return 0;
   }

   if( ((ListDocData*)data)->SendOrders() )
      Refresh();
   return 0;
}

LRESULT ListDoc::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   OpenOrgList(dtOrder);
   return 0;
}

LRESULT ListDoc::SetViewType(int id, LPNMHDR header, BOOL &handled)
{
   if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;
   const DocType *dt = SelectDocType(&menuBar, m_hWnd, true);
   if( dt != NULL )
   {
      ((ListDocData*)data)->OpenDocType(dt->Type());

      TBBUTTONINFO bi;
      bi.cbSize = sizeof(bi);
      bi.dwMask = TBIF_TEXT;
      bi.pszText = (LPWSTR)dt->Type();
      menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);

      Refresh();
   }
   return 0;
}

bool ListDoc::SetData(IFormData *_data)
{
   if( ListForm::SetData(_data) == false )
      return false;

   CImageList il;
   HBITMAP bmp = LoadBitmap(_Module.GetResourceInstance(), MAKEINTRESOURCE(
      (GetSystemMetrics(SM_CXSMICON) == 16) ? IDB_CHECKED : IDB_CHECKED32));

   BITMAP binfo;
   GetObject(bmp, sizeof(binfo), &binfo);
   il.Create(binfo.bmHeight, binfo.bmHeight, ILC_COLOR, binfo.bmWidth / binfo.bmHeight, 1);
   il.Add(bmp);
   listCtrl.SetImageList(il, LVSIL_SMALL);
   DeleteObject((HGDIOBJ)bmp);

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID()));
   SetListLayout(false);

   int count = listCtrl.GetItemCount();
   if( count > 0 )
      listCtrl.EnsureVisible(count-1, FALSE);

   TBBUTTONINFO bi;
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_TEXT;
   bi.pszText = (LPWSTR)((ListDocData*)data)->Type()->Type();
   menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);

   return true;
}

void OpenListDoc(const wchar_t *docType)
{
#ifdef SCRIPT_DOC
   if( docType == dtOrder )
      docType = dtScript;
#endif
   _Module.GetFrame()->Load(IDD_ORDER_LIST, new ListDocData(docType));
}

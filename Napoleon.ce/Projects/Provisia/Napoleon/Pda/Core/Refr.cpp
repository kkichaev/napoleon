/*
 * Copyright (C), 2007-2012, Денис Мосягин
 *
 * Оплата накладных
 *
 *  ert   25/04/2012   creating
 *
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <NapoleonRes.h>
#include <BaseForm.h>
#include <BaseDialog.h>
#include <ListForm.h>

#include <FormEntries.h>
#include <ObjImpl.h>
#include <Refr.h>

struct RfrItem : public IReflectableData
{
   wchar_t *name;
   DECLARE_TYPE_REFLECTION(RfrItem)
};

BEGIN_TYPE_REFLECTION(RfrDocItem)
   REGISTER_STRING_MEMBER(RfrDocItem, id)
   REGISTER_STRING_MEMBER(RfrDocItem, text)
   REGISTER_STRING_MEMBER(RfrDocItem, name)
   REGISTER_ULONG_MEMBER(RfrDocItem, flags)
END_TYPE_REFLECTION(RfrDocItem)

BEGIN_TYPE_REFLECTION(RfrDoc)
   REGISTER_STRING_MEMBER(RfrDoc, id)
   REGISTER_TIMESTAMP_MEMBER(RfrDoc, created)
   //REGISTER_ULONG_MEMBER(RfrDoc, unitCode)
   REGISTER_ULONG_MEMBER(RfrDoc, flags)
   REGISTER_COLLECTION_MEMBER(RfrDoc, items, RfrDocItem)
   REGISTER_STRING_MEMBER(RfrDoc, remark)
END_TYPE_REFLECTION(RfrDoc)

BEGIN_TYPE_REFLECTION(RfrItem)
   REGISTER_STRING_MEMBER(RfrItem, name)
END_TYPE_REFLECTION(RfrItem)

static ListFormData::Header listDocHeader[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 50 },
};

struct DocRfrgData : public ListFormData
{
   DocRfrgData(DocRfrgImpl *doc, bool rdl) { this->doc = doc; retToDocList = rdl; }
   ~DocRfrgData() { delete doc; }

   virtual int Count() const { return doc->items.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   virtual bool Selecting(int index);
   virtual bool Editing(int index);
   virtual const Header *GetHeader() const { return listDocHeader; }
   virtual int ColumnsCount() const { return sizeof(listDocHeader)/sizeof(listDocHeader[0]); }

   virtual const DataReflector& DataType() const { return RfrItem().GetType(); }

   bool IsChecked(int index) const;
   void Send();

   DocRfrgImpl *doc;
   bool retToDocList;

   mutable std::wstring text;
};

class RfrgDocForm : public ListForm
{
public:
   RfrgDocForm() {}

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(RfrgDocForm)
     //COMMAND_HANDLER(IDC_UNIT_TEXT, CBN_SELCHANGE, UnitChanged)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   DECLARE_FORM(RfrgDocForm, IDD_RFRG_PAY)

   virtual DWORD GetMenuID() const { return IDD_RFRG_PAY; }
   virtual DWORD GetResourceID() const { return IDD_RFRG_PAY; }
   virtual DWORD GetMenuBarID() const { return IDD_RFRG_PAY; }

   virtual void UpdateLayout(bool forceRecalc);
protected:
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   //LRESULT UnitChanged(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled);

   virtual LRESULT SetCellInfo(LPNMHDR hdr);

protected:
};


IMPLEMENT_FORM(RfrgDocForm);

bool DocRfrgData::Get(IReflectableData* data, int index) const
{
   if( index < 0 || index >= (int)doc->items.size() )
      return false;

   const RfrDocItem& i = doc->items.at(index);

   text = i.name;
   ((RfrItem*)data)->name = (wchar_t*)text.c_str();

   return true;
}

bool DocRfrgData::IsChecked(int index) const
{
   if( index < 0 || index >= (int)doc->items.size() )
      return false;

   return ((doc->items.at(index).flags & 1) != 0);
}

void DocRfrgData::Send()
{
   if(SendDocument(doc, docTypeManager.GetDocType(dtRfrDoc)))
   {
      doc->flags |= ofExported;
      doc->Write();
   }
}

class NoteDlg : public BaseDialog
{
public:
   NoteDlg(const wchar_t* text) : BaseDialog(IDC_REMARK) { this->text = text; }

   BEGIN_MSG_MAP(NoteDlg)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)

      COMMAND_ID_HANDLER(IDCANCEL, Close)
      COMMAND_ID_HANDLER(IDOK, Close)

      CHAIN_MSG_MAP(BaseDialog)
   END_MSG_MAP()

   const wchar_t* Text() const { return text.c_str(); }
protected:
   std::wstring text;

   LRESULT OnInitDialog(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

   LRESULT Close(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
};

LRESULT NoteDlg::OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

   int bwdh = 0, btop = hgh, pbottom = nTitleHeight + 2;
   CRect bounds;
   if( GetDlgItemRect(bounds, IDOK) )
   {
      btop = hgh - bounds.Height() - offset;
      bwdh = bounds.Width();
      GetDlgItem(IDOK).MoveWindow(offset, btop, bwdh, bounds.Height(), FALSE);      
   }

   if( GetDlgItemRect(bounds, IDCANCEL) )
   {
      if( btop == hgh )
         btop = hgh - bounds.Height() - offset;
      GetDlgItem(IDCANCEL).MoveWindow(bwdh + 3*offset, btop, 
         bounds.Width(), bounds.Height(), FALSE);
   }

   GetDlgItem(IDC_TEXT).MoveWindow(offset, pbottom+offset, wdh-2*offset, btop - pbottom - 2 * offset, FALSE);

   return 0;
}

LRESULT NoteDlg::OnInitDialog(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
   bHandled = FALSE;

   GetDlgItem(IDC_TEXT).SetWindowText(text.c_str());
   return FALSE;
}

LRESULT NoteDlg::Close(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   EndDialog(m_hWnd, wID);
   SHSipPreference(m_hWnd, SIP_DOWN);

   CWindow w(GetDlgItem(IDC_TEXT));
   int wl = w.GetWindowTextLength() + 1;
   wchar_t *buf = (wchar_t*)alloca(wl * sizeof(wchar_t));
   w.GetWindowText(buf, wl);

   text = buf;
   return FALSE;
}

bool DocRfrgData::Editing(int index)
{
   if( index <  0 || index >= (int)doc->items.size() )
      return false;

   RfrDocItem& item = doc->items.at(index);
   NoteDlg dlg(item.text);

   if( dlg.DoModal() == IDOK && doc->IsDirty() )
   {
      item.text = doc->holder.Add(dlg.Text());
      doc->Write();
   }
   return false;
}

bool DocRfrgData::Selecting(int index)
{
   if( doc->IsDirty() == false || index < 0 || index >= (int)doc->items.size() )
      return false;

   RfrDocItem& i = doc->items.at(index);
   if( (i.flags & 1) != 0 )
      i.flags &= (~1);
   else
      i.flags |= 1;

   doc->Write();
   return true;
}

bool RfrgDocForm::SetData(IFormData *_data)
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

   //OrgImpl o;
   //o.id = ((DocRfrgData*)data)->doc->id;
   //o.Read();

   //CComboBox cb(GetDlgItem(IDC_UNIT_TEXT));
   //vector_t<OrgUnit>::const_iterator oi = o.units.begin();
   //for( ; oi != o.units.end(); oi++ )
   //{
   //   int index = cb.AddString(oi->name);
   //   if( ((DocRfrgData*)data)->doc->unitCode == oi->id )
   //      cb.SetCurSel(index);

   //   cb.SetItemData(index, (LPARAM)oi->id);
   //}
   return true;
}

//LRESULT RfrgDocForm::UnitChanged(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
//{
//   if( ((DocRfrgData*)data)->doc->IsDirty() )
//   {
//      CComboBox cb(GetDlgItem(IDC_UNIT_TEXT));
//      int curSel = cb.GetCurSel();
//      ((DocRfrgData*)data)->doc->unitCode = cb.GetItemData(curSel);
//      ((DocRfrgData*)data)->doc->Write();
//   }
//   return 0;
//}

void RfrgDocForm::UpdateLayout(bool forceRecalc)
{
   //CRect bounds, rc;
   //
   //GetClientRect(bounds);

   //CWindow wnd(GetDlgItem(IDC_UNIT_TEXT));
   //wnd.GetWindowRect(rc);
   //ScreenToClient(rc);
   //wnd.MoveWindow(rc.left, rc.top, bounds.right - rc.left - 2, rc.Height());

   SetListLayout(forceRecalc, 0);
}

LRESULT RfrgDocForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   if( !CreateNextDoc(((DocRfrgData*)data)->doc->id) )
   {
      if( ((DocRfrgData*)data)->retToDocList )
         OpenListDoc();
      else
         OpenOrgDocs(((DocRfrgData*)data)->doc->id, dtRfrDoc);
   }
   return 0;
}

LRESULT RfrgDocForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   ((DocRfrgData*)data)->Send();
   return 0;
}

LRESULT RfrgDocForm::SetCellInfo(LPNMHDR hdr)
{
   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( ListForm::SetCellInfo(hdr) == FALSE )
   {
      if( di->item.mask & LVIF_IMAGE )
         di->item.iImage = 0;
      return FALSE;
   }

   int index = di->item.iItem;
   if( di->item.mask & LVIF_IMAGE )
      di->item.iImage = ((DocRfrgData*)data)->IsChecked(index) ? 1 : 0;

   return TRUE;
}

void OpenRfrgDoc(DocRfrgImpl* document, bool retToDocList)
{
   DocRfrgData *data = new DocRfrgData(document, retToDocList);
   _Module.GetFrame()->Load(IDD_RFRG_PAY, data);
}

//
// ----------------------- DocRfrgImpl
//
void DocRfrgImpl::EditDocument(UINT retForm)
{
   OpenRfrgDoc(this, (retForm == IDD_ORDER_LIST));
}

bool DocRfrgImpl::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) )
   {
      EditDocument(0);
      return true;
   }

   return false;
}

const wchar_t* DocRfrgImpl::Description() const
{
   //if( IsProceeded() ) return (*podRemark != L'\0') ? podRemark : L"в обработке";
   return (flags & ofExported) ? L"отправлен" : L"";
}

bool DocRfrgImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   //if( rid == NO_ROWID ) return false;

   if( reverse )
   {
      if( flags & ofExported ) flags &= (~ofExported);
      else flags |= ofExported;
   } else
      flags |= ofExported;
   return (updateTable == NULL) ? true : updateTable->Update(*this, L"flags", rid);
}

bool DocRfrgImpl::Init(const ROWID &orgID)
{
   OrgImpl org;
   org.Read(orgID);
   id = holder.Add(org.id);

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &created);

   flags = 0;
   remark = L"";

   vector_t<Refrigerator>::const_iterator oi = org.refrigerators.begin();
   for( ; oi != org.refrigerators.end(); oi++ )
   {
      RfrDocItem item;
      item.name = holder.Add(oi->name);
      item.id = holder.Add(oi->id);
      item.flags = 0;
      item.text = L"";

      items.push_back(item);
   }

   //if( org.units.size() > 0 )
   //   unitCode = org.units.front().id;
   //else
   //   unitCode = 0;

   return true;
}

bool DocRfrgImpl::CanRemove() const
{
   return MessageBox(GetActiveWindow(), L"Удалить документ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES;
}

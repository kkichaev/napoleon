/*
 * Copyright (C), 2007-2011, Денис Мосягин
 *
 * Обещанный платеж
 *
 *  ert   19/04/2011   creating
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

#include <Add.h>
//#include "NumInput.h"
#include "NplConfig.h"
#include <ObjImpl.h>

BEGIN_TYPE_REFLECTION(PPayItem)
   REGISTER_STRING_MEMBER(PPayItem, number)
   REGISTER_FILETIME_MEMBER(PPayItem, date)
END_TYPE_REFLECTION(PPayItem)

BEGIN_TYPE_REFLECTION(FocusedItems)
   REGISTER_STRING_MEMBER(FocusedItems, id)
END_TYPE_REFLECTION(FocusedItems)

BEGIN_TYPE_REFLECTION(PPayDoc)
   REGISTER_STRING_MEMBER(PPayDoc, id)
   REGISTER_FILETIME_MEMBER(PPayDoc, date)
   REGISTER_ULONG_SCALE_MEMBER(PPayDoc, sum, SUM_SCALE)
   REGISTER_STRING_MEMBER(PPayDoc, remark)
   REGISTER_ULONG_MEMBER(PPayDoc, flags)
   REGISTER_COLLECTION_MEMBER(PPayDoc, items, PPayItem)
END_TYPE_REFLECTION(PPayDoc)

extern ListFormData::Header payHeader[];

struct PPayData : public ListFormData
{
   PPayData(PPay *doc, bool rdl);
   ~PPayData() { delete doc; }

   virtual int Count() const { return payDocs.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   virtual bool Selecting(int index);
   virtual const Header *GetHeader() const { return payHeader; }
   virtual int ColumnsCount() const { return 4; }

   virtual const DataReflector& DataType() const { return PayItem().GetType(); }
   COLORREF GetItemColor(int index) const;

   bool IsDocInPay(int index) const;

   PPay *doc;
   bool retToDocList;

   mutable PaymentImpl p;
   std::vector<ROWID> payDocs;
   mutable std::wstring sum, date, num, type;
};

PPayData::PPayData(PPay *doc, bool rdl) : retToDocList(rdl)
{
   this->doc = doc;

   std::wstring sql(L" WHERE id='");
   sql += doc->id;
   sql += L"' ORDER BY date";

   SQLTable t(p.Name());
   t.RIDList(&payDocs, sql.c_str());
}

bool PPayData::IsDocInPay(int index) const
{
   if( index >= (int) payDocs.size() ) 
      return false;

   p.Read(payDocs[index]);
   vector_t<PPayItem>::const_iterator i = doc->items.begin();
   for( ; i != doc->items.end(); i++ )
   {
      if( wcscmp(i->number, p.number) == 0 )
         break;
   }

   return (i != doc->items.end());
}

COLORREF PPayData::GetItemColor(int index) const
{
   if( index >= (int) payDocs.size() ) 
      return 0;

   p.Read(payDocs[index]);
   return p.color;
}

bool PPayData::Get(IReflectableData* data, int index) const
{
   if( index >= (int) payDocs.size() ) return false;

   p.Read(payDocs[index]);

   wchar_t buf[50], src[40];

   num = p.number;
   type = p.type;

   SYSTEMTIME st;
   FileTimeToSystemTime(&p.dlvDate, &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));

   date = buf;
   FileTimeToSystemTime(&p.date, &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));

   date += L'\n';
   date += buf;

   long sumV = p.sum;
   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
   sum = buf;
   if( p.delay != 0 )
   {
      wsprintf(buf, L"%d", p.delay);
      sum += L'\n';
      sum += buf;
   }

   ((PayItem*)data)->sum = sum.c_str();
   ((PayItem*)data)->flags = num.c_str();
   ((PayItem*)data)->date = date.c_str();
   ((PayItem*)data)->type = type.c_str();

   return true;
}

bool PPayData::Selecting(int index)
{
   if( index >= (int) payDocs.size() || !doc->IsDirty() ) return false;
   p.Read(payDocs[index]);

   bool done = false;
   vector_t<PPayItem>::iterator i = doc->items.begin();
   for( ; i != doc->items.end(); i++ )
   {
      if( wcscmp(i->number, p.number) == 0 )
      {
         doc->items.erase(i);
         done = true;
         break;
      }
   }

   if( !done ) // add doc
   {
      PPayItem pi;
      pi.number = doc->holder.Add(p.number);
      pi.date = p.dlvDate;
      doc->items.push_back(pi);
   }

   doc->Write();
   return true;
}

class PPayForm : public ListForm, public CCustomDraw<PPayForm>
{
public:
   PPayForm() {}

   virtual DWORD GetResourceID() const { return IDD_PPAY; }
   virtual DWORD GetMenuBarID() const { return IDD_PROXY; }

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   DECLARE_FORM(PPayForm, IDD_PPAY)

   BEGIN_MSG_MAP(PPayForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      CHAIN_MSG_MAP(CCustomDraw<PPayForm>)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/)
   {
      return CDRF_NOTIFYITEMDRAW;
   }

   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
   {
      NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
      lvcd->clrTextBk = ((PPayData*)data)->GetItemColor(lvcd->nmcd.dwItemSpec);
      if( lvcd->clrTextBk == 0 ) lvcd->clrTextBk = 0xFFFFFF;
      return CDRF_NOTIFYITEMDRAW;
   }

   virtual int ImageListID(ListViewMultiLine *list) const { return ((GetSystemMetrics(SM_CXSMICON) == 16) ? IDB_CHECKED : IDB_CHECKED32); }

protected:
   virtual LRESULT SetCellInfo(LPNMHDR hdr);

protected:
   //void MoveChildWindow(UINT id , int top);
   void LoadDataFromForm();
};

IMPLEMENT_FORM(PPayForm)

LRESULT PPayForm::SetCellInfo(LPNMHDR hdr)
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
   {
      di->item.iImage = (((PPayData*)data)->IsDocInPay(index) ? 1 : 0);
   }
   return TRUE;
}

void PPayForm::LoadDataFromForm()
{
   CWindow text(GetDlgItem(IDC_REMARK));

   int len = text.GetWindowTextLength() + 1;
   wchar_t *txt = (wchar_t*)alloca(len * sizeof(wchar_t));
   text.GetWindowText(txt, len);

   ((PPayData*)data)->doc->remark = ((PPayData*)data)->doc->holder.Add(txt);

   wchar_t buf[20];
   GetDlgItem(IDC_SUM).GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
   DWORD sum = GetValue(buf, SUM_SCALE);
   bool sumChanged = (sum != ((PPayData*)data)->doc->sum);
   ((PPayData*)data)->doc->sum = sum;

   SYSTEMTIME st;
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &((PPayData*)data)->doc->date);

   ((PPayData*)data)->doc->Write();
   if( sumChanged )
      docTypeManager.SumChanged(dtPPay, ((PPayData*)data)->doc->id);
}

LRESULT PPayForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (((PPayData*)data)->doc->flags & ofExported) == 0 )
      LoadDataFromForm();

   if( !CreateNextDoc(((PPayData*)data)->doc->id) )
   {
      if( ((PPayData*)data)->retToDocList )
         OpenListDoc();
      else
         OpenOrgDocs(((PPayData*)data)->doc->id, dtPPay);
   }
   return 0;
}

LRESULT PPayForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (((PPayData*)data)->doc->flags & ofExported) == 0 )
      LoadDataFromForm();

   if( SendDocument(((PPayData*)data)->doc, docTypeManager.GetDocType(dtPPay), L"Документ отправлен") )
   {
      ((PPayData*)data)->doc->ClearDirty(NULL, false);
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
      listCtrl.EnableWindow(TRUE);
   }
   return 0;
}

bool PPayForm::SetData(IFormData *_data)
{
   if( !ListForm::SetDataEx(_data, 2) )
      return false;

   if( (((PPayData*)data)->doc->flags & ofExported) != 0 )
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
   listCtrl.EnableWindow(TRUE);

   std::wstring val;
   NapoleonConfig cfg;
   cfg.ReadValue(&val, L"ОбещанныйПлатеж");

   CComboBox cbBox(GetDlgItem(IDC_REMARK));

   std::wstring::size_type sp = 0;
   while(true)
   {
      std::wstring::size_type ep = val.find_first_of(SEP_SYM, sp);
      std::wstring cval;

      if( ep == std::wstring::npos ) cval = val.substr(sp, ep);
      else cval = val.substr(sp, ep - sp);

      int index = cbBox.AddString(cval.c_str());
      if( cval.compare(((PPayData*)data)->doc->remark) == 0 )
         cbBox.SetCurSel(index);

      if( ep == std::wstring::npos ) break;
      sp = ep + 1;
   }

   SetDlgItemText(IDC_REMARK, ((PPayData*)data)->doc->remark);

   OrgImpl oi;
   oi.id = ((PPayData*)data)->doc->id;
   oi.Read();
   SetDlgItemText(IDC_ORG_TITLE, oi.name);

   wchar_t buf[20], src[20];
   long value = (long)((PPayData*)data)->doc->sum;
   ConvertScaling(src, value, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % SUM_SCALE, SUM_SCALE, false);
   CEdit sum(GetDlgItem(IDC_SUM));
   sum.SetWindowText(buf);
   sum.SetSel(0, -1);

   SYSTEMTIME st;
   FileTimeToSystemTime(&((PPayData*)data)->doc->date, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   UpdateLayout(false);
   return true;
}

//void PPayForm::MoveChildWindow(UINT id, int top)
//{
//   CRect rc;
//   CWindow w(GetDlgItem(id));
//   w.GetWindowRect(rc);
//   ScreenToClient(rc);
//   w.MoveWindow(rc.left, top, rc.Width(), rc.Height(), FALSE);
//}
//
void PPayForm::UpdateLayout(bool forceRecalc)
{
   CRect rc;
   GetClientRect(rc);

   CStatic title(GetDlgItem(IDC_ORG_TITLE));

   CRect bounds;
   bounds.top = 2;
   bounds.left = 2;
   bounds.right = rc.right-2;

   CalcTextHeight(title.m_hWnd, bounds);
   title.MoveWindow(bounds.left, bounds.top, rc.right - bounds.left, bounds.Height(), FALSE);

   GetDlgItem(IDC_REMARK).GetWindowRect(bounds);
   ScreenToClient(bounds);

   SetListLayout(forceRecalc, bounds.bottom + 2);
}

void OpenPPay(PPay* document, bool retToDocList)
{
   PPayData *data = new PPayData(document, retToDocList);
   _Module.GetFrame()->Load(IDD_PPAY, data);
}

void PPay::EditDocument(UINT retForm)
{
   OpenPPay(this, (retForm == IDD_ORDER_LIST));
}

bool PPay::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) )
   {
      EditDocument(0);
      return true;
   }

   return false;
}

const wchar_t* PPay::Description() const
{
   return (flags & ofExported) ? L"отправлен" : L"";
}

bool PPay::ClearDirty(SQLTable *updateTable, bool reverse)
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

bool PPay::Init(const ROWID &orgID)
{
   OrgImpl org;
   org.Read(orgID);
   id = holder.Add(org.id);

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &date);

   if( !Read() )
   {
      flags = 0;
      remark = L"";
      sum = 0;
   }

   return true;
}

bool PPay::CanRemove() const
{
   return MessageBox(GetActiveWindow(), L"Удалить документ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES;
}

/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Доверенность
 *
 *  ert   07/09/2009   creating
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

#include <Progress.h>

#include "ObjImpl.h"
#include "FormEntries.h"
#include "InitDoc.h"
#include "StdFuncs.h"
#include <NplConfig.h>

wchar_t dtProxy[] = L"Довер.";

void OpenProxy(ProxyImpl *visit, bool retToDocList);

struct ProxyData : public IFormData
{
   ProxyData(ProxyImpl *proxy, bool rdl) : retToDocList(rdl) { this->proxy = proxy; }
   ~ProxyData() { delete proxy; }

   ProxyImpl *proxy;
   bool retToDocList;
};

class ProxyForm : public BaseForm
{
public:
   ProxyForm() : data(NULL) {}
   ~ProxyForm();

   virtual DWORD GetResourceID() const { return IDD_PROXY_ADD; }
   virtual DWORD GetMenuBarID() const { return IDD_PROXY; }

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   DECLARE_FORM(ProxyForm, IDD_PROXY)

   BEGIN_MSG_MAP(ProxyForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      MESSAGE_HANDLER(WM_SETTINGCHANGE, CheckSIP)
      CHAIN_MSG_MAP(BaseForm)
   END_MSG_MAP()

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
   {
      if( IsSquareScreen() == false && wParam == SPI_SETSIPINFO )
      {
         SIPINFO si;
         memset (&si, 0, sizeof (si));
         si.cbSize = sizeof (si);

         if (SipGetInfo(&si)) 
         {
            CRect rc, parc;
            GetWindowRect(rc);
            GetParent().GetClientRect(parc);
            GetParent().ScreenToClient(rc);
            rc.bottom = si.rcVisibleDesktop.bottom - si.rcVisibleDesktop.top;
            if( rc.bottom > parc.bottom ) rc.bottom = parc.bottom;

            MoveWindow(rc.left, rc.top, rc.Width(), rc.Height(), TRUE);

            UpdateLayout(false);
         }
      }
      return 0;
   }

protected:
   ProxyData *data;
   CMenuBarCtrl menuBar;

protected:
   void MoveChildWindow(UINT id , int top);
   void LoadDataFromForm();
};

//
//----------------------------------- ProxyType -------------------------------------------
//
struct ProxyFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new ProxyImpl(); }
   virtual void Free(IDocument* document) const { delete (ProxyImpl*)document; }
} proxyFactory;

ProxyType::ProxyType() : DocType(dtProxy, &proxyFactory, dtHaveSum)
{
}

//
//----------------------------------- ProxyImpl -------------------------------------------
//
const wchar_t* ProxyImpl::Description() const
{
   return (flags & ofExported) ? L"отправлен" : L"";
}

void ProxyImpl::EditDocument(UINT retForm)
{
   OpenProxy(this, (retForm == IDD_ORDER_LIST));
}

bool ProxyImpl::ClearDirty(SQLTable *updateTable, bool reverse)
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

bool ProxyImpl::Init(const ROWID &orgID)
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
      type = L"";
   }

   return true;
}

bool ProxyImpl::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) )
   {
      OpenProxy(this, false);
      return true;
   }

   return false;
}

bool ProxyImpl::CanRemove() const
{
   return MessageBox(GetActiveWindow(), L"Удалить документ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES;
}

//
//------------------------------------- ProxyForm ---------------------------------
//

IMPLEMENT_FORM(ProxyForm)

void ProxyForm::LoadDataFromForm()
{
   CWindow text(GetDlgItem(IDC_REMARK));

   int len = text.GetWindowTextLength() + 1;
   wchar_t *txt = (wchar_t*)alloca(len * sizeof(wchar_t));
   text.GetWindowText(txt, len);

   data->proxy->remark = data->proxy->holder.Add(txt);

   wchar_t buf[20];
   GetDlgItem(IDC_SUM).GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
   DWORD sum = GetValue(buf, SUM_SCALE);
   bool sumChanged = (sum != data->proxy->sum);
   data->proxy->sum = sum;

   SYSTEMTIME st;
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &data->proxy->date);

   CWindow tcause(GetDlgItem(IDC_COST_TYPE));
   len = tcause.GetWindowTextLength() + 1;
   wchar_t *tc = (wchar_t*)alloca(len * sizeof(wchar_t));
   tcause.GetWindowText(tc, len);
   data->proxy->type = data->proxy->holder.Add(tc);

   data->proxy->Write();
   if( sumChanged )
      docTypeManager.SumChanged(dtProxy, data->proxy->id);
}

LRESULT ProxyForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (data->proxy->flags & ofExported) == 0 )
      LoadDataFromForm();

   if( !CreateNextDoc(data->proxy->id) )
   {
      if( data->retToDocList )
         OpenListDoc();
      else
         OpenOrgDocs(data->proxy->id, dtProxy);
   }
   return 0;
}

LRESULT ProxyForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (data->proxy->flags & ofExported) == 0 )
      LoadDataFromForm();

   if( SendDocument(data->proxy, docTypeManager.GetDocType(dtProxy), L"Доверенность отправлена") )
   {
      data->proxy->ClearDirty(NULL, false);
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
   }
   /*
   ProgressWindow pw;
   std::wstring answer;

   pw.CreateSTDWindow(m_hWnd);
   long ec = _Module.SendDocument(data->proxy, docTypeManager.GetDocType(dtProxy), &answer, &pw);
   pw.DestroyWindow();

   if( ec )
   {
      _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при передаче:\n");
   } else
   {
      data->proxy->flags |= ofExported;
      data->proxy->Update(L"flags");
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);

      MessageBox(L"Доверенность отправлена", L"Подтверждение", MB_OK|MB_ICONINFORMATION);
   }
*/
   return 0;
}

ProxyForm::~ProxyForm()
{
   delete data;
}

bool ProxyForm::SetData(IFormData *_data)
{
   data = (ProxyData*)_data;

   if( (data->proxy->flags & ofExported) != 0 )
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);

   SetDlgItemText(IDC_REMARK, data->proxy->remark);

   OrgImpl oi;
   oi.id = data->proxy->id;
   oi.Read();
   SetDlgItemText(IDC_ORG_TITLE, oi.name);

   wchar_t buf[20], src[20];
   long value = (long)data->proxy->sum;
   ConvertScaling(src, value, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % SUM_SCALE, SUM_SCALE, false);
   SetDlgItemText(IDC_SUM, buf);

   NapoleonConfig config;
   std::wstring cval;
   config.ReadValue(&cval, L"ТипПлатежа");
   CComboBox cbBox(GetDlgItem(IDC_COST_TYPE));

   const wchar_t *psrc = data->proxy->type;
   std::wstring::size_type sp = 0;
   for( int i=0; ; i++ )
   {
      std::wstring::size_type ep = cval.find_first_of(SEP_SYM, sp);
      const std::wstring& vl = (ep == std::wstring::npos) ? cval.substr(sp, ep) : cval.substr(sp, ep - sp);
      int index = cbBox.AddString(vl.c_str());

      if( wcscmp(vl.c_str(), psrc) == 0 )
         cbBox.SetCurSel(index);

      if( ep == std::wstring::npos ) break;
      sp = ep + 1;
   }
   cbBox.SetCurSel(value);

   SYSTEMTIME st;
   FileTimeToSystemTime(&data->proxy->date, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   UpdateLayout(false);
   return true;
}

void ProxyForm::MoveChildWindow(UINT id, int top)
{
   CRect rc;
   CWindow w(GetDlgItem(id));
   w.GetWindowRect(rc);
   ScreenToClient(rc);
   w.MoveWindow(rc.left, top, rc.Width(), rc.Height(), FALSE);
}

void ProxyForm::UpdateLayout(bool forceRecalc)
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

   int top = bounds.bottom + 10;
   MoveChildWindow(IDC_DATE_LABEL, top);
   MoveChildWindow(IDC_ORDER_DATE, top - 2);
   MoveChildWindow(IDC_SUM_LABEL, top);
   MoveChildWindow(IDC_SUM, top - 2);

   top += 22;
   MoveChildWindow(IDC_COST_LABEL, top);
   MoveChildWindow(IDC_COST_TYPE, top - 2);

   top += 22;
   CWindow w(GetDlgItem(IDC_REMARK));
   w.MoveWindow(2, top, rc.right - 4, rc.bottom - 2 - top, FALSE);
}

void OpenProxy(ProxyImpl *proxy, bool retToDocList)
{
   ProxyData *data = new ProxyData(proxy, retToDocList);
   _Module.GetFrame()->Load(IDD_PROXY, data);
}

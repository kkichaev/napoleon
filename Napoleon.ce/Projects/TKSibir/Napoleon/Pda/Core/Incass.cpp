/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Инкассация
 *
 *  ert   17/12/2010   creating
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
#include "Incass.h"
#include "FormEntries.h"
#include "InitDoc.h"
#include "StdFuncs.h"
#include "NumInput.h"

#include <ObjExchange.h>

wchar_t dtIncass[] = L"Инкасс.";

void OpenIncass(IncassImpl *visit, bool retToDocList);

struct IncassData : public IFormData
{
   IncassData(IncassImpl *doc, bool rdl) : retToDocList(rdl) { this->doc = doc; }
   ~IncassData() { delete doc; }

   IncassImpl *doc;
   bool retToDocList;
};

class IncassForm : public BaseForm
{
public:
   IncassForm() : data(NULL), numInput(IDC_SUM) {}
   ~IncassForm();

   virtual DWORD GetResourceID() const { return IDD_INCASS; }
   virtual DWORD GetMenuBarID() const { return IDD_INCASS; }

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   DECLARE_FORM(IncassForm, IDD_INCASS)

   BEGIN_MSG_MAP(IncassForm)
      NUM_INPUT_HANDLER(numInput)
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
   IncassData *data;
   CMenuBarCtrl menuBar;
   NumInput numInput;

protected:
   void MoveChildWindow(UINT id , int top);
   void LoadDataFromForm();
};

BEGIN_TYPE_REFLECTION(Incass)
   REGISTER_STRING_MEMBER(Incass, id)
   REGISTER_FILETIME_MEMBER(Incass, date)
   REGISTER_FILETIME_MEMBER(Incass, payDate)
   REGISTER_ULONG_SCALE_MEMBER(Incass, sum, SUM_SCALE)
   REGISTER_STRING_MEMBER(Incass, remark)
   REGISTER_ULONG_MEMBER(Incass, flags)
END_TYPE_REFLECTION(Incass)

//
//----------------------------------- IncassType -------------------------------------------
//
struct IncassFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new IncassImpl(); }
   virtual void Free(IDocument* document) const { delete (IncassImpl*)document; }
} IncassFactory;

IncassType::IncassType() : DocType(dtIncass, &IncassFactory, dtHaveSum)
{
}

//
//----------------------------------- IncassImpl -------------------------------------------
//
const wchar_t* IncassImpl::Description() const
{
   return (flags & ofExported) ? L"отправлен" : L"";
}

void IncassImpl::EditDocument(UINT retForm)
{
   OpenIncass(this, (retForm == IDD_ORDER_LIST));
}

bool IncassImpl::ClearDirty(SQLTable *updateTable, bool reverse)
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

bool IncassImpl::Init(const ROWID &orgID)
{
   OrgImpl org;
   org.Read(orgID);
   id = holder.Add(org.id);

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &date);
   payDate = date;

   if( !Read() )
   {
      flags = 0;
      remark = L"";
      sum = 0;
   }

   return true;
}

bool IncassImpl::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) )
   {
      OpenIncass(this, false);
      return true;
   }

   return false;
}

bool IncassImpl::CanRemove() const
{
   return MessageBox(GetActiveWindow(), L"Удалить доверенность?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES;
}

//
//------------------------------------- IncassForm ---------------------------------
//

IMPLEMENT_FORM(IncassForm)

void IncassForm::LoadDataFromForm()
{
   //CWindow text(GetDlgItem(IDC_REMARK));

   //int len = text.GetWindowTextLength() + 1;
   //wchar_t *txt = (wchar_t*)alloca(len * sizeof(wchar_t));
   //text.GetWindowText(txt, len);

   //data->doc->remark = data->doc->holder.Add(txt);

   wchar_t buf[20];
   CEdit sumCtrl(GetDlgItem(IDC_SUM));
   sumCtrl.GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
   DWORD sum = GetValue(buf, SUM_SCALE);
   bool sumChanged = (sum != data->doc->sum);
   data->doc->sum = sum;

   SYSTEMTIME st;
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &data->doc->payDate);

   data->doc->Write();
   if( sumChanged )
      docTypeManager.SumChanged(dtIncass, data->doc->id);
}

LRESULT IncassForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (data->doc->flags & ofExported) == 0 )
      LoadDataFromForm();

   if( !CreateNextDoc(data->doc->id) )
   {
      if( data->retToDocList )
         OpenListDoc();
      else
         OpenOrgDocs(data->doc->id, dtIncass);
   }
   return 0;
}

LRESULT IncassForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (data->doc->flags & ofExported) == 0 )
      LoadDataFromForm();

   Preference p;
   p.Load();

   if( (p.flags & opfSendOnLine) == 0 )
   {
      if( SendDocument(data->doc, docTypeManager.GetDocType(dtIncass), L"Доверенность отправлена") )
         EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
      return 0;
   }

   bool ret = false;
   std::wstring answer;
   ServObject<IncassImpl> o;

   CopyData(&o, *data->doc);
   int res = ObjectExchange(&o, WRITE_OBJECTS, &answer);
   const wchar_t* msg = NULL;
   std::wstring buf;
   const wchar_t* title = NULL;
   DWORD addFlag = 0;

   if( res != 0 )
   {
      _Module.ShowErrorBox(res, answer.c_str(), L"Ошибка: ");
   } else
   {
      if( o.servResult == RESULT_FAIL )
      {
         title = L"Ошибка проведения";
         buf = o.servResponse;
         msg = buf.c_str();
         addFlag = MB_ICONSTOP;
      } else
      {
         CopyData(data->doc, o);

         title = L"Информация";
         addFlag = MB_ICONINFORMATION;

         data->doc->ClearDirty(NULL, false);
         data->doc->Write();

         msg = L"Документ успешно проведен";
         EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
      }
   }

   if( *o.servResponse != L'\0' )
      msg = o.servResponse;

   if( msg && title )
      MessageBox(msg, title, MB_OK | addFlag);


   return 0;
}

IncassForm::~IncassForm()
{
   delete data;
}

bool IncassForm::SetData(IFormData *_data)
{
   data = (IncassData*)_data;

   if( (data->doc->flags & ofExported) != 0 )
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);

   //SetDlgItemText(IDC_REMARK, data->doc->remark);

   OrgImpl oi;
   oi.id = data->doc->id;
   oi.Read();
   SetDlgItemText(IDC_ORG_TITLE, oi.name);

   wchar_t buf[20], src[20];
   long value = (long)data->doc->sum;
   ConvertScaling(src, value, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % SUM_SCALE, SUM_SCALE, false);
   CEdit sumCtrl(GetDlgItem(IDC_SUM));
   sumCtrl.SetWindowText(buf);
   sumCtrl.SetSelAll();

   SYSTEMTIME st;
   FileTimeToSystemTime(&data->doc->payDate, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   UpdateLayout(false);
   return true;
}

void IncassForm::MoveChildWindow(UINT id, int top)
{
   CRect rc;
   CWindow w(GetDlgItem(id));
   w.GetWindowRect(rc);
   ScreenToClient(rc);
   w.MoveWindow(rc.left, top, rc.Width(), rc.Height(), FALSE);
}

void IncassForm::UpdateLayout(bool forceRecalc)
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

   //int top = bounds.bottom + 10;
   //MoveChildWindow(IDC_DATE_LABEL, top);
   //MoveChildWindow(IDC_ORDER_DATE, top - 2);
   //MoveChildWindow(IDC_SUM_LABEL, top);
   //MoveChildWindow(IDC_SUM, top - 2);

   //top += 22;
   //CWindow w(GetDlgItem(IDC_REMARK));
   //w.MoveWindow(2, top, rc.right - 4, rc.bottom - 2 - top, FALSE);
}

void OpenIncass(IncassImpl *Incass, bool retToDocList)
{
   IncassData *data = new IncassData(Incass, retToDocList);
   _Module.GetFrame()->Load(IDD_INCASS, data);
}

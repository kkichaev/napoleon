/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Реализация функций оплаты при продаже
 *
 *  ert   27/07/20107   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "ObjImpl.h"
#include "DocImpl.h"
#include "FormEntries.h"
#include <StdFuncs.h>
#include <NapoleonRes.h>
#include "Progress.h"
#include <BaseDialog.h>
#include "InitDoc.h"
#include "NumInput.h"

#ifdef GPS_POS
#include <OrgDocs.h>
#include <FormEntries.h>
#endif

#include "DoPrint.h"

void OpenPayment(PaymentImpl* p, bool retToDocList);

struct PaymentData : public IFormData
{
   PaymentData(PaymentImpl *pay, bool retToDocList);
   ~PaymentData();

   void Print(IProgressIndicator *pi);

   PaymentImpl *doc;
   bool RetToDocList() const { return retToDocList; }

   bool retToDocList;
};

void PaymentData::Print(IProgressIndicator *pi)
{
   std::wstring fname;
   _Module.MakeFileName(&fname, L"PKO.xml");

   PaymentPrint dp(*doc);
   PaymentSource rs(&dp);
   PC canceller;

   if( ::DoPrint(fname.c_str(), &rs, pi, &canceller) == true )
   {
   }
}

class PaymentForm : public BaseForm
{
public:
   PaymentForm();
   ~PaymentForm();

   virtual bool SetData(IFormData *_data);

   DECLARE_FORM(PaymentForm, IDD_PAYMENT)

   BEGIN_MSG_MAP(PaymentForm)
      COMMAND_ID_HANDLER(IDC_BACK, Closing)
      COMMAND_ID_HANDLER(IDC_PRINT, Print)
      COMMAND_CODE_HANDLER(EN_SETFOCUS, OnSetFocus)
      NUM_INPUT_HANDLER(numInput)
      MESSAGE_HANDLER(WM_PAINT, DoPaint)
   END_MSG_MAP()

   LRESULT OnSetFocus(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
   LRESULT Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT DoPaint(UINT uMsg, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
   LRESULT Print(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   virtual void UpdateLayout(bool forceRecalc);

protected:
   void Save();

protected:
   PaymentData *data;
   CMenuBarCtrl menuBar;
   SumLabel sumLabel;
   NumInput numInput;
   StringHolder sh;
};

IMPLEMENT_FORM(PaymentForm)

PaymentData::PaymentData(PaymentImpl *p, bool retToDocList) : doc(p)
{
   this->retToDocList = retToDocList;
}

PaymentData::~PaymentData()
{
   delete doc;
}

PaymentForm::PaymentForm() : data(NULL), numInput(IDC_ORDER_SUM)
{
}

PaymentForm::~PaymentForm()
{
   delete data;
}

static DWORD GetItemValue(CWindow sum, int scale)
{
   int len = sum.GetWindowTextLength() + 1;
   wchar_t* buf = (wchar_t*)alloca(len * sizeof(wchar_t));
   sum.GetWindowText(buf, len);
   return GetValue(buf, SUM_SCALE);
}

void PaymentForm::Save()
{
   PaymentImpl *pi = data->doc;
   CWindow rem(GetDlgItem(IDC_REMARK));
   int len = rem.GetWindowTextLength();
   len++;
   wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
   rem.GetWindowText(buf, len);
   pi->remark = pi->holder.Add(buf);

   CWindow num(GetDlgItem(IDC_DOC_NUMBER));
   len = num.GetWindowTextLength();
   len++;
   buf = (wchar_t*)alloca(len * sizeof(wchar_t));
   num.GetWindowText(buf, len);
   pi->number = pi->holder.Add(buf);

   pi->sum = GetItemValue(GetDlgItem(IDC_ORDER_SUM), SUM_SCALE);
   pi->sumTax = GetItemValue(GetDlgItem(IDC_TAX), SUM_SCALE);

   SYSTEMTIME st;
  ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
  SystemTimeToFileTime(&st, &pi->date);

   CComboBox cb(GetDlgItem(IDC_FIRMS));
   int idx = cb.GetCurSel();
   if( idx >= 0 )
   {
      wchar_t* code = (wchar_t*)cb.GetItemDataPtr(idx);
      if( wcscmp(code, pi->supplyer) )
         pi->supplyer = pi->holder.Add(code);
   }

   pi->Write();
   docTypeManager.SumChanged(dtPayment, pi->id);
}


LRESULT PaymentForm::Print(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Save(); 

   ProgressWindow pw;
   pw.CreateSTDWindow(m_hWnd);
   data->Print(&pw);

   pw.DestroyWindow();
   return 0;
}

LRESULT PaymentForm::OnSetFocus(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   if( wID == IDC_TAX || wID == IDC_ORDER_SUM )
      numInput.SetTargetControl(wID);

   return 0;
}

LRESULT PaymentForm::DoPaint(UINT uMsg, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   PAINTSTRUCT ps;
   HDC dc = BeginPaint(&ps);

   CRect bounds;
   CWindow wnd(GetDlgItem(IDC_ORG_TITLE));

   wnd.GetWindowRect(bounds);
   ScreenToClient(bounds);

   MoveToEx(dc, 0, bounds.bottom, NULL);
   LineTo(dc, bounds.right, bounds.bottom);

   EndPaint(&ps);
   return 0;
}

void PaymentForm::UpdateLayout(bool forceRecalc)
{
   const int offset = 2;
   CRect bounds;
   GetClientRect(bounds);

   CWindow wnd(GetDlgItem(IDC_REMARK));
   CRect rc;
   wnd.GetWindowRect(rc);
   ScreenToClient(rc);
   wnd.MoveWindow(offset, rc.top, bounds.right - offset * 2, bounds.bottom - rc.top - offset);

   wnd = GetDlgItem(IDC_FIRMS);
   wnd.GetWindowRect(rc);
   ScreenToClient(rc);
   wnd.MoveWindow(rc.left, rc.top, bounds.right - offset - rc.left, rc.Height());

   CWindow wnd3(GetDlgItem(IDC_ORG_TITLE));
   wnd3.GetWindowRect(rc);
   ScreenToClient(rc);
   wnd3.MoveWindow(offset, rc.top, bounds.right - 2 * offset, rc.Height());
}

static void SetItemValue(CEdit edit, int value, int scale)
{
   wchar_t buf[20], src[20];
   ConvertScaling(src, (long)value, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, scale, false);

   edit.SetWindowText(buf);
   edit.SetSelAll();
}

bool PaymentForm::SetData(IFormData *_data)
{
   data = (PaymentData*)_data;

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   sumLabel.CreateLabel(menuBar.m_hWnd, 0, (GetSystemMetrics(SM_CXSMICON) * 23) / 10);
   sumLabel.SetSum(data->doc->sum);

   OrgImpl oi;
   oi.id = data->doc->id;
   oi.Read();
   SetDlgItemText(IDC_ORG_TITLE, oi.name);
   SetDlgItemText(IDC_DOC_NUMBER, data->doc->number);
   SetDlgItemText(IDC_REMARK, data->doc->remark);

   SetItemValue(CEdit(GetDlgItem(IDC_ORDER_SUM)), data->doc->sum, SUM_SCALE);
   SetItemValue(CEdit(GetDlgItem(IDC_TAX)), data->doc->sumTax, SUM_SCALE);

   SYSTEMTIME st;
   FileTimeToSystemTime(&data->doc->date, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   CComboBox cb(GetDlgItem(IDC_FIRMS));
   FirmImpl fi;
   SQLTable table(fi.Name());
   bool res = table.Select(&fi, L" ORDER BY name");
   while( res )
   {
      int index = cb.AddString(fi.name);
      cb.SetItemDataPtr(index, sh.Add(fi.id));

      if( !wcscmp(data->doc->supplyer, fi.id) )
         cb.SetCurSel(index);

      res = table.SelectNext(&fi);
   }

   if( data->doc->IsDirty() == false )
      EnumChildWindows(m_hWnd, DisableChildsProc, (LPARAM)((HWND)GetDlgItem(IDCANCEL)));

   UpdateLayout(false);
   return true;
}

LRESULT PaymentForm::Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Save();
   if( ((PaymentData*)data)->RetToDocList() )
      OpenOrgDocs(data->doc->id, dtPayment);
   else
      OpenListDoc(dtPayment);
   return 0;
}


//
// ----------------------------------- Payment Impl --------------------------------
//
IDocument* PaymentImpl::Copy()
{
   if( rid == NO_ROWID ) return NULL;

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   PaymentImpl *p = new PaymentImpl();

   p->Read(rid);
   SystemTimeToFileTime(&st, &p->created);
   p->params = 0;
   p->rid = NO_ROWID;
   p->Write();
   return p;
}

bool PaymentImpl::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) == true )
   {
      OpenPayment(this, true);
      return true;
   }

   return false;
}

static void MakeDocNumber(std::wstring *num)
{
   wchar_t buf[100];
   DWORD nnum = 0;

   PaymentImpl pi;
   SQLTable table(pi.Name());
   if( table.Select(&pi, L"ORDER BY created DESC") )
   {
      const wchar_t* p = pi.number;
      while( iswdigit(*p) == 0 && *p != L'\0')
      {
         num->append(1, *p);
         p++;
      }
      nnum = _wtoi(p);
   }

   wsprintf(buf, L"%d", nnum + 1);
   num->append(buf);
}

bool PaymentImpl::Init(const ROWID &orgID)
{
#ifdef GPS_POS
   if( !CheckGPSPos(L"Получить координаты?") )
      return false;

   latitude = gCurrentGPSPos.latitude;
   longitude = gCurrentGPSPos.longitude;
#endif

   OrgImpl org;
   org.Read(orgID);
   id = holder.Add(org.id);
   rid = NO_ROWID;

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &created);
   date = created;

   params = 0;
   sum = 0;

   std::wstring tn;
   MakeDocNumber(&tn);
   number = holder.Add(tn.c_str());

   return true;
}

bool PaymentImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   //if( rid == NO_ROWID ) return false;

   if( reverse )
   {
      if( params & ofExported ) params &= (~ofExported);
      else params |= ofExported;
   } else
      params |= ofExported;

   return (updateTable == NULL) ? true : updateTable->Update(*this, L"params", rid);
}

bool PaymentImpl::CanRemove() const
{
   bool needDelete = false;
   if( !IsDirty() )
   {
      int id = MessageBox(GetActiveWindow(), L"Удалить оплату?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION);
      if( id == IDYES )
         needDelete = true;
   } else
   {
      int id = MessageBox(GetActiveWindow(), L"ВНИМАНИЕ!\nОплата не передана на компьютер\nУдалить оплату?", 
         L"Подтверждение", MB_YESNO|MB_ICONQUESTION);

      if( id == IDYES )
         needDelete = true;
   }

   return needDelete;
}

void PaymentImpl::EditDocument(UINT retForm)
{
   OpenPayment(this, (retForm != IDD_ORDER_LIST));
}


void OpenPayment(PaymentImpl *p, bool retToDocList)
{
   PaymentData *data = new PaymentData(p, retToDocList);
   _Module.GetFrame()->Load(IDD_PAYMENT, data);
}

//
// ----------------------------------- PaymentPrint --------------------------------
//

PaymentPrint::PaymentPrint(const PaymentImpl &pi)
{
   date = pi.date;

   number = sh.Add(pi.number);
   remark = sh.Add(pi.remark);

   sumTax = pi.sumTax;
   sum = pi.sum;

   wchar_t buf[50];
   std::wstring txt;
   DigToText(&txt, sum / SUM_SCALE);
   wsprintf(buf, L" руб. %02d коп.", sum % SUM_SCALE);

   txt += buf;
   buf[0] = txt[1];
   buf[1] = L'\0';
   txt[1] = *CharUpper(buf);
   sumText = sh.Add(txt.c_str()+1);

   if( sumTax == 0 )
   {
      taxText = L"без НДС";
   } else
   {
      wsprintf(buf, L"НДС %d руб. %02d коп.", sumTax / SUM_SCALE, sumTax % SUM_SCALE);
      taxText = sh.Add(buf);
   }

   OrgImpl oi;
   oi.id = pi.id;
   oi.Read();

#ifdef Autopteka_van
   suppl.SetSupplyer(pi.supplyer, L"");
#else
   suppl.SetSupplyer(pi.supplyer);
#endif

   name = sh.Add(oi.name);
   address = sh.Add(oi.address);
   phone = sh.Add(oi.phone);
   inn = sh.Add(oi.inn);
   bank = sh.Add(oi.bank);
}

BEGIN_TYPE_REFLECTION(PaymentPrint)
   REGISTER_FILETIME_MEMBER(PaymentPrint, date)
   REGISTER_STRING_MEMBER(PaymentPrint, number)
   REGISTER_STRING_MEMBER(PaymentPrint, remark)

   REGISTER_STRING_MEMBER(PaymentPrint, name)
   REGISTER_STRING_MEMBER(PaymentPrint, address)
   REGISTER_STRING_MEMBER(PaymentPrint, phone)
   REGISTER_STRING_MEMBER(PaymentPrint, inn)
   REGISTER_STRING_MEMBER(PaymentPrint, bank)

   REGISTER_LONG_SCALE_MEMBER(PaymentPrint, sum, SUM_SCALE)
   REGISTER_LONG_SCALE_MEMBER(PaymentPrint, sumTax, SUM_SCALE)

   REGISTER_STRING_MEMBER(PaymentPrint, taxText)
   REGISTER_STRING_MEMBER(PaymentPrint, sumText)
END_TYPE_REFLECTION(PaymentPrint)

bool PaymentSource::GetValue(std::wstring *value, const wchar_t *name)
{
   NapoleonConfig cfg;
   if( cfg.ReadValue(value, name) || ((PaymentPrint*)data)->suppl.GetValue(value, name) )
      return true;

   return ReflectableSource::GetValue(value, name);
}

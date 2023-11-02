/*
 * Copyright (C), 2007-2011, Денис Мосягин
 *
 * Оплата накладных
 *
 *  ert   11/08/2011   creating
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
#include <EnterNumber.h>

BEGIN_TYPE_REFLECTION(DocPayItem)
   REGISTER_STRING_MEMBER(DocPayItem, number)
   REGISTER_FILETIME_MEMBER(DocPayItem, date)
   REGISTER_ULONG_SCALE_MEMBER(DocPayItem, sum, SUM_SCALE)
END_TYPE_REFLECTION(DocPayItem)

BEGIN_TYPE_REFLECTION(DocPay)
   REGISTER_STRING_MEMBER(DocPay, id)
   REGISTER_TIMESTAMP_MEMBER(DocPay, date)
   REGISTER_ULONG_SCALE_MEMBER(DocPay, sum, SUM_SCALE)
   REGISTER_STRING_MEMBER(DocPay, remark)
   REGISTER_ULONG_MEMBER(DocPay, flags)
   REGISTER_COLLECTION_MEMBER(DocPay, items, DocPayItem)
   REGISTER_ULONG_MEMBER(DocPay, supplier)
   REGISTER_STRING_MEMBER(DocPay, number)
   REGISTER_STRING_MEMBER(DocPay, podRemark)
   REGISTER_STRING_MEMBER(DocPay, type)
END_TYPE_REFLECTION(DocPay)

BEGIN_TYPE_REFLECTION(PayItem)
   REGISTER_STRING_MEMBER(PayItem, date)
   REGISTER_STRING_MEMBER(PayItem, flags)
   REGISTER_STRING_MEMBER(PayItem, sum)
END_TYPE_REFLECTION(PayItem)


static ListFormData::Header docHeader[] = 
{
   { ListFormData::Header::Left, L"Номер", L"flags", 50 },
   { ListFormData::Header::Center, L"Дата", L"date", 50 },
   { ListFormData::Header::Right, L"Сумма/Оплата", L"sum", 50 },
};

struct DocPayData : public ListFormData
{
   DocPayData(DocPayImpl *doc, bool rdl);
   ~DocPayData() { delete doc; }

   virtual int Count() const { return payDocs.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   virtual bool Selecting(int index);
   virtual const Header *GetHeader() const { return docHeader; }
   virtual int ColumnsCount() const { return sizeof(docHeader)/sizeof(docHeader[0]); }

   virtual const DataReflector& DataType() const { return PayItem().GetType(); }

   DocPayImpl *doc;
   bool retToDocList;

   mutable PaymentImpl p;
   std::vector<ROWID> payDocs;
   mutable std::wstring sum, date, num;
};

DocPayData::DocPayData(DocPayImpl *doc, bool rdl) : retToDocList(rdl)
{
   this->doc = doc;

   std::wstring sql(L" WHERE id='");
   sql += doc->id;
   sql += L"' ORDER BY date";

   SQLTable t(p.Name());
   t.RIDList(&payDocs, sql.c_str());
}

bool DocPayData::Get(IReflectableData* data, int index) const
{
   if( index >= (int) payDocs.size() ) return false;

   p.Read(payDocs[index]);

   wchar_t buf[50], src[40];

   num = p.number;

   SYSTEMTIME st;
   FileTimeToSystemTime(&p.date, &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));

   date = buf;

   long sumV = p.sum;
   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
   sum = buf;

   sumV = doc->GetPayment(p);
   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
   sum += L'\n';
   sum += buf;

   ((PayItem*)data)->sum = sum.c_str();
   ((PayItem*)data)->flags = num.c_str();
   ((PayItem*)data)->date = date.c_str();

   return true;
}

class DocPayForm : public ListForm
{
public:
   DocPayForm() {}

   virtual DWORD GetResourceID() const { return IDD_DOC_PAY; }
   virtual DWORD GetMenuBarID() const { return IDD_PROXY; }

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   DECLARE_FORM(DocPayForm, IDD_DOC_PAY)

   BEGIN_MSG_MAP(DocPayForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   DWORD GetSum() const;

   void LoadCombobox(const std::wstring &val, int id, int value);
protected:
   //void MoveChildWindow(UINT id , int top);
   void LoadDataFromForm();

   StringHolder holder;
};

IMPLEMENT_FORM(DocPayForm)

bool DocPayData::Selecting(int index)
{
   if( index >= (int) payDocs.size() || !doc->IsDirty() )
      return false;
   if( (doc->flags & ofExported) != 0 )
      return false;


   DWORD sum = ((DocPayForm*)owner)->GetSum();
   if( sum == 0 )
   {
      MessageBox(NULL, L"Сначала введите сумму оплаты", L"Ошибка", MB_OK | MB_ICONSTOP);
      return false;
   }

   PaymentImpl pay;
   pay.Read(payDocs[index]);

   doc->sum = sum;
   DWORD psum = doc->PaymentSum() - doc->GetPayment(pay);
   if( psum >= sum )
   {
      MessageBox(NULL, L"Сумма оплаты распеределна", L"Ошибка", MB_OK | MB_ICONSTOP);
      return false;
   }

   sum -= psum;

   if( sum > pay.sum )
      sum = pay.sum;

   EnterNumber dlg;
   dlg.value = sum;
   dlg.title = L"Сумма платежа";
   if( dlg.DoModal() == IDOK )
   {
      std::wstring alert;
      if( doc->SetPayment(pay, dlg.value, &alert) )
      {
         docTypeManager.SumChanged(dtDocPay, doc->id);
         return true;
      }

      MessageBox(NULL, alert.c_str(), L"Ошибка", MB_OK | MB_ICONSTOP);
   }

   return false;
}

DWORD DocPayForm::GetSum() const
{
   wchar_t buf[20];
   GetDlgItem(IDC_SUM).GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
   return GetValue(buf, SUM_SCALE);
}

void DocPayForm::LoadDataFromForm()
{
   DWORD sum = GetSum();
   bool sumChanged = (sum != ((DocPayData*)data)->doc->sum);
   ((DocPayData*)data)->doc->sum = sum;

   // время отрезать нельзя из-за constraint
   //SYSTEMTIME st;
   //((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
   //ResetTime(&st);
   //SystemTimeToFileTime(&st, &((DocPayData*)data)->doc->date);

   CComboBox cbs(GetDlgItem(IDC_SUPPL));
   int cs = cbs.GetCurSel();
   if( cs >= 0 )
      ((DocPayData*)data)->doc->supplier = cs;

   std::wstring tstr;
   GetString(&tstr, GetDlgItem(IDC_DOC_NUMBER));
   ((DocPayData*)data)->doc->number = ((DocPayData*)data)->doc->holder.Add(tstr.c_str());

   CWindow tcause(GetDlgItem(IDC_COST_TYPE));
   int len = tcause.GetWindowTextLength() + 1;
   wchar_t *tc = (wchar_t*)alloca(len * sizeof(wchar_t));
   tcause.GetWindowText(tc, len);
   ((DocPayData*)data)->doc->type = ((DocPayData*)data)->doc->holder.Add(tc);

   ((DocPayData*)data)->doc->Write();

   if( sumChanged )
      docTypeManager.SumChanged(dtDocPay, ((DocPayData*)data)->doc->id);
}

LRESULT DocPayForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (((DocPayData*)data)->doc->flags & ofExported) == 0 )
   {
      LoadDataFromForm();
      DocPayImpl* doc = ((DocPayData*)data)->doc;
      if( doc->items.size() == 0 || doc->sum == 0 )
      {
         if( MessageBox(L"Документ пустой.\nУдалить документ?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDNO )
            return 0;
         doc->RemoveDocument();
      }
   }

   if( !CreateNextDoc(((DocPayData*)data)->doc->id) )
   {
      if( ((DocPayData*)data)->retToDocList )
         OpenListDoc();
      else
         OpenOrgDocs(((DocPayData*)data)->doc->id, dtDocPay);
   }
   return 0;
}

LRESULT DocPayForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (((DocPayData*)data)->doc->flags & ofExported) == 0 )
   {
      LoadDataFromForm();
      DocPayImpl* doc = ((DocPayData*)data)->doc;
      if( doc->items.size() == 0 || doc->sum == 0 )
      {
         MessageBox(L"Документ пустой.\nПередача не возможна", L"Ошибка", MB_OK | MB_ICONSTOP);
         return 0;
      }
   }

   if( SendDocument(((DocPayData*)data)->doc, docTypeManager.GetDocType(dtDocPay), L"Документ отправлен") )
   {
      ((DocPayData*)data)->doc->ClearDirty(NULL, false);
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
      listCtrl.EnableWindow(TRUE);
   }
   return 0;
}

void DocPayForm::LoadCombobox(const std::wstring &val, int id, int value)
{
   CComboBox cbBox(GetDlgItem(id));

   std::wstring::size_type sp = 0;
   for( int i=0; ; i++ )
   {
      std::wstring::size_type ep = val.find_first_of(SEP_SYM, sp);
      std::wstring tval = val.substr(sp, (ep != std::wstring::npos) ? ep - sp : std::wstring::npos);

      int index = cbBox.AddString(tval.c_str());
     
      //if( sepSym != std::wstring::npos )
      //{
      //   const wchar_t *code = sh->Add(tval.substr(sepSym + 1).c_str());
      //   cbBox.SetItemData(index, (DWORD)code);
      //   if( wcscmp(value, code) == 0 )
      //      cbBox.SetCurSel(index);
      //}

      if( ep == std::wstring::npos ) break;
      sp = ep + 1;
   }

   if( value >= 0 )
      cbBox.SetCurSel(value);
}

bool DocPayForm::SetData(IFormData *_data)
{
   if( !ListForm::SetDataEx(_data, 2) )
      return false;

   if( (((DocPayData*)data)->doc->flags & ofExported) != 0 )
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
   listCtrl.EnableWindow(TRUE);

   OrgImpl oi;
   oi.id = ((DocPayData*)data)->doc->id;
   oi.Read();
   SetDlgItemText(IDC_ORG_TITLE, oi.name);

   wchar_t buf[20], src[20];
   long value = (long)((DocPayData*)data)->doc->sum;
   ConvertScaling(src, value, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % SUM_SCALE, SUM_SCALE, false);
   CEdit sum(GetDlgItem(IDC_SUM));
   sum.SetWindowText(buf);
   sum.SetSel(0, -1);

   SYSTEMTIME st;
   FileTimeToSystemTime(&((DocPayData*)data)->doc->date, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   NapoleonConfig config;
   std::wstring val;

   config.ReadValue(&val, SUPPL_TYPE);
   LoadCombobox(val, IDC_SUPPL, ((DocPayData*)data)->doc->supplier);

   config.ReadValue(&val, L"ТипПлатежа");
   CComboBox cbBox(GetDlgItem(IDC_COST_TYPE));

   const wchar_t *psrc = ((DocPayData*)data)->doc->type;
   std::wstring::size_type sp = 0;
   for( int i=0; ; i++ )
   {
      std::wstring::size_type ep = val.find_first_of(SEP_SYM, sp);
      const std::wstring& vl = (ep == std::wstring::npos) ? val.substr(sp, ep) : val.substr(sp, ep - sp);
      int index = cbBox.AddString(vl.c_str());

      if( wcscmp(vl.c_str(), psrc) == 0 )
         cbBox.SetCurSel(index);

      if( ep == std::wstring::npos ) break;
      sp = ep + 1;
   }
   cbBox.SetCurSel(value);

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   GetDlgItem(IDC_DOC_NUMBER).SetWindowText(((DocPayData*)data)->doc->number);

   UpdateLayout(false);
   return true;
}

//void DocPayForm::MoveChildWindow(UINT id, int top)
//{
//   CRect rc;
//   CWindow w(GetDlgItem(id));
//   w.GetWindowRect(rc);
//   ScreenToClient(rc);
//   w.MoveWindow(rc.left, top, rc.Width(), rc.Height(), FALSE);
//}
//
void DocPayForm::UpdateLayout(bool forceRecalc)
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

   CWindow wnd;

   wnd = GetDlgItem(IDC_DOC_NUMBER);
   wnd.GetWindowRect(bounds);
   ScreenToClient(bounds);
   wnd.MoveWindow(bounds.left, bounds.top, rc.right - bounds.left, bounds.Height(), FALSE);

   wnd = GetDlgItem(IDC_SUPPL);
   wnd.GetWindowRect(bounds);
   ScreenToClient(bounds);
   wnd.MoveWindow(bounds.left, bounds.top, rc.right - bounds.left, bounds.Height(), FALSE);

   wnd = GetDlgItem(IDC_COST_TYPE);
   wnd.GetWindowRect(bounds);
   ScreenToClient(bounds);
   wnd.MoveWindow(bounds.left, bounds.top, rc.right - bounds.left, bounds.Height(), FALSE);

   SetListLayout(forceRecalc, bounds.bottom + 2);
}

void OpenDocPay(DocPayImpl* document, bool retToDocList)
{
   DocPayData *data = new DocPayData(document, retToDocList);
   _Module.GetFrame()->Load(IDD_DOC_PAY, data);
}

void DocPayImpl::EditDocument(UINT retForm)
{
   OpenDocPay(this, (retForm == IDD_ORDER_LIST));
}

bool DocPayImpl::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) )
   {
      EditDocument(0);
      return true;
   }

   return false;
}

const wchar_t* DocPayImpl::Description() const
{
   if( IsProceeded() ) return (*podRemark != L'\0') ? podRemark : L"в обработке";
   return (flags & ofExported) ? L"отправлен" : L"";
}

bool DocPayImpl::ClearDirty(SQLTable *updateTable, bool reverse)
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

bool DocPayImpl::Init(const ROWID &orgID)
{
   OrgImpl org;
   org.Read(orgID);
   id = holder.Add(org.id);

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &date);

   flags = 0;
   remark = L"";
   sum = 0;

   Preference p;
   p.Load();
   supplier = 0;

   number = L"";
   type = L"";

   return true;
}

bool DocPayImpl::CanRemove() const
{
   return MessageBox(GetActiveWindow(), L"Удалить документ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES;
}

DWORD DocPayImpl::GetPayment(const PaymentImpl& p) const
{
   DWORD sum = 0;
   vector_t<DocPayItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( wcscmp(i->number, p.number) == 0 && CompareFileTime(&i->date, &p.date) == 0 )
      {
         sum = i->sum;
         break;
      }
   }

   return sum;
}

DWORD DocPayImpl::PaymentSum() const
{
   DWORD sum = 0;
   vector_t<DocPayItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
      sum += i->sum;

   return sum;
}

bool DocPayImpl::SetPayment(const PaymentImpl& p, DWORD paySum, std::wstring* alert)
{
   // сначала узнаем можем ли мы добаить такую сумму
   DWORD curSum = 0;
   vector_t<DocPayItem>::iterator i = items.begin(), fnd = items.end();
   for( ; i != items.end(); i++ )
   {
      curSum += i->sum;
      if( wcscmp(i->number, p.number) == 0 && CompareFileTime(&i->date, &p.date) == 0 )
         fnd = i;
   }

   if( fnd != items.end() )
      curSum -= fnd->sum;

   if( paySum > p.sum )
      paySum = p.sum;

   if( curSum + paySum > sum )
   {
      alert->assign(L"Сумма превышает введенную оплату");
      return false;
   }

   if( fnd != items.end() )
   {
      if( paySum == 0 )
         items.erase(fnd);
      else
         fnd->sum = paySum;
   }
   else
   {
      DocPayItem item;
      item.date = p.date;
      item.number = holder.Add(p.number);
      item.sum = paySum;

      items.push_back(item);
   }
   WriteDocument();

   return true;
}

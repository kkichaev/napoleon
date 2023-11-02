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
   REGISTER_STRING_MEMBER(DocPay, supplier)
   REGISTER_STRING_MEMBER(DocPay, number)
   REGISTER_STRING_MEMBER(DocPay, podRemark)
END_TYPE_REFLECTION(DocPay)


static ListFormData::Header docHeader[] = 
{
   { ListFormData::Header::Left, L"Номер", L"flags", 50 },
   { ListFormData::Header::Center, L"Накл/Оплата", L"date", 50 },
   { ListFormData::Header::Right, L"Сумма/Оплачено", L"sum", 50 },
   { ListFormData::Header::Right, L"Тип", L"type", 35 },
};

struct DocPayData : public ListFormData
{
   DocPayData(DocPayImpl *doc, bool rdl);
   ~DocPayData() { delete doc; }

   virtual int Count() const { return payDocs.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   virtual bool Selecting(int index);
   virtual const Header *GetHeader() const { return docHeader; }
   virtual int ColumnsCount() const { return 4; }

   virtual const DataReflector& DataType() const { return PayItem().GetType(); }
   COLORREF GetItemColor(int index) const;

   DocPayImpl *doc;
   bool retToDocList;

   mutable PaymentImpl p;
   std::vector<ROWID> payDocs;
   mutable std::wstring sum, date, num, type;
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

COLORREF DocPayData::GetItemColor(int index) const
{
   if( index >= (int) payDocs.size() ) 
      return 0;

   p.Read(payDocs[index]);
   return p.color;
}

bool DocPayData::Get(IReflectableData* data, int index) const
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

   sumV = doc->GetPayment(p);
   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
   sum += L'\n';
   sum += buf;

   ((PayItem*)data)->sum = sum.c_str();
   ((PayItem*)data)->flags = num.c_str();
   ((PayItem*)data)->date = date.c_str();
   ((PayItem*)data)->type = type.c_str();

   return true;
}

class DocPayForm : public ListForm, public CCustomDraw<DocPayForm>
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
      CHAIN_MSG_MAP(CCustomDraw<DocPayForm>)
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
      lvcd->clrTextBk = ((DocPayData*)data)->GetItemColor(lvcd->nmcd.dwItemSpec);
      if( lvcd->clrTextBk == 0 ) lvcd->clrTextBk = 0xFFFFFF;
      return CDRF_NOTIFYITEMDRAW;
   }

   DWORD GetSum() const;

   void LoadComboboxWithCode(const std::wstring &val, int id, wchar_t* value, wchar_t sepCodeSym, StringHolder *sh, int start = 0);
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
      ((DocPayData*)data)->doc->supplier = ((DocPayData*)data)->doc->holder.Add((wchar_t*)cbs.GetItemDataPtr(cs));

   std::wstring tstr;
   GetString(&tstr, GetDlgItem(IDC_DOC_NUMBER));
   ((DocPayData*)data)->doc->number = ((DocPayData*)data)->doc->holder.Add(tstr.c_str());

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

void DocPayForm::LoadComboboxWithCode(const std::wstring &val, int id, wchar_t* value, wchar_t sepCodeSym, StringHolder *sh, int start)
{
   CComboBox cbBox(GetDlgItem(id));

   std::wstring::size_type sp = 0;
   for( int i=start; ; i++ )
   {
      std::wstring::size_type ep = val.find_first_of(SEP_SYM, sp);
      std::wstring tval = val.substr(sp, (ep != std::wstring::npos) ? ep - sp : std::wstring::npos);

      std::wstring::size_type sepSym = tval.find(sepCodeSym);

      int index = cbBox.AddString(tval.substr(0, sepSym).c_str());
     
      if( sepSym != std::wstring::npos )
      {
         const wchar_t *code = sh->Add(tval.substr(sepSym + 1).c_str());
         cbBox.SetItemData(index, (DWORD)code);
         if( wcscmp(value, code) == 0 )
            cbBox.SetCurSel(index);
      }

      if( ep == std::wstring::npos ) break;
      sp = ep + 1;
   }
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
   LoadComboboxWithCode(val, IDC_SUPPL, ((DocPayData*)data)->doc->supplier, L'\t', &holder);

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
   supplier = holder.Add(p.defaultFirm);

   number = L"";

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
      if( wcscmp(i->number, p.number) == 0 && CompareFileTime(&i->date, &p.dlvDate) == 0 )
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
      if( wcscmp(i->number, p.number) == 0 && CompareFileTime(&i->date, &p.dlvDate) == 0 )
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
      item.date = p.dlvDate;
      item.number = holder.Add(p.number);
      item.sum = paySum;

      items.push_back(item);
   }
   WriteDocument();

   return true;
}

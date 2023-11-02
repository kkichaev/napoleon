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

BEGIN_TYPE_REFLECTION(IncassItem)
   REGISTER_STRING_MEMBER(IncassItem, number)
   REGISTER_FILETIME_MEMBER(IncassItem, date)
   REGISTER_ULONG_SCALE_MEMBER(IncassItem, sum, SUM_SCALE)
END_TYPE_REFLECTION(IncassItem)

BEGIN_TYPE_REFLECTION(Incass)
   REGISTER_STRING_MEMBER(Incass, id)
   REGISTER_TIMESTAMP_MEMBER(Incass, date)
   REGISTER_FILETIME_MEMBER(Incass, created)
   REGISTER_ULONG_SCALE_MEMBER(Incass, sum, SUM_SCALE)
   REGISTER_STRING_MEMBER(Incass, remark)
   REGISTER_ULONG_MEMBER(Incass, flags)
   REGISTER_TIMESTAMP_MEMBER(Incass, dovDate)
   REGISTER_STRING_MEMBER(Incass, dovNumber)
   REGISTER_COLLECTION_MEMBER(Incass, items, IncassItem)
END_TYPE_REFLECTION(Incass)


struct PayItemDisplay : public IReflectableData
{
   wchar_t* number;
   wchar_t* sum;
   wchar_t* payDelay;
   wchar_t* manager;

   DECLARE_TYPE_REFLECTION(PayItemDisplay)
};

BEGIN_TYPE_REFLECTION(PayItemDisplay)
   REGISTER_STRING_MEMBER(PayItemDisplay, number)
   REGISTER_STRING_MEMBER(PayItemDisplay, sum)
   REGISTER_STRING_MEMBER(PayItemDisplay, payDelay)
   REGISTER_STRING_MEMBER(PayItemDisplay, manager)
END_TYPE_REFLECTION(PayItemDisplay)

static ListFormData::Header docHeader[] = 
{
   { ListFormData::Header::Left, L"Номер/Дата", L"number", 50 },
   { ListFormData::Header::Right, L"Долг/Оплата", L"sum", 50 },
   { ListFormData::Header::Right, L"Просчка/Отсрочка", L"payDelay", 50 },
   { ListFormData::Header::Left, L"Проект", L"manager", 100 },
};

struct IncassData : public ListFormData
{
   IncassData(IncassImpl *doc, bool rdl);
   ~IncassData() { delete doc; }

   virtual int Count() const { return payDocs.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   virtual bool Selecting(int index);
   virtual const Header *GetHeader() const { return docHeader; }
   virtual int ColumnsCount() const { return 4; }

   virtual const DataReflector& DataType() const { return PayItemDisplay().GetType(); }

   IncassImpl *doc;
   bool retToDocList;

   mutable PaymentImpl p;
   std::vector<ROWID> payDocs;
   mutable std::wstring sum, payDelay, num, manager;
};

IncassData::IncassData(IncassImpl *doc, bool rdl) : retToDocList(rdl)
{
   this->doc = doc;

   std::wstring sql(L" WHERE id='");
   sql += doc->id;
   sql += L"' ORDER BY date";

   SQLTable t(p.Name());
   t.RIDList(&payDocs, sql.c_str());
}

bool IncassData::Get(IReflectableData* data, int index) const
{
   if( index >= (int) payDocs.size() ) return false;

   p.Read(payDocs[index]);

   wchar_t buf[50], src[40];

   num = p.number;
   num += L'\n';

   SYSTEMTIME st;
   FileTimeToSystemTime(&p.date, &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));

   num += buf;

   long sumV = p.sum;
   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
   sum = buf;

   sumV = doc->GetPayment(p);
   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
   sum += L'\n';
   sum += buf;

   manager = p.manager;

   wsprintf(buf, L"%d\n%d", p.payDelay, p.overDelay);
   payDelay = buf;

   ((PayItemDisplay*)data)->sum = (wchar_t*)sum.c_str();
   ((PayItemDisplay*)data)->number = (wchar_t*)num.c_str();
   ((PayItemDisplay*)data)->payDelay = (wchar_t*)payDelay.c_str();
   ((PayItemDisplay*)data)->manager = (wchar_t*)manager.c_str();

   return true;
}

class IncassForm : public ListForm
{
public:
   IncassForm() {}

   virtual DWORD GetResourceID() const { return IDD_DOC_PAY; }
   virtual DWORD GetMenuBarID() const { return IDD_DOC_PAY; }

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   DECLARE_FORM(IncassForm, IDD_DOC_PAY)

   BEGIN_MSG_MAP(IncassForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   DWORD GetSum() const;
protected:
   //void MoveChildWindow(UINT id , int top);
   void LoadDataFromForm();

   StringHolder holder;
};

IMPLEMENT_FORM(IncassForm)

bool IncassData::Selecting(int index)
{
   if( index >= (int) payDocs.size() || !doc->IsDirty() )
      return false;
   if( (doc->flags & ofExported) != 0 )
      return false;


   DWORD sum = ((IncassForm*)owner)->GetSum();
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
         docTypeManager.SumChanged(dtIncass, doc->id);
         return true;
      }

      MessageBox(NULL, alert.c_str(), L"Ошибка", MB_OK | MB_ICONSTOP);
   }

   return false;
}

DWORD IncassForm::GetSum() const
{
   wchar_t buf[20];
   GetDlgItem(IDC_SUM).GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
   return GetValue(buf, SUM_SCALE);
}

void IncassForm::LoadDataFromForm()
{
   DWORD sum = GetSum();
   bool sumChanged = (sum != ((IncassData*)data)->doc->sum);
	IncassImpl *doc = ((IncassData*)data)->doc;
   doc->sum = sum;

	std::wstring val;
	GetString(&val, GetDlgItem(IDC_DOC_NUMBER).m_hWnd);
	doc->dovNumber = doc->holder.Add(val.c_str());

	SYSTEMTIME st = {0};
	((CDateTimePickerCtrl)GetDlgItem(IDC_PAY_DATE)).GetSystemTime(&st);
	SystemTimeToFileTime(&st, &doc->dovDate);

   doc->Write();

   if( sumChanged )
      docTypeManager.SumChanged(dtIncass, ((IncassData*)data)->doc->id);
}

LRESULT IncassForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (((IncassData*)data)->doc->flags & ofExported) == 0 )
   {
      LoadDataFromForm();
      IncassImpl* doc = ((IncassData*)data)->doc;
      if( doc->items.size() == 0 || doc->sum == 0 )
      {
         if( MessageBox(L"Документ пустой.\nУдалить документ?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDNO )
            return 0;
         doc->RemoveDocument();
      }
   }

   if( !CreateNextDoc(((IncassData*)data)->doc->id) )
   {
      if( ((IncassData*)data)->retToDocList )
         OpenListDoc();
      else
         OpenOrgDocs(((IncassData*)data)->doc->id, dtIncass);
   }
   return 0;
}

LRESULT IncassForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (((IncassData*)data)->doc->flags & ofExported) == 0 )
   {
      LoadDataFromForm();
      IncassImpl* doc = ((IncassData*)data)->doc;
      if( doc->items.size() == 0 || doc->sum == 0 )
      {
         MessageBox(L"Документ пустой.\nПередача не возможна", L"Ошибка", MB_OK | MB_ICONSTOP);
         return 0;
      }
   }

   if( SendDocument(((IncassData*)data)->doc, docTypeManager.GetDocType(dtIncass), L"Документ отправлен") )
   {
      ((IncassData*)data)->doc->ClearDirty(NULL, false);
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
      listCtrl.EnableWindow(TRUE);
   }
   return 0;
}

bool IncassForm::SetData(IFormData *_data)
{
   if( !ListForm::SetDataEx(_data, 2) )
      return false;

   if( (((IncassData*)data)->doc->flags & ofExported) != 0 )
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
   listCtrl.EnableWindow(TRUE);

   OrgImpl oi;
   oi.id = ((IncassData*)data)->doc->id;
   oi.Read();
   SetDlgItemText(IDC_ORG_TITLE, oi.name);

   wchar_t buf[20], src[20];
   long value = (long)((IncassData*)data)->doc->sum;
   ConvertScaling(src, value, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % SUM_SCALE, SUM_SCALE, false);
   CEdit sum(GetDlgItem(IDC_SUM));
   sum.SetWindowText(buf);
   sum.SetSel(0, -1);

	CEdit dn(GetDlgItem(IDC_DOC_NUMBER));
	dn.SetWindowText(((IncassData*)data)->doc->dovNumber);

   SYSTEMTIME st;
   FileTimeToSystemTime(&((IncassData*)data)->doc->date, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   FileTimeToSystemTime(&((IncassData*)data)->doc->dovDate, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_PAY_DATE)).SetSystemTime(GDT_VALID, &st);

	menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   UpdateLayout(false);
   return true;
}

//void IncassForm::MoveChildWindow(UINT id, int top)
//{
//   CRect rc;
//   CWindow w(GetDlgItem(id));
//   w.GetWindowRect(rc);
//   ScreenToClient(rc);
//   w.MoveWindow(rc.left, top, rc.Width(), rc.Height(), FALSE);
//}
//
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

   CWindow wnd;

   wnd = GetDlgItem(IDC_PAY_DATE);
   wnd.GetWindowRect(bounds);
   ScreenToClient(bounds);

//   SetListLayout(forceRecalc, bounds.bottom + 2);

   rc.left = 0;
   rc.top = bounds.bottom + 2;
   if( rc.right < 400 )
      rc.right = 400;
   //rc.bottom = bottom;

   listCtrl.SetLayout(forceRecalc, rc, data);

   sumLabel.UpdateLayout();
}

void OpenIncass(IncassImpl* document, bool retToDocList)
{
   IncassData *data = new IncassData(document, retToDocList);
   _Module.GetFrame()->Load(IDD_DOC_PAY, data);
}

void IncassImpl::EditDocument(UINT retForm)
{
   OpenIncass(this, (retForm == IDD_ORDER_LIST));
}

bool IncassImpl::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) )
   {
      EditDocument(0);
      return true;
   }

   return false;
}

const wchar_t* IncassImpl::Description() const
{
   //if( IsProceeded() ) return (*podRemark != L'\0') ? podRemark : L"в обработке";
   return (flags & ofExported) ? L"отправлен" : L"";
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
	dovNumber = L"";
	dovDate = date;

   flags = 0;
   remark = L"";
   sum = 0;

   return true;
}

bool IncassImpl::CanRemove() const
{
   return MessageBox(GetActiveWindow(), L"Удалить документ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES;
}

DWORD IncassImpl::GetPayment(const PaymentImpl& p) const
{
   DWORD sum = 0;
   vector_t<IncassItem>::const_iterator i = items.begin();
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

DWORD IncassImpl::PaymentSum() const
{
   DWORD sum = 0;
   vector_t<IncassItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
      sum += i->sum;

   return sum;
}

bool IncassImpl::SetPayment(const PaymentImpl& p, DWORD paySum, std::wstring* alert)
{
   // сначала узнаем можем ли мы добаить такую сумму
   DWORD curSum = 0;
   vector_t<IncassItem>::iterator i = items.begin(), fnd = items.end();
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
      IncassItem item;
      item.date = p.date;
      item.number = holder.Add(p.number);
      item.sum = paySum;

      items.push_back(item);
   }
   WriteDocument();

   return true;
}

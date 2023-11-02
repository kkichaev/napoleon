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
#include "Plan.h"
#include "ListForm.h"
#include "FormEntries.h"
#include "InitDoc.h"
#include "StdFuncs.h"
#include "NumInput.h"

#include <ObjExchange.h>

void OpenPlan(PlanImpl* pi, const wchar_t* svDocType);

struct PlanDisplayItem : public IReflectableData
{
   wchar_t* text;
   FILETIME date;
   DWORD sum; // SUM_SCALE

   DECLARE_TYPE_REFLECTION(PlanDisplayItem)
};

class PlansData : public ListFormData
{
public:
   PlansData(const wchar_t *svDocType);
   ~PlansData();

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual const DataReflector& DataType() const { return PlanDisplayItem().GetType(); }
   virtual int Count() const { return items.size(); }

   virtual bool Get(IReflectableData* data, int index) const;
   
   virtual bool Adding();
   virtual bool Removing(int index);
   virtual bool Update(const IReflectableData& data, int index) { return false; }

   virtual bool Selecting(int index) { return Editing(index); }
   virtual bool Editing(int index);

   const wchar_t *GetDocType() const { return svDocType; }

protected:
   mutable PlanImpl plan;
   mutable std::wstring text;
   const wchar_t* svDocType;
   std::vector<ROWID> items;
};

class Plans : public ListForm
{
public:
   Plans() {}

   DECLARE_FORM(Plans, IDD_PLAN_LIST);

   virtual DWORD GetMenuID() const { return IDR_ADD_REMOVE; }
   virtual DWORD GetResourceID() const { return IDD_PLAN_LIST; }
   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(Plans)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
   {
      OpenOrgList(((PlansData*)data)->GetDocType());
      return 0;
   }
};

struct PlanData : public IFormData
{
   PlanData(PlanImpl *doc, const wchar_t* svDocType) : retDocType(svDocType) { this->doc = doc; }
   ~PlanData() { delete doc; }

   PlanImpl *doc;
   const wchar_t* retDocType;
};

class PlanForm : public BaseForm
{
public:
   PlanForm() : data(NULL), numInput(IDC_SUM) {}
   ~PlanForm();

   virtual DWORD GetResourceID() const { return IDD_PLAN; }
   virtual DWORD GetMenuBarID() const { return IDD_PLAN; }

   virtual bool SetData(IFormData *_data);

   DECLARE_FORM(PlanForm, IDD_PLAN)

   BEGIN_MSG_MAP(PlanForm)
      NUM_INPUT_HANDLER(numInput)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      CHAIN_MSG_MAP(BaseForm)
   END_MSG_MAP()

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

protected:
   PlanData *data;
   CMenuBarCtrl menuBar;
   NumInput numInput;

protected:
   void LoadDataFromForm();
};

IMPLEMENT_FORM(Plans);
IMPLEMENT_FORM(PlanForm);

BEGIN_TYPE_REFLECTION(Plan)
   REGISTER_FILETIME_MEMBER(Plan, date)
   REGISTER_ULONG_SCALE_MEMBER(Plan, sum, SUM_SCALE)
   REGISTER_ULONG_MEMBER(Plan, flags)
END_TYPE_REFLECTION(Plan)

BEGIN_TYPE_REFLECTION(PlanDisplayItem)
   REGISTER_STRING_MEMBER(PlanDisplayItem, text)
   REGISTER_FILETIME_MEMBER(PlanDisplayItem, date)
   REGISTER_ULONG_SCALE_MEMBER(PlanDisplayItem, sum, SUM_SCALE)
END_TYPE_REFLECTION(PlanDisplayItem)

//
//---------------------------------------- PlanImpl -------------------------------------------
//
const wchar_t* PlanImpl::Description() const
{
   return (flags & ofExported) ? L"отправлен" : L"";
}

void PlanImpl::EditDocument(UINT retForm)
{
   OpenPlan(this, svDocType);
}

bool PlanImpl::ClearDirty(SQLTable *updateTable, bool reverse)
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

bool PlanImpl::Init(const ROWID &orgID)
{
   SYSTEMTIME st;
   GetLocalTime(&st);
   ResetTime(&st);

   SystemTimeToFileTime(&st, &date);
   *(__int64*)&date += (__int64)10000000 * 3600 * 24; // устанавливаем на следующий день

   if( !Read() )
   {
      flags = 0;
      sum = 0;
   }

   return true;
}

bool PlanImpl::CreateDocument(const ROWID &orgID)
{
   if( Init(orgID) )
   {
      OpenPlan(this, svDocType);
      return true;
   }

   return false;
}

bool PlanImpl::CanRemove() const
{
   return MessageBox(GetActiveWindow(), L"Удалить план?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES;
}

//
//---------------------------------- PlansData -----------------------------------
//
static ListFormData::Header header[] = 
{
   { ListFormData::Header::Right, L"", L"text", 50 },
   { ListFormData::Header::Right, L"Дата", L"date", 50 },
   { ListFormData::Header::Right, L"Сумма", L"sum", 50 },
};

PlansData::PlansData(const wchar_t *svDocType)
{
   this->svDocType = svDocType;

   SQLTable t(plan.Name());
   t.RIDList(&items, L" ORDER by date" );
}

PlansData::~PlansData()
{
}

const ListFormData::Header* PlansData::GetHeader() const
{
   return header;
}

int PlansData::ColumnsCount() const
{
   return sizeof(header)/sizeof(header[0]);
}

bool PlansData::Get(IReflectableData* data, int index) const
{
   if( index >= (int)items.size() )
      return false;

   plan.Read(items[index]);
   ((PlanDisplayItem*)data)->sum = plan.sum;
   ((PlanDisplayItem*)data)->date = plan.date;

   text = plan.Description();
   ((PlanDisplayItem*)data)->text = (wchar_t*)text.c_str();


   return true;
}

bool PlansData::Adding()
{
   PlanImpl *pi = new PlanImpl();
   pi->SetSVDocType(svDocType);
   pi->CreateDocument(0);
   return false;
}

bool PlansData::Removing(int index)
{
   if( index < (int)items.size() )
   {
      PlanImpl *pi = new PlanImpl();
      pi->Read(items[index]);
      bool bdo = ( pi->CanRemove() && pi->RemoveDocument() );
      delete pi;

      if( bdo )
      {
         std::vector<ROWID>::iterator i = items.begin();
         advance(i, index);
         items.erase(i);
         return true;
      }
   }

   return false;
}

bool PlansData::Editing(int index)
{
   if( index < (int)items.size() )
   {
      PlanImpl *pi = new PlanImpl();
      pi->Read(items[index]);
      pi->SetSVDocType(svDocType);
      pi->EditDocument(0);
   }
   return false;
}

//
//---------------------------------- Plans -----------------------------------
//
bool Plans::SetData(IFormData *_data)
{
   if( !ListForm::SetDataEx(_data, 2) )
      return false;

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID()));

   UpdateLayout(false);
   return true;
}

//
//----------------------------------- PlanForm -------------------------------------------
//
void PlanForm::LoadDataFromForm()
{
   wchar_t buf[20];
   CEdit sumCtrl(GetDlgItem(IDC_SUM));
   sumCtrl.GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
   DWORD sum = GetValue(buf, SUM_SCALE);
   data->doc->sum = sum;

   SYSTEMTIME st;
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
   ResetTime(&st);
   SystemTimeToFileTime(&st, &data->doc->date);

   data->doc->Write();
}

LRESULT PlanForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (data->doc->flags & ofExported) == 0 )
      LoadDataFromForm();

   if( *data->retDocType != L'\0' )
      OpenPlans(data->retDocType);
   else
      OpenListDoc(dtPlans);

   return 0;
}

LRESULT PlanForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( (data->doc->flags & ofExported) == 0 )
      LoadDataFromForm();

   Preference p;
   p.Load();

   if( (p.flags & opfSendOnLine) == 0 )
   {
      if( SendDocument(data->doc, docTypeManager.GetDocType(dtPlans), L"Документ отправлена") )
         EnumChildWindows(m_hWnd, DisableChildsProc, NULL);

      return 0;
   }

   bool ret = false;
   std::wstring answer;
   ServObject<PlanImpl> o;

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

PlanForm::~PlanForm()
{
   delete data;
}

bool PlanForm::SetData(IFormData *_data)
{
   data = (PlanData*)_data;

   if( (data->doc->flags & ofExported) != 0 )
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);

   wchar_t buf[20], src[20];
   long value = (long)data->doc->sum;
   ConvertScaling(src, value, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % SUM_SCALE, SUM_SCALE, false);
   CEdit sumCtrl(GetDlgItem(IDC_SUM));
   sumCtrl.SetWindowText(buf);
   sumCtrl.SetSelAll();

   SYSTEMTIME st;
   FileTimeToSystemTime(&data->doc->date, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   return true;
}

void OpenPlan(PlanImpl* pi, const wchar_t* svDocType)
{
   PlanData *pd = new PlanData(pi, svDocType);
   _Module.GetFrame()->Load(IDD_PLAN, pd);
}

void OpenPlans(const wchar_t *svDocType)
{
   PlansData *pd = new PlansData(svDocType);
   _Module.GetFrame()->Load(IDD_PLAN_LIST, pd);
}
/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Планы
 *
 *  ert   02/08/2010   creating
 */
#include "stdafx.h"
#include "Add.h"
#include <FormEntries.h>
#include <StdFuncs.h>
#include <EnterNumber.h>
#include <SAnchor.h>

struct PlanDisplayItem : public IReflectableData
{
   wchar_t *name;
   wchar_t *date; // дата + %
   wchar_t *value;

   DECLARE_TYPE_REFLECTION(PlanDisplayItem)
};

class PlansData : public ListFormData
{
public:
   PlansData(const wchar_t *svDocType);
   ~PlansData();

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const { return 3; }

   virtual const DataReflector& DataType() const { return PlanDisplayItem().GetType(); }
   virtual int Count() const { return items.size(); }

   virtual bool Get(IReflectableData* data, int index) const;
   
   virtual bool Adding() { return false; }
   virtual bool Removing(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }

   virtual bool Selecting(int index) { return false; }
   virtual bool Editing(int index) { return false; }

   const wchar_t *GetDocType() const { return svDocType; }

protected:
   mutable PlanImpl plan;
   mutable std::wstring name, value, date;
   const wchar_t* svDocType;
   std::vector<ROWID> items;
};

class Plans : public ListForm
{
public:
   Plans() {}

   DECLARE_FORM(Plans, IDD_PLAN_LIST);

   virtual DWORD GetMenuID() const { return -1; }
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

IMPLEMENT_FORM(Plans);

BEGIN_TYPE_REFLECTION(PlanDisplayItem)
   REGISTER_STRING_MEMBER(PlanDisplayItem, name)
   REGISTER_STRING_MEMBER(PlanDisplayItem, value)
   REGISTER_STRING_MEMBER(PlanDisplayItem, date)
END_TYPE_REFLECTION(PlanDisplayItem)

BEGIN_TYPE_REFLECTION(Plan)
   REGISTER_FILETIME_MEMBER(Plan, date)
   REGISTER_STRING_MEMBER(Plan, name)

   REGISTER_ULONG_SCALE_MEMBER(Plan, plan, SUM_SCALE)
   REGISTER_ULONG_SCALE_MEMBER(Plan, fact, SUM_SCALE)
END_TYPE_REFLECTION(Plan)

//
//---------------------------------- PlanImpl -----------------------------------
//

//
//---------------------------------- PlansData -----------------------------------
//
static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Название", L"name", 100 },
   { ListFormData::Header::Right, L"План/Факт", L"value", 50 },
   { ListFormData::Header::Right, L"Дата", L"date", 50 },
};

PlansData::PlansData(const wchar_t *svDocType)
{
   this->svDocType = svDocType;

   SQLTable t(plan.Name());
   t.RIDList(&items, L" ORDER by date, name" );
}

PlansData::~PlansData()
{
}

const ListFormData::Header* PlansData::GetHeader() const
{
   return header;
}

bool PlansData::Get(IReflectableData* data, int index) const
{
   if( index >= (int)items.size() )
      return false;

   plan.Read(items[index]);

   wchar_t buf[100], src[100];
   name = plan.name;

   long sumV = plan.plan;
   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
   value = buf;

   sumV = plan.fact;
   ConvertScaling(src, sumV, SUM_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % SUM_SCALE, SUM_SCALE, false);
   value += L"\n";
   value += buf;

   SYSTEMTIME st;
   FileTimeToSystemTime(&plan.date, &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));
   date = buf;

   sumV = (long)(((__int64)plan.fact * 1000) / plan.plan);
   ConvertScaling(src, sumV, PLAN_SCALE);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), sumV % PLAN_SCALE, PLAN_SCALE, false);
   date += L"\n";
   date += buf;
   date += L" %";

   ((PlanDisplayItem*)data)->name = (wchar_t*)name.c_str();
   ((PlanDisplayItem*)data)->value = (wchar_t*)value.c_str();
   ((PlanDisplayItem*)data)->date = (wchar_t*)date.c_str();

   return true;
}

//
//---------------------------------- Plans -----------------------------------
//
bool Plans::SetData(IFormData *_data)
{
   if( !ListForm::SetDataEx(_data, 3) )
      return false;

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID()));

   UpdateLayout(false);
   return true;
}

void OpenPlans(const wchar_t *svDocType)
{
   PlansData *pd = new PlansData(svDocType);
   _Module.GetFrame()->Load(IDD_PLAN_LIST, pd);
}

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

class PlansData : public ListFormData
{
public:
   PlansData(const wchar_t *svDocType);
   ~PlansData();

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const { return 4; }

   virtual const DataReflector& DataType() const { return Plan().GetType(); }
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
   const wchar_t* svDocType;
   std::vector<ROWID> items;
};

class Plans : public ListForm
{
public:
   Plans() {}

   DECLARE_FORM(Plans, IDD_PLAN_LIST);

   virtual DWORD GetMenuID() const { return -1; }
   virtual DWORD GetMenuBarID() const { return IDD_PLAN_LIST; }
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

BEGIN_TYPE_REFLECTION(Plan)
   REGISTER_STRING_MEMBER(Plan, name)
   REGISTER_STRING_MEMBER(Plan, plan)
   REGISTER_STRING_MEMBER(Plan, fact)
   REGISTER_STRING_MEMBER(Plan, procent)
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
   { ListFormData::Header::Left, L"План", L"plan", 50 },
   { ListFormData::Header::Left, L"Факт", L"fact", 50 },
   { ListFormData::Header::Left, L"Процент", L"procent", 50 },
};

PlansData::PlansData(const wchar_t *svDocType)
{
   this->svDocType = svDocType;

   SQLTable t(plan.Name());
   t.RIDList(&items, L" ORDER by name" );
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

   ((Plan*)data)->name = (wchar_t*)plan.name;
   ((Plan*)data)->plan = (wchar_t*)plan.plan;
   ((Plan*)data)->fact = (wchar_t*)plan.fact;
   ((Plan*)data)->procent = (wchar_t*)plan.procent;

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

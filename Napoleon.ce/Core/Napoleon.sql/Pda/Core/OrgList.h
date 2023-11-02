/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Список организаций
 *
 *  ert   13/08/2007   creating
 */
#ifndef __ORG_LIST_H
#define __ORG_LIST_H

#include <ListForm.h>
#include "ObjImpl.h"

#include "FormEntries.h"
#include "OrgFuncs.h"

#include <NapoleonRes.h>

#include <SearchCtrl.h>
#include <SQLSearch.h>

struct OrgListItem : public IReflectableData
{
   const wchar_t *name;
   long    sum;

   DECLARE_TYPE_REFLECTION(OrgListItem)
};

class OrgList : public ListForm, public OrgFuncs, public SearchControl::ISearchEvent
{
public:
   OrgList();

   DECLARE_FORM(OrgList, IDD_ORG_LIST);

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(OrgList)
      ORG_FUNC_HANDLER()
      COMMAND_ID_HANDLER(IDC_SHOW_2_ROW, ChangeRows)
      COMMAND_ID_HANDLER(IDC_DEL, RemoveOrg)
      COMMAND_ID_HANDLER(IDC_CALENDAR, ChangeView)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   virtual DWORD GetMenuID() const;
   virtual DWORD GetResourceID() const { return IDD_ORG_LIST; }

   virtual void SearchClear(); // нажали на кнопку новый
   virtual void SearchDo(const wchar_t *text); 

   LRESULT ChangeView(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

protected:
   virtual void UpdateLayout(bool forceRecalc);
   virtual void Refresh();

   bool SetDataEx(IFormData *_data, int scale);

   LRESULT ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT RemoveOrg(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

public:
   static int lastViewed;
};

struct OrgListData : public ListFormData, public OrgData
{
   OrgListData();
   ~OrgListData();

   virtual OrgListData* Clone();
   virtual ROWID GetOID(int index) const;

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual const DataReflector& DataType() const;
   virtual int Count() const { return (inSearch) ? searchResult.size() : rows.size(); }

   virtual bool Get(IReflectableData* data, int index) const;
   
   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index);
   virtual bool Update(const IReflectableData& data, int index) { return false; }

   virtual bool Selecting(int index);

   virtual void Refresh();

   bool FindItems(const wchar_t *text);
   void ClearSearch();

protected:
   bool Open();

   // можно изменить name для Get функции. Все строковые поля orgItem еще живы
   virtual void AfterSetName() const;

protected:
   std::vector<ROWID> rows;

   mutable OrgImpl org;
   mutable std::wstring name;
   mutable OrgSumImpl sums;

   SQLTextSearcher searchHelper;
   std::vector<ROWID> searchResult;
   bool inSearch;
};

#endif

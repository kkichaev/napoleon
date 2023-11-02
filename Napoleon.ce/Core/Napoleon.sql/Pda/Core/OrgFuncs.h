/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Функционал для окна организаций
 *
 *  ert   30/10/2007   creating
 */
#ifndef __ORG_FINCS_H
#define __ORG_FINCS_H

#include "ListForm.h"
#include "FormEntries.h"
#include <DocType.h>
#include <SearchCtrl.h>
#include <set>

#define ORG_FUNC_HANDLER() \
   { \
      bHandled = TRUE; \
      EventHandler(uMsg, hWnd, wParam, lParam,  bHandled, lResult);        \
      if( bHandled == TRUE ) \
         return TRUE; \
   }
 
class OrgData
{
public:
   // set dtOrder default
   OrgData();

   void DestroyData();

   virtual ROWID GetOID(int index) const = 0;

   const DocType* GetDocType() const  { return docType; }
   virtual void SetDocType(const wchar_t *type);

   virtual DWORD GetSum() const { return docType->GetSum(); }

   virtual COLORREF GetItemColor(int index, COLORREF defaultColor, NMLVCUSTOMDRAW *lvcd) const;

   DWORD GetColor(const Org& org, COLORREF defaultColor) const;

protected:
   const DocType *docType;
   std::set<std::wstring> orgIds;
};

class OrgFuncs
{
public:
   OrgFuncs(ListForm *form);

   void SetOrgData(OrgData *orgData, SearchControl::ISearchEvent* handler);

   virtual void LoadMenuBar(bool hideSIP);

   BOOL EventHandler(UINT uMsg, HWND hWnd, WPARAM wParam, LPARAM lParam, BOOL &bHandled, LRESULT &lResult);

   LRESULT CreateOrder(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ReceivePrice(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT EditPreference(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OpenListDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT PriceList(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT SetViewType(int id, LPNMHDR header, BOOL &bHandled);

   LRESULT OnMeasureItem(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
   LRESULT OnDrawItem(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/) { return CDRF_NOTIFYITEMDRAW; }
   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

   void UpdateLayout(const CRect& bounds);

protected:
   ListForm *form;
   OrgData *orgData;
   SearchControl search;
};

#endif

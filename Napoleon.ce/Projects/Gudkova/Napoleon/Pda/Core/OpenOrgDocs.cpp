/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Список организаций
 *
 *  ert   13/08/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DocType.h>

#include <DBImpl.h>
#include "OrgDocs.h"
#include "FormEntries.h"
#include <StdFuncs.h>
#include <InitDoc.h>

struct OrgDocsDataAdd : public OrgDocsListData
{
   OrgDocsDataAdd(const wchar_t *org, const wchar_t* type) : OrgDocsListData(org, type)
   {
      SYSTEMTIME st;
      GetLocalTime(&st);
      ResetTime(&st);

      SystemTimeToFileTime(&st, &curTime);
   }

   COLORREF GetItemColor(int index, COLORREF defaultColor) const
   {
      if( docType->Type() == dtBalance )
      {
         IDocument *doc = docList->Get(index);
         if( doc != NULL && ((BalanceDoc*)doc)->isDelivery )
         {
            Delivery* d = (Delivery*)((BalanceDoc*)doc)->Data();
            if( d->sumD > 0 && !IsStartDate(d->payDate) && CompareFileTime(&curTime, &d->payDate) > 0 )
               return RGB(255,00,00);
         }
      }

      return defaultColor;
   }

   FILETIME curTime;
};

class OrgDocsAdd : public OrgDocsList, public CCustomDraw<OrgDocsAdd>
{
 public:
   OrgDocsAdd() {}

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS; }

   DECLARE_FORM(OrgDocsAdd, IDD_ORG_DOCS_ADD)

   BEGIN_MSG_MAP(OrgDocsAdd)
      CHAIN_MSG_MAP(CCustomDraw<OrgDocsAdd>)      
      CHAIN_MSG_MAP(OrgDocsList)
   END_MSG_MAP()

   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/)
   {
      return CDRF_NOTIFYITEMDRAW;
   }

   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
   {
      NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
      lvcd->clrText = ((OrgDocsDataAdd*)data)->GetItemColor(lvcd->nmcd.dwItemSpec, listCtrl.GetTextColor());
      return CDRF_NOTIFYITEMDRAW;
   }

};

IMPLEMENT_FORM(OrgDocsAdd)

void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsDataAdd(orgID, type));
}

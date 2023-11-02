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

#include "OrgDocs.h"
#include "NplConfig.h"
#include "FormEntries.h"

#include <Visit.h>
#include <OrgRmnts.h>
#include "Add.h"

struct OrgDocsDataAdd : public OrgDocsListData
{
   OrgDocsDataAdd(const wchar_t *org, const wchar_t* type) : OrgDocsListData(org, type) {}

   virtual void GetTitle(const Org &org, std::wstring *title)
   {
      OrgDocsListData::GetTitle(org, title);
      title->append(L"\nкод: ");
      title->append(org.id);
   }
};

class OrgDocsAdd : public OrgDocsList
{
 public:
   OrgDocsAdd() {}
   ~OrgDocsAdd() {}

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS; }

   DECLARE_FORM(OrgDocsAdd, IDD_ORG_DOCS_ADD)

 protected:
   virtual void SetViewType(const DocType *newDT)
   {
      const wchar_t *nt = newDT->Type();
      if( nt != ((OrgDocsListData*)data)->GetDocType()->Type() != 0 )
      {
         if( nt != dtBalance )
         {
            ((OrgDocsListData*)data)->SetDocType(nt);
            Refresh();
         } else
            OpenOrgDocs(((OrgDocsListData*)data)->ID(), nt);
      }
   }
};

struct PayData : public OrgDocsDataAdd
{
   PayData(const wchar_t *id);

   virtual bool Adding()
   {
      AddNewDocument(owner, docTypeManager.GetDocType(dtOrder), orgID);
      return false;
   }

   virtual int Count() const { return (loaded) ? 2 : 0; }

   virtual bool Get(IReflectableData* data, int index) const;

protected:
   bool loaded;
   PaymentImpl doc;
};

class PayForm : public OrgDocsList
{
 public:
   PayForm();

   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuID() const { return -1; }

   virtual BOOL EnableAddButton() const { return TRUE; }

   DECLARE_FORM(PayForm, IDD_PAY)

   BEGIN_MSG_MAP(PayForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      CHAIN_MSG_MAP(OrgDocsList)
   END_MSG_MAP()

   virtual void SetViewType(const DocType *newDT)
   {
      const wchar_t *nt = newDT->Type();
      if( nt != ((OrgDocsListData*)data)->GetDocType()->Type() != 0 )
         OpenOrgDocs(((OrgDocsListData*)data)->ID(), nt);
   }

protected:
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
};

IMPLEMENT_FORM(PayForm)
IMPLEMENT_FORM(OrgDocsAdd)

PayData::PayData(const wchar_t *id) : OrgDocsDataAdd(id, dtBalance)
{
   std::wstring wh(L"WHERE id='");
   wh += id; wh += L"'";
   SQLTable t(doc.Name());
   loaded = t.Select(&doc, wh.c_str());

   docType = docTypeManager.GetDocType(dtBalance);

}

bool PayData::Get(IReflectableData* data, int index) const
{
   if( !loaded || index > 1 )
      return false;

   ((OrderDocListItem*)data)->date = doc.date;
   ((OrderDocListItem*)data)->sum = (index == 0) ? doc.sum : doc.sum2;
   ((OrderDocListItem*)data)->flags = (index == 0) ? L"баланс" : L"просрочено";         
   return true;
}

PayForm::PayForm()
{
}

LRESULT PayForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   OpenOrgList(dtBalance);
   return 0;
}

//
//------------------------------------------ OrgDocsAdd ---------------------------------------------------------
//

void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   if( type == dtOrder || type == dtDelivery || type == dtVisit || type == dtRemnants || type == dtDisplay )
      _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsDataAdd(orgID, type));
   else if( type == dtBalance )
      _Module.GetFrame()->Load(IDD_PAY, new PayData(orgID));
   else
   {
      const DocType *dt = docTypeManager.GetDocType(type);
      dt->OpenForm(orgID, NULL);
   }
}

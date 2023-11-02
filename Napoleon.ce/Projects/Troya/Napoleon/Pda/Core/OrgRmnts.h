/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Загрузчик типов докуменов дополнительный
 *
 *  ert   15/03/2008   creating
 */
#ifndef __ORG_RMNTS_H
#define __ORG_RMNTS_H

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <ListForm.h>
#include <CEInt.h>

class OrgRemnantsForm : public ListForm
{
public:
   OrgRemnantsForm();
   virtual bool SetData(IFormData *_data);

   virtual DWORD GetMenuID() const { return IDR_ADD_REMOVE; }
   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuBarID() const;

   BEGIN_MSG_MAP(OrgRemnantsForm)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      COMMAND_ID_HANDLER(IDC_AUTO_ORDER, AutoOrder)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   DECLARE_FORM(OrgRemnantsForm, IDD_ORG_REMNANTS)

protected:
   void LoadMenuBar(bool hideSIP);

   LRESULT AutoOrder(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);

};

struct OrgRemnantsImpl : public OrgRemnants
{
   OrgRemnantsImpl(CEOID orgID);

   void Update(CEOID id, DWORD qty);
   OrgRemnantsItem *FindItem(CEOID id) const;

   void Save();
   bool Read();

   CEOID rmid;
};

class OrderImpl;
void OpenOrgRemnantsForm(CEOID oid);
void OpenOrgRemnantsForm(OrderImpl *order);

#endif

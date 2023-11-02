/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Выбор контрагента
*
*  ert   05/09/2010   creating
*/
#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <StdFuncs.h>
#include <ListForm.h>
#include "FormEntries.h"

#include "ObjImpl.h"

class PartnerData : public IFormData
{
public:
   std::vector <PartnerImpl> partners;

   void Load();

protected:
   StringHolder sh;
};

class Partners : public BaseForm
{
public:
   Partners() {}
   ~Partners() { delete data; }

   DECLARE_FORM(Partners, IDS_ORG_LIST)

   virtual DWORD GetResourceID() const { return IDS_ORG_LIST; }
   virtual DWORD GetMenuBarID() const { return IDS_ORG_LIST; }
   virtual DWORD GetMenuID() const { return -1; }

   BEGIN_MSG_MAP(Partners)
      COMMAND_ID_HANDLER(IDOK, OnOK)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_CODE_HANDLER(EN_SETFOCUS, OnSetFocus)
      COMMAND_CODE_HANDLER(EN_KILLFOCUS, OnKillFocus)
      CHAIN_MSG_MAP(BaseForm)
   END_MSG_MAP()

   virtual bool SetData(IFormData *_data);

   LRESULT OnKillFocus(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnSetFocus(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnOK(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

protected:
   PartnerData *data;
   CMenuBarCtrl menuBar;
};

IMPLEMENT_FORM(Partners)

void PartnerData::Load()
{
   PartnerImpl pi;
   SQLTable t(pi.Name());

   bool bdo = t.Select(&pi, L"ORDER BY name");
   while( bdo )
   {
      UnbindingItem(&pi, &sh);
      partners.push_back(pi);

      bdo = t.SelectNext(&pi);
   }
}

LRESULT Partners::OnSetFocus(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   SHSipPreference(m_hWnd, (id == IDC_PWD) ? SIP_UP : SIP_DOWN);
   return 0;
}

LRESULT Partners::OnKillFocus(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( id == IDC_PWD )
      SHSipPreference(m_hWnd, SIP_DOWN);
   return 0;
}

bool Partners::SetData(IFormData *_data)
{
   data = (PartnerData*)_data;

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(),0,0));

   data->Load();

   CComboBox orgs(GetDlgItem(IDS_ORG_LIST));

   std::vector<PartnerImpl>::const_iterator i = data->partners.begin();
   for( ; i != data->partners.end(); i++ )
      orgs.AddString(i->name);

   GetDlgItem(IDC_PWD).SetWindowText(L"");
   return true;
}

LRESULT Partners::OnOK(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   CComboBox orgs(GetDlgItem(IDS_ORG_LIST));
   int cs = orgs.GetCurSel();
   if( cs >= 0 )
   {
      const AgentsImpl* a = _Module.Agent();
      if( a != NULL && CheckPassword(m_hWnd, *a) )
      {
         OpenCost(data->partners.at(cs).id);
      }
   }
   return 0;
}

LRESULT Partners::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenMainForm();
   return 0;
}


void OpenPartnerList()
{
   _Module.GetFrame()->Load(IDS_ORG_LIST, new PartnerData());
}
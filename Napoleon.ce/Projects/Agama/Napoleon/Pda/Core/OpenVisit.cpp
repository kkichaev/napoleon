/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Визиты
 *
 *  ert   08/12/2008   creating
 *  ert   23/06/2009   update
 */
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>
#include <MainFrame.h>
#include "Visit.h"

#include <StdFuncs.h>
#include <ListForm.h>
#include <InitDoc.h>
#include <BaseDialog.h>
#include "PhotoFolder.h"
#include "PrfDlg.h"
#include <PicWindow.h>
#include <NplConfig.h>
#include "FileType.h"

#include "Add.h"

class VisitAdd : public VisitForm
{
public:
   VisitAdd() : inited(false) {}

   virtual DWORD GetResourceID() const { return IDD_VISIT_ADD; }

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   DECLARE_FORM(VisitAdd, IDD_VISIT_ADD)

   UnitList units;

   BEGIN_MSG_MAP(VisitAdd)
      CHAIN_MSG_MAP(VisitForm)
      REFLECT_NOTIFICATIONS()
   END_MSG_MAP()

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   virtual void UpdateData();

   bool inited;
};

IMPLEMENT_FORM(VisitAdd)

void VisitAdd::UpdateData()
{
   VisitImpl *v = data->visit;
   if( v->IsDirty() )
   {
      int uc = units.GetSelectedItemCode();
      if( uc != v->unitCode )
      {
         v->unitCode = uc;
         if( v->unitCode < 0 )
            v->unitCode = 0;
         v->Write();
      }
   }

   VisitForm::UpdateData();
}

bool VisitAdd::SetData(IFormData *_data)
{
   if( !VisitForm::SetData(_data) )
      return false;

   Visit* visit = data->visit;
   OrgImpl o;
   o.id = visit->id;
   if( o.Read() )
   {
      if( o.units.size() > 0 )
      {
         if( visit->unitCode == 0 )
            visit->unitCode = o.units.front().id;

         units.Init(*this, IDC_UNIT_LIST, IDC_UNIT_TEXT_LABEL, IDC_UNIT_TEXT, o, visit->unitCode);
      }
   }

   inited = true;
   UpdateLayout(true);

   return true;
}

void VisitAdd::UpdateLayout(bool forceRecalc)
{
   if( !inited )
      return;

   CRect rc, rc1;
   GetClientRect(rc);

   units.UpdateLayout(rc.Width(), rc.Height());

   CWindow tbl(GetDlgItem(IDC_TABLE));
   tbl.GetWindowRect(rc1);
   int hgh = rc1.Height();

   CWindow r(GetDlgItem(IDC_REMARK));
   GetDlgItem(IDC_UNIT_TEXT).GetWindowRect(rc1);
   ScreenToClient(rc1);

   rc.bottom = rc.Height() - rc1.top - 6 - hgh; 
   rc.top = rc1.bottom + 2;
   r.MoveWindow(rc);

   rc.top = rc.bottom + 1;
   rc.bottom = rc.top + hgh;
   tbl.MoveWindow(rc);
}

void OpenVisit(VisitImpl *visit, bool retToDocList)
{
   OrgImpl o;
   o.id = visit->id;
   o.Read();
   if( o.units.size() == 0 )
   {
      delete visit;
      MessageBox(NULL, L"У клиента нет адреса доставки", L"Ошибка", MB_OK|MB_ICONINFORMATION);
      return;
   }

   VisitData *data = new VisitData(visit, retToDocList);
   _Module.GetFrame()->Load(IDD_VISIT_ADD, data);
}

/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Диалог настроек
 *
 *  ert   24/08/2007   creating
 */ 
#include "stdafx.h"
#include "PropDialog.h"
#include <TopApp.h>

PropDialog::PropDialog()
{
   TopApp::EnableDoneButton(true);
}

PropDialog::PropDialog(ATL::_U_STRINGorID title) : Base(title)
{
   TopApp::EnableDoneButton(true);
}

void PropDialog::AddPage(PropPage *page)
{
   if( CPropertySheetImpl<PropDialog>::AddPage(page->GetPropPage()) == TRUE )
   {
      pages.push_back(page);
      page->SetOwner(this);
   }
}

PropDialog::~PropDialog()
{
   TopApp::EnableDoneButton(false);

   std::vector<PropPage*>::iterator i = pages.begin();
   for( ;i != pages.end(); i++ )
      delete (*i);
}

void PropDialog::PageRemoved(PropPage *page)
{
   std::vector<PropPage*>::iterator i = pages.begin();
   for( ;i != pages.end(); i++ )
   {
      if((*i) == page)
      {
         pages.erase(i);
         break;
      }
   }
}

void PropDialog::OnCancel()
{
   PostMessage(PSM_PRESSBUTTON, PSBTN_CANCEL, 0);
}


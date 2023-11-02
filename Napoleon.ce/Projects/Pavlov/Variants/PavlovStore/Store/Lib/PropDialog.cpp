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

//BOOL PropDialog::PreTranslateMessage(MSG* pMsg)
//{
//   BOOL ret = FALSE;
//   if( pMsg->message == WM_KEYDOWN )
//   {
//      WPARAM key = pMsg->wParam;
//      switch(key)
//      {
//      case VK_LEFT:
//      case VK_RIGHT:
//         {
//            bool selNext = (key == VK_RIGHT);
//            HWND hCur = (HWND)SendMessage(PSM_GETCURRENTPAGEHWND, 0, 0);
//            if( hCur != NULL )
//            {
//               for( int index = 0; index < (int)pages.size(); index++ )
//               {
//                  PropPage* pp = pages.at(index);
//                  if( pp->m_hWnd == hCur )
//                  {
//                     int newIndex = (selNext) ? index+1 : index-1;
//                     if( newIndex >= 0 && newIndex < (int)pages.size() )
//                        SetActivePage(newIndex);
//                     break;
//                  }
//               }
//            }
//         }
//         break;
//      }
//   }
//
//   return ret;
//}

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


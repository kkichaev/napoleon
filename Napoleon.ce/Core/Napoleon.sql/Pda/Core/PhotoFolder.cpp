/*
* Copyright (C), 2007-2010, Денис Мосягин
*
* Comobox для папки фото
* 
*  ert   19/05/2010   creating
*/ 
#include "stdafx.h"
#include "PhotoFolder.h"
#include <Module.h>
#include "MainFrame.h"
#include <NapoleonRes.h>

const wchar_t MAIN_STORAGE[] = L"Основная память";

#ifdef WIN32_PLATFORM_PSPC
#include <projects.h>
#include <Preference.h>

void LoadFolderData(CWindow& parent, UINT id)
{
   CComboBox cbx(parent.GetDlgItem(id));
   cbx.AddString(MAIN_STORAGE);

   WIN32_FIND_DATA fnd;
   HANDLE hFile = FindFirstFlashCard(&fnd);
   if( hFile )
   {
      BOOL done;
      do
      {
         cbx.AddString(fnd.cFileName);
         done = FindNextFlashCard(hFile, &fnd);
      } while(done == TRUE);
      FindClose(hFile);
   }

   Preference p;
   int selected = 0;
   if( p.Load() )
   {
      if( p.photoInMainMemory  == false )
      {
         wchar_t buf[MAX_PATH];
         mbstowcs(buf, p.photoFolder, MAX_PATH);
         selected = cbx.FindString(0, buf);
      }
      if( selected < 0 ) selected = 0;
   }

   cbx.SetCurSel(selected);
}

void StoreFolderData(CWindow& parent, UINT id, Preference* p)
{
   wchar_t buf[MAX_PATH];
   char fbuf[MAX_PATH];

   CComboBox cbx(parent.GetDlgItem(id));

   bool removeP = false;
   if( p == NULL )
   {
      removeP = true;
      p = new Preference();
   }

   p->Load();
   if( cbx.GetCurSel() == 0 )
   {
      p->photoInMainMemory = true;
      *p->photoFolder = L'\0';
   } else
   {
      p->photoInMainMemory = false;

      parent.GetDlgItemText(IDC_PHOTO_FOLDER, buf, MAX_PATH);
      wcstombs(fbuf, buf, MAX_PATH);
      strncpy(p->photoFolder, fbuf, MAX_PATH);
   }

   if( removeP )
   {
      p->Save();
      delete p;
   }
}
#else

void LoadFolderData(CWindow& parent, UINT id)
{
   CComboBox cbx(parent.GetDlgItem(id));
   cbx.AddString(MAIN_STORAGE);

   cbx.SetCurSel(0);
}

void StoreFolderData(CWindow& parent, UINT id, Preference* p)
{
   bool removeP = false;
   if( p == NULL )
   {
      removeP = true;
      p = new Preference();
   }

   p->Load();

   p->photoInMainMemory = true;
   *p->photoFolder = L'\0';

   if( removeP )
   {
      p->Save();
      delete p;
   }
}

#endif

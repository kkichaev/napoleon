/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Базовый диалог
 *
 *  ert   24/08/2007   creating
 */ 
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include <NapoleonRes.h>
#include "BaseDialog.h"

LPDLGTEMPLATE MakeDlgTemplate(WORD dlgTemplateID)
{
   HRSRC hResource = FindResource(_Module.GetResourceInstance(), MAKEINTRESOURCE(dlgTemplateID), (LPTSTR)RT_DIALOG);
   if( hResource == NULL )
      return NULL;

   DLGTEMPLATE *src = (DLGTEMPLATE*)LockResource(LoadResource(_Module.GetResourceInstance(), hResource));
   DWORD size = SizeofResource(_Module.GetResourceInstance(), hResource);

   DLGTEMPLATE *dest = (DLGTEMPLATE *)new char [size];
   memcpy(dest, src, size);

   BYTE *buf;
   
   buf = (BYTE*)dest + sizeof(DLGTEMPLATE);
   buf += sizeof(short);  // skip menuID
   ReadResourceString(buf, &buf); // class name
   ReadResourceString(buf, &buf); // title

#if defined WIN32_PLATFORM_PSPC && !defined(NATIVE_CE)
   DWORD wsize;
   SHGetUIMetrics(SHUIM_FONTSIZE_PIXEL, &wsize, sizeof(wsize), NULL);
   // font size
   if( wsize > 3 )
      wsize -= 3;
   if( wsize > 11 )
      wsize = 11;
   if( wsize != 0 )
      *(WORD*)buf = (WORD)wsize;
#endif

   return dest;
}

BOOL CALLBACK DisableChildsProc(HWND hwnd, LPARAM lParam)
{
   if( hwnd != (HWND)lParam )
      EnableWindow(hwnd, FALSE);

   return TRUE;
}

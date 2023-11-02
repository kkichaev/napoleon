/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Проверка последней синхронизации
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
#include <Progress.h>

#include <DoSync.h>

void ClearSyncFile()
{
   std::wstring fn;
   _Module.MakeFileName(&fn, SYNC_STAMP);
   DeleteFile(fn.c_str());
}

static bool NeedSync()
{
   std::wstring fn;
   _Module.MakeFileName(&fn, SYNC_STAMP);

   bool retVal = true;
   FILE *f = _wfopen(fn.c_str(), L"rb");
   if( f != NULL )
   {
      DWORD w;
      SYSTEMTIME st;
      __int64 ft;
      GetSystemTime(&st);
      SystemTimeToFileTime(&st, (FILETIME*)&ft);
      DWORD res = (DWORD)(ft / ((__int64)10000000 * 3600 * 24));
      fread(&w, sizeof(w), 1, f);

      retVal = (abs(res-w) > MARK_SYNCED);

      fclose(f);
   }

   return retVal;
}

void MarkSynced()
{
   std::wstring fn;
   _Module.MakeFileName(&fn, SYNC_STAMP);

   FILE *f = _wfopen(fn.c_str(), L"wb");
   if( f )
   {
      SYSTEMTIME st;
      __int64 ft;
      GetSystemTime(&st);
      SystemTimeToFileTime(&st, (FILETIME*)&ft);
      DWORD res = (DWORD)(ft / ((__int64)10000000 * 3600 * 24));
      
      fwrite(&res, sizeof(res), 1, f);
      fclose(f);
   }
}

bool DoSync(std::wstring *answer)
{
   HWND hWnd = GetActiveWindow();

   ProgressWindow pw;
   pw.CreateSTDWindow(hWnd);

   DWORD ec = _Module.ReceivePrice(answer, &pw, false, false);
#ifdef Zakroma
   if( ec == 0 )
      ec = _Module.ReceiveDocs(answer, &pw, NapoleonApp::efBalance);
#endif

   pw.DestroyWindow();

   bool ret = false;
   if( ec == 0 )
   {
      MarkSynced();
      ret = true;
   }

   return ret;
}

bool CheckSync()
{
   if( NeedSync() )
   {
      HWND hWnd = GetActiveWindow();
      MessageBox(hWnd, L"Данные устарели, нажмите OK для проведения синхронизации", L"Информация", MB_OK|MB_ICONINFORMATION);
      std::wstring answer;
      while( !DoSync(&answer) )
      {
#if defined(Zakroma) || defined(Migma) || defined(SklRybinsk)
         break;
#else
         answer += L"\nнажмите OK для повтора";
         MessageBox(hWnd, answer.c_str(), L"Информация", MB_OK|MB_ICONINFORMATION);
#endif
      }
   }

   return true;
}

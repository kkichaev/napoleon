/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Napoleon Logistic
*
*  ert   02/09/2010   creating
*/
#include "stdafx.h"

#include <Module.h>
#include <SQLTable.h>

Application::Application() : agentInited(false)
{
}

Application::~Application()
{
}

void Application::SetAgent(const AgentsImpl& a)
{
   agent = a;
   agent.UnbindStrings();

   agentInited = true;
}

void Application::MakeFileName(std::wstring *fullName, const wchar_t *fileName)
{
   if( *fileName == L'\\' )
   {
      *fullName = fileName;
      return;
   }
   wchar_t buf[MAX_PATH];
   GetModuleFileName(_Module.GetModuleInstance(), buf, sizeof(buf));

   wchar_t *p = wcsrchr(buf, L'\\');
   if( p ) p++;
   else p = buf;
   *p = L'\0';

   (*fullName) = buf;
   (*fullName) += fileName;
}

void Application::WaitThreadComplete(HANDLE thread)
{
   if( thread != INVALID_HANDLE_VALUE )
   {
      CMessageLoop *ml = GetMessageLoop();
      while( true )
      {
         DWORD res = WaitForSingleObject(thread, 0);
         if( res == WAIT_OBJECT_0 || res == WAIT_FAILED )
            break;

         MSG msg;
         if( ::PeekMessage(&msg, NULL, 0, 0, PM_REMOVE) == FALSE )
            continue;
         
         if( msg.message == WM_QUIT )
            break;

         if( ml && ml->PreTranslateMessage(&msg) )
            continue;

         ::TranslateMessage(&msg);
         ::DispatchMessage(&msg);
      }
      CloseHandle(thread);
   }
}

struct TableName : public IReflectableData
{
   wchar_t *name;
   DECLARE_TYPE_REFLECTION(TableName)
};

BEGIN_TYPE_REFLECTION(TableName)
   REGISTER_STRING_MEMBER(TableName, name)
END_TYPE_REFLECTION(TableName)

void Application::BaseRemove()
{
   SQLTable table(L"sqlite_master");
   TableName tn;
   std::vector<std::wstring> tableNames;

   bool bdo = table.Select(&tn, L"WHERE type='table'");
   while(bdo)
   {
      tableNames.push_back(tn.name);
      bdo = table.SelectNext(&tn);
   }

   std::vector<std::wstring>::const_iterator i = tableNames.begin();
   for(; i != tableNames.end(); i++ )
      SQLTable::DropTable(i->c_str());
}

void Application::DataClose()
{
   SQLTable::CloseDB();
}

void Application::DataInit(const char *dbName)
{
   if( dbName == NULL || *dbName == '\0' ) dbName = DEFAULT_BASE;

   int len = strlen(dbName) + 1;
   wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
   mbstowcs(buf, dbName, len);

   std::wstring fileName;
   _Module.MakeFileName(&fileName, buf);

   if( SQLTable::OpenDB(fileName.c_str()) == false )
      MessageBox(NULL, L"Не могу открыть базу данных", L"Ошибка", MB_OK | MB_ICONSTOP);
}

void Application::ShowErrorBox(long ec, const std::wstring& msg, const wchar_t* prefix)
{
   std::wstring message(prefix);
   if( !msg.empty() )
      message += msg;
   else
   {
      LPVOID lpMsgBuf;
      int len = FormatMessage( FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | 
          FORMAT_MESSAGE_IGNORE_INSERTS, NULL, ec, 0, (LPTSTR) &lpMsgBuf, 0, NULL );
      
      if( len )
      {
         message += (wchar_t*)lpMsgBuf;
         LocalFree(lpMsgBuf);
      } else
      {
         wchar_t buf[30];
         wsprintf(buf, L"код ошибки %d", ec);
         message += buf;
      }
   }
   MessageBox(GetActiveWindow(), message.c_str(), L"Ошибка", MB_OK|MB_ICONERROR);
}

DWORD GetValue(CWindow wnd, DWORD scale)
{
   int len = wnd.GetWindowTextLength();
   wchar_t *buf = (wchar_t*)alloca((len+1) * sizeof(buf[0]));
   wnd.GetWindowText(buf, len+1);

   wchar_t decBuf[4], sepBuf[4];
   int cch = GetLocaleInfoW(LOCALE_USER_DEFAULT, LOCALE_SDECIMAL, decBuf, sizeof(decBuf)/sizeof(decBuf[0]));
   decBuf[cch] = L'\0';
   cch = GetLocaleInfoW(LOCALE_USER_DEFAULT, LOCALE_STHOUSAND, sepBuf, sizeof(sepBuf)/sizeof(sepBuf[0]));
   sepBuf[cch] = L'\0';

   DWORD val = 0;
   DWORD sign = 1;

   if( *buf == '-' )
   {
      sign = (DWORD)-1;
      buf++;
   }

   while( *buf != L'\0' && *buf != *decBuf && *buf != L'.' )
   {
      if( *buf != *sepBuf )
         val = val * 10 + *buf - L'0';
      buf++;
   }

   val *= scale;
   if( *buf == *decBuf || *buf == L'.' )
   {
      while( *(++buf) && scale > 1 )
      {
         scale /= 10;
         val += (*buf - L'0') * scale;
      }
   }
   return val * sign;
}

void SetScalingValue(CWindow wnd, int value, DWORD scale, bool hideRest)
{
   wchar_t buf[20], src[20];

   ConvertScaling(src, (long)value, scale);
   FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), abs(value) % scale, scale, hideRest);
   wnd.SetWindowText(buf);
}

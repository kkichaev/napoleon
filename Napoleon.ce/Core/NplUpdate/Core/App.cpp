/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* NplUpdate::Application
*
*  ert   09/11/2009   creating
*/
#include "stdafx.h"
#include <regext.h>
#include <snapi.h>
#include "NplUpdate.h"
#include "Util.h"

const wchar_t UpdateFolder[] = L"Update";

Application app;

Application::Application() : hMutex(INVALID_HANDLE_VALUE)
{
}

Application::~Application()
{
   if( hMutex != INVALID_HANDLE_VALUE )
   {
      ReleaseMutex(hMutex);
      CloseHandle(hMutex);
      hMutex = INVALID_HANDLE_VALUE;
   }
}

bool Application::Start(HINSTANCE hInstance, const wchar_t* cmdLine)
{
   bool ret = false;
   const wchar_t* p = cmdLine;

   this->hInstance = hInstance;

   //Alert(L"Run", IDC_UPDATE);

   Log("Program run");

   ReadToken(&configFileName, p, &p);
   if( configFileName.empty() || config.Load(configFileName.c_str()) == false ) 
   {
      char buf[MAX_PATH];
      wcstombs(buf, configFileName.c_str(), configFileName.size() + 1);
      Log("Error loading config file %s", buf);
      return false;
   }

   hMutex = CreateMutex(NULL, FALSE, L"NplUpdateMutex");

   if( GetLastError() == ERROR_ALREADY_EXISTS )
   {
      if( config.curState != NULL && config.curState->IsSelfUpdate() )
      {
         if( WaitForSingleObject(hMutex, INFINITE) == WAIT_OBJECT_0 )
         {
            ret = true;
         } else
         {
            Log("Error Wait Mutex in Update");
         }
      }
   } else
   {
      if( WaitForSingleObject(hMutex, 1000) == WAIT_OBJECT_0 )
         ret = true;
      else
         Log("Error Wait Mutex");
   }

   return ret;
}

void Application::Do(const wchar_t* stateFile)
{
   std::wstring file;
   GetProgName(&file);

   PROCESS_INFORMATION pi;
   std::wstring cmd(L"\"");
   cmd += stateFile;
   cmd += L"\"";

   CreateProcess(file.c_str(), cmd.c_str(), NULL, NULL, FALSE, CREATE_NEW_CONSOLE, NULL, NULL, NULL, &pi);

   CloseHandle(pi.hThread);
   CloseHandle(pi.hProcess);
}

void Application::Do()
{
   if( config.curState == NULL )
      config.curState = State::Find(config.action.c_str());

   while( config.curState != NULL )
   {
      config.action = INTERNAL_ACTION;

      config.curState = config.curState->Execute();
      if( config.curState != NULL )
      {
         config.Save(configFileName.c_str());
         if( config.curState->IsSelfUpdate() )
            break;
      }
   }
}

void Application::ResetVersion()
{
   config.version = config.saveVersion;
}

void Application::SetVersion(const wchar_t* version, int size)
{
   config.saveVersion = config.version;
   config.version.assign(version, size);
}

void Application::CommitUpdate()
{
   DeleteFile(configFileName.c_str());
}

bool Application::SaveConfig(State* curState)
{
   State *sv = config.curState;
   config.curState = curState;

   bool ret = config.Save(configFileName.c_str());
   config.curState = sv;
   return ret;
}

void Application::GetUpdateFolder(std::wstring *fn)
{
   GetAppFolder(fn);
   fn->append(L"Update");
   CreateDirectory(fn->c_str(), NULL);
   fn->append(L"\\");
}

void Application::GetAppFolder(std::wstring *fn)
{
   wchar_t buf[MAX_PATH];
   GetModuleFileName(hInstance, buf, sizeof(buf)/sizeof(buf[0]));

   const wchar_t *p = wcsrchr(buf, L'\\');
   if( p == NULL )
      fn->assign(L".\\");
   else
      fn->assign(buf, p - buf + 1);
}

void Application::GetProgName(std::wstring *name)
{
   wchar_t buf[MAX_PATH];
   GetModuleFileName(hInstance, buf, sizeof(buf)/sizeof(buf[0]));
   name->assign(buf);
}

const wchar_t EVENT_NAME[] = L"NplConnectEvent";
void Application::AddNetListner()
{
   wchar_t buf[MAX_PATH+2];
   NOTIFICATIONCONDITION nc;

   nc.ctComparisonType = REG_CT_GREATER_OR_EQUAL;
   nc.TargetValue.dw = 1;  
   nc.dwMask = 0xFFFFFFFF;

   GetModuleFileName(hInstance, buf, sizeof(buf)/sizeof(buf[0]));
   std::wstring cmdLine(buf);
   cmdLine += L" \"";
   cmdLine += configFileName;
   cmdLine += L"\"";


   RegistryNotifyApp(SN_CONNECTIONSCOUNT_ROOT, SN_CONNECTIONSCOUNT_PATH, SN_CONNECTIONSCOUNT_VALUE,
      EVENT_NAME, cmdLine.c_str(), NULL, NULL, 0, 0, &nc);

}

void Application::RemoveListner()
{
   RegistryStopNotification(EVENT_NAME);
}

//
//------------------------------------------------ ProgConfig -------------------------------------
//
ProgConfig::ProgConfig() : curState(NULL)
{
}

bool ProgConfig::AddLoad(FILE *rd)
{
   if( action.compare(INTERNAL_ACTION) != 0 ) return true;
   return (State::Load(this, rd) && curState != NULL);
}

bool ProgConfig::AddSave(FILE* wr) const
{
   if( action.compare(INTERNAL_ACTION) != 0 ) return true;
   if( curState == NULL ) return false;

   curState->WriteState(wr);
   return true;
}


static bool logStarted = false;
void Log(const char* msg, ... )
{
   HINSTANCE hInst = app.GetInstance();
   if( hInst == INVALID_HANDLE_VALUE || hInst == 0 )
      return;

   wchar_t name[MAX_PATH], *p;
   GetModuleFileName((HMODULE)hInst, name, sizeof(name)/sizeof(name[0]));
   p = wcsrchr(name, L'.');
   if( p == NULL ) wcscat(p, L".txt");
   else wcscpy(p, L".txt");

   if( !logStarted )
   {
      DeleteFile(name);
      logStarted = true;
   }

   va_list args;
   va_start(args, msg);

   FILE *file = _wfopen(name, L"at");
   if( file )
   {
      SYSTEMTIME st;
      GetLocalTime(&st);
      fprintf(file, "%02d:%02d:%02d ", st.wHour, st.wMinute, st.wSecond);

      vfprintf(file, msg, args);
      fputs("\n", file);

      fclose(file);
   }
}

/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * run action
 *
 * ert   03/07/2010   creating
 */
#include "stdafx.h"
#include "xml.h"
#include "event.h"
#include "objdef.h"
#include "sessobj.h"
#include "session.h"
#include "parse.h"
#include "actresolver.h"
#include "server.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>


using namespace GRServer;

class RunAction : public IActionExecutor
{
public:
   RunAction(const std::wstring& object, const std::wstring& startFolder, const std::wstring& params);

   virtual bool Do(Session* session, SessionObject* sourceObject, const std::vector<Token>& params, Action& action);

protected:
   std::wstring object;
   std::wstring startFolder;
   std::wstring params;
};

class RunLoader : public IActionExecutorLoader, IXmlHandler
{
public:
   RunLoader();
   virtual void Load(ActionLoader* prevHandler, const IXmlHandler::Attributes& attributes);

   virtual void StartElement(const std::wstring& name, const Attributes& atts) {}
   virtual void EndElement(const std::wstring& name);
   virtual void CharacterData(const std::wstring& name);

   virtual bool IsError() const { return (error.empty() == false); }
   virtual const wchar_t* GetError() const { return error.c_str(); }

protected:
   std::wstring error;
   ActionLoader* handler;

   std::wstring object;
   std::wstring params;
   std::wstring startFolder;
};

static RunLoader runLoader;

//
//----------------------- PutAction --------------------------
//
RunLoader::RunLoader()
{
   Action::Register(L"run", this);
}

void RunLoader::Load(ActionLoader* prevHandler, const IXmlHandler::Attributes& atts)
{
   error.clear();

   handler = prevHandler;
   handler->owner->SetHandler(this);

   params.clear();
   startFolder.clear();

   std::wstring val;
   if( !atts.Find(&object, L"object") )
   {
      error = L"Нет тэга object.action";
   } else
   {
      atts.Find(&startFolder, L"startFolder");
   }
}

void RunLoader::EndElement(const std::wstring& name)
{
   if( error.empty() )
      handler->Add(new RunAction(object, startFolder, params));

	owner->SetHandler(handler);
}

void RunLoader::CharacterData(const std::wstring& name)
{
   if( error.empty() )
   {
      params = name;
   }
}

RunAction::RunAction(const std::wstring& object, const std::wstring& startFolder, const std::wstring& params)
{
   this->object = object;
   this->startFolder = startFolder;
   this->params = params;
}

#ifdef UNIX
bool RunAction::Do(Session* session, SessionObject* sourceObject, const std::vector<Token>& params, Action& action)
{
   return true;
}
#else
bool RunAction::Do(Session* session, SessionObject* sourceObject, const std::vector<Token>& params, Action& action)
{
   USES_CONVERSION;

   PROCESS_INFORMATION pi;
   STARTUPINFO si;

   memset(&si, 0, sizeof(si));
   si.cb = sizeof(si);
   si.lpDesktop = L"WinSta0\\Default";

   std::wstring program(object);
   bool localPath = !( program.at(1) == L':' || (program.at(0) == L'\\' && program.at(1) == L'\\'));
   if( localPath )
   {
      const char* basePath = session->Config().ExchangeFolder();
      program = A2W(basePath) + program;
   }

   wchar_t cmd[1000];
   wcscpy(cmd, this->params.c_str());

   bool ret = (CreateProcess(program.c_str(), cmd, NULL, NULL, TRUE, CREATE_NEW_CONSOLE, NULL,
      (startFolder.empty()) ? NULL : startFolder.c_str(), &si, &pi) == TRUE);

   if( ret )
   {
      CloseHandle(pi.hThread);
      CloseHandle(pi.hProcess);
   }

   return true;
}
#endif

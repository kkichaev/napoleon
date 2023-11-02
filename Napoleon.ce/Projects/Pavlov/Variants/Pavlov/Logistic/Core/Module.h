/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Napoleon Logistic
*
*  ert   02/09/2010   creating
*/
#ifndef __MAIN_LOGISTIC_H
#define __MAIN_LOGISTIC_H

#include <string>

#include <atlapp.h>

#include <Reflection.h>
#include <TypeHolder.h>
#include <StdConsts.h>
#include <BaseFrame.h>
#include <ObjImpl.h>
#include <ServObject.h>

#define UPDATE_CATEGORY L"lgtpda"
#define DEFAULT_BASE "NplLgt.sdb"

class Application : public CAppModule
{
public:
   Application();
   ~Application();

   void MakeFileName(std::wstring *fullName, const wchar_t *fileName);

   void WaitThreadComplete(HANDLE thread);

   // DB function
   void BaseRemove();
   void DataClose();
   void DataInit(const char *dbName);

   IFrame* GetFrame() const { return frame; }
   void SetFrame(IFrame *_frame) { frame = _frame; }

   int Sync(std::wstring *answer, IProgressIndicator *pi);

   int ObjectExchange(IServObject *object, const wchar_t* command, std::wstring *answer, IProgressIndicator *pi);

   void ShowErrorBox(long ec, const std::wstring& answer, const wchar_t* prefix);

   const AgentsImpl* Agent() const { return (agentInited) ? &agent : NULL; }
   void SetAgent(const AgentsImpl& a);
   void ClearAgent() { agentInited = false; }

protected:
   IFrame *frame;
   AgentsImpl agent;
   bool agentInited;
};

extern Application _Module;

#endif

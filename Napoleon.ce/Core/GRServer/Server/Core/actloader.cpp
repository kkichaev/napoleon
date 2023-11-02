/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * ActionLoader
 *
 * ert   10/03/2010   creating
 */
#include "stdafx.h"
#include "xml.h"
#include "event.h"
#include "objdef.h"

using namespace GRServer;

class Actions : public std::map<std::wstring, Action*>
{
public:
   Actions() {}

   ~Actions()
   {
      iterator i = begin();
      for( ; i != end(); i++ )
         delete i->second;
   }

   Action* Find(const std::wstring& name)
   {
      iterator fnd = find(name);
      return (fnd == end()) ? NULL : fnd->second;
   }

} actions;


typedef std::map<std::wstring, IActionExecutorLoader*> LoaderMap;

#ifdef UNIX
static LoaderMap loaders __attribute__ ((init_priority (300)));
#else
#pragma warning(disable : 4073)
#pragma init_seg(lib)
static LoaderMap loaders;
#endif

void Action::Register(const std::wstring& name, IActionExecutorLoader* loader)
{
   loaders[name] = loader;
}

void ActionLoader::StartElement(const std::wstring& name, const Attributes& atts)
{
   switch(CurState())
   {
   case Error:
      break;
   case Initial:
      if( name.compare(L"action") == 0 )
         SetState(stAction, atts);
      break;
   case stAction:
      if( name.compare(L"params") == 0 )
         SetState(Params, atts);
      else if( name.compare(L"do") == 0 )
         SetState(Do, atts);
      break;
   case Params:
      if( name.compare(L"param") == 0 )
         SetState(Param, atts);
      break;
   case Do:
   {
      LoaderMap::iterator fnd = loaders.find(name);
      if( fnd != loaders.end() )
      {
         fnd->second->Load(this, atts);
         break;
      }
   }
   default:
      break;
   }
}

void ActionLoader::SetState(State state, const Attributes& atts)
{
   if( IsError() ) return;

   std::wstring val;

   states.push_back(state);
   switch(state)
   {
   case stAction:
      actionName.clear();
      actions.clear();
      params.clear();
      atts.Find(&actionName, L"name");
      break;
   case Param:
      if( atts.Find(&val, L"name") )
         params.push_back(val);
      else
      {
         errorText = L"Нет аттрбиута 'param.name'";
         states.push_back(Error);
      }
      break;
   default:
      break;
   }
}

void ActionLoader::PopState(const std::wstring& name)
{
   State state;
   if( (states.size() == 0) || ((state = states.back()) == Error) )
   {
      Attributes atts(NULL);
      SetState(Error, atts);
      errorText = L"Нет тэга закрытия";
      return;
   }

   if( name.compare(L"actions") == 0 )
   {
      delete this;
      return;
   }

   states.pop_back();
   switch( state )
   {
   case stAction:
      if( !actionName.empty() && actions.size() > 0 )
         ::actions[actionName] = new Action(params, actions);
      break;
   default:
      break;
   }
}

bool Action::LoadXml(IXmlHandler* prevHandler)
{
	prevHandler->owner->SetHandler(new ActionLoader(prevHandler));
   return true;
}

Action* Action::Get(const std::wstring& name)
{
   return ::actions.Find(name);
}

#ifdef UNIX
#include <unistd.h>
#include <time.h>

uint32_t getTick() {
   struct timespec ts;
   unsigned theTick = 0U;
   clock_gettime(CLOCK_REALTIME, &ts);
   theTick = ts.tv_nsec / 1000000;
   theTick += ts.tv_sec * 1000;
   return theTick;
}

#else

inline uint32_t getTick() { return GetTickCount(); }

#endif

static void MakeActionName(std::wstring* str)
{
   wchar_t buf[100];
   wsprintf(buf, L"%X", getTick());
   str->assign(buf);
}

NonameActionLoader::NonameActionLoader(std::wstring* name, IXmlHandler* prevHandler) :
   ActionLoader(prevHandler)
{
   MakeActionName(&actionName);
   name->assign(actionName);
   states.push_back(stAction);
}

void NonameActionLoader::PopState(const std::wstring& name)
{
   if (name.compare(L"do") == 0)
   {
      // we need propagate action to event loader
      if (actions.size() > 0)
         ::actions[actionName] = new Action(params, actions);
      else
      {
      }

      delete this;
   }
}
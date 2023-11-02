/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * События и Действия
 *
 * ert   10/03/2010   creating
 */ 

/*
 Тип события определяет параметры Invoke.
*/

#ifndef _GR_EVENTS_H
#define _GR_EVENTS_H

#include <vector>
#include "token.h"
#include <xml.h>

#include <ievent.h>
#include <set>

#include <mutex_t.h>

namespace GRServer {

struct IXmlHandler;
class Session;

class Event : public IEvent
{
public:

   struct Data
   {
      std::wstring action;
      std::wstring param;
   };

   Event(Type type, const std::vector<Data>& data);
	virtual ~Event();

   Type GetType() const { return type; }

   bool Fire(Session* session, SessionObject* object);
   void AddData(const std::vector<Data>& data);

   static Type EventTypeFromString(const std::wstring& type);

protected:
   Type type;
   std::vector<Data> data;
	std::set<Session*> fired;

	Mutex mutex;
};

class EventList : public std::vector<Event>
{
public:
   EventList();

   bool Fire(Event::Type type, Session* session, SessionObject* object);
   bool LoadXml(IXmlHandler* prevHandler);
   bool LoadEvent(IXmlHandler* prevHandler, const IXmlHandler::Attributes &attributes );

   iterator FindEvent(Event::Type type) const;
	bool HaveEvent(Event::Type type) const { return (FindEvent(type) != end()); }
};

class Action;
class ActionLoader;
struct IActionExecutor;
struct IActionExecutorLoader;

struct IActionExecutor
{
   virtual ~IActionExecutor() {}
   virtual bool Do(Session* session, SessionObject* sourceObject, const std::vector<Token>& params, Action& action) = 0;
};

struct IActionExecutorLoader
{
   virtual ~IActionExecutorLoader() {}
   virtual void Load(ActionLoader* prevHandler, const IXmlHandler::Attributes& attributes) = 0;
};

class Action
{
public:
   Action(const std::vector<std::wstring>& params, const std::vector<IActionExecutor*>& actions);
   ~Action();

   void AddExecutor(IActionExecutor* e) { actions.push_back(e); }

   bool Do(Session* session, SessionObject* sourceObject, const std::vector<Token>& params);

   const std::vector<std::wstring>& Params() const { return params; }

   //----------- static methods ----------------------

   static bool LoadXml(IXmlHandler* prevHandler);
   static void Register(const std::wstring& name, IActionExecutorLoader* loader); // don't delete loaders

   static Action* Get(const std::wstring& name);

protected:
   std::vector<std::wstring> params; // имена параметров (значения приходят в Do)
   std::vector<IActionExecutor*> actions;
};

// самоуничтожается на </actions>
class ActionLoader : public IXmlHandler
{
public:
   ActionLoader(IXmlHandler* prevHandler)
   {
      states.push_back(Initial);
      this->prevHandler = prevHandler;
   }

   ~ActionLoader()
   {
		prevHandler->owner->SetHandler(prevHandler);
   }

   virtual void StartElement(const std::wstring& name, const Attributes& atts);
   virtual void CharacterData(const std::wstring& name) {}
   virtual void EndElement(const std::wstring& name)
   {
      if (!IsError() )
         PopState(name); 
   }

   virtual bool IsError() const { return (CurState() == Error); } 

   virtual const wchar_t* GetError() const { return errorText.c_str(); }

   void Add( IActionExecutor* executor ) { actions.push_back(executor); }

protected:
   enum State
   {
      Initial,
      Error,
      stAction,
      Params,
      Param,
      Do,
   };

   IXmlHandler* prevHandler;

   void SetState(State state, const Attributes& atts);
   virtual void PopState(const std::wstring& name);
   State CurState() const { return (states.size()>0) ? states.back() : Error; }

   std::vector<State> states;

   std::wstring actionName;
   std::vector<std::wstring> params; // имена параметров (значения приходят в Do)
   std::vector<IActionExecutor*> actions;

   std::wstring errorText;
};

class NonameActionLoader : public ActionLoader
{
public:
   NonameActionLoader(std::wstring* name, IXmlHandler* prevHandler);

private:
   virtual void PopState(const std::wstring& name);
};

} // namespace GRServer

#endif
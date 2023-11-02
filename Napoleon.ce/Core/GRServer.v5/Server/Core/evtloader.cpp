/*
 * Copyright (C), 2009, ����� �������
 *
 * EventList::LoadXml
 *
 * ert   12/03/2010   creating
 */
#include "stdafx.h"
#include "xml.h"
#include "event.h"
#include "objdef.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

// ���������������� �� </actions>
class EventsLoader : public IXmlHandler
{
public:
   EventsLoader(IXmlHandler* prevHandler, EventList *eventList)
   {
      states.push_back(Initial);
      this->prevHandler = prevHandler;
      this->eventList = eventList;
   }

   ~EventsLoader()
   {
		prevHandler->owner->SetHandler(prevHandler);
   }

   virtual void StartElement(const std::wstring& name, const Attributes& atts);
   virtual void CharacterData(const std::wstring& name);
   virtual void EndElement(const std::wstring& name)
   {
      if (!IsError() )
         PopState(name);
   }

   virtual bool IsError() const { return (CurState() == Error); }

   virtual const wchar_t* GetError() const { return errorText.c_str(); }

protected:
   enum State
   {
      Initial,
      Error,
      stEvent,
      stAction,
   };

   IXmlHandler* prevHandler;

   void SetState(State state, const Attributes& atts);
   void PopState(const std::wstring& name);
   State CurState() const { return (states.size()>0) ? states.back() : Error; }

   std::vector<State> states;

   EventList* eventList;
   Event::Type type;
   Event::Data curData;
   std::vector<Event::Data> data;

   std::wstring errorText;
};

class EventLoader : public EventsLoader
{
public:
   EventLoader(IXmlHandler* prevHandler, EventList *eventList, const IXmlHandler::Attributes &attributes) : EventsLoader(prevHandler, eventList)
   {
      SetState(stEvent, attributes);
   }

   virtual void EndElement(const std::wstring& name)
   {
      EventsLoader::EndElement(name);
      if( name.compare(L"event") == 0 )
      {
         delete this;
      }
   }
};

void EventsLoader::StartElement(const std::wstring& name, const Attributes& atts)
{
   switch(CurState())
   {
   case Error:
      break;
   case Initial:
      if( name.compare(L"event") == 0 )
         SetState(stEvent, atts);
      break;
   case stEvent:
      if( name.compare(L"action") == 0 )
         SetState(stAction, atts);
      break;
   default:
      break;
   }
}

void EventsLoader::CharacterData(const std::wstring& name)
{
   switch( CurState() )
   {
   case stAction:
      //if( !curData.action.empty() )
      curData.param = name;
      break;
   default: break;
   }
}

void EventsLoader::SetState(State state, const Attributes& atts)
{
   if( IsError() ) return;

   std::wstring val;

   states.push_back(state);
   switch(state)
   {
   case stEvent:
      data.clear();
      if( atts.Find(&val, L"type") )
         type = Event::EventTypeFromString(val);
      break;
   case stAction:
      if (atts.Find(&curData.action, L"name") == false)
      {
         owner->SetHandler(new NonameActionLoader(&curData.action, this));
         //curData.action.clear();
      }
      break;
   default: break;
   }
}

void EventsLoader::PopState(const std::wstring& name)
{
   State state;
   if( (states.size() == 0) || ((state = states.back()) == Error) )
   {
      Attributes atts(NULL);
      SetState(Error, atts);
      errorText = L"No closing tag";
      return;
   }

   if( name.compare(L"events") == 0 )
   {
      delete this;
      return;
   }

   states.pop_back();
   switch( state )
   {
   case stAction:
		/*
		<event type = "onLoad">
			<action>ResizePhoto($object.id, $Feature.MAX_PIC_SIZE)</action>
		</event>
		*/
		//if( !curData.action.empty() )
      data.push_back(curData);
      break;
   case stEvent:
      if( data.size() > 0 )
      {
         bool loaded = false;
         EventList::iterator i = eventList->begin();
         for( ; i != eventList->end(); i++ )
         {
            if( (*i).GetType() == type )
            {
               (*i).AddData(data);
               loaded = true;
               break;
            }
         }
         if( !loaded )
            eventList->push_back(Event(type, data));
      }
      break;
   default: break;
   }
}

bool EventList::LoadXml(IXmlHandler* prevHandler)
{
	prevHandler->owner->SetHandler(new EventsLoader(prevHandler, this));
   return true;
}

bool EventList::LoadEvent(IXmlHandler* prevHandler, const IXmlHandler::Attributes &attributes)
{
	prevHandler->owner->SetHandler(new EventLoader(prevHandler, this, attributes));
   return true;
}

EventList::iterator EventList::FindEvent(Event::Type type) const
{
   iterator i = const_cast<EventList*>(this)->begin();
   for( ; i !=  const_cast<EventList*>(this)->end(); i++ )
   {
      if( i->GetType() == type )
         return i;
   }
   return i;
}

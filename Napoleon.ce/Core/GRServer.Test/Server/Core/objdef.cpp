/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * ObjectDef impl.
 *
 * ert   26/09/2009   creating
 */
#include "stdafx.h"
#include "objdef.h"
#include "sessobj.h"
#include "xml.h"
#include "server.h"
#include "chgdef.h"
#include "loaders.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
using namespace std;

ObjectDef::ObjectSet ObjectDef::objects;
SourceDefList ObjectDef::commonSource;
std::wstring ObjectDef::commonFieldData;
EventList ObjectDef::globalEvents;

class ObjectDefLoader : public ObjectDef, public IXmlHandler
{
public:

   ObjectDefLoader()
   {
      states.push_back(Initial);
      loader = NULL;
   }

   ~ObjectDefLoader()
   {
      delete loader;
   }

   virtual void StartElement(const std::wstring& name, const Attributes& atts);
   virtual void CharacterData(const std::wstring& name);
   virtual void EndElement(const std::wstring& name)
   {
      if (!IsError() )
         PopState();
   }

   virtual bool IsError() const { return (CurState() == Error); }

   virtual const wchar_t* GetError() const { return errorText.c_str(); }

protected:
   enum State
   {
      Initial,
      Error,
      ServerDefs,
      Objects,
      CommonSection,
      CommonObject,
      ObjectSources,
      ObjectSource,
      SourceParam,
      CommonField,
      CommonFieldData,
      FieldData,
      Object,
      ObjectMember,
      ObjectField,
      ObjectFieldData,
   };

   void SetState(State state, const Attributes& atts);
   void PopState();
   State CurState() const { return (states.size()>0) ? states.back() : Error; }

   std::vector<State> states;

   std::wstring errorText;

   ObjectLoader* loader;
 };

//
//--------------------------------------- ObjectDef ------------------------------------------
//
ObjectDef::ObjectDef() : sendLimit(0)
{
   flags = 0;
	needDebug = false;
}

bool ObjectDef::HaveExecutableFields(int mask) const
{
	Fields::const_iterator fi = fields.begin();
	for (; fi != fields.end(); fi++)
	{
		if( (fi->format.flags & mask) != 0)
			return true;
	}
	return false;
}

void ObjectDef::CreateFKConstraint(CString** text, CString** indexText, const CVector<MemberFormat>& fkFields, wchar_t escape) const
{
   if( fkFields.size() == 0 ) return;

   *indexText = new CString(L"CREATE INDEX fki_");
   (*indexText)->append(tableName);
   (*indexText)->append(L" ON ");
   (*indexText)->append(escape);
   (*indexText)->append(tableName);
   (*indexText)->append(escape);
   (*indexText)->append(L" (");

   wstring fTable;

   *text = new CString(L"CONSTRAINT fk_");
   (*text)->append(tableName);
   (*text)->append(L" FOREIGN KEY (");

   CVector<MemberFormat>::const_iterator ki = fkFields.begin();
   while( ki != fkFields.end() )
   {
      if( ki != fkFields.begin() )
      {
         (*text)->append(L",");
         (*indexText)->append(L",");
      }
      else
      {
         size_t pos = ki->name.find_last_of(L'$');
         fTable.assign(ki->name.substr(0, pos));
      }
      (*indexText)->append(escape);
      (*indexText)->append(ki->name);
      (*indexText)->append(escape);

      (*text)->append(escape);
      (*text)->append(ki->name);
      (*text)->append(escape);
      ki++;
   }
   (*text)->append(L") REFERENCES ");
   (*text)->append(escape);
   (*text)->append(fTable);
   (*text)->append(escape);
   (*text)->append(L" (");

   ki = fkFields.begin();
   while( ki != fkFields.end() )
   {
      if( ki != fkFields.begin() )
         (*text)->append(L",");

      size_t pos = ki->name.find_last_of(L'$');
      (*text)->append(escape);
      (*text)->append(ki->name.substr(pos+1));
      (*text)->append(escape);
      ki++;
   }

   (*text)->append(L") ON DELETE CASCADE");
   (*indexText)->append(L")");
}

bool ObjectDef::LoadFK(CVector<MemberFormat>** formats, CVector<Field> **fields) const
{
   if( fields != NULL )
      *fields = new CVector<Field>();
   if( formats != NULL )
      *formats = new CVector<MemberFormat>();

   if( parent.empty() ) return false;

   const ObjectDef* pobj = ObjectDef::Get(parent);
	if (pobj == NULL) return false;

   Members::const_iterator keyI = pobj->members.find(PRIMARY_KEY_STR);
   if( keyI == pobj->members.end() ) return false;

   const std::wstring& str = (*keyI->second.begin() == L'"') ? keyI->second.substr(1, keyI->second.size()-2) : keyI->second;

   wstring::size_type lastPos = str.find_first_not_of(L",", 0);
   wstring::size_type pos     = str.find_first_of(L",", lastPos);

   while (string::npos != pos || string::npos != lastPos)
   {
      wstring::size_type size = pos - lastPos;
      wstring::size_type start = lastPos;
      if( *str.begin() == L'"' ) start++;
      if( *str.rbegin() == L'"' ) size--;

      const std::wstring& vstr = str.substr(start, size);
      start = vstr.find_first_not_of(L' ');
      size = vstr.find_last_not_of(L' ');
      if( size >= start )
      {
         Field f;
         f.format.name = vstr.substr(start, size - start + 1);

         ObjectDef::Fields::const_iterator fnd = pobj->fields.find(f);
         if( fnd != pobj->fields.end() )
         {
            std::wstring fieldName(fnd->format.name);
            fieldName.insert(0, pobj->tableName + L"$");
            if( formats != NULL )
            {
               MemberFormat mf = fnd->format;
               mf.name = fieldName;
               (*formats)->push_back(mf);
            }

            if( fields != NULL )
            {
               Field f = (*fnd);
               f.format.name = fieldName;
               (*fields)->push_back(f);
            }
         }
      }

      lastPos = str.find_first_not_of(L",", pos);
      pos = str.find_first_of(L",", lastPos);
   }
   return true;
}

void ObjectDef::RemoveField(const std::wstring& name)
{
   Field f;
   f.format.name = name;
   Fields::iterator fnd = fields.find(f);
   if( fnd != fields.end() )
   {
      if( fnd->format.type == MemberFormat::mtObject )
      {
         ObjectDef od;
         od.name = fnd->data;
         ObjectDef::ObjectSet::iterator of = objects.find(od);
         if( of != objects.end() )
            objects.erase(of);
      }
      fields.erase(fnd);
   }
}

void ObjectDef::RemoveMember(const std::wstring& name)
{
   Members::iterator fnd = members.find(name);
   if( fnd != members.end() )
      members.erase(fnd);
}

void ObjectDef::PutField(const Field& f)
{
   std::pair<Fields::iterator, bool> ib = fields.insert(f);
   if( ib.second == false )
      *((Field*)&(*ib.first)) = f;
}

void ObjectDef::PutMember(const std::wstring& name, const std::wstring& value)
{
   members[name] = value;
}

void ObjectDef::AddMemberArray(const std::wstring& name, const std::wstring& value)
{
	memberArray[name].push_back(value);
}

void ObjectDef::AddSource(const SourceDef& _source)
{
   source.push_back(_source);
}

void ObjectDef::ReplaceSources(const SourceDefList& sources)
{
   this->source = sources;
}

void ObjectDef::Clear()
{
   objects.clear();
   commonFieldData.clear();
   globalEvents.clear();
   commonSource.clear();
}

void ObjectDef::RemoveEvent(const std::wstring& type)
{
   IEvent::Type t = Event::EventTypeFromString(type);
   EventList::iterator i = events.FindEvent(t);
   if( i != events.end() )
      events.erase(i);
}

void ObjectDef::AddEvents(const EventList& events)
{
   EventList::const_iterator i = events.begin();
   for( ; i != events.end(); i++ )
   {
      EventList::iterator fnd = this->events.FindEvent(i->GetType());
      if( fnd != this->events.end() )
         this->events.erase(fnd);
      this->events.push_back(*i);
   }
}

bool ObjectDef::Load(const std::string& fileName)
{
   ObjectDefLoader loader;
	XmlParser *p = new XmlParser();
   bool ret = p->Parsing(fileName, &loader);
   if( !ret )
   {
      USES_CONVERSION;
      const char *errText = W2A(p->GetError());
      if( *errText == '\0' )
         gServer->AddError(true, "Ошибка при обработке файла '%s'", fileName.c_str());
      else
         gServer->AddError(true, errText);
   }
	delete p;

   return ret;
}

void ObjectDef::ModifyObjects()
{
   UpdateObjectDef(&objects);

   ObjectDef::ObjectSet::iterator i = objects.begin();
   for( ; i != objects.end(); i++ )
   {
      SourceDefList::const_iterator si = i->source.begin();

      if( i->tableName.empty() )
      {
         ObjectDef od;
         od.name = i->parent;
         ObjectDef::ObjectSet::const_iterator fnd = objects.find(od);
         if( fnd != objects.end() )
         {
            size_t pos = i->name.find_last_of(L'$');
            const_cast<std::wstring&>(i->tableName) = fnd->tableName + L"$" + i->name.substr(pos+1);
         }
         else
            const_cast<std::wstring&>(i->tableName) = i->name;
      }

      *((int*)(&i->flags)) &= (~(IObjectDef::Internal | IObjectDef::HaveEFields));
      for( ; si != i->source.end(); si++ )
      {
         if( si->type == stInternal )
         {
            *((int*)(&i->flags)) |= IObjectDef::Internal;
            break;
         }
      }

      ObjectDef::Fields::const_iterator fi = i->fields.begin();
      for( ; fi != i->fields.end(); fi++ )
      {
         if( (fi->format.flags & (MemberFormat::ExecOnPut | MemberFormat::ExecOnGet)) != 0 )
         {
            *((int*)(&i->flags)) |= IObjectDef::HaveEFields;
            break;
         }
      }
   }
}

bool ObjectDef::Load(const Binary& data)
{
   ObjectDefLoader loader;
	XmlParser *p = new XmlParser();
   bool ret = p->Parsing(data, &loader);
   if( !ret )
   {
      USES_CONVERSION;
      gServer->AddError(true, W2A(p->GetError()));
   }
	delete p;

   return ret;
}

const ObjectDef* ObjectDef::Get(const std::wstring& objName)
{
   ObjectDef od;
   od.name = objName;

   ObjectSet::const_iterator fnd = objects.find(od);
   return (fnd == objects.end()) ? (const ObjectDef*)NULL : &(*fnd);
}

bool ObjectDef::HaveEvent(Event::Type type)
{
	return (globalEvents.FindEvent(type) != globalEvents.end());
}

bool ObjectDef::Fire(Event::Type type, Session* session, SessionObject* param)
{
   return globalEvents.Fire(type, session, param);
}

//void ObjectDef::GetObjectsName(std::vector<std::wstring> *names, DWORD flags)
void ObjectDef::GetObjectsName(CVector<CString> **names, DWORD flags)
{
   *names = new CVector<CString>();
   ObjectSet::const_iterator i = objects.begin();
   for( ; i != objects.end(); i++ )
   {
      if( flags == 0 || ((i->flags & flags) != 0) )
         (*names)->push_back(i->name);
   }
}

const IObjectData::Field* ObjectDef::FindField(const std::wstring& name) const
{
   IObjectData::Field f;
   f.format.name = name;

   Fields::const_iterator fnd = fields.find(f);
   return (fnd == fields.end()) ? NULL : &(*fnd);
}

//
//------------------------------- ObjDefSvc ------------------------------------
//
struct ObjDefSvc : public IObjectDef
{
   virtual bool Load(const std::string& fileName)
   {
      return ObjectDef::Load(fileName);
   }

   virtual const IObjectData* Get(const std::wstring& objName)
   {
      return ObjectDef::Get(objName);
   }

   //virtual void GetObjectsName(std::vector<std::wstring>* names, DWORD flags)
   virtual void GetObjectsName(CVector<CString>** names, DWORD flags)
   {
      ObjectDef::GetObjectsName(names, flags);
   }

   virtual bool Fire(IEvent::Type type, ISession* session, IObject* param = NULL)
   {
      return false;
      //return ObjectDef::Fire(type, session, param);
   }
};

static ObjDefSvc objDefSvc;
IObjectDef* ObjectDef::GetService()
{
   return &objDefSvc;
}

//
//------------------------------- ObjectDefLoader ------------------------------------
//
void ObjectDefLoader::StartElement(const std::wstring &name, const GRServer::IXmlHandler::Attributes &atts)
{
   switch(CurState())
   {
   case Error:
      break;
   case Initial:
      if( name.compare(L"serverDefs") == 0 )
         SetState(ServerDefs, atts);
      break;
   case ServerDefs:
      if( name.compare(L"objects") == 0 )
         SetState(Objects, atts);
      else if( name.compare(L"actions") == 0 )
         Action::LoadXml(this);
      else if( name.compare(L"events") == 0 )
         globalEvents.LoadXml(this);
      else if( name.compare(L"feature") == 0 )
         LoadFeature(this, atts);
      break;
   case Objects:
      if( name.compare(L"common") == 0 )
         owner->SetHandler(new SkipTagHandler(this, L"common"));
      else if( name.compare(L"object") == 0 )
      {
         delete loader;
         loader = new ObjectLoader(&objects, this, atts, L"", NULL);
      } else if( name.compare(L"modify") == 0 )
         LoadModifyData(&objects, this, atts, name);
      break;
   default :break;
   }
}

void ObjectDefLoader::CharacterData(const std::wstring &name)
{
}

void ObjectDefLoader::SetState(State state, const Attributes& atts)
{
   if( IsError() ) return;

   states.push_back(state);
}

void ObjectDefLoader::PopState()
{
   State state;
   if( (states.size() == 0) || ((state = states.back()) == Error) )
   {
      Attributes atts(NULL);
      errorText = L"Нет тэга закрытия";
      SetState(Error, atts);
      return;
   }

   states.pop_back();
}

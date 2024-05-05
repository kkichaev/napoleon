/*
 * Copyright (C), 2009-2011, Денис Мосягин
 *
 * modify tag of objdef
 *
 * ert   13/07/2011   creating
 */
#include "stdafx.h"
#include "chgdef.h"
#include "loaders.h"
#include <servobj.h>
#include "server.h"
#include "event.h"

using namespace GRServer;

class IChanger : public IXmlHandler
{
public:
   IChanger(const wchar_t* _endElement) : prevHandler(NULL), endElement(_endElement) {}
   virtual ~IChanger() {}

   void Load(IXmlHandler* prevHandler);

   virtual void Modify(ObjectDef* object) const = 0;

   virtual void StartElement(const std::wstring& name, const Attributes& atts) {}
   virtual void EndElement(const std::wstring& name)
   {
      if( name.compare(endElement) == 0 )
         RestoreHandler();
   }

   virtual void CharacterData(const std::wstring& name) {}

   virtual bool IsError() const { return false; }
   virtual const wchar_t* GetError() const { return L""; }

   void RestoreHandler();
protected:
   IXmlHandler* prevHandler;
   const wchar_t* endElement;
};

class FieldRemover : public IChanger
{
public:
   FieldRemover(const IXmlHandler::Attributes& atts) : IChanger(L"field") { atts.Find(&name, L"name"); }
   FieldRemover(const IXmlHandler::Attributes& atts, const wchar_t* endElement) : IChanger(endElement) { atts.Find(&name, L"name"); }

   virtual void Modify(ObjectDef* object) const { object->RemoveField(name); }

protected:
   std::wstring name;
};

class MemberRemover : public FieldRemover
{
public:
   MemberRemover(const IXmlHandler::Attributes& atts) : FieldRemover(atts, L"member") {}

   virtual void Modify(ObjectDef* object) const { object->RemoveMember(name); }
};

class FieldUpdate : public IChanger
{
public:
   FieldUpdate(ObjectDef::ObjectSet* objSet, const std::wstring& objName,
      const IXmlHandler::Attributes& atts, IXmlHandler* prevHandler) : IChanger(L"")
   {
      loader = new FieldLoader(objSet, &field, prevHandler, atts, objName);
   }

   ~FieldUpdate() { delete loader; }

   virtual void Modify(ObjectDef* object) const { object->PutField(field); }

protected:
   ObjectDef::Field field;
   FieldLoader* loader;
};

class FieldChanger : public IChanger
{
public:
	FieldChanger(ObjectDef::ObjectSet* objSet, const std::wstring& objName,
		const IXmlHandler::Attributes& atts, IXmlHandler* prevHandler) : IChanger(L"change")
	{
		Load(prevHandler);

		std::wstring tstr;

		field.format.name = objName;
		if (atts.Find(&field.data, L"data"))
		{
			changedTags.push_back(FieldChanger::Tags::Data);
		}

		if (atts.Find(&tstr, L"width"))
		{
			changedTags.push_back(FieldChanger::Tags::Width);
			field.width = _wtoi(tstr.c_str());
		}
	}

	~FieldChanger() {}

	virtual void Modify(ObjectDef* object) const 
	{
		ObjectDef::Fields::iterator fnd = object->fields.find(field);
		if (fnd != object->fields.end())
		{
			ObjectDef::Field& dest = (ObjectDef::Field&)*fnd;
			std::vector<Tags>::const_iterator i = changedTags.begin();
			for (; i != changedTags.end(); i++)
			{
				if (*i == FieldChanger::Tags::Data)
				{
					dest.data.assign(field.data);
				}
				else if (*i == FieldChanger::Tags::Width)
				{
					dest.width = field.width;
				}
			}
		}
	}

protected:
	enum class Tags { Data, Width };
	
	IObjectData::Field field;
	std::vector<Tags> changedTags;
};

class SourcesUpdate : public IChanger
{
public:
   SourcesUpdate(IXmlHandler* prevHandler, const Attributes& atts) : IChanger(L"")
   {
      loader = new SourcesLoader(NULL, &sources, prevHandler, atts);
   }

   ~SourcesUpdate() { delete loader; }

   virtual void Modify(ObjectDef* object) const { object->ReplaceSources(sources); }

protected:
   SourceDefList sources;
   SourcesLoader* loader;
};

class SourceAdd : public IChanger
{
public:
   SourceAdd(IXmlHandler* prevHandler, const Attributes& atts) : IChanger(L"")
   {
      loader = new SourceLoader(NULL, &source, prevHandler, atts);
   }

   ~SourceAdd() { delete loader; }

   virtual void Modify(ObjectDef* object) const { object->AddSource(source); }

protected:
   SourceDef source;
   SourceLoader* loader;
};

class EventAdd : public IChanger
{
public:
   EventAdd(IXmlHandler* prevHandler, const IXmlHandler::Attributes& atts) : IChanger(L"") { events.LoadEvent(prevHandler, atts); }

   virtual void Modify(ObjectDef* object) const { object->AddEvents(events); }

protected:
   EventList events;
};

class EventRemover : public IChanger
{
public:
   EventRemover(const IXmlHandler::Attributes& atts) : IChanger(L"event") { atts.Find(&type, L"type"); }

   virtual void Modify(ObjectDef* object) const { object->RemoveEvent(type); }

protected:
   std::wstring type;
};

class MemberUpdate : public IChanger
{
public:
   MemberUpdate(const IXmlHandler::Attributes& atts) :IChanger(L"member") { atts.Find(&name, L"name"); }

   virtual void Modify(ObjectDef* object) const { object->PutMember(name, value); }
   virtual void CharacterData(const std::wstring& name) { value = name; }

protected:
   std::wstring name, value;
};

class MemberArrayAdd : public IChanger
{
public:
	MemberArrayAdd(const IXmlHandler::Attributes& atts) :IChanger(L"memberArray") { atts.Find(&name, L"name"); }

	virtual void Modify(ObjectDef* object) const { object->AddMemberArray(name, value); }
	virtual void CharacterData(const std::wstring& name) { value = name; }

protected:
	std::wstring name, value;
};

struct ModifyObject : public IXmlHandler
{
protected:
   enum class State { stStart, stAdd, stRemove, stChange };

   State state;
   std::wstring src;
   std::wstring dest;
   std::wstring table;
   std::wstring sendLimit;
	std::wstring sendAlways;
	std::wstring removeOnCommit;
	std::wstring needDebug;

   DWORD flags;

   std::vector<Pointer<IChanger> > changers;

   IXmlHandler* prevHandler;
   ObjectDef::ObjectSet* objects;

protected:
   virtual void StartElement(const std::wstring& name, const Attributes& atts);
   virtual void EndElement(const std::wstring& name);
   virtual void CharacterData(const std::wstring& name);

   virtual bool IsError() const { return false; }
   virtual const wchar_t* GetError() const { return L""; }

public:
   ModifyObject(ObjectDef::ObjectSet* _objects) : state(ModifyObject::State::stStart), objects(_objects), prevHandler(NULL), flags(-1) {}

   bool IsSelfModify() const { return dest.empty(); }
   bool IsChild() const { return (src.find(L'$') != std::wstring::npos) || (dest.find(L'$') != std::wstring::npos); }

   void Load(IXmlHandler* prevHandler, const IXmlHandler::Attributes& atts);
   bool Modify(ObjectDef::ObjectSet* objects) const;
};

static std::vector<Pointer<ModifyObject> > modifyObjects;

//
//------------------------------------------------------------------------------
//
void IChanger::RestoreHandler()
{
	prevHandler->owner->SetHandler(prevHandler);
}

void IChanger::Load(IXmlHandler* prevHandler)
{
   this->prevHandler = prevHandler;
	prevHandler->owner->SetHandler(this);
}

void ModifyObject::Load(IXmlHandler* prevHandler, const IXmlHandler::Attributes& atts)
{
   atts.Find(&src, L"src");
   atts.Find(&dest, L"dest");
   if( !atts.Find(&table, L"table") )
		atts.Find(&table, L"tableName");
   atts.Find(&sendLimit, L"sendLimit");
	atts.Find(&sendAlways, L"sendAlways");
	atts.Find(&removeOnCommit, L"removeOnCommit");
	atts.Find(&needDebug, L"debug");

   std::wstring tvalue;
   if (atts.Find(&tvalue, L"defaultAccess"))
   {
      DWORD flg = (tvalue.compare(L"read") == 0) ? IObjectDef::AccessFlags::ReadAccess :
         (tvalue.compare(L"none") == 0) ? IObjectDef::AccessFlags::NonAccess :
         0;
      flags = 0;
      flags |= flg;
   }

   if( dest.compare(src) == 0 )
      dest.clear();

   this->prevHandler = prevHandler;
	prevHandler->owner->SetHandler(this);
}

void ModifyObject::StartElement(const std::wstring& name, const Attributes& atts)
{
	IChanger *ic = NULL;
	bool needLoad = false;
	
	if (name.compare(L"add") == 0)
      state = ModifyObject::State::stAdd;
   else if( name.compare(L"remove") == 0 )
      state = ModifyObject::State::stRemove;
	else if (name.compare(L"change") == 0)
	{
		std::wstring name;
		if (atts.Find(&name, L"name"))
		{
			ic = new FieldChanger(objects, name, atts, this);
		} else
			state = ModifyObject::State::stChange;
	}
   else
   {
      if( name.compare(L"field") == 0 )
      {
         if( state == ModifyObject::State::stRemove )
         {
            needLoad = true;
            ic = new FieldRemover(atts);
         } else if( state == ModifyObject::State::stChange || state == ModifyObject::State::stAdd )
         {
            ic = new FieldUpdate(objects, ((dest.empty()) ? src : dest), atts, this);
         }
      } else if( name.compare(L"member") == 0 )
      {
         needLoad = true;
         if( state == ModifyObject::State::stRemove )
         {
            ic = new MemberRemover(atts);
         } else if( state == ModifyObject::State::stChange  || state == ModifyObject::State::stAdd )
         {
            ic = new MemberUpdate(atts);
         }
		}
		else if (name.compare(L"memberArray") == 0)
		{
			needLoad = true;
			if (state == ModifyObject::State::stAdd)
			{
				ic = new MemberArrayAdd(atts);
			}
		}
		else if (name.compare(L"sources") == 0)
      {
         if( state == ModifyObject::State::stChange )
         {
            ic = new SourcesUpdate(this, atts);
         }
      } else if( name.compare(L"source") == 0 )
      {
         if( state == ModifyObject::State::stAdd )
         {
            ic = new SourceAdd(this, atts);
         }
      } else if( name.compare(L"event") == 0 )
      {
         if( state == ModifyObject::State::stAdd )
         {
            ic = new EventAdd(this, atts);
         } else if( state == ModifyObject::State::stRemove )
         {
            needLoad = true;
            ic = new EventRemover(atts);
         }
      }
   }

	if (ic)
	{
		changers.push_back(ic);
		if (needLoad)
			ic->Load(this);
	}
}

void ModifyObject::EndElement(const std::wstring& name)
{
   state = ModifyObject::State::stStart;
   if( name.compare(L"modify") == 0 )
   {
		prevHandler->owner->SetHandler(prevHandler);
      return;
   }
}

void ModifyObject::CharacterData(const std::wstring& name)
{
}

static ObjectDef* CopyObjectDef(ObjectDef::ObjectSet* objects, const ObjectDef& source, const std::wstring& name, 
                                const std::wstring& table, const std::wstring& sendLimit, const std::wstring& removeOnCommit)
{
   ObjectDef dest;
   dest = source;
   dest.name = name;
   if( table.empty() == false )
      dest.tableName = table;
   if( !sendLimit.empty() )
      dest.sendLimit = _wtoi(sendLimit.c_str());
   if( !removeOnCommit.empty() )
      SetBooleanFlag(&dest.flags, removeOnCommit, IObjectDef::RemoveOnCommit);

   ObjectDef::Fields::iterator i = dest.fields.begin();
   for( ; i != dest.fields.end(); i++ )
   {
      if( i->format.type == MemberFormat::mtObject )
      {
         const ObjectDef* src = ObjectDef::Get(i->data);
         if( src != NULL )
         {
            std::wstring objName(name + L"$" + i->format.name);
            std::wstring tblName(dest.tableName + L"$" + i->format.name);
            std::wstring emptyStr;
            const_cast<std::wstring&>(i->data).assign(objName);
            ObjectDef* child = CopyObjectDef(objects, *src, objName, tblName, emptyStr, emptyStr);
            child->parent = name;
         }
      }
   }
   return const_cast<ObjectDef*>(&(*objects->insert(dest).first));
}

bool ModifyObject::Modify(ObjectDef::ObjectSet* objects) const
{
   bool ret = false;
   if( src.empty() )
   {
      return ret;
   }

   ObjectDef od;
   od.name = src;
   if( !table.empty() )
      od.tableName = table;

   ObjectDef::ObjectSet::iterator fnd = objects->find(od);
   if( fnd != objects->end() )
   {
      ObjectDef* data = const_cast<ObjectDef*>(&(*fnd));
      if( !dest.empty() )
      {
         // копирование будем делсть только если объект не создан
         od.name = dest;
         ObjectDef::ObjectSet::iterator dfnd = objects->find(od);
         if( dfnd == objects->end() )
            data = CopyObjectDef(objects, *fnd, dest, table, sendLimit, removeOnCommit);
         else
         {
            // иначе используем dest для модификации
            data = const_cast<ObjectDef*>(&(*dfnd));
            //data = NULL;
         }
      } else
      {
         if( !table.empty() )
				const_cast<std::wstring&>(fnd->tableName) = table;
         if( !sendLimit.empty() )
				(DWORD)(fnd->sendLimit) = _wtoi(sendLimit.c_str());
         if( !removeOnCommit.empty() )
				SetBooleanFlag((DWORD*)&((ObjectDef::ObjectSet::iterator)fnd)->flags, removeOnCommit, IObjectDef::RemoveOnCommit);
      }

      if( data != NULL )
      {
         if (flags != (DWORD)-1)
         {
            data->flags &= (~IObjectDef::AccessFlags::AccFlags);
            data->flags |= (flags & IObjectDef::AccessFlags::AccFlags);
         }
         
         if (!needDebug.empty())
				data->needDebug = ((_wcsicmp(needDebug.c_str(), L"true") == 0 || _wtoi(needDebug.c_str()) == 1));;

			if (sendAlways.size() > 0)
				SetBooleanFlag(&data->flags, sendAlways, IObjectDef::SendAlways);

         std::vector<Pointer<IChanger> >::const_iterator i = changers.begin();
         for( ; i != changers.end(); i++ )
            (*i)->Modify(data);
      }

      ret = true;
   }
   return ret;
}

static bool IsFeaturePresents(const IXmlHandler::Attributes& atts)
{
   std::wstring expr;
   if( atts.Find(&expr, L"expr") )
      return gServer->GetConfig().HaveFeature(expr);

   return false;
}

void GRServer::LoadFeature(IXmlHandler *prevHandler, const IXmlHandler::Attributes& atts)
{
   if( !IsFeaturePresents(atts) )
		prevHandler->owner->SetHandler(new SkipTagHandler(prevHandler, L"feature"));
}

void GRServer::LoadModifyData(ObjectDef::ObjectSet* objects, IXmlHandler *prevHandler, const IXmlHandler::Attributes& atts, const std::wstring& tag)
{
   std::wstring feature;
   if( atts.Find(&feature, L"feature") && !gServer->GetConfig().HaveFeature(feature) )
   {
		prevHandler->owner->SetHandler(new SkipTagHandler(prevHandler, tag.c_str()));
      return;
   }

   ModifyObject *mo = new ModifyObject(objects);
   modifyObjects.push_back(mo);
   mo->Load(prevHandler, atts);
}

void GRServer::UpdateObjectDef(ObjectDef::ObjectSet* objects)
{
   // 
	// сначала модифицируем верхние объекты самомодифицируемые (если объекта еще нет - добавляем в tryAgain)
	// затем child самомодифицируемые (если объекта еще нет - добавляем в tryAgain)
	// затем создаем топовые объекты копированием (там же создаются внутренние объекты)
	// затем поптыка создать объекты (tryAgain)
	// затем копирование child
	//
   std::vector<Pointer<ModifyObject> >::const_iterator i = modifyObjects.begin();
   std::vector<const ModifyObject*> tryAgain;
   for( ; i!= modifyObjects.end(); i++ )
      if( !(*i)->IsChild() && (*i)->IsSelfModify() )
      {
         if( !(*i)->Modify(objects) )
            tryAgain.push_back((const ModifyObject*)(*i));
      }

#ifdef _DEBUG
   //MessageBox(NULL, PROJECT_NAME, L"!", MB_OK);
#endif
	i = modifyObjects.begin();
	for (; i != modifyObjects.end(); i++)
		if ((*i)->IsChild() && (*i)->IsSelfModify())
		{
			if (!(*i)->Modify(objects))
				tryAgain.push_back((const ModifyObject*)(*i));
		}


   i = modifyObjects.begin();
   for( ; i!= modifyObjects.end(); i++ )
      if( !(*i)->IsChild() && !(*i)->IsSelfModify() )
         (*i)->Modify(objects);

	std::vector<const ModifyObject*> tryNext;
	std::vector<const ModifyObject*>::const_iterator ti = tryAgain.begin();
   for( ; ti != tryAgain.end(); ti++ )
		if (!(*ti)->Modify(objects))
		{
			tryNext.push_back((const ModifyObject*)(*ti));
		}

   i = modifyObjects.begin();
   for( ; i!= modifyObjects.end(); i++ )
		if ((*i)->IsChild() && !(*i)->IsSelfModify())
         (*i)->Modify(objects);

	ti = tryNext.begin();
	for (; ti != tryNext.end(); ti++)
		(*ti)->Modify(objects);

   modifyObjects.clear();
}

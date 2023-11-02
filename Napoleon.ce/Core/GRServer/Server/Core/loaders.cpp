/*
 * Copyright (C), 2009 - 2011, Денис Мосягин
 *
 * Загрузчики объектов
 *
 * ert   14/07/2011   creating
 */
#include "stdafx.h"
#include "loaders.h"
#include "server.h"
#include "constloader.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

ILoader::ILoader(IXmlHandler* _prevHandler) : isCompleete(false), prevHandler(_prevHandler)
{
	prevHandler->owner->SetHandler(this);
}

void ILoader::SetCompleete(bool compleete)
{
   isCompleete = compleete;

   if( compleete )
		prevHandler->owner->SetHandler(prevHandler);
   else
		prevHandler->owner->SetError(L"not compleete");

}
//
//------------------------------------ ObjectLoader ----------------------------------------------
//
ObjectLoader::ObjectLoader(ObjectDef::ObjectSet* objectSet, IXmlHandler* prevHandler, const Attributes& atts, const std::wstring& parent, ObjectDef::Field *field) : ILoader(prevHandler)
{
   fieldsLoader = NULL;
   sourcesLoader = NULL;
   memberLoader = NULL;
	memberArrayLoader = NULL;

   bool canLoadField = true;

   std::wstring feature;
   if( atts.Find(&feature, L"feature") )
      canLoadField = gServer->GetConfig().HaveFeature(feature);
   if( !canLoadField )
   {
      isCompleete = true;
		prevHandler->owner->SetHandler(new SkipTagHandler(prevHandler, L"object"));
   }

   bool findBaseError = false;
   this->objectSet = objectSet;
   object = new ObjectDef();
   std::wstring src;
   if (atts.Find(&src, L"base"))
   {
      ObjectDef od;
      od.name = src;
      ObjectDef::ObjectSet::const_iterator fnd = objectSet->find(od);
      if (fnd != objectSet->end())
      {
         fnd->CopyTo(object);
      }
      else
      {
         findBaseError = true;
      }
   }

   if( field != NULL )
   {
      object->name = parent + L"$" + field->format.name;
      field->data = object->name;
      object->parent = parent;
   } else if( !atts.Find(&object->name, L"name") )
   {
		prevHandler->owner->SetError(L"Нет аттрбиута 'object.name'");
      return;
   }

   if(findBaseError)
   {
      USES_CONVERSION;
      gServer->AddLog("Can't find base document %s for %s", W2A(src.c_str()), W2A(object->name.c_str()));
   }


   if( !atts.Find(&object->tableName, L"tableName") )
   {
      if( field == NULL )
         object->tableName = object->name;
      // случай внутренних объектов разбираем после загрузки ObjectDef::ModifyObject
   }

   std::wstring tvalue;
   if( atts.Find(&tvalue, L"removeOnCommit") )
      SetBooleanFlag(&object->flags, tvalue, IObjectDef::RemoveOnCommit);
   if( atts.Find(&tvalue, L"sendAlways") )
      SetBooleanFlag(&object->flags, tvalue, IObjectDef::SendAlways);
   if( atts.Find(&tvalue, L"sendLimit") )
      object->sendLimit = _wtoi(tvalue.c_str());
	if (atts.Find(&tvalue, L"debug"))
		object->needDebug = ((_wcsicmp(tvalue.c_str(), L"true") == 0 || _wtoi(tvalue.c_str()) == 1));
	atts.Find(&object->alias, L"alias");
}

ObjectLoader::~ObjectLoader()
{
   delete object;
   delete fieldsLoader;
   delete sourcesLoader;
   delete memberLoader;
   delete memberArrayLoader;
}

void ObjectLoader::StartElement(const std::wstring& name, const Attributes& atts)
{
   std::wstring err(L"Ошибка опеределения объекта ");
   err += object->name;

   if( name.compare(L"member") == 0 )
   {
      if( memberLoader )
      {
         if( memberLoader->IsCompleete() == false )
         {
            err += L" member compleete";
				owner->SetError(err.c_str());
            return;
         }
         delete memberLoader;
      }
      memberLoader = new MemberLoader(&object->members, this, atts);
   } else if( name.compare(L"memberArray") == 0 )
   {
      if( memberArrayLoader )
      {
         if( memberArrayLoader->IsCompleete() == false )
         {
            err += L" member compleete";
				owner->SetError(err.c_str());
            return;
         }
         delete memberArrayLoader;
      }
		memberArrayLoader = new MemberArrayLoader(&object->memberArray, this, atts);
   }
	else if( name.compare(L"field") == 0 )
   {
      if( fieldsLoader )
      {
         if( !fieldsLoader->IsCompleete() )
         {
            err += L" field compleete";
				owner->SetError(err.c_str());
            return;
         }
         delete fieldsLoader;
      }
      fieldsLoader = new FieldsLoader(objectSet, &object->fields, this, atts, object);
   } else if( name.compare(L"sources") == 0 )
   {
      if( sourcesLoader )
      {
         if( !sourcesLoader->IsCompleete() )
         {
            err += L" sources compleete";
				owner->SetError(err.c_str());
            return;
         }
         delete sourcesLoader;
      }

      sourcesLoader = new SourcesLoader(object, &object->source, this, atts);
   } else if( name.compare(L"events") == 0 )
      object->events.LoadXml(this);
}

bool ObjectLoader::CheckCompleete()
{
   return (object->fields.size() && (!fieldsLoader || fieldsLoader->IsCompleete()) && (!sourcesLoader || sourcesLoader->IsCompleete()));
}

void ObjectLoader::EndElement(const std::wstring& name)
{
   if( name.compare(L"object") == 0 )
   {
      SetCompleete(CheckCompleete());

      std::pair<ObjectDef::ObjectSet::iterator, bool> ins = objectSet->insert(*object);
      if( ins.second == false )
      {
         *((ObjectDef*)&(*ins.first)) = *object;
      }
   }
}

//
//------------------------------------ MemberLoader ----------------------------------------------
//
MemberLoader::MemberLoader(ObjectDef::Members* members, IXmlHandler* prevHandler, const Attributes& atts) :
   ILoader(prevHandler)
{
   this->members = members;
   if( !atts.Find(&name, L"name") )
		owner->SetError(L"Нет аттрбиута 'member.name'");
}

void MemberLoader::EndElement(const std::wstring& name)
{
   if( name.compare(L"member") == 0 )
   {
      (*members)[this->name] = value;
      SetCompleete(true);
   }
}

//
//------------------------------------ MemberArrayLoader ----------------------------------------------
//
MemberArrayLoader::MemberArrayLoader(ObjectDef::MemberArray* memberArray, IXmlHandler* prevHandler, const Attributes& atts) :
   ILoader(prevHandler)
{
   this->memberArray = memberArray;
   if( !atts.Find(&name, L"name") )
		owner->SetError(L"Нет аттрбиута 'member.name'");
}

void MemberArrayLoader::EndElement(const std::wstring& name)
{
   if( name.compare(L"memberArray") == 0 )
   {
		(*memberArray)[this->name].push_back(value);
      SetCompleete(true);
   }
}

//
//------------------------------------ FieldLoader ----------------------------------------------
//
FieldLoader::FieldLoader(ObjectDef::ObjectSet* objectSet, ObjectDef::Field* field, IXmlHandler* prevHandler,
                         const Attributes& atts, const std::wstring& objectName) : ILoader(prevHandler)
{
   SetData(objectSet, field, atts, objectName);
}

FieldLoader::~FieldLoader()
{
   delete objectLoader;
}

void FieldLoader::SetError(const wchar_t* errText)
{
   std::wstring err(L"Объект ");
	err += parent;
	if (!field->format.name.empty()) {
		err += L".";
		err += field->format.name;
	}
	err += L" ";
   err += errText;
	owner->SetError(err.c_str());
}

void FieldLoader::SetData(ObjectDef::ObjectSet* objectSet, ObjectDef::Field* field, const Attributes& _atts, const std::wstring& objectName)
{
   state = State::stInitial;
   objectLoader = NULL;

   Attributes atts(_atts);
   FieldTemplateLoader* ft = FieldTemplateLoader::Get(_atts);
   if (ft != NULL)
   {
      ft->Update(&atts);
   }

   this->objectSet = objectSet;
   this->field = field;
   parent = objectName;

   field->flags = 0;

   std::wstring type, wdh, val;
	if (!atts.Find(&field->format.name, L"name"))
	{
		SetError(L"нет аттрбиута 'name'");
		return;
	}

	if (!atts.Find(&type, L"type"))
   {
      SetError(L"нет аттрбиута 'type'");
      return;
   }

   field->width = 0;
   //WORD width = 0;
   if (atts.Find(&wdh, L"width"))
      if (!ConstLoader::CheckConst(wdh, &field->width))
      {
         SetError(L"wrong constant");
         return;
      }

   field->pass = 1;
   if( atts.Find(&val, L"pass") )
      field->pass = _wtoi(val.c_str());

   // назначим data перед типом, т.к. для типа file будем использоваеть его под аттрибут src
   if( !atts.Find(&field->data, L"data") )
      field->data = field->format.name;

   if( type.compare(L"string") == 0 )
   {
      field->format.type = MemberFormat::mtString;
      //field->width = width;
   } else if( type.compare(L"number") == 0 )
   {
      field->format.type = MemberFormat::mtNumber;
      field->format.format.fraction = 0;

      if( atts.Find(&val, L"prec") )
         field->format.format.fraction = _wtoi(val.c_str());

      field->width = ((field->width > 0) ? field->width : 9) + field->format.format.fraction;
      //field->width = ((width > 0) ? width : 9) + field->format.format.fraction;
   } else if( type.compare(L"hex") == 0 )
   {
      field->format.type = MemberFormat::mtNumber;
      field->format.format.fraction = 0;
      field->flags |= ObjectDef::Field::Hex;
      field->width = 8; // 4 байта
   } else if( type.compare(L"date") == 0 )
   {
      field->format.type = MemberFormat::mtDateTime;
      field->format.format.dateFormat = MemberFormat::Date;
   } else if( type.compare(L"time") == 0 )
   {
      field->format.type = MemberFormat::mtDateTime;
      field->format.format.dateFormat = MemberFormat::Time;
   } else if( type.compare(L"timestamp") == 0 || type.compare(L"datetime") == 0 )
   {
      field->format.type = MemberFormat::mtDateTime;
      field->format.format.dateFormat = MemberFormat::Stamp;
   } else if( type.compare(L"collection") == 0 )
   {
      field->format.type = MemberFormat::mtObject;
   } else if( type.compare(L"file") == 0 )
   {
      field->format.type = MemberFormat::mtBinary;
      field->flags |= ObjectDef::Field::File;
      atts.Find(&field->src, L"src");
		
		std::wstring flags;
		if (atts.Find(&flags, L"flags") && flags.find(L"create") != std::wstring::npos)
			field->format.flags |= MemberFormat::CanCreate;
      //if( atts.Find(&field->baseFolder, L"baseFolder") )
      //{
      //   wchar_t sym = *field->baseFolder.rbegin();
      //   if( sym != L'\\' && sym != L'/' )
      //      field->baseFolder.append(1, L'\\');
      //}
   } else if( type.compare(L"binary") == 0 )
   {
      field->format.type = MemberFormat::mtBinary;
   } else
   {
      SetError(L"Неизвестный аттрбиут 'field.type'");
      return;
   }

   if( atts.Find(&val, L"hidden") )
   {
      const wchar_t *pval = val.c_str();
      if( _wcsicmp(pval, L"true") == 0 || _wtoi(pval) == 1 )
         field->format.flags |= MemberFormat::Hidden;
      else if( _wcsicmp(pval, L"put") == 0 )
         field->format.flags |= MemberFormat::HiddenPut;
   }

   if( atts.Find(&val, L"execOn") )
   {
      const wchar_t *pval = val.c_str();
      if( _wcsicmp(pval, L"put") == 0 ) field->format.flags |= MemberFormat::ExecOnPut;
      else if( _wcsicmp(pval, L"get") == 0 ) field->format.flags |= MemberFormat::ExecOnGet;
   }

   if (ft != NULL && ft->SetData(&field->execStmt))
   {
      if ((field->format.flags & (MemberFormat::ExecOnPut | MemberFormat::ExecOnGet)) == 0)
         field->format.flags |= (MemberFormat::ExecOnPut | MemberFormat::ExecOnGet);
   }

   atts.Find(&field->dataFormat, L"format");
}

void FieldLoader::StartElement(const std::wstring& name, const Attributes& atts)
{
   if( state == stInitial )
   {
      if( name.compare(L"data") == 0 )
         state = stData;
   } else if( state == stData )
   {
      if( name.compare(L"object") == 0 )
      {
         if( objectLoader != NULL )
         {
            SetError(L"второй тэг object");
            return;
         }
         objectLoader = new ObjectLoader(objectSet, this, atts, parent, field);
      }
   }
}

void FieldLoader::CharacterData(const std::wstring& name)
{
   const wchar_t *p = name.c_str();
   while( *p != L'\0' && iswspace(*p)  ) p++;
   if( *p != L'\0' )
   {
      if( (field->format.flags & (MemberFormat::ExecOnPut | MemberFormat::ExecOnGet)) == 0 )
         field->format.flags |= (MemberFormat::ExecOnPut | MemberFormat::ExecOnGet);
      field->execStmt = p;
   }
}

void FieldLoader::EndElement(const std::wstring& name)
{
   if( name.compare(L"field") == 0 )
   {
      SetCompleete(true);
      OnCompleete();
   }
}

//
//------------------------------------ FieldsLoader ----------------------------------------------
//
FieldsLoader::FieldsLoader(ObjectDef::ObjectSet* objectSet, ObjectDef::Fields* fields, IXmlHandler* prevHandler,
                           const Attributes& atts, ObjectDef* object) : FieldLoader(prevHandler)
{
   canLoadField = true;

   this->fields = fields;
   this->object = object;

   SetData(objectSet, &field, atts, object->name);

   std::wstring feature;
   if( atts.Find(&feature, L"feature") )
      canLoadField = gServer->GetConfig().HaveFeature(feature);

   if( !canLoadField )
   {
      isCompleete = true;
		owner->SetHandler(new SkipTagHandler(prevHandler, L"field"));
   }
}

void FieldsLoader::OnCompleete()
{
   if( canLoadField )
      fields->insert(field);
}

//
//---------------------------------------- FieldTemplateLoader ---------------------------------------------
//
static std::map<std::wstring, FieldTemplateLoader*> fldTemplates;

class XMLFTLoader : public ILoader
{
public:
   XMLFTLoader(IXmlHandler* prev, FieldTemplateLoader* _templ) : ILoader(prev), templ(_templ) {}

   virtual void EndElement(const std::wstring& name)
   {
      SetCompleete(true);
      delete this;
   }

   virtual void CharacterData(const std::wstring& name) { templ->Assign(name); }

   FieldTemplateLoader* templ;
};

void FieldTemplateLoader::Load(IXmlHandler* prev, const IXmlHandler::Attributes& atts)
{
   FieldTemplateLoader* loader = new FieldTemplateLoader(atts);
   std::wstring name;
   if (atts.Find(&name, L"name"))
   {
      std::pair<std::map<std::wstring, FieldTemplateLoader*>::iterator, bool> ins =
         fldTemplates.insert(std::map<std::wstring, FieldTemplateLoader*>::value_type(name, loader));

      if (!ins.second)
      {
         delete ins.first->second;
         ins.first->second = loader;
      }

      new XMLFTLoader(prev, loader);
   }
}

static void UpdateFrom(std::map<std::wstring, std::wstring>* dest, const std::map<std::wstring, std::wstring>& src)
{
   std::map<std::wstring, std::wstring>::const_iterator i = src.begin();

   for (; i != src.end(); i++)
      dest->insert(std::map<std::wstring, std::wstring>::value_type(i->first, i->second));
}

FieldTemplateLoader::FieldTemplateLoader(const IXmlHandler::Attributes& atts)
{
   std::wstring name;
   atts.Find(&name, L"fieldName");

   FieldTemplateLoader* src = FieldTemplateLoader::Get(atts);
   if (src != NULL)
   {
      UpdateFrom(&atributes, src->atributes);
   }

   UpdateFrom(&atributes, atts);
   if (!name.empty())
      atributes[L"name"] = name;
}

void FieldTemplateLoader::Clear()
{
   std::map<std::wstring, FieldTemplateLoader*>::iterator i = fldTemplates.begin();
   for (; i != fldTemplates.end(); i++)
      delete i->second;

   fldTemplates.clear();
}

FieldTemplateLoader* FieldTemplateLoader::Get(const IXmlHandler::Attributes& atts)
{
   FieldTemplateLoader* ret = NULL;
   
   std::wstring name;
   if (atts.Find(&name, L"template"))
   {
      std::map<std::wstring, FieldTemplateLoader*>::iterator fnd = fldTemplates.find(name);
      if (fnd != fldTemplates.end())
         ret = fnd->second;
   }

   return ret;
}

void FieldTemplateLoader::Update(IXmlHandler::Attributes* dest) const
{
   UpdateFrom(dest, atributes);
}

bool FieldTemplateLoader::SetData(std::wstring* dest) const
{
   if (!data.empty())
   {
      dest->assign(data);
      return true;
   }

   return false;
}

//
//---------------------------------------- SourceLoader ---------------------------------------------
//
SourceLoader::SourceLoader(ObjectDef* object, SourceDef* source, IXmlHandler* prevHandler, const Attributes& atts) : 
   ILoader(prevHandler), readParam(false)
{
   this->source = source;

   if( !atts.Find(&source->name, L"name") )
   {
      std::wstring err;
      if( object )
      {
         err += L"Ошибка загрузки объекта ";
         err += object->name;
         err += L" ";
      }
      err += L"нет аттрбиута 'source.name'";
		owner->SetError(err.c_str());
      return;
   }

   std::wstring type;
   if( atts.Find(&type, L"type") )
      source->type = SourceTypeFromString(type);
   else
      source->type = stCommon;
}

void SourceLoader::StartElement(const std::wstring& name, const Attributes& atts)
{
   if( name.compare(L"param") == 0 )
   {
      readParam = true;

      param.name.clear();
      param.value.clear();
      atts.Find(&param.name, L"name");
   }
}

void SourceLoader::EndElement(const std::wstring& name)
{
   if( name.compare(L"param") == 0 )
   {
      readParam = false;
      source->parameters.push_back(param);
   } else if( name.compare(L"source") == 0 )
      SetCompleete(true);
}

void SourceLoader::CharacterData(const std::wstring& name)
{
   if( readParam )
   {
      param.value.append(name);
      //source->parameters.push_back(param);
      //readParam = false;
   }
}

//
//-------------------------------------- SourcesLoader --------------------------------------------
//
SourcesLoader::SourcesLoader(ObjectDef* object, SourceDefList* sources, IXmlHandler* prevHandler, const Attributes& atts) :
   ILoader(prevHandler)
{
   this->sources = sources;
   this->object = object;
   loader = NULL;
}

SourcesLoader::~SourcesLoader()
{
   delete loader;
}

void SourcesLoader::StartElement(const std::wstring& name, const Attributes& atts)
{
   if( name.compare(L"source") == 0 )
   {
      if( loader != NULL )
      {
         sources->push_back(source);
         source.parameters.clear();
         source.name.clear();

         delete loader;
			loader = NULL;
      }
		bool canLoad = true;
		std::wstring feature;
		if (atts.Find(&feature, L"feature"))
			canLoad = gServer->GetConfig().HaveFeature(feature);

		if(canLoad)
			loader = new SourceLoader(object, &source, this, atts);
		else
			owner->SetHandler(new SkipTagHandler(this, L"source"));
   }
}

void SourcesLoader::EndElement(const std::wstring& name)
{
   if( name.compare(L"sources") == 0 )
   {
      SetCompleete(true);
      if( loader != NULL )
         sources->push_back(source);
   }
}

void GRServer::SetBooleanFlag(DWORD* flags, const std::wstring& value, DWORD flag)
{
   const wchar_t *p = value.c_str();
   if( _wcsicmp(p, L"true") == 0 || _wtoi(p) == 1 ) *flags |= flag;
   else (*flags) &= (~flag);
}

SourceType GRServer::SourceTypeFromString(std::wstring& val)
{
   if( val.compare(L"internal") == 0 ) return stInternal;
	if (val.compare(L"any") == 0) return stAny;
	return stCommon;
}

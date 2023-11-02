/*
 * Copyright (C), 2009 - 2011, Денис Мосягин
 *
 * Загрузчики объектов
 *
 * ert   14/07/2011   creating
 */
#ifndef __LOADERS_H
#define __LOADERS_H

#include "objdef.h"
#include "xml.h"

namespace GRServer {

class SkipTagHandler : public IXmlHandler
{
public:
   SkipTagHandler(IXmlHandler* _prevHndlr, const std::wstring& _tag) : count(0), tag(_tag), prevHandler(_prevHndlr) {}

   virtual void StartElement(const std::wstring& name, const Attributes& atts)
   {
      if( name.compare(tag) == 0 )
         count++;
   }

   virtual void EndElement(const std::wstring& name)
   {
      if( name.compare(tag) == 0 && count-- == 0 )
      {
         prevHandler->owner->SetHandler(prevHandler);
         delete this;
      }
   }

   virtual void CharacterData(const std::wstring& name) {}

   virtual bool IsError() const { return false; }
   virtual const wchar_t* GetError() const { return L""; }

protected:
   int count;
   std::wstring tag;
   IXmlHandler* prevHandler;
};

class ILoader : public IXmlHandler
{
public:
   ILoader(IXmlHandler* prevHandler);

   virtual ~ILoader() {}

   bool IsCompleete() const { return isCompleete; }

   virtual void StartElement(const std::wstring& name, const Attributes& atts) {}
   virtual void EndElement(const std::wstring& name) = 0;
   virtual void CharacterData(const std::wstring& name) {}

   virtual bool IsError() const { return false; }
   virtual const wchar_t* GetError() const { return L""; }

   void SetCompleete(bool compleete);

public:
   bool isCompleete;
   IXmlHandler* prevHandler;
};

class SourceLoader;
class FieldLoader;
class FieldsLoader;
class MemberLoader;
class MemberArrayLoader;
class SourcesLoader;

class ObjectLoader : public ILoader
{
public:
   ObjectLoader(ObjectDef::ObjectSet* objectSet, IXmlHandler* prevHandler, const Attributes& atts,
      const std::wstring& parent, ObjectDef::Field *field);

   ~ObjectLoader();

   virtual void StartElement(const std::wstring& name, const Attributes& atts);
   virtual void EndElement(const std::wstring& name);

protected:
   ObjectDef::ObjectSet* objectSet;
   ObjectDef* object;
   FieldsLoader* fieldsLoader;
   SourcesLoader* sourcesLoader;
   MemberLoader* memberLoader;
   MemberArrayLoader* memberArrayLoader;

   bool CheckCompleete();
};

class MemberLoader : public ILoader
{
public:
   MemberLoader(ObjectDef::Members* members, IXmlHandler* prevHandler, const Attributes& atts);

   virtual void StartElement(const std::wstring& name, const Attributes& atts) {}
   virtual void EndElement(const std::wstring& name);
   virtual void CharacterData(const std::wstring& name) { value = name; }

protected:
   ObjectDef::Members* members;

   std::wstring name;
   std::wstring value;
};

class MemberArrayLoader : public ILoader
{
public:
   MemberArrayLoader(ObjectDef::MemberArray* memberArray, IXmlHandler* prevHandler, const Attributes& atts);

   virtual void StartElement(const std::wstring& name, const Attributes& atts) {}
   virtual void EndElement(const std::wstring& name);
   virtual void CharacterData(const std::wstring& name) { value = name; }

protected:
   ObjectDef::MemberArray* memberArray;

   std::wstring name;
   std::wstring value;
};

class FieldTemplateLoader 
{
public:
   static void Load(IXmlHandler  *prev, const IXmlHandler::Attributes& atts);
   static void Clear();

   static FieldTemplateLoader* Get(const IXmlHandler::Attributes& atts);

   FieldTemplateLoader(const IXmlHandler::Attributes& atts);

   void Update(IXmlHandler::Attributes* dest) const;
   bool SetData(std::wstring* dest) const;

   void Assign(const std::wstring& src) { data = src;  }

private:
   std::map<std::wstring, std::wstring> atributes;
   std::wstring data;
};

class FieldLoader : public ILoader
{
public:
   FieldLoader(ObjectDef::ObjectSet* objectSet, ObjectDef::Field* field,
      IXmlHandler* prevHandler, const Attributes& atts, const std::wstring& objectName);
   ~FieldLoader();

   virtual void StartElement(const std::wstring& name, const Attributes& atts);
   virtual void EndElement(const std::wstring& name);
   virtual void CharacterData(const std::wstring& name);

protected:
   enum State { stInitial, stData } state;

   ObjectDef::ObjectSet* objectSet;
   ObjectDef::Field* field;
   ObjectLoader* objectLoader;
   std::wstring parent;

protected:
   // после конструктора обязательно вызвать SetData
   FieldLoader(IXmlHandler* prevHandler) : ILoader(prevHandler) {}

   void SetData(ObjectDef::ObjectSet* objectSet, ObjectDef::Field* field, const Attributes& atts, const std::wstring& objectName);
   virtual void OnCompleete() {}

   void SetError(const wchar_t* err);
};

class FieldsLoader : public FieldLoader
{
public:
   FieldsLoader(ObjectDef::ObjectSet* objectSet, ObjectDef::Fields* fields, IXmlHandler* prevHandler,
      const Attributes& atts, ObjectDef* object);

protected:
   ObjectDef::Field field;
   ObjectDef::Fields* fields;
   ObjectDef* object;

   bool canLoadField;

   virtual void OnCompleete();
};

class SourceLoader : public ILoader
{
public:
   SourceLoader(ObjectDef* object, SourceDef* source, IXmlHandler* prevHandler, const Attributes& atts);

   virtual void StartElement(const std::wstring& name, const Attributes& atts);
   virtual void EndElement(const std::wstring& name);
   virtual void CharacterData(const std::wstring& name);

protected:
   SourceDef* source;
   Parameter param;
   bool readParam;
};

class SourcesLoader : public ILoader
{
public:
   SourcesLoader(ObjectDef* object, SourceDefList* sources, IXmlHandler* prevHandler, const Attributes& atts);
   ~SourcesLoader();

   virtual void StartElement(const std::wstring& name, const Attributes& atts);
   virtual void EndElement(const std::wstring& name);

protected:
   SourceDefList* sources;
   SourceDef source;
   ObjectDef* object;
   SourceLoader *loader;
};

void SetBooleanFlag(DWORD* flags, const std::wstring& value, DWORD flag);

} // namespace GRServer

#endif

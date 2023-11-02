/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * ObjectDef ecl. 
 *
 * ert   12/03/2010   creating
 */
#ifndef _GRSERVER_OBJ_DEF_H
#define _GRSERVER_OBJ_DEF_H

#include <string>
#include <vector>
#include <set>
#include <map>

#include "member.h"
#include "event.h"
#include <gservices.h>
#include <srctype.h>

namespace GRServer {

struct IDataSource;

class ObjectDef : public IObjectData
{
public:

   // class functions
   static bool Load(const std::string& fileName, int phase);
   static bool Load(const Binary& data);

   static void ModifyObjects();

   static const ObjectDef* Get(const std::wstring& objName);
   //static void GetObjectsName(std::vector<std::wstring>* names, DWORD flags);
   static void GetObjectsName(CVector<CString>** names, DWORD flags);
   static bool Fire(Event::Type type, Session* session, SessionObject* param = NULL);
	static bool HaveEvent(Event::Type type);

   static IObjectDef* GetService();

   static void Clear();

   typedef std::set<ObjectDef> ObjectSet;

public:
   //instance fuctions
   ObjectDef();

   //bool HaveExecutableFields() const { return ((flags & IObjectDef::HaveEFields) != 0); }
	bool HaveExecutableFields(int mask) const;

   const SourceDefList& GetSource() const { return (source.size() > 0) ? source : commonSource; }

   bool operator< (const ObjectDef& item) const { return (_wcsicmp(name.c_str(), item.name.c_str()) < 0); }

   void RemoveField(const std::wstring& name);
   void RemoveMember(const std::wstring& name);

   void PutField(const IObjectData::Field& f);
   void PutMember(const std::wstring& name, const std::wstring& value);
	void AddMemberArray(const std::wstring& name, const std::wstring& value);

   void AddSource(const SourceDef& source);
   void ReplaceSources(const SourceDefList& sources);

   void RemoveEvent(const std::wstring& type);
   void AddEvents(const EventList& events);

   virtual const IObjectData::Field* FindField(const std::wstring& name) const;
   virtual bool LoadFK(CVector<MemberFormat>** formats, CVector<Field> **fields = NULL) const;
   virtual void CreateFKConstraint(CString** fktext, CString** indexText, const CVector<MemberFormat>& fields, wchar_t escape = L'"') const;
   virtual const ParamList* InternalSourceParams() const;
   virtual bool IsOrderedSource() const;

   void PrepareConsts();

   void CopyTo(ObjectDef* dest) const;
public:
   SourceDefList source;
   EventList events;
   DWORD sendLimit;
	bool needDebug;

protected:
   static ObjectSet objects;

public:
   static SourceDefList commonSource;
   static std::wstring commonFieldData;
   static EventList globalEvents;
};

} // namespace GRServer

#endif

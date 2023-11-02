/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Session object.
 *
 * ert   21/08/2010   creating
 */
#ifndef __GR_SESSION_OBJECT_H
#define __GR_SESSION_OBJECT_H

#include <isessobj.h>
#include <ievent.h>
#include "datasource.h"

namespace GRServer {

struct IDataSource;
struct Token;
class Session;
class SessionObject;
class Socket;
class ObjectDef;
struct ObjectSource;

class ServObjFormat : public Format
{
public:
   ServObjFormat() {}
   ServObjFormat(const ServObjFormat& fmt) : Format(fmt) {}
   ServObjFormat(const std::wstring& _name) : Format(_name) {}

protected:
   virtual bool ReadMembers(ParseStreamU& stream, FormatList *fmtList);
};

class SessionObject : public ServObject, public ISessionObject, public IObjectLoader
{
public:
   SessionObject(const ObjectDef* objDef, Session *session);
   SessionObject(const std::wstring& name, Session *session);

   ~SessionObject();

   virtual ServObject* Self() const { return const_cast<SessionObject*>(this); }
   virtual const IObjectData* GetObjectDef() const { return (const IObjectData*)(objDef); }

   virtual IObjectLoader* GetObjectLoader() { return CreateReader(L"", true, false) ? (IObjectLoader*)this : NULL; }

   virtual ObjectSource* GetSource() const { return (ObjectSource*)&source; }

   bool GetValue(Token* dest, const std::wstring& member, bool toValue) const;

   bool CreateReader(const wchar_t* filter = L"", bool forceCreate = false, bool clearObject = true);
   bool MoveNext();
   void WriteFormat(OutStream* os) const;
   bool WriteTo(OutStream* os);
   bool Load(Object* parentObject, bool resolveFiles = false);
   void CloseReader();

   DWORD SendLimit() const;

   IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parentWriter, SourceType srcType, std::wstring *sourceName);
   bool CreateWriter(IDataSource::IWriter* parentWriter, SourceType srcType = stCommon)
   {
      return (CreateWriter(parentWriter, srcType, NULL) != NULL);
   }

   bool Write(bool updateExecutable, RID_LIST *ids = NULL);
   void CloseWriter();

   bool DoObjCommand(const std::wstring& action, OutStream* stream);

   virtual bool Removing(const wchar_t* filter = L"");

   IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, SourceType srcType, std::wstring *sourceName);
   void CloseRemover();

   void RemoveSource();

	virtual bool Reading(const wchar_t* filter = L"", bool createReader = true, bool resolveFiles = false);
   virtual bool Writing(RID_LIST *ids = NULL);

   virtual ISessionObject* Parent() const { return const_cast<SessionObject*>(parent); }
   virtual ISessionObject* GetChild(const std::wstring& fieldName) const;
   virtual ISession& GetSession() const { return *(ISession*)((const_cast<Session*>(session))); }
   virtual bool PrepareFilterStr(CString* dest, const CString& src) const;

   //const SessionObject* Parent() const { return parent; }
   //const SessionObject* GetChild(const std::wstring& fieldName) const;
   //const Session& GetSession() const { return *session; }

   bool HaveExecutableFields(bool put) const;
   void RefreshExecutableData(bool put);
   bool UpdateExecutableFields(Object* curObject, bool put);
   bool UpdateExecutableFields(Object& parentObject, Object::Field& field, bool put);

   int CurObjectIndex() const { return curObjIndex; }
	//void SetCurrentObject(int index)
	//{
	//	if( index < (int)size() )
	//		curObjIndex = index;
	//}

   //  IObjectLoader
   virtual void LoadObject(Object* object) { LoadObject(object, true); }
   virtual void LoaderClose();

	void LoadObject(Object* object, bool updateExecutable, bool resolveFiles = false);

	virtual void PrepareToString(const Object& obj) const;
	virtual void AfterToString(const Object& obj) const;

	void SetUserid(const std::wstring& uid) { userid = uid; }
	const std::wstring& UserID() const { return userid; }

	bool NeedDebug() const { return needDebug; }

protected:
   struct Child
   {
      SessionObject* object;
      std::wstring fieldName;

      Child() : object(NULL) {}
      Child(const Child& ch)
      {
         fieldName = ch.fieldName;
         object = ch.object;

         const_cast<Child&>(ch).object = NULL;
      }
      ~Child() { delete object; }
   };

   class ChildList : public std::vector<Child>
   {
   public:
      ChildList() {}
      bool UpdateExecutableFields(Object& parentObject, bool put);
		void UpdateFileFields(const Object& parentObject, bool prepare) const;
	};

   SessionObject(Session *session);
   void InitObject(const ObjectDef* objDef);

	Object* ReadObject(bool updateExecutable, bool resolveFiles = false);
   bool Write(const Object& o, RowID *ids);

   bool LoadExecutableFields(Object& current, bool put, std::vector<int> *fieldIndexes = NULL);

   void SaveWriter(std::vector<IDataSource::IWriter*> *writers);
   void RestoreWriter(std::vector<IDataSource::IWriter*>::iterator current);
	void FireEvent(IEvent::Type eventType, Object* o);

   const ObjectDef *objDef;
   ObjectSource source;

   Session* session;
   SessionObject* parent;
   ChildList childs;
   int currentIndex;
   DWORD flags;
   DWORD curObjIndex;

	std::vector<FileField*> lazyWriters;
	bool fireEvent;
	bool needDebug;

	std::wstring userid;
};

void SetFileFieldBaseFolder(std::string* dest, const IObjectData::Field& src, const IServerConfig& config);

} // namespace GRServer

#endif


/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * ISession object.
 *
 * ert   22/06/2012   creating
 */ 
#ifndef __GR_I_SESSION_OBJECT_H
#define __GR_I_SESSION_OBJECT_H

#include <servobj.h>
#include "iobject.h"
#include "token.h"

namespace GRServer {

struct ISession;
struct ISessionObject;
struct ObjectSource;

/*
Загружает все child объекты и executable fields - объект читается извне (сделано для ODBC ObjectSource Online Завяки)
 */
struct IObjectLoader
{
   virtual void LoadObject(Object* object) = 0;
   virtual void LoaderClose() = 0;
};

class ParamHelper;

struct ISessionObject
{
   virtual ServObject* Self() const = 0;
   
   virtual IObjectLoader* GetObjectLoader() = 0;

   virtual const IObjectData* GetObjectDef() const = 0;

   virtual ISessionObject* Parent() const = 0;
   virtual ISessionObject* GetChild(const std::wstring& fieldName) const = 0;
   virtual ISession& GetSession() const = 0;

   virtual bool Reading(const wchar_t* filter = L"", bool createReader = true, bool resolveFiles = false) = 0;

   virtual bool Writing(RID_LIST *ids = NULL) = 0;

   virtual bool Removing(const wchar_t* filter = L"") = 0;

   virtual ObjectSource* GetSource() const = 0;

   // преобразует дату в число ToDate('20.04.2010') ToDate('20-04-2010 10:20:15') ToDate('20/04/2010 10:20:15')
   virtual bool PrepareFilterStr(CString* dest, const CString& src) const = 0;

   virtual const ParamHelper* GetParamHelper() const = 0;
};

class StrSet : public std::set<std::wstring>
{
public:
   bool Find(const std::wstring& id) const { return (find(id) != end()); }
};

struct ISession
{
   struct IHandler
   {
      virtual void SessionClosed(ISession* sender) = 0;
   };

   // создает и читает объект
   virtual ISessionObject* LoadObject(const std::wstring& objName, const ISessionObject* thisObject, const wchar_t* filter = L"") = 0;

   // создает объект и присоединяет его к списку объектов сессии
   virtual ISessionObject* GetObject(const std::wstring& objName, const ISessionObject* thisObject) = 0;

   virtual FormatList* GetFormatList() const = 0;

   virtual const StrSet& AllowedUID() const = 0;

   virtual const IServerConfig& Config() const = 0;

   virtual bool Parse(CString** res, const std::wstring& expr, const ISessionObject* thisObject) const = 0;
	virtual bool Parse(Token* res, const std::wstring& expr, const ISessionObject* thisObject) const = 0;
	virtual bool CheckCondition(const std::wstring& expr, const ISessionObject* thisObject) const = 0;

   virtual void AddHandler(IHandler* handler) = 0;
   virtual void RemoveHandler(IHandler* handler) = 0;

   virtual ExchangeList* Ack() const = 0;
   virtual void AddToAnswer(ServObject *object) = 0;

   virtual GRServer::Format* RegisterType(const std::wstring& type, bool registerObjDef) = 0;
   virtual ISessionObject* CreateObject(const std::wstring& objName, bool addToResponse) = 0;
	
   virtual bool Impresonate(const wchar_t *userId, bool addAnswer, const wchar_t *password = NULL) = 0;
   virtual void RestoreUser() = 0;
	virtual void RestoreUser(bool removeObjects) = 0;

   virtual bool Execute(const wchar_t* stmt) = 0;
   virtual ISessionObject* Query(const wchar_t* stmt, const wchar_t* name, const wchar_t* groupExpr) = 0;
	
	
	virtual void PostObject(ISessionObject* object) = 0;
	virtual SOCKET GetSocket() const = 0;

	virtual bool MemoryLimitExceeded() const = 0;
	virtual void MemoryStat(std::string* out, bool printLimit) const = 0;
};

// query & table helpers
// move from srctype
struct Parameter
{
	std::wstring name; // m.b. empty
	std::wstring value;
};

class ParamList : public std::vector<Parameter>
{
public:
	ParamList() {}

	// ищет или по имени, или по порядку (если имя пустое или не найден параметр с именем)
	const Parameter* Find(const std::wstring& name, int order) const
	{
		const_iterator i = begin();
		for (; i != end(); i++)
			if (i->name.compare(name) == 0)
				return &(*i);

		if (order >= 0 && order < (int)size())
		{
			const Parameter* param = &at(order);
			return (param->name.empty()) ? param : NULL;
		}
		return NULL;
	}
   void Load(std::vector <std::wstring> *out, const std::wstring& name, const ISessionObject& iobject) const
   {
      const ISession& sess = iobject.GetSession();

      const_iterator i = begin();
      for (; i != end(); i++)
      {
         if (i->name.compare(name) != 0) continue;
         out->push_back(i->value);
      }
   }

};


class ParamHelper
{
public:
	ParamHelper(const ParamHelper* defaults);
	virtual ~ParamHelper();

	virtual void Read(const ParamList& parameters, const ISession* session, const ISessionObject* thisObject, IErrorLogger* logger); // читаем параметры название которых начинается с $
	virtual void Read(const wchar_t* filter, const ISession* session, const ISessionObject* thisObject, IErrorLogger* logger); // читаем параметры из фильтра имена присваеем $01;$02 ...

	virtual CString* Substitute(const wchar_t* filter, bool checkDollar = true) const; // подставляем параметры в строку фильтра
private:
	std::map<std::wstring, std::wstring> params;
   const ParamHelper* defaults;
};

} // namespace GRServer

#endif
/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Сессия
 *
 * ert   29/09/2009   creating
 */
#ifndef __SESSION_H
#define __SESSION_H

#include "sessobj.h"
#include "parse.h"
#include "server.h"
#include <socket.h>

#ifndef PROJECT_NAME
#define PROJECT_NAME L"Test"
#endif

namespace GRServer {

class DataController;
class Dispatcher;
class User;
struct IUpdatableModule;

class ServerAnswer : public SessionObject
{
public:
   ServerAnswer(Session *s);
   void Add(bool response, const std::wstring& message);
};

class Session : public ISession, public IResolver, public IServObjectCreator
{
public:
   enum Flags { NeedRestart = 1, };

	Session(Dispatcher* controller);
   ~Session();

   virtual void AddHandler(IHandler* handler);
   virtual void RemoveHandler(IHandler* handler);

   void AssignSocket(Socket* socket, HANDLE evStop) { this->socket = socket; this->evStop = evStop; }
   bool SendStream();

   void Clear();

   // Ack методы
   enum AckReturn { arFail, arAck, arPrevious };
   AckReturn ReadAck(DWORD timeout);
   bool AckHaveData() const { return (ack.size() > 0); }
   bool AckIs(const wchar_t* type) const { return (ack.front()->Name().compare(type) == 0); }
   bool CommandIs(const wchar_t* command) const;

	const char* AckError() const { return ack.readError; }

   bool PopAck();

   const Object& Command() const { return (*ack.front())[0]; }

   // методы аутентификации
   bool Auth();

#ifdef UNIX
#else
   // используется только в compat
   bool Auth(const wchar_t* login, const wchar_t* pwd);
#endif

   // передается окончание команды с AS, т.е. вместе с кавычками и пробелом перед строкой
	virtual bool Impresonate(const wchar_t *userId, bool addAnswer, const wchar_t *password = NULL);
   void AddAnswer(bool response, const std::wstring& message);

	virtual void RestoreUser() { RestoreUser(false); }
	virtual void RestoreUser(bool removeObjects);

	bool IsUserAssigned() const { return (user != NULL); }
   const User& GetUser() const { return *user; }

   // Response методы
   const SessionObject* WriteObject(const std::wstring& objName, const wchar_t* filter = L"");

   virtual const StrSet& AllowedUID() const;

   virtual ISessionObject* GetObject(const std::wstring& objName, const ISessionObject* thisObject);
   virtual ISessionObject* LoadObject(const std::wstring& objName, const ISessionObject* thisObject, const wchar_t* filter = L"");
   //const SessionObject* LoadObject(const std::wstring& objName, const SessionObject* thisObject, const wchar_t* filter = L"");
   const SessionObject* FindObject(const std::wstring& name, const ISessionObject* thisObject) const;

   virtual ExchangeList* Ack() const { return (ExchangeList*)&ack; }
   virtual void AddToAnswer(ServObject *object);
   virtual GRServer::Format* RegisterType(const std::wstring& type, bool registerObjDef);
   virtual ISessionObject* CreateObject(const std::wstring& objName, bool addToResponse);

   virtual const IServerConfig& Config() const { return config; }

   virtual bool    Execute(const wchar_t* stmt);
   virtual ISessionObject* Query(const wchar_t* stmt, const wchar_t* typeDef, const wchar_t* groupExpr);
	virtual void PostObject(ISessionObject* object);
	virtual int GetSocket() const { return socket->GetSocket(); }

   SessionObject* Build(const std::wstring& name, bool createAlways);

	void WriteAnswerToOutStream();
	void WriteStdObjects();
	void WriteLicenseRequest(const CString& managerLog);

   virtual bool Parse(Token* res, const std::wstring& expr, const ISessionObject* thisObject) const;

   bool Parse(std::wstring* res, const std::wstring& expr, const ISessionObject* thisObject) const
   {
      bool ret = false;
      Token val;
      if( Parse(&val, expr, thisObject) && val.type == Token::ttString )
      {
         res->assign(*val.value.str);
         ret = true;
      }
      return ret;
   }

   virtual bool Parse(CString** res, const std::wstring& expr, const ISessionObject* thisObject) const
   {
      bool ret = false;
      Token val;
		std::wstring buf;
		if (Parse(&val, expr, thisObject) && val.ToString(&buf))
		{
			delete *res;
			*res = new CString(buf);
			ret = true;
		}
      return ret;
   }

   // IResolver
   virtual bool Resolve(Token* dest, StringStream &stream, const std::wstring& val, const SessionObject* thisObject) const;
   virtual bool EndStatement(Token &result, StringStream &stream, wchar_t sym);

   bool Resolve(Token* dest, const std::wstring& val, const SessionObject* thisObject, bool toValue) const;

   virtual FormatList* GetFormatList() const { return (FormatList*)&formats; }

   bool StoreAckObjects(bool retID = false, bool updateExecutable = true);

   bool CheckUpdate(const Member* category);
   bool GetUpdate(const Member* category);
   bool GetUpdatePacket(const Member* category);
   // load update into update folder
   bool LoadUpdatePacket();

   // delete sended objects if need
   void Commit();

   bool HandleGet();
   bool Selecting(const Member* category);
   bool Removing(const Member* category);

   bool DoObjCommand(const Member* param);
   bool GetObjectFormat(const Member* param);

   const sockaddr_in& Address() const;
	void GetIPAddress(std::wstring* ip) const;

   HANDLE EvStop() const { return evStop; }

   void SetNeedRestart() { flags |= NeedRestart; }
   bool IsNeedRestart() const { return ((flags & NeedRestart) != 0); }

	Dispatcher* GetDispatcher()  { return dispatcher; }

	bool HandleCommand(const wchar_t* command, const Member* param);

   // ----------- IServObjectCreator ----------------------
   virtual ServObject* Create(const std::wstring &name) { return Build(name, true); }

   const ObjectDef* GetObjDef(const std::wstring& name) const;

   DWORD WriteToStream(SessionObject& so, bool addFormat);

	void PostObjects();

protected:
   AckReturn ReadPreviousVersion(Binary* packet);

   bool CheckAdmin(OutStream* os, const std::wstring& password);

   void UpdateToStream(Binary* dest);

   void SetObjDef(ObjectDef* od, Format* f, FormatList *fl);
	bool SendStreamPart();
	bool WriteObjectToStream(SessionObject* so, DWORD *cb, DWORD *count);

protected:
   class ServerFormatList : public FormatList
   {
   public:
      virtual Format* NewFormat(const std::wstring& name) const { return new ServObjFormat(name); }
   };

   typedef std::vector<IHandler*> HandlerList;
   ServerFormatList formats;
   HandlerList handlers;

   Dispatcher* dispatcher;
   ServerConfig config;

   User *user, *curUser;

   ExchangeList ack;
   ExchangeList response;
	ExchangeList trash;

   ServerAnswer *answer;
   OutStream outStream;

   Socket* socket;
   HANDLE evStop;
   DWORD flags;

   std::set<ObjectDef> localObjDef;
	std::vector<ISessionObject*> postObjects;
};

} // namespace GRServer

#endif

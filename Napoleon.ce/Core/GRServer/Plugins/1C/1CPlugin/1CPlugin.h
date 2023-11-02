/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * 1C plugin
 *
 * ert   19/08/2010   creating
 */

#ifndef __PLUGIN_1C_H
#define __PLUGIN_1C_H

#define DEFINE_PLUGIN
#include <iplugin.h>
#include <Socket.h>

#include <PluginName.h>
#include <stdobjs.h>

namespace GRServer {

class ThreadWorker;
class ExchangeList;
class ServObject;

class Plugin : public IPlugin
{
public:
   Plugin();
   ~Plugin();

   virtual const wchar_t* Name() const { return PLUGIN_NAME; }
   virtual const wchar_t* Version() const { return L"1.0.0.1"; }

   virtual bool Init(IServer* server);
   virtual bool Connect(Socket *socket, const wchar_t* password);
   virtual void Close();

   virtual IPluginConfig* GetConfig() const { return NULL; }

   void LostConnection();
   bool Read();

   ExchangeList* Do(const ServObject& object, const std::wstring& action, IFormatHolder* f, wchar_t** msg);

   FormatList* GetFormatList() const { return (FormatList*)objCreator.GetFormatList(); }
   ObjCreator* GetObjCreator() const { return (ObjCreator*)&objCreator; }

protected:
   void HandleObject(ExchangeList* object);
   void CloseConnection();
   void MakeResult(ExchangeList* object, int index); // index == -1 нету

   IServer* server;

   Socket socket;

   ObjCreator objCreator;
   ExchangeList *result;

   HANDLE evStop, evResult;
   ThreadWorker* worker;
   friend class ThreadWorker;
};

} // namespace GRServer

#endif
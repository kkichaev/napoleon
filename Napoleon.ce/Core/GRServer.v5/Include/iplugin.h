/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Интерфес плагина
 *
 * ert   21/08/2010   creating
 */

#ifndef __IPLUGIN_H
#define __IPLUGIN_H

#include <member.h>

#ifdef UNIX
   #define DECL_SPEC
#else
   #ifdef DEFINE_PLUGIN
      #define DECL_SPEC __declspec(dllexport)
   #else
      #define DECL_SPEC __declspec(dllimport)
   #endif
#endif

namespace GRServer {

class Socket;
struct IServer;
struct ISession;

struct IPluginConfig
{
   virtual ~IPluginConfig() {}

   virtual bool Configure(IServer* server, HWND owner) = 0;
};

struct IPlugin
{
   virtual ~IPlugin() {}

   virtual const wchar_t* Name() const = 0;

   virtual const wchar_t* Version() const = 0;

   virtual bool Init(IServer* server) = 0;

   virtual bool Connect(Socket *socket, const wchar_t* password) = 0;

   virtual void Close() = 0;

   virtual IPluginConfig* GetConfig() const = 0;

   virtual bool Handle(const wchar_t* command, const Member* param, ISession* session) { return false; }
};

/*
  Вызывается несколько раз - пока не выдаст false;
*/
extern "C" DECL_SPEC bool GetPlugin(IPlugin** plugin);

bool AddOnInit();

} // namespace GRServer

#endif

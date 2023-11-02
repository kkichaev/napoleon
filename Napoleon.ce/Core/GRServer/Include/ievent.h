/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * IEvent - интерфесный объект 
 *
 * ert   21/08/2010   creating
 */
#ifndef __IEVENT_H
#define __IEVENT_H

namespace GRServer {

struct IEvent
{
   enum Type
   {
      Login,
      FailLogin,
      Error,
      Warning,
      Get,
      Put,
      Remove,
      Impersonate,
      WriteCommit,
		BeforePut,
		ReadCommit,
		ResolveObjects,
		BeforeRead,
		OnLoad,
   };
};

} // namespace GRServer

#endif

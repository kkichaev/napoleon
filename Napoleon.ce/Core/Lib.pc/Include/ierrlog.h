/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * Интерфес нити
 *
 * ert   25/07/2012   creating
 */
#ifndef __I_ERR_LOG_H
#define __I_ERR_LOG_H

struct _EXCEPTION_POINTERS;
namespace GRServer {

struct IErrorLogger
{
   enum DebugLevel { None, Short, Full };

   //
   //------------------- обработка ошибок ---------------
   //
   virtual void AddLog(const char* msg, ... ) = 0;

   virtual void AddError(bool critical, const char* msg, ... ) = 0;

   virtual void AddLog(DebugLevel level, const char* msg, ... ) = 0;
};

} // namespace GRServer

#endif

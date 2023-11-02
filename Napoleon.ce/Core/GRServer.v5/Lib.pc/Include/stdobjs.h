/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Стандартные объекты
 *
 * ert   21/08/2010   creating
 */
#ifndef __GRSERVER_STD_OBJECTS_H
#define __GRSERVER_STD_OBJECTS_H

#include "servobj.h"
#include <ServerDefs.h>

class OutStream;

namespace GRServer {

class ServerCommandFormat : public Format
{
public:
   ServerCommandFormat();

   static const wchar_t* Name() { return SERVER_COMMAND; }
};

class ServerAnswerFormat : public Format
{
public:
   ServerAnswerFormat();

   static const wchar_t* Name() { return SERVER_ANSWER; }
};

class ObjCreator : public IServObjectCreator
{
public:
   ObjCreator() {}

   FormatList* GetFormatList() const { return (FormatList*)&formats; }

   virtual ServObject* Create(const std::wstring &name)
   {
      Format *fmt = formats.GetFormat(name);
      if( fmt == NULL )
      {
         fmt = new Format(name);
         formats.AddFormat(fmt, true);
      }

      return new ServObject(fmt);
   }

protected:
   FormatList formats;
};

//bool SendCommand(Socket* socket, const wchar_t* cmd, const wchar_t* param, const wchar_t* user = L"", const wchar_t *password = L"", unsigned duration = 0);
bool SendAnswer(Socket* socket, bool res, const wchar_t* message);
bool WriteAnswer(OutStream* stream, bool res, const wchar_t* message);
bool ReadAnswer(Socket* socket, DWORD timeout, bool* ret, std::wstring* answer, unsigned* duration = NULL, HANDLE evStop = 0);

} // namespace GRServer

#endif

/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Службы доступные через IServer
 *
 * ert   20/08/2010   creating
 */
#ifndef __GR_SERVICES_H
#define __GR_SERVICES_H

#include "ievent.h"
#include "iobject.h"

namespace GRServer {

struct ISession;

#define OBJDEF_SERVICE L"ObjectDef"
struct IObjectDef
{
   enum Flags { RemoveOnCommit = 1, SendAlways = 2, Internal = 4, HaveEFields = 8 };
   enum AccessFlags { ReadAccess = 0x10, NonAccess = 0x20, AccFlags = 0x30 };

   //virtual bool Load(const std::string& fileName) = 0;
   virtual const IObjectData* Get(const std::wstring& objName) = 0;
//   virtual void GetObjectsName(std::vector<std::wstring>* names, DWORD flags) = 0;
   virtual void GetObjectsName(CVector<CString>** names, DWORD flags) = 0;
   virtual bool Fire(IEvent::Type type, ISession* session, IObject* param = NULL) = 0;
};


} // namespace GRServer

#endif


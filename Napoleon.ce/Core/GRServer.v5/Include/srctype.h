/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * SourceType
 *
 * ert   12/03/2010   creating
 */ 
#ifndef __GR_SERVER_SOURCE_TYPE_H
#define __GR_SERVER_SOURCE_TYPE_H

#include <string>
#include <isessobj.h> // move paramlist to lib.pc

namespace GRServer {

struct ISessionObject;

enum SourceType
{
   stCommon,
   stInternal,
	stAny
};

SourceType SourceTypeFromString(std::wstring& val);


struct SourceDef
{
   SourceType type;
   std::wstring name;
   ParamList parameters;
};

class SourceDefList : public std::vector<SourceDef>
{
public:
   enum SourceKind { skReader, skWriter, skRemover };

   void* CreateSource(SourceKind kind, void* parent, const ISessionObject& object, std::wstring *name, SourceType* srcType = NULL) const;
};

} // namespace GRServer

#endif


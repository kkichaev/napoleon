/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * Источник сервера
 *
 * ert   21/03/2012   creating
 */ 
#ifndef __SERVER_SOURCE_H
#define __SERVER_SOURCE_H

#include "parse.h"
#include "member.h"
#include "datasource.h"

namespace GRServer {

class ServerSourceCreator : public IDataSource::ICreator
{
public:
   virtual const wchar_t* Name() const { return L"ServerInfo"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};

class ActiveUsersCreator : public IDataSource::ICreator
{
public:
	virtual const wchar_t* Name() const { return L"ActiveUsers"; }
	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};

} // namespace GRServer

#endif

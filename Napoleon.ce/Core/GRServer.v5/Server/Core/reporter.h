#ifndef __REPORTER_H
#define __REPORTER_H

#include "sessobj.h"
namespace GRServer {

struct ReporterCreater : public IDataSource::ICreator
{
	virtual const wchar_t* Name() const { return L"ReportSource"; }

	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};

} //namspace GRServer

#endif
/*
 * Copyright (C), 2009 - 2013, Денис Мосягин
 *
 * OleDB Writer
 *
 * ert   26/07/2013   creating
 */

#pragma once

#include "Source.h"
#include "Binder.h"

namespace GRServer {

class WriteBinder : public ParamBinder
{
public:
   WriteBinder() : fkIndex(0xFFFFFFFF), orderIndex(0) {}
   ~WriteBinder();

   bool PrepareWrite(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor);
   bool Write(const Object& o, const Object* parent);

   DWORD orderIndex;
protected:
   DWORD fkIndex;

protected:
   bool CreateStmt(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor);

   std::vector<FileField*> files;
};

class Writer : public IDataSource::IWriter
{
public:
   Writer(const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor);

	~Writer();

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid) { return Write(o, NULL, rid); }
   virtual void Close();

   bool Write(const Object& o, const Object* parent, RowID *rid);

protected:
   int doCount;
   bool rootObject;

   SQLHDBC hDbc;
   ODBCFlavor* flavor;
   WriteBinder* writer;

	//std::string tableName;

#ifdef DEBUG
   int totalCount;
#endif
};

} // namespace GRServer
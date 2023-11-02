/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Catalog Writer
 *
 * ert   03/10/2009   creating
 */
#ifndef __CATALOG_WRITER_H
#define __CATALOG_WRITER_H

#include "datasource.h"

namespace GRServer {

class CatalogItemsWriter : public IDataSource::IWriter
{
public:
   CatalogItemsWriter(const SessionObject& object);

   virtual bool Prepare(const ISessionObject& object) { return true; }
   virtual bool Write(const Object& o, RowID *rid) { return true; }
   virtual void Close() {}

	virtual void AddChild(IWriter* writer, const std::wstring& typeName);

   void SetDBFields(std::vector<DBRec>* fields);
   bool Write(const Object& o, DataForm& base);

protected:
   FieldWriter writer;
   int childIndex;
   const GRServer::Format* format;
   const IObjectData* objDef;
	CatalogItemsWriter* childWriter;
};

class CatalogWriter : public DBFWriter
{
public:
   CatalogWriter(const GRServer::Format& fmt, const IObjectData* _objDef, const std::string& _fileName) :
      DBFWriter(fmt, _objDef, _fileName),
      childWriter(NULL)
   {
   }

   virtual bool Write(const Object& o, RowID *rid);
   virtual void AddChild(IWriter* writer, const std::wstring& typeName);

protected:
   virtual void AddFields(std::vector<DBRec>* dbFields)
   {
      if( childWriter != NULL )
         childWriter->SetDBFields(dbFields);
   }

   CatalogItemsWriter* childWriter;
};


} // namespace GRServer

#endif

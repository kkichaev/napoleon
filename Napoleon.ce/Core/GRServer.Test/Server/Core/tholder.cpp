/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Table Holder
 *
 * ert   03/10/2009   creating
 */
#include "stdafx.h"
#include "server.h"
#include "sources.h"
#include "cwriter.h"
#include "session.h"
#include "dbf.h"
#include "token.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

class CatalogReader;
bool FindCatalog(int *index, WORD* offset, WORD* width, const ParamList& parameters, const SessionObject& object, const DataForm& base);

struct ItemsSet
{
   virtual ~ItemsSet() {}

   virtual bool Add(Object* o, DataForm& base) = 0;
   virtual unsigned Size() const = 0;
   virtual bool Get(Object* o) = 0;
};

template <class Key, class Cmp = std::less<Key> > class ItemSetBase : public ItemsSet
{
public:
   ItemSetBase(int _member) : member(_member) {}

   typedef std::map<Key, Object*, Cmp> ItemsMap;
   virtual ~ItemSetBase()
   {
      typename ItemsMap::iterator i = items.begin();
      for( ; i != items.end(); i++ )
         delete i->second;
   }

   virtual unsigned Size() const { return (unsigned)items.size(); }

   virtual bool Get(Object* o)
   {
      typename ItemsMap::iterator i = items.begin();
      Object* ob = i->second;
      bool retVal = ob->MoveTo(o);
      items.erase(i);
      delete ob;

      return retVal;
   }

   ItemsMap items;
   int member;
};

struct StringItemsSet : public ItemSetBase<std::wstring>
{
   StringItemsSet(int _member) : ItemSetBase(_member) {}
   StringItemsSet(WORD _offset, WORD _width) : ItemSetBase(-1), offset(_offset), width(_width) {}

   virtual bool Add(Object* o, DataForm& base)
   {
      std::wstring str;
      if( member >= 0 )
      {
         Member& m = o->at(member);
         str = (const std::wstring&)(*m.str);
      } else
      {
         USES_CONVERSION;
         char* buf = (char*)alloca(width+1);
         memcpy(buf, base.GetRec() + offset, width);
         buf[width]='\0';

         std::string sbuf;
         const char* p = Trunc(buf, &sbuf);
         if( *p == '\0') return false;

         str = A2W_CP(p, DBF_CODE_PAGE);
      }

      ItemsMap::const_iterator fnd = items.find(str);
      if( fnd == items.end() )
      {
         items.insert(ItemsMap::value_type(str, o));
         return true;
      }
      return false;
   }

   const wchar_t* Current() const { return (items.size()) ? items.begin()->first.c_str() : NULL; }

   WORD offset, width;
};

struct NumberItemsSet : public ItemSetBase<double>
{
   NumberItemsSet(int _member) : ItemSetBase(_member) {}

   virtual bool Add(Object* o, DataForm& base)
   {
      Member& m = o->at(member);
      ItemsMap::const_iterator fnd = items.find(m.number);
      if( fnd == items.end() )
      {
         items.insert(ItemsMap::value_type(m.number, o));
         return true;
      }
      return false;
   }
};

struct DateItemsSet : public ItemSetBase<FILETIME,CmpFileTime>
{
   DateItemsSet(int _member) : ItemSetBase(_member) {}

   virtual bool Add(Object* o, DataForm& base)
   {
      Member& m = o->at(member);
      ItemsMap::const_iterator fnd = items.find(m.datetime);
      if( fnd == items.end() )
      {
         items.insert(ItemsMap::value_type(m.datetime, o));
         return true;
      }
      return false;
   }
};

class UniqueReader : public ObjectReader
{
public:
   bool IsInited() const { return (uniqueData.size() > 0); }
   void SetUniqueFields(const std::wstring& fields, const DataForm& base) { uniqueData.SetData(fields, base); }
   bool IsUnique(const DataForm& base)
   {
      std::string str;
      uniqueData.Read(&str, base);
      return items.insert(str).second;
   }

protected:
   struct Data
   {
      WORD offset;
      WORD width;
   };

   class UniqueData : public std::vector<Data>
   {
   public:
      void SetData(const std::wstring& fields, const DataForm& base)
      {
         if( size() )
            return;

         USES_CONVERSION;
         std::wstring::size_type off = 0, nextOff;
         while( true )
         {
            nextOff = fields.find(L',', off);
            std::wstring val = fields.substr(off, (nextOff != std::wstring::npos) ? nextOff - off : std::wstring::npos);

            DBField *f = base.GetFieldRef(W2A_CP(val.c_str(), DBF_CODE_PAGE));
            if( f != NULL )
            {
               Data d;
               d.offset = f->offset;
               d.width = f->width;

               push_back(d);
            }

            if( nextOff == std::wstring::npos )
               break;
            off = nextOff + 1;
         }
      }

      void Read(std::string* value, const DataForm& base) const
      {
         value->clear();
         const char *rec = base.GetRec();

         const_iterator i = begin();
         for( ; i != end(); i++ )
            value->append(rec + i->offset, i->width);
      }
   };

   UniqueData uniqueData;
   std::set<std::string> items;
};

class CatalogReader;
class CatalogItemTable : public IDataSource::IReader
{
public:
   CatalogItemTable(const GRServer::Format& fmt) : items(NULL), objectFormat(fmt) {}
   ~CatalogItemTable() { Close(); }

   bool Open(const ParamList& parameters, const SessionObject& object);

   bool NextRec(Object* parent, DataForm& base);
   void Clear() { items->Clear(); }

   virtual void Close()
   {
      if( items != NULL )
      {
         delete items;
         items = NULL;
      }
   }

   virtual void Remove() {}

   virtual bool MoveNext(Object *parentObject);

   virtual bool Get(Object* object) const
   {
      return items->Get(object);
   }

   virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
   virtual const Member* Value(const wchar_t* name) const { return NULL; }

protected:
   ChildItemsHolder* items;
   UniqueReader reader;
   CatalogReader *parent;

   bool catalogIsField;
   std::wstring uniqueFields;

   const GRServer::Format& objectFormat;
};

class CatalogReader : public DBFReader
{
public:
   CatalogReader(const GRServer::Format& fmt) : DBFReader(fmt), catalog(NULL), items(NULL), width(0), offset(0), curObject(NULL)
   { assigned = false; }

   ~CatalogReader() { Close(); }

   virtual bool Open(const std::string& fileName, const SessionObject& object, FilterReader::Data& filter, const ParamList& parameters);

   void AddCatalogItem(CatalogItemTable *rt) { catalog = rt; }

   DataForm& Base() { return base; }

   virtual bool MoveNext(Object *parentObject);

   void Init(Object *parentObject);

   const wchar_t* CurrentCatalog() const { return curCatalog.c_str(); }

   virtual bool Get(Object* o) const;

   virtual void Close();

   void CatalogData(int *index, WORD *width, WORD *offset) const
   {
      *index = catalogIndex; *width = this->width; *offset = this->offset;
   }

   bool Ordered() const { return ordered; }

protected:
   bool NextOrdered(Object* parentObject);

protected:
   CatalogItemTable *catalog;
   ItemsSet* items;

   int catalogIndex;
   WORD width, offset;

   mutable Object* curObject;
   mutable std::wstring curCatalog;

   bool assigned;
   bool ordered;
};

bool CatalogItemTable::Open(const ParamList& parameters, const SessionObject& object)
{
   bool retVal = false;
   const ISessionObject* parentI = object.Parent();
   const SessionObject* parent = (parentI == NULL) ? NULL : (const SessionObject*)parentI->Self();
   if( parent != NULL )
   {
      ObjectSource* os = parent->GetSource();
      if( os != NULL && os->readerName.compare(TEHCreator().Name()) == 0 )
      {
         CatalogReader* cr = (CatalogReader*)os->reader;
         if( cr != NULL )
         {
            cr->AddCatalogItem(this);
            this->parent = cr;

            reader.Create(object, cr->Base());
            retVal = true;

            const Parameter* param = parameters.Find(L"unique", -1);
            if( param )
            {
               const Session& s = (const Session&)object.GetSession();
               s.Parse(&uniqueFields, param->value, &object);
            }

            int m;
            WORD offset, width;
            cr->CatalogData(&m, &width, &offset);
            if( m >= 0 )
            {
               catalogIsField = true;
               const MemberFormat& mf = parent->format->at(m);
               if( mf.type == MemberFormat::mtNumber ) items = new NumberItemsHolder(m);
               else if( mf.type == MemberFormat::mtString ) items = new StringItemsHolder(m);
               else if( mf.type == MemberFormat::mtDateTime ) items = new DateTimeItemsHolder(m, mf.format.dateFormat);
               else retVal = false;
            } else
            {
               catalogIsField = false;
               items = new StringItemsHolder(offset, width);
            }
         }
      }
   }

   return retVal;
}

bool CatalogReader::Open(const std::string& fileName, const SessionObject& object, FilterReader::Data& filter, const ParamList& parameters)
{
   if( DBFReader::Open(fileName, object, filter) == false ) return false;

   const Parameter *p = parameters.Find(L"ordered", 2);
   ordered = (p != NULL && _wtoi(p->value.c_str()) > 0);

   bool retVal = FindCatalog(&catalogIndex, &offset, &width, parameters, object, base);
   if( retVal )
   {
      if( catalogIndex >= 0 )
      {
         const MemberFormat& mf = object.format->at(catalogIndex);
         if( mf.type == MemberFormat::mtNumber ) items = new NumberItemsSet(catalogIndex);
         else if( mf.type == MemberFormat::mtString ) items = new StringItemsSet(catalogIndex);
         else if( mf.type == MemberFormat::mtDateTime ) items = new DateItemsSet(catalogIndex);
         else retVal = false;
      } else
      {
         items = new StringItemsSet(offset, width);
      }
   }
   return retVal;
}

void CatalogReader::Init(Object *parentObject)
{
   if( !ordered )
   {
      while( DBFReader::MoveNext(parentObject) )
      {
         Object *o = Object::Create(objectFormat);
         DBFReader::Get(o);
         bool needRemove = !items->Add(o, base);

         // set catalog
         if( catalogIndex < 0 )
         {
            const wchar_t *p = ((StringItemsSet*)items)->Current();
            curCatalog = (p == NULL) ? L"" : p;
         }

         catalog->NextRec(o, base);
         if( needRemove ) delete o;
      }
   }
}

void CatalogReader::Close()
{
   DBFReader::Close();

   delete items;
   items = NULL;

   delete curObject;
   curObject = NULL;
}


bool CatalogReader::Get(Object* o) const
{
   if( ordered )
   {
      if( curObject != NULL )
      {
         curObject->MoveTo(o);
         delete curObject;
         curObject = NULL;

         return true;
      }

      return false;
   }
   if( catalogIndex < 0 )
   {
      const wchar_t *p = ((StringItemsSet*)items)->Current();
      curCatalog = (p == NULL) ? L"" : p;
   }
   return items->Get(o);
}

bool CatalogReader::NextOrdered(Object* parentObject)
{
   if( !DBFReader::MoveNext(parentObject) )
      return false;

   curObject = Object::Create(objectFormat);
   DBFReader::Get(curObject);

   catalog->Clear();
   catalog->NextRec(curObject, base);

   char *keyValue = (char*)alloca(width);
   memcpy(keyValue, base.GetRec()+offset, width);

   while(true)
   {
      if( !DBFReader::MoveNext(parentObject) )
         break;

      if( memcmp(keyValue, base.GetRec()+offset, width) == 0 )
      {
         catalog->NextRec(curObject, base);
      } else
      {
         rc--;
         break;
      }
   }

   return true;
}

bool CatalogReader::MoveNext(Object *parentObject)
{
   if( catalog == NULL ) return false;

   if( !assigned )
   {
      Init(parentObject);
      assigned = true;
   }

   return (ordered) ? NextOrdered(parentObject) : (items->Size() > 0);
}

bool CatalogItemTable::MoveNext(Object *parentObject)
{
   if( catalogIsField ) return items->MoveNext(parentObject);
   else
   {
      const wchar_t* catalog = parent->CurrentCatalog();
      return (catalog != NULL) ? ((StringItemsHolder*)items)->MoveNext(catalog) : false;
   }
}

bool CatalogItemTable::NextRec(Object* parent, DataForm& base)
{
   if( !uniqueFields.empty() )
   {
      if( reader.IsInited() == false)
         reader.SetUniqueFields(uniqueFields, base);
      if( !reader.IsUnique(base) )
         return false;
   }

   Object *o = Object::Create(objectFormat);
   reader.Read(o, base);

   if( catalogIsField ) items->Add(parent, o);
   else items->Add(base, o);

   return true;
}


bool CatalogWriter::Write(const Object& o, RowID *rid)
{
   base.ResetRec();
   if( !writer.Write(o, base) ) return false;
   if( childWriter == NULL || !childWriter->Write(o, base) )
      base.Append();

	//
	// ошибка СПК - пишет в SQL базу Visit$items до записи визита, т.к. определен DBFCatalogTable для посещения
	// здесь не должно быть записи
	//
   //WriterList::iterator i = childs.begin();
   //for( ; i != childs.end(); i++ )
   //   (*i)->Write(o, NULL);

   return true;
}

void CatalogWriter::AddChild(IDataSource::IWriter* writer, const std::wstring& typeName)
{
   if( typeName.compare(TRHCreator().Name()) == 0 )
      childWriter = (CatalogItemsWriter*)writer;
   else
      childs.push_back(writer);
}

CatalogItemsWriter::CatalogItemsWriter(const SessionObject& object) : childIndex(-1), childWriter(NULL)
{
   format = object.format;
   objDef = object.GetObjectDef();

   const ISessionObject *parentObjI = object.Parent();
   const SessionObject *parentObj = (parentObjI == NULL) ? NULL : (const SessionObject *)parentObjI->Self();
   if( parentObj && objDef )
   {
      const std::wstring& oname = object.Name();
      size_t off = oname.find_last_of(L'$');
      childIndex = parentObj->format->FindMember(oname.substr(off+1).c_str());

      writer.AddFields(object);
   }
}

void CatalogItemsWriter::AddChild(IDataSource::IWriter* writer, const std::wstring& typeName)
{
	if (typeName.compare(TRHCreator().Name()) == 0)
		childWriter = (CatalogItemsWriter*)writer;
	else
		childs.push_back(writer);
}

void CatalogItemsWriter::SetDBFields(std::vector<DBRec>* dbFields)
{
   ObjectDef::Fields::const_iterator fi = objDef->fields.begin();
   for( ; fi != objDef->fields.end(); fi++ )
      SetDBField(dbFields, (*fi));
	if (childWriter != NULL)
		childWriter->SetDBFields(dbFields);
}

bool CatalogItemsWriter::Write(const Object& o, DataForm& base)
{
   bool ret = false;
   const Member& m = o.at(childIndex);
   const ServObject* object = m.object;
   if( object != NULL )
   {
      ServObject::const_iterator i = object->begin();
      for( ; i != object->end(); i++ )
      {
         writer.Write(*(*i), base);
			if (childWriter == NULL || !childWriter->Write(*(*i), base))
				base.Append();
         ret = true;
      }
   }

   return ret;
}

bool FindCatalog(int *index, WORD* offset, WORD* width, const ParamList& parameters, const SessionObject& object, const DataForm& base)
{
   const Parameter *tname = parameters.Find(L"catalog", 1);
   const IObjectData* od = object.GetObjectDef();
   if( tname == NULL || od == NULL ) return false;

   Token t;
   if( ((Session&)object.GetSession()).Parse(&t, tname->value, &object) && t.type == Token::ttString )
   {
      const GRServer::Format* fmt = object.format;
      *index = fmt->FindMember(t.value.str->c_str());

      std::string fieldName;
      USES_CONVERSION;
      if( *index >= 0 )
      {
         const MemberFormat& mf = object.format->at(*index);
         const ObjectDef::Field* f = od->FindField(mf.name);
         if( f != NULL )
         {
            fieldName = W2A_CP(f->data.c_str(), DBF_CODE_PAGE);
         }
      } else
      {
         if( t.value.str->at(0) == L'[' )
         {
            fieldName = W2A_CP(t.value.str->substr(1, t.value.str->size()-2).c_str(), DBF_CODE_PAGE);
         }
      }
      if( fieldName.empty() ) return false;
      DBField* dbf = base.GetFieldRef(fieldName.c_str());
      if( dbf == NULL ) return false;

      *width = dbf->width;
      *offset = dbf->offset;
   }

   return true;
}

//
//---------------------------------------------------- Creators --------------------------------------------------
//
IDataSource::IReader* TEHCreator::Create(const std::string& fileName, const ISessionObject& iobject,
                                         FilterReader::Data& filter, const ParamList& parameters) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   CatalogReader *reader = new CatalogReader(*object.format);

   if( !reader->Open(fileName, object, filter, parameters) )
   {
      delete reader;
      reader = NULL;
   }

   return reader;
}

IDataSource::IWriter* TEHCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
                                               const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   std::string fileName;
   if( !GetTableName(&fileName, parameters, object) ) return NULL;

   return new CatalogWriter(*object.format, object.GetObjectDef(), fileName);
}

IDataSource::IReader* TRHCreator::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   CatalogItemTable *cit = new CatalogItemTable(*object.format);
   if( !cit->Open(parameters, object) )
   {
      delete cit;
      cit = NULL;
   }

   return cit;
}

IDataSource::IWriter* TRHCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
                                               const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   return new CatalogItemsWriter(object);
}


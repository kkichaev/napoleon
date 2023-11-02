/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Источник данных
 *
 * ert   30/09/2009   creating
 */
#ifndef __SOURCES_H
#define __SOURCES_H

#include "creators.h"
#include "objdef.h"
#include "dbf.h"
#include <map>

namespace GRServer {

struct FilterData
{
   enum Operations { None, InSet, Equal };

   struct DBData
   {
      WORD offset; //dest field
      WORD length;

      const SessionObject* src;
      int srcIndex;
   };

   bool destIsObjectField;
   std::wstring destField;
   Operations op;
   std::wstring source;
   std::wstring sourceField;

   FilterData() : op(None) {}

   bool Parse(const std::wstring& str);
   bool SetDBData(DBData* data, const DataForm &base, const SessionObject &object, bool addData) const;

   void SetUserFilter(const std::wstring& filter) { userFilter = filter; }

protected:
   std::wstring userFilter; // userid is null || userid = 'code'
};

struct FieldReader
{
   virtual ~FieldReader() {}
   virtual void Read(Object* object, const DataForm& base) const = 0;
};

struct IObjectReader
{
   virtual ~IObjectReader() {}

   virtual bool Create(const SessionObject& object, const DataForm& base) = 0;
   virtual void Read(ServObject* object, const DataForm& base) const = 0;
};

class ObjectReader : std::vector<Pointer<FieldReader> >, public IObjectReader
{
public:
   ObjectReader() {}

   bool Create(const SessionObject& object, const DataForm& base);
   void Read(ServObject* object, const DataForm& base) const;

   void Read(Object* o, const DataForm& base) const;
};

class DBFReader : public IDataSource::IReader
{
public:
   static const char* ROWID;
   class Filter
   {
   public:
      struct Set
      {
         virtual ~Set() {}
         virtual void Add(const Member& m) = 0;
         virtual bool InSet(const char* value) = 0;
      };

      Filter(const DataForm& base, const FilterData& filter, const SessionObject& object);
      ~Filter();

      bool InSet(const DataForm& base);

   protected:
      Set *set;
      WORD offset, length;
   };

   DBFReader(const GRServer::Format& fmt);
   ~DBFReader();

   virtual bool Open(const std::string& fileName, const SessionObject& object, FilterReader::Data& filter);

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object*) const;

   virtual const MemberFormat* Type(const wchar_t* name) const;
   virtual const Member* Value(const wchar_t* name) const;

   virtual void Close();

   virtual void Remove();

protected:

   bool SetValue(Member* member, int index) const;

protected:
   DataForm base;
   long rc;

   ObjectReader items;
   IFilterInSet* filter;

   const GRServer::Format& objectFormat;
   const SessionObject* thisObject;

   mutable MemberFormat mf;
   mutable Member mv;
   mutable CString value;

	//bool doLoging;
};

struct ChildItemsHolder
{
public:
   virtual ~ChildItemsHolder() {}

   virtual void Add(DataForm& base, Object* object) = 0;
   virtual void Add(Object* parent, Object* object) = 0;

   virtual bool Find(Object* parent) const = 0;

   virtual bool MoveNext(Object* parentObject) = 0;
   virtual bool Get(Object* object) = 0;

   virtual void Clear() = 0;
};

struct CmpFileTime
{
   bool operator()(const FILETIME& _left, const FILETIME& _right) const
	{
	   return (CompareFileTime(&_left, &_right) < 0);
	}

};

template <class Key, class Cmp = std::less<Key> > class ChildItemsHolderBase : public ChildItemsHolder
{
public:
   typedef std::vector<Object*> ObjList; // схема в Move не позволяет использовать Poniter
   typedef std::map<Key, ObjList, Cmp> Items;

   ChildItemsHolderBase(int _pi, WORD _offset, WORD _length) : parentIndex(_pi), offset(_offset), length(_length) { current = items.end(); }
   ~ChildItemsHolderBase() { Clear(); }

   virtual const Key& GetField(const Member& m) const = 0;

   virtual bool Find(Object* parent) const
   {
      Member& m = parent->at(parentIndex);
      typename Items::const_iterator fnd = items.find(GetField(m));
      return (fnd != items.end());
   }

   virtual bool MoveNext(Object* parentObject)
   {
      Member& m = parentObject->at(parentIndex);
      current = items.find(GetField(m));

      return ( current != items.end() && current->second.size() > 0 );
   }

   virtual bool Get(Object* object)
   {
      bool retVal = false;

      if( current != items.end() )
      {
         ObjList& ol = current->second;
         if( ol.size() != 0 )
         {
            Object* o = ol.front();
            if( o->MoveTo(object) )
            {
               ol.erase(ol.begin());
               retVal = true;
            }
            delete o;
         }
      }
      return retVal;
   }

   virtual void Clear()
   {
      typename Items::iterator i = items.begin();
      for( ; i != items.end(); i++ )
      {
         ObjList::iterator oi = i->second.begin();
         for( ; oi != i->second.end(); oi++ )
            delete (*oi);
      }
      items.clear();
   }

protected:
   int parentIndex;
   WORD offset, length;
   Items items;
   typename Items::iterator current;
};

class StringItemsHolder : public ChildItemsHolderBase<std::wstring>
{
public:
   StringItemsHolder(const FilterData::DBData& data);
   StringItemsHolder(WORD _offset, WORD _length) : ChildItemsHolderBase(-1, _offset, _length) {}
   StringItemsHolder(int pi) : ChildItemsHolderBase(pi, 0, 0) {}

   virtual void Add(DataForm& base, Object* object);
   virtual void Add(Object* parent, Object* object);

   virtual const std::wstring& GetField(const Member& m) const { return (const std::wstring&)*m.str; }

   bool MoveNext(const wchar_t* field);

   bool Find(const wchar_t* parentField) const
   {
      Items::const_iterator fnd = items.find(parentField);
      return (fnd != items.end());
   }
};

class NumberItemsHolder : public ChildItemsHolderBase<double>
{
public:
   NumberItemsHolder(const FilterData::DBData& data);
   NumberItemsHolder(int pi) : ChildItemsHolderBase(pi, 0, 0) {}

   virtual void Add(DataForm& base, Object* object);
   virtual void Add(Object* parent, Object* object);

   virtual const double& GetField(const Member& m) const { return m.number; }
};

class DateTimeItemsHolder : public ChildItemsHolderBase<FILETIME, CmpFileTime>
{
public:
   DateTimeItemsHolder(const FilterData::DBData& data, MemberFormat::DateFormat _format);
   DateTimeItemsHolder(int pi, MemberFormat::DateFormat _format) : ChildItemsHolderBase(pi, 0, 0), format(_format) {}

   virtual void Add(DataForm& base, Object* object);
   virtual void Add(Object* parent, Object* object);

   virtual const FILETIME& GetField(const Member& m) const { return m.datetime; }

protected:
   MemberFormat::DateFormat format;
};

class ChildDBFReader : public DBFReader
{
public:
   ChildDBFReader(const GRServer::Format& fmt) : DBFReader(fmt), holder(NULL) {}
   ~ChildDBFReader() { delete holder; }

   virtual bool Open(const std::string& fileName, const SessionObject& object, FilterReader::Data& filter);

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object*) const;

   virtual void Close();

protected:
   //ChildItemsHolder* itemsHolder;
   IFilterObjHolder *holder;
};

struct IFieldWriter
{
   IFieldWriter(int mi, char *name) : index(mi)
   {
      strncpy(this->name, name, sizeof(this->name));
      this->name[sizeof(this->name)-1] = '\0';
   }

   virtual ~IFieldWriter() {}
   virtual bool Write(const Object& o, DataForm& base) = 0;

   int index;
   char name[15];
};

class FieldWriter : public std::vector<Pointer<IFieldWriter> >
{
public:
   bool Write(const Object& o, DataForm& base)
   {
      iterator i = begin();
      for( ; i != end(); i++ )
         if( (*i)->Write(o, base) == false ) return false;

      return true;
   }

   bool AddFields(const SessionObject& object);
};

class DBFWriter : public IDataSource::IWriter
{
   class KeyToRec
   {
   public:
      KeyToRec();

      void Setup(const DataForm& base, const IObjectData& objDef);
      int GetRec(const DataForm& base);
		void PutRec(const DataForm& base, int rec);
      void Close(DataForm* base);

   protected:
      void LoadValue(std::string* value, const DataForm& base);
      void LoadKeyFields(const DataForm& base, const IObjectData& objDef);

      struct Data
      {
         std::vector<int> recs;
         bool used;
      };

      struct FieldData
      {
         WORD offset;
         WORD width;
      };

      typedef std::map<std::string, Data> KeyData;
      KeyData data;

      std::vector<FieldData> keyFields;
   };

public:
   DBFWriter(const GRServer::Format& fmt, const IObjectData* _objDef, const std::string& _fileName, bool appendMode = false);

   virtual ~DBFWriter() { Close(); }

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close();

   static void MakeDBFName(char* dest, const std::wstring& name);

protected:
   bool CreateBase(const SessionObject& object);
   bool WriteObject(const Object& object);

   virtual void AddFields(std::vector<DBRec>* dbFields) {}
   virtual void BeforeWrite(const Object& o) {}

protected:
   const GRServer::Format& format;
   const IObjectData* objDef;
   std::string fileName;

   FieldWriter writer;
   DataForm base;
   KeyToRec keyRec;

   int objIndex;
   bool appendMode;
};

bool GetTableName(std::string* tableName, const ParamList& parameters, const SessionObject& object);
bool SetDBField(std::vector<DBRec>* fields, const ObjectDef::Field& src);
FILETIME ReadFileTime(MemberFormat::DateFormat format, const char* str);

} // namespace GRServer

#endif

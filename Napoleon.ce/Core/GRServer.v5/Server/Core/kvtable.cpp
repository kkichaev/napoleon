/*
 * Copyright (C), 2009 - 2022, Denis Mosiagin
 *
 * KeyValueTable
 *
 * ert   02/10/2009   creating
 */
#include "stdafx.h"
#include "server.h"
#include "sources.h"
#include "session.h"
#include "srvutility.h"
#include "dbf.h"
#include "token.h"
#include <algorithm>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

class KeyValueReader : public IDataSource::IReader
{
public:
   KeyValueReader() {}
   ~KeyValueReader()
   {
      delete key.str;
      delete value.str;
   }

   bool Open(const std::string& fileName, const SessionObject& object);

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object*) const;

   virtual const MemberFormat* Type(const wchar_t* name) const
   {
      if( _wcsicmp(name, L"key") == 0 ) return &keyFormat;
      if( _wcsicmp(name, L"value") == 0 ) return &valueFormat;
      return NULL;
   }

   virtual const Member* Value(const wchar_t* name) const
   {
      if( _wcsicmp(name, L"key") == 0 ) return &key;
      if( _wcsicmp(name, L"value") == 0 ) return &value;
      return NULL;
   }

   virtual void Remove() { _unlink(fileName.c_str()); }

   virtual void Close() {}

   std::string fileName;

   MemberFormat keyFormat, valueFormat;
   mutable Member key, value;
   int keyIndex, valueIndex;

   std::map<std::string, std::string> values;
};

class KVRemover :  public IDataSource::IRemover
{
public:
   KVRemover(const std::string& fileName) { this->fileName = fileName; }

   virtual bool Remove(const wchar_t* filter) { return (DeleteFileA(fileName.c_str()) != 0); }

   virtual void Close() {}

protected:
   std::string fileName;
};

class KVWriter : public IDataSource::IWriter
{
public:
   KVWriter(const std::string& fileName) { this->fileName = fileName; }

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close();

protected:
   std::string fileName;
   int keyIndex, valueIndex;
   std::map<std::string, std::string> values;
};

class UnionReader : public IDataSource::IReader
{
public:
   UnionReader(const ParamList& parameters, const SessionObject& object);

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object*) const;

   virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
   virtual const Member* Value(const wchar_t* name) const { return NULL; }

   virtual void Remove() {}
   virtual void Close();

protected:
   struct MemberWriter
   {
      int srcIndex;
      int destIndex;
      MemberFormat format;

      void Write(Object *dest, const Object& src) const;
   };

   bool OpenNext();
   void CreateWriter(const SessionObject& src);

   const SessionObject& destObject;

   std::vector<MemberWriter> writer;
   std::vector<SessionObject*> srcObjects;
   SessionObject* currentObject;
   int current;
};

class  KeyValueDBFReader : public IDataSource::IReader
{
public:
   KeyValueDBFReader(const std::wstring& key, const SessionObject& object);

   ~KeyValueDBFReader()
   {
      delete key.str;
      delete value.str;
   }

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object*) const;

   virtual const MemberFormat* Type(const wchar_t* name) const
   {
      if( _wcsicmp(name, L"key") == 0 ) return &keyFormat;
      if( _wcsicmp(name, L"value") == 0 ) return &valueFormat;
      return NULL;
   }

   virtual const Member* Value(const wchar_t* name) const
   {
      if( _wcsicmp(name, L"key") == 0 ) return &key;
      if( _wcsicmp(name, L"value") == 0 ) return &value;
      return NULL;
   }

   virtual void Remove() {}
   virtual void Close() {}

   bool ReadValue(const std::string& file, const std::wstring& itemName, const std::wstring& itemKey);
protected:

   MemberFormat keyFormat, valueFormat;
   mutable Member key, value;
   int keyIndex, valueIndex;
   bool readed;
};

static bool GetFileName(std::string* fileName, const ParamList& parameters, const SessionObject& object)
{
   const Parameter *tname = parameters.Find(L"fileName", 0);
   if( tname == NULL ) return false;

   const Session& session = (const Session&)object.GetSession();
   Token tableName;
   if( !session.Parse(&tableName, tname->value, &object) ) return false;
   if( tableName.type != Token::ttString ) return false;

   USES_CONVERSION;
   const IServerConfig &config = object.GetSession().Config();

   *fileName = config.ExchangeFolder();
   //fileName->append(W2A_CP(tableName.value.str->c_str(), CP_OEMCP));
	fileName->append(W2A_CP(tableName.value.str->c_str(), CP_ACP));

   return true;
}

IDataSource::IReader* KeyValueSC::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   std::string fileName;
   if( !GetFileName(&fileName, parameters, object) )
      return NULL;

   KeyValueReader* kvr = new KeyValueReader();
   if( !kvr->Open(fileName, object) )
   {
      delete kvr;
      kvr = NULL;
   }

   return kvr;
}

//
// -------------------------------------- KeyValueSC --------------------------------------
//
IDataSource::IWriter* KeyValueSC::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   std::string fileName;
   if( !GetFileName(&fileName, parameters, object) )
      return NULL;

   return new KVWriter(fileName);
}

IDataSource::IRemover* KeyValueSC::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   std::string fileName;
   if( !GetFileName(&fileName, parameters, object) )
      return NULL;

   return new KVRemover(fileName);
}

bool KeyValueReader::Open(const std::string& fileName, const SessionObject& object)
{
   keyIndex = object.format->FindMember(L"key");
   valueIndex = object.format->FindMember(L"value");

   if( keyIndex < 0 || valueIndex < 0 ) return false;

   keyFormat = object.format->at(keyIndex);
   valueFormat = object.format->at(valueIndex);

   key.str = new CString();
   value.str = new CString();

   this->fileName = fileName;
   const IObjectData* od = object.GetObjectDef();
   if( od != NULL && (od->flags & IObjectDef::RemoveOnCommit) != 0 )
   {
      this->fileName.append(".TMP");
      if( !IsFileExists(this->fileName) )
         MoveFileA(fileName.c_str(), this->fileName.c_str());
   }

   FILE *rd = fopen(this->fileName.c_str(), "rt");
   if( rd == NULL ) return false;

   do
   {
      std::string key, value;
		if (!ReadKeyValue(rd, &key, &value))
		{
			if(feof(rd))
				break;
			continue;
		}

      values[key] = value;
   } while(true);

   fclose(rd);

   return true;
}

bool KeyValueReader::MoveNext(Object *parentObject)
{
   std::map<std::string, std::string>::iterator i = values.begin();
   if( i == values.end() ) return false;

   USES_CONVERSION;

   const wchar_t *pKey = A2W(i->first.c_str());
   const wchar_t *pValue = A2W(i->second.c_str());

   key.str->assign(pKey);
   value.str->assign(pValue);

   values.erase(i);
   return true;
}

bool KeyValueReader::Get(Object* object) const
{
   object->at(keyIndex).str->assign((const std::wstring&)*key.str);
   object->at(valueIndex).str->assign((const std::wstring&)*value.str);

   return true;
}

bool KVWriter::Prepare(const ISessionObject& iobject)
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   keyIndex = object.format->FindMember(L"key");
   valueIndex = object.format->FindMember(L"value");

   FILE *rd = fopen(this->fileName.c_str(), "rt");
   if( rd != NULL )
   {
      do
      {
         std::string key, value;
         if( !ReadKeyValue(rd, &key, &value) )
            break;

         values[key] = value;
      } while(true);

      fclose(rd);
   }

   return (keyIndex >= 0 && valueIndex >= 0);
}

bool KVWriter::Write(const Object& o, RowID *rid)
{
   USES_CONVERSION;

   const char *key = W2A_CP(o.at(keyIndex).str->c_str(), CP_UTF8);
   const char *val = W2A_CP(o.at(valueIndex).str->c_str(), CP_UTF8);

   values[key] = val;
   return true;
}

void KVWriter::Close()
{
   FILE *file = fopen(this->fileName.c_str(), "wt");
   if( file != NULL )
   {
      std::map<std::string, std::string>::const_iterator i = values.begin();
      for( ; i != values.end(); i++ )
      {
         fprintf(file, "%s=%s\n", i->first.c_str(), i->second.c_str());
      }

      fclose(file);
   }
}

//
// -------------------------------------- KeyValueDBFSC --------------------------------------
//
IDataSource::IReader* KeyValueDBFSC::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   std::wstring file, key, itemName, itemKey;
   const Session& session = (const Session&)object.GetSession();

   const Parameter *p = parameters.Find(L"fileName", 0);
   if( p == NULL || !session.Parse(&file, p->value, &object) )
      return NULL;
   p = parameters.Find(L"key", -1);
   if( p == NULL || !session.Parse(&key, p->value, &object) )
      return NULL;
   p = parameters.Find(L"itemName", -1);
   if( p == NULL || !session.Parse(&itemName, p->value, &object) )
      return NULL;
   p = parameters.Find(L"itemKey", -1);
   if( p != NULL )
      session.Parse(&itemKey, p->value, &object);

#ifdef UNIX
   ConvertPath(file, &file);
#endif
   USES_CONVERSION;

   std::string fileName(object.GetSession().Config().ExchangeFolder());
   fileName += W2A_CP(file.c_str(), CP_UTF8);

	IDataSource::IReader* rdr = new KeyValueDBFReader(key, object);
	if( ((KeyValueDBFReader*)rdr)->ReadValue(fileName, itemName, itemKey) )
		return rdr;
	
	delete rdr;
	return NULL;
}

IDataSource::IWriter* KeyValueDBFSC::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters,
      const ISessionObject& object) const
{
   return NULL;
}

IDataSource::IRemover* KeyValueDBFSC::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
{
   return NULL;
}

KeyValueDBFReader::KeyValueDBFReader(const std::wstring& key, const SessionObject& object)
{
   readed = false;

   keyIndex = object.format->FindMember(L"key");
   valueIndex = object.format->FindMember(L"value");

   if( keyIndex < 0 || valueIndex < 0 )
      return;

   keyFormat = object.format->at(keyIndex);
   valueFormat = object.format->at(valueIndex);

   this->key.str = new CString(key);
   value.str = new CString();

}

bool KeyValueDBFReader::ReadValue(const std::string& file, const std::wstring& itemName, const std::wstring& itemKey)
{
   USES_CONVERSION;

   DataForm base;
	if( !base.Open(file.c_str()) )
		return false;

   const char* in = W2A_CP(itemName.c_str(), DBF_CODE_PAGE);
   const char* ik = W2A_CP(itemKey.c_str(), DBF_CODE_PAGE);

   std::string sbuf;
   std::wstring cw;
   for( int rc = 0; base.ReadRec(rc); rc++ )
   {
      const char *p;
      p = base[in];
      if( p == NULL )
         break;

      cw += A2W_CP(Trunc(p, &sbuf), DBF_CODE_PAGE);

      if( *ik )
      {
         p = base[ik];
         if( p != NULL )
         {
            cw += L"\t";
            cw += A2W_CP(Trunc(p, &sbuf), DBF_CODE_PAGE);
         }
      }

      cw += L";";
   }

   value.str->assign(cw.substr(0, cw.size()-1));
   return true;
}

bool KeyValueDBFReader::MoveNext(Object *parentObject)
{
   if( keyIndex >= 0 && valueIndex >= 0 )
   {
      if( !readed )
      {
         readed = true;
         return true;
      }
   }

   return false;
}

bool KeyValueDBFReader::Get(Object* object) const
{
   if( keyIndex >= 0 && valueIndex >= 0 )
   {
      object->at(keyIndex).str->assign((const std::wstring&)*key.str);
      object->at(valueIndex).str->assign((const std::wstring&)*value.str);
   }

   return true;
}

//
// -------------------------------------- UnionSC --------------------------------------
//
IDataSource::IReader* UnionSC::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   return new UnionReader(parameters, object);
}

UnionReader::UnionReader(const ParamList& parameters, const SessionObject& object) :
   destObject(object), currentObject(NULL), current(-1)
{
   Session& session = (Session&)object.GetSession();

   ParamList::const_iterator i = parameters.begin();
   for( ; i != parameters.end(); i++ )
   {
      std::wstring res;
      if( session.Parse(&res, i->value, &object) )
      {
         SessionObject* so = (SessionObject*)session.LoadObject(res, NULL);
         if( so != NULL )
            srcObjects.push_back(so);
      }
   }
}

bool UnionReader::MoveNext(Object *parentObject)
{
   if( (currentObject == NULL || current >= (int)currentObject->size() - 1))
   {
      if( !OpenNext() )
         return false;

      CreateWriter(*currentObject);
   }

   current++;
   return true;
}

bool UnionReader::Get(Object* dest) const
{
   Object* src = currentObject->at(current);

   std::vector<MemberWriter>::const_iterator i = writer.begin();
   for( ; i != writer.end(); i++ )
   {
      i->Write(dest, *src);
   }

   return true;
}

void UnionReader::Close()
{
   writer.clear();
   srcObjects.clear();
}

void UnionReader::MemberWriter::Write(Object *destO, const Object& srcO) const
{
   Member &dest = destO->at(destIndex);
   const Member &src = srcO.at(srcIndex);

   switch(format.type)
   {
   case MemberFormat::mtString:
      dest.str->assign(*src.str);
      break;
   case MemberFormat::mtNumber:
      dest.number = src.number;
      break;
   case MemberFormat::mtDateTime:
      dest.datetime = src.datetime;
      break;
   default: break;
   }
}

bool UnionReader::OpenNext()
{
   if( srcObjects.size() == 0 )
      return false;

   current = -1;
   if( currentObject == NULL )
   {
      currentObject = srcObjects.front();
      return true;
   }

   srcObjects.erase(srcObjects.begin());
   while( srcObjects.size() > 0 )
   {
      currentObject = srcObjects.front();
      if( currentObject->size() > 0 )
         break;

      srcObjects.erase(srcObjects.begin());
   }

   return (srcObjects.size() > 0);
}

void UnionReader::CreateWriter(const SessionObject& src)
{
   writer.clear();

   GRServer::Format *srcFmt = src.format;
   GRServer::Format *f = destObject.format;
   GRServer::Format::const_iterator i = f->begin();
   int destIndex = 0;
   for( ; i != f->end(); i++, destIndex++ )
   {
      MemberFormat destMF = (*i);
      if( destMF.type != MemberFormat::mtString && destMF.type != MemberFormat::mtNumber && destMF.type != MemberFormat::mtDateTime )
         continue;

      int srcIndex = srcFmt->FindMember(i->name.c_str());
      if( srcIndex >= 0 )
      {
         MemberFormat srcMF = srcFmt->at(srcIndex);
         if( srcMF.type == destMF.type )
         {
            MemberWriter mw;
            mw.srcIndex = srcIndex;
            mw.destIndex = destIndex;
            mw.format = destMF;
            writer.push_back(mw);
         }
      }
   }
}

/*
 * Copyright (C), 2009, ����� �������
 *
 * MessageTable
 *
 * ert   03/10/2009   creating
 */
#include "stdafx.h"
#include "server.h"
#include "sources.h"
#include "session.h"
#include "srvutility.h"
#include "token.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

class MessageReader : public IDataSource::IReader
{
public:
   MessageReader() : readed(false) {}

   bool Open(const ParamList& parameters, const SessionObject& object);

   virtual bool MoveNext(Object *parentObject)
   {
      if( !readed )
      {
         readed = true;
         return true;
      }
      return false;
   }
   virtual bool Get(Object* object) const;

   virtual void Close() {}

   virtual void Remove() { _unlink(fileName.c_str()); }

   virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
   virtual const Member* Value(const wchar_t* name) const { return NULL; }

protected:
   bool readed;
   FILETIME curDate;
   std::string message, fileName;
   int dateMember, messageMember;
};

class MessageRemover : public IDataSource::IRemover
{
public:
   MessageRemover(const std::string& fileName);

   virtual bool Remove(const wchar_t* filter);
   virtual void Close() {}

protected:
   std::string fileName;
};

class MessageWriter : public IDataSource::IWriter
{
public:
   MessageWriter(const std::string& fileName);

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close();

protected:
   FILE *file;
   std::string fileName;
   int msgIndex;
};

static bool GetFileName(std::string* fileName, const GRServer::ParamList &parameters, const GRServer::SessionObject &object);

IDataSource::IReader* MessageCreator::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   MessageReader* reader = new MessageReader();
   if( reader->Open(parameters, object) == false )
   {
      delete reader;
      reader = NULL;
   }

   return reader;
}

IDataSource::IWriter* MessageCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   std::string fn;
   if( GetFileName(&fn, parameters, object) )
      return new MessageWriter(fn);
   else
   {
      //gServer->AddError(false,
      return NULL;
   }
}

IDataSource::IRemover* MessageCreator::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   std::string fn;
   if( GetFileName(&fn, parameters, object) )
      return new MessageRemover(fn);
   else
   {
      //gServer->AddError(false,
      return NULL;
   }
}

static bool GetFileName(std::string* fileName, const GRServer::ParamList &parameters, const GRServer::SessionObject &object)
{
   const Parameter *fname = parameters.Find(L"fileName", 0);
   if( fname == NULL )
      return false;

   Token fName;
   if( !((Session&)object.GetSession()).Parse(&fName, fname->value, &object) || fName.type != Token::ttString )
      return false;

   USES_CONVERSION;
   const IServerConfig &config = object.GetSession().Config();

   fileName->assign(config.ExchangeFolder());
   //fileName->append(W2A_CP(fName.value.str->c_str(), CP_OEMCP));
	fileName->append(W2A_CP(fName.value.str->c_str(), CP_ACP));
#ifdef UNIX
   ConvertPath(*fileName, fileName);
#endif
   return true;
}

//
//--------------------------------------- MessageWriter ----------------------------------------
//
MessageWriter::MessageWriter(const std::string& fileName) : file(NULL)
{
   this->fileName = fileName;
}

bool MessageWriter::Prepare(const ISessionObject& iobject)
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   msgIndex = object.format->FindMember(L"message");
   return (msgIndex >= 0);
}

bool MessageWriter::Write(const Object& o, RowID *rid)
{
   bool res = false;

   if( msgIndex >= 0 )
   {
      USES_CONVERSION;
      if( file == NULL )
         file = fopen(fileName.c_str(), "at");
      if( file != NULL )
      {
         const Member& m = o.at(msgIndex);

         USES_CONVERSION;

         const char* str = W2A_CP(m.str->c_str(), CP_UTF8);
         fputs(str, file);
         fputs("\n", file);
         res = true;
      }
   }

   return res;
}

void MessageWriter::Close()
{
   if( file != NULL )
   {
      fclose(file);
      file = NULL;
   }
}

//
//--------------------------------------- MessageRemover ----------------------------------------
//
MessageRemover::MessageRemover(const std::string& fileName)
{
   this->fileName = fileName;
}

bool MessageRemover::Remove(const wchar_t* filter)
{
   return (_unlink(fileName.c_str()) == 0);
}

//
//--------------------------------------- MessageReader ----------------------------------------
//
bool MessageReader::Open(const GRServer::ParamList &parameters, const GRServer::SessionObject &object)
{
   const GRServer::Format& objectFormat = *object.format;

   dateMember = objectFormat.FindMember(L"date");
   messageMember = objectFormat.FindMember(L"message");
   if( dateMember < 0 || messageMember < 0 ) return false;

   if( objectFormat.at(dateMember).type != MemberFormat::mtDateTime ||
      objectFormat.at(messageMember).type != MemberFormat::mtString ) return false;

   if( !GetFileName(&fileName, parameters, object) )
      return false;

   const IObjectData* od = object.GetObjectDef();
   if( od != NULL && (od->flags & IObjectDef::RemoveOnCommit) != 0 )
   {
      std::string tName(fileName);

      fileName.append(".TMP");
      if( !IsFileExists(fileName) )
         MoveFileA(tName.c_str(), fileName.c_str());
   }

   FILE *file = fopen(fileName.c_str(), "rt");
   if( file == NULL ) return false;

   size_t len = _filelength(_fileno(file));
   char *buf = (char*)alloca(len);
   len = fread(buf, sizeof(char), len, file);
   fclose(file);

   SYSTEMTIME st;
   GetLocalTime(&st);
   SystemTimeToFileTime(&st, &curDate);

   message.assign(buf, len);

   return true;
}

bool MessageReader::Get(Object* object) const
{
   Member& m = object->at(dateMember);
   m.datetime = curDate;

   Member& m1 = object->at(messageMember);

   USES_CONVERSION;
   m1.str->assign(A2W_CP(message.c_str(), CP_UTF8));
   return true;
}

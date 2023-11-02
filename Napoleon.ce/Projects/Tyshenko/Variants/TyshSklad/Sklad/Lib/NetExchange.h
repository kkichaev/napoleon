/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Обмен данными с GRServer
 * 
 *  ert   23/09/2009   creating
 */ 
#ifndef __NET_EXCHANGE_H
#define __NET_EXCHANGE_H

#include "TypeHolder.h"
#include "Binary.h"
#include "Network.h"
#include "SQLTable.h"
#include <ServerDefs.h>

struct ServerCommand : public IReflectableData
{
   ServerCommand();

   wchar_t *command;
   wchar_t *param;
   wchar_t *userid;
   wchar_t *password;
   wchar_t *version;
   wchar_t *category;

   DWORD   duration;

#ifdef CHECK_LOGIN_PROGID
   wchar_t *progid;
#endif

   DECLARE_TYPE_REFLECTION(ServerCommand)
};

struct ServerAnswer : public IReflectableData
{
   WORD response;
   wchar_t *message;

   DECLARE_TYPE_REFLECTION(ServerAnswer)
};

struct IPAddress
{
   std::wstring ip;
   WORD port;
};

//
// true если соединение приняло "GRPACKET" - дальше принимаем "(SIZE);...DATA;" уже отдельно
//

class ReceivedStream
{
public:
   ReceivedStream();
   ~ReceivedStream();

   void Add(const Binary& b) { Add(b, b.Size()); }
   void Add(const BYTE *b, DWORD cb);

   void SetSize(DWORD size);

   void Clear();

   DWORD Size() const { return totSize; }
   DWORD CurPos() const { return cp; }
   DWORD CRC32();

   bool DecompressTo(ReceivedStream* stream);

   // --------------- read funcs -----------------------
   void PrepareRead();

   DWORD CopyTo(BYTE* buffer, DWORD cb);
   bool CopyUntill(std::wstring* value, wchar_t sym);

   bool EOS() const { return ((isFile) ? (feof(buffer.file) != 0) : (cp == totSize)); }

   wchar_t Get();
   void Unget(wchar_t sym);

   void SkipObject();

protected:
   bool isFile;

   std::wstring fileName;
   BYTE* pb;
   DWORD totSize, cp;

   union
   {
      FILE* file;
      Binary* memory;
   } buffer;
};

struct ConnectParam;
class NetworkExchange : public Network
{
public:
   NetworkExchange() {}

   bool Send(OutStream& stream, IProgressIndicator *pf);
   bool Send(const Packet& packet, IProgressIndicator *pf)
   {
      if( !Network::Send((const BYTE*)packet.head.c_str(), packet.head.size() * sizeof(wchar_t), pf) ) return false;
      if( !Network::Send(*packet.data, packet.data->Size(), pf) ) return false;

      return true;
   }

   // возвращает уже обработанный Packet
   ReceivedStream* Receive(const IPAddress* addr1, const IPAddress* addr2, const ServerCommand& command,
      IProgressIndicator *pf = NULL, bool establishConnect = true);
   ReceivedStream* Receive(const IPAddress* addr1, const IPAddress* addr2, OutStream& stream, IProgressIndicator *pf = NULL);

   ReceivedStream* ReceiveStream(IProgressIndicator *pf = NULL);

   bool Receive(Binary *b);

protected:
   class NetStream
   {
   public:
      NetStream(NetworkExchange& net);

      bool CopyUntill(std::wstring *value, wchar_t sym);
      bool Receive(ReceivedStream* stream, DWORD size, IProgressIndicator *pf);

   protected:
      bool ReadNextBuffer();

      NetworkExchange& network;
      Binary buffer;
      const wchar_t *sp, *ep;
   };

protected:
   bool Connecting(const IPAddress* addr1, const IPAddress* addr2, const ServerCommand& cmd, WORD timeOut, bool establishConnect);
   bool Connecting(const IPAddress* addr1, const IPAddress* addr2, OutStream& stream, WORD timeOut, bool establishConnect);

   bool SetConnectResult(ConnectParam &p, ConnectParam &p2, HANDLE hThread);

   bool ReadPacket(NetStream &stream, std::vector<PacketOperator> *operations, ReceivedStream* rcvd, IProgressIndicator *pf);
   
   static DWORD TryConnect(ConnectParam *param);
};

struct IReceiveObject
{
   virtual ~IReceiveObject() {}

   virtual const wchar_t* Name() const = 0;
   virtual const wchar_t* ProgressText() const = 0;

   virtual const wchar_t* Command() const = 0;
   virtual const wchar_t* Params() const = 0;

   virtual bool Read(ReceivedStream* stream) = 0;
   virtual void Close() = 0;
};

class ReceiveObjects : public std::vector<IReceiveObject*>
{
public:
   IReceiveObject* FindObject(const std::wstring& name)
   {
      iterator i = begin();
      for( ; i != end(); i++ )
         if( name.compare((*i)->Name()) == 0 ) return (*i);

      return NULL;
   }
};

class DataReader;
/*
используем Name() - имя объекта
data.Name() - имя таблицы
*/
template <class Impl> class DBObjectRcvr : public IReceiveObject
{
public:
   DBObjectRcvr(UINT idProgress, bool clearTable, const wchar_t* excluded = L"") : 
      clearBefore(clearTable), reader(NULL), table(NULL), excludedFields(excluded)
   {
      _Module.LoadString(&progressText, idProgress);
   }

   ~DBObjectRcvr()
   {
      delete reader;

      if( table != NULL )
      {
         table->EndTransaction();
         delete table;
      }
   }

   virtual const wchar_t* Name() const { return data.Type().Name(); }
   virtual const wchar_t* ProgressText() const { return progressText.c_str(); }
   virtual const wchar_t* Command() const { return GET_COMMAND; }
   virtual const wchar_t* Params() const { return Name(); }

   virtual bool Prepare(ReceivedStream* stream)
   {
      if( reader == NULL )
         reader = DataReader::CreateReader(data.GetType(), stream);
      if( reader == NULL ) return false;

      if( clearBefore ) SQLTable::DropTable(data.Name());
      if( !SQLCheckTable(data) ) return false;

      table = new SQLTable(data.Name());
      table->StartTransaction(500);
      return true;
   }

   virtual bool Write(const Impl& data)
   {
      return (table->Write(data, excludedFields) != NO_ROWID);
   }

   virtual bool Read(ReceivedStream* stream)
   {
      if( reader == NULL && !Prepare(stream) )
         return false;
      if( reader->Read(&data, stream) == false )
         return false;

      if( Write(data) == false )
         return false;

      return true;
   }

   virtual void Close()
   {
      table->EndTransaction();
      delete table;
      table = NULL;

      delete reader;
      reader = NULL;
   }

protected:
   std::wstring progressText;
   const wchar_t *excludedFields;
   bool clearBefore;

   DataReader* reader;

   SQLTable* table;

   Impl data;
};

template <class Impl> class DBObjAliasRcvr : public DBObjectRcvr<Impl>
{
public:
   DBObjAliasRcvr(UINT idText, bool clearTable, const wchar_t* alias) : 
      DBObjectRcvr<Impl>(idText, clearTable)
      {
         this->alias = alias;
      }

   virtual const wchar_t* Name() const { return alias.c_str(); }
   virtual const wchar_t* Params() const { return alias.c_str(); }

protected:
   std::wstring alias;
};

template <class Impl> class ArrayRcvr : public IReceiveObject
{
public:
   ArrayRcvr(UINT idProgress) : reader(NULL)
   {
      _Module.LoadString(&progressText, idProgress);
   }

   ~ArrayRcvr()
   {
      delete reader;

		std::vector<Impl*>::iterator i = list.begin();
		for( ; i != list.end(); i++)
			delete (*i);
   }

   virtual const wchar_t* Name() const { return Impl().GetType().Name(); }
   virtual const wchar_t* ProgressText() const { return progressText.c_str(); }
   virtual const wchar_t* Command() const { return GET_COMMAND; }
   virtual const wchar_t* Params() const { return Name(); }

   virtual bool Prepare(ReceivedStream* stream)
   {
      if( reader == NULL )
         reader = DataReader::CreateReader(Impl().GetType(), stream);
      return reader != NULL;
   }

   virtual bool Read(ReceivedStream* stream)
   {
      if( reader == NULL && !Prepare(stream) )
         return false;
      Impl *data = new Impl();
		if( reader->Read(data, stream) == false )
		{
			delete data;
			return false;
		}

		data->UnbindStrings();
		list.push_back(data);
      return true;
   }

   virtual void Close()
   {
      delete reader;
      reader = NULL;
   }

public:
	std::vector<Impl*> list;

protected:
   std::wstring progressText;
   DataReader* reader;
}; 


struct ReceivePacketParam
{
   ReceivePacketParam(IProgressIndicator* _pi) : pi(_pi), ec(0), receivePhoto(false), clearBase(false) {}

   IProgressIndicator *pi;
   DWORD ec;
   std::wstring answer;

   bool receivePhoto;
   bool clearBase;

   ReceiveObjects objects;
};

struct ReportRequestParam
{
	ReportRequestParam() : ec(0), pi(NULL) {}

   DWORD ec;
   IProgressIndicator *pi;

	std::wstring reportName;
	std::wstring answer;
	IReflectableData* param;
   ReceiveObjects objects;
};

class DocType;
struct SendObjectsData
{
   std::vector<ROWID> documents;
   const DocType* type;

   std::wstring name;
   bool sended;
};

struct IDocument;
struct SendPacketParam
{
   IProgressIndicator *pi;
   DWORD ec;
   std::wstring answer;

   StringHolder holder;
   IPAddress addr1, addr2;

   OutStream stream;

   std::vector<SendObjectsData> data;

   void AddDocument(IDocument* doc, const DocType* docType);
   SendObjectsData* Find(const wchar_t* name);
};

void MakeServerCommand(ServerCommand* scmd, StringHolder* holder,  IPAddress* addr1, IPAddress* addr2, 
                              const wchar_t* command, const wchar_t* params); // in ModuleNet.cpp

bool CheckAnswer(ReceivedStream* stream, std::wstring *answer);

bool ProcessStream(ReceivedStream* stream, ReceiveObjects &objects, IProgressIndicator *pi, bool *bContinue);

#endif
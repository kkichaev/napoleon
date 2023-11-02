/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Модуль приложения + globals
 * 
 *  ert   08/08/2007   creating
 *  ert   13/08/2007   modifing
 */ 
#include "stdafx.h"

#include "ObjImpl.h"

#include <Module.h>
#include <Compress.h>

#include <StringHolder.h>
#include <DocImpl.h>
#include <Network.h>
#include "Progress.h"
#include "NplConfig.h"
#include <DocType.h>
#include "PrfDlg.h"
#include <NetExchange.h>
#include <DataReader.h>
#include <ServerDefs.h>
#include <StdFuncs.h>
#include <BalanceRcv.h>
#include <algorithm>
#include "OrdPcd.h"

#ifdef VISIT_DOC
#include <Visit.h>   
#endif

#ifdef Autopteka
#include <Add.h>
#endif

#ifdef ORG_TASK
#include <Task.h>
#endif

#ifdef BastionNeva
#include <Add.h>
#endif

#include <UpdateConfig.h>

static void MakeParamStr(std::wstring* res, const wchar_t* typeName, const wchar_t* fieldName)
{
   SYSTEMTIME st;
   wchar_t sdate[30], edate[30];

   GetLocalTime(&st);
   wsprintf(edate, L"%02d.%02d.%d 23:59:59", st.wDay, st.wMonth, st.wYear);

   if( st.wMonth != 1 ) st.wMonth--;
   else
   {
      st.wYear--;
      st.wMonth = 12;
   }
   wsprintf(sdate, L"01.%02d.%d 00:00:00", st.wMonth, st.wYear);

   res->assign(typeName);
   res->append(L":userid = '$CURRENT_USERID' and ");
   res->append(fieldName);
   res->append(L" >= ToDate('");
   res->append(sdate);
   res->append(L"') and ");
   res->append(fieldName);
   res->append(L" <= ToDate('");
   res->append(edate);
   res->append(L"')");
}

class OrderRcvd : public DBObjectRcvr<OrderImpl>
{
public:
   typedef DBObjectRcvr<OrderImpl> Base;
   OrderRcvd() : Base(L"Обработка заявок...", false)
   {
   }

   virtual const wchar_t* Command() const { return SELECT_COMMAND; }

   virtual bool Write(const OrderImpl& data)
   {
      ((OrderImpl&)data).ClearDirty(NULL, false);
      return Base::Write(data);
   }

   virtual const wchar_t* Params() const
   {
      MakeParamStr(&params, data.Type().Name(), L"created");
      return params.c_str();
   }

   mutable std::wstring params;
};

#ifdef SHOW_OFF_TAKE
#include "OrgRmnts.h"
class OrgRmntsRcvd : public DBObjectRcvr<OrgRemnantsImpl>
{
public:
   typedef DBObjectRcvr<OrgRemnantsImpl> Base;
   OrgRmntsRcvd() : Base(L"Обработка остатков...", false)
   {
   }

   virtual const wchar_t* Command() const { return SELECT_COMMAND; }

   virtual bool Write(const OrgRemnantsImpl& data)
   {
      ((OrgRemnantsImpl&)data).ClearDirty(NULL, false);
      return Base::Write(data);
   }

   virtual const wchar_t* Params() const
   {
      MakeParamStr(&params, data.Type().Name(), L"date");
      return params.c_str();
   }

   mutable std::wstring params;
};
#endif

class RemnantsImpl : public DBImpl<PriceRemnants>
{
public:
   RemnantsImpl() :  DBImpl(L"price") {}
   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};

class RemnantsRcvr : public DBObjectRcvr<RemnantsImpl>
{
public:
   typedef DBObjectRcvr<RemnantsImpl> Base;

   RemnantsRcvr() : DBObjectRcvr<RemnantsImpl>(L"Обработка остатков...", false)
   {
   }

   virtual bool Prepare(ReceivedStream* stream)
   {
      if( !Base::Prepare(stream) ) return false;

      ClearPriceQty(data.Name());

      const DataReflector& type = data.GetType();
      MemberType *idT = (MemberType*)&type.Type(L"id");
      MemberType *qtyT = (MemberType*)&type.Type(L"qty");

      std::wstring sql(L"UPDATE ");
      sql += data.Name();
      sql += L" SET qty=? WHERE id=?";

      std::vector<MemberType*> params;
      params.push_back(qtyT);
      params.push_back(idT);

      bool ret = table->PrepareCommand(sql, params);
      if( ret )
         table->StartTransaction(500);

      return ret;
   }

   virtual bool Write(const RemnantsImpl& data)
   {
      return table->ExecCommand(data);
   }
};

struct PhotoRcvDataItem : public IReflectableData
{
   wchar_t *id;
   DECLARE_TYPE_REFLECTION(PhotoRcvDataItem)
};

struct PhotoRcvData : public IReflectableData
{
   vector_t<PhotoRcvDataItem> items;
   DECLARE_TYPE_REFLECTION(PhotoRcvData)
};

class PhotoWriter : public BinaryFileWriter
{
public:
   PhotoWriter();
   virtual ~PhotoWriter();

   virtual void GetFileName(std::wstring* fileName);
   virtual void AfterWrite(IReflectableData* data, const std::wstring& fileName);

   DWORD index;
   std::wstring fileBase;
   PricePhotoImpl pi;
   SQLTable *writeTable;
};

class PhotoRcvr : public IReceiveObject
{
public:
   PhotoRcvr() : reader(NULL) {}

   virtual const wchar_t* Name() const { return L"PricePhoto"; }
   virtual const wchar_t* ProgressText() const
   {
      static wchar_t buf[50];
      wsprintf(buf, L"Обработка фото %d...", index);
      return buf; 
   }

   virtual const wchar_t* Command() const { return GET_COMMAND; }
   virtual const wchar_t* Params() const { return Name(); }

   virtual bool Read(ReceivedStream* stream)
   {
      if( reader == NULL )
      {
         reader = DataReader::CreateReader(data.GetType(), stream, FileWriter);
      }

      if( !reader->Read(&data, stream) )
         return false;

      if( fileWriter != NULL ) index = fileWriter->index;
      return true;
   }

   virtual void Close()
   {
      delete reader;
      reader = NULL;

      delete fileWriter;
      fileWriter = NULL;
   }

   static IBinaryWriter* FileWriter(const wchar_t* fieldName)
   {
      if( wcscmp(fieldName, L"photo") != 0 ) return NULL;

      ATLASSERT(fileWriter == NULL);

      fileWriter = new PhotoWriter();
      return fileWriter;
   }

   static void ClearIndex() { index = 0; }
   static void SetIndex(DWORD newIndex) { index = newIndex; }
   static DWORD GetIndex() { return index; }

protected:
   PhotoRcvData data;
   DataReader* reader;

   static PhotoWriter* fileWriter;
   static DWORD index;
};

class SendAnswerRcvr : public IReceiveObject
{
public:
   SendAnswerRcvr(SendPacketParam* _param): reader(NULL), param(_param) {}
   ~SendAnswerRcvr() { Close(); }

   virtual const wchar_t* Name() const { return L"ServerAnswer"; }
   virtual const wchar_t* ProgressText() const { return NULL; }
   virtual const wchar_t* Command() const { return GET_COMMAND; }
   virtual const wchar_t* Params() const { return Name(); }

   virtual bool Read(ReceivedStream* stream)
   {
      ServerAnswer sa;
      if( reader == NULL )
         reader = DataReader::CreateReader(sa.GetType(), stream);

      if( reader == NULL || !reader->Read(&sa, stream) ) return false;

      SendObjectsData* data = param->Find(sa.message);
      if( data != NULL )
      {
         if( sa.response )
         {
            if( data->type )
               data->type->ClearDirty(data->documents);

            data->sended = true;
         }
         else
            data->sended = false;
      }
      return true;
   }

   virtual void Close()
   {
      if( reader != NULL )
      {
         delete reader;
         reader = NULL;
      }
   }

protected:
   DataReader* reader;
   SendPacketParam* param;
};

#ifdef RCV_MESSAGE
class MessageRcvr : public DBObjectRcvr<MessageImpl>
{
public:
   typedef DBObjectRcvr<MessageImpl> Base;

   MessageRcvr() : Base(L"Прием сообщений...", false) {}

   virtual bool Write(const MessageImpl& data)
   {
      if( !Base::Write(data) ) return false;
      _Module.SetMessageDate(data.date);
      return true;
   }
};
#endif

BEGIN_TYPE_REFLECTION(PhotoRcvDataItem)
   REGISTER_STRING_MEMBER(PhotoRcvDataItem, id)
END_TYPE_REFLECTION(PhotoRcvDataItem)

BEGIN_TYPE_REFLECTION(PhotoRcvData)
   REGISTER_COLLECTION_MEMBER(PhotoRcvData, items, PhotoRcvDataItem)
END_TYPE_REFLECTION(PhotoRcvData)

PhotoWriter* PhotoRcvr::fileWriter;
DWORD PhotoRcvr::index = 0;

bool ClearPriceQty(const wchar_t* tableName)
{
   std::wstring sql(L"UPDATE ");
   sql += tableName;
   sql += L" SET qty = 0";
   return SQLTable::Execute(sql.c_str());
}

PhotoWriter::PhotoWriter()
{
   Preference pref;
   pref.Load();
   wchar_t buf[MAX_PATH];

   index = PhotoRcvr::GetIndex();

   if( pref.photoInMainMemory )
   {
      SHGetSpecialFolderPath(NULL, buf, CSIDL_PERSONAL, TRUE);
      fileBase = buf;
   } else
   {
      fileBase = L'\\';
      mbstowcs(buf, pref.photoFolder, MAX_PATH);
      fileBase += buf;
   }

   fileBase += PHOTO_FOLDER;
   CreateDirectory(fileBase.c_str(), NULL);
   fileBase += L'\\';

   writeTable = NULL;
}

PhotoWriter::~PhotoWriter()
{
   PhotoRcvr::SetIndex(index);
   delete writeTable;
}

void PhotoWriter::GetFileName(std::wstring* fileName)
{
   wchar_t imgBuf[30];
   wsprintf(imgBuf, L"IMG%04d.JPG", index++);

   fileName->assign(fileBase);
   fileName->append(imgBuf);
}

void PhotoWriter::AfterWrite(IReflectableData* data, const std::wstring& fileName)
{
   vector_t<PhotoRcvDataItem> &items = ((PhotoRcvData*)data)->items;

   if( writeTable == NULL )
   {
      writeTable = new SQLTable(pi.Name());
   }

   vector_t<PhotoRcvDataItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      pi.id = i->id;
      //pi.Read();
      pi.photo = (wchar_t*)fileName.c_str();
      writeTable->Write(pi);
   }
}

const wchar_t* ConfigImpl::LoadIP(const wchar_t* ip, const Preference& p)
{
   key = (wchar_t*)ip;
   if( !Read() )
   {
      wchar_t buf[sizeof(p.ip) + 1];
      mbstowcs(buf, (ip == ConfigImpl::IP1) ? p.ip : p.ip2, sizeof(p.ip));
      buf[sizeof(p.ip)] = L'\0';
      value = holder.Add(buf);
      Write();
   }
   return value;
}

#ifdef CHECK_LOGIN_PROGID

static std::wstring phoneID;
static bool inited = false;
static HANDLE hRILEvent = NULL;

typedef HANDLE HRIL, *LPHRIL;
#define MAXLENGTH_EQUIPINFO 128

typedef void (CALLBACK *RILRESULTCALLBACK)(
    DWORD dwCode,           // @parm result code
    HRESULT hrCmdID,        // @parm ID returned by the command that originated this response
    const void* lpData,     // @parm data associated with the notification
    DWORD cbData,           // @parm size of the strcuture pointed to lpData
    DWORD dwParam           // @parm parameter passed to <f RIL_Initialize>
);

typedef void (CALLBACK *RILNOTIFYCALLBACK)(
    DWORD dwCode,           // @parm notification code
    const void* lpData,     // @parm data associated with the notification
    DWORD cbData,           // @parm size of the strcuture pointed to lpData
    DWORD dwParam           // @parm parameter passed to <f RIL_Initialize>
);
 
typedef HRESULT (*RIL_InitializeT)(
    DWORD dwIndex,                      // @parm index of the RIL port to use (e.g., 1 for RIL1:)
    RILRESULTCALLBACK pfnResult,        // @parm function result callback
    RILNOTIFYCALLBACK pfnNotify,        // @parm notification callback
    DWORD dwNotificationClasses,        // @parm classes of notifications to be enabled for this client
    DWORD dwParam,                      // @parm custom parameter passed to result and notififcation callbacks
    HRIL* lphRil                        // @parm returned handle to RIL instance
);

typedef HRESULT (*RIL_DeinitializeT)(
    HRIL hRil                           // @parm handle to an RIL instance returned by <f RIL_Initialize>
);

typedef HRESULT (*RIL_GetEquipmentInfoT)(
  HRIL hRil
);

typedef struct {
  DWORD cbSize;
  DWORD dwParams;
  char szManufacturer[MAXLENGTH_EQUIPINFO];
  char szModel[MAXLENGTH_EQUIPINFO];
  char szRevision[MAXLENGTH_EQUIPINFO];
  char szSerialNumber[MAXLENGTH_EQUIPINFO];
} RILEQUIPMENTINFO;

static void CALLBACK GetEquipmentInfo(DWORD dwCode, HRESULT hrCmdID, const void* lpData, DWORD cbData, DWORD dwParam)
{
   int len = strlen(((RILEQUIPMENTINFO*)lpData)->szSerialNumber) + 1;
   wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
   mbstowcs(buf, ((RILEQUIPMENTINFO*)lpData)->szSerialNumber, len);
   phoneID = buf;

   SetEvent(hRILEvent);
}

static const wchar_t* GetPhoneID()
{
   if( !inited )
   {
      inited = true;

      HINSTANCE hLib = hLib = LoadLibrary(L"ril.dll");
      if( hLib != NULL )
      {
         RIL_InitializeT init = (RIL_InitializeT)GetProcAddress((HMODULE)hLib, L"RIL_Initialize");
         RIL_DeinitializeT deinit = (RIL_DeinitializeT)GetProcAddress((HMODULE)hLib, L"RIL_Deinitialize");
         RIL_GetEquipmentInfoT getInfo = (RIL_GetEquipmentInfoT)GetProcAddress((HMODULE)hLib, L"RIL_GetEquipmentInfo");

         if( init != NULL && deinit != NULL && getInfo != NULL )
         {
            hRILEvent = CreateEvent(NULL, TRUE, FALSE, NULL);

            HRIL hRil = 0;
            HRESULT hres = init(1, GetEquipmentInfo, NULL, 0, 0, &hRil);
            if( hres == S_OK )
            {
               getInfo(hRil);
               WaitForSingleObject(hRILEvent, 1000);

               deinit(hRil);
               CloseHandle(hRILEvent);
            }
         }

         FreeLibrary(hLib);
      }
   }

   return phoneID.c_str();
}
#endif

void MakeServerCommand(ServerCommand* scmd, StringHolder* holder,  IPAddress* addr1, IPAddress* addr2, 
                              const wchar_t* command, const wchar_t* params)
{
   Preference pref;
   pref.Load();

   scmd->command = (wchar_t*)command;
   scmd->param = (wchar_t*)params;
   scmd->userid = holder->Add(pref.login);
   scmd->password = holder->Add(pref.password);

   scmd->duration = pref.worked;
   scmd->category = holder->Add(UPDATE_CATEGORY);

#ifdef CHECK_LOGIN_PROGID
   scmd->progid = (wchar_t*)GetPhoneID();
#endif

   std::wstring ver;
   if( GetVersionStr(&ver, _Module.GetModuleInstance()) )
   {
      scmd->version = holder->Add(ver.c_str());
   } else
      scmd->version = L"";

   ConfigImpl cfg;
   if( addr1 != NULL )
   {
      addr1->port = pref.port;
      addr1->ip = cfg.LoadIP(ConfigImpl::IP1, pref);
   }

   if( addr2 != NULL )
   {
      addr2->port = pref.port;
      addr2->ip = cfg.LoadIP(ConfigImpl::IP2, pref);
   }
}

static bool SendCommand(NetworkExchange& net, const wchar_t* pcmd)
{
   OutStream cmdStream;
   StringHolder holder;
   ServerCommand cmd;

   MakeServerCommand(&cmd, &holder, NULL, NULL, pcmd, L"");

   const DataReflector& type = cmd.GetType();
   type.ToStream(&cmdStream);
   type.DataToStream(&cmdStream, cmd);

   return net.Send(cmdStream, NULL);
}

static void CheckUpdate()
{
   Preference p;
   p.Load();
   int uf = (((p.flags & ufMask) >> ufShift) & 0x3);
   if( uf == ufNone )
      return;

   std::wstring fileName, cmdLine;
   _Module.MakeFileName(&fileName, UPDATE_PROGRAM);
   if(!IsFileExist(fileName) )
       return;

   int ppos = fileName.find_last_of(L'\\');
   UpdateConfig config;

   GetVersionStr(&config.version, _Module.GetModuleInstance());
   config.action = (uf==ufCheck) ? CHECK_UPDATE_ACTION : DO_UPDATE_ACTION;
   config.category = UPDATE_CATEGORY;
   config.rootFolder = fileName.substr(0, ppos);
   strncpy(config.login, p.login, sizeof(config.login));
   strncpy(config.password, p.password, sizeof(config.password));

   ConfigImpl cfg;
   UpdateConfig::IPData data;
   const wchar_t* ip = cfg.LoadIP(ConfigImpl::IP1, p);
   if( *ip )
   {
      data.ip = ip;
      data.port = p.port;
      config.address.push_back(data);
   }

   ip = cfg.LoadIP(ConfigImpl::IP2, p);
   if( *ip )
   {
      data.ip = ip;
      data.port = p.port;
      config.address.push_back(data);
   }

   cmdLine = fileName.substr(0, ppos+1);
   cmdLine += L"NplPdaUpdate.config";

   if( config.Save(cmdLine.c_str()) )
   {
      PROCESS_INFORMATION pi;

      cmdLine.insert(0, L"\"");
      cmdLine.append(L"\"");

      CreateProcess(fileName.c_str(), cmdLine.c_str(), NULL, NULL, FALSE, 0, NULL, NULL, NULL, &pi);
      CloseHandle(pi.hThread);
      CloseHandle(pi.hProcess);
   }
}

static bool DoProcessStream(ReceivedStream* stream, ReceiveObjects &objects, IProgressIndicator *pi, bool *bContinue)
{
#ifdef RCV_MESSAGE
   MessageRcvr mrcvr;
   objects.push_back(&mrcvr);
#endif

#ifdef Zakroma
   RemnantsRcvr rrcvr;
   objects.push_back(&rrcvr);
#endif

   OrdPcdRcvr opr;
   objects.push_back(&opr);

   return ProcessStream(stream, objects, pi, bContinue);
}

DWORD DoReceive(ReceivePacketParam *param)
{
   StringHolder holder;
   IPAddress addr1, addr2;
   ServerCommand cmd;
   NetworkExchange net;

   std::wstring sparam;

   OutStream os;
   MakeServerCommand(&cmd, &holder, &addr1, &addr2, L"", L"");
   const DataReflector& type = cmd.GetType();
   type.ToStream(&os);

   ReceiveObjects::const_iterator i = param->objects.begin();
   for( ; i != param->objects.end(); i++ )
   {
      const IReceiveObject* obj = (*i);
      cmd.param = (wchar_t*)obj->Params();
      cmd.command = (wchar_t*)obj->Command();

      type.DataToStream(&os, cmd);
   }

   CheckUpdate();

   net.SetTimeout(NETWORK_TIMEOUT * 10);
   ReceivedStream* stream = net.Receive(&addr1, &addr2, os, param->pi);
   if( stream )
   {
      stream->PrepareRead();
      if( param->pi )
      {
         param->pi->SetText(L"Обработка...");
         param->pi->SetMax(stream->Size());
      }

      if( CheckAnswer(stream, &param->answer) )
      {
         bool ret = false;
         if( !param->receivePhoto )
         {
#ifdef NAPOLEON_APPS
            _Module.DoCheckTime();
#endif
            SendCommand(net, BYE_COMMAND);
            if( param->clearBase )
            {
               void BaseRemove();
               BaseRemove();
            }
            ret = DoProcessStream(stream, param->objects, param->pi, NULL);
         } else
         {
            PhotoRcvr::ClearIndex();
            ret = true;
            while( ret )
            {
               bool bContinue = false;

               ret = DoProcessStream(stream, param->objects, param->pi, &bContinue);
               if( !ret || !bContinue )
               {
                  SendCommand(net, BYE_COMMAND);
                  break;
               }
               delete stream;

               SendCommand(net, DONE_COMMAND);
               stream = net.ReceiveStream(param->pi);
               if( stream == NULL )
                  break;
               stream->PrepareRead();
            }
         }
         SQLTable::Execute("END;");
         if( !ret )
         {
            param->ec = 1;
            param->answer = L"Ошибка при обработке информации";
         }
      }
      else
      {
         param->ec = net.GetLastError();
         if( param->ec == 0 )
            param->ec = 1;
      }
      delete stream;
   } else
   {
      param->ec = 1;
      param->answer = L"Сервер не отвечает";
   }
   return 0;
}

void SendPacketParam::AddDocument(IDocument* doc, const DocType* docType)
{
   const DataReflector& type = doc->Data()->GetType();
   if( data.size() == 0 || data.back().type != docType )
   {
      SendObjectsData ddata;
      ddata.name = docType->SendTypeName();
      ddata.type = docType;
      ddata.sended = false;
      data.push_back(ddata);

      type.ToStream(&stream, docType->SendTypeName());
   }

   data.back().documents.push_back(doc->RowID());
   type.DataToStream(&stream, *doc->Data());
}

SendObjectsData* SendPacketParam::Find(const wchar_t* name)
{
   std::vector<SendObjectsData>::const_iterator i = data.begin();
   for( ; i != data.end(); i++ )
   {
      if( i->type != NULL )
      {
         if( i->name.compare(name) == 0 )
            return (SendObjectsData*)&(*i);

         //IDocument* doc = i->type->CreateDocument();
         //const DataReflector& type = doc->Data()->GetType();
         //bool res = (wcscmp(type.Name(), name) == 0);
         //i->type->FreeDocument(doc);

         //if( res ) return (SendObjectsData*)&(*i);
      }
   }

   return NULL;
}

static DWORD DoSend(SendPacketParam* param)
{
   OutStream cmdStream;
   NetworkExchange net;
   StringHolder holder;
   ServerCommand cmd;

   MakeServerCommand(&cmd, &holder, &param->addr1, &param->addr2, PUT_COMMAND, L"");

   CheckUpdate();

   const DataReflector& type = cmd.GetType();
   type.ToStream(&cmdStream);
   type.DataToStream(&cmdStream, cmd);

   param->ec = 1;
   param->answer = L"Сервер не отвечает";

   net.SetTimeout(NETWORK_TIMEOUT * 10);
   ReceivedStream* stream = net.Receive(&param->addr1, &param->addr2, cmdStream, NULL);
   if( stream )
   {
      stream->PrepareRead();
      bool good = CheckAnswer(stream, &param->answer);
      delete stream;

      if( good )
      {
         param->answer.clear();
         if( param->pi )
            param->pi->SetText(L"Передача данных");

         if( net.Send(param->stream, param->pi) )
         {
            if( param->pi )
               param->pi->SetText(L"Прием ответа от сервера...");

            stream = net.ReceiveStream(param->pi);
            if( stream != NULL )
            {
               SendAnswerRcvr arcvr(param);
               ReceiveObjects objects;
               objects.push_back(&arcvr);

               stream->PrepareRead();
               SendCommand(net, BYE_COMMAND);

#ifdef NAPOLEON_APPS
               _Module.DoCheckTime();
#endif

               if( param->pi )
               {
                  param->pi->SetText(L"Обработка ответа...");
                  param->pi->SetMax(stream->Size());
               }

               DoProcessStream(stream, objects, param->pi, NULL);
               delete stream;

               bool sended = true;
               std::vector<SendObjectsData>::const_iterator i = param->data.begin();
               for( ; i != param->data.end(); i++ )
               {
                  if( i->sended == false)
                  {
                     sended = false;
                     break;
                  }
               }

               if( !sended )
               {
                  param->ec = 1;
                  if( param->answer.empty() )
                     param->answer = L"Ошибка на сервере при приеме";
               } else
               {
                  param->ec = 0;
                  if( param->answer.empty() )
                     param->answer = L"Данные отправлены";
               }
            } else
               param->answer = L"Сервер не отвечает";
         }
      }
      else
      {
         param->ec = net.GetLastError();
         if( param->ec == 0 )
            param->ec = 1;
      }
   }

   return 0;
}

long NapoleonApp::ReceiveRemnants(std::wstring *answer, IProgressIndicator *pi)
{
   RemnantsRcvr ri;
   DBObjAliasRcvr<ConfigImpl> sci(L"Обработка настроек...", false, L"ServerConfig");
   ReceivePacketParam param(pi);

   param.objects.push_back(&ri);
   param.objects.push_back(&sci);

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReceive, &param, 0, NULL);
   WaitThreadComplete(thread);
   *answer = param.answer;

#ifdef GPS_POS
   _Module.UpdateApps();
#endif
   return param.ec;
}

#ifdef Kolbiko
#include <Add.h>
#endif
long NapoleonApp::ReceiveDocs(std::wstring *answer, IProgressIndicator *pi, DWORD flags)
{
   DeliveryRcvr di;
   PaymentRcvr payi;
   OrderRcvd ordi;

#ifdef SHOW_OFF_TAKE
   OrgRmntsRcvd rmdi;
#endif

   ReceivePacketParam param(pi);

#ifdef Kolbiko
   RetRcvr reti;
   OrgRestRcvr orsi;
#endif

#ifdef BastionNeva
   DBObjectRcvr<PaysImpl> pysi(L"Обработка оплат", true);
#endif

   if( flags & efBalance )
   {
      param.objects.push_back(&di);
      param.objects.push_back(&payi);
#ifdef Kolbiko
      param.objects.push_back(&reti);
      param.objects.push_back(&orsi);
#endif
#ifdef BastionNeva
      param.objects.push_back(&pysi);
#endif
   }

   if( flags & efOrders )
   {
      param.objects.push_back(&ordi);
#ifdef SHOW_OFF_TAKE
      param.objects.push_back(&rmdi);
#endif
   }

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReceive, &param, 0, NULL);
   WaitThreadComplete(thread);
   *answer = param.answer;

   if( param.ec == 0 )
      DocTypeManager::UpdateDocInfo();

   if( flags & efOrders )
   {
      docTypeManager.Refresh(dtOrder);
#ifdef SHOW_OFF_TAKE
      docTypeManager.Refresh(dtRemnants);
#endif
   }
#ifdef Kolbiko
   if( flags & efBalance )
      docTypeManager.Refresh(dtReturn);
#endif
#ifdef BastionNeva
   if( flags & efBalance )
      docTypeManager.Refresh(dtPays);
#endif

   return param.ec;
}

static void ClearPhotoTable()
{
   HCURSOR hCurs = GetCursor();
   SetCursor(LoadCursor(NULL, IDC_WAIT));

   PricePhotoImpl ppi;
   SQLTable table(ppi.Name());
   bool bdo = table.Select(&ppi);
   while( bdo )
   {
      if( *ppi.photo != L'\0' )
         ::DeleteFile(ppi.photo);
      bdo = table.SelectNext(&ppi);
   }

   SQLTable::DropTable(PricePhotoImpl().Name());
   SQLCheckTable(ppi);

   SetCursor(hCurs);
}

long NapoleonApp::ReceivePhoto(std::wstring *answer, IProgressIndicator *pi)
{
   PhotoRcvr phi;
   ReceivePacketParam param(pi);

   param.receivePhoto = true;
   param.objects.push_back(&phi);

   ClearPhotoTable();

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReceive, &param, 0, NULL);
   WaitThreadComplete(thread);
   *answer = param.answer;

   return param.ec;
}

void NapoleonApp::ShowErrorBox(long error, const wchar_t *msg, const wchar_t *prefix)
{
   std::wstring message(prefix);
   if( *msg != 0 )
      message += msg;
   else
   {
      LPVOID lpMsgBuf;
      int len = FormatMessage( FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | 
          FORMAT_MESSAGE_IGNORE_INSERTS, NULL, error, 0, (LPTSTR) &lpMsgBuf, 0, NULL );
      
      if( len )
      {
         message += (wchar_t*)lpMsgBuf;
         LocalFree(lpMsgBuf);
      } else
      {
         wchar_t buf[30];
         wsprintf(buf, L"код ошибки %d", error);
         message += buf;
      }
   }
   MessageBox(GetActiveWindow(), message.c_str(), L"Ошибка", MB_OK|MB_ICONERROR);
}

void AddToStream(SendPacketParam* param, DocumentList *dl, const DocType *type)
{
   for( unsigned i=0; i<dl->Count(); i++ )
   {
      IDocument *doc = dl->Get(i);
      ICreatableDocument *c = doc->Creatable();
      if( c == NULL ) break;
      if( !c->IsDirty() ) continue;

      param->AddDocument(doc, type);
   }
}

#ifdef GPS_POS
#include <GPSArchive.h>
#endif

static void PrepareSend(DocDataList* documents, SendPacketParam* param, bool sendOrgDocs, DWORD flags, WORD dayInterval)
{
   bool sendVisits = ((flags & NapoleonApp::efVisits) != 0);
   bool sendDocs = ((flags & NapoleonApp::efDocs) != 0 || sendVisits || sendOrgDocs);

   if( sendDocs || documents )
   {
      if( documents != NULL )
         documents->AddDocuments(param);
         //param->AddDocument(document, doscType);

      if( documents == NULL || sendOrgDocs )
      {
         const DocType* docType = (documents == NULL || documents->size() == 0) ? NULL : documents->at(0).docType;
         const wchar_t* id = (documents == NULL || documents->size() == 0) ? L"" : documents->at(0).document->ID();
         DocTypeManager::iterator i = docTypeManager.begin();
         for( ; i != docTypeManager.end(); i++ )
         {
            if( documents != NULL && (*i) == docType ) continue;

#ifdef VISIT_DOC
            if( !sendVisits && (*i)->Type() == dtVisit ) continue;
#endif

            DocumentList *dl;
            if( (*i)->GetDocuments(id, &dl) )
            {
               AddToStream(param, dl, (*i));
               delete dl;
            }
         }
      }
   }
   
#ifdef GPS_POS
   if( (flags & NapoleonApp::efGPS) != 0 )
   {
      if( dayInterval == 0 )
         GPSArchive::SerializeCurrent(param);
      else
         GPSArchive::SerializeArchive(param, dayInterval);
   }
#endif
}

struct CmpDocData
{
   bool operator() (const SendDocData& _left, const SendDocData& _right) const
   {
      return (_left.docType < _right.docType);
   }
};

void DocDataList::AddDocuments(SendPacketParam* dest)
{
   sort(begin(), end(), CmpDocData());
   iterator i = begin();
   for( ; i != end(); i++ )
      dest->AddDocument(i->document, i->docType);
}

void DocDataList::ClearDirty(bool reverse)
{
   iterator i = begin();
   for( ; i != end(); i++ )
   {
      ICreatableDocument *c = (*i).document->Creatable();
      if( c != NULL )
      {
         c->ClearDirty(NULL, reverse);
         c->WriteDocument();
      }
   }
}

void DocDataList::RemoveDocuments()
{
   iterator i = begin();
   for( ; i != end(); i++ )
   {
      if( i->docType )
         i->docType->FreeDocument(i->document);
      i->document = NULL;
   }
}

bool SendDocuments(DocDataList* documents, const wchar_t* sendConfirmMsg, bool sendOrgDocs)
{
   HWND activeWindow = GetActiveWindow();
   ProgressWindow pw;
   pw.CreateSTDWindow(activeWindow);

   std::wstring answer;
   long ec = _Module.SendDocument(documents, &answer, &pw, sendOrgDocs);
   pw.DestroyWindow();

   if( ec == 0 )
   {
      MessageBox(activeWindow, (sendConfirmMsg != NULL && *sendConfirmMsg != L'\0' ) ? sendConfirmMsg :
         L"Данные отправлены", L"Подтверждение", MB_OK|MB_ICONINFORMATION);

      documents->ClearDirty(false);
   } else
   {
      _Module.ShowErrorBox(ec, answer.c_str(), L"Ошибка при передаче:\n");
   }
#ifdef RCV_MESSAGE
   _Module.ShowMessage();
#endif

   return (ec==0);
}

bool SendDocument(IDocument* document, const DocType* docType, const wchar_t* sendConfirmMsg, bool sendOrgDocs)
{
   DocDataList docs;
   SendDocData sdd;
   sdd.document = document;
   sdd.docType = docType;
   docs.push_back(sdd);

   return SendDocuments(&docs, sendConfirmMsg, sendOrgDocs);
}

long NapoleonApp::SendDocument(DocDataList* documents, std::wstring *answer, 
                               IProgressIndicator *pi, bool sendOrgDocs, DWORD flags, WORD dayInterval, NapoleonApp::IExportHook* hook)
{
   SendPacketParam param;
   param.pi = pi;
   param.ec = 0;

#ifdef GPS_POS
   // если не указана передача GPS - передаем текущие координаты 
   if( (flags & NapoleonApp::efGPS) == 0 )
   {
      flags |= NapoleonApp::efGPS;
      dayInterval = 0;
   }
#endif
   PrepareSend(documents, &param, sendOrgDocs, flags, dayInterval);

   if( hook != NULL )
      hook->Prepared(&param);

   BeforeSendDocs(&param);

   if( param.stream.Size() == 0 )
   {
      *answer = L"Нет данных для передачи";
      param.ec = neNoDocuments;
   } else
   {
      HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoSend, &param, 0, NULL);
      WaitThreadComplete(thread);
      *answer = param.answer;

#ifdef GPS_POS
      if( (flags & NapoleonApp::efGPS) != 0 && dayInterval == 0 )
         GPSArchive::MoveCurrentToArchive(GPS_ARCHIVE_SIZE);
#endif

      AfterSendDocs(&param);
   }

   return param.ec;
}

//void WriteLog(const char *msg)
//{
//   return;
//}

long NapoleonApp::ExportDocuments(std::wstring *answer, IProgressIndicator *pi, DWORD flags, WORD dayInterval, NapoleonApp::IExportHook* hook)
{
   return SendDocument(NULL, answer, pi, true, flags, dayInterval, hook); 
}

//bool NetReceiver::Receive(ReceiveParam *param, const char *ackCmd)
//{
//   return (param->ec) ? 1 : 0;
//}
//
//bool NetReceiver::ReceiveToFile(const wchar_t *cmd, bool checkVersion, const char *fileName, const wchar_t *message)
//{
//   return false;
//}

void NapoleonApp::ChangePreference()
{
   PreferenceDialog dlg;
   if( dlg.DoModal() == IDOK && preferenceHandler != NULL )
      preferenceHandler->PreferenceChanged();
}

#ifdef RCV_MESSAGE
void NapoleonApp::SetMessageDate(const FILETIME& date)
{
   msgDate = date;
}

void NapoleonApp::ShowMessage()
{
   if( msgDate.dwHighDateTime != 0 )
   {
      MessageImpl mi;
      mi.date = msgDate;
      if( mi.Read() )
         mi.Show();

      msgDate.dwHighDateTime = 0;
   }
}
#endif //RCV_MESSAGE

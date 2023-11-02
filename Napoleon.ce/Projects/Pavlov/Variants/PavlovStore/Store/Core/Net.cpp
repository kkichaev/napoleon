/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Napoleon Logistic Network
*
*  ert   04/09/2010   creating
*/
#include "stdafx.h"

#include <Module.h>
#include <StdFuncs.h>
#include <MainFrame.h>
#include <SQLTable.h>

#include "ObjImpl.h"
#include "DocImpl.h"

#include <NetExchange.h>
#include <DataReader.h>
#include <ServerDefs.h>
#include <StdFuncs.h>

#include "Progress.h"
#include "Preference.h"
#include <DocType.h>

void RTrim(std::wstring *str)
{
   while( str->size() > 0 && *str->rbegin() == L' ' )
      str->erase(str->size() - 1);
}

class PriceReceiver : public DBObjectRcvr<PriceRcvImpl>
{
public:
   typedef DBObjectRcvr<PriceRcvImpl> Base;

   PriceReceiver(const wchar_t* _id) : DBObjectRcvr<PriceRcvImpl>(0, false), id(_id)
   {
   }

   virtual const wchar_t* Name() const { return price.GetType().Name(); }
   virtual const wchar_t* Command() const { return SELECT_COMMAND; }
   virtual const wchar_t* Params() const
   {
      buf = Name();
      buf += L":";
      buf += id;
      return buf.c_str();
   }

   virtual bool Prepare(ReceivedStream* stream)
   {
      if( reader == NULL )
         reader = DataReader::CreateReader(data.GetType(), stream);
      if( reader == NULL ) return false;

      //if( clearBefore ) SQLTable::DropTable(price.Name());
      if( !SQLCheckTable(price) ) return false;

      table = new SQLTable(price.Name());
      table->StartTransaction(500);
      return true;
   }

   virtual bool Write(const PriceRcvImpl& data)
   {
      std::wstring barcode(L"|");
      std::wstring id = data.id;
      std::wstring name = data.name;

      RTrim(&id);
      RTrim(&name);

      vector_t<PriceItem>::const_iterator i = data.items.begin();
      for( ; i != data.items.end(); i++ )
      {
         std::wstring bc(i->barcode);
         RTrim(&bc);
         barcode += bc;
         barcode += L'|';
      }

      PriceRcvImpl& pr = const_cast<PriceRcvImpl&>(data);
      pr.barcode = (wchar_t*)barcode.c_str();
      pr.id = (wchar_t*)id.c_str();
      pr.name = (wchar_t*)name.c_str();
      return (table->Write(data, excludedFields) != NO_ROWID);
   }

private:
   PriceImpl price;
   std::wstring id;
   mutable std::wstring buf;
};

class ConfigRcvr : public DBObjectRcvr<ConfigImpl>
{
public:
   typedef DBObjectRcvr<ConfigImpl> Base;

   ConfigRcvr() : DBObjectRcvr<ConfigImpl>(IDS_PROCESS_SETTINGS, false)
   {
   }

   virtual bool Prepare(ReceivedStream* stream)
   {
      if( !Base::Prepare(stream) ) return false;
      std::wstring stmt(L"DELETE FROM '"); stmt += data.Name(); stmt += L"' WHERE NOT key LIKE 'ServerIPName%'";
      SQLTable::Execute(stmt.c_str());
      return true;
   }
};

const wchar_t* ConfigImpl::IP1 = L"ServerIPName1";
const wchar_t* ConfigImpl::IP2 = L"ServerIPName2";

const wchar_t* ConfigImpl::LoadIP(const wchar_t* ip)
{
   key = (wchar_t*)ip;
   if( !Read() )
      value = L"";
   return value;
}

static void MakeServerCommand(ServerCommand* scmd, StringHolder* holder,  IPAddress* addr1, IPAddress* addr2, 
                              const wchar_t* command, const wchar_t* params)
{
   Preference pref;
   pref.Load();

   scmd->command = (wchar_t*)command;
   scmd->param = (wchar_t*)params;
   scmd->category = holder->Add(UPDATE_CATEGORY);

   MainFrame* mf = (MainFrame*)_Module.GetFrame();
   const wchar_t* ip = mf->GetIP();
   const wchar_t *login = mf->GetCurLogin();
   const wchar_t *password = mf->GetCurPassword();

   if( *login != L'\0' )
   {
      scmd->userid = holder->Add(login);
      scmd->password = holder->Add(password);
   } else 
   {
      scmd->userid = holder->Add(pref.login);
      scmd->password = holder->Add(pref.password);
   }

   scmd->duration = 0;//pref.worked;

   std::wstring ver;
#ifndef _DEBUG
   if( GetVersionStr(&ver, _Module.GetModuleInstance()) )
   {
      scmd->version = holder->Add(ver.c_str());
   } else
      scmd->version = L"";
#else
      scmd->version = L"";
#endif

   ConfigImpl c;
   if( addr1 != NULL )
   {
      addr1->port = pref.port;
      if( *ip != L'\0' )
         addr1->ip = ip;
      else
         addr1->ip = c.LoadIP(ConfigImpl::IP1);
   }

   if( addr2 != NULL )
   {
      addr2->port = pref.port;
      addr2->ip = c.LoadIP(ConfigImpl::IP2);
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

static DWORD DoReceive(ReceivePacketParam *param)
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

   net.SetTimeout(NETWORK_TIMEOUT * 10);
   ReceivedStream* stream = net.Receive(&addr1, &addr2, os, param->pi);
   if( stream )
   {
      stream->PrepareRead();
      if( param->pi )
      {
         std::wstring txt;
         _Module.LoadString(&txt, IDS_PROCESSING); 
         param->pi->SetText(txt.c_str());
         param->pi->SetMax(stream->Size());
      }

      if( CheckAnswer(stream, &param->answer) )
      {
         bool ret = false;
         if( !param->receivePhoto )
         {
            SendCommand(net, BYE_COMMAND);
            if( param->clearBase )
            {
               _Module.BaseRemove();
            }
            ret = ProcessStream(stream, param->objects, param->pi, NULL);
         } else
         {
            ret = true;
            while( ret )
            {
               bool bContinue = false;

               ret = ProcessStream(stream, param->objects, param->pi, &bContinue);
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
            _Module.LoadString(&param->answer, IDS_ERROR_PROCESSING);
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
      _Module.LoadString(&param->answer, IDS_SERVER_NOT_RESPONSE);
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
      if( wcscmp(i->type->SendTypeName(), name) == 0)
         return (SendObjectsData*)&(*i);
   }

   return NULL;
}


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


static DWORD DoSend(SendPacketParam* param)
{
   OutStream cmdStream;
   NetworkExchange net;
   StringHolder holder;
   ServerCommand cmd;

   MakeServerCommand(&cmd, &holder, &param->addr1, &param->addr2, PUT_COMMAND, L"");

   const DataReflector& type = cmd.GetType();
   type.ToStream(&cmdStream);
   type.DataToStream(&cmdStream, cmd);

   param->ec = 1;
   _Module.LoadString(&param->answer, IDS_SERVER_NOT_RESPONSE);

   net.SetTimeout(NETWORK_TIMEOUT * 10);
   ReceivedStream* stream = net.Receive(&param->addr1, &param->addr2, cmdStream, NULL);
   if( stream )
   {
      stream->PrepareRead();
      bool good = CheckAnswer(stream, &param->answer);
      delete stream;

      if( good )
      {
         std::wstring txt;
         param->answer.clear();
         if( param->pi )
         {
            _Module.LoadString(&txt, IDS_TX_DATA);
            param->pi->SetText(txt.c_str());
         }

         if( net.Send(param->stream, param->pi) )
         {
            if( param->pi )
            {
               _Module.LoadString(&txt, IDS_RX_DATA);
               param->pi->SetText(txt.c_str());
            }

            stream = net.ReceiveStream(param->pi);
            if( stream != NULL )
            {
               SendAnswerRcvr arcvr(param);
               ReceiveObjects objects;
               objects.push_back(&arcvr);

               stream->PrepareRead();
               SendCommand(net, BYE_COMMAND);

               if( param->pi )
               {
                  _Module.LoadString(&txt, IDS_PROCESS_DATA);
                  param->pi->SetText(txt.c_str());
                  param->pi->SetMax(stream->Size());
               }

               ProcessStream(stream, objects, param->pi, NULL);
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
                     _Module.LoadString(&param->answer, IDS_ERROR_WHILE_RCV);
               } else
               {
                  param->ec = 0;
                  if( param->answer.empty() )
                     _Module.LoadString(&param->answer, IDS_DATA_SENDED);
               }
            } else
               _Module.LoadString(&param->answer, IDS_SERVER_NOT_RESPONSE);
         }
      }
      else
      {
         param->ec = net.GetLastError();
         if( param->ec == 0 )
            param->ec = 1;
      }
   }

   std::vector<SendObjectsData>::const_iterator i = param->data.begin();
   for( ; i != param->data.end(); i++ )
      delete i->type;

   return 0;
}

int Application::ReceivePrice(const wchar_t* barcode, std::wstring* answer)
{
   ReceivePacketParam params(NULL);
   PriceReceiver prc(barcode);
   params.objects.push_back(&prc);

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReceive, &params, 0, NULL);
   WaitThreadComplete(thread);

   *answer = params.answer;
   return params.ec;
}

static bool GetFromBC(const std::wstring& barcode, PriceImpl* data)
{
   SQLTable t(data->Name());
   std::wstring whereStr(L"WHERE ");
   if( barcode.size() < 7 )
   {
      whereStr += L"id='"; whereStr += barcode; whereStr += L"'";
   } else
   {
      whereStr += L"barcode LIKE '%|"; whereStr += barcode; whereStr += L"|%'";
   }
   return t.Select(data, whereStr.c_str());
}

//
// Может вводиться ш/к или код товара(strlen < 7), для весового товара 7 симв. ш/к, 5 - вес, 1 - контр.сумма
//
bool PriceImpl::Get(const wchar_t* _bc, bool searchExact)
{
   std::wstring barcode(_bc);
   
   isWeight = false;
   weight = 0;
   if( GetFromBC(barcode, this) )
      return true;

   if( searchExact )
      return false;

   if( barcode.size() > 12 )
   {
      std::wstring bc(barcode.substr(0, 7));
      if( GetFromBC(bc.c_str(), this) )
      {
         isWeight = true;
         weight = _wtoi(barcode.substr(7,5).c_str());
         return true;
      }
   }

   return false;
}

int Application::ReceiveData(std::wstring *answer, IProgressIndicator *pi, bool clearBase)
{
   ReceivePacketParam params(pi);
   DBObjAliasRcvr<AgentImpl> agents(IDS_AGENTS, true, L"Agents");
   DBObjAliasRcvr<ConfigImpl> sci(IDS_PROCESS_SETTINGS, false, L"ServerConfig");
   DBObjectRcvr<ServerImpl> srv(IDS_PROCESS_SETTINGS, true);

   params.clearBase = clearBase;
   params.objects.push_back(&sci);
   params.objects.push_back(&agents);
   params.objects.push_back(&srv);

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReceive, &params, 0, NULL);
   WaitThreadComplete(thread);
   *answer = params.answer;

   return params.ec;
}

void AddToStream(SendPacketParam* param, DocumentList *dl, const DocType *docType)
{
   for( unsigned i=0; i<dl->Count(); i++ )
   {
      IDocument *doc = dl->Get(i);
      ICreatableDocument *c = doc->Creatable();
      if( c == NULL ) break;
      if( !c->IsDirty() ) continue;

      param->AddDocument(doc, docType);
   }
}

static void LoadNewDocuments(SendPacketParam* param, const wchar_t* id)
{
   DocTypeManager::iterator i = docTypeManager.begin();
   for( ; i != docTypeManager.end(); i++ )
   {
      if( (*i)->IsCreatable() == false )
         continue;

      DocumentList *dl;
      if( (*i)->GetDocuments(id, &dl) )
      {
         AddToStream(param, dl, (*i));
         delete dl;
      }
   }
}

int Application::ExportDocuments(std::wstring *answer, IProgressIndicator *pi)
{
   SendPacketParam param;
   
   LoadNewDocuments(&param, L"");
   if( param.data.size() == 0 )
   {
      *answer = L"Нет данных для передачи";
      return neNoDocuments;
   }

   param.pi = pi;
   param.ec = 0;

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoSend, &param, 0, NULL);
   WaitThreadComplete(thread);

   *answer = param.answer;
   return param.ec;
}

int Application::SendDocument(IDocument* document, const DocType* type)
{
   HWND activeWindow = GetActiveWindow();
   ProgressWindow pw;
   pw.CreateSTDWindow(activeWindow);

   std::wstring tval;
   LoadString(&tval, IDS_PREPARE);
   pw.SetText(tval.c_str());

	Log("Write to stream");

   SendPacketParam param;
   param.AddDocument(document, type);
   param.pi = &pw;
   param.ec = 0;

	Log("Sending...");

	HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoSend, &param, 0, NULL);
   WaitThreadComplete(thread);

	Log("Sended");

   std::wstring info;
   LoadString(&info, IDS_INFO);
   ::MessageBox(activeWindow, param.answer.c_str(), info.c_str(), MB_OK|MB_ICONINFORMATION);
   pw.DestroyWindow();

   return param.ec;
}

//static bool logStarted = false;
void Log(const char* msg, ... )
{
   wchar_t name[MAX_PATH];
   GetModuleFileName(0, name, sizeof(name)/sizeof(name[0]));
   wcscat(name, L".txt");

   //if( !logStarted )
   //{
   //   DeleteFile(name);
   //   logStarted = true;
   //}

   va_list args;
   va_start(args, msg);

   FILE *file = _wfopen(name, L"at");
   if( file )
   {
      SYSTEMTIME st;
      GetLocalTime(&st);
      fprintf(file, "%02d:%02d:%02d ", st.wHour, st.wMinute, st.wSecond);

      vfprintf(file, msg, args);
      fputs("\n", file);

      fclose(file);
   }
}
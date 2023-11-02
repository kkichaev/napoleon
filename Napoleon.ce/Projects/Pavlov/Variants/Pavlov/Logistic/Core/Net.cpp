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

#include <NetExchange.h>
#include <DataReader.h>
#include <ServerDefs.h>
#include <StdFuncs.h>

#include "Progress.h"
#include "Preference.h"

static void MakeServerCommand(ServerCommand* scmd, StringHolder* holder,  IPAddress* addr1, IPAddress* addr2, 
                              const wchar_t* command, const wchar_t* params)
{
   Preference pref;
   pref.Load();

   scmd->command = (wchar_t*)command;
   scmd->param = (wchar_t*)params;

   const AgentsImpl* a = _Module.Agent();
   if( a != NULL )
   {
      scmd->userid = holder->Add( a->login );
      scmd->password = holder->Add( a->password );
   } else
   {
      scmd->userid = holder->Add(pref.login);
      scmd->password = holder->Add(pref.password);
   }

   scmd->duration = 0;//pref.worked;

   std::wstring ver;
   if( GetVersionStr(&ver, _Module.GetModuleInstance()) )
   {
      scmd->version = holder->Add(ver.c_str());
   } else
      scmd->version = L"";

   wchar_t buf[sizeof(pref.ip) + 1];
   if( addr1 != NULL )
   {
      addr1->port = pref.port;

      mbstowcs(buf, pref.ip, sizeof(pref.ip));
      buf[sizeof(pref.ip)] = L'\0';

      addr1->ip = buf;
   }

   if( addr2 != NULL )
   {
      addr2->port = pref.port;
      mbstowcs(buf, pref.ip2, sizeof(pref.ip));
      buf[sizeof(pref.ip)] = L'\0';

      addr2->ip = buf;
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
//
//static DWORD DoSend(SendPacketParam* param)
//{
//   OutStream cmdStream;
//   NetworkExchange net;
//   StringHolder holder;
//   ServerCommand cmd;
//
//   MakeServerCommand(&cmd, &holder, &param->addr1, &param->addr2, PUT_COMMAND, L"");
//
//   //CheckUpdate();
//
//   const DataReflector& type = cmd.GetType();
//   type.ToStream(&cmdStream);
//   type.DataToStream(&cmdStream, cmd);
//
//   param->ec = 1;
//   param->answer = L"Сервер не отвечает";
//
//   net.SetTimeout(NETWORK_TIMEOUT * 10);
//   ReceivedStream* stream = net.Receive(&param->addr1, &param->addr2, cmdStream, NULL);
//   if( stream )
//   {
//      stream->PrepareRead();
//      bool good = CheckAnswer(stream, &param->answer);
//      delete stream;
//
//      if( good )
//      {
//         param->answer.clear();
//         if( param->pi )
//            param->pi->SetText(L"Передача данных");
//
//         if( net.Send(param->stream, param->pi) )
//         {
//            if( param->pi )
//               param->pi->SetText(L"Прием ответа от сервера...");
//
//            stream = net.ReceiveStream(param->pi);
//            if( stream != NULL )
//            {
//               SendAnswerRcvr arcvr(param);
//               ReceiveObjects objects;
//               objects.push_back(&arcvr);
//
//               stream->PrepareRead();
//               SendCommand(net, BYE_COMMAND);
//
//               if( param->pi )
//               {
//                  param->pi->SetText(L"Обработка ответа...");
//                  param->pi->SetMax(stream->Size());
//               }
//
//               DoProcessStream(stream, objects, param->pi, NULL);
//               delete stream;
//
//               bool sended = true;
//               std::vector<SendObjectsData>::const_iterator i = param->data.begin();
//               for( ; i != param->data.end(); i++ )
//               {
//                  if( i->sended == false)
//                  {
//                     sended = false;
//                     break;
//                  }
//               }
//
//               if( !sended )
//               {
//                  param->ec = 1;
//                  if( param->answer.empty() )
//                     param->answer = L"Ошибка на сервере при приеме";
//               } else
//               {
//                  param->ec = 0;
//                  if( param->answer.empty() )
//                     param->answer = L"Данные отправлены";
//               }
//            } else
//               param->answer = L"Сервер не отвечает";
//         }
//      }
//      else
//      {
//         param->ec = net.GetLastError();
//         if( param->ec == 0 )
//            param->ec = 1;
//      }
//   }
//
//   return 0;
//}

int Application::Sync(std::wstring *answer, IProgressIndicator *pi)
{
   ReceivePacketParam params(pi);

   DBObjectRcvr<AgentsImpl> a(L"Список сотрудников", true);
   DBObjectRcvr<PartnerImpl> p(L"Контрагенты", true);
   DBObjectRcvr<BoardsImpl> b(L"Список полок", true);

   params.objects.push_back(&a);
   params.objects.push_back(&p);
   params.objects.push_back(&b);

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReceive, &params, 0, NULL);
   WaitThreadComplete(thread);
   *answer = params.answer;

   return params.ec;
}


struct SendObjParam
{
   IServObject* object;
   const wchar_t *command;
   IProgressIndicator *pi;

   std::wstring answer;
   int ec;

   SendObjParam() : ec(0) {}
};

class ServObjRcvr : public IReceiveObject
{
public:
   ServObjRcvr(IServObject* object) : reader(NULL) { this->object = object; }
   
   virtual const wchar_t* Name() const { return object->DataType().Name(); }
   virtual const wchar_t* ProgressText() const { return L""; }

   virtual const wchar_t* Command() const { return L""; }
   virtual const wchar_t* Params() const { return L""; }

   virtual bool Read(ReceivedStream* stream)
   {
      if( reader == NULL )
         reader = DataReader::CreateReader(object->GetSelf()->GetType(), stream);

      bool res = false;
      if( reader )
      {
         IReflectableData* data = object->GetSelf();
         if( reader->Read(data, stream) )
         {
            object->UnbindData();
            res = true;
         }
      }
      return res;
   }

   virtual void Close() { delete reader; }

protected:
   IServObject* object;
   DataReader* reader;
};

DWORD ObjExchange(SendObjParam* param)
{
   OutStream stream;
   NetworkExchange net;
   StringHolder holder;
   ServerCommand cmd;
   IPAddress addr1;

   MakeServerCommand(&cmd, &holder, &addr1, NULL, OBJECTS_COMMAND, param->command);
   const DataReflector& type = cmd.GetType();
   type.ToStream(&stream);
   type.DataToStream(&stream, cmd);

   const wchar_t *alias = param->object->DataType().Name();
   IReflectableData* data = param->object->GetSelf();
   const DataReflector& dataType = data->GetType();

   dataType.ToStream(&stream, alias);
   dataType.DataToStream(&stream, *data);

   if( param->pi != NULL )
      param->pi->SetText(L"Передача...");

   param->ec = 1;
   param->answer = L"Сервер не отвечает";

   net.SetTimeout(NETWORK_TIMEOUT * 10);
   ReceivedStream* ostream = net.Receive(&addr1, NULL, stream, NULL);
   if( ostream )
   {
      ostream->PrepareRead();

      // Auth
      bool good = CheckAnswer(ostream, &param->answer);
      if( good )
      {
         SendCommand(net, BYE_COMMAND);

         // obj result
         good = CheckAnswer(ostream, &param->answer);
         if( good )
         {
            if( param->pi != NULL )
               param->pi->SetText(L"Обработка...");

            ServObjRcvr sor(param->object);
            ReceiveObjects rcvObj;
            rcvObj.push_back(&sor);

            good = ProcessStream(ostream, rcvObj, NULL, NULL);
            if( good )
               param->ec = 0;
            else
               param->answer = L"Ошибка при обработке";
         }
      }
      delete ostream;
   }

   return 0;
}

int Application::ObjectExchange(IServObject *object, const wchar_t* command, std::wstring *answer, IProgressIndicator *pi)
{
   SendObjParam param;

   param.object = object;
   param.command = command;
   param.pi = pi;

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)ObjExchange, &param, 0, NULL);
   WaitThreadComplete(thread);
   *answer = param.answer;

   return param.ec;
}

/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Класс диспетчера сервера
 *
 * ert   31/03/2009   creating
 */

#include "stdafx.h"
#include <malloc.h>
#include "dispatcher.h"
#include "sessobj.h"
#include "session.h"
#include "http_server.h"


// это для отладки
#include "objects.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

static const int WAIT_CLIENT_TIMEOUT = 2 * 60 * 1000;
static const int START_CLIENT_TIMEOUT = 30 * 1000;


using namespace GRServer;

Dispatcher::RequestHandler::RequestHandler() : dispatcher(NULL)
{
}

Dispatcher::RequestHandler::~RequestHandler()
{
}

bool Dispatcher::RequestHandler::Accept(Dispatcher *dispatcher, SOCKET srvSock)
{
   this->dispatcher = dispatcher;
   return socket.Accept(srvSock);
}

DWORD Dispatcher::RequestHandler::Execute()
{
	//GRPACKET
#ifdef HTTP_SERVER
	char buf[8];
	if (!socket.PeekData((BYTE*)buf, sizeof(buf), START_CLIENT_TIMEOUT, dispatcher->evStop))
	{
		dispatcher->AddLog(IErrorLogger::Short, "drop socket (%d)", (int)socket.GetSocket());
		return 0;
	}

	if (memcmp(buf, L"GRPA", 8) != 0)
	{
		return HandleHTTP(socket, dispatcher);
	}

#endif

   Session session(dispatcher);

   while( true )
   {
      session.Clear();
      dispatcher->AddLog(IErrorLogger::Short, "accept socket (%d)", (int)socket.GetSocket());

      session.AssignSocket(&socket, dispatcher->evStop);
      Session::AckReturn ar = session.ReadAck(WAIT_CLIENT_TIMEOUT);
      if( ar == Session::arPrevious )
         continue;

      if( ar != Session::arAck || !session.AckHaveData() )
      {
			const char* err = session.AckError();
			dispatcher->AddLog(IErrorLogger::Short, "ReadPacket error (%d) %s", (int)socket.GetSocket(), (err == NULL) ? "" : err );
			//gServer->AddError(false, "ReadPacket error session terminated");
         break;
      }

      // connect plgin - move from session to dispatcher
      if( session.AckIs(SERVER_COMMAND) && session.Command()[COMMAND_MEMBER]->str->compare(PLUGIN_CONNECT) == 0 )
      {
         dispatcher->ConnectPlugin(session.Command()[PARAM_MEMBER]->str->c_str(), &socket);
         break;
      }



		//{
		//	session.Auth();
		//	session.AddAnswer(true, L"OK");
		//	bool sv = session.SendStream();

		//	if (session.ReadAck(WAIT_CLIENT_TIMEOUT))
		//	{
		//		if (session.AckIs(SERVER_COMMAND) && session.CommandIs(BYE_COMMAND))
		//		{
		//			session.Commit();
		//			dispatcher->AddLog(IErrorLogger::Short, "commit (%d)", (int)socket.GetSocket());
		//			break;
		//		}
		//		dispatcher->AddLog(IErrorLogger::Short, "session broken (%d)", (int)socket.GetSocket());
		//	}
		//	break;

		//}


      bool retVal = session.Auth();
      bool addStdObjects = false;
      bool isUpdate = false;

      USES_CONVERSION;
      const char* loginStr = (retVal) ? (const char*)W2A(session.GetUser().ID()) : "?";
      dispatcher->AddLog(IErrorLogger::Short, "auth client (%d) %s '%s'", (int)socket.GetSocket(), 
         (retVal) ? "true" : "false", loginStr);

      if( retVal )
      {
         if( session.AckIs(UPDATE_COMMAND) )
         {
            retVal = session.LoadUpdatePacket();
         } else
         {
            bool canRepeat = true;
            while( retVal && canRepeat && session.AckIs(SERVER_COMMAND) )
            {
               const Member* m = session.Command()[COMMAND_MEMBER];
               if( m == NULL ) break;

               wchar_t *cmdBuf = (wchar_t*)alloca((m->str->size() + 1) * sizeof(wchar_t));
               wcscpy(cmdBuf, m->str->c_str());
               const wchar_t *cmd = cmdBuf;
               const Member* param = session.Command()[PARAM_MEMBER];

               const char *aCmdBuf = W2A(cmdBuf);
               dispatcher->AddLog(IErrorLogger::Full, "do command (%d) %s", (int)socket.GetSocket(), aCmdBuf);

               // update программы идут отдельным пакетом
               if( wcscmp(cmd, CHECK_UPDATE) == 0 )
               {
                  isUpdate = true;
                  retVal = session.CheckUpdate(param);
               } else if( wcscmp(cmd, GET_UPDATE) == 0 )
               {
                  isUpdate = true;
                  retVal = session.GetUpdate(param);
               } else if( wcscmp(cmd, GET_UPD_PACKET) == 0 )
               {
                  isUpdate = true;
                  retVal = session.GetUpdatePacket(param);
               }

               if( isUpdate )
                  break;

               const wchar_t *imp = wcsstr(cmd, IMPERSONATE);
               if( imp != NULL )
               {
                  retVal = session.Impresonate(imp + sizeof(IMPERSONATE)/sizeof(IMPERSONATE[0]) - 1, true);
                  if( !retVal )
                     break;

                  //remove trailing space from command
                  imp--;
                  while( *imp == L' ' ) imp--;
                  const_cast<wchar_t*>(imp)[1] = L'\0';
               }

               if( wcscmp(cmd, QUIT_COMMAND) == 0 )
               {
						const User& u = session.GetUser();
						std::wstring ip;
						session.GetIPAddress(&ip);
						dispatcher->dataCtrl.FreeSession(u.Duration(), u.ID(), ip);

						retVal = true;
               } else if( wcscmp(cmd, GET_COMMAND) == 0 )
               {
                  addStdObjects = true;
                  retVal = session.HandleGet();
               } else if( wcscmp(cmd, PUT_COMMAND) == 0 )
               {
                  //canRepeat = false;
                  retVal = session.SendStream();
                  if( retVal )
                  {
							// если нет подтверждения то считаем все ок. Пришли пустые данные
							if( session.ReadAck(WAIT_CLIENT_TIMEOUT / 2) )
								retVal = session.StoreAckObjects();
							else
								session.AddAnswer(true, L"");
                     addStdObjects = true;
                  }
               } else if( wcscmp(cmd, FORCE_PUT) == 0  || wcscmp(cmd, PUT_NO_EXEC) == 0 )
               {
                  bool retIDS = (!param->str->empty() && (param->str->compare(RET_IDS) == 0));
                  session.PopAck();
                  retVal = session.StoreAckObjects(retIDS, (wcscmp(cmd, FORCE_PUT) == 0));
               } else if( wcscmp(cmd, SELECT_COMMAND) == 0 )
               {
                  retVal = session.Selecting(param);
               } else if( wcscmp(cmd, REMOVE_COMMAND) == 0 )
               {
                  retVal = session.Removing(param);
               } else if( wcscmp(cmd, OBJECTS_COMMAND) == 0 )
               {
                  retVal = session.DoObjCommand(param);
               } else if( wcscmp(cmd, GET_OBJ_FORMAT) == 0 )
               {
                  retVal = session.GetObjectFormat(param);
               } else
               {
                  if(session.HandleCommand(cmd, param))
                  {
                     retVal = true;
							session.PostObjects();
                     //addStdObjects = false;
                     //canRepeat = false;
                  }
               }

               session.RestoreUser();
               if( canRepeat ) canRepeat = session.PopAck();
            }
         }
      }

      if( isUpdate )
      {
         if( retVal )
            session.SendStream();
         break;
      }

		if (retVal && addStdObjects)
         session.WriteStdObjects();
		bool sv = session.SendStream();
		
		if (session.IsNeedRestart())
      {
         gServer->Stopping(SERVER_MUTEX);
         break;
      }

      if( retVal )
         retVal = sv;
      if( !retVal ) break;

      if( session.ReadAck(WAIT_CLIENT_TIMEOUT) )
      {
         if( session.AckIs(SERVER_COMMAND) && session.CommandIs(BYE_COMMAND) )
         {
            session.Commit();

            //int *p = 0x0;
            //*p = 0;
            dispatcher->AddLog(IErrorLogger::Short, "commit (%d)", (int)socket.GetSocket());
            break;
         }
         dispatcher->AddLog(IErrorLogger::Short, "session broken (%d)", (int)socket.GetSocket());
      }

   }

   return 0;
}

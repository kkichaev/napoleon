/*
 * Copyright (C), 2009, ����� �������
 *
 * ����� ���������� �������
 *
 * ert   31/03/2009   creating
 */

#include "stdafx.h"
#include <malloc.h>
#include "dispatcher.h"
#include "sessobj.h"
#include "session.h"
#include "http_server.h"


// ��� ��� �������
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

static const char GRPA[] = "G\x0R\x0P\x0""A\x0";
DWORD Dispatcher::RequestHandler::Execute()
{
	//GRPACKET
	char buf[sizeof(GRPA)];
	if (!socket.PeekData((BYTE*)buf, sizeof(GRPA) - sizeof(char), START_CLIENT_TIMEOUT, dispatcher->evStop))
	{
		dispatcher->AddLog(IErrorLogger::Short, "drop socket (%d)", (int)socket.GetSocket());
		return 0;
	}
	if (memcmp(buf, GRPA, sizeof(GRPA) - sizeof(char)) != 0)
	{
		return HandleHTTP(socket, dispatcher);
	}

   Session session(dispatcher);

   dispatcher->AddLog(IErrorLogger::Short, "accept socket (%d)", (int)socket.GetSocket());

   session.AssignSocket(&socket, dispatcher->evStop);
   Session::AckReturn ar = session.ReadAck(WAIT_CLIENT_TIMEOUT);
   if( ar == Session::arPrevious )
      return 0;

   if( ar != Session::arAck || !session.AckHaveData() )
   {
		const char* err = session.AckError();
		dispatcher->AddLog(IErrorLogger::Short, "ReadPacket error (%d) %s", (int)socket.GetSocket(), (err == NULL) ? "" : err );
		//gServer->AddError(false, "ReadPacket error session terminated");
		return 0;
   }

//    // connect plgin - move from session to dispatcher
//    if( session.AckIs(SERVER_COMMAND) && session.Command()[COMMAND_MEMBER]->str->compare(PLUGIN_CONNECT) == 0 )
//    {
//       dispatcher->ConnectPlugin(session.Command()[PARAM_MEMBER]->str->c_str(), &socket);
// 		return 0;
//    }

   bool retVal = session.Auth();
   bool addStdObjects = false;
   //bool isUpdate = false;

   USES_CONVERSION;
   const char* loginStr = (retVal) ? (const char*)W2A_CP(session.GetUser().ID(), CP_UTF8) : "?";
   dispatcher->AddLog(IErrorLogger::Short, "auth client (%d) %s '%s'", (int)socket.GetSocket(), 
      (retVal) ? "true" : "false", loginStr);

	if (!retVal)
	{
		session.SendStream();
		return 0;
	}

   if( session.AckIs(UPDATE_COMMAND) )
   {
      retVal = session.LoadUpdatePacket();
		retVal = session.SendStream();
	
		if (retVal && session.ReadAck(WAIT_CLIENT_TIMEOUT))
		{
			if (session.AckIs(SERVER_COMMAND) && session.CommandIs(BYE_COMMAND))
			{
				session.Commit();
				dispatcher->AddLog(IErrorLogger::Short, "commit (%d)", (int)socket.GetSocket());
			}
			else
			{
				retVal = false;
			}
		}
		else
		{
			retVal = false;
		}

		if (!retVal)
		{
			dispatcher->AddLog(IErrorLogger::Short, "session broken (%d)", (int)socket.GetSocket());
		}

		return 0;
	}

	bool stdObjectSended = false;
	//, putLicenseRequest = false;
	while (retVal)
	{
		bool canRepeat = true;
		while (retVal && canRepeat && session.AckIs(SERVER_COMMAND))
		{
			const Member* m = session.Command()[COMMAND_MEMBER];
			if (m == NULL) break;

			wchar_t *cmdBuf = (wchar_t*)alloca((m->str->size() + 1) * sizeof(wchar_t));
			wcscpy(cmdBuf, m->str->c_str());
			const wchar_t *cmd = cmdBuf;
			const Member* param = session.Command()[PARAM_MEMBER];

			const char *aCmdBuf = W2A_CP(cmdBuf, CP_UTF8);
			dispatcher->AddLog(IErrorLogger::Full, "do command (%d) %s", (int)socket.GetSocket(), aCmdBuf);

			const wchar_t *imp = wcsstr(cmd, IMPERSONATE);
			if (imp != NULL)
			{
				retVal = session.Impresonate(imp + sizeof(IMPERSONATE) / sizeof(IMPERSONATE[0]) - 1, true);
				if (!retVal)
					break;

				//remove trailing space from command
				imp--;
				while (*imp == L' ') imp--;
				const_cast<wchar_t*>(imp)[1] = L'\0';
			}

			if (wcscmp(cmd, QUIT_COMMAND) == 0)
			{
				const User& u = session.GetUser();
				std::wstring ip;
				session.GetIPAddress(&ip);
				dispatcher->dataCtrl.FreeSession(u.Duration(), u.ID(), ip);

				retVal = true;
			}
			else if (wcscmp(cmd, GET_COMMAND) == 0)
			{
				addStdObjects = true;
				retVal = session.HandleGet();
			}
			else if (wcscmp(cmd, PUT_COMMAND) == 0)
			{
				retVal = session.SendStream();
				if (retVal)
				{
					if (session.ReadAck(WAIT_CLIENT_TIMEOUT / 2))
						retVal = session.StoreAckObjects();
					else
						session.AddAnswer(true, L"");
					addStdObjects = true;
				}
			}
			else if (wcscmp(cmd, FORCE_PUT) == 0 || wcscmp(cmd, PUT_NO_EXEC) == 0)
			{
				bool retIDS = (!param->str->empty() && (param->str->compare(RET_IDS) == 0));
				session.PopAck();
				bool forcePut = (wcscmp(cmd, FORCE_PUT) == 0);
				retVal = session.StoreAckObjects(retIDS, forcePut);
			}
			else if (wcscmp(cmd, SELECT_COMMAND) == 0)
			{
				retVal = session.Selecting(param);
			}
			else if (wcscmp(cmd, REMOVE_COMMAND) == 0)
			{
				//retVal = session.Removing(param);
				session.Removing(param);
			}
			else if (wcscmp(cmd, GET_OBJ_FORMAT) == 0)
			{
				retVal = session.GetObjectFormat(param);
			}
			else
			{
				if (session.HandleCommand(cmd, param))
				{
					retVal = true;
					session.PostObjects();
				}
			}

			session.RestoreUser();
			if (canRepeat) canRepeat = session.PopAck();
		}


		if (retVal && addStdObjects && !stdObjectSended)
		{
			session.WriteStdObjects();
			stdObjectSended = true;
		}

		// CString *mgrLogStr = new CString();
		// if (dispatcher->ReqLicenseData(mgrLogStr))
		// {
		// 	putLicenseRequest = true;
		// 	session.WriteLicenseRequest(*mgrLogStr);
		// }
		// delete mgrLogStr;

		bool sv = session.SendStream();
		if (retVal)
			retVal = sv;
		if (retVal && session.ReadAck(WAIT_CLIENT_TIMEOUT) && session.AckIs(SERVER_COMMAND))
		{
			if ( session.CommandIs(BYE_COMMAND))
			{
				session.Commit();
				dispatcher->AddLog(IErrorLogger::Short, "commit (%d)", (int)socket.GetSocket());
				break;
			}
		}
		else
		{
			retVal = false;
		}
		if (!retVal)
		{
			dispatcher->AddLog(IErrorLogger::Short, "session broken (%d) no bye command", (int)socket.GetSocket());
		}
	}

	// if (putLicenseRequest)
	// 	dispatcher->LicenseRequestDone(false); // true in online sources see LicenseTypeSource


	if (dispatcher->GetConfig().Debug() == IErrorLogger::Full)
	{
		std::stringstream str;
		str << "session (" << socket.GetSocket() << ") ";

		std::string out(str.str());
		session.MemoryStat(&out, false);
		gServer->AddLog(out.c_str());
	}
   return 0;
}

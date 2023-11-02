// UpdateCRC.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "servobj.h"
#include "stdobjs.h"
#include "socket.h"
#include "atlconv.h"

using namespace GRServer;

const wchar_t *name = L"update_prezent";
const wchar_t *login = L"";
const wchar_t *password = L"";

const wchar_t *param_name = L"cmd";
const wchar_t *param_value = L"update_crc";

ServObject* MakeCommand(FormatList* list, const wchar_t* cmd, const wchar_t* param)
{
   Format* fmt = list->GetFormat(ServerCommandFormat::Name());
   if( fmt == NULL )
   {
      fmt = new ServerCommandFormat();
      list->AddFormat(fmt, true);
   }

   ServObject *so = new ServObject(fmt);
   Object *ocmd = so->AddObject();

   (*ocmd)[COMMAND_MEMBER]->str->assign(cmd);
   (*ocmd)[PARAM_MEMBER]->str->assign(param);
   (*ocmd)[USERID_MEMBER]->str->assign(COM_LOGIN);
   (*ocmd)[PASSWORD_MEMBER]->str->assign(password);
   (*ocmd)[DURATION_MEMBER]->number = 0;

   return so;
}

static ServObject* MakeServObject(Format* format)
{
   format->clear();
   {
      MemberFormat mf;
      mf.name = param_name;
      mf.type = MemberFormat::mtString;

      format->push_back(mf);
   }

   ServObject* so = new ServObject(format);
   GRServer::Object *o = so->AddObject();

	o->at(0).str->assign(param_value);

	return so;
}

static void GetServerError(std::string *err, const ExchangeList& el)
{
	USES_CONVERSION;
   if( el.size() > 0 )
   {
      const ServObject* curObj = el.at(0);
      int i = curObj->format->FindMember(MESSAGE_MEMBER);
      int ir = curObj->format->FindMember(RESPONSE_MEMBER);
      if( i >= 0 )
      {
         const Object* co = curObj->at(0);
         GRServer::CString *str = co->at(i).str;
         bool response = (co->at(ir).number > 0);
         (*err) = W2A(( str->empty() || response ) ? L"Объект отсутствует или пустой" : str->c_str());
      }
   } else
   {
      *err = "Сервер не отвечает";
   }
}

int _tmain(int argc, _TCHAR* argv[])
{
	if( argc != 3 )
	{
		printf("%s ip port", argv[0]);
		return 1;
	}

	Format fmt(L"ReportParam");

	ObjCreator objCreator;
   ExchangeList cmd(objCreator.GetFormatList());
   cmd.push_back(MakeCommand(objCreator.GetFormatList(), GET_REPORT, name));
   ServObject* so = MakeServObject(&fmt);
   if( so )
      cmd.push_back(so);

	std::string errorMessage;

   WSADATA wsaData;
   WSAStartup(MAKEWORD(2,2), &wsaData);

	Socket s;
	if( s.Connect(argv[1], atoi(argv[2])) )
	{
      Binary buf;

		ExchangeList el(objCreator.GetFormatList());
      if( cmd.Write(&s) && el.Read(&buf, &s, 10 * 60000, NULL, &objCreator, true) )
      {
			SendCommand(&s, BYE_COMMAND, L"", COM_LOGIN, L"", 0);
		} else
		{
			GetServerError(&errorMessage, el);
		}
	} else
      errorMessage = "Сервер не отвечает";
	
	WSACleanup();

	if( !errorMessage.empty() )
	{
		char *ep = _strdup(errorMessage.c_str());
		CharToOem(errorMessage.c_str(), ep);
		printf("%s", ep);
		free(ep);
		return 1;
	}
	printf("‚лЇ®«­Ґ­®");
	return 0;
}


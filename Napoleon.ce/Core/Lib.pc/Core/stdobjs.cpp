/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Стандартные объекты
 *
 * ert   21/08/2010   creating
 */
#include "stdafx.h"
#include <stdobjs.h>

using namespace GRServer;

ServerCommandFormat::ServerCommandFormat()
{
   name = Name();

   MemberFormat mf;

   mf.name = COMMAND_MEMBER;
   mf.type = MemberFormat::mtString;
   push_back(mf);

   mf.name = PARAM_MEMBER;
   mf.type = MemberFormat::mtString;
   push_back(mf);

   mf.name = USERID_MEMBER;
   mf.type = MemberFormat::mtString;
   push_back(mf);

   mf.name = LOGIN_MEMBER;
   mf.type = MemberFormat::mtString;
   push_back(mf);

   mf.name = PASSWORD_MEMBER;
   mf.type = MemberFormat::mtString;
   push_back(mf);

   mf.name = VERSION_MEMBER;
   mf.type = MemberFormat::mtString;
   push_back(mf);

   mf.name = DURATION_MEMBER;
   mf.type = MemberFormat::mtNumber;
   mf.format.fraction = 0;
   push_back(mf);

	mf.name = CATEGORY_MEMBER;
	mf.type = MemberFormat::mtString;
	push_back(mf);
}

ServerAnswerFormat::ServerAnswerFormat()
{
   name = Name();

   MemberFormat mf;

   mf.name = MESSAGE_MEMBER;
   mf.type = MemberFormat::mtString;
   push_back(mf);

   mf.name = RESPONSE_MEMBER;
   mf.type = MemberFormat::mtNumber;
   mf.format.fraction = 0;
   push_back(mf);
}

//bool GRServer::SendCommand(Socket* socket, const wchar_t* cmd, const wchar_t* param, const wchar_t* user, const wchar_t *password, unsigned duration)
//{
//   FormatList flist;
//   ExchangeList obj(&flist);
//
//   Format *fmt = new ServerCommandFormat();
//   flist.AddFormat(fmt, true);
//
//   ServObject *so = new ServObject(fmt);
//   Object *ocmd = so->AddObject();
//
//   (*ocmd)[COMMAND_MEMBER]->str->assign(cmd);
//   (*ocmd)[PARAM_MEMBER]->str->assign(param);
//   (*ocmd)[USERID_MEMBER]->str->assign(user);
//   (*ocmd)[PASSWORD_MEMBER]->str->assign(password);
//   (*ocmd)[DURATION_MEMBER]->number = duration;
//
//   obj.push_back(so);
//
//   return obj.Write(socket);
//}

static ServObject* MakeAnswer(FormatList* flist, bool res, const wchar_t* message)
{
   Format *fmt = flist->GetFormat(ServerAnswerFormat::Name());
   if( fmt == NULL )
   {
      fmt = new ServerAnswerFormat();
      flist->AddFormat(fmt, true);
   }

   ServObject *so = new ServObject(fmt);
   Object *ocmd = so->AddObject();

   (*ocmd)[RESPONSE_MEMBER]->number = (res) ? 1.0 : 0;
   (*ocmd)[MESSAGE_MEMBER]->str->assign(message);

   return so;
}

bool GRServer::WriteAnswer(OutStream* stream, bool res, const wchar_t* message)
{
   FormatList flist;
   ServObject *so = MakeAnswer(&flist, res, message);

   so->ToString(stream, &flist);
   delete so;

   return true;
}

bool GRServer::SendAnswer(Socket* socket, bool res, const wchar_t* message)
{
   FormatList flist;
   ExchangeList obj(&flist);

   ServObject *so = MakeAnswer(&flist, res, message);
   obj.push_back(so);

   return obj.Write(socket);
}

bool GRServer::ReadAnswer(Socket* socket, DWORD timeout, bool* ret, std::wstring* answer, unsigned* duration, HANDLE evStop)
{
   bool res = false;

   ObjCreator creator;
   ExchangeList obj(creator.GetFormatList());

   if( obj.Read(socket, timeout, evStop, &creator) && obj.size() > 0 )
   {
      ServObject* so = obj.at(0);
      if( so->size() > 0 && so->Name().compare(SERVER_ANSWER) == 0 )
      {
         res = true;

         Object *o = so->at(0);
         *ret = ((*o)[RESPONSE_MEMBER]->number > 0);
         if( answer != NULL )
            *answer = (std::wstring&)*((*o)[MESSAGE_MEMBER]->str);
         if( duration != NULL )
         {
            wchar_t *ep;
				*duration = (unsigned)wcstoul((*o)[MESSAGE_MEMBER]->str->c_str(), &ep, 16);
         }
      }
   }

   return res;
}

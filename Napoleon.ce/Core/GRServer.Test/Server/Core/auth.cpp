/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Аутентификация пользователя
 *
 * ert   24/09/2009   creating
 */
#include "stdafx.h"
#include <ServerDefs.h>
#include "server.h"
#include "session.h"
#include "objdef.h"
#include "objects.h"
#include "dispatcher.h"
#include "srvdata.h"
#include "srvutility.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
using namespace std;

void Session::AddAnswer(bool bresponse, const std::wstring& message)
{
   if( answer == NULL )
      answer = new ServerAnswer(this);

   answer->Add(bresponse, message);

   // мы уже "сбрасывали" аутентификацию в поток - сейчас там только текущая
   if( answer->size() == 1 && outStream.Size() > 0 )
   {
      OutStream s;
      answer->ToString(&s, GetFormatList());
      outStream.Append(s);
      answer->clear();
   }
}

bool Session::Auth()
{
   if( ack.size() == 0 )
      return false;

	Dispatcher::RequestSemahore();

   const Object& command = (*ack.front())[0];
   user = dispatcher->Controller().GetUser(command, this);
   
	Dispatcher::ReleaseSemaphore();
	
	bool retVal = (user != NULL);

   const ObjectDef* so = ObjectDef::Get(L"ServerCommand");
   SessionObject uso(so, this);

   uso.push_back(const_cast<Object*>(&command));

   ObjectDef::Fire((retVal) ? Event::Login : Event::FailLogin, this, &uso);

   uso.front() = NULL;

   return retVal;
}

#ifdef UNIX
#else
bool Session::Auth(const wchar_t* login, const wchar_t* pwd)
{
   USES_CONVERSION;
   SessionObject* so = Build(SERVER_COMMAND, false);
   if( so == NULL )
   {
      gServer->AddError(false, "Не могу создать объект '%s'", W2A(SERVER_COMMAND));
      return false;
   }

   const Format& f = *so->format;
   Object* o = so->AddObject();

   o->at(f.FindMember(USERID_MEMBER)).str->assign(login);
   o->at(f.FindMember(PASSWORD_MEMBER)).str->assign(pwd);

   ack.clear();
   ack.push_back(so);

   bool res = Auth();

   ack.clear();
   delete answer;
   answer = NULL;
   response.clear();

   return res;
}
#endif

const wchar_t NULL_TOK[] = L"null";
static bool ReadUserId(std::wstring* dest, const wchar_t *userId)
{
   while( *userId == L' ' ) userId++;
   if( *userId != L'\'' )
   {
      if( _wcsnicmp(userId, NULL_TOK, 4) == 0 )
      {
         *dest = NULL_TOK;
         return true;
      }
      //return false;
   } else
		userId++;

   while( *userId != L'\'' && *userId != L'\0' )
   {
      dest->append(1, *userId);
      userId++;
   }
   return (*userId == L'\'' || *userId == L'\0');
}

bool Session::Impresonate(const wchar_t *userId, bool addAnswer, const wchar_t *password)
{
   if( user == NULL || curUser != NULL )
       return false;

   User* newUser = NULL;
   std::wstring uid;

	bool retVal = false;

#ifdef RESTRICTED_AGENTS
	if (wcscmp(userId, L"admin") == 0)
	{
		if (password != NULL && ServerData::CheckAdminPassword(password))
		{
			std::wstring version;
			GetServerVersion(&version);

			newUser = new User(this);
			newUser->Assign(ADMIN_ID, L"admin", version.c_str(), 0, true);
			newUser->SetAdmin();

			retVal = true;
		}
	} else
#endif
	{
		if (ReadUserId(&uid, userId))
		{
			if (uid.compare(NULL_TOK) == 0)
			{
				user->SetImpersonateAsNull(true);
				retVal = true;
			}
			else if (user->AllowedUID().Find(uid) || wcscmp(user->ID(), COM_ID) == 0)
			{
				newUser = dispatcher->Controller().GetUser(uid, this);
			}
		}
	}

   if( newUser != NULL )
   {
		if (addAnswer)
			ObjectDef::Fire(Event::Impersonate, this, newUser);

		newUser->CopyRights(user);
      curUser = user;
      user = newUser;

      retVal = true;
   }

   if( addAnswer )
      AddAnswer(retVal, uid.c_str());
   return retVal;
}

void Session::RestoreUser(bool removeObjects)
{
   if( curUser != NULL )
   {
		if (removeObjects)
		{
			const wchar_t* uid = curUser->ID();
			if (*uid)
			{
				ExchangeList::iterator i = response.begin();
				for (; i != response.end(); )
				{
					SessionObject* so = (SessionObject*)((const ServObject*)(*i));
					if (so->UserID().compare(uid) == 0)
					{
						trash.push_back(so);
						*i = NULL;
						i = response.erase(i);
					}
					else
						i++;
				}
			}
		}
      delete user;
      user = curUser;
      curUser = NULL;
   }
   user->SetImpersonateAsNull(false);
}

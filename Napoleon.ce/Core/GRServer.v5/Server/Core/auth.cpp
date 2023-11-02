/*
 * Copyright (C), 2009, ����� �������
 *
 * �������������� ������������
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

static const std::wstring ServerCommandTag(L"ServerCommand");
static const std::wstring LoginTag(L"userid");
static const std::wstring PasswordTag(L"password");
static const std::wstring UUIDTag(L"uuid");
static const std::wstring UserUUID(L"UserUUIDQuery");
static const std::wstring UserType(L"user_type");
static const std::wstring IDTag(L"id");
static const std::wstring UndefUser(L"User is undefined");
static const std::wstring AgentsTag(L"Agents");
static const std::wstring DivisionTag(L"division");
static const std::wstring CategoryTag(L"category");
static const std::wstring Blocked(L"No funds on balance");

void Session::AddAnswer(bool bresponse, const std::wstring& message)
{
   if (jsonWriter != NULL)
   {
      ServerAnswer* sa = new ServerAnswer(this);

      sa->Add(bresponse, message);
      sa->WriteTo(*GetResultObject(sa->format->name));
      delete sa;
      return;
   }

   if( answer == NULL )
      answer = new ServerAnswer(this);

   answer->Add(bresponse, message);

   if( answer->size() == 1 && outStream.Size() > 0 )
   {
      OutStream s;
      answer->ToString(&s, GetFormatList());
      outStream.Append(s);
      answer->clear();
   }
}

bool Session::Auth(const std::string& login, const std::string& password)
{
   SessionObject* so = Build(ServerCommandTag, false);
   if (so == NULL)
      return false;

   USES_CONVERSION;

   Object* o = so->AddObject();
   const Member* m;
   m = (*o)[LoginTag];
   m->str->assign(A2W_CP(login.c_str(), CP_UTF8));

   m = (*o)[PasswordTag];
   m->str->assign(A2W_CP(password.c_str(), CP_UTF8));

   bool ret = AuthInt(*o);
   delete so;

   return ret;
}

bool Session::AuthInt(const Object& command)
{
   const Member* mver = command[VERSION_MEMBER];
   const Member* mcat = command[CategoryTag];
   bool retVal = false;

   if(dispatcher->IsBlocked((const std::wstring&)*mcat->str))
   {
      AddAnswer(false, Blocked);
   } else 
   {
      if(!command[UUIDTag]->str->empty()) 
      {
         ISessionObject *userAuth = LoadObject(UserUUID, NULL, command[UUIDTag]->str->c_str());
         if(userAuth && userAuth->Self()->size() > 0)
         {
            Object* o = userAuth->Self()->at(0);
            CString* utype = (*o)[UserType]->str;

            Member *id = (*o)[IDTag];
            if(utype->compare(AgentsTag) == 0)
            {
               user = new User(this);
               user->Assign(id->str->c_str(), L"", mver->str->c_str(), 0, true, NULL);
            } else 
            {
               user = new User(this);
               Member *division = (*o)[DivisionTag];
               user->Assign((const std::wstring&)*id->str, division->number, 0, (const std::wstring&)*mver->str);
            }
         } 
      } else
      {
         if(command[LoginTag]->str->compare(COM_LOGIN) == 0) 
         {
            if (ServerData::CheckCOMPassword((const std::wstring&)*command[PASSWORD_MEMBER]->str))
            {
               user = new User(this);
               user->Assign(COM_ID, COM_DIVISION, 0, (const std::wstring&)*mver->str);
            }
         }
      }

      retVal = (user != NULL);
      if(!retVal)
      {
         AddAnswer(false, UndefUser);
      } else
      {
         AddAnswer(true, L"");
      }
   }

   // user = dispatcher->Controller().GetUser(command, this);

   const ObjectDef* so = ObjectDef::Get(L"ServerCommand");
   SessionObject uso(so, this);

   uso.push_back(const_cast<Object*>(&command));

   ObjectDef::Fire((retVal) ? Event::Login : Event::FailLogin, this, &uso);

   uso.front() = NULL;

   return retVal;
}

bool Session::Auth()
{
   if( ack.size() == 0 )
      return false;

   return AuthInt((*ack.front())[0]);
}

#ifdef UNIX
#else
bool Session::Auth(const wchar_t* login, const wchar_t* pwd)
{
   USES_CONVERSION;
   SessionObject* so = Build(SERVER_COMMAND, false);
   if( so == NULL )
   {
      gServer->AddError(false, "�� ���� ������� ������ '%s'", W2A_CP(SERVER_COMMAND, CP_UTF8));
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
						AddToTemp(so);
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

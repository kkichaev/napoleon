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

#include <mutex_t.h>
#include <thread.h>
#include <curl_service.h>
#include <regex>

using namespace GRServer;
using namespace std;

class Version
{
public:
   Version(const std::wstring& src);
   Version(const std::string& src);

   bool IsGreaterThan(const Version& v) const;

private:
   std::vector<unsigned> versions;
};

static Mutex verMutex;
static std::map <std::wstring, Version> versions;
static FILETIME lastVersionUpdate;

const __int64 UPD_INTERVAL = (__int64)5 * 60 * 10000000;
const char PROJECTS_URL[] = "http://212.232.41.126/upgrade/projects.php";

class VerUpdateThread : public IThreadWorker
{
public:
   virtual DWORD Execute();
};

Version::Version(const std::wstring& src)
{
   unsigned val = -1;
   std::wstring::const_iterator i = src.begin();
   for (; i != src.end(); i++)
   {
      wchar_t sym = *i;
      if ((int)sym > 0 && iswdigit(sym))
      {
         if (val == -1)
            val = sym - L'0';
         else
            val = val * 10 + sym - L'0';
      }
      else if (sym == L'.')
      {
         if (val != -1)
            versions.push_back(val);
         val = -1;
      }
   }
   if (val != -1)
      versions.push_back(val);
}

Version::Version(const std::string& src)
{
   unsigned val = -1;
   std::string::const_iterator i = src.begin();
   for (; i != src.end(); i++)
   {
      char sym = *i;
      if ((int)sym > 0 && isdigit(sym))
      {
         if (val == -1)
            val = sym - '0';
         else
            val = val * 10 + sym - '0';
      }
      else if (sym == '.')
      {
         if (val != -1)
            versions.push_back(val);
         val = -1;
      }
   }
   if (val != -1)
      versions.push_back(val);
}


bool Version::IsGreaterThan(const Version& v) const
{
   std::vector<unsigned>::const_iterator s = versions.begin();
   std::vector<unsigned>::const_iterator d = v.versions.begin();

   while (s != versions.end() && d != v.versions.end())
   {
      if (*s > *d)
         return true;

      s++;
      d++;
   }
   return false;
}

DWORD VerUpdateThread::Execute()
{
   CurlService* cs = (CurlService*)gServer->GetService(CURL_SERVICE);
   ICurlHandler* h = cs->CreateHandler();

   h->SetUrl(PROJECTS_URL);
   h->Preform();

   gServer->AddLog(IErrorLogger::Full, "Read versions");
   if (h->GetResultCode() == 200)
   {
      std::string msgBuf;
      h->GetOutput(&msgBuf);

      if (verMutex.Acquire(1000))
      {
         std::regex line("<tr>\\W*<td>(.*?)</td>\\W*<td>(.*?)</td>.*?</tr>", std::regex_constants::ECMAScript | std::regex_constants::icase);

         std::cmatch match;
         const char* p = msgBuf.c_str();
         const char* ep = p + msgBuf.size();
         USES_CONVERSION;
         while (std::regex_search(p, ep, match, line))
         {
            std::string project(match[1].first, match[1].second - match[1].first);
            std::string version(match[2].first, match[2].second - match[2].first);
            //gServer->AddLog(IErrorLogger::Full, "Version %s", version.c_str());

            if (version.find('.') != std::string::npos)
            {
               Version v(version);
               versions.insert(std::map<std::wstring, Version>::value_type(A2W(project.c_str()), v));
            }
            p = match[0].second;
         }

         verMutex.Release();
         gServer->AddLog(IErrorLogger::Full, "Read versions done %d", versions.size());
      }
      else 
      {
         gServer->AddLog(IErrorLogger::Full, "Read versions acquire mutex error");
      }
   }
   else
   {
      gServer->AddLog(IErrorLogger::Full, "Read versions error %d", h->GetResultCode());
   }

   delete h;
   return 0;
}

void Dispatcher::UpdateVersions()
{
   SYSTEMTIME st;
   FILETIME ft;
   GetLocalTime(&st);
   SystemTimeToFileTime(&st, &ft);

   if(lastVersionUpdate.dwHighDateTime == 0)
      verMutex.Init();

   __int64 diff = *(__int64*)&ft - *(__int64*)&lastVersionUpdate;
   if (diff > UPD_INTERVAL)
   {
      lastVersionUpdate = ft;
      Thread::Starting(new VerUpdateThread(), gServer, 0, false);
   }
}

bool Dispatcher::IsOldVersion(const wchar_t* project, const wchar_t* version)
{
   bool ret = false;
   if (verMutex.Acquire(1000))
   {
      std::map<std::wstring, Version>::const_iterator fnd = versions.find(project);
      if (fnd != versions.end())
      {
         Version v(version);
         ret = fnd->second.IsGreaterThan(v);
      }
      verMutex.Release();
   }

   UpdateVersions();
   return ret;
}

const wchar_t* FORBIDDEN_TEXT = L"Обмен невозможен. Необходимо обновить приложение до актуальной версии";
const wchar_t* WARNING_TEXT = L"Доступно новое обновление, рекомендуем обновить Наполеон";


void Session::AddAnswer(bool bresponse, const std::wstring& message, const wchar_t* kind)
{
   if( answer == NULL )
      answer = new ServerAnswer(this);

   answer->Add(bresponse, message, kind);

   // мы уже "сбрасывали" аутентификацию в поток - сейчас там только текущая
   if( answer->size() == 1 && outStream.Size() > 0 )
   {
      OutStream s;
      answer->ToString(&s, GetFormatList());
      outStream.Append(s);
      answer->clear();
   }
}

Session::UserVersionActions Session::GetUserUpdateAction(const User& user)
{
   Session::UserVersionActions ret = Session::UserVersionActions::None;

   std::wstring objName(L"NewVersionAction");
   SessionObject* o = Build(objName, false);
   if (o != NULL)
   {
      std::wstring uid = user.ID();
      std::wstring filter(L"\"userid\"=");

      filter.append(L"'").append(uid).append(L"'");
      if (o->Reading(filter.c_str()))
      {
         ServObject* so = o->Self();
         if (so->size() > 0)
         {
            Member *m = (*so->at(0))[L"action"];
            if (m)
            {
               int val = (int)(m->number + 0.005);
               ret = val == 0 ? Session::UserVersionActions::Warning :
                  val == 1 ? Session::UserVersionActions::Forbidden :
                  Session::UserVersionActions::None;
            }
         }
      }
      delete o;
   }

   return ret;
}

const wchar_t* UPDATE_KIND = L"update";

bool Session::CheckProgVersion(const wchar_t* project, const User& user)
{
   bool ret = true;

#ifdef NEW_VERSION_ACTION
   Session::UserVersionActions action = GetUserUpdateAction(user);
   if (action == Session::UserVersionActions::None)
      return true;

   const wchar_t* ver = user.Version();
   if (!*ver)
      return true;

   if (dispatcher->IsOldVersion(project, ver))
   {
      if (action == UserVersionActions::Forbidden)
      {
         ret = false;
         AddAnswer(false,  FORBIDDEN_TEXT, UPDATE_KIND);
      }
      else if(action == UserVersionActions::Warning)
      {
         ISessionObject* so = GetObject(L"Message", NULL);
         Object *mo = so->Self()->AddObject();
         (*mo)[L"message"]->str->assign(WARNING_TEXT);
         Member* km = (*mo)[L"kind"];
         if (km != NULL)
            km->str->assign(UPDATE_KIND);

         SYSTEMTIME st;
         GetLocalTime(&st);

         SystemTimeToFileTime(&st, &(*mo)[L"date"]->datetime);
         WriteObjectToStream((SessionObject*)so->Self(), NULL, NULL);
      }
   }

#endif // NEW_VERSION_ACTION
   return ret;
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
   if (retVal)
   {
      const Member* vm = command[PROJECT_MEMBER];

#ifdef Serviko
      const wchar_t* project = vm == NULL ? L"Serviko" : vm->str->c_str();
#else
      const wchar_t* project = vm == NULL ? NULL : vm->str->c_str();
#endif

      if (project != NULL)
      {
         if (!CheckProgVersion(project, *user))
         {
            retVal = false;

            delete user;
            user = NULL;
         }
      }
   }


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

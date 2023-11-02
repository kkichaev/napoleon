/*
 * Copyright (C), 2009 - 2022, Denis Mosiagin
 *
 * DataController
 *
 * ert   11/10/2010   creating
 */
#include "stdafx.h"
#include "session.h"
#include "objects.h"
#include "datactrl.h"
#include "srvdata.h"
#include "srvutility.h"
#include "objdef.h"
#include "server.h"
#include "srvdata.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>


const wchar_t AUTH_OK[] = L"Auth";
const wchar_t AUTH_ERROR[] = L"User is undefined";
const wchar_t AUTH_ERROR_NO_USERS[] = L"No user info";
const wchar_t AUTH_LICENSE[] = L"Demo is expired";
const wchar_t AUTH_MGR_ERROR[] = L"Can't enter, no free licensions";
const wchar_t AUTH_NO_ID[] = L"User havn't ID";
const wchar_t AUTH_MONITOR_ERROR[] = L"Incorrect monitor program";
const wchar_t AUTH_MONITOR_LOGIN_ERROR[] = L"Incorrect monitor login";

const wchar_t AUTH_COM_LOGIN_ERROR[] = L"Wrong credentials";

const wchar_t AUTH_ADMIN_ERR[] = L"Wrong admin credentials";
const wchar_t AGENTS[] = L"Agents";
const wchar_t ADMIN_USER[] = L"admin";

const DWORD SESSION_TIME = 15 * 60; // 15 �����

using namespace GRServer;

DataController::DataController()
{
}

DataController::~DataController()
{
}

void DataController::Close()
{
}

static User* CheckAdmin(Session* session, const std::wstring& password)
{
   User *user = NULL;
   if( ServerData::CheckAdminPassword(password) )
   {
      std::wstring version;
      GetServerVersion(&version);

      user = new User(session);
      user->Assign(ADMIN_ID, L"admin", version.c_str(), 0, true);
      user->SetAdmin();

      session->AddAnswer(true, AUTH_OK);
   } else
   {
      session->AddAnswer(false, AUTH_ADMIN_ERR);
   }

   return user;
}

// void DataController::PluginConnected(std::wstring* password, const std::wstring& pluginName)
// {
//    wchar_t buf[20];
//    wsprintf(buf, L"%X", GetTickCount());

//    plugins[pluginName] = buf;
//    *password = buf;
// }

// void DataController::PluginClosed(const std::wstring& pluginName)
// {
//    std::map<std::wstring, std::wstring>::iterator fnd = plugins.find(pluginName);
//    if( fnd != plugins.end() )
//       plugins.erase(fnd);
// }

// User* DataController::CheckPlugin(const std::wstring& name, const std::wstring& pwd, Session* session)
// {
//    User *user = NULL;
//    std::map<std::wstring, std::wstring>::const_iterator fnd = plugins.find(name);
//    if( fnd != plugins.end() )
//    {
//       if( fnd->second.compare(pwd) == 0 )
//       {
//          user = new User(session);
//          user->Assign(name.c_str(), name.c_str(), L"", 0, true);
//       }
//    }

//    return user;
// }

bool DataController::GetDivisionManager(User** user, std::wstring* message, const std::wstring& name,
                                const std::wstring& passwd, Session* session, const Object& data)
{
   bool ret = false;

   DWORD duration = (DWORD)((__int64)(data[DURATION_MEMBER]->number + 0.05));
   if( name.compare(COM_LOGIN) == 0 )
   {
		if (ServerData::CheckCOMPassword(passwd))
		{
			*user = new User(session);

			const Member* mver = data[VERSION_MEMBER];
			(*user)->Assign(COM_ID, COM_DIVISION, duration, (const std::wstring&)*mver->str);
			return true;
		}

		*message = AUTH_COM_LOGIN_ERROR;
		return false;
   }

	const Member* mc = data[L"category"];
	std::wstring category;
   if( mc && mc->str )
      category.assign((const std::wstring&)*mc->str);

	const ObjectDef* mgr = ObjectDef::Get(L"DivisionManager");
   if( mgr )
   {
      std::wstring filter(L"\"login\"='");
      filter += name;
      //filter += L"' AND password='"; filter += passwd;
      filter += L"'";

      SessionObject managers(mgr, session);
      managers.CreateReader(filter.c_str());
      managers.Load(NULL);
      managers.CloseReader();

      if( managers.size() > 0 )
      {
         ret = true;
         const Object& m = *((const Object*)managers.front());
         if( m[L"password"]->str->compare(passwd) != 0 )
         {
            *message = AUTH_ERROR;
         } else
         {
            int division = (int)((__int64)(m[L"division"]->number + 0.05));
            std::wstring login((const std::wstring&)*m[L"login"]->str);
            const Member* mid = m[L"id"];

            std::wstring id( mid != NULL ? (const std::wstring&)*mid->str : login);

				ServerData::LicenseType lt = (mc != NULL) ? ServerData::LicenseTypeFromString(category) : ServerData::lcManager;

				bool check = false;
				if( UserActivityHolder::CanCheck(category) )
					check = (UserActivityHolder::IsGranted(message, login, category, true) && 
						CanAccess(duration, login, UserActivityHolder::SessionTime(login)));
				else
				{
					if (monitorUser.find(login) != monitorUser.end())
					{
						check = (lt == ServerData::lcMonitor);
						if (!check)
							*message = AUTH_MONITOR_ERROR;
					} else
					{
						if (lt == ServerData::lcMonitor)
						{
							check = false;
							*message = AUTH_MONITOR_LOGIN_ERROR;
						}
						else
						{
							std::wstring ip;
							session->GetIPAddress(&ip);
							check = CheckManager(duration, login, lt, ip);
						}
					}
				}
            if( !check )
            {
					if( message->empty() )
						*message = AUTH_MGR_ERROR;
            } else
            {
               wchar_t answID[60];
               wsprintf(answID, L"%X", duration);
               *message = answID;

               *user = new User(session);

               const Member* mver = data[VERSION_MEMBER];
               (*user)->Assign(id, division, duration, (const std::wstring&)*mver->str);
            }
         }
      }
   }

   return ret;
}

User* DataController::CheckUser(std::wstring* message, const std::wstring& name, const std::wstring& passwd, Session* session, const Object& data)
{
   User *user = NULL;
   if( GetDivisionManager(&user, message, name, passwd, session, data) )
      return user;

	// assigned some error message from manager
	if (!message->empty())
		return user;

   const ObjectDef* a = ObjectDef::Get(L"Agents");
   if( a == NULL )
   {
      gServer->AddError(false, "No Agents defs");
      *message = AUTH_ERROR_NO_USERS;
      return user;
   }

	const SessionObject &agents = *session->Agents();
   if( agents.size() == 0 )
   {
      *message = AUTH_ERROR_NO_USERS;
      return user;
   }

	*message = AUTH_ERROR;

   int login = agents.format->FindMember(LOGIN_MEMBER);
   int pwd  = agents.format->FindMember(PASSWORD_MEMBER);
#ifdef CHECK_LOGIN_PROGID
   const Member* mprogid = data[PROGID_MEMBER];
   int progid  = agents.format->FindMember(PROGID_MEMBER);
   if( mprogid == NULL && name.empty() )
      return user;
#endif
   if( login >= 0 && pwd >= 0 )
   {
      SessionObject::const_iterator i =  agents.begin();
      for( ; i != agents.end(); i++ )
      {
         const Object* o = (*i);
#ifdef CHECK_LOGIN_PROGID
         const Member* apid = (progid < 0) ? NULL : &o->at(progid);
         bool progIdCompared = (mprogid == NULL) || (apid == NULL) || (apid->str->empty()) || (mprogid->str->compare((const std::wstring&)*apid->str) == 0);
         if( o->at(login).str->compare(name) == 0 &&
            o->at(pwd).str->compare(passwd) == 0 && progIdCompared )
#else
         if( o->at(login).str->compare(name) == 0 &&
            o->at(pwd).str->compare(passwd) == 0 )
#endif
         {
            int id = agents.format->FindMember(USER_ID_MEMBER);
            int iuser = agents.format->FindMember(USER_NAME_MEMBER);
            DWORD duration = (DWORD)((__int64)(data[DURATION_MEMBER]->number + 0.05));
            const std::wstring *pID = (const std::wstring*)o->at(id).str;

            if( pID->empty() )
            {
               *message = AUTH_NO_ID;
            } else
            {
               const Member* mver = data[VERSION_MEMBER];
               std::wstring category;
               const Member* mc = data[L"category"];
               if( mc && mc->str )
                  category.assign((const std::wstring&)*mc->str);

               user = new User(session);
               user->Assign(pID->c_str(),
                  (user >= 0) ? o->at(iuser).str->c_str() : L"",
                  (mver != NULL) ? mver->str->c_str() : L"",
                  duration, true, o);

#ifdef CHECK_LOGIN_PROGID
					user->SetProgID((mprogid == NULL) ? L"" : mprogid->str->c_str());
#endif

               bool reg = false;
               if( user->IsManager() )
               {
                  *message = AUTH_OK;
                  reg = true;
                  DWORD sessID = duration;
                  bool check = false;

						if( UserActivityHolder::CanCheck(category) )
						{
							check = (UserActivityHolder::IsGranted(message, *pID, category, true) && 
								CanAccess(duration, *pID, UserActivityHolder::SessionTime(*pID)));
						} else
						{
							ServerData::LicenseType lt = (mc != NULL) ? ServerData::LicenseTypeFromString(category) : ServerData::lcManager;
							std::wstring ip;
							session->GetIPAddress(&ip);
							check = CheckManager(sessID, *pID, lt, ip);
						}
                  if( check )
                  {
                     wchar_t answID[60];
                     wsprintf(answID, L"%X", sessID);
                     *message = answID;
                     reg = true;
                  } else
                  {
							if( message->empty() )
								*message = AUTH_MGR_ERROR;
                  }
               } else
               {
						bool check = false;
						if( UserActivityHolder::CanCheck(category) )
							check = (UserActivityHolder::IsGranted(message, *pID, category, false) && 
								CanAccess(duration, *pID, UserActivityHolder::SessionTime(*pID)));
						else
							check = ServerData::Registred(*pID, category, duration, user);
                  if( check )
                  {
                     *message = AUTH_OK;
                     reg = true;
                  } else
                  {
							//if( message->empty() )
								*message = (user->IsManager()) ? AUTH_MGR_ERROR : AUTH_LICENSE;
                  }
               }
               if( !reg )
               {
                  delete user;
                  user = NULL;
					}
            }
            break;
         }
      }
   }

   return user;
}


User* DataController::GetUser(const Object& data, Session* session)
{
   User *user = NULL;

   const Member* mpassword = data[PASSWORD_MEMBER];
   const Member* mlogin = data[USERID_MEMBER];
#ifdef CHECK_LOGIN_PROGID
	const Member* mprogid = data[PROGID_MEMBER];
#endif
#ifdef CHECK_LOGIN_PROGID
	if( (mpassword != NULL && mlogin != NULL && !mlogin->str->empty()) || (mprogid != NULL && !mprogid->str->empty()) )
#else
	if( mpassword != NULL && mlogin != NULL && !mlogin->str->empty() )
#endif
   {

      if( mlogin->str->compare(ADMIN_USER) == 0 )
      {
         user = CheckAdmin(session, (const std::wstring&)*mpassword->str);
      } else
      {
			//USES_CONVERSION;
			//gServer->AddLog("L/P %s/%s", W2A(mlogin->str->c_str()), W2A(mpassword->str->c_str()));

         std::wstring message;
         // user = CheckPlugin((const std::wstring&)*mlogin->str, (const std::wstring&)*mpassword->str, session);
         // if( user == NULL )
            user = CheckUser(&message, (const std::wstring&)*mlogin->str, (const std::wstring&)*mpassword->str, session, data);

         session->AddAnswer((user != NULL), message);
      }
   } else
		session->AddAnswer(false, L"Empty user login");

   return user;
}

User* DataController::GetUser(const std::wstring& id, Session* session)
{
   User *user = NULL;

	const SessionObject &agents = *session->Agents();
   if( agents.size() > 0 )
   {
      int idMember = agents.format->FindMember(USER_ID_MEMBER);
      int nameMember = agents.format->FindMember(USER_NAME_MEMBER);

      SessionObject::const_iterator i =  agents.begin();
      for( ; i != agents.end(); i++ )
      {
         const Object* o = (*i);
         if( o->at(idMember).str->compare(id) == 0 )
         {
            user = new User(session);
            user->Assign(id.c_str(), o->at(nameMember).str->c_str(), L"", 0, false, o);
            break;
         }
      }
	}
   return user;
}

void DataController::RefreshUsers()
{
   RegistredList* rl = ServerData::GetRegistredUsers();
   if( !rl )
   {
      exclusiveSessions.clear();
      return;
   }

   if( exclusiveSessions.size() )
   {
      ExclusiveMap::iterator i = exclusiveSessions.begin();
      for( ; i != exclusiveSessions.end(); )
      {
         RegLicenseData rd;
         rd.id = i->first;
         rd.type = ServerData::lcExclusiveManager;
         RegistredList::iterator rlfnd = rl->find(rd);
         if( rlfnd == rl->end() )
         {
            exclusiveSessions.erase(i++);
         }
         else
         {
            rl->erase(rlfnd);
            i++;
         }
      }
   }

	monitorUser.clear();
	RegistredList::const_iterator i = rl->begin();
   for( ; i != rl->end(); i++ )
   {
      if( i->type == ServerData::lcExclusiveManager )
      {
         ExclusiveData ed;
         ed.addr = 0;
         ed.seconds = 0;
         exclusiveSessions[i->id] = ed;
		}
		else if (i->type == ServerData::lcMonitor)
		{
			ExclusiveData ed;
			ed.addr = 0;
			ed.seconds = 0;
			monitorUser[i->id] = ed;
		}
   }
   ExclusiveData ed;
   ed.addr = 0;
   ed.seconds = 0;
   exclusiveSessions[COM_ID] = ed;


   delete rl;
}

bool DataController::CanAccess(DWORD &id, const std::wstring& uid, DWORD sessionTime)
{
   MgrData data;
   data.uid = uid;
	data.addr = id;

	DWORD curTick = GetTickCount();
   DWORD seconds = curTick / 1000;

	bool accepted = true;

	MgrMap::iterator im = mobileMgrSessions.begin();
	for( ; im != mobileMgrSessions.end(); im++ )
	{
		if( im->first.uid.compare(uid) == 0 )
		{
			if( im->first.addr != id )
			{
				// ������, �� ����� ������
				if((seconds - im->second) > sessionTime)
					mobileMgrSessions.erase(im);
				else
					accepted = false;
			}
			// ����� �� ������ - ������
			break;
		}
	}

	if( accepted )
		mobileMgrSessions[data] = seconds;
	return accepted;
}

void DataController::FreeSession(DWORD id, const std::wstring& uid, const std::wstring& ip)
{
   MgrData data;
   data.uid = uid;
	data.addr = id;
	data.ip = ip;

	mobileMgrSessions.erase(data);
	managerSessions.erase(data);

	ExclusiveMap::iterator efnd = exclusiveSessions.find(uid);
	if (efnd != exclusiveSessions.end() && efnd->second.addr == id)
	{
		efnd->second.addr = 0;
		efnd->second.ip.clear();
	}
}

void DataController::GetActiveUsers(std::vector<ActiveUsersData>* data) const
{
	DWORD curTick = GetTickCount();
	DWORD seconds = curTick / 1000;

	ExclusiveMap::const_iterator ei = exclusiveSessions.begin();
	for (; ei != exclusiveSessions.end(); ei++)
	{
		if (ei->second.addr != 0 && (seconds - ei->second.seconds) <= SESSION_TIME)
		{
			ActiveUsersData aud;
			aud.userid = ei->first;
			aud.duration = seconds - ei->second.seconds;
			aud.isExclusive = 1;
			aud.ip = ei->second.ip;
			data->push_back(aud);
		}
	}

	MgrMap::const_iterator mi = managerSessions.begin();
	for (; mi != managerSessions.end(); mi++)
	{
		if ((seconds - mi->second) <= SESSION_TIME)
		{
			ActiveUsersData aud;
			aud.userid = mi->first.uid;
			aud.duration = seconds - mi->second;
			aud.isExclusive = 0;
			aud.ip = mi->first.ip;
			data->push_back(aud);
		}
	}
}

bool DataController::CheckManager(DWORD &id, const std::wstring& uid, int licType, const std::wstring& ip)
//bool DataController::CheckManager(const sockaddr_in& iaddr, const std::wstring& uid)
{
   bool accepted = false;
   DWORD curTick = GetTickCount();
   DWORD seconds = curTick / 1000;

   ExclusiveMap::iterator efnd = exclusiveSessions.find(uid);
   if( efnd != exclusiveSessions.end() )
   {
      if( efnd->second.addr == 0 || efnd->second.addr == id || (seconds - efnd->second.seconds) > SESSION_TIME )
      {
         if( efnd->second.addr != id )
         {
            id = curTick;
            efnd->second.addr = curTick;
         }
         efnd->second.seconds = seconds;
			efnd->second.ip = ip;
         accepted = true;
      }
   }

   if( accepted )
      return true;

	if (licType == ServerData::lcADSManager)
		return true;

	if( licType == ServerData::lcManagerPDA )
		return CanAccess(id, uid, SESSION_TIME);

   MgrData data;
   data.uid = uid;
   data.addr = id;
	data.ip = ip;

	//curTick = 0x9cFFFE35;

	accepted = true;
	MgrMap::iterator mfnd = managerSessions.find(data);
	if( mfnd == managerSessions.end() )
	{
		accepted = false;
		data.addr = curTick;

		DWORD lic = ServerData::LicenseCount(ServerData::lcManager);
		if( managerSessions.size() < lic )
		{
			id = curTick;
			managerSessions[data] = seconds;
			accepted = true;
		} else
		{
			MgrMap::iterator is = managerSessions.begin();
			for( ; is != managerSessions.end(); is++ )
			{
				if( (seconds - is->second) > SESSION_TIME )
				{
					managerSessions.erase(is);
					id = curTick;
					managerSessions[data] = seconds;
					accepted = true;
					break;
				}
			}
		}
	}
	else
	{
		accepted = true;
		mfnd->second = seconds;
	}

   return accepted;
}

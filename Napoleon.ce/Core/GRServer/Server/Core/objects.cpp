/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Стандартные объекты (sessobj.h должен быть включен перед этим файлом)
 *
 * ert   30/04/2010   creating
 */
#include "stdafx.h"
#include "sessobj.h"
#include "objects.h"
#include <ServerDefs.h>
#include "objdef.h"
#include "session.h"

using namespace GRServer;

//
//----------------------------------------------- UserRights ---------------------------------------
//

UserRights* UserRights::LoadRights(const wchar_t* user, Session* s)
{
	std::wstring filter(L"\"DivisionManager$login\"='");
	filter += user; filter += L"'";

	ISessionObject *so = s->LoadObject(L"DivisionManager$rights", NULL, filter.c_str());

	UserRights* ur = new UserRights();
	ur->Load(so);

	return ur;
}

UserRights::UserRights()
{
}

UserRights* UserRights::CopyFrom(UserRights* src)
{
	UserRights* ret = NULL;

	if (src != NULL)
	{
		ret = new UserRights();
		std::map<std::wstring, int>::const_iterator i = src->rights.begin();
		for (; i != src->rights.end(); i++)
			ret->rights[i->first] = i->second;
	}

	return ret;
}

void UserRights::Load(ISessionObject* so)
{
	if (so == NULL)
		return;

	const ServObject* obj = so->Self();
	int tokenIdx = obj->format->FindMember(L"token");
	int rightIdx = obj->format->FindMember(L"right");

	if (tokenIdx >= 0 && rightIdx >= 0)
	{
		ServObject::const_iterator i = obj->begin();
		for (; i != obj->end(); i++)
		{
			const Object* o = (*i);
			rights[(const std::wstring&)(*o->at(tokenIdx).str)] = (int)(o->at(rightIdx).number + 0.05);
		}
	}
}

bool UserRights::CanDo(const std::wstring& name, User::ObjectActions action) const
{
	std::map<std::wstring, int>::const_iterator fnd = rights.find(name);
	//if (fnd == rights.end())
	//	fnd = rights.find(L"");

	if (fnd == rights.end())
		return true;

	if (action == User::oaRead)
		return fnd->second >= User::rvRead;

	return fnd->second >= User::rvWrite;
}

//
//----------------------------------------------- User ---------------------------------------
//
User::User(Session *session) : SessionObject(session), flags(0), rights(NULL), license(ServerData::lcNone)
{
   ObjectDef* udef = const_cast<ObjectDef*>(ObjectDef::Get(L"User"));
   const ObjectDef* adef = ObjectDef::Get(L"Agents");

   if( adef != NULL )
   {
      IObjectData::Fields::const_iterator fi = adef->fields.begin();
      for( ; fi != adef->fields.end(); fi++ )
      {
         if( udef->FindField(fi->format.name) != NULL )
            continue;
         switch( fi->format.type )
         {
         case MemberFormat::mtNumber:
         case MemberFormat::mtString:
         case MemberFormat::mtDateTime:
            udef->fields.insert(*fi);
            break;
         default: break;
         }
      }
   }
   InitObject(udef);
}

User::~User()
{
	delete rights;
	rights = NULL;
}

void User::Assign(const std::wstring& id, int division, DWORD duration, const std::wstring& version)
{
   if( size() > 0 )
      clear();

   Object& oUser = *AddObject();

   oUser[USER_ID_MEMBER]->str->assign(id);
   oUser[USER_NAME_MEMBER]->str->assign(id);
   oUser[VERSION_MEMBER]->str->assign(version);
   oUser[DURATION_MEMBER]->number = duration;
   oUser[REGISTRED_MEMBER]->number = 1;

   Division d(session);
   if( d.LoadDivision(division) )
   {
      flags |= Manager;
      d.SetAllowedUID(&allowedUID);
      allowedUID.insert(id);
		rights = UserRights::LoadRights(id.c_str(), session);
		license = ServerData::GetLicense(id);
		oUser[LICENSE_TYPE_MEMBER]->str->assign(ServerData::LicenseTypeToString(license));
	}
	else
   {
      flags &= (~Manager);
      allowedUID.insert(id);
   }
}

void User::SetProgID(const std::wstring& newPID)
{ 
	if (size() > 0) 
	{
		Object* oUser = at(0);
		(*oUser)[L"progID"]->str->assign(newPID);
	}
}


void User::Assign(const wchar_t* id, const wchar_t* name,  const wchar_t *version, DWORD duration, bool registred, const Object* src)
{
   if( size() > 0 )
      clear();

   Object& oUser = *AddObject();

   oUser[USER_ID_MEMBER]->str->assign(id);
   oUser[USER_NAME_MEMBER]->str->assign(name);
   oUser[VERSION_MEMBER]->str->assign(version);
   oUser[DURATION_MEMBER]->number = duration;
   oUser[REGISTRED_MEMBER]->number = (registred) ? 1 : 0;

   if( src != NULL )
   {
      try
      {
         int srcIdx = 0;
         Format::const_iterator fi = src->format.begin();
         for( ; fi != src->format.end(); fi++,srcIdx++ )
         {
            if( fi->name.compare(USER_ID_MEMBER) == 0 || fi->name.compare(USER_NAME_MEMBER) == 0 ||
               fi->name.compare(VERSION_MEMBER) == 0 || fi->name.compare(DURATION_MEMBER) == 0 ||
               fi->name.compare(REGISTRED_MEMBER) == 0 )
            {
               continue;
            }
            int idx = format->FindMember(fi->name.c_str());
            if( idx < 0 )
               continue;
            Member& m = oUser.at(idx);
            const Member& msrc = src->at(srcIdx);
            switch( fi->type )
            {
            case MemberFormat::mtNumber:
               m.number = msrc.number;
               break;
            case MemberFormat::mtString:
					delete (m.str);
					m.str = new CString(*msrc.str);
               break;
            case MemberFormat::mtDateTime:
               m.datetime = msrc.datetime;
               break;
            default: break;
            }
         }
      } catch(...)
      {
      }
   }

   flags &= (~Manager);
   allowedUID.insert(id);
}

void User::CopyRights(User* other)
{
	delete rights;
	rights = UserRights::CopyFrom(other->rights);
}

void User::SetLicense(const std::wstring& lctype)
{ 
   at(0)->operator[](LICENSE_TYPE_MEMBER)->str->assign(lctype);
}


void User::AssignRootDivision(const std::wstring& id, DWORD duration, const std::wstring& version)
{
	Division d(session);
	std::wstring filter(L"\"parent\"=0");
	d.LoadDivision(filter);
	Assign(id, d.ID(), duration, version);
}

const wchar_t* User::Version() const
{
   const wchar_t* id = L"";
   if( size() > 0 )
   {
      const Object& o = (*this)[0];
      id = o[VERSION_MEMBER]->str->c_str();
   }
   return id;
}

DWORD User::Duration() const
{
	int ret = 0;
   if( size() > 0 )
   {
      const Object& o = (*this)[0];
		ret = (DWORD)(o[DURATION_MEMBER]->number + 0.0005);
   }
	return ret;
}

const wchar_t* User::ID() const
{
   const wchar_t* id = L"";
   if( size() > 0 )
   {
      const Object& o = (*this)[0];
      id = o[USER_ID_MEMBER]->str->c_str();
   }
   return id;
}

const wchar_t* User::UserName() const
{
   const wchar_t* id = L"";
   if( size() > 0 )
   {
      const Object& o = (*this)[0];
      id = o[USER_NAME_MEMBER]->str->c_str();
   }
   return id;
}

static const wchar_t* AdminObjects[] =
{
   L"UserActivity", L"LicensedUsers", L"LicenseCountEx", L"LicenseCount", L"Agents", L"Division", L"DivisionManager",
   L"UserLog", L"LogData", L"ServerConfig", L"ManagerConfig", L"LicenseProjectData", L"LicenseType", L"LicensingUsersData", 
	L"ContractDef", L"NBTLViewer", L"SyncInfo", L"%ActiveUsers", L"AgentActivity", L"UserPinData",	L"Suppliers", L"ProgramSettings",
   L"NewVersionAction", L"ServerTaskSchedulerUpdate", L"ServerTaskParams",L"ServerTaskLog",
#ifdef _Project_PavlovStore
   L"Server",
#endif
#ifdef _Project_EuroasiaTD
	L"ManagerFolder", L"MonitorFolder", L"ScriptDef", L"Monitor",
#endif
#ifdef _Project_Discount
	L"OrgTaskDBF", L"OrgTask", L"Question", L"QuestionDBF", L"QuestionItemValueDBF", L"AgentQuest",
#endif
#ifdef _Project_Servolux
	L"AdmRequestSync",
#endif
#ifdef _Project_Dobrogost
	L"MoveDocs", L"CopyDocs",
#endif
};

static const wchar_t* ADSDisabledObjects[] =
{
	L"Agents", L"Division", L"DivisionManager", L"LicenseProjectData", L"LicenseType", L"LicensingUsersData",
};

static const wchar_t* MonitorWRObjects[] =
{
	L"UserLog",
};

static const wchar_t* LCSObjects[] = 
{
	L"LicenseTypeSource", L"LicensingUsersDataSource", L"DemoProjectDataSource", L"LicenseProjectDataSource",
	L"LicenseType", L"LicensingUsers", L"DemoProjectData", L"LicenseProject",
};

static bool FindObject(const wchar_t* name, const wchar_t* objects[], unsigned count)
{
	for (unsigned i = 0; i < count; i++)
		if (wcscmp(objects[i], name) == 0)
			return true;

	return false;
}

bool User::ObjectAllowed(const std::wstring& name, ObjectActions action) const
{
	const wchar_t* pname = name.c_str();
	bool allowed = true;

	if (FindObject(pname, LCSObjects, sizeof(LCSObjects) / sizeof(LCSObjects[0])))
	{
		return allowed;
	}

   if( IsAdmin() )
   {
		allowed = FindObject(pname, AdminObjects, sizeof(AdminObjects) / sizeof(AdminObjects[0]));
	}
	else 
	{
		if (license == ServerData::lcMonitor)
		{
			if (action != oaRead)
			{
				allowed = FindObject(pname, MonitorWRObjects, sizeof(MonitorWRObjects) / sizeof(MonitorWRObjects[0]));
			}
		}
		else
		{
#ifdef RESTRICTED_AGENTS
			allowed = !FindObject(pname, ADSDisabledObjects, sizeof(ADSDisabledObjects) / sizeof(ADSDisabledObjects[0]));
#endif
			if (rights != NULL)
			{
				allowed = rights->CanDo(name, action);
			}
		}
	}
   return allowed;
}

//
//----------------------------------------------- Division ---------------------------------------
//
Division::Division(Session *session) : SessionObject(session)
{
   InitObject(ObjectDef::Get(L"Division"));
}

struct ISetter
{
   virtual void Set(const Member& m) = 0;
};

class SetUID : public ISetter
{
public:
   SetUID(StrSet *ss) : uids(ss) {}
   virtual void Set(const Member& m) { uids->insert((const std::wstring&)*m.str); }

protected:
   StrSet* uids;
};

static bool LoadObjectData(const Object& o, int index, ISetter *setter)
{
   bool ret = false;
   ServObject *so = o.at(index).object;
   if( so != NULL )
   {
      int id = so->format->FindMember(L"id");
      if( id >= 0 )
      {
         ret = true;
         ServObject::const_iterator soI = so->begin();
         for( ; soI != so->end(); soI++ )
         {
            const Member& m = (*soI)->at(id);
            setter->Set(m);
         }
      }
   }

   return ret;
}

int Division::ID() const
{
	int ret = 0;
   if( size() > 0 )
   {
      const Object& o = (*this)[0];
		ret = (int)(o[L"id"]->number + 0.0005);
   }
	return ret;
}

bool Division::SetAllowedUID(StrSet* allowedUID, std::set<int>* checkedID)
{
   bool deleteChecked = false;
   if( checkedID == NULL )
   {
      deleteChecked = true;
      checkedID = new std::set<int>();
   }

   std::set<int> parentsID;

   int ii = format->FindMember(L"id");
   int ai = format->FindMember(L"agents");

   bool ret = false;
   if( ai >= 0 && ii >= 0 )
   {
      ret = true;
      iterator i = begin();
      SetUID suid(allowedUID);

      for( ; i != end(); i++ )
      {
         int id = (int)((*i)->at(ii).number + 0.05);
         if( checkedID->find(id) == checkedID->end() )
         {
            checkedID->insert(id);
            LoadObjectData(*(*i), ai, &suid);

            parentsID.insert(id);
         }
      }
   }

   if( parentsID.size() > 0 )
   {
      Division ch(session);
      if( ch.LoadDivision(parentsID) )
         ch.SetAllowedUID(allowedUID, checkedID);
   }

   if( deleteChecked )
      delete checkedID;

   return ret;
}

bool Division::LoadDivision(const std::wstring& filter)
{
   bool retVal = false;
   if( CreateReader(filter.c_str()) )
   {
      Load(NULL);
      CloseReader();

      retVal = true;
   }

   return (retVal && (size() > 0));
}

bool Division::LoadDivision(const std::set<int>& ids)
{
   std::wstring filter = L"\"parent\" in (";
   std::set<int>::const_iterator i = ids.begin();
   for( ; i != ids.end(); i++ )
   {
      wchar_t buf[20];
      if( i != ids.begin() ) filter += L",";
      wsprintf(buf, L"%d", (*i));
      filter += buf;
   }
   filter += L")";

	return LoadDivision(filter);
}

bool Division::LoadDivision(int id)
{
   wchar_t buf[20];
   if( id == COM_DIVISION ) *buf = L'\0';
   else wsprintf(buf, L"\"id\" = %d", id);

	return LoadDivision(buf);
}

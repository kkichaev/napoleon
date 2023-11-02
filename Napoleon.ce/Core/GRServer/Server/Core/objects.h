/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Стандартные объекты (servobj.h должен быть включен перед этим файлом)
 *
 * ert   30/04/2010   creating
 */
#ifndef __STD_OBJECTS_H
#define __STD_OBJECTS_H

#include "sessobj.h"
#include "srvdata.h"

namespace GRServer {

class UserRights;
class User : public SessionObject
{
public:
   // AsNull impersonate as null
   enum UserFlags { Manager = 1, Admin = 2, AsNull = 4 };
   enum ObjectActions { oaRead, oaWrite };
   enum RightValues { rvNone = 0, rvRead = 1, rvWrite = 2};

   User(Session *session);
	~User();

   void Assign(const wchar_t* id, const wchar_t* name,  const wchar_t *version = L"", DWORD duration = 0, bool registred = false, const Object* src = NULL);
   void Assign(const std::wstring& id, int division, DWORD duration, const std::wstring& version);
   void AssignRootDivision(const std::wstring& id, DWORD duration, const std::wstring& version);

   const wchar_t* ID() const;
   const wchar_t* UserName() const;
   const wchar_t* Version() const;
	DWORD Duration() const;

   // if action == oaUpdate, name == L""
   bool ObjectAllowed(const std::wstring& name, ObjectActions action) const;

   bool IsManager() const { return ((flags & Manager) != 0); }
   bool IsAdmin() const { return ((flags & Admin) != 0); }
   bool ImpersonateAsNull() const { return ((flags & AsNull) != 0); }

   const StrSet& AllowedUID() const { return allowedUID; }

   void SetAdmin() { flags |= Admin; }

   void SetImpersonateAsNull(bool val)
   {
      if( val ) flags |= AsNull;
      else flags &= (~AsNull);
   }

	void CopyRights(User* other);

	void SetProgID(const std::wstring& newPID);

   // need for NBTL
   void SetLicense(const std::wstring& lctype);

protected:
   DWORD flags;
   StrSet allowedUID;
	UserRights *rights;
	ServerData::LicenseType license;
};

class UserRights
{
public:
	static UserRights* LoadRights(const wchar_t* user, Session* s);

	bool CanDo(const std::wstring& name, User::ObjectActions action) const;

	static UserRights* CopyFrom(UserRights* src);

protected:
	UserRights();

	void Load(ISessionObject* so);

	std::map<std::wstring, int> rights;
};

class Division : public SessionObject
{
public:
   Division(Session *session);

	int ID() const;

   bool LoadDivision(const std::set<int>& ids);
   bool LoadDivision(int id);
	bool LoadDivision(const std::wstring& filter);

   bool SetAllowedUID(StrSet* allowedUID, std::set<int>* checkedID = NULL);
};

} // namespace GRServer

#endif

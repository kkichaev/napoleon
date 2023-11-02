/*
 * Copyright (C), 2009-2015, Денис Мосягин
 *
 * Управление лицензиями
 *
 * ert   09/10/2015   creating
 */ 
#ifndef _GR_SERVER_LICENSE_H
#define _GR_SERVER_LICENSE_H

#include <string>
#include <map>
#include <pstream.h>

class Binary;

namespace GRServer {

struct ISessionObject;

inline int StringCount(const std::wstring& str) { return str.size() * sizeof(wchar_t) + sizeof(WORD); }

struct LicenseType
{
	std::wstring type;
	std::wstring title;
	short forAgents;

	DWORD Size() const { return sizeof(forAgents) + StringCount(type) + StringCount(title); }
	BYTE* Put(BYTE* pb) const;
	const BYTE* Set(const BYTE* pb, DWORD cb);
	bool UpdateFrom(const std::map<std::string, std::string>& values);
};

struct LicenseData
{
	DWORD id;
	std::wstring type;
	DWORD count;
	FILETIME start;
	FILETIME end;

	DWORD Size() const { return sizeof(id) + sizeof(count) + sizeof(start) + sizeof(end) + StringCount(type); }
	BYTE* Put(BYTE* pb) const;
	const BYTE* Set(const BYTE* pb, DWORD cb);
	bool UpdateFrom(const std::map<std::string, std::string>& values);
};

struct DemoData
{
	DWORD id;
	std::wstring type;
	DWORD allowCount;
	DWORD timeSpan;

	DWORD Size() const { return sizeof(id) + sizeof(allowCount) + sizeof(allowCount) + StringCount(type); }
	BYTE* Put(BYTE* pb) const;
	const BYTE* Set(const BYTE* pb, DWORD cb);

	bool UpdateFrom(const std::map<std::string, std::string>& values);
};

struct LicensingUsers
{
	std::wstring login;
	DWORD licenseID;

	DWORD Size() const { return sizeof(licenseID) + StringCount(login); }
	BYTE* Put(BYTE* pb) const;
	const BYTE* Set(const BYTE* pb, DWORD cb);
};

class LicenseDataRT
{
private:
	std::map<std::wstring, LicenseType> types;
	std::map<DWORD, LicenseData> license;
	std::map<std::wstring, DemoData> demos;
	std::map<std::wstring, LicensingUsers> users;

	Binary* Serialize() const;
	bool WriteData() const;

	bool UpdateDemoData(ParseStreamA& stream);
	bool UpdateLicenseData(ParseStreamA& stream);
	bool UpdateLicenseTypes(ParseStreamA& stream);

public:
	LicenseDataRT();

	void CopyUsers(std::vector<LicensingUsers> *users) const;
	void UpdateUsers(const std::vector<LicensingUsers> &users);

	void LoadLicenseTypes(ISessionObject * dest) const;
	void LoadLicenseData(ISessionObject * dest) const;

	bool Update(const Binary& b);
	bool Update(const char* serverResp, const char*ep);

	bool IsKnownType(const std::wstring& licType) const;

	bool IsLicensed(const std::wstring& login, const std::wstring& licType, const FILETIME &curTime) const;
	const DemoData& GetDemoData(const std::wstring& type) const;
};

} // namespace GRServer

#endif
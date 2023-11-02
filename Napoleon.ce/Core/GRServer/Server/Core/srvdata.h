/*
 * Copyright (C), 2009-2010, ƒенис ћос€гин
 *
 * ”правление лицензи€ми
 *
 * ert   18/03/2010   creating
 */ 
#ifndef _GR_SERVER_DATA_H
#define _GR_SERVER_DATA_H

#include <string> 
#include <vector>
#include <set>
#include <map>

class Binary;

namespace GRServer {

const DWORD LicenseID       = 1;
const DWORD AdminPasswordID = 2;
const DWORD UserLicenseID   = 3;
const DWORD ServerLicenseData   = 5;
const DWORD UserSinceFromData = 6;
const DWORD COMPasswordID = 7;

class RegistredList;
class User;

class DataController;
class Dispatcher;
class CString;

class UserActivityHolder
{
public:
	static const DWORD SESSION_ALIVE;
	static bool CanCheck(const std::wstring& licType);
	static bool IsGranted(std::wstring* errMsg, const std::wstring& login, const std::wstring& licType, bool isManager);
	static DWORD SessionTime(const std::wstring& login);
	
	static bool CanSendRequest(CString *mgrLog);
	static void CommitRequest(bool commit);

	static bool Init();
	static void Close();
};

class ServerData
{
public:
   // добавл€€ тип не забыть изменить функции LicenseTypeToString & LicenseTypeFromString (srvdata.cpp)
   enum LicenseType { lcPDA, lcManager, lcExclusiveManager, lcVanPDA, lcNone, lcADS, 
      lcBTLType, lcFastFood, lcAdsLight, lcSkladWinStyle, lcVendPDA, lcManagerPDA,
		lcExpeditorPDA, lcDispatcher, lcADSManager, lcMonitor,
   };

   static DWORD LicenseCount(LicenseType tp);
   static bool  LicenseUpdate(const Binary& data);

   static bool CheckAdminPassword(const std::wstring& password);
   static bool SetAdminPassword(const std::wstring& password);
	static bool SetCOMPassword(const std::wstring& password);
	static bool CheckCOMPassword(const std::wstring& password);
   static void GetCOMPassword(std::wstring* out);

   static bool Registred(const std::wstring& login, const std::wstring& category, DWORD duration, User *u);
   //static bool RegisterUser(const std::wstring& login, LicenseType tp);
   //static bool UnRegistredUser(const std::wstring& login, LicenseType tp);

	static bool Init(Dispatcher* dispatcher);

   static RegistredList* GetRegistredUsers();

	static const wchar_t* LicenseTypeToString(LicenseType type);
	static LicenseType LicenseTypeFromString(const std::wstring& str);

	static Binary* GetServerData(int id);
	static bool AddServerData(const Binary& b, int id);
	static LicenseType GetLicense(const std::wstring& id);

	static std::map<std::wstring, LicenseType> cacheLicense;
};

struct RegLicenseData
{
   std::wstring id;
   ServerData::LicenseType type;

   bool operator<(const RegLicenseData& _item) const
   {
      if( type < _item.type ) return true;
      if( type > _item.type ) return false;
      return (id.compare(_item.id) < 0);
   }
};

class RegistredList : public std::set<RegLicenseData>
{
public:
};

struct IInternalDataSource;
extern IInternalDataSource* internalDataSource;

} // namespace GRServer

#endif

/*
 * Copyright (C), 2009 - 2010, ����� �������
 *
 * DataController
 *
 * ert   11/10/2010   creating
 */
#ifndef __DATA_CONTROLLER_H
#define __DATA_CONTROLLER_H

#include <server.h>

namespace GRServer {

struct ActiveUsersData
{
	std::wstring userid;
	std::wstring ip;
	int isExclusive;
	DWORD duration;
};


class Session;
class Object;
class User;
class DataController
{
public:
   DataController();
   ~DataController();

   void Close();

   // data ������ ������� ServerCommand
   // response ������ ������� ServerAnswer
   User* GetUser(const Object& data, Session* session);
   User* GetUser(const std::wstring& id, Session* session);

   // void PluginConnected(std::wstring* password, const std::wstring& pluginName);
   // void PluginClosed(const std::wstring& pluginName);
   // void PluginsClear() { plugins.clear(); }

   void RefreshUsers();

   ServerConfig& Config() const { return (ServerConfig&)config; }

	bool CanAccess(DWORD &id, const std::wstring& uid, DWORD sessionTime);
	void FreeSession(DWORD id, const std::wstring& uid, const std::wstring& ip);

	void GetActiveUsers(std::vector<ActiveUsersData>* data) const;

protected:
   //bool CheckManager(const sockaddr_in& addr, const std::wstring& uid);
	bool CheckManager(DWORD &id, const std::wstring& uid, int licType, const std::wstring& ip);

   // User* CheckPlugin(const std::wstring& name, const std::wstring& pwd, Session* session);
   User* CheckUser(std::wstring* message, const std::wstring& name, const std::wstring& pwd, Session* session, const Object& data);
   bool GetDivisionManager(User** user, std::wstring* message, const std::wstring& name, 
                           const std::wstring& passwd, Session* session, const Object& data);

   struct MgrData
   {
      DWORD addr;
      std::wstring uid;
		std::wstring ip;

      bool operator< (const MgrData& src) const
      {
			if (addr == src.addr)
			{
				int cmp = uid.compare(src.uid);
				if (cmp == 0)
					return (ip.compare(src.ip) < 0);
				return (cmp < 0);
			}
			return (addr<src.addr) ? true : false;
			//return (addr<src.addr) ? true : (addr>src.addr) ? false : (uid.compare(src.uid) < 0);
      }
   };

   struct ExclusiveData
   {
      DWORD addr;
      DWORD seconds;
		std::wstring ip;
   };

   typedef std::map<MgrData, ULONG> MgrMap;
   typedef std::map<std::wstring, ExclusiveData> ExclusiveMap;
   MgrMap managerSessions, mobileMgrSessions;
   ExclusiveMap exclusiveSessions;
	ExclusiveMap monitorUser;

   // std::map<std::wstring, std::wstring> plugins;

   ServerConfig config;
};

} // namespace GRServer

#endif

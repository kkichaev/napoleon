/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Общие процедуры реализованные в сервере
 *
 * ert   19/03/2009   creating
 */
#ifndef __GR_SERVER_H
#define __GR_SERVER_H

#include <vector>
#include <iserver.h>

#define PLUGIN_KEY "loadPlugin"

namespace GRServer {

class ServerConfig : public IServerConfig
{
public:
	static const char* BackupExtention;
	static const char* BackupPrefix;
	static const char* BackupFileTag;
	static const char* MakeBackupName(std::string* outName, const std::string& folder, FILETIME& ft);

   ServerConfig();
   ServerConfig(const ServerConfig& src);

   virtual const char* ExchangeFolder() const { return feFolder.c_str();}
   virtual const char* UpdateFolder() const { return fuFolder.c_str();}
   virtual const char* PluginsFolder() const { return fpFolder.c_str();}
	virtual const char* ImageFolder() const { return imageFolder.c_str(); }

   virtual IErrorLogger::DebugLevel Debug() const { return debugLevel; }

   virtual bool HaveFeature(const std::wstring& ftrExpr) const;
	virtual bool NoCheckFormat() const { return noCheckFormat; }

   std::string configFile; // файл конфигурации сервера (по умолчанию GRServer.ini)
   WORD port;
   IErrorLogger::DebugLevel debugLevel;

   std::string defsFile;
   std::string updateFolder;
   std::string serverBase;
   std::vector<std::string> addDefsFile;
   std::string pluginsFolder;
   std::string featuresFile;
	std::string imageFolder; // also http root

	std::string jsLogin;
	std::string jsPassword;
	bool useGRJS;

	DWORD logLength;
	DWORD concurentConnections;
	bool makeDumpOnException;
	bool noCheckFormat;

	bool makeBackup;
	std::string backupFolder;
	int backupCopies;
	int backupStartTime;
	int backupRunDays;

	enum RunDays {
		Monday    =    1,
		Tuesday   =    2,
		Wednesday =    4,
		Thursday  =    8,
		Friday    = 0x10,
		Saturday  = 0x20,
		Sunday    = 0x40,
	};

   ServerConfig& operator= (const ServerConfig& src);

   virtual const char* Option(const std::string& key) const
   {
      std::map<std::string, Values>::const_iterator fnd = items.find(key);
      return (fnd == items.end()) ? "" : fnd->second.front().c_str();
   }

   void ParseCmdLine(DWORD argc, const char* argv[]);

   bool Load(); // configFile
   void Save(); // configFile

   bool GetOption(std::vector<std::string> *res, const std::string& key)
   {
      std::map<std::string, Values>::const_iterator fnd = items.find(key);
      if(fnd == items.end())
         return false;
      res->operator=(fnd->second);
      return true;
   }

	std::string& ExchFolderInt() { return exchangeFolder; }

#ifdef UNIX
#else
   bool Edit(HINSTANCE hInstance);
#endif

protected:
   void SetDefault();
   bool SetValue(const std::string& key, const std::string& value);
   void UpdateFullNames();

protected:
   std::string exchangeFolder;

   std::string feFolder, fuFolder, fpFolder;
   typedef std::vector<std::string> Values;
   std::map<std::string, Values> items;

   static bool ftrLoaded;
};

extern const char SERVER_MUTEX[];
extern IServer* gServer; // in grserver.cpp для локального использования
extern "C" int DBF_CODE_PAGE;

bool AddOnInit();

} // namespace GRServer

#endif


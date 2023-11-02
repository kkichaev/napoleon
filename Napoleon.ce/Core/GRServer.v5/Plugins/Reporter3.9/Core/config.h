#pragma once

#include <map>
#include <iserver.h>

struct Config
{
	std::string pythonHome;
	bool debug;

	int maxCOMSlots;
	long liveTimeCOM; // in seconds

	std::string debugFile;

	std::map<std::string, std::string> configs;
	
	std::vector<std::string> userSites;

	Config();
	bool Load(const std::string& fileName, const GRServer::IServerConfig& scfg);
	void SetValue(const std::string& key, const std::string& value, const GRServer::IServerConfig& scfg);
};


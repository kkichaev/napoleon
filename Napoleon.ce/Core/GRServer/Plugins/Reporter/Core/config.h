#pragma once

#include <map>

struct Config
{
	std::string pythonHome;
	bool debug;

	int maxCOMSlots;
	long liveTimeCOM; // in seconds

	std::string debugFile;

	std::map<std::string, std::string> configs;

	Config();
	bool Load(const std::string& fileName);
	void SetValue(const std::string& key, const std::string& value);
};


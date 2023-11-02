#pragma once

#include <string>
struct JoinServerStatusHandler;
class JoinServer
{
public:
	enum Status { None, Connecting, Working, Error };

	static bool Start(const std::string& jsLogin, const std::string& jsPwd, const std::string& jsProject, WORD serverPort, bool checkOnly);
	
	// erro utf-8
	static DWORD Register(std::string* error, const std::string& jsLogin, const std::string& jsPwd, const std::string& jsProject);

	static void Stop();

	static Status GetStatus();
	static DWORD GetID();
	static const std::string& GetError();
	static void SetHandler(JoinServerStatusHandler* h);
};

struct JoinServerStatusHandler
{
	virtual void OnStatusChange(JoinServer::Status status) = 0;
};


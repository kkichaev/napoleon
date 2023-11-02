/*
* Copyright (C), 2020-2022, Denis Mosiagin
*
* serveer Backup
*
* ert   01/02/2020   creating
*/

#include "stdafx.h"

#include "dispatcher.h"
#include "thread.h"
#include "idatasource.h"
#include "srvdata.h"
#include <algorithm>

using namespace GRServer;

static ThreadHandle hBackupThread;

struct BackupParam
{
	HANDLE hStop;
	std::string folder;
	int copies;
	int startTime;
	int runDays;
};
#ifdef UNIX
#else

static bool IsRunDay(WORD dayOfWeek, const BackupParam &param)
{
	static int flags[] = 
	{
		ServerConfig::Sunday, ServerConfig::Monday, ServerConfig::Tuesday, ServerConfig::Wednesday, ServerConfig::Thursday, ServerConfig::Friday, ServerConfig::Saturday
	};

	return dayOfWeek < 7 && ((param.runDays & flags[dayOfWeek]) != 0);
}

static DWORD CountNextBackupStart(const BackupParam &param)
{
	int h = (param.startTime & 0xFF00) >> 8;
	int m = param.startTime & 0xFF;
	
	__int64 ft;
	SYSTEMTIME st;

	GetLocalTime(&st);
	SystemTimeToFileTime(&st, (FILETIME*)&ft);

	__int64 rt = ft;

	for (int i = 0, cw = st.wDayOfWeek; i <= 7; i++, cw++)
	{
		if (IsRunDay((cw % 7), param))
		{
			if (i > 0)
				FileTimeToSystemTime((FILETIME*)&rt, &st);

			st.wHour = h;
			st.wMilliseconds = 0;
			st.wMinute = m;
			st.wSecond = 0;

			SystemTimeToFileTime(&st, (FILETIME*)&rt);

			__int64 diff = (rt - ft) / 10000;
			if (diff < 0)
			{
				// check is a current minute or below
				if ((-diff) < 60 * 1000)
					return 1000;
				// continue to next day
			}
			else
			{
				return (DWORD)(diff);
			}
		}
		rt += ((__int64)24 * 3600 * 10000000);
	}

	return 0;
}

static void CheckFilesCount(unsigned maxCount, const BackupParam& param)
{
	std::vector<std::string> files;

	std::string ffile(param.folder);
	ffile.append(ServerConfig::BackupPrefix).append("*").append(ServerConfig::BackupExtention);
	WIN32_FIND_DATAA ffd;
	HANDLE hFind = FindFirstFileA(ffile.c_str(), &ffd);
	if (hFind != INVALID_HANDLE_VALUE)
	{
		while (true)
		{
			files.push_back(param.folder + ffd.cFileName);

			if (FindNextFileA(hFind, &ffd) == FALSE)
				break;
		}
		FindClose(hFind);
	}

	std::sort(files.begin(), files.end());
	while (files.size() > maxCount)
	{
		const std::string& fn = files.front();
		DeleteFileA(fn.c_str());
		gServer->AddLog("Backup remove file '%s'", fn.c_str());
		files.erase(files.begin());
	}
}

static void CopyBackupFile(const std::string& fileName, const BackupParam& param, FILETIME ft)
{
	FILE *rd = fopen(fileName.c_str(), "rb");
	if (rd == NULL)
	{
		gServer->AddLog("Backup error %d can't open temp file '%s'.", errno, fileName.c_str());
	}
	else
	{
		std::string bkName;
		ServerConfig::MakeBackupName(&bkName, param.folder, ft);
		FILE *wr = fopen(bkName.c_str(), "wb");
		if (wr == NULL)
		{
			gServer->AddLog("Backup error %d can't create file '%s'.", errno, bkName.c_str());
		}
		else
		{
			fputs(ServerConfig::BackupFileTag, wr);
			fprintf(wr, "%08X%08X", ft.dwHighDateTime, ft.dwLowDateTime);

			int bufSize = 1024 * 1024;
			char *buf = (char*)malloc(bufSize);

			while (true)
			{
				size_t rc = fread(buf, 1, bufSize, rd);
				if (rc == 0)
					break;
				fwrite(buf, 1, rc, wr);
			}

			free(buf);
			fclose(wr);

			gServer->AddLog("Backup create file '%s'", bkName.c_str());
		}
		fclose(rd);
		DeleteFileA(fileName.c_str());
	}
}

static ThreadExitType BackupThread(BackupParam* _param)
{
	BackupParam param = *_param;
	gServer->AddLog("Backup starting thread folder: '%s', copies: %d, runDays: %d, startTime: %d", 
		param.folder.c_str(), param.copies, param.runDays, param.startTime);

	while (true)
	{
		DWORD waitInterval = CountNextBackupStart(param);
		if (waitInterval == 0)
		{
			gServer->AddLog("Backup no next backup. Exit");
			break;
		}
		else
		{
			gServer->AddLog("Backup run after %d sec", waitInterval / 1000);
		}
		DWORD res = WaitForSingleObject(param.hStop, waitInterval);
		if (res == WAIT_OBJECT_0)
		{
			break;
		}
		if (res != WAIT_TIMEOUT)
		{
			gServer->AddLog("Backup wait error %d", GetLastError());
			break;
		}
		gServer->AddLog("Backup starting...");

		std::string fileName(param.folder);
		fileName += "bkp.tmp";
		if (internalDataSource->BackupBase(fileName.c_str()))
		{
			if (param.copies > 0)
				CheckFilesCount(param.copies - 1, param);

			FILETIME ft;
			SYSTEMTIME st;

			GetLocalTime(&st);
			SystemTimeToFileTime(&st, &ft);
			CopyBackupFile(fileName, param, ft);
		}
		else
		{
			gServer->AddLog("Backup not created.");
		}

		Sleep(70000); // we need wait before next run
	}

	delete _param;
	CloseHandle(hBackupThread);
	return 0;
}
#endif


void Dispatcher::StartBackupThread()
{
#ifdef UNIX
#else
	ServerConfig& sc = dataCtrl.Config();
	if (sc.makeBackup)
	{
		BackupParam *param = new BackupParam();
		param->hStop = evStop;
		param->folder = sc.backupFolder;
		param->copies = sc.backupCopies;
		param->runDays = sc.backupRunDays;
		param->startTime = sc.backupStartTime;

		hBackupThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)BackupThread, param, 0, NULL);
	}
#endif
}
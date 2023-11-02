/*
 * Copyright (C), 2009 - 2018, Денис Мосягин
 *
 * Интерфес сервера
 *
 * ert   20/03/2018   creating
 */
#ifndef __CURL_SERVICE_H
#define __CURL_SERVICE_H

#include <string>

namespace GRServer {

#define CURL_SERVICE L"CurlService"

struct ICurlHandler
{
	enum Method { Get, Post, Custom };

	virtual ~ICurlHandler() {}

	virtual void SetMethod(Method method) = 0;
	virtual void SetUrl(const char* url) = 0;

	virtual void AddHeader(const char* header) = 0;

	virtual void AddData(const char* data) = 0;

	virtual void AddMimeData(const char* name, const char* data, size_t dataSize) = 0;
	virtual void AddMimeFileData(const char* name, const char* data, size_t dataSize, const char* fileName, const char* type) = 0;
	virtual void SetCustomRequest(const char* request) = 0;

	virtual bool Preform() = 0;

	virtual long GetResultCode() = 0;

	virtual void GetOutput(std::string* output) = 0;
};

class CurlService
{
public:
	virtual ~CurlService();
	virtual ICurlHandler* CreateHandler();

	static CurlService* GetService();
};

} // namespace GRServer

#endif

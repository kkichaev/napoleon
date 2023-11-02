/*
 * Copyright (C), 2009 - 2022, Denis Mosiagin
 *
 * DataController
 *
 * ert   20/03/2018   creating
 */
#include "stdafx.h"
#include "curl_service.h"

#define CURL_STATICLIB
#include "curl/curl.h"

using namespace GRServer;

static CurlService service;

class CurlHandler : public ICurlHandler
{
public:
	CurlHandler();

	virtual ~CurlHandler();

	virtual void SetMethod(Method method) { this->method = method; }
	virtual void SetUrl(const char* url) { curl_easy_setopt(handle, CURLOPT_URL, url); }

	virtual void AddHeader(const char* headerStr) { headers = curl_slist_append(headers, headerStr);  }

	virtual void AddData(const char* data) { curl_easy_setopt(handle, CURLOPT_POSTFIELDS, data); }

	virtual void AddMimeData(const char* name, const char* data, size_t dataSize);
	virtual void AddMimeFileData(const char* name, const char* data, size_t dataSize, const char* fileName, const char* type);
	virtual void SetCustomRequest(const char* request);

	virtual bool Preform();

	virtual long GetResultCode()
	{
		long code;
		curl_easy_getinfo(handle, CURLINFO_RESPONSE_CODE, &code);
		return code;
	}

	virtual void GetOutput(std::string* output)
	{ 
		long size = 0;
		curl_easy_getinfo(handle, CURLINFO_HEADER_SIZE, &size);
		if (size > 0)
			output->assign(data.substr(size));
		else
			output->assign(data);
	}

	static size_t WriteHeader(char *buffer, size_t size, size_t nitems, void *userdata);
	static size_t WriteOut(char *ptr, size_t size, size_t nmemb, void *userdata);

private:
	CURL* handle;
	curl_mime *mime;
	Method method;
	curl_slist *headers;

	std::string data;
};

CurlHandler::CurlHandler() : mime(NULL), method(Get), headers(NULL)
{
	handle = curl_easy_init();
	curl_easy_setopt(handle, CURLOPT_WRITEFUNCTION, WriteOut);
	curl_easy_setopt(handle, CURLOPT_WRITEDATA, (void*)this);

	curl_easy_setopt(handle, CURLOPT_HEADERFUNCTION, WriteOut);
	curl_easy_setopt(handle, CURLOPT_HEADERDATA, (void*)this);
	curl_easy_setopt(handle, CURLOPT_HEADER, 0L);
}

CurlHandler::~CurlHandler()
{
	curl_easy_cleanup(handle);
	if (mime != NULL)
		curl_mime_free(mime);
	if (headers != NULL)
		curl_slist_free_all(headers);
}

void CurlHandler::SetCustomRequest(const char* request)
{
	method = Custom;
	curl_easy_setopt(handle, CURLOPT_CUSTOMREQUEST, request);
}

size_t  CurlHandler::WriteHeader(char *buffer, size_t size, size_t nitems, void *userdata)
{
	return size * nitems;
}

size_t  CurlHandler::WriteOut(char *buffer, size_t size, size_t nitems, void *userdata)
{
	((CurlHandler*)userdata)->data.append(buffer, size * nitems);
	return size * nitems;
}

void CurlHandler::AddMimeData(const char* name, const char* data, size_t dataSize)
{
	if (mime == NULL)
		mime = curl_mime_init(handle);

	curl_mimepart* part = curl_mime_addpart(mime);
	curl_mime_data(part, data, dataSize);
	curl_mime_name(part, name);
}

void CurlHandler::AddMimeFileData(const char* name, const char* data, size_t dataSize, const char* fileName, const char* type)
{
	if (mime == NULL)
		mime = curl_mime_init(handle);

	curl_mimepart* part = curl_mime_addpart(mime);
	curl_mime_data(part, data, dataSize);
	curl_mime_filename(part, fileName);
	curl_mime_name(part, name);
	if (type != NULL)
		curl_mime_type(part, type);
}

bool CurlHandler::Preform()
{
	if (headers != NULL)
		curl_easy_setopt(handle, CURLOPT_HTTPHEADER, headers);

	if (method == Post)
	{
		if (mime == NULL)
			curl_easy_setopt(handle, CURLOPT_POST, 1);
		else
			curl_easy_setopt(handle, CURLOPT_MIMEPOST, mime);
	}


	return (curl_easy_perform(handle) == 0);
}

CurlService::~CurlService()
{
	curl_global_cleanup();
}

ICurlHandler* CurlService::CreateHandler()
{
	return new CurlHandler();
}

static bool inited = false;
CurlService* GRServer::CurlService::GetService() 
{
	if (!inited)
	{
		curl_global_init(CURL_GLOBAL_DEFAULT);
		inited = true;
	}

	return &service;
}
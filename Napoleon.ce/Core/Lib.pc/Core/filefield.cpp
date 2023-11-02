/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * Session object.
 *
 * ert   22/06/2012   creating
 */
#include "stdafx.h"
#include <servobj.h>
#include <isessobj.h>
#ifdef UNIX
#else
#include "Shlobj.h"
#include <io.h>
#endif
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

#ifdef UNIX
#else
static void ChangeDivider(std::wstring* str)
{
	size_t pos = str->find(L'/');
	while (pos != std::wstring::npos)
	{
		str->replace(pos, 1, 1, L'\\');
		pos = str->find(L'/', pos + 1);
	}

}
#endif


//
//------------------------------------------ FileField ----------------------------------------------------
//
FileField::FileField(int srcIndex, int meIndex, const char* folder, IErrorLogger* logger)
{
   this->srcIndex = srcIndex;
   this->meIndex = meIndex;
   this->folder = folder;
   this->logger = logger;
}

void FileField::Close()
{
}

bool FileField::WriteFile(const Object& src)
{
	bool ret = true;

   const Member& m = src.at(meIndex);
   IBinary *b = m.binary;
   if( b != NULL && b->Size() > 0 )
   {
#ifdef UNIX
		USES_CONVERSION;
		std::string fileName(folder);
		fileName += W2A(src.at(srcIndex).str->c_str());

		std::string tf;
      ConvertPath(fileName, &tf);
      fileName.assign(tf);
       std::string::size_type idx = fileName.find_last_of("/");
       if(idx != std::string::npos)
      {
         std::string dir = fileName.substr(0, idx);
         bool res = MakePath(dir.c_str());
         if( logger != NULL && !res )
            logger->AddError(false, "error (%d) creating folder '%s'", res, dir.c_str());
      }
		 FILE* f = fopen(fileName.c_str(), "wb");
		 if (f == NULL)
		 {
			 if (logger != NULL)
				 logger->AddError(false, "error (%d) creating file '%s'", GetLastError(), fileName.c_str());
			 return false;
		 }

		 size_t size = b->Size();
		 size_t cb = fwrite(b->Bytes(), 1, size, f);
		 fclose(f);
		 ret = (cb == size);
		 if (!ret)
		 {
			 if (logger != NULL)
				 logger->AddError(false, "errorno (%d) writing file file '%s'", errno, fileName.c_str());
		 }

#else
		USES_CONVERSION;
		std::wstring fileName(A2W(folder.c_str()));
		fileName += src.at(srcIndex).str->c_str();

		ChangeDivider(&fileName);
		size_t idx = fileName.find_last_of(L"\\");
      if( idx != std::string::npos )
      {
         std::wstring dir = fileName.substr(0, idx);
         int res = SHCreateDirectory(NULL, dir.c_str());
         if( logger != NULL && res != ERROR_SUCCESS && res != ERROR_FILE_EXISTS && res != ERROR_ALREADY_EXISTS )
            logger->AddError(false, "error (%d) creating folder '%s'", res, dir.c_str());
      }
      FILE *f = _wfopen(fileName.c_str(), L"wb");
      if( f == NULL )
      {
         if( logger != NULL )
            logger->AddError(false, "error (%d) creating file '%s'", GetLastError(), fileName.c_str());
         return false;
      }

		size_t size = b->Size();
      size_t cb = fwrite(b->Bytes(), 1, size, f);
      fclose(f);
		ret = (cb == size);
		if (!ret)
		{
			if (logger != NULL)
				logger->AddError(false, "errorno (%d) writing file file '%s'", errno, fileName.c_str());
		}
#endif

   }
   return ret;
}

bool FileField::ReadFile(Object* dest) const
{
#ifdef UNIX
	USES_CONVERSION;
	std::string fileName;

	std::string itemName(W2A(dest->at(srcIndex).str->c_str()));
	while (itemName.size() > 0 && *itemName.rbegin() == L' ')
		itemName.erase(itemName.size() - 1);

	while (itemName.size() > 0 && *itemName.begin() == L' ')
		itemName.erase(0, 1);
	const char* p = itemName.c_str();

	if( *p != '\0' && (*p == '/' || *p == '~') )
       fileName = p;
   else
   {
      fileName = folder;
      fileName += p;
   }

	Member& m = dest->at(meIndex);
	FILE* f = fopen(fileName.c_str(), "rb");
	if (f != NULL)
	{
		DWORD size = (DWORD)_filelength(_fileno(f));
		if (size > 0)
		{
			Binary* b = new Binary();
			BYTE* pb = b->Alloc(size);

			fread(pb, size, 1, f);
			if (m.binary == NULL)
				m.binary = new MemoryBinary();
			m.binary->Assign(b);
		}
		fclose(f);
	}

#else
	USES_CONVERSION;
	std::wstring fileName;

	std::wstring itemName(dest->at(srcIndex).str->c_str());
	while (itemName.size() > 0 && *itemName.rbegin() == L' ')
		itemName.erase(itemName.size() - 1);

	while (itemName.size() > 0 && *itemName.begin() == L' ')
		itemName.erase(0, 1);
	const wchar_t* p = itemName.c_str();

	if( *p != L'\0' && p[1] != L'\0' && ((*p == L'\\' && p[1] == L'\\') || p[1] == L':') )
      fileName = p;
   else
   {
      fileName.assign(A2W(folder.c_str()));
      fileName += p;
   }
	ChangeDivider(&fileName);

	Member& m = dest->at(meIndex);
	FILE* f = _wfopen(fileName.c_str(), L"rb");
	if (f != NULL)
	{
		DWORD size = (DWORD)_filelength(_fileno(f));
		if (size > 0)
		{
			Binary* b = new Binary();
			BYTE* pb = b->Alloc(size);

			fread(pb, size, 1, f);
			if (m.binary == NULL)
				m.binary = new MemoryBinary();
			m.binary->Assign(b);
		}
		fclose(f);
	}

#endif

   return true;
}


ParamHelper::ParamHelper(const ParamHelper* _defaults) : defaults(_defaults)
{
}

ParamHelper::~ParamHelper()
{
	delete defaults;
}

void ParamHelper::Read(const ParamList& parameters, const ISession* session, const ISessionObject* thisObject, IErrorLogger* logger)
{
	ParamList::const_iterator i = parameters.begin();
	for (; i != parameters.end(); i++)
	{
		if (*i->name.begin() == L'$')
		{
			CString *res = NULL;
			if (!session->Parse(&res, i->value, thisObject))
			{
				if (logger != NULL)
				{
					USES_CONVERSION;
					std::string error("Error parse param %s ");
					if (thisObject != NULL)
					{
						error.append("of object ").append(W2A(thisObject->Self()->Name().c_str()));
					}
					logger->AddError(false, error.c_str(), W2A(i->name.c_str()));
				}
				continue;
			}
			else
			{
				params[i->name] = res->c_str();
			}
			delete res;
		}
	}
}

//
// Вычисляем параметр только если выражение содержит переменные $
//
static bool NeedParse(const std::wstring& param)
{
	return (param.find(L'$') != std::wstring::npos);
}

void ParamHelper::Read(const wchar_t* filter, const ISession* session, const ISessionObject* thisObject, IErrorLogger* logger)
{
	int count = 0;
	bool eatLast = (*filter == L'"') || (*filter == '\'');
	const wchar_t* p = (eatLast) ? filter + 1 : filter;
	while (++count) 
	{
		const wchar_t *ep = wcschr(p, L';');
		size_t len = (ep == NULL) ? wcslen(p) - (eatLast ? 1 : 0) : ep - p;
		std::wstring param(p, len);
		if (!param.empty())
		{
			wchar_t numBuf[10];
			wsprintf(numBuf, L"$%02d", count);
			if (!NeedParse(param))
			{
				params[numBuf] = param;
			}
			else
			{
				CString *res = NULL;
				if (!session->Parse(&res, param, thisObject))
				{
					delete res;
					if (logger != NULL)
					{
						std::string error("Error parse param %d ");
						if (thisObject != NULL)
						{
							USES_CONVERSION;
							error.append("of object ").append(W2A(thisObject->Self()->Name().c_str()))
								.append(" with filter ").append(W2A(filter));
						}
						logger->AddError(false, error.c_str(), count);
					}
				}
				else
				{
					params[numBuf] = res->c_str();
					delete res;
				}
			}
		}

		if (ep == NULL)
		{
			break;
		}
		p = ep + 1;
	}
}

CString* ParamHelper::Substitute(const wchar_t* filter, bool checkDollar) const
{
	CString *ret = new CString();

	const wchar_t *p = filter;
	while (true)
	{
		wchar_t sym = *p++;
		if (!checkDollar || sym == L'$')
		{
			std::wstring prmName;
			if (!checkDollar)
				prmName.append(1, L'$');

			prmName.append(1, sym);
			while (true)
			{
				sym = *p++;
				if (sym == L'\0')
					break;
				if (!iswalnum(sym))
				{
					break;
				}
				prmName.append(1, sym);
			}
			std::map<std::wstring, std::wstring>::const_iterator fnd = params.find(prmName);
			if (fnd != params.end())
			{
				ret->append(fnd->second);
			}
			else if (defaults != NULL)
			{
				fnd = defaults->params.find(prmName);
				ret->append((fnd != defaults->params.end()) ? fnd->second : prmName);
			}
			else
			{
				ret->append(prmName);
			}
		}

		if (sym == L'\0')
			break;

		ret->append(sym);
	}

	return ret;
}

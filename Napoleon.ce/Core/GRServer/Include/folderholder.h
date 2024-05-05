/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * Интерфес сервера
 *
 * ert   17/11/2012   creating
 */
#ifndef __FOLDER_HOLDER_H
#define __FOLDER_HOLDER_H

#include <string>
#include <map>

#include <isessobj.h>
#include <mutex_t.h>

namespace GRServer {

#define FOLDER_ID_SERVICE L"FolderIDHolder"
class FolderIDHolder : public std::map<ISession*, std::map<std::wstring, DWORD> >, public ISession::IHandler
{
public:
	FolderIDHolder() { mutex.Init(); }
	~FolderIDHolder() { mutex.Release(); }

   virtual bool Get(ISession *s, DWORD *val, const std::wstring& key) const;

   virtual void SetValue(ISession* s, const Object& o, int keyIndex, int valueIndex);

	virtual void SessionClosed(ISession* s);

	virtual bool ContainsData(ISession* s);

	virtual void Clear(ISession *s);

private:
	Mutex mutex;
};

} // namespace GRServer

#endif

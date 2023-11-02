/*
 * Copyright (C), 2009-2015, ����� �������
 *
 * ���������� ����������
 *
 * ert   09/10/2015   creating
 */
#include "stdafx.h"
#include <string>
#include "srvdata.h"
#include "server.h"
#include "AES.h"

#include "sessobj.h"
#include "session.h"
#include "dispatcher.h"

#include <socket.h>
#include <atlconv.h>

using namespace std;
using namespace GRServer;

//static FILETIME sending, lastConnect;
//
#ifdef DEBUG
	//#define ___RT_TEST 1
#endif
//
//static __int64 CHECK_SERVER_INTERVAL = (__int64)5 * 60 * 10000000;
//
//
//#define HOST_TO_CONNECT "grsoft.ru"
////#define HOST_TO_CONNECT "212.232.41.126"
////#define HOST_TO_CONNECT "127.0.0.1"
//#define HOST_PORT 80
//#define HOST_WAIT_TIMEOUT 1 * 60000
//#define HOST_PAGE "/int_cli_2/exchg.php?"
//
//const char unsafeChars[] = " \"<>%\\^[]`+$,@:;/!#?=&";
//const char hexValues[] = "0123456789ABCDEF";
//static HANDLE hQueryThread = 0;


#define WINDOWS_TICK 10000000
#define SEC_TO_UNIX_EPOCH 11644473600LL

inline size_t StringCount(const std::wstring& str) { return str.size() * sizeof(wchar_t) + sizeof(WORD); }

struct LicenseType
{
	std::wstring type;
	std::wstring title;
	short forAgents;

	DWORD Size() const { return sizeof(forAgents) + (DWORD)StringCount(type) + (DWORD)StringCount(title); }
	BYTE* Put(BYTE* pb) const;
	const BYTE* Set(const BYTE* pb, DWORD cb);
	bool UpdateFrom(const std::map<std::string, std::string>& values);

	class Helper
	{
	public:
		Helper(GRServer::Format* format);
		void ToObject(Object* o, const LicenseType src);
		void ToData(LicenseType* dest, const Object& o);

	private:
		int typeIdx, titleIdx, forAgentsIdx;
	};
};

struct LicenseData
{
	DWORD id;
	std::wstring type;
	DWORD count;
	FILETIME start;
	FILETIME end;

	DWORD Size() const { return (DWORD)(sizeof(id) + sizeof(count) + sizeof(start) + sizeof(end) + StringCount(type)); }
	BYTE* Put(BYTE* pb) const;
	const BYTE* Set(const BYTE* pb, DWORD cb);
	bool UpdateFrom(const std::map<std::string, std::string>& values);

	class Helper
	{
	public:
		Helper(GRServer::Format* format);
		void ToObject(Object* o, const LicenseData src);
		void ToData(LicenseData* dest, const Object& o);

	private:
		int idIdx, typeIdx, countIdx, startIdx, endIdx;
	};
};

struct DemoData
{
	DWORD id;
	std::wstring type;
	DWORD allowCount;
	DWORD timeSpan;

	DWORD Size() const { return (DWORD)(sizeof(id) + sizeof(allowCount) + sizeof(allowCount) + StringCount(type)); }
	BYTE* Put(BYTE* pb) const;
	const BYTE* Set(const BYTE* pb, DWORD cb);

	bool UpdateFrom(const std::map<std::string, std::string>& values);

	class Helper
	{
	public:
		Helper(GRServer::Format* format);
		void ToObject(Object* o, const DemoData src);
		void ToData(DemoData* dest, const Object& o);

	private:
		int idIdx, typeIdx, countIdx, spanIdx;
	};
};

struct LicensingUsers
{
	std::wstring login;
	DWORD licenseID;

	DWORD Size() const { return (DWORD)(sizeof(licenseID) + StringCount(login)); }
	BYTE* Put(BYTE* pb) const;
	const BYTE* Set(const BYTE* pb, DWORD cb);
};

class LicenseDataRT
{
private:
	static const DWORD MAX_LOG_COUNT;
	static const DWORD REQUEST_INTERVAL; // in seconds

	std::map<std::wstring, LicenseType> types;
	std::map<DWORD, LicenseData> license;
	std::map<std::wstring, DemoData> demos;
	std::map<std::wstring, LicensingUsers> users;

	DWORD lastRequest, tempRequest;

	// store unixtime
	mutable std::map<std::wstring, std::vector<DWORD>> log;

	// ������������ ��� ������ ���� �� ������. log ��������� � shadowLog ���� �� - �������, ���� ��� ���������� � ��� �������
	mutable std::map<std::wstring, std::vector<DWORD>> shadowLog;

	LicenseType::Helper *lth;
	LicenseData::Helper *ldh;
	DemoData::Helper * ddh;
	std::map<std::wstring, LicenseType> shadowTypes;
	std::map<DWORD, LicenseData> shadowLicense;
	std::map<std::wstring, DemoData> shadowDemos;

	Binary* Serialize() const;

	//bool UpdateDemoData(ParseStreamA& stream);
	//bool UpdateLicenseData(ParseStreamA& stream);
	//bool UpdateLicenseTypes(ParseStreamA& stream);

	void MoveLog(std::map<std::wstring, std::vector<DWORD>> *dest, std::map<std::wstring, std::vector<DWORD>>* src) const;

public:
	enum Writers {LicenseTypeWR, LicenseDataWR, DemoDataWR};

	LicenseDataRT();
	~LicenseDataRT();

	void CopyUsers(std::vector<LicensingUsers> *users) const;
	void UpdateUsers(const std::vector<LicensingUsers> &users);

	void LoadLicenseTypes(ISessionObject * dest) const;
	void LoadLicenseData(ISessionObject * dest) const;
	void LoadDemoData(ISessionObject * idest) const;

	bool Update(const Binary& b);
	bool WriteData() const;
	//bool Update(const char* serverResp, const char*ep);

	bool IsKnownType(const std::wstring& licType) const;

	bool IsLicensed(const std::wstring& login, const std::wstring& licType, const FILETIME &curTime) const;
	const DemoData& GetDemoData(const std::wstring& type) const;

	void AddToLog(const std::wstring& login, const FILETIME& ft);

	void GetLog(CString* out);
	void CommitLog(bool commit);
	bool CanSendRequest();

	bool PrepareWrite(Writers w, GRServer::Format* format);
	bool Write(Writers w, const Object& o);
	void CommitWrite(Writers w);
};
static LicenseDataRT rtData;

struct ActivityData
{
	FILETIME date;
	bool success;
};

class FakeReader : public IDataSource::IReader
{
public:
	virtual void Remove() {}
	virtual void Close() {}
	virtual bool MoveNext(Object *parentObject) { return false; }
	virtual bool Get(Object* o) const { return false; }
	virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
	virtual const Member* Value(const wchar_t* name) const { return NULL; }
};

class LiceseTypeDataWriter : public IDataSource::IWriter
{
public:
	LiceseTypeDataWriter(LicenseDataRT::Writers type) : dispatcher(NULL){ this->type = type; }

	virtual bool Prepare(const ISessionObject& object) 
	{
		if (type == LicenseDataRT::DemoDataWR)
			dispatcher = ((Session&)object.GetSession()).GetDispatcher();
		ServObject *so = object.Self();
		return rtData.PrepareWrite(type, so->format); 
	}
	
	virtual bool Write(const Object& o, RowID *rid) { return rtData.Write(type, o); }
	
	virtual void Close() 
	{ 
		rtData.CommitWrite(type); 
		// if (dispatcher != NULL)
		// 	dispatcher->LicenseRequestDone(true);
	}

private:
	LicenseDataRT::Writers type;
	Dispatcher* dispatcher;
};

class LicenseTypeSource : public IDataSource::ICreator
{
public:
	virtual const wchar_t* Name() const { return L"LicenseTypeSource"; }
	virtual LicenseDataRT::Writers WriterType() const { return LicenseDataRT::LicenseTypeWR; }

	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
	{
		Session &session = (Session&)iobject.GetSession();

		const SessionObject* so;
		if ((so = session.FindObject(L"LicenseType", NULL)) == NULL || so->size() == 0)
			rtData.LoadLicenseTypes(session.GetObject(L"LicenseType", NULL));
		if ((so = session.FindObject(L"LicenseProjectData", NULL)) == NULL || so->size() == 0)
			rtData.LoadLicenseData(session.GetObject(L"LicenseProjectData", NULL));
		if ((so = session.FindObject(L"DemoProjectData", NULL)) == NULL || so->size() == 0)
			rtData.LoadDemoData(session.GetObject(L"DemoProjectData", NULL));
		return new FakeReader();
	}

	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
	{
		return new LiceseTypeDataWriter(WriterType());
	}
};

class LicenseProjectDataSource : public LicenseTypeSource
{
public:
	virtual LicenseDataRT::Writers WriterType() const { return LicenseDataRT::LicenseDataWR; }
	virtual const wchar_t* Name() const { return L"LicenseProjectDataSource"; }
};

class DemoProjectDataSource : public LicenseTypeSource
{
public:
	virtual LicenseDataRT::Writers WriterType() const { return LicenseDataRT::DemoDataWR; }
	virtual const wchar_t* Name() const { return L"DemoProjectDataSource"; }
};

class LicensingUsersDataSource : public IDataSource::ICreator
{
public:
	virtual const wchar_t* Name() const { return L"LicensingUsersDataSource"; }

	class Reader : public IDataSource::IReader
	{
	public:
		Reader(const SessionObject& object);
		virtual ~Reader() { }

		virtual void Remove() {}
		virtual void Close() {}
		virtual bool MoveNext(Object *parentObject) { return current != users.end(); }
		virtual bool Get(Object* o) const;
		virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
		virtual const Member* Value(const wchar_t* name) const { return NULL; }

	protected:
		std::vector<LicensingUsers> users;
		mutable std::vector<LicensingUsers>::const_iterator current;
		int loginIdx, licenseIDIdx;
	};

	class Writer : public IDataSource::IWriter
	{
	public:
		Writer() {}

		virtual bool Prepare(const ISessionObject& object);
		virtual bool Write(const Object& o, RowID *rid);
		virtual void Close()
		{
			if (loginIdx >= 0 && licenseIDIdx >= 0)
				rtData.UpdateUsers(users);
		}

	protected:
		std::vector<LicensingUsers> users;
		int loginIdx, licenseIDIdx;
	};

	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
	{
		const SessionObject& object = *(const SessionObject*)iobject.Self();
		return new Reader(object);
	}
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
	{
		return new Writer();
	}
};

const DWORD UserActivityHolder::SESSION_ALIVE = 30 * 60;
const DWORD LicenseDataRT::REQUEST_INTERVAL = 10 * 60; // 10 min in seconds
const DWORD LicenseDataRT::MAX_LOG_COUNT = 1000;

static DemoData emptyDD = { 0, L"", 10, 10 };

static std::map<std::wstring, std::vector<ActivityData>> activity;

static Mutex mutex;
// static HANDLE mutex;
// static bool mutexCreated = false;

inline DWORD FileTimeToUnixTime(const FILETIME& ft)
{
	__int64 ct = *((__int64*)&ft);
	return (DWORD)(ct / WINDOWS_TICK - SEC_TO_UNIX_EPOCH);
}

static bool WaitMutex()
{
	mutex.Init();
	return mutex.Acquire(10000);

// 	if( !mutexCreated )
// 	{
// 		mutexCreated = true;
// 		mutex = CreateMutex(NULL, FALSE, NULL);
// 	}

//    if( WaitForSingleObject(mutex, 10000) == WAIT_OBJECT_0 )
// 		return true;

// 	return false;
}

LicenseType::Helper::Helper(GRServer::Format* format)
{
	typeIdx = format->FindMember(L"type");
	titleIdx = format->FindMember(L"title");
	forAgentsIdx = format->FindMember(L"forAgents");
}

void LicenseType::Helper::ToObject(Object* o, const LicenseType src)
{
	o->at(typeIdx).str->assign(src.type);
	o->at(forAgentsIdx).number = src.forAgents;
	o->at(titleIdx).str->assign(src.title);
}


void LicenseType::Helper::ToData(LicenseType* dest, const Object& o)
{
	dest->type.assign((const std::wstring&)*(o.at(typeIdx).str));
	dest->forAgents = (short)o.at(forAgentsIdx).number;
	dest->title.assign((const std::wstring&)*(o.at(titleIdx).str));
}

LicenseData::Helper::Helper(GRServer::Format* format)
{
	idIdx = format->FindMember(L"id");
	typeIdx = format->FindMember(L"type");
	countIdx = format->FindMember(L"count");
	startIdx = format->FindMember(L"start");
	endIdx = format->FindMember(L"end");
}

void LicenseData::Helper::ToObject(Object* o, const LicenseData src)
{
	o->at(typeIdx).str->assign(src.type);
	o->at(idIdx).number = src.id;
	o->at(countIdx).number = src.count;
	o->at(startIdx).datetime = src.start;
	o->at(endIdx).datetime = src.end;
}

void LicenseData::Helper::ToData(LicenseData* dest, const Object& o)
{
	dest->type.assign((const std::wstring&)*o.at(typeIdx).str);
	dest->id = (DWORD)o.at(idIdx).number;
	dest->count = (DWORD)o.at(countIdx).number;
	dest->start = o.at(startIdx).datetime;
	dest->end = o.at(endIdx).datetime;
}

DemoData::Helper::Helper(GRServer::Format* format)
{
	idIdx = format->FindMember(L"id");
	typeIdx = format->FindMember(L"type");
	countIdx = format->FindMember(L"allowCount");
	spanIdx = format->FindMember(L"timeSpan");
}

void DemoData::Helper::ToObject(Object* o, const DemoData src)
{
	o->at(typeIdx).str->assign(src.type);
	o->at(idIdx).number = src.id;
	o->at(countIdx).number = src.allowCount;
	o->at(spanIdx).number = src.timeSpan;
}

void DemoData::Helper::ToData(DemoData* dest, const Object& o)
{
	dest->type.assign((const std::wstring&)*o.at(typeIdx).str);
	dest->id = (DWORD)o.at(idIdx).number;
	dest->allowCount = (DWORD)o.at(countIdx).number;
	dest->timeSpan = (DWORD)o.at(spanIdx).number;
}


static void PutActivity(const std::wstring& login, const FILETIME& ft, bool success)
{
	if( !WaitMutex() )
		return;

	if (success)
	{
		rtData.AddToLog(login, ft);
	}

	ActivityData ad;
	ad.date = ft;
	ad.success = success;

	activity[login].push_back(ad);

#ifdef ___RT_TEST
	USES_CONVERSION;
	gServer->AddLog("Put Activity: %s %s", W2A_CP(login.c_str(), CP_UTF8), ((success)? "1" : "0"));
#endif

	mutex.Release();
	// ReleaseMutex(mutex);
}

static BYTE* PutString(BYTE *pb, const std::wstring& str)
{
	WORD cb = (WORD)(str.size() * sizeof(wchar_t));
	*(WORD*)pb = cb;
	pb += sizeof(WORD);
	memcpy(pb, str.c_str(), cb);

	return pb + cb;
}

static const BYTE* GetString(const BYTE *pb, std::wstring* str, const BYTE *ep)
{
	WORD cb = *(WORD*)pb;
	pb += sizeof(WORD);
	if( pb + cb <= ep )
		str->assign((const wchar_t*)pb, cb / sizeof(wchar_t));
	return pb + cb;
}

BYTE* LicenseType::Put(BYTE* pb) const
{
	*(short*)pb = forAgents;
	pb = PutString(pb + sizeof(short), type);
	return PutString(pb, title);
}

const BYTE* LicenseType::Set(const BYTE* pb, DWORD cb)
{
	forAgents = *(const short*)pb;
	pb = GetString(pb + sizeof(short), &type, pb + cb);
	return GetString(pb, &title, pb + cb - StringCount(type));
}

static void SetString(std::wstring* ret, const std::string& _src)
{
	if( _src.empty() )
	{
		ret->clear();
		return;
	}

	const char *p = _src.c_str();

	USES_CONVERSION;
	if( _src.find("\\u") == std::string::npos )
	{
		ret->assign(A2W(p));
		return;
	}

	char abuf[5], *ep;
	wchar_t* buf = (wchar_t*)alloca((_src.size() + 1) * sizeof(wchar_t));
	wchar_t *dp = buf;
	while( *p != 0 )
	{
		if( *p == '\\' && p[1] == 'u')
		{
			memcpy(abuf, p + 2, 4);
			abuf[4] = 0;

			*dp++ = (wchar_t)strtol(abuf, &ep, 16);
			p += 6;
		} else {
			*abuf = *p++;
			abuf[1] = '\0';
			*dp++ = *A2W_CP(abuf, CP_UTF8);
		}
	}
	*dp = L'\0';

   ret->assign(buf);	
}

bool LicenseType::UpdateFrom(const std::map<std::string, std::string>& values)
{
	std::map<std::string, std::string>::const_iterator i = values.begin();
	for( ; i != values.end() ; i++ )
	{
		if( i->first.compare("forAgents")  == 0 )
		{
			forAgents = (short)atoi(i->second.c_str());
		} else if( i->first.compare("type") == 0 )
		{
			SetString(&type, i->second);
		} else if( i->first.compare("title") == 0 )
		{
			SetString(&title, i->second);
		}
	}
	return true;
}

BYTE* LicenseData::Put(BYTE* pb) const
{
	*(DWORD*)pb = id;
	pb += sizeof(DWORD);
	*(DWORD*)pb = count;
	pb += sizeof(DWORD);
	*(FILETIME*)pb = start;
	pb += sizeof(FILETIME);
	*(FILETIME*)pb = end;
	pb += sizeof(FILETIME);

	return PutString(pb, type);
}

const BYTE* LicenseData::Set(const BYTE* pb, DWORD cb)
{
	const BYTE* ep = pb + cb;
	
	id = *(const DWORD*)pb;
	pb += sizeof(DWORD);
	count = *(const DWORD*)pb;
	pb += sizeof(DWORD);
	start = *(const FILETIME*)pb;
	pb += sizeof(FILETIME);
	end = *(const FILETIME*)pb;
	pb += sizeof(FILETIME);

	return GetString(pb, &type, ep);
}

bool LicenseData::UpdateFrom(const std::map<std::string, std::string>& values)
{
	std::map<std::string, std::string>::const_iterator i = values.begin();
	for( ; i != values.end() ; i++ )
	{
		if( i->first.compare("id")  == 0 )
		{
			id = (DWORD)atoi(i->second.c_str());
		} else if( i->first.compare("type") == 0 )
		{
			SetString(&type, i->second);
		} else if( i->first.compare("count") == 0 )
		{
			count = (DWORD)atoi(i->second.c_str());
		} else if( i->first.compare("start") == 0 )
		{
			*(__int64*)&start = ((__int64)strtoul(i->second.c_str(), NULL, 10) + SEC_TO_UNIX_EPOCH) * WINDOWS_TICK;
		} else if( i->first.compare("end") == 0 )
		{
			*(__int64*)&end = ((__int64)strtoul(i->second.c_str(), NULL, 10) + SEC_TO_UNIX_EPOCH) * WINDOWS_TICK;
		}
	}
	return true;
}

BYTE* DemoData::Put(BYTE* pb) const
{
	*(DWORD*)pb = id;
	pb += sizeof(DWORD);
	*(DWORD*)pb = allowCount;
	pb += sizeof(DWORD);
	*(DWORD*)pb = timeSpan;
	pb += sizeof(DWORD);

	return PutString(pb, type);
}

const BYTE* DemoData::Set(const BYTE* pb, DWORD cb)
{
	const BYTE* ep = pb + cb;
	
	id = *(const DWORD*)pb;
	pb += sizeof(DWORD);
	allowCount = *(const DWORD*)pb;
	pb += sizeof(DWORD);
	timeSpan = *(const DWORD*)pb;
	pb += sizeof(DWORD);

	return GetString(pb, &type, ep);
}

bool DemoData::UpdateFrom(const std::map<std::string, std::string>& values)
{
	std::map<std::string, std::string>::const_iterator i = values.begin();
	for( ; i != values.end() ; i++ )
	{
		if( i->first.compare("id")  == 0 )
		{
			id = (DWORD)atoi(i->second.c_str());
		} else if( i->first.compare("type") == 0 )
		{
			SetString(&type, i->second);
		} else if( i->first.compare("allowCount") == 0 )
		{
			allowCount = (DWORD)atoi(i->second.c_str());
		} else if( i->first.compare("timespan") == 0 )
		{
			timeSpan = (DWORD)atoi(i->second.c_str());
		}
	}
	return true;
}

BYTE* LicensingUsers::Put(BYTE* pb) const
{
	*(DWORD*)pb = licenseID;
	pb += sizeof(DWORD);

	return PutString(pb, login);
}

const BYTE* LicensingUsers::Set(const BYTE* pb, DWORD cb)
{
	const BYTE* ep = pb + cb;
	
	licenseID = *(const DWORD*)pb;
	pb += sizeof(DWORD);

	return GetString(pb, &login, ep);
}

LicenseDataRT::LicenseDataRT()
{
	lastRequest = 0;
	tempRequest = 0;

	lth = NULL;
	ldh = NULL;
	ddh = NULL;

	//LicenseType lt;
	//lt.forAgents = 0;
	//lt.title = L"��������� ��������";
	//lt.type = L"managerPDA";

	//types[lt.type] = lt;
}

LicenseDataRT::~LicenseDataRT()
{
	delete lth;
	delete ldh;
	delete ddh;
}

bool LicenseDataRT::Update(const Binary& b)
{
	const BYTE* pb = (const BYTE*)b;
	const BYTE* ep = pb + b.Size();

	types.clear();
	WORD count = *(WORD*)pb;
	pb += sizeof(WORD);
	for (; pb < ep && count > 0; count--)
	{
		LicenseType lt;
		pb = lt.Set(pb, (DWORD)(ep - pb));
		if( pb <= ep )
			types[lt.type] = lt;
	}
	if( pb >= ep )
		return false;

	license.clear();
	count = *(WORD*)pb;
	pb += sizeof(WORD);
	for (; pb < ep && count > 0; count--)
	{
		LicenseData lt;
		pb = lt.Set(pb, (DWORD)(ep - pb));
		if( pb <= ep )
			license[lt.id] = lt;
	}
	if( pb >= ep )
		return false;
	
	demos.clear();
	count = *(WORD*)pb;
	pb += sizeof(WORD);
	for (; pb < ep && count > 0; count--)
	{
		DemoData lt;
		pb = lt.Set(pb, (DWORD)(ep - pb));
		if (pb <= ep)
		{
			demos[lt.type] = lt;
#ifdef ___RT_TEST
			USES_CONVERSION;
			gServer->AddLog("___RT_TEST Load Demo lic '%s' allow %d, timespan %d ", W2A_CP(lt.type.c_str(), CP_UTF8), lt.allowCount, lt.timeSpan);
#endif
		}
	}
	if( pb >= ep )
		return false;

	users.clear();
	count = *(WORD*)pb;
	pb += sizeof(WORD);
	for (; pb < ep && count > 0; count--)
	{
		LicensingUsers lt;
		pb = lt.Set(pb, (DWORD)(ep - pb));
		if( pb <= ep )
			users[lt.login] = lt;
	}

	if (count != 0)
		return false;

	if (pb >= ep)
		return true;

	log.clear();
	count = *(WORD*)pb;
	pb += sizeof(WORD);
	for (; pb < ep && count > 0; count--)
	{
		std::wstring buf;
		pb = GetString(pb, &buf, ep);
		if (pb >= ep)
			return false;
		std::vector<DWORD>& vec = log[buf];
		WORD countj = *(WORD*)pb;
		pb += sizeof(WORD);
		for (; pb < ep && countj > 0; countj--)
		{
			DWORD val = *(DWORD*)pb;
			pb += sizeof(DWORD);
			vec.push_back(val);
		}
	}

#ifdef ___RT_TEST
	gServer->AddLog("___RT_TEST Load log %d", log.size());
#endif

	return (count == 0);
}

Binary* LicenseDataRT::Serialize() const
{
	// count bytest
	size_t cb = 0;
	
	cb += sizeof(WORD);
	std::map<std::wstring, LicenseType>::const_iterator ti = types.begin();
	for( ; ti != types.end(); ti++ )
		cb += ti->second.Size();

	cb += sizeof(WORD);
	std::map<DWORD, LicenseData>::const_iterator li = license.begin();
	for( ; li != license.end(); li++ )
		cb += li->second.Size();

	cb += sizeof(WORD);
	std::map<std::wstring, DemoData>::const_iterator di = demos.begin();
	for( ; di != demos.end(); di++ )
		cb += di->second.Size();

	cb += sizeof(WORD);
	std::map<std::wstring, LicensingUsers>::const_iterator ui = users.begin();
	for( ; ui != users.end(); ui++ )
		cb += ui->second.Size();

	MoveLog(&log, &shadowLog);
	cb += sizeof(WORD);
	std::map<std::wstring, std::vector<DWORD>>::const_iterator logi = log.begin();
	for (; logi != log.end(); logi++)
	{
		if (logi->second.size() > 0)
			cb += StringCount(logi->first) + sizeof(WORD) + logi->second.size() * sizeof(DWORD);
	}

#ifdef ___RT_TEST
	gServer->AddLog("___RT_TEST Save log %d", log.size());
#endif

	// write
	Binary *ret = new Binary();
	BYTE *pb = ret->Alloc((DWORD)cb);

	*(WORD*)pb = (WORD)types.size();
	pb += sizeof(WORD);
	ti = types.begin();
	for( ; ti != types.end(); ti++ )
		pb = ti->second.Put(pb);

	*(WORD*)pb = (WORD)license.size();
	pb += sizeof(WORD);
	li = license.begin();
	for( ; li != license.end(); li++ )
		pb = li->second.Put(pb);

	*(WORD*)pb = (WORD)demos.size();
	pb += sizeof(WORD);
	di = demos.begin();
	for( ; di != demos.end(); di++ )
		pb = di->second.Put(pb);

	*(WORD*)pb = (WORD)users.size();
	pb += sizeof(WORD);
	ui = users.begin();
	for( ; ui != users.end(); ui++ )
		pb = ui->second.Put(pb);

	*(WORD*)pb = (WORD)log.size();
	pb += sizeof(WORD);
	logi = log.begin();
	for (; logi != log.end(); logi++)
	{
		if (logi->second.size() > 0)
		{
			pb = PutString(pb, logi->first);
			*(WORD*)pb = (WORD)logi->second.size();
			pb += sizeof(WORD);
			std::vector<DWORD>::const_iterator logj = logi->second.begin();
			for (; logj != logi->second.end(); logj++)
			{
				*(DWORD*)pb = *logj;
				pb += sizeof(DWORD);
			}
		}
	}
	return ret;
}

void LicenseDataRT::MoveLog(std::map<std::wstring, std::vector<DWORD>> *dest, std::map<std::wstring, std::vector<DWORD>>* src) const
{
	if (!WaitMutex())
		return;

	std::map<std::wstring, std::vector<DWORD>>::iterator i = src->begin();
	for (; i != src->end(); i++)
	{
		std::vector<DWORD>& vec = (*dest)[i->first];
		std::vector<DWORD>::const_iterator j = i->second.begin();
		for (; j != i->second.end(); j++)
			vec.push_back(*j);
	}

	src->clear();

	mutex.Release();
	// ReleaseMutex(mutex);
}


void LicenseDataRT::AddToLog(const std::wstring& login, const FILETIME& ft)
{
	DWORD unixVal = FileTimeToUnixTime(ft);
	std::vector<DWORD>& vec = log[login];
	while (vec.size() > MAX_LOG_COUNT)
		vec.erase(vec.begin());
	vec.push_back(unixVal);
}

bool LicenseDataRT::PrepareWrite(Writers w, GRServer::Format* format)
{
	switch (w)
	{
	case LicenseDataRT::LicenseTypeWR:
	{
		if (lth == NULL)
			lth = new LicenseType::Helper(format);
		break;
	}
	case LicenseDataRT::LicenseDataWR:
	{
		if (ldh == NULL)
			ldh = new LicenseData::Helper(format);
		break;
	}
	case LicenseDataRT::DemoDataWR:
	{
		if (ddh == NULL)
			ddh = new DemoData::Helper(format);
		break;
	}
	}
	return true;
}

bool LicenseDataRT::Write(Writers w, const Object& o)
{
	try 
	{
		switch (w)
		{
		case LicenseDataRT::LicenseTypeWR:
		{
			LicenseType lt;
			lth->ToData(&lt, o);
			shadowTypes[lt.type] = lt;
			break;
		}
		case LicenseDataRT::LicenseDataWR:
		{
			LicenseData ld;
			ldh->ToData(&ld, o);
			shadowLicense[ld.id] = ld;
			break;
		}
		case LicenseDataRT::DemoDataWR:
		{
			DemoData dd;
			ddh->ToData(&dd, o);
			shadowDemos[dd.type] = dd;
#ifdef ___RT_TEST
			USES_CONVERSION;
			gServer->AddLog("___RT_TEST Receive Demo lic '%s' allow %d, timespan %d ", W2A_CP(dd.type.c_str(), CP_UTF8), dd.allowCount, dd.timeSpan);
#endif
			break;
		}
		}
	}
	catch (...)
	{
	}
	return true;
}

void LicenseDataRT::CommitWrite(Writers w)
{
	size_t count = 0;

	switch (w)
	{
	case LicenseDataRT::LicenseTypeWR:
		types = shadowTypes;
		count = types.size();
		shadowTypes.clear();
		delete lth;
		lth = NULL;
		break;
	case LicenseDataRT::LicenseDataWR:
		license = shadowLicense;
		count = license.size();
		shadowLicense.clear();
		delete ldh;
		ldh = NULL;
		break;
	case LicenseDataRT::DemoDataWR:
		demos = shadowDemos;
		count = demos.size();
		shadowDemos.clear();
		delete ddh;
		ddh = NULL;
		break;
	default:
		break;
	}

#ifdef ___RT_TEST
	gServer->AddLog("___RT_TEST Commit License writer %d size %d", (DWORD)w, count);
#endif
}

static void EscapeString(std::wstring* out, const std::wstring& src)
{
	size_t pos = 0;
	while (true)
	{
		size_t ep = src.find_first_of(L"\\[", pos);
		if (ep == std::wstring::npos)
		{
			out->append(src.substr(pos));
			break;
		}
		out->append(src.substr(pos, ep - pos));
		out->append(1, L'\\');
		out->append(1, src.at(ep));
		pos = ep + 1;
	}
}

void LicenseDataRT::GetLog(CString* out)
{
	MoveLog(&shadowLog, &log);

	std::map<std::wstring, std::vector<DWORD>>::const_iterator i = shadowLog.begin();
	for (; i != shadowLog.end(); i++)
	{
		std::wstring login;
		EscapeString(&login, i->first);
		out->append(login).append(L'[');
		std::vector<DWORD>::const_iterator vi = i->second.begin();
		for (; vi != i->second.end(); vi++)
		{
			wchar_t buf[10];
			if (vi != i->second.begin())
				out->append(L',');
			wsprintf(buf, L"%X", (*vi));
			out->append(buf);
		}
		out->append(L']');
	}

#ifdef ___RT_TEST
	USES_CONVERSION;
	gServer->AddLog("___RT_TEST Put manager log '%s'", W2A_CP(out->c_str(), CP_UTF8));
#endif
}

bool LicenseDataRT::CanSendRequest()
{
	SYSTEMTIME st;
	FILETIME ft;

	GetLocalTime(&st);
	SystemTimeToFileTime(&st, &ft);

	DWORD curTime = FileTimeToUnixTime(ft);

#ifdef ___RT_TEST
	gServer->AddLog("___RT_TEST Test req send curTime %d, lastReq %d", curTime, lastRequest);
#endif

	if (curTime > lastRequest && (curTime - lastRequest) > REQUEST_INTERVAL)
	{
		tempRequest = lastRequest;
		lastRequest = curTime;

#ifdef ___RT_TEST
		USES_CONVERSION;
		gServer->AddLog("___RT_TEST Test req send - allow %s", W2A_CP(PROJECT_NAME, CP_UTF8));
#endif
		return true;
	}
	return false;
}

void LicenseDataRT::CommitLog(bool commit)
{
#ifdef ___RT_TEST
	gServer->AddLog("___RT_TEST Commit log %s", commit ? "true" : "false");
#endif

	if (!commit)
	{
		MoveLog(&log, &shadowLog);
		lastRequest = tempRequest;
	}
	else
	{
		tempRequest = lastRequest;
	}
	shadowLog.clear();
}

bool LicenseDataRT::IsKnownType(const std::wstring& licType) const
{
	//if (types.size() == 0)
	//{
	//	if (hQueryThread == NULL)
	//		hQueryThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoQueryServer, (LPVOID)1, 0, NULL);
	//}
	return types.find(licType) != types.end();
}

bool LicenseDataRT::IsLicensed(const std::wstring& login, const std::wstring& licType, const FILETIME &curTime) const
{
	std::map<std::wstring, LicensingUsers>::const_iterator fnd = users.find(login);
	if(fnd == users.end())
		return false;

	std::map<DWORD, LicenseData>::const_iterator lfnd = license.find(fnd->second.licenseID);
	if( lfnd == license.end() )
		return false;

	const LicenseData &ld = lfnd->second;
	return (ld.type.compare(licType) == 0 && CompareFileTime(&ld.start, &curTime) <= 0 && CompareFileTime(&ld.end, &curTime) >= 0);
}

const DemoData& LicenseDataRT::GetDemoData(const std::wstring& type) const
{
	std::map<std::wstring, DemoData>::const_iterator fnd = demos.find(type);
	if( (fnd != demos.end()) )
		fnd->second;
	fnd = demos.find(L"");
	return (fnd != demos.end()) ? fnd->second : emptyDD;
}

bool LicenseDataRT::WriteData() const
{
	bool ret = false;
	Binary *b = Serialize();
	if( b )
	{
		ret = ServerData::AddServerData(*b, ServerLicenseData);
		delete b;
	}

	return ret;
}

void LicenseDataRT::LoadLicenseTypes(ISessionObject * idest) const
{
	if( idest == NULL )
		return;

	ServObject* dest = idest->Self();
	GRServer::Format *format = dest->format;
	LicenseType::Helper h(format);

	std::map<std::wstring, LicenseType>::const_iterator i = types.begin();
	for( ; i!= types.end(); i++ )
	{
		Object *o = dest->AddObject();
		h.ToObject(o, i->second);
	}
}

void LicenseDataRT::LoadLicenseData(ISessionObject * idest) const
{
	if( idest == NULL )
		return;

	ServObject* dest = idest->Self();
	GRServer::Format *format = dest->format;
	LicenseData::Helper h(format);

	std::map<DWORD, LicenseData>::const_iterator i = license.begin();
	for( ; i!= license.end(); i++ )
	{
		Object *o = dest->AddObject();
		h.ToObject(o, i->second);
	}
}

void LicenseDataRT::LoadDemoData(ISessionObject * idest) const
{
	if (idest == NULL)
		return;

	ServObject* dest = idest->Self();
	GRServer::Format *format = dest->format;
	DemoData::Helper h(format);

	std::map<std::wstring, DemoData>::const_iterator i = demos.begin();
	for (; i != demos.end(); i++)
	{
		Object *o = dest->AddObject();
		h.ToObject(o, i->second);
	}
}


void LicenseDataRT::CopyUsers(std::vector<LicensingUsers> *dest) const
{
	std::map<std::wstring, LicensingUsers>::const_iterator i = users.begin();
	for( ; i != users.end(); i++ )
		dest->push_back(i->second);

	WriteData();
}

void LicenseDataRT::UpdateUsers(const std::vector<LicensingUsers> &src)
{
	std::vector<LicensingUsers>::const_iterator i = src.begin();
	users.clear();
	for( ; i != src.end(); i++ )
		users[i->login] = *i;

	WriteData();
}

bool UserActivityHolder::CanCheck(const std::wstring& licType)
{
	return rtData.IsKnownType(licType);
}

DWORD UserActivityHolder::SessionTime(const std::wstring& login)
{
	return SESSION_ALIVE;
}

bool UserActivityHolder::CanSendRequest(CString *mgrLog)
{
	if (rtData.CanSendRequest())
	{
		rtData.GetLog(mgrLog);
		return true;
	}
	return false;
}

void UserActivityHolder::CommitRequest(bool commit)
{
	rtData.CommitLog(commit);
}

bool UserActivityHolder::IsGranted(std::wstring* errMsg, const std::wstring& login, const std::wstring& licType, bool isManager)
{
	bool granted = false;
	FILETIME ft;
	SYSTEMTIME st;
	GetLocalTime(&st);
	SystemTimeToFileTime(&st, &ft);

	granted = rtData.IsLicensed(login, licType, ft);
	if( !granted )
	{
		const DemoData& dd = rtData.GetDemoData(licType);
		__int64 check = *(__int64*)&ft;
		check -= (__int64)dd.timeSpan * WINDOWS_TICK;

		const std::vector<ActivityData> &vect = activity[login];
		std::vector<ActivityData>::const_reverse_iterator ri = vect.rbegin();

		DWORD cc = dd.allowCount;
		__int64 lastSuccessConnect = 0;
		while( cc > 0 && ri != vect.rend() )
		{
			if( ri->success )
			{
				__int64 current = *((__int64*)&(ri->date));
				if( current < check )
					break;

				lastSuccessConnect = current;
				cc--;
			}

			ri++;
		}

#ifdef ___RT_TEST
		USES_CONVERSION;
		gServer->AddLog("RT_TEST login %s type %s, allow %d, ts %d, cc %d", W2A_CP(login.c_str(), CP_UTF8), 
			W2A_CP(dd.type.c_str(), CP_UTF8), dd.allowCount, dd.timeSpan, cc);
#endif


		if( cc == 0 )
		{
			if( dd.allowCount == 0 )
			{
				errMsg->assign(L"Can't connect in demo mode");
			} else
			{
				wchar_t timeBuf[200];

				__int64 span = lastSuccessConnect - check;
				span /= WINDOWS_TICK;

				errMsg->assign(L"You can connect in ");
				if( span > 3600 )
				{
					DWORD h = (DWORD)span / 3600;
					span %= 3600;
					
					wsprintf(timeBuf, L" %d h", h);
					errMsg->append(timeBuf);
				}

				if( span > 60 )
				{
					DWORD m = (DWORD)span / 60;
					span %= 60;

					wsprintf(timeBuf, L" %d min", m);
					errMsg->append(timeBuf);
				}

				if( span == 0 )
					wcscpy(timeBuf, L" second");
				else
					wsprintf(timeBuf, L" %d sec", (DWORD)span);
				errMsg->append(timeBuf);
			}
		}

		granted = cc > 0;
	}

	PutActivity(login, ft, granted);
	return granted;
}

LicensingUsersDataSource::Reader::Reader(const GRServer::SessionObject &object)
{
	loginIdx = object.format->FindMember(L"login");
	licenseIDIdx = object.format->FindMember(L"licenseID");

	rtData.CopyUsers(&users);
	current = users.begin();
}

bool LicensingUsersDataSource::Reader::Get(Object* o) const
{
	if(loginIdx < 0 || licenseIDIdx < 0)
		return false;

	o->at(loginIdx).str->assign(current->login);
	o->at(licenseIDIdx).number = current->licenseID;

	current++;
	return true;
}

bool LicensingUsersDataSource::Writer::Prepare(const ISessionObject& iobject)
{
	const GRServer::Format* format = ((const SessionObject*)iobject.Self())->format;;
   loginIdx = format->FindMember(L"login");
   licenseIDIdx = format->FindMember(L"licenseID");
   return (loginIdx >= 0 && licenseIDIdx >= 0);
}

bool LicensingUsersDataSource::Writer::Write(const Object& o, RowID *rid)
{
	LicensingUsers lu;
	lu.login = (const std::wstring&)*o.at(loginIdx).str;
	lu.licenseID = (DWORD)(o.at(licenseIDIdx).number + 0.0005);

	users.push_back(lu);
	return true;
}

void UserActivityHolder::Close()
{
	rtData.WriteData();

	//if( hQueryThread == 0 )
	//	hQueryThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoQueryServer, NULL, 0, NULL);
	//
	//WaitForSingleObject(hQueryThread, 10*1000);
}

bool UserActivityHolder::Init()
{
	Binary* b = ServerData::GetServerData(ServerLicenseData);
	if( b != NULL )
	{
		rtData.Update(*b);
		delete b;
	}

	DataSource::AddCreator(new LicensingUsersDataSource());
	DataSource::AddCreator(new LicenseTypeSource());
	DataSource::AddCreator(new LicenseProjectDataSource());
	DataSource::AddCreator(new DemoProjectDataSource());

	return true;
}

//static bool MoveTo(ParseStreamA& stream, char sym)
//{
//	while( stream.Current() != sym && stream.MoveNext() )
//		;
//
//	return !stream.EOS();
//}
//
//static bool FillData(std::map<std::string, std::string> *values, ParseStreamA& stream)
//{
//	bool ret = true;
//
//	while( !stream.EOS() )
//	{
//		std::string key, value;
//		if( !MoveTo(stream, '"') || !stream.MoveNext() )
//		{
//			ret = false;
//			break;
//		}
//		stream.CopyUntill(&key, '"');
//
//		if( (!stream.MoveNext() && stream.Current() != ':')  || !MoveTo(stream, '"') || !stream.MoveNext() )
//		{
//			ret = false;
//			break;
//		}
//		stream.CopyUntill(&value, '"');
//		values->insert(std::map<std::string, std::string> ::value_type(key, value));
//		
//		stream.MoveNext();
//		if( stream.Current() != ',' )
//			break;
//	}
//
//	return ret;
//}

//bool LicenseDataRT::UpdateDemoData(ParseStreamA& stream)
//{
//	bool ret = true;
//
//	std::map<std::wstring, DemoData> tdata;
//	while( !stream.EOS() )
//	{
//		std::map<std::string, std::string> values;
//		if( !FillData(&values, stream) || stream.Current() != '}' )
//		{
//			ret = false;
//			break;
//		}
//
//		DemoData data;
//		if( !data.UpdateFrom(values) )
//		{
//			ret = false;
//			break;
//		}
//
//		if( tdata.find(data.type) == tdata.end() )
//			tdata[data.type] = data;
//
//		stream.MoveNext();
//		if( stream.Current() == ']' )
//			break;
//	}
//
//	if( ret )
//		demos = tdata;
//	return ret;
//}
//
//bool LicenseDataRT::UpdateLicenseData(ParseStreamA& stream)
//{
//	bool ret = true;
//
//	std::map<DWORD, LicenseData> tdata;
//	while( !stream.EOS() )
//	{
//		std::map<std::string, std::string> values;
//		if( !FillData(&values, stream) || stream.Current() != '}' )
//		{
//			ret = false;
//			break;
//		}
//
//		LicenseData data;
//		if( !data.UpdateFrom(values) )
//		{
//			ret = false;
//			break;
//		}
//
//		tdata[data.id] = data;
//
//		stream.MoveNext();
//		if( stream.Current() == ']' )
//			break;
//	}
//
//	if( ret )
//		license = tdata;
//	return ret;
//}
//
//bool LicenseDataRT::UpdateLicenseTypes(ParseStreamA& stream)
//{
//	bool ret = true;
//
//	std::map<std::wstring, LicenseType> tdata;
//	while( !stream.EOS() )
//	{
//		std::map<std::string, std::string> values;
//		if( !FillData(&values, stream) || stream.Current() != '}' )
//		{
//			ret = false;
//			break;
//		}
//
//		LicenseType data;
//		if( !data.UpdateFrom(values) )
//		{
//			ret = false;
//			break;
//		}
//
//		tdata[data.type] = data;
//
//		stream.MoveNext();
//		if( stream.Current() == ']' )
//			break;
//	}
//
//	if( ret )
//		types = tdata;
//	return ret;
//}


//bool LicenseDataRT::Update(const char* serverResp, const char* ep)
//{
//	bool ret = true;
//
//	ParseStreamA stream(serverResp, ep);
//	while(!stream.EOS() && ret)
//	{
//		std::string val;
//		if( !stream.CopyUntill(&val, '[') )
//			break;
//
//		if( val.compare("DemoData") == 0 )
//			ret = UpdateDemoData(stream);
//		else if( val.compare("LicenseData") == 0 )
//			ret = UpdateLicenseData(stream);
//		else if( val.compare("LicenseTypeData") == 0 )
//			ret = UpdateLicenseTypes(stream);
//		
//		stream.MoveNext();
//	}
//
//	if( ret )
//	{
//		std::map<std::wstring, LicensingUsers>::iterator ui = users.begin();
//		for( ; ui != users.end(); )
//		{
//			if( license.find(ui->second.licenseID) == license.end() )
//			{
//				users.erase(ui);
//				ui = users.begin();
//			} else
//				ui++;
//		}
//
//		WriteData();
//	}
//	return ret;
//}

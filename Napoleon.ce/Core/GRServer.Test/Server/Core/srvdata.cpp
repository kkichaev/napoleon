/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Управление лицензиями
 *
 * ert   18/03/2010   creating
 */
#include "stdafx.h"
#include <string>
#include "srvdata.h"
#include "AES.h"
#include "sessobj.h"

#include "dispatcher.h"
#include "session.h"
#include "objects.h"

#include <set>
#include <atlconv.h>

using namespace std;
using namespace GRServer;

extern Key uploadKey;

const wchar_t AdminPasswordDefault[] = L"admin";


#ifdef DEMO_PERIOD
const DWORD UserDemoDays = DEMO_PERIOD;
#else
const DWORD UserDemoDays = 60; // 60 дней
#endif

const DWORD LicenseVersion = 1;
std::map<std::wstring, ServerData::LicenseType> ServerData::cacheLicense;

class LicenseTypeData : public std::map<int, int>
{
public:
   bool Load(const Binary& data);
   bool Load(const char* data, int size);
   void Store(Binary* out);
   int  Count(ServerData::LicenseType licType) const
   {
      std::map<int, int>::const_iterator fnd = find(licType);
      return (fnd == end()) ? 0 : fnd->second;
   }
private:
   bool LoadPrev(const Binary& data);

   static const char *Tag;
};

struct LicenseData
{
   DWORD version;
   DWORD size;
   DWORD pdaCount;
   DWORD mgrCount;
};

struct LicenseData1 : public LicenseData
{
   DWORD suMgrCount;
};

class LicensedUsersSource : public IDataSource::ICreator
{
   virtual const wchar_t* Name() const { return L"LicensedUsersSource"; }

   class Reader : public IDataSource::IReader
   {
   public:
      Reader(const SessionObject& object);
      virtual ~Reader() { delete value.str; }

      virtual void Remove() {}
      virtual void Close() {}
      virtual bool MoveNext(Object *parentObject);
      virtual bool Get(Object* o) const;
      virtual const MemberFormat* Type(const wchar_t* name) const;
      virtual const Member* Value(const wchar_t* name) const;

   protected:
      RegistredList users;
      mutable RegistredList::const_iterator current;

      int idMember, typeMember;
      mutable MemberFormat format;
      mutable Member value;
   };

   class Writer : public IDataSource::IWriter
   {
   public:
      Writer();

      virtual bool Prepare(const ISessionObject& object);
      virtual bool Write(const Object& o, RowID *rid);
      virtual void Close();

   protected:
      RegistredList users;
      int idMember, typeMember;
      //int licenseCount, eManagerCount;
      DataController* controller;
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

class LicenseCountSource : public IDataSource::ICreator
{
   class Reader : public IDataSource::IReader
   {
   public:
      Reader(const SessionObject& object);

      virtual bool MoveNext(Object *parentObject);
      virtual void Remove() {}
      virtual void Close() {}
      virtual bool Get(Object* o) const;
      virtual const MemberFormat* Type(const wchar_t* name) const;
      virtual const Member* Value(const wchar_t* name) const;

   protected:
      int pdaIndex, mgrIndex, emgrIndex;
      bool assigned;
      DWORD pdaCount, mgrCount, emgrCount;

      mutable MemberFormat format;
      mutable Member value;
   };

   virtual const wchar_t* Name() const { return L"LicenseCountSource"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
   {
      const SessionObject& object = *(const SessionObject*)iobject.Self();
      return new Reader(object);
   }

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
   {
      return NULL;
   }
};

class LicenseCountExSource : public IDataSource::ICreator
{
   class Reader : public IDataSource::IReader
   {
   public:
      Reader(const SessionObject& object);

      virtual bool MoveNext(Object *parentObject) { return (current != ltd.end()); }
      virtual void Remove() {}
      virtual void Close() {}
      virtual bool Get(Object* o) const;
      virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
      virtual const Member* Value(const wchar_t* name) const { return NULL; }

   protected:
      int typeIndex, countIndex;
      LicenseTypeData ltd;
      mutable LicenseTypeData::const_iterator current;
   };

   virtual const wchar_t* Name() const { return L"LicenseCountSourceEx"; }

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
   {
      const SessionObject& object = *(const SessionObject*)iobject.Self();
      return new Reader(object);
   }

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
   {
      return NULL;
   }
};

struct UserSinceData
{
	__int64 startFrom;
	__int64 lastUpdate;
};

class UserSinceFrom : public std::map<std::wstring, UserSinceData>
{
public:
	UserSinceFrom() {}
	bool Load(const Binary& b);
	Binary* Store() const;

	__int64* SinceFrom(const wchar_t* uid) const;
	void Add(const wchar_t* uid, const __int64& start);
};

__int64* UserSinceFrom::SinceFrom(const wchar_t* uid) const
{
	UserSinceFrom::const_iterator fnd = find(uid);
	return (fnd == end()) ? NULL : (__int64*)&(fnd->second.startFrom);
}

void UserSinceFrom::Add(const wchar_t* uid, const __int64& start)
{
	SYSTEMTIME st;
	GetLocalTime(&st);

	UserSinceData ud;
	ud.startFrom = start;
	SystemTimeToFileTime(&st, (FILETIME*)&ud.lastUpdate);
	insert(value_type(uid, ud));
}

bool UserSinceFrom::Load(const Binary& b)
{
	const BYTE* p = (const BYTE*)b;
	const BYTE* ep = p + b.Size();
	WORD count, version;
	version = *(WORD*)p;
	p += sizeof(WORD);
	count = *(WORD*)p;
	p += sizeof(WORD);

	while (count-- > 0 && p < ep)
	{
		WORD size = *(WORD*)p;
		p += sizeof(WORD);

		std::wstring uid;
		uid.assign((const wchar_t*)p, size);
		p += (size * sizeof(wchar_t));

		UserSinceData ud = *(UserSinceData*)p;
		p += sizeof(UserSinceData);

		insert(value_type(uid, ud));
	}

	return true;
}

Binary* UserSinceFrom::Store() const
{
	// version(W), count(W), [id, data]
	int len = 0;
	len += sizeof(WORD); // version
	len += sizeof(WORD); // count
	
	const_iterator i = begin();
	for (; i != end(); i++)
	{
		// lenId,id,data
		len += (int)(sizeof(WORD) + i->first.size() * sizeof(wchar_t) + sizeof(UserSinceData));
	}

	Binary *out = new Binary();
	BYTE *p = out->Alloc(len);

	*(WORD*)p = (WORD)1;
	p += sizeof(WORD);
	*(WORD*)p = (WORD)size();
	p += sizeof(WORD);
	i = begin();
	for (; i != end(); i++)
	{
		WORD size = i->first.size();
		*(WORD*)p = size;
		p += sizeof(WORD);

		memcpy(p, i->first.c_str(), size * sizeof(wchar_t));
		p += (size * sizeof(wchar_t));
		
		*(UserSinceData*)p = i->second;
		p += sizeof(UserSinceData);
	}

	return out;
}

static UserSinceFrom userSinceFrom;

static bool LoadRegistredList(RegistredList* res);
static bool StoreRegistredList(RegistredList* res);

const wchar_t* ServerData::LicenseTypeToString(ServerData::LicenseType type)
{
   switch(type)
   {
   case ServerData::lcPDA:
      return L"pda";
   case ServerData::lcADS:
      return L"ads";
   case ServerData::lcAdsLight:
      return L"adslight";
   case ServerData::lcBTLType:
      return L"btl";
   case ServerData::lcVanPDA:
      return L"vanpda";
   case ServerData::lcManager:
      return L"manager";
	case ServerData::lcADSManager:
		return L"adsmanager";
   case ServerData::lcExclusiveManager:
      return L"exclusiveManager";
   case ServerData::lcFastFood:
      return L"fastfoodpda";
   case ServerData::lcSkladWinStyle:
      return L"skaldwspda";
   case ServerData::lcVendPDA:
      return L"vend";
	case ServerData::lcManagerPDA:
		return L"managerPDA";
	case ServerData::lcExpeditorPDA:
		return L"expeditorpda";
	case ServerData::lcDispatcher:
		return L"dispatcher";
	case ServerData::lcMonitor:
		return L"monitor";
	default: break;
   }

   return L"";
}

ServerData::LicenseType ServerData::LicenseTypeFromString(const std::wstring& str)
{
   if( !wcscmp(str.c_str(), L"pda") ) return ServerData::lcPDA;
   else if( !wcscmp(str.c_str(), L"ads") ) return ServerData::lcADS;
   else if( !wcscmp(str.c_str(), L"btl") ) return ServerData::lcBTLType;
   else if( !wcscmp(str.c_str(), L"vanpda") ) return ServerData::lcVanPDA;
   else if( !wcscmp(str.c_str(), L"manager") ) return ServerData::lcManager;
   else if( !wcscmp(str.c_str(), L"exclusiveManager") ) return ServerData::lcExclusiveManager;
   else if( !wcscmp(str.c_str(), L"fastfoodpda") ) return ServerData::lcFastFood;
   else if( !wcscmp(str.c_str(), L"skaldwspda") ) return ServerData::lcSkladWinStyle;
   else if( !wcscmp(str.c_str(), L"adslight") ) return ServerData::lcAdsLight;
   else if( !wcscmp(str.c_str(), L"vend") ) return ServerData::lcVendPDA;
   else if( !wcscmp(str.c_str(), L"managerPDA") ) return ServerData::lcManagerPDA;
	else if (!wcscmp(str.c_str(), L"adsmanager")) return ServerData::lcADSManager;
	else if (!wcscmp(str.c_str(), L"expeditorpda")) return ServerData::lcExpeditorPDA;
	else if (!wcscmp(str.c_str(), L"dispatcher")) return ServerData::lcDispatcher;
	else if (!wcscmp(str.c_str(), L"monitor")) return ServerData::lcMonitor;
	return ServerData::lcNone;
}

static std::map<int, Binary*> dataCache;
static Mutex dataMutex;

static void PutToCache(int id, const BYTE* src, DWORD size)
{
	if (dataMutex.Acquire(1000))
	{
		Binary *svb = new Binary();
		BYTE* p = svb->Alloc(size);
		memcpy(p, src, size);

		std::map<int, Binary*>::iterator fnd = dataCache.find(id);
		if (fnd != dataCache.end())
			delete fnd->second;

		dataCache[id] = svb;

		dataMutex.Release();
	}
}

Binary* ServerData::GetServerData(int id)
{
	Binary *ret = NULL;
	
	if (dataMutex.Acquire(1000))
	{
		std::map<int, Binary*>::const_iterator fnd = dataCache.find(id);

		if (fnd != dataCache.end())
		{
			if (fnd->second->Size() != 0)
			{
				ret = new Binary();
				BYTE* p = ret->Alloc(fnd->second->Size());
				memcpy(p, (const BYTE*)(*fnd->second), ret->Size());
			}
		}
		dataMutex.Release();
	}
	if (ret != NULL)
		return ret;
	
	IBinary* b = internalDataSource->GetServerData(id);
	if (b == NULL || b->Size() == 0)
	{
		delete b;
		return NULL;
	}

	Binary* dest = AESDecode(b->Bytes(), b->Size(), uploadKey);
   delete b;
	
	if (dest != NULL)
		PutToCache(id, *dest, dest->Size());
	return dest;
}

bool ServerData::AddServerData(const Binary& b, int id)
{
   Binary* dest = AESEncode(b, uploadKey);
   bool ret = internalDataSource->PutServerData(id, *dest);
   delete dest;

	PutToCache(id, b, b.Size());
	return ret;
}

inline Binary* GetLicenseData()
{
   return ServerData::GetServerData(LicenseID);
}

inline Binary* GetAdminPassword()
{
   return ServerData::GetServerData(AdminPasswordID);
}

inline Binary* GetCOMPassword()
{
	return ServerData::GetServerData(COMPasswordID);
}

static bool AddNewLicenseData()
{
   //LicenseData1 data;
   //data.version = LicenseVersion;
   //data.size = sizeof(data);
   //data.pdaCount = 1;
   //data.mgrCount = 1;
   //data.suMgrCount = 0;

   Binary b;
   LicenseTypeData ltd;
#ifdef ADS_DEF
   ltd[ServerData::lcADS] = 1;
#elif ADS_LIGHT
   ltd[ServerData::lcAdsLight] = 1;
#else
   ltd[ServerData::lcPDA] = 1;
#endif
   ltd[ServerData::lcManager] = 1;
   ltd.Store(&b);
   return ServerData::AddServerData(b, LicenseID);

   //return AddLicenseData(db, data);
}

static void UpdateUserSinceFrom(Dispatcher* dispatcher)
{
	
	Session *s = new Session(dispatcher);

	ISessionObject* iso = s->LoadObject(L"UserActivity", NULL, NULL);
	if (iso != NULL && iso->Self()->size() > 0)
	{
		SessionObject* so = (SessionObject*)iso->Self();
		int idate = so->format->FindMember(L"date");
		int idrt = so->format->FindMember(L"duration");
		int iid = so->format->FindMember(L"id");
		ServObject::const_iterator i = iso->Self()->begin();
		for (; i != iso->Self()->end(); i++)
		{
			const Object& o = *(*i);
			const wchar_t* id = o.at(iid).str->c_str();
			__int64 ft = *(__int64*)&o.at(idate).datetime;
			ft -= (__int64)((o.at(idrt).number + 0.05) * 10000000 * 24 * 3600);

			userSinceFrom.Add(id, ft);
		}
	}

	Binary* b = userSinceFrom.Store();
	if (b != NULL)
		internalDataSource->PutServerData(UserSinceFromData, *b);
	delete b;

	delete s;
}

bool ServerData::Init(Dispatcher* dispatcher)
{
	dataMutex.Init();
   bool res = true;
   Binary* data = NULL;

   data = GetLicenseData();
   if( data == NULL )
      res = AddNewLicenseData();
   else
      delete data;

   if( res )
   {
      data = GetAdminPassword();
      if( data == NULL )
         res = SetAdminPassword(AdminPasswordDefault);
      else
         delete data;
   }

   if( res )
   {
      DataSource::AddCreator(new LicensedUsersSource());
      DataSource::AddCreator(new LicenseCountSource());
      DataSource::AddCreator(new LicenseCountExSource());

		res = UserActivityHolder::Init();
		data = GetServerData(UserSinceFromData);
		if (data == NULL || userSinceFrom.Load(*data) == false)
		{
			UpdateUserSinceFrom(dispatcher);
			data = userSinceFrom.Store();
			if (data != NULL)
				AddServerData(*data, UserSinceFromData);
			delete data;
		}
   }
   return res;
}

#ifdef TMP_MGR_LICENSE_COUNT
inline DWORD MgrLicenseCount(DWORD count)
{
   if( count < TMP_MGR_LICENSE_COUNT )
   {
      SYSTEMTIME st;
      __int64 ft;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, (FILETIME*)&ft);

      ft /= (__int64)10000000;
      DWORD value = (DWORD)(ft / 3600);
      if( value < TMP_MGR_TILL )
         count = TMP_MGR_LICENSE_COUNT;
   }
   return count;
}
#else
inline DWORD MgrLicenseCount(DWORD count) { return count; }
#endif

#ifdef TMP_PDA_LICENSE_COUNT
inline DWORD PdaLicenseCount(DWORD count)
{
	if (count < TMP_PDA_LICENSE_COUNT)
	{
		SYSTEMTIME st;
		__int64 ft;
		GetLocalTime(&st);
		SystemTimeToFileTime(&st, (FILETIME*)&ft);

		ft /= (__int64)10000000;
		DWORD value = (DWORD)(ft / 3600);
		if (value < TMP_PDA_TILL)
			count = TMP_PDA_LICENSE_COUNT;
	}
	return count;
}
#else
inline DWORD PdaLicenseCount(DWORD count) { return count; }
#endif

#ifdef TMP_ADS_LICENSE_COUNT
inline DWORD AdsLicenseCount(DWORD count)
{
	if (count < TMP_ADS_LICENSE_COUNT)
	{
		SYSTEMTIME st;
		__int64 ft;
		GetLocalTime(&st);
		SystemTimeToFileTime(&st, (FILETIME*)&ft);

		ft /= (__int64)10000000;
		DWORD value = (DWORD)(ft / 3600);
		if (value < TMP_ADS_TILL)
			count = TMP_ADS_LICENSE_COUNT;
	}
	return count;
}
#else
inline DWORD AdsLicenseCount(DWORD count) { return count; }
#endif

DWORD ServerData::LicenseCount(LicenseType tp)
{
   DWORD count = 0;
   Binary* data = GetLicenseData();
   LicenseTypeData ltd;
   if( data != NULL && ltd.Load(*data) )
   {
      LicenseTypeData::const_iterator fnd = ltd.find(tp);
      if( fnd != ltd.end() )
         count = fnd->second;
   }
   delete data;
   if( tp == ServerData::lcManager )
      count = MgrLicenseCount(ltd[tp]);
	return count;
}

const char LicenseTag[] = "GRServer LicenseFile";
bool ServerData::LicenseUpdate(const Binary& data)
{
   if( data.Size() < sizeof(LicenseData) )
      return false;

   LicenseTypeData ltd;
   const BYTE* p = (const BYTE*)(data);
   if(memcmp(p, LicenseTag, sizeof(LicenseTag) - 1) == 0 )
   {
		char* p = (char*)alloca(data.Size() + 1);
		memcpy(p, (const BYTE*)(data), data.Size());
		p[data.Size()] = '\0';
      const char* sp = (const char*)p;
      sp = strchr(sp, '\n');
      if( sp == NULL )
         return false;

      ltd.Load(sp+1, data.Size() - (int)(sp - (const char*)p) - 1);
   } else
   {
      if( !ltd.Load(data) )
         return false;

   }
   Binary b;
   ltd.Store(&b);
   return AddServerData(b, LicenseID);
}

bool ServerData::CheckAdminPassword(const std::wstring& password)
{
   bool res = false;
   Binary* data = GetAdminPassword();
   if( data != NULL )
   {
      const wchar_t *pwd = (const wchar_t*)(const BYTE*)(*data);
      res = (wmemcmp(password.c_str(), pwd, data->Size() / sizeof(wchar_t)) == 0);
      delete data;
   }

   return res;
}

bool ServerData::SetAdminPassword(const std::wstring& password)
{
   DWORD size = (DWORD)(password.size() * sizeof(wchar_t));
   Binary b;
   memcpy(b.Alloc(size), password.c_str(), size);

   return AddServerData(b, AdminPasswordID);
}

bool ServerData::CheckCOMPassword(const std::wstring& password)
{
	bool res = password.empty();
	Binary* data = GetCOMPassword();
	if (data != NULL)
	{
		const wchar_t *pwd = (const wchar_t*)(const BYTE*)(*data);
		if (*pwd)
			res = (wmemcmp(password.c_str(), pwd, data->Size() / sizeof(wchar_t)) == 0);
		delete data;
	}

	return res;
}

bool ServerData::SetCOMPassword(const std::wstring& password)
{
	DWORD size = (DWORD)(password.size() * sizeof(wchar_t));
	Binary b;
	memcpy(b.Alloc(size), password.c_str(), size);

	return AddServerData(b, COMPasswordID);
}

bool LoadRegistredList(RegistredList* res)
{
   bool ret = false;

   Binary* b = ServerData::GetServerData(UserLicenseID);
   if( b )
   {
      DWORD size = b->Size();
      const BYTE* p = (*b), *ep = p + size;

      res->clear();

      bool err = false;
      while( p < ep )
      {
         WORD len = *(WORD*)p;
         p += sizeof(WORD);

         if( len > (ep - p) )
         {
            err = true;
            break;
         }

         RegLicenseData rd;
         rd.id.assign((const wchar_t*)p, len);
         p += len * sizeof(wchar_t);

         rd.type = *(ServerData::LicenseType*)p;
         p += sizeof(ServerData::LicenseType);
         res->insert(rd);
      }

      delete b;
      if( !err ) ret = true;
   }

   return ret;
}

bool StoreRegistredList(RegistredList* res)
{
   Binary* data = GetLicenseData();
   LicenseTypeData ltd;
   if( data != NULL )
      ltd.Load(*data);

   std::map<ServerData::LicenseType, int> counts;
   DWORD cb = (DWORD)(res->size() * sizeof(WORD));
   RegistredList::const_iterator i = res->begin();
   for( ; i != res->end(); i++ )
   {
      ServerData::LicenseType lt = (*i).type;
      int count = counts[lt];
      count++;
      counts[lt] = count;
      if( counts[lt] <= ltd[lt] )
         cb += (DWORD)(i->id.size() * sizeof(wchar_t) + sizeof(ServerData::LicenseType));
   }
   counts.clear();

   Binary dest;
   BYTE* p = dest.Alloc(cb);
   for( i = res->begin(); i != res->end(); i++ )
   {
      ServerData::LicenseType lt = (*i).type;
      int count = counts[lt];
      count++;
      counts[lt] = count;
      if( counts[lt] <= ltd[lt] )
      {
         WORD len = (WORD)i->id.size();
         *(WORD*)p = len;
         p += sizeof(WORD);
         memcpy(p, i->id.c_str(), len * sizeof(wchar_t));
         p += len * sizeof(wchar_t);
         *(ServerData::LicenseType*)p = i->type;
         p += sizeof(ServerData::LicenseType);
      }
   }

   bool ret = ServerData::AddServerData(dest, UserLicenseID);
	if (ret)
		ServerData::cacheLicense.clear();
   return ret;
}

ServerData::LicenseType ServerData::GetLicense(const std::wstring& id)
{
	std::map<std::wstring, LicenseType>::const_iterator fnd = cacheLicense.find(id);
	if (fnd != cacheLicense.end())
		return fnd->second;

	LicenseType ret = lcNone;

	RegistredList rl;
	if (LoadRegistredList(&rl))
	{
		RegistredList::const_iterator i = rl.begin();
		for (; i != rl.end(); i++)
		{
			if (i->id.compare(id) == 0)
			{
				ret = i->type;
				break;
			}
		}
	}

	cacheLicense[id] = ret;
	return ret;
}

bool ServerData::Registred(const std::wstring& login, const std::wstring& category, DWORD duration, User *u)
{
   USES_CONVERSION;

   bool ret = false;
   RegistredList rl;
   if( LoadRegistredList(&rl) )
   {
      RegLicenseData data;
      data.id = login;
      data.type = (category.empty()==false) ? LicenseTypeFromString(category) : ServerData::lcPDA;
      ret = (rl.find(data) != rl.end());
      if( ret == false )
      {
         gServer->AddLog("RegistresUsersCount %d. Fail to login for id '%s'",  rl.size(), W2A(login.c_str()));
      }
   }
   else
   {
      gServer->AddLog("No load registred users. Fail to login for id '%s'", W2A(login.c_str()));
   }

   if( !ret && u->size() > 0 )
   {
		__int64 ft;
		SYSTEMTIME st;
		GetLocalTime(&st);
		SystemTimeToFileTime(&st, (FILETIME*)&ft);

		const wchar_t *uid = u->ID();
		__int64* sinceFrom = userSinceFrom.SinceFrom(uid);
		if (sinceFrom == NULL)
		{
			userSinceFrom.Add(uid, ft);
			sinceFrom = &ft;
			Binary* b = userSinceFrom.Store();
			if (b != NULL)
				AddServerData(*b, UserSinceFromData);
			delete b;
		}

		if (ft >= (*sinceFrom))
		{
			__int64 duration = ft - (*sinceFrom);
			duration /= ((__int64)10000000 * 24 * 3600);
			if (duration < UserDemoDays)
				ret = true;
		}

		/*
      std::wstring filter(L"\"id\"='");
      filter += u->ID();
      filter += L"'";

      Member* dm = (*u->at(0))[DURATION_MEMBER];
      dm->number = 0;
      ISessionObject* iso = u->GetSession().LoadObject(L"UserActivity", NULL, filter.c_str());
      if( iso != NULL && iso->Self()->size() > 0 )
      {
         SessionObject* so = (SessionObject*)iso->Self();
         int idate = so->format->FindMember(L"date");
         int idrt = so->format->FindMember(L"duration");

         if( idate >= 0 && idrt >= 0 )
         {
            Object *o = so->at(0);
            double ud = o->at(idrt).number;
            __int64 ut = *(__int64*)&o->at(idate).datetime;

            __int64 ft;
            SYSTEMTIME st;
            GetLocalTime(&st);
            SystemTimeToFileTime(&st, (FILETIME*)&ft);

            if( ft > ut )
            {
               ft -= ut;
               ft /= ((__int64)10000000 * 24 * 3600);
               ud += (double)ft;
            }
            if( ud < UserDemoDays )
               ret = true;

            dm->number = ud;
         } else
            ret = true;
      } else
         ret = true;
			*/
   }
#ifdef NO_DEMO_MODE
	ret = true;
#endif
   return ret; //(ret) ? true : (duration < UserDemoPeriod);
}

LicenseCountSource::Reader::Reader(const SessionObject& object) : pdaIndex(-1), mgrIndex(-1), assigned(false)
{
   format.type = MemberFormat::mtNumber;
   format.format.fraction = 0;

   Binary* data = GetLicenseData();
   if( data != NULL )
   {
      LicenseTypeData ltd;
      if( ltd.Load(*data) )
      {
         pdaCount = ltd[ServerData::lcPDA];
         mgrCount = MgrLicenseCount(ltd[ServerData::lcManager]);
         emgrCount = ltd[ServerData::lcExclusiveManager];
      }
      //const LicenseData* l = (const LicenseData*)(const BYTE*)(*data);
      //pdaCount = l->pdaCount;
      //mgrCount = MgrLicenseCount(l->mgrCount);
      //emgrCount = 0;
      //if( l->size == sizeof(LicenseData1) )
      //   emgrCount = ((LicenseData1*)l)->suMgrCount;

      delete data;

      pdaIndex = object.format->FindMember(L"pda");
      mgrIndex = object.format->FindMember(L"manager");
      emgrIndex = object.format->FindMember(L"exclusiveManager");
   }
}

bool LicenseCountSource::Reader::MoveNext(Object *parentObject)
{
   if( !assigned )
   {
      assigned = true;
      return true;
   }
   return false;
}

bool LicenseCountSource::Reader::Get(Object* o) const
{
   if( pdaIndex >= 0 && mgrIndex >= 0 )
   {
      o->at(pdaIndex).number = pdaCount;
      o->at(mgrIndex).number = mgrCount;
      if( emgrIndex >= 0 )
         o->at(emgrIndex).number = emgrCount;

      return true;
   }
   return false;
}

const MemberFormat* LicenseCountSource::Reader::Type(const wchar_t* name) const
{
   if( wcscmp(name, L"pda") == 0 )
   {
      format.name = name;
      return &format;
   } else if( wcscmp(name, L"manager") == 0 )
   {
      format.name = name;
      return &format;
   } else if( wcscmp(name, L"exclusiveManager") == 0 )
   {
      format.name = name;
      return &format;
   }

   return NULL;
}

const Member* LicenseCountSource::Reader::Value(const wchar_t* name) const
{
   if( wcscmp(name, L"pda") == 0 )
   {
      value.number = pdaCount;
      return &value;
   } else if( wcscmp(name, L"manager") == 0 )
   {
      value.number = mgrCount;
      return &value;
   } else if( wcscmp(name, L"exclusiveManager") == 0 )
   {
      value.number = emgrCount;
      return &value;
   }

   return NULL;
}

LicensedUsersSource::Reader::Reader(const SessionObject& object)
{
   idMember = object.format->FindMember(L"id");
   typeMember = object.format->FindMember(L"type");

   if( !LoadRegistredList(&users) )
      users.clear();

   format.type = MemberFormat::mtString;
   current = users.begin();

   value.str = new CString();
}

LicenseCountExSource::Reader::Reader(const SessionObject& object) : typeIndex(-1), countIndex(-1)
{
   Binary* data = GetLicenseData();
   if( data != NULL )
   {
      ltd.Load(*data);
      delete data;

      typeIndex = object.format->FindMember(L"type");
      countIndex = object.format->FindMember(L"count");
   }
   current = ltd.begin();
}

bool LicenseCountExSource::Reader::Get(Object* o) const
{
   if( typeIndex >= 0 && countIndex >= 0 )
   {
      ServerData::LicenseType tp = (ServerData::LicenseType)current->first;
      o->at(typeIndex).str->assign(ServerData::LicenseTypeToString(tp));
      int count = current->second;

      if( tp == ServerData::lcManager )
         count = MgrLicenseCount(count);

      o->at(countIndex).number = count;
      current++;
      return true;
   }
   return false;
}

bool LicensedUsersSource::Reader::MoveNext(Object *parentObject)
{
   return (idMember >= 0 && typeMember >= 0 && current != users.end());
}

bool LicensedUsersSource::Reader::Get(Object* o) const
{
   const RegLicenseData &c = *current;
   o->at(idMember).str->assign(c.id);
   o->at(typeMember).str->assign(ServerData::LicenseTypeToString(c.type));
   current++;
   return true;
}

const MemberFormat* LicensedUsersSource::Reader::Type(const wchar_t* name) const
{
   if( wcscmp(name, L"id") == 0 )
   {
      format.name = name;
      return &format;
   } else if( wcscmp(name, L"type") == 0 )
   {
      format.name = name;
      return &format;
   }
   return NULL;
}

const Member* LicensedUsersSource::Reader::Value(const wchar_t* name) const
{
   if( current != users.end() )
   {
      if( wcscmp(name, L"id") == 0 )
      {
         value.str->assign(current->id);
         return &value;
      } else if( wcscmp(name, L"id") == 0 )
      {
         value.str->assign(ServerData::LicenseTypeToString(current->type));
         return &value;
      }
   }
   return NULL;
}

LicensedUsersSource::Writer::Writer()
{
   //licenseCount = ServerData::LicenseCount(ServerData::lcPDA);
   //eManagerCount = ServerData::LicenseCount(ServerData::lcExclusiveManager);
}

bool LicensedUsersSource::Writer::Prepare(const ISessionObject& iobject)
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   idMember = object.format->FindMember(L"id");
   typeMember = object.format->FindMember(L"type");
   controller = &((Session&)object.GetSession()).GetDispatcher()->Controller();
   return (idMember >= 0 && typeMember >= 0);
}

bool LicensedUsersSource::Writer::Write(const Object& o, RowID *rid)
{
   ServerData::LicenseType type = ServerData::LicenseTypeFromString((const std::wstring&)*o.at(typeMember).str);

   //if( type == ServerData::lcPDA && licenseCount-- <= 0 ) return true;
   //if( type == ServerData::lcExclusiveManager && eManagerCount-- <= 0 ) return true;

   RegLicenseData rd;
   rd.id = (const std::wstring&)*o.at(idMember).str;
   rd.type = type;
   users.insert(rd);

   return true;
}

void LicensedUsersSource::Writer::Close()
{
   StoreRegistredList(&users);
   controller->RefreshUsers();
}

RegistredList* ServerData::GetRegistredUsers()
{
   RegistredList* ret = new RegistredList();
   LoadRegistredList(ret);
   return ret;
}

const char* LicenseTypeData::Tag = "LicenseTag";

bool LicenseTypeData::LoadPrev(const Binary& data)
{
   bool ret = true;
   const LicenseData* l = (const LicenseData*)((const BYTE*)data);
   if( l->size == sizeof(LicenseData) )
   {
      (*this)[ServerData::lcPDA] = l->pdaCount;
      (*this)[ServerData::lcManager] = l->mgrCount;
   } else if( l->size == sizeof(LicenseData1) )
   {
      (*this)[ServerData::lcPDA] = l->pdaCount;
      (*this)[ServerData::lcManager] = l->mgrCount;
      (*this)[ServerData::lcExclusiveManager] = ((LicenseData1*)l)->suMgrCount;
   } else
      ret = false;
   return ret;
}

bool LicenseTypeData::Load(const Binary& data)
{
   size_t len = strlen(Tag);
   const BYTE* p = (const BYTE*)data;
   const BYTE* ep = p + data.Size();
   if( strncmp((const char*)p, Tag, len) != 0 )
      return LoadPrev(data);

   p += len;
   p += sizeof(WORD); // version

//   WORD elcount = *(WORD*)p;
   p += sizeof(WORD); // count

	bool assignAds = false;
   while( p < ep )
   {
      DWORD type = *(DWORD*)p;
      p += sizeof(DWORD);
      DWORD count = *(DWORD*)p;
      p += sizeof(DWORD);

		if (type == ServerData::lcPDA)
			count = PdaLicenseCount(count);
		else if (type == ServerData::lcADS)
		{
			count = AdsLicenseCount(count);
			assignAds = true;
		}
		(*this)[type] = count;
   }

#ifdef TMP_ADS_LICENSE_COUNT
	if (!assignAds)
	{
		DWORD count = AdsLicenseCount(0);
		if (count > 0)
		{
			(*this)[ServerData::lcADS] = count;
		}
	}
#endif
	return true;
}

bool LicenseTypeData::Load(const char* data, int size)
{
   USES_CONVERSION;

   const char *edata = data + size;
   while( data < edata )
   {
      while( data < edata && *data > 0 && isspace(*data) )
         data++;
      if( data >= edata || *data <= 0 )
         break;

      std::string tag;
      while( data < edata && *data > 0 && !isspace(*data) )
      {
         tag.append(1, *data);
         data++;
      }
      if( data >= edata || *data <= 0 )
         break;

      std::wstring tagW = A2W(tag.c_str());
      ServerData::LicenseType tp = ServerData::LicenseTypeFromString(tagW);
      const char *ep;
      int val = strtol(data, (char**)&ep, 10);
      if( tp != ServerData::lcNone)
         (*this)[tp] = val;
      data = ep;
   }

   return true;
}

void LicenseTypeData::Store(Binary* out)
{
   int tagLen = (int)strlen(Tag), len;
   len = tagLen;
   len += sizeof(WORD); // version
   len += sizeof(WORD); // count
   len += (int)(sizeof(DWORD) * 2 * size());

   const BYTE *p = out->Alloc(len);
   memcpy((void*)p, Tag, tagLen);
   p += tagLen;
   *(WORD*)p = (WORD)LicenseVersion;
   p += sizeof(WORD);
   *(WORD*) p = (WORD)size();
   p += sizeof(WORD);

   const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      *(DWORD*)p = i->first;
      p += sizeof(DWORD);
      *(DWORD*)p = i->second;
      p += sizeof(DWORD);
   }
}

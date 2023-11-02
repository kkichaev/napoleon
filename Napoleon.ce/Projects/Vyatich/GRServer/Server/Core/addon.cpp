/*
 * Copyright (C), 2009-2010, ƒенис ћос€гин
 *
 * Add on - дополнени€ дл€ разных клиентов
 *
 * ert   16/06/2010   creating
 */ 
#include "stdafx.h"
#include <vector>
#include <map>
#include "server.h"
#include "servobj.h"
#include "objdef.h"
#include "parse.h"
#include "datasource.h"
#include "session.h"
#include "dateparse.h"
#include "Shlobj.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#include "creators.h"
#include "objects.h"
#include "dbf.h"
#include "StdConsts.h"
#include "srvutility.h"

#include <folderholder.h>
#include <cost.h>

using namespace GRServer;
using namespace std;

namespace VyatichNamespace {

struct PriceCostData
{
   std::wstring id;
   double cost;

   bool operator < (const PriceCostData &l) const { return id.compare(l.id) < 0; }
};


class VTBaseReader;
class VTBaseCreator : public IDataSource::ICreator
{
public:
   VTBaseCreator(){}
   virtual ~VTBaseCreator() {}

   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }

   virtual VTBaseReader* CreateReader() const = 0;
};

static void IncrementFileName(std::string* file)
{
	std::string::reverse_iterator ri = file->rbegin();

	while (ri != file->rend())
	{
		char sym = *ri;
		if ((int)sym < 0 && isdigit(sym) == 0)
			break;

		if (sym == '9') sym = '0';
		else sym++;

		*ri = sym;
		if (sym == '0')
			ri++;
		else
			break;
	}
}

static char* RemoveNewLines(char *str)
{
	const char* rmvChars = "\n\r";

	char *p = str;
	while (*p)
	{
		if (strchr(rmvChars, *p) != NULL)
			*p = ' ';
		p++;
	}
	return str;
}
class VTBaseReader : public IDataSource::IReader
{
public:
   VTBaseReader() {}
   ~VTBaseReader() { Close(); }

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return true; }
   virtual void Remove() {}
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
   virtual const Member* Value(const wchar_t* name) const { return NULL; }

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;

   virtual bool Open(const std::wstring& fileName, const ISessionObject& object) = 0;

   void SetBackupFolder(const std::string& back) { backupFolder = back; }

protected:
   mutable std::vector<Object*> objects;
   std::string backupFolder;
};

class VTFridgeReader : public VTBaseReader
{
public:
	VTFridgeReader() {}
	virtual bool Open(const std::wstring& fileName, const ISessionObject& object);
};

class VTOrgReader : public VTBaseReader
{
public:
   VTOrgReader() {}
   virtual bool Open(const std::wstring& fileName, const ISessionObject& object);
};

class VTPriceReader : public VTBaseReader
{
public:
   VTPriceReader() {}
   virtual bool Open(const std::wstring& fileName, const ISessionObject& object);
};

class VTCostReader : public VTBaseReader
{
public:
   VTCostReader() {}
   virtual bool Open(const std::wstring& fileName, const ISessionObject& object);

private:
   Binary* LoadCost(const std::string& fileName, const User& userid, std::map<std::wstring, std::set<std::wstring>> *actions);
};

class VTPODReader : public VTBaseReader
{
public:
   VTPODReader() {}
   virtual bool Open(const std::wstring& fileName, const ISessionObject& object);
   virtual void Remove();

private:
   std::string file;
};

class VTPriceCostReader : public IDataSource::IReader
{
public:
   VTPriceCostReader() : idIndex(-1) {}
   bool Open(const ISessionObject& object);

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;

   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return true; }
   virtual void Remove() {}
   virtual void Close() { cost.clear(); }

   virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
   virtual const Member* Value(const wchar_t* name) const { return NULL; }

private:
   std::set<PriceCostData> cost;
   PriceCostData current;
   int costIndex, idIndex;
};

class VTFolderReader : public VTBaseReader
{
public:
   VTFolderReader() : keyIndex(-1), valueIndex(-1), curIndex(0), folderHolder(NULL), session(NULL) {}
   virtual bool Open(const std::wstring& fileName, const ISessionObject& object);
   
   virtual bool Get(Object* o) const;

private:
   ISession* session;
   mutable FolderIDHolder* folderHolder;
   int keyIndex, valueIndex;
   mutable int curIndex;
};

class VTOrgCreator : public VTBaseCreator
{
public:
   virtual const wchar_t* Name() const { return L"VTOrgReader"; }
   virtual VTBaseReader* CreateReader() const { return new VTOrgReader(); }
};

class VTFrigeCreator : public VTBaseCreator
{
public:
	virtual const wchar_t* Name() const { return L"VTFridgeReader"; }
	virtual VTBaseReader* CreateReader() const { return new VTFridgeReader(); }
};

class VTPriceCreator : public VTBaseCreator
{
public:
   virtual const wchar_t* Name() const { return L"VTPriceReader"; }
   virtual VTBaseReader* CreateReader() const { return new VTPriceReader(); }
};

class VTFolderCreator : public VTBaseCreator
{
public:
   virtual const wchar_t* Name() const { return L"VTFolderReader"; }
   virtual VTBaseReader* CreateReader() const { return new VTFolderReader(); }
};

class VTCostCreator : public VTBaseCreator
{
public:
   virtual const wchar_t* Name() const { return L"VTCostReader"; }
   virtual VTBaseReader* CreateReader() const { return new VTCostReader(); }
};

class VTPODCreator : public VTBaseCreator
{
public:
   virtual const wchar_t* Name() const { return L"VTPODReader"; }
   virtual VTBaseReader* CreateReader() const { return new VTPODReader(); }
};

class VTPriceCostCreator : public IDataSource::ICreator
{
public:
   virtual const wchar_t* Name() const { return L"VTPriceCostReader"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};

class OrderWriter;
class VTOrderCreator : public IDataSource::ICreator
{
public:
   virtual const wchar_t* Name() const { return L"VTOrderWriter"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;

protected:
   virtual OrderWriter* CreateWriter(const char* fileName) const;
};

class VTOrderItemsCreator : public IDataSource::ICreator
{
public:
   virtual const wchar_t* Name() const { return L"VTOrderItemsWriter"; }
   virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
};

class VTReturnCreator : public VTOrderCreator
{
public:
	virtual const wchar_t* Name() const { return L"VTReturnWriter"; }
	virtual OrderWriter* CreateWriter(const char* fileName) const;
};

class VTReturnItemsCreator : public IDataSource::ICreator
{
public:
	virtual const wchar_t* Name() const { return L"VTReturnItemsWriter"; }
	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
};

class VTOrderCancelCreator : public VTOrderCreator
{
public:
   virtual const wchar_t* Name() const { return L"VTOrderCancel"; }
protected:
   virtual OrderWriter* CreateWriter(const char* fileName) const;
};

class VTOrderCancelItemsCreator : public VTOrderItemsCreator
{
public:
   virtual const wchar_t* Name() const { return L"VTOrderCancelItems"; }
};

class OrderItemsWriter : public IDataSource::IWriter
{
public:
   OrderItemsWriter(OrderWriter* parent);
   virtual ~OrderItemsWriter() { Close(); }

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close() {}

   void SetDestFile(FILE* f) { destFile = f; }

	virtual void WriteObjectData(FILE* destFile, const Object& o) {}

protected:
   FILE *destFile;
   int iId, iQty, childIndex;
};

class ReservReader
{
public:
   ReservReader() : itemsFormat(NULL), iId(-1), iDate(-1), iNumber(-1), iItems(-1), iiId(-1), 
      iiQty(-1), iiCost(-1), iCreated(-1) {}
   
   void Init(SessionObject* reserv);
   bool ReadTo(SessionObject* dest, const std::string& fileName, const FILETIME& created);

private:
   int iId, iDate, iNumber, iItems, iCreated;
   int iiId, iiQty, iiCost;

   const GRServer::Format* itemsFormat;
   DateParser dateParser;
};

class OrderWriter : public IDataSource::IWriter
{
public:
	OrderWriter(const char* _fileName) : fileName(_fileName), itemsWriter(NULL), iId(-1), iDate(-1), iCreated(-1), reserv(NULL) {}
	//OrderWriter(const char* _fileName) : fileName(_fileName), itemsWriter(NULL), iId(-1), iDate(-1), iRet(-1), isRet(0), iCreated(-1), reserv(NULL) {}
   virtual ~OrderWriter() { Close(); }

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close();

   void SetItemsWriter(OrderItemsWriter* writer) { itemsWriter = writer; }

protected:
   //virtual const char* GetStatus() const { return  isRet != 0 ? "возврат" : "заказ"; }
	virtual const char* GetStatus() const { return "заказ"; }
	virtual const char* GetNumber() const { return "0"; }
   virtual SessionObject* GetReservObject(Session& s) const { return (SessionObject*)s.GetObject(L"OrderReserv", NULL); }

protected:
   OrderItemsWriter *itemsWriter;
   std::string fileName, userId, userName;
	int iId, iDate, iCreated, iRemark, iOrgName; // , iRet, isRet;
   SessionObject *reserv;
   DateParser createdFormat;
   ReservReader reservWriter;
};

class OrderCancelWriter : public OrderWriter
{
public:
   OrderCancelWriter(const char *fileName) : OrderWriter(fileName) {}

   virtual bool Prepare(const ISessionObject& object);
   virtual bool Write(const Object& o, RowID *rid);
   virtual const char* GetNumber() const { return number.c_str(); }

protected:
   std::string number;

   virtual const char* GetStatus() const { return "отказ"; }
   virtual SessionObject* GetReservObject(Session& s) const { return NULL; }
   int iNumber;
};

class VTReqClientsCreator : public VTOrderCreator
{
public:
   virtual const wchar_t* Name() const { return L"VTReqClients"; }

protected:
   virtual OrderWriter* CreateWriter(const char* fileName) const { return new Writer(fileName); }

   class Writer : public OrderWriter
   {
   public:
      Writer(const char* fileName) : OrderWriter(fileName) {}
      virtual ~Writer() {}

      virtual bool Prepare(const ISessionObject& object);
      virtual bool Write(const Object& o, RowID *rid) { return true; }
      virtual void Close() {}
   };
};

class InvFrgItemsWriter;
class InvFrgWriter : public IDataSource::IWriter
{
public:
	InvFrgWriter(const char *_fileName) : fileName(_fileName), itemsWriter(NULL), iId(-1), iDate(-1), iCreated(-1) {}
	virtual ~InvFrgWriter() { Close(); }

	virtual bool Prepare(const ISessionObject& object);
	virtual bool Write(const Object& o, RowID *rid);
	virtual void Close();

	void SetItemsWriter(InvFrgItemsWriter* writer) { itemsWriter = writer; }

protected:
	//virtual const char* GetStatus() const { return  isRet != 0 ? "возврат" : "заказ"; }
	virtual const char* GetStatus() const { return "заказ"; }
	virtual const char* GetNumber() const { return "0"; }
	virtual SessionObject* GetReservObject(Session& s) const { return (SessionObject*)s.GetObject(L"OrderReserv", NULL); }

protected:
	InvFrgItemsWriter *itemsWriter;
	std::string fileName, userId, userName;
	int iId, iDate, iCreated, iRemark, iArend, iRetEquip; // , iRet, isRet;
	DateParser createdFormat;
};

class InvFrgItemsWriter : public IDataSource::IWriter
{
public:
	InvFrgItemsWriter(InvFrgWriter *parent) { parent->SetItemsWriter(this); }

	virtual ~InvFrgItemsWriter() { Close(); }

	virtual bool Prepare(const ISessionObject& object);
	virtual bool Write(const Object& o, RowID *rid);
	virtual void Close() {}

	void SetDestFile(FILE* f) { destFile = f; }

protected:
	FILE *destFile;
	int iId, childIndex, iName, iNumber, iBC, iNew, iExists;
};

bool InvFrgWriter::Prepare(const ISessionObject& object)
{
	USES_CONVERSION;
	Session& s = (Session&)object.GetSession();
	const User& user = s.GetUser();

	userId = W2A(user.ID());
	userName = W2A(user.UserName());

	const GRServer::Format *format = object.Self()->format;
	iId = format->FindMember(L"id");
	iDate = format->FindMember(L"date");
	iCreated = format->FindMember(L"created");
	iRemark = format->FindMember(L"remark");
	iArend = format->FindMember(L"tenant");
	iRetEquip = format->FindMember(L"retEquip");

	createdFormat.SetFormat(L"YYYYMMDDHHmmSS");
	return true;
}

void InvFrgWriter::Close()
{
	itemsWriter = NULL;
}

bool InvFrgWriter::Write(const Object& o, RowID *rid)
{
	if (iId < 0 || iDate < 0 || iCreated < 0 || iRemark < 0 || itemsWriter == NULL)
	{
		gServer->AddLog(IErrorLogger::Full, "OrderWriter iId < 0 || iDate < 0 || iCreated < 0 || iRemark < 0 || itemsWriter == NULL");
		return false;
	}

	while (IsFileExists(fileName))
	{
		IncrementFileName(&fileName);
	}

	FILE* f = fopen(fileName.c_str(), "wt");
	if (f == NULL)
	{
		gServer->AddLog(IErrorLogger::Short, "Can't open file %s error %d", fileName.c_str(), GetLastError());
		return false;
	}

	SYSTEMTIME st;
	USES_CONVERSION;
	char* remark = RemoveNewLines(_strdup(W2A(o.at(iRemark).str->c_str())));
	std::wstring crbuf;

	int chgArend = 0, rtEq = 0;
	if (o.at(iArend).number > 0) chgArend = 1;
	if (o.at(iRetEquip).number > 0) rtEq = 1;

	createdFormat.ToString(&crbuf, o.at(iCreated).datetime);

	FileTimeToSystemTime(&o.at(iDate).datetime, &st);
	const char* id = W2A(o.at(iId).str->c_str());
	fprintf(f, "status|%s|0|||\n""client||%s|||%d/%d/%d||||||%d|%d\n""manager|%s|%s|%s||%s\n",
		"’ќ_инв", 
		id, st.wDay, st.wMonth, st.wYear, chgArend, rtEq,
		userId.c_str(), userName.c_str(), remark, W2A(crbuf.c_str()));
	itemsWriter->SetDestFile(f);
	itemsWriter->Write(o, rid);
	fclose(f);
	free(remark);
	gServer->AddLog(IErrorLogger::Full, "Write Order (or OrderCancel) file %s", fileName.c_str());

	return true;
}


bool InvFrgItemsWriter::Prepare(const ISessionObject& object)
{
	const ISessionObject* parent = object.Parent();
	if (parent == NULL)
	{
		gServer->AddLog(IErrorLogger::Full, "InvFrgItemsWriter::Prepare no parent");
		return false;
	}

	const GRServer::Format* format = object.Self()->format;
	int off = format->name.find_last_of(L'$');
	childIndex = parent->Self()->format->FindMember(format->name.substr(off + 1).c_str());

	iId = format->FindMember(L"id");
	iName = format->FindMember(L"name");
	iNumber = format->FindMember(L"number");
	iBC = format->FindMember(L"barcode");
	iNew = format->FindMember(L"isnew");
	iExists = format->FindMember(L"exist");

	if (childIndex < 0 || iId < 0 || iBC < 0)
	{
		gServer->AddLog(IErrorLogger::Full, "InvFrgItemsWriter::Prepare (childIndex < 0 || )");
		return false;
	}
	return true;
}

bool InvFrgItemsWriter::Write(const Object& o, RowID *rid)
{
	if (destFile == NULL)
		return false;

	fputs("---\n", destFile);
	const Member& m = o.at(childIndex);
	if (m.object != NULL)
	{
		USES_CONVERSION;
		ServObject::const_iterator i = m.object->begin();
		for (; i != m.object->end(); i++)
		{
			fprintf(destFile, "%s|%s|%s|%s|%d|%d", W2A((*i)->at(iId).str->c_str()), W2A((*i)->at(iName).str->c_str()), W2A((*i)->at(iNumber).str->c_str()), 
				W2A((*i)->at(iBC).str->c_str()), (int)((*i)->at(iNew).number + 0.1), (int)((*i)->at(iExists).number + 0.1));
			fprintf(destFile, "\n");
		}
	}

	destFile = NULL;
	return true;
}


class VTFrgInvCreator : public IDataSource::ICreator
{
public:
	virtual const wchar_t* Name() const { return L"VTInvFrgWriter"; }
	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
	{
		InvFrgWriter* writer = NULL;
		const Parameter* p = parameters.Find(L"fileName", -1);
		if (p != NULL)
		{
			CString *fileName = NULL;
			const Session& session = (Session&)object.GetSession();
			if (session.Parse(&fileName, p->value, &object))
			{
				USES_CONVERSION;
				std::string file(session.Config().ExchangeFolder());
				file += W2A(fileName->c_str());
				writer = new InvFrgWriter(file.c_str());

			}
			delete fileName;
		}

		return writer;
	}
};

class VTFrgInvItemsCreator : public IDataSource::ICreator
{
public:
	virtual const wchar_t* Name() const { return L"VTInvFrgItemsWriter"; }
	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
	{
		if (parent == NULL)
			return NULL;
		return new InvFrgItemsWriter((InvFrgWriter*)parent);

	}
};

class PriceReader : ISession::IHandler
{
public:
   PriceReader(ISession* session, const wchar_t *objectName);
   ~PriceReader();

   void AttachToSession();

   void GetPrice(std::vector<Object*> *dest) { MoveObjects(dest, price); }
   void GetFolders(std::vector<Object*> *dest) { MoveObjects(dest, folders); }
   void GetCost(std::set<PriceCostData> *dest);

   virtual void SessionClosed(ISession* sender);
   bool Read(const wchar_t* fileName, std::string& backFolder);

	bool IsManagerObject() const { return isManagerObject; }

protected:
   ISession* session;

   std::vector<Object*> price;
   std::vector<Object*> folders;
   std::set<PriceCostData> cost;
	bool isManagerObject;

   void MoveObjects(std::vector<Object*>* dest, std::vector<Object*> &src);
};

class PriceHolder : public std::map<ISession*, PriceReader*>
{
public:
   PriceHolder() {}
   ~PriceHolder()
   {
      PriceHolder::iterator i = begin();
      for( ; i != end(); i++ )
         delete i->second;
      clear();
   }
};
static void IncrementFileName(std::string* file);
static void MoveToArchive(const std::string& file, const std::string& arcFolder);

PriceHolder priceHolder;

PriceReader::PriceReader(ISession* session, const wchar_t *objectName)
{
   this->session = session;
	isManagerObject = (wcsncmp(objectName, L"Manager", 7) == 0);
}

void PriceReader::AttachToSession()
{
   session->AddHandler(this);
   priceHolder[session] = this;
}

PriceReader::~PriceReader()
{
   std::vector<Object*>::iterator i = price.begin();
   for( ; i != price.end(); i++ )
      delete (*i);
   price.clear();

   i = folders.begin();
   for( ; i != folders.end(); i++ )
      delete (*i);
   folders.clear();
}

void PriceReader::SessionClosed(ISession* sender)
{
   PriceHolder::iterator fnd = priceHolder.find(session);
   if( fnd != priceHolder.end() )
      priceHolder.erase(fnd);
   session = NULL;

   delete this;
}

void PriceReader::MoveObjects(std::vector<Object*>* dest, std::vector<Object*> &src)
{
   std::vector<Object*>::iterator i = src.begin();
   for( ; i != src.end(); i++ )
      dest->push_back( (*i) );
   
   src.clear();
}

void VTBaseReader::Close()
{
   std::vector<Object*>::iterator i = objects.begin();
   for( ; i != objects.end(); i++ )
      delete (*i);

   objects.clear();
}

bool VTBaseReader::MoveNext(Object *parentObject)
{
   return (objects.size() > 0);
}

bool VTBaseReader::Get(Object* o) const
{
   if( objects.size() == 0 )
      return false;

   Object* src = objects.front();
   src->MoveTo(o);
   delete src;

   objects.erase(objects.begin());
   return true;
}

class ItemReader
{
public:
   virtual ~ItemReader() {}
   virtual bool Read(const std::string& src, Object* obj, std::vector<std::wstring> *rest) = 0; 
};

class RestReader : public ItemReader
{
public:
   RestReader() {}
   virtual bool Read(const std::string& src, Object* obj, std::vector<std::wstring> *rest)
   {
      USES_CONVERSION;
      rest->push_back(A2W(src.c_str()));
      return true;
   }
};

class EmptyReader : public ItemReader
{
public:
   EmptyReader() {}
   virtual bool Read(const std::string& src, Object* obj, std::vector<std::wstring> *rest) { return true; }
};

class FieldReader : public ItemReader
{
public:
   static FieldReader* Create(int index, const MemberFormat& format);

protected:
   FieldReader(int index) { this->index = index; }

   int index;
};

class NumberReader : public FieldReader
{
public:
   NumberReader(int index) : FieldReader(index) {}
   virtual bool Read(const std::string& src, Object* obj, std::vector<std::wstring> *rest)
   {
		const char* str = src.c_str();
      obj->at(index).number = isDigit(str) ? atof(str) : 0;
      return true;
   }

	static bool isDigit(const char* str) {
		while (*str) {
			if (*str != '.' && !isdigit(*str))
				return false;
			str++;
		}

		return true;
	}
};

class StringReader : public FieldReader
{
public:
   StringReader(int index) : FieldReader(index) {}
   virtual bool Read(const std::string& src, Object* obj, std::vector<std::wstring> *rest)
   {
      USES_CONVERSION;
      obj->at(index).str->assign(A2W(src.c_str()));
      return true;
   }
};

FieldReader* FieldReader::Create(int index, const MemberFormat& format)
{
   FieldReader* ret = NULL;
   switch(format.type)
   {
   case MemberFormat::mtNumber:
      ret = new NumberReader(index);
      break;
   case MemberFormat::mtString:
      ret = new StringReader(index);
      break;
   }
   return ret;
}

class ReadHelper
{
public:
   ReadHelper(const GRServer::Format* format, const std::wstring& pattern);
   ~ReadHelper();

   bool ReadLine(FILE* f, Object* obj, std::vector<std::wstring> *rest);
   void ParseLine(std::string& line, Object* obj, std::vector<std::wstring> *rest);

private:
   std::vector<ItemReader*> readers;
};

ReadHelper::ReadHelper(const GRServer::Format* format, const std::wstring& pattern)
{
   std::wstring::size_type pos = 0;
   while( true )
   {
      std::wstring::size_type epos = pattern.find(L'|', pos);
      std::wstring item = pattern.substr(pos, (epos != std::wstring::npos) ? epos - pos : -1);
      
      if( item.empty() )
         readers.push_back(new RestReader());
      else
      {
         int idx = (format == NULL) ? -1 : format->FindMember(item.c_str());
         if( idx >= 0 )
         {
            FieldReader* fr = FieldReader::Create(idx, format->at(idx));
            if( fr != NULL )
            {
               readers.push_back(fr);
            } else
            {
               readers.push_back(new EmptyReader());
            }
         } else
         {
            if( format != NULL )
            {
               USES_CONVERSION;
               gServer->AddLog(IErrorLogger::Full, "ReaderHelper: no field %s in object %s", 
                  W2A(item.c_str()),
                  W2A(format->name.c_str()));
            }
         }
      }

      if( epos == std::wstring::npos ) break;
      pos = epos + 1;
   }
}

ReadHelper::~ReadHelper()
{
   std::vector<ItemReader*>::iterator i = readers.begin();
   for( ; i != readers.end(); i++ )
      delete (*i);
}

bool ReadHelper::ReadLine(FILE* f, Object* obj, std::vector<std::wstring> *rest)
{
   std::string line;
   if( !::ReadLine(&line, f) )
      return false;
   ParseLine(line, obj, rest);
   return true;
}

void ReadHelper::ParseLine(std::string& line, Object* obj, std::vector<std::wstring> *rest)
{
   std::vector<ItemReader*>::iterator ri = readers.begin();
   std::string::size_type pos = 0;
   while( ri != readers.end() )
   {
      std::string::size_type epos = line.find(L'|', pos);
      std::string item = line.substr(pos, (epos != std::string::npos) ? epos - pos : -1);
      
      (*ri)->Read(item, obj, rest);

      if( epos == std::string::npos ) break;
      pos = epos + 1;
      ri++;
   }
}

bool VTPODReader::Open(const std::wstring& fileName, const ISessionObject& object)
{
   USES_CONVERSION;
   const Session& s = (const Session&)object.GetSession();

   file.assign(s.Config().ExchangeFolder());
   file += W2A(fileName.c_str());

	std::string fileOld(file);
	file += ".bak";
	unlink(file.c_str());
	MoveFileA(fileOld.c_str(), file.c_str());

   FILE *f = fopen(file.c_str(), "rt");
   if( f == NULL )
   {
      gServer->AddLog(IErrorLogger::Full, "Open %s error %d", file.c_str(), GetLastError());
      return false;
   }
   const GRServer::Format* format = object.Self()->format;
   ReadHelper rh(format, L"|remark|number|type");
   int iCreated = format->FindMember(L"created");

   while(true)
   {
      std::vector<std::wstring> rest;
      Object* obj = Object::Create(*format);
      if( !rh.ReadLine(f, obj, &rest) )
      {
         delete obj;
         break;
      }
      SYSTEMTIME st = {0};
      if( rest.size() > 0 )
      {
         const wchar_t* cstr = rest[0].c_str();
         swscanf(cstr, L"%4d%2d%2d%2d%2d%2d", (int*)&st.wYear, (int*)&st.wMonth, (int*)&st.wDay, (int*)&st.wHour, (int*)&st.wMinute, (int*)&st.wSecond);
         SystemTimeToFileTime(&st, &obj->at(iCreated).datetime);
         gServer->AddLog(IErrorLogger::Full, "read status file %s created %s", file.c_str(), W2A(cstr));
      }
      objects.push_back(obj);
   }

   fclose(f);
   return true;
}

static void MoveToArchive(const std::string& file, const std::string& arcFolder)
{
   int pos = file.rfind(L'\\');
   int pos1 = file.rfind(L'/');
   if( pos < pos1 ) pos = pos1;
   std::string newFile(arcFolder);
   newFile += file.substr(pos+1);
   char buf[50];
   SYSTEMTIME st;
   GetLocalTime(&st);
   wsprintfA(buf, ".%d%02d%02d_%02d%02d%02d%d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond, st.wMilliseconds);
   newFile += buf;

   gServer->AddLog(IErrorLogger::Full, "MoveFile %s, %s", file.c_str(), newFile.c_str());
   
   USES_CONVERSION;
   SHCreateDirectory(NULL, A2W(arcFolder.c_str()));

   if( MoveFileA(file.c_str(), newFile.c_str()) == 0 )
      gServer->AddLog(IErrorLogger::Short, "MoveFile error %d", GetLastError());
}

void VTPODReader::Remove()
{
   if( !file.empty() )
   {
      if( !backupFolder.empty() )
         MoveToArchive(file, backupFolder);
      else
         _unlink(file.c_str());
   }
}

bool VTOrgReader::Open(const std::wstring& fileName, const ISessionObject& object)
{
   USES_CONVERSION;
   const Session& s = (const Session&)object.GetSession();

   std::string file(s.Config().ExchangeFolder());
   file += W2A(fileName.c_str());

   FILE *f = fopen(file.c_str(), "rt");
   if( f == NULL )
   {
      gServer->AddLog(IErrorLogger::Full, "Open %s error %d", file.c_str(), GetLastError());
      return false;
   }
   const GRServer::Format* format = object.Self()->format;

   const User& user = s.GetUser();
   int idx = user.format->FindMember(L"kisID");
   int iAdr, iInfo, iIdo, iId;
   iAdr = format->FindMember(L"address");
   iInfo = format->FindMember(L"info");
   iIdo = format->FindMember(L"ido");
   iId = format->FindMember(L"id");
   const wchar_t* kisid = (idx < 0) ? user.ID() : user.at(0)->at(idx).str->c_str();
   ReadHelper rh(format, L"|id|name|||balance|ido");

   while(true)
   {
      std::vector<std::wstring> rest;
      Object* obj = Object::Create(*format);
      if( !rh.ReadLine(f, obj, &rest) )
      {
         delete obj;
         break;
      }

		Member& mido = obj->at(iIdo);
		if( mido.str == NULL || ((std::wstring*)mido.str)->length() == 0 )
		{
			delete mido.str;
			mido.str = new CString(*obj->at(iId).str);
		}

      if( rest.size() > 1 )
      {
         const std::wstring &src = rest[1];
         int off = src.find(L"\\n");
         std::wstring adr = src.substr(0, off);
         std::wstring info;
         if( off != std::wstring::npos )
            info = src.substr(off+2);
         obj->at(iAdr).str->assign(adr);
         obj->at(iInfo).str->assign(info);
      }
      if( rest.size() > 0 && rest[0].compare(kisid) == 0 )
         objects.push_back(obj);
      else
         delete obj;
   }

   fclose(f);
   return true;
}

bool VTFridgeReader::Open(const std::wstring& fileName, const ISessionObject& object)
{
	USES_CONVERSION;
	const Session& s = (const Session&)object.GetSession();

	std::string file(s.Config().ExchangeFolder());
	file += W2A(fileName.c_str());

	FILE *f = fopen(file.c_str(), "rt");
	if (f == NULL)
	{
		gServer->AddLog(IErrorLogger::Full, "Open %s error %d", file.c_str(), GetLastError());
		return false;
	}
	const GRServer::Format* format = object.Self()->format;
	ReadHelper rh(format, L"id|ido|name|number|barcode");

	while (true)
	{
		std::vector<std::wstring> rest;
		Object* obj = Object::Create(*format);
		if (!rh.ReadLine(f, obj, &rest))
		{
			delete obj;
			break;
		}

		objects.push_back(obj);
	}

	fclose(f);
	return true;
}

static bool WaitForFileReady(const char* file, const char* waitStr)
{
   const DWORD MAX_FILE_WAIT = 3 * 60 * 1000;
   const DWORD SLEEP_TIME = 100;

   DWORD ct = GetTickCount();
   
   while( GetTickCount() - ct < MAX_FILE_WAIT )
   {
      FILE *f = fopen(file, "rt");
      if( f != NULL )
      {
         bool ready = false;
         std::string line;
         if( ReadLine(&line, f) )
            ready = ((int)line.find(waitStr) >= 0);
         fclose(f);
         if( ready )
            return true;
      }
      Sleep(SLEEP_TIME);
   }

   return false;
}

static Object* FindByName(int nameIndex, std::vector<Object*> &folders, const std::wstring& name)
{
   std::vector<Object*>::iterator i = folders.begin();
   for( ; i != folders.end(); i++ )
   {
      Object* o = (*i);
      if( o->at(nameIndex).str->compare(name) == 0 )
         return o;
   }

   return NULL;
}

void PriceReader::GetCost(std::set<PriceCostData> *dest)
{
   std::set<PriceCostData>::iterator i = cost.begin();
   for( ; i != cost.end(); i++ )
      dest->insert(*i);
   cost.clear();
}

static bool RequestPrice(const std::string& file, const User& user, const char* priceCode)
{
//#ifdef DEBUG
//   return true;
//#endif

   const wchar_t* userid = user.ID();
   const wchar_t* username = user.UserName();
   int idx = user.format->FindMember(L"kisID");
   const wchar_t* kisid = (idx < 0) ? userid : user.at(0)->at(idx).str->c_str();

   std::string fileName(file);
   while( IsFileExists(fileName) )
   {
      IncrementFileName(&fileName);
   }
   FILE *f;
   f = fopen(fileName.c_str(), "wt");

   if( f == NULL )
   {
      gServer->AddLog(IErrorLogger::Full, "Open %s error %d", file.c_str(), GetLastError());
      return false;
   }

   USES_CONVERSION;
   fprintf(f, "status|заказ прайса|||\n""client|ќбщий прайс-лист|%s|\n""manager|%s|%s|%s\n", 
      priceCode, W2A(userid), W2A(username), W2A(kisid));
   fclose(f);
   return true;
}

bool PriceReader::Read(const wchar_t* fileName, std::string& backFolder)
{
   USES_CONVERSION;
   std::string file(((Session*)session)->Config().ExchangeFolder());
   file += W2A(fileName);

   static std::string checkStr;
   if( checkStr.compare(file) == 0 )
      return false;
   checkStr = file;

   gServer->AddLog(IErrorLogger::Full, "Request price 00000000 %s", file.c_str());
   if( !RequestPrice(file, ((Session*)session)->GetUser(), "00000000") )
      return false;

   if( !WaitForFileReady(file.c_str(), "status|прайс|") )
   {
      gServer->AddLog(IErrorLogger::Full, "Wait answer timeout %s", file.c_str());
      return false;
   }


	const wchar_t* fldObj = isManagerObject ? L"ManagerFolder" : L"Folder";
	const wchar_t* prcObj = isManagerObject ? L"ManagerPrice" : L"Price";

   ((Session*)session)->GetObject(fldObj, NULL);
   ((Session*)session)->GetObject(prcObj, NULL);

   GRServer::Format *fldFormat = session->GetFormatList()->GetFormat( fldObj);
	GRServer::Format *priceFormat = session->GetFormatList()->GetFormat(prcObj);

   ReadHelper folderReader(fldFormat, L"|||");
   ReadHelper priceReader(priceFormat, L"name|qty|qtyInPack|||id||weight||volume|info|barcode");

   bool priceStarted = false;
   wchar_t curFolder[20];
   int index = 1;
   int folderNameIndex = fldFormat->FindMember(L"name");
   int folderIdIndex = fldFormat->FindMember(L"fid");
   int folderLevelIndex = fldFormat->FindMember(L"level");

   int priceIdIndex = priceFormat->FindMember(L"id");
   int priceFolderIndex = priceFormat->FindMember(L"fid");

   FILE *f = fopen(file.c_str(), "rt");
   if( f == NULL )
   {
      gServer->AddLog(IErrorLogger::Full, "Can't read file %s", file.c_str());
      return false;
   }

   try
   {
      while(true)
      {
         std::string line;
         if( !ReadLine(&line, f) )
            break;

         if( !priceStarted )
         {
            priceStarted = ((int)line.find("---") >= 0);
            continue;
         }

         std::vector<std::wstring> rest;
         if( ((int)line.find("cat|")) >= 0 )
         {
            Object* obj = Object::Create(*fldFormat);
            folderReader.ParseLine(line, obj, &rest); 
            Object* fndFld = FindByName(folderNameIndex, folders, rest[1]);
            if( fndFld == NULL )
            {
               fndFld = FindByName(folderNameIndex, folders, rest[2]);
               if( fndFld == NULL )
               {
                  Object* parentObj = Object::Create(*fldFormat);
                  _itow(index++, curFolder, 10);
                  parentObj->at(folderNameIndex).str->assign(rest[2]);
                  parentObj->at(folderLevelIndex).number = 0.0;
                  parentObj->at(folderIdIndex).str->assign(curFolder);
                  folders.push_back(parentObj);
               }

               _itow(index++, curFolder, 10);
               obj->at(folderNameIndex).str->assign(rest[1]);
               obj->at(folderLevelIndex).number = 1.0;
               obj->at(folderIdIndex).str->assign(curFolder);
               folders.push_back(obj);
            } else
            {
               delete obj;
               wcscpy(curFolder, fndFld->at(folderIdIndex).str->c_str());
            }
         } else
         {
            Object* obj = Object::Create(*priceFormat);
            priceReader.ParseLine(line, obj, &rest);

            PriceCostData pcd;
            pcd.id.assign((const std::wstring&)*obj->at(priceIdIndex).str);
            pcd.cost = _wtof(rest[1].c_str());
            cost.insert(pcd);

            obj->at(priceFolderIndex).str->assign(curFolder);
            price.push_back(obj);
         }
      }
   } catch(...)
   {
      gServer->AddError(false, "Exception при чтении PriceReader");
   }
   fclose(f);

   if( !backFolder.empty() )
   {
      MoveToArchive(file, backFolder); 
      file += "_all";
      MoveToArchive(file, backFolder); 
   } else {
      if(DeleteFileA(file.c_str()) == FALSE )
         gServer->AddLog(IErrorLogger::Full, "Delete file %s error %d", file.c_str(), GetLastError());
      
      file += "_all";
      if(DeleteFileA(file.c_str()) == FALSE )
         gServer->AddLog(IErrorLogger::Full, "Delete file %s error %d", file.c_str(), GetLastError());
   }

   return true;
}


bool VTPriceReader::Open(const std::wstring& fileName, const ISessionObject& object)
{
   ISession& s = (ISession&)object.GetSession();
	const std::wstring& name = object.Self()->format->name;
	
   PriceHolder::iterator fnd = priceHolder.find(&s);
   PriceReader *reader;
   if( fnd == priceHolder.end() )
   {
      reader = new PriceReader(&s, name.c_str());
      if( !reader->Read(fileName.c_str(), backupFolder) )
      {
         delete reader;
         return false;
      }
      reader->AttachToSession();
   }
   else
   {
      reader = fnd->second;
   }
   reader->GetPrice(&objects);
   return true;
}

bool VTFolderReader::Get(Object* o) const
{
   if( !VTBaseReader::Get(o) )
      return false;

   if( folderHolder && keyIndex >= 0 && valueIndex >= 0 )
   {
      o->at(valueIndex).number = curIndex++;
      folderHolder->SetValue(session, *o, keyIndex, valueIndex);
   }

   return true;
}

bool VTPriceCostReader::Open(const ISessionObject& object)
{
   ISession* session = (ISession*)&object.GetSession();
   PriceHolder::iterator fnd = priceHolder.find(session);
   if( fnd == priceHolder.end() )
      return false;

   costIndex = object.Self()->format->FindMember(L"cost");
   (fnd->second)->GetCost(&cost);
   return true;
}

bool VTPriceCostReader::MoveNext(Object *parentObject)
{
   if( idIndex == -1 )
      idIndex = parentObject->GetFormat().FindMember(L"id");
   
   current.id = (const std::wstring&)*parentObject->at(idIndex).str;
   std::set<PriceCostData>::iterator fnd = cost.find(current);
   if( fnd == cost.end() )
   {
      current.id.clear();
      return false;
   }
   current.cost = fnd->cost;
   cost.erase(fnd);
   return true;
}

bool VTPriceCostReader::Get(Object* o) const
{
   if( current.id.empty() )
      return false;
   o->at(costIndex).number = current.cost;
   return true;
}

bool VTFolderReader::Open(const std::wstring& fileName, const ISessionObject& object)
{
   session = (ISession*)&object.GetSession();
   PriceHolder::iterator fnd = priceHolder.find(session);
   PriceReader *reader = NULL;
   if( fnd == priceHolder.end() )
   {
		reader = new PriceReader(session, object.Self()->format->name.c_str());
      if( !reader->Read(fileName.c_str(), backupFolder) )
      {
         delete reader;
         return false;
      }
      reader->AttachToSession();
	}
	else
	{
		bool ism = (wcsncmp(object.Self()->format->name.c_str(), L"Manager", 7) == 0);
		if (fnd->second->IsManagerObject() == ism)
		{
			reader = fnd->second;
		}
	}
	if (reader)
		reader->GetFolders(&objects);

	GRServer::Format* f = object.Self()->format;
	if (wcsncmp(f->name.c_str(), L"Manager", 7) != 0)
	{
		folderHolder = (FolderIDHolder*)gServer->GetService(FOLDER_ID_SERVICE);
		valueIndex = f->FindMember(L"id");
	}

   keyIndex = f->FindMember(L"fid");

   return true;
}

bool VTCostReader::Open(const std::wstring& fileName, const ISessionObject& object)
{
   USES_CONVERSION;

   Session& s = (Session&)object.GetSession();
   std::string file(s.Config().ExchangeFolder());
   file += W2A(fileName.c_str());
   
   const GRServer::Format* format = object.Self()->format;
   int index = format->FindMember(L"data");

	std::map<std::wstring, std::set<std::wstring>> actions;
   Binary* b = LoadCost(file, s.GetUser(), &actions);
   if( b != NULL )
   {
      Object *obj = Object::Create(*format);
      obj->at(index).binary = new MemoryBinary(b);
      objects.push_back(obj);

		if( actions.size() > 0 )
		{
			ISessionObject* io = s.GetObject(L"StockOrg", NULL);
			if( io != NULL)
			{
				GRServer::Format* itemsFormat = s.GetFormatList()->GetFormat(L"StockOrg$items");
				ServObject *so = (io != NULL) ? io->Self() : NULL;
				int iid = -1, iitems = -1;
				if (so != NULL)
				{
					iid = so->format->FindMember(L"id");
					iitems = so->format->FindMember(L"items");
				}
				if (iid < 0 || iitems < 0)
					so = NULL;

				std::map<std::wstring, std::set<std::wstring>>::const_iterator i = actions.begin();
				for( ; i != actions.end(); i++ )
				{
					if (so != NULL && i->second.size() > 0)
					{
						GRServer::Object *obj = so->AddObject();
						obj->at(iid).str->assign(i->first);
						ServObject *child = new ServObject(itemsFormat);
						obj->at(iitems).object = child;

						std::set<std::wstring>::const_iterator j = i->second.begin();
						for (; j != i->second.end(); j++)
						{
							GRServer::Object *objI = child->AddObject();
							objI->at(0).str->assign(*j);
						}
					}
				}
				if(so != NULL)
					object.GetSession().AddToAnswer(so);
			}
		}

      return true;
   }

   return false;
}

typedef std::map<std::wstring, DWORD> OrgCost;

static bool LoadPrice(FILE* f, std::set<std::wstring>* allItems, std::map<std::wstring, OrgCost*>* orgsCost, 
							 DWORD *headSize, DWORD *priceSize, std::map<std::wstring, std::set<std::wstring>> *actions)
{
   OrgCost* curOrg = NULL;
	std::wstring orgId;
   ReadHelper reader(NULL, L"|||");
   bool readPrice = false;
   while( true )
   {
      std::vector<std::wstring> rest;
      if( !reader.ReadLine(f, NULL, &rest) )
         break;

      if( rest[0].compare(L"client") == 0 )
      {
         orgId = rest[2];
         std::map<std::wstring, OrgCost*>::iterator fnd = orgsCost->find(orgId);
         if( fnd != orgsCost->end() )
         {
            fnd->second->clear();
         } else 
         {
            curOrg = new OrgCost();
            (*orgsCost)[orgId] = curOrg;
            *headSize += (orgId.size() + 1) * 2; // код цены - ее же название
         }
         readPrice = false;
         continue;
      }
      if( curOrg == NULL )
         continue;
      if( rest[0].compare(L"---") == 0 )
      {
         readPrice = true;
         continue;
      }
      if( !readPrice )
         continue;
      if( rest.size() < 2 )
         continue;

      const std::wstring& priceId = rest[1];
      if(allItems->insert(priceId).second == true)
         *priceSize += priceId.size() + 1;

      (*curOrg)[rest[1]] = ScaleDouble(_wtof(rest[0].c_str()), SUM_SCALE);
		if( rest.size() > 2 )
		{
			if (_wtoi(rest[2].c_str()) == 1)
			{
				(*actions)[orgId].insert(priceId);
				//actions->insert(orgId);
			}
		}
   }

   return true;
}

Binary* VTCostReader::LoadCost(const std::string& fileName, const User& user, std::map<std::wstring, std::set<std::wstring>> *actions)
{
   gServer->AddLog(IErrorLogger::Full, "Request cost 99999999 %s", fileName.c_str());
   if( !RequestPrice(fileName, user, "99999999") )
      return NULL;

   std::string file(fileName);
   std::string file1(file);

   file += "_all";
   if( !WaitForFileReady(file.c_str(), "status|прайс_по_всем_клиентам|") )
   {
      gServer->AddLog(IErrorLogger::Full, "Wait answer timeout %s", file.c_str());
      return NULL;
   }

   FILE *f = fopen(file.c_str(), "rt");
   if( f == NULL )
   {
      gServer->AddLog(IErrorLogger::Full, "Open file %s error %d", file.c_str(), GetLastError());
      return NULL;
   }

   // реализован алгоритм - каждому контрагенту сво€ цена на товар
   std::set<std::wstring> price;
   std::map<std::wstring, OrgCost*> orgsCost;

   DWORD typeSize = 0, priceSize = 0;
   LoadPrice(f, &price, &orgsCost, &typeSize, &priceSize, actions);
   fclose(f);

   if( !backupFolder.empty() )
   {
      MoveToArchive(file1, backupFolder); 
      MoveToArchive(file, backupFolder); 
   } else {
      if( DeleteFileA(file1.c_str()) == FALSE )
         gServer->AddLog(IErrorLogger::Full, "Delete file %s error %d", file1.c_str(), GetLastError());
      if( DeleteFileA(file.c_str()) == FALSE )
         gServer->AddLog(IErrorLogger::Full, "Delete file %s error %d", file.c_str(), GetLastError());
   }

   Costs header;
   header.priceCount = (WORD)price.size();
   header.costCount = (WORD)orgsCost.size();
   header.costTypeOffset = priceSize + sizeof(Costs);
   header.priceOffset = header.costTypeOffset + typeSize;

   priceSize = price.size() * orgsCost.size() * sizeof(DWORD);

   DWORD size = header.priceOffset + priceSize;
   BYTE *data = (BYTE*)malloc(size);
   memset(data, 0, size);

   Binary *ret = new Binary(data, size);

   *(Costs*)data = header;
   char *destPrice = (char*)data + sizeof(header);
   DWORD *cost = (DWORD*)(data + header.priceOffset);
   char *destType = (char*)data + header.costTypeOffset;
   bool typesWrited = false;

   USES_CONVERSION;
   std::set<std::wstring>::const_iterator i = price.begin();
   for( ; i != price.end(); i++ )
   {
      // put price id
      strcpy(destPrice, W2A(i->c_str()));
      destPrice += i->size() + 1;

      std::map<std::wstring, OrgCost*>::const_iterator pi = orgsCost.begin();
      for( ; pi != orgsCost.end(); pi++ )
      {
         // put cost type id & name
         if( !typesWrited )
         {
            const char* orgId = W2A(pi->first.c_str());
            strcpy(destType, orgId);
            destType += pi->first.size() + 1;
            strcpy(destType, orgId);
            destType += pi->first.size() + 1;
         }

         // put cost
         OrgCost::const_iterator fnd = pi->second->find(*i);
         *cost++ = (fnd == pi->second->end()) ? 0 : fnd->second;
      }

      typesWrited = true;
   }

   std::map<std::wstring, OrgCost*>::iterator oi = orgsCost.begin();
   for( ; oi != orgsCost.end(); oi++ )
      delete oi->second;

   return ret;
}

//
//----------------------------------------- Order Writers --------------------------------------------------
//
void ReservReader::Init(GRServer::SessionObject *reserv)
{
   Session& s = (Session&)reserv->GetSession();
   itemsFormat = s.GetFormatList()->GetFormat(L"OrderReserv$items");
   if( itemsFormat == NULL )
      return;

   const GRServer::Format *f = reserv->Self()->format;
   iId = f->FindMember(L"id");
   iDate = f->FindMember(L"date");
   iNumber = f->FindMember(L"number");
   iItems = f->FindMember(L"items");
   iCreated = f->FindMember(L"created");

   iiId = itemsFormat->FindMember(L"id");
   iiQty = itemsFormat->FindMember(L"qty");
   iiCost = itemsFormat->FindMember(L"cost");

   dateParser.SetFormat(L"D/M/YYYY");
}

bool ReservReader::ReadTo(GRServer::SessionObject *dest, const std::string &fileName, const FILETIME& created)
{
   if( iId < 0 || iDate < 0 || iNumber < 0 || iItems < 0 || iiId < 0 || iiQty < 0 || iiCost < 0 || iCreated < 0 )
      return false;

   FILE *f = fopen(fileName.c_str(), "rt");
   if( f == NULL )
   {
      return false;
   }

   Object* od = dest->AddObject();
   ServObject *child = new ServObject((GRServer::Format*)itemsFormat);
   od->at(iItems).object = child;

   od->at(iCreated).datetime = created;

   bool readItems = false;
   ReadHelper rh(NULL, L"|||||||");
   while( true )
   {
      std::string line;
      if( !::ReadLine(&line, f) )
         break;
      
      std::vector<std::wstring> rest;
      rh.ParseLine(line, NULL, &rest);

      if( !readItems )
      {
         if( rest[0].compare(L"status") == 0 )
         {
            od->at(iNumber).str->assign(rest[2]);
         } else if( rest[0].compare(L"client") == 0 )
         {
            od->at(iId).str->assign(rest[2]);
            dateParser.FromString(&od->at(iDate).datetime, rest[5].c_str());
         } else if( rest[0].compare(L"---") == 0 )
         {
            readItems = true;
         }
         continue;
      }

      Object* chO = child->AddObject();
      chO->at(iiId).str->assign(rest[0]);
      chO->at(iiQty).number = _wtof(rest[2].c_str());
      chO->at(iiCost).number = _wtof(rest[4].c_str());
   }
   fclose(f);
   return true;
}

OrderItemsWriter::OrderItemsWriter(OrderWriter* parent) : destFile(NULL), iId(-1), iQty(-1), childIndex(-1)
{
   parent->SetItemsWriter(this);
}

bool OrderItemsWriter::Prepare(const ISessionObject& object)
{
   const ISessionObject* parent = object.Parent();
   if( parent == NULL )
   {
      gServer->AddLog(IErrorLogger::Full, "OrderItemsWriter::Prepare no parent");
      return false;
   }

   const GRServer::Format* format = object.Self()->format;
   int off = format->name.find_last_of(L'$');
   childIndex = parent->Self()->format->FindMember(format->name.substr(off+1).c_str());

   iId = format->FindMember(L"id");
   iQty = format->FindMember(L"qty");

   if(childIndex < 0 || iId < 0 || iQty < 0)
   {
      gServer->AddLog(IErrorLogger::Full, "OrderItemsWriter::Prepare (childIndex < 0 || iId < 0 || iQty < 0)");
      return false;
   }
   return true;
}

bool OrderItemsWriter::Write(const Object& o, RowID *rid)
{
   if( destFile == NULL )
      return false;
   
   fputs("---\n", destFile);
   const Member& m = o.at(childIndex);
   if( m.object != NULL )
   {
      USES_CONVERSION;
      ServObject::const_iterator i = m.object->begin();
      for( ; i != m.object->end(); i++ )
      {
         fprintf(destFile, "%s|%d|||", W2A((*i)->at(iId).str->c_str()), (int)((*i)->at(iQty).number + 0.5));
			WriteObjectData(destFile, *(*i));
			fprintf(destFile, "\n");
      }
   }

   destFile = NULL;
   return true;
}


bool OrderWriter::Prepare(const ISessionObject& object)
{
   USES_CONVERSION;
   Session& s = (Session&)object.GetSession();
   const User& user = s.GetUser();
   
   userId = W2A(user.ID());
   userName = W2A(user.UserName());

   const GRServer::Format *format = object.Self()->format;
   iId = format->FindMember(L"id");
   iDate = format->FindMember(L"date");
   iCreated = format->FindMember(L"created");
   iRemark = format->FindMember(L"remark");
   iOrgName = format->FindMember(L"orgName");
	//iRet = format->FindMember(L"ret");

   reserv = GetReservObject(s);
   if( reserv != NULL )
      reservWriter.Init(reserv);

   createdFormat.SetFormat(L"YYYYMMDDHHmmSS");
   return true;
}

bool OrderCancelWriter::Prepare(const ISessionObject& object)
{
   if( !OrderWriter::Prepare(object) )
      return false;

   const GRServer::Format *format = object.Self()->format;
   iNumber = format->FindMember(L"number");
   if( iNumber < 0 )
      number = "0";

   return true;
}

static bool WriteConfirm(const char* fileName, const char* headText)
{
   FILE *f = fopen(fileName, "rt");
   if( f == NULL )
   {
      gServer->AddLog(IErrorLogger::Short, "WriteConfirm open for read file %s error %d", fileName, GetLastError());
      return false;
   }

   std::vector<std::string> lines;
   std::string line;
   while( ::ReadLine(&line, f) )
      lines.push_back(line);
   fclose(f);

   if( lines.size() > 0 )
   {
      std::string& first = lines.at(0);
      
      int pos1 = first.find('|');
      int pos2 = first.find('|', pos1+1);
      std::string head(headText);
      head += first.substr(pos2+1);

      first.assign(head);
   }

   f = fopen(fileName, "wt");
   if( f == NULL )
   {
      gServer->AddLog(IErrorLogger::Short, "WriteConfirm open for write file %s error %d", fileName, GetLastError());
      return false;
   }
   std::vector<std::string>::const_iterator i = lines.begin();
   for( ; i != lines.end(); i++ )
   {
      fputs(i->c_str(), f);
      fputs("\n", f);
   }
   fclose(f);

   return true;
}

void OrderWriter::Close()
{
   itemsWriter = NULL;
   if( reserv != NULL && reserv->size() > 0 )
   {
      Session& s = (Session&)reserv->GetSession();
      bool writeFormat = true;
      while( reserv->MoveNext() )
      {
         s.WriteToStream(*reserv, writeFormat);
         writeFormat = false;
      }
   }
}

bool OrderWriter::Write(const Object& o, RowID *rid)
{
   if( iId < 0 || iDate < 0 || iCreated < 0 || iRemark < 0 || itemsWriter == NULL )
   {
      gServer->AddLog(IErrorLogger::Full, "OrderWriter iId < 0 || iDate < 0 || iCreated < 0 || iRemark < 0 || itemsWriter == NULL");
      return false;
   }

   while( IsFileExists(fileName) )
   {
      IncrementFileName(&fileName);
   }

   FILE* f = fopen(fileName.c_str(), "wt");
   if( f == NULL )
   {
      gServer->AddLog(IErrorLogger::Short, "Can't open file %s error %d", fileName.c_str(), GetLastError());
      return false;
   }

   SYSTEMTIME st;
   USES_CONVERSION;
   char* remark = RemoveNewLines(_strdup(W2A(o.at(iRemark).str->c_str())));
   const char* orgName = (iOrgName < 0) ? "" : W2A(o.at(iOrgName).str->c_str());
   std::wstring crbuf;

   createdFormat.ToString(&crbuf, o.at(iCreated).datetime);
	//isRet = 0;
	//if (iRet >= 0)
	//{
	//	isRet = o.at(iRet).number > 0 ? 1 : 0;
	//}

   FileTimeToSystemTime(&o.at(iDate).datetime, &st);
   const char* id = W2A(o.at(iId).str->c_str());
   fprintf(f, "status|%s|%s|||\n""client|%s|%s|||%d/%d/%d|||on|||\n""manager|%s|%s|%s||%s\n",
      GetStatus(),GetNumber(),
      orgName, id, st.wDay, st.wMonth, st.wYear, userId.c_str(),
      userName.c_str(), remark, W2A(crbuf.c_str()));
   itemsWriter->SetDestFile(f);
   itemsWriter->Write(o, rid);
   fclose(f);
   free(remark);
   gServer->AddLog(IErrorLogger::Full, "Write Order (or OrderCancel) file %s", fileName.c_str());

//#ifdef DEBUG
//#else
   //if( !WaitForFileReady(fileName.c_str(), "status|резерв|") )
   //{
   //   gServer->AddLog(IErrorLogger::Short, "OrderWriter status|резерв timeout for %s", fileName.c_str());
   //   return false;
   //}

   //if( !WriteConfirm(fileName.c_str(), "status|подтверждение|") )
   //   return false;

   //if( !WaitForFileReady(fileName.c_str(), "status|подтверждение2|") )
   //{
   //   gServer->AddLog(IErrorLogger::Short, "OrderWriter status|подтверждение2 timeout for %s", fileName.c_str());
   //   return false;
   //}

   //if( reserv != NULL )
   //   reservWriter.ReadTo(reserv, fileName.c_str(), o.at(iCreated).datetime);
//#endif

   return true;
}

bool OrderCancelWriter::Write(const Object& o, RowID *rid)
{
   USES_CONVERSION;
   if( iNumber >= 0 )
      number = W2A(o.at(iNumber).str->c_str());

   std::wstring crbuf;
   createdFormat.ToString(&crbuf, o.at(iCreated).datetime);
   gServer->AddLog(IErrorLogger::Full, "try OrderCancel write created=%s number=%s", W2A(crbuf.c_str()), number.c_str());

   return OrderWriter::Write(o, rid);
}

bool VTReqClientsCreator::Writer::Prepare(const GRServer::ISessionObject &object)
{
   FILE *wr = fopen(fileName.c_str(), "wt");
   if( wr == NULL )
   {
      gServer->AddLog(IErrorLogger::Short, "VTReqClientsCreator file (%s) open error %d", fileName.c_str(), GetLastError());
      return false;
   }

   fprintf(wr, "status|заказ списка клиентов|\n");
   fclose(wr);
   return true;
}

//
//-------------------------------------------- Creators -----------------------------------------------------
//
IDataSource::IReader* VTPriceCostCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   VTPriceCostReader *reader = new VTPriceCostReader();
   if( !reader->Open(object) )
   {
      delete reader;
      return NULL;
   }

   return reader;
}

IDataSource::IReader* VTBaseCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   VTBaseReader* reader = NULL;
   const Parameter* p = parameters.Find(L"fileName", -1);
   if( p != NULL )
   {
      CString *fileName = NULL;
      const ISession& session = object.GetSession();
      if( session.Parse(&fileName, p->value, &object) )
      {
         reader = CreateReader();

         USES_CONVERSION;
         p = parameters.Find(L"backupFolder", -1);
         if( p != NULL )
         {
            CString *back = NULL;
            if( session.Parse(&back, p->value, &object) && back != NULL )
            {
               std::string file(((const Session&)session).Config().ExchangeFolder());
               file += W2A(back->c_str());
               char sym = *file.rbegin();
               if( sym != '\\' && sym != '/' )
                  file += "\\";
               reader->SetBackupFolder(file);
            }
            delete back;
         }

         gServer->AddLog(IErrorLogger::Short, "Try open %s", W2A(fileName->c_str()));

         if( reader->Open((const std::wstring&)*fileName, object) )
         {
            return reader;
         }
      }
      delete fileName;
   }

   USES_CONVERSION;
   gServer->AddLog(IErrorLogger::Short, "Can't create %s reader", W2A(object.Self()->Name().c_str()));
   delete reader;
   return NULL;
}

OrderWriter* VTOrderCreator::CreateWriter(const char* fileName) const
{
   return new OrderWriter(fileName);
}

IDataSource::IWriter* VTOrderCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   OrderWriter* writer = NULL;
   const Parameter* p = parameters.Find(L"fileName", -1);
   if( p != NULL )
   {
      CString *fileName = NULL;
      const Session& session = (Session&)object.GetSession();
      if( session.Parse(&fileName, p->value, &object) )
      {
         USES_CONVERSION;
         std::string file(session.Config().ExchangeFolder());
         file += W2A(fileName->c_str());
         writer = CreateWriter(file.c_str());

      }
      delete fileName;
   }

   return writer;
}

OrderWriter* VTOrderCancelCreator::CreateWriter(const char* fileName) const
{
   return new OrderCancelWriter(fileName);
}

IDataSource::IWriter* VTOrderItemsCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   if( parent == NULL )
      return NULL;
   return new OrderItemsWriter((OrderWriter*)parent);
}

class ReturnWriter : public OrderWriter
{
public:
	ReturnWriter(const char *fileName) : OrderWriter(fileName) {}
protected:
	virtual const char* GetStatus() const { return "возврат"; }
	virtual SessionObject* GetReservObject(Session& s) const { return NULL; }
};

class ReturnItemsWriter : public OrderItemsWriter
{
public:
	ReturnItemsWriter(OrderWriter *parent) : OrderItemsWriter(parent) {}

	virtual bool Prepare(const ISessionObject& object);
	virtual void WriteObjectData(FILE* destFile, const Object& o);
private:
	int iExpDate, iProdDate;
};

bool ReturnItemsWriter::Prepare(const ISessionObject& object)
{
	if (!OrderItemsWriter::Prepare(object))
		return false;

	const GRServer::Format* format = object.Self()->format;
	iExpDate = format->FindMember(L"expired");
	iProdDate = format->FindMember(L"production");
	return true;
}

static void MakeStrDate(std::string* out, const FILETIME& src)
{
	SYSTEMTIME st;
	FileTimeToSystemTime(&src, &st);
	if (st.wYear < 2000)
	{
		out->assign("");
	}
	else
	{
		char buf[40];
		sprintf(buf, "%02d.%02d.%d", st.wDay, st.wMonth, st.wYear);
		out->assign(buf);
	}
}

void ReturnItemsWriter::WriteObjectData(FILE* destFile, const Object& o)
{
	std::string prod, exp;
	if (iProdDate >= 0)
	{
		MakeStrDate(&prod, o.at(iProdDate).datetime);
	}

	if (iExpDate >= 0)
	{
		MakeStrDate(&exp, o.at(iExpDate).datetime);
	}
	fprintf(destFile, "%s|%s|", prod.c_str(), exp.c_str());
}

OrderWriter* VTReturnCreator::CreateWriter(const char* fileName) const
{
	return new ReturnWriter(fileName);
}

IDataSource::IWriter* VTReturnItemsCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
	if (parent == NULL)
		return NULL;
	return new ReturnItemsWriter((OrderWriter*)parent);
}


} // namespace VyatichNamespace


bool GRServer::AddOnInit()
{
   DataSource::AddCreator(new VyatichNamespace::VTOrgCreator());
   DataSource::AddCreator(new VyatichNamespace::VTPriceCreator());
   DataSource::AddCreator(new VyatichNamespace::VTFolderCreator());
   DataSource::AddCreator(new VyatichNamespace::VTPriceCostCreator());
   DataSource::AddCreator(new VyatichNamespace::VTCostCreator());
   DataSource::AddCreator(new VyatichNamespace::VTOrderCreator());
   DataSource::AddCreator(new VyatichNamespace::VTOrderItemsCreator());
   DataSource::AddCreator(new VyatichNamespace::VTPODCreator());
   DataSource::AddCreator(new VyatichNamespace::VTOrderCancelCreator());
   DataSource::AddCreator(new VyatichNamespace::VTOrderCancelItemsCreator());
   DataSource::AddCreator(new VyatichNamespace::VTReqClientsCreator());
	DataSource::AddCreator(new VyatichNamespace::VTReturnCreator());
	DataSource::AddCreator(new VyatichNamespace::VTReturnItemsCreator());
	DataSource::AddCreator(new VyatichNamespace::VTFrgInvCreator());
	DataSource::AddCreator(new VyatichNamespace::VTFrgInvItemsCreator());
	DataSource::AddCreator(new VyatichNamespace::VTFrigeCreator());

   return true;
}


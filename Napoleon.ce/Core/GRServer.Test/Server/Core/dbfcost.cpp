/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * DBFCost
 *
 * ert   25/08/2010   creating
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

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#include "creators.h"
#include "objects.h"
#include "dbf.h"
#include "StdConsts.h"
#include "cost.h"

#ifdef UNIX
#include <glob.h>
#endif

using namespace GRServer;
using namespace std;

CostService costService;

class CostReader : public IDataSource::IReader
{
public:
   CostReader(const std::wstring& costFile, const std::wstring& costTypeFile, const SessionObject& object, 
		const char* priceIDField, const char* costTypeField, const std::string& dataFields);

   virtual bool MoveNext(Object *parentObject);
   virtual bool Get(Object* o) const;
   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return false; }
   virtual void Remove() {}
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
   virtual const Member* Value(const wchar_t* name) const { return NULL; }

protected:
   void LoadPriceIDS(Session* session);

	std::vector<std::string> dataFields;

   std::set<std::string> priceIDS;
   std::string costFile, costTypeFile, userID, exchangeFolder, costTypeField, priceIDField;
   int dataIndex;
   bool readed;

   mutable Binary *data;
};

IDataSource::IReader* CostCreator::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
   IDataSource::IReader *reader = NULL;

   std::wstring costFile, costTypeFile;

   const Session& session = (Session&)object.GetSession();
   const Parameter* prm1 = parameters.Find(L"cost", 0);
   const Parameter* prm2 = parameters.Find(L"costType", 1);

	const char *priceIDField = "ID";
	const char *costTypeField = "IDC";
	const char *dataFields = "COST";
	wstring tval1;
	wstring tval;
	string tdf;

	USES_CONVERSION;
	const Parameter* prm = parameters.Find(L"costType_PriceID", -1);
	if( prm != NULL )
	{
		if( session.Parse(&tval1, prm->value,(const SessionObject*)object.Self()) )
			priceIDField = W2A(tval1.c_str());
	}
	prm = parameters.Find(L"costType_TypeID", -1);
	if( prm != NULL )
	{
		if( session.Parse(&tval, prm->value,(const SessionObject*)object.Self()) )
			costTypeField = W2A(tval.c_str());
	}

	prm = parameters.Find(L"dataFields", -1);
	if (prm != NULL)
	{
		wstring tdfw;
		if (session.Parse(&tdfw, prm->value, (const SessionObject*)object.Self()))
		{
			USES_CONVERSION;
			tdf = W2A(tdfw.c_str());
			dataFields = tdf.c_str();
		}
	}


	if( prm2 != NULL )
		session.Parse(&costTypeFile, prm2->value, (const SessionObject*)object.Self());
   if( prm1 != NULL && session.Parse(&costFile, prm1->value, (const SessionObject*)object.Self())) 
   {
      reader = new CostReader(costFile, costTypeFile, *(const SessionObject*)object.Self(), priceIDField, costTypeField, dataFields);
   }

   return reader;
}

IDataSource::IWriter* CostCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   return NULL;
}

IDataSource::IRemover* CostCreator::CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const
{
   return NULL;
}

static void StrToList(std::vector<std::string>* fields, const std::string& str, char sepSym = ',')
{
	string::const_iterator si = str.begin(), ei = str.end();
	string f;
	for (; si != ei; si++)
	{
		char sym = *si;

		if (sym == sepSym)
		{
			if (!f.empty())
			{
				size_t start = f.find_first_not_of(L' ');
				fields->push_back(f.substr(start, f.size() - start));
			}
			f.clear();
		}
		else
			f.append(1, sym);
	}
	if (!f.empty())
	{
		size_t start = f.find_first_not_of(' ');
		fields->push_back(f.substr(start, f.size() - start));
	}
}

CostReader::CostReader(const std::wstring& costFile, const std::wstring& costTypeFile, const SessionObject& object, const char* priceIDField, const char* costTypeField, const std::string& dataFields)
{
   USES_CONVERSION;

   this->costFile = W2A(costFile.c_str());
   this->costTypeFile = W2A(costTypeFile.c_str());

   Session *session = (Session*)&object.GetSession();
   userID = W2A(session->GetUser().ID());

   dataIndex = object.format->FindMember(L"data");
   readed = false;
   data = NULL;

   exchangeFolder = session->Config().ExchangeFolder();
	LoadPriceIDS(session);

	StrToList(&this->dataFields, dataFields);

	this->costTypeField = costTypeField;
	this->priceIDField = priceIDField;
}

static bool LoadTypes(std::vector<CostType> *types, std::set<CTypeSet> *typeset, const std::string& fileName)
{
   DataForm ct;
   if( !ct.Open(fileName.c_str()) )
      return false;

   long rc;
   char buf[300];
   std::string sbuf;
   for( rc = 0; ct.ReadRec(rc); rc++ )
   {
      CostType ctype;

      string val = Trunc(ct["ID"], &sbuf);
		if (DBF_CODE_PAGE == CP_OEMCP)
		{
			OemToCharBuffA(val.c_str(), buf, (DWORD)val.size());
			ctype.id.assign(buf, val.size());
		} else
			ctype.id.assign(val);

      const char* p = ct["NAME"];
      val = (p == NULL) ? "" : Trunc(p, &sbuf);
		if (DBF_CODE_PAGE == CP_OEMCP)
		{
			OemToCharBuffA(val.c_str(), buf, (DWORD)val.size());
			ctype.name.assign(buf, val.size());
		} else
			ctype.name.assign(val);

      CTypeSet cts;
      cts.id = ctype.id;
      cts.index = (long)types->size();

      if( typeset->insert(cts).second )
		{
         types->push_back(ctype);
		}
   }

   return true;
}

static bool LoadCosts(std::set<PriceCostItem> *price, std::set<CTypeSet> *typeset,
                      const std::string &fName,  const std::string& userID, 
							 const std::set<std::string> &priceIDS, const char* priceIDField, const char* costTypeField, 
							 bool loadCostTypes, std::vector<CostType> *types, const std::vector<std::string>& dataFields)
{
   string fileName(fName);
   fileName += userID;
   fileName += ".dbf";

#ifdef UNIX
   glob_t gfnd;
   glob(fileName.c_str(), 0, NULL, &gfnd);
   if( gfnd.gl_pathc == 0 )
   {
      fileName = fName + ".dbf";
      glob(fileName.c_str(), 0, NULL, &gfnd);
      if( gfnd.gl_pathc == 0 )
         return false;
   }
   globfree(&gfnd);
#else
   WIN32_FIND_DATAA data;
   HANDLE handle = FindFirstFileA(fileName.c_str(), &data);
   if( handle == INVALID_HANDLE_VALUE )
   {
      FindClose(handle);
      fileName = fName + ".dbf";
      handle = FindFirstFileA(fileName.c_str(), &data);
      if( handle == INVALID_HANDLE_VALUE )
      {
         FindClose(handle);
         return false;
      }
   }
   FindClose(handle);
#endif

   DataForm ct;
   if( !ct.Open(fileName.c_str()) )
      return false;

   char buf[300];
   PriceCostItem pi;
   CTypeSet ts;
   long rc;
   std::string sbuf;
	std::vector<int> fIdx;
	std::vector<std::string>::const_iterator dfi = dataFields.begin();
	for (; dfi != dataFields.end(); dfi++)
		fIdx.push_back(ct.Field(dfi->c_str()));

   for( rc = 0; ct.ReadRec(rc); rc++ )
   {
      string val = Trunc(ct[priceIDField], &sbuf);
      if( priceIDS.find(val) == priceIDS.end() )
         continue;

		if (DBF_CODE_PAGE == CP_OEMCP)
		{
			OemToCharBuffA(val.c_str(), buf, (DWORD)val.size());
			pi.id.assign(buf, val.size());
		}
		else
			pi.id.assign(val);


      val = Trunc(ct[costTypeField], &sbuf);
		if (DBF_CODE_PAGE == CP_OEMCP)
		{
			OemToCharBuffA(val.c_str(), buf, (DWORD)val.size());
			ts.id.assign(buf, val.size());
		}
		else
			ts.id.assign(val);

      std::set<CTypeSet>::const_iterator typef = typeset->find(ts);
      if( typef == typeset->end() )
		{
			if( loadCostTypes == false )
				continue;
					
			ts.index = (long)types->size();
			typef = typeset->insert(ts).first;

			CostType ctype;
			ctype.id = ts.id;
			types->push_back(ctype);

			//*typeHeadSize += (DWORD)(ctype.id.size() + 1);
			//*typeHeadSize += (DWORD)(ctype.name.size() + 1);
		}

      std::set<PriceCostItem>::iterator fnd = price->find(pi);
      if( fnd == price->end() )
      {
         pi.costs.clear();
         fnd = price->insert(pi).first;
      }
		std::vector<ItemCostData>& cs = const_cast<std::vector<ItemCostData>&>(fnd->costs);
		if( (int)cs.size() <= typeset->size() )
			cs.resize(typeset->size());
		cs[typef->index].clear();
		for (unsigned idx = 0; idx < fIdx.size(); idx++) 
		{
			int fi = fIdx[idx];
			cs[typef->index].push_back(fi < 0 ? 0 : ((DWORD)ScaleDouble(atof(ct[fi]), SUM_SCALE)));
		}
      //cs[typef->index] = (DWORD)ScaleDouble(atof(ct["COST"]), SUM_SCALE);
   }

   return true;
}

Binary* CostService::MakeCostsBinary(const vector<CostType> &types, const set<PriceCostItem>& price, DWORD dataFieldCount)
{
	Costs header;
	DWORD typeSize = 0;
	DWORD priceSize = 0;
	vector<CostType>::const_iterator cti = types.begin();
	for (; cti != types.end(); cti++)
	{
		typeSize += (DWORD)(cti->id.size() + 1);
		typeSize += (DWORD)(cti->name.size() + 1);
	}

	set<PriceCostItem>::const_iterator pi = price.begin();
	for (; pi != price.end(); pi++)
	{
		priceSize += (DWORD)(pi->id.size() + 1);
	}

	header.priceCount = (WORD)price.size();
	header.costCount = (WORD)types.size();
	header.costTypeOffset = priceSize + sizeof(Costs);
	header.priceOffset = header.costTypeOffset + typeSize;

	priceSize = (DWORD)(price.size() * types.size() * (sizeof(DWORD) * dataFieldCount));

	DWORD size = header.priceOffset + priceSize;
	BYTE *data = (BYTE*)malloc(size);
	memset(data, 0, size);

	Binary *ret = new Binary(data, size);

	*(Costs*)data = header;
	char *dest = (char*)data + sizeof(header);
	DWORD *cost = (DWORD*)(data + header.priceOffset);

	pi = price.begin();
	for (; pi != price.end(); pi++)
	{
		vector<ItemCostData>::const_iterator ci = pi->costs.begin();
		for (; ci != pi->costs.end(); ci++)
		{
			const ItemCostData& icd = (*ci);
			unsigned icd_i = 0;
			for (; icd_i < icd.size(); icd_i++)
				*cost++ = icd[icd_i];
			for (; icd_i < dataFieldCount; icd_i++)
				*cost++ = 0;
		}

		strcpy(dest, pi->id.c_str());
		dest += pi->id.size() + 1;
	}

	cti = types.begin();
	for (; cti != types.end(); cti++)
	{
		strcpy(dest, cti->id.c_str());
		dest += cti->id.size() + 1;

		strcpy(dest, cti->name.c_str());
		dest += cti->name.size() + 1;
	}

	return ret;
}

static Binary* LoadCosts(const std::string& basePath, const std::string& typeFile, const std::string& costFile,
                         const std::string& userID, const std::set<std::string> &priceIDS,
								 const char* priceIDField, const char* costTypeField, const std::vector<std::string>& dataFields)
{
   vector<CostType> types;
   set<CTypeSet> typeset;
   set<PriceCostItem> price;
   string exchangeFolder(basePath);

	if( !typeFile.empty() && !LoadTypes(&types, &typeset, exchangeFolder + typeFile) )
      return NULL;

   if( !LoadCosts(&price, &typeset, exchangeFolder + costFile, userID, 
		priceIDS, priceIDField, costTypeField, typeFile.empty(), &types, dataFields) )
	{
      return NULL;
	}
	
	return costService.MakeCostsBinary(types, price, dataFields.size());


  // Costs header;
  // header.priceCount = (WORD)price.size();
  // header.costCount = (WORD)types.size();
  // header.costTypeOffset = priceSize + sizeof(Costs);
  // header.priceOffset = header.costTypeOffset + typeSize;

  // priceSize = (DWORD)(price.size() * types.size() * (sizeof(DWORD) * dataFields.size()));

  // DWORD size = header.priceOffset + priceSize;
  // BYTE *data = (BYTE*)malloc(size);
  // memset(data, 0, size);

  // Binary *ret = new Binary(data, size);

  // *(Costs*)data = header;
  // char *dest = (char*)data + sizeof(header);
  // DWORD *cost = (DWORD*)(data + header.priceOffset);

  // set<PriceCostItem>::const_iterator pi = price.begin();
  // for( ; pi != price.end(); pi++ )
  // {
		//vector<ItemCostData>::const_iterator ci = pi->costs.begin();
		//for (; ci != pi->costs.end(); ci++)
		//{
		//	const ItemCostData& icd = (*ci);
		//	unsigned icd_i = 0;
		//	for ( ; icd_i < icd.size(); icd_i++)
		//		*cost++ = icd[icd_i];
		//	for (; icd_i < dataFields.size(); icd_i++)
		//		*cost++ = 0;
		//}

  //    strcpy(dest, pi->id.c_str());
  //    dest += pi->id.size() + 1;
  // }

  // vector<CostType>::const_iterator cti = types.begin();
  // for( ; cti != types.end(); cti++ )
  // {
  //    strcpy(dest, cti->id.c_str());
  //    dest += cti->id.size() + 1;

  //    strcpy(dest, cti->name.c_str());
  //    dest += cti->name.size() + 1;
  // }

  // return ret;
}

void CostReader::LoadPriceIDS(Session* session)
{
   const ISessionObject *ip = session->LoadObject(L"Price", NULL, L"SetQtyFilter(false)");
   const SessionObject *price = (ip == NULL) ? NULL : (const SessionObject*)ip->Self();
   if( price == NULL )
		return;

	int index = price->format->FindMember(L"id");
	if( index < 0 )
		return;

	unsigned bufSize = 100;
	char *buf = (char*)malloc(bufSize);
	SessionObject::const_iterator i = price->begin();

	for( ; i != price->end(); i++ )
	{
		CString *str = (*i)->at(index).str;
		const wchar_t* pp = str->c_str();
		if( str->size() > bufSize - 1)
		{
			bufSize = str->size() + 1;
			free(buf);
			buf = (char*)malloc(bufSize);
		}
		WideCharToMultiByte(DBF_CODE_PAGE, 0, pp, -1, buf, str->size() + 1, NULL, NULL);
		priceIDS.insert( buf );
	}

	free(buf);
}

bool CostReader::MoveNext(Object *parentObject)
{
   if( dataIndex < 0 || readed )
      return false;

   data = LoadCosts(exchangeFolder, costTypeFile, costFile, userID, priceIDS, priceIDField.c_str(), costTypeField.c_str(), dataFields);

   if( data != NULL )
      readed = true;
   return (data != NULL);
}

bool CostReader::Get(Object* o) const
{
   if( data != NULL )
   {
      Member& m = o->at(dataIndex);
      m.binary = new MemoryBinary(data);

      data = NULL;
   }
   return true;
}

void CostReader::Close()
{
   if( data != NULL )
   {
      delete data;
      data = NULL;
   }
	priceIDS.clear();
}

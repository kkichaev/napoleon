/*
* Copyright (C), 2009 - 2012, Денис Мосягин
*
* OleDB plugin
*
* ert   17/11/2012   creating
*/
#include "stdafx.h"

#include "Source.h"
#include "cost.h"
#include "QuerySource.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>


class SQLCostReader : public IDataSource::IReader
{
public:
	SQLCostReader(const CString& stmt, const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor, bool debug, std::wstring& dataFields);

	virtual bool MoveNext(Object *parentObject);
	virtual bool Get(Object* o) const;
	virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object) { return false; }
	virtual void Remove() {}
	virtual void Close();

	virtual const MemberFormat* Type(const wchar_t* name) const { return NULL; }
	virtual const Member* Value(const wchar_t* name) const { return NULL; }

private:
	Binary* LoadCost();

	bool readed;
	int dataIndex;
	mutable Binary *data;
	ISessionObject* costObject;

	bool debug;
	SQLHDBC hDbc;
	ODBCFlavor* flavor;
	CString stmt;
	std::vector<std::wstring> dataFieldsName;
};

SQLCostReader::SQLCostReader(const CString& stmt, const ISessionObject& object, SQLHDBC hDbc, ODBCFlavor* flavor, bool debug, std::wstring& dataFields)
{
	dataIndex = object.Self()->format->FindMember(L"data");
	readed = false;
	data = NULL;

	costObject = object.GetSession().CreateObject(L"CostFields", true);
	
	this->debug = debug;
	this->hDbc = hDbc;
	this->flavor = flavor;
	this->stmt = stmt;

	PKToList(&dataFieldsName, dataFields, false);
}


struct PriceData
{
	/// type => cost
	std::map<std::string, ItemCostData> cost;
};

void Convert(std::string* dest ,CString* src)
{
	USES_CONVERSION;
	dest->assign(W2A_CP(src->c_str(), CP_UTF8));
}

Binary* SQLCostReader::LoadCost()
{
	QueryBinder binder;
	std::vector<const ISessionObject*> objects;
	objects.push_back(costObject);

	std::wstring str((const std::wstring&)stmt);
	stmt.clear();
	costObject->PrepareFilterStr(&stmt, str);

	if (!binder.Prepare((const std::wstring&)stmt, objects, hDbc, flavor))
	{
		return NULL;
	}

	Object* src = costObject->Self()->CreateObject();
	std::vector<int> dataIndex;
	std::vector<std::wstring>::const_iterator ci = dataFieldsName.begin();
	for (; ci != dataFieldsName.end(); ci++) {
		int costIdx = src->format.FindMember((*ci).c_str());
		dataIndex.push_back(costIdx);
	}
	int prcIdIdx = src->format.FindMember(L"price_id");
	int typeIdIdx = src->format.FindMember(L"type_id");
	int typeNameIdx = src->format.FindMember(L"type_name");

	if (dataIndex.size() == 0 || prcIdIdx < 0 || typeIdIdx < 0 || typeNameIdx < 0)
	{
		AddToLog(IErrorLogger::None, "Not defined cost fields");
		return NULL;
	}

	std::map<std::string, std::string> typeMap;
	std::map<std::string, PriceData> priceMap;

	while (binder.MoveNext(NULL))
	{
		binder.Read(src);
		
		std::string id;
		std::string ct;
		std::string tn;

		Convert(&id, src->at(prcIdIdx).str);
		Convert(&ct, src->at(typeIdIdx).str);
		Convert(&tn, src->at(typeNameIdx).str);

		typeMap[ct] = tn;
		std::vector<DWORD>* srcCost;
		std::map<std::string, PriceData>::iterator fnd = priceMap.find(id);
		if (fnd == priceMap.end())
		{
			PriceData &pd = priceMap.insert(std::map<std::string, PriceData>::value_type(id, PriceData())).first->second;

			//pd.cost[ct].push_back(cost);
			srcCost = &pd.cost[ct];
		}
		else
		{
			srcCost = &(fnd->second.cost[ct]);
		}

		std::vector<int>::const_iterator di = dataIndex.begin();
		for (; di != dataIndex.end(); di++) 
		{
			DWORD cost = 0;
			if (*di >= 0)
			{
				cost = ScaleDouble(src->at(*di).number, 100);
			}
			srcCost->push_back(cost);
		}
	}
	delete src;
	binder.Close();

	std::vector<CostType> types;
	std::map<std::string, std::string>::const_iterator ti = typeMap.begin();
	for (; ti != typeMap.end(); ti++)
	{
		CostType ct;
		ct.id = ti->first;
		ct.name = ti->second;
		types.push_back(ct);
	}

	std::set<PriceCostItem> price;
	std::map<std::string, PriceData>::const_iterator pi = priceMap.begin();
	for (; pi != priceMap.end(); pi++)
	{
		PriceCostItem pci;
		pci.id = pi->first;

		
		std::vector<CostType>::const_iterator ti = types.begin();
		for (; ti != types.end(); ti++)
		{
			std::map<std::string, ItemCostData>::const_iterator fnd = pi->second.cost.find(ti->id);
			ItemCostData icd;
			if (fnd != pi->second.cost.end())
			{
				icd.insert(icd.begin(), fnd->second.begin(), fnd->second.end());
			}
			else
			{
				icd.insert(icd.begin(), 0, dataFieldsName.size());
			}
			pci.costs.push_back(icd);
		}

		price.insert(pci);
	}

	CostService*cserv = (CostService*)gServer->GetService(COST_SERVICE_NAME);
	return cserv->MakeCostsBinary(types, price, dataFieldsName.size());
}

bool SQLCostReader::MoveNext(Object *parentObject)
{
	if (dataIndex < 0 || readed)
		return false;

	data = LoadCost();

	if (data != NULL)
		readed = true;
	return (data != NULL);
}

bool SQLCostReader::Get(Object* o) const
{
	if (data != NULL)
	{
		Member& m = o->at(dataIndex);
		m.binary = new MemoryBinary(data);

		data = NULL;
	}
	return true;
}

void SQLCostReader::Close()
{
	if (data != NULL)
	{
		delete data;
		data = NULL;
	}
}


IDataSource::IReader* SQLCostSource::CreateReader(const ParamList& parameters, const ISessionObject& object) const
{
	ODBCFlavor* flavor = GetFlavor();
	if (flavor == NULL)
	{
		gServer->AddError(false, "ODBC не соединен");
		return NULL;
	}

	const Parameter* p = parameters.Find(L"stmt", -1);
	if (p == NULL)
	{
		gServer->AddError(false, "SQLQuery нет параметра stmt");
		return NULL;
	}

	CString *stmt = NULL;
	if (!object.GetSession().Parse(&stmt, p->value, &object))
	{
		gServer->AddError(false, "SQLQuery не правильный параметр stmt");
		delete stmt;
		return NULL;
	}

	p = parameters.Find(L"debug", -1);
	bool debug = (p != NULL);

	std::wstring dataFields = L"cost";

	p = parameters.Find(L"dataFields", -1);
	if (p != NULL) 
	{
		CString* res = NULL;
		if (object.GetSession().Parse(&res, p->value, &object))
		{
			dataFields = (const std::wstring&)(*res);
		}
		delete res;
	}
	SQLCostReader* ret = new SQLCostReader(*stmt, object, GetHDBC(), flavor, debug, dataFields);
	delete stmt;

	return ret;
}
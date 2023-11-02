/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Add on - дополнения для разных клиентов
 *
 * ert   16/06/2010   creating
 */
#include "stdafx.h"
#include <vector>
#include <map>
#include "server.h"

#include "event.h"
#include "isessobj.h"
#include "session.h"
#include "datasource.h"
#include "creators.h"
#include "sources.h"
#include "cwriter.h"
#include <atlconv.h>

using namespace GRServer;

//class CheckPut : public IActionExecutor
//{
//public:
//	virtual bool Do(Session* session, SessionObject* sourceObject, const std::vector<Token>& params, Action& action)
//	{
//		bool canPut = true;
//		ISessionObject *iso = session->LoadObject(L"DisableOrderWrite", NULL);
//		if (iso != NULL && iso->Self()->size() > 0)
//		{
//			Object *o = iso->Self()->at(0);
//			int idx = iso->Self()->format->FindMember(L"disable");
//			if (idx >= 0)
//				canPut = (o->at(idx).number < 0.05);
//		}
//		return canPut;
//	}
//};
//
//class CheckPutLoader : public IActionExecutorLoader, IXmlHandler
//{
//public:
//	CheckPutLoader()
//	{
//		Action::Register(L"checkCanPut", this);
//		//MessageBox(NULL, L"", L"", MB_OK);
//	}
//
//	virtual void Load(ActionLoader* prevHandler, const IXmlHandler::Attributes& attributes)
//	{
//		handler = prevHandler;
//		prevHandler->owner->SetHandler(this);
//	}
//
//	virtual void StartElement(const std::wstring& name, const Attributes& atts) {}
//	virtual void CharacterData(const std::wstring& name) {}
//	virtual bool IsError() const { return false; }
//	virtual const wchar_t* GetError() const { return L""; }
//
//	virtual void EndElement(const std::wstring& name)
//	{
//		handler->Add(new CheckPut());
//		handler->owner->SetHandler(handler);
//	}
//
//private:
//	ActionLoader* handler;
//};


class DBFOrderWriter : public DataSource::ICreator
{
public:
	virtual const wchar_t* Name() const { return L"ServoluxOrderWriter"; }
	virtual IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
	virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
};

class SVWriter : public DataSource::IWriter
{
public:
	SVWriter(const std::wstring& firmField, const std::wstring& userid, const std::wstring& prefix);

	virtual bool Prepare(const ISessionObject& object);

	virtual bool Write(const Object& o, RowID *rid);
	virtual void Close();

	virtual void AddChild(IWriter* writer, const std::wstring& typeName)
	{
		if (typeName.compare(TRHCreator().Name()) == 0)
			childWriter = (CatalogItemsWriter*)writer;
		else
			childs.push_back(writer);
	}

private:
	CatalogItemsWriter* childWriter;

	std::map<std::string, DataForm*> bases;
	std::wstring firmField, userId, prefix;

	std::string expFolder;

	const ISessionObject* object;
	int firmIndex;

	FieldWriter writer;
};

IDataSource::IWriter* DBFOrderWriter::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
	Token firmField, userField, tpref;
	const Parameter *tname = parameters.Find(L"firmField", -1);
	Session& session = (Session&)object.GetSession();

	if (tname == NULL || !session.Parse(&firmField, tname->value, &object) || firmField.type != Token::ttString)
		return NULL;

	tname = parameters.Find(L"userID", -1);
	if (tname == NULL || !session.Parse(&userField, tname->value, &object) || userField.type != Token::ttString)
		return NULL;

	std::wstring prefix(L"ORD_");
	tname = parameters.Find(L"prefix", -1);
	if (tname != NULL && session.Parse(&tpref, tname->value, &object) && tpref.type == Token::ttString)
		prefix.assign(*tpref.value.str);


	return new SVWriter(*firmField.value.str, *userField.value.str, prefix);
}

SVWriter::SVWriter(const std::wstring &firmField, const std::wstring& userId, const std::wstring& prefix) :
childWriter(NULL), object(NULL)
{
	this->firmField = firmField;
	this->userId = userId;
	this->prefix = prefix;
}

bool SVWriter::Prepare(const ISessionObject& object)
{
	this->object = &object;
	firmIndex = object.Self()->format->FindMember(firmField.c_str());
	expFolder = object.GetSession().Config().ExchangeFolder();

	return writer.AddFields((const SessionObject&)(*object.Self()));
}

void SVWriter::Close()
{
	std::map<std::string, DataForm*>::iterator i = bases.begin();
	for (; i != bases.end(); i++)
	{
		try
		{
			i->second->Close();
			delete i->second;
		}
		catch (...)
		{
			gServer->AddError(false, "Exception SVWriter::Close()");
		}
	}
	bases.clear();
}

bool SVWriter::Write(const Object& o, RowID *rid)
{
	USES_CONVERSION;

	std::string fileName(expFolder);
	fileName += W2A(prefix.c_str());// "ORD_";
	if (firmIndex >= 0)
	{
		fileName += W2A(o.at(firmIndex).str->c_str());
	}

	fileName += "_";
	fileName += W2A(userId.c_str());
	fileName += ".DBF";

	DataForm* base;
	std::map<std::string, DataForm*>::iterator fnd = bases.find(fileName);
	bool ret = true;
	try
	{
		if (fnd == bases.end())
		{
			base = new DataForm();
			if (!base->Open(fileName.c_str()))
			{
				const IObjectData* od = object->GetObjectDef();
				if (od == NULL)
					return false;

				bool ret = false;
				std::vector<DBRec> dbFields;

				ObjectDef::Fields::const_iterator fi = od->fields.begin();
				for (; fi != od->fields.end(); fi++)
					SetDBField(&dbFields, (*fi));

				childWriter->SetDBFields(&dbFields);

				if (dbFields.size() > 0)
				{
					DBRec *flds = new DBRec[dbFields.size()];
					for (WORD i = 0; i < dbFields.size(); i++)
						flds[i] = dbFields[i];

					ret = base->Create(fileName.c_str(), (int)dbFields.size(), flds);
					delete flds;
				}

				if (!ret)
					return false;
			}
			bases.insert(std::map<std::string, DataForm*>::value_type(fileName, base));
		}
		else
			base = fnd->second;

		base->MarkDelete(false);

		if (!writer.Write(o, *base))
			ret = false;
		else 
		{
			if (childWriter == NULL || !childWriter->Write(o, *base))
				ret = base->Append();
		}
	}
	catch (...)
	{
		gServer->AddError(false, "Exception SVWriter::Write %s", fileName.c_str());
	}

	return ret;
}

//static CheckPutLoader loader;
bool GRServer::AddOnInit()
{
	DataSource::AddCreator(new DBFOrderWriter());
	return true;
}


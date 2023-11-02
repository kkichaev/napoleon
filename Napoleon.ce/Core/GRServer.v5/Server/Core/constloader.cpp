/*
 * Copyright (C), 2009, ����� �������
 *
 * ����� ���������� �������
 *
 * ert   23/10/2022   creating
 */

#include "stdafx.h"
#include <map>
#include "constloader.h"
#include "loaders.h"
#include "parse.h"
#include "server.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
using namespace std;

static map<wstring, Token> consts;

struct ValueHolder
{
	virtual ~ValueHolder() {}

	virtual void Apply(const Token& constVal) = 0;
};

class ConstXMLLoader : public ILoader
{
public:
	ConstXMLLoader(IXmlHandler* prevHandler) : ILoader(prevHandler) {}

	virtual void StartElement(const std::wstring& name, const Attributes& atts);
	virtual void EndElement(const std::wstring& name);
	virtual void CharacterData(const std::wstring& name) { value += name; }

private:
	wstring value, name;
};

class ConstResolver : public IResolver
{
public:
	virtual bool Resolve(Token* dest, StringStream& stream, const std::wstring& val, const SessionObject* thisObject) const { return false; }
	virtual bool EndStatement(Token& result, StringStream& stream, wchar_t endSym) { return false;  }
};

void ConstXMLLoader::StartElement(const std::wstring& name, const Attributes& atts)
{
	if (!atts.Find(&this->name, L"name"))
	{
		owner->SetError(L"No name attribute for constant");
	}
}


void ConstXMLLoader::EndElement(const std::wstring& _name)
{
	const wchar_t* p = value.c_str();
	const wchar_t* ep = p + value.size();
	StringStream ss(p, ep);

	ConstResolver resolver;
	Token res;
	if (!ParseStr(&res, ss, NULL, resolver))
	{
		wstring msg(L"Error while parsing value for ");
		msg.append(this->name).append(L" const");
		owner->SetError(msg.c_str());
	}
	else
	{
		consts[this->name] = res;
		SetCompleete(true);
	}
	delete this;
}

void ConstLoader::Load(IXmlHandler* prevHandler, const GRServer::IXmlHandler::Attributes& atts)
{
	ConstXMLLoader* l = new ConstXMLLoader(prevHandler);
	l->StartElement(L"", atts);
}

static Token* GetConsData(const wstring& val)
{
	static wstring constTag(L"$const.");

	size_t pos = val.find(constTag);
	if (pos == wstring::npos)
		return NULL;

	size_t offset = constTag.size() + pos;
	wstring name(val.substr(offset, val.size() - (offset + pos)));

	map<wstring, Token>::iterator fnd = consts.find(name);
	if (fnd == consts.end())
		return NULL;
	return &(fnd->second);
}

bool ConstLoader::CheckConst(std::wstring* val)
{
	bool ret = true;
	Token* cd = GetConsData(*val);
	if (cd != NULL)
	{
		ret = cd->ToString(val);
	}

	return ret;
}

bool ConstLoader::CheckConst(const std::wstring& text, short* val)
{
	bool ret = true;
	Token* cd = GetConsData(text);
	if (cd != NULL)
	{
		double dval;
		if ((ret=cd->ToNumber(&dval)))
		{
			*val = (short)dval;
		}
	}
	else
	{
		*val = (short)_wtoi(text.c_str());
	}

	return ret;
}

bool ConstLoader::CheckConst(const std::wstring& text, int* val)
{
	bool ret = true;
	Token* cd = GetConsData(text);
	if (cd != NULL)
	{
		double dval;
		if ((ret = cd->ToNumber(&dval)))
		{
			*val = (int)dval;
		}
	}
	else
	{
		*val = (int)std::stoll(text.c_str());
	}

	return ret;
}
#ifdef UNIX
#else
bool ConstLoader::CheckConst(const std::wstring& text, long* val)
{
	bool ret = true;
	Token* cd = GetConsData(text);
	if (cd != NULL)
	{
		double dval;
		if ((ret = cd->ToNumber(&dval)))
		{
			*val = (long)dval;
		}
	}
	else
	{
		*val = (long)std::stoll(text.c_str());
	}

	return ret;
}
#endif

bool ConstLoader::CheckConst(const std::wstring& text, __int64* val)
{
	bool ret = true;
	Token* cd = GetConsData(text);
	if (cd != NULL)
	{
		double dval;
		if ((ret = cd->ToNumber(&dval)))
		{
			*val = (__int64)dval;
		}
	}
	else
	{
		*val = std::stoll(text.c_str());
	}

	return ret;
}


//class ConstData
//{
//public:
//	ConstData(Token value) : constVal(value) {}
//	~ConstData();
//
//	void Add(ValueHolder* vh) { values.push_back(vh); }
//	void SetValues();
//	void SetToken(Token val) { constVal = val; }
//
//	bool IsEmpty() const { return constVal.IsNone(); }
//
//private:
//	Token constVal;
//	vector<ValueHolder*> values;
//};
//
//class ConstResolver : public IResolver
//{
//public:
//	virtual bool Resolve(Token* dest, StringStream& stream, const std::wstring& val, const SessionObject* thisObject) const { return false; }
//	virtual bool EndStatement(Token& result, StringStream& stream, wchar_t endSym) { return false;  }
//};
//
//static map<wstring, ConstData*> consts;
//
//void ConstData::SetValues()
//{
//	vector<ValueHolder*>::iterator i = values.begin();
//	for (; i != values.end(); i++)
//	{
//		(*i)->Apply(constVal);
//	}
//}
//
//ConstData::~ConstData()
//{
//	vector<ValueHolder*>::iterator i = values.begin();
//	for (; i != values.end(); i++)
//	{
//		delete (*i);
//	}
//}

//void ConstXMLLoader::StartElement(const std::wstring& name, const Attributes& atts) 
//{
//	if (!atts.Find(&this->name, L"name"))
//	{
//		owner->SetError(L"No name attribute for constant");
//	}
//}
//
//
//void ConstXMLLoader::EndElement(const std::wstring& _name)
//{
//	const wchar_t* p = value.c_str();
//	const wchar_t* ep = p + value.size();
//	StringStream ss(p, ep);
//
//	ConstResolver resolver;
//	Token res;
//	if (!ParseStr(&res, ss, NULL, resolver))
//	{
//		wstring msg(L"Error while parsing value for ");
//		msg.append(this->name).append(L" const");
//		owner->SetError(msg.c_str());
//	} else
//	{
//		map<wstring, ConstData*>::iterator fnd = consts.find(this->name);
//		if (fnd == consts.end())
//		{
//			consts[this->name] = new ConstData(res);
//		}
//		else
//		{
//			fnd->second->SetToken(res);
//		}
//		SetCompleete(true);
//	}
//	delete this;
//}
//
//void ConstLoader::Load(IXmlHandler* prevHandler, const GRServer::IXmlHandler::Attributes& atts)
//{
//	ConstXMLLoader *l = new ConstXMLLoader(prevHandler);
//	l->StartElement(L"", atts);
//}

//void ConstLoader::ResolveConsts() 
//{
//	map<wstring, ConstData*>::iterator i = consts.begin();
//	for (; i != consts.end(); i++)
//	{
//		if (i->second->IsEmpty())
//		{
//			USES_CONVERSION;
//
//			gServer->AddError(true, "No value for constant named %s", W2A(i->first.c_str()));
//			break;
//		}
//		i->second->SetValues();
//	}
//}

//static ConstData* GetConsData(const wstring& val)
//{
//	static wstring constTag(L"$const.");
//
//	size_t pos = val.find(constTag);
//	if (pos == wstring::npos)
//		return NULL;
//
//	size_t offset = constTag.size() + pos;
//	wstring name(val.substr(offset, val.size() - (offset + pos)));
//
//	map<wstring, ConstData*>::iterator fnd = consts.find(name);
//	if (fnd == consts.end())
//	{
//		ConstData* ret = new ConstData(Token());
//		consts[name] = ret;
//		return ret;
//	}
//	return fnd->second;
//}

//struct StringHolder : public ValueHolder
//{
//	StringHolder(wstring* dest) { this->dest = dest; }
//
//	virtual void Apply(const Token& constVal) { constVal.ToString(dest); }
//
//	wstring* dest;
//};

//bool ConstLoader::CheckConst(std::wstring* val)
//{
//	ConstData* cd = GetConsData(*val);
//	if (cd != NULL)
//	{
//		cd->Add(new StringHolder(val));
//	}
//	return true;
//}

//template <typename VT> struct NumberHolder : public ValueHolder
//{
//	NumberHolder(VT* dest) { this->dest = dest; }
//
//	virtual void Apply(const Token& constVal) 
//	{
//		double val;
//		if (constVal.ToNumber(&val))
//		{
//			*dest = (VT)val;
//		}
//	}
//
//	VT* dest;
//};

//bool ConstLoader::CheckConst(const std::wstring& text, short* val)
//{
//	ConstData* cd = GetConsData(text);
//	if (cd != NULL)
//	{
//		cd->Add(new NumberHolder<short>(val));
//	}
//	else
//	{
//		*val = (short)_wtoi(text.c_str());
//	}
//
//	return true;
//}
//
//bool ConstLoader::CheckConst(const std::wstring& text, int* val)
//{
//	ConstData* cd = GetConsData(text);
//	if (cd != NULL)
//	{
//		cd->Add(new NumberHolder<int>(val));
//	}
//	else
//	{
//		wchar_t* ep;
//		*val = (int)wcstol(text.c_str(), &ep, 10);
//	}
//
//	return true;
//}
//bool ConstLoader::CheckConst(const std::wstring& text, long* val)
//{
//	ConstData* cd = GetConsData(text);
//	if (cd != NULL)
//	{
//		cd->Add(new NumberHolder<long>(val));
//	}
//	else
//	{
//		wchar_t* ep;
//		*val = (long)wcstoll(text.c_str(), &ep, 10);
//	}
//
//	return true;
//}
//
//bool ConstLoader::CheckConst(const std::wstring& text, __int64* val)
//{
//	ConstData* cd = GetConsData(text);
//	if (cd != NULL)
//	{
//		cd->Add(new NumberHolder<__int64>(val));
//	}
//	else
//	{
//		wchar_t* ep;
//		*val = wcstoll(text.c_str(), &ep, 10);
//	}
//
//	return true;
//}



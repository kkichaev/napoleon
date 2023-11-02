/*
* Copyright (C), 2009-..., Денис Мосягин
*
* Token impl
*
* ert   04/05/2020   creating
*/
#include "stdafx.h"
#include <token.h>

#include <Binary.h>
#include <servobj.h>

using namespace GRServer;

//
//------------------------------------------ MemberData ----------------------------------------------------
//
bool MemberData::CopyFrom(const MemberData& src)
{
	if (src.format->type != format->type)
		return false;

	switch (format->type)
	{
	case MemberFormat::mtNumber:
		member->number = src.member->number;
		break;
	case MemberFormat::mtDateTime:
		member->datetime = src.member->datetime;
		break;
	case MemberFormat::mtString:
		member->str->assign(*src.member->str);
		break;
	default:
		return false;
	}

	return true;
}

//
//------------------------------------------ Token ----------------------------------------------------
//
Token::Token() : type(ttNone)
{
}

Token::Token(const Token& src) : type(ttNone)
{
	(*this) = src;
}

Token::~Token()
{
	Clear();
}

void Token::Clear()
{
	if (type == ttString) delete value.str;
	else if (type == ttBinary) delete value.binary;
	type = ttNone;
}

Token& Token::operator= (const Token& token)
{
	Clear();

	type = token.type;
	if (type == ttString)
		value.str = new std::wstring(*token.value.str);
	else if (type == ttBinary)
	{
		Binary *b = NULL;
		if (token.value.binary)
		{
			b = new Binary();
			DWORD cb = token.value.binary->Size();
			BYTE *pb = b->Alloc(cb);
			memcpy(pb, token.value.binary->Bytes(), cb);
		}
		value.binary = new MemoryBinary(b);
	}
	else
		value.datetime = token.value.datetime;

	return *this;
}

Token& Token::operator= (const std::wstring& val)
{
	Clear();
	type = ttString;
	value.str = new std::wstring(val);
	return *this;
}

Token& Token::operator= (double val)
{
	Clear();
	type = ttNumber;
	value.number = val;
	return *this;
}

Token& Token::operator= (const FILETIME& val)
{
	Clear();
	type = ttDateTime;
	value.datetime = val;
	return *this;
}

Token& Token::operator= (SessionObject* obj)
{
	Clear();
	type = ttServObject;
	value.object = obj;
	return *this;
}

Token& Token::operator= (const MemberData& data)
{
	Clear();
	type = ttMember;
	value.member = data;
	return *this;
}

Token& Token::operator= (const IBinary* data)
{
	Clear();
	type = ttBinary;
	if (data)
	{
		Binary *b = new Binary();
		DWORD cb = data->Size();
		BYTE *pb = b->Alloc(cb);
		memcpy(pb, data->Bytes(), cb);
		value.binary = new MemoryBinary(b);
	}
	else
		value.binary = NULL;
	return *this;
}

bool Token::Sub(const Token& src)
{
	if (type == ttNone)
	{
		if (src.type != ttNumber)
			return false;

		value.number = -src.value.number;
		type = ttNumber;
	}
	else
	{
		if (type != ttNumber && src.type != ttNumber)
			return false;
		value.number -= src.value.number;
	}
	return true;
}

bool Token::ToString(std::wstring* value) const
{
	wchar_t buf[50];
	bool res = true;
	switch (type)
	{
	case ttString:
		value->assign(*this->value.str);
		break;
	case ttNumber:
	{
		__int64 tval = (this->value.number > 0) ? (__int64)(this->value.number + 0.05) : (__int64)(this->value.number - 0.05);
		swprintf(buf, sizeof(buf) / sizeof(buf[0]), L"%lld", tval);
		value->assign(buf);
		break;
	}
	case ttDateTime:
	{
		SYSTEMTIME st;
		FileTimeToSystemTime(&this->value.datetime, &st);
		wsprintf(buf, L"%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
		value->assign(buf);
		break;
	}
	default:
		res = false;
		break;
	}
	return res;
}

bool Token::ToNumber(double* value) const
{
	bool res = true;
	switch (type)
	{
	case ttNumber:
		*value = this->value.number;
		break;
	case ttString:
		*value = _wtof(this->value.str->c_str());
		break;
	case ttDateTime:
		*value = (double)(*((__int64*)&this->value.datetime));
		break;
	default:
		res = false;
	}

	return res;
}

bool Token::Add(const Token& src)
{
	bool res = false;
	switch (type)
	{
	case ttNumber:
	{
		double val;
		res = src.ToNumber(&val);
		if (res) value.number += val;
		break;
	}
	case ttString:
	{
		std::wstring val;
		res = src.ToString(&val);
		if (res) value.str->append(val);

		break;
	}
	default:
		break;
	}

	return res;
}

bool Token::Mul(const Token& src)
{
	if (src.type != ttNumber && type != ttNumber) return false;

	value.number *= src.value.number;
	return true;
}

void Token::Assign(bool res)
{
	Clear();

	value.result = res;
	type = ttBoolean;
}

int Token::Compare(const Token& src) const
{
	if (type != src.type)
		return -2;

	switch (type)
	{
	case ttString:
		return value.str->compare(*src.value.str);
	case ttNumber:
		return (int)(value.number - src.value.number);
	case ttDateTime:
		return CompareFileTime(&value.datetime, &src.value.datetime);
	case ttBoolean:
		return value.result == src.value.result ? 0 : value.result ? 1 : -1;
	}

	return -2;
}

bool Token::Assign(const GRServer::Token &token)
{
	if (type != ttMember) return false;

	if (token.type == ttMember)
		return value.member.CopyFrom(token.value.member);

	MemberFormat::MemberType t = value.member.format->type;

	switch (t)
	{
	case MemberFormat::mtString:
		if (token.type == ttString)
		{
			value.member.member->str->assign(*token.value.str);
			return true;
		}
		else if (token.type == ttDateTime)
		{
			wchar_t buf[50];
			SYSTEMTIME st;
			FileTimeToSystemTime(&token.value.datetime, &st);

			wsprintf(buf, L"%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
			value.member.member->str->assign(buf);
			return true;
		}
		break;
	case MemberFormat::mtDateTime:
		if (token.type == ttDateTime)
		{
			value.member.member->datetime = token.value.datetime;
			return true;
		}
		break;
	case MemberFormat::mtNumber:
	{
		double val;
		if (token.ToNumber(&val))
		{
			value.member.member->number = val;
			return true;
		}
		break;
	}
	default: break;
	}

	return false;
}

bool Token::CopyTo(MemberData *md) const
{
	MemberFormat::MemberType t = md->format->type;
	bool ret = false;
	SYSTEMTIME st;
	wchar_t buf[50];

	switch (t)
	{
	case MemberFormat::mtString:
		switch (type)
		{
		case ttDateTime:
			FileTimeToSystemTime(&value.datetime, &st);
			wsprintf(buf, L"%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
			md->member->str->assign(buf);
			ret = true;
			break;
		case ttNumber:
			_swprintf(buf, L"%f", value.number);
			md->member->str->assign(buf);
			ret = true;
			break;
		case ttString:
			ret = true;
			md->member->str->assign(*value.str);
			break;
		default: break;
		}
		break;
	case MemberFormat::mtDateTime:
		if (type == ttDateTime)
		{
			md->member->datetime = value.datetime;
			ret = true;
		}
		break;
	case MemberFormat::mtNumber:
		switch (type)
		{
		case ttNumber:
			md->member->number = value.number;
			ret = true;
			break;

		case ttString:
			ret = true;
			md->member->number = _wtof(value.str->c_str());
			break;
		case ttDateTime:
			ret = true;
			md->member->number = (double)(*(__int64*)&value.datetime);
			break;
		default: break;
		}
		break;
	case MemberFormat::mtBinary:
		if (type == ttNone || type == ttBinary)
		{
			Binary *bd = NULL;
			if (type == ttBinary && value.binary)
			{
				bd = new Binary();
				DWORD cb = value.binary->Size();
				BYTE *pb = bd->Alloc(cb);
				memcpy(pb, value.binary->Bytes(), cb);
			}
			md->member->binary = new MemoryBinary(bd);
			ret = true;
		}
		break;
	default: break;
	}

	return ret;
}

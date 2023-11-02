/*
* Copyright (C), 2009, ����� �������
*
* JSON
*
* ert   11/05/2020   creating
*/

#include "stdafx.h"
#include <string>
#include "json.h"

#include <sstream>

static JSONValue* ReadValue(std::string::const_iterator& i, std::string::const_iterator ep, std::string* err);

static JSONValue NULL_VALUE;

JSONValue& JSONValue::get(const std::string& name) const
{
	if (!IsObject())
		return NULL_VALUE;

	JSONObject::const_iterator fnd = value.object->find(name);
	return (fnd == value.object->end()) ? NULL_VALUE : *((JSONValue*)fnd->second);
}

bool JSONValue::operator==(const std::string& value) const
{
	return (IsString() && (this->value.string->compare(value) == 0));
}
bool JSONValue::operator==(long long value) const
{
	return (IsInt() && this->value.longValue == value) || (IsDouble() && this->value.doubleValue == value);
}

bool JSONValue::operator==(double value) const
{
	return (IsInt() && this->value.longValue == value) || (IsDouble() && this->value.doubleValue == value);
}

bool JSONValue::read(DWORD* out, const std::string& name) const
{
	if (!IsObject())
		return false;

	JSONObject::const_iterator fnd = value.object->find(name);
	if (fnd != value.object->end())
	{
		const JSONValue& src = *fnd->second;
		if (src.IsInt()) {
			*out = (DWORD)src.value.longValue;
			return true;
		}
		if (src.IsString())
		{
			char* ep;
			*out = (DWORD)strtoul(src.value.string->c_str(), &ep, 10);
			return true;
		}
		return true;
	}
	return false;
}

bool JSONValue::read(int* out, const std::string& name) const
{
	return read((DWORD*)out, name);
}

bool JSONValue::read(float* out, const std::string& name) const
{
	if (!IsObject())
		return false;
	JSONObject::const_iterator fnd = value.object->find(name);
	if (fnd != value.object->end()) {
		if (fnd->second->IsString()) {
			*out = (float)atof(fnd->second->value.string->c_str());
			return true;
		}
		if (fnd->second->IsDouble()) {
			*out = (float)fnd->second->value.doubleValue;
			return true;
		}
	}
	return false;
}

bool JSONValue::read(std::string* out, const std::string& name) const
{
	if (!IsObject())
		return false;

	JSONObject::const_iterator fnd = value.object->find(name);
	if (fnd != value.object->end() && fnd->second->IsString() && fnd->second->value.string != NULL)
	{
		out->assign(*fnd->second->value.string);
		return true;
	}
	return false;
}

JSONValue::~JSONValue()
{
	if (type == jtString)
		delete value.string;
	else if (type == jtObject)
		delete value.object;
	else if (type == jtArray)
		delete value.array;
}

bool JSONValue::Compose(JSONValue* dest, const std::vector<std::string>& tags) const
{
	if (!IsObject())
		return false;

	JSONObject* res = new JSONObject();
	std::vector<std::string>::const_iterator i = tags.begin();
	for (; i != tags.end(); i++)
	{
		JSONObject::const_iterator fnd = value.object->find(*i);
		if (fnd == value.object->end())
		{
			continue;
		}

		res->Put(*i, fnd->second->Copy());
	}

	dest->type = jtObject;
	dest->value.object = res;
	return true;
}

JSONValue* JSONValue::Copy() const
{
	switch (type)
	{
	case jtString:
		return new JSONValue(*value.string);
	case jtInt:
		return new JSONValue(value.longValue);
	case jtDouble:
		return new JSONValue(value.doubleValue);
	case jtTrue:
	case jtFalse:
		return new JSONValue(value.boolValue);
	case jtArray:
		return value.array->Copy();
	case jtObject:
		return value.object->Copy();
	}

	return new JSONValue();
}

void JSONValue::dump(std::stringstream* str) const
{
	switch (type)
	{
	case jtString:
		(*str) << '"' << *value.string << '"';
		break;
	case jtInt:
		(*str) << value.longValue;
		break;
	case jtDouble:
		(*str) << value.doubleValue;
		break;
	case jtTrue:
	case jtFalse:
		(*str) << value.boolValue ? "true" : "false";
		break;
	case jtArray:
		value.array->dump(str);
		return;
	case jtObject:
		value.object->dump(str);
		return;
	case jtNull:
		(*str) << "null";
	}

}

void JSONObject::dump(std::stringstream* out) const
{
	(*out) << '{';

	const_iterator i = begin();
	for (; i != end(); i++)
	{
		if (i != begin())
			(*out) << ',';

		(*out) << '"' << i->first << "\":";
		i->second->dump(out);
	}

	(*out) << '}';
}

void JSONArray::dump(std::stringstream* out) const
{
	(*out) << '[';
	const_iterator i = begin();
	for (; i != end(); i++)
	{
		if (i != begin())
			(*out) << ',';
		(*i)->dump(out);
	}

	(*out) << ']';
}

JSONObject::~JSONObject()
{
	iterator i = begin();
	for (; i != end(); i++)
		delete i->second;
}

JSONValue* JSONObject::Copy() const
{
	JSONObject* res = new JSONObject();

	const_iterator i = begin();
	for (; i != end(); i++)
	{
		res->Put(i->first, i->second->Copy());
	}

	return new JSONValue(res);
}

JSONArray::~JSONArray()
{
	iterator i = begin();
	for (; i != end(); i++)
		delete (*i);
}

JSONValue* JSONArray::Copy() const
{
	JSONArray* res = new JSONArray();

	const_iterator i = begin();
	for (; i != end(); i++)
	{
		res->push_back((*i)->Copy());
	}

	return new JSONValue(res);
}

JSONReader::JSONReader()
{
}

JSONReader::~JSONReader()
{
}

static std::string ToUtf8(unsigned utf32)
{
	std::string utf8;

	if (utf32 <= 0x7f)
		utf8.push_back(utf32 & 0x7f);
	else if (utf32 <= 0x7ff) {
		utf8.push_back(0xc0 + ((utf32 & 0x7c0) >> 6));
		utf8.push_back(0x80 + (utf32 & 0x3f));
	}
	else if (utf32 <= 0xffff) {
		utf8.push_back(0xe0 + ((utf32 & 0xf000) >> 12));
		utf8.push_back(0x80 + ((utf32 & 0x0fc0) >> 6));
		utf8.push_back(0x80 + (utf32 & 0x3f));
	}
	else if (utf32 <= 0x1fffff) {
		utf8.push_back(0xf0 + ((utf32 & 0x1c0000) >> 18));
		utf8.push_back(0x80 + ((utf32 & 0x3f000) >> 12));
		utf8.push_back(0x80 + ((utf32 & 0x0fc0) >> 6));
		utf8.push_back(0x80 + (utf32 & 0x3f));
	}
	else if (utf32 <= 0x03ffffff) {
		utf8.push_back(0xf8 + (utf32 >> 24));
		utf8.push_back(0x80 + ((utf32 & 0xfc0000) >> 18));
		utf8.push_back(0x80 + ((utf32 & 0x3f000) >> 12));
		utf8.push_back(0x80 + ((utf32 & 0x0fc0) >> 6));
		utf8.push_back(0x80 + (utf32 & 0x3f));
	}
	else {
		utf8.push_back(0xfc + (utf32 >> 30));
		utf8.push_back(0x80 + ((utf32 & 0x3f000000) >> 24));
		utf8.push_back(0x80 + ((utf32 & 0xfc0000) >> 18));
		utf8.push_back(0x80 + ((utf32 & 0x3f000) >> 12));
		utf8.push_back(0x80 + ((utf32 & 0x0fc0) >> 6));
		utf8.push_back(0x80 + (utf32 & 0x3f));
	}

	return utf8;
}

static std::string* ReadString(std::string::const_iterator& i, std::string::const_iterator ep, std::string* err)
{
	bool done = false;
	std::string* res = new std::string();
	while (i < ep)
	{
		char sym = (*i);
		if (sym == '"')
		{
			i++;
			done = true;
			break;
		}
		if (sym == '\\')
		{
			i++;
			sym = (*i);
			if (sym == '"' || sym == '\\' || sym == '/')
				res->append(1, sym);
			else if (sym == 'b')
				res->append(1, '\b');
			else if (sym == 'f')
				res->append(1, '\f');
			else if (sym == 'n')
				res->append(1, '\n');
			else if (sym == 'r')
				res->append(1, '\r');
			else if (sym == 't')
				res->append(1, '\t');
			else if (sym == 'u')
			{
				// i++;

				std::string val;
				char* ep;

				for (int j = 0; j < 4; j++)
					val.append(1, *++i);
				unsigned value = strtol(val.c_str(), &ep, 16);
				res->append(ToUtf8(value));
			}
		}
		else
		{
			res->append(1, sym);
		}
		i++;
	}

	if (!done)
	{
		err->assign("No terminal quote in string");
		delete res;
		res = NULL;
	}
	return res;
}

static JSONValue* ReadArray(std::string::const_iterator& i, std::string::const_iterator ep, std::string* err)
{
	bool done = false, haveError = false;
	JSONArray* res = new JSONArray();
	while (i < ep)
	{
		char sym = (*i);
		if (((int)sym > 0 && isspace(sym)) || sym == ',')
		{
			i++;
		}
		else if (sym == ']')
		{
			done = true;
			i++;
			break;
		}
		else
		{
			JSONValue* el = ReadValue(i, ep, err);
			if (el == NULL)
			{
				haveError = true;
				break;
			}
			res->push_back(el);
		}
	}
	if (!done)
	{
		delete res;
		if (!haveError)
			err->assign("Error while reading array");
		return NULL;
	}
	return new JSONValue(res);
}

static JSONValue* ReadObject(std::string::const_iterator& i, std::string::const_iterator ep, std::string* err)
{
	bool done = false, haveError = false;
	std::string* member = NULL;
	JSONObject* res = new JSONObject();
	while (i < ep)
	{
		char sym = (*i);
		if (((int)sym > 0 && isspace(sym)) || sym == ',')
		{
			i++;
		}
		else if (sym == '}')
		{
			done = true;
			i++;
			break;
		}
		else if (sym == ':')
		{
			if (member == NULL)
			{
				err->assign("No member name before ':'");
				haveError = true;
				break;
			}
			i++;
			JSONValue* val = ReadValue(i, ep, err);
			if (val == NULL)
			{
				haveError = true;
				break;
			}
			else
			{
				res->insert(JSONObject::value_type(*member, val));
			}
		}
		else if (sym == '"')
		{
			i++;
			if (member)
			{
				delete member;
			}
			member = ReadString(i, ep, err);
			if (member == NULL)
			{
				std::stringstream str;
				res->dump(&str);
				err->assign("Can't read string while read object ").append(str.str());
				haveError = true;
				break;
			}
		}
		else {
			std::stringstream str;
			res->dump(&str);

			err->assign("Wrong symbol <").append(1, sym).append("> while read object ").append(str.str());
			haveError = true;
			break;
		}
	}

	delete member;
	if (!done)
	{
		delete res;
		if (!haveError)
			err->assign("Error while reading array");
		return NULL;
	}
	return new JSONValue(res);
}

const char* WhiteSpaces = " \t\n\r\f\v";
inline std::string& rtrim(std::string& s, const char* t = WhiteSpaces)
{
	return s.erase(s.find_last_not_of(t) + 1);
}

// trim from beginning of string (left)
inline std::string& ltrim(std::string& s, const char* t = WhiteSpaces)
{
	return s.erase(0, s.find_first_not_of(t));
}

// trim from both ends of string (right then left)
inline std::string& trim(std::string& s, const char* t = WhiteSpaces)
{
	return ltrim(rtrim(s, t), t);
}

static JSONValue* ReadElement(std::string::const_iterator& i, std::string::const_iterator ep, std::string* err)
{
	bool isDouble = false, haveError = false;
	std::string	val;
	while (i < ep)
	{
		char sym = (*i);
		if (sym < 0)
		{
			haveError = true;
			break;
		}
		if (sym == ',' || sym == ']' || sym == '}')
			break;

		val.append(1, sym);
		i++;
	}

	val = trim(val);

	const char* p = val.c_str();
	if (_stricmp(p, "true") == 0)
	{
		return new JSONValue(true);
	}
	else if (_stricmp(p, "false") == 0)
	{
		return new JSONValue(false);

	}
	else if (_stricmp(p, "null") == 0)
	{
		return new JSONValue();
	}

	if (!val.empty())
	{
		char* sep;
		if (strchr(p, '.') != NULL)
		{
			setlocale(LC_NUMERIC, "English");
			double dval = strtod(val.c_str(), (char**)&sep);
			if (*sep == '\0')
				return new JSONValue(dval);
		}
		else
		{
			long long lval = strtoll(val.c_str(), (char**)&sep, 10);
			if (*sep == '\0')
				return new JSONValue(lval);
		}
	}

	err->assign("Error in value ").append(val).append(1, *i);
	return NULL;

	//while (i < ep)
	//{
	//	char sym = (*i);
	//	if (sym == '-' || ((int)sym > 0 && isdigit(sym)) || sym == '+')
	//	{
	//	}
	//	else if (sym == 'e' || sym == 'E')
	//	{
	//		isDouble = true;
	//	}
	//	else if (sym == '.')
	//	{
	//		if (isDouble)
	//		{
	//			haveError = true;
	//			break;
	//		}
	//		isDouble = true;
	//	}
	//	else
	//	{
	//		haveError = (sym < 0 || !isspace(sym) && sym != ',' && sym != ']' && sym != '}');
	//		break;
	//	}
	//	val.append(1, sym);
	//	i++;
	//}

	//if (!haveError)
	//{
	//	char *ep;
	//	if (isDouble)
	//	{
	//		setlocale(LC_NUMERIC, "English");
	//		double dval = strtod(val.c_str(), (char**)&ep);
	//		if (*ep == '\0')
	//			return new JSONValue(dval);
	//	}
	//	else
	//	{
	//		long long lval = strtoll(val.c_str(), (char**)&ep, 10);
	//		if (*ep == '\0')
	//			return new JSONValue(lval);
	//	}
	//}

	//err->assign("Error in number ").append(val).append(1, *i);
	//return NULL;
}

static JSONValue* ReadValue(std::string::const_iterator& i, std::string::const_iterator ep, std::string* err)
{
	char sym = (*i);
	while ((int)*i > 0 && isspace(sym = (*i)) && i < ep)
		i++;

	if ((int)sym > 0 && isspace(sym))
	{
		err->assign("Only white spaces");
		return NULL;
	}

	if (sym == '"')
	{
		std::string* val = ReadString(++i, ep, err);
		if (val == NULL)
			return NULL;

		if (val->compare("true") == 0)
		{
			delete val;
			return new JSONValue(true);
		}
		if (val->compare("false") == 0)
		{
			delete val;
			return new JSONValue(false);
		}
		if (_stricmp(val->c_str(), "null") == 0)
		{
			delete val;
			return new JSONValue();
		}
		return new JSONValue(val);
	}

	if (sym == '[')
		return ReadArray(++i, ep, err);
	if (sym == '{')
		return ReadObject(++i, ep, err);

	return ReadElement(i, ep, err);
}

JSONValue* JSONReader::Parse(const std::string& buf)
{
	if (buf.empty())
	{
		error = "Emtpy string";
		return NULL;
	}

	std::string::const_iterator i = buf.begin();
	std::string::const_iterator ep = buf.end();
	if (*i == '\xef') // skip utf-8 marker
	{
		i += 3;
	}

	return ReadValue(i, ep, &error);
}


JSONWriter::JSONWriter()
{
}

JSONWriter::~JSONWriter()
{
}

static bool __compat_mode = true;
static void PutString(std::string* buf, const std::string& src)
{
	buf->append(1, '"');
	std::string::const_iterator i = src.begin();
	for (; i != src.end(); i++)
	{
		char sym = (*i);
		if (sym == '\"' || sym == '\\' || sym == '/')
		{
			buf->append(1, '\\');
			buf->append(1, sym);
		}
		else if (sym == '\b')
		{
			if(!__compat_mode)
				buf->append("\\b");
		}
		else if (sym == '\f')
		{
			if(!__compat_mode)
				buf->append("\\f");
		}
		else if (sym == '\n')
		{
			buf->append("\\n");
		}
		else if (sym == '\r')
		{
			buf->append("\\r");
		}
		else if (sym == '\t')
		{
			buf->append("\\t");
		}
		else 
		{
			if((unsigned)sym < 0x20) {
				char tsym[10];
				sprintf(tsym, "\\u%04x", (unsigned)sym);
				buf->append(tsym);
			} else {
				if(__compat_mode && sym == 0x7f) {

				} else {
					buf->append(1, sym);
				}
			}
		}
	}
	buf->append(1, '"');
}

bool JSONWriter::Write(std::string* buf, const JSONValue& value) const
{
	switch (value.type)
	{
	case JSONValue::jtTrue:
		buf->append("\"true\"");
		break;
	case JSONValue::jtFalse:
		buf->append("\"false\"");
		break;
	case JSONValue::jtNull:
		buf->append("\"null\"");
		break;
	case JSONValue::jtDouble:
	case JSONValue::jtInt:
	{
		std::ostringstream str;
		if (value.type == JSONValue::jtDouble)
			str << value.value.doubleValue;
		else
			str << value.value.longValue;
		buf->append(str.str());
		break;
	}
	case JSONValue::jtString:
		PutString(buf, *value.value.string);
		break;
	case JSONValue::jtObject:
	{
		buf->append("{");

		JSONObject::const_iterator i = value.value.object->begin();
		JSONObject::const_iterator ep = value.value.object->end();
		while (i != ep)
		{
			PutString(buf, i->first);
			buf->append(":");
			Write(buf, (*i->second));

			i++;
			if (i == ep)
				break;
			buf->append(",");
		}
		buf->append(" }");
		break;
	}
	case JSONValue::jtArray:
	{
		buf->append("[");
		JSONArray::const_iterator i = value.value.array->begin();
		JSONArray::const_iterator ep = value.value.array->end();
		for (; i != ep; i++)
		{
			Write(buf, *(*i));
			if (i + 1 != ep)
				buf->append(",");
		}
		buf->append("]");
		break;
	}
	}

	return true;
}
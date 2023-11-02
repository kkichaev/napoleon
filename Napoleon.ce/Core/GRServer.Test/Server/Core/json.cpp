/*
* Copyright (C), 2009, Денис Мосягин
*
* JSON
*
* ert   11/05/2020   creating
*/

#include "stdafx.h"
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
	return (IsInt() && this->value.longValue == value) || (IsDouble() && this->value.doblueValue == value);
}

bool JSONValue::operator==(double value) const
{
	return (IsInt() && this->value.longValue == value) || (IsDouble() && this->value.doblueValue == value);
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

JSONObject::~JSONObject()
{
	iterator i = begin();
	for (; i != end(); i++)
		delete i->second;
}

JSONArray::~JSONArray()
{
	iterator i = begin();
	for (; i != end(); i++)
		delete (*i);
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
	std::string *res = new std::string();
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
				i++;

				std::string val;
				char* ep;

				for (int j = 0; j < 4; j++, i++)
					val.append(1, *i);
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
	JSONArray *res = new JSONArray();
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
			member = ReadString(i, ep, err);
			if (member == NULL)
			{
				err->assign("Can't read object member name");
				haveError = true;
				break;
			}
		}
		else {
			err->assign("Wrong symbol in object");
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

static JSONValue* ReadNumber(std::string::const_iterator& i, std::string::const_iterator ep, std::string* err)
{
	bool isDouble = false, haveError = false;
	std::string	val;
	while (i < ep)
	{
		char sym = (*i);
		if (sym == '-' || ((int)sym > 0 && isdigit(sym)) || sym == '+')
		{
		}
		else if (sym == 'e' || sym == 'E')
		{
			isDouble = true;
		}
		else if (sym == '.')
		{
			if (isDouble)
			{
				haveError = true;
				break;
			}
			isDouble = true;
		}
		else
		{
			break;
		}
		val.append(1, sym);
		i++;
	}

	if (!haveError)
	{
		char *ep;
		if (isDouble)
		{
			setlocale(LC_NUMERIC, "English");
			double dval = strtod(val.c_str(), (char**)&ep);
			if (*ep == '\0')
				return new JSONValue(dval);
		}
		else
		{
			long long lval = strtoll(val.c_str(), (char**)&ep, 10);
			if (*ep == '\0')
				return new JSONValue(lval);
		}
	}

	err->assign("Error in number ");
	return NULL;
}

static JSONValue* ReadValue(std::string::const_iterator& i, std::string::const_iterator ep, std::string* err)
{
	char sym;
	while ((int)*i > 0 && isspace(sym = (*i)) && i < ep)
		i++;

	if ((int)sym > 0 && isspace(sym))
	{
		err->assign("Only white spaces");
		return NULL;
	}

	if (sym == '"')
	{
		std::string *val = ReadString(++i, ep, err);
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
		if (val->compare("null") == 0)
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

	return ReadNumber(i, ep, err);
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

	JSONValue* ret = ReadValue(i, ep, &error);
	return ret;
}


JSONWriter::JSONWriter()
{
}

JSONWriter::~JSONWriter()
{
}

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
			buf->append("\\b");
		}
		else if (sym == '\f')
		{
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
			buf->append(1, sym);
	}
	buf->append(1, '"');
}

bool JSONWriter::Write(std::string* buf, const JSONValue& value)
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
	case JSONValue::jtDoube:
	case JSONValue::jtInt:
	{
		std::ostringstream str;
		if (value.type == JSONValue::jtDoube)
			str << value.value.doblueValue;
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
		while (true)
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
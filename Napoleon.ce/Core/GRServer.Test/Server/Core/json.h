#pragma once

#include <vector>
#include <map>

class JSONObject;
class JSONArray;
struct JSONValue
{
	enum Type {
		jtObject, jtArray, jtString, jtInt, jtDoube, jtTrue, jtFalse, jtNull,
	} type;

	union {
		JSONObject *object;
		JSONArray *array;
		std::string* string;
		long long longValue;
		double doblueValue;
		bool boolValue;
	} value;

	JSONValue() : type(jtNull) { value.object = NULL; }
	JSONValue(JSONObject *object) : type(jtObject) { value.object = object; }
	JSONValue(JSONArray *array) : type(jtArray) { value.array = array; }
	JSONValue(std::string* string) : type(jtString) { value.string = string; }
	JSONValue(const std::string& src) : type(jtString) { std::string *strVal = new std::string(src); value.string = strVal; }
	JSONValue(const char* src) : type(jtString) { std::string *strVal = new std::string(src); value.string = strVal; }
	JSONValue(int longValue) : type(jtInt) { value.longValue = longValue; }
	JSONValue(long long longValue) : type(jtInt) { value.longValue = longValue; }
	JSONValue(double doblueValue) : type(jtDoube) { value.doblueValue = doblueValue; }
	JSONValue(bool boolValue) : type(boolValue ? jtTrue : jtFalse) { value.boolValue = boolValue; }

	bool IsNull() const { return type == jtNull; }
	bool IsObject() const { return type == jtObject; }
	bool IsArray() const { return type == jtArray; }
	bool IsString() const { return type == jtString; }
	bool IsInt() const { return type == jtInt; }
	bool IsDouble() const { return type == jtDoube; }
	bool IsTrue() const { return type == jtTrue; }
	bool IsFalse() const { return type == jtFalse; }

	~JSONValue();

	JSONValue& get(const std::string& name) const;

	JSONValue& operator[](const std::string& name) const { return get(name); }

	bool read(std::string* out, const std::string& name) const;

	bool operator==(const std::string& value) const;
	bool operator==(long long value) const;
	bool operator==(double value) const;
	bool operator==(int value) const { return operator==((long long)value); }
	bool operator==(unsigned value) const { return operator==((long long)value); }

	bool operator!=(const std::string& value) const { return !(operator==(value)); }
	bool operator!=(long long value) const { return !(operator==(value)); }
	bool operator!=(double value) const { return !(operator==(value)); }
};


class JSONObject : public std::map<std::string, JSONValue*>
{
public:
	~JSONObject();
};

class JSONArray : public std::vector<JSONValue*>
{
public:
	~JSONArray();
};

class JSONReader
{
public:
	JSONReader();
	~JSONReader();

	JSONValue* Parse(const std::string& buf);
	void GetError(std::string* err) const { err->assign(error); }

private:
	std::string error;
};

class JSONWriter
{
public:
	JSONWriter();
	~JSONWriter();

	bool Write(std::string* buf, const JSONValue& value);
	void GetError(std::string* err) const { err->assign(error); }

private:
	std::string error;
};
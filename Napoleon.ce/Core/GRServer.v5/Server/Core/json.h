#pragma once

#include <vector>
#include <map>

class JSONObject;
class JSONArray;
struct JSONValue
{
	enum Type {
		jtObject, jtArray, jtString, jtInt, jtDouble, jtTrue, jtFalse, jtNull,
	} type;

	union {
		JSONObject* object;
		JSONArray* array;
		std::string* string;
		long long longValue;
		double doubleValue;
		bool boolValue;
	} value;

	JSONValue() : type(jtNull) { value.object = NULL; }
	JSONValue(JSONObject* object) : type(jtObject) { value.object = object; }
	JSONValue(JSONArray* array) : type(jtArray) { value.array = array; }
	JSONValue(std::string* string) : type(jtString) { value.string = string; }
	JSONValue(const std::string& src) : type(jtString) { std::string* strVal = new std::string(src); value.string = strVal; }
	JSONValue(const char* src) : type(jtString) { std::string* strVal = new std::string(src); value.string = strVal; }
	JSONValue(int longValue) : type(jtInt) { value.longValue = longValue; }
	JSONValue(DWORD longValue) : type(jtInt) { value.longValue = longValue; }
	JSONValue(long long longValue) : type(jtInt) { value.longValue = longValue; }
	JSONValue(double doblueValue) : type(jtDouble) { value.doubleValue = doblueValue; }
	JSONValue(bool boolValue) : type(boolValue ? jtTrue : jtFalse) { value.boolValue = boolValue; }

	JSONValue(DWORD longValue, bool asString) : type(jtString)
	{
		char buf[20];
		wsprintfA(buf, "%u", longValue);
		value.string = new std::string(buf);
	}

	JSONValue(int longValue, bool asString) : type(jtString)
	{
		char buf[20];
		wsprintfA(buf, "%d", longValue);
		value.string = new std::string(buf);
	}

	bool IsNull() const { return type == jtNull; }
	bool IsObject() const { return type == jtObject; }
	bool IsArray() const { return type == jtArray; }
	bool IsString() const { return type == jtString; }
	bool IsInt() const { return type == jtInt; }
	bool IsDouble() const { return type == jtDouble; }
	bool IsTrue() const { return type == jtTrue; }
	bool IsFalse() const { return type == jtFalse; }

	~JSONValue();

	JSONValue& get(const std::string& name) const;

	JSONValue& operator[](const std::string& name) const { return get(name); }

	// to safe free object
	void SetNull() { type = jtNull; value.object = NULL; }

	bool read(std::string* out, const std::string& name) const;
	bool read(DWORD* out, const std::string& name) const;
	bool read(int* out, const std::string& name) const;
	bool read(float* out, const std::string& name) const;

	bool operator==(const std::string& value) const;
	bool operator==(long long value) const;
	bool operator==(double value) const;

	bool operator!=(const std::string& value) const { return !(operator==(value)); }
	bool operator!=(long long value) const { return !(operator==(value)); }
	bool operator!=(double value) const { return !(operator==(value)); }

	bool Compose(JSONValue* dest, const std::vector<std::string>& tags) const;

	JSONValue* Copy() const;

	void dump(std::stringstream* out) const;
};


class JSONObject : public std::map<std::string, JSONValue*>
{
public:
	~JSONObject();

	JSONObject& Put(const std::string& name, const std::string& value) { insert(value_type(name, new JSONValue(value))); return *this; }
	JSONObject& Put(const std::string& name, int value) { insert(value_type(name, new JSONValue(value))); return *this; }
	JSONObject& Put(const std::string& name, DWORD value) { insert(value_type(name, new JSONValue(value))); return *this; }
	JSONObject& Put(const std::string& name, JSONArray* value) { insert(value_type(name, new JSONValue(value))); return *this; }

	JSONObject& Put(const std::string& name, long long value) { insert(value_type(name, new JSONValue(value))); return *this; }
	JSONObject& Put(const std::string& name, double value) { insert(value_type(name, new JSONValue(value))); return *this; }

	JSONObject& Put(const std::string& name, JSONValue* value) { insert(value_type(name, value)); return *this; }

	JSONValue* Copy() const;

	void dump(std::stringstream* out) const;
};

class JSONArray : public std::vector<JSONValue*>
{
public:
	~JSONArray();
	JSONValue* Copy() const;
	void dump(std::stringstream* out) const;
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

	bool Write(std::string* buf, const JSONValue& value) const;
	void GetError(std::string* err) const { err->assign(error); }

private:
	std::string error;
};

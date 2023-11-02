#include "stdafx.h"
#include "postgre.h"

#include <fstream>

using namespace GRServer;
using namespace std;

const std::string USER_TAG("user");
const std::string PWD_TAG("password");
const std::string HOST_TAG("host");
const std::string DB_TAG("database");


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

/*
postgresql://[userspec@][hostspec][/dbname][?paramspec]
where userspec is:
user[:password]
and hostspec is:
[host][:port][,...]
and paramspec is:
name=value[&...]

*/

Config::Config()
{

}

bool Config::Load(const std::string& fileName)
{
	fstream f;
	f.open(fileName, ios_base::in);
	if (!f.good())
		return false;

	std::string str;
	while (getline(f, str, '\n'))
	{
		size_t pos = str.find('=');
		if (pos == string::npos)
			continue;

		std::string key = str.substr(0, pos);
		trim(key);
		std::string value = str.substr(pos + 1);
		trim(value);
		if (key == USER_TAG) user = value;
		else if (key == PWD_TAG) password.assign(":").append(value);
		else if (key == HOST_TAG) host = value;
		else if (key == DB_TAG) database = value;
		else params[key] = value;
	}
	f.close();

	return true;
}

bool Config::Save(const std::string& fileName)
{
	fstream f;
	f.open(fileName, ios_base::out | ios_base::trunc);
	if (!f.good())
		return false;

	f << USER_TAG << '=' << user << '\n';
	if(!password.empty())
		f << PWD_TAG << '=' << password << '\n';
	f << HOST_TAG << '=' << host << '\n';
	f << DB_TAG << '=' << database << '\n';

	std::map<std::string, std::string>::const_iterator i = params.begin();
	for (; i != params.end(); i++)
	{
		f << i->first << '=' << i->second << '\n';
	}
	f.close();
	return true;
}

void Config::GetConnectionString(std::string* out) const
{
	out->assign("postgresql://").append(user).append(password).append(1, '@').append(host).append(1, '/').append(database);

	std::map<std::string, std::string>::const_iterator i = params.begin();
	for (; i != params.end(); i++)
	{
		out->append(1, i == params.begin() ? '?' : '&').append(i->first).append(1, '=').append(i->second);
	}
}

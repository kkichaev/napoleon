#include "gtkservice.h"

#ifndef WIN32
inline int stricmp(const char *s1, const char *s2) { return strcasecmp(s1, s2); }
#endif

void Trim(std::string* res, const std::string& _src, size_t offset, size_t size)
{
	const std::string& src = _src.substr(offset, size);

	size_t es = 0, ss = 0;
	std::string::const_reverse_iterator rb = src.rbegin();
	while (rb != src.rend())
	{
		if (isspace(*rb) == 0) break;
		rb++;
		es++;
	}

	std::string::const_iterator b = src.begin();
	while (b != src.end())
	{
		if (isspace(*b) == 0) break;
		b++;
		ss++;
	}

	res->assign(src.substr(ss, src.size() - es - ss));
}

bool ReadLine(std::string* v, FILE *f)
{
	char buf[100];

	if (fgets(buf, sizeof(buf), f) == NULL) return false;

	v->clear();

	do
	{
		char *p = strchr(buf, '\n');
		if (p != NULL)
		{
			*p = '\0';
			v->append(buf, p - buf);
			break;
		}

		v->append(buf, strlen(buf));
	} while (fgets(buf, sizeof(buf), f) != NULL);

	return true;
}

void Config::SetValue(const std::string& key, const std::string& value)
{
	const char *k = key.c_str();

	if (stricmp(k, "port") == 0) port = atoi(value.c_str());
	else if (stricmp(k, "readtimeout") == 0) atoi(value.c_str());
	else if (stricmp(k, "dbHost") == 0)			dbHost = value;
	else if (stricmp(k, "dbDatabase") == 0)	dbDatabase = value;
	else if (stricmp(k, "dbLogin") == 0)		dbLogin = value;
	else if (stricmp(k, "dbPassword") == 0)	dbPassword = value;
	else if (stricmp(k, "dbPort") == 0)			dbPort = atoi(value.c_str());
	else if (stricmp(k, "vncPortMin") == 0)	vncPortMin = atoi(value.c_str());
	else if (stricmp(k, "vncPortMax") == 0)	vncPortMax = atoi(value.c_str());
	else if (stricmp(k, "uploadFolder") == 0) {
		uploadFolder = value;
		while (true)
		{
			size_t fnd = uploadFolder.find("\\");
			if (fnd == std::string::npos)
				break;
			uploadFolder.replace(fnd, 1, 1, '/');
		}
		char esym = *uploadFolder.rbegin();
		if (esym != '/')
			uploadFolder.append(1, '/');
	}
	//else if (stricmp(k, "deviceConnectsTable") == 0)	deviceConnectsTable = value;
	//else if (stricmp(k, "deviceConnectsDataTable") == 0)	deviceConnectsDataTable = value;
}

bool Config::Read()
{
#ifdef WIN32
	const char* fileName = "gklservice.ini";
#else
	const char* fileName = "/etc/gklservice/gklservice.ini";
#endif

	port = 7654;
	readtimeout = 5; // in seconds

	vncPortMin = 1000;
	vncPortMax = 50000;

	dbHost = "";
	dbDatabase = "";
	dbLogin = "";
	dbPassword = "";
	dbPort = 3306;

	deviceConnectsTable = "device_connects";
	deviceConnectsDataTable = "device_connects_data";
	uploadFolder = "./";


	FILE *rd = fopen(fileName, "rt");
	if (rd != NULL)
	{
		std::string line;
		while (ReadLine(&line, rd))
		{
			if (line.empty() || *line.begin() == '#')
				continue;

			size_t pos = line.find('=');
			if (pos != std::string::npos)
			{
				std::string key, value;

				Trim(&key, line, 0, pos);
				Trim(&value, line, pos + 1, -1);

				SetValue(key, value);
			}
		}
	}

	return true;
}


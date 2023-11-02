/*
 * Copyright (C), 2009 - 2022, Denis Mosiagin
 *
 * Reporter plugin
 *
 * ert   16/06/2012   creating
 */
#include "stdafx.h"
#include "Reporter.h"
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#include <ServerDefs.h>

using namespace GRServer;

static const char PYTHON_HOME[] = "PythonHome";
static const char PYTHON_DEBUG[] = "Debug";
static const char COM_SLOTS[] = "COMSlots";
static const char COM_LIVE_TIME[] = "COMLiveTime";
static const char USER_SITE_TAG[] = "UserSite";

Config::Config()
{
	debug = false;
	maxCOMSlots = 5;
	liveTimeCOM = 4 * 3600;
}

static bool ReadLine(std::string *line, FILE* file)
{
   line->clear();

   if( file == NULL || feof(file) ) return false;

   while( !feof(file) )
   {
      char buf[200];
      if( fgets(buf, sizeof(buf), file) == NULL )
         break;

      char *newLine = strchr(buf, '\n');
      if( newLine != NULL )
         *newLine = '\0';

      line->append(buf);
   
      if( newLine != NULL )
         break;
   }

   return true;
}

static void Trim(std::string* res, const std::string& _src, size_t offset, size_t size)
{
   const std::string& src = _src.substr(offset, size);

   int es = 0, ss = 0;
   std::string::const_reverse_iterator rb = src.rbegin();
   while( rb != src.rend() )
   {
      if( *rb != ' ' ) break;
      rb++;
      es++;
   }

   std::string::const_iterator b = src.begin();
   while( b != src.end() )
   {
      if( *b != ' ' ) break;
      b++;
      ss++;
   }

   res->assign(src.substr(ss, src.size() - es - ss));
}

static bool ReadBoolean(bool *dest, const std::string& value)
{
	bool ret = false;
	const char* str = value.c_str();
	if (_stricmp(str, "true") == 0)
	{
		*dest = true;
		ret = true;
	}
	else if (_stricmp(str, "false") == 0)
	{
		*dest = false;
		ret = true;
	}

	return ret;
}

void Config::SetValue(const std::string& key, const std::string& value, const IServerConfig& scfg)
{
   if (key.compare(PYTHON_HOME) == 0) pythonHome = value;
   else if (key.compare(PYTHON_DEBUG) == 0)
   {
      if (!ReadBoolean(&debug, value))
      {
         debug = false;
         debugFile = value;
      }
      else
         debugFile.clear();
   }
   else if (key.compare(COM_SLOTS) == 0)
      maxCOMSlots = atoi(value.c_str());
   else if (key.compare(COM_LIVE_TIME) == 0)
      liveTimeCOM = atol(value.c_str());
   else if (key.compare(USER_SITE_TAG) == 0)
   {
      std::string tstr;
      MakeFullFileName(&tstr, value, scfg.ConfigFolder());
      userSites.push_back(tstr);
   }
   else
		configs[key] = value;
}

bool Config::Load(const std::string& fileName, const IServerConfig& scfg)
{
   FILE* f = fopen(fileName.c_str(), "rt");
   if( f == NULL )
      return false;

   std::string line;
   while(ReadLine(&line, f))
   {
      size_t pos = line.find('=');
		if( pos != std::string::npos )
      {
         std::string key, value;

         Trim(&key, line, 0, pos);
         Trim(&value, line, pos+1, -1);

         SetValue(key, value, scfg);
      }
   }
   fclose(f);
   return true;
}

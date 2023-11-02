/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* Update
*
*  ert   19/10/2009   creating
*/
#ifndef __NPL_UPDATE_H
#define __NPL_UPDATE_H

#include <string>
#include <NetExchange.h>
#include "UpdateConfig.h"

#define IDC_UPDATE   1000
#define IDC_DOWNLOAD 1100
#define IDC_ERROR    1200

class State;
class ProgConfig : public UpdateConfig
{
public:
   ProgConfig();

   State* curState;

protected:
   virtual bool AddLoad(FILE *rd);
   virtual bool AddSave(FILE* wr) const;
};

class State
{
public:
   static bool Load(ProgConfig* config, FILE *rd);
   static State* Find(const wchar_t* name);

public:
   virtual State* Execute() = 0;
   virtual void Write(FILE* wr) const {}
   virtual bool IsSelfUpdate() const { return false; }

   void WriteState(FILE* wr) const;

protected:
   State(const wchar_t* name) { this->name = name; AddState(this); }
   virtual ~State() { RemoveState(this); }

   const wchar_t* Name() const { return name; }

   virtual bool Load(FILE* rd) { return true; }

   const wchar_t *name;

protected:
   static void AddState(State *state); // все состояния создаются статическими
   static void RemoveState(State *state);
};

class Application
{
public:
   Application();
   ~Application();

   bool Start(HINSTANCE hInstance, const wchar_t* cmdLine);

   void Do(const wchar_t *stateFile);
   void Do();

   HINSTANCE GetInstance() const { return hInstance; }

   // return true if choice make update
   bool Alert(const wchar_t* text, DWORD id) const;

   void GetUpdateFolder(std::wstring *folder);
   void GetAppFolder(std::wstring *folder);
   void GetProgName(std::wstring *name);

   //void UpdateSelf(const std::wstring& fileName);

   void AddNetListner();
   void RemoveListner();

   bool SaveConfig(State* curState);

   const ProgConfig& Config() const { return config; }
   void ResetVersion();
   void SetVersion(const wchar_t* version, int size);
   const std::wstring& ConfigFileName() const { return configFileName; }

   void CommitUpdate();

private:
   ProgConfig config;
   std::wstring configFileName;

   HINSTANCE hInstance;
   HANDLE hMutex;
};

extern Application app;


bool InstallUpdate(const std::wstring& updateFile);
void RemoveUpdateFiles(const std::wstring& updateFile);

void GetIntStateFile(std::wstring* fileName, const wchar_t *category);
void Log(const char* msg, ... );

#endif


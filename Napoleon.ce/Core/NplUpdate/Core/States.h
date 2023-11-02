/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* States
*
*  ert   09/11/2009   creating
*/
#ifndef __NPL_UPD_STATES_H
#define __NPL_UPD_STATES_H

#include "NplUpdate.h"

class FNState : public State
{
public:
   FNState(const wchar_t *name) : State(name) {}

   virtual bool Load(FILE* rd) { return ReadString(rd, &fileName); }
   virtual void Write(FILE* wr) const { WriteString(wr, fileName); }

   void SetFileName(const std::wstring& fileName) { this->fileName = fileName; }

protected:
   std::wstring fileName;
};

class CheckUpdate : public State
{
public:
   CheckUpdate() : State(CHECK_UPDATE_ACTION) {}
   virtual State* Execute();
};

class Update : public State
{
public:
   static const wchar_t* StateName;

   Update() : State(DO_UPDATE_ACTION), size(0) {}

   virtual bool Load(FILE* rd)
   {
      return (fread(&size, sizeof(size), 1, rd) == 1);
   }
   
   virtual void Write(FILE* wr) const
   {
      fwrite(&size, sizeof(size), 1, wr);
   }

   virtual State* Execute();

   void Clear();

protected:
   DWORD size;

   State* Receive(const wchar_t* cmd, const wchar_t* cParam);

   void GetUpdateFileName(std::wstring *fileName);

   DWORD GetUpdateSize();
   bool WriteUpdate(ReceivedStream* stream);
};

class DecodeState : public FNState
{
public:
   DecodeState();
   virtual State* Execute();
};

class Restore : public FNState
{
public:
   Restore();
   virtual State* Execute();
};

class Install : public State
{
public:
   Install();

   virtual bool Load(FILE* rd);
   virtual void Write(FILE* wr) const;

   void SetUpdatedFiles(const std::vector<std::wstring>& files);
   virtual State* Execute();

protected:
   Install(const wchar_t* stateName) : State(stateName) {}

   std::vector<std::wstring> files;

   bool UpdateSelf(std::wstring* fileName);
};

class MoveTo : public Install
{
public:
   MoveTo();

   virtual bool IsSelfUpdate() const { return true; }

   virtual void RunProc(const std::wstring& fileName);

   virtual bool Load(FILE* rd);
   virtual void Write(FILE* wr) const;

   virtual State* Execute();

protected:
   MoveTo(const wchar_t* stateName) : Install(stateName) {}

   std::wstring fileName;
};

class RemoveProc : public MoveTo
{
public:
   RemoveProc();

   virtual State* Execute();
};

class ErrorState : public State
{
public:
   ErrorState() : State(L"error") {}

   virtual State* Execute();
   void SetText(const wchar_t* str);

protected:
   std::wstring message;
};

extern CheckUpdate checkUpdate;
extern Update updateState;
extern DecodeState decodeState;
extern Restore restoreState;
extern Install installState;
extern MoveTo moveTo;
extern RemoveProc removeProc;
extern ErrorState errorState;

#endif
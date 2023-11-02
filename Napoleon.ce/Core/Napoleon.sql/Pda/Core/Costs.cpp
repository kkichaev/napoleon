/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Работа с ценами
 * 
 *  ert   01/06/2009   creating
 */ 
#include "stdafx.h"

#include <Module.h>
#include "Costs.h"
#include "Network.h"
#include <DataReader.h>

#define COST_FILE L"NapoleonCosts"

using namespace std;

bool CostManager::loaded = false;
vector<string> CostManager::price;
CostManager::CostList CostManager::costTypes;

Costs CostManager::header;

void CostManager::Clear()
{
   price.clear();
   costTypes.clear();

   loaded = false;
}

void CostManager::LoadCostData()
{
   wstring fileName;
   _Module.MakeFileName(&fileName, COST_FILE);

   FILE *f = _wfopen(fileName.c_str(), L"rb");
   if( !f )
      return;

   fread(&header, sizeof(header), 1, f);

   DWORD len = header.costTypeOffset - sizeof(header);
   BYTE *buf = (BYTE*)malloc(len);
   fread(buf, len, 1, f);

   int count = header.priceCount;
   char *p = (char*)buf;
   while( count-- > 0 )
   {
      string val(p);
      p += val.size() + 1;
      price.push_back(val);
   }
   free(buf);

   len = header.priceOffset - header.costTypeOffset;
   buf = (BYTE*)malloc(len);
   fread(buf, len, 1, f);
   count = header.costCount;
   p = (char*)buf;
   while( count-- > 0 )
   {
      CostItem ci;
      ci.id.assign(p);
      p += ci.id.size() + 1;

      ci.name.assign(p);
      p += ci.name.size() + 1;

      costTypes.push_back(ci);
   }
   free(buf);

   fclose(f);
   loaded = true;
}

const CostManager::CostList& CostManager::CostTypes()
{
   if( !loaded )
      LoadCostData();

   return costTypes;
}

DWORD CostManager::CostIndex(const wchar_t* costType)
{
   if( !loaded )
      LoadCostData();

   int len = wcslen(costType) + 1;
   char *buf = (char*)malloc(len * sizeof(char));
   wcstombs(buf, costType, len);

   int ci = costTypes.size() - 1;
   while( ci >= 0 )
   {
      const CostItem& cti = costTypes[ci];
      if( cti.id.compare(buf) == 0 )
         break;

      ci--;
   }

   free(buf);
   return ( ci < 0 ) ? 0 : ci;
}

DWORD CostManager::GetCost(const wchar_t *itemID, const wchar_t *costType)
{
   DWORD ci = CostIndex(costType);
   return GetCost(itemID, ci);
}

DWORD CostManager::GetCost(const wchar_t *itemID, DWORD costType)
{
   if( !loaded )
      LoadCostData();

   if( costType >= costTypes.size() )
      return 0;

   int len = wcslen(itemID) + 1;
   char *buf = (char*)alloca(len * sizeof(char));
   wcstombs(buf, itemID, len);

   int l = 0, r = price.size()-1, pi;
   int cmp = -1;
   while( l <= r )
   {
      pi = (r+l) / 2;

      cmp = price[pi].compare(buf);
      if( cmp < 0 ) l = pi + 1;
      else if( cmp > 0 ) r = pi - 1;
      else break;
   }
   if( cmp != 0 )
      return 0;

   wstring fileName;
   _Module.MakeFileName(&fileName, COST_FILE);

   FILE *f = _wfopen(fileName.c_str(), L"rb");
   if( !f )
      return 0;

   fseek(f, (pi * costTypes.size() + costType) * sizeof(DWORD) + header.priceOffset, SEEK_SET);
   DWORD val;
   fread(&val, sizeof(val), 1, f);
   fclose(f);

   return val;
}

class CostWriter : public BinaryFileWriter
{
public:
   CostWriter() { }

   virtual ~CostWriter() { }

   virtual void GetFileName(std::wstring* fileName)
   {
      _Module.MakeFileName(fileName, COST_FILE);
   }

   virtual void AfterWrite(IReflectableData* data, const std::wstring& fileName)
   {
   }
};

struct CostData : public IReflectableData
{
   wchar_t *dummy;
   DECLARE_TYPE_REFLECTION(CostData)
};

BEGIN_TYPE_REFLECTION(CostData)
   REGISTER_STRING_MEMBER(CostData, dummy)
END_TYPE_REFLECTION(CostData)

class CostReceiver : public IReceiveObject
{
 public:
   CostReceiver() { reader = NULL; }

   virtual const wchar_t* Name() const { return L"Cost"; }
   virtual const wchar_t* ProgressText() const { return L"Обработка цен"; }

   virtual const wchar_t* Command() const { return GET_COMMAND; }
   virtual const wchar_t* Params() const { return Name(); }

   virtual bool Read(ReceivedStream* stream)
   {
      if( reader == NULL )
         reader = DataReader::CreateReader(data.GetType(), stream, FileWriter);

      return reader->Read(&data, stream);
   }

   virtual void Close()
   {
      delete reader;
      reader = NULL;

      delete fileWriter;
      fileWriter = NULL;
   }

   static IBinaryWriter* FileWriter(const wchar_t* fieldName)
   {
      if( wcscmp(fieldName, L"data") != 0 ) return NULL;

      ATLASSERT(fileWriter == NULL);

      fileWriter = new CostWriter();
      return fileWriter;
   }

protected:
   CostData data;
   DataReader* reader;
   static CostWriter *fileWriter;
};

CostWriter *CostReceiver::fileWriter;

long CostManager::ReceiveCosts(std::wstring *answer, IProgressIndicator *pi)
{
   CostReceiver cr;
   ReceivePacketParam param(pi);

   param.clearBase = false;
   param.objects.push_back(&cr);

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReceive, &param, 0, NULL);
   _Module.WaitThreadComplete(thread);
   *answer = param.answer;

   return param.ec;
}
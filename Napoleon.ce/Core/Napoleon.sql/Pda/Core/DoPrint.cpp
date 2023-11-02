/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Печать
 *
 *  ert   28/05/2008   creating
 */
#include "stdafx.h"

#include "DoPrint.h"
#include <Module.h>

//#define DEBUG_FANTOM 1

#ifdef DEBUG
#define TEST_BMP 1
#endif

class PrintCancel : public IPrintCancel
{
public:
   PrintCancel() : printer(NULL), thread(NULL) {}

   virtual void Cancel()
   {
      if( printer != NULL )
      {
         printer->Cancel();
         delete printer;
         printer = NULL;
      }

      if( thread != NULL )
      {
         TerminateThread(thread, 0);
         thread = NULL;
      }
   }

   IPrinter *printer;
   HANDLE thread;
};


typedef IConnect* (*TGetConnection)(int index);
typedef void (*TGetPrinter)(IPrinter **printer);

const char CONNECT_CONFIG[] = "NplConnect.cfg";

ConnectConfig::ConnectConfig() : data(NULL)
{
   copies = 1;
}

ConnectConfig::~ConnectConfig()
{
   if( data != NULL )
   {
      delete data->addr;
      delete data->name;
      delete data;
   }
}

void ConnectConfig::SetData(ConnectData *_data)
{
#ifdef DEBUG_F
#else
   delete data;

   data = new ConnectData();

   int len = wcslen(_data->name) + 1;
   data->name = new wchar_t [len];
   wcscpy(data->name, _data->name);

   data->addrLen = _data->addrLen;
   data->addr = (sockaddr*)new BYTE [_data->addrLen];
   memmove(data->addr, _data->addr, _data->addrLen);

   data->connectID = _data->connectID;
#endif
}

bool ConnectConfig::Save()
{
   std::string fn;
   _Module.MakeFileName(&fn, CONNECT_CONFIG);

   FILE *f = fopen(fn.c_str(), "wb");
   if( f == NULL ) return false;

   WORD val;

   val = type.size();
   fwrite(&val, sizeof(val), 1, f);
   fwrite(type.c_str(), val, 1, f);

#ifdef DEBUG_F
#else
   val = wcslen(data->name);
   fwrite(&val, sizeof(val), 1, f);
   fwrite(data->name, sizeof(wchar_t) * val, 1, f);
   fwrite(&data->addrLen, sizeof(data->addrLen), 1, f);
   fwrite(data->addr, data->addrLen, 1, f);
   fwrite(&data->connectID, sizeof(data->connectID), 1, f);
#endif

   fwrite(&copies, sizeof(copies), 1, f);

   fclose(f);
   return true;
}

bool ConnectConfig::Load()
{
   std::string fn;
   _Module.MakeFileName(&fn, CONNECT_CONFIG);

   FILE *f = fopen(fn.c_str(), "rb");
   if( f == NULL ) return false;

   WORD val;
   fread(&val, sizeof(val), 1, f);
   char *tstr = (char*)alloca(val+1);
   fread(tstr, val, 1, f);
   tstr[val] = '\0';
   type = tstr;

#ifdef DEBUG_F
#else
   data = new ConnectData();
   memset(data, 0, sizeof(*data));

   fread(&val, sizeof(val), 1, f);
   data->name = new wchar_t [val+1];
   fread(data->name, val * sizeof(wchar_t), 1, f);
   data->name[val] = L'\0';

   fread(&data->addrLen, sizeof(data->addrLen), 1, f);
   data->addr = (struct sockaddr*)new BYTE [data->addrLen];
   fread(data->addr, data->addrLen, 1, f);
   fread(&data->connectID, sizeof(data->connectID), 1, f);
#endif

   fread(&copies, sizeof(copies), 1, f);

   fclose(f);
   return true;
}

struct PrintParam
{
   //IPrinter *printer;
   const wchar_t *formName;
   IDataSource *source;
   IProgressIndicator *pc;
   PrintCancel *cancel;
   int copies;

   bool retVal;
};

static IPrinter *printer;
static HINSTANCE hPrint;
void CALLBACK CloseProc()
{
   delete printer;
   printer = NULL;

   FreeLibrary(hPrint);
   hPrint = NULL;
}

static DWORD Printing(PrintParam *param)
{
   param->retVal = false;

   if( printer != NULL )
   {
      CloseProc();
   }

   if( hPrint == NULL )
      hPrint = LoadLibrary(L".\\NPrinter.dll");
   if( hPrint == NULL )
   {
      printer = NULL;
      return 0;
   }

#ifdef TEST_BMP

   TGetPrinter tg = (TGetPrinter)GetProcAddress(hPrint, L"GetPrinter");
   if( tg == NULL )
   {
      FreeLibrary(hPrint);
      return 0;
   }

   tg(&printer);

   param->retVal = printer->Print(param->formName, param->source, param->pc, param->copies);

   delete printer;
   printer = NULL;
   FreeLibrary(hPrint);

#else

   if( param->pc != NULL )
      param->pc->SetText(L"Подключение к принтеру...");

   TGetPrinter tg = (TGetPrinter)GetProcAddress(hPrint, L"GetPrinter");
   if( tg == NULL )
   {
      printer = NULL;
      FreeLibrary(hPrint);
      return 0;
   }

   bool connected = false;
   ConnectConfig cfg;
   if( cfg.Load() == false )
   {
      MessageBox(GetActiveWindow(), L"Настройте, пожалуйста, принтер перед печатью.", L"Ошибка", MB_OK|MB_ICONSTOP);

      delete printer;
      printer = NULL;
      FreeLibrary(hPrint);
      return 0;
   }

   if( printer == NULL )
   {
      tg(&printer);
#ifndef DEBUG_FANTOM
      connected = printer->Connect(*cfg.data, (PrinterType)cfg.type.c_str());
#else
      connected = printer->Connect(*cfg.data, "deskjet 3320");
#endif
   } else
      connected = true;

   if( !connected )
   {
      MessageBox(GetActiveWindow(), L"Не могу соединиться с принтером.", L"Ошибка", MB_OK|MB_ICONSTOP);

      delete printer;
      FreeLibrary(hPrint);
      return 0;
   }

   param->cancel->printer = printer;
   param->copies = cfg.copies;
   param->retVal = printer->Print(param->formName, param->source, param->pc, param->copies);

   //closeTimer = SetTimer(NULL, 0, 5000, TimerProc);
   //delete printer;
   //printer = NULL;
   //FreeLibrary(hPrint);

#endif
   return 0;
}

//#define TEST_BMP 1

bool DoPrint(const wchar_t *formName, IDataSource *source, IProgressIndicator *pc, IPrintCanceller *printCanceller)
{
   PrintCancel printCancel;
   PrintParam param;

   param.formName = formName;
   param.pc = pc;
   param.source = source;
   param.retVal = false;
   param.cancel = &printCancel;

   param.copies = 1;

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)Printing, &param, 0, NULL);

   printCancel.thread = thread;
   //printCanceller->SetCanceller(&printCancel);

   _Module.WaitThreadComplete(thread);

   printCanceller->SetCanceller(NULL);

   return true;
}


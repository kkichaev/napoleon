/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Интерфейс печати
 *
 *  ert   09/05/2008   creating
 */ 
#ifndef __PRINT_DEF_H
#define __PRINT_DEF_H

#include <string>
#include <atldef.h>
#include <Reflection.h>

typedef char* PrinterType;

struct ConnectData
{
   wchar_t *name;
   struct sockaddr *addr;
   int addrLen;

   WORD connectID;
};

struct IConnect
{
   virtual bool Connect(const ConnectData& data) = 0;

   virtual bool LookupPrepare() = 0;
   virtual ConnectData* LookupNext() = 0;

   virtual bool Read(BYTE *buf, DWORD *len) = 0;
   virtual bool Write(const BYTE *buf, DWORD *len) = 0;

   virtual void PreparePrint() = 0;
   virtual void EndPrint() = 0;

   virtual const char *Name() const = 0;

   virtual void Close() = 0;

   virtual ~IConnect() {}
};

struct IDataSource
{
   virtual ~IDataSource() {}

   // не вызывать деструктор - все удаляется снаружи
   virtual IDataSource *GetObject(const wchar_t *name) = 0;

   virtual bool GetValue(std::wstring *value, const wchar_t *name) = 0;

   // return true if source have next data
   virtual bool HaveMoreData() const = 0;

   virtual bool MoveNext() = 0;

   virtual void StartPage() = 0;

   virtual void PrintData() = 0;
};

struct IPrinter
{
   virtual ~IPrinter() {}

   virtual PrinterType* GetPrinterTypes(int *count) = 0;

   virtual char**  GetPrinterDesc(int *count) = 0;

   virtual bool Connect(const ConnectData& data, PrinterType printer) = 0;

   virtual bool Print(const wchar_t *name, IDataSource *source, IProgressIndicator *pc, int copies) = 0;

   virtual void Cancel() = 0;
};

#ifdef UNDER_CE

extern "C"
{
   void GetPrinter(IPrinter **printer);

   // return NULL if index > connection count
   IConnect* GetConnection(int index);
}

#endif

#endif

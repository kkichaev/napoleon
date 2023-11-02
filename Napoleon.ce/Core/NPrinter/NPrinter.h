/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Интерфейс печати
 *
 *  ert   09/05/2008   creating
 */ 
#ifndef __NAPOLEON_PRINTER_H
#define __NAPOLEON_PRINTER_H

#include "Print.h"
#include <NForm.h>
#include <Reflection.h>

enum ConnectionID
{
   BTConnectID,
};

#include "hpprintapi.h"

using namespace APDK_NAMESPACE;

class NPlatform : public SystemServices
{
public:
   NPlatform(IConnect *connect, PrinterType printer);
   ~NPlatform();

   virtual void DisplayPrinterStatus (DISPLAY_STATUS ePrinterStatus);
   virtual DRIVER_ERROR BusyWait(DWORD msec);
   virtual DRIVER_ERROR ReadDeviceID(BYTE* strID, int iSize);
   virtual BOOL GetStatusInfo(BYTE* bStatReg);

   virtual DRIVER_ERROR ToDevice(const BYTE* pBuffer, DWORD* dwCount);
   virtual DRIVER_ERROR FromDevice(BYTE* pReadBuff, DWORD* wReadCount);

   virtual BYTE* AllocMem (int iMemSize) { return new BYTE [iMemSize]; }
   virtual void FreeMem (BYTE* pMem) { delete pMem; }

   virtual DWORD GetSystemTickCount (void) { return GetTickCount(); }

   virtual float power(float x, float y) { return 0; }

   void CancelJob();

   void PreparePrint() { connect->PreparePrint(); }
   void EndPrint() { connect->EndPrint(); }

   PrinterType Type() const { return printType; }

protected:
   IConnect *connect;

   bool cancel;
   PrinterType printType;
};

class NPrinter : public IPrinter
{
public:
   NPrinter();
   ~NPrinter();

   virtual PrinterType* GetPrinterTypes(int *count);

   virtual char**  GetPrinterDesc(int *count);

   virtual bool Connect(const ConnectData& data, PrinterType printer);

   virtual bool Print(const wchar_t *name, IDataSource *source, IProgressIndicator *pc, int copies);

   virtual void Cancel();

protected:
   void Close();

protected:
   static PrinterType printerTypes[];
   static char*     printerDesc[];

   NPlatform *printer;
   PrintContext *printContext;
   Job *job;
   BYTE *line;
   HBITMAP hbmp;

   bool startPrinting;
};

class BTConnection : public IConnect
{
 public:

   BTConnection();
   ~BTConnection();

   virtual bool Connect(const ConnectData& data);

   virtual bool LookupPrepare();
   virtual ConnectData* LookupNext();

   virtual bool Read(BYTE *buf, DWORD *len);
   virtual bool Write(const BYTE *buf, DWORD *len);

   virtual void PreparePrint() {}
   virtual void EndPrint() {}

   virtual const char *Name() const { return "Bluetooth"; }

   virtual void Close();

 protected:
   SOCKET sock;
   HANDLE hLookup;
   DWORD mode;
}; 

#endif

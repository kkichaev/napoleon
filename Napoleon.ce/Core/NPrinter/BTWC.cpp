/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Соединение через Bluetooth Widcomm
 *
 *  ert   23/07/2008   creating
 */ 
#include "stdafx.h"
#include "Print.h"

#include "BtSdkCE.h"
#include <vector>
#include <set>

#include <stdlib.h>
#include <stdio.h>

using namespace std;

#define TEMP_PRINT L"TempPrint"

HINSTANCE hInst;

class COMConnect : public CSppClient
{
public:
   COMConnect() : connected(false), hcom(INVALID_HANDLE_VALUE), port(0)
   {
   }

   virtual void OnClientStateChange(BD_ADDR bda, DEV_CLASS cls, BD_NAME name, short port, SPP_STATE_CODE state)
   {
      this->port = port;
      connected = (state == SPP_CONNECTED);
   }

   bool Open();
   void Close();

   DWORD Write(const void *buf, DWORD len)
   {
      if( hcom == INVALID_HANDLE_VALUE ) return 0;

      WriteFile(hcom, buf, len, &len, NULL   );
      return len;
   }

   bool connected;
   short port;

   HANDLE hcom;
};

bool COMConnect::Open()
{
   if( !connected || port == 0 ) return false;

   wchar_t buf[20];
   if (port >= 10)
      wsprintf (buf, L"BTC%d:", port - 10);
   else
      wsprintf (buf, L"COM%d:", port);

   hcom = CreateFile(buf, GENERIC_READ | GENERIC_WRITE, 0, NULL, OPEN_EXISTING, 0, NULL);
   if( hcom == INVALID_HANDLE_VALUE )
      return false;

   SetupComm(hcom, 4096, 4096);
   //DBC dcb = { 0 };

   return true;
}

void COMConnect::Close()
{
   if( hcom == INVALID_HANDLE_VALUE ) return;

   SetCommMask(hcom, 0);
   EscapeCommFunction( hcom, CLRDTR );
   PurgeComm( hcom, PURGE_TXABORT | PURGE_RXABORT | PURGE_TXCLEAR | PURGE_RXCLEAR );
   CloseHandle( hcom );

   hcom = INVALID_HANDLE_VALUE;
}

class BTWC : public CBtIf, public IConnect
{
public:
   BTWC();

   ~BTWC();

   virtual const char *Name() const { return "Bluetooth"; }
 
   virtual bool Connect(const ConnectData& data);

   virtual bool LookupPrepare();
   virtual ConnectData* LookupNext();

   virtual bool Read(BYTE *buf, DWORD *len);
   virtual bool Write(const BYTE *buf, DWORD *len);

   virtual void OnDeviceResponded (BD_ADDR bda, DEV_CLASS devClass, BD_NAME bdName, BOOL bConnected);
   virtual void OnInquiryComplete (BOOL success, short num_responses)
   { 
      stopInquiry = true;
   }

   virtual void OnDiscoveryComplete()
   {
      stopDiscovery = true;
   }

   virtual void PreparePrint();
   virtual void EndPrint();

protected:
   void Close();

   bool stopInquiry, stopDiscovery;
   DWORD startTick;
   vector<ConnectData*> finded;

   COMConnect comCon;

   struct ADDR
   {
     BD_ADDR addr;

     bool operator < (const ADDR &src) const
     {
        return (memcmp(addr, src.addr, sizeof(addr)) < 0);
     }
   };
   set<ADDR> loaded;
};
//
//--------------------------------------- BTWC ------------------------------
//

BTWC::BTWC() : stopInquiry(false), stopDiscovery(false)
{
}

BTWC::~BTWC()
{
   Close();
}

void BTWC::Close()
{
   if( comCon.connected )
   {
      comCon.RemoveConnection();
      comCon.connected = false;
   }
}

void BTWC::PreparePrint()
{
}

void BTWC::EndPrint()
{
   comCon.Close();
}

bool BTWC::Connect(const ConnectData& data)
{
   BD_ADDR connectAddr;
   memcpy(connectAddr, data.addr, sizeof(connectAddr));

   int i = 0;

   stopDiscovery = false;
   if( StartDiscovery(connectAddr, NULL) == 0 ) return false;

   while( !stopDiscovery && i++ < 600 )
      Sleep(100);
   if( !stopDiscovery ) return false;

   CSdpDiscoveryRec sdr;
   if (ReadDiscoveryRecords(connectAddr, 1, &sdr, (GUID *)(&CBtIf::guid_SERVCLASS_SERIAL_PORT)))
   {
      UINT8 scn;
      sdr.FindRFCommScn(&scn);

      if( comCon.CreateConnection(connectAddr, sdr.m_service_name) != 0 )
         return false;

      i = 0;
      while( !comCon.connected && i++ < 600 )
         Sleep(100);
      if( comCon.connected == false ) return false;

      if( comCon.Open() == false ) return false;
   } else
      return false;

   return true;
}

bool BTWC::LookupPrepare()
{
   startTick = GetTickCount();
   stopInquiry = false;
   loaded.clear();
   return (StartInquiry() != FALSE);
}

ConnectData* BTWC::LookupNext()
{
   while( true )
   {
      if( finded.size() > 0 )
      {
         vector<ConnectData*>::iterator i = finded.begin();
         ConnectData *data = (*i);
         finded.erase(i);

         return data;
      } else
      {
         if( stopInquiry )
            return NULL;
      }

      DWORD ct = GetTickCount() - startTick;
      if( ct > 60 * 1000 )
      {
         StopInquiry();
         return NULL;
      }

      Sleep(100);
   }
}

bool BTWC::Read(BYTE *buf, DWORD *len)
{
   return false;
}

bool BTWC::Write(const BYTE *buf, DWORD *len)
{
   DWORD wr = comCon.Write(buf, *len);

   *len -= wr;
   return (*len == 0);
}

void BTWC::OnDeviceResponded (BD_ADDR bda, DEV_CLASS devClass, BD_NAME bdName, BOOL bConnected)
{
   ADDR adr;
   memcpy(&adr.addr, bda, sizeof(adr.addr));

   if( stopInquiry || loaded.find(adr) != loaded.end() ) return;

   wchar_t bufName[BD_NAME_LEN+1];
   mbstowcs(bufName, (const char*)bdName, BD_NAME_LEN);

   ConnectData *cd = new ConnectData();
   cd->name = _wcsdup(bufName);

   cd->addr = (struct sockaddr *)malloc(BD_ADDR_LEN);
   memcpy(cd->addr, bda, BD_ADDR_LEN);
   cd->addrLen = BD_ADDR_LEN;

   finded.push_back(cd);

   startTick = GetTickCount();

   loaded.insert(adr);
}

IConnect* GetConnection(int index)
{
   if( index > 0 ) return NULL;

   return new BTWC();
}

BOOL WINAPI DllMain( HANDLE hInstDll, ULONG ulReason, LPVOID lpReserved )
{
   switch( ulReason )
   {
      case DLL_PROCESS_ATTACH :
         hInst = (HINSTANCE)hInstDll;
         break;
      
      case DLL_PROCESS_DETACH:
         WIDCOMMSDK_ShutDown();
         break;
         
      case DLL_THREAD_ATTACH:
         break;
         
      case DLL_THREAD_DETACH:
         break;
         
   }
   return TRUE;
}
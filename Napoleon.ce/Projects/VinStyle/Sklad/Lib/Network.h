/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Настройки системы
 * 
 *  ert   07/08/2007   creating
 */ 
#ifndef __NETWORK_H
#define __NETWORK_H

#define NETWORK_TIMEOUT 10 
#define DEF_BUF_SIZE 10240

//#ifndef CMD_LENGTH
//#define CMD_LENGTH 60
//#endif

//#define COUNT_CHARS(str) (sizeof(str)/sizeof(wchar_t) - 1)
//#define CHECK_CMD(cmd, check) !wcsncmp((cmd), (check), COUNT_CHARS(check))

struct IProgressIndicator;

class Network
{
public:
   Network(WORD timeout = NETWORK_TIMEOUT);
   virtual ~Network();

   bool Connect(const char *ip, WORD port, bool establishConnect = true, bool tcpConnect = true);
   bool ConnectByName(const char *name, WORD port, bool establishConnect = true, bool tcpConnect = true);
   void Close();
   
   //bool SendCommand(const char *cmd);
   bool Send(const BYTE *buf, DWORD len, IProgressIndicator *pf = NULL);
   bool Send(FILE *file, IProgressIndicator *pf = NULL);

   void SetTimeout(WORD newTimeout) { timeout = newTimeout; }

   //const wchar_t* ReceiveCommand();
   bool Receive(BYTE *buf, DWORD *len, IProgressIndicator *pf = NULL);
   //bool ReceiveToFile(FILE *file, DWORD *len, IProgressIndicator *pf = NULL);

   int GetLastError() const { return lastError; }

   void CopyConnection(Network *rcvr);

   DWORD AvailBytes() { DWORD cb; ioctlsocket(socket, FIONREAD, &cb); return cb; }

   bool WaitData();

   static void ReleaseConnection(DWORD cache);

protected:
   SOCKET socket;
   int lastError;
   WORD timeout;

   static HANDLE hConnection;

protected:
   bool TryEstablishConnect();
   void CloseConnection();
   bool ConnectInt(const sockaddr_in& adr, bool establishConnect, bool tcpConnect);
};

FILE* Decompress(const char *srcName, const char *destName);

#endif

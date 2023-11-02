/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Декларация общих структур и классов
 * 
 *  ert   04/08/2007   creating
 */ 
#ifndef __MAIN_H
#define __MAIN_H

#include <string>

#include <atlapp.h>
#include "Preference.h"

#ifdef Fusion
#define UPDATE_CATEGORY L"vanpda"
#else
#define UPDATE_CATEGORY L"ordpda"
#endif

#define TMP_FILE       ".\\NapoleonTmp"
#define TMP_PRICE      ".\\NapoleonTmpPrice"
#define TMP_ORGS       ".\\NapoleonTmpOrgs"
#define TMP_FORGS      ".\\NapoleonTmpFOrgs"
#define TMP_FOLDERS    ".\\NapoleonTmpFolders"
#define TMP_CONFIG     ".\\NapoleonTmpConfig"
#define TMP_DLV        ".\\NapoleonTmpDlv"
#define TMP_BALANCE    ".\\NapoleonTmpBalance"
#define TMP_PHOTO      ".\\NapoleonTmpPhoto"
#define TMP_POLL       ".\\NapoleonTmpPoll"

#define PHOTO_FOLDER   L"\\NapoleonPhoto"

#define TMP_DECOMPRESS ".\\NapoleonDecompress"

#define NPL_DOC_INDEX "NplOrgDocs.idx"

#ifdef RCV_MESSAGE
#define MESSAGE_FILE   ".\\NapoleonMessage"
#endif

struct Order;
class Network;
struct IFrame;
struct ReceiveParam;
struct ReceivePacketParam;
struct IDocument;
class DocType;
class DocumentList;
struct SendPacketParam;

struct IPreferenceChangeHandler
{
   virtual void PreferenceChanged() = 0;
};

enum NetworkError { neCommon = 1, neNoDocuments = 2 };

bool SendDocument(IDocument* document, const DocType* docType, const wchar_t* sendConfirmMsg = NULL, bool sendOrgDocs = true);
struct SendDocData
{
   IDocument* document;
   const DocType* docType;
};

class DocDataList : public std::vector<SendDocData>
{
public:
   void ClearDirty(bool reverse);
   void AddDocuments(SendPacketParam* dest);
   void RemoveDocuments();
};

bool SendDocuments(DocDataList* documents, const wchar_t* sendConfirmMsg = NULL, bool sendOrgDocs = false);

DWORD DoReceive(ReceivePacketParam *param);

void DataClose();
void DataInit(const char *dbName);

void BeforeReceviePrice(ReceivePacketParam* param);
void AfterReceviePrice(ReceivePacketParam* param);

void BeforeSendDocs(SendPacketParam* param);
void AfterSendDocs(SendPacketParam* param);
void AddToStream(SendPacketParam* param, DocumentList *dl, const DocType *type);

bool ClearPriceQty(const wchar_t* tableName);

class NapoleonApp : public CAppModule
{
public:
   struct IExportHook
   {
      virtual void Prepared(SendPacketParam* param) = 0;
   };

   NapoleonApp();
   ~NapoleonApp();

   enum ExportFlags { efDocs = 1, efGPS = 2, efVisits = 4, efOrders = 8, efBalance = 0x10 };
   enum Tracking { trkNone, trkGSM, trkGPSpoint, trkGPSroute };

   //const Preference& GetPreference();

   long SendDocument(DocDataList* documents, std::wstring *answer, IProgressIndicator *pi, bool sendOrgDocs = true, DWORD flags=efDocs, WORD dayInterval = 0, IExportHook* hook = NULL);
   
   // отправить все неотправленные заказы
   long ExportDocuments(std::wstring *answer, IProgressIndicator *pi, DWORD flags=efDocs, WORD dayInterval = 0, IExportHook* hook = NULL);

#ifdef RCV_MESSAGE
   void SetMessageDate(const FILETIME& date);
   void ShowMessage();
#endif

   long ReceivePrice(std::wstring *answer, IProgressIndicator *pi, bool clearBase, bool fullPrice);
   long ReceivePhoto(std::wstring *answer, IProgressIndicator *pi);
   long ReceiveDocs(std::wstring *answer, IProgressIndicator *pi, DWORD flags); // efBalance | efOrders

   long ReceiveRemnants(std::wstring *answer, IProgressIndicator *pi);

   void ShowErrorBox(long error, const wchar_t *msg, const wchar_t *prefix);

   void MakeFileName(std::string *fullName, const char *fileName);
   void MakeFileName(std::wstring *fullName, const wchar_t *fileName);
   void DeleteFile(const char *fileName);

   void WaitThreadComplete(HANDLE thread);

   IFrame* GetFrame() const { return frame; }

   void SetFrame(IFrame *_frame) { frame = _frame; }

   bool createOrder;

   DWORD GetStartTick() const { return startTick; }
   void SetStartTick() { startTick = GetTickCount(); }

   //
   // preference handler
   //
   void ChangePreference();
   void SetPreferenceChangeHandler(IPreferenceChangeHandler *handler) { preferenceHandler = handler; }

   void GetLocalTime(FILETIME* ft);

   // Apps
#ifdef GPS_POS
   bool GPSTracking() const;
   bool GSMTracking() const;

   void StartApps();
   void StopApps();
   void UpdateApps();
   void DoApps(const wchar_t* cmd);
   void DoCheckTime();

   HINSTANCE AppsIntance() const;

   Tracking GetTracking();
#endif

protected:
   bool preferenceLoaded;
   DWORD startTick;
   //Preference preference;

   IFrame *frame;
   IPreferenceChangeHandler *preferenceHandler;

#ifdef RCV_MESSAGE
   FILETIME msgDate;
#endif
};

extern NapoleonApp _Module;

#endif

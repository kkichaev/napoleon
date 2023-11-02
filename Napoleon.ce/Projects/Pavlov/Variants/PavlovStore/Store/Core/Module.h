/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Napoleon Logistic
*
*  ert   02/09/2010   creating
*/
#ifndef __MAIN_LOGISTIC_H
#define __MAIN_LOGISTIC_H

#include <NapoleonRes.h>

#include <string>

#include <atlapp.h>

#include <Reflection.h>
#include <TypeHolder.h>
#include <StdConsts.h>
#include <BaseFrame.h>
#include <ObjImpl.h>
#include <ServObject.h>

#define UPDATE_CATEGORY L"skaldwspda"
#define DEFAULT_BASE "SkladVV.sdb"

struct IDocument;
class DocType;

enum NetworkError { neCommon = 1, neNoDocuments = 2 };

class Application : public CAppModule
{
public:
   Application();
   ~Application();

   void MakeFileName(std::wstring *fullName, const wchar_t *fileName);

   void WaitThreadComplete(HANDLE thread);

   // DB function
   void BaseRemove();
   void DataClose();
   void DataInit(const char *dbName);

   IFrame* GetFrame() const { return frame; }
   void SetFrame(IFrame *_frame) { frame = _frame; }

   int ReceiveData(std::wstring *answer, IProgressIndicator *pi, bool clearBase);
   int ReceivePrice(const wchar_t* barcode, std::wstring* answer);

   int ExportDocuments(std::wstring *answer, IProgressIndicator *pi);
   int SendDocument(IDocument* document, const DocType* type);

   void ShowErrorBox(long ec, const std::wstring& answer, int prefix);

   bool LoadString(std::wstring* val, UINT id);

protected:
   IFrame *frame;
};

extern Application _Module;

#endif

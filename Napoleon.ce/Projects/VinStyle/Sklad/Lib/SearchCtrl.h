/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Контрол для поиска
 *
 *  ert   05/08/2008   creating
 */ 
#ifndef __SEARCH_CTRL_H
#define __SEARCH_CTRL_H

#include <atlcrack.h>

#include <atlapp.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>

class SearchControl : public CWindowImpl<SearchControl>
{
 public:
   struct ISearchEvent
   {
      virtual void SearchClear() = 0; // нажали на кнопку новый
      virtual void SearchDo(const wchar_t *text) = 0;
   };

   DECLARE_WND_CLASS(L"SRCH_CTRL")

   BEGIN_MSG_MAP(SearchControl)
      MSG_WM_PAINT(Paint)
      MSG_WM_TIMER(HandleTimer)
      MSG_WM_LBUTTONDOWN(OnClick)
      COMMAND_ID_HANDLER(textID, HandleText)
   END_MSG_MAP()

   SearchControl(UINT textID, UINT searchIco, const wchar_t *prompt = TYPE_NAME);

   void SetHandler(HWND parent, ISearchEvent *handler);

   void UpdateLayout(int top, int width, int *height, HFONT hFont, int left = 0);

   static const wchar_t TYPE_NAME[];
   static int SEARCH_DELAY;

   void NewSearch(bool fireEvent = true);

   CWindow TextWindow() const { return GetDlgItem(textID); }

 protected:
   UINT textID;
   UINT searchIco;
   UINT timerID;

   const wchar_t *searchPrompt;

   ISearchEvent *handler;
   // inSearch требуется для обработки таймера (не заходил второй раз во время поиска
   // doSearch для изменения иконки
   bool inSearch, clearingString, doSearch;

 protected:
   void HandleTimer(UINT timer, TIMERPROC);
   void HandleTimer(UINT timer) { HandleTimer(timer, NULL); }

   void OnClick(UINT flags, const CPoint &pt);
   LRESULT HandleText(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   void DoSearch(bool doing);

   void Paint(HDC);
   int SearchOffset() const;
};

#ifdef SQL_TABLES
#else

#include <SearchTable.h>
class SearchHelper
{
 public:
   SearchHelper(const SyncFormat &format, const wchar_t *field);

   void SetIterator(ITableForwardIterator *iterator) { pIterator = iterator; }
   void SetResult(std::vector<CEOID> *oResult) { pResult = oResult; }

   void CopyResult(std::vector<CEOID> *oResult) { *oResult = *pResult; }

   void Search(const wchar_t *text);

   void Clear();

 private:
   std::vector<CEOID> *pResult;
   std::vector<CEOID> result;

   ITableForwardIterator *pIterator;

   std::wstring saveText;

   CEDBFormat format;
   SearchTable  table;

   const SyncFormat &syncFormat;
};
#endif // SQL_TABLES

#endif

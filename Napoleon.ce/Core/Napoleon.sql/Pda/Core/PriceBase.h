/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Базовый класс прайс-листа
 *
 *  ert   08/09/2009   creating
 */
#ifndef __PRICE_BASE_H
#define __PRICE_BASE_H

#include "SQLFolderForm.h"
#include <Exchange.h>
#include "ObjImpl.h"

#include <SearchCtrl.h>
#include <SQLSearch.h>

// отвечает за отображение только названия

enum PriceDataFlags { pdInSerach = 1, pdInMatrix = 2 };

struct PriceBaseData : public SQLFolderFormData
{
   PriceBaseData(const ROWID& upFolder = NO_ROWID);

   virtual void InitData() { SelectFolder(saveRoot); saveRoot = NO_ROWID; }

   virtual COLORREF GetItemColor(int index) const { return textColor; }

   virtual PriceBaseData* Clone() = 0;

   // priceItem или folderItem утсановлены после работы этой функции
   virtual bool Get(IReflectableData* data, int index) const;

   bool FindItems(const wchar_t *text);
   void ClearSearch();

   // для запрещения кнопок Up&Dn&Back
   virtual bool IsTopLevel() const { return ((state & pdInSerach) != 0) ? true : (current == &root); }
   virtual bool HaveLeaf() const { return ((state & pdInSerach) != 0) ? false : (leafs.size() != 0); }

   virtual bool IsFiltred() const { return filtred; }
   virtual void SetFilter(bool filtred);

   void ClearCache()
   {
      priceItem.ClearCache();
      folderItem.ClearCache();
   }

   virtual const wchar_t *GetTitle() const { return (IsTopLevel()) ? L"Прайс-лист" : title.c_str(); }

   COLORREF textColor, selectColor, lastColor;
#ifdef SHOW_OFF_TAKE
   COLORREF rmntsColor;
#endif

protected:
   virtual void LoadTree();
   virtual void LoadFolderData(const TreeNode& folder);

   virtual void PrepareSearch(std::wstring* result, const std::wstring &whereStr);

protected:
   static bool filtred;

   mutable PriceImpl priceItem;
   mutable FolderImpl folderItem;
   ROWID saveRoot;

   std::wstring whereStr;
   std::vector<ROWID> childs;
   SQLTextSearcher searchHelper;
   DWORD state;

#if defined(MULTI_WH) || defined(FIRMS_REST)|| defined(WH_QTY)
   WORD currentWh;
#endif

};

class PriceBaseForm : public SQLFolderForm, public CCustomDraw<PriceBaseForm>, public SearchControl::ISearchEvent
{
public:
   PriceBaseForm();

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(PriceBaseForm)
      COMMAND_ID_HANDLER(IDC_SHOW_2_ROW, ChangeRows)
      COMMAND_ID_HANDLER(IDC_PRICE_FILTER, Filtring)
      COMMAND_ID_HANDLER(IDC_CLOSE, Closing)

      CHAIN_MSG_MAP(CCustomDraw<PriceBaseForm>)
      CHAIN_MSG_MAP(SQLFolderForm)
   END_MSG_MAP()

   LRESULT OnCommand(WORD msg, WPARAM, LPARAM, BOOL &bHandled);

   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/)
   {
      return CDRF_NOTIFYITEMDRAW;
   }

   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST; }

   virtual DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);
   virtual DWORD OnItemPostPaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

   virtual void LoadMenuBar();
   virtual void Refresh();

   virtual LRESULT Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled) = 0;

protected:
   bool SetDataEx(IFormData *_data, int scale);

   virtual void UpdateLayout(bool forceRecalc);
   virtual void SearchClear(); // нажали на кнопку новый
   virtual void SearchDo(const wchar_t *text); 

   virtual bool HideSIP() const { return false; }

   LRESULT ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Filtring(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

protected:
   SearchControl search;
   static WORD prevScale;
};

#endif
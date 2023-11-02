/*
 * Copyright (C), 2007, Денис Мосягин
 *
 *  Процедура открытия прайс-листа
 *
 *  ert   06/02/2008   creating
 */
#include "stdafx.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <NapoleonRes.h>
#include <PriceForm.h>
#include <FormEntries.h>
#include <NplConfig.h>

const wchar_t TYPE_CODE[] = L"Введите артикул...";

int saveIdWidth, saveWh;
struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order) : PriceFormData(_order)
   {
      Init();
   }

   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder) : PriceFormData(_order, upFolder)
   {
      Init();
   }
   
   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();

      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   virtual COLORREF GetItemColor(int index) const;
   
   bool FindItemsByCode(const wchar_t *text);  

   SQLTextSearcher searchCode;

private:
   void Init()
   {
      searchCode.SetData(priceItem.Name(), L"article");
   }
};

class PriceFormAdd;
struct CodeSearchHelper : public SearchControl::ISearchEvent
{
   CodeSearchHelper(PriceFormAdd *owner) { this->owner = owner; }

   virtual void SearchClear(); // нажали на кнопку новый
   virtual void SearchDo(const wchar_t *text); 

   PriceFormAdd *owner;
};

class PriceFormAdd : public PriceForm
{
public:
   PriceFormAdd();

   DECLARE_FORM(PriceFormAdd, IDD_PRICE_LIST_ADD)

   BEGIN_MSG_MAP(PriceFormAdd)
      COMMAND_ID_HANDLER(IDC_PRICE_NUM, SwitchCode)
      CHAIN_MSG_MAP(PriceForm)
   END_MSG_MAP()

   virtual DWORD GetMenuID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST_ADD; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST; }

   void CodeSearchDo(const wchar_t *text); 
   virtual void SearchClear(); // нажали на кнопку новый

protected:
   virtual void UpdateLayout(bool forceRecalc);
   virtual bool SetData(IFormData *_data);
   virtual void LoadMenuBar();

   LRESULT SwitchCode(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

protected:
   bool codeShowed, clearing;
   CodeSearchHelper csHelper;
   SearchControl codeSearch;
};

COLORREF PriceFormDataAdd::GetItemColor(int index) const
{   
   COLORREF c = PriceFormData::GetItemColor(index);
   int ci = index - folders.size();
   if( c == textColor && ci >= 0 )
   {
      priceItem.Read(leafs[ci]);
      return priceItem.color;
   }
   return c;
}

bool PriceFormDataAdd::FindItemsByCode(const wchar_t *text)
{
   if( *text == L'\0' ) return false;

   state |= pdInSerach;
   if( saveRoot == 0 )
      saveRoot = current->id;

   if( saveRoot != 0 && childs.size() == 0 )
   {
      current->NodeWithLeafs(&childs);
      // where folderID in (select id from folders where (rowid in (r1[,r2])))
      if( childs.size() > 0 )
      {
         FolderImpl f;
         whereStr.assign(L"folderID IN (SELECT id FROM ");
         whereStr += f.Name();
         whereStr += L" WHERE rowid IN (";

         std::vector<ROWID>::const_iterator i = childs.begin();
         while( i != childs.end() )
         {
            if( i != childs.begin() ) whereStr += L",";
            wchar_t buf[50];
            __int64 value = (*i);
            if( value < 1000000000 )
               wsprintf(buf, L"%d", value % 1000000000);
            else
               wsprintf(buf, L"%d%09d", (*i) / 1000000000, (*i) % 1000000000);

            whereStr += buf;
            i++;
         }
         whereStr += L"))";
      }
   }

   folders.clear();
   leafs.clear();

   searchCode.Search(&leafs, text, &whereStr);
   return true;
}

IMPLEMENT_FORM(PriceFormAdd)

void CodeSearchHelper::SearchClear()
{
   owner->SearchClear();
}

void CodeSearchHelper::SearchDo(const wchar_t *text)
{
   owner->CodeSearchDo(text);
}

PriceFormAdd::PriceFormAdd() : codeShowed(false), clearing(false),
   csHelper(this), codeSearch(IDC_FIND_CODE, IDC_FIND, TYPE_CODE)
{
}

void PriceFormAdd::SearchClear()
{
   if( clearing ) return;

   clearing = true;

   codeSearch.NewSearch();
   search.NewSearch();

   PriceForm::SearchClear();

   clearing = false;
}


void PriceFormAdd::CodeSearchDo(const wchar_t *text)
{
   if( ((PriceFormDataAdd*)data)->FindItemsByCode(text) )
      Refresh();
}

LRESULT PriceFormAdd::SwitchCode(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   codeShowed = !codeShowed;
   UpdateLayout(true);
   return 0;
}

bool PriceFormAdd::SetData(IFormData *_data)
{
   codeSearch.SetHandler(m_hWnd, &csHelper);

   if( PriceForm::SetData(_data) == false )
      return false;

   return true;
}

void PriceFormAdd::LoadMenuBar()
{
   PriceForm::LoadMenuBar();

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (codeShowed) ? 7 : 6;
   menuBar.SetButtonInfo(IDC_PRICE_NUM, &bi);

   if( sumLabel.m_hWnd != NULL )
      sumLabel.ShowWindow(SW_HIDE);
}

void PriceFormAdd::UpdateLayout(bool forceRecalc)
{
   /*
   ListFormData::Header* priceHeader = (ListFormData::Header*)data->GetHeader();
   if( menuBar.m_hWnd != NULL )
   {
      for( int i=0; i<sizeof(priceHeader)/sizeof(priceHeader[0]); i ++ )
         priceHeader[i].curWidth = listCtrl.GetColumnWidth(i);

      if( codeShowed )
      {
         priceHeader[1].curWidth = (saveIdWidth != 0) ? saveIdWidth : priceHeader[0].curWidth/2;
         priceHeader[1].startWidth = 100;
         priceHeader[0].curWidth -= priceHeader[1].curWidth;
         if( priceHeader[0].curWidth < 0 ) priceHeader[0].curWidth = 0;
      } else
      {
         saveIdWidth = priceHeader[1].curWidth;
         priceHeader[0].curWidth += priceHeader[1].curWidth;
         priceHeader[1].curWidth = 0;
         priceHeader[1].startWidth = 0;
      }
   } else
      priceHeader[1].startWidth = (codeShowed) ? 100 : 0;
*/
   listCtrl.SetRedraw(FALSE);
   PriceForm::UpdateLayout(forceRecalc);
   listCtrl.SetRedraw(TRUE);

   if( menuBar.m_hWnd != NULL )
   {
      TBBUTTONINFO bi = {0};
      bi.cbSize = sizeof(bi);
      bi.dwMask = TBIF_IMAGE;
      bi.iImage = (codeShowed) ? 7 : 6;
      menuBar.SetButtonInfo(IDC_PRICE_NUM, &bi);

      sumLabel.ShowWindow(SW_HIDE);
   }

   CRect bounds;

   listCtrl.GetWindowRect(bounds);
   ScreenToClient(bounds);

   int cw = bounds.Width()/2;
   search.UpdateLayout(cw, bounds.top, listCtrl.GetFont(), cw);
   codeSearch.UpdateLayout(cw, bounds.top, listCtrl.GetFont());
}

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}


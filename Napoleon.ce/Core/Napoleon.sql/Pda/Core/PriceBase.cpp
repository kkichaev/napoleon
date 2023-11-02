/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Базовый класс прайс-листа
 *
 *  ert   08/09/2009   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "PriceBase.h"

#include <Preference.h>
#include <set>

#ifdef Fusion
bool PriceBaseData::filtred = false;
#elif PRICE_FILTER0
bool PriceBaseData::filtred = true;
#else
bool PriceBaseData::filtred = false;
#endif
WORD PriceBaseForm::prevScale = 0;

PriceBaseData::PriceBaseData(const ROWID& upFolder) : saveRoot(upFolder), state(0)
{
#if defined(MULTI_WH) || defined(FIRMS_REST) || defined(WH_QTY)
   currentWh = 0;
#endif
}

bool PriceBaseData::FindItems(const wchar_t *text)
{
   if( *text == L'\0' ) return false;

   state |= pdInSerach;
   if( saveRoot == NO_ROWID )
      saveRoot = current->id;

   if( saveRoot != NO_ROWID && childs.size() == 0 )
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
               wsprintf(buf, L"%d%09d", (DWORD)((*i) / 1000000000), (DWORD)((*i) % 1000000000));

            whereStr += buf;
            i++;
         }
         whereStr += L"))";
      }
   }

   folders.clear();
   leafs.clear();

   std::wstring wstr;
   PrepareSearch(&wstr, whereStr);

   searchHelper.Search(&leafs, text, &wstr);
   return true;
}

void PriceBaseData::PrepareSearch(std::wstring* result, const std::wstring &whereStr)
{
   *result = whereStr;
}

void PriceBaseData::ClearSearch()
{
   searchHelper.Clear();
   childs.clear();
   whereStr.clear();

   state &= (~pdInSerach);

   SelectFolder(saveRoot, true);

   saveRoot = NO_ROWID;
}

struct FLoader : public FolderObj
{
   ROWID rowid;

   DECLARE_TYPE_REFLECTION(FLoader);
};

struct FID : public IReflectableData
{
   DWORD folderID;
   DECLARE_TYPE_REFLECTION(FID)
};

BEGIN_TYPE_REFLECTION(FID)
   REGISTER_ULONG_MEMBER(FID, folderID)
END_TYPE_REFLECTION(FID)

BEGIN_TYPE_REFLECTION(FLoader)
   REGISTER_INT64_MEMBER(FLoader, rowid)
   CHAIN_REFLECTION(FLoader, FolderObj)
END_TYPE_REFLECTION(FLoader)

typedef std::set<DWORD> LeafFolders;

bool Load(TreeNode *parent, FLoader &folder, SQLTable &table, const LeafFolders& leafFolders)
{
   int curLevel = folder.level;

   bool res = table.SelectNext(&folder);
   while( res && (int)folder.level > curLevel )
   {
      TreeNode *current = new TreeNode(parent);

      current->id = folder.rowid;

      LeafFolders::const_iterator fnd = leafFolders.find(folder.id);
      current->haveLeafs = (fnd != leafFolders.end());

      res = Load(current, folder, table, leafFolders);

      if( current->HaveChild() )
         parent->childs.push_back(current);
      else
         delete current;
   }

   return res;
}

static void LoadLeafFolders(LeafFolders* lf)
{
   FID fid;

   SQLTable fidTable(PriceImpl().Name());
   bool bdo = fidTable.Select(&fid, L"", true); //select distinct folderID from price
   while( bdo )
   {
      lf->insert(fid.folderID);
      bdo = fidTable.SelectNext(&fid);
   }
}

void PriceBaseData::LoadTree()
{
   FLoader f;
   SQLTable t(folderItem.Name());

   if( !t.Select(&f, L"ORDER BY id") ) return;

   LeafFolders leafFolders;
   LoadLeafFolders(&leafFolders);

   bool res = true;
   while( res )
   {
      TreeNode *current = new TreeNode(&root);
      current->id = f.rowid;

      LeafFolders::const_iterator fnd = leafFolders.find(f.id);
      current->haveLeafs = (fnd != leafFolders.end());

      res = Load(current, f, t, leafFolders);

      if( current->HaveChild() )
         root.childs.push_back(current);
      else
         delete current;
   }
}

void PriceBaseData::LoadFolderData(const TreeNode& folder)
{
   if( folder.id == NO_ROWID || !folderItem.Read(folder.id) ) return;

   title = folderItem.name;

   wchar_t buf[400];
   wsprintf(buf, L"WHERE folderID=%d", folderItem.id);

   if( filtred )
   {
#ifdef Agama
      wsprintf(buf+wcslen(buf), L" AND collectionValue(qty, 'QtyItem', %d, 'qty') >= qtyInPack", currentWh);
#elif MULTI_WH
      wsprintf(buf+wcslen(buf), L" AND collectionValue(qty, 'QtyItem', %d, 'qty') <> 0", currentWh);
#elif FIRMS_REST
      if( currentWh == 0 ) wcscat(buf, L" AND qty <> 0");
      else wsprintf(buf+wcslen(buf), L" AND collectionValue(firmQty, 'QtyItem', %d, 'qty') <> 0", currentWh-1);
#elif Volnenko
      wcscat(buf, L" AND qty > 1000");
#else
      wcscat(buf, L" AND qty <> 0");
#endif
   }
#ifdef Agama
   else
   {
      // убираем все delisted && qty == 0
      wsprintf(buf+wcslen(buf), L" AND (collectionValue(qty, 'QtyItem', %d, 'qty') > 0 OR (flags & 1) = 0)", currentWh);
   }
#endif

   Preference p;
   p.Load();
   wcscat(buf, L" ORDER BY name COLLATE " );
   wcscat(buf, (p.flags & apfSortNoCase) ? L"RUSS_NOCASE" : L"RUSS" );
   SQLTable table(priceItem.Name());
   table.RIDList(&leafs, buf);
}

void PriceBaseData::SetFilter(bool filtred)
{
   if( this->filtred != filtred )
   {
      this->filtred = filtred;
      SelectFolder(current->id, true);
   }
}

bool PriceBaseData::Get(IReflectableData* data, int index) const
{
   if( index >= Count() ) return false;

   int fsize = folders.size();
   if( index < fsize )
   {
      folderItem.Read(folders[index]);
      ((FolderFormItem*)data)->name = folderItem.name;
   } else
   {
      priceItem.Read(leafs[index-fsize], false);
      ((FolderFormItem*)data)->name = priceItem.name;
   }
   return true;
}

PriceBaseForm::PriceBaseForm() : search(IDC_FIND, IDC_FIND)
{
}

bool PriceBaseForm::SetData(IFormData *_data)
{
   Preference p;
   p.Load();
   return SetDataEx(_data, p.priceScale + 1);
}

bool PriceBaseForm::SetDataEx(IFormData *_data, int scale)
{
   if( SQLFolderForm::SetDataEx(_data, scale) == false )
      return false;

   search.SetHandler(m_hWnd, this);

   ((PriceBaseData*)data)->textColor = listCtrl.GetTextColor();
   ((PriceBaseData*)data)->selectColor = RGB(0, 255, 0);
   ((PriceBaseData*)data)->lastColor = RGB(255, 0, 0);
#ifdef SHOW_OFF_TAKE
   ((PriceBaseData*)data)->rmntsColor = RGB(0, 0, 255);
#endif

   LoadMenuBar();

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (scale == 1) ? 12 : (scale == 2) ? 28 : 13;
   menuBar.SetButtonInfo(IDC_SHOW_2_ROW, &bi);

   return true;
}

LRESULT PriceBaseForm::ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Preference p;
   p.Load();

#ifdef Provisia
   if( p.priceScale > 0 )
      p.priceScale = 0;
   else
      p.priceScale = p.priceLines - 1;
#else
   p.priceScale++;
   if( p.priceScale >= MAX_SCALE_ROW )
      p.priceScale = 0;
#endif

   p.Save();

   PriceBaseData *pfd = ((PriceBaseData*)data)->Clone(); //new PriceFormData(order, upFolder);
   _Module.GetFrame()->Load(GetID(), pfd);
   return 0;
}

void PriceBaseForm::UpdateLayout(bool forceRecalc)
{
   CRect rc, bounds;

   CHeaderCtrl header(listCtrl.GetHeader());

   header.GetWindowRect(bounds);
   GetParent().GetClientRect(rc);

   int itemHeight = bounds.Height();

   search.UpdateLayout(rc.Width(), itemHeight, listCtrl.GetFont());
   SetListLayout(forceRecalc, itemHeight);
   sumLabel.UpdateLayout();
}

void PriceBaseForm::SearchClear()
{
   ((PriceBaseData*)data)->ClearSearch();
   Refresh();
}

void PriceBaseForm::SearchDo(const wchar_t *text)
{
   if( ((PriceBaseData*)data)->FindItems(text) )
      Refresh();
}

DWORD PriceBaseForm::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)   
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   DWORD item = lvcd->nmcd.dwItemSpec;
   lvcd->clrText = ((PriceBaseData*)data)->GetItemColor(item);

   return CDRF_NOTIFYITEMDRAW;
}

DWORD PriceBaseForm::OnItemPostPaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
{
   return CDRF_NOTIFYITEMDRAW;
}

LRESULT PriceBaseForm::Filtring(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   ((PriceBaseData*)data)->SetFilter(!((PriceBaseData*)data)->IsFiltred());

   search.NewSearch(false);

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (((PriceBaseData*)data)->IsFiltred()) ? 5 : 4;
   menuBar.SetButtonInfo(IDC_PRICE_FILTER, &bi);

   Refresh();
   return 0;
}

void PriceBaseForm::LoadMenuBar()
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, ((HideSIP()) ? SHCMBF_HIDESIPBUTTON : 0)));
   //menuBar.EnableButton(IDC_BACK, enableUp);

   BOOL enable = (((PriceBaseData*)data)->HaveLeaf()) ? TRUE : FALSE;
   menuBar.EnableButton(IDC_NEXT, enable);
   menuBar.EnableButton(IDC_PREV, enable);

#ifdef PRICE_FILTER0
   TBBUTTON button;
   button.iBitmap = (((PriceBaseData*)data)->IsFiltred()) ? 5 : 4;;
   button.idCommand = IDC_PRICE_FILTER;
   button.fsState = TBSTATE_ENABLED;
   button.fsStyle = TBSTYLE_BUTTON | TBSTYLE_AUTOSIZE;
   button.dwData = 0;
   button.iString = IDC_PRICE_FILTER;

   menuBar.AddButtons(1, &button);
#else
   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (((PriceBaseData*)data)->IsFiltred()) ? 5 : 4;
   menuBar.SetButtonInfo(IDC_PRICE_FILTER, &bi);
#endif

   listCtrl.SetFocus();
}

void PriceBaseForm::Refresh()
{
   ((PriceBaseData*)data)->ClearCache();

   SQLFolderForm::Refresh();
}
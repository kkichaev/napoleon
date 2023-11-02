/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Формы списка
 *
 *  ert   13/08/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <ListForm.h>
#include <StdFuncs.h>

BEGIN_TYPE_REFLECTION(FolderFormItem)
   REGISTER_STRING_MEMBER(FolderFormItem, name)
END_TYPE_REFLECTION(FolderFormItem)

ListViewMultiLine::ListViewMultiLine() : owner(NULL), endTrack(false)
{
}

/*
LRESULT ListViewMultiLine::EndTrack(LPNMHDR hdr)
{
   SetMsgHandled(FALSE);
   InvalidateRect(NULL, TRUE);
   UpdateWindow();
   //RedrawItems(GetTopIndex(), GetLastVisibleItem());
   return 0;
}
*/

LRESULT ListViewMultiLine::OnNotify(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
   bHandled = FALSE;

   NMHDR *hdr = (NMHDR*)lParam;

   if( hdr->code == HDN_ITEMCHANGED && endTrack )
   {
      endTrack = false;
      InvalidateRect(NULL, TRUE);
   } else
   {
      if( hdr->code == HDN_ENDTRACK )
         endTrack = true;
   }

   return 0;
}

int ListViewMultiLine::ItemNotify(DRAWITEMSTRUCT *ds, NMLVCUSTOMDRAW *cd, const CRect &textBounds, DWORD stage, int subItem)
{
   cd->iSubItem = subItem;
   cd->dwItemType = LVCDI_ITEM;
   cd->rcText = textBounds;
   cd->uAlign = 0;

   cd->nmcd.dwDrawStage = stage;
   cd->nmcd.dwItemSpec = ds->itemID;
   cd->nmcd.hdc = ds->hDC;
   cd->nmcd.lItemlParam = 0;
   cd->nmcd.rc = ds->rcItem;
   cd->nmcd.uItemState = ds->itemState;
   cd->nmcd.hdr.hwndFrom = m_hWnd;
   cd->nmcd.hdr.code = NM_CUSTOMDRAW;
   cd->nmcd.hdr.idFrom = ds->itemID;

   if( ds->itemState & ODS_SELECTED )
   {
      cd->clrText = GetSysColor(COLOR_HIGHLIGHTTEXT);
      cd->clrTextBk = GetSysColor(COLOR_HIGHLIGHT);
   } else
   {
      cd->clrText = GetSysColor(COLOR_WINDOWTEXT);
      cd->clrTextBk = GetSysColor(COLOR_WINDOW);
   }
   return GetParent().SendMessage(WM_NOTIFY, GetDlgCtrlID(), (LPARAM)cd);
}

void ListViewMultiLine::DrawBack(DRAWITEMSTRUCT *ds, NMLVCUSTOMDRAW *cd)
{
   LOGBRUSH brsh;
   brsh.lbColor = cd->clrTextBk;
   brsh.lbHatch = 0;
   brsh.lbStyle = BS_SOLID;

   HBRUSH bkBrsh;
   bkBrsh = CreateBrushIndirect(&brsh);

   CRect clientBounds;
   GetClientRect(clientBounds);
   clientBounds.top = ds->rcItem.top;
   clientBounds.bottom = ds->rcItem.bottom;

   FillRect(ds->hDC, clientBounds, bkBrsh);
   DeleteObject(bkBrsh);
}

void ListViewMultiLine::DrawImage(DRAWITEMSTRUCT *ds, RECT *textBounds)
{
   //CImageList iList(GetImageList(LVSIL_SMALL));
   //if( iList.IsNull() )
   //   return;
   if( images.IsNull() )
      return;

   LVITEM item;
   item.iItem = ds->itemID;
   item.iSubItem = 0;
   item.mask = LVIF_IMAGE;
   GetItem(&item);

   if( item.iImage >= 0 )
   {
      //iList.Draw(ds->hDC, item.iImage, textBounds->left, textBounds->top, 
      //   (ds->itemState & ODS_SELECTED) ? ILD_SELECTED : ILD_NORMAL);
      images.Draw(ds->hDC, item.iImage, textBounds->left, textBounds->top, 
         (ds->itemState & ODS_SELECTED) ? ILD_SELECTED : ILD_NORMAL);

      int cx, cy;
      images.GetIconSize(cx, cy);
      textBounds->left += cx + 1;
   }

   //CRect bounds;
   //GetItemRect(ds->itemID, bounds, LVIR_ICON);
   //textBounds->left += bounds.Width()+1;
}

//short pressY;
//LRESULT ListViewMultiLine::OnMousePress(UINT uMsg, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
//{
//   if( uMsg == WM_LBUTTONDOWN )
//   {
//      pressY = (short)HIWORD(lParam);
//      SetCapture();
//   }
//   else
//      ReleaseCapture();
//   return 0;
//}
//
//LRESULT ListViewMultiLine::OnMouseMove(UINT, WPARAM fwKeys, LPARAM lParam, BOOL &bHandled)
//{
//   if( (fwKeys & MK_LBUTTON) == 0 )
//   {
//      bHandled = FALSE;
//      return 0;
//   }
//
//   CRect rc;
//   short yPos = (short)HIWORD(lParam);
//
//   yPos -= pressY;
//
//   GetClientRect(rc);
//
//   int cp = GetLastVisibleItem(), items = GetItemCount();
//   int newPos = yPos * items / rc.Height();
//   cp += newPos;
//   if( cp >= items ) cp = items-1;
//
//   EnsureVisible(cp, FALSE);
//   return 0;
//}

int ListViewMultiLine::GetLastVisibleItem() const
{
   CPoint pt;
   int itemOnList = 1;
   int topIndex = GetTopIndex();
   if( GetItemPosition(topIndex+1, &pt) )
   {
      CRect bounds;
      int itemHeight = pt.y;

      GetItemPosition(topIndex, &pt);
      itemHeight -= pt.y;
      GetClientRect(bounds);
      
      itemOnList = (bounds.Height() - pt.y) / itemHeight;
   }
   return topIndex + itemOnList - 1;
}

static int GetDividerPos(HDC dc, const wchar_t *text, int len, int extent)
{
   SIZE size;
   INT fit, *dx = (INT*)alloca(sizeof(INT) * len);
   const wchar_t dvdr[] = L" .,/-+%*";
   const wchar_t dvdrBfore[] = L"\"\'<№#";

   GetTextExtentExPoint(dc, text, len, extent, &fit, dx, &size);

   if( fit >= len ) return -1;

   // свободно не больше 1/5 пространства
   int gap = extent - extent / 4, i = fit-1;
   while( dx[i] > gap && i >= 0 )
   {
      wchar_t sym = text[i];
      if( wcschr(dvdr, sym) != NULL )
         break;
      if( wcschr(dvdrBfore, sym) != NULL )
      {
         i--;
         break;
      }

      i--;
   }

   if( i < 0 ) return -1;
   if( dx[i] <= gap ) return i+1;
   return i;
}

//
// гарантируем, что в этой функции нет \n
//
static void DivideLine(HDC dc, std::wstring *text, int extent)
{
   int pos = GetDividerPos(dc, text->c_str(), text->size(), extent);
   if( pos < 0 ) return;

   std::wstring tstr;
   do
   {
      int dvd = pos;
      const wchar_t *pText = text->c_str();
      if( pText[pos] == L' ' ) dvd--; // пробел убираем

      tstr.append(pText, dvd + 1);
      tstr.append(L"\n");

      *text = text->substr(pos+1);
      pos = GetDividerPos(dc, text->c_str(), text->size(), extent);
   } while( pos >= 0 );

   tstr.append(*text);
   *text = tstr;
}

static void DrawCellText(HDC dc, const wchar_t *pText, RECT *bounds, DWORD flags)
{
   int extent = bounds->right - bounds->left;

   std::wstring out;
   std::wstring lines(pText);
   std::wstring::size_type newLine = lines.find(L'\n'), sp = 0;

   do
   {
      std::wstring line = lines.substr(sp, newLine);
      DivideLine(dc, &line, extent);

      out += line;

      if( newLine == std::wstring::npos )
         break;

      out += L'\n';
      sp = newLine + 1;
      newLine = lines.find(sp, L'\n');
   } while( true );

   DrawText(dc, out.c_str(), out.size(), bounds, flags);
}

LRESULT ListViewMultiLine::DrawItem(UINT, WPARAM, LPARAM lParam, BOOL &)
{
   LPDRAWITEMSTRUCT ds = (LPDRAWITEMSTRUCT)lParam;
   NMLVCUSTOMDRAW cd;

   CRect textBounds(ds->rcItem);
   textBounds.InflateRect(-1, -1);

   SelectObject(ds->hDC, GetFont());

   LVCOLUMN column = {0};
   int columns = GetHeader().GetItemCount();
   column.mask = LVCF_FMT;
   bool drawStarted = false;

   for( int i=0; i<columns; i++ )
   {
      int addedColumns = 0;

      int cw = GetColumnWidth(i);
      if( cw <= 0 ) continue;

      if( owner != NULL )
      {
         while( i + 1 + addedColumns < columns && 
            (owner->CanSetColumn(ds->itemID, i + 1 + addedColumns) == false || GetColumnWidth(i) == 0) )
         {
            addedColumns++;
            cw += GetColumnWidth(i+addedColumns);
         }
      }

      textBounds.right = textBounds.left + cw - 1;
      GetColumn(i, &column);

      int res = ItemNotify(ds, &cd, textBounds, CDDS_ITEMPREPAINT, i);
      ::SetTextColor(ds->hDC, cd.clrText);

      if( drawStarted == false )
      {
         DrawBack(ds, &cd);
         if( ds->itemState & ODS_FOCUS )
            DrawFocusRect(ds->hDC, &ds->rcItem);

         drawStarted = true;
     }

      if( res != CDRF_SKIPDEFAULT )
      {
         if( i == 0 )
            DrawImage(ds, &cd.rcText);

         DWORD flags = DT_WORDBREAK;
         wchar_t buf[500];
         GetItemText(ds->itemID, i, buf, sizeof(buf)/sizeof(buf[0]));

         if( (column.fmt & LVCFMT_RIGHT)  ) flags |= DT_RIGHT;
         else if( (column.fmt & LVCFMT_CENTER) )flags |= DT_CENTER;

         DrawCellText(ds->hDC, buf, &cd.rcText, flags);
         //DrawCellText(ds->hDC, buf, textBounds, flags);
      }

      ItemNotify(ds, &cd, textBounds, CDDS_ITEMPOSTPAINT, i);

      textBounds.left = textBounds.right + 1;
      i += addedColumns;
   }

   CRect clientBounds;
   HPEN cpen = ::CreatePen(PS_SOLID,0,RGB(192,192,192));
   HGDIOBJ svpen = SelectObject(ds->hDC, cpen);
   GetClientRect(clientBounds);

   MoveToEx(ds->hDC, clientBounds.left, ds->rcItem.bottom-1, NULL);
   LineTo(ds->hDC, clientBounds.right, ds->rcItem.bottom-1);
   //MoveToEx(ds->hDC, ds->rcItem.left, ds->rcItem.bottom-1, NULL);
   //LineTo(ds->hDC, ds->rcItem.right, ds->rcItem.bottom-1);

   SelectObject(ds->hDC, svpen);
   DeleteObject(cpen);
   return 0;
}

void ListViewMultiLine::SetLayout(bool forceRecalc, const CRect& bounds, ListFormData *listData)
{
   CRect rc;
   GetParent().GetClientRect(rc);
   SetWindowPos(NULL, bounds.left, bounds.top, rc.Width(), bounds.Height(), SWP_NOZORDER|SWP_NOOWNERZORDER);

   int columnsWidth = bounds.right - GetSystemMetrics(SM_CXVSCROLL) - 2;

   const ListFormData::Header *header = listData->GetHeader();
   if( header[0].curWidth == 0 || forceRecalc )
   {
      int width = 0, i=0;
      int totalWidth = 0;
      for( i; i<listData->ColumnsCount(); i++ )
         width += header[i].startWidth;

      for( i=0; i<listData->ColumnsCount(); i++ )
      {
         int cw = (i != listData->ColumnsCount()-1) ? 
            columnsWidth * header[i].startWidth / width : 
            columnsWidth - totalWidth;

         SetColumnWidth(i, cw);
         totalWidth += cw;
      }
   } else
   {
      for( int i=0; i<listData->ColumnsCount(); i++ )
         SetColumnWidth(i, header[i].curWidth);
   }

   SetRedraw(true);
}


void ListViewMultiLine::LoadImageList(int resID)
{
   int cx = GetSystemMetrics(SM_CXSMICON);
   HBITMAP bmp;

   bmp = LoadBitmap(_Module.GetResourceInstance(), MAKEINTRESOURCE(resID));
   if( bmp ) 
   {
      //CImageList il;
      //il.Create(cx, cx, ILC_COLORDDB, 1, 0);
      //il.Add(bmp);

      //SetImageList(il, LVSIL_SMALL);
      if((GetStyle() & LVS_OWNERDRAWFIXED) != 0 )
      {
         images.Create(cx, cx, ILC_COLORDDB, 1, 0);
         images.Add(bmp);
      } else
      {
         CImageList il;
         il.Create(cx, cx, ILC_COLORDDB, 1, 0);
         il.Add(bmp);
         SetImageList(il, LVSIL_SMALL);
      }
      DeleteObject((HGDIOBJ)bmp);
   }
}

void ListViewMultiLine::Setup(int scale, ListFormData *listData, int imageList)
{
   SetSystemFont(m_hWnd);
   bool setFont = ((GetStyle() & LVS_OWNERDRAWFIXED) != 0 && scale > 1);
   HFONT listFont, newFont = NULL;
   if( setFont )
   {
      LOGFONT lf;
      listFont = GetFont();
      GetObject(listFont, sizeof(lf), &lf);
      lf.lfHeight *= scale;

      newFont = CreateFontIndirect(&lf);
      SetFont(newFont);
   }

   // переключить в LVS_REPORT|LVS_SINGLESEL и установить число столбцов
   SetExtendedListViewStyle(LVS_EX_FULLROWSELECT);
   ModifyStyle(0, LVS_REPORT|LVS_SHOWSELALWAYS|LVS_SINGLESEL|WS_VSCROLL);
   SetRedraw(false);

   int ctr = 0;
   int nColumns = GetHeader().GetItemCount();
   while( nColumns-- > 0 )
      DeleteColumn(0);

   const ListFormData::Header *header = listData->GetHeader();
   while( ctr < listData->ColumnsCount() )
   {
      ListFormData::Header::Justify justify = header[ctr].justify;
      DWORD fmt = (justify == ListFormData::Header::Left) ? LVCFMT_LEFT : 
         (justify == ListFormData::Header::Right) ? LVCFMT_RIGHT : LVCFMT_CENTER;

      InsertColumn(ctr, header[ctr].title, fmt, header[ctr].startWidth);
      
      ctr++;
   }
   // установить число элементов
   SetItemCount(listData->Count());

   LoadImageList(imageList);

   if( setFont )
      SetFont(listFont);
   if( newFont != NULL )
      DeleteObject(newFont);
}

ListFormData::ListFormData() : owner(NULL)
{
}

ListFormData::~ListFormData()
{
}

ListForm::ListForm() : data(NULL), element(NULL), curIndex(-1)
   //, reverse(false), sortedColumn(-1)
{
}

ListForm::ListForm(int sumScale) : data(NULL), element(NULL), curIndex(-1), sumLabel(sumScale)
   //, reverse(false), sortedColumn(-1)
{
}

ListForm::~ListForm()
{ 
   delete data;
   delete element; 

   FreeSystemFont();
}

LRESULT ListForm::ShowContextMenu(HWND hWnd, const CPoint &org)
{
   int i = listCtrl.GetSelectedIndex();

   if( i < 0 ) return 0;

   HMENU hm = LoadMenu(_Module.GetResourceInstance(), MAKEINTRESOURCE(GetMenuID()));
   if( hm )
   {
      TrackPopupMenuEx(GetSubMenu(hm, 0),  TPM_TOPALIGN, org.x, org.y, m_hWnd, NULL);   
      DestroyMenu(hm);
   }
   return 0;
}

int ListForm::GetLastVisibleItem() const
{
   return listCtrl.GetLastVisibleItem();
   /*
   CPoint pt;
   int itemOnList = 1;
   int topIndex = listCtrl.GetTopIndex();
   if( listCtrl.GetItemPosition(topIndex+1, &pt) )
   {
      CRect bounds;
      int itemHeight = pt.y;

      listCtrl.GetItemPosition(topIndex, &pt);
      itemHeight -= pt.y;
      listCtrl.GetClientRect(bounds);
      
      itemOnList = (bounds.Height() - pt.y) / itemHeight;
   }
   return topIndex + itemOnList - 1;
   */
}

void ListForm::SetupListCtrl(ListViewMultiLine *list, int scale, ListFormData *listData)
{
   list->Setup(scale, listData, ImageListID(list));
}

bool ListForm::SetDataEx(IFormData *_data, int scale)
{ 
   data = (ListFormData*)_data;
   if( !data ) return false;
   
   listCtrl.Set(this);

   data->owner = this;

   const DataReflector& reflector = data->DataType();
   element  = reflector.Create();

   // установим связи с компонентами и настроим их для работы
   HWND hList = ::GetDlgItem(m_hWnd, IDC_TABLE);   
   ATLASSERT(hList != NULL);

   listCtrl.SubclassWindow(hList);
   SetupListCtrl(&listCtrl, scale, data);

   ::SetFocus(hList);
   return true;
}

LRESULT ListForm::SetHeaderWidth(LPNMHDR hdr)
{
   NMHEADER *nmhdr = (NMHEADER*)hdr;
   data->SetWidth(nmhdr->iItem, listCtrl.GetColumnWidth(nmhdr->iItem));
   return 0;
}

void ListForm::SetListLayout(bool forceRecalc, int listTop, int height, ListViewMultiLine *list, ListFormData *listData)
{
   if( list == NULL ) list = &listCtrl;
   if( listData == NULL ) listData = (ListFormData*)data;

   CRect rc;
   GetParent().GetClientRect(rc);
   SetWindowPos(NULL, 0, 0, rc.right, rc.Height(), SWP_NOZORDER|SWP_NOOWNERZORDER);
   
   if( height == 0 ) height = rc.Height() - listTop;

   rc.left = 0;
   rc.top = listTop;
   rc.bottom = height + listTop;

   list->SetLayout(forceRecalc, rc, listData);
}

void ListForm::Destroy()
{
   if( listCtrl.IsWindow() == TRUE )
   {
      for( int i=0; i<data->ColumnsCount(); i++ )
         data->SetWidth(i, listCtrl.GetColumnWidth(i));
   }

   BaseForm::Destroy();
}

void ListForm::UpdateLayout(bool forceRecalc)
{
   SetListLayout(forceRecalc);
   sumLabel.UpdateLayout();
}

void ListForm::LoadMenuBar(bool hideSIP)
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, (hideSIP) ? SHCMBF_HIDESIPBUTTON : 0));
}

LRESULT ListForm::HandleCommands(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   int selected = listCtrl.GetSelectedIndex();
   switch( id )
   {
   case IDC_ADD:
      if( ((ListFormData*)data)->Adding() )
         Refresh();
      break;
   case IDC_EDIT:
      if( selected >= 0 && ((ListFormData*)data)->Editing(selected) )
         Refresh();
      break;
   case IDC_DEL:
      if( selected >= 0 && ((ListFormData*)data)->Removing(selected) )
         Refresh();
      break;
   }
   return 0;
}

void ListForm::Refresh()
{
   curIndex = -1;
   int count = ((ListFormData*)data)->Count();
   listCtrl.SetItemCount(count);
   if( count )
      listCtrl.RedrawItems(listCtrl.GetTopIndex(), count);
}

LRESULT ListForm::OnKeyDown(LPNMHDR hdr)
{
   NMLVKEYDOWN *kd = (NMLVKEYDOWN*)hdr;
   if( kd->wVKey == VK_RETURN )
   {
      int index = listCtrl.GetSelectedIndex();
      if( index >= 0 && ((ListFormData*)data)->Selecting(index) )
         Refresh();
      return TRUE;
   }
   return FALSE;
}

LRESULT ListForm::SetCellInfo(LPNMHDR hdr)
{
   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( !(di->item.mask & LVIF_TEXT) || data == NULL )
      return TRUE;

   const DataReflector& reflector = data->DataType();
   if( curIndex != di->item.iItem )
   {
      curIndex = di->item.iItem;
      if( !data->Get(element, curIndex) )
         curIndex = -1;
   }
   if( curIndex >= 0 && CanSetColumn(curIndex, di->item.iSubItem))
   {
      const MemberType &tp = reflector.Type(data->GetHeader()[di->item.iSubItem].field);
      tp.ToString(*element, di->item.pszText, di->item.cchTextMax);
   } else
      *di->item.pszText = L'\0';

   if( di->item.iSubItem == 0 )
   {
      di->item.mask |= LVIF_PARAM;
      di->item.lParam = curIndex;
   }

   return TRUE;
}

LRESULT ListForm::ItemSelected(LPNMHDR hdr)
{
   if( hdr->hwndFrom == listCtrl.m_hWnd )
   {
      int index = ((NMLISTVIEW*)hdr)->iItem;
      if( index >= 0 && ((ListFormData*)data)->Selecting(index) )
         Refresh();
   }
   return TRUE;
}

/*
int ListForm::Compare(int item1, int item2)
{
   if( sortedColumn < 0 ) return item1 - item2;

   const MemberType &mt = data->DataType().Type(sortedColumn);
   IReflectableData *element2 = data->DataType().Create();

   data->Get(element, item1);
   data->Get(element2, item2);

   LONG cmp = 0;
   void *val1 = mt.GetValue(*element), *val2 = mt.GetValue(*element2);

   switch( mt.type )
   {
   case MemberType::DateTime:
      cmp = CompareFileTime((FILETIME*)val1, (FILETIME*)val2);
      break;
   case MemberType::Short:
      cmp = *(short*)val1 - *(short*)val2;
      break;
   case MemberType::UShort:
      cmp = (*(unsigned short*)val1 > *(unsigned short*)val2) ? 1 : 
         (*(unsigned short*)val1 < *(unsigned short*)val2) ? -1 : 0;
      break;
   case MemberType::Integer:
      cmp = *(int*)val1 - *(int*)val2;
      break;
   case MemberType::Unsigned:
      cmp = (*(unsigned*)val1 > *(unsigned*)val2) ? 1 : 
         (*(unsigned*)val1 < *(unsigned*)val2) ? -1 : 0;
   case MemberType::Long:
      cmp = *(long*)val1 - *(long*)val2;
      break;
   case MemberType::ULong:
      cmp = (*(unsigned long*)val1 > *(unsigned long*)val2 ) ? 1 : 
         (*(unsigned long*)val1 < *(unsigned long*)val2) ? -1 : 0;
      break;
   case MemberType::String:
      cmp = wcscmp((const wchar_t*)val1, (const wchar_t*)val2);
      break;
   default:
      cmp = item1 - item2;
      break;
   }

   delete element2;
   return (reverse) ? ((cmp > 0) ? -1 : (cmp < 0) ?  1 : 0) : cmp;
}

int CALLBACK ListForm::CompareItems(LPARAM lParam1, LPARAM lParam2, LPARAM lParamSort)
{
   return ((ListForm*)lParamSort)->Compare(lParam1, lParam2);
}
*/

LRESULT ListForm::Sorting(LPNMHDR hdr)
{
   /*
   if( sortedColumn == ((LPNMLISTVIEW)hdr)->iSubItem )
      reverse = true;
   else
      sortedColumn = ((LPNMLISTVIEW)hdr)->iSubItem;

   listCtrl.SortItems(CompareItems, (LPARAM)this);
   */
   return TRUE;
}

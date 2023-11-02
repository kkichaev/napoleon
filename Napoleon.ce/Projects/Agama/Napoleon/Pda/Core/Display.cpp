/*
 * Copyright (C), 2006-2011, Денис Мосягин
 *
 * Выкладка товара
 *
 *  ert   06/06/2011   update
 */

#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>
#include <MainFrame.h>

#include <StdFuncs.h>
#include <ListForm.h>
#include <InitDoc.h>
#include <BaseDialog.h>
#include "PhotoFolder.h"
#include "PrfDlg.h"
#include <PicWindow.h>
#include <NplConfig.h>
#include "FileType.h"

#include "Add.h"
#include <BaseDialog.h>
#include <PicWindow.h>

class DetailDlg : public BaseDialog
{
public:
   DetailDlg(DisplayImpl *_doc) : BaseDialog(IDD_DISPLAY_DETAIL) { doc = _doc; }

   BEGIN_MSG_MAP(DetailDlg)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      CHAIN_MSG_MAP(BaseDialog)
      REFLECT_NOTIFICATIONS()
   END_MSG_MAP()


protected:
   DisplayImpl* doc;

   UnitList units;

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
};

struct DisplayData : public IFormData
{
   DisplayData(DisplayImpl *_doc, bool rdl) : retToDocList(rdl), doc(_doc) {}
   ~DisplayData() { delete doc; }

   DisplayImpl *doc;
   bool retToDocList;
};

class TreeViewEx : public CWindowImpl<TreeViewEx, CTreeViewCtrl>
{
public:
   TreeViewEx() : handler(NULL) {}

   struct Handler
   {
      virtual void Selected(HTREEITEM item) = 0;
   };

   BEGIN_MSG_MAP(TreeViewEx)
      MESSAGE_HANDLER(WM_LBUTTONDOWN, OnClick)
   END_MSG_MAP()

   LRESULT OnClick(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);

   void SetHandler(Handler* h) { handler = h; }
   void ClearHandler() { handler = NULL; }

protected:
   Handler* handler;
};

class DisplayForm : public BaseForm, public TreeViewEx::Handler
{
public:
   DisplayForm() : data(NULL), picWindow(NULL), selected(NULL) {}
   ~DisplayForm();

   virtual DWORD GetResourceID() const { return IDD_DISPLAY; }
   virtual DWORD GetMenuBarID() const { return IDD_DISPLAY; }

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   DECLARE_FORM(DisplayForm, IDD_DISPLAY)

   BEGIN_MSG_MAP(DisplayForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_PHOTO, MakePhoto)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      COMMAND_ID_HANDLER(IDC_EDIT, ShowPicture)
      COMMAND_ID_HANDLER(IDC_DEL, RemovePicture)
      COMMAND_ID_HANDLER(IDC_DETAIL, EditDetail)
      NOTIFY_CODE_HANDLER_EX(NM_CLICK, OnClick)
      MESSAGE_HANDLER(WM_COMMAND, OnCommand)
      MSG_WM_CONTEXTMENU(ShowContextMenu)
   END_MSG_MAP()

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT EditDetail(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT MakePhoto(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ShowPicture(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT RemovePicture(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ShowContextMenu(HWND hWnd, const CPoint &org);
   LRESULT OnClick(LPNMHDR hdr);

   LRESULT OnCommand(WORD msg, WPARAM, LPARAM, BOOL &bHandled)
   {
      if( picWindow )
      {
         picWindow->Cancel();
         delete picWindow;
         picWindow = NULL;
      } else
         bHandled = FALSE;
      return 0;
   }

protected:
   void RefreshPhoto();
   void AddPhoto(const DisplayItem& item);
   void RemovePhoto(int index);

   virtual void Selected(HTREEITEM item);

   bool CheckData();

protected:
   DisplayData *data;
   CMenuBarCtrl menuBar;
   CImageList images;
   CListViewCtrl photos;
   TreeViewEx folders;

   PicWindow *picWindow;

   DisplayItem* selected;
   int selectedIndex;
};

IMPLEMENT_FORM(DisplayForm)

BEGIN_TYPE_REFLECTION(DisplayItem)
   REGISTER_FILE_MEMBER(DisplayItem, id)
   REGISTER_STRING_MEMBER(DisplayItem, folder)
END_TYPE_REFLECTION(DisplayItem)

BEGIN_TYPE_REFLECTION(Display)
   REGISTER_TIMESTAMP_MEMBER(Display, date)
   REGISTER_STRING_MEMBER(Display, id)
   REGISTER_ULONG_MEMBER(Display, unitCode)
   REGISTER_ULONG_MEMBER(Display, flags)
   REGISTER_COLLECTION_MEMBER(Display, items, DisplayItem)
#ifdef GPS_POS
   REGISTER_LONG_SCALE_MEMBER(Display, latitude, GPS_SCALE)
   REGISTER_LONG_SCALE_MEMBER(Display, longitude, GPS_SCALE)
#endif
END_TYPE_REFLECTION(Display)

void OpenDisplay(DisplayImpl* d, bool retToDocList);

DisplayImpl::~DisplayImpl()
{
}

bool DisplayImpl::CreateDocument(const ROWID &orgID)
{
   if( !Init(orgID) || !EditDetail() )
      return false;

   EditDocument(0);
   return true;
}

const wchar_t* DisplayImpl::Description() const
{
   return (flags & ofExported) ? L"отправлен" : L"";
}

bool DisplayImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   if( reverse )
   {
      if( flags & ofExported ) flags &= (~ofExported);
      else flags |= ofExported;
   } else
      flags |= ofExported;
   return (updateTable == NULL) ? true : updateTable->Update(*this, L"flags", rid);
}

bool DisplayImpl::Init(const ROWID &orgID)
{
#ifdef GPS_POS
   if( !CheckGPSPos(L"Получить координаты?") )
      return false;

   latitude = gCurrentGPSPos.latitude;
   longitude = gCurrentGPSPos.longitude;
#endif

   OrgImpl org;
   org.Read(orgID);
   if( org.units.size() == 0 )
   {
      MessageBox(NULL, L"Нет адресов доставки у контрагента", L"Ошибка", MB_OK | MB_ICONSTOP);
      return false;
   }
   id = holder.Add(org.id);

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &date);

   flags = 0;
   unitCode = org.units.front().id;
   return true;
}

bool DisplayImpl::CanRemove() const
{
   return MessageBox(GetActiveWindow(), L"Удалить документ?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES;
}

bool DisplayImpl::RemoveDocument()
{
   bool ret = Remove();
   if( ret )
   {
      vector_t<DisplayItem>::iterator i = items.begin();
      for( ; i != items.end(); i++ )
         DeleteFile(i->id);
   }
   return ret;
}

void DisplayImpl::EditDocument(UINT retForm)
{
   OpenDisplay(this, (retForm != IDD_ORDER_LIST));
}

bool DisplayImpl::EditDetail()
{
   DetailDlg dlg(this);

   return (dlg.DoModal() == IDOK);
}

LRESULT DetailDlg::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   bHandled = FALSE;

   OrgImpl o;
   o.id = doc->id;
   if( o.Read() )
      units.Init(*this, IDC_UNIT_LIST, IDC_UNIT_TEXT_LABEL, IDC_UNIT_TEXT, o, doc->unitCode);

   if( doc->IsDirty() == false )
      DisableChilds();

   return TRUE;
}

LRESULT DetailDlg::OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

   units.UpdateLayout(wdh, hgh);
   MoveButtons(wdh, hgh);

   return 0;
}

LRESULT DetailDlg::Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   bHandled = FALSE;

   if( wID == IDOK )
      doc->unitCode = units.GetSelectedItemCode();

   return 0;
}

LRESULT TreeViewEx::OnClick(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   bHandled = FALSE;

   if( handler != NULL )
   {
      TVHITTESTINFO ht;
      ht.pt.x = LOWORD(lParam);
      ht.pt.y = HIWORD(lParam);

      HTREEITEM item = HitTest(&ht);
      if( item != NULL && (ht.flags & TVHT_ONITEMLABEL) == TVHT_ONITEMLABEL )
         handler->Selected(item);
   }
   return 0;
}

DisplayForm::~DisplayForm()
{
   delete data;
   images.Destroy();
}

struct FolderLData
{
   HTREEITEM item;
   int level;
};

static void MakeFolderTree(CTreeViewCtrl& folders)
{
   TV_INSERTSTRUCT item;

   item.hParent = TVI_ROOT;
   item.hInsertAfter = TVI_LAST;

   item.item.mask = TVIF_TEXT | TVIF_PARAM;
   item.item.pszText = L"Прайс-лист";
   item.item.lParam = -1;

   HTREEITEM root = folders.InsertItem(&item);

   FolderImpl f;
   SQLTable t(f.Name());
   bool bdo = t.Select(&f, L"ORDER BY id");

   std::vector<FolderLData> levels;
   while( bdo )
   {
      if( levels.size() == 0 )
      {
         item.hParent = root;
      }
      else
      {
         FolderLData& last = levels.back();
         if( f.level == last.level )
         {
            levels.pop_back();
            item.hParent = (levels.size() > 0) ? levels.back().item : root;
         } else if( f.level > last.level )
         {
            item.hParent = last.item;
         } else
         {
            while( f.level < last.level && levels.size() > 0 )
            {
               levels.pop_back();
               last = levels.back();
            }

            item.hParent = (levels.size() > 0) ? levels.back().item : root;
         }
      }

      item.item.pszText = f.name;
      item.item.lParam = f.id;

      HTREEITEM current = folders.InsertItem(&item);
      FolderLData fld;
      fld.level = f.level;
      fld.item = current;
      levels. push_back(fld);

      bdo = t.SelectNext(&f);
   }

   folders.Expand(root, TVE_EXPAND);
}

static HTREEITEM FindItem(CTreeViewCtrl& folders, HTREEITEM parent, const wchar_t* id)
{
   while( true )
   {
      // check self
      wchar_t buf[500];
      if( folders.GetItemText(parent, buf, sizeof(buf) / sizeof(buf[0])) )
      {
         if( wcscmp(buf, id) == 0 )
            return parent;
      }

      // check childs
      HTREEITEM hi = folders.GetChildItem(parent);
      if( hi != NULL )
      {
         hi = FindItem(folders, hi, id);
         if( hi )
            return hi;
      }

      // move to next sibling
      parent = folders.GetNextSiblingItem(parent);
      if( parent == NULL )
         return NULL;
   }
}

static void SelectFolder(CTreeViewCtrl& folders, const wchar_t* id)
{
   HTREEITEM hi;
   // find and unselect
   hi = folders.GetSelectedItem();
   if( hi != NULL )
      folders.SetItemState(hi, 0, TVIS_SELECTED);

   hi = FindItem(folders, folders.GetRootItem(), id);
   if( hi != NULL )
      folders.SelectItem(hi);
}

const int ImgWdh = 60;
const int ImgHgh = 40;
bool DisplayForm::SetData(IFormData *_data)
{
   data = (DisplayData*)_data;

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   int scale = GetSystemMetrics(SM_CXSMICON) / 16;
   images.Create(ImgWdh * scale, ImgHgh * scale, ILC_COLOR24, 1, 1);
   photos.Attach(GetDlgItem(IDC_TABLE));
   photos.SetImageList(images, LVSIL_NORMAL);

   //photos.ModifyStyle(0, LVS_REPORT|LVS_SINGLESEL|WS_VSCROLL);
   photos.InsertColumn(0, L"Фото", LVCFMT_LEFT, ImgWdh + 10);
   photos.InsertColumn(1, L"Папка", LVCFMT_LEFT, 200);
   photos.BringWindowToTop();

   folders.SetHandler(this);
   folders.SubclassWindow(GetDlgItem(IDC_SET_FOLDER));
   MakeFolderTree(folders);

   UpdateLayout(true);

   RefreshPhoto();

   if( data->doc->IsDirty() == false )
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);

   return true;
}

LRESULT DisplayForm::OnClick(LPNMHDR hdr)
{
   if( hdr->idFrom == IDC_TABLE )
   {
      int index = ((NMLISTVIEW*)hdr)->iItem;
      if( index >= 0 )
      {
         selected = &data->doc->items.at(index);
         selectedIndex = -1; // use in SetFolder
         SelectFolder(folders, selected->folder);
         selectedIndex = index;
         folders.BringWindowToTop();
         folders.SetFocus();
         //folders.ShowWindow(SW_SHOW);
      }
   } else
      SetMsgHandled(FALSE);
   return TRUE;
}

void DisplayForm::Selected(HTREEITEM item)
{
   if( selectedIndex >= 0 )
   {
      wchar_t buf[500];
      if( folders.GetItemText(item, buf, sizeof(buf) / sizeof(buf[0])) )
      {
         selected->folder = data->doc->holder.Add(buf);
         photos.SetItemText(selectedIndex, 0, selected->folder);

         data->doc->Write();
      }

      photos.BringWindowToTop();
      photos.SetFocus();
   }
}

void DisplayForm::UpdateLayout(bool forceRecalc)
{
   CRect rc;
   GetClientRect(rc);

   CWindow tbl(GetDlgItem(IDC_TABLE));
   tbl.MoveWindow(rc);

   if( forceRecalc )
   {
      photos.SetColumnWidth(1, rc.Width() - ImgWdh - 10 - GetSystemMetrics(SM_CXVSCROLL)-2 );
      folders.MoveWindow(rc);
   }
}

bool DisplayForm::CheckData()
{
   int index = 0;
   vector_t<DisplayItem>::const_iterator i = data->doc->items.begin();
   for( ; i != data->doc->items.end(); i++, index++ )
   {
      if( *(i->folder) == L'\0' )
      {
         MessageBox( L"Пперед выходом укажите папки для всех фото!", L"Ошибка", MB_OK | MB_ICONSTOP);
         photos.SelectItem(index);
         photos.SetFocus();
         return false;
      }
   }

   return true;
}

LRESULT DisplayForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   //if( selectedIndex >= 0 )
   //{
   //   photos.BringWindowToTop();
   //   photos.SetFocus();
   //   selectedIndex = -1;
   //   return 0;
   //}
   if( !CheckData() )
      return 0;

   if( !CreateNextDoc(data->doc->id) )
   {
      if( data->retToDocList )
         OpenOrgDocs(data->doc->id, dtDisplay);
      else
         OpenListDoc();
   }
   return 0;
}

LRESULT DisplayForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( SendDocument(data->doc, docTypeManager.GetDocType(dtDisplay), L"Документ отправлен") )
   {
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
      data->doc->Write();
   }

   return 0;
}

LRESULT DisplayForm::MakePhoto(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( data->doc->IsDirty() == false )
      return 0;

#ifdef WIN32_PLATFORM_PSPC
   std::wstring photo;
   if( MainFrame::MakePhoto(m_hWnd, &photo) )
   {
      DisplayItem di;
      di.id = data->doc->holder.Add(photo.c_str());
      di.folder = L"";

      data->doc->items.push_back(di);
      data->doc->Write();

      AddPhoto(di);
   }
#endif
   return 0;
}

LRESULT DisplayForm::ShowPicture(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
#ifdef WIN32_PLATFORM_PSPC
   int index = photos.GetSelectedIndex();
   if( index >= 0 )
   {
      HBITMAP hBmp = ::SHLoadImageFile(data->doc->items[index].id);
      if( hBmp != NULL )
      {
         if( picWindow != NULL )
         {
            picWindow->Cancel();
            delete picWindow;
         }
         picWindow = new PicWindow(hBmp);
         picWindow->Show(m_hWnd);
         delete picWindow;
         picWindow = NULL;
      }
   }
#endif
   return 0;
}

LRESULT DisplayForm::EditDetail(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   data->doc->EditDetail();
   return 0;
}

LRESULT DisplayForm::RemovePicture(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( data->doc->IsDirty() )
   {
      int index = photos.GetSelectedIndex();
      if( index >= 0 && MessageBox(L"Удалить фото?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
      {
         vector_t<DisplayItem>::iterator i = data->doc->items.begin();
         advance(i, index);

         DeleteFile(i->id);
         data->doc->items.erase(i);

         RemovePhoto(index);
         data->doc->Write();
      }
   }
   return 0;
}

LRESULT DisplayForm::ShowContextMenu(HWND hWnd, const CPoint &org)
{
   photos.SetFocus();

   CPoint pt(org);
   photos.ScreenToClient(&pt);
   int index = photos.GetItemCount() - 1;
   for( ; index >=0; index-- )
   {
      CRect rc;
      photos.GetItemRect(index, rc, LVIR_ICON);
      if( rc.PtInRect(pt) )
         break;
   }

   if( index >= 0 )
   {
      int prevSel = photos.GetSelectedIndex();
      if( prevSel >= 0 && prevSel != index )
         photos.SetItemState(prevSel, 0, LVIS_SELECTED|LVIS_FOCUSED);
      if( prevSel != index )
         photos.SetItemState(index, LVIS_SELECTED|LVIS_FOCUSED, LVIS_SELECTED|LVIS_FOCUSED);

      HMENU hm = LoadMenu(_Module.GetResourceInstance(), MAKEINTRESOURCE(IDD_DISPLAY));
      if( hm )
      {
         TrackPopupMenuEx(GetSubMenu(hm, 0),  TPM_TOPALIGN, org.x, org.y, m_hWnd, NULL);   
         DestroyMenu(hm);
      }
   }
   return 0;
}

void DisplayForm::RefreshPhoto()
{
   HCURSOR hCurs = GetCursor();
   SetCursor(LoadCursor(NULL, IDC_WAIT));

   photos.SetRedraw(FALSE);

   vector_t<DisplayItem>::const_iterator i = data->doc->items.begin();
   for( ; i != data->doc->items.end(); i++ )
      AddPhoto((*i));

   photos.SetRedraw(TRUE);

   SetCursor(hCurs);
}

void DisplayForm::AddPhoto(const DisplayItem& ditem)
{
#ifdef WIN32_PLATFORM_PSPC
   HBITMAP hBmp = ::SHLoadImageFile(ditem.id);
   if( hBmp != NULL )
   {
      BITMAP bm;
      GetObject(hBmp, sizeof(bm), &bm);

      HDC memDC = CreateCompatibleDC(NULL);

      SelectObject(memDC, hBmp);
      int scale = GetSystemMetrics(SM_CXSMICON) / 16;
      HBITMAP hDest = CreateCompatibleBitmap(memDC, ImgWdh * scale, ImgHgh * scale);
      HGDIOBJ svBmp = SelectObject(memDC, hDest);

      RECT rc = { 0, 0, ImgWdh * scale, ImgHgh * scale };
      FillRect(memDC, &rc, (HBRUSH)GetStockObject(WHITE_BRUSH));

      PaintScale(memDC, hBmp, ImgWdh * scale, ImgHgh * scale);

      SelectObject(memDC, GetStockObject(NULL_BRUSH));
      Rectangle(memDC, rc.left, rc.top, rc.right, rc.bottom);

      SelectObject(memDC, svBmp);

      int index = images.Add(hDest);

      DeleteDC(memDC);
      DeleteObject(hDest);
      DeleteObject(hBmp);

      LVITEM item = {0};
      item.mask = LVIF_IMAGE | LVIF_TEXT;
      item.iImage = index;
      item.iItem = photos.GetItemCount();
      item.pszText = (*ditem.folder == L'\0') ? L"<?>" : ditem.folder;

      photos.InsertItem(&item);
   }
#endif
}

void DisplayForm::RemovePhoto(int index)
{
   photos.DeleteItem(index);
   photos.Arrange(LVA_DEFAULT);
}

void OpenDisplay(DisplayImpl* d, bool retToDocList)
{
   OrgImpl o;
   o.id = d->id;
   o.Read();
   if( o.units.size() == 0 )
   {
      delete d;
      MessageBox(NULL, L"У клиента нет адреса доставки", L"Ошибка", MB_OK|MB_ICONINFORMATION);
      return;
   }

   DisplayData *data = new DisplayData(d, retToDocList);
   _Module.GetFrame()->Load(IDD_DISPLAY, data);
}
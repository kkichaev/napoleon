/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Визиты
 *
 *  ert   08/12/2008   creating
 *  ert   23/06/2009   update
 */
#include "stdafx.h"

#include "Visit.h"

#include <StdFuncs.h>
#include <InitDoc.h>
#include <BaseDialog.h>
#include "PhotoFolder.h"
#include "PrfDlg.h"
#include <NplConfig.h>
#include "FileType.h"

#ifndef MAX_VISIT_PHOTO
#define MAX_VISIT_PHOTO 3
#endif

wchar_t dtVisit[] = L"Посещения";

#define VISIT_CAUSE_STR L"ПричиныВизита"
 
BEGIN_TYPE_REFLECTION(VisitItem)
   REGISTER_FILE_MEMBER(VisitItem, id)
END_TYPE_REFLECTION(VisitItem)

BEGIN_TYPE_REFLECTION(Visit)
   REGISTER_TIMESTAMP_MEMBER(Visit, date)
   REGISTER_TIMESTAMP_MEMBER(Visit, created)
   REGISTER_STRING_MEMBER(Visit, id)
   REGISTER_STRING_MEMBER(Visit, remark)
#ifdef VISIT_CAUSE
   REGISTER_STRING_MEMBER(Visit, cause)
#endif
#ifdef Agama
   REGISTER_ULONG_MEMBER(Visit, unitCode)
#endif
   REGISTER_ULONG_MEMBER(Visit, flags)
   REGISTER_COLLECTION_MEMBER(Visit, items, VisitItem)
#ifdef GPS_POS
   REGISTER_LONG_SCALE_MEMBER(Visit, latitude, GPS_SCALE)
   REGISTER_LONG_SCALE_MEMBER(Visit, longitude, GPS_SCALE)
#endif
END_TYPE_REFLECTION(Visit)



//
//----------------------------------- VisitType -------------------------------------------
//
struct VisitFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new VisitImpl(); }
   virtual void Free(IDocument* document) const { delete (VisitImpl*)document; }
} visitFactory;

VisitType::VisitType() :
   DocType(dtVisit, &visitFactory, 0)
{
}
//
//----------------------------------- VisitImpl -------------------------------------------
//
const wchar_t* VisitImpl::Description() const
{
   return (flags & ofExported) ? L"отправлен" : L"";
}

void VisitImpl::EditDocument(UINT retForm)
{
   OpenVisit(this, (retForm != IDD_ORDER_LIST));
}

bool VisitImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   //if( rid == NO_ROWID ) return false;

   if( reverse )
   {
      if( flags & ofExported ) flags &= (~ofExported);
      else flags |= ofExported;
   } else
      flags |= ofExported;
   return (updateTable == NULL) ? true : updateTable->Update(*this, L"flags", rid);
}

bool VisitImpl::Init(const ROWID &orgID)
{
#ifdef GPS_POS
   if( !CheckGPSPos(L"Получить координаты?") )
      return false;

   latitude = gCurrentGPSPos.latitude;
   longitude = gCurrentGPSPos.longitude;
#endif

   OrgImpl org;
   org.Read(orgID);
   id = holder.Add(org.id);

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &date);
   created = date;

#ifdef VISIT_CAUSE
   cause = L"";
#endif

   if( !Read() )
   {
      flags = 0;
      remark = L"";
   }
   return true;
}

bool VisitImpl::CreateDocument(const ROWID &orgID)
{
   if( !Init(orgID) )
      return false;

   OpenVisit(this, true);
   return true;
}

bool VisitImpl::RemoveDocument()
{
   bool ret = Remove();
   if( ret )
   {
      vector_t<VisitItem>::iterator i = items.begin();
      for( ; i != items.end(); i++ )
         DeleteFile(i->id);
   }
   return ret;
}

bool VisitImpl::CanRemove() const
{
   return MessageBox(GetActiveWindow(), L"Удалить визит?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES;
}

//
//-------------------------------------------------- VisitForm ----------------------------------------------
//

IMPLEMENT_FORM(VisitForm)

void VisitForm::WriteChanges()
{
   if( data->visit->IsDirty() )
   {
      CWindow text(GetDlgItem(IDC_REMARK));

      int len = text.GetWindowTextLength() + 1;
      wchar_t *txt = (wchar_t*)alloca(len * sizeof(wchar_t));
      text.GetWindowText(txt, len);

      bool changed = (wcscmp(txt, data->visit->remark) != 0);

#ifdef VISIT_CAUSE
      CWindow tcause(GetDlgItem(IDC_CAUSE));
      len = tcause.GetWindowTextLength() + 1;
      wchar_t *tc = (wchar_t*)alloca(len * sizeof(wchar_t));
      tcause.GetWindowText(tc, len);

      changed = changed || (wcscmp(tc, data->visit->cause) != 0);
#endif

      if( changed && MessageBox( L"Сохранить посещение?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES )
      {
         UpdateData();
         data->visit->Write();
      }
   }
}

LRESULT VisitForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   WriteChanges();

   if( !CreateNextDoc(data->visit->id) )
   {
      if( data->retToDocList )
         OpenOrgDocs(data->visit->id, dtVisit);
      else
         OpenListDoc();
   }
   return 0;
}

void VisitForm::UpdateData()
{
   CWindow text(GetDlgItem(IDC_REMARK));

   int len = text.GetWindowTextLength() + 1;
   wchar_t *txt = (wchar_t*)alloca(len * sizeof(wchar_t));
   text.GetWindowText(txt, len);
   data->visit->remark = data->visit->holder.Add(txt);

#ifdef VISIT_CAUSE
   CWindow tcause(GetDlgItem(IDC_CAUSE));
   len = tcause.GetWindowTextLength() + 1;
   wchar_t *tc = (wchar_t*)alloca(len * sizeof(wchar_t));
   tcause.GetWindowText(tc, len);
   data->visit->cause = data->visit->holder.Add(tc);
#endif

}

LRESULT VisitForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( data->visit->IsDirty() )
      UpdateData();

#ifdef SCRIPT_DOC
   if( SendDocument(data->visit, docTypeManager.GetDocType(dtVisit), L"Посещение отправлено", false) )
#else
   if( SendDocument(data->visit, docTypeManager.GetDocType(dtVisit), L"Посещение отправлено") )
#endif
   {
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
      data->visit->Write();
   }
   return 0;
}

LRESULT VisitForm::MakePhoto(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
#ifdef WIN32_PLATFORM_PSPC
   if( data->visit->items.size() >= MAX_VISIT_PHOTO )
   {
      MessageBox(L"Перед добавлением удалите одну из предыдущий фотографий", L"Ошибка", MB_OK);
      return 0;
   }

   std::wstring photo;
   if( MainFrame::MakePhoto(m_hWnd, &photo) )
   {
      VisitItem vi;
      vi.id = data->visit->holder.Add(photo.c_str());
      data->visit->items.push_back(vi);

      UpdateData();
      data->visit->Write();

      AddPhoto(photo.c_str());
   }
#endif
   return 0;
}

VisitForm::~VisitForm()
{
   delete data;
   images.Destroy();
}

void VisitForm::UpdateLayout(bool forceRecalc)
{
   CRect rc, rc1;
   GetClientRect(rc);

   CWindow tbl(GetDlgItem(IDC_TABLE));
   tbl.GetWindowRect(rc1);
   int hgh = rc1.Height();

#ifdef VISIT_CAUSE
   CWindow cause(GetDlgItem(IDC_CAUSE));
   cause.GetWindowRect(rc1);
   ScreenToClient(rc1);
   rc1.right = rc.right - 2;
   cause.MoveWindow(rc1);

   rc.top = rc1.bottom + 2;
   rc.bottom -= hgh + 1;
   GetDlgItem(IDC_REMARK).MoveWindow(rc);
#else
   rc.top = (GetSystemMetrics(SM_CXSMICON) == 16) ? 30 : 60;
   rc.bottom -= hgh + 1;
   GetDlgItem(IDC_REMARK).MoveWindow(rc);
#endif

   rc.top = rc.bottom + 1;
   rc.bottom += hgh;
   tbl.MoveWindow(rc);
}

const int ImgWdh = 60;
const int ImgHgh = 40;
bool VisitForm::SetData(IFormData *_data)
{
   data = (VisitData*)_data;

   if( (data->visit->flags & ofExported) != 0 )
      GetDlgItem(IDC_REMARK).EnableWindow(FALSE);

   GetDlgItem(IDC_REMARK).SetWindowText(data->visit->remark);

#ifdef VISIT_CAUSE
   CComboBox cbBox(GetDlgItem(IDC_CAUSE));
   NapoleonConfig cfg;
   std::wstring cval;
   if( cfg.ReadValue(&cval, VISIT_CAUSE_STR) )
   {
      const wchar_t *src = data->visit->cause;

      std::wstring::size_type sp = 0;
      for( int i=0; ; i++ )
      {
         std::wstring::size_type ep = cval.find_first_of(SEP_SYM, sp);
         const std::wstring& vl = (ep == std::wstring::npos) ? cval.substr(sp, ep) : cval.substr(sp, ep - sp);
         int index = cbBox.AddString(vl.c_str());

         if( wcscmp(vl.c_str(), src) == 0 )
            cbBox.SetCurSel(index);

         if( ep == std::wstring::npos ) break;
         sp = ep + 1;
      }
   }
#endif

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   if( !data->visit->IsDirty() )
      menuBar.EnableButton(IDC_PHOTO, FALSE);

#ifdef HappyLand
   menuBar.HideButton(IDC_SEND);
#endif

   int scale = GetSystemMetrics(SM_CXSMICON) / 16;
   images.Create(ImgWdh * scale, ImgHgh * scale, ILC_COLOR24, 1, 1);
   photos.Attach(GetDlgItem(IDC_TABLE));
   photos.SetImageList(images, LVSIL_NORMAL);
   photos.SetIconSpacing((ImgWdh+10) * scale, (ImgHgh+10) * scale);

   RefreshPhoto();
   return true;
}

void VisitForm::RefreshPhoto()
{
   photos.SetRedraw(FALSE);

   vector_t<VisitItem>::const_iterator i = data->visit->items.begin();
   for( ; i != data->visit->items.end(); i++ )
      AddPhoto(i->id);

   photos.SetRedraw(TRUE);
}

void VisitForm::AddPhoto(const wchar_t *file)
{
#ifdef WIN32_PLATFORM_PSPC
   HBITMAP hBmp = ::SHLoadImageFile(file);
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
      item.mask = LVIF_IMAGE;
      item.iImage = index;
      item.iItem = photos.GetItemCount();

      photos.InsertItem(&item);
   }
#endif
}

void VisitForm::RemovePhoto(int index)
{
   photos.DeleteItem(index);
   photos.Arrange(LVA_DEFAULT);
}

LRESULT VisitForm::ShowContextMenu(HWND hWnd, const CPoint &org)
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

      HMENU hm = LoadMenu(_Module.GetResourceInstance(), MAKEINTRESOURCE(IDD_VISIT));
      if( hm )
      {
         TrackPopupMenuEx(GetSubMenu(hm, 0),  TPM_TOPALIGN, org.x, org.y, m_hWnd, NULL);   
         DestroyMenu(hm);
      }
   }
   return 0;
}

LRESULT VisitForm::ShowPicture(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
#ifdef WIN32_PLATFORM_PSPC
   int index = photos.GetSelectedIndex();
   if( index >= 0 )
   {
      HBITMAP hBmp = ::SHLoadImageFile(data->visit->items[index].id);
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

LRESULT VisitForm::RemovePicture(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( data->visit->IsDirty() )
   {
      int index = photos.GetSelectedIndex();
      if( index >= 0 && MessageBox(L"Удалить фото?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
      {
         vector_t<VisitItem>::iterator i = data->visit->items.begin();
         advance(i, index);

         DeleteFile(i->id);
         data->visit->items.erase(i);

         RemovePhoto(index);
         data->visit->Write();
      }
   }
   return 0;
}

//
//---------------------------------------- PhotoProperties ----------------------------
//
PhotoProperties::PhotoProperties() : PrefPage(IDD_PHOTO_PREFERENCE, L"Фото")
{
}

LRESULT PhotoProperties::OnSelChange(WORD notifyID, WORD idc, HWND hWnd, BOOL& bHandled)
{
   CComboBox q(GetDlgItem(IDC_PHOTO));
   BOOL enabled = (q.GetCurSel() == 2) ? TRUE : FALSE;

   GetDlgItem(IDC_WIDTH).EnableWindow(enabled);
   GetDlgItem(IDC_HEIGHT).EnableWindow(enabled);
   return 0;
}

void PhotoProperties::Init()
{
   LoadFolderData(*this, IDC_PHOTO_FOLDER);

   CComboBox q(GetDlgItem(IDC_PHOTO));
   //q.AddString(L"низкое");
   q.AddString(L"среднее");
   q.AddString(L"высокое");
   q.AddString(L"задать");

   Preference p;
   p.Load();
   q.SetCurSel(p.photoQuality);

   int hghI = 0, wdhI = 0;
   if( p.photoQuality == 2 )
   {
      hghI = p.picHeight;
      wdhI = p.picWidth;
   }
   SetDlgItemInt(IDC_WIDTH, wdhI);
   SetDlgItemInt(IDC_HEIGHT, hghI);

   BOOL b = FALSE;
   OnSelChange(0, 0, NULL, b);
}

void PhotoProperties::Save(Preference *preference)
{
   if( m_hWnd == NULL ) return;

   StoreFolderData(*this, IDC_PHOTO_FOLDER, preference);

   CComboBox q(GetDlgItem(IDC_PHOTO));
   preference->photoQuality = q.GetCurSel();

   int hghI = 0, wdhI = 0;
   if( preference->photoQuality == 2 )
   {
      hghI = GetDlgItemInt(IDC_HEIGHT, NULL, FALSE);
      wdhI = GetDlgItemInt(IDC_WIDTH, NULL, FALSE);
   }

   preference->picHeight = hghI;
   preference->picWidth = wdhI;
}

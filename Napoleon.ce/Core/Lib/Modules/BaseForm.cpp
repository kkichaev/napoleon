/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Базовая форма
 *
 *  ert   16/08/2007   creating
 */ 
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <BaseForm.h>
#include <NapoleonRes.h>
#include <BaseDialog.h>

WORD BaseForm::screenWidth = 0;

void BaseForm::Destroy()
{ 
   if( m_hWnd ) DestroyWindow(); 
   delete this;
}

//
//--------------------- Form loader -------------------------
//

struct ResourceFont
{
   WORD      size; // размер шрифта
   wchar_t*  name; // название
};

inline BYTE* ALIGN_WORD(BYTE *v)
{
   return ((int)v % 2) ? v + 1 : v;
}

inline BYTE* ALIGN_DWORD(BYTE *v)
{
   int rest = (int)v % 4;
   return (rest) ? v + (4 - rest) : v;
}

static ResourceFont ReadFont(BYTE *src, BYTE **endPtr)
{
   ResourceFont font;

   font.size = *(WORD*)src;

   src += sizeof(WORD);
   font.name = (wchar_t*)src; 

   if( endPtr )
      *endPtr = (BYTE*)wcschr((wchar_t*)src, L'\0') + sizeof(wchar_t);
   return font;
}

ResourceString ReadResourceString(BYTE *src, BYTE **endPtr)
{
   ResourceString rs;

   WORD value = *(WORD*)src;
   src += sizeof(WORD);
   if( value != 0xFFFF )
   {
      if( value == 0 )
      {
         rs.size = 0;
         rs.str = L"";

         if( endPtr )
            *endPtr = src;
      } else
      {
         rs.size = wcslen((wchar_t*)src);
         rs.str = (const wchar_t*)src;

         if( endPtr )
            *endPtr = (BYTE*)wcschr((wchar_t*)src, L'\0') + sizeof(wchar_t);
      }
   } else
   {
      wchar_t *str = (wchar_t*)LoadString(_Module.GetResourceInstance(), *(WORD*)src, NULL, 0);
      rs.str = str;
      rs.size = *((WORD*)str - 1);

      if( endPtr )
         *endPtr = src + sizeof(DWORD);
   }
   return rs;
}

static const wchar_t* DecodeClassName(WORD classAtom)
{
   switch(classAtom)
   {
	   case 0x0080:
         return L"BUTTON";
		
      case 0x0081:
	      return L"EDIT";
		
      case 0x0082:
         return L"STATIC";
		
      case 0x0083:
         return L"LISTBOX";

      case 0x0084:
         return L"SCROLLBAR";

      case 0x0085:
         return L"COMBOBOX";
   }
   return L"";
}

static void LoadItem(BaseForm *form, DLGITEMTEMPLATE *item, BYTE **endPtr)
{
   BYTE *cp = (BYTE*)item + sizeof(DLGITEMTEMPLATE);
   
   RECT bounds;
   void *param = NULL;
   const wchar_t* className, *title = L"";
   WORD value = *(WORD*)cp;
   if( value == 0xFFFF )
   {
      cp += sizeof(WORD);
      className = DecodeClassName(*(WORD*)cp);
      cp += sizeof(WORD);
   } else
   {
      className = (const wchar_t*)cp;
      cp = (BYTE*)wcschr((const wchar_t*)cp, L'\0') + sizeof(wchar_t);
   }


   value = *(WORD*)cp;
   if( value == 0xFFFF ) // have icon or resource
      cp += sizeof(DWORD) + sizeof(WORD);
   else
   {
      title = (const wchar_t*)cp;
      cp = (BYTE*)wcschr((const wchar_t*)cp, L'\0') + sizeof(wchar_t);
   }

   cp = ALIGN_WORD(cp);

   WORD initDataSize = *(WORD*)cp;
   cp += sizeof(WORD);
   if( initDataSize ) // have init data
   {
      param = cp;
      cp += initDataSize - sizeof(WORD);
   }

   LONG units = GetDialogBaseUnits();
   bounds.left   = (item->x * LOWORD(units)) / 4;
   bounds.right  = (item->cx * LOWORD(units)) / 4;
   bounds.top    = (item->y * HIWORD(units)) / 8;
   bounds.bottom = (item->cy * HIWORD(units)) / 8;

   ::CreateWindowEx(item->dwExtendedStyle, className, title, item->style,
         bounds.left, bounds.top, bounds.right, bounds.bottom,
         form->m_hWnd, (HMENU)item->id, _Module.GetResourceInstance(), param);

   if( endPtr )
      *endPtr = ALIGN_DWORD(cp);
}

bool BaseForm::Load(HWND parent)
{
   HRSRC hResource = FindResource(_Module.GetResourceInstance(), MAKEINTRESOURCE(GetResourceID()), (LPTSTR)RT_DIALOG);
   if( hResource == NULL )
      return false;

   DLGTEMPLATE *pDlg = (DLGTEMPLATE*)LockResource(LoadResource(_Module.GetResourceInstance(), hResource));
   BYTE *buf;
   
   buf = (BYTE*)pDlg + sizeof(DLGTEMPLATE);
   buf += sizeof(short);  // skip menuID
   ReadResourceString(buf, &buf); // class name

   std::wstring title = ReadResourceString(buf, &buf).MakeString(); // title

   // create window
   RECT bounds;
   ::GetClientRect(parent, &bounds);
   Create(parent, bounds, title.c_str(), /*WS_VISIBLE | */WS_CHILD);

   if( (pDlg->style & DS_SETFONT) )
      ReadFont(buf, &buf);

   for( int i=0; i<pDlg->cdit; i++ )
      LoadItem(this, (DLGITEMTEMPLATE*)buf, &buf);

   return true;
}

//
//--------------------- Form creator -------------------------
//
#pragma warning(disable : 4073)
#pragma init_seg(lib)
std::map<DWORD, IFrame::FormCreator> formCreatorMap;

void IFrame::RegisterFormCreator(DWORD id, FormCreator creator)
{
   formCreatorMap[id] = creator;
}

IFrame::FormCreator IFrame::GetFormCreator(DWORD id)
{
   std::map<DWORD, FormCreator>::const_iterator fnd = formCreatorMap.find(id);
   return (fnd == formCreatorMap.end()) ? NULL : fnd->second;
}


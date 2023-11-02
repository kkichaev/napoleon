/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Печать формы
 *
 *  ert   12/05/2008   creating
 */ 
#include "stdafx.h"
#include "NForm.h"

HBITMAP CreateMonoBitmap(HDC dc, DWORD width, DWORD height, BYTE **bits);

//HBITMAP FormMaker::MakePage(HDC dc, DWORD *pageHeight, BYTE **bits)
int idx;
bool FormMaker::MakePage(HDC dc, DWORD *pageHeight)
{
   if( current == cells.end() ) return false;

   if( source != NULL )
      source->StartPage();

   //HBITMAP bmp = CreateMonoBitmap(dc, Width(), Height(), bits);
   //if( bmp == NULL ) return NULL;

   //if( current == cells.begin() )
   //   idx = 1;
   //int curItem = 0;
   //if( idx > 1 )
   //   return true;

   int shiftY = 0;
   for( ; current != cells.end(); current++ )
   {
      IPrintable *ip = (*current);
      *pageHeight = Height();

      //if( idx > 1 && curItem > 0 )
      //{
      //   break;
      //}

      if( ip->Draw(dc, fontHolder, &shiftY, pageHeight) == false )
      {
         curPage++;
         break;
      }

      //curItem++;
   }
   //idx++;

   if( *pageHeight < Height() - 10 )
      *pageHeight += 10;

   return true;
   //return bmp;
}

FontHolder::FontHolder(DWORD resY)
{
   this->resY = resY;
}

FontHolder::~FontHolder()
{
   Clear();
}

void FontHolder::Clear()
{
   std::map<FontData, HFONT>::iterator i = fonts.begin();
   for( ; i != fonts.end(); i++ )
      DeleteObject(i->second); 
}

#define MulDiv(a,b,c)       (((a)*(b))/(c))

HFONT FontHolder::GetFont(int point, int flags)
{
   FontData fd;
   fd.point = point;
   fd.flags = flags;

   std::map<FontData, HFONT>::iterator fnd = fonts.find(fd);
   if( fnd != fonts.end() ) return fnd->second;

   HFONT sysF = (HFONT)GetStockObject(SYSTEM_FONT);
   LOGFONT lf;
   GetObject(sysF, sizeof(lf), &lf);

   *lf.lfFaceName = L'\0';
   lf.lfPitchAndFamily = DEFAULT_PITCH;

   lf.lfWidth = 0;
#ifndef UNDER_CE
   lf.lfWeight = FW_THIN;
#endif


   lf.lfHeight = -(int)MulDiv(point, resY, 72);

   if( flags & bold ) lf.lfWeight = FW_BOLD;
   if( flags & italic ) lf.lfItalic = TRUE;
   if( flags & underline ) lf.lfUnderline = TRUE;

   HFONT newFont = CreateFontIndirect(&lf);
   fonts.insert(std::map<FontData, HFONT>::value_type(fd, newFont));

   return newFont;
}

FormMaker::FormMaker(DWORD width, DWORD height, DWORD resY)
{
   album = false;

   this->width = width;
   this->height = height;

   fontHolder = new FontHolder(resY);
}

FormMaker::~FormMaker()
{
   delete fontHolder;

   CELLS::iterator i = cells.begin();
   for( ; i != cells.end(); i++ )
      delete (*i);
}

HBITMAP CreateMonoBitmap(HDC dc, DWORD width, DWORD height, BYTE **bits)
{
   int bufSize = sizeof(BITMAPINFO) + sizeof(RGBQUAD);
   char *buf = (char*) alloca(bufSize);

   BITMAPINFO *bi = (BITMAPINFO*)buf;
   bi->bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
   bi->bmiHeader.biWidth = width;
   bi->bmiHeader.biHeight = height;
   bi->bmiHeader.biPlanes = 1;
   bi->bmiHeader.biBitCount = 1;
   bi->bmiHeader.biCompression = BI_RGB;
   bi->bmiHeader.biSizeImage = 0;
   bi->bmiHeader.biXPelsPerMeter = 1;
   bi->bmiHeader.biYPelsPerMeter = 1;
   bi->bmiHeader.biClrUsed = 0;
   bi->bmiHeader.biClrImportant = 0;

   bi->bmiColors[0].rgbBlue = 0;
   bi->bmiColors[0].rgbRed = 0;
   bi->bmiColors[0].rgbGreen = 0;
   bi->bmiColors[1].rgbBlue = 0xFF;
   bi->bmiColors[1].rgbRed = 0xFF;
   bi->bmiColors[1].rgbGreen = 0xFF;

   HBITMAP hb = CreateDIBSection(dc, bi, DIB_RGB_COLORS, (void**)bits, NULL, 0);
 
   BITMAP bmp;
   GetObject(hb, sizeof(bmp), &bmp);
   memset(*bits, 0xFF, bmp.bmWidthBytes * bmp.bmHeight);

   return hb;
}

void FormMaker::SetProperty(const wchar_t *name, const wchar_t *value)
{
   if( wcscmp(name, L"Album") == 0 && wcscmp(value, L"True") == 0 )
   {
      DWORD tval = width;

      width = height;
      height = tval;

      album = true;
   }
}

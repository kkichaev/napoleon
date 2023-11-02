/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Печать формы
 *
 *  ert   12/05/2008   creating
 */ 
#ifndef __PRINT_FORM_H
#define __PRINT_FORM_H

#include <vector>
#include <map>
#include <set>
#include <string>

#include <Print.h>

#define FIELD_SYM L'`'

class ContentHandler;
class FontHolder
{
 public:
   FontHolder(DWORD resY);
   ~FontHolder();

   void Clear();

   enum Flags
   {
      bold = 1,
      italic = 2,
      underline = 4,
   };

   HFONT GetFont(int point, int flags);

 protected:
   struct FontData
   {
      int point;
      int flags;

      bool operator < (const FontData &ref) const
      {
         if( point < ref.point) return true;
         if( point > ref.point) return false;
         return flags < ref.flags;
      }
   };

   std::map<FontData, HFONT> fonts;
   DWORD resY;
};

struct IPrintable
{
   virtual ~IPrintable() {}

   RECT bounds;

   bool operator < (const IPrintable &ref) const
   { 
      int val = bounds.top - ref.bounds.top;
      return (val < 0) ? true : (val > 0) ? false : bounds.left < ref.bounds.left; 
      //int val = bounds.bottom - ref.bounds.bottom;
      //return (val < 0) ? true : (val > 0) ? false : bounds.right < ref.bounds.right; 
   }

   // shiftY - changes top for bounds
   // maxY - on input page height, on output - cell bottom
   // return false - need draw nextPage
   virtual bool Draw(HDC dc, FontHolder *fh, int *shiftY, DWORD *maxY) = 0;

   virtual void BeforePrint(IDataSource *source) = 0;

   struct Compare
   {
      bool operator() (IPrintable *_left, IPrintable *_right) const { return (*_left) < (*_right); }
   };
};

class FormMaker;
struct IXMLStreamable;
typedef std::set<IPrintable*, IPrintable::Compare> CELLS;

class FormMaker
{
 public:
   FormMaker(DWORD width, DWORD height, DWORD resY);
   ~FormMaker();

   //HBITMAP MakePage(HDC dc, DWORD *pageHeight, BYTE **bits);
   bool MakePage(HDC dc, DWORD *pageHeight);

   bool Load(const wchar_t *formFile, IDataSource *source);

   void SetProperty(const wchar_t *name, const wchar_t *value);

   bool Album() const { return album; }
   DWORD Width() const { return width; }
   DWORD Height() const { return height; }

   FontHolder* GetFontHolder() const { return fontHolder; }

   void AddCell(IPrintable *cell) { cells.insert(cell); }

   // prepare after loading
   void AfterLoading() { current = cells.begin(); }

protected:

protected:
   bool album;

   CELLS cells;
   CELLS::iterator current;

   DWORD width;
   DWORD height;

   FontHolder *fontHolder;
   IDataSource *source;

   int curPage;

   friend class ContentHandler;
};
 
#endif

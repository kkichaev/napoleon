/*
 * Copyright (C), 2006-2008, ƒенис ћос€гин
 *
 * «агрузка из XML
 *
 *  ert   13/05/2008   creating
 */ 
#include "stdafx.h"
#include "IStreamable.h"

const int FontSize = 6;

void MakePrintText(std::wstring *value, const std::wstring &text, IDataSource *source);

class CellBase : public IXMLStreamable
{
 public:
   enum BoundsInited { biSize = 1, biLocation = 2 };
   enum Docking { top = 1, bottom = 2, left = 3, right = 4 };

   CellBase() : boundsInited(0), docking(0)
   {
      bounds.left = 0;
      bounds.right = 0;
      bounds.top = 0;
      bounds.bottom = 0;
   }

   virtual bool StartElement(const wchar_t *name, const SAXAttributes &attributes) { return true; }

   // return true only on </object>
   virtual bool EndElement(const wchar_t *name) { return (wcscmp(name, L"object") == 0); }

   virtual bool Valid() const { return ((boundsInited & (biSize|biLocation)) == (biSize|biLocation)); }

   bool SetProperty(const wchar_t *name, const wchar_t *value);

 protected:
   WORD boundsInited;
   WORD docking;
};

class LineCell : public CellBase
{
 public:
   LineCell();

   virtual bool SetProperty(const wchar_t *name, const wchar_t *value);
   virtual bool Draw(HDC dc, FontHolder *fh, int *shiftY, DWORD *maxY);

   virtual void BeforePrint(IDataSource *source) {}

 protected:
   bool horizontal;
   std::wstring text;
};

struct Align
{
   enum VAlign { Top = 1, Middle = 2, Bottom = 4 };
   enum HAlign { Left = 0x10, Right = 0x20, Center = 0x40 };

   Align() { value = (Middle | Center); }

   unsigned short value;

   DWORD DrawFlags() const;
   bool SetProperty(const wchar_t *name, const wchar_t *value);
   void PaintText(HDC dc, HFONT font, const std::wstring &text, const RECT &bounds, int xoffset, int yoffset);
};

class TextCell : public CellBase
{
 public:

   TextCell();

   virtual bool SetProperty(const wchar_t *name, const wchar_t *value);
   virtual bool Draw(HDC dc, FontHolder *fh, int *shiftY, DWORD *maxY);
   virtual void BeforePrint(IDataSource *source);

 protected:
   std::wstring text;
   std::wstring printText;
   Align align;
   IDataSource *source;

   int fontSize;
};

class Table : public CellBase
{
 public:
   Table();
   ~Table();

   virtual bool StartElement(const wchar_t *name, const SAXAttributes &attributes);
   virtual bool EndElement(const wchar_t *name);
   virtual bool SetProperty(const wchar_t *name, const wchar_t *value);

   virtual bool Draw(HDC dc, FontHolder *fh, int *shiftY, DWORD *maxY);
   virtual void BeforePrint(IDataSource *source);

 protected:
   enum State { tsNone, lsWidth, lsHeight, lsRow, tsPrintNextPage };
   struct Cell
   {
      enum Borders { None = 0, Left = 1, Right = 2, Top = 4, Bottom = 8, All = 15 };

      int index;
      int colspan;
      int rowspan;

      std::wstring text;
      std::wstring printText;

      Align align;
      Borders border;

      Cell(const wchar_t *params);

      Borders ParseBorders(wchar_t *p);

      int TextHeight(HDC dc, const std::vector<int> &width, FontHolder *fh, int fontSize);
      int Width(const std::vector<int> &width);
      int Height(int rowIndex, const std::vector<int> &height);

      void MakeText(IDataSource *source);

      void PaintText(HDC dc, const RECT &bounds, FontHolder *fh, bool multiLine, int fontSize);
   };

   typedef std::vector<Cell> Row;
   typedef std::vector<Row> ROWS;

   State state;

   int variableRow;
   std::wstring object;

   std::vector<int> width, height;
   ROWS rows;

   IDataSource *rowSource, *source;
   int fontSize;

 protected:
   int RowTextHeight(HDC dc, int rowIndex, FontHolder *fh);
   DWORD TailHeight();

   void DrawRaw(HDC dc, int rowIndex, int curY, Row &row, FontHolder *fh, DWORD textHeight);
   bool DrawVRow(HDC dc, FontHolder *fh, DWORD *top, DWORD *maxHeight, bool checkData);
   DWORD DrawRaws(HDC dc, FontHolder *fh, DWORD top, int startRow, int endRow);
};

//
// ---------------------------- CellBase -------------------------
//
bool CellBase::SetProperty(const wchar_t *name, const wchar_t *value)
{
   wchar_t *ep;
   if( wcscmp(name, L"Location") == 0 )
   {
      boundsInited |= biLocation;

      bounds.left = wcstol(value, &ep, 10);
      while( *ep != L'\0' && iswdigit(*ep) == 0 ) ep++;
      bounds.top = wcstol(ep, &ep, 10);

      if( boundsInited & biSize )
      {
         bounds.right += bounds.left;
         bounds.bottom += bounds.top;
      }
      return true;
   }

   if( wcscmp(name, L"Size") == 0 )
   {
      boundsInited |= biSize;

      bounds.right = wcstol(value, &ep, 10);
      while( *ep != L'\0' && iswdigit(*ep) == 0 ) ep++;
      bounds.bottom = wcstol(ep, &ep, 10);

      if( boundsInited & biLocation )
      {
         bounds.right += bounds.left;
         bounds.bottom += bounds.top;
      }
      return true;
   }

   if( wcscmp(name, L"Dock") == 0 )
   {
      if( wcsstr(value, L"Top") != 0 ) docking |= top;
      if( wcsstr(value, L"Bottom") != 0 ) docking |= bottom;
      if( wcsstr(value, L"Left") != 0 ) docking |= left;
      if( wcsstr(value, L"Right") != 0 ) docking |= right;
      return true;
   }
   return false;
}

//
// ---------------------------- Line Cell -------------------------
//
LineCell::LineCell() : horizontal(false)
{
   docking = (left | top);
}

bool LineCell::SetProperty(const wchar_t *name, const wchar_t *value)
{
   if( CellBase::SetProperty(name, value) ) return true;

   if( wcscmp(name, L"Horizontal") == 0 )
      horizontal = ( wcscmp(value, L"True") == 0 );
   else if( wcscmp(name, L"LineText") == 0 )
      text = value;
   return true;
}

bool LineCell::Draw(HDC dc, FontHolder *fh, int *shiftY, DWORD *maxY)
{
   SelectObject(dc, GetStockObject(BLACK_PEN));

   RECT bnds = bounds;

   bnds.top += *shiftY;
   bnds.bottom += *shiftY;

   if( horizontal )
   {
      int y = bnds.top;

      MoveToEx(dc, bnds.left, y, NULL);
      LineTo(dc, bnds.right, y);

      if( !text.empty() )
      {
         HFONT hf = fh->GetFont(FontSize-3, 0);
         SelectObject(dc, hf);

         RECT rc = bnds;
         rc.top += 1;
         rc.bottom -= 2;
         DrawTextW(dc, text.c_str(), text.size(), &rc, DT_CENTER | DT_SINGLELINE | DT_TOP);
      }
   }
   else
   {
      int x = bnds.left;

      MoveToEx(dc, x, bnds.top, NULL);
      LineTo(dc, x, bnds.top + (bounds.bottom - bounds.top));
   }

   *maxY = bnds.bottom;
   return true;
}

//
// ---------------------------- Align -------------------------
//
DWORD Align::DrawFlags() const
{
   DWORD flags = 0;

   if( (value & Right) != 0 ) flags |= DT_RIGHT;
   else if( (value & Center) != 0 ) flags |= DT_CENTER;
   else flags |= DT_LEFT;

   if( (value & Bottom) != 0 ) flags |= DT_BOTTOM;
   else if( (value & Middle) != 0 ) flags |= DT_VCENTER;
   else flags |= DT_TOP;

   return flags;
}

void Align::PaintText(HDC dc, HFONT font, const std::wstring &text, const RECT &bounds, int xoffset, int yoffset)
{
   RECT db = bounds;
   if( xoffset != 0 || yoffset != 0 )
      InflateRect(&db, -xoffset, -yoffset);

   DWORD alignFlags = DrawFlags();
   DWORD flags = DT_WORDBREAK | (alignFlags & (DT_LEFT|DT_RIGHT|DT_CENTER));

   HFONT prevFont = (HFONT)SelectObject(dc, font);

   DrawTextW(dc, text.c_str(), text.size(), &db, DT_CALCRECT | flags);
   db.left = bounds.left + xoffset;
   db.right = bounds.right - xoffset;
   if( alignFlags & DT_VCENTER )
   {
      if( db.bottom < bounds.bottom )
      {
         int shiftY = (bounds.bottom - db.bottom) / 2;
         db.top += shiftY;
         db.bottom = bounds.bottom;
      } else
         db = bounds;
   } else if( alignFlags & DT_BOTTOM )
   {
      int shiftY = bounds.bottom - db.bottom - yoffset; 
      db.top += shiftY;
      db.bottom = bounds.bottom;
   } else
   {
      db.top = bounds.top + yoffset;
      db.bottom = bounds.bottom;
   }

   DrawTextW(dc, text.c_str(), text.size(), &db, flags);

   SelectObject(dc, prevFont);
}

bool Align::SetProperty(const wchar_t *name, const wchar_t *svalue)
{
   if( wcscmp(name, L"TextAlign") == 0 )
   {
      value = 0;
      if( wcsstr(svalue, L"Bottom") ) value |= Bottom;
      else if( wcsstr(svalue, L"Top") ) value |= Top;
      else value |= Middle;

      if( wcsstr(svalue, L"Right") ) value |= Right;
      else if( wcsstr(svalue, L"Left") ) value |= Left;
      else value |= Center;
   }

   return true; 
}

//
// ---------------------------- Text Cell -------------------------
//
TextCell::TextCell() : fontSize(FontSize), source(NULL)
{
}

bool TextCell::SetProperty(const wchar_t *name, const wchar_t *value)
{
   if( CellBase::SetProperty(name, value) ) return true;

   if( wcscmp(name, L"Text") == 0 )
      text = value;
   if( wcscmp(name, L"FontSize") == 0 )
      fontSize = _wtoi(value);
   else
      align.SetProperty(name, value);

   return true;
}

void TextCell::BeforePrint(IDataSource *source)
{
   if( source != NULL )
      this->source = source;
   else
      printText = text;
}

void MakePrintText(std::wstring *printText, const std::wstring &text, IDataSource *source)
{
   std::wstring::size_type startPos = 0;

   printText->clear();
   while( true )
   {
      std::wstring::size_type fldPos = text.find(FIELD_SYM, startPos);

      (*printText) += text.substr(startPos, fldPos - startPos);
      if( fldPos == std::wstring::npos )
         break;

      fldPos++;
      std::wstring::size_type ep = text.find(FIELD_SYM, fldPos);
      if( ep == std::wstring::npos )
         break;

      std::wstring name = text.substr(fldPos, ep-fldPos);
      std::wstring value;
      if( source != NULL && source->GetValue(&value, name.c_str()) )
         (*printText) += value;

      startPos = ep + 1;
   }
}

bool TextCell::Draw(HDC dc, FontHolder *fh, int *shiftY, DWORD *maxY)
{
   if( source != NULL )
   {
      MakePrintText(&printText, text, source);
      source = NULL;
   }

   HFONT hf = fh->GetFont(fontSize, 0);

   RECT bnds = bounds;
   bnds.top += *shiftY;
   bnds.bottom += *shiftY;

   int sm = SetBkMode(dc, TRANSPARENT);
   align.PaintText(dc, hf, printText, bnds, 0, 0);
   SetBkMode(dc, sm);

   *maxY = bnds.bottom;
   return true;
}

//
// ---------------------------- Table -------------------------
//
Table::Table() : state(tsNone), variableRow(-1), rowSource(NULL), fontSize(FontSize)
{
}

Table::~Table()
{
}

bool Table::StartElement(const wchar_t *name, const SAXAttributes &attributes)
{
   if( wcscmp(name, L"TableWidth") == 0 )
      state = lsWidth;
   else if( wcscmp(name, L"TableHeight") == 0 )
      state = lsHeight;
   else if( wcsncmp(name, L"Cells", sizeof(L"Cells")/sizeof(wchar_t) - 1) == 0 )
   {
      state = lsRow;
      rows.push_back(Row());
   }

   return true;
}

bool Table::EndElement(const wchar_t *name)
{
   if( wcscmp(name, L"TableWidth") == 0 || wcscmp(name, L"TableHeight") == 0 ||
       wcsncmp(name, L"Cells", sizeof(L"Cells")/sizeof(wchar_t) - 1) == 0 )
   {
      state = tsNone;
   }

   return (wcscmp(name, L"object") == 0);
}

bool Table::SetProperty(const wchar_t *name, const wchar_t *value)
{
   if( state == tsNone )
   {
      if( wcscmp(name, L"Object") == 0 )
         object = value;
      else if( wcscmp(name, L"VariableRow") == 0 )
         variableRow = _wtoi(value);
      else if( wcscmp(name, L"FontSize") == 0 )
         fontSize = _wtoi(value);
      else
         return CellBase::SetProperty(name, value);

      return true;
   }

   if( state == lsWidth ) width.push_back(_wtoi(value));
   else if( state == lsHeight ) height.push_back(_wtoi(value));
   else if( state == lsRow ) rows.back().push_back(Cell(value));

   return true;
}

Table::Cell::Borders Table::Cell::ParseBorders(wchar_t *str)
{
   Borders b = None;

   wchar_t *sp, *ep;
   sp = str;
   ep = wcschr(sp, L'|');
   while( true )
   {
      if( ep != NULL ) *ep = L'\0';

      if( wcscmp(sp, L"Left") == 0 ) b = (Borders)(b | Left);
      else if( wcscmp(sp, L"Right") == 0 ) b = (Borders)(b | Right);
      else if( wcscmp(sp, L"Top") == 0 ) b = (Borders)(b | Top);
      else if( wcscmp(sp, L"Bottom") == 0 ) b = (Borders)(b | Bottom);

      if( ep == NULL ) break;
      sp = ep + 1;
      ep = wcschr(sp, L'|');
   }
   return b;
}

Table::Cell::Cell(const wchar_t *params) : border(All)
{
   const wchar_t *ep, *sp = params;
   index = wcstol(sp, (wchar_t**)&ep, 10);
   sp = ep + 1;

   colspan = wcstol(sp, (wchar_t**)&ep, 10);
   sp = ep + 1;

   rowspan = wcstol(sp, (wchar_t**)&ep, 10);
   if( *ep != L'\0' )
   {
      sp = ep + 1;

      // states
      // 0 - норм. текст
      // 1 - был нормальный, тек.символ - кавычка
      // 2 - текст в кавычках
      // 3 - кавычка после текста, ждем следующий символ чтобы пон€ть что это
      // 4 - две кавычки пришли, ждем следующий символ чтобы пон€ть что это
      int state = 0;
      while( *sp )
      {
         if( *sp == ',' && (state == 0 || state == 3 || state == 4) )
            break;
         if( state == 4 )
         {
            text += L"\"";
            state = 0;
         }
         if( *sp == '"' )
         {
            if( state == 0 )
               state = 1;
            else if( state == 1 )
               state = 4;
            else if( state == 2 )
               state = 3;
            else if( state == 3 )
            {
               text += L"\"";
               state = 2;
            }
         } else
         {
            if( state == 1 ) state = 2;
            text += *sp;
         }
         sp++;
      }
      if( *sp !=  L'\0' )
      {
         sp = wcschr(ep+1, L',');
         const wchar_t *mp;
         if( sp != NULL && (mp = wcschr(sp+1, L'-')) != NULL )
         {
            sp++;

            wchar_t *tbuf = _wcsdup(sp);
            tbuf[mp-sp] = L'\0';
            align.SetProperty(L"TextAlign", tbuf);

            border = ParseBorders(tbuf + (mp-sp) + 1);
            
            free(tbuf);
         } else
            align.SetProperty(L"TextAlign", ep+1);
      }
   }
}

int Table::Cell::Width(const std::vector<int> &width)
{
   int w = width[index + colspan - 1];
   if( index != 0 ) w -= width[index-1];

   return w;
}

int Table::Cell::Height(int rowIndex, const std::vector<int> &height)
{
   int h = height[rowIndex + rowspan - 1];
   if( rowIndex != 0 ) h -= height[rowIndex-1];

   return h;
}


static int GetDividerPos(HDC dc, const wchar_t *text, int len, int extent)
{
   SIZE size;
   INT fit, *dx = (INT*)alloca(sizeof(INT) * len);
   const wchar_t dvdr[] = L" .,/-+%*\\";
   const wchar_t dvdrBfore[] = L"\"\'<є#/\\";

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

int Table::Cell::TextHeight(HDC dc, const std::vector<int> &width, FontHolder *fh, int fontSize)
{
   if( text.empty() ) return 0;

   RECT rc = {0};
   rc.right = Width(width);

   HFONT hf = fh->GetFont(fontSize, 0);
   SelectObject(dc, hf);
   std::wstring txt(printText);
   DivideLine(dc, &txt, rc.right);
   DrawTextW(dc, txt.c_str(), txt.size(), &rc, DT_CALCRECT | DT_WORDBREAK);
   return rc.bottom;
}

void Table::Cell::MakeText(IDataSource *source)
{
   MakePrintText(&printText, text, source);
}

void Table::Cell::PaintText(HDC dc, const RECT &bounds, FontHolder *fh, bool multiLine, int fontSize)
{
   if( text.size() == 0 ) return;

   const int XOffset = 5, YOffset = 1;
   HFONT hf = fh->GetFont(fontSize, 0);

   std::wstring txt(printText);
   DivideLine(dc, &txt, bounds.right - bounds.left);
   align.PaintText(dc, hf, txt, bounds, XOffset, YOffset);
}

void Table::BeforePrint(IDataSource *source)
{
   if( !object.empty() && source != NULL )
      rowSource = source->GetObject(object.c_str());
   else
      rowSource = source;

   this->source = source;
}

int Table::RowTextHeight(HDC dc, int rowIndex, FontHolder *fh)
{
   const int YOffset = 6;
   int textHgh = 0;
   Row &row = rows[rowIndex];
   Row::iterator i = row.begin();
   for( ; i != row.end(); i++ )
   {
      i->MakeText(rowSource);
      int hgh = i->TextHeight(dc, width, fh, fontSize);
      if( textHgh < hgh ) textHgh = hgh;
   }

   return textHgh + YOffset;
}

void Table::DrawRaw(HDC dc, int rowIndex, int curY, Row &row, FontHolder *fh, DWORD textHeight)
{
   int h = textHeight;
   Row::iterator cellI = row.begin();

   for( ; cellI != row.end(); cellI++ )
   {
      if(rowIndex != variableRow)
      {
         cellI->MakeText(source);
         h = cellI->Height(rowIndex, height);
      }

      RECT cb;
      cb.left = (cellI->index == 0) ? 0 : width[cellI->index-1]; 
      cb.left += bounds.left;
      cb.top = curY;
      cb.right = cb.left + cellI->Width(width) + 1;
      cb.bottom = curY + h + 1;

      cellI->PaintText(dc, cb, fh, (rowIndex==variableRow), fontSize);

      SelectObject(dc, GetStockObject(NULL_BRUSH));

      if( cellI->border != Cell::All )
      {
         if( (cellI->border & Cell::Left) != 0 )
         {
            MoveToEx(dc, cb.left, cb.top, NULL);
            LineTo(dc, cb.left, cb.bottom-1);
         }
         if( (cellI->border & Cell::Right) != 0 )
         {
            MoveToEx(dc, cb.right-1, cb.top, NULL);
            LineTo(dc, cb.right-1, cb.bottom-1);
         }
         if( (cellI->border & Cell::Top) != 0 )
         {
            MoveToEx(dc, cb.left, cb.top, NULL);
            LineTo(dc, cb.right-1, cb.top);
         }
         if( (cellI->border & Cell::Bottom) != 0 )
         {
            MoveToEx(dc, cb.left, cb.bottom-1, NULL);
            LineTo(dc, cb.right-1, cb.bottom-1);
         }
      } else
         Rectangle(dc, cb.left, cb.top, cb.right, cb.bottom);
   }
}

DWORD Table::DrawRaws(HDC dc, FontHolder *fh, DWORD top, int startRow, int endRow)
{
   if( startRow >= (int)rows.size() || startRow < 0 )
      return top;

   int cr = startRow, curY = top;
   ROWS::iterator i = rows.begin() + startRow;

   for( ; i != rows.end() && cr != endRow; i++, cr++ )
   {
      // draw
      DrawRaw(dc, cr, curY, (*i), fh, 0);

      int dh = height[cr];
      if( cr > 0 ) dh -= height[cr-1];
      curY += dh;
   }

   return curY;
}

DWORD Table::TailHeight()
{
   if( variableRow == -1 || variableRow == rows.size()-1 ) return 0;
   DWORD h = height.back();
   h -= height[variableRow];

   return h;
}

bool Table::DrawVRow(HDC dc, FontHolder *fh, DWORD *top, DWORD *maxHeight, bool checkData)
{
   Row &varRow = rows[variableRow];

   while( true )
   {
      if( checkData && rowSource != NULL && rowSource->HaveMoreData() == false )
         return false;

      DWORD ch = RowTextHeight(dc, variableRow, fh);
      if( ch > *maxHeight )
         return false;

      if( rowSource != NULL ) rowSource->PrintData();
      DrawRaw(dc, variableRow, *top, varRow, fh, ch);

      *top += ch;
      *maxHeight -= ch;

      if( rowSource == NULL || !rowSource->MoveNext() )
         return true;
   }
}

bool Table::Draw(HDC dc, FontHolder *fh, int *shiftY, DWORD *maxY)
{
   const int TopShift = 50;
   // при печати второй и далее страницы - начинаем печать с начала листа + 50 пискелов
   if( (state == tsPrintNextPage) )
      *shiftY -= (bounds.top - TopShift);

   DWORD top = DrawRaws(dc, fh, bounds.top + *shiftY, 0, variableRow);
   if( variableRow == -1 )
   {
      *maxY = top;
      return true;
   }

   DWORD vh = height[variableRow];
   if( variableRow != 0 )
      vh -= height[variableRow - 1];
   vh -= *shiftY;

   if( DrawVRow(dc, fh, &top, &vh, false) ) 
   {
      *shiftY += (top - height[variableRow]);
      if( state != tsPrintNextPage )
         *shiftY -= bounds.top;
      else
         *shiftY -= TopShift;

      *maxY = DrawRaws(dc, fh, top, variableRow + 1, -1);
      return true;
   }

   state = tsPrintNextPage;
   // рисуем до конца страницы
   vh = *maxY - TailHeight() - top;
   DrawVRow(dc, fh, &top, &vh, true);
   *maxY = DrawRaws(dc, fh, top, variableRow + 1, -1);

   return false;
}

//
// ---------------------------- IXMLStreamable -------------------------
//
IXMLStreamable* IXMLStreamable::Create(const SAXAttributes &attributes)
{
   SAXAttributes::const_iterator fnd = attributes.find(L"name");
   if( fnd == attributes.end() ) return NULL;

   const wchar_t *name = fnd->second.c_str();
   int cch = wcslen(name);
   if( cch == 0 ) return NULL;

   if( wcsncmp(name, L"Label", min(sizeof(L"Label")/sizeof(wchar_t) - 1, cch)) == 0 )
      return new TextCell();

   if( wcsncmp(name, L"Line", min(sizeof(L"Line")/sizeof(wchar_t) - 1, cch)) == 0 )
      return new LineCell();

   if( wcsncmp(name, L"Table", min(sizeof(L"Table")/sizeof(wchar_t) - 1, cch)) == 0 )
      return new Table();

   return NULL;
}

/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * RightAlignDrawer
 *
 *  ert   06/08/2010   extract from QTY.cpp
 */
#ifndef __RADRAWER_H
#define __RADRAWER_H

class RightAlignDrawer : public CWindowImpl<RightAlignDrawer>
{
public:
   RightAlignDrawer();
   ~RightAlignDrawer();

   DECLARE_WND_CLASS(L"RADRW")

   BEGIN_MSG_MAP(RightAlignDrawer)
      MESSAGE_HANDLER(WM_PAINT, DoPaint)
   END_MSG_MAP()

   void SetBkColor(COLORREF newValue)  { bkColor = newValue; }

   LRESULT DoPaint(UINT uMsg, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);

   void SetLabels(const wchar_t* line, int width, bool rightSide);

protected:
   void SetCellsBounds(std::vector<int> *widths, int* lineHeight, const std::vector<wchar_t*> &strings, HDC dc);

protected:
   COLORREF bkColor;

   bool rightSide;
   int labelWidth;
   std::vector<wchar_t*> labels;
   wchar_t* labelBuf;
};

#endif
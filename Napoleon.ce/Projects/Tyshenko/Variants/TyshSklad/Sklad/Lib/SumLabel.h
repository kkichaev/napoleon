/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Элемент Сумма
 *
 *  ert   22/08/2007   creating
 */
#ifndef __SUM_LABEL_H
#define __SUM_LABEL_H

#include <atlcrack.h>

class SumLabel : public CWindowImpl<SumLabel, CStatic>
{
public:
   SumLabel(int scale = SUM_SCALE);
   ~SumLabel();

   BEGIN_MSG_MAP(SumLabel)
      MSG_WM_PAINT(Paint)
   END_MSG_MAP()

   static const int STD_OFFSET = 2;
   static const int STD_WIDTH = 80;

   bool CreateLabel(HWND hOwner, int width = STD_WIDTH, int offset = STD_OFFSET);
   void UpdateLayout(int bottomPos = -1);
   void SetSum(long sum, long paySum = 0, bool hideRest = false);

protected:
   void Paint(HDC dc);
   void UpdatePosition(const wchar_t *txt);

protected:
   int offset;
   int scale;
   HFONT newFont;
};


#endif

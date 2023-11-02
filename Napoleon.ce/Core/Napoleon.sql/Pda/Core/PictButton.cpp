/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Кнопка с картинокой на диалоге
 *
 *  ert   09/06/2009   creating
 */
#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <atlwince.h>

#include "Preference.h"
//#include <NapoleonRes.h>

#include "PictButton.h"
#include <StdFuncs.h>

PictButton::PictButton(int id)
{
   hbmp = LoadBitmap(_Module.GetResourceInstance(), MAKEINTRESOURCE(id));
}

PictButton::~PictButton()
{
   DeleteObject(hbmp);
}

LRESULT PictButton::Draw(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   DRAWITEMSTRUCT *ds = (DRAWITEMSTRUCT*)lParam;
   CRect rc;
   GetWindowRect(rc);

   if( ds->itemState & ODS_FOCUS || ds->itemState & ODS_SELECTED )
   {
      SelectObject(ds->hDC, GetStockObject(WHITE_BRUSH));
      Rectangle(ds->hDC, rc.left, rc.top, rc.Width(), rc.Height());
   } else
      FillRect(ds->hDC, rc, (HBRUSH)GetStockObject(WHITE_BRUSH));

   PaintScale(ds->hDC, hbmp, rc.Width(), rc.Height());

   return 0;
}

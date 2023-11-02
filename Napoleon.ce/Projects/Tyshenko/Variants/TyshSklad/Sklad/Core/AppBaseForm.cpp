/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Napoleon Logistic MainForm
*
*  ert   03/09/2010   creating
*/
#include "stdafx.h"

#include "AppBaseForm.h"
#include <Preference.h>

BOOL CALLBACK SetChildFont(HWND hWnd, LPARAM lParam)
{
   CFont *fnt = (CFont*)lParam;
   SendMessage(hWnd, WM_SETFONT, (WPARAM)((HFONT)*fnt), 0);
   return FALSE;
}

bool CreateFont(CFont* font, int fontSize, bool bold)
{
   LOGFONT lf = {0};
   lf.lfHeight = fontSize;
   lf.lfWeight = (bold) ? FW_BOLD : FW_NORMAL;
   lf.lfCharSet = ANSI_CHARSET;
   lf.lfPitchAndFamily = DEFAULT_PITCH;
   wcscpy(lf.lfFaceName, L"Tahoma"); //L"Arial");

   return (font->CreateFontIndirect(&lf) != NULL);
}

void UpdateChildFont(HWND hWnd, CFont* font)
{
   //Preference p;
   //p.Load();

   //int fontSize = 6 * p.fontSize + 12;
   //CreateFont(font, fontSize, true);
   //EnumChildWindows(hWnd, SetChildFont, (LPARAM)font);
}

AppBaseForm::~AppBaseForm()
{
}

void AppBaseForm::SetFontToChild(int fontSize, bool bold)
{
   CreateFont(&font, fontSize, bold);
   EnumChildWindows(m_hWnd, SetChildFont, (LPARAM)&font);
}

LRESULT AppBaseForm::GetStaticBrush(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   if( back.IsNull() )
      back.CreateSolidBrush(GetSysColor(COLOR_WINDOW));

   return (LRESULT)(HBRUSH)(back);
}

int AppBaseForm::MessageBox(int text, int caption, UINT type)
{
   std::wstring txt, title;
   _Module.LoadString(&txt, text);
   _Module.LoadString(&title, caption);
   return BaseForm::MessageBox(txt.c_str(), title.c_str(), type);
}


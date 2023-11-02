/*
* Copyright (C), 2007, Денис Мосягин
*
* Класс для создания единственного приложения на КПК
* 
*  ert   07/03/2008  creating
*/

#ifndef TOP_APP_IMPL_H
#define TOP_APP_IMPL_H

class TopApp
{
public:
   static void Start(HWND hMain);

   static void MakeAutorun();
   static void RemoveAutorun();

   static void ChangeOrientation(bool landscape);
   static void RestoreOrientation();
   
   // может быть вызвана несколько раз, без вызова Start
   static void Stop();

   static void EnableDoneButton(bool enable);

protected:
   static LRESULT CALLBACK TaskWndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam);
   static LRESULT CALLBACK MainWndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam);

   //
   // оба буйера MAX_PATH
   //
   static void MakeShortcutName(wchar_t *shortcut, wchar_t *fileName);

protected:   
   static WNDPROC taskWndProc, mainWndProc;
   static HWND taskHWnd, mainWnd;

   static bool enableDoneButton;
   static RECT doneBounds;

   static bool hooked;

   static DWORD saveOrientation;
};

#endif

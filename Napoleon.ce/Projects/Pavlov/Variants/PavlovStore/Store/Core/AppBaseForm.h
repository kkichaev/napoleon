/*
* Copyright (C), 2007 - 2013, Денис Мосягин
*
* Napoleon Logistic MainForm
*
*  ert   10/04/2013   creating
*/
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <BaseForm.h>

#include <atlgdi.h>

class AppBaseForm : public BaseForm
{
public:
   virtual ~AppBaseForm();

   BEGIN_MSG_MAP(AppBaseForm)
      MESSAGE_HANDLER(WM_CTLCOLORSTATIC, GetStaticBrush)
      CHAIN_MSG_MAP(BaseForm)
   END_MSG_MAP()

public:
   void SetFontToChild(int fontSize, bool bold=false);
   LRESULT GetStaticBrush(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);

   int MessageBox(int text, int caption, UINT type=0);

protected:
   CFont font;
   CBrush back;
};

BOOL CALLBACK SetChildFont(HWND hWnd, LPARAM lParam);
bool CreateFont(CFont* font, int fontSize, bool bold);
void UpdateChildFont(HWND hWnd, CFont* font);


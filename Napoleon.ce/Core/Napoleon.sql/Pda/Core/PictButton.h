/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Кнопка с картинокой на диалоге
 *
 *  ert   09/06/2009   creating
 */

class PictButton : public CWindowImpl<PictButton, CButton>
{
public:
   PictButton(int id);
   ~PictButton();

   DECLARE_WND_CLASS(L"PICTBTTN")

   BEGIN_MSG_MAP(PictButton)
      MESSAGE_HANDLER(OCM_DRAWITEM, Draw)
   END_MSG_MAP()

   LRESULT Draw(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   HBITMAP hbmp;
};


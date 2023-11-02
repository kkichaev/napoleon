/*
* Copyright (C), 2007, Денис Мосягин
*
* Static Anchor
*
*  ert   05/03/2008   creating
*/
#ifndef _S_ANCHOR_H
#define _S_ANCHOR_H

class StaticAnchor : public CWindowImpl<StaticAnchor>
{
public:
   struct IClickHandler
   {
      virtual void Click(void* source) = 0;
   };

   StaticAnchor() : drawFlags(0)
   {
   }

   DECLARE_WND_CLASS(L"ANCHR")

   BEGIN_MSG_MAP(StaticAnchor)
      MESSAGE_HANDLER(WM_PAINT, DoPaint)
      MESSAGE_HANDLER(OCM_COMMAND, OnClick)
   END_MSG_MAP()

   LRESULT DoPaint(UINT uMsg, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
   LRESULT OnClick(UINT uMsg, WPARAM wParam, LPARAM /*lParam*/, BOOL& /*bHandled*/);

   void DrawFlags(DWORD flags)
   {
      drawFlags = flags;
   }

   void SetClickHandler(IClickHandler* handler) { handlers.push_back(handler); }
   void RemoveClickHandler(IClickHandler* handler);

protected:
   DWORD drawFlags;

   std::vector<IClickHandler*> handlers;
};


#endif

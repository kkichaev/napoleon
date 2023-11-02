/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * Список задач
 *
 *  ert   09/08/2010   creating
 */ 
#ifndef __TASK_LIST_H
#define __TASK_LIST_H

//
// сейчас использует IDD_TASK_OK_BMP (AddRes.h) для отрисовки галочки
//
class TaskList : public CWindowImpl<TaskList, CListBox>
{
public:
   struct Item
   {
      enum Flags { Checked = 1, Disabled = 2 };

      const wchar_t *text;
      int id;
      WORD flags;
   };

   TaskList(int id);
   ~TaskList();

   void UpdateLayout();

   DECLARE_WND_CLASS(L"NPLTASK_LIST")

   BEGIN_MSG_MAP(TaskList)
      MESSAGE_HANDLER(OCM_DRAWITEM, DrawItem)
   END_MSG_MAP()

protected:
   LRESULT DrawItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

   HFONT hFont;
   int CtrlID;
};

#endif

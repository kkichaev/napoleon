/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Базовая форма
 *
 *  ert   13/08/2007   creating
 */ 
#ifndef __BASE_FORM_H
#define __BASE_FORM_H

#include <Form.h>
#include <atlwince.h>

#include <map>

//
// Все формы порождены от этого класса
//
class BaseForm : public CWindowImpl<BaseForm>, public IForm
{
public:
   BaseForm() {}

   bool Load(HWND hParent);

   virtual void Destroy();

   virtual void WriteChanges() {}

   // Resource ID для формы
   virtual DWORD GetResourceID() const { return GetID(); }

   // Shortcut Menu для формы
   virtual DWORD GetMenuID() const { return GetID(); }

   // Menu bar ID
   virtual DWORD GetMenuBarID() const { return GetID(); }

   // перечитать данные
   virtual void Refresh() {}
   //изменение размера формы (вертикальный/горизонтальный экран)
   virtual void UpdateLayout(const RECT& bounds, bool forceRecalc) {}

   typedef CWindowImpl<BaseForm> BaseClass;
   DECLARE_WND_CLASS(L"NPLFORM")

   static WORD screenWidth;

   BEGIN_MSG_MAP(BaseForm)
   END_MSG_MAP()
};

extern std::map<DWORD, IFrame::FormCreator> formCreatorMap;


#endif

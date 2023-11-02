/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Интерфесы форм и фрейма
 *
 *  ert   13/08/2007   creating
 */
#ifndef _I_FORM_H
#define _I_FORM_H

struct IFrame;
struct IForm;
struct IProgressIndicator;

// данные формы
struct IFormData
{
   virtual ~IFormData() {}
};

struct IForm
{
   virtual ~IForm() {}

   // форма только использует данные, но не удаляет их
   // вызываетс один раз при загрузке формы
   virtual bool SetData(IFormData *data) = 0;
   virtual DWORD GetID() const = 0;
   virtual void Destroy() = 0;
   virtual void WriteChanges() = 0;
};

struct IFrame
{
   virtual ~IFrame() {}

   virtual bool Load(DWORD formID, IFormData *data) = 0;
   virtual HWND LoadMenuBar(DWORD barID, DWORD barV5 = 0, DWORD flags = SHCMBF_HIDESIPBUTTON) = 0;

   virtual void Quit() = 0;
   virtual void SetTitle(const wchar_t *title) = 0;

   virtual void CameraActive(bool active) = 0;

   typedef IForm* (*FormCreator)();

   static FormCreator GetFormCreator(DWORD id);
   static void RegisterFormCreator(DWORD id, FormCreator creator);
};

#include <TypeHolder.h> // class __RegisterHelper

#define DECLARE_FORM(_class, id) \
   enum __Form_ID { __FORM_ID_ = id }; \
   virtual DWORD GetID() const { return __FORM_ID_; } \
   static IForm* Creator() { return new _class(); }

#define IMPLEMENT_FORM(_class) \
   static void RegisterForm ## _class() { IFrame::RegisterFormCreator( _class::__FORM_ID_, _class::Creator );  } \
   static __RegisterHelper r ## _class(RegisterForm ## _class);


#endif

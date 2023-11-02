/*
 * Copyright (C), 2010 - 2013, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   15/11/2013   creating
 */

using System.Windows.Forms;
namespace GRSoft.NapoleonAdmin
{
   class FormEntries
   {
      internal static System.Type GetObjectType(System.Type baseType)
      {
         return baseType;
      }

      internal static IFormDecorator GetFormDecorator(System.Type formType)
      {
         return new EmptyDecorator();
      }
   }
}
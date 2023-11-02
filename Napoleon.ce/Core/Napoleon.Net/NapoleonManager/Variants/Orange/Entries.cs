/*
 * Copyright (C), 2010 - 2011, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   28/03/2011   creating
 */
namespace GRSoft.NapoleonManager
{
   class FormEntries
   {
      internal static DivisionForm OpenDivisionForm()
      {
         return new DivisionForm();
      }

      internal static FmDetail OpenDetailForm(FmDetailData data)
      {
         return new FmDetail(data);
      }

      internal static UserForm OpenUserForm(Divisions owner)
      {
         return new UserFormEx(owner);
      }

      internal static System.Type GetFormType(System.Type baseType)
      {
         return baseType;
      }
   }
}
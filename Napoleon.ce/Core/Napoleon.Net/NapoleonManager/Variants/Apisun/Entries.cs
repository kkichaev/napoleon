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
         return new DivisionFormEx();
      }

      internal static FmDetail OpenDetailForm(FmDetailData data)
      {
         return new FmDetailEx(data);
      }

      internal static UserForm OpenUserForm(Divisions owner)
      {
         return new UserFormEx(owner);
      }

      internal static FmCensus OpenCensusForm()
      {
         return new FmCensus();
      }

      internal static System.Type GetFormType(System.Type baseType)
      {
         if (baseType == typeof(FmPrice))
            return typeof(FmPriceEx);

         if (baseType == typeof(MainForm))
            return typeof(MainFormEx);

         if (baseType == typeof(DivisionSummary))
            return typeof(DivisionSummaryEx);

         return baseType;
      }
   }
}
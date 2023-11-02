/*
 * Copyright (C), 2010 - 2011, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   28/03/2011   creating
 */
using System;
using System.Collections.Generic;
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
         if (baseType == typeof(MainForm))
            return typeof(MainFormEx);
         if (baseType == typeof(Divisions))
            return typeof(DivisionsEx);
         if (baseType == typeof(FmReports))
            return typeof(FmReportsEx);
         if (baseType == typeof(DivisionSummary))
            return typeof(DivisionSummaryEx);
         if (baseType == typeof(FmOrgRadiusDocs))
            return typeof(FmOrgRadiusDocsEx);
         return baseType;
      }
   }
}
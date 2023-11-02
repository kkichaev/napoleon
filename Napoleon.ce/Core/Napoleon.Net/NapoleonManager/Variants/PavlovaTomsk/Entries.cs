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
         return new UserForm(owner);
      }

      internal static FmCensus OpenCensusForm()
      {
         return new FmCensusEx();
      }

      internal static System.Type GetFormType(System.Type baseType)
      {
         if (baseType == typeof(MainForm))
            return typeof(MainFormEx);
         if (baseType == typeof(FmGPSReport))
            return typeof(FmGPSReportEx);
         if (baseType == typeof(Route))
            return typeof(RouteEx);
         if (baseType == typeof(SummaryData))
            return typeof(SummaryDataEx);
         if (baseType == typeof(SummaryDivisionData))
            return typeof(SummaryDivisionDataEx);
         if (baseType == typeof(Divisions))
            return typeof(DivisionsEx);
         if (baseType == typeof(FmReports))
            return typeof(FmReportsEx);
         return baseType;
      }
   }
}
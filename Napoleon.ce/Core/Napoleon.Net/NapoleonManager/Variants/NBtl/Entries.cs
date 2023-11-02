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
         if (baseType == typeof(MainForm))
            return typeof(MainFormEx);

         if (baseType == typeof(Divisions))
            return typeof(DivisionsEx);

         if (baseType == typeof(FmScriptEdit))
            return typeof(FmScriptEditEx);

         if (baseType == typeof(FmCoverArea))
            return typeof(FmCoverAreaEx);

         if (baseType == typeof(QuestionExelReport))
            return typeof(QuestionExcelReportEx);

         if (baseType == typeof(Route))
            return typeof(FmRouteAssign);

#if NBtlGoldenShelf
         if (baseType == typeof(FmSlsnet))
            return typeof(FmSlsnetEx);
#endif

         if (baseType == typeof(FmExportPhoto))
            return typeof(FmExportPhotoNBTL);

         if (baseType == typeof(EdBoolean))
            return typeof(EdBooleanEx);

         if (baseType == typeof(FmQuestionReport))
            return typeof(FmQuestionReportEx);

         if (baseType == typeof(FmReports))
            return typeof(FmReportsEx);

#if NbtlMonitor
         if (baseType == typeof(SummaryData))
            return typeof(SummaryDataMon);
#endif

         if (baseType == typeof(FmQuestItemEdit))
            return typeof(FmQuestItemEditEx);

         if (baseType == typeof(FmQuestEdit))
            return typeof(FmQuestEditEx);

         return baseType;
      }
   }
}
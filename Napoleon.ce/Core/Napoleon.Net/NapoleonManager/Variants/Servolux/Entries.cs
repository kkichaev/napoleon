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
         return new FmDetailEx(data);
      }

      internal static UserForm OpenUserForm(Divisions owner)
      {
         return new UserForm(owner);
      }

      internal static FmCensus OpenCensusForm()
      {
         return new FmCensus();
      }

      internal static System.Type GetFormType(System.Type baseType)
      {
         if (baseType == typeof(FmOrdersReport))
            return typeof(FmAgentOrderReport);

         if (baseType == typeof(MainForm))
            return typeof(MainFormEx);

         if (baseType == typeof(Divisions))
            return typeof(DivisionsEx);

         if (baseType == typeof(FmScriptDesigner))
            return typeof(FmScriptDesignerEx);

         if (baseType == typeof(FmScriptEdit))
            return typeof(FmScriptEditEx);

         if (baseType == typeof(ScriptOverview))
            return typeof(ScriptOverviewEx);

         if (baseType == typeof(SummaryData))
            return typeof(SummaryDataEx);

         if (baseType == typeof(FmReports))
            return typeof(FmReportsEx);
         if (baseType == typeof(FmExportPhoto))
            return typeof(FmExportPhotoEx);
         return baseType;
      }
   }
}
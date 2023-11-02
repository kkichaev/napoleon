/*
 * Copyright (C), 2010, Гильдия разработчиков
 *
 * Фабрика для генерации отчетов
 * 
 * kki   30/09/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Reports.Excel;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager.Reports
{
   interface IReport
   {
      void Show();
      void Build();
   }

   interface IReportImplementation
   {
      void Show();
      void Build(ReportData data);
   }

   interface IReportFactory
   {
      IReport MakeRouteReport(DataSet<int, Schedule> dsOrgFolder, List<Division.DivisionAgent> agents,
         Type reportType);
   }

   class ExcelReportFactory : IReportFactory
   {
      private static ExcelReportFactory instance = null;

      public static ExcelReportFactory CreateFactory() 
      {
         if (instance == null)
            instance = new ExcelReportFactory();

         return instance;
      }

      private ExcelReportFactory() { }

      #region IReportFactory Members

      public IReport MakeRouteReport(DataSet<int, Schedule> dsOrgFolder, List<Division.DivisionAgent> agents,
         Type reportType)
      {
         return (IReport)Activator.CreateInstance(reportType, dsOrgFolder, agents);
      }

      #endregion
   }
}

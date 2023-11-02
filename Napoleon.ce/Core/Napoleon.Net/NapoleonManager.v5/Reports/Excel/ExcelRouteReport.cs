/*
 * Copyright (C), 2010, Гильдия разработчиков
 *
 * Отчет в Excele о маршруте агента
 * 
 * kki   30/09/2010   creating
 */

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager.Reports.Excel
{
   class ExcelRouteReport : Excel, IReport
   {
      private DataSet<int, RouteTemplate> dsOrgFolder;
      private List<Division.DivisionAgent> agents;

      public ExcelRouteReport(DataSet<int, RouteTemplate> dsOrgFolder,
         List<Division.DivisionAgent> agents)
      {
         this.dsOrgFolder = dsOrgFolder;
         this.agents = agents;
      }

      protected DataSet<int, RouteTemplate> OrgFolderDataSet
      {
         get { return dsOrgFolder; }
      }

      protected List<Division.DivisionAgent> AgentsList
      {
         get { return agents; }
      }

      #region IReport Members

      public virtual void Show()
      {
         Visible = true;
      }

      #endregion

      #region IReport Members


      public void Build()
      {
         throw new Exception("The method or operation is not implemented.");
      }

      #endregion
   }
}

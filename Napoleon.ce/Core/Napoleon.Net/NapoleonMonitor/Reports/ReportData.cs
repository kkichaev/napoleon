/*
 * Copyright (C), 2010, Гильдия разработчиков
 *
 * Абстрактный клас содержащий данные для отчета
 * 
 * kki   19/03/2011   creating
 */

using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager.Reports
{
   abstract class ReportData
   {
   }

   class DivisionItem
   {
      private Division division;

      public DivisionItem(Division division)
      {
         this.division = division;
      }

      public List<Division.DivisionAgent> Agents { get { return division.GetAllAgents(); } }
      public override string ToString()
      {
         return division.name;
      }
   }
}

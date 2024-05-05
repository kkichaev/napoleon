/*
 * Copyright (C), 2010 - 2011, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   28/03/2011   creating
 */
using System.Windows.Forms;
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
         return new FmDetail(data);
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
         if (baseType == typeof(FmStopOrgList))
            return typeof(FmStopEx);
         if (baseType == typeof(FmReports))
            return typeof(FmReportsEx);
         return baseType;
      }
   }

   class FmStopEx : FmStopOrgList
   {
      DataGridViewTextBoxColumn clmnBalance;
      public FmStopEx()
         : base()
      {
         clmnBalance = new DataGridViewTextBoxColumn();
         //clmnBalance.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmnBalance.DataPropertyName = "Balance";
         clmnBalance.FillWeight = 100F;
         clmnBalance.HeaderText = "Долг";
         clmnBalance.Name = "clmnBalance";

         dgvOrgs.Columns.Add(clmnBalance);
      }
   }
}
using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      DataGridViewTextBoxColumn clmnIncassSum = new DataGridViewTextBoxColumn();
      public MainFormEx()
      {
         tgvAgentsSummarySum.HeaderText = "Сумма заказов";

         clmnIncassSum.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmnIncassSum.DataPropertyName = "IncassSum";
         clmnIncassSum.DefaultCellStyle.Format = "C2";
         clmnIncassSum.FillWeight = 55.3934F;
         clmnIncassSum.HeaderText = "Сумма инкассации";
         clmnIncassSum.Name = "clmnIncassSum.";
         clmnIncassSum.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;

         tgvAgentsSummary.Columns.Insert(tgvAgentsSummarySum.Index+1, clmnIncassSum);
         tgvAgentsSummary.ColumnHeadersHeight = 44;
      }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig);
      }
   }

   class DivisionSummaryEx : DivisionSummary
   {
      public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig) : base(dsConfig) { }

      protected override SummaryDivisionData CreateSummaryDivisionData(Division d)
      {
         return new SummaryDivisionDataEx(d);
      }

      protected override SummaryData CreateSummaryData(Agent agent, DataSet<int, CommonConfig> config)
      {
         return new SummaryDataEx(agent, config);
      }
   }

   class SummaryDataEx : SummaryData
   {
      public double incassSum = 0;

      public SummaryDataEx(Agent a, DataSet<int, CommonConfig> config) : base(a, config) { }

      public double IncassSum { get { return incassSum; } }

      public override void Add(Incass o)
      {
         pko++;
         incassSum += o.Sum();
         AddOrg(o);
      }

      public override void Add(PKO o)
      {
         pko++;
         incassSum += o.Sum();
         AddOrg(o);
      }
   }

   class SummaryDivisionDataEx : SummaryDivisionData
   {
      double incassSum = 0;

      public SummaryDivisionDataEx(Division d) : base(d) { }

      internal override void AddChildDivision(SummaryDivisionData chData)
      {
         base.AddChildDivision(chData);
         incassSum += ((SummaryDivisionDataEx)chData).incassSum;
      }

      public override void Add(SummaryData data)
      {
         base.Add(data);
         incassSum += ((SummaryDataEx)data).incassSum;
      }

      public double IncassSum { get { return incassSum; } }
   }
}
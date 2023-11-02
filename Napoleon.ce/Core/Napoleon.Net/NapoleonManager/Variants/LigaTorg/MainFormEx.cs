using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public MainFormEx()
      {
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
      public int ord = 0;
      public int bank = 0;
      public int visit = 0;
      
      public SummaryDataEx(Agent a, DataSet<int, CommonConfig> config) : base(a, config) { }

      public double AvgOrder { get { return ord == 0 ? 0 : sum / ord; } }

      public override void Add(Order o)
      {
         base.Add(o);

         if( o.Sum() != 0 )
            ord++;
         else
            bank++;
      }

      public override void Add(VisitInfo doc)
      {
         base.Add(doc);
         visit++;
      }

      public override int GetVisitCount()
      {
         return orders + visit;
      }
   }

   class SummaryDivisionDataEx : SummaryDivisionData
   {
      public int ord = 0;
      public int bank = 0;

      public SummaryDivisionDataEx(Division d) : base(d) { }

      internal override void AddChildDivision(SummaryDivisionData chData)
      {
         base.AddChildDivision(chData);

         SummaryDivisionDataEx sde = (SummaryDivisionDataEx)chData;
         ord += sde.ord;
         bank += sde.bank;
      }

      public override void Add(SummaryData data)
      {
         base.Add(data);

         SummaryDataEx sde = (SummaryDataEx)data;
         ord += sde.ord;
         bank += sde.bank;
      }

      public double AvgOrder { get { return ord == 0 ? 0 : sum / ord; } }
   }
}
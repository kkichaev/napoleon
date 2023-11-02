using GRSoft.Network;
using GRSoft.UILib;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public MainFormEx()
      {
         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.abiword_3;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "mtxtimw";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Oтчёт по плану на день";
         button.Click += new System.EventHandler((s, e) => { (new FmDaliyPlanReport()).Show(); });

         tsbConfig.Items.Add(button);

      }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig);
      }

  }
   class DivisionSummaryEx : DivisionSummary
   {
      public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig) : base(dsConfig) { }

      //protected override SummaryDivisionData CreateSummaryDivisionData(Division d)
      //{
      //   return new SummaryDivisionDataEx(d);
      //}

      protected override SummaryData CreateSummaryData(Agent agent, DataSet<int, CommonConfig> config)
      {
         return new SummaryDataEx(agent, config);
      }
   }

   class SummaryDataEx : SummaryData
   {
      Dictionary<string, int> uniqVisits = new Dictionary<string, int>();

      public SummaryDataEx(Agent agent, DataSet<int, CommonConfig> config)
         : base(agent, config)
      {

      }

      public override void Add(VisitInfo doc)
      {
         base.Add(doc);
         String k = MakeDateIdOrgString(doc.id, doc.Date);
         uniqVisits[k] = 1;
      }

      protected override bool IsUniqueOrgsListContaintsOrgId(DateTime d, string orgID)
      {
         String k = MakeDateIdOrgString(orgID, d);
         return uniqVisits.ContainsKey(k);
      }

      public override int GetVisitCount()
      {
         return uniqVisits.Count;
      }
   }
}

using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Reflection;
using System.ComponentModel;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      SimpleDataSet<Layout> dsLayouts = new SimpleDataSet<Layout>(GRSoft.NapoleonManager.Layout.OBJECT_NAME);

      DataGridViewTextBoxColumn clmnLayout = new DataGridViewTextBoxColumn();
      public MainFormEx()
      {
         tgvAgentsSummarySum.HeaderText = "Сумма заказов";

         clmnLayout.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmnLayout.DataPropertyName = "LayoutCount";
         clmnLayout.FillWeight = 55.3934F;
         clmnLayout.HeaderText = "Выкладок";
         clmnLayout.Name = "clmnLayoutCount";
         clmnLayout.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;

         tgvAgentsSummary.Columns.Insert(Column1.Index+1, clmnLayout);
         tgvAgentsSummary.ColumnHeadersHeight = 44;

         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.statoper;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "statoper";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Статистика по работе операторов";
         button.Click += new System.EventHandler((s, e) => { new FmStatOperRpt().Show(); });


         tsbConfig.Items.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.spktask;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "spktaskrep";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Отчет по задачам";
         button.Click += new System.EventHandler((s, e) => { new FmSPKTaskRpt().Show(); });

         tsbConfig.Items.Add(button);

         DBConnection.TIMEOUT = 10 * 60 * 1000;
      }

      protected override void OnQuestReportPressed(object sender, EventArgs args)
      {
         new FmManagerQuestRep().Show();
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         updSets.Add(dsLayouts);
      }

      protected override void AdjustFilterForDS(DateTime dateBegin, DateTime dateEnd)
      {
         base.AdjustFilterForDS(dateBegin, dateEnd);
         dsLayouts.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd);
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

      protected override void PostAddData()
      {
         IDataSet cdata = DataModule.Get(Layout.OBJECT_NAME);
         if (cdata != null)
            foreach (Layout doc in cdata.Data)
            {
               if (ContainsKey(doc.AgentID))
               {
                  SummaryDataEx sd = (SummaryDataEx)this[doc.AgentID];
                  sd.Add(doc);
               }
            }
      }
   }

   class SummaryDataEx : SummaryData
   {
      public int layoutCount = 0;

      public SummaryDataEx(Agent a, DataSet<int, CommonConfig> config) : base(a, config) { }

      public int LayoutCount { get { return layoutCount; } }

      public void Add(Layout doc)
      {
         if(!doc.IsEmpty)
         {
            layoutCount++;
            AddOrg(doc);
         }
      }
   }

   class SummaryDivisionDataEx : SummaryDivisionData
   {
      int layoutCount = 0;

      public SummaryDivisionDataEx(Division d) : base(d) { }

      internal override void AddChildDivision(SummaryDivisionData chData)
      {
         base.AddChildDivision(chData);
         layoutCount += ((SummaryDivisionDataEx)chData).layoutCount;
      }

      public override void Add(SummaryData data)
      {
         base.Add(data);
         layoutCount += ((SummaryDataEx)data).layoutCount;
      }

      public int LayoutCount { get { return layoutCount; } }
   }
}
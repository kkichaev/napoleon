using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.IO;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      DataSet<string, DocsTotalResult> totals = new DataSet<string, DocsTotalResult>(DocsTotalResult.OBJECT_NAME, false);
      public MainFormEx()
      {
         tgvAgentsSummarySum.HeaderText = "Сумма заказов";

         int index = tgvAgentsSummarySum.Index + 1;
         DataGridViewTextBoxColumn clmn;

         clmn = new DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "Dlv1C";
         clmn.DefaultCellStyle.Format = "C2";
         clmn.FillWeight = 40F;
         clmn.HeaderText = "Накл.1с";
         clmn.Name = "clmn.";
         clmn.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         tgvAgentsSummary.Columns.Insert(index++, clmn);

         clmn = new DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "IncassSum";
         clmn.DefaultCellStyle.Format = "C2";
         clmn.FillWeight = 40F;
         clmn.HeaderText = "Сумма ПКО";
         clmn.Name = "clmn.";
         clmn.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         tgvAgentsSummary.Columns.Insert(index++, clmn);

         clmn = new DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
         clmn.DataPropertyName = "Incass1C";
         clmn.DefaultCellStyle.Format = "C2";
         clmn.FillWeight = 40F;
         clmn.HeaderText = "ПКО 1С";
         clmn.Name = "clmn.";
         clmn.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.NotSortable;
         tgvAgentsSummary.Columns.Insert(index++, clmn);

         tgvAgentsSummary.ColumnHeadersHeight = 44;
         tgvAgentsSummaryCount.DataPropertyName = "UniqOrders";

         ToolStripButton rttReport;
         rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.actgs;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttReport";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Реализация посещений";
         rttReport.Click += new System.EventHandler((o, e) =>
         {
            if (CheckIsMainDataPresents(true))
               new VisitQualityReport().Show();
            else
               MessageBox.Show("Необходимо нажать кнопку обновить");
         });
         tsbConfig.Items.Add(rttReport);

         rttReport = new System.Windows.Forms.ToolStripButton();
         rttReport.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         rttReport.Image = Properties.Resources.help;
         rttReport.ImageTransparentColor = System.Drawing.Color.Magenta;
         rttReport.Name = "rttHelp";
         rttReport.Size = new System.Drawing.Size(23, 22);
         rttReport.Text = "Справка";
         rttReport.Alignment = ToolStripItemAlignment.Right;
         rttReport.Click += new System.EventHandler((o, e) =>
         {
            string helpFile = Application.StartupPath + "\\NapoleonManager.doc";
            if(!File.Exists(helpFile))
            {
               helpFile = Application.StartupPath + "\\NapoleonManager.docx";
               if( !File.Exists(helpFile) )
               {
                  MessageBox.Show("Нет файла справки");
                  return;
               }
            }
            OpenLink.OpenFile(helpFile);
         });
         tsbConfig.Items.Insert(tsbConfig.Items.Count, rttReport);

         Width += 90;
         cbConfig.Left -= 24;
      }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig, totals);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         GetDocsReportParam param = new GetDocsReportParam();
         param.start = dtpBeginDate.Value.Date;
         param.end = GetRangeEndDate();
         param.detailed = 0;

         Manager m = CurrentUser.user as Manager;
         if (m != null)
            foreach (Agent a in m.GetAgents().Data)
               param.users.Add(a);

         Report rpt = new Report("get_docs", param, totals);
         updSets.Add(rpt);
      }

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();
      }
   }

   class DivisionSummaryEx : DivisionSummary
   {
      DataSet<string, DocsTotalResult> totals;
      public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig, DataSet<string, DocsTotalResult> totals) : base(dsConfig) 
      {
         this.totals = totals;
      }

      protected override SummaryDivisionData CreateSummaryDivisionData(Division d)
      {
         return new SummaryDivisionDataEx(d);
      }

      protected override SummaryData CreateSummaryData(Agent agent, DataSet<int, CommonConfig> config)
      {
         return new SummaryDataEx(agent, config, totals);
      }
   }

   class SummaryDataEx : SummaryData
   {
      double incassSum = 0;
      double incass1C = 0;
      double delivery1C = 0;

      public SummaryDataEx(Agent a, DataSet<int, CommonConfig> config, DataSet<string, DocsTotalResult> totals) : base(a, config)
      {
         if( totals.ContainsKey(a.id))
         {
            incass1C = totals[a.id].incass;
            delivery1C = totals[a.id].deliveries;
         }
      }

      public double IncassSum { get { return incassSum; } }
      public double Incass1C { get { return incass1C; } }
      public double Dlv1C { get { return delivery1C; } }

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

      public override void Add(Order o)
      {
         base.Add(o);
         if (o.incass > 0)
            incassSum += o.incass;
      }

      public override void CountProgress(DateTime start, DateTime end, DataSet<int, OrgFolder> dsOrgFolder)
      {
         int dayCount = 0;
         double pc = 0;
         DateTime day = start;

         do
         {
            List<OrgFolderItem> items = GetAgentRoute(day, dsOrgFolder.Data);

            //Проверяем присутсвтует или маршрут на выбранный день,
            //если маршрута нет, то этот день мы просто не считаем для среднего
            if (items != null && items.Count != 0)
            {
               BeforeProgressCount(day, items);

               int count = 0;
               foreach (OrgFolderItem oi in items)
               {
                  string key = MakeOrderKey(oi.name, day);
                  if (uniqOrder.ContainsKey(key))
                     count++;
               }

               pc += (double)count / (double)items.Count;
               dayCount++;
            }

            day = day.AddDays(1);
         } while (day < end);

         plan = dayCount == 0 ? 0 : pc / (double)dayCount * 100;
      }
   }

   class SummaryDivisionDataEx : SummaryDivisionData
   {
      double incassSum = 0;
      double incass1C = 0;
      double delivery1C = 0;

      public SummaryDivisionDataEx(Division d) : base(d) { }

      internal override void AddChildDivision(SummaryDivisionData chData)
      {
         base.AddChildDivision(chData);

         SummaryDivisionDataEx sde = (SummaryDivisionDataEx)chData;

         incassSum += sde.IncassSum;
         incass1C += sde.Incass1C;
         delivery1C += sde.Dlv1C;
      }

      public override void Add(SummaryData data)
      {
         base.Add(data);
         SummaryDataEx sde = (SummaryDataEx)data;
         incassSum += sde.IncassSum;
         incass1C += sde.Incass1C;
         delivery1C += sde.Dlv1C;
      }

      public double IncassSum { get { return incassSum; } }
      public double Incass1C { get { return incass1C; } }
      public double Dlv1C { get { return delivery1C; } }
   }
}
using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Drawing;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      DataGridViewImageColumn clmnProgress;
      DataGridViewTextBoxColumn clmnWeight;
      public MainFormEx()
      {
       
         clmnProgress = new DataGridViewImageColumn();

         clmnProgress.DataPropertyName = "ProgressImage2";
         
         DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
         dataGridViewCellStyle2.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
         dataGridViewCellStyle2.NullValue = null;
         dataGridViewCellStyle2.Padding = new System.Windows.Forms.Padding(2, 3, 2, 3);
         clmnProgress.DefaultCellStyle = dataGridViewCellStyle2;
         clmnProgress.FillWeight = 55.3934F;
         clmnProgress.HeaderText = "% заявок";
         clmnProgress.Name = "clmnPropgress";
         clmnProgress.Resizable = System.Windows.Forms.DataGridViewTriState.False;

         tgvAgentsSummary.Columns.Add(clmnProgress);

         clmnWeight = new DataGridViewTextBoxColumn();
         clmnWeight.DataPropertyName = "Weight";
         clmnWeight.HeaderText = "Вес";
         clmnWeight.Name = "clmnWeight";
         clmnWeight.DefaultCellStyle.Format = "N1";
         clmnWeight.Resizable = System.Windows.Forms.DataGridViewTriState.False;

         tgvAgentsSummary.Columns.Insert(tgvAgentsSummarySum.DisplayIndex+1, clmnWeight);

         ToolStripButton btnDubDocs = new ToolStripButton();
         btnDubDocs.Click += btnDubDocs_Click;
         btnDubDocs.Image = Resources.ic_list_alt;
         btnDubDocs.Name = "btnDubDocs";
         btnDubDocs.Text = "Выгрузка документов";
         btnDubDocs.DisplayStyle = ToolStripItemDisplayStyle.Image;
         tsbConfig.Items.Add(btnDubDocs);
      }

      void btnDubDocs_Click(object sender, EventArgs e)
      {
         new FmDubDocs().Show();
      }

      protected override DivisionSummary CreateDivisionSummary()
      {
         return new DivisionSummaryEx(dsConfig, clmnProgress);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         DataSet<string, Price> dsPrice = new DataSet<string, Price>(Price.OBJECT_NAME);
         dsPrice.Filter = DataUtils.COMMON_PRICE_FILTER_STR;
         updSets.Insert(0, dsPrice);
      }

      public string DataUtil { get; set; }
   }

   class DivisionSummaryEx : DivisionSummary
   {
      DataGridViewImageColumn progressColumn;

      public DivisionSummaryEx(DataSet<int, CommonConfig> dsConfig, DataGridViewImageColumn pc) : base(dsConfig)
      {
         progressColumn = pc;
      }

      protected override SummaryDivisionData CreateSummaryDivisionData(Division d)
      {
         return new SummaryDivisionDataEx(d, progressColumn);
      }

      protected override SummaryData CreateSummaryData(Agent agent, DataSet<int, CommonConfig> config)
      {
         return new SummaryDataEx(agent, config, progressColumn);
      }
   }

   class SummaryDataEx : SummaryData
   {
      public double weight = 0;
      Image pimage;
      DataGridViewImageColumn progressColumn;
      Dictionary<DateTime, Dictionary<string, bool>> ordersByDay = new Dictionary<DateTime, Dictionary<string, bool>>();

      public double orderProgress = 0;
      public int daysCount = 0;

      public SummaryDataEx(Agent a, DataSet<int, CommonConfig> config, DataGridViewImageColumn pc) : base(a, config) 
      {
         progressColumn = pc;
      }

      protected override void BeforeProgressCount(DateTime day, List<OrgFolderItem> items)
      {
         daysCount++;

         int oc = 0;
         if (ordersByDay.ContainsKey(day.Date))
         {
            Dictionary<string, bool> orders = ordersByDay[day.Date];
            foreach (OrgFolderItem ofi in items)
               if (orders.ContainsKey(ofi.name))
                  oc++;
         }
         orderProgress += (double)oc/ (double)items.Count;
      }

      public override void Add(Order o)
      {
         base.Add(o);

         Dictionary<string, bool> orders;
         DateTime checkDate = o.created.Date;
         if (ordersByDay.ContainsKey(checkDate))
            orders = ordersByDay[checkDate];
         else
         {
            orders = new Dictionary<string, bool>();
            ordersByDay[checkDate] = orders;
         }
         orders[o.id] = true;

         weight += o.Weight;
      }

      public Image ProgressImage2
      {
         get
         {
            if( pimage == null )
               pimage = GRSoft.NapoleonManager.Utils.ProgressImage.CreateProgressImage(OrderProgress, progressColumn);
            return pimage;
         }
      }

      public double Weight { get { return weight; } }
      public double OrderProgress { get { return daysCount == 0 ? 0 : orderProgress / daysCount * 100; } }
   }

   class SummaryDivisionDataEx : SummaryDivisionData
   {
      Image pimage;
      DataGridViewImageColumn progressColumn;
      public double weight = 0;
      public double orderProgress = 0;

      public SummaryDivisionDataEx(Division d, DataGridViewImageColumn pc) : base(d)
      {
         progressColumn = pc;
      }

      internal override void AddChildDivision(SummaryDivisionData chData)
      {
         base.AddChildDivision(chData);

         SummaryDivisionDataEx sde = (SummaryDivisionDataEx)chData;

         orderProgress += sde.orderProgress;
         weight += sde.weight;
      }

      public override void Add(SummaryData data)
      {
         base.Add(data);

         SummaryDataEx sde = (SummaryDataEx)data;

         orderProgress += sde.orderProgress;
         weight += sde.weight;
      }

      public Image ProgressImage2
      {
         get
         {
            if (pimage == null)
               pimage = GRSoft.NapoleonManager.Utils.ProgressImage.CreateProgressImage(OrderProgress, progressColumn);
            return pimage;
         }
      }

      public double OrderProgress { get { return ChildsCount == 0 ? 0 : orderProgress / ChildsCount * 100; } }
      public double Weight { get { return weight; } }
   }
}